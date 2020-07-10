package org.aztec.framework.async.task.entity;

import java.util.Date;

import javax.persistence.Id;
import javax.persistence.Table;

@Table(name="async_task")
public class AsyncTaskDO{

    /**
	 * 
	 */
	private static final long serialVersionUID = 6980958977080251408L;

	/**
     * column base_wmp_task.id  ����
     */
	@Id
    private Long id;

    /**
     * column base_wmp_task.gmt_create  ����ʱ��
     */
    private Date createTime;

    /**
     * column base_wmp_task.gmt_modified  �޸�ʱ��
     */
    private Date updateTime;
    
    private Long warehouseId;
    
    private Long userId;
    
    private String name;
    
    private String seqNo;
    
    private Integer status;
    
    private String errMsg;
    /**
     * ʣ�����Դ���
     */
    private Integer retryTime;
    /**
     * oss �ϵ��ļ���ȡurl
     */
    private String ossFileUrl;
    /**
     * ִ��������
     */
    private String executor;
    /**
     * ִ�в���
     */
    private String excParam;
    /**
     * ��������
     */
    private String lang;
    /**
     * ģ����
     */
    private String module;
    
    /**
     * 执行调用方模块
     */
    private String invokeModule;
    /**
     * �ļ���׺��
     */
    private String fileSuffix;
    /**
     * �ļ�����
     */
    private String fileDesc;
    /**
     * �Ự���ݣ��������Ե����������õģ�
     */
    private String sessionData;
    
    private String remoteIp;
    
    private String userAgent;
    
    private String userComInfo;
    
    private Long subTaskNum;
    
    private Integer taskType;
    

	public Long getId() {
		return id;
	}

	public void setId(Long id) {
		this.id = id;
	}

	

	public Date getCreateTime() {
        return createTime;
    }

    public void setCreateTime(Date createTime) {
        this.createTime = createTime;
    }

    public Date getUpdateTime() {
        return updateTime;
    }

    public void setUpdateTime(Date updateTime) {
        this.updateTime = updateTime;
    }

    public Long getWarehouseId() {
		return warehouseId;
	}

	public void setWarehouseId(Long warehouseId) {
		this.warehouseId = warehouseId;
	}

	public Long getUserId() {
		return userId;
	}

	public void setUserId(Long userId) {
		this.userId = userId;
	}

	public String getName() {
		return name;
	}

	public void setName(String name) {
		this.name = name;
	}

	public Integer getStatus() {
		return status;
	}

	public void setStatus(Integer status) {
		this.status = status;
	}

	public String getErrMsg() {
		return errMsg;
	}

	public void setErrMsg(String errMsg) {
		this.errMsg = errMsg;
	}

	public Integer getRetryTime() {
		return retryTime;
	}

	public void setRetryTime(Integer retryTime) {
		this.retryTime = retryTime;
	}

	public String getOssFileUrl() {
		return ossFileUrl;
	}

	public void setOssFileUrl(String ossFileUrl) {
		this.ossFileUrl = ossFileUrl;
	}

	public String getExecutor() {
		return executor;
	}

	public void setExecutor(String executor) {
		this.executor = executor;
	}

	public String getExcParam() {
		return excParam;
	}

	public void setExcParam(String excParam) {
		this.excParam = excParam;
	}

	public String getLang() {
		return lang;
	}

	public void setLang(String lang) {
		this.lang = lang;
	}

	public String getModule() {
		return module;
	}

	public void setModule(String module) {
		this.module = module;
	}

	public String getSeqNo() {
		return seqNo;
	}

	public void setSeqNo(String seqNo) {
		this.seqNo = seqNo;
	}


	public String getFileSuffix() {
		return fileSuffix;
	}

	public void setFileSuffix(String fileSuffix) {
		this.fileSuffix = fileSuffix;
	}

	public String getFileDesc() {
		return fileDesc;
	}

	public void setFileDesc(String fileDesc) {
		this.fileDesc = fileDesc;
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

    public Long getSubTaskNum() {
        return subTaskNum;
    }

    public void setSubTaskNum(Long subTaskNum) {
        this.subTaskNum = subTaskNum;
    }

    public Integer getTaskType() {
        return taskType;
    }

    public void setTaskType(Integer taskType) {
        this.taskType = taskType;
    }

    public String getInvokeModule() {
        return invokeModule;
    }

    public void setInvokeModule(String invokeModule) {
        this.invokeModule = invokeModule;
    }
	
	
}
