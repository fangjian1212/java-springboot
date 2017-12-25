package com.fangjian.xj.web.common.config;

import com.fangjian.framework.rest.result.vo.RestResultCode;
import com.fangjian.framework.rest.result.vo.RestResultVO;
import com.fangjian.framework.utils.self.json.JsonUtil;
import com.fangjian.xj.service.common.enums.XjServiceCodeEnum;
import com.fangjian.xj.service.common.exception.XjServiceException;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.util.CollectionUtils;
import org.springframework.util.StringUtils;
import org.springframework.validation.FieldError;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.servlet.HandlerExceptionResolver;
import org.springframework.web.servlet.ModelAndView;
import org.springframework.web.servlet.view.json.MappingJackson2JsonView;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.util.HashMap;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

/**
 * @date:15/12/22 上午10:18
 * <p/>
 * Description:
 * <p/>
 * c web 异常统一拦截处理
 */
@Configuration
public class ExceptionHandler implements HandlerExceptionResolver {

    private static final Logger LOGGER = LoggerFactory.getLogger(ExceptionHandler.class);

    public ModelAndView resolveException(HttpServletRequest request, HttpServletResponse response, Object handler,
                                         Exception ex) {
        ModelAndView mv = new ModelAndView();
        MappingJackson2JsonView view = new MappingJackson2JsonView();

        RestResultVO restResultVo = new RestResultVO();

        if (ex instanceof XjServiceException) {
            XjServiceException e = (XjServiceException) ex;
            XjServiceCodeEnum code = e.getcode();
            if (null != code) {
                restResultVo.setCode(code.getCode());
                restResultVo.setMessage(StringUtils.isEmpty(e.getMessage()) ? code.getDesc() : e.getMessage());
            } else {
                restResultVo.setCode(XjServiceCodeEnum.UNKNOWN_ERROR.getCode());
                restResultVo.setMessage(XjServiceCodeEnum.UNKNOWN_ERROR.getDesc() + "|" + e.getMessage());
            }
        } else if (ex instanceof MethodArgumentNotValidException) {
            LOGGER.info("MethodArgumentNotValidException...");
            MethodArgumentNotValidException e = (MethodArgumentNotValidException) ex;
            List<FieldError> fieldErrors = e.getBindingResult().getFieldErrors();
            Map<String, String> msg = new LinkedHashMap<>();
            if (!CollectionUtils.isEmpty(fieldErrors)) {
                for (FieldError fieldError : fieldErrors) {
                    if (null == fieldError) {
                        continue;
                    }
                    msg.put(fieldError.getField(), fieldError.getDefaultMessage());
                }
            }
            restResultVo.setCode(RestResultCode.C400.getCode());
            restResultVo.setMessage(RestResultCode.C400.getDesc());
            restResultVo.setDataMap(msg.toString());
        } else {
            restResultVo.setCode(RestResultCode.C500.getCode());
            restResultVo.setMessage(RestResultCode.C500.getDesc() + "|" + ex.getMessage());
            restResultVo.setDataMap(ex);
            LOGGER.error("500 error===:{}", ex);
        }


        view.setAttributesMap(JsonUtil.toBean(restResultVo, HashMap.class));
        mv.setView(view);
        return mv;
    }

    @Bean
    public ExceptionHandler createExceptionHandler() {
        return new ExceptionHandler();
    }
}
