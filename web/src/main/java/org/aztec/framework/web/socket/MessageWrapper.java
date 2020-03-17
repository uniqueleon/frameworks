package org.aztec.framework.web.socket;

import org.aztec.framework.api.rest.entity.WSMessageDTO;

public interface MessageWrapper {

    public String wrap(WSMessageDTO message);
}
