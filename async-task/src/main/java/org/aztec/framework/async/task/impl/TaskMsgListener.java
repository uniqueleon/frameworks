package org.aztec.framework.async.task.impl;

import java.util.Map;

import javax.annotation.Resource;

import org.aztec.framework.async.task.AsyncTaskConst;
import org.aztec.framework.async.task.AsyncTaskServerBean;
import org.aztec.framework.async.task.event.AsyncTaskEvent;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.context.ApplicationListener;
import org.springframework.context.annotation.Scope;
import org.springframework.context.event.EventListener;
import org.springframework.stereotype.Component;

import com.google.common.collect.Maps;
import com.sjsc.framework.api.restful.entity.async.AsyncTaskDTO;

@AsyncTaskServerBean
@Component
@Scope("singleton")
public class TaskMsgListener implements ApplicationListener<AsyncTaskEvent> {
	
	private static final Logger log = LoggerFactory.getLogger(AsyncTaskConst.TASK_LOG_NAME);

	@Resource
	private TaskDispatcher dataExportDispatcher;
	
	private static Map<String,Long> duplicateEvents = Maps.newConcurrentMap();
	

    @Override
    @EventListener
    public void onApplicationEvent(AsyncTaskEvent event) {
        // TODO Auto-generated method stub
        clearCache();
        if(duplicateEvents.containsKey(event.getId())){
            return;
        }
        duplicateEvents.put(event.getId(), System.currentTimeMillis());
        AsyncTaskDTO taskDto = (AsyncTaskDTO) event.getSource();
        log.info("Dispatching task[" + taskDto.getSeqNo() + "] to executor!");
        dataExportDispatcher.dispatchTask(taskDto);
        log.info("Dispatch task finished!");
    }

    public void clearCache(){
        for(String key : duplicateEvents.keySet()){
            long startTime = duplicateEvents.get(key);
            if(System.currentTimeMillis() - startTime > 3600 * 1000){
                duplicateEvents.remove(key);
            }
        }
    }
}