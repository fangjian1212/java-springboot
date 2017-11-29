package com.fangjian.framework.utils;

import java.util.Map;

/**
 * @date:2016/11/9 下午1:44
 *
 * Description:
 *
 * 数据校验结果
 */
public class ValidationResult {
    //校验结果是否有错
    private boolean hasErrors;

    //校验错误信息
    private Map<String, String> errorMsg;

    public boolean isHasErrors() {
        return hasErrors;
    }

    public void setHasErrors(boolean hasErrors) {
        this.hasErrors = hasErrors;
    }

    public Map<String, String> getErrorMsg() {
        return errorMsg;
    }

    public void setErrorMsg(Map<String, String> errorMsg) {
        this.errorMsg = errorMsg;
    }

    @Override
    public String toString() {
        return "ValidationResult [hasErrors=" + hasErrors + ", errorMsg="
                + errorMsg + "]";
    }
}
