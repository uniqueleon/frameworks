package org.aztec.framework.api.rest.entity;

import java.util.List;
import java.util.Map;

import com.google.common.collect.Maps;

/**
 * 页面渲染的参数
 * 
 * @author tansonlam
 * @create 2016年12月30日
 * 
 */
public class RenderParamContext {

    private Map<String, List<RenderParam>> renderParams = Maps.newHashMap();

    /**
     * 添加页面参数
     * 
     * @param key
     * @param renderParamList
     */
    public void addParam(String key, List<RenderParam> renderParamList) {
        renderParams.put(key, renderParamList);
    }

    public Map<String, List<RenderParam>> getRenderParams() {
        return renderParams;
    }

    public void setRenderParams(Map<String, List<RenderParam>> renderParams) {
        this.renderParams = renderParams;
    }

}
