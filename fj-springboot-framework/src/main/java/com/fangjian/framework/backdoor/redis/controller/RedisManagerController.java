package com.fangjian.framework.backdoor.redis.controller;

import com.fangjian.framework.backdoor.redis.service.IRedisManagerService;
import com.fangjian.framework.backdoor.redis.vo.RedisVo;
import com.fangjian.framework.rest.result.vo.RestResultCode;
import com.fangjian.framework.rest.result.vo.RestResultVO;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.web.bind.annotation.*;

import javax.annotation.Resource;

/**
 * 缓存后门程序
 * Created by fangjian on 2017/5/8.
 */
@RestController
@RequestMapping("backdoor/redis")
public class RedisManagerController {


    private static final Logger LOG = LoggerFactory.getLogger(RedisManagerController.class);

    //无效缓存key 提示
    private static final String PATTERN_ILLEGAL = "pattern is Illegal!!!";
    //无权访问 提示
    private static final String NO_PERMISSION = "NO Permission!!!";

    @Resource(name = "redis-ManagerService")
    private IRedisManagerService redisManagerService;


    /**
     * 清除fc缓存
     *
     * @param secret
     * @param pattern
     * @return
     */
    @RequestMapping(value = "delete/{pattern}", method = {RequestMethod.GET})
    public RestResultVO<RedisVo> delete(@RequestParam(value = "secret", required = true) String secret, @PathVariable("pattern") String pattern) {
        //keyPattern 校验
        if (redisManagerService.isIllegalPattern(pattern)) {
            return new RestResultVO(RestResultCode.C400.getCode(), RestResultCode.C400.getDesc(), PATTERN_ILLEGAL);
        }

        if (!redisManagerService.isPermissible(secret)) {
            return new RestResultVO(RestResultCode.C403.getCode(), RestResultCode.C403.getDesc(), NO_PERMISSION);
        }
        return new RestResultVO(redisManagerService.delete(pattern));

    }


    /**
     * 清除fc缓存 （无脑删除）
     *
     * @param secret
     * @param pattern
     * @return
     */
    @RequestMapping(value = "deleteAnyway/{pattern}", method = {RequestMethod.GET})
    public RestResultVO<RedisVo> deleteAnyway(@RequestParam(value = "secret", required = true) String secret, @PathVariable("pattern") String pattern) {
        //keyPattern 校验
//        if (redisManagerService.isIllegalPattern(pattern)) {
//            return new RestResultVO(RestResultCode.C400.getCode(), RestResultCode.C400.getDesc(), PATTERN_ILLEGAL);
//        }

        if (!redisManagerService.isPermissible(secret)) {
            return new RestResultVO(RestResultCode.C403.getCode(), RestResultCode.C403.getDesc(), NO_PERMISSION);
        }
        return new RestResultVO(redisManagerService.deleteAnyway(pattern));

    }

    /**
     * 查看fc缓存数据
     *
     * @param pattern
     * @return
     */
    @RequestMapping(value = "getData/{pattern}", method = {RequestMethod.GET})
    public RestResultVO<RedisVo> getData(@PathVariable("pattern") String pattern) {
        //keyPattern 校验
        if (redisManagerService.isIllegalPattern(pattern)) {
            return new RestResultVO(RestResultCode.C400.getCode(), RestResultCode.C400.getDesc(), PATTERN_ILLEGAL);
        }

        return new RestResultVO(redisManagerService.getData(pattern));

    }

    /**
     * 查看缓存keys
     *
     * @param pattern
     * @return
     */
    @RequestMapping(value = "getKeys/{pattern}", method = {RequestMethod.GET})
    public RestResultVO<RedisVo> getKeys(@PathVariable("pattern") String pattern) {
        //keyPattern 校验
        //code

        return new RestResultVO(redisManagerService.getKeys(pattern));

    }


    /**
     * 设置缓存
     *
     * @param secret
     * @param key    缓存key，规则依赖全局
     * @param value  缓存key对应的value
     * @param day    缓存天数，默认30天
     * @return
     */
    @RequestMapping(value = "set/{key}", method = {RequestMethod.GET})
    public RestResultVO<RedisVo> setKV(@RequestParam(value = "secret", required = true) String secret,
                                       @RequestParam(value = "value", required = true) String value,
                                       @RequestParam(value = "day", required = false) Integer day,
                                       @PathVariable("key") String key) {
        //keyPattern 校验
        if (redisManagerService.isIllegalPattern(key)) {
            return new RestResultVO(RestResultCode.C400.getCode(), RestResultCode.C400.getDesc(), PATTERN_ILLEGAL);
        }

        if (!redisManagerService.isPermissible(secret)) {
            return new RestResultVO(RestResultCode.C403.getCode(), RestResultCode.C403.getDesc(), NO_PERMISSION);
        }
        redisManagerService.setKV(key, value, day);
        return new RestResultVO();

    }


}
