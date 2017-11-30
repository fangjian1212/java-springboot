package com.fangjian.xj.web.controller.demo;

import com.fangjian.framework.rest.result.vo.RestResultVO;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

/**
 * @description:
 * @author: fangjian<jian.fang@chinaredstar.com>
 * @date: Create in 11:08 2017/11/30
 * @modified by:
 */
@RestController
@RequestMapping("/demo")
public class DemoController {

    private static final Logger LOGGER = LoggerFactory.getLogger(DemoController.class);


    /**
     * demo
     *
     * @param id
     * @return
     */
    @RequestMapping(value = "/demo", method = {RequestMethod.GET})
    public RestResultVO<Object> demo(@RequestParam Integer id) {
        LOGGER.info("id:[{}]", id);
        return new RestResultVO(id);
    }


}
