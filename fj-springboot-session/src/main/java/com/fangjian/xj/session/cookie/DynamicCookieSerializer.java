/*
 */

package com.fangjian.xj.session.cookie;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.session.web.http.CookieSerializer;

import javax.servlet.ServletRequest;
import javax.servlet.http.Cookie;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.util.ArrayList;
import java.util.List;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

/**
 * Cookie Name 动态解析的实现
 */
public class DynamicCookieSerializer implements CookieSerializer {
    private static final Logger LOG = LoggerFactory.getLogger(DynamicCookieSerializer.class);
    private String fallbackCookieName = "SESSION";

    private Boolean useSecureCookie;

    private boolean useHttpOnlyCookie = isServlet3();

    private String cookiePath;

    private int cookieMaxAge = -1;

    private String domainName;

    private Pattern domainNamePattern;

    private String jvmRoute;

    private CookieNameResolver cookieNameResolver;

    public List<String> readCookieValues(HttpServletRequest request) {
        Cookie cookies[] = request.getCookies();
        List<String> matchingCookieValues = new ArrayList<String>();
        String cookieName = getCookieName(request);
        if (cookies != null && cookieName != null) {
            for (Cookie cookie : cookies) {
                if (cookieName.equals(cookie.getName())) {
                    String sessionId = cookie.getValue();
                    if (sessionId == null) {
                        continue;
                    }
                    if (jvmRoute != null && sessionId.endsWith(jvmRoute)) {
                        sessionId = sessionId.substring(0, sessionId.length() - jvmRoute.length());
                    }
                    matchingCookieValues.add(sessionId);
                }
            }
        }
        return matchingCookieValues;
    }

    public void writeCookieValue(CookieValue cookieValue) {
        HttpServletRequest request = cookieValue.getRequest();
        HttpServletResponse response = cookieValue.getResponse();
        String cookieName = getCookieName(request);
        if (cookieName == null) {
            LOG.error("cannot resolve cookie name, write cookie[{}] failed", cookieValue.getCookieValue());
            return;
        }

        String requestedCookieValue = cookieValue.getCookieValue();
        String actualCookieValue = jvmRoute == null ? requestedCookieValue : requestedCookieValue + jvmRoute;
        Cookie sessionCookie = new Cookie(cookieName, actualCookieValue);
        sessionCookie.setSecure(isSecureCookie(request));
        sessionCookie.setPath(getCookiePath(request));
        String domainName = getDomainName(request);
        if (domainName != null) {
            sessionCookie.setDomain(domainName);
        }

        if (useHttpOnlyCookie) {
            sessionCookie.setHttpOnly(true);
        }

        if ("".equals(requestedCookieValue)) {
            sessionCookie.setMaxAge(0);
        } else {
            sessionCookie.setMaxAge(cookieMaxAge);
        }

        response.addCookie(sessionCookie);
    }

    protected String getCookieName(HttpServletRequest request) {
        if (cookieNameResolver != null) {
            String resolvedCookieName = cookieNameResolver.resolve(request);
            if (resolvedCookieName != null) {
                return resolvedCookieName;
            }
        }
        return null;
    }

    /**
     * Sets if a Cookie marked as secure should be used. The default is to use
     * the value of {@link HttpServletRequest#isSecure()}.
     *
     * @param useSecureCookie determines if the cookie should be marked as secure.
     */
    public void setUseSecureCookie(boolean useSecureCookie) {
        this.useSecureCookie = useSecureCookie;
    }

    /**
     * 设置cookie是否只在http请求中使用,如果是true,将无法通过javascript获取cookie
     *
     * @param useHttpOnlyCookie
     */
    public void setUseHttpOnlyCookie(boolean useHttpOnlyCookie) {
        if (useHttpOnlyCookie && !isServlet3()) {
            throw new IllegalArgumentException("You cannot set useHttpOnlyCookie to true in pre Servlet 3 environment");
        }
        this.useHttpOnlyCookie = useHttpOnlyCookie;
    }

    private boolean isSecureCookie(HttpServletRequest request) {
        if (useSecureCookie == null) {
            return request.isSecure();
        }
        return useSecureCookie;
    }

    /**
     * 设置Cookie的路径, 默认是contextPath
     *
     * @param cookiePath Cookie的路径. 如果是null, 使用contextPath
     */
    public void setCookiePath(String cookiePath) {
        this.cookiePath = cookiePath;
    }

    public void setFallbackCookieName(String fallbackCookieName) {
        if (fallbackCookieName == null) {
            throw new IllegalArgumentException("fallbackCookieName cannot be null");
        }
        this.fallbackCookieName = fallbackCookieName;
    }

    /**
     * 设置cookie生命周期,默认浏览器关闭时过期
     *
     * @param cookieMaxAge 过期时间,单位:秒
     */
    public void setCookieMaxAge(int cookieMaxAge) {
        this.cookieMaxAge = cookieMaxAge;
    }

    /**
     * 设置cookie的域名,默认是当前域名
     *
     * @param domainName 域名
     * @throws IllegalStateException if the domainNamePattern is also set
     */
    public void setDomainName(String domainName) {
        if (this.domainNamePattern != null) {
            throw new IllegalStateException("Cannot set both domainName and domainNamePattern");
        }
        this.domainName = domainName;
    }

    /**
     * <p>
     * Sets a case insensitive pattern used to extract the domain name from the
     * {@link HttpServletRequest#getServerName()}. The pattern should provide a
     * single grouping that defines what the value is that should be matched.
     * User's should be careful not to output malicious characters like new
     * lines to prevent from things like
     * <a href= "https://www.owasp.org/index.php/HTTP_Response_Splitting">HTTP
     * Response Splitting</a>.
     * </p>
     * <p>
     * <p>
     * If the pattern does not match, then no domain will be set. This is useful
     * to ensure the domain is not set during development when localhost might
     * be used.
     * </p>
     * <p>
     * An example value might be "^.+?\\.(\\w+\\.[a-z]+)$". For the given input,
     * it would provide the following explicit domain (null means no domain name
     * is set):
     * </p>
     * <p>
     * <ul>
     * <li>example.com - null</li>
     * <li>child.sub.example.com - example.com</li>
     * <li>localhost - null</li>
     * <li>127.0.1.1 - null</li>
     * </ul>
     *
     * @param domainNamePattern the case insensitive pattern to extract the domain name with
     * @throws IllegalStateException if the domainName is also set
     */
    public void setDomainNamePattern(String domainNamePattern) {
        if (this.domainName != null) {
            throw new IllegalStateException("Cannot set both domainName and domainNamePattern");
        }
        this.domainNamePattern = Pattern.compile(domainNamePattern, Pattern.CASE_INSENSITIVE);
    }

    /**
     * <p>
     * Used to identify which JVM to route to for session affinity. With some
     * implementations (i.e. Redis) this provides no performance benefit.
     * However, this can help with tracing logs of a particular user. This will ensure that the value of the cookie is formatted as
     * </p>
     * <code>
     * sessionId + "." jvmRoute
     * </code>
     * <p>
     * To use set a custom route on each JVM instance and setup a frontend proxy
     * to forward all requests to the JVM based on the route.
     * </p>
     *
     * @param jvmRoute the JVM Route to use (i.e. "node01jvmA", "n01ja", etc)
     */
    public void setJvmRoute(String jvmRoute) {
        this.jvmRoute = "." + jvmRoute;
    }

    private String getDomainName(HttpServletRequest request) {
        if (domainName != null) {
            return domainName;
        }
        if (domainNamePattern != null) {
            Matcher matcher = domainNamePattern.matcher(request.getServerName());
            if (matcher.matches()) {
                return matcher.group(1);
            }
        }
        return null;
    }

    private String getCookiePath(HttpServletRequest request) {
        if (cookiePath == null) {
            return request.getContextPath() + "/";
        }
        return cookiePath;
    }

    /**
     * Returns true if the Servlet 3 APIs are detected.
     *
     * @return
     */
    private boolean isServlet3() {
        try {
            ServletRequest.class.getMethod("startAsync");
            return true;
        } catch (NoSuchMethodException e) {
        }
        return false;
    }

    public void setCookieNameResolver(CookieNameResolver cookieNameResolver) {
        this.cookieNameResolver = cookieNameResolver;
    }
}
