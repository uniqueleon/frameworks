package org.aztec.framework.api.rest.constant;

public interface WebsocketConstant {

    public static final String DEFAULT_BROADCAST_TOPIC_SUFFIX = "/system/broadcast";
    public static final String DEFAULT_SECRET_TOPIC_SUFFIX = "/system/secret";
    
    public static final String SYSTEM_SOURCE_ID = "SYSTEM";
    public static final String SYSTEM_SOURCE_INFO = "{\"name\":\"system\"}";
    
    public static interface ErrorCodes{
        String PREFIX = "WS_ERR_";
        String SOURCE_IS_NULL = PREFIX + "005";
        String TOPIC_IS_NULL = PREFIX + "001";
        String CONTENT_IS_NULL = PREFIX + "002";
        String TITLE_IS_NULL = PREFIX + "003";
        String MESSAGE_TYPE_IS_NULL = PREFIX + "004";
        String DUPLICATE_MSG = PREFIX + "006";
    }
}
