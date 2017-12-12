/*
 */
package com.fangjian.xj.session.core;

import com.fangjian.xj.session.SessionGroup;
import com.fangjian.xj.session.util.WebUtils;
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
