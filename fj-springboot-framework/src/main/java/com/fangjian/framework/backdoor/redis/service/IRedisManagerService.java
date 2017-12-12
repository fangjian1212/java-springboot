package com.fangjian.framework.backdoor.redis.service;


import com.fangjian.framework.backdoor.redis.vo.RedisVo;

/**
 * Created by fangjian on 2016/11/3.
 */
public interface IRedisManagerService {

    /**
     * 清除缓存
     *
     * @param pattern "fc:houseList:*"
     */
    RedisVo delete(String pattern);

    /**
     * 清除缓存（无脑删除）
     *
     * @param pattern "fc:houseList:*"
     */
    RedisVo deleteAnyway(String pattern);

    /**
     * 获取缓存data
     *
     * @param pattern "fc:houseList:*"
     */
    RedisVo getData(String pattern);

    /**
     * 获取keys
     *
     * @param pattern "fc:houseList:*"
     */
    RedisVo getKeys(String pattern);


    /**
     * 设置缓存
     *
     * @param key   缓存key，规则依赖全局
     * @param value 缓存key对应的value
     * @param day   缓存天数，默认30天
     */
    void setKV(String key, String value, Integer day);

    /**
     * 获取缓存value
     *
     * @param key
     * @return
     */
    String getV(String key);


    /**
     * 简要权限认证
     *
     * @param secret
     * @return
     */
    boolean isPermissible(String secret);

    /**
     * pattern 是否非法(是否房产缓存前缀)
     *
     * @param pattern
     * @return
     */
    boolean isIllegalPattern(String pattern);


}
