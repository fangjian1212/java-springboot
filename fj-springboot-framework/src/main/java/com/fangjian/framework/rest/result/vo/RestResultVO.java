package com.fangjian.framework.rest.result.vo;

import java.io.Serializable;

/**
 * @date:16/3/10 上午9:29
 * <p/>
 * Description:
 * <p/>
 */
public class RestResultVO<T> implements Serializable {

    private static final long serialVersionUID = 6609358423117831573L;
    private int code;
    private String message;
    private T dataMap;

    public RestResultVO() {

    }

    public RestResultVO(int code, String message, T dataMap) {
        this.code = code;
        this.message = message;
        this.dataMap = dataMap;
    }

    public RestResultVO(int code, String message) {
        this.code = code;
        this.message = message;
    }

    public RestResultVO(T dataMap) {
        this.dataMap = dataMap;
        this.code = RestResultCode.C200.getCode();
        this.message = RestResultCode.C200.getDesc();
    }


    public String getMessage() {
        return message;
    }

    public void setMessage(String message) {
        this.message = message;
    }

    public static <T> RestResultVO<T> getResult(T value) {
        return new RestResultVO<>(value);
    }

    public static RestResultVO getResult() {
        return new RestResultVO(null);
    }

    public int getCode() {
        return code;
    }

    public void setCode(int code) {
        this.code = code;
    }

    public T getDataMap() {
        return dataMap;
    }

    public void setDataMap(T dataMap) {
        this.dataMap = dataMap;
    }

}
