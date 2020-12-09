package org.hrds.rdupm.nexus.infra.filter;

import java.io.IOException;
import javax.servlet.ServletException;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import org.apache.http.HttpRequest;
import org.mitre.dsmiley.httpproxy.ProxyServlet;

/**
 * Created by wangxiang on 2020/12/9
 */
public class NexusProxyServlet extends ProxyServlet {

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
        return pathInfo;
    }
}
