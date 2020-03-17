package org.aztec.framework.core.security;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

/**
 * 重放攻击检测器
 * @author 01390615
 *
 */
@Component
public class ReplayAttackDetector {

    @Autowired
    NonceManager nonceManager;
    
    /**
     * 判 断是否是重放攻击
     * @param timestamp
     * @param nonce
     * @param redisKey
     * @param ttl
     * @return
     */
    public boolean isReplayAttack(Long timestamp,String nonce,String redisKey,Long ttl){
        
        boolean isAttack = true;
        if(!nonceManager.isNonceExists(redisKey, nonce)){
            Long now = System.currentTimeMillis();
            if(now - timestamp < ttl){
                nonceManager.setNonce(redisKey, nonce, ttl);
                return false;
            }
        }
        return isAttack;
    }
}
