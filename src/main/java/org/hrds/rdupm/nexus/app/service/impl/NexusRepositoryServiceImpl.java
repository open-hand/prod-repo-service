package org.hrds.rdupm.nexus.app.service.impl;

import io.choerodon.core.exception.CommonException;
import org.hrds.rdupm.nexus.api.dto.NexusRepositoryCreateDTO;
import org.hrds.rdupm.nexus.api.dto.ProdRepositoryDTO;
import org.hrds.rdupm.nexus.app.service.NexusRepositoryService;
import org.hrds.rdupm.nexus.app.service.NexusServerConfigService;
import org.hrds.rdupm.nexus.client.nexus.NexusClient;
import org.hrds.rdupm.nexus.client.nexus.constant.NexusApiConstants;
import org.hrds.rdupm.nexus.client.nexus.model.NexusServer;
import org.hrds.rdupm.nexus.client.nexus.model.NexusServerRole;
import org.hrds.rdupm.nexus.client.nexus.model.NexusServerUser;
import org.hrds.rdupm.nexus.domain.entity.NexusServerConfig;
import org.hrds.rdupm.nexus.domain.entity.ProdRepository;
import org.hrds.rdupm.nexus.domain.repository.NexusServerConfigRepository;
import org.hrds.rdupm.nexus.domain.repository.ProdRepositoryRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

/**
 * 制品库_nexus仓库信息表应用服务默认实现
 *
 * @author weisen.yang@hand-china.com 2020-03-27 11:43:00
 */
@Service
public class NexusRepositoryServiceImpl implements NexusRepositoryService {

	@Autowired
	private ProdRepositoryRepository prodRepositoryRepository;
	@Autowired
	private NexusClient nexusClient;
	@Autowired
	private NexusServerConfigService configService;

	@Override
	@Transactional(rollbackFor = Exception.class)
	public ProdRepositoryDTO createProdRepo(Long tenantId, ProdRepositoryDTO prodRepositoryDTO) {
		ProdRepository prodRepository = new ProdRepository();
		prodRepository.setType(prodRepositoryDTO.getType());
		prodRepository.setName(prodRepositoryDTO.getType().toLowerCase());
		prodRepository.setTenantId(tenantId);
		prodRepositoryRepository.insertSelective(prodRepository);

		if (prodRepositoryDTO.getNexusRepoFlag() != null && prodRepositoryDTO.getNexusRepoFlag()
				&& prodRepositoryDTO.getProdMavenDTO() != null) {
			// nexus仓库创建 TODO
		}
		return prodRepositoryDTO;
	}

	@Override
	public NexusRepositoryCreateDTO createMavenRepo(Long tenantId, Long projectId, NexusRepositoryCreateDTO nexusRepoCreateDTO) {

		// 步骤
		// 1. 创建仓库
		// 2. 创建仓库默认角色，赋予权限：nx-repository-view-maven2-[仓库名]-*
		// 3. 创建仓库默认用户，默认赋予角色，上述创建的角色
		// 4. 是否允许匿名
		//     允许，赋予匿名用户权限：nx-repository-view-maven2-[仓库名]-read   nx-repository-view-maven2-[仓库名]-browse
		//     不允许，去除匿名用户权限：nx-repository-view-maven2-[仓库名]-read   nx-repository-view-maven2-[仓库名]-browse
		// 5. 更新数据库数据


		NexusServerConfig serverConfig = configService.setNexusInfo(nexusClient);

		// 创建仓库
		switch (nexusRepoCreateDTO.getType()) {
			case NexusApiConstants.RepositoryType.HOSTED:
				// 创建本地仓库
				nexusClient.getRepositoryApi().createMavenRepository(nexusRepoCreateDTO.convertMavenHostedRequest());
				break;
			case NexusApiConstants.RepositoryType.PROXY:
				// 创建代理仓库
				nexusClient.getRepositoryApi().createAndUpdateMavenProxy(nexusRepoCreateDTO.convertMavenProxyRequest());
				break;
			case NexusApiConstants.RepositoryType.GROUP:
				// 创建仓库组
				nexusClient.getRepositoryApi().createAndUpdateMavenGroup(nexusRepoCreateDTO.convertMavenGroupRequest());
				break;
			default:break;
		}

		// 创建默认角色
		NexusServerRole nexusServerRole = new NexusServerRole();
		nexusServerRole.createDefRole(nexusRepoCreateDTO.getName());
		nexusClient.getNexusRoleApi().createRole(nexusServerRole);

		// 创建默认用户
		NexusServerUser nexusServerUser = new NexusServerUser();
		nexusServerUser.createDefUser(nexusRepoCreateDTO.getName(), nexusServerRole.getId());
		nexusClient.getNexusUserApi().createUser(nexusServerUser);

		// 匿名访问出来
		if (nexusRepoCreateDTO.getAllowAnonymous() == 1) {
			// 允许匿名
			NexusServerRole anonymousRole = nexusClient.getNexusRoleApi().getRoleById(serverConfig.getAnonymousRole());
			anonymousRole.setAnonymousPri(nexusRepoCreateDTO.getName(), 1);
			nexusClient.getNexusRoleApi().updateRole(anonymousRole);
		}

		// 数据库更新



		nexusClient.removeNexusServerInfo();
		return null;
	}
}
