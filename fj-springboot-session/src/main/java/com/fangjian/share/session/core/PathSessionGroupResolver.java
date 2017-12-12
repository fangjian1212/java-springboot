/*
 */
package com.fangjian.share.session.core;

import com.fangjian.share.session.util.WebUtils;
import com.fangjian.share.session.SessionGroup;
import org.springframework.util.AntPathMatcher;
import org.springframework.util.PathMatcher;

import javax.servlet.http.HttpServletRequest;

/**
 */
public class PathSessionGroupResolver extends AbstractSessionGroupMappingResolver {
    private PathMatcher pathMatcher = new AntPathMatcher();

    @Override
    public SessionGroup resolve(HttpServletRequest request) {
        String pathWithinApplication = WebUtils.getPathWithinApplication(request);
        for (String path : sessionGroupMapping.keySet()) {
            if (pathMatcher.match(path, pathWithinApplication)) {
                return sessionGroupMapping.get(path);
            }
        }
        return null;
    }
}
