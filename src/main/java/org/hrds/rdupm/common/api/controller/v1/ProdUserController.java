package org.hrds.rdupm.common.api.controller.v1;

import io.choerodon.core.exception.CommonException;
import io.choerodon.core.oauth.DetailsHelper;
import io.choerodon.swagger.annotation.Permission;
import io.choerodon.core.iam.ResourceLevel;
import io.swagger.annotations.ApiParam;
import org.apache.commons.collections.CollectionUtils;
import org.hrds.rdupm.common.api.vo.ProductLibraryDTO;
import org.hrds.rdupm.common.app.service.ProdUserService;
import org.hrds.rdupm.harbor.app.service.HarborAuthService;
import org.hrds.rdupm.harbor.domain.entity.HarborAuth;
import org.hrds.rdupm.harbor.domain.repository.HarborAuthRepository;
import org.hrds.rdupm.nexus.domain.entity.NexusRepository;
import org.hrds.rdupm.nexus.domain.repository.NexusAuthRepository;
import org.hzero.core.util.Results;
import org.hzero.core.base.BaseController;
import org.hrds.rdupm.common.domain.entity.ProdUser;
import org.hrds.rdupm.common.domain.repository.ProdUserRepository;
import org.hzero.starter.keyencrypt.core.Encrypt;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import io.swagger.annotations.ApiOperation;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

/**
 * 制品库-制品用户表 管理 API
 *
 * @author xiuhong.chen@hand-china.com 2020-05-21 15:47:14
 */
@RestController("prodUserController.v1")
@RequestMapping("/v1/prod-users")
public class ProdUserController extends BaseController {

    @Autowired
    private ProdUserRepository prodUserRepository;

    @Autowired
	private ProdUserService prodUserService;
    @Autowired
	private NexusAuthRepository nexusAuthRepository;
    @Autowired
	private HarborAuthRepository harborAuthRepository;

    @ApiOperation(value = "个人层--查询制品库用户信息")
	@Permission(level = ResourceLevel.ORGANIZATION,permissionLogin = true)
    @GetMapping("/{userId}")
    public ResponseEntity<ProdUser> detail(@PathVariable @ApiParam("猪齿鱼用户ID") Long userId) {
    	if(!userId.equals(DetailsHelper.getUserDetails().getUserId())){
    		throw new CommonException("error.user.not.current.user");
		}
        ProdUser prodUser = prodUserRepository.select(ProdUser.FIELD_USER_ID,userId).stream().findFirst().orElse(null);
        if(prodUser != null && prodUser.getPwdUpdateFlag().intValue()==1){
        	prodUser.setPassword(null);
		}
        return Results.success(prodUser);
    }

	@ApiOperation(value = "个人层--修改制品库用户默认密码")
	@Permission(level = ResourceLevel.ORGANIZATION,permissionLogin = true)
	@PostMapping("/updatePwd")
	public ResponseEntity<ProdUser> updatePwd(@RequestBody @ApiParam("必输字段用户IDuserId、旧密码oldPassword、新密码password、确认密码rePassword") ProdUser prodUser) {
		prodUserService.updatePwd(prodUser);
		return Results.success();
	}

	@ApiOperation(value = "项目层--获取当前用户，对应仓库分配的权限")
	@Permission(level = ResourceLevel.ORGANIZATION,permissionLogin = true)
	@PostMapping("/getRoleList")
	public ResponseEntity<Map<String, Map<Long, List<String>>>> getRoleList(@RequestBody List<NexusRepository> nexusRepositories,
																			@ApiParam(value = "项目Id", required = true) @RequestParam Long projectId) {

		Map<String, Map<Long, List<String>>> resultMap = new HashMap<>(6);
		// DOCKER
		Map<Long, List<String>> dockerMap = new HashMap<>();
		List<String> dockerCode = harborAuthRepository.getHarborRoleList(projectId);
		dockerMap.put(projectId, dockerCode != null ? dockerCode : new ArrayList<>());
		resultMap.put(ProductLibraryDTO.TYPE_DOCKER, dockerMap);

		// MAVEN、NPM
		List<Long> repositoryIds = new ArrayList<>();
		if (CollectionUtils.isNotEmpty(nexusRepositories)) {
			repositoryIds = nexusRepositories.stream().map(NexusRepository::getRepositoryId).collect(Collectors.toList());
		}
		Map<String, Map<Long, List<String>>> nexusMap = nexusAuthRepository.getRoleList(repositoryIds);
		nexusMap.forEach(resultMap::put);
		return Results.success(resultMap);
	}

}
