package org.aztec.framework.api.rest.entity;

public class Paginator<T> {

    protected Integer pageNo = 0;
    protected Integer totalPage;
    protected Long totalRecord;
    protected Integer pageSize = 10;
    protected T data;

    public Integer getPageNo() {
        return pageNo;
    }

    public void setPageNo(Integer pageNo) {
        this.pageNo = pageNo;
    }

    public Integer getPageSize() {
        return pageSize;
    }

    public void setPageSize(Integer pageSize) {
        this.pageSize = pageSize;
    }

    public Integer getBegin() {
        return pageNo * pageSize;
    }

    public Integer getTotalPage() {
        return totalPage;
    }

    public void setTotalPage(Integer totalPage) {
        this.totalPage = totalPage;
    }

    public Long getTotalRecord() {
        return totalRecord;
    }

    public void setTotalRecord(Long totalRecord) {
        this.totalRecord = totalRecord;
    }

    public T getData() {
        return data;
    }

    public void setData(T data) {
        this.data = data;
    }

    public Paginator(Integer pageNo, Integer pageSize) {
        super();
        this.pageNo = pageNo != null ? pageNo : 0;
        this.pageSize = pageSize != null ? pageSize : 10;
    }

    public Paginator(Integer pageNo, Integer pageSize, Long total) {
        super();
        this.pageNo = pageNo != null ? pageNo : 0;
        this.pageSize = pageSize != null ? pageSize : 10;
        if (total != null) {
            this.totalRecord = total;
            this.totalPage = new Long(totalRecord / pageSize).intValue();
        }
    }

    public Paginator() {
        super();
    }

}
