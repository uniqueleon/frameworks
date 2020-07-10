package org.aztec.framework.async.task.impl;

import java.util.Date;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.Future;
import java.util.concurrent.ThreadFactory;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.TimeoutException;

import javax.annotation.Resource;

import org.aztec.framework.async.task.AsyncTaskConst;
import org.aztec.framework.async.task.AsyncTaskContext;
import org.aztec.framework.async.task.AsyncTaskDataService;
import org.aztec.framework.async.task.AsyncTaskExecutor;
import org.aztec.framework.async.task.AsyncTaskServerBean;
import org.aztec.framework.async.task.entity.TaskResult;
import org.aztec.framework.disconf.items.AsyncTaskConfigure;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.BeansException;
import org.springframework.beans.factory.BeanFactory;
import org.springframework.beans.factory.BeanFactoryAware;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import com.sjsc.framework.api.restful.entity.async.AsyncTaskDTO;

@AsyncTaskServerBean
@SuppressWarnings("rawtypes")
@Component
public class TaskDispatcher implements BeanFactoryAware{

	private static final Logger log = LoggerFactory.getLogger(AsyncTaskConst.TASK_LOG_NAME);
	
	@Resource
	private AsyncTaskDataService notifier;
	
	private BeanFactory factory;

	private static ExecutorService execService;
	private static ExecutorService checkerPool;
	private static final String WMP_EXPORT_TASK_THREAD_GROUP_NAME = "WMP_EXPORT_TASK_TRHEADS-";
	private static final String WMP_EXPORT_TASK_CHECKER_THREAD_GROUP_NAME = "WMP_EXPORT_TASK_CHECKER-";
	private final static Object lockObj = new Object();
	@Autowired
	AsyncTaskConfigure config;
	@Autowired
	AsyncTaskExecutor executor;

	@Override
	public void setBeanFactory(BeanFactory arg0)
			throws BeansException {
		this.factory = arg0;
	}
	
	public void dispatchTask(AsyncTaskDTO taskDto) {
		boolean result = notifier.notifyExecute(taskDto);
	    if (result == true) {
			try {
				initThreadPool();
				if (executor != null) {
					AsyncTaskContext context = TaskContextBuilder
							.build(taskDto);
					AsyncTaskSubmitter taskExecutor = factory.getBean(AsyncTaskSubmitter.class,
					        new Object[]{ context, taskDto});

                    String logMsg = "submit async task of [" + taskDto.getSeqNo() + "] by thread hashcode[" + 
                            Thread.currentThread().getName() + ":" + Thread.currentThread().hashCode() + "]!";
                    log.info(logMsg);
					Future<TaskResult> future = execService
							.submit(taskExecutor);
                    logMsg = "submit async task of [" + taskDto.getSeqNo() + "] by thread hashcode[" + 
                            Thread.currentThread().getName() + ":" + Thread.currentThread().hashCode() + "] finished!";
					log.info(logMsg);
					checkerPool.execute(new ResultChecker(future,
							taskExecutor, taskDto));
				} else {
					notifier.notifyExecutorNotFound(taskDto);
				}
			} catch (Exception e) {
				log.error(e.getMessage(), e);
				notifier.notifyUnkownError(taskDto, e);
			}
		}
	}
	
	private void initThreadPool() throws InterruptedException {
		if(execService == null || checkerPool == null){
			synchronized (lockObj) {
				if (execService == null) {
					execService = createThreadPool(1,WMP_EXPORT_TASK_THREAD_GROUP_NAME);
				}
				if (checkerPool == null) {
					checkerPool = createThreadPool(2,WMP_EXPORT_TASK_CHECKER_THREAD_GROUP_NAME);
				}
			}
		}
	}
	
	private ExecutorService createThreadPool(int poolType,
			final String groupName) {
		ThreadFactory factory = new ThreadFactory() {

			@Override
			public Thread newThread(Runnable r) {
				Thread newThread = new Thread(r,
						groupName + r.hashCode());
				newThread.setDaemon(true);
				return newThread;
			}
		};
		int defaultThreadNum = config.getMaxTaskThreadNum() == null ? 10 : config.getMaxTaskThreadNum();
		switch (poolType) {
		case 1:
			return Executors.newFixedThreadPool(defaultThreadNum ,factory);
		case 2:
			return Executors.newCachedThreadPool(factory);
		default:
			return Executors.newFixedThreadPool(defaultThreadNum,factory);
		}

	}

	private class ResultChecker implements Runnable {

		private Future submitedTask;
		private AsyncTaskSubmitter taskExecutor;
		private AsyncTaskDTO taskInfo;

		public ResultChecker(Future submitedTask,
				AsyncTaskSubmitter taskExecutor, AsyncTaskDTO taskInfo) {
			super();
			this.submitedTask = submitedTask;
			this.taskExecutor = taskExecutor;
			this.taskInfo = taskInfo;
		}

		@Override
		public void run() {
			try {
				submitedTask.get(config.getTaskTimeout(), TimeUnit.SECONDS);
			} catch (InterruptedException e) {
				log.error(e.getMessage(),e);
				notifier.notifyFail(taskInfo, e);
			} catch (ExecutionException e) {
				log.error(e.getMessage(),e);
				notifier.notifyFail(taskInfo, e);
			} catch (TimeoutException e) {
				submitedTask.cancel(true);
				taskExecutor.cancel();
				notifier.notifyTimeout(taskInfo);
			}
		}

	}

}
