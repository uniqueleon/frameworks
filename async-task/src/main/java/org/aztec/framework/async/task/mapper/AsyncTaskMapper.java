package org.aztec.framework.async.task.mapper;

import java.util.List;
import java.util.Map;

import org.apache.ibatis.annotations.Param;
import org.aztec.framework.async.task.entity.AsyncTaskDO;

import com.sjsc.framework.api.restful.entity.async.AsyncTaskQO;
import com.sjsc.framework.mybatis.conf.dao.ibatis.mapper.MyMapper;

public interface AsyncTaskMapper<T extends AsyncTaskDO> extends MyMapper<T>{
	
	public List<AsyncTaskDO> findBySample(@Param("task")AsyncTaskQO taskDO);
	//public List<AsyncTaskDO> findSummaryBySample(@Param("task")AsyncTaskQO taskDO);
	public AsyncTaskDO findById(@Param("id")Long id);
	public List<AsyncTaskDO> findExecutableTask();
	public List<AsyncTaskDO> findExpiredTask();
	public AsyncTaskDO findTaskByStatus(@Param("id")Long id,@Param("status")Integer status);
	//public int insert(@Param("task")AsyncTaskDO taskDO);
	public Integer updateStatus(Map<String,Object> params);
	public Integer countAllSumitedTask(@Param("task")AsyncTaskQO taskQO);
	//public Integer updateExpiredTaskAsFail(@Param("dataList")List<AsyncTaskDO> taskDOs);
}
