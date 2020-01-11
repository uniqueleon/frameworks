package org.aztec.framework.api.rest.constant;

/**
 * 定义全局状态码。采用字符串编码。优点：扩展灵活，见名思义，不容易产生冲突
 * 
 * @author 01390615
 *
 */
public interface StatusCodes {

    // 未知错误
    String UNKOWN_ERROR = "UNKOWN_ERR_001";

    // 内部服务错误
    String INTERNAL_ERROR = "INTERNAL_ERROR";

    String INNER_SERVICE_ERROR = "INNER_ERR_002";

    String NON_OPERATION = "INNER_ERR_003";

    String PARAM_KEY_ILLEGAL_ERROR= "PARAM_KEY_ILLEGAL_ERROR";

    String PARAM_VALUE_ILLEGAL_ERROR = "PARAM_VALUE_ILLEGAL_ERROR";

    String COMMON_SUCCESS = "SUCCESS";

    String COMMON_FAIL = "FAIL";
    
    String TOKEN_MISSING = "TOKEN_MISSED";

}
