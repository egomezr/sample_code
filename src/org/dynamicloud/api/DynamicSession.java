package org.dynamicloud.api;

import org.dynamicloud.api.model.RecordField;
import org.dynamicloud.api.model.RecordModel;
import org.dynamicloud.exception.DynamiCloudSessionException;

import java.io.File;
import java.util.List;

/**
 * This interface declare all of method that a DynamicSession implementation will have to implement.
 *
 * @author Eleazar Gomez
 * @version 1.0.0
 * @since 8/24/15
 **/
public interface DynamicSession {
    /**
     * Sets the credentials to this session
     *
     * @param credential the credentials to this session
     */
    void setRecordCredential(RecordCredential credential);

    /**
     * This method will load a record using rid and will instantiate an object with attributes bound to Model's fields.
     *
     * @param rid        record id
     * @param boundClass bound class to model's fields.
     * @return BoundInstance object related to boundClass
     */
    BoundInstance loadRecord(Long rid, RecordModel model, Class boundClass);

    /**
     * This method will call an update operation in DynamiCloud servers
     * using model and BoundInstance object
     *
     * @param model    record model
     * @param instance bound instance object
     * @throws DynamiCloudSessionException if something wrong occurred
     */
    void updateRecord(RecordModel model, BoundInstance instance) throws DynamiCloudSessionException;

    /**
     * This method will call a save operation in DynamiCloud servers
     * using model and BoundInstance object
     *
     * @param model    record model
     * @param instance bound instance object
     * @throws DynamiCloudSessionException if something wrong occurred
     */
    void saveRecord(RecordModel model, BoundInstance instance) throws DynamiCloudSessionException;

    /**
     * This method will call a delete operation in DynamiCloud servers
     * using model and Record id
     *
     * @param model record model
     * @param rid   record id
     * @throws DynamiCloudSessionException if something wrong occurred
     */
    void deleteRecord(RecordModel model, Long rid) throws DynamiCloudSessionException;

    /**
     * Will create a RecordQuery and sets to this session
     *
     * @param recordModel model to use to execute operations
     * @return this RecordQuery instance
     */
    Query createQuery(RecordModel recordModel);

    /**
     * Gets model record information from DynamiCloud servers.
     *
     * @param modelId model id in DynamiClod servers
     * @return RecordModel object
     * @throws DynamiCloudSessionException if any error occurs
     */
    RecordModel loadModel(Long modelId) throws DynamiCloudSessionException;

    /**
     * Loads all models related to CSK and ACI keys in DynamiCloud servers
     *
     * @return list of models
     * @throws DynamiCloudSessionException if any error occurs
     */
    List<RecordModel> loadModels() throws DynamiCloudSessionException;

    /**
     * Loads all model's fields according ModelID
     *
     * @param mid modelID
     * @return list of model's fields.
     * @throws DynamiCloudSessionException
     */
    List<RecordField> loadFields(Long mid) throws DynamiCloudSessionException;

    /**
     * Uploads a file in record <b>rid</b>
     *
     * @param modelId       owner model id of this record <b>rid<b/>
     * @param recordId      record id
     * @param fieldName     fieldName target
     * @param file          file to upload
     * @param contentType   contentType of this file
     * @param preferredName preferred name to later downloads
     */
    void uploadFile(Long modelId, Long recordId, String fieldName, File file, String contentType, String preferredName)
            throws DynamiCloudSessionException;

    /**
     * This method will make a request to generate a link to download the file related to this recordId and fieldName
     *
     * @param modelId   model id
     * @param recordId  record id
     * @param fieldName field name
     * @return link to download file
     * @throws DynamiCloudSessionException if any error occurs
     */
    String shareFile(Long modelId, Long recordId, String fieldName) throws DynamiCloudSessionException;

    /**
     * Downloads a file according <b>rid</b> and <b>fieldName</b>
     *
     * @param modelId   owner model id of this record <b>rid<b/>
     * @param recordId  record id
     * @param fieldName fieldName target
     * @param destiny   destiny file
     * @throws DynamiCloudSessionException if any error occurs
     */
    void downloadFile(Long modelId, Long recordId, String fieldName, File destiny) throws DynamiCloudSessionException;

    /**
     * Set the bound instance to get the fields and values that will used to update records
     *
     * @param boundInstance bondInstance with values
     * @return this instance of DynamicSession
     * @throws DynamiCloudSessionException if any error occurs
     */
    DynamicSession setBoundInstance(BoundInstance boundInstance) throws DynamiCloudSessionException;

    /**
     * Executes an update using query as a selection and boundInstance with values
     *
     * @param query selection
     * @throws DynamiCloudSessionException if any error occurs
     */
    void update(Query query) throws DynamiCloudSessionException;

    /**
     * Executes a delete using query as a selection
     *
     * @param query selection
     * @throws DynamiCloudSessionException if any error occurs
     */
    void delete(Query query) throws DynamiCloudSessionException;


    class Impl {
        public static DynamicSession getInstance(RecordCredential credential) {
            DynamicSession session = new DynamicSessionImpl();
            session.setRecordCredential(credential);

            return session;
        }
    }
}