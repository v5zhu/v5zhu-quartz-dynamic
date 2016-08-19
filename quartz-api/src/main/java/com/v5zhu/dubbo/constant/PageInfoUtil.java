package com.v5zhu.dubbo.constant;

import java.io.Serializable;
import java.util.List;

/**
 * Created by zhuxl on 2015/6/2.
 */
public class PageInfoUtil<T> implements Serializable {
    private int pageNum;//当前页
    private int pageSize;//每页个数
    private int size;//当前页个数
    private long total;//总共个数
    private int pages;//总共页数
    private boolean hasPreviousPage;//是否有前一页
    private boolean hasNextPage;//是否有后一页
    private List<T> list;

    public PageInfoUtil() {
    }


    public List<T> getList() {
        return list;
    }

    public void setList(List<T> list) {
        this.list = list;
    }

    public int getPageNum() {
        return pageNum;
    }

    public void setPageNum(int pageNum) {
        this.pageNum = pageNum;
    }

    public boolean isHasNextPage() {
        return hasNextPage;
    }

    public void setHasNextPage(boolean hasNextPage) {
        this.hasNextPage = hasNextPage;
    }

    public boolean isHasPreviousPage() {
        return hasPreviousPage;
    }

    public void setHasPreviousPage(boolean hasPreviousPage) {
        this.hasPreviousPage = hasPreviousPage;
    }

    public int getPages() {
        return pages;
    }

    public void setPages(int pages) {
        this.pages = pages;
    }

    public long getTotal() {
        return total;
    }

    public void setTotal(long total) {
        this.total = total;
    }

    public int getSize() {
        return size;
    }

    public void setSize(int size) {
        this.size = size;
    }

    public int getPageSize() {
        return pageSize;
    }

    public void setPageSize(int pageSize) {
        this.pageSize = pageSize;
    }
}
