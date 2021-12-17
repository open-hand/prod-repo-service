package org.hrds.rdupm.init.service;

import com.mysql.cj.jdbc.MysqlDataSource;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.*;
import java.util.stream.Collectors;

import javax.annotation.Resource;

import com.alibaba.fastjson.JSONObject;
import com.google.common.reflect.TypeToken;
import com.google.gson.Gson;
import io.choerodon.asgard.saga.producer.TransactionalProducer;
import org.apache.commons.collections.CollectionUtils;
import org.hrds.rdupm.common.domain.entity.ProdUser;
import org.hrds.rdupm.common.domain.repository.ProdUserRepository;
import org.hrds.rdupm.harbor.domain.entity.*;
import org.hrds.rdupm.harbor.domain.repository.HarborCustomRepoRepository;
import org.hrds.rdupm.harbor.domain.repository.HarborRepoServiceRepository;
import org.hrds.rdupm.harbor.infra.feign.dto.ProjectDTO;
import org.hrds.rdupm.harbor.infra.mapper.HarborRepositoryMapper;
import org.hrds.rdupm.harbor.infra.util.HarborUtil;
import org.hrds.rdupm.init.dto.DevopsAppService;
import org.hrds.rdupm.init.dto.DevopsConfigDto;
import org.hrds.rdupm.init.dto.FdProjectDto;
import org.hrds.rdupm.harbor.api.vo.HarborAuthVo;
import org.hrds.rdupm.harbor.api.vo.HarborCountVo;
import org.hrds.rdupm.harbor.app.service.C7nBaseService;
import org.hrds.rdupm.init.config.HarborInitConfiguration;
import org.hrds.rdupm.harbor.domain.repository.HarborAuthRepository;
import org.hrds.rdupm.harbor.domain.repository.HarborRepositoryRepository;
import org.hrds.rdupm.harbor.infra.constant.HarborConstants;
import org.hrds.rdupm.harbor.infra.feign.dto.UserDTO;
import org.hrds.rdupm.harbor.infra.util.HarborHttpClient;
import org.hzero.core.base.BaseConstants;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.jdbc.core.BeanPropertyRowMapper;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.jdbc.core.namedparam.NamedParameterJdbcTemplate;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

/**
 * description
 *
 * @author chenxiuhong 2020/05/28 2:37 下午
 */
@Service
public class HarborInitServiceImpl implements HarborInitService {
	private static final Logger LOGGER = LoggerFactory.getLogger(HarborInitServiceImpl.class);

	@Resource
	private HarborHttpClient harborHttpClient;
	@Autowired
	private C7nBaseService c7nBaseService;
	@Autowired
	private HarborRepositoryRepository harborRepositoryRepository;
	@Autowired
	private HarborInitConfiguration harborInitConfiguration;
	@Autowired
	private HarborAuthRepository repository;
	@Resource
	private TransactionalProducer transactionalProducer;
	@Autowired
	private HarborCustomRepoRepository harborCustomRepoRepository;
	@Autowired
	private HarborRepoServiceRepository harborRepoServiceRepository;
	@Autowired
	private ProdUserRepository prodUserRepository;
	@Resource
	private HarborRepositoryMapper harborRepositoryMapper;

	private final String sagaCode = "rdupm-docker-auth-create-init";

	private final Integer pageSize = 100;

	/***
	 * 默认仓库初始化、关联Harbor项目、创建默认账户、生成默认权限
	 */
	@Override
	public void defaultRepoInit(){
		LOGGER.debug("=====================================默认仓库初始化=====================================");
		long start = System.currentTimeMillis();

		//获取猪齿鱼中项目信息
		String selectSql = "SELECT\n" +
				"\tfp.`CODE` code,fp.`NAME` name,fp.id projectId,fp.ORGANIZATION_ID organizationId,fp.CREATED_BY createdBy,ht.tenant_num tenantNum,ht.tenant_name tenantName,\n" +
				"\tconcat( ht.tenant_num, CONCAT( '-', fp.`CODE` ) ) tenantProjectCode\n" +
				"FROM\n" +
				"\tfd_project fp\n" +
				"\tLEFT JOIN hpfm_tenant ht ON fp.ORGANIZATION_ID = ht.TENANT_ID";
		List<FdProjectDto> fdProjectDtoList =  getDefaultJdbcTemplate().query(selectSql,new BeanPropertyRowMapper<>(FdProjectDto.class));
		if(CollectionUtils.isEmpty(fdProjectDtoList)) {
			return;
		}

		//获取Harbor中项目总数
		ResponseEntity<String> countResponse = harborHttpClient.exchange(HarborConstants.HarborApiEnum.COUNT,null,null,true);
		HarborCountVo harborCountVo = JSONObject.parseObject(countResponse.getBody(), HarborCountVo.class);
		int totalProjectCount = harborCountVo.getTotalProjectCount().intValue();
		if(totalProjectCount < pageSize){
			defaultRepoInitToDb(1,fdProjectDtoList);
		}else {
			int part = totalProjectCount/pageSize;
			for(int i=1; i<=part+1;i++){
				defaultRepoInitToDb(i,fdProjectDtoList);
			}
		}

		long end = System.currentTimeMillis();
		LOGGER.debug("=====================================默认仓库初始化完成：{}(ms)============================",end-start);
	}

	public void defaultRepoInitToDb(int page,List<FdProjectDto> fdProjectDtoList){
		//分页查询Harbor项目信息
		Map<String,Object> paramMap = new HashMap<>(2);
		paramMap.put("page",page);
		paramMap.put("page_size",pageSize);
		ResponseEntity<String> projectResponse = harborHttpClient.exchange(HarborConstants.HarborApiEnum.LIST_PROJECT,paramMap,null,true);
		Map<String,HarborProjectDTO> harborProjectMap = new HashMap<>(16);
		List<String> projectList= JSONObject.parseArray(projectResponse.getBody(),String.class);
		Gson gson = new Gson();
		for(String object : projectList){
			HarborProjectDTO projectResponseDto = gson.fromJson(object, HarborProjectDTO.class);
			harborProjectMap.put(projectResponseDto.getName(),projectResponseDto);
		}

		//harbor项目和猪齿鱼项目做关联:默认仓库列表、项目ID和用户关联Map、用户IDSet
		List<HarborRepository> harborRepositoryList = new ArrayList<>();
		Map<Long,Long> projectIdUserIdMap = new HashMap<>(16);
		Set<Long> creatUserIdSet = new HashSet<>(16);
		fdProjectDtoList.stream().forEach(dto->{
			HarborProjectDTO harborProjectDTO= harborProjectMap.get(dto.getTenantProjectCode());
			if(harborProjectDTO != null){
				HarborRepository harborRepository = new HarborRepository(dto.getProjectId(),dto.getTenantProjectCode(),dto.getName(),harborProjectDTO.getMetadata().getPublicFlag(),Long.parseLong(harborProjectDTO.getHarborId().toString()),dto.getOrganizationId());
				harborRepositoryList.add(harborRepository);
				if(dto.getCreatedBy() == 0){
					UserDTO userDTO = c7nBaseService.getProjectOwnerById(dto.getProjectId());
					projectIdUserIdMap.put(dto.getProjectId(),userDTO == null ? -1 : userDTO.getId());
					creatUserIdSet.add(userDTO == null ? -1 : userDTO.getId());
				}else {
					projectIdUserIdMap.put(dto.getProjectId(),dto.getCreatedBy());
					creatUserIdSet.add(dto.getCreatedBy());
				}
			}
		});

		/***
		* 1.数据库：插入项目
		* 2.数据库：批量插入用户
		* 3.Harbor：创建用户账号
		* 4.Harbor：创建用户权限
		* 5.数据库：插入用户权限
		* */
		List<HarborAuth> authList = new ArrayList<>();
		Map<Long,UserDTO> userDtoMap = c7nBaseService.listUsersByIds(creatUserIdSet);

		harborRepositoryList.forEach(dto->{
			if(CollectionUtils.isEmpty(harborRepositoryRepository.select(HarborRepository.FIELD_PROJECT_ID,dto.getProjectId()))){
				harborRepositoryRepository.insertSelective(dto);

				Long userId = projectIdUserIdMap.get(dto.getProjectId());
				UserDTO userDTO = userDtoMap.get(userId);
				if(userDTO != null && !"admin".equals(userDTO.getLoginName())){
					HarborAuth harborAuth = getOwnerAuth(dto,userDTO);
					authList.add(harborAuth);
				}else {
					UserDTO projectOwnerDTO = c7nBaseService.getProjectOwnerById(dto.getProjectId());
					if(projectOwnerDTO != null && !"admin".equals(projectOwnerDTO.getLoginName())){
						creatUserIdSet.add(projectOwnerDTO.getId());
						userDtoMap.put(projectOwnerDTO.getId(),projectOwnerDTO);
						HarborAuth harborAuth = getOwnerAuth(dto,projectOwnerDTO);
						authList.add(harborAuth);
					}
				}
			}
		});
		batchInsertUserToDb(creatUserIdSet,userDtoMap);
		batchAssignAuthToDb(authList);

		LOGGER.debug("Thread name:{},task:{}",Thread.currentThread().getName(),page);
	}

	public HarborAuth getOwnerAuth(HarborRepository dto,UserDTO userDTO){
		HarborAuth harborAuth = new HarborAuth();
		harborAuth.setHarborId(dto.getHarborId());
		harborAuth.setProjectId(dto.getProjectId());
		harborAuth.setOrganizationId(dto.getOrganizationId());
		harborAuth.setUserId(userDTO.getId());
		harborAuth.setLoginName(userDTO.getLoginName());
		harborAuth.setRealName(userDTO.getRealName());
		harborAuth.setHarborRoleValue(HarborConstants.HarborRoleEnum.PROJECT_ADMIN.getRoleValue());
		try {
			harborAuth.setEndDate(new SimpleDateFormat(BaseConstants.Pattern.DATE).parse("2099-12-31"));
		} catch (ParseException e) {
			e.printStackTrace();
		}
		return harborAuth;
	}

	private void batchInsertUserToDb(Set<Long> creatUserIdSet,Map<Long,UserDTO> userDtoMap){
		List<ProdUser> prodUserList = new ArrayList<>();
		for(Long userId : creatUserIdSet){
			UserDTO userDTO = userDtoMap.get(userId);
			if(userDTO == null){
			    continue;
            }
			if(!"admin".equals(userDTO.getLoginName())){
				String password = HarborUtil.getPassword();
				//校验DB中是否已存在用户
				List<ProdUser> existUserList = prodUserRepository.select(ProdUser.FIELD_USER_ID,userId);
				if(CollectionUtils.isEmpty(existUserList)) {
                    ProdUser prodUser = new ProdUser(userId, userDTO.getLoginName(), password, 0);
                    prodUserList.add(prodUser);
				}
				//校验Harbor中是否已存在用户
				Map<String,Object> paramMap = new HashMap<>(1);
				paramMap.put("username",userDTO.getLoginName());
				ResponseEntity<String> userResponse = harborHttpClient.exchange(HarborConstants.HarborApiEnum.SELECT_USER_BY_USERNAME,paramMap,null,true);
				List<User> userList = JSONObject.parseArray(userResponse.getBody(), User.class);
				Map<String,User> userMap = CollectionUtils.isEmpty(userList) ? new HashMap<>(1) : userList.stream().collect(Collectors.toMap(User::getUsername, dto->dto));
				if(userMap.get(userDTO.getLoginName()) == null){
					User user = new User(userDTO.getLoginName(),userDTO.getEmail(),password,userDTO.getRealName());
					harborHttpClient.exchange(HarborConstants.HarborApiEnum.CREATE_USER,null,user,true);
				}
			}
		}
		prodUserRepository.batchInsert(prodUserList);
	}

	private void batchAssignAuthToDb(List<HarborAuth> authList){
		for(HarborAuth harborAuth : authList){
			Long harborId = harborAuth.getHarborId();
			//Harbor:创建用户权限
			ResponseEntity<String> responseEntity = harborHttpClient.exchange(HarborConstants.HarborApiEnum.LIST_AUTH,null,null,true,harborId);
			List<HarborAuthVo> harborAuthVoList = new Gson().fromJson(responseEntity.getBody(),new TypeToken<List<HarborAuthVo>>(){}.getType());
			Map<String,HarborAuthVo> harborAuthVoMap = CollectionUtils.isEmpty(harborAuthVoList) ? new HashMap<>(1) : harborAuthVoList.stream().collect(Collectors.toMap(HarborAuthVo::getEntityName,dto->dto));
			if(harborAuthVoMap.get(harborAuth.getLoginName()) == null){
				Map<String,Object> bodyMap = new HashMap<>(2);
				Map<String,Object> memberMap = new HashMap<>(1);
				memberMap.put("username",harborAuth.getLoginName());
				bodyMap.put("role_id",harborAuth.getHarborRoleId());
				bodyMap.put("member_user",memberMap);
				harborHttpClient.exchange(HarborConstants.HarborApiEnum.CREATE_ONE_AUTH,null,bodyMap,true,harborId);
			}

			//DB：插入权限
			ResponseEntity<String> responseEntity2 = harborHttpClient.exchange(HarborConstants.HarborApiEnum.LIST_AUTH,null,null,true,harborId);
			List<HarborAuthVo> harborAuthVoList2 = new Gson().fromJson(responseEntity2.getBody(),new TypeToken<List<HarborAuthVo>>(){}.getType());
			Map<String,HarborAuthVo> harborAuthVoMap2 = CollectionUtils.isEmpty(harborAuthVoList2) ? new HashMap<>(1) : harborAuthVoList2.stream().collect(Collectors.toMap(HarborAuthVo::getEntityName,dto->dto));
			if(harborAuthVoMap2.get(harborAuth.getLoginName()) != null){
				harborAuth.setHarborAuthId(harborAuthVoMap2.get(harborAuth.getLoginName()).getHarborAuthId());
				repository.insertSelective(harborAuth);
			}

		}
	}

	/***
	 * 自定义仓库初始化
	 */
	@Override
	@Transactional(rollbackFor = Exception.class)
	public void customRepoInit() {
		LOGGER.debug("=====================================自定义仓库初始化=====================================");

		//获取猪齿鱼数据库中自定义仓库配置信息
		String selectSql = "select * from devops_config where type = 'harbor' and (app_service_id is not null or organization_id is not null or project_id is not null)";
		List<DevopsConfigDto> devopsConfigDtoList =  getCustomJdbcTemplate().query(selectSql,new BeanPropertyRowMapper<>(DevopsConfigDto.class));
		if(CollectionUtils.isEmpty(devopsConfigDtoList)){
			return;
		}
		devopsConfigDtoList.forEach(dto->dto.parseConfig());

		//过滤数据：存在appServiceId、存在projectId、存在organizationId
		Map<Long, DevopsConfigDto> appServiceIdMap = devopsConfigDtoList.stream().filter(dto->dto.getAppServiceId() != null).collect(Collectors.toMap(DevopsConfigDto::getAppServiceId,dto->dto));
		Map<Long, DevopsConfigDto> projectIdMap = devopsConfigDtoList.stream().filter(dto->dto.getProjectId() != null).collect(Collectors.toMap(DevopsConfigDto::getProjectId,dto->dto));

		//根据appServiceId批量获取服务详情信息，然后分组获得projectIdMap
		List<Long> appServiceIdList = new ArrayList<>(appServiceIdMap.keySet());
		List<DevopsAppService> devopsAppServiceList = new ArrayList<>();
		if(appServiceIdList.size() < 1000){
			devopsAppServiceList = listAppServiceByIds(appServiceIdList);
		}else {
			int part = appServiceIdList.size()/1000;
			for(int i=0; i<part;i++){
				devopsAppServiceList.addAll(listAppServiceByIds(appServiceIdList.subList(i,(i+1)*1000)));
			}
		}
		Map<Long, DevopsAppService> zcyAppServiceMap = devopsAppServiceList.stream().collect(Collectors.toMap(DevopsAppService::getId, dto -> dto));
		Map<Long, List<DevopsAppService>> devopsAppServiceProjectIdMap = devopsAppServiceList.stream().collect(Collectors.groupingBy(DevopsAppService::getProjectId));
		Set<Long> devopsAppServiceProjectIdSet = devopsAppServiceProjectIdMap.keySet();

		//根据projectId批量获取组织信息
		Set<Long> projectIdSet = new HashSet<>(16);
		projectIdSet.addAll(projectIdMap.keySet());
		projectIdSet.addAll(devopsAppServiceProjectIdSet);
		Map<Long, ProjectDTO> projectDTOMap = c7nBaseService.queryProjectByIds(projectIdSet);

		//新增自定义仓库、创建关联关系
		List<HarborRepoService> harborRepoServiceList = new ArrayList<>();
		for(DevopsConfigDto devopsConfigDto : devopsConfigDtoList){
			//根据应用服务查询项目ID、组织ID。创建关联关系
			Long appServiceId = devopsConfigDto.getAppServiceId();
			Long organizationId = devopsConfigDto.getOrganizationId();
			Long projectId = devopsConfigDto.getProjectId();
			HarborRepoService harborRepoService = new HarborRepoService();
			if(appServiceId != null){
				DevopsAppService devopsAppService = zcyAppServiceMap.get(appServiceId);
				if(devopsAppService == null){
					LOGGER.debug("devopsAppService is null,appServiceId:{}",appServiceId);
				}else {
					ProjectDTO projectDTO = projectDTOMap.get(devopsAppService.getProjectId());
					if(projectDTO == null){
						LOGGER.debug("projectDTO is null,appServiceId:{},projectId:{}",devopsAppService.getId(),devopsAppService.getProjectId());
					}else {
						harborRepoService = new HarborRepoService(appServiceId,devopsAppService.getProjectId(),projectDTO.getOrganizationId());
					}
				}

			} else if(projectId != null){
				ProjectDTO projectDTO = projectDTOMap.get(projectId);
				if(projectDTO == null){
					LOGGER.debug("projectDTO is null,projectId:{}",projectId);
				}else {
					harborRepoService = new HarborRepoService(null,projectId,projectDTO.getOrganizationId());
				}
			}else if(organizationId != null){
				harborRepoService = new HarborRepoService(null,null,organizationId);
			}

			//创建自定义仓库
			if(harborCustomRepoRepository.selectByPrimaryKey(devopsConfigDto.getId()) == null) {
				HarborCustomRepo harborCustomRepo = new HarborCustomRepo();
				BeanUtils.copyProperties(devopsConfigDto, harborCustomRepo);
				harborCustomRepo.setProjectId(harborRepoService.getProjectId());
				harborCustomRepo.setOrganizationId(harborRepoService.getOrganizationId());
				harborCustomRepo.setProjectShare(HarborConstants.FALSE);
				harborCustomRepo.setEnabledFlag(HarborConstants.Y);
				harborCustomRepoRepository.insertSelective(harborCustomRepo);


				harborRepoService.setCustomRepoId(harborCustomRepo.getId());
				harborRepoServiceList.add(harborRepoService);
			}
		}
		harborRepoServiceRepository.batchInsert(harborRepoServiceList);
		initHarborCustomRepoNoAnyId();

		LOGGER.debug("=====================================自定义仓库初始化完成===================================");

	}

	public List<DevopsAppService> listAppServiceByIds(List<Long> appServiceIdList){
		String sql = "select id,project_id from devops_app_service where id in (:ids)";
		Map<String, Object> paramMap = new HashMap<String, Object>();
		paramMap.put("ids", appServiceIdList);
		NamedParameterJdbcTemplate jdbc = new NamedParameterJdbcTemplate(getCustomJdbcTemplate());
		List<DevopsAppService> devopsAppServiceList = jdbc.query(sql, paramMap , new BeanPropertyRowMapper<>(DevopsAppService.class));
		return devopsAppServiceList;
	}

	private JdbcTemplate getDefaultJdbcTemplate(){
		MysqlDataSource mysqlDataSource = new MysqlDataSource();
		mysqlDataSource.setURL(harborInitConfiguration.getDefaultRepoUrl());
		mysqlDataSource.setUser(harborInitConfiguration.getDefaultRepoUsername());
		mysqlDataSource.setPassword(harborInitConfiguration.getDefaultRepoPassword());
		JdbcTemplate jdbcTemplate = new JdbcTemplate(mysqlDataSource);
		return jdbcTemplate;
	}

	private JdbcTemplate getCustomJdbcTemplate(){
		MysqlDataSource mysqlDataSource = new MysqlDataSource();
		mysqlDataSource.setURL(harborInitConfiguration.getCustomRepoUrl());
		mysqlDataSource.setUser(harborInitConfiguration.getCustomRepoUsername());
		mysqlDataSource.setPassword(harborInitConfiguration.getCustomRepoPassword());
		JdbcTemplate jdbcTemplate = new JdbcTemplate(mysqlDataSource);
		return jdbcTemplate;
	}

	/***
	 * 服务ID、组织ID、项目ID都为空时初始化进去，不保存关联关系
	 */
	@Override
	public void initHarborCustomRepoNoAnyId() {
		LOGGER.info("=====================================自定义仓库初始化修复=====================================");

		//获取猪齿鱼数据库中自定义仓库配置信息
		String selectSql = "select * from devops_config where type = 'harbor' and (app_service_id is null and organization_id is null and project_id is null)";
		List<DevopsConfigDto> devopsConfigDtoList =  getCustomJdbcTemplate().query(selectSql,new BeanPropertyRowMapper<>(DevopsConfigDto.class));
		if(CollectionUtils.isEmpty(devopsConfigDtoList)){
			return;
		}
		devopsConfigDtoList.forEach(dto->dto.parseConfig());
		for(DevopsConfigDto devopsConfigDto : devopsConfigDtoList){
			//创建自定义仓库
			if(harborCustomRepoRepository.selectByPrimaryKey(devopsConfigDto.getId()) == null){
				HarborCustomRepo harborCustomRepo = new HarborCustomRepo();
				BeanUtils.copyProperties(devopsConfigDto,harborCustomRepo);
				harborCustomRepo.setProjectShare(HarborConstants.FALSE);
				harborCustomRepo.setEnabledFlag(HarborConstants.Y);
				harborCustomRepoRepository.insertSelective(harborCustomRepo);
			}else {
				LOGGER.info("自定义数据已存在：id:{},repo_url:{},repo_name:{}",devopsConfigDto.getId(),devopsConfigDto.getRepoUrl(),devopsConfigDto.getRepoName());
			}
		}
		LOGGER.info("=====================================自定义仓库初始化修复完成=====================================");
	}

	/***
	 * 当仓库下没有任何管理员用户时，初始化项目所有者为仓库管理员权限
	 */
	@Override
	public void fixHarborUserAuth(){
		LOGGER.info("=====================================默认仓库初始化修复=====================================");

		List<HarborRepository> harborRepositoryList = harborRepositoryMapper.selectRepoNoAuth();
		if(CollectionUtils.isEmpty(harborRepositoryList)){
			return;
		}
		Set<Long> creatUserIdSet = new HashSet<>(16);
		Map<Long,UserDTO> userDtoMap = new HashMap<>(16);
		List<HarborAuth> authList = new ArrayList<>();

		for(HarborRepository dto : harborRepositoryList){
			Long projectId = dto.getProjectId();
			UserDTO userDTO = c7nBaseService.getProjectOwnerById(projectId);
			if(userDTO != null && !"admin".equals(userDTO.getLoginName())){
				creatUserIdSet.add(userDTO.getId());
				userDtoMap.put(userDTO.getId(),userDTO);

				HarborAuth harborAuth = getOwnerAuth(dto,userDTO);
				authList.add(harborAuth);
			}
		}
		batchInsertUserToDb(creatUserIdSet,userDtoMap);
		batchAssignAuthToDb(authList);
		LOGGER.info("=====================================默认仓库初始化修复完成=====================================");

	}

}
