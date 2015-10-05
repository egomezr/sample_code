package org.dynamicloud.api;

import org.dynamicloud.api.criteria.*;
import org.dynamicloud.api.model.RecordModel;
import org.dynamicloud.exception.DynamiCloudSessionException;
import org.dynamicloud.service.ServiceCallback;
import org.dynamicloud.service.ServiceCaller;
import org.dynamicloud.service.ServiceError;
import org.dynamicloud.service.ServiceResponse;
import org.dynamicloud.util.ConfigurationProperties;

import java.net.URLEncoder;
import java.util.HashMap;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;

/**
 * This is a mutable class to build a query
 * This class implements Query to public the needed methods to execute operations
 *
 * @author Eleazar Gomez
 * @version 1.0.0
 * @since 8/22/15
 **/
public class RecordQuery<T> implements Query<T> {
    public static final int DEFAULT_COUNT = 15;
    private RecordModel recordModel;
    private RecordCredential credentials;
    private OrderByClause orderBy;
    private GroupByClause groupBy;
    private String[] projection;
    private int offset;
    private int count;
    private RecordCallback<T> currentCallback;
    private boolean listWasCalled = false;

    private List<Condition> conditions = new LinkedList<>();

    public RecordQuery(RecordModel recordModel) {
        this.recordModel = recordModel;
    }

    /**
     * This method will add a new condition to a AND list of conditions.
     *
     * @param condition new condition to a list of conditions to use
     * @return this instance of Query
     */
    public RecordQuery add(Condition condition) {
        conditions.add(condition);

        return this;
    }

    /**
     * This method sets the projection to use in this query.  The query execution will return those projection.
     * If projection == null then, this query will returns all model's projection.
     *
     * @param projection projection in this query
     * @return this instance of Query
     */
    public Query addProjection(String[] projection) {
        this.projection = projection;

        return this;
    }

    /**
     * This method sets the projection to use in this query
     * The projection could be math operations like:COUNT, MAX, MIN, SUM, AVG, etc.
     *
     * @param projection projection in this query
     * @return this instance of Query
     */
    @Override
    public Query addProjection(String projection) {
        return addProjection(new String[]{projection});
    }

    /**
     * This method adds an order by condition.  The condition will have an asc ordering by default.
     *
     * @param attribute attribute by the query will be ordered.
     * @return this instance of Query
     */
    public Query orderBy(String attribute) {
        orderBy = OrderByClause.asc(attribute);
        return this;
    }

    /**
     * Apply a desc ordering to the current order by object
     * An IllegalStateException will be thrown if orderBy object is null
     *
     * @return this instance of Query
     */
    public RecordQuery desc() {
        if (orderBy == null) {
            throw new IllegalStateException("You must call orderBy method before call this method");
        }

        orderBy.setAsc(false);

        return this;
    }

    /**
     * Apply a asc ordering to the current order by object
     * An IllegalStateException will be thrown if orderBy object is null
     *
     * @return this instance of Query
     */
    public RecordQuery asc() {
        if (orderBy == null) {
            throw new IllegalStateException("You must call orderBy method before call this method");
        }

        orderBy.setAsc(true);

        return this;
    }

    /**
     * This method create a groupBy condition using projection
     *
     * @param attributes attribute by this query will group.
     * @return this instance of Query
     */
    public Query groupBy(String[] attributes) {
        setGroupBy(attributes);

        return this;
    }

    /**
     * Gets the current offset so far.  This attribute will increase according calls of method next()
     *
     * @return int of current offset
     */
    @Override
    public int getCurrentOffSet() {
        return getOffset();
    }

    /**
     * Will execute a list operation with an offset += count and will use the same callback object in list method.
     * If list() method without callback was called, then this method will return a RecordResults object otherwise null
     *
     * @throws DynamiCloudSessionException if any error occurs.
     */
    @Override
    public RecordResults next() throws DynamiCloudSessionException {
        if (!listWasCalled) {
            throw new IllegalStateException("You have to call list(callback) method first.");
        }

        offset = getOffset() + getCount();

        if (currentCallback == null) {
            return list();
        }

        list(currentCallback);

        return null;
    }

    @Override
    public RecordModel getModel() {
        return this.recordModel;
    }

    /**
     * This method create a groupBy condition using attribute
     *
     * @param attribute attribute by this query will group.
     * @return this instance of Query
     */
    public Query groupBy(String attribute) {
        setGroupBy(new String[]{attribute});

        return this;
    }

    /**
     * get the current conditions
     *
     * @return the conditions
     */
    public List<Condition> getConditions() {
        return conditions;
    }

    /**
     * get the current orderBy condition
     *
     * @return the order by condition
     */
    public OrderByClause getOrderBy() {
        return orderBy;
    }

    /**
     * get the current groupBy condition
     *
     * @return the group by condition
     */
    public GroupByClause getGroupBy() {
        return groupBy;
    }

    /**
     * This method create a groupBy condition using projection
     *
     * @param attributes projection by this query will group.
     */
    private void setGroupBy(String[] attributes) {
        groupBy = new GroupByClause(attributes);
    }

    /**
     * Gets the current offset
     * If offset == 0 then will return default offset (0)
     *
     * @return the current offset
     */
    protected int getOffset() {
        return offset < 0 ? 0 : offset;
    }

    /**
     * Sets an offset to this query to indicates the page of a big data result.
     *
     * @param offset new offset
     * @return this instance of Query
     */
    public Query setOffset(int offset) {
        this.offset = offset;

        return this;
    }

    /**
     * Gets the current count
     * If count == 0 then will return default count (DEFAULT_COUNT)
     *
     * @return the current count
     */
    protected int getCount() {
        return count == 0 ? DEFAULT_COUNT : count;
    }

    /**
     * Sets how many items per page (offset) this query will fetch
     *
     * @param count how many items
     * @return this instance of Query
     */
    public Query setCount(int count) {
        this.count = count;

        return this;
    }

    /**
     * Sets the credentials to use
     *
     * @param credentials credentials to execute operations.
     */
    protected void setCredentials(RecordCredential credentials) {
        this.credentials = credentials;
    }

    /**
     * This method will execute a query and returns a list of records
     *
     * @param callback this a callback to use at moment the errors or success
     * @throws DynamiCloudSessionException if any error occurs.
     */
    @SuppressWarnings("unchecked")
    public void list(final RecordCallback<T> callback) throws DynamiCloudSessionException {

        String criteria = DynamiCloudUtil.buildString(getConditions(), getGroupBy(), getOrderBy(),
                DynamiCloudUtil.buildProjection(this.projection));

        String urlGetRecords;
        if (projection == null) {
            urlGetRecords = ConfigurationProperties.getInstance().getProperty("url.get.records");
        } else {
            urlGetRecords = ConfigurationProperties.getInstance().getProperty("url.get.specific.fields");
        }

        String url = ConfigurationProperties.getInstance().getProperty("url");

        try {
            urlGetRecords = url + urlGetRecords.replaceAll("\\{csk}", URLEncoder.encode(credentials.getCsk(), "UTF-8")).
                    replaceAll("\\{aci}", URLEncoder.encode(credentials.getAci(), "UTF-8"));
            urlGetRecords = urlGetRecords.replaceAll("\\{mid}", recordModel.getId().toString());
            urlGetRecords = urlGetRecords.replaceAll("\\{count}", String.valueOf(this.getCount()));
            urlGetRecords = urlGetRecords.replaceAll("\\{offset}", String.valueOf(this.getOffset()));

            Map<String, String> params = new HashMap<>();
            params.put("criteria", criteria);
            ServiceCaller.Impl.getInstance().callService(urlGetRecords, params, new ServiceCallback() {

                /**
                 * This method will be called when the response code is ok
                 *
                 * @param response response from Dynamicloud servers
                 */
                @Override
                public void success(ServiceResponse response) {
                    callback.success(DynamiCloudUtil.buildRecordResults(response, recordModel.getBoundClass()));
                }

                /**
                 * This method will be called when the response code is ok
                 *
                 * @param error is a bean with a message and code returned from Dynamicloud servers.
                 */
                @Override
                public void error(ServiceError error) {
                    callback.error(new RecordError(error.getMessage(), error.getCode()));
                }
            });

            currentCallback = callback;
            listWasCalled = true;
        } catch (Exception e) {
            throw new DynamiCloudSessionException(e.getMessage());
        }
    }

    /**
     * This method will execute a query and returns a list of records
     *
     * @throws DynamiCloudSessionException if any error occurs.
     */
    @SuppressWarnings("unchecked")
    public RecordResults list() throws DynamiCloudSessionException {

        String criteria = DynamiCloudUtil.buildString(getConditions(), getGroupBy(), getOrderBy(),
                DynamiCloudUtil.buildProjection(this.projection));

        String urlGetRecords;
        if (projection == null) {
            urlGetRecords = ConfigurationProperties.getInstance().getProperty("url.get.records");
        } else {
            urlGetRecords = ConfigurationProperties.getInstance().getProperty("url.get.specific.fields");
        }

        String url = ConfigurationProperties.getInstance().getProperty("url");

        try {
            urlGetRecords = url + urlGetRecords.replaceAll("\\{csk}", URLEncoder.encode(credentials.getCsk(), "UTF-8")).
                    replaceAll("\\{aci}", URLEncoder.encode(credentials.getAci(), "UTF-8"));
            urlGetRecords = urlGetRecords.replaceAll("\\{mid}", recordModel.getId().toString());
            urlGetRecords = urlGetRecords.replaceAll("\\{count}", String.valueOf(this.getCount()));
            urlGetRecords = urlGetRecords.replaceAll("\\{offset}", String.valueOf(this.getOffset()));

            Map<String, String> params = new HashMap<>();
            params.put("criteria", criteria);
            ServiceResponse serviceResponse = ServiceCaller.Impl.getInstance().callService(urlGetRecords, params);

            listWasCalled = true;

            return DynamiCloudUtil.buildRecordResults(serviceResponse, recordModel.getBoundClass());
        } catch (Exception e) {
            throw new DynamiCloudSessionException(e.getMessage());
        }
    }



}