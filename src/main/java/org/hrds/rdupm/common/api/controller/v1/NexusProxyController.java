package org.hrds.rdupm.common.api.controller.v1;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import org.hzero.core.util.Results;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RestController;

import io.choerodon.core.exception.ExceptionResponse;
import io.choerodon.swagger.annotation.Permission;

/**
 * nexus代理
 *
 * @author weisen.yang@hand-china.com 2020/11/6
 */
@RestController("nexusProxyController.v1")
@RequestMapping("/v1/nexus/proxy")
public class NexusProxyController {
    private static final Logger logger = LoggerFactory.getLogger(NexusProxyController.class);

    @RequestMapping(value = "/**", method = RequestMethod.GET)
    @Permission(permissionPublic = true)
    public ResponseEntity<ExceptionResponse> nexusProxy(HttpServletRequest request, HttpServletResponse response) throws Exception {
        logger.error("nexus proxy is error!");
        return Results.newResult(HttpStatus.INTERNAL_SERVER_ERROR.value(), new ExceptionResponse("error.hsop_component_repo.nexus_proxy_error", "nexus proxy is error!", "error"));
    }
}
