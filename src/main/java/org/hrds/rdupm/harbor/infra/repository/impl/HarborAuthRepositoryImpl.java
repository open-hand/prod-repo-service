package org.hrds.rdupm.harbor.infra.repository.impl;

import java.util.ArrayList;
import java.util.List;

import io.choerodon.core.oauth.DetailsHelper;
import org.apache.commons.collections.CollectionUtils;
import org.hrds.rdupm.harbor.infra.constant.HarborConstants;
import org.hzero.mybatis.base.impl.BaseRepositoryImpl;
import org.hrds.rdupm.harbor.domain.entity.HarborAuth;
import org.hrds.rdupm.harbor.domain.repository.HarborAuthRepository;
import org.springframework.stereotype.Component;

/**
 * 制品库-harbor权限表 资源库实现
 *
 * @author xiuhong.chen@hand-china.com 2020-04-27 16:12:54
 */
@Component
public class HarborAuthRepositoryImpl extends BaseRepositoryImpl<HarborAuth> implements HarborAuthRepository {


	@Override
	public List<String> getHarborRoleList(Long id) {
		HarborAuth harborAuth = new HarborAuth();
		harborAuth.setUserId(DetailsHelper.getUserDetails().getUserId());
		harborAuth.setProjectId(id);
		List<HarborAuth> harborAuthList = this.select(harborAuth);
		if(CollectionUtils.isNotEmpty(harborAuthList)){
			List<String> roleCodeList = new ArrayList<>();
			harborAuthList.forEach(dto->{
				String roleCode = HarborConstants.HarborRoleEnum.getValueById(dto.getHarborRoleId());
				roleCodeList.add(roleCode);
			});
			return roleCodeList;
		}
		return null;
	}
}
