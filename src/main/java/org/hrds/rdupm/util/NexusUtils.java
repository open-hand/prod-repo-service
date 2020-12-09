package org.hrds.rdupm.util;

import javax.servlet.http.HttpServletRequest;
import org.hrds.rdupm.init.config.NexusProxyConfigProperties;

/**
 * 描述
 *
 * @author weisen.yang@hand-china.com 2020/11/6
 */
public class NexusUtils {

    public static String getServletUri(HttpServletRequest servletRequest, NexusProxyConfigProperties nexusProxyConfigProperties) {
        String servletUri = servletRequest.getRequestURI();
        servletUri = servletUri.replace(nexusProxyConfigProperties.getUriPrefix(), "");
        return servletUri;
    }
}
