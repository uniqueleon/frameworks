package org.aztec.framework.api.rest.entity;

public class RestRequest<T> {

    public enum OperationType {
        LOGIN, QUERY, INSERT, UPDATE, DELETE;
    }

    /**
     * 签名，根据请求数据和加密方式计算的签名值
     */
    private String sign;
    /**
     * 业务ID
     */
    private String id;
    /**
     * 请求的业务方
     */
    private String clientId;
    /**
     * 加密方式 MD5
     */
    private String encrypt;
    private Integer pageNo;

    private Integer pageSize;
    private T param;
    private String optType;

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

    public T getParam() {
        return param;
    }

    public void setParam(T param) {
        this.param = param;
    }

    public String getOptType() {
        return optType;
    }

    public void setOptType(String optType) {
        this.optType = optType;
    }

}
