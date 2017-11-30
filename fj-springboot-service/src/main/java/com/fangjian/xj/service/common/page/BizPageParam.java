package com.fangjian.xj.service.common.page;

import java.io.Serializable;

/**
 * <p/>
 * 需要分页查询的bean继承此类
 */
public class BizPageParam implements Serializable {

    private static final long serialVersionUID = 2142926093095478252L;

    private Integer pageNo = 0; //前端不传，默认不分页

    private Integer pageSize = 10;//前端不传，默认取10条

    public boolean isPage() {
        return pageNo > 0;
    }

    public int getPageNo() {
        return pageNo;
    }

    public void setPageNo(int pageNo) {
        this.pageNo = pageNo;
    }

    public int getPageSize() {
        return pageSize;
    }

    public void setPageSize(int pageSize) {
        this.pageSize = pageSize;
    }

    public Integer getStartRow() {
        return this.pageNo > 0 ? (this.pageNo - 1) * this.pageSize : 0;

    }

    public Integer getEndRow() {
        return this.getStartRow() + this.pageSize;
    }
}
