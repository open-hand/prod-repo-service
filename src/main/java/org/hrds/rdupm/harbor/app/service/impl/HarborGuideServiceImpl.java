package org.hrds.rdupm.harbor.app.service.impl;

import org.hrds.rdupm.harbor.api.vo.HarborGuideVo;
import org.hrds.rdupm.harbor.app.service.HarborGuideService;
import org.hrds.rdupm.harbor.config.HarborInfoConfiguration;
import org.hrds.rdupm.harbor.domain.entity.HarborRepository;
import org.hrds.rdupm.harbor.domain.repository.HarborRepositoryRepository;
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
		String harborBaseUrl = harborInfoConfiguration.getBaseUrl();

		HarborRepository harborRepository = harborRepositoryRepository.select(HarborRepository.FIELD_PROJECT_ID,projectId).stream().findFirst().orElse(null);
		String code = harborRepository == null ? null : harborRepository.getCode();

		String loginCmd = String.format("docker login %s -u userName",harborBaseUrl);
		String dockerFile = HarborVelocityUtils.getJsonString(null,HarborVelocityUtils.DOCKER_FILE_NAME);
		String buildCmd = String.format("docker build -t %s/%s/imageName:tagName .",harborBaseUrl,code);
		String pushCmd = String.format("docker push %s/%s/imageName:tagName",harborBaseUrl,code);
		String pullCmd = String.format("docker pull %s/%s/imageName:tagName",harborBaseUrl,code);

		return new HarborGuideVo(loginCmd,dockerFile,buildCmd,pushCmd,pullCmd);
	}

	@Override
	public HarborGuideVo getTagGuide(String repoName, String tagName) {
		String harborBaseUrl = harborInfoConfiguration.getBaseUrl();
		String loginCmd = String.format("docker login %s -u userName",harborBaseUrl);
		String pullCmd = String.format("docker pull %s/%s:%s",harborBaseUrl,repoName,tagName);
		return new HarborGuideVo(loginCmd,null,null,null,pullCmd);
	}
}
