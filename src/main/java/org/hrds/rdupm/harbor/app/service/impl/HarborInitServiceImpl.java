package org.hrds.rdupm.harbor.app.service.impl;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.*;
import java.util.stream.Collectors;

import javax.annotation.Resource;

import com.alibaba.fastjson.JSONObject;
import com.mysql.jdbc.jdbc2.optional.MysqlDataSource;
import org.apache.commons.collections.CollectionUtils;
import org.hrds.rdupm.harbor.api.vo.FdProjectDto;
import org.hrds.rdupm.harbor.api.vo.HarborCountVo;
import org.hrds.rdupm.harbor.app.service.C7nBaseService;
import org.hrds.rdupm.harbor.app.service.HarborAuthService;
import org.hrds.rdupm.harbor.app.service.HarborInitService;
import org.hrds.rdupm.harbor.config.HarborInitConfiguration;
import org.hrds.rdupm.harbor.domain.entity.HarborAuth;
import org.hrds.rdupm.harbor.domain.entity.HarborProjectDTO;
import org.hrds.rdupm.harbor.domain.entity.HarborRepository;
import org.hrds.rdupm.harbor.domain.repository.HarborRepositoryRepository;
import org.hrds.rdupm.harbor.infra.constant.HarborConstants;
import org.hrds.rdupm.harbor.infra.feign.dto.UserDTO;
import org.hrds.rdupm.harbor.infra.util.HarborHttpClient;
import org.hzero.core.base.BaseConstants;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.jdbc.core.BeanPropertyRowMapper;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.stereotype.Service;

/**
 * description
 *
 * @author chenxiuhong 2020/05/28 2:37 下午
 */
@Service
public class HarborInitServiceImpl implements HarborInitService {

	@Resource
	private HarborHttpClient harborHttpClient;
	@Autowired
	private C7nBaseService c7nBaseService;
	@Autowired
	private HarborRepositoryRepository harborRepositoryRepository;
	@Autowired
	private HarborAuthService harborAuthService;
	@Autowired
	private HarborInitConfiguration harborInitConfiguration;

	@Override
	public void init(){
		ResponseEntity<String> countResponse = harborHttpClient.exchange(HarborConstants.HarborApiEnum.COUNT,null,null,true);
		HarborCountVo harborCountVo = JSONObject.parseObject(countResponse.getBody(), HarborCountVo.class);

		ResponseEntity<String> projectResponse = harborHttpClient.exchange(HarborConstants.HarborApiEnum.LIST_PROJECT,null,null,true);
		List<HarborProjectDTO> harborProjectList= JSONObject.parseArray(projectResponse.getBody(),HarborProjectDTO.class);
		Map<String,HarborProjectDTO> map = harborProjectList.stream().collect(Collectors.toMap(HarborProjectDTO::getName,dto->dto));

		String selectSql = "SELECT\n" +
				"\tfp.`CODE` code,fp.`NAME` name,fp.id projectId,fp.ORGANIZATION_ID organizationId,fp.CREATED_BY createdBy,ht.tenant_num tenantNum,ht.tenant_name tenantName,\n" +
				"\tconcat( ht.tenant_num, CONCAT( '-', fp.`CODE` ) ) tenantProjectCode\n" +
				"FROM\n" +
				"\tfd_project fp\n" +
				"\tLEFT JOIN hpfm_tenant ht ON fp.ORGANIZATION_ID = ht.TENANT_ID";
		List<FdProjectDto> fdProjectDtoList =  getJdbcTemplate().query(selectSql,new BeanPropertyRowMapper<>(FdProjectDto.class));

		List<HarborRepository> harborRepositoryList = new ArrayList<>();
		Map<Long,Long> userMap = new HashMap<>(16);
		Set<Long> userIdSet = new HashSet<>(16);
		if(CollectionUtils.isNotEmpty(fdProjectDtoList)){
			fdProjectDtoList.stream().forEach(dto->{
				HarborProjectDTO harborProjectDTO= map.get(dto.getTenantProjectCode());
				if(harborProjectDTO != null){
					HarborRepository harborRepository = new HarborRepository(dto.getProjectId(),dto.getTenantProjectCode(),dto.getName(),harborProjectDTO.getMetadata().getPublicFlag(),Long.parseLong(harborProjectDTO.getHarborId().toString()),dto.getOrganizationId());
					harborRepositoryList.add(harborRepository);
					if("0".equals(dto.getCreatedBy())){
						UserDTO userDTO = c7nBaseService.listProjectOwnerById(dto.getProjectId());
						userIdSet.add(userDTO.getId());
						userMap.put(dto.getProjectId(),userDTO.getId());
					}else {
						userIdSet.add(dto.getCreatedBy());
						userMap.put(dto.getProjectId(),dto.getCreatedBy());
					}
				}
			});
			//批量保存项目
			//创建用户、分配权限
			harborRepositoryList.forEach(dto->{
				if(CollectionUtils.isNotEmpty(harborRepositoryRepository.select(HarborRepository.FIELD_PROJECT_ID,dto.getProjectId()))){
					harborRepositoryRepository.insertSelective(dto);
				}

				List<HarborAuth> authList = new ArrayList<>();
				HarborAuth harborAuth = new HarborAuth();
				harborAuth.setUserId(userMap.get(dto.getProjectId()));
				harborAuth.setHarborRoleValue(HarborConstants.HarborRoleEnum.PROJECT_ADMIN.getRoleValue());
				try {
					harborAuth.setEndDate(new SimpleDateFormat(BaseConstants.Pattern.DATE).parse("2099-12-31"));
				} catch (ParseException e) {
					e.printStackTrace();
				}
				authList.add(harborAuth);
				harborAuthService.save(dto.getProjectId(),authList);
			});
		}
	}


	private JdbcTemplate getJdbcTemplate(){
		MysqlDataSource mysqlDataSource = new MysqlDataSource();
		mysqlDataSource.setURL(harborInitConfiguration.getUrl());
		mysqlDataSource.setUser(harborInitConfiguration.getUsername());
		mysqlDataSource.setPassword(harborInitConfiguration.getPassword());
		JdbcTemplate jdbcTemplate = new JdbcTemplate(mysqlDataSource);
		return jdbcTemplate;
	}
}
