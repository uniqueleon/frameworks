package org.aztec.framework.api.rest.entity;

import java.util.List;

import com.google.common.collect.Lists;

/**
 * 分页统一处理
 * 
 * @author tansonlam
 * @create 2016年12月30日
 * 
 */
public class PageUtil {

    public static final int DEFAULT_PAGE_SIZE = 15;

    /**
     * 获取 offset
     * 
     * @param page
     * @param pageSize
     * @return
     */
    public static int getOffset(Integer page, Integer pageSize) {
        int offset = (page == null || page < 1) ? 0 : (page - 1)
                * getLimit(page, pageSize);
        return offset;
    }

    /**
     * 获取limit
     * 
     * @param page
     * @param pageSize
     * @return
     */
    public static int getLimit(Integer page, Integer pageSize) {
        return pageSize == null || pageSize == 0 ? DEFAULT_PAGE_SIZE : pageSize;
    }

    /**
     * 根据最大的限制数limitSize计算pageSize.
     * 
     * @param limitSize
     *            最大的限制数limitSize
     * @param page
     *            当前页
     * @param pageSize
     *            每页数据大小
     * @return
     */
    public static int getLimitPageSize(Integer limitSize, Integer page,
            Integer pageSize) {
        if (limitSize == null)
            throw new IllegalArgumentException("limitSize should not be null");
        if (page == null || page < 0)
            throw new IllegalArgumentException(
                    "page should not be null or illegal value");
        if (pageSize == null || pageSize <= 0)
            pageSize = DEFAULT_PAGE_SIZE;
        int pageStart = (page - 1) * pageSize;
        int total = page * pageSize;
        if (total > limitSize)
            return limitSize - pageStart > 0 ? limitSize - pageStart : 0;
        else
            return pageSize;
    }

    /**
     * 对数组进行分页切割
     * 
     * @param list
     *            数组
     * @param page
     *            页码
     * @param pageSize
     *            每页数据大小
     * @return
     */
    public static <T> List<T> getPageList(List<T> list, Integer page,
            Integer pageSize) {
        if (list == null || list.isEmpty())
            return Lists.newArrayList();
        if (page == null || page <= 0)
            page = 1;
        if (pageSize == null || pageSize <= 0)
            pageSize = 20;
        Integer total = list.size();
        Integer fromIndex = (page - 1) * pageSize;
        Integer toIndex = fromIndex + pageSize;
        if (fromIndex > total)
            fromIndex = total;
        if (toIndex > total)
            toIndex = total;
        return list.subList(fromIndex, toIndex);
    }
}
