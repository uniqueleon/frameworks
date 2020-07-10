package org.aztec.framework.async.task.controller;

import org.aztec.framework.async.task.AsyncTaskException;
import org.aztec.framework.async.task.AsyncTaskManager;
import org.aztec.framework.async.task.AsyncTaskServerBean;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.MediaType;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RestController;

import com.sjsc.framework.api.restful.entity.RestRequest;
import com.sjsc.framework.api.restful.entity.RestResult;
import com.sjsc.framework.api.restful.entity.async.AsyncTaskReport;
import com.sjsc.framework.api.restful.feign.TaskDetailReportService;
import com.sjsc.framework.core.FrameworkLogger;

@AsyncTaskServerBean
@RestController
public class SubTaskReportController implements TaskDetailReportService{
    
    @Autowired
    AsyncTaskManager taskManager;
    
    @RequestMapping(value="/subtask/report", method = { RequestMethod.POST }, consumes = {
            MediaType.APPLICATION_JSON_UTF8_VALUE }, produces = { MediaType.APPLICATION_JSON_UTF8_VALUE })
    @Override
    public RestResult<Boolean> report(@RequestBody RestRequest<AsyncTaskReport> report)  {
        // TODO Auto-generated method stub
        try {
            taskManager.reportSubtask(report.getParam());
            return new RestResult<>(true,"ok","ok",true);
        } catch (AsyncTaskException e) {
            FrameworkLogger.error(e);
            return new RestResult<>(false,"fail","fail",false);
        }
    }

    
    
}
