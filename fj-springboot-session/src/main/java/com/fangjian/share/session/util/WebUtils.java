/*
 */

package com.fangjian.share.session.util;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import javax.servlet.http.Cookie;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;
import java.io.IOException;
import java.util.*;
import java.util.regex.Matcher;
import java.util.regex.Pattern;


public class WebUtils {
    private static final Logger logger = LoggerFactory.getLogger(WebUtils.class);

    public static final String INCLUDE_REQUEST_URI_ATTRIBUTE = "javax.servlet.include.request_uri";
    public static final String INCLUDE_CONTEXT_PATH_ATTRIBUTE = "javax.servlet.include.context_path";
    public static final String INCLUDE_SERVLET_PATH_ATTRIBUTE = "javax.servlet.include.servlet_path";
    public static final String INCLUDE_PATH_INFO_ATTRIBUTE = "javax.servlet.include.path_info";

    public static final String LAST_REQUEST_SESSION_KEY = "last_request_url";

    /**
     * 根据request取得IP
     *
     * @param request
     * @return
     */
    public static String getRemoteAddr(HttpServletRequest request) {
        String ip = "";
        try {
            ip = request.getHeader("x-forwarded-for");
            if (ip == null || ip.length() == 0 || "unknown".equalsIgnoreCase(ip)) {
                ip = request.getHeader("Proxy-Client-IP");
            }
            if (ip == null || ip.length() == 0 || "unknown".equalsIgnoreCase(ip)) {
                ip = request.getHeader("WL-Proxy-Client-IP");
            }
            if (ip == null || ip.length() == 0 || "unknown".equalsIgnoreCase(ip)) {
                ip = request.getHeader("HTTP_CLIENT_IP");
            }
            if (ip == null || ip.length() == 0 || "unknown".equalsIgnoreCase(ip)) {
                ip = request.getHeader("HTTP_X_FORWARDED_FOR");
            }
            if (ip == null || ip.length() == 0 || "unknown".equalsIgnoreCase(ip)) {
                ip = request.getRemoteAddr();
            }

            String[] ips = ip.split(",");

            if (ips.length > 0) {
                ip = ips[0];
            }
        } catch (Exception e) {
            logger.warn(e.getMessage());
        }

        return ip;
    }

    /**
     * 从request中获得参数Map，并返回可读的Map
     *
     * @param request
     * @return
     */
    public static Map<String, String> getParameterMap(HttpServletRequest request) {
        // 参数Map
        Map<String, String[]> properties = request.getParameterMap();
        // 返回值Map
        Map<String, String> returnMap = new HashMap<String, String>();
        Iterator<Map.Entry<String, String[]>> entries = properties.entrySet().iterator();
        Map.Entry<String, String[]> entry;
        String name = "";
        String value = "";
        while (entries.hasNext()) {
            entry = entries.next();
            name = entry.getKey();
            Object valueObj = entry.getValue();
            if (null == valueObj) {
                value = "";
            } else if (valueObj instanceof String[]) {
                String[] values = (String[]) valueObj;
                for (int i = 0; i < values.length; i++) {
                    value = values[i] + ",";
                }
                value = value.substring(0, value.length() - 1);
            } else {
                value = valueObj.toString();
            }
            returnMap.put(name, value);
        }
        return returnMap;
    }

    /**
     * 验证输入参数是否在请求参数
     *
     * @param request
     * @param str
     * @return
     */
    public static Boolean getParameterMap(HttpServletRequest request, String str) {
        Boolean flag = true;
        Map<String, String[]> propertiesMap = request.getParameterMap();
        if (propertiesMap.size() == 0) {
            flag = false;
        } else {
            if (!propertiesMap.containsKey(str)) {
                flag = false;
            }
        }
        return flag;
    }

    /**
     * 参数多参数验证请求范围
     *
     * @param request
     * @param arr
     * @return
     */
    public static Boolean getParameterMap(HttpServletRequest request, String[] arr) {
        Boolean flag = true;
        Map<String, String[]> propertiesMap = request.getParameterMap();
        if (propertiesMap.size() == 0) {
            flag = false;
        } else {
            for (int i = 0; i < arr.length; i++) {
                if (!propertiesMap.containsKey(arr[i])) {
                    flag = false;
                }
            }
        }
        return flag;
    }

    public static Cookie getCookie(HttpServletRequest request, String name) {
        Cookie cookies[] = request.getCookies();
        if (cookies != null) {
            for (Cookie cookie : cookies) {
                if (name.equals(cookie.getName())) {
                    return cookie;
                }
            }
        }
        return null;
    }

    public static boolean isCorsRequest(HttpServletRequest request) {
        return (request.getHeader("Origin") != null);
    }

    /**
     * @param request
     * @return
     */
    public static boolean isCrossDomainFlightRequest(HttpServletRequest request) {
        return ((request.getHeader("Origin") != null) && "OPTIONS".equalsIgnoreCase(request.getMethod()) &&
                request.getHeader("Access-Control-Request-Method") != null);
    }

    public static void allowCrossDomian(HttpServletRequest request, HttpServletResponse response) {
        if (!isCorsRequest(request)) {
            //不是跨域请求，不处理
            return;
        }
        if (response.containsHeader("Access-Control-Allow-Origin")) {
            //已经包含 Access-Control-Allow-Origin 头，跳过处理
            return;
        }
        String origin = request.getHeader("Origin");
        String allowMethod = null;
        String allowHeaders = null;
        if (isCrossDomainFlightRequest(request)) {
            //预检请求，从头部获取相应字段
            allowMethod = request.getHeader("Access-Control-Request-Method");
            allowHeaders = request.getHeader("Access-Control-Request-Headers");
        } else {
            allowMethod = request.getMethod();
            List<String> headerNames = Collections.list(request.getHeaderNames());
            allowHeaders = StringUtils.toDelimitedString(headerNames, ",");
        }
        response.setHeader("Access-Control-Allow-Origin", origin);
        response.setHeader("Access-Control-Allow-Credentials", "true");
        response.setHeader("Access-Control-Allow-Methods", allowMethod);
        response.setHeader("Access-Control-Allow-Headers", allowHeaders);
        response.setHeader("Access-Control-Expose-Headers", "x-auth-token");
    }

    public static void addCookie(HttpServletRequest request, HttpServletResponse response, String cookieName, String cookieValue, String domain, String path, Integer maxAge) {
        Cookie cookie = new Cookie(cookieName, cookieValue);
        if (domain != null) {
            cookie.setDomain(domain);
        }
        if (path != null) {
            cookie.setPath(path);
        } else {
            cookie.setPath(request.getContextPath() + "/");
        }
        if (maxAge != null) {
            cookie.setMaxAge(maxAge);
        } else {
            cookie.setMaxAge(-1);
        }
        cookie.setSecure(request.isSecure());
        response.addCookie(cookie);
        logger.debug("增加cookie [" + cookieName + "] = [" + cookieValue + "]");
    }

    public static void removeCookie(HttpServletRequest request, HttpServletResponse response, String cookieName, String domain, String path) {
        Cookie cookie = new Cookie(cookieName, "");
        if (domain != null) {
            cookie.setDomain(domain);
        }
        if (path != null) {
            cookie.setPath(path);
        } else {
            cookie.setPath(request.getContextPath() + "/");
        }
        cookie.setMaxAge(0);
        response.addCookie(cookie);
        logger.debug("删除cookie [" + cookieName + "]");
    }

    /**
     * 获取根域
     *
     * @param request
     * @return
     */
    public static String getRootDomain(HttpServletRequest request) {
        Pattern domainPattern = Pattern.compile("^.+?\\.(\\w+\\.[a-z]+)$", Pattern.CASE_INSENSITIVE);
        Matcher matcher = domainPattern.matcher(request.getServerName());
        if (matcher.matches()) {
            return matcher.group(1);
        }
        return null;
    }

    public static boolean isAPIRequest(HttpServletRequest request) {
        boolean ajax = "XMLHttpRequest".equals(request.getHeader("X-Requested-With"));
        boolean html = request.getHeader("Accept") != null && request.getHeader("Accept").contains("text/html");
        return ajax || !html;
    }

    public static void saveLastRequest(HttpServletRequest request) {
        if (request.getMethod().equalsIgnoreCase("GET") && !isAPIRequest(request)) {
            StringBuffer requestURL = request.getRequestURL();
            String query = request.getQueryString();
            if (query != null) {
                requestURL.append('?').append(query);
            }
            request.getSession().setAttribute(LAST_REQUEST_SESSION_KEY, requestURL.toString());
        }
    }

    public static String getLastRequestUrl(HttpServletRequest request, boolean remove) {
        String lastRequestUrl = null;
        HttpSession session = request.getSession(false);
        if (session != null) {
            lastRequestUrl = (String) session.getAttribute(LAST_REQUEST_SESSION_KEY);
            if (remove) {
                session.removeAttribute(LAST_REQUEST_SESSION_KEY);
            }
        }
        return lastRequestUrl;
    }

    public static void redirectToLastRequest(HttpServletRequest request, HttpServletResponse response, String fallbackUrl) throws IOException {
        String redirecturl = getLastRequestUrl(request, true);
        if (redirecturl == null) {
            redirecturl = fallbackUrl;
        }
        if (redirecturl == null) {
            throw new IllegalStateException("最后请求地址和fallbackUrl不能同时为空");
        }
        response.sendRedirect(redirecturl);

    }

    /**
     * 获取请求路径，不包含contextPath
     *
     * @param request
     * @return
     */
    public static String getPathWithinApplication(HttpServletRequest request) {
        String contextPath = getContextPath(request);
        String requestUri = getRequestUri(request);
        if (requestUri.startsWith(contextPath)) {
            String path = requestUri.substring(contextPath.length());
            return ((path.length() > 0) ? path : "/");
        } else {
            return requestUri;
        }
    }

    public static String getContextPath(HttpServletRequest request) {
        String contextPath = (String) request.getAttribute(INCLUDE_CONTEXT_PATH_ATTRIBUTE);
        if (contextPath == null) {
            contextPath = request.getContextPath();
        }
        contextPath = normalize(contextPath, true);
        if ("/".equals(contextPath)) {
            contextPath = "";
        }
        return contextPath;
    }

    public static String getRequestUri(HttpServletRequest request) {
        String uri = (String) request.getAttribute(INCLUDE_REQUEST_URI_ATTRIBUTE);
        if (uri == null) {
            uri = request.getRequestURI();
        }

        int semicolonIndex = uri.indexOf(';');
        uri = (semicolonIndex != -1 ? uri.substring(0, semicolonIndex) : uri);
        return normalize(uri, true);
    }

    private static String normalize(String path, boolean replaceBackSlash) {

        if (path == null)
            return null;

        // Create a place for the normalized path
        String normalized = path;

        if (replaceBackSlash && normalized.indexOf('\\') >= 0)
            normalized = normalized.replace('\\', '/');

        if (normalized.equals("/."))
            return "/";

        // Add a leading "/" if necessary
        if (!normalized.startsWith("/"))
            normalized = "/" + normalized;

        // Resolve occurrences of "//" in the normalized path
        while (true) {
            int index = normalized.indexOf("//");
            if (index < 0)
                break;
            normalized = normalized.substring(0, index) +
                    normalized.substring(index + 1);
        }

        // Resolve occurrences of "/./" in the normalized path
        while (true) {
            int index = normalized.indexOf("/./");
            if (index < 0)
                break;
            normalized = normalized.substring(0, index) +
                    normalized.substring(index + 2);
        }

        // Resolve occurrences of "/../" in the normalized path
        while (true) {
            int index = normalized.indexOf("/../");
            if (index < 0)
                break;
            if (index == 0)
                return (null);  // Trying to go outside our context
            int index2 = normalized.lastIndexOf('/', index - 1);
            normalized = normalized.substring(0, index2) +
                    normalized.substring(index + 3);
        }

        // Return the normalized path that we have completed
        return (normalized);

    }

    public static String cleanQueryStringParameter(HttpServletRequest request, String cleanParameter) {
        StringBuffer buffer = request.getRequestURL();
        if (request.getQueryString() != null) {
            String queryString = request.getQueryString().replaceFirst("(&)?" + cleanParameter + "=[^&]+", "");
            if (queryString.length() > 0) {
                buffer.append("?").append(queryString);
            }
        }
        return buffer.toString();
    }
}