package org.aztec.framework.async.task.controller;

import java.io.IOException;
import java.util.ArrayList;
import java.util.Enumeration;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;

import javax.annotation.Resource;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;

import org.apache.commons.lang3.StringUtils;
import org.aztec.framework.async.task.AsyncTaskConst;
import org.aztec.framework.async.task.AsyncTaskServerBean;
import org.aztec.framework.async.task.AsyncTaskConst.TASK_STATUS;
import org.aztec.framework.async.task.entity.AsyncTaskDO;
import org.aztec.framework.async.task.entity.AsyncTaskParam;
import org.aztec.framework.async.task.entity.AsyncTaskResult;
import org.aztec.framework.async.task.entity.AsyncTaskVO;
import org.aztec.framework.async.task.entity.SelectJsonVO;
import org.aztec.framework.async.task.util.ExportTaskUtils;
import org.aztec.framework.async.task.util.StringUtilsProperties;
import org.aztec.framework.disconf.items.AsyncTaskConfigure;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.BeanUtils;
import org.springframework.http.MediaType;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RestController;

import com.alibaba.fastjson.JSON;
import com.google.common.collect.Maps;
import com.sjsc.framework.api.restful.entity.RestRequest;
import com.sjsc.framework.api.restful.entity.RestResult;
import com.sjsc.framework.api.restful.entity.async.AsyncTaskQO;
import com.sjsc.framework.web.controller.BaseController;

@AsyncTaskServerBean
@RestController
public class AsyncTaskController extends BaseController {

    private static final int DEFAULT_PAGE_SIZE = 10;
    @Resource
    private ExportTaskUtils taskUtils;
    @Resource
    private AsyncTaskConfigure config;

    private static final Logger logger = LoggerFactory.getLogger(AsyncTaskController.class);

//    @RequestMapping(value = "/async/task/query", method = { RequestMethod.POST }, consumes = {
//            MediaType.APPLICATION_JSON_UTF8_VALUE }, produces = { MediaType.APPLICATION_JSON_UTF8_VALUE })
    public RestResult<AsyncTaskResult> queryTask(@RequestBody RestRequest<AsyncTaskQO> restReq,
            HttpServletRequest request, HttpServletResponse response) {

        AsyncTaskQO taskQO = restReq.getParam();
        AsyncTaskResult context = new AsyncTaskResult();
        try {
            String optType = taskQO.getOptType();
            AsyncTaskParam param = new AsyncTaskParam();
            BeanUtils.copyProperties(taskQO, param);
            Long userID = getUserID(request);
            taskQO.setUserId(userID);
            switch (optType) {
            case "QUERY":
                executeQuery(request, context, taskQO);
                break;
            case "DOWNLOAD":
                taskQO.setUserId(userID);
                // nav.redirectToLocation(getOssAccessLink(taskQO));

                break;
            // case "CREATE":

            // RedisManage manager = RedisManage.getInstance();
            // String sessionID = request.getSession().getId();
            // param = (ExportTaskParam)
            // request.getSession().getAttribute(ExportTaskConst.TASK_PARAM_COOKIE_KEY);
            // param =
            // manager.get(ExportTaskConst.EXPORT_TASK_PARAM_REDIS_KEY_PREFIX +
            // sessionID);
            // if(isRequestParamToLong(param)){
            // context.setErrorMsg(StringUtilsProperties.getSesseionByProt("EXPORT_TASK_CREAT_ERR_2"));
            // } else if(isSessionDataToLong(param)){
            // context.setErrorMsg(StringUtilsProperties.getSesseionByProt("EXPORT_TASK_CREAT_ERR_3"));
            // }
            // taskUtils.submitTask(param);
            // context.setSubmitParam(param);
            // context.setTaskQO(taskQO);
            // break;
            // case "SUBMIT":
            //
            // param.setPreSubmit(false);
            // taskUtils.submitTask(param);
            // break;
            case "AUDIT":
                executeAuditQuery(request, context, taskQO);
                break;
            default:
                break;
            }
        } catch (Exception e) {
            // e.printStackTrace();
            logger.error("ExportTask execute fail:", e);
        }
        return new RestResult<AsyncTaskResult>(true, "OK", "OK", context);
    }

    @RequestMapping(value = "/async/task/submit", method = { RequestMethod.POST }, consumes = {
            MediaType.APPLICATION_JSON_UTF8_VALUE }, produces = { MediaType.APPLICATION_JSON_UTF8_VALUE })
    public RestResult<AsyncTaskResult> submit(@RequestBody RestRequest<AsyncTaskQO> restReq,HttpServletRequest request) {

        AsyncTaskResult context = new AsyncTaskResult();
        AsyncTaskParam param = new AsyncTaskParam();
        BeanUtils.copyProperties(restReq.getParam(), param);
        param.setSessionData(getSessionData(request));
        if (isRequestParamToLong(param)) {
            context.setErrorMsg(StringUtilsProperties.getSesseionByProt("EXPORT_TASK_CREAT_ERR_2"));
        } else if (isSessionDataToLong(param)) {
            context.setErrorMsg(StringUtilsProperties.getSesseionByProt("EXPORT_TASK_CREAT_ERR_3"));
        }
        taskUtils.submitTask(param);
        context.setSubmitParam(param);
        return new RestResult<AsyncTaskResult>(true, "OK", "OK", context);
    }
    
    private String getSessionData(HttpServletRequest request){
        
        String retString = "";
        HttpSession session = request.getSession();
        Map<String,Object> dataMap = Maps.newHashMap();
        Enumeration<String> attrNames = session.getAttributeNames();
        while(attrNames.hasMoreElements()){
            String key = attrNames.nextElement();
            dataMap.put(key, session.getAttribute(key));
        }
        return JSON.toJSONString(dataMap);
    }

    private void executeAuditQuery(HttpServletRequest request, AsyncTaskResult context, AsyncTaskQO taskQO)
            throws Exception {

        initQueryParam(request, taskQO);
        List<AsyncTaskDO> taskDatas = taskUtils.getSummaryTaskInfo(taskQO);
        setPaginator(taskQO.getOptType(), context, taskQO, taskDatas);
        context.setTaskQO(taskQO);
        initStatusSelector(context);
    }

    private void executeQuery(HttpServletRequest request, AsyncTaskResult context, AsyncTaskQO taskQO)
            throws Exception {

        initQueryParam(request, taskQO);
        List<AsyncTaskDO> taskDatas = taskUtils.findTasks(taskQO);
        setPaginator(taskQO.getOptType(), context, taskQO, taskDatas);
        initStatusSelector(context);
    }

    private boolean isSessionDataToLong(AsyncTaskParam param) {

        Boolean sessionDataLimit = Boolean.parseBoolean(config.getSessionDataLimitEnabled());
        if(param.getSessionData() == null){
            return false;
        }
        int sessionDataLength = param.getSessionData().length();
        Integer lengthLimit = Integer.parseInt(config.getSessionDataLimit());
        if (sessionDataLimit && sessionDataLength > lengthLimit) {
            return true;
        }
        return false;
    }

    private boolean isRequestParamToLong(AsyncTaskParam param) {

        Boolean requestParamLimit = Boolean.parseBoolean(config.getRequestParamLimitEnabled());
        Integer lengthLimit = Integer.parseInt(config.getRequestParamLimit());
        if(param.getExcParam() == null){
            return false;
        }
        if (requestParamLimit && param.getExcParam().length() > lengthLimit) {
            return true;
        }
        return false;
    }

    private String getOssAccessLink(AsyncTaskQO taskQO) throws IOException {
        // response.sendRedirect(taskUtils.getOssFileAccessURL(taskQO.getOssFileUrl()));
        return taskUtils.getOssFileAccessURL(taskQO.getOssFileUrl());
    }

    private void initQueryParam(HttpServletRequest request, AsyncTaskQO taskQO) {

        taskQO.setIsJoin(true);
        if (taskQO.getBegin() == null) {
            taskQO.setBegin(0);
        } else if (!isOnlyPageChange(request, taskQO)) {
            taskQO.setBegin(0);
        }
        if (taskQO.getNum() == null) {
            taskQO.setNum(DEFAULT_PAGE_SIZE);
        }
        if (taskQO.getStatus() != null && taskQO.getStatus() == -1) {
            taskQO.setStatus(null);
        }
    }

    private void setPaginator(String optType, AsyncTaskResult context, AsyncTaskQO taskQO, List<AsyncTaskDO> taskDatas)
            throws Exception {
        if (optType.equals("QUERY") && StringUtils.isNotBlank(taskQO.getStatusCode())) {
            String tipsMsg = StringUtilsProperties.getSesseionByProt("expts_tips_" + taskQO.getStatusCode());
            if (taskQO.getSeqNo() != null) {
                tipsMsg += StringUtilsProperties.getSesseionByProt("expts_tips_seq_no_prefix") + taskQO.getSeqNo();
            }
            context.setErrorMsg(tipsMsg);
        }
        context.setTaskList(toTaskVO(taskDatas));
        context.setTaskQO(taskQO);
        int currentPage = 1;
        int totalPage = 1;
        if (taskDatas.size() > 0) {
            int totalNum = taskDatas.size() * DEFAULT_PAGE_SIZE;
            totalPage = (totalNum / DEFAULT_PAGE_SIZE) + (totalNum % DEFAULT_PAGE_SIZE == 0 ? 0 : 1);
            currentPage = (taskQO.getBegin() / DEFAULT_PAGE_SIZE) + 1;
        }
        context.setCurrentPage(currentPage);
        context.setTotalPage(totalPage);
        taskQO.setBegin(currentPage);
        setLastQueryObj(taskQO);
    }

    private List<AsyncTaskVO> toTaskVO(List<AsyncTaskDO> taskDatas) {
        List<AsyncTaskVO> taskVOs = new ArrayList<>();
        for (AsyncTaskDO taskDO : taskDatas) {
            AsyncTaskVO taskVO = new AsyncTaskVO();
            BeanUtils.copyProperties(taskDO, taskVO);
            try {
                Map<String, String> computerInfoMap = JSON.parseObject(taskDO.getUserComInfo(), Map.class);
                taskVO.setMac(computerInfoMap.get("MacAddress"));
            } catch (Exception e1) {
                taskVO.setMac(taskDO.getUserComInfo());
            }
            taskVO.setOssFileUrl(taskDO.getOssFileUrl());
            taskVO.setStatusCode("" + taskDO.getStatus());
            taskVO.setStatus(StringUtilsProperties.getSesseionByProt("expts_status_" + taskDO.getStatus()));
            if (StringUtils.isNotBlank(taskDO.getErrMsg())) {
                try {
                    Map<String, Object> errDataMap = JSON.toJavaObject(JSON.parseObject(taskDO.getErrMsg()), Map.class);
                    String targetMsg = StringUtilsProperties.getSesseionByProt(
                            "expts_tips_TR_F_" + errDataMap.get(AsyncTaskConst.ERR_MSG_ATTACHMENT_KEYS.ERR_CODE));
                    errDataMap.remove(AsyncTaskConst.ERR_MSG_ATTACHMENT_KEYS.ERR_CODE);
                    for (Entry<String, Object> dataKey : errDataMap.entrySet()) {
                        targetMsg = targetMsg.replace("{" + dataKey.getKey() + "}", "" + dataKey.getValue().toString());
                    }
                    taskVO.setErrMsg(targetMsg);
                } catch (Exception e) {
                    taskVO.setErrMsg(taskDO.getErrMsg());
                }
            }
            taskVOs.add(taskVO);
        }
        return taskVOs;
    }

    private void initStatusSelector(AsyncTaskResult context) {
        TASK_STATUS[] status = AsyncTaskConst.TASK_STATUS.values();

        List<SelectJsonVO> statusList = new ArrayList<SelectJsonVO>();
        statusList.add(new SelectJsonVO(StringUtilsProperties.getSesseionByProt("expts_status_all"), "-1"));
        for (TASK_STATUS thisStatus : status) {
            statusList.add(
                    new SelectJsonVO(StringUtilsProperties.getSesseionByProt("expts_status_" + thisStatus.getDbCode()),
                            "" + thisStatus.getDbCode()));
        }
        // context.put("taskStatusJson", JSON.toJSON(statusList));
    }

    private boolean isOnlyPageChange(HttpServletRequest request, AsyncTaskQO taskQO) {
        Object attrObj = request.getSession().getAttribute("lastQO");
        if (attrObj == null)
            return false;
        String lastQO = (String) attrObj;
        AsyncTaskQO queryQO = new AsyncTaskQO();
        BeanUtils.copyProperties(taskQO, queryQO);
        queryQO.setBegin(null);
        queryQO.setNum(null);
        String jsonForm = JSON.toJSONString(queryQO);
        if (jsonForm.equals(lastQO))
            return true;
        return false;
    }

    private void setLastQueryObj(AsyncTaskQO taskQO) {
        AsyncTaskQO queryQO = new AsyncTaskQO();
        BeanUtils.copyProperties(taskQO, queryQO);
        queryQO.setBegin(null);
        queryQO.setNum(null);
        // request.getSession().setAttribute("lastQO",
        // JSON.toJSONString(queryQO));
    }

}
