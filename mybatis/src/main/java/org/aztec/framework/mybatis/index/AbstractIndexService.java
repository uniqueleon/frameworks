package org.aztec.framework.mybatis.index;

import java.io.IOException;
import java.io.UnsupportedEncodingException;
import java.math.BigDecimal;
import java.security.NoSuchAlgorithmException;
import java.util.Date;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;

import org.aztec.framework.core.utils.CodecUtils;
import org.aztec.framework.core.utils.ZipUtils;
import org.aztec.framework.disconf.items.ShardIndexConf;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;

import com.google.api.client.util.Lists;
import com.google.api.client.util.Maps;

public abstract class AbstractIndexService implements IndexService {

    protected abstract BasicIndexMapper getMapper();

    protected abstract BasicIndex newInstance();

    @Autowired
    ShardIndexConf shardConf;

    public static final String KEY_VALUE_SEPERATOR = "_";
    public static final String VALUE_SEPERATOR = "#";
    public static final String DATA_SEPERATOR = ",";
    
    private static final Logger LOG = LoggerFactory.getLogger(AbstractIndexService.class);

    private BasicIndex newIndex(long remainder, Map<String, List<Long>> indexData)
            throws UnsupportedEncodingException, IOException {

        BasicIndex bIndex = newInstance();
        bIndex.setCreateTime(new Date());
        bIndex.setUpdateTime(new Date());
        bIndex.setDataSize(indexData.keySet().size() + 0l);
        bIndex.setMaxDataSize(Long.parseLong(shardConf.getMaxDataSize()));
        bIndex.setIndexData(ZipUtils.zip(wrapIndexData(indexData).getBytes("UTF-8")));
        bIndex.setCreateTime(new Date());
        bIndex.setUpdateTime(new Date());
        bIndex.setRemainder(remainder);
        // bIndex.setIndexData(indexData);

        return bIndex;
    }
    
    public boolean isSame(List<Long> oldData,List<Long> newData){
        if(oldData.size() ==  newData.size()){
            for(int i = 0;i < oldData.size();i++){
                if(!newData.contains(oldData.get(i))){
                    return false;
                }
            }
            return true;
        }
        return false;
    }
    
    public List<String> replaceSameData(String oldData,Map<String, List<Long>> indexData){
        
        List<String> matchKeys = Lists.newArrayList();
        for(String indexKey : indexData.keySet()){
            String targetStr = searchKey(oldData, indexKey);
            if(targetStr != null){
                matchKeys.add(indexKey);
                List<Long> extData = extractData(targetStr);
                List<Long> newData = indexData.get(indexKey);
                if(!isSame(extData, newData)){
                    oldData = oldData.replace(targetStr, wrapSingleData(indexKey, newData));
                }
            }
        }
        return matchKeys;
    }
    
    private static class NoDataUpdateException extends Exception{

        public NoDataUpdateException(String message) {
            super(message);
            // TODO Auto-generated constructor stub
        }
        
    }

    private BasicIndex updateIndex(BasicIndex bIndex, long remainder, Map<String, List<Long>> indexData)
            throws UnsupportedEncodingException, IOException, IllegalArgumentException, NoDataUpdateException {

        bIndex.setUpdateTime(new Date());
        Long newDataSize = new Long(indexData.values().size());
        String newData = null;
        
        if(bIndex.getIndexData() != null){
            String oldData = new String(ZipUtils.unzip(bIndex.getIndexData()));
            List<String> replaceKeys = replaceSameData(oldData, indexData);
            newDataSize -= replaceKeys.size();
            if(newDataSize == 0){
                throw new NoDataUpdateException("The remainder [" + remainder + "] data has nothing to update!");
            }
            Map<String,List<Long>> insertData = Maps.newHashMap();
            insertData.putAll(indexData);
            for(String replKey : replaceKeys){
                insertData.remove(replKey);
            }
            newData = wrapIndexData(insertData) + "," + oldData;
        }
        else {
            newData = wrapIndexData(indexData);
        }
        bIndex.setIndexData(ZipUtils.zip(newData.getBytes("UTF-8")));
        bIndex.setRemainder(remainder);
        bIndex.setNewSize(newDataSize);
        // bIndex.setIndexData(indexData);
        return bIndex;
    }
    
    private String wrapSingleData(String key,List<Long> datas){
        StringBuilder builder = new StringBuilder();
        builder.append(key + "_");
        for (int i = 0; i < datas.size(); i++) {
            if (i != 0) {
                builder.append("#");
            }
            builder.append(datas.get(i));
        }
        return builder.toString();
    }

    private String wrapIndexData(Map<String, List<Long>> indexData) {
        StringBuilder builder = new StringBuilder();
        for (String key : indexData.keySet()) {
            if (!builder.toString().isEmpty()) {
                builder.append(",");
            }
            builder.append(wrapSingleData(key, indexData.get(key)));
            
        }
        return builder.toString();
    }

    private Map<Long, List<String>> shuffle(Set<String> indexKeys)
            throws UnsupportedEncodingException, NoSuchAlgorithmException {
        Map<Long, List<String>> retMap = Maps.newHashMap();
        for (String key : indexKeys) {
            BigDecimal bd = CodecUtils.sha256ToBigDecimal(key);
            bd = bd.remainder(new BigDecimal(shardConf.getModular()));
            Long remainder = bd.longValue();
            List<String> keyList = retMap.get(remainder);
            if (keyList == null) {
                keyList = Lists.newArrayList();
            }
            keyList.add(key);
            retMap.put(remainder, keyList);
        }
        return retMap;
    }

    private Map<Long, Map<String, List<Long>>> shuffle(Map<String, List<Long>> indexData)
            throws UnsupportedEncodingException, NoSuchAlgorithmException {
        Map<Long, List<String>> shuffleKeys = shuffle(indexData.keySet());
        Map<Long, Map<String, List<Long>>> retMap = Maps.newHashMap();
        for (Long remainder : shuffleKeys.keySet()) {
            List<String> keys = shuffleKeys.get(remainder);
            Map<String, List<Long>> dataMap = retMap.get(remainder);
            if (dataMap == null) {
                dataMap = Maps.newHashMap();
            }
            for (String key : keys) {
                dataMap.put(key, indexData.get(key));
            }
            retMap.put(remainder, dataMap);
        }
        return retMap;
    }

    private Long split(List<Long> remainders) {
        long totalData = getMapper().sumDataSize(remainders);
        long affordable = Long.parseLong(shardConf.getAffordableSize());
        return totalData / affordable;

    }

    @Override
    public void addIndex(Map<String, List<Long>> indexData)
            throws NoSuchAlgorithmException, IOException, InterruptedException {
        Map<Long, Map<String, List<Long>>> shuffleIndex = shuffle(indexData);
        BasicIndexMapper mapper = getMapper();
        List<Long> remainders = Lists.newArrayList(shuffleIndex.keySet());
        for (Long remainder : remainders) {
            int result = 0;
            do {

                BasicIndex indexRecord = mapper.findOneByRemainder(remainder);
                if (indexRecord == null || (indexRecord.getMaxDataSize() < (indexRecord.getDataSize()
                        + shuffleIndex.get(remainder).values().size()))) {
                    BasicIndex newIndex = newIndex(remainder, shuffleIndex.get(remainder));
                    result = isInsertable(remainder) ? mapper.insert(newIndex) : 0;
                } else {
                    try {
                        result = mapper.updateIndex(updateIndex(indexRecord, remainder, shuffleIndex.get(remainder)));
                    }  catch (NoDataUpdateException e) {
                        LOG.warn(e.getMessage(),e);
                    }
                }
            } while (result == 0);
        }

        // mapper.findByRemainder(remainders)
    }

    private boolean isInsertable(Long remainder) throws InterruptedException {

        BasicIndex indexRecord = getMapper().findOneByRemainder(remainder);
        Integer checkNum = Integer.parseInt(shardConf.getCheckNum());
        Long sleepInterval = Long.parseLong(shardConf.getCheckInterval());
        for (int i = 0; i < checkNum; i++) {
            indexRecord = getMapper().findOneByRemainder(remainder);
            Thread.sleep(sleepInterval);
            if (indexRecord != null) {
                return false;
            }
        }
        return true;
    }
    
    public String searchKey(String dataStr,String key){
        int strIndex = dataStr.indexOf(key + KEY_VALUE_SEPERATOR);
        if (strIndex != -1) {
            int end = dataStr.indexOf(DATA_SEPERATOR, strIndex + 1);
            if (end == -1) {
                end = dataStr.length();
            }
            return dataStr.substring(strIndex + 1, end);
        }
        return null;
    }
    
    public List<Long> extractData(String targetStr){
        String valueStr = targetStr.split(KEY_VALUE_SEPERATOR)[1];
        String[] indDataArr = valueStr.split(VALUE_SEPERATOR);
        List<Long> dataList = Lists.newArrayList();
        for (int i = 0; i < indDataArr.length; i++) {
            dataList.add(Long.parseLong(indDataArr[i]));
        }
        return dataList;
    }

    private Map<String, List<Long>> searchData(Map<Long, List<String>> shuffleKeys, List<BasicIndex> indexes)
            throws UnsupportedEncodingException, IOException {
        Map<String, List<Long>> extractData = Maps.newHashMap();
        for (BasicIndex index : indexes) {
            if (shuffleKeys.containsKey(index.getRemainder()) && shuffleKeys.get(index.getRemainder()) != null
                    && !shuffleKeys.get(index.getRemainder()).isEmpty()) {
                List<String> keys = shuffleKeys.get(index.getRemainder());
                String dataStr = new String(ZipUtils.unzip(index.getIndexData()), "UTF-8");
                for (String key : keys) {
                    String targetStr = searchKey(dataStr, key);
                    if (targetStr != null) {
                        extractData.put(key, extractData(targetStr));
                    }
                }
            }
        }
        return extractData;
    }

    @Override
    public Map<String, List<Long>> fetchIndexes(List<String> indexes) throws NoSuchAlgorithmException, IOException {
        Map<Long, List<String>> shuffleKeys = shuffle(new HashSet<String>(indexes));
        List<Long> remainders = Lists.newArrayList(shuffleKeys.keySet());
        Long split = split(Lists.newArrayList(shuffleKeys.keySet()));
        Map<String, List<Long>> retDatas = Maps.newHashMap();
        BasicIndexMapper mapper = getMapper();

        if (split == 0) {
            IndexQuery qo = new IndexQuery();
            qo.setRemainders(remainders);
            retDatas.putAll(searchData(shuffleKeys, mapper.findByRemainder(qo)));
        } else {
            long cursor = 0;
            Long rowCount = mapper.countIndex(remainders);
            Long limit = rowCount / split;
            IndexQuery qo = new IndexQuery();
            qo.setRemainders(remainders);
            while (cursor < rowCount) {
                qo.setOffset(cursor);
                qo.setLimit(limit);
                retDatas.putAll(searchData(shuffleKeys, mapper.findByRemainder(qo)));
                cursor += limit;
            }
        }
        return retDatas;
    }

}
