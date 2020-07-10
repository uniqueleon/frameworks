package org.aztec.framework.async.task.entity;

import java.util.Date;

import javax.persistence.Id;
import javax.persistence.Table;

@Table(name="async_task_detail")
public class AsyncTaskDetailDO {


//`id` bigint primary key auto_increment,
    @Id
    private Long id;
//`task_id` bigint not null comment '主任务id',
    private Long taskId;
//`seq_no` int not null comment '子任务序号',
    private Integer seqNo;
//`current_data_no` bigint not null comment '当前记录数',
    private Long currentDataNo;
//`data_size` bigint not null comment '数据数',
    private Long dataSize;
//`precent` float not null comment '百分数',
    private Float precent;
//`result_type` int not null commment '结果类型.0、无输出.1、文件',
    private Integer resultType;
//`result_file` varchar(100) not null comment '结果文件',
    private String resultData;
//`status` int not null comment '是否成功.0.未启动,1.已执行.3.成功.4.失败',
    private Integer status;
//`result_msg` varchar(500) comment '结果信息'
    private String resultMsg;
    
    /**
     * column base_wmp_task.gmt_create  ����ʱ��
     */
    private Date createTime;

    /**
     * column base_wmp_task.gmt_modified  �޸�ʱ��
     */
    private Date updateTime;
    
    private Date startTime;
    
    private String server;
    
    public Long getId() {
        return id;
    }
    public void setId(Long id) {
        this.id = id;
    }
    public Long getTaskId() {
        return taskId;
    }
    public void setTaskId(Long taskId) {
        this.taskId = taskId;
    }
    public Integer getSeqNo() {
        return seqNo;
    }
    public void setSeqNo(Integer seqNo) {
        this.seqNo = seqNo;
    }
    public Long getCurrentDataNo() {
        return currentDataNo;
    }
    public void setCurrentDataNo(Long currentDataNo) {
        this.currentDataNo = currentDataNo;
    }
    public Long getDataSize() {
        return dataSize;
    }
    public void setDataSize(Long dataSize) {
        this.dataSize = dataSize;
    }
    public Float getPrecent() {
        return precent;
    }
    public void setPrecent(Float precent) {
        this.precent = precent;
    }
    public Integer getResultType() {
        return resultType;
    }
    public void setResultType(Integer resultType) {
        this.resultType = resultType;
    }
    
    public String getResultData() {
        return resultData;
    }
    public void setResultData(String resultData) {
        this.resultData = resultData;
    }
    public Integer getStatus() {
        return status;
    }
    public void setStatus(Integer status) {
        this.status = status;
    }
    public String getResultMsg() {
        return resultMsg;
    }
    public void setResultMsg(String resultMsg) {
        this.resultMsg = resultMsg;
    }
    public String getServer() {
        return server;
    }
    public void setServer(String server) {
        this.server = server;
    }
    public Date getCreateTime() {
        return createTime;
    }
    public void setCreateTime(Date createTime) {
        this.createTime = createTime;
    }
    public Date getUpdateTime() {
        return updateTime;
    }
    public void setUpdateTime(Date updateTime) {
        this.updateTime = updateTime;
    }
    public Date getStartTime() {
        return startTime;
    }
    public void setStartTime(Date startTime) {
        this.startTime = startTime;
    }
    
    
}
