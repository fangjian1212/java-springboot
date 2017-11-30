package com.fangjian.xj.service.common.page;


import java.io.Serializable;

/**
 * @title :
 */
public class BizPageResult<T> implements Serializable {
    private int totalPage;
    private int currentPage;
    private int showCount;
    private int totalResult;
    private T data;
    private boolean hasNextPage;

    public BizPageResult() {
    }

    public BizPageResult(com.github.pagehelper.PageInfo page, T data) {
        this.totalPage = page.getPages();
        this.currentPage = page.getPageNum();
        this.showCount = page.getPageSize();
        this.totalResult = (int) page.getTotal();
        this.hasNextPage = page.isHasNextPage();
        this.data = data;
    }

    public boolean getHasNextPage() {
        if (currentPage >= totalPage) {
            return false;
        }
        return true;
    }

    public int getShowCount() {
        return showCount;
    }

    public void setShowCount(int showCount) {
        this.showCount = showCount;
    }

    public int getTotalResult() {
        return totalResult;
    }

    public void setTotalResult(int totalResult) {
        this.totalResult = totalResult;
    }

    public int getTotalPage() {
        return totalPage;
    }

    public void setTotalPage(int totalPage) {
        this.totalPage = totalPage;
    }

    public int getCurrentPage() {
        return currentPage;
    }

    public void setCurrentPage(int currentPage) {
        this.currentPage = currentPage;
    }

    public T getData() {
        return data;
    }

    public void setData(T data) {
        this.data = data;
    }
}
