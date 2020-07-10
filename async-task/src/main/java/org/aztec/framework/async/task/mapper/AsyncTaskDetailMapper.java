package org.aztec.framework.async.task.mapper;

import java.util.List;

import org.apache.ibatis.annotations.Param;
import org.aztec.framework.async.task.entity.AsyncTaskDetailDO;

import com.sjsc.framework.mybatis.conf.dao.ibatis.mapper.MyMapper;


public interface AsyncTaskDetailMapper<T extends AsyncTaskDetailDO> extends MyMapper<T> {

    public List<AsyncTaskDetailDO> findTaskDetails(@Param("taskId")Long taskId);
    public AsyncTaskDetailDO findSubtask(@Param("taskId")Long taskId,@Param("seqNo")Integer seqNo);
}
