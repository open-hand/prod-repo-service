package org.hrds.rdupm.harbor.api.controller.v1;

import java.util.HashMap;
import java.util.Map;

import io.choerodon.core.annotation.Permission;
import io.choerodon.core.enums.ResourceType;
import io.swagger.annotations.ApiOperation;
import io.swagger.annotations.ApiParam;
import org.hrds.rdupm.harbor.api.vo.HarborGuideVo;
import org.hzero.core.util.Results;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

/**
 * description
 *
 * @author chenxiuhong 2020/04/26 8:46 下午
 */
@RestController("HarborLogController.v1")
@RequestMapping("/v1/harbor-log")
public class HarborLogController {

	@ApiOperation(value = "项目层-镜像操作日志列表")
	@Permission(type = ResourceType.PROJECT, permissionPublic = true)
	@GetMapping(value = "/image/list-project/{projectId}")
	public ResponseEntity getProjectGuid(@PathVariable(value = "projectId") @ApiParam(value = "猪齿鱼项目ID") Long projectId) {
		Map<String,Object> paramMap = new HashMap<>();
		paramMap.put("username",null);
		paramMap.put("repository",null);
		paramMap.put("tag",null);
		paramMap.put("operation",null);
		paramMap.put("begin_timestamp",null);
		paramMap.put("end_timestamp",null);
		paramMap.put("page",null);
		paramMap.put("page_size",null);

		/*
		{
    "log_id": 37,
    "username": "admin",
    "project_id": 34,
    "repo_name": "cxh-test/busybox",
    "repo_tag": "1.0",
    "guid": "",
    "operation": "delete",
    "op_time": "2020-04-24T07:48:02.600387Z"
  }

  /api/users/current/permissions?scope=/project/34&relative=true
  /api/projects/34/members

entity_id: 36
entity_name: "15367"
entity_type: "u"
id: 57
project_id: 34
role_id: 1
role_name: "projectAdmin"

		* */
		return Results.success();
	}

}
