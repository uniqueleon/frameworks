package org.aztec.framework.core.disconf;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.util.Map;
import java.util.Properties;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import com.baidu.disconf.client.usertools.IDisconfDataGetter;
import com.google.common.collect.Maps;

/**
 * 
 * @author liming
 *
 */
@Component
public class DefaultDataGetter implements IDisconfDataGetter {
    
    @Autowired
    DisconfConnectionConfig config;
    
    private static final Logger LOG = LoggerFactory.getLogger(IDisconfDataGetter.class);
    
    
    private static Map<String,Map<String,Object>> mapCache = Maps.newConcurrentMap();

    @Override
    public Map<String, Object> getByFile(String fileName) {
        
        return getMap(fileName);
    }
    
    private Map<String,Object> getMap(String fileName){
        try {
            File configFile = getFile(fileName, config);
            String md5 = DisconfWebUtils.getMD5Substract(configFile);
            if(md5 != null && mapCache.containsKey(md5)){
                return mapCache.get(md5);
            }
            else{
                Map<String,Object> newMap = readContentAsMap(configFile);
                mapCache.put(md5, newMap);
                return newMap;
            }
        } catch (Exception e) {
            LOG.error(e.getMessage(),e);
            return Maps.newHashMap();
        }
    }
    
    private Map<String,Object> readContentAsMap(File downloadFile) throws FileNotFoundException, IOException{
        Properties properties = new Properties();
        //properties.load(inStream);
        Map<String,Object> retMap = Maps.newHashMap();
        properties.load(new FileInputStream(downloadFile));
        properties.forEach((key,value) -> {
            retMap.put((String)key, value);
        });
        return retMap;
    }
    
    private File getFile(String fileName,DisconfConnectionConfig config){
        File downloadFile = new File(config.getDownloadDir() + File.separator + fileName);
        try {
            if(!downloadFile.exists()){
                DisconfWebUtils.download(fileName, config);
            }
            if(!DisconfWebUtils.isWatcherExists(fileName, config)){
                DisconfWebUtils.registWatcher(fileName, config);
            }
        } catch (Exception e) {
            LOG.error(e.getMessage(),e);
        }
        return downloadFile;
    }

    @Override
    public Object getByFileItem(String fileName, String fileItem) {
        Map<String,Object> dataMap = getMap(fileName);
        return dataMap.get(fileItem);
    }

    @Override
    public Object getByItem(String itemName) {
        throw new UnsupportedOperationException("Unsupport disconf item !");
    }

}
