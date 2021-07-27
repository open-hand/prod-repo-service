package org.hrds.rdupm.nexus.infra.filter;

import java.io.IOException;
import javax.servlet.ServletException;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.apache.commons.lang3.StringUtils;
import org.apache.http.HttpRequest;
import org.hrds.rdupm.nexus.infra.constant.NexusProxyConstants;
import org.hzero.core.base.BaseConstants;
import org.mitre.dsmiley.httpproxy.ProxyServlet;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * Created by wangxiang on 2020/12/9
 */
public class NexusProxyServlet extends ProxyServlet {
    private static final Logger LOGGER = LoggerFactory.getLogger(NexusProxyServlet.class);

    @Override
    protected void service(HttpServletRequest servletRequest, HttpServletResponse servletResponse) throws ServletException, IOException {
        super.service(servletRequest, servletResponse);
    }

    @Override
    protected void copyRequestHeaders(HttpServletRequest servletRequest, HttpRequest proxyRequest) {
        super.copyRequestHeaders(servletRequest, proxyRequest);
    }

    @Override
    protected String rewritePathInfoFromRequest(HttpServletRequest servletRequest) {
        String pathInfo = super.rewritePathInfoFromRequest(servletRequest);
        Long configId = (Long) servletRequest.getAttribute(NexusProxyConstants.CONFIG_SERVER_ID);
        if (configId != null) {
            return BaseConstants.Symbol.SLASH + StringUtils.substringAfter(StringUtils.substringAfter(pathInfo, BaseConstants.Symbol.SLASH), BaseConstants.Symbol.SLASH);
        }
        return pathInfo;
    }
}
