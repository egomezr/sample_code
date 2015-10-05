package org.dynamicloud.api;

import java.util.List;

/**
 * This is a class that represents a service callback.
 * It has two method success method when the executionwas OK
 * and error when any error occurs at execution moment.
 *
 * @author Eleazar Gomez
 * @version 1.0.0
 * @since 8/25/15
 **/
public interface RecordCallback<T> {
    /**
     * This method will be called when the response was ok.
     *
     * @param results results object from DynamiCloud servers
     */
    void success(RecordResults<T> results);

    /**
     * This method will be called when the response code is not ok
     *
     * @param error is a bean with a message and returned code from Dynamicloud servers.
     */
    void error(RecordError error);
}
