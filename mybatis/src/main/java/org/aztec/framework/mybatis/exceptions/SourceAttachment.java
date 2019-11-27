package org.aztec.framework.mybatis.exceptions;

import java.util.List;

/**
 * Exception has source attachment
 */
public interface SourceAttachment {

    String getSourceFile();
    List<String> getSource();
    Integer getLineNumber();
}
