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
		String harborBaseUrl = harborInfoConfiguration.getDomain();

		HarborRepository harborRepository = harborRepositoryRepository.select(HarborRepository.FIELD_PROJECT_ID,projectId).stream().findFirst().orElse(null);
		String code = harborRepository == null ? null : harborRepository.getCode();

		String vimHostCmd = String.format("vim /etc/hosts \n%s %s",harborInfoConfiguration.getIp(),harborInfoConfiguration.getDomain());
		String mkdirCertCmd = String.format("mkdir -p /etc/docker/certs.d/%s/",harborInfoConfiguration.getDomain());
		String certUrl = harborInfoConfiguration.getCertUrl();
		String keyUrl = harborInfoConfiguration.getKeyUrl();
		String configRegistryCmd = String.format("{\n  \"insecure-registries\": [\"http://%s\"]\n }",harborInfoConfiguration.getDomain());
		String loginCmd = String.format("docker login %s -u userName",harborBaseUrl);
		String dockerFile = HarborVelocityUtils.getJsonString(null,HarborVelocityUtils.DOCKER_FILE_NAME);
		String buildCmd = String.format("docker build -t %s/%s/imageName:tagName .",harborBaseUrl,code);
		String pushCmd = String.format("docker push %s/%s/imageName:tagName",harborBaseUrl,code);
		String pullCmd = String.format("docker pull %s/%s/imageName:tagName",harborBaseUrl,code);

		return new HarborGuideVo(vimHostCmd,mkdirCertCmd,certUrl,keyUrl,configRegistryCmd,loginCmd,dockerFile,buildCmd,pushCmd,pullCmd);
	}

	@Override
	public HarborGuideVo getTagGuide(String repoName, String tagName) {
		String harborBaseUrl = harborInfoConfiguration.getDomain();
		String loginCmd = String.format("docker login %s -u userName",harborBaseUrl);
		String pullCmd = String.format("docker pull %s/%s:%s",harborBaseUrl,repoName,tagName);
		return new HarborGuideVo(loginCmd,null,null,null,pullCmd);
	}
}
