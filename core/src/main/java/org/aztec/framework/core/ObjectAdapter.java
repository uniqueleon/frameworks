package org.aztec.framework.core;

public interface ObjectAdapter {

    public <T> T cast(Class<? extends T> clazz);
}
