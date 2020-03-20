package org.hrds.rdupm.nexus.api.controller.v1;

import io.choerodon.core.annotation.Permission;
import io.choerodon.core.enums.ResourceType;
import io.choerodon.core.exception.CommonException;
import io.swagger.annotations.ApiOperation;
import org.apache.commons.io.IOUtils;
import org.hrds.rdupm.nexus.client.nexus.NexusClient;
import org.hrds.rdupm.nexus.client.nexus.model.*;
import org.hrds.rdupm.nexus.infra.constant.NexusMessageConstants;
import org.hzero.core.base.BaseController;
import org.hzero.core.util.Results;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.io.FileSystemResource;
import org.springframework.core.io.InputStreamResource;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.io.InputStream;
import java.util.ArrayList;
import java.util.List;

/**
 * @author weisen.yang@hand-china.com 2020/3/16
 */
@RestController("TestController.v1")
@RequestMapping("/v1/test/")
public class TestController extends BaseController{

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
									   @RequestBody RepositoryMavenRequest repositoryRequest) {
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
									   @RequestBody RepositoryMavenRequest repositoryRequest) {
		NexusServer nexusServer = new NexusServer(ip, username, password);
		nexusClient.setNexusServerInfo(nexusServer);
		nexusClient.getRepositoryApi().updateMavenRepository(repositoryRequest);
		return Results.success();
	}

	@ApiOperation(value = "pro group")
	@Permission(type = ResourceType.PROJECT, permissionPublic = true)
	@PostMapping("/rep/group")
	public ResponseEntity<?> repGroup(@RequestParam("username") String username,
									   @RequestParam("password") String password,
									   @RequestParam("ip") String ip,
									   @RequestBody NexusMavenGroup nexusMavenGroup) {
		NexusServer nexusServer = new NexusServer(ip, username, password);
		nexusClient.setNexusServerInfo(nexusServer);
		nexusClient.getRepositoryApi().createMavenGroup(nexusMavenGroup);
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

	@ApiOperation(value = "pro upload")
	@Permission(type = ResourceType.PROJECT, permissionPublic = true)
	@PostMapping("/com/upload")
	public ResponseEntity<?> comUpload(@RequestParam("username") String username,
									   @RequestParam("password") String password,
									   @RequestParam("ip") String ip,
									   NexusComponentUpload componentUpload,
									   @RequestParam(name = "assetJar", required = false) MultipartFile assetJar,
									   @RequestParam(name = "assetPom", required = false) MultipartFile assetPom) {
		NexusServer nexusServer = new NexusServer(ip, username, password);
		nexusClient.setNexusServerInfo(nexusServer);
		if (assetJar == null && assetPom == null) {
			throw new CommonException(NexusMessageConstants.NEXUS_SELECT_FILE);
		}
		this.validateFileType(assetJar, NexusAssetUpload.JAR);
		this.validateFileType(assetPom, NexusAssetUpload.XML);
		try (
				InputStream assetJarStream = assetJar != null ? assetJar.getInputStream() : null;
				InputStream assetPomStream = assetPom != null ? assetPom.getInputStream() : null
		) {
			List<NexusAssetUpload> assetUploadList = new ArrayList<>();
			if (assetJarStream != null) {
				NexusAssetUpload assetUpload = new NexusAssetUpload();
				assetUpload.setAssetName(new InputStreamResource(assetJarStream));
				assetUpload.setExtension(NexusAssetUpload.JAR);
				assetUploadList.add(assetUpload);
			}
			if (assetPomStream != null) {
				NexusAssetUpload assetUpload = new NexusAssetUpload();
				assetUpload.setAssetName(new InputStreamResource(assetPomStream));
				assetUpload.setExtension(NexusAssetUpload.POM);
				assetUploadList.add(assetUpload);
			}
			componentUpload.setAssetUploads(assetUploadList);
			nexusClient.getComponentsHttpApi().createMavenComponent(componentUpload);
		} catch (IOException e) {
			throw new CommonException(e.getMessage());
		}
		return Results.success();
	}

	private void validateFileType(MultipartFile file, String type){
		if (file != null) {
			String name = file.getOriginalFilename();
			String sourceType = name.substring(name.lastIndexOf(".")+1);
			if (!type.equals(sourceType)) {
				throw new CommonException(NexusMessageConstants.NEXUS_FILE_TYPE_ERROR);
			}
		}
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



	@ApiOperation(value = "test role")
	@Permission(type = ResourceType.PROJECT, permissionPublic = true)
	@GetMapping("/role/getList")
	public ResponseEntity<List<NexusRole>> roleList(@RequestParam("username") String username,
													@RequestParam("password") String password,
													@RequestParam("ip") String ip) {
		NexusServer nexusServer = new NexusServer(ip, username, password);
		nexusClient.setNexusServerInfo(nexusServer);
		List<NexusRole> nexusRoleList =  nexusClient.getNexusRoleApi().getRoles();
		return Results.success(nexusRoleList);
	}
	@ApiOperation(value = "test role")
	@Permission(type = ResourceType.PROJECT, permissionPublic = true)
	@GetMapping("/role/get")
	public ResponseEntity<NexusRole> roleGet(@RequestParam("username") String username,
											 @RequestParam("password") String password,
											 @RequestParam("ip") String ip,
											 @RequestParam("roleId") String roleId) {
		NexusServer nexusServer = new NexusServer(ip, username, password);
		nexusClient.setNexusServerInfo(nexusServer);
		NexusRole nexusRole =  nexusClient.getNexusRoleApi().getRoleById(roleId);
		return Results.success(nexusRole);
	}

	@ApiOperation(value = "role delete")
	@Permission(type = ResourceType.PROJECT, permissionPublic = true)
	@DeleteMapping("/role/delete")
	public ResponseEntity<?> roleDelete(@RequestParam("username") String username,
									    @RequestParam("password") String password,
									    @RequestParam("ip") String ip,
										@RequestParam("roleId") String roleId) {
		NexusServer nexusServer = new NexusServer(ip, username, password);
		nexusClient.setNexusServerInfo(nexusServer);
		nexusClient.getNexusRoleApi().deleteRole(roleId);
		return Results.success();
	}

	@ApiOperation(value = "role delete")
	@Permission(type = ResourceType.PROJECT, permissionPublic = true)
	@PostMapping("/role/create")
	public ResponseEntity<?> roleCreate(@RequestParam("username") String username,
										@RequestParam("password") String password,
									    @RequestParam("ip") String ip,
									    @RequestBody NexusRole nexusRole) {
		NexusServer nexusServer = new NexusServer(ip, username, password);
		nexusClient.setNexusServerInfo(nexusServer);
		nexusClient.getNexusRoleApi().createRole(nexusRole);
		return Results.success();
	}

	@ApiOperation(value = "role delete")
	@Permission(type = ResourceType.PROJECT, permissionPublic = true)
	@PutMapping("/role/update")
	public ResponseEntity<?> roleUpdate(@RequestParam("username") String username,
									    @RequestParam("password") String password,
									    @RequestParam("ip") String ip,
										@RequestBody NexusRole nexusRole) {
		NexusServer nexusServer = new NexusServer(ip, username, password);
		nexusClient.setNexusServerInfo(nexusServer);
		nexusClient.getNexusRoleApi().updateRole(nexusRole);
		return Results.success();
	}



	@ApiOperation(value = "test user")
	@Permission(type = ResourceType.PROJECT, permissionPublic = true)
	@GetMapping("/user/getList")
	public ResponseEntity<List<NexusUser>> userList(@RequestParam("username") String username,
													@RequestParam("password") String password,
													@RequestParam("ip") String ip,
													@RequestParam(name = "userId", required = false) String userId) {
		NexusServer nexusServer = new NexusServer(ip, username, password);
		nexusClient.setNexusServerInfo(nexusServer);
		List<NexusUser> nexusUserList =  nexusClient.getNexusUserApi().getUsers(userId);
		nexusClient.removeNexusServerInfo();
		return Results.success(nexusUserList);
	}

	@ApiOperation(value = "user delete")
	@Permission(type = ResourceType.PROJECT, permissionPublic = true)
	@DeleteMapping("/user/delete")
	public ResponseEntity<?> userDelete(@RequestParam("username") String username,
										@RequestParam("password") String password,
										@RequestParam("ip") String ip,
										@RequestParam("userId") String userId) {
		NexusServer nexusServer = new NexusServer(ip, username, password);
		nexusClient.setNexusServerInfo(nexusServer);
		nexusClient.getNexusUserApi().deleteUser(userId);
		return Results.success();
	}

	@ApiOperation(value = "user delete")
	@Permission(type = ResourceType.PROJECT, permissionPublic = true)
	@PostMapping("/user/create")
	public ResponseEntity<?> userCreate(@RequestParam("username") String username,
										@RequestParam("password") String password,
										@RequestParam("ip") String ip,
										@RequestBody NexusUser nexusUser) {
		NexusServer nexusServer = new NexusServer(ip, username, password);
		nexusClient.setNexusServerInfo(nexusServer);
		nexusClient.getNexusUserApi().createUser(nexusUser);
		return Results.success();
	}

	@ApiOperation(value = "user user")
	@Permission(type = ResourceType.PROJECT, permissionPublic = true)
	@PutMapping("/user/update")
	public ResponseEntity<?> userUpdate(@RequestParam("username") String username,
										@RequestParam("password") String password,
										@RequestParam("ip") String ip,
										@RequestBody NexusUser nexusUser) {
		NexusServer nexusServer = new NexusServer(ip, username, password);
		nexusClient.setNexusServerInfo(nexusServer);
		nexusClient.getNexusUserApi().updateUser(nexusUser);
		return Results.success();
	}

	@ApiOperation(value = "user user")
	@Permission(type = ResourceType.PROJECT, permissionPublic = true)
	@PutMapping("/user/changePassword")
	public ResponseEntity<?> changePassword(@RequestParam("username") String username,
											@RequestParam("password") String password,
											@RequestParam("ip") String ip,
											@RequestParam("userId") String userId,
											@RequestParam("newPassword") String newPassword,
											@RequestParam("oldPassword") String oldPassword) {
		NexusServer nexusServer = new NexusServer(ip, username, password);
		nexusClient.setNexusServerInfo(nexusServer);
		nexusClient.getNexusUserApi().changePassword(userId, newPassword, oldPassword);
		return Results.success();
	}


	@ApiOperation(value = "script upload")
	@Permission(type = ResourceType.PROJECT, permissionPublic = true)
	@PostMapping("/script/upload")
	public ResponseEntity<?> scriptUpload(@RequestParam("username") String username,
										  @RequestParam("password") String password,
										  @RequestParam("ip") String ip,
										  @RequestBody NexusScript nexusScript){
		NexusServer nexusServer = new NexusServer(ip, username, password);
		nexusClient.setNexusServerInfo(nexusServer);
		nexusClient.getNexusScriptApi().uploadScript(nexusScript);
		return Results.success();
	}


	@ApiOperation(value = "script upload")
	@Permission(type = ResourceType.PROJECT, permissionPublic = true)
	@PostMapping("/script/update")
	public ResponseEntity<?> scriptUpdate(@RequestParam("username") String username,
										  @RequestParam("password") String password,
										  @RequestParam("ip") String ip,
										  @RequestBody NexusScript nexusScript){
		NexusServer nexusServer = new NexusServer(ip, username, password);
		nexusClient.setNexusServerInfo(nexusServer);
		nexusClient.getNexusScriptApi().updateScript(nexusScript.getName(), nexusScript);
		return Results.success();
	}
	@ApiOperation(value = "script upload")
	@Permission(type = ResourceType.PROJECT, permissionPublic = true)
	@DeleteMapping("/script/delete")
	public ResponseEntity<?> scriptDelete(@RequestParam("username") String username,
										  @RequestParam("password") String password,
										  @RequestParam("ip") String ip,
										  @RequestParam("scriptName") String scriptName){
		NexusServer nexusServer = new NexusServer(ip, username, password);
		nexusClient.setNexusServerInfo(nexusServer);
		nexusClient.getNexusScriptApi().deleteScript(scriptName);
		return Results.success();
	}
}
