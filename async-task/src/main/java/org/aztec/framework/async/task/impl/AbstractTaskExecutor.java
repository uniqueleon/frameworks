package org.aztec.framework.async.task.impl;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;

import org.aztec.framework.async.task.AsyncTaskContext;
import org.aztec.framework.async.task.AsyncTaskExecutor;
import org.aztec.framework.async.task.AsyncTaskServerBean;
import org.aztec.framework.async.task.entity.TaskResult;

import com.sjsc.framework.api.restful.SessionKeyConstant;
import com.sjsc.framework.api.restful.entity.async.AsyncTaskDTO;
import com.sjsc.framework.redis.entity.UserSession;
import com.sjsc.framework.redis.util.AuditUserInfo;

@AsyncTaskServerBean
public abstract class AbstractTaskExecutor implements AsyncTaskExecutor {

    protected AsyncTaskDTO taskInfo;
	protected HttpServletRequest request;
	protected HttpServletResponse response;
	protected HttpSession session;
	//@Resource
	//protected ParserRequestContext parse;

	private Long userId;
	private Long warehouseId;
	
	/**
	 * ���ڱ�����̳��Ը���ԭ����Ĭ���ļ���׺��
	 * @return
	 */
	protected String getOutputFileSuffix(){
		return ".xls";
	}
	
	@Override
	public void initContext(AsyncTaskContext context) {
		// TODO Auto-generated method stub
		UserSession.setSesseionLanguage(new UserSession(context.getLang()));
		this.response = context.getHttpResponse();
		this.request = context.getHttpRequest();
		this.session = request.getSession();
		this.userId = context.getUserID();
		this.warehouseId = context.getWarehouseID();
		this.taskInfo = context.getTaskDTO();
		autoLogin();
		((OSSFileReponse)this.response).setOssFileSuffix(getOutputFileSuffix());
	}

	@Override
	public TaskResult doExport() throws Exception {
		
		doRealExport();
		TaskResult result = new TaskResult();
		result.setOssFileUrl(((OSSFileReponse) response)
				.getOSSFileUrl());
		return result;
	}

	@Override
	public void cancel() {
		// TODO Auto-generated method stub

	}

	public abstract void doRealExport() throws Exception;
	
	private void autoLogin(){
	    if(session != null){
	        String userId = session
	                .getAttribute(SessionKeyConstant.ATTRIBUTE_USER_ID) == null ? ""
	                : session
	                        .getAttribute(SessionKeyConstant.ATTRIBUTE_USER_ID)
	                        .toString();
	        String account = session
	                .getAttribute(SessionKeyConstant.ATTRIBUTE_ACCOUNT) == null ? ""
	                : session
	                        .getAttribute(SessionKeyConstant.ATTRIBUTE_ACCOUNT)
	                        .toString();
	        String warehouseIdStr= session.getAttribute(SessionKeyConstant.ATTRIBUTE_WAREHOUSE_ID) == null ? ""
	                : session.getAttribute(SessionKeyConstant.ATTRIBUTE_WAREHOUSE_ID).toString();
	        if(warehouseIdStr != null && !warehouseIdStr.isEmpty()){
	            this.warehouseId = Long.parseLong(warehouseIdStr);
	        }
	        AuditUserInfo.setLoginUser(userId, account, null, isRootUser(account),warehouseId);
	    }
	}

	protected Long getUserId() {
		return userId;
	}

	protected Long getWarehouseId() {
		return warehouseId;
	}

	
	public static boolean isRootUser(String account) {
		//List<String> rootList = AuthDiamondProperty.getInstance().getRootList();
	    /*List<String> rootList = Lists.newArrayList();
		if (StringUtils.isNotBlank(account)
				&& rootList.contains(account.trim())) {
			return true;
		}
		return false;*/
	    return true;
	}

	
}
