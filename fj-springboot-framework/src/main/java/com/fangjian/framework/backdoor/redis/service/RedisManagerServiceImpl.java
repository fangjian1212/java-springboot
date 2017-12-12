package com.fangjian.framework.backdoor.redis.service;

import com.fangjian.framework.backdoor.redis.vo.RedisVo;
import com.fangjian.framework.utils.self.json.JsonUtil;
import org.apache.commons.collections4.CollectionUtils;
import org.apache.commons.lang.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.stereotype.Service;
import org.springframework.util.Base64Utils;

import javax.annotation.Resource;
import java.util.Iterator;
import java.util.LinkedHashMap;
import java.util.Map;
import java.util.Set;
import java.util.concurrent.TimeUnit;

/**
 * 缓存管理
 * Created by fangjian on 2016/11/3.
 */
@Service("redis-ManagerService")
public class RedisManagerServiceImpl implements IRedisManagerService {


    private static final Logger LOG = LoggerFactory.getLogger(RedisManagerServiceImpl.class);


    //缓存前缀
    @Value("${redis.pattern.prefix}")
    private String patternPrefix = "jc";

    //操作秘钥
    @Value("${redis.delete.secret}")
    private String dbSecret = "";

    @Resource
    private RedisTemplate<String, Object> cacheRedisTemplate;


    /**
     * 清除缓存
     *
     * @param pattern "fc:houseList:*"
     */
    @Override
    public RedisVo delete(String pattern) {
        RedisVo redisVo = new RedisVo();
        LOG.info("RedisManagerService delete param=" + pattern);
        redisVo.setKeyPattern(pattern);
        if (StringUtils.isNotEmpty(pattern)) {
            Set<String> keys = cacheRedisTemplate.keys(pattern);
            redisVo.setKeys(keys);
            LOG.info("RedisManagerService delete keys=" + JsonUtil.toString(keys));
            if (CollectionUtils.isNotEmpty(keys)) {
                redisVo.setData(getDate(keys));
                //执行删除
                cacheRedisTemplate.delete(keys);
                redisVo.setSuccess(true);
                redisVo.setMessage("delete success...!");
            } else {
                redisVo.setMessage("delete pattern keys=null");
            }
        } else {
            redisVo.setMessage("delete pattern=null");
        }
        return redisVo;
    }

    /**
     * 清除缓存（无脑删除）
     *
     * @param pattern "fc:houseList:*"
     */
    @Override
    public RedisVo deleteAnyway(String pattern) {
        RedisVo redisVo = new RedisVo();
        LOG.info("RedisManagerService deleteAnyway param=" + pattern);
        redisVo.setKeyPattern(pattern);
        if (StringUtils.isNotEmpty(pattern)) {
            Set<String> keys = cacheRedisTemplate.keys(pattern);
            redisVo.setKeys(keys);
            LOG.info("RedisManagerService deleteAnyway keys=" + JsonUtil.toString(keys));
            if (CollectionUtils.isNotEmpty(keys)) {
//                redisVo.setData(getDate(keys));
                //执行删除
                cacheRedisTemplate.delete(keys);
                redisVo.setSuccess(true);
                redisVo.setMessage("deleteAnyway success...!");
            } else {
                redisVo.setMessage("deleteAnyway pattern keys=null");
            }
        } else {
            redisVo.setMessage("deleteAnyway pattern=null");
        }
        return redisVo;
    }

    /**
     * 获取缓存data
     *
     * @param pattern "fc:houseList:*"
     */
    @Override
    public RedisVo getData(String pattern) {
        RedisVo redisVo = new RedisVo();
        LOG.info("RedisManagerService getData param=" + pattern);
        redisVo.setKeyPattern(pattern);
        if (StringUtils.isNotEmpty(pattern)) {
            Set<String> keys = cacheRedisTemplate.keys(pattern);
            redisVo.setKeys(keys);
            LOG.info("RedisManagerService getData keys=" + JsonUtil.toString(keys));
            if (CollectionUtils.isNotEmpty(keys)) {
                redisVo.setData(getDate(keys));
                redisVo.setSuccess(true);
                redisVo.setMessage("getData success...!");
            } else {
                redisVo.setMessage("getData pattern keys=null");
            }
        } else {
            redisVo.setMessage("getData pattern=null");
        }
        return redisVo;
    }

    /**
     * 获取keys
     *
     * @param pattern "fc:houseList:*"
     */
    @Override
    public RedisVo getKeys(String pattern) {
        RedisVo redisVo = new RedisVo();
        LOG.info("RedisManagerService getData param=" + pattern);
        redisVo.setKeyPattern(pattern);
        if (StringUtils.isNotEmpty(pattern)) {
            Set<String> keys = cacheRedisTemplate.keys(pattern);
            redisVo.setKeys(keys);
            redisVo.setSuccess(true);
        } else {
            redisVo.setMessage("getKeys pattern=null");
        }
        return redisVo;
    }

    /**
     * 设置缓存
     *
     * @param key   缓存key，规则依赖全局
     * @param value 缓存key对应的value
     * @param day   缓存天数，默认30天
     */
    @Override
    public void setKV(String key, String value, Integer day) {
        if (StringUtils.isEmpty(key) || StringUtils.isEmpty(value)) {
            return;
        }
        if (null == day || 0 == day) {
            day = 30;
        }
        cacheRedisTemplate.opsForValue().set(key, value, day * 24 * 60 * 60, TimeUnit.SECONDS);
    }

    /**
     * 获取缓存value
     *
     * @param key
     * @return
     */
    @Override
    public String getV(String key) {
        LOG.info("getV()key[{}]", key);
        Object value = cacheRedisTemplate.opsForValue().get(key);
        if (null == value) {
            return "";
        }
        LOG.info("getV()key_value[{}][{}]", key, value.toString());
        return (String) value;
    }

    /**
     * 根据keys 封装缓存数据
     *
     * @param keys
     * @return
     */
    private Map getDate(Set<String> keys) {
        Map data = new LinkedHashMap();
        for (Iterator<String> it = keys.iterator(); it.hasNext(); ) {
            String next = it.next();
            data.put(next, cacheRedisTemplate.opsForValue().get(next));
        }
        return data;
    }


    /**
     * 简要权限认证
     *
     * @param secret
     * @return
     */
    @Override
    public boolean isPermissible(String secret) {
        if (StringUtils.isEmpty(secret)) {
            return false;
        }
        if (StringUtils.isEmpty(dbSecret)) {
            return false;
        }
        return secret.equals(new String(Base64Utils.decode(dbSecret.getBytes())));
    }


    /**
     * pattern 是否非法(是否房产缓存前缀)
     *
     * @param pattern
     * @return
     */
    @Override
    public boolean isIllegalPattern(String pattern) {
        if (StringUtils.isEmpty(pattern)) {
            return true;
        }
        if (StringUtils.isEmpty(patternPrefix)) {
            return true;
        }
        String[] prefixs = patternPrefix.split("[,]");
        for (int i = 0; i < prefixs.length; i++) {
            if (pattern.startsWith(prefixs[i])) {
                return false;
            }
        }
        return true;
    }

}
