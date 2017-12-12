package com.fangjian.xj.web.controller.demo;

import com.fangjian.framework.rest.result.vo.RestResultVO;
import com.fangjian.share.session.Constants;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;

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

    /**
     * demo2
     *
     * @param request
     * @return
     */
    @RequestMapping(value = "/demo2", method = {RequestMethod.GET})
    public RestResultVO<Object> demo2(HttpServletRequest request) {

        LOGGER.info("id:[{}]", request.getUserPrincipal().getName());

        return new RestResultVO(request.getUserPrincipal().getName() + "|" + request.getSession().getId());
    }


    /**
     * demo3
     *
     * @param request
     * @param response
     * @return
     */
    @RequestMapping(value = "/demo3", method = {RequestMethod.POST})
    public RestResultVO<Object> demo3(HttpServletRequest request, HttpServletResponse response) {

        HttpSession session = request.getSession();
        LOGGER.info("session:[{}]", session.getId());

        session.setAttribute(Constants.DEFAULT_PRINCIPAL_SESSION_KEY, "userid");//这里的userid登陆时候，放入具体值

        response.setHeader("x-auth-token", session.getId());

        return new RestResultVO(session.getId());
    }


}
