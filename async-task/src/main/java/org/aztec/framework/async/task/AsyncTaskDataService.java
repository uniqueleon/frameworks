package org.aztec.framework.async.task;

import java.util.List;

import org.aztec.framework.async.task.entity.AsyncTaskDO;
import org.aztec.framework.async.task.entity.AsyncTaskParam;
import org.aztec.framework.async.task.entity.TaskSubmitResult;

import com.sjsc.framework.api.restful.entity.async.AsyncTaskDTO;
import com.sjsc.framework.api.restful.entity.async.AsyncTaskQO;

public interface AsyncTaskDataService {


	/**
	 * �ж��첽�������Ƿ��
	 * @return
	 */
	public Boolean isAsynTaskEnable(String modules);
	/**
	 * �ύ�첽����
	 * @param targetObj ԭ��ͬ�������ִ�ж���
	 * @param param �����������
	 * @param isAutoRun �Ƿ��Զ�ִ�к�����������Ϊtrue�������������ݿ���ֵ���Զ���ҳ���ض�������ִ��չʾҳ(ͨ������HttpServletResponse����)�����Զ�ִ�д���@AsynExportTaskע��Ĵ����
	 * @return �����ύ���
	 */
	public TaskSubmitResult submit(AsyncTaskParam param);
	//public WmpExportTaskDO submitTask(ExportTaskParam taskParam) throws DataExportException;
	/**
	 * ���ݲ�ѯ�����������������б�
	 * @param sample ��ѯ����
	 * @return �����б�
	 * @throws AsyncTaskException
	 */
	public List<AsyncTaskDO> findTasks(AsyncTaskQO sample) throws Exception;
	/**
	 * ���ݲ�ѯ������������ͳ������
	 * @param sample ��ѯ����
	 * @return �����б�
	 * @throws AsyncTaskException
	 */
	public List<AsyncTaskDO> getSummaryTasks(AsyncTaskQO sample) throws Exception;
	/**
	 * ��ȡ����OSS�ļ���ȡ��ַ
	 * @param key ����OSS ���� KEY
	 * @return ��ʱ��Ȩ��������
	 */
	public String getOssFileAccessURL(String key);
	
	public boolean notifySuccess(AsyncTaskDTO expTaskDO);
	
	public boolean notifyFail(AsyncTaskDTO expTaskDO,Exception e);
	
	public boolean notifyEternalFail(AsyncTaskDTO expTaskDO,String errMsg);
	
	public boolean notifyTimeout(AsyncTaskDTO expTaskDO);
	
	public boolean notifySend(AsyncTaskDTO expTaskDO);
	
	public boolean notifyExecute(AsyncTaskDTO expTaskDO);
	
	public boolean notifyExecutorNotFound(AsyncTaskDTO expTaskDO);
	
	public boolean notifyUnkownError(AsyncTaskDTO expTaskDO,Exception e);
	
	public boolean revertStatus(AsyncTaskDTO expTaskDO);
	
	public void updateTask(AsyncTaskDTO expTaskDO);
	
}
