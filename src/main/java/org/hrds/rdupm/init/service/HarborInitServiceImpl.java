package org.hrds.rdupm.init.service;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.*;
import java.util.stream.Collectors;

import javax.annotation.Resource;

import com.alibaba.fastjson.JSONObject;
import com.google.common.reflect.TypeToken;
import com.google.gson.Gson;
import com.mysql.jdbc.jdbc2.optional.MysqlDataSource;
import io.choerodon.asgard.saga.annotation.Saga;
import io.choerodon.asgard.saga.producer.StartSagaBuilder;
import io.choerodon.asgard.saga.producer.TransactionalProducer;
import io.choerodon.core.exception.CommonException;
import io.choerodon.core.iam.ResourceLevel;
import org.apache.commons.collections.CollectionUtils;
import org.hrds.rdupm.harbor.domain.entity.*;
import org.hrds.rdupm.harbor.domain.repository.HarborCustomRepoRepository;
import org.hrds.rdupm.harbor.domain.repository.HarborRepoServiceRepository;
import org.hrds.rdupm.harbor.infra.feign.dto.ProjectDTO;
import org.hrds.rdupm.init.dto.DevopsAppService;
import org.hrds.rdupm.init.dto.DevopsConfigDto;
import org.hrds.rdupm.init.dto.FdProjectDto;
import org.hrds.rdupm.harbor.api.vo.HarborAuthVo;
import org.hrds.rdupm.harbor.api.vo.HarborCountVo;
import org.hrds.rdupm.harbor.app.service.C7nBaseService;
import org.hrds.rdupm.init.config.HarborInitConfiguration;
import org.hrds.rdupm.harbor.domain.repository.HarborAuthRepository;
import org.hrds.rdupm.harbor.domain.repository.HarborRepositoryRepository;
import org.hrds.rdupm.harbor.infra.annotation.OperateLog;
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
import org.springframework.scheduling.annotation.Async;
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

	private final String sagaCode = "rdupm-docker-auth-create-init";

	private final Integer pageSize = 100;

	/***
	 * 默认仓库初始化、关联Harbor项目、创建默认账户、生成默认权限
	 */
	@Override
	public void defaultRepoInit(){
		long start = System.currentTimeMillis();

		//获取Harbor中项目总数
		ResponseEntity<String> countResponse = harborHttpClient.exchange(HarborConstants.HarborApiEnum.COUNT,null,null,true);
		HarborCountVo harborCountVo = JSONObject.parseObject(countResponse.getBody(), HarborCountVo.class);
		if(harborCountVo.getTotalProjectCount().intValue() < pageSize){
			defaultRepoInitToDb(1);
		}else {
			int part = harborCountVo.getTotalProjectCount().intValue()/pageSize;
			for(int i=1; i<=part;i++){
				defaultRepoInitToDb(i);
			}
		}

		long end = System.currentTimeMillis();
		LOGGER.debug("初始化完成：{}(ms)",end-start);
	}

	@Async("init-executor")
	public void defaultRepoInitToDb(int page){
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

		//harbor项目和猪齿鱼项目做关联
		List<HarborRepository> harborRepositoryList = new ArrayList<>();
		Map<Long,Long> projectIdUserIdMap = new HashMap<>(16);
		fdProjectDtoList.stream().forEach(dto->{
			HarborProjectDTO harborProjectDTO= harborProjectMap.get(dto.getTenantProjectCode());
			if(harborProjectDTO != null){
				HarborRepository harborRepository = new HarborRepository(dto.getProjectId(),dto.getTenantProjectCode(),dto.getName(),harborProjectDTO.getMetadata().getPublicFlag(),Long.parseLong(harborProjectDTO.getHarborId().toString()),dto.getOrganizationId());
				harborRepositoryList.add(harborRepository);
				if(dto.getCreatedBy() == 0){
					UserDTO userDTO = c7nBaseService.getProjectOwnerById(dto.getProjectId());
					projectIdUserIdMap.put(dto.getProjectId(),userDTO == null ? -1 : userDTO.getId());
				}else {
					projectIdUserIdMap.put(dto.getProjectId(),dto.getCreatedBy());
				}
			}
		});

		//保存项目到数据库、创建默认用户、分配最高权限
		harborRepositoryList.forEach(dto->{
			if(CollectionUtils.isEmpty(harborRepositoryRepository.select(HarborRepository.FIELD_PROJECT_ID,dto.getProjectId()))){
				harborRepositoryRepository.insertSelective(dto);

				List<HarborAuth> authList = new ArrayList<>();
				HarborAuth harborAuth = new HarborAuth();
				harborAuth.setUserId(projectIdUserIdMap.get(dto.getProjectId()));
				harborAuth.setHarborRoleValue(HarborConstants.HarborRoleEnum.PROJECT_ADMIN.getRoleValue());
				try {
					harborAuth.setEndDate(new SimpleDateFormat(BaseConstants.Pattern.DATE).parse("2099-12-31"));
				} catch (ParseException e) {
					e.printStackTrace();
				}
				authList.add(harborAuth);
				save(dto,authList);
			}
		});
		LOGGER.debug("Thread name:{},task:{}",Thread.currentThread().getName(),page);
	}

	/***
	 * 自定义仓库初始化
	 */
	@Override
	@Transactional(rollbackFor = Exception.class)
	public void customRepoInit() {
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
			HarborCustomRepo harborCustomRepo = new HarborCustomRepo();
			BeanUtils.copyProperties(devopsConfigDto,harborCustomRepo);
			harborCustomRepo.setProjectId(harborRepoService.getProjectId());
			harborCustomRepo.setOrganizationId(harborRepoService.getOrganizationId());
			harborCustomRepo.setProjectShare(HarborConstants.FALSE);
			harborCustomRepoRepository.insertSelective(harborCustomRepo);

			harborRepoService.setCustomRepoId(harborCustomRepo.getId());
			harborRepoServiceList.add(harborRepoService);
		}
		harborRepoServiceRepository.batchInsert(harborRepoServiceList);
	}

	public List<DevopsAppService> listAppServiceByIds(List<Long> appServiceIdList){
		String sql = "select id,project_id from devops_app_service where id in (:ids)";
		Map<String, Object> paramMap = new HashMap<String, Object>();
		paramMap.put("ids", appServiceIdList);
		NamedParameterJdbcTemplate jdbc = new NamedParameterJdbcTemplate(getCustomJdbcTemplate());
		List<DevopsAppService> devopsAppServiceList = jdbc.query(sql, paramMap , new BeanPropertyRowMapper<>(DevopsAppService.class));
		return devopsAppServiceList;
	}

	@OperateLog(operateType = HarborConstants.ASSIGN_AUTH,content = "%s 分配 %s 权限角色为 【%s】,过期日期为【%s】")
	@Saga(code = sagaCode,description = "分配权限",inputSchemaClass = List.class)
	public void save(HarborRepository harborRepository,List<HarborAuth> dtoList) {
		//校验是否已分配权限
		List<HarborAuth> existList = repository.select(HarborAuth.FIELD_PROJECT_ID,harborRepository.getProjectId());
		Map<Long,HarborAuth> harborAuthMap = CollectionUtils.isEmpty(existList) ? new HashMap<>(1) : existList.stream().collect(Collectors.toMap(HarborAuth::getUserId,dto->dto));

		//生成权限数据
		Set<Long> userIdSet = dtoList.stream().map(dto->dto.getUserId()).collect(Collectors.toSet());
		Map<Long,UserDTO> userDtoMap = c7nBaseService.listUsersByIds(userIdSet);
		dtoList.forEach(dto->{
			UserDTO userDTO = userDtoMap.get(dto.getUserId());
			dto.setLoginName(userDTO == null ? null : userDTO.getLoginName());
			dto.setRealName(userDTO == null ? null : userDTO.getRealName());

			if(harborAuthMap.get(dto.getUserId()) != null){
				throw new CommonException("error.harbor.auth.already.exist",dto.getRealName());
			}

			dto.setProjectId(harborRepository.getProjectId());
			dto.setOrganizationId(harborRepository.getOrganizationId());
			dto.setHarborId(harborRepository.getHarborId());
			dto.setHarborRoleValue(dto.getHarborRoleValue());
			dto.setHarborAuthId(-1L);
		});

		//调用Saga
		transactionalProducer.apply(StartSagaBuilder.newBuilder()
						.withSagaCode(sagaCode)
						.withLevel(ResourceLevel.PROJECT)
						.withRefType("dockerRepo")
						.withSourceId(harborRepository.getProjectId()),
				startSagaBuilder -> {

					//保存到数据库
					Long harborId = dtoList.get(0).getHarborId();
					ResponseEntity<String> responseEntity = harborHttpClient.exchange(HarborConstants.HarborApiEnum.LIST_AUTH,null,null,true,harborId);
					List<HarborAuthVo> harborAuthVoList = new Gson().fromJson(responseEntity.getBody(),new TypeToken<List<HarborAuthVo>>(){}.getType());
					Map<String,HarborAuthVo> harborAuthVoMap = CollectionUtils.isEmpty(harborAuthVoList) ? new HashMap<>(1) : harborAuthVoList.stream().collect(Collectors.toMap(HarborAuthVo::getEntityName,dto->dto));
					dtoList.stream().forEach(dto->{
						if(harborAuthVoMap.get(dto.getLoginName()) != null){
							throw new CommonException("error.harbor.auth.find.harborAuthId");
						}
					});
					repository.batchInsert(dtoList);

					startSagaBuilder.withPayloadAndSerialize(dtoList).withSourceId(harborRepository.getProjectId());
				});
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

}
