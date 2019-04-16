package com.microfocus.adm.almoctane.siteparamtool;

import com.hpe.adm.nga.sdk.entities.OctaneCollection;
import com.hpe.adm.nga.sdk.model.BooleanFieldModel;
import com.hpe.adm.nga.sdk.model.DateFieldModel;
import com.hpe.adm.nga.sdk.model.Entity;
import com.hpe.adm.nga.sdk.model.EntityModel;
import com.hpe.adm.nga.sdk.model.ErrorModel;
import com.hpe.adm.nga.sdk.model.FieldModel;
import com.hpe.adm.nga.sdk.model.FloatFieldModel;
import com.hpe.adm.nga.sdk.model.LongFieldModel;
import com.hpe.adm.nga.sdk.model.MultiReferenceFieldModel;
import com.hpe.adm.nga.sdk.model.ObjectFieldModel;
import com.hpe.adm.nga.sdk.model.ReferenceErrorModel;
import com.hpe.adm.nga.sdk.model.ReferenceFieldModel;
import com.hpe.adm.nga.sdk.model.StringFieldModel;

import org.json.JSONArray;
import org.json.JSONObject;
import org.json.JSONTokener;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.time.ZonedDateTime;
import java.util.*;
import java.util.stream.IntStream;

public final class CustomModelParser {
	
	final class OctaneCollectionImpl<T extends Entity> extends LinkedHashSet<T> implements OctaneCollection<T> {

	    private final int totalCount;
	    private final boolean exceedsTotalCount;

	    OctaneCollectionImpl(final int totalCount, final boolean exceedsTotalCount) {
	        super();
	        this.exceedsTotalCount = exceedsTotalCount;
	        this.totalCount = totalCount;
	    }

	    OctaneCollectionImpl() {
	        this(NO_TOTAL_COUNT_SET, false);
	    }

	    @Override
	    public int getTotalCount() {
	        return totalCount;
	    }

	    @Override
	    public boolean exceedsTotalCount() {
	        return exceedsTotalCount;
	    }
	}
	
	
    private static final String JSON_DATA_NAME = "data";
    private static final String JSON_ERRORS_NAME = "errors";
    private static final String JSON_TOTAL_COUNT_NAME = "total_count";
    private static final String JSON_EXCEEDS_TOTAL_COUNT_NAME = "exceeds_total_count";
    private static final String REGEX_DATE_FORMAT = "\\d{4}-\\d{1,2}-\\d{1,2}T\\d{1,2}:\\d{1,2}:\\d{1,2}Z";
    private static final String LOGGER_INVALID_FIELD_SCHEME_FORMAT = " field scheme is invalid";

    private final Logger logger = LoggerFactory.getLogger(CustomModelParser.class.getName());

    private static CustomModelParser modelParser = new CustomModelParser();

    private CustomModelParser() {
    }

    public static CustomModelParser getInstance() {
        return modelParser;
    }

    /**
     * get a new json object based on a given EntityModel object
     *
     * @param entityModel the given entity model object
     * @return new json object based on a given EntityModel object
     */
    public final JSONObject getEntityJSONObject(EntityModel entityModel) {
        return getEntityJSONObject(entityModel, false);
    }

    /**
     * get a new json object based on a given EntityModel object
     *
     * @param entityModel the given entity model object
     * @param onlyDirty   Return only dirty fields (used for updates)
     * @return new json object based on a given EntityModel object
     */
    public final JSONObject getEntityJSONObject(EntityModel entityModel, boolean onlyDirty) {

        Collection<FieldModel> fieldModels = entityModel.getValues();
        JSONObject objField = new JSONObject();
        fieldModels.forEach((i) -> objField.put(i.getName(), getFieldValue(i)));

        return objField;
    }

    /**
     * get a new json object based on a given EntityModel list
     *
     * @param entitiesModels - Collection of entities models
     * @return new json object conatin entities data
     */
    public final JSONObject getEntitiesJSONObject(Collection<EntityModel> entitiesModels) {
        return getEntitiesJSONObject(entitiesModels, false);
    }

    /**
     * get a new json object based on a given EntityModel list
     *
     * @param entitiesModels - Collection of entities models
     * @param onlyDirty      Converts only dirty fields (relevant for updating entity)
     * @return new json object conatin entities data
     */
    public final JSONObject getEntitiesJSONObject(Collection<EntityModel> entitiesModels, boolean onlyDirty) {

        JSONObject objBase = new JSONObject();
        JSONArray objEntities = new JSONArray();
        objBase.put(JSON_DATA_NAME, objEntities);
        objBase.put(JSON_TOTAL_COUNT_NAME, entitiesModels.size());
        objBase.put(JSON_EXCEEDS_TOTAL_COUNT_NAME, false);
        entitiesModels.forEach((i) -> objEntities.put(getEntityJSONObject(i, onlyDirty)));

        return objBase;
    }

    /**
     * GetEntities an object that represent a field value based on the Field Model
     *
     * @param fieldModel the source fieldModel
     * @return field value
     */
    private Object getFieldValue(FieldModel fieldModel) {

        Object fieldValue;

        if (fieldModel.getClass() == ReferenceFieldModel.class) {
            EntityModel fieldEntityModel = ((ReferenceFieldModel) fieldModel).getValue();
            fieldValue = JSONObject.NULL;

            if (fieldEntityModel != null) {
                fieldValue = getEntityJSONObject(fieldEntityModel, false);
            }

        } else if (fieldModel.getClass() == MultiReferenceFieldModel.class) {

            Collection<EntityModel> entities = ((MultiReferenceFieldModel) fieldModel).getValue();
            fieldValue = getEntitiesJSONObject(entities, false);

        } else {

            fieldValue = fieldModel.getValue();
        }

        return fieldValue;
    }

    /**
     * get a new EntityModel object based on json object
     *
     * @param jsonEntityObj - json object
     * @return new EntityModel object
     */
    public EntityModel getEntityModel(JSONObject jsonEntityObj) {

        Set<FieldModel> fieldModels = new HashSet<>();
        Iterator<?> keys = jsonEntityObj.keys();
        EntityModel entityModel;

        while (keys.hasNext()) {

            FieldModel fldModel = null;
            String strKey = (String) keys.next();
            Object aObj = jsonEntityObj.get(strKey);
            if (aObj == JSONObject.NULL) {
                fldModel = new ReferenceFieldModel(strKey, null);
            } else if (aObj instanceof Long || aObj instanceof Integer) {
                fldModel = new LongFieldModel(strKey, Long.parseLong(aObj.toString()));
            } else if (aObj instanceof Double || aObj instanceof Float) {
                fldModel = new FloatFieldModel(strKey, Float.parseFloat(aObj.toString()));
            } else if (aObj instanceof Boolean) {
                fldModel = new BooleanFieldModel(strKey, Boolean.parseBoolean(aObj.toString()));
            } else if (aObj instanceof JSONObject) {

                JSONObject fieldObject = jsonEntityObj.getJSONObject(strKey);

                if (!fieldObject.isNull(JSON_DATA_NAME)) {

                    Collection<EntityModel> entities = getEntities(aObj.toString());
                    fldModel = new MultiReferenceFieldModel(strKey, entities);
                } else if (!fieldObject.isNull("type") && !fieldObject.isNull("id")) {
                    EntityModel ref = getEntityModel(jsonEntityObj.getJSONObject(strKey));
                    fldModel = new ReferenceFieldModel(strKey, ref);
                } else {
                    fldModel = new ObjectFieldModel(strKey, aObj.toString());
                }

            } else if (aObj instanceof String) {

                boolean isMatch = aObj.toString().matches(REGEX_DATE_FORMAT);
                if (isMatch) {

                    final ZonedDateTime zonedDateTime = ZonedDateTime.parse(aObj.toString());
                    fldModel = new DateFieldModel(strKey, zonedDateTime);

                } else {
                    fldModel = new StringFieldModel(strKey, aObj.toString());
                }
            } else {
                //logger.debug(strKey + LOGGER_INVALID_FIELD_SCHEME_FORMAT);
                continue; //do not put it inside the model object to avoid a null pointer exception
            }

            fieldModels.add(fldModel);
        }

        entityModel = new EntityModel(fieldModels, EntityModel.EntityState.CLEAN);
        return entityModel;
    }

    /**
     * get a entity model collection based on a given json string
     *
     * @param json The JSON to parse
     * @return entity model collection based on a given json string
     */
    public OctaneCollection<EntityModel> getEntities(String json) {
        JSONTokener tokener = new JSONTokener(json);
        JSONObject jsonObj = new JSONObject(tokener);
        JSONArray jsonDataArr = jsonObj.getJSONArray(JSON_DATA_NAME);

        final OctaneCollection<EntityModel> entityModels;
        if (jsonObj.has(JSON_EXCEEDS_TOTAL_COUNT_NAME) && jsonObj.has(JSON_TOTAL_COUNT_NAME)) {
            final boolean exceedsTotalAmount = jsonObj.getBoolean("exceeds_total_count");
            final int totalCount = jsonObj.getInt("total_count");
            entityModels = new OctaneCollectionImpl<>(totalCount, exceedsTotalAmount);
        } else {
            entityModels = new OctaneCollectionImpl<>();
        }
        IntStream.range(0, jsonDataArr.length()).forEach((i) -> entityModels.add(getEntityModel(jsonDataArr.getJSONObject(i))));

        return entityModels;
    }

    public boolean hasErrorModels(String json) {
        JSONTokener tokener = new JSONTokener(json);
        JSONObject jsonObj = new JSONObject(tokener);
        return jsonObj.has(JSON_ERRORS_NAME) && jsonObj.get(JSON_ERRORS_NAME) instanceof JSONArray;
    }

    /**
     * GetEntities Error models based on a given error json string
     *
     * @param json - json string with error information
     * @return collection of error models
     */
    public Collection<ErrorModel> getErrorModels(String json) {

        JSONTokener tokener = new JSONTokener(json);
        JSONObject jsonObj = new JSONObject(tokener);
        JSONArray jsonErrArr = jsonObj.getJSONArray(JSON_ERRORS_NAME);
        Collection<ErrorModel> ErrModels = new ArrayList<>();
        IntStream.range(0, jsonErrArr.length()).forEach((i) -> ErrModels.add(getErrorModelFromjson(jsonErrArr.getJSONObject(i).toString())));

        return ErrModels;
    }

    /**
     * GetEntities Error model based on a given error json string
     *
     * @param json - json string with error information
     * @return error model
     */
    public ErrorModel getErrorModelFromjson(String json) {

        JSONTokener tokener = new JSONTokener(json);
        JSONObject jsonErrObj = new JSONObject(tokener);

        Set<FieldModel> fieldModels = new HashSet<>();
        Iterator<?> keys = jsonErrObj.keys();

        while (keys.hasNext()) {

            String strKey = (String) keys.next();
            Object aObj = jsonErrObj.get(strKey);

            FieldModel fldModel;

            if (aObj == JSONObject.NULL) {
                fldModel = new ReferenceErrorModel(strKey, null);
            } else if (aObj instanceof JSONObject || aObj == JSONObject.NULL) {
                EntityModel ref = getEntityModel(jsonErrObj.getJSONObject(strKey));
                fldModel = new ReferenceFieldModel(strKey, ref);
            } else {

                fldModel = new StringFieldModel(strKey, aObj.toString());
            }

            fieldModels.add(fldModel);

        }


        return new ErrorModel(fieldModels);
    }
}