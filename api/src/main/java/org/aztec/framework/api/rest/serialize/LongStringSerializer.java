package org.aztec.framework.api.rest.serialize;

import java.io.IOException;

import com.fasterxml.jackson.core.JsonGenerator;
import com.fasterxml.jackson.databind.JsonSerializer;
import com.fasterxml.jackson.databind.SerializerProvider;

public class LongStringSerializer extends JsonSerializer<Long> {

    @Override
    public void serialize(Long arg0, JsonGenerator arg1, SerializerProvider arg2) throws IOException {
        // TODO Auto-generated method stub
        arg1.writeString("" + arg0);
    }

}
