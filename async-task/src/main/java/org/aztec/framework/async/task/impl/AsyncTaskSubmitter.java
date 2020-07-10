package org.aztec.framework.async.task.impl;

import java.util.Enumeration;
import java.util.Map;
import java.util.concurrent.Callable;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;

import org.aztec.framework.async.task.AsyncTaskConst;
import org.aztec.framework.async.task.AsyncTaskContext;
import org.aztec.framework.async.task.AsyncTaskDataService;
import org.aztec.framework.async.task.AsyncTaskException;
import org.aztec.framework.async.task.AsyncTaskExecutor;
import org.aztec.framework.async.task.AsyncTaskServerBean;
import org.aztec.framework.async.task.entity.TaskResult;
import org.aztec.framework.disconf.items.AsyncTaskConfigure;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Scope;
import org.springframework.stereotype.Component;

import com.sjsc.framework.api.restful.entity.async.AsyncTaskDTO;

@AsyncTaskServerBean
@Component
@Scope("prototype")
public class AsyncTaskSubmitter implements Callable<TaskResult> {

    private AsyncTaskContext context;
    // private static final ThreadLocal<DataExportExecutor> localExcutor = new
    // ThreadLocal<>();
    @Autowired
    AsyncTaskExecutor executor;
    @Autowired
    private AsyncTaskDataService notifier;
    private AsyncTaskDTO taskDTO;
    private static final Logger logger = LoggerFactory.getLogger(AsyncTaskConst.TASK_LOG_NAME);
    @Autowired
    AsyncTaskConfigure configure;

    public AsyncTaskSubmitter(AsyncTaskContext context, AsyncTaskDTO taskDAO)
            throws InstantiationException, IllegalAccessException {
        this.context = context;
        this.taskDTO = taskDAO;
    }

    @Override
    public TaskResult call() throws Exception {
        try {
            executor.initContext(context);
            TaskResult finalResult = null;
            HttpServletRequest contextReq = context.getHttpRequest();
            HttpServletResponse contextRsp = context.getHttpResponse();
            Integer cursor = 0;
            if (contextReq.getParameter(AsyncTaskConst.CONTEXT_REQUEST_BEGIN_PARAM_NAME) != null) {
                cursor = Integer.parseInt(contextReq.getParameter(AsyncTaskConst.CONTEXT_REQUEST_BEGIN_PARAM_NAME));
            } else {
                contextReq.setAttribute(
                        AsyncTaskConst.CONTEXT_REQUEST_ATTR_PREFIX + AsyncTaskConst.CONTEXT_REQUEST_BEGIN_PARAM_NAME,
                        cursor);
            }
            Integer num = 1;
            if (contextReq.getParameter(AsyncTaskConst.CONTEXT_REQUEST_NUM_PARAM_NAME) != null) {
                num = Integer.parseInt(contextReq.getParameter(AsyncTaskConst.CONTEXT_REQUEST_NUM_PARAM_NAME));
            } else {
                contextReq.setAttribute(
                        AsyncTaskConst.CONTEXT_REQUEST_ATTR_PREFIX + AsyncTaskConst.CONTEXT_REQUEST_NUM_PARAM_NAME,
                        num);
            }
            Integer totalNum = executor.count();
            Integer maxSize = Integer.parseInt(configure.getMaxDataSize());
            log4Task("The task data size:" + totalNum + ",while upper limit size is " + maxSize);
            if (totalNum >= maxSize) {
                AsyncTaskException dee = new AsyncTaskException("", AsyncTaskException.TASK_DATA_SIZE_EXCEED, null);
                dee.attachData(AsyncTaskConst.ERR_MSG_ATTACHMENT_KEYS.EXPORT_DATA_SIZE, totalNum);
                dee.attachData(AsyncTaskConst.ERR_MSG_ATTACHMENT_KEYS.MAX_DATA_SIZE, maxSize);
                throw dee;
            }
            printTaskInfo(contextReq, totalNum, cursor, num);
            int subTaskNum = totalNum / num;
            AsyncTaskDTO taskDto = context.getTaskDTO();
            taskDto.setSubTaskNum(new Long(subTaskNum));
            taskDto.setStatus(AsyncTaskConst.TASK_STATUS.EXECUTED.getDbCode());
            notifier.updateTask(taskDto);
            int taskCursor = 0;
            do {
                contextReq.setAttribute(
                        AsyncTaskConst.CONTEXT_REQUEST_ATTR_PREFIX + AsyncTaskConst.CONTEXT_REQUEST_BEGIN_PARAM_NAME,
                        cursor);
                contextReq.setAttribute(
                        AsyncTaskConst.CONTEXT_REQUEST_ATTR_PREFIX + AsyncTaskConst.CONTEXT_REQUEST_SUB_TASK_NO,
                        taskCursor);
                log4Task("executor is working on page=" + (cursor / num) + "....!");

                TaskResult result = executor.doExport();
                log4Task("data export on page=" + (cursor / num) + " success!");
                if (finalResult == null) {
                    finalResult = result;
                } else {
                    finalResult.append(result);
                }
                contextRsp.resetBuffer();
                cursor += num;
                taskCursor++;
            } while (cursor < totalNum);
            /*if (finalResult != null) {
                taskDTO.setOssFileUrl(finalResult.getOssFileUrl());
                notifier.notifySuccess(taskDTO);
            } else {
                throw new AsyncTaskException("No Data export!", AsyncTaskException.NO_DATA_EXPORT, null);
            }*/
            return finalResult;
        } catch (AsyncTaskException de) {
            logger.warn(de.getMessage());
            notifier.notifyEternalFail(taskDTO, de.toJson());
            return null;
        } catch (Exception e) {
            logger.error(e.getMessage(), e);
            try {
                notifier.notifyEternalFail(taskDTO, e.getMessage());
            } catch (Exception e1) {
                logger.error(e1.getMessage(), e1);
            }
            return null;
        }
    }

    public void cancel() {
        logger.info("Export task[seqNo=" + taskDTO.getSeqNo() + "] timeout! Cancel!");
        executor.cancel();
    }

    private void printTaskInfo(HttpServletRequest request, int total, int cursor, int pageNum) {

        log4Task("Executor[" + taskDTO.getExecutor() + "],submitter[ID=" + taskDTO.getUserId()
                + "] is ready to execute!(totalCount=" + total + ",offset=" + cursor + ",page size:" + pageNum + ")");
        log4RequestParams(request);
        log4SessionAttributes(request);
    }

    private void log4RequestParams(HttpServletRequest request) {
        StringBuilder builder = new StringBuilder();
        builder.append("\n>>>>>>>>>>>>>>>>>>>>>>>TASK[" + taskDTO.getSeqNo() + "] REQUEST PARAM<<<<<<<<<<<<<<<<<<<<\n");
        Map requestMap = request.getParameterMap();
        if (requestMap != null) {
            for (Object paramName : requestMap.keySet()) {
                builder.append(paramName + "=" + request.getParameter((String) paramName) + "\n");
            }
        }
        builder.append(">>>>>>>>>>>>>>>>>>>>>>>TASK[" + taskDTO.getSeqNo() + "] REQUEST PARAM<<<<<<<<<<<<<<<<<<<<\n");
        log4Task(builder.toString());
    }

    private void log4SessionAttributes(HttpServletRequest request) {
        StringBuilder builder = new StringBuilder();
        builder.append(
                "\n>>>>>>>>>>>>>>>>>>>>>>>TASK[" + taskDTO.getSeqNo() + "] SESSION ATTRIBUTE<<<<<<<<<<<<<<<<<<<<\n");
        HttpSession session = request.getSession();
        Enumeration attrNames = session.getAttributeNames();
        while (attrNames.hasMoreElements()) {
            Object attrKey = attrNames.nextElement();
            String attrNameStr = attrKey.toString();
            builder.append(attrNameStr + "=" + session.getAttribute(attrNameStr).toString() + "\n");
        }
        builder.append(
                ">>>>>>>>>>>>>>>>>>>>>>>TASK[" + taskDTO.getSeqNo() + "] SESSION ATTRIBUTE<<<<<<<<<<<<<<<<<<<<\n");
        log4Task(builder.toString());
    }

    private void log4Task(String logMsg) {
        String preffix = "Export Task[seqNo=" + taskDTO.getSeqNo() + "]:";
        logger.info(preffix + logMsg);
    }
}
