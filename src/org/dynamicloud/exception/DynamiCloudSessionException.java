package org.dynamicloud.exception;

/**
 * @author Eleazar Gomez
 * @version 1.0.0
 * @since 8/24/15
 **/
public class DynamiCloudSessionException extends Exception {
    private int lineNumber;

    /**
     * Default constructor
     *
     * @param s a description of the occurred error.
     */
    public DynamiCloudSessionException(String s) {
        super(s);
        this.lineNumber = Thread.currentThread().getStackTrace()[2].getLineNumber();
    }

    @Override
    public String getMessage() {
        return "\n{\n" +
                "\terror=" + super.getMessage() + ", \n" +
                "\tline number = " + lineNumber +
                "\n}";
    }
}