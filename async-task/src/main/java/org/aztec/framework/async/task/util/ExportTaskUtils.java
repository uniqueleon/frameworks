package org.aztec.framework.async.task.util;

import java.util.List;

import javax.annotation.Resource;

import org.aztec.framework.async.task.AsyncTaskDataService;
import org.aztec.framework.async.task.AsyncTaskServerBean;
import org.aztec.framework.async.task.entity.AsyncTaskDO;
import org.aztec.framework.async.task.entity.AsyncTaskParam;
import org.aztec.framework.async.task.entity.TaskSubmitResult;
import org.aztec.framework.disconf.items.AsyncTaskConfigure;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import com.sjsc.framework.api.restful.entity.async.AsyncTaskQO;

@AsyncTaskServerBean
@Component("exportTaskUtils")
public class ExportTaskUtils {

	@Resource
	AsyncTaskDataService dubboService;
	@Autowired
	AsyncTaskConfigure config;
	
	private static final int DEFAULT_TASK_PARAM_TTL = 60;

	public TaskSubmitResult submitTask(
			AsyncTaskParam taskParam) {
		//taskParam.init(config,request);
		TaskSubmitResult result = null;
		if (isAsynTaskEnable(taskParam.getModule())) {
			try {
				result = dubboService.submit(taskParam);
				
				return result;
			} catch (Exception e) {
				return new TaskSubmitResult(config,
						TaskSubmitResult.StatusCode.AUTO_HANDLE_ASYNC_ERROR,
						taskParam);
			}
		} else {
			return null;
		}
	}

//	private void addSession(HttpServletRequest request, ExportTaskParam param) {
//		RedisManage manager = RedisManage.getInstance();
//		String sessionID = request.getSession().getId();
//		manager.set(ExportTaskConst.EXPORT_TASK_PARAM_REDIS_KEY_PREFIX + sessionID,
//				param, DEFAULT_TASK_PARAM_TTL);
//		/*request.getSession().setAttribute(
//				ExportTaskConst.TASK_PARAM_COOKIE_KEY, param);*/
//	}

	public String getOssFileAccessURL(String key) {
		return dubboService.getOssFileAccessURL(key);
	}

	public List<AsyncTaskDO> findTasks(AsyncTaskQO sample)
			throws Exception {
		return dubboService.findTasks(sample);
	}
	
	public List<AsyncTaskDO> getSummaryTaskInfo(AsyncTaskQO query) throws Exception{
		return dubboService.getSummaryTasks(query);
	}
	
	public boolean isAsynTaskEnable(String module) {
		// TODO Auto-generated method stub
		return config.isEnabled(module);
	}
}
