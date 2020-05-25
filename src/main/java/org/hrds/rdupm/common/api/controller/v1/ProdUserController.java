package org.hrds.rdupm.common.api.controller.v1;

import io.choerodon.swagger.annotation.Permission;
import io.choerodon.core.iam.ResourceLevel;
import io.swagger.annotations.ApiParam;
import org.hrds.rdupm.common.app.service.ProdUserService;
import org.hzero.core.util.Results;
import org.hzero.core.base.BaseController;
import org.hrds.rdupm.common.domain.entity.ProdUser;
import org.hrds.rdupm.common.domain.repository.ProdUserRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import io.swagger.annotations.ApiOperation;

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

    @ApiOperation(value = "根据用户ID查询制品库用户信息，若默认密码已经被修改，则查询结果中不展示password字段")
	@Permission(level = ResourceLevel.ORGANIZATION)
    @GetMapping("/{userId}")
    public ResponseEntity<ProdUser> detail(@PathVariable @ApiParam("猪齿鱼用户ID") Long userId) {
        ProdUser prodUser = prodUserRepository.select(ProdUser.FIELD_USER_ID,userId).stream().findFirst().orElse(null);
        if(prodUser != null && prodUser.getPwdUpdateFlag().intValue()==1){
        	prodUser.setPassword(null);
		}
        return Results.success(prodUser);
    }

	@ApiOperation(value = "制品库-修改默认密码")
	@Permission(level = ResourceLevel.ORGANIZATION)
	@PostMapping("/updatePwd")
	public ResponseEntity<ProdUser> updatePwd(@RequestBody @ApiParam("必输字段用户IDuserId、旧密码oldPassword、新密码password、确认密码rePassword") ProdUser prodUser) {
		prodUserService.updatePwd(prodUser);
		return Results.success();
	}

}
