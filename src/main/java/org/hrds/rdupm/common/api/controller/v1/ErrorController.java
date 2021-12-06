package org.hrds.rdupm.common.api.controller.v1;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import org.hzero.core.util.Results;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import io.choerodon.core.exception.ExceptionResponse;
import io.choerodon.swagger.annotation.Permission;

/**
 * Created by wangxiang on 2021/10/12
 */
@RestController("errorController.v1")
@RequestMapping(value = "/v1")
public class ErrorController {

    @RequestMapping(value = "/exceeded/capacity/limit")
    @Permission(permissionPublic = true)
    public ResponseEntity<ExceptionResponse> nexusProxy(HttpServletRequest request, HttpServletResponse response) throws Exception {
        return Results.newResult(HttpStatus.PAYMENT_REQUIRED.value(), new ExceptionResponse("error.exceeded.capacity", "nexus proxy is error!", "error"));
    }
}
