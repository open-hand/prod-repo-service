package org.hrds.rdupm.harbor.api.controller.v1;

import java.util.List;

import javax.annotation.Resource;

import com.github.pagehelper.PageInfo;
import io.choerodon.core.annotation.Permission;
import io.choerodon.core.enums.ResourceType;
import io.swagger.annotations.ApiParam;
import org.hrds.rdupm.harbor.app.service.HarborAuthService;
import org.hrds.rdupm.harbor.infra.feign.BaseFeignClient;
import org.hrds.rdupm.harbor.infra.feign.dto.UserDTO;
import org.hzero.core.util.Results;
import org.hzero.core.base.BaseController;
import org.hrds.rdupm.harbor.domain.entity.HarborAuth;
import org.hrds.rdupm.harbor.domain.repository.HarborAuthRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.hzero.mybatis.helper.SecurityTokenHelper;

import io.choerodon.core.domain.Page;
import io.choerodon.mybatis.pagehelper.annotation.SortDefault;
import io.choerodon.mybatis.pagehelper.domain.PageRequest;
import io.choerodon.mybatis.pagehelper.domain.Sort;
import io.swagger.annotations.ApiOperation;
import springfox.documentation.annotations.ApiIgnore;

/**
 * 制品库-harbor权限表 管理 API
 *
 * @author xiuhong.chen@hand-china.com 2020-04-27 16:12:54
 */
@RestController("harborAuthController.v1")
@RequestMapping("/v1/harbor-auths")
public class HarborAuthController extends BaseController {

    @Autowired
    private HarborAuthRepository harborAuthRepository;

    @Resource
	private BaseFeignClient baseFeignClient;

    @Autowired
	private HarborAuthService harborAuthService;

    @ApiOperation(value = "项目层-权限列表")
	@Permission(type = ResourceType.PROJECT, permissionPublic = true)
	@GetMapping(value = "/list-project/{projectId}")
	public ResponseEntity<PageInfo<HarborAuth>> list(@ApiParam("猪齿鱼项目ID") @PathVariable Long projectId,
												 @ApiParam("参数DTO") HarborAuth harborAuth,
												 @ApiIgnore @SortDefault(value = HarborAuth.FIELD_AUTH_ID, direction = Sort.Direction.DESC) PageRequest pageRequest) {
		harborAuth.setProjectId(projectId);
    	PageInfo<HarborAuth> list = harborAuthService.pageList(pageRequest, harborAuth);
        return Results.success(list);
    }

    @ApiOperation(value = "项目层--权限明细")
	@Permission(type = ResourceType.PROJECT, permissionPublic = true)
    @GetMapping("/detail/{authId}")
    public ResponseEntity<HarborAuth> detail(@PathVariable Long authId) {
        HarborAuth harborAuth = harborAuthRepository.selectByPrimaryKey(authId);
        return Results.success(harborAuth);
    }

    @ApiOperation(value = "项目层--分配权限")
	@Permission(type = ResourceType.PROJECT, permissionPublic = true)
    @PostMapping("/create/{projectId}")
    public ResponseEntity<List<HarborAuth>> create(@ApiParam("猪齿鱼项目ID") @PathVariable Long projectId,
											 @RequestBody List<HarborAuth> dtoList) {
        validObject(dtoList);
		harborAuthService.save(projectId,dtoList);
        return Results.success(dtoList);
    }

    @ApiOperation(value = "项目层--更新权限")
	@Permission(type = ResourceType.PROJECT, permissionPublic = true)
    @PutMapping
    public ResponseEntity<HarborAuth> update(@RequestBody HarborAuth harborAuth) {
        SecurityTokenHelper.validToken(harborAuth);
		harborAuthService.update(harborAuth);
        return Results.success(harborAuth);
    }

    @ApiOperation(value = "项目层--删除权限")
	@Permission(type = ResourceType.PROJECT, permissionPublic = true)
    @DeleteMapping
    public ResponseEntity<?> remove(@RequestBody HarborAuth harborAuth) {
        SecurityTokenHelper.validToken(harborAuth);
		harborAuthService.delete(harborAuth);
        return Results.success();
    }

	@ApiOperation(value = "猪齿鱼接口--根据projectId查询项目下的团队成员")
	@Permission(type = ResourceType.PROJECT,permissionPublic = true)
	@GetMapping("/list-project-member/{projectId}")
	public ResponseEntity<List<UserDTO>> getUserList(@PathVariable Long projectId,
													 @ApiParam("条件模糊查询") @RequestParam(required = false) String param) {
		List<UserDTO> userDTOList = baseFeignClient.listUsersByName(projectId,null).getBody();
		return Results.success(userDTOList);
	}
}
