package org.aztec.framework.heartbeat.impl;

import org.aztec.framework.heartbeat.HeartbeatManagerBeans;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@HeartbeatManagerBeans
@Configuration
public class EvaluatorFactory {
    
    private static final int TASK_NUM_WEIGHT = 2;

    @Bean
    public ServerEvaluator getEvaluator(){
        return (serverNode) -> {
            double score = 1;
            score *= 1 / serverNode.getCpu();
            score *= 1 / serverNode.getMemoryRatio();
            score *= 1 / serverNode.getDiskRatio();
            if(serverNode.getPayload() != 0){
                score *= Math.pow(1d / serverNode.getPayload(),TASK_NUM_WEIGHT);
            }
            score *= (serverNode.getDisk() * 1d / (1024 * 1024 * 1024));
            score *= (serverNode.getMemory() * 1d / (1024 * 1024 * 1024));
            return score;
        };
    }
}
