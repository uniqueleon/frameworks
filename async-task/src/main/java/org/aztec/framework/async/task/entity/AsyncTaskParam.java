package org.aztec.framework.async.task.entity;

import java.io.Serializable;
import java.io.UnsupportedEncodingException;

import org.aztec.framework.async.task.AsyncTaskExecutor;
import org.aztec.framework.disconf.items.AsyncTaskConfigure;

import com.sjsc.framework.redis.entity.UserSession;

public class AsyncTaskParam implements Serializable{

	/**
	 * 
	 */
	private static final long serialVersionUID = 3698449090007095788L;
	public static final int DEFAULT_PAGE_NUM = 5;
	private String executor;
	private String sessionData;
	private String lang;
	private String name;
	private String fileSuffix;
	private String fileDesc;
	private String remoteIp;
	private String userAgent;
	private String userComInfo; 
	private Long warehouseId;
    
    private Long userId;
    
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
    
    private Integer taskType;
    /**
     * ִ�в���
     */
    private String excParam;
    /**
     * ģ����
     */
    private String module;
    
    private String invokeModule;
    
    private String description;
	private Boolean preSubmit = false;
	
	
	public String getExecutor() {
		return executor;
	}
	public void setExecutor(String executor) {
		this.executor = executor;
	}
	public String getLang() {
		return lang;
	}
	public void setLang(String lang) {
		this.lang = lang;
	}
	public String getName() {
		return name;
	}
	public void setName(String name) {
		this.name = name;
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
	
	public AsyncTaskParam() {
		super();
	}
	
    public AsyncTaskParam(AsyncTaskConfigure config,
            Class<? extends  AsyncTaskExecutor> executorCls){

        this.executor = executorCls.getName();
        //this.moduleName = TASK_MODULES.getModuleByDubboAppName(ConfigUtils.getProperty("dubbo.application.name")).getName();
        this.lang = UserSession.getSesseionLanguage().getSessionLg();
    }
    
	
	protected AsyncTaskParam(String executor, 
			String moduleName, String lang) {
		super();
	}
	
	
	public String toUrlQueryString() throws UnsupportedEncodingException {
		String queryStr =  "params=" + excParam + "&executor=" + executor
				+ "&moduleName=" + module + "&warehouseID=" + warehouseId
				+ "&userID=" + userId + "&lang=" + lang 
				+ (fileSuffix != null ? "&fileSuffix=" + fileSuffix : "")
				+ (fileDesc != null ? "&fileDesc=" + fileDesc : "");
		return queryStr;
	}
	public Boolean getPreSubmit() {
		return preSubmit;
	}
	public void setPreSubmit(Boolean preSubmit) {
		this.preSubmit = preSubmit;
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
    public String getOssFileUrl() {
        return ossFileUrl;
    }
    public void setOssFileUrl(String ossFileUrl) {
        this.ossFileUrl = ossFileUrl;
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
    public String getExcParam() {
        return excParam;
    }
    public void setExcParam(String excParam) {
        this.excParam = excParam;
    }
    public String getModule() {
        return module;
    }
    public void setModule(String module) {
        this.module = module;
    }
    public String getDescription() {
        return description;
    }
    public void setDescription(String description) {
        this.description = description;
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
