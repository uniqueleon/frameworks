package org.aztec.framework.async.task;

import java.util.List;

import org.aztec.framework.async.task.entity.AsyncTaskDO;
import org.aztec.framework.async.task.entity.AsyncTaskParam;
import org.aztec.framework.async.task.entity.TaskSubmitResult;

import com.sjsc.framework.api.restful.entity.async.AsyncTaskDetailDTO;
import com.sjsc.framework.api.restful.entity.async.AsyncTaskQO;
import com.sjsc.framework.api.restful.entity.async.AsyncTaskReport;

/**
 * 
 * �첽��������������ӿ�
 * 
 * @author ����
 *
 */
public interface AsyncTaskManager {

	/**
	 * �ж��첽�������Ƿ��
	 * @return
	 */
	public boolean isAsynTaskEnable(String module);
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
	public List<AsyncTaskDO> findTasks(AsyncTaskQO sample);
	/**
	 * ���ݲ�ѯ����������������ͳ����Ϣ�б�
	 * @param sample ��ѯ����
	 * @return �����б�
	 * @throws AsyncTaskException
	 */
	public List<AsyncTaskDO> findSummaryTasks(AsyncTaskQO sample);
	
	/**
	 * ��ȡ����OSS�ļ���ȡ��ַ
	 * @param key ����OSS ���� KEY
	 * @return ��ʱ��Ȩ��������
	 */
	public String getOssFileAccessURL(String key);
	
	
	public AsyncTaskDetailDTO reportSubtask(AsyncTaskReport report) throws AsyncTaskException;
}
