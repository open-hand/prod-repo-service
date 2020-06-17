package org.hrds.rdupm.nexus.app.eventhandler.payload;

import lombok.Getter;
import lombok.Setter;
import org.hrds.rdupm.nexus.domain.entity.NexusAuth;
import org.hrds.rdupm.nexus.domain.entity.NexusRepository;
import org.hrds.rdupm.nexus.domain.entity.NexusRole;
import org.hrds.rdupm.nexus.domain.entity.NexusUser;

import java.util.List;

/**
 * nexus maven 仓库删除
 * @author weisen.yang@hand-china.com 2020/4/10
 */
@Getter
@Setter
public class NexusRepositoryDeletePayload {
	private NexusRepository nexusRepository;
	private NexusRole nexusRole;
	private NexusUser nexusUser;
	private List<NexusAuth> nexusAuthList;

}
