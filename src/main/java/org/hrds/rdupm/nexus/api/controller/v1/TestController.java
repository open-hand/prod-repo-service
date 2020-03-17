package org.hrds.rdupm.nexus.api.controller.v1;

import io.choerodon.core.annotation.Permission;
import io.choerodon.core.domain.Page;
import io.choerodon.core.enums.ResourceType;
import io.choerodon.mybatis.pagehelper.annotation.SortDefault;
import io.choerodon.mybatis.pagehelper.domain.PageRequest;
import io.choerodon.mybatis.pagehelper.domain.Sort;
import io.swagger.annotations.ApiOperation;
import org.hrds.rdupm.nexus.client.nexus.NexusClient;
import org.hrds.rdupm.nexus.client.nexus.model.NexusRepository;
import org.hrds.rdupm.nexus.client.nexus.model.NexusServer;
import org.hzero.core.util.Results;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import springfox.documentation.annotations.ApiIgnore;

import java.util.List;

/**
 * @author weisen.yang@hand-china.com 2020/3/16
 */
@RestController("TestController.v1")
@RequestMapping("/v1/test/")
public class TestController {

	@Autowired
	private NexusClient nexusClient;

	@ApiOperation(value = "test")
	@Permission(type = ResourceType.PROJECT, permissionPublic = true)
	@GetMapping("/getString")
	public ResponseEntity<List<NexusRepository>> list(@RequestParam("username") String username,
													  @RequestParam("password") String password,
													  @RequestParam("ip") String ip) {
		NexusServer nexusServer = new NexusServer(ip, username, password);
		nexusClient.setNexusServerInfo(nexusServer);
		List<NexusRepository> repositoryList =  nexusClient.getRepositoryApi().getRepository();
		return Results.success(repositoryList);
	}

	@ApiOperation(value = "test")
	@Permission(type = ResourceType.PROJECT, permissionPublic = true)
	@DeleteMapping("/rep/delete")
	public ResponseEntity<String> list(@RequestParam("username") String username,
									   @RequestParam("password") String password,
									   @RequestParam("ip") String ip,
									   @RequestParam("repositoryName") String repositoryName) {
		NexusServer nexusServer = new NexusServer(ip, username, password);
		nexusClient.setNexusServerInfo(nexusServer);
		String repose = nexusClient.getRepositoryApi().deleteRepository(repositoryName);
		return Results.success(repose);
	}
}
