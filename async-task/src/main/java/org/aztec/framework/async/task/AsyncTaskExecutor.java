package org.aztec.framework.async.task;

import org.aztec.framework.async.task.entity.TaskResult;

public interface AsyncTaskExecutor {

	//public boolean isExecutable(DataExportContext context) throws Exception;
	public void initContext(AsyncTaskContext context);
	public TaskResult doExport() throws Exception;
	public void cancel();
	public Integer count();
	//public DataExportExecutor cloneOne();
}
