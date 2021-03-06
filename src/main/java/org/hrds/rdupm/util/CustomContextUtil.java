package org.hrds.rdupm.util;

import java.util.Collections;
import java.util.HashMap;
import java.util.concurrent.Callable;
import javax.annotation.Nullable;
import org.apache.commons.lang3.ObjectUtils;
import org.hrds.rdupm.harbor.infra.feign.dto.UserDTO;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.oauth2.provider.OAuth2Authentication;
import org.springframework.security.oauth2.provider.OAuth2Request;
import org.springframework.security.oauth2.provider.authentication.OAuth2AuthenticationDetails;

import io.choerodon.asgard.saga.consumer.MockHttpServletRequest;
import io.choerodon.core.exception.CommonException;
import io.choerodon.core.oauth.CustomUserDetails;
import io.choerodon.core.oauth.DetailsHelper;

/**
 * Creator: ChangpingShi0213@gmail.com
 * Date:  17:24 2019/3/1
 * Description:
 */
public class CustomContextUtil {
    private static final Long DEFAULT_USER_ID = 0L;

    private CustomContextUtil() {
    }

    public static void setUserContext(Long userId) {
        setUserContext("unknown", userId, 1L);
    }

    public static void setDefaultIfNull(@Nullable Long userId) {
        setUserContext(ObjectUtils.defaultIfNull(userId, DEFAULT_USER_ID));
    }

    public static void setDefault() {
        setUserContext(DEFAULT_USER_ID);
    }

    public static void setDefaultIfNull(@Nullable UserDTO user) {
        if (user == null) {
            setUserContext(DEFAULT_USER_ID);
        } else {
            setUserContext(user.getLoginName(), user.getId(), user.getOrganizationId());
        }
    }

    /**
     * 设置用户上下文，谨慎使用
     *
     * @param loginName 登录名
     * @param userId    用户id
     * @param orgId     组织id
     */
    public static void setUserContext(String loginName, Long userId, Long orgId) {
        try {
            CustomUserDetails customUserDetails = DetailsHelper.getUserDetails() == null ? new CustomUserDetails(loginName, "unknown", Collections.emptyList()) : DetailsHelper.getUserDetails();
            customUserDetails.setUserId(userId);
            customUserDetails.setOrganizationId(orgId);
            customUserDetails.setLanguage("zh_CN");
            customUserDetails.setTimeZone("CCT");
            Authentication user = new UsernamePasswordAuthenticationToken("default", "N/A", Collections.emptyList());
            OAuth2Request request = new OAuth2Request(new HashMap<>(0), "", Collections.emptyList(), true,
                    Collections.emptySet(), Collections.emptySet(), null, null, null);
            OAuth2Authentication authentication = new OAuth2Authentication(request, user);
            OAuth2AuthenticationDetails oAuth2AuthenticationDetails = new OAuth2AuthenticationDetails(new MockHttpServletRequest());
            oAuth2AuthenticationDetails.setDecodedDetails(customUserDetails);
            authentication.setDetails(oAuth2AuthenticationDetails);
            SecurityContextHolder.getContext().setAuthentication(authentication);

        } catch (Exception e) {
            throw new CommonException("context.set.error", e);
        }
    }

    /**
     * 设置特定的用户上下文
     *
     * @param context 用户上下文
     */
    public static void setUserContext(UserDetails context) {
        try {
            Authentication user = new UsernamePasswordAuthenticationToken("default", "N/A", Collections.emptyList());
            OAuth2Request request = new OAuth2Request(new HashMap<>(0), "", Collections.emptyList(), true,
                    Collections.emptySet(), Collections.emptySet(), null, null, null);
            OAuth2Authentication authentication = new OAuth2Authentication(request, user);
            OAuth2AuthenticationDetails oAuth2AuthenticationDetails = new OAuth2AuthenticationDetails(new MockHttpServletRequest());
            oAuth2AuthenticationDetails.setDecodedDetails(context);
            authentication.setDetails(oAuth2AuthenticationDetails);
            SecurityContextHolder.getContext().setAuthentication(authentication);
        } catch (Exception e) {
            throw new CommonException("context.set.error", e);
        }
    }

    /**
     * 在特定的上下文运行callable中的代码，运行后的上下文与运行前一致
     *
     * @param contextUserId 特定用户上下文
     * @param callable      可执行的操作
     * @param <T>           返回结果的类型
     * @return 返回callable的返回值
     */
    public static <T> T executeCallableInCertainContext(Long contextUserId, Callable<T> callable) {
        UserDetails preContext = null;
        try {
            preContext = DetailsHelper.getUserDetails();
            setUserContext(contextUserId);
            return callable.call();
        } catch (CommonException ex) {
            throw ex;
        } catch (Exception ex) {
            throw new CommonException("error.execute.in.certain.context", ex);
        } finally {
            setUserContext(preContext);
        }
    }

    /**
     * 在特定的上下文运行runnable中的代码，运行后的上下文与运行前一致
     *
     * @param contextUserId 特定用户上下文
     * @param runnable      可执行的操作
     */
    public static void executeRunnableInCertainContext(Long contextUserId, Runnable runnable) {
        UserDetails preContext = null;
        try {
            preContext = DetailsHelper.getUserDetails();
            setUserContext(contextUserId);
            runnable.run();
        } catch (CommonException ex) {
            throw ex;
        } catch (Exception ex) {
            throw new CommonException("error.execute.in.certain.context", ex);
        } finally {
            setUserContext(preContext);
        }
    }

    /**
     * 清除上下文
     */
    public static void clearContext() {
        SecurityContextHolder.getContext().setAuthentication(null);
    }
}
