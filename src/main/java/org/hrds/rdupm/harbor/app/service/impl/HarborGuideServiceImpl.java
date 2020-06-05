package org.hrds.rdupm.harbor.app.service.impl;

import io.choerodon.core.oauth.DetailsHelper;
import org.hrds.rdupm.harbor.api.vo.HarborGuideVo;
import org.hrds.rdupm.harbor.app.service.HarborGuideService;
import org.hrds.rdupm.harbor.config.HarborInfoConfiguration;
import org.hrds.rdupm.harbor.domain.entity.HarborRepository;
import org.hrds.rdupm.harbor.domain.repository.HarborRepositoryRepository;
import org.hrds.rdupm.harbor.infra.constant.HarborConstants;
import org.hrds.rdupm.harbor.infra.util.HarborVelocityUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

/**
 * description
 *
 * @author chenxiuhong 2020/04/23 2:41 下午
 */
@Service
public class HarborGuideServiceImpl implements HarborGuideService {

	@Autowired
	private HarborInfoConfiguration harborInfoConfiguration;

	@Autowired
	private HarborRepositoryRepository harborRepositoryRepository;

	@Override
	public HarborGuideVo getProjectGuide(Long projectId) {
		String loginName = DetailsHelper.getUserDetails().getUsername();
		String harborBaseUrl = harborInfoConfiguration.getDomain();

		HarborRepository harborRepository = harborRepositoryRepository.select(HarborRepository.FIELD_PROJECT_ID,projectId).stream().findFirst().orElse(null);
		String code = harborRepository == null ? null : harborRepository.getCode();

		String vimHostCmd = String.format("vim /etc/hosts \n%s %s",harborInfoConfiguration.getIp(),harborInfoConfiguration.getDomain());
		String configRegistryCmd = String.format("{\n  \"insecure-registries\": [\"http://%s\"]\n }",harborInfoConfiguration.getDomain());
		String loginCmd = String.format("#\"个人信息-->个人设置-->制品库设置\"中可查看默认密码 \ndocker login %s -u %s -p 密码",harborBaseUrl,loginName);
		String dockerFile = HarborVelocityUtils.getJsonString(null,HarborVelocityUtils.DOCKER_FILE_NAME);
		String buildCmd = String.format("docker build -t %s/%s/镜像名称:镜像版本名称 .",harborBaseUrl,code);
		String pushCmd = String.format("docker push %s/%s/镜像名称:镜像版本名称",harborBaseUrl,code);
		String pullCmd = String.format("docker pull %s/%s/镜像名称:镜像版本名称",harborBaseUrl,code);

		return new HarborGuideVo(vimHostCmd,configRegistryCmd,loginCmd,dockerFile,buildCmd,pushCmd,pullCmd);
	}

	@Override
	public HarborGuideVo getTagGuide(String repoName, String tagName) {
		String loginName = DetailsHelper.getUserDetails().getUsername();
		String harborBaseUrl = harborInfoConfiguration.getDomain();
		String loginCmd = String.format("#\"个人信息-->个人设置-->制品库设置\"中可查看默认密码 \ndocker login %s -u %s -p 密码",harborBaseUrl,loginName);
		String pullCmd = String.format("docker pull %s/%s:%s",harborBaseUrl,repoName,tagName);
		return new HarborGuideVo(loginCmd,null,null,null,pullCmd);
	}
}
