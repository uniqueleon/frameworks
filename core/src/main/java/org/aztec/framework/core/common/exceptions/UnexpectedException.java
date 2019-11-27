package org.aztec.framework.core.common.exceptions;

/**
 * An unexpected exception
 */
public class UnexpectedException extends SystemException {

    private static final long serialVersionUID = 101798964204303310L;

    public UnexpectedException(String message) {
        super(message);
    }

    public UnexpectedException(Throwable exception) {
        super("Unexpected Error", exception);
    }

    public UnexpectedException(String message, Throwable cause) {
        super(message, cause);
    }

    public String getErrorTitle() {
        if (getCause() == null) {
            return "Unexpected error";
        }
        return String.format("Oops: %s", getCause().getClass().getSimpleName());
    }

    public String getErrorDescription() {
        if (getCause() != null && getCause().getClass() != null) {
            return String.format("An unexpected error occured caused by exception %s\n%s",
                    getCause().getClass().getSimpleName(), getCause().getMessage());
        } else {
            return String.format("Unexpected error : %s", getMessage());
        }
    }

}
