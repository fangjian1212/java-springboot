/*
 * Copyright 2014-2016 the original author or authors.
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *      http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package com.fangjian.xj.session.core;

import org.springframework.session.Session;
import org.springframework.session.web.http.*;
import org.springframework.session.web.http.CookieSerializer.CookieValue;
import org.springframework.util.Assert;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpServletResponseWrapper;
import java.io.UnsupportedEncodingException;
import java.net.URLEncoder;
import java.util.*;
import java.util.regex.Pattern;

/**
 * 兼容老的session cookie，允许同一个session 写多个cookie
 */
public class CompatibleCookieHttpSessionStrategy
        implements MultiHttpSessionStrategy, HttpSessionManager {
    private static final String DEFAULT_DELIMITER = " ";

    private static final String SESSION_IDS_WRITTEN_ATTR = CompatibleCookieHttpSessionStrategy.class
            .getName().concat(".SESSIONS_WRITTEN_ATTR");

    static final String DEFAULT_ALIAS = "0";

    static final String DEFAULT_SESSION_ALIAS_PARAM_NAME = "_s";

    private static final Pattern ALIAS_PATTERN = Pattern.compile("^[\\w-]{1,50}$");

    private String sessionParam = DEFAULT_SESSION_ALIAS_PARAM_NAME;

    private CookieSerializer cookieSerializer = new DefaultCookieSerializer();

    private String deserializationDelimiter = DEFAULT_DELIMITER;

    private String serializationDelimiter = DEFAULT_DELIMITER;

    //session的cookie名
    private String cookieName;

    public String getRequestedSessionId(HttpServletRequest request) {
        Map<String, String> sessionIds = getSessionIds(request);
        String sessionAlias = getCurrentSessionAlias(request);
        return sessionIds.get(sessionAlias);
    }

    public String getCurrentSessionAlias(HttpServletRequest request) {
        if (this.sessionParam == null) {
            return DEFAULT_ALIAS;
        }
        String u = request.getParameter(this.sessionParam);
        if (u == null) {
            return DEFAULT_ALIAS;
        }
        if (!ALIAS_PATTERN.matcher(u).matches()) {
            return DEFAULT_ALIAS;
        }
        return u;
    }

    public String getNewSessionAlias(HttpServletRequest request) {
        Set<String> sessionAliases = getSessionIds(request).keySet();
        if (sessionAliases.isEmpty()) {
            return DEFAULT_ALIAS;
        }
        long lastAlias = Long.decode(DEFAULT_ALIAS);
        for (String alias : sessionAliases) {
            long selectedAlias = safeParse(alias);
            if (selectedAlias > lastAlias) {
                lastAlias = selectedAlias;
            }
        }
        return Long.toHexString(lastAlias + 1);
    }

    private long safeParse(String hex) {
        try {
            return Long.decode("0x" + hex);
        } catch (NumberFormatException notNumber) {
            return 0;
        }
    }

    public void onNewSession(Session session, HttpServletRequest request,
                             HttpServletResponse response) {
        Set<String> sessionIdsWritten = getSessionIdsWritten(request);
        //判断是否session是否写过,不同的cookie都可以写一次
        if (sessionIdsWritten.contains(session.getId())) {
            return;
        }
        sessionIdsWritten.add(session.getId());

        Map<String, String> sessionIds = getSessionIds(request);
        String sessionAlias = getCurrentSessionAlias(request);
        sessionIds.put(sessionAlias, session.getId());

        String cookieValue = createSessionCookieValue(sessionIds);
        this.cookieSerializer
                .writeCookieValue(new CookieValue(request, response, cookieValue));
    }

    @SuppressWarnings("unchecked")
    private Set<String> getSessionIdsWritten(HttpServletRequest request) {
        String sessionIdsWrittenAttr = getSessionIdsWrittenAttr();
        Set<String> sessionsWritten = (Set<String>) request.getAttribute(sessionIdsWrittenAttr);
        if (sessionsWritten == null) {
            sessionsWritten = new HashSet<String>();
            request.setAttribute(sessionIdsWrittenAttr, sessionsWritten);
        }
        return sessionsWritten;
    }


    private String getSessionIdsWrittenAttr() {
        if (cookieName != null) {
            return SESSION_IDS_WRITTEN_ATTR.concat(cookieName);
        }
        return SESSION_IDS_WRITTEN_ATTR;
    }

    private String createSessionCookieValue(Map<String, String> sessionIds) {
        if (sessionIds.isEmpty()) {
            return "";
        }
        if (sessionIds.size() == 1 && sessionIds.keySet().contains(DEFAULT_ALIAS)) {
            return sessionIds.values().iterator().next();
        }

        StringBuffer buffer = new StringBuffer();
        for (Map.Entry<String, String> entry : sessionIds.entrySet()) {
            String alias = entry.getKey();
            String id = entry.getValue();

            buffer.append(alias);
            buffer.append(this.serializationDelimiter);
            buffer.append(id);
            buffer.append(this.serializationDelimiter);
        }
        buffer.deleteCharAt(buffer.length() - 1);
        return buffer.toString();
    }

    public void onInvalidateSession(HttpServletRequest request,
                                    HttpServletResponse response) {
        Map<String, String> sessionIds = getSessionIds(request);
        String requestedAlias = getCurrentSessionAlias(request);
        sessionIds.remove(requestedAlias);

        String cookieValue = createSessionCookieValue(sessionIds);
        this.cookieSerializer
                .writeCookieValue(new CookieValue(request, response, cookieValue));
    }

    /**
     * Sets the name of the HTTP parameter that is used to specify the session alias. If
     * the value is null, then only a single session is supported per browser.
     *
     * @param sessionAliasParamName the name of the HTTP parameter used to specify the
     *                              session alias. If null, then ony a single session is supported per browser.
     */
    public void setSessionAliasParamName(String sessionAliasParamName) {
        this.sessionParam = sessionAliasParamName;
    }

    /**
     * Sets the {@link CookieSerializer} to be used.
     *
     * @param cookieSerializer the cookieSerializer to set. Cannot be null.
     */
    public void setCookieSerializer(CookieSerializer cookieSerializer) {
        Assert.notNull(cookieSerializer, "cookieSerializer cannot be null");
        this.cookieSerializer = cookieSerializer;
    }

    public void setCookieName(String cookieName) {
        this.cookieName = cookieName;
    }

    /**
     * Sets the delimiter between a session alias and a session id when deserializing a cookie. The default is " "
     * This is useful when using <a href="https://tools.ietf.org/html/rfc6265">RFC
     * 6265</a> for writing the cookies which doesn't allow for spaces in the cookie
     * values.
     *
     * @param delimiter the delimiter to set (i.e. "_ " will try a delimeter of either "_" or " ")
     */
    public void setDeserializationDelimiter(String delimiter) {
        this.deserializationDelimiter = delimiter;
    }

    /**
     * Sets the delimiter between a session alias and a session id when deserializing a cookie. The default is " ".
     * This is useful when using <a href="https://tools.ietf.org/html/rfc6265">RFC
     * 6265</a> for writing the cookies which doesn't allow for spaces in the cookie
     * values.
     *
     * @param delimiter the delimiter to set (i.e. "_")
     */
    public void setSerializationDelimiter(String delimiter) {
        this.serializationDelimiter = delimiter;
    }

    public Map<String, String> getSessionIds(HttpServletRequest request) {
        List<String> cookieValues = this.cookieSerializer.readCookieValues(request);
        String sessionCookieValue = cookieValues.isEmpty() ? ""
                : cookieValues.iterator().next();
        Map<String, String> result = new LinkedHashMap<String, String>();
        StringTokenizer tokens = new StringTokenizer(sessionCookieValue, this.deserializationDelimiter);
        if (tokens.countTokens() == 1) {
            result.put(DEFAULT_ALIAS, tokens.nextToken());
            return result;
        }
        while (tokens.hasMoreTokens()) {
            String alias = tokens.nextToken();
            if (!tokens.hasMoreTokens()) {
                break;
            }
            String id = tokens.nextToken();
            result.put(alias, id);
        }
        return result;
    }

    public HttpServletRequest wrapRequest(HttpServletRequest request,
                                          HttpServletResponse response) {
        request.setAttribute(HttpSessionManager.class.getName(), this);
        return request;
    }

    public HttpServletResponse wrapResponse(HttpServletRequest request,
                                            HttpServletResponse response) {
        return new MultiSessionHttpServletResponse(response, request);
    }

    public String encodeURL(String url, String sessionAlias) {
        String encodedSessionAlias = urlEncode(sessionAlias);
        int queryStart = url.indexOf("?");
        boolean isDefaultAlias = DEFAULT_ALIAS.equals(encodedSessionAlias);
        if (queryStart < 0) {
            return isDefaultAlias ? url
                    : url + "?" + this.sessionParam + "=" + encodedSessionAlias;
        }
        String path = url.substring(0, queryStart);
        String query = url.substring(queryStart + 1, url.length());
        String replacement = isDefaultAlias ? "" : "$1" + encodedSessionAlias;
        query = query.replaceFirst("((^|&)" + this.sessionParam + "=)([^&]+)?",
                replacement);
        if (!isDefaultAlias && url.endsWith(query)) {
            // no existing alias
            if (!(query.endsWith("&") || query.length() == 0)) {
                query += "&";
            }
            query += this.sessionParam + "=" + encodedSessionAlias;
        }

        return path + "?" + query;
    }

    private String urlEncode(String value) {
        try {
            return URLEncoder.encode(value, "UTF-8");
        } catch (UnsupportedEncodingException e) {
            throw new RuntimeException(e);
        }
    }

    /**
     * A {@link CookieHttpSessionStrategy} aware {@link HttpServletResponseWrapper}.
     */
    class MultiSessionHttpServletResponse extends HttpServletResponseWrapper {
        private final HttpServletRequest request;

        MultiSessionHttpServletResponse(HttpServletResponse response,
                                        HttpServletRequest request) {
            super(response);
            this.request = request;
        }

        @Override
        public String encodeRedirectURL(String url) {
            url = super.encodeRedirectURL(url);
            return CompatibleCookieHttpSessionStrategy.this.encodeURL(url,
                    getCurrentSessionAlias(this.request));
        }

        @Override
        public String encodeURL(String url) {
            url = super.encodeURL(url);

            String alias = getCurrentSessionAlias(this.request);
            return CompatibleCookieHttpSessionStrategy.this.encodeURL(url, alias);
        }
    }

}
