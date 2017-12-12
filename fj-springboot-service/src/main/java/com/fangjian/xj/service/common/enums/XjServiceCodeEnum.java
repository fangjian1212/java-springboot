package com.fangjian.xj.service.common.enums;

/**
 * XjServiceCodeEnum code
 * Created by fangjian on 2016/11/7.
 */
public enum XjServiceCodeEnum {

    NO_AUTHORIZATION (-401,"未登录"),

    BAD_REQUEST(10001, "参数错误"),
    NO_RESULT(10002, "查询信息不存在"),
    UNKNOWN_ERROR(10003, "未知错误"),

    ;

    XjServiceCodeEnum(int code, String desc) {
        this.code = code;
        this.desc = desc;
    }

    private int code;
    private String desc;

    public int getCode() {
        return code;
    }

    public String getDesc() {
        return desc;
    }
}
