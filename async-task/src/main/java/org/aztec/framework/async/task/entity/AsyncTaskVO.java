package org.aztec.framework.async.task.entity;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import org.apache.commons.collections.CollectionUtils;
import org.apache.commons.lang3.StringUtils;

public class AsyncTaskVO {

	private Long id;
	private String status;
	private String statusCode;
	private String seqNo;
	private List<String> ossFileUrls = new ArrayList<>();
	private String name;
	private String warehouseName;
    /**
     * ģ����
     */
    private String module;
	/**
     * �û���
     */
    private String userName;
	
	/**
     * column base_wmp_task.gmt_create  ����ʱ��
     */
    private Date gmtCreate;

    /**
     * column base_wmp_task.gmt_modified  �޸�ʱ��
     */
    private Date gmtModified;

    private String errMsg;
    
    private String fileDesc;

    private String sessionData;
    
    private String remoteIp;
    
    private String userAgent;
    
    private String userComInfo;
    
    private String excParam;
    
    private Long taskCount;
    
    private String mac;

	public String getStatus() {
		return status;
	}

	public void setStatus(String status) {
		this.status = status;
	}

	public String getSeqNo() {
		return seqNo;
	}

	public void setSeqNo(String seqNo) {
		this.seqNo = seqNo;
	}

	public List<String> getOssFileUrls() {
		return ossFileUrls;
	}

	public void setOssFileUrl(String ossFileUrl) {
		if(ossFileUrls == null){
			ossFileUrls = new ArrayList<>();
		}
		if(StringUtils.isNotBlank(ossFileUrl)){
			ossFileUrls.clear();
			CollectionUtils.addAll(ossFileUrls,ossFileUrl.split(","));
		}
	}

	public String getName() {
		return name;
	}

	public void setName(String name) {
		this.name = name;
	}

	public String getWarehouseName() {
		return warehouseName;
	}

	public void setWarehouseName(String warehouseName) {
		this.warehouseName = warehouseName;
	}

	public String getModule() {
		return module;
	}

	public void setModule(String module) {
		this.module = module;
	}

	public String getUserName() {
		return userName;
	}

	public void setUserName(String userName) {
		this.userName = userName;
	}

	public Date getGmtCreate() {
		return gmtCreate;
	}

	public void setGmtCreate(Date gmtCreate) {
		this.gmtCreate = gmtCreate;
	}

	public Date getGmtModified() {
		return gmtModified;
	}

	public void setGmtModified(Date gmtModified) {
		this.gmtModified = gmtModified;
	}

	public String getErrMsg() {
		return errMsg;
	}

	public void setErrMsg(String errMsg) {
		this.errMsg = errMsg;
	}

	public Long getId() {
		return id;
	}

	public void setId(Long id) {
		this.id = id;
	}

	public String getFileDesc() {
		return fileDesc;
	}

	public void setFileDesc(String fileDesc) {
		this.fileDesc = fileDesc;
	}

	public void setOssFileUrls(List<String> ossFileUrls) {
		this.ossFileUrls = ossFileUrls;
	}

	public String getStatusCode() {
		return statusCode;
	}

	public void setStatusCode(String statusCode) {
		this.statusCode = statusCode;
	}

	public String getSessionData() {
		return sessionData;
	}

	public void setSessionData(String sessionData) {
		this.sessionData = sessionData;
	}

	public String getRemoteIp() {
		return remoteIp;
	}

	public void setRemoteIp(String remoteIp) {
		this.remoteIp = remoteIp;
	}

	public String getUserAgent() {
		return userAgent;
	}

	public void setUserAgent(String userAgent) {
		this.userAgent = userAgent;
	}

	public String getUserComInfo() {
		return userComInfo;
	}

	public void setUserComInfo(String userComInfo) {
		this.userComInfo = userComInfo;
	}

	public String getExcParam() {
		return excParam;
	}

	public void setExcParam(String excParam) {
		this.excParam = excParam;
	}

	public Long getTaskCount() {
		return taskCount;
	}

	public void setTaskCount(Long taskCount) {
		this.taskCount = taskCount;
	}

	public String getMac() {
		return mac;
	}

	public void setMac(String mac) {
		this.mac = mac;
	}
	
}
