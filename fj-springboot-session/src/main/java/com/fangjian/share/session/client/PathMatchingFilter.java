/*
 */
package com.fangjian.share.session.client;

import com.fangjian.share.session.util.StringUtils;
import com.fangjian.share.session.util.WebUtils;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.springframework.util.AntPathMatcher;
import org.springframework.util.PathMatcher;
import org.springframework.web.filter.OncePerRequestFilter;

import javax.servlet.FilterChain;
import javax.servlet.ServletException;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.util.HashSet;
import java.util.Set;

/**
 * 根据路径匹配请求进行处理
 * <p>
 * excludePaths 设置需要排除的路径，匹配到的请求不作处理
 * <p>
 * includePaths 设置需要处理的路径，匹配到的请求将处理
 * <p>
 * excludePaths和includePaths都不设置将处理所有请求
 * <p>
 */
public abstract class PathMatchingFilter extends OncePerRequestFilter {
    private static final Log LOG = LogFactory.getLog(PathMatchingFilter.class);
    public static final String DELIMITERS = ",; \t\n";
    private PathMatcher pathMatcher = new AntPathMatcher();
    private Set<String> includePaths = new HashSet<>();
    private Set<String> excludePaths = new HashSet<>();

    @Override
    protected void doFilterInternal(HttpServletRequest request, HttpServletResponse response, FilterChain filterChain) throws ServletException, IOException {
        if (isMatch(request)) {
            onMatching(request, response, filterChain);
        } else {
            filterChain.doFilter(request, response);
        }
    }

    protected abstract void onMatching(HttpServletRequest request, HttpServletResponse response, FilterChain filterChain) throws ServletException, IOException;

    private boolean isMatch(HttpServletRequest request) {
        String requestUri = WebUtils.getPathWithinApplication(request);
        if (excludePaths != null) {
            for (String excludePath : excludePaths) {
                if (pathMatcher.match(excludePath, requestUri)) {
                    return false;
                }
            }
        }
        if (includePaths == null || includePaths.isEmpty()) {
            return true;
        } else {
            for (String includePath : includePaths) {
                if (pathMatcher.match(includePath, requestUri)) {
                    return true;
                }
            }
            return false;
        }
    }

    public void setExcludePaths(String excludePaths) {
        addExcludePath(excludePaths);
    }

    public void setIncludePaths(String includePaths) {
        addIncludePath(includePaths);
    }

    public void addExcludePath(String excludePath) {
        String[] excludePathsArr = StringUtils.splitToStringArray(excludePath, DELIMITERS);
        if (excludePathsArr != null && excludePathsArr.length > 0) {
            for (int i = 0; i < excludePathsArr.length; i++) {
                this.excludePaths.add(excludePathsArr[i]);
            }
        }
    }

    public void addIncludePath(String includePath) {
        String[] includePathsArr = StringUtils.splitToStringArray(includePath, DELIMITERS);
        if (includePathsArr != null && includePathsArr.length > 0) {
            for (int i = 0; i < includePathsArr.length; i++) {
                this.includePaths.add(includePathsArr[i]);
            }
        }
    }
}
