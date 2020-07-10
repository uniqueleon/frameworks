package org.aztec.framework.async.task;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import com.sjsc.framework.api.restful.entity.async.AsyncTaskDTO;

public interface AsyncTaskContext{

	public String getExecutorName();
	public String getLang();
	public AsyncTaskDTO getTaskDTO();
	public <T> T getExportParam(Class<T> paramCls);
	public Long getUserID();
	public Long getWarehouseID();
	public Long getTaskID();
	public HttpServletResponse getHttpResponse();
	public String getResultFileUrl();
	public HttpServletRequest getHttpRequest();
	public void init();
}
