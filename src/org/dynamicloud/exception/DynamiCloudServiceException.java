package org.dynamicloud.exception;

import org.dynamicloud.net.http.HttpStatus;

/**
 * This is a Service exception to indicate a specific situation.
 *
 * @author Eleazar Gomez
 * @version 1.0.0
 * @since 2015-08-22
 *
 **/
public class DynamiCloudServiceException extends Exception {
    private HttpStatus status;
    private int lineNumber;

    /**
     * Default super constructor
     *
     * @param s a description of the occurred error.
     */
    public DynamiCloudServiceException(String s) {
        super(s);
    }

    /**
     * Default constructor
     *
     * @param status http status response
     * @param s a description of the occurred error.
     */
    public DynamiCloudServiceException(HttpStatus status, String s) {
        super(s);
        this.status = status;
        this.lineNumber = Thread.currentThread().getStackTrace()[2].getLineNumber();
    }

    @Override
    public String getMessage() {
        return "\n{\n" +
                "\tstatus=" + (status == null ? "N/A" : status) + ", \n" +
                "\terror=" + super.getMessage() + ", \n" +
                "\tline number = " + lineNumber +
                "\n}";
    }
}