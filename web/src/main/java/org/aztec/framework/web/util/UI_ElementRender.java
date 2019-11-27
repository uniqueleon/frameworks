package org.aztec.framework.web.util;

import java.util.List;

import org.aztec.framework.api.rest.entity.RenderParamContext;
import org.aztec.framework.web.util.render.RenderParamMetaData;

public interface UI_ElementRender {

    /**
     * 通过配置文件读取
     * @param preifx
     * @return
     */
    public RenderParamContext renderByConf(String preifx);
    
    /**
     * 通过元数据来读取渲染参数.
     * @see RenderParamMetaData
     * @param metaDatas
     * @return
     */
    public RenderParamContext renderByMetaData(List<RenderParamMetaData> metaDatas);
    
    
    
    
}
