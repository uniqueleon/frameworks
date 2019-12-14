package org.aztec.framework.elastic_job.job;

import org.aztec.framework.core.SpringApplicationContext;
import org.aztec.framework.disconf.items.DisconfJobPropertiesConfig;
import org.aztec.framework.elastic_job.beans.JobExecuteContext;
import org.aztec.framework.elastic_job.beans.JobExecuteContext.ContextKeys;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;

import com.dangdang.ddframe.job.api.ShardingContext;
import com.dangdang.ddframe.job.api.simple.SimpleJob;
import com.dangdang.ddframe.job.executor.JobExecutorFactory;

/**
 * 作业基类
 * 
 * @author tansonlam
 * @create 2018年2月7日
 * 
 */
public class SimpleBaseJob implements SimpleJob {
    private Exception err;
    private final static Logger log = LoggerFactory
            .getLogger(SimpleBaseJob.class);
    

    //@Autowired
    //private DisconfJobPropertiesConfig config;
    
    /**
     * 根据传入的数据主键ID计算是否允许本实例节点执行
     * 
     * @param dataId
     *            数据主键ID
     * @param shardingContext
     * @return
     */
    public <T> Boolean isPermitData4LocalNode(
            ShardingContext shardingContext) {
        Integer serverIndex = JobExecuteContext.getValueWithDefault(ContextKeys.SERVER_INDEX,1);
        //Integer serverIndex = Integer.parseInt(config.getIndex());
        long index = Long.valueOf(serverIndex)
                % shardingContext.getShardingTotalCount();
        return index == shardingContext.getShardingItem();
    }

    public void setJobException(Exception e) {
        err = e;
    }

    

    public final void execute(ShardingContext shardingContext) {

        Boolean isEnabled = JobExecuteContext.getValueWithDefault(ContextKeys.SHARD_JOB_ENABLED,true);
        //boolean isEnabled = Boolean.parseBoolean(config.getEnabled());
        if(isEnabled && isPermitData4LocalNode(shardingContext)){
            try {
                SimpleJob baseJob = SpringApplicationContext.getBean(shardingContext.getJobName());
                if(baseJob == null){
                    throw new NullPointerException("Target Job[" + shardingContext.getJobName() + "] dosen't exists!");
                }
                baseJob.execute(shardingContext);
            } catch (Exception e) {
                log.error(e.getMessage(),e);
                throw new RuntimeException(e);
            }
        }
        //String moduleName = ConfigUtils.getProperty("dubbo.application.name");
        //String jobSwitch = ElasticConfig.getInstance().getConfig(moduleName);// 只有jobSwitch等于2的情况下才可以启动elastic方式的定时任务
        
        /*String jobSwitch = "2";
        if ("2".equals(jobSwitch)) {
            executeBusiness(shardingContext);
            if (err != null) {
                throw new RuntimeException(err);
            }
        }*/
    }
    
    //public boolean runSwitch(){
        
       // String moduleName = ConfigUtils.getProperty("dubbo.application.name");
        //String jobSwitch = ElasticConfig.getInstance().getConfig(moduleName);// 只有jobSwitch等于2的情况下才可以启动elastic方式的定时任务
        //String jobSwitch = "2";
        //return "2".equals(jobSwitch);
        
    //}

}
