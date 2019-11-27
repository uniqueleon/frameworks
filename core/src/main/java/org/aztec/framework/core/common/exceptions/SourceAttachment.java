package org.aztec.framework.core.common.exceptions;

import java.util.List;

/**
 * Exception has source attachment
 */
public interface SourceAttachment {

    String getSourceFile();
    List<String> getSource();
    Integer getLineNumber();
}
