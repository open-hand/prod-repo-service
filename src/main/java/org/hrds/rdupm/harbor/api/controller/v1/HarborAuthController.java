package org.hrds.rdupm.harbor.api.controller.v1;

import java.util.HashSet;
import java.util.List;
import java.util.Set;

import javax.annotation.Resource;
import javax.servlet.http.HttpServletResponse;

import io.choerodon.core.domain.Page;
import io.choerodon.core.oauth.DetailsHelper;
import io.choerodon.swagger.annotation.Permission;
import io.choerodon.core.iam.ResourceLevel;
import io.swagger.annotations.ApiParam;
import org.hrds.rdupm.harbor.app.service.C7nBaseService;
import org.hrds.rdupm.harbor.app.service.HarborAuthService;
import org.hrds.rdupm.harbor.infra.feign.dto.UserDTO;
import org.hrds.rdupm.harbor.infra.util.HarborUtil;
import org.hzero.core.util.Results;
import org.hzero.core.base.BaseController;
import org.hrds.rdupm.harbor.domain.entity.HarborAuth;
import org.hrds.rdupm.harbor.domain.repository.HarborAuthRepository;
import org.hzero.export.vo.ExportParam;
import org.hzero.mybatis.domian.Condition;
import org.hzero.mybatis.util.Sqls;
import org.hzero.starter.keyencrypt.core.Encrypt;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.hzero.mybatis.helper.SecurityTokenHelper;

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

    @Autowired
	private C7nBaseService c7nBaseService;

    @Autowired
	private HarborAuthService harborAuthService;

    @ApiOperation(value = "项目层--权限列表")
	@Permission(level = ResourceLevel.ORGANIZATION)
	@GetMapping(value = "/list-project/{projectId}")
	public ResponseEntity<Page<HarborAuth>> listByProject(@ApiParam("猪齿鱼项目ID") @PathVariable Long projectId,
												 @ApiParam("登录名") @RequestParam(required = false) String loginName,
												 @ApiParam("用户名") @RequestParam(required = false) String realName,
												 @ApiParam("权限角色名称") @RequestParam(required = false) String harborRoleName,
												 @ApiParam("权限角色") @RequestParam(required = false) String harborRoleValue,
												 @ApiParam("params") @RequestParam(required = false) String params,
												 @ApiIgnore PageRequest pageRequest) {
		HarborAuth harborAuth = new HarborAuth(projectId,loginName,realName,harborRoleName);
		harborAuth.setHarborRoleValue(harborRoleValue);
		harborAuth.setParams(params);
		harborAuth.setOrganizationId(DetailsHelper.getUserDetails().getTenantId());
		Page<HarborAuth> list = harborAuthService.pageList(pageRequest, harborAuth);
        return Results.success(list);
    }

	@ApiOperation(value = "组织层--权限列表")
	@Permission(level = ResourceLevel.ORGANIZATION)
	@GetMapping(value = "/list-org/{organizationId}")
	public ResponseEntity<Page<HarborAuth>> listByOrg(@ApiParam("猪齿鱼组织ID") @PathVariable Long organizationId,
													 @ApiParam("镜像仓库编码") @RequestParam(required = false) String code,
													 @ApiParam("镜像仓库名称") @RequestParam(required = false) String name,
													 @ApiParam("登录名") @RequestParam(required = false) String loginName,
													 @ApiParam("用户名") @RequestParam(required = false) String realName,
													 @ApiParam("权限角色名称") @RequestParam(required = false) String harborRoleName,
													  @ApiParam("params") @RequestParam(required = false) String params,
													  @ApiIgnore PageRequest pageRequest) {
		HarborAuth harborAuth = new HarborAuth(loginName,realName,organizationId,code,name,harborRoleName);
		harborAuth.setParams(params);
		Page<HarborAuth> list = harborAuthService.pageList(pageRequest, harborAuth);
		return Results.success(list);
	}

    @ApiOperation(value = "项目层--权限明细")
	@Permission(level = ResourceLevel.ORGANIZATION)
    @GetMapping("/detail/{authId}")
    public ResponseEntity<HarborAuth> detail(@Encrypt(HarborAuth.ENCRYPT_KEY) @PathVariable Long authId) {
        HarborAuth harborAuth = harborAuthRepository.selectByCondition(Condition.builder(HarborAuth.class).where(Sqls.custom()
				.andEqualTo(HarborAuth.FIELD_ORGANIZATION_ID,DetailsHelper.getUserDetails().getTenantId())
				.andEqualTo(HarborAuth.FIELD_AUTH_ID,authId)
		).build()).stream().findFirst().orElse(null);
        return Results.success(harborAuth);
    }

    @ApiOperation(value = "项目层--分配权限")
	@Permission(level = ResourceLevel.ORGANIZATION)
    @PostMapping("/create/{projectId}")
    public ResponseEntity<List<HarborAuth>> create(@ApiParam("猪齿鱼项目ID") @PathVariable Long projectId,
											 @RequestBody @ApiParam("必输字段endDate、harborRoleValue、userId") List<HarborAuth> dtoList) {
        validObject(dtoList);
		harborAuthService.save(projectId,dtoList);
        return Results.success(dtoList);
    }

    @ApiOperation(value = "项目层--更新权限")
	@Permission(level = ResourceLevel.ORGANIZATION)
    @PutMapping
    public ResponseEntity<HarborAuth> update(@RequestBody HarborAuth harborAuth) {
        SecurityTokenHelper.validToken(harborAuth);
		harborAuthService.update(harborAuth);
        return Results.success(harborAuth);
    }

    @ApiOperation(value = "项目层--删除权限")
	@Permission(level = ResourceLevel.ORGANIZATION)
    @DeleteMapping
    public ResponseEntity<?> remove(@RequestBody HarborAuth harborAuth) {
        SecurityTokenHelper.validToken(harborAuth);
		harborAuthService.delete(harborAuth);
        return Results.success();
    }

	@ApiOperation(value = "项目层--获取项目下的团队成员（猪齿鱼接口）")
	@Permission(level = ResourceLevel.ORGANIZATION)
	@GetMapping("/list-project-member/{projectId}")
	public ResponseEntity<List<UserDTO>> getUserList(@PathVariable Long projectId,
													 @ApiParam("条件模糊查询") @RequestParam(required = false) String param) {
		List<UserDTO> userDTOList = c7nBaseService.listProjectUsersByIdName(projectId,null);
		return Results.success(userDTOList);
	}

	@ApiOperation(value = "项目层--导出权限")
	@Permission(level = ResourceLevel.ORGANIZATION)
	@GetMapping("/export/project/{projectId}")
	public ResponseEntity<Page<HarborAuth>> projectExport(@ApiParam("猪齿鱼项目ID") @PathVariable Long projectId,
														   @ApiParam("登录名") @RequestParam(required = false) String loginName,
														   @ApiParam("用户名") @RequestParam(required = false) String realName,
														   @ApiParam("权限角色名称") @RequestParam(required = false) String harborRoleName,
														   @ApiIgnore @SortDefault(value = HarborAuth.FIELD_AUTH_ID, direction = Sort.Direction.DESC) PageRequest pageRequest,
														  @ApiParam("导出，输入exportType=DATA即可") ExportParam exportParam,
														   HttpServletResponse response) {
		HarborUtil.setIds(exportParam);
		HarborAuth harborAuth = new HarborAuth(projectId,loginName,realName,harborRoleName);
		return Results.success(harborAuthService.export(pageRequest, harborAuth, exportParam, response));
	}

	@ApiOperation(value = "组织层--导出权限")
	@Permission(level = ResourceLevel.ORGANIZATION)
	@GetMapping("/export/organization/{organizationId}")
	public ResponseEntity<Page<HarborAuth>> orgExport(@ApiParam("猪齿鱼组织ID") @PathVariable Long organizationId,
															   @ApiParam("镜像仓库编码") @RequestParam(required = false) String code,
															   @ApiParam("镜像仓库名称") @RequestParam(required = false) String name,
															   @ApiParam("登录名") @RequestParam(required = false) String loginName,
															   @ApiParam("用户名") @RequestParam(required = false) String realName,
															   @ApiParam("权限角色名称") @RequestParam(required = false) String harborRoleName,
													           @ApiParam("导出，输入exportType=DATA即可") ExportParam exportParam, HttpServletResponse response, PageRequest pageRequest) {
		HarborUtil.setIds(exportParam);
		HarborAuth harborAuth = new HarborAuth(loginName,realName,organizationId,code,name,harborRoleName);
		return Results.success(harborAuthService.export(pageRequest, harborAuth, exportParam, response));
	}
}
