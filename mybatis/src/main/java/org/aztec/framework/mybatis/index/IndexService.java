package org.aztec.framework.mybatis.index;

import java.util.List;
import java.util.Map;

public interface IndexService {

    public void addIndex(Map<String,List<Long>> indexData) throws Exception;
    public Map<String,List<Long>> fetchIndexes(List<String> indexes) throws Exception;
}
