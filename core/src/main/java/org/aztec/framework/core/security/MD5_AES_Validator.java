package org.aztec.framework.core.security;

import java.io.UnsupportedEncodingException;

import org.apache.commons.lang.RandomStringUtils;
import org.aztec.framework.core.utils.CodecUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

/**
 * MD5 + AES 签名校验器。签名过程为
 * <li>1) 报文内容进行MD5 数字签名</li>
 * <li>2) 将数字签名内容转换为BASE 64</li>
 * <li>3) 使用密钥加密</li>
 * <li>4) 将密文再转换成BASE 64</li>
 * 校验过程:
 * 
 * <li>1) 将BASE 64还原成AES密文</li>
 * <li>2) 解密AES密文</li>
 * <li>3) 将报文内容进行MD5签名并转为BASE 64</li>
 * <li>4) 比对签名内容</li>
 * @author liming
 *
 */
@Component
public class MD5_AES_Validator implements SignatureValidator {
    
    private static Logger LOG = LoggerFactory.getLogger(MD5_AES_Validator.class);
    
    @Autowired
    private AccessTokenManager tokenManager;

    @Override
    public int getSignatureType() {
        return 1;
    }
    

    @Override
    public boolean isValidate(String appKey,String appSecret,String content, String signature, Object... args) throws UnsupportedEncodingException {
        // TODO Auto-generated method stub
        if(args.length == 2){
            String signContent = wrapSignBody(appKey, (String)args[0], (Long)args[1]);

            byte[] byteCode = CodecUtils.decodeBASE64Binary(signature);
            String decryptContent = CodecUtils.decryptAES(byteCode, appSecret);
            String md5Digest = CodecUtils.hexMD5(signContent);
            if(decryptContent.equals(md5Digest)){
                return true;
            }
        }
        return false;
    }
    

    private String wrapSignBody(String appID,String nonce,Long timestamp){

        AccessToken accessToken = tokenManager.getToken(appID);
        if(accessToken ==  null){
            return null;
        }
        return accessToken.getAsString() + "_" + nonce + "_" + timestamp;
    }
    

    public static void main(String[] args) {
        try {
            //String testContent= "{\"param\":{\"account\":\"liming6\"},\"optType\":\"QUERY\"}";

            String testContent= "{\"param\":\"ok\"}";
            String key =  "yGTLRoKObbVKAQyGwSjRqqCDrqAZfSmd";
            String md5Str = CodecUtils.hexMD5(testContent);
            String encryptContent = CodecUtils.byteToHexString(CodecUtils.encryptAES(md5Str, key));
            System.out.println("signature:" + encryptContent);
            System.out.println(RandomStringUtils.randomAlphabetic(32));
        } catch (Exception e) {
            // TODO Auto-generated catch block
            e.printStackTrace();
        }
        
    }

}
