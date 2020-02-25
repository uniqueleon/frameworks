package org.aztec.framework.mybatis.index;

import java.util.List;

public class IndexQuery {

    private List<Long> remainders;
    private Long offset;
    private Long limit;
    public List<Long> getRemainders() {
        return remainders;
    }
    public void setRemainders(List<Long> remainders) {
        this.remainders = remainders;
    }
    public Long getOffset() {
        return offset;
    }
    public void setOffset(Long offset) {
        this.offset = offset;
    }
    public Long getLimit() {
        return limit;
    }
    public void setLimit(Long limit) {
        this.limit = limit;
    }
    
    
}
