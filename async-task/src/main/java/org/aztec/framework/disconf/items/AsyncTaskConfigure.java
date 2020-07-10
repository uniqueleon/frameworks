package org.aztec.framework.disconf.items;

import java.util.Calendar;
import java.util.Date;
import java.util.HashMap;
import java.util.Map;

import org.apache.commons.lang3.StringUtils;
import org.aztec.framework.async.task.AsyncTaskServerBean;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.context.annotation.Scope;
import org.springframework.stereotype.Service;

import com.alibaba.fastjson.JSON;
import com.baidu.disconf.client.common.annotations.DisconfFile;
import com.baidu.disconf.client.common.annotations.DisconfFileItem;

/**
 * ����������������
 * 
 * @author liming
 * 
 */

@AsyncTaskServerBean
@Service
@Scope("singleton")
@DisconfFile(filename = "async_task.properties")
public class AsyncTaskConfigure {


	/**
	 * �Ƿ������첽���ݵ�������
	 */
	private String enabled = "{\"all\":true}";
	
	/**
	 * �Ƿ�ִ�е�������
	 */
	private boolean executable = false;
	/**
	 * ��ʱ�ļ�����С
	 */
	private String tempFileMaxSize = "8M";
	/**
	 * ��ʱ�ļ�д�����С
	 */
	private String tempFileBufferSize = "128K";
	/**
	 * ��̨������󵼳������߳���
	 */
	private Integer maxTaskThreadNum;
	/**
	 * ����ʱʱ��
	 */
	private Integer taskTimeout;
	/**
	 * ��ͬ����ִ�м��ʱ��
	 */
	private Long duplicateTaskInterval = 30 * 60 * 1000l;

	/**
	 * ������oss bucket ����
	 */
	private String ossBucketName;
	private String ossFilePath;
	private String ossUrlBase;
	private String ossAccessID;
	private String ossAccessKey;
	private String ossEndPoint;
	private String ossUrlTtl;
	private int ossUploadBufferSize = 1024;
	private String baseRedirectUrl;

	//private String ossLogFilePath = "/data/export/";

	/**
	 * �û����յ�������������
	 */
	private String userTaskLimit;
	/**
	 * �ֿⵥ�յ�������������
	 */
	private String warehouseTaskLimit;
	/**
	 * ģ�鵥�յ�������������
	 */
	private String moduleTaskLimit;
	/**
	 * ��ҳ��󵼳���
	 */
	private String maxPageNum = "60000";
	
	private String maxDataSize = "100000";
	
	private String requestParamLimitEnabled = "false";
	
	private String sessionDataLimitEnabled = "false";
	
	private String requestParamLimit = "65535";
	
	private String sessionDataLimit = "4096";
	
	private String tmpDownloadDir = "/data/tmp/download";
	
	private static final Object lockObj = new Object();
	
	private Map<String,Object> enabledFlags = new HashMap<>();
	
	public final static String DATAID = "com.taobao.basic.export.task";
	private final static String DIAMOND_GROUP_ID = "DEFAULT_GROUP";
	private final static Logger Logger = LoggerFactory.getLogger(AsyncTaskConfigure.class);
	
	public AsyncTaskConfigure() {
		
	}


	public boolean isEnabled(String module) {
		if (module == null)
			return false;
		Map<String,Object> enabledFlags = JSON.parseObject(getEnabled(),Map.class);
		if((enabledFlags.get("all") != null
				&& new Boolean(true).equals(enabledFlags.get("all")))
				|| (enabledFlags.get(module) != null
						&& new Boolean(true).equals(module))){
			return true;
		}
		return false;
	}
	
	@DisconfFileItem(name="enabled",associateField="enabled")
	public String getEnabled() {
        return enabled;
    }


    @SuppressWarnings("unchecked")
	public void setEnabled(String enabled) {
		if(StringUtils.isNotBlank(enabled)){
			enabledFlags = JSON.parseObject(enabled,Map.class);
		}
		else{
			enabledFlags = JSON.parseObject(this.enabled,Map.class);
		}
	}
	
	@DisconfFileItem(name="ossBucketName",associateField="ossBucketName")
	public String getOssBucketName() {
		return ossBucketName;
	}

	public void setOssBucketName(String ossBucketName) {
		this.ossBucketName = ossBucketName;
	}

    @DisconfFileItem(name="maxTaskThreadNum",associateField="maxTaskThreadNum")
	public Integer getMaxTaskThreadNum() {
		return maxTaskThreadNum;
	}

	public void setMaxTaskThreadNum(Integer maxTaskThreadNum) {
		this.maxTaskThreadNum = maxTaskThreadNum;
	}

	public void setMaxTaskThreadNum(String maxTaskThreadNum) {
		this.maxTaskThreadNum = Integer.parseInt(maxTaskThreadNum);
	}

    @DisconfFileItem(name="taskTimeout",associateField="taskTimeout")
	public Integer getTaskTimeout() {
		return taskTimeout;
	}

	public void setTaskTimeout(Integer taskTimeout) {
		this.taskTimeout = taskTimeout;
	}

    @DisconfFileItem(name="ossFilePath",associateField="ossFilePath")
	public String getOssFilePath() {
		return ossFilePath;
	}

	public void setOssFilePath(String ossFilePath) {
		this.ossFilePath = ossFilePath;
	}

    @DisconfFileItem(name="ossUrlBase",associateField="ossUrlBase")
	public String getOssUrlBase() {
		return ossUrlBase;
	}

	public void setOssUrlBase(String ossUrlBase) {
		this.ossUrlBase = ossUrlBase;
	}

    @DisconfFileItem(name="ossAccessID",associateField="ossAccessID")
	public String getOssAccessID() {
		return ossAccessID;
	}

	public void setOssAccessID(String ossAccessID) {
		this.ossAccessID = ossAccessID;
	}

    @DisconfFileItem(name="ossAccessKey",associateField="ossAccessKey")
	public String getOssAccessKey() {
		return ossAccessKey;
	}

	public void setOssAccessKey(String ossAccessKey) {
		this.ossAccessKey = ossAccessKey;
	}

    @DisconfFileItem(name="ossEndPoint",associateField="ossEndPoint")
	public String getOssEndPoint() {
		return ossEndPoint;
	}

	public void setOssEndPoint(String ossEndPoint) {
		this.ossEndPoint = ossEndPoint;
	}

    @DisconfFileItem(name="ossUploadBufferSize",associateField="ossUploadBufferSize")
	public int getOssUploadBufferSize() {
		return ossUploadBufferSize;
	}

	public void setOssUploadBufferSize(int ossUploadBufferSize) {
		this.ossUploadBufferSize = ossUploadBufferSize;
	}

	public void setOssUploadBufferSize(String ossUploadBufferSize) {
		this.ossUploadBufferSize = Integer.parseInt(ossUploadBufferSize);
	}

    @DisconfFileItem(name="duplicateTaskInterval",associateField="duplicateTaskInterval")
	public Long getDuplicateTaskInterval() {
		return duplicateTaskInterval;
	}

	public void setDuplicateTaskInterval(Long duplicateTaskInterval) {
		this.duplicateTaskInterval = duplicateTaskInterval;
	}

    @DisconfFileItem(name="baseRedirectUrl",associateField="baseRedirectUrl")
	public String getBaseRedirectUrl() {
		return baseRedirectUrl;
	}

	public void setBaseRedirectUrl(String baseRedirectUrl) {
		this.baseRedirectUrl = baseRedirectUrl;
	}

	public Date getUrlExpiredDate() {
		Integer amount = Integer.parseInt(getOssUrlTtl().substring(0,
		        getOssUrlTtl().length() - 1));
		Date nowDate = new Date();
		Calendar calendar = Calendar.getInstance();
		calendar.setTime(nowDate);
		String timeUnit = ossUrlTtl.substring(ossUrlTtl.length() - 1,
				ossUrlTtl.length());
		switch (timeUnit) {
		case "d":
			calendar.add(Calendar.DATE, amount);
			break;
		case "h":
			calendar.add(Calendar.HOUR, amount);
			break;
		case "m":
			calendar.add(Calendar.MINUTE, amount);
			break;
		case "s":
			calendar.add(Calendar.SECOND, amount);
			break;
		case "M":
			calendar.add(Calendar.MONTH, amount);
			break;
		}
		return calendar.getTime();
	}


    @DisconfFileItem(name="ossUrlTtl",associateField="ossUrlTtl")
	public String getOssUrlTtl() {
		return ossUrlTtl;
	}

	public void setOssUrlTtl(String ossUrlTtl) {
		this.ossUrlTtl = ossUrlTtl;
	}

    @DisconfFileItem(name="userTaskLimit",associateField="userTaskLimit")
	public String getUserTaskLimit() {
		return userTaskLimit;
	}

	public void setUserTaskLimit(String userTaskLimit) {
		this.userTaskLimit = userTaskLimit;
	}

    @DisconfFileItem(name="warehouseTaskLimit",associateField="warehouseTaskLimit")
	public String getWarehouseTaskLimit() {
		return warehouseTaskLimit;
	}

	public void setWarehouseTaskLimit(String warehouseTaskLimit) {
		this.warehouseTaskLimit = warehouseTaskLimit;
	}

    @DisconfFileItem(name="moduleTaskLimit",associateField="moduleTaskLimit")
	public String getModuleTaskLimit() {
		return moduleTaskLimit;
	}

	public void setModuleTaskLimit(String moduleTaskLimit) {
		this.moduleTaskLimit = moduleTaskLimit;
	}


    @DisconfFileItem(name="executable",associateField="executable")
	public boolean isExecutable() {
		return executable;
	}

	public void setExecutable(boolean executable) {
		this.executable = executable;
	}
	
	public void setExecutable(String executable) {
		this.executable = Boolean.parseBoolean(executable);
	}

	
	public static Long toLong(String dataStr){
	    String tempFileMaxSize = dataStr;
		int length = tempFileMaxSize.length();
		Integer amount = Integer.parseInt(tempFileMaxSize.substring(0,
				length - 1));
		String unit = tempFileMaxSize.substring(length - 1,length);
		switch(unit){
		case "M":
			return amount * 1024 * 1024l;
		case "K":
			return amount * 1024l;
		case "b":
			return amount * 1l;
		default :
			return amount * 1l;
		}
	}

    @DisconfFileItem(name="tempFileBufferSize",associateField="tempFileBufferSize")
	public String getTempFileBufferSize() {
		return tempFileBufferSize;
	}

	public void setTempFileBufferSize(String tempFileBufferSize) {
		this.tempFileBufferSize = tempFileBufferSize;
	}
	
	@DisconfFileItem(name="maxPageNum",associateField="maxPageNum")
	public String getMaxPageNum() {
		return maxPageNum;
	}


    @DisconfFileItem(name="tempFileMaxSize",associateField="tempFileMaxSize")
	public String getTempFileMaxSize() {
		return tempFileMaxSize;
	}

	public void setTempFileMaxSize(String tempFileMaxSize) {
		this.tempFileMaxSize = tempFileMaxSize;
	}
	public void setMaxPageNum(String maxPageNum) {
		this.maxPageNum = maxPageNum;
	}


    @DisconfFileItem(name="maxDataSize",associateField="maxDataSize")
	public String getMaxDataSize() {
		return maxDataSize;
	}

	public void setMaxDataSize(String maxDataSize) {
		this.maxDataSize = maxDataSize;
	}

    @DisconfFileItem(name="requestParamLimitEnabled",associateField="requestParamLimitEnabled")
	public String getRequestParamLimitEnabled() {
		return requestParamLimitEnabled;
	}

	public void setRequestParamLimitEnabled(String requestParamLimitEnabled) {
		this.requestParamLimitEnabled = requestParamLimitEnabled;
	}

    @DisconfFileItem(name="sessionDataLimitEnabled",associateField="sessionDataLimitEnabled")
	public String getSessionDataLimitEnabled() {
		return sessionDataLimitEnabled;
	}

	public void setSessionDataLimitEnabled(String sessionDataLimitEnabled) {
		this.sessionDataLimitEnabled = sessionDataLimitEnabled;
	}

    @DisconfFileItem(name="requestParamLimit",associateField="requestParamLimit")
	public String getRequestParamLimit() {
		return requestParamLimit;
	}

	public void setRequestParamLimit(String requestParamLimit) {
		this.requestParamLimit = requestParamLimit;
	}

    @DisconfFileItem(name="sessionDataLimit",associateField="sessionDataLimit")
	public String getSessionDataLimit() {
		return sessionDataLimit;
	}

	public void setSessionDataLimit(String sessionDataLimit) {
		this.sessionDataLimit = sessionDataLimit;
	}

    @DisconfFileItem(name="tmpDownloadDir",associateField="tmpDownloadDir")
    public String getTmpDownloadDir() {
        return tmpDownloadDir;
    }
	
}
