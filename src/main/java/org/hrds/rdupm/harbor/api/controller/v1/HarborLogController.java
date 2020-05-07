package org.hrds.rdupm.harbor.api.controller.v1;

import java.util.Date;

import com.github.pagehelper.PageInfo;
import io.choerodon.core.annotation.Permission;
import io.choerodon.core.enums.ResourceType;
import io.swagger.annotations.ApiParam;
import org.hrds.rdupm.harbor.api.vo.HarborImageLog;
import org.hrds.rdupm.harbor.app.service.HarborLogService;
import org.hzero.core.util.Results;
import org.hzero.core.base.BaseController;
import org.hrds.rdupm.harbor.domain.entity.HarborLog;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import io.choerodon.mybatis.pagehelper.annotation.SortDefault;
import io.choerodon.mybatis.pagehelper.domain.PageRequest;
import io.choerodon.mybatis.pagehelper.domain.Sort;
import io.swagger.annotations.ApiOperation;
import springfox.documentation.annotations.ApiIgnore;

/**
 * 制品库-harbor日志表 管理 API
 *
 * @author xiuhong.chen@hand-china.com 2020-04-29 14:54:57
 */
@RestController("harborLogController.v1")
@RequestMapping("/v1/harbor-logs")
public class HarborLogController extends BaseController {

    @Autowired
    private HarborLogService service;

    @ApiOperation(value = "项目层-权限日志列表")
	@Permission(type = ResourceType.PROJECT,permissionPublic = true)
    @GetMapping("/auth/list-project/{projectId}")
    public ResponseEntity<PageInfo<HarborLog>> listAuthLogByProject(@ApiParam("猪齿鱼项目ID") @PathVariable Long projectId,
															 @ApiParam("用户名") @RequestParam(required = false) String loginName,
															 @ApiParam("操作类型") @RequestParam(required = false) String operateType,
															 @ApiParam("开始日期") @RequestParam(required = false) Date startDate,
															 @ApiParam("结束日期") @RequestParam(required = false) Date endDate,
															 @ApiIgnore @SortDefault(value = HarborLog.FIELD_LOG_ID, direction = Sort.Direction.DESC) PageRequest pageRequest) {
		PageInfo<HarborLog> pageInfo = service.listAuthLog(pageRequest, new HarborLog(projectId,null,operateType,loginName,startDate,endDate));
        return Results.success(pageInfo);
    }

	@ApiOperation(value = "组织层-权限日志列表")
	@Permission(type = ResourceType.PROJECT,permissionPublic = true)
	@GetMapping("/auth/list-org/{organizationId}")
	public ResponseEntity<PageInfo<HarborLog>> listAuthLogByOrg(@ApiParam("猪齿鱼组织ID") @PathVariable Long organizationId,
															 @ApiParam("用户名") @RequestParam(required = false) String loginName,
															 @ApiParam("操作类型") @RequestParam(required = false) String operateType,
															 @ApiParam("开始日期") @RequestParam(required = false) Date startDate,
															 @ApiParam("结束日期") @RequestParam(required = false) Date endDate,
															 @ApiIgnore @SortDefault(value = HarborLog.FIELD_LOG_ID, direction = Sort.Direction.DESC) PageRequest pageRequest) {
		PageInfo<HarborLog> pageInfo = service.listAuthLog(pageRequest, new HarborLog(null,organizationId,operateType,loginName,startDate,endDate));
		return Results.success(pageInfo);
	}

	@ApiOperation(value = "项目层-镜像日志列表")
	@Permission(type = ResourceType.PROJECT,permissionPublic = true)
	@GetMapping("/image/list-project/{projectId}")
	public ResponseEntity<PageInfo<HarborImageLog>> listImageLogByProject(@ApiParam("猪齿鱼项目ID") @PathVariable Long projectId,
															 @ApiParam("登录名") @RequestParam(required = false) String loginName,
															 @ApiParam("镜像名") @RequestParam(required = false) String imageName,
															 @ApiParam("镜像TAG名") @RequestParam(required = false) String tagName,
															 @ApiParam("操作类型") @RequestParam(required = false) String operateType,
															 @ApiParam("开始日期") @RequestParam(required = false) Date startDate,
															 @ApiParam("结束日期") @RequestParam(required = false) Date endDate,
															 @ApiIgnore @SortDefault(value = HarborLog.FIELD_LOG_ID, direction = Sort.Direction.DESC) PageRequest pageRequest) {
		PageInfo<HarborImageLog> pageInfo = service.listImageLogByProject(pageRequest, projectId,imageName,loginName,tagName,operateType,startDate,endDate);
		return Results.success(pageInfo);
	}


	@ApiOperation(value = "组织层-镜像日志列表")
	@Permission(type = ResourceType.PROJECT,permissionPublic = true)
	@GetMapping("/image/list-org/{organizationId}")
	public ResponseEntity<PageInfo<HarborImageLog>> listImageLogByOrg(@ApiParam("猪齿鱼组织ID") @PathVariable Long organizationId,
																		  @ApiParam("镜像仓库编码") @RequestParam(required = false) String projectCode,
																		  @ApiParam("镜像仓库名称") @RequestParam(required = false) String projectName,
																	 	  @ApiParam("登录名") @RequestParam(required = false) String loginName,
																	 	  @ApiParam("镜像名") @RequestParam(required = false) String imageName,
																		  @ApiParam("镜像TAG名") @RequestParam(required = false) String tagName,
																		  @ApiParam("操作类型") @RequestParam(required = false) String operateType,
																		  @ApiParam("开始日期") @RequestParam(required = false) Date startDate,
																		  @ApiParam("结束日期") @RequestParam(required = false) Date endDate,
																		  @ApiIgnore @SortDefault(value = HarborLog.FIELD_LOG_ID, direction = Sort.Direction.DESC) PageRequest pageRequest) {
		PageInfo<HarborImageLog> pageInfo = service.listImageLogByOrg(pageRequest, organizationId,projectCode,projectName,imageName,loginName,tagName,operateType,startDate,endDate);
		return Results.success(pageInfo);
	}

}
