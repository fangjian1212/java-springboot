package com.fangjian.share.session.shiro;

import com.fangjian.share.session.Constants;
import com.fangjian.share.session.util.JacksonUtils;
import com.fasterxml.jackson.core.type.TypeReference;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.apache.shiro.SecurityUtils;
import org.apache.shiro.authc.AuthenticationException;
import org.apache.shiro.authc.AuthenticationInfo;
import org.apache.shiro.authc.AuthenticationToken;
import org.apache.shiro.authc.SimpleAuthenticationInfo;
import org.apache.shiro.authz.AuthorizationInfo;
import org.apache.shiro.authz.SimpleAuthorizationInfo;
import org.apache.shiro.realm.AuthorizingRealm;
import org.apache.shiro.session.Session;
import org.apache.shiro.subject.PrincipalCollection;
import org.apache.shiro.subject.Subject;

import javax.servlet.http.HttpServletRequest;
import java.security.Principal;
import java.util.List;

/**
 */
public class RedstarRedisRealm extends AuthorizingRealm {
    public static final Log LOG = LogFactory.getLog(RedstarRedisRealm.class);
    // session 中角色的属性名
    private String roleAttrName = Constants.DEFAULT_ROLE_SESSION_KEY;

    // session 中权限的属性名
    private String permissionAttrName = Constants.DEFAULT_PERMISSION_SESSION_KEY;

    public RedstarRedisRealm() {
        setAuthenticationTokenClass(SessionAuthToken.class);
    }

    @Override
    protected AuthorizationInfo doGetAuthorizationInfo(PrincipalCollection principals) {
        SimpleAuthorizationInfo simpleAuthorizationInfo = new SimpleAuthorizationInfo();
        Subject subject = SecurityUtils.getSubject();
        Session session = subject.getSession();
        String roleNamesJsonStr = (String) session.getAttribute(roleAttrName);
        if (roleNamesJsonStr != null) {
            List<String> roleNames = JacksonUtils.readValue(roleNamesJsonStr, new TypeReference<List<String>>(){});
            simpleAuthorizationInfo.addRoles(roleNames);
        }
        String permissionNamesJsonStr = (String) session.getAttribute(permissionAttrName);
        if (permissionNamesJsonStr != null) {
            List<String> permissionNames = JacksonUtils.readValue(permissionNamesJsonStr, new TypeReference<List<String>>(){});
            simpleAuthorizationInfo.addStringPermissions(permissionNames);
        }
        return simpleAuthorizationInfo;
    }

    @Override
    protected AuthenticationInfo doGetAuthenticationInfo(AuthenticationToken token) throws AuthenticationException {
        SessionAuthToken sessionToken = (SessionAuthToken) token;
        HttpServletRequest request = sessionToken.getServletRequest();
        if (request == null) {
            return null;
        }
        Principal principal = request.getUserPrincipal();
        if(principal == null){
            return null;
        }

        return new SimpleAuthenticationInfo(principal, sessionToken.getCredentials(), getName());
    }

}
