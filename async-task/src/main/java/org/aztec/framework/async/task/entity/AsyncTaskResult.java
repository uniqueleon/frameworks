package org.aztec.framework.async.task.entity;

import java.util.List;

import com.sjsc.framework.api.restful.entity.async.AsyncTaskQO;

public class AsyncTaskResult {

    private String errorMsg;
    
    private AsyncTaskParam submitParam;
    
    private AsyncTaskQO taskQO;
    
    private List<AsyncTaskVO> taskList;

    private Integer currentPage;
    private Integer totalPage;

    public String getErrorMsg() {
        return errorMsg;
    }


    public void setErrorMsg(String errorMsg) {
        this.errorMsg = errorMsg;
    }


    public AsyncTaskParam getSubmitParam() {
        return submitParam;
    }


    public void setSubmitParam(AsyncTaskParam submitParam) {
        this.submitParam = submitParam;
    }


    public AsyncTaskQO getTaskQO() {
        return taskQO;
    }


    public void setTaskQO(AsyncTaskQO taskQO) {
        this.taskQO = taskQO;
    }


    public List<AsyncTaskVO> getTaskList() {
        return taskList;
    }


    public void setTaskList(List<AsyncTaskVO> taskList) {
        this.taskList = taskList;
    }


    public Integer getCurrentPage() {
        return currentPage;
    }


    public void setCurrentPage(Integer currentPage) {
        this.currentPage = currentPage;
    }


    public Integer getTotalPage() {
        return totalPage;
    }


    public void setTotalPage(Integer totalPage) {
        this.totalPage = totalPage;
    }
    
    
}
