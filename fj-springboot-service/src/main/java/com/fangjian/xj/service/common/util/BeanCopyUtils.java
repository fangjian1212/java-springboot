package com.fangjian.xj.service.common.util;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.BeanUtils;
import org.springframework.util.CollectionUtils;

import java.util.ArrayList;
import java.util.List;

/**
 * 集合工具类
 */
public class BeanCopyUtils {

    private static Logger logger = LoggerFactory.getLogger(BeanCopyUtils.class);

    public static List BeanCopyList(List sources, Class target) {

        try {
            List list = new ArrayList();
            //如果入参为空，则不进行处理
            if (CollectionUtils.isEmpty(sources)) {
                return list;
            }
            //拷贝
            for (Object source : sources) {
                Object obj = target.newInstance();
                BeanUtils.copyProperties(source, obj);
                list.add(obj);
            }
            return list;
        } catch (Exception e) {
            logger.error(e.getMessage());
            throw new RuntimeException("集合BeanCopy异常");
        }
    }


    public static <T> T BeanCopy(Object source, Class<T> clazz) {
        try {
            T object = clazz.newInstance();
            if (source == null) {
                return null;
            }
            BeanUtils.copyProperties(source, object);
            return object;
        } catch (Exception e) {
            logger.error(e.getMessage());
            throw new RuntimeException("BeanCopy异常");
        }
    }


}
