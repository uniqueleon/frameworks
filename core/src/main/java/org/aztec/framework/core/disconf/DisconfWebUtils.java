package org.aztec.framework.core.disconf;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.util.Map;
import java.util.Scanner;

import org.apache.commons.io.IOUtils;
import org.apache.commons.pool.impl.GenericObjectPool;
import org.apache.curator.RetryPolicy;
import org.apache.curator.framework.CuratorFramework;
import org.apache.curator.framework.recipes.cache.NodeCache;
import org.apache.curator.framework.recipes.cache.NodeCacheListener;
import org.apache.curator.retry.ExponentialBackoffRetry;
import org.apache.zookeeper.data.Stat;
import org.aztec.framework.core.utils.CodecUtils;
import org.aztec.framework.core.utils.HttpRequestUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.google.common.collect.Maps;

/**
 * 与Disconf web后台通讯的工具类 
 * @author liming
 *
 */
public class DisconfWebUtils {
    
    private static final String DOWNLOAD_URL_PATTERN = "/api/config/file?app=%s&env=%s&version=%s&key=%s";
    
    private static final String ZK_MONITOR_PATH_TEMPLATE = "/disconf/%s_%s_%s/file/%s";
    
    private static final Map<String,NodeCache> caches = Maps.newConcurrentMap();
    
    private static final Logger LOG = LoggerFactory.getLogger(DisconfWebUtils.class);

    private static GenericObjectPool<CuratorFramework> clientPool;
    
    private static Object lockObj = new Object();
    

    /**
     * 从disconf web后端读取配置文件
     * @param fileName
     * @param config
     * @return
     * @throws Exception
     */
    public static File download(String fileName,DisconfConnectionConfig config) throws Exception{
        
        String theUrl = config.getWebUrl() + DOWNLOAD_URL_PATTERN;
        theUrl = String.format(theUrl, new Object[]{config.getAppName(),config.getEnv(),config.getVersion(),fileName});
        String downloadPath = config.getDownloadDir() + File.separator + fileName;
        File baseDir = new File(config.getDownloadDir());
        if(!baseDir.exists()){
            baseDir.mkdirs();
        }
        File targetFile =  HttpRequestUtils.download(theUrl, downloadPath);
        refreshMD5(targetFile);
        return targetFile;
    }
    
    /**
     * 判断某个文件监视器是否存在
     * @param fileName
     * @param config
     * @return
     */
    public static boolean isWatcherExists(String fileName,DisconfConnectionConfig config){
        String path = getDisconfZkMonitorPath(config, fileName);
        return caches.containsKey(path);
    }
    
    /**
     * 为某个配置文件注册一个监视器
     * @param fileName
     * @param config
     * @throws Exception
     */
    public static void registWatcher(String fileName,DisconfConnectionConfig config) throws Exception{
        RetryPolicy retryPolicy = new ExponentialBackoffRetry(1000, 3);
        if(clientPool == null){
            synchronized (lockObj) {
                if(clientPool == null){
                    clientPool = new GenericObjectPool<>(new CuratorClientFactory(config));
                    for(int i = 0;i < 2;i++){
                        clientPool.addObject();
                    }
                }
            }
        }
        CuratorFramework client = clientPool.borrowObject();
        //client.create().
        String path = getDisconfZkMonitorPath(config, fileName);
        Stat stat = client.checkExists().forPath(path);
        if(stat != null){
            //client.create().forPath(path);
            NodeCache nodeCache = new NodeCache(client, path);
            nodeCache.start(true);
            nodeCache.getListenable().addListener(new ConfigFileMonitor(config, fileName,path));
            caches.put(path,nodeCache);
        }
        else {
            LOG.error("Can't regist watcher ,because the path[" + path + "] is not exists!");
        }
        clientPool.returnObject(client);
    }
    
    /**
     * 获取配置文件监视器在 zookeeper 中的路径
     * @param config
     * @param fileName
     * @return
     */
    public static String getDisconfZkMonitorPath(DisconfConnectionConfig config,String fileName){
        return String.format(ZK_MONITOR_PATH_TEMPLATE, new Object[]{config.getAppName(),config.getVersion(),config.getEnv(),fileName});
    }
    
    /**
     * 配置文件监视器
     * @author liming
     *
     */
    public static class ConfigFileMonitor implements NodeCacheListener{
        
        private static final String THREAD_NAME_PREFIX = "DISCONF_FILE_";
        
        private DisconfConnectionConfig config;
        private String fileName;
        private String path;
        
        public ConfigFileMonitor(DisconfConnectionConfig config,String fileName,String path){
            this.config = config;
            this.fileName = fileName;
        }


        @Override
        public void nodeChanged() throws Exception {
            // TODO Auto-generated method stub
            System.out.println("update config!");
            File configFile = download(fileName, config);
            refreshMD5(configFile);
        }
        
        
    }
    
    private static String getMD5FileName(File targetFile){

        String fileName = targetFile.getName().substring(0,targetFile.getName().lastIndexOf("."));
        return fileName + ".md5";
    }
    
    private static void refreshMD5(File targetFile) throws FileNotFoundException, IOException{
        FileInputStream fis = new FileInputStream(targetFile);
        String fileContent = IOUtils.toString(fis);
        String md5Content = CodecUtils.hexMD5(fileContent);
        File md5Text = new File(getMD5FileName(targetFile));
        FileOutputStream fos = new FileOutputStream(md5Text);
        fos.write(md5Content.getBytes());
        fos.flush();
        fos.close();
        fis.close();
    }
    
    public static String getMD5Substract(File targetFile) throws FileNotFoundException, IOException{
        File md5File = new File(getMD5FileName(targetFile));
        if(!md5File.exists()){
            return null;
        }
        FileInputStream fis = new FileInputStream(md5File);
        String md5 = IOUtils.toString(fis);
        fis.close();
        return md5;
    }
    
    public static void main(String[] args) {
        try {
            download("job_simple1_conf.properties",new SysEnvConfig());
            registWatcher("job_simple1_conf.properties", new SysEnvConfig());
            while(true){
                Scanner scanner = new Scanner(System.in);
                while(scanner.hasNext()){

                    String cmd = scanner.next();
                    if(cmd.equals("exit")){
                        System.out.println("Bye!");
                        System.exit(0);
                    }
                }
            }
        } catch (Exception e) {
            // TODO Auto-generated catch block
            e.printStackTrace();
        }
    }
}
