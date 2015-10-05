package org.dynamicloud.api;

import org.dynamicloud.api.model.RecordField;
import org.dynamicloud.api.model.RecordFieldType;
import org.dynamicloud.api.model.RecordModel;
import org.dynamicloud.exception.DynamiCloudSessionException;
import org.dynamicloud.logger.LoggerTool;
import org.dynamicloud.net.http.HttpMethod;
import org.dynamicloud.service.ServiceCaller;
import org.dynamicloud.service.ServiceResponse;
import org.dynamicloud.util.ConfigurationProperties;
import org.json.JSONArray;
import org.json.JSONObject;

import java.io.File;
import java.net.URLEncoder;
import java.util.*;

/**
 * This class implements DynamicSession to execute CRUD operations.
 *
 * @author Eleazar Gomez
 * @version 1.0.0
 * @since 8/24/15
 **/
public class DynamicSessionImpl implements DynamicSession {
    private static final LoggerTool log = LoggerTool.getLogger(DynamicSession.class);
    private BoundInstance boundInstance;
    private RecordCredential credentials;

    /**
     * Builds a session
     */
    public DynamicSessionImpl() {
    }

    /**
     * Sets the credentials to this session
     *
     * @param credential the credentials to this session
     */
    @Override
    public void setRecordCredential(RecordCredential credential) {
        this.credentials = credential;
    }

    /**
     * This method will load a record using rid and will instantiate an object with attributes bound to Model's fields.
     *
     * @param rid        record id
     * @param boundClass bound class to model's fields.
     * @return BoundInstance object related to boundClass
     */
    @Override
    public BoundInstance loadRecord(Long rid, RecordModel model, Class boundClass) {
        String url = ConfigurationProperties.getInstance().getProperty("url");
        String urlGetRecords = ConfigurationProperties.getInstance().getProperty("url.get.record.info");

        try {
            urlGetRecords = url + urlGetRecords.replaceAll("\\{csk}", URLEncoder.encode(credentials.getCsk(), "UTF-8")).
                    replaceAll("\\{aci}", URLEncoder.encode(credentials.getAci(), "UTF-8"));
            urlGetRecords = urlGetRecords.replaceAll("\\{mid}", model.getId().toString());
            urlGetRecords = urlGetRecords.replaceAll("\\{rid}", rid.toString());

            ServiceResponse serviceResponse = ServiceCaller.Impl.getInstance().callService(urlGetRecords);

            JSONObject json = new JSONObject(serviceResponse.getResponse());
            Object r = boundClass.newInstance();

            DynamiCloudUtil.setData2record(r, json.getJSONObject("record"));

            return (BoundInstance) r;
        } catch (Exception e) {
            log.warn(e.getMessage());
        }

        return null;
    }

    /**
     * This method will call an update operation in DynamiCloud servers
     * using model and BoundInstance object
     *
     * @param model    record model
     * @param instance bound instance object
     * @throws DynamiCloudSessionException if something wrong occurred
     */
    @Override
    public void updateRecord(RecordModel model, BoundInstance instance) throws DynamiCloudSessionException {
        String url = ConfigurationProperties.getInstance().getProperty("url");
        String urlGetRecords = ConfigurationProperties.getInstance().getProperty("url.update.record");

        try {
            urlGetRecords = url + urlGetRecords.replaceAll("\\{csk}", URLEncoder.encode(credentials.getCsk(), "UTF-8")).
                    replaceAll("\\{aci}", URLEncoder.encode(credentials.getAci(), "UTF-8"));
            urlGetRecords = urlGetRecords.replaceAll("\\{mid}", model.getId().toString());
            urlGetRecords = urlGetRecords.replaceAll("\\{rid}", instance.getRecordId().toString());

            String fields = DynamiCloudUtil.buildFieldsJSON(instance);

            Map<String, String> params = new HashMap<>();
            params.put("fields", fields);

            ServiceResponse serviceResponse = ServiceCaller.Impl.getInstance().callService(urlGetRecords, params);

            JSONObject json = new JSONObject(serviceResponse.getResponse());
            if (json.getLong("status") != 200) {
                throw new RuntimeException(json.getString("message"));
            }
        } catch (Exception e) {
            throw new DynamiCloudSessionException(e.getMessage());
        }
    }

    /**
     * This method will call a save operation in DynamiCloud servers
     * using model and BoundInstance object
     *
     * @param model    record model
     * @param instance bound instance object
     * @throws DynamiCloudSessionException if something wrong occurred
     */
    public void saveRecord(RecordModel model, BoundInstance instance) throws DynamiCloudSessionException {
        String url = ConfigurationProperties.getInstance().getProperty("url");
        String urlGetRecords = ConfigurationProperties.getInstance().getProperty("url.save.record");

        try {
            urlGetRecords = url + urlGetRecords.replaceAll("\\{csk}", URLEncoder.encode(credentials.getCsk(), "UTF-8")).
                    replaceAll("\\{aci}", URLEncoder.encode(credentials.getAci(), "UTF-8"));
            urlGetRecords = urlGetRecords.replaceAll("\\{mid}", model.getId().toString());

            String fields = DynamiCloudUtil.buildFieldsJSON(instance);

            Map<String, String> params = new HashMap<>();
            params.put("fields", fields);

            ServiceResponse serviceResponse = ServiceCaller.Impl.getInstance().callService(urlGetRecords, params);

            JSONObject json = new JSONObject(serviceResponse.getResponse());
            if (json.getLong("status") != 200) {
                throw new RuntimeException(json.getString("message"));
            }

            long rid = json.getLong("rid");
            instance.setRecordId(rid);
        } catch (Exception e) {
            throw new DynamiCloudSessionException(e.getMessage());
        }
    }

    /**
     * This method will call a delete operation in DynamiCloud servers
     * using model and Record id
     *
     * @param model record model
     * @param rid   record id
     * @throws DynamiCloudSessionException if something wrong occurred
     */
    public void deleteRecord(RecordModel model, Long rid) throws DynamiCloudSessionException {
        String url = ConfigurationProperties.getInstance().getProperty("url");
        String urlGetRecords = ConfigurationProperties.getInstance().getProperty("url.delete.record");

        try {
            urlGetRecords = url + urlGetRecords.replaceAll("\\{csk}", URLEncoder.encode(credentials.getCsk(), "UTF-8")).
                    replaceAll("\\{aci}", URLEncoder.encode(credentials.getAci(), "UTF-8"));
            urlGetRecords = urlGetRecords.replaceAll("\\{mid}", model.getId().toString());
            urlGetRecords = urlGetRecords.replaceAll("\\{rid}", rid.toString());

            ServiceResponse serviceResponse = ServiceCaller.Impl.getInstance().callService(urlGetRecords, null,
                    HttpMethod.DELETE);

            JSONObject json = new JSONObject(serviceResponse.getResponse());
            if (json.getLong("status") != 200) {
                throw new RuntimeException(json.getString("message"));
            }
        } catch (Exception e) {
            throw new DynamiCloudSessionException(e.getMessage());
        }
    }

    /**
     * Will create a RecordQuery and sets to this session
     *
     * @param recordModel model to use to execute operations
     * @return this RecordQuery instance
     */
    @Override
    public Query createQuery(RecordModel recordModel) {
        RecordQuery recordQuery = new RecordQuery<>(recordModel);
        recordQuery.setCredentials(credentials);

        return recordQuery;
    }

    /**
     * Gets model record information from DynamiCloud servers.
     *
     * @param modelId model id in DynamiClod servers
     * @return RecordModel object
     * @throws DynamiCloudSessionException if any error occurs
     */
    @Override
    public RecordModel loadModel(Long modelId) throws DynamiCloudSessionException {
        String url = ConfigurationProperties.getInstance().getProperty("url");
        String urlGetRecords = ConfigurationProperties.getInstance().getProperty("url.get.model.info");

        try {
            urlGetRecords = url + urlGetRecords.replaceAll("\\{csk}", URLEncoder.encode(credentials.getCsk(), "UTF-8")).
                    replaceAll("\\{aci}", URLEncoder.encode(credentials.getAci(), "UTF-8"));
            urlGetRecords = urlGetRecords.replaceAll("\\{mid}", modelId.toString());

            ServiceResponse serviceResponse = ServiceCaller.Impl.getInstance().callService(urlGetRecords, null,
                    HttpMethod.GET);

            JSONObject json = new JSONObject(serviceResponse.getResponse());

            if (json.getLong("status") != 200) {
                throw new RuntimeException(json.getString("message"));
            }

            RecordModel model = new RecordModel(modelId, null);
            model.setName(json.getString("name"));
            model.setDescription(json.getString("description"));

            return model;
        } catch (Exception e) {
            throw new DynamiCloudSessionException(e.getMessage());
        }
    }

    /**
     * Loads all models related to CSK and ACI keys in DynamiCloud servers
     *
     * @return list of models
     * @throws DynamiCloudSessionException if any error occurs
     */
    @Override
    public List<RecordModel> loadModels() throws DynamiCloudSessionException {
        String url = ConfigurationProperties.getInstance().getProperty("url");
        String urlGetRecords = ConfigurationProperties.getInstance().getProperty("url.get.models");

        try {
            urlGetRecords = url + urlGetRecords.replaceAll("\\{csk}", URLEncoder.encode(credentials.getCsk(), "UTF-8")).
                    replaceAll("\\{aci}", URLEncoder.encode(credentials.getAci(), "UTF-8"));

            ServiceResponse serviceResponse = ServiceCaller.Impl.getInstance().callService(urlGetRecords, null,
                    HttpMethod.GET);

            JSONObject json = new JSONObject(serviceResponse.getResponse());

            if (json.getLong("status") != 200) {
                throw new RuntimeException(json.getString("message"));
            }

            List<RecordModel> models = new LinkedList<>();
            JSONArray array = json.getJSONArray("models");
            for (int i = 0; i < array.length(); i++) {
                JSONObject jsonObject = array.getJSONObject(i);

                RecordModel model = new RecordModel(jsonObject.getLong("id"), null);
                model.setName(jsonObject.getString("name"));
                model.setDescription(jsonObject.getString("description"));

                models.add(model);
            }

            return models;
        } catch (Exception e) {
            throw new DynamiCloudSessionException(e.getMessage());
        }
    }

    /**
     * Loads all model's fields according ModelID
     *
     * @param mid modelID
     * @return list of model's fields.
     * @throws DynamiCloudSessionException
     */
    @Override
    public List<RecordField> loadFields(Long mid) throws DynamiCloudSessionException {
        String url = ConfigurationProperties.getInstance().getProperty("url");
        String urlGetRecords = ConfigurationProperties.getInstance().getProperty("url.get.fields");

        try {
            urlGetRecords = url + urlGetRecords.replaceAll("\\{csk}", URLEncoder.encode(credentials.getCsk(), "UTF-8")).
                    replaceAll("\\{aci}", URLEncoder.encode(credentials.getAci(), "UTF-8"));
            urlGetRecords = urlGetRecords.replaceAll("\\{mid}", mid.toString());

            ServiceResponse serviceResponse = ServiceCaller.Impl.getInstance().callService(urlGetRecords, null,
                    HttpMethod.GET);

            JSONObject json = new JSONObject(serviceResponse.getResponse());

            if (json.getLong("status") != 200) {
                throw new RuntimeException(json.getString("message"));
            }

            JSONObject fs = json.getJSONObject("fields");

            List<RecordField> fields = new LinkedList<>();
            Iterator keys = fs.keys();
            while (keys.hasNext()) {
                String key = (String) keys.next();

                JSONObject jf = fs.getJSONObject(key);

                RecordField field = new RecordField(mid);
                field.setId(jf.getLong("id"));
                field.setIdentifier(jf.getString("identifier"));
                field.setLabel(jf.getString("label"));
                field.setComment(jf.getString("comment"));
                field.setUniqueness(jf.getBoolean("uniqueness"));
                field.setUniqueness(jf.getBoolean("required"));
                field.setType(RecordFieldType.getFieldType(jf.getInt("field_type")));
                field.setItems(DynamiCloudUtil.buildItems(jf.getJSONArray("items")));

                fields.add(field);
            }

            return fields;
        } catch (Exception e) {
            throw new DynamiCloudSessionException(e.getMessage());
        }
    }

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
    public void uploadFile(Long modelId, Long recordId, String fieldName, File file, String contentType, String preferredName)
            throws DynamiCloudSessionException {
        String url = ConfigurationProperties.getInstance().getProperty("url");
        String urlGetRecords = ConfigurationProperties.getInstance().getProperty("url.upload.file");

        try {
            urlGetRecords = url + urlGetRecords.replaceAll("\\{csk}", URLEncoder.encode(credentials.getCsk(), "UTF-8")).
                    replaceAll("\\{aci}", URLEncoder.encode(credentials.getAci(), "UTF-8"));
            urlGetRecords = urlGetRecords.replaceAll("\\{mid}", modelId.toString()).
                    replaceAll("\\{rid}", recordId.toString());

            HashMap<String, String> params = new HashMap<>();
            params.put("pickedFileName", file.getName());
            params.put("file_type", contentType);
            params.put("file_name", preferredName);
            params.put("identifier", fieldName);

            ServiceResponse serviceResponse = ServiceCaller.Impl.getInstance().callService(urlGetRecords, params, file, false);

            JSONObject json = new JSONObject(serviceResponse.getResponse());

            if (json.getLong("status") != 200) {
                throw new RuntimeException(json.getString("message"));
            }
        } catch (Exception e) {
            throw new DynamiCloudSessionException(e.getMessage());
        }
    }

    /**
     * This method will make a request to generate a link to download the file related to this recordId and fieldName
     *
     * @param modelId   model id
     * @param recordId  record id
     * @param fieldName field name
     * @return link to share file
     */
    public String shareFile(Long modelId, Long recordId, String fieldName) throws DynamiCloudSessionException {
        String url = ConfigurationProperties.getInstance().getProperty("url");
        String urlGetRecords = ConfigurationProperties.getInstance().getProperty("url.share.file");

        try {
            urlGetRecords = url + urlGetRecords.replaceAll("\\{csk}", URLEncoder.encode(credentials.getCsk(), "UTF-8")).
                    replaceAll("\\{aci}", URLEncoder.encode(credentials.getAci(), "UTF-8"));
            urlGetRecords = urlGetRecords.replaceAll("\\{mid}", modelId.toString()).
                    replaceAll("\\{rid}", recordId.toString()).replaceAll("\\{identifier}", fieldName);

            ServiceResponse serviceResponse = ServiceCaller.Impl.getInstance().callService(urlGetRecords, null, HttpMethod.GET);

            JSONObject json = new JSONObject(serviceResponse.getResponse());

            if (json.getLong("status") != 200) {
                throw new RuntimeException(json.getString("message"));
            }

            return json.getString("link");
        } catch (Exception e) {
            throw new DynamiCloudSessionException(e.getMessage());
        }
    }

    /**
     * Downloads a file according <b>rid</b> and <b>fieldName</b>
     *
     * @param modelId   owner model id of this record <b>rid<b/>
     * @param recordId  record id
     * @param fieldName fieldName target
     * @param destiny   destiny file.  If this file doesn't exist then will be created
     */
    @Override
    public void downloadFile(Long modelId, Long recordId, String fieldName, File destiny)
            throws DynamiCloudSessionException {
        String url = ConfigurationProperties.getInstance().getProperty("url");
        String urlGetRecords = ConfigurationProperties.getInstance().getProperty("url.download.file");

        try {
            urlGetRecords = url + urlGetRecords.replaceAll("\\{csk}", URLEncoder.encode(credentials.getCsk(), "UTF-8")).
                    replaceAll("\\{aci}", URLEncoder.encode(credentials.getAci(), "UTF-8"));
            urlGetRecords = urlGetRecords.replaceAll("\\{mid}", modelId.toString()).
                    replaceAll("\\{rid}", recordId.toString()).replaceAll("\\{identifier}", fieldName);

            if (!destiny.exists()) {
                boolean newFile = destiny.createNewFile();
                if (!newFile) {
                    throw new IllegalStateException("It couldn't create the file.");
                }
            }

            ServiceResponse serviceResponse = ServiceCaller.Impl.getInstance().callService(urlGetRecords, null, destiny, true);

            JSONObject json = new JSONObject(serviceResponse.getResponse());

            if (json.getLong("status") != 200) {
                throw new RuntimeException(json.getString("message"));
            }
        } catch (Exception e) {
            throw new DynamiCloudSessionException(e.getMessage());
        }
    }

    /**
     * Set the bound instance to get the fields and values that will used to update records
     *
     * @param boundInstance bondInstance with values
     * @return this instance of DynamicSession
     * @throws DynamiCloudSessionException if any error occurs
     */
    @Override
    public DynamicSession setBoundInstance(BoundInstance boundInstance) throws DynamiCloudSessionException {
        this.boundInstance = boundInstance;

        return this;
    }

    /**
     * Executes an update using query as a selection and boundInstance with values
     * DynamiCloud will normalize the key pair values.  That is, will be used field identifiers only.
     *
     * @param query selection
     * @throws DynamiCloudSessionException if any error occurs
     */
    @Override
    @SuppressWarnings("unchecked")
    public void update(Query query) throws DynamiCloudSessionException {
        if (boundInstance == null) {
            throw new IllegalStateException("BoundInstance is null and this object has the values used to update records.");
        }

        String selection = DynamiCloudUtil.buildString(query.getConditions(), null, null, null);
        String fields = "{\"updates\": " + DynamiCloudUtil.buildFieldsJSON(boundInstance) + "}";

        String url = ConfigurationProperties.getInstance().getProperty("url");
        String urlGetRecords = ConfigurationProperties.getInstance().getProperty("url.update.selection");

        try {
            urlGetRecords = url + urlGetRecords.replaceAll("\\{csk}", URLEncoder.encode(credentials.getCsk(), "UTF-8")).
                    replaceAll("\\{aci}", URLEncoder.encode(credentials.getAci(), "UTF-8"));
            urlGetRecords = urlGetRecords.replaceAll("\\{mid}", query.getModel().getId().toString());

            Map<String, String> params = new HashMap<>();
            params.put("fields", fields);
            params.put("selection", selection);

            ServiceResponse serviceResponse = ServiceCaller.Impl.getInstance().callService(urlGetRecords, params);

            JSONObject json = new JSONObject(serviceResponse.getResponse());

            if (json.getLong("status") != 200) {
                throw new RuntimeException(json.getString("message"));
            }
        } catch (Exception e) {
            throw new DynamiCloudSessionException(e.getMessage());
        }

    }

    /**
     * Executes a delete using query as a selection
     *
     * @param query selection
     * @throws DynamiCloudSessionException if any error occurs
     */
    @Override
    @SuppressWarnings("unchecked")
    public void delete(Query query) throws DynamiCloudSessionException {
        String selection = DynamiCloudUtil.buildString(query.getConditions(), null, null, null);
        System.out.println("Selection = " + selection);

        String url = ConfigurationProperties.getInstance().getProperty("url");
        String urlGetRecords = ConfigurationProperties.getInstance().getProperty("url.delete.selection");

        try {
            urlGetRecords = url + urlGetRecords.replaceAll("\\{csk}", URLEncoder.encode(credentials.getCsk(), "UTF-8")).
                    replaceAll("\\{aci}", URLEncoder.encode(credentials.getAci(), "UTF-8"));
            urlGetRecords = urlGetRecords.replaceAll("\\{mid}", query.getModel().getId().toString());

            Map<String, String> params = new HashMap<>();
            params.put("selection", selection);

            ServiceResponse serviceResponse = ServiceCaller.Impl.getInstance().callService(urlGetRecords, params);

            JSONObject json = new JSONObject(serviceResponse.getResponse());

            if (json.getLong("status") != 200) {
                throw new RuntimeException(json.getString("message"));
            }
        } catch (Exception e) {
            throw new DynamiCloudSessionException(e.getMessage());
        }
    }
}