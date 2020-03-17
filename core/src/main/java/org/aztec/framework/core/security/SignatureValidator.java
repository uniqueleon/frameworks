package org.aztec.framework.core.security;

/**
 * 签名校验器
 * @author liming
 *
 */
public interface SignatureValidator {

    /**
     * 获取签名类型。1.AES+MD5
     * @return
     */
    public int getSignatureType();
    /**
     * 是否合法签名
     * @param appKey
     * @param appSecret
     * @param content
     * @param signature
     * @param args
     * @return
     * @throws Exception
     */
    public boolean isValidate(String appKey,String appSecret,String content,String signature,Object... args) throws Exception;
}
