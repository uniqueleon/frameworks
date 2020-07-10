package org.aztec.framework.async.task.controller;

import java.util.List;
import java.util.concurrent.Executor;
import java.util.concurrent.Executors;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.MediaType;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RestController;

import com.sjsc.framework.api.job.AsyncSubTask;
import com.sjsc.framework.api.job.TaskInfoListener;
import com.sjsc.framework.api.restful.entity.RestRequest;
import com.sjsc.framework.api.restful.entity.RestResult;
import com.sjsc.framework.api.restful.entity.async.AsyncTaskDTO;
import com.sjsc.framework.api.restful.entity.async.AsyncTaskReport;
import com.sjsc.framework.api.restful.entity.async.SubTaskExecParam;
import com.sjsc.framework.api.restful.feign.AsyncTaskInvokeService;
import com.sjsc.framework.core.FrameworkLogger;
import com.sjsc.framework.core.SpringApplicationContext;
import com.sjsc.framework.heartbeat.TaskInfoRecorder;
import com.sjsc.framework.heartbeat.impl.HeartbeatChecker;


@RestController
public class SubTaskController implements AsyncTaskInvokeService{

    
    private static final Executor executor = Executors.newFixedThreadPool(10);
    
    @Autowired
    private HeartbeatChecker checker;
    
    @Autowired(required=false)
    private List<TaskInfoListener> reporters;

    @RequestMapping(path = "/subtask/invoke", method = { RequestMethod.POST }, consumes = {
            MediaType.APPLICATION_JSON_UTF8_VALUE }, produces = { MediaType.APPLICATION_JSON_UTF8_VALUE })
    @Override
    public RestResult<Boolean> invoke(@RequestBody RestRequest<SubTaskExecParam> taskDTO) {
        // TODO Auto-generated method stub
        SubTaskExecParam excParam = taskDTO.getParam();
        executor.execute(new AsyncSubTaskThread(excParam));
        return new RestResult<>(true, "OK", "OK",true);
    }
    
    @RequestMapping(path = "/subtask/receive", method = { RequestMethod.POST }, consumes = {
            MediaType.APPLICATION_JSON_UTF8_VALUE }, produces = { MediaType.APPLICATION_JSON_UTF8_VALUE })
    @Override
    public RestResult<Boolean> reveiveTaskReport(@RequestBody RestRequest<AsyncTaskDTO> reportDTO){
        if(reporters != null ){
            for(TaskInfoListener reporter : reporters){
                reporter.listen(reportDTO.getParam());
            }
        }
        return RestResult.successResult(true);
    }

    public class AsyncSubTaskThread implements Runnable{
        
        SubTaskExecParam excParam;

        public AsyncSubTaskThread(SubTaskExecParam excParam) {
            super();
            this.excParam = excParam;
        }

        @Override
        public void run() {
            try {
                TaskInfoRecorder.begin();
                checker.reportStatus();
                if(excParam != null){
                    
                    AsyncSubTask subTask = SpringApplicationContext.getBean(excParam.getExecutor());
                    if(subTask != null){
                        subTask.execute(excParam);
                    }
                }
                TaskInfoRecorder.end();
                checker.reportStatus();
            } catch (Throwable e) {
                FrameworkLogger.error(e);
            }
        }
        
    }
}
