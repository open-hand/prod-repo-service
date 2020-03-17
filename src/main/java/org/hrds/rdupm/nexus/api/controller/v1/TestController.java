package org.hrds.rdupm.nexus.api.controller.v1;

import io.choerodon.core.annotation.Permission;
import io.choerodon.core.domain.Page;
import io.choerodon.core.enums.ResourceType;
import io.choerodon.mybatis.pagehelper.annotation.SortDefault;
import io.choerodon.mybatis.pagehelper.domain.PageRequest;
import io.choerodon.mybatis.pagehelper.domain.Sort;
import io.swagger.annotations.ApiOperation;
import org.hrds.rdupm.nexus.client.nexus.NexusClient;
import org.hrds.rdupm.nexus.client.nexus.model.*;
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

	@ApiOperation(value = "test pro")
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

	@ApiOperation(value = "pro delete")
	@Permission(type = ResourceType.PROJECT, permissionPublic = true)
	@DeleteMapping("/rep/delete")
	public ResponseEntity<?> repDelete(@RequestParam("username") String username,
									   @RequestParam("password") String password,
									   @RequestParam("ip") String ip,
									   @RequestParam("repositoryName") String repositoryName) {
		NexusServer nexusServer = new NexusServer(ip, username, password);
		nexusClient.setNexusServerInfo(nexusServer);
		nexusClient.getRepositoryApi().deleteRepository(repositoryName);
		return Results.success();
	}

	@ApiOperation(value = "pro delete")
	@Permission(type = ResourceType.PROJECT, permissionPublic = true)
	@PostMapping("/rep/create")
	public ResponseEntity<?> repCreate(@RequestParam("username") String username,
									   @RequestParam("password") String password,
									   @RequestParam("ip") String ip,
									   @RequestBody RepositoryRequest repositoryRequest) {
		NexusServer nexusServer = new NexusServer(ip, username, password);
		nexusClient.setNexusServerInfo(nexusServer);
		nexusClient.getRepositoryApi().createMavenRepository(repositoryRequest);
		return Results.success();
	}

	@ApiOperation(value = "pro delete")
	@Permission(type = ResourceType.PROJECT, permissionPublic = true)
	@PutMapping("/rep/update")
	public ResponseEntity<?> repUpdate(@RequestParam("username") String username,
									   @RequestParam("password") String password,
									   @RequestParam("ip") String ip,
									   @RequestBody RepositoryRequest repositoryRequest) {
		NexusServer nexusServer = new NexusServer(ip, username, password);
		nexusClient.setNexusServerInfo(nexusServer);
		nexusClient.getRepositoryApi().updateMavenRepository(repositoryRequest);
		return Results.success();
	}

	@ApiOperation(value = "com/get")
	@Permission(type = ResourceType.PROJECT, permissionPublic = true)
	@GetMapping("/com/get")
	public ResponseEntity<List<NexusComponent>> comGet(@RequestParam("username") String username,
													   @RequestParam("password") String password,
													   @RequestParam("ip") String ip,
													   @RequestParam("repositoryName") String repositoryName) {
		NexusServer nexusServer = new NexusServer(ip, username, password);
		nexusClient.setNexusServerInfo(nexusServer);
		List<NexusComponent> componentList = nexusClient.getComponentsHttpApi().getComponents(repositoryName);
		return Results.success(componentList);
	}

	@Permission(type = ResourceType.PROJECT, permissionPublic = true)
	@GetMapping("/com/getInfo")
	public ResponseEntity<List<NexusComponentInfo>> comGetInfo(@RequestParam("username") String username,
															   @RequestParam("password") String password,
															   @RequestParam("ip") String ip,
															   @RequestParam("repositoryName") String repositoryName) {
		NexusServer nexusServer = new NexusServer(ip, username, password);
		nexusClient.setNexusServerInfo(nexusServer);
		List<NexusComponentInfo> componentInfoList = nexusClient.getComponentsHttpApi().getComponentInfo(repositoryName);
		return Results.success(componentInfoList);
	}

	@ApiOperation(value = "pro delete")
	@Permission(type = ResourceType.PROJECT, permissionPublic = true)
	@DeleteMapping("/com/delete")
	public ResponseEntity<?> comDelete(@RequestParam("username") String username,
									   @RequestParam("password") String password,
									   @RequestParam("ip") String ip,
									   @RequestParam("componentId") String componentId) {
		NexusServer nexusServer = new NexusServer(ip, username, password);
		nexusClient.setNexusServerInfo(nexusServer);
		nexusClient.getComponentsHttpApi().deleteComponent(componentId);
		return Results.success();
	}

	@ApiOperation(value = "com/get")
	@Permission(type = ResourceType.PROJECT, permissionPublic = true)
	@GetMapping("/pri/get")
	public ResponseEntity<List<NexusPrivilege>> priGet(@RequestParam("username") String username,
													   @RequestParam("password") String password,
													   @RequestParam("ip") String ip) {
		NexusServer nexusServer = new NexusServer(ip, username, password);
		nexusClient.setNexusServerInfo(nexusServer);
		List<NexusPrivilege> privilegeList = nexusClient.getPrivilegeApi().getPrivileges();
		return Results.success(privilegeList);
	}

	@Permission(type = ResourceType.PROJECT, permissionPublic = true)
	@GetMapping("/pri/getInfo")
	public ResponseEntity<List<NexusPrivilege>> priGetInfo(@RequestParam("username") String username,
															   @RequestParam("password") String password,
															   @RequestParam("ip") String ip,
															   @RequestParam("name") String name) {
		NexusServer nexusServer = new NexusServer(ip, username, password);
		nexusClient.setNexusServerInfo(nexusServer);
		List<NexusPrivilege> privilegeList = nexusClient.getPrivilegeApi().getPrivileges(name);
		return Results.success(privilegeList);
	}

	@Permission(type = ResourceType.PROJECT, permissionPublic = true)
	@GetMapping("/blo/get")
	public ResponseEntity<List<NexusBlobStore>> priGetInfo(@RequestParam("username") String username,
														   @RequestParam("password") String password,
														   @RequestParam("ip") String ip) {
		NexusServer nexusServer = new NexusServer(ip, username, password);
		nexusClient.setNexusServerInfo(nexusServer);
		List<NexusBlobStore> blobStoreList = nexusClient.getBlobStoreApi().getBlobStore();
		return Results.success(blobStoreList);
	}
}
