package org.dynamicloud.api;

import org.dynamicloud.api.criteria.Condition;
import org.dynamicloud.api.model.RecordModel;
import org.dynamicloud.exception.DynamiCloudSessionException;

import java.util.List;

/**
 * This interface declares the accessible methods to execute operations.
 *
 * @author Eleazar Gomez
 * @version 1.0.0
 * @since 8/29/15
 **/
public interface Query<T> {

    /**
     * Apply a desc ordering to the current order by object
     * An IllegalStateException will be thrown if orderBy object is null
     *
     * @return this instance of Query
     */
    Query desc();

    /**
     * Apply a asc ordering to the current order by object
     * An IllegalStateException will be thrown if orderBy object is null
     *
     * @return this instance of Query
     */
    Query asc();

    /**
     * This method will add a new condition to a AND list of conditions.
     *
     * @param condition new condition to a list of conditions to use
     * @return this instance of Query
     */
    Query add(Condition condition);

    /**
     * This method sets the projection to use in this query.  The query execution will return those projection.
     * If projection == null then, this query will returns all model's projection.
     *
     * @param projection projection in this query
     * @return this instance of Query
     */
    Query addProjection(String[] projection);

    /**
     * This method sets the projection to use in this query
     * Additionally, the projection could be math operations as well: COUNT, MAX, MIN, SUM, AVG, etc.
     *
     * @param projection projection in this query
     * @return this instance of Query
     */
    Query addProjection(String projection);

    /**
     * Sets an offset to this query to indicates the page of a big data result.
     *
     * @param offset new offset
     * @return this instance of Query
     */
    Query setOffset(int offset);

    /**
     * Sets how many items per page (offset) this query will fetch
     *
     * @param count how many items
     * @return this instance of Query
     */
    Query setCount(int count);

    /**
     * This method will execute a query and returns a list of records
     *
     * @param callback this a callback to use at moment the errors or success
     * @throws DynamiCloudSessionException if any error occurs.
     */
    void list(RecordCallback<T> callback) throws DynamiCloudSessionException;

    /**
     * This method will execute a query and returns a list of records
     *
     * @throws DynamiCloudSessionException if any error occurs.
     */
    RecordResults list() throws DynamiCloudSessionException;

    /**
     * This method adds an order by condition.  The condition will have an asc ordering by default.
     *
     * @param attribute attribute by the query will be ordered.
     * @return this instance of Query
     */
    Query orderBy(String attribute);

    /**
     * This method create a groupBy condition using attribute
     *
     * @param attribute attribute by this query will group.
     * @return this instance of Query
     */
    Query groupBy(String attribute);

    /**
     * get the current conditions
     *
     * @return the conditions
     */
    List<Condition> getConditions();

    /**
     * This method create a groupBy condition using attributes
     *
     * @param attributes attribute by this query will group.
     * @return this instance of Query
     */
    Query groupBy(String[] attributes);

    /**
     * Gets the current offset so far.  This attribute will increase according calls of method next(RecordCallback<T> callback)
     *
     * @return int of current offset
     */
    int getCurrentOffSet();

    /**
     * Will execute a list operation with an offset += count and will use the same callback object in list method.
     * If list() method without callback was called, then this method will return a RecordResults object otherwise null
     *
     * @throws DynamiCloudSessionException if any error occurs.
     */
    RecordResults next() throws DynamiCloudSessionException;

    /**
     * Returns the current RecordModel associated to this query
     * @return RecordModel
     */
    RecordModel getModel();
}