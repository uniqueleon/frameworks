package org.aztec.framework.core.utils;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.ApplicationArguments;
import org.springframework.boot.ApplicationRunner;

public class EurekaClientRunner implements ApplicationRunner {
    
    
    @Value("${spring.application.name}")
    private String appName;

    @Value("${eureka.instance.instance-id}")
    private String instanceId;
    
    private static final Logger LOG = LoggerFactory.getLogger(EurekaClientRunner.class);

    public EurekaClientRunner() {
        // TODO Auto-generated constructor stub
    }

    @Override
    public void run(ApplicationArguments args) throws Exception {
        // TODO Auto-generated method stub
        LOG.info("ADD SHUTDOWN HOOK TO ERUEKA SERVER!");
        Runtime.getRuntime().addShutdownHook(new ShutdownHook(Thread.currentThread(),appName,instanceId));
    }

    public static class ShutdownHook extends Thread {
        private Thread mainThread;
        
        private String appName;
        private String instanceId;
        
        
        public ShutdownHook(Thread mainThread, String appName, String instanceId) {
            super();
            this.mainThread = mainThread;
            this.appName = appName;
            this.instanceId = instanceId;
        }

        @Override
        public void run() {
            /*while(mainThread.isAlive()){

                System.out.println("main Thread is alive");
                //mainThread.interrupt();//给主线程发送一个中断信号
                try {
                    //sendServiceDownRequest();
                    Thread.sleep(1000);
                     //等待 mainThread 正常运行完毕
                } catch (Exception e) {
                    e.printStackTrace();
                }
           }*/

            try {
                mainThread.join();
                LOG.info("clear eureka server registry");
                EurekaRequestUtils.sendServiceDeleteRequest(appName,instanceId);
            } catch (Exception e) {
                LOG.error(e.getMessage(),e);
            }
        }

    }
}
