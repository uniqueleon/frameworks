package org.aztec.framework.core.common.exceptions;

/**
 * 
 * @author KingsHunter
 * @createDate May 30th,2016
 *
 */
public class FastRuntimeException extends RuntimeException {

    private static final long serialVersionUID = 3971320345280707623L;

    public FastRuntimeException() {
        super();
    }

    public FastRuntimeException(String desc) {
        super(desc);
    }

    public FastRuntimeException(String desc, Throwable cause) {
        super(desc, cause);
    }

    public FastRuntimeException(Throwable cause) {
        super(cause);
    }

    /**
     * Since we override this method, no stacktrace is generated - much faster
     * 
     * @return always null
     */
    public Throwable fillInStackTrace() {
        return null;
    }

}
