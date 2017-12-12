/*
 */
package com.fangjian.xj.session.core;

import com.fangjian.xj.session.Constants;
import com.fangjian.xj.session.EnableSession;
import com.fangjian.xj.session.SessionException;
import com.fangjian.xj.session.SessionGroup;
import com.fangjian.xj.session.cookie.CookieNameResolver;
import com.fangjian.xj.session.cookie.DynamicCookieSerializer;
import com.fangjian.xj.session.cookie.FixedCookieNameResolver;
import com.fangjian.xj.session.cookie.SessionGroupCookieNameResolver;
import com.fangjian.xj.session.redis.HashValueDeserializeDelayedRedisTemplate;
import com.fangjian.xj.session.redis.HashValueDeserializePredicate;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.springframework.beans.BeansException;
import org.springframework.beans.factory.*;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.context.ApplicationEventPublisher;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.ImportAware;
import org.springframework.core.annotation.AnnotationAttributes;
import org.springframework.core.type.AnnotationMetadata;
import org.springframework.data.redis.connection.RedisConnection;
import org.springframework.data.redis.connection.RedisConnectionFactory;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.data.redis.listener.PatternTopic;
import org.springframework.data.redis.listener.RedisMessageListenerContainer;
import org.springframework.data.redis.serializer.RedisSerializer;
import org.springframework.data.redis.serializer.StringRedisSerializer;
import org.springframework.scheduling.annotation.EnableScheduling;
import org.springframework.session.ExpiringSession;
import org.springframework.session.FindByIndexNameSessionRepository;
import org.springframework.session.SessionRepository;
import org.springframework.session.data.redis.RedisFlushMode;
import org.springframework.session.data.redis.config.ConfigureNotifyKeyspaceEventsAction;
import org.springframework.session.data.redis.config.ConfigureRedisAction;
import org.springframework.session.web.http.*;
import org.springframework.util.Assert;
import org.springframework.util.StringUtils;

import javax.annotation.PostConstruct;
import javax.servlet.ServletContext;
import javax.servlet.http.HttpSessionListener;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Map;
import java.util.concurrent.Executor;

/**
 * 基于redis的分布式session配置
 */
@Configuration
@EnableScheduling
public class RedstarRedisSessionConfiguration implements ImportAware, BeanFactoryAware {
    public static final Log LOG = LogFactory.getLog(RedstarRedisSessionConfiguration.class);

    private Integer maxInactiveIntervalInSeconds = 1800;

    private ConfigureRedisAction configureRedisAction = new ConfigureNotifyKeyspaceEventsAction();

    /**
     * 命名空间，将根据sessionGroup配置命名空间，不同的用户群在不同的命名空间下
     */
    private String namespace = "";

    private RedisFlushMode redisFlushMode = RedisFlushMode.ON_SAVE;

    private RedisSerializer<Object> defaultRedisSerializer;

    private Executor redisTaskExecutor;

    private Executor redisSubscriptionExecutor;

    private List<HttpSessionListener> httpSessionListeners = new ArrayList<HttpSessionListener>();

    private ServletContext servletContext;

    private String connectionFactoryBeanName;

    /**
     * 会话组（用户群体）
     */
    private SessionGroup sessionGroup;

    private BeanFactory beanFactory;

    private String cookieName = Constants.DEFAULT_COOKIE_NAME;
    private String cookieDomain;
    private String cookieDomainPattern = Constants.DEFAULT_COOKIE_DOMAIN_PATTERN;
    /**
     * 是否允许创建session，默认：true
     */
    private boolean allowCreateSession = true;
    /**
     * 是否允许手动设置超时时间，默认：false
     */
    private boolean allowManualTimeout = false;
    /**
     * principal 在 session 中的键名
     */
    private String principalSessionKey = Constants.DEFAULT_PRINCIPAL_SESSION_KEY;

    private RedisConnectionFactory redisConnectionFactory;

    private SessionGroupResolver sessionGroupResolver;

    @Bean
    public RedisMessageListenerContainer redisMessageListenerContainer(RedisOperationsSessionRepository messageListener) {

        RedisMessageListenerContainer container = new RedisMessageListenerContainer();
        container.setConnectionFactory(redisConnectionFactory);
        if (redisTaskExecutor != null) {
            container.setTaskExecutor(redisTaskExecutor);
        }
        if (redisSubscriptionExecutor != null) {
            container.setSubscriptionExecutor(redisSubscriptionExecutor);
        }
        container.addMessageListener(messageListener,
                Arrays.asList(new PatternTopic("__keyevent@0:del"), new PatternTopic("__keyevent@0:expired")));
        container.addMessageListener(messageListener, Arrays.asList(new PatternTopic(messageListener.getSessionCreatedChannelPrefix() + "*")));
        return container;
    }

    @Bean
    public RedisTemplate<Object, Object> sessionRedisTemplate() {
        RedisTemplate<Object, Object> template = new HashValueDeserializeDelayedRedisTemplate<Object, Object>(new HashValueDeserializePredicate() {
            @Override
            public boolean isDelayed(Object key, byte[] value) {
                if (key instanceof String) {
                    String keyStr = (String) key;
                    return keyStr.startsWith("sessionAttr:") && !keyStr.endsWith(FindByIndexNameSessionRepository.PRINCIPAL_NAME_INDEX_NAME);
                } else {
                    return false;
                }
            }
        });
        template.setKeySerializer(new StringRedisSerializer());
        template.setHashKeySerializer(new StringRedisSerializer());
        if (defaultRedisSerializer != null) {
            template.setDefaultSerializer(defaultRedisSerializer);
        }
        template.setConnectionFactory(redisConnectionFactory);
        return template;
    }

    @Bean
    public RedisOperationsSessionRepository sessionRepository(ApplicationEventPublisher applicationEventPublisher) {
        RedisOperationsSessionRepository sessionRepository = new RedisOperationsSessionRepository(sessionRedisTemplate());
        sessionRepository.setApplicationEventPublisher(applicationEventPublisher);
        sessionRepository.setDefaultMaxInactiveInterval(maxInactiveIntervalInSeconds);
        if (defaultRedisSerializer != null) {
            sessionRepository.setDefaultSerializer(defaultRedisSerializer);
        }

        String redisNamespace = getRedisNamespace();
        if (StringUtils.hasText(redisNamespace)) {
//            sessionRepository.setRedisKeyNamespace(redisNamespace);
        }

        sessionRepository.setRedisFlushMode(redisFlushMode);
        return sessionRepository;
    }

    private String getRedisNamespace() {
        if (StringUtils.hasText(this.namespace)) {
            return this.namespace;
        }
        return System.getProperty("spring.session.redis.namespace", "");
    }

    @Override
    public void setImportMetadata(AnnotationMetadata importMetadata) {

        Map<String, Object> enableAttrMap = importMetadata.getAnnotationAttributes(EnableSession.class.getName());
        AnnotationAttributes enableAttrs = AnnotationAttributes.fromMap(enableAttrMap);
        maxInactiveIntervalInSeconds = enableAttrs.getNumber("maxInactiveIntervalInSeconds");
        this.redisFlushMode = enableAttrs.getEnum("redisFlushMode");
        this.connectionFactoryBeanName = enableAttrs.getString("connectionFactory");
        this.sessionGroup = enableAttrs.getEnum("group");
        this.cookieName = enableAttrs.getString("cookieName");
        this.cookieDomain = enableAttrs.getString("cookieDomain");
        this.cookieDomainPattern = enableAttrs.getString("cookieDomainPattern");
        this.allowCreateSession = enableAttrs.getBoolean("allowCreateSession");
        this.allowManualTimeout = enableAttrs.getBoolean("allowManualTimeout");
        this.principalSessionKey = enableAttrs.getString("principalSessionKey");
    }

    @Bean
    public InitializingBean enableRedisKeyspaceNotificationsInitializer() {
        return new EnableRedisKeyspaceNotificationsInitializer(redisConnectionFactory, configureRedisAction);
    }

    /**
     * Ensures that Redis is configured to send keyspace notifications. This is important to ensure that expiration and
     * deletion of sessions trigger SessionDestroyedEvents. Without the SessionDestroyedEvent resources may not get
     * cleaned up properly. For example, the mapping of the Session to WebSocket connections may not get cleaned up.
     */
    static class EnableRedisKeyspaceNotificationsInitializer implements InitializingBean {

        private final RedisConnectionFactory connectionFactory;
        private ConfigureRedisAction configure;

        EnableRedisKeyspaceNotificationsInitializer(RedisConnectionFactory connectionFactory, ConfigureRedisAction configure) {
            this.connectionFactory = connectionFactory;
            this.configure = configure;
        }

        @Override
        public void afterPropertiesSet() throws Exception {
            RedisConnection connection = connectionFactory.getConnection();
            configure.configure(connection);
        }

    }

    @Bean
    public SessionEventHttpSessionListenerAdapter sessionEventHttpSessionListenerAdapter() {
        return new SessionEventHttpSessionListenerAdapter(httpSessionListeners);
    }

    @Bean
    public <S extends ExpiringSession> SessionRepositoryFilter<? extends ExpiringSession> springSessionRepositoryFilter(
            SessionRepository<S> sessionRepository, @Qualifier("httpSessionStrategy") HttpSessionStrategy httpSessionStrategy) {
        SessionRepositoryFilter<S> sessionRepositoryFilter = new SessionRepositoryFilter<S>(sessionRepository);
        sessionRepositoryFilter.setServletContext(servletContext);
        if (httpSessionStrategy instanceof MultiHttpSessionStrategy) {
            sessionRepositoryFilter.setHttpSessionStrategy((MultiHttpSessionStrategy) httpSessionStrategy);
        } else {
            sessionRepositoryFilter.setHttpSessionStrategy(httpSessionStrategy);
        }
        return sessionRepositoryFilter;
    }

    ///////////////////////////////////////////////////////////////////////////
    // 初始化方法
    ///////////////////////////////////////////////////////////////////////////
    @PostConstruct
    public void initialize() {
        if (principalSessionKey == null) {
            throw new SessionException("principalSessionKey 没有配置");
        }
        initializeNamespace();
        initializeRedisConnectionFactory();
    }

    private void initializeNamespace() {
        if (sessionGroup != null) {
            this.namespace = sessionGroup.toString().toLowerCase();
        }
    }

    /**
     * 初始化RedisConnectionFactory
     */
    public void initializeRedisConnectionFactory() {
        if (redisConnectionFactory == null) {
            if (StringUtils.hasText(this.connectionFactoryBeanName)) {
                redisConnectionFactory = beanFactory.getBean(this.connectionFactoryBeanName, RedisConnectionFactory.class);
            } else {
                try {
                    //获取当前容器中配置的RedisConnectionFactory
                    redisConnectionFactory = beanFactory.getBean(RedisConnectionFactory.class);
                } catch (NoUniqueBeanDefinitionException e) {
                    throw new SessionException("发现" + e.getNumberOfBeansFound() +
                            "个RedisConnectionFactory配置，可通过@EnableRedstarSession的connectionFactory属性指定Session所使用的RedisConnectionFactory的beanName，" +
                            "详见 http://wiki.corp.rs.com/pages/viewpage.action?pageId=7345867");
                } catch (NoSuchBeanDefinitionException e) {
                    throw new SessionException(
                            "RedisConnectionFactory 未定义，可通过@EnableRedstarSession的connectionFactory属性指定RedisConnectionFactory的beanName，" +
                                    "详见 http://wiki.corp.rs.com/pages/viewpage.action?pageId=7345867");
                }
            }
        }

    }

    @Bean
    public HttpSessionStrategy httpSessionStrategy() {
        List<MultiHttpSessionStrategy> httpSessionStrategies = new ArrayList<>();
        httpSessionStrategies.add(HttpSessionStrategyFactory.createMultiHttpSessionStrategy(headerHttpSessionStrategy()));
        //新的cookie,根据SessionGroup来区分
        httpSessionStrategies.add(HttpSessionStrategyFactory.createMultiHttpSessionStrategy(cookieHttpSessionStrategy()));
        //向下兼容名为SESSION的cookie
//        httpSessionStrategies.add(HttpSessionStrategyFactory.createMultiHttpSessionStrategy(legacyCookieHttpSessionStrategy()));

        httpSessionStrategies.add(HttpSessionStrategyFactory.createMultiHttpSessionStrategy(new SessionResponsePostProcessor(allowCreateSession, allowManualTimeout)));
        httpSessionStrategies.add(HttpSessionStrategyFactory.createMultiHttpSessionStrategy(new PrincipalRequestPostProcessor(principalSessionKey)));
        return new HttpSessionStrategyComposite(httpSessionStrategies);
    }

    private HttpSessionStrategy headerHttpSessionStrategy() {
        return new HeaderHttpSessionStrategy();
    }

    private HttpSessionStrategy cookieHttpSessionStrategy() {
        CookieHttpSessionStrategy cookieHttpSessionStrategy = new CookieHttpSessionStrategy();
        cookieHttpSessionStrategy.setCookieSerializer(cookieSerializer());
        return cookieHttpSessionStrategy;
    }

    private HttpSessionStrategy legacyCookieHttpSessionStrategy() {
        CompatibleCookieHttpSessionStrategy cookieHttpSessionStrategy = new CompatibleCookieHttpSessionStrategy();
        cookieHttpSessionStrategy.setCookieName(Constants.DEFAULT_COOKIE_NAME);
        DefaultCookieSerializer cookieSerializer = new DefaultCookieSerializer();
        if (StringUtils.hasText(cookieDomain)) {
            cookieSerializer.setDomainName(cookieDomain);

        } else if (StringUtils.hasText(cookieDomainPattern)) {
            cookieSerializer.setDomainNamePattern(cookieDomainPattern);
        }
        cookieSerializer.setCookiePath("/");
        cookieHttpSessionStrategy.setCookieSerializer(cookieSerializer);
        return cookieHttpSessionStrategy;
    }

    @Bean
    public CookieSerializer cookieSerializer() {
        DynamicCookieSerializer cookieSerializer = new DynamicCookieSerializer();
        cookieSerializer.setCookieNameResolver(cookieNameResolver());

        if (StringUtils.hasText(cookieDomain)) {
            cookieSerializer.setDomainName(cookieDomain);
        } else if (StringUtils.hasText(cookieDomainPattern)) {
            cookieSerializer.setDomainNamePattern(cookieDomainPattern);
        }
        cookieSerializer.setCookiePath("/");
        return cookieSerializer;
    }

    @Bean
    public CookieNameResolver cookieNameResolver() {
        if (sessionGroupResolver != null) {
            return new SessionGroupCookieNameResolver(sessionGroupResolver);
        } else {
            String fixedCookieName = getCookieName();
            return new FixedCookieNameResolver(fixedCookieName);
        }
    }

    private String getCookieName() {
        String finalCookieName = null;
        if (StringUtils.hasText(cookieName)) {
            finalCookieName = cookieName;
        } else {
            finalCookieName = Constants.DEFAULT_COOKIE_NAME;
        }
        if (sessionGroup != null) {
            finalCookieName = finalCookieName.concat(".").concat(sessionGroup.name().toLowerCase());
        }
        return finalCookieName;
    }

    /**
     * Sets the action to perform for configuring Redis.
     *
     * @param configureRedisAction the configureRedis to set. The default is {@link ConfigureNotifyKeyspaceEventsAction}.
     */
    @Autowired(required = false)
    public void setConfigureRedisAction(ConfigureRedisAction configureRedisAction) {
        this.configureRedisAction = configureRedisAction;
    }

    @Autowired(required = false)
    @Qualifier("springSessionDefaultRedisSerializer")
    public void setDefaultRedisSerializer(RedisSerializer<Object> defaultRedisSerializer) {
        this.defaultRedisSerializer = defaultRedisSerializer;
    }

    @Autowired(required = false)
    @Qualifier("springSessionRedisTaskExecutor")
    public void setRedisTaskExecutor(Executor redisTaskExecutor) {
        this.redisTaskExecutor = redisTaskExecutor;
    }

    @Autowired(required = false)
    @Qualifier("springSessionRedisSubscriptionExecutor")
    public void setRedisSubscriptionExecutor(Executor redisSubscriptionExecutor) {
        this.redisSubscriptionExecutor = redisSubscriptionExecutor;
    }

    @Autowired(required = false)
    public void setServletContext(ServletContext servletContext) {
        this.servletContext = servletContext;
    }

    @Autowired(required = false)
    public void setHttpSessionListeners(List<HttpSessionListener> listeners) {
        this.httpSessionListeners = listeners;
    }

    @Override
    public void setBeanFactory(BeanFactory beanFactory) throws BeansException {
        this.beanFactory = beanFactory;
    }

    @Autowired(required = false)
    public void setSessionGroupResolver(SessionGroupResolver sessionGroupResolver) {
        this.sessionGroupResolver = sessionGroupResolver;
    }
    ///////////////////////////////////////////////////////////////////////////
    // setter 方法，提供给基于xml的配置方式使用
    ///////////////////////////////////////////////////////////////////////////

    public void setMaxInactiveIntervalInSeconds(int maxInactiveIntervalInSeconds) {
        this.maxInactiveIntervalInSeconds = maxInactiveIntervalInSeconds;
    }

    public void setRedisFlushMode(RedisFlushMode redisFlushMode) {
        Assert.notNull(redisFlushMode, "redisFlushMode cannot be null");
        this.redisFlushMode = redisFlushMode;
    }

    public void setConnectionFactoryBeanName(String connectionFactoryBeanName) {
        this.connectionFactoryBeanName = connectionFactoryBeanName;
    }

    public void setSessionGroup(SessionGroup sessionGroup) {
        this.sessionGroup = sessionGroup;
    }

    public void setCookieDomain(String cookieDomain) {
        this.cookieDomain = cookieDomain;
    }

    public void setCookieName(String cookieName) {
        this.cookieName = cookieName;
    }

    public void setCookieDomainPattern(String cookieDomainPattern) {
        this.cookieDomainPattern = cookieDomainPattern;
    }

    public void setAllowCreateSession(boolean allowCreateSession) {
        this.allowCreateSession = allowCreateSession;
    }

    public void setAllowManualTimeout(boolean allowManualTimeout) {
        this.allowManualTimeout = allowManualTimeout;
    }

    public void setPrincipalSessionKey(String principalSessionKey) {
        this.principalSessionKey = principalSessionKey;
    }
}
