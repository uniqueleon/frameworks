package org.aztec.framework.heartbeat;

import java.util.Map;
import java.util.concurrent.atomic.AtomicLong;

import com.google.common.collect.Maps;


public class TaskInfoRecorder {

    private static AtomicLong count = new AtomicLong(0);
    private static AtomicLong finisehedCount = new AtomicLong(0);
    private static Map<Long,Long> startTimes = Maps.newConcurrentMap();
    private static Long totalLatency = 0l;
    private static Long avgLatency = 0l;
    
    public static void begin(){
        Long threadId = Thread.currentThread().getId();
        count.incrementAndGet();
        startTimes.put(threadId, System.currentTimeMillis());
    }
    
    public static void end(){
        Long threadId = Thread.currentThread().getId();
        if(startTimes.containsKey(threadId)){

            count.decrementAndGet();
            Long curTime = System.currentTimeMillis();
            Long startTime = startTimes.get(threadId);
            totalLatency += curTime - startTime;
            finisehedCount.incrementAndGet();
            avgLatency = finisehedCount.get() == 0 ? 0 : totalLatency / finisehedCount.get();
        }
    }
    
    public static Long getPayLoad(){
        return count.get();
    }
    
    public static Long getAverageLatency(){
        return avgLatency;
    }
    
    public static void set(Long payload){

        long currentCount = count.get();
        do{
            currentCount = count.get();
            if(currentCount == payload){
                return ;
            }
        }while(!count.compareAndSet(currentCount, payload));
    }
}
