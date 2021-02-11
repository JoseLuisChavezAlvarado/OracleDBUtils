package penoles.oraclebdutils.database.controller;

import java.lang.reflect.Field;
import java.lang.reflect.InvocationTargetException;
import java.sql.Date;
import java.sql.PreparedStatement;
import java.sql.SQLException;
import java.util.List;
import java.util.Map;
import penoles.oraclebdutils.entities.KeyColumnObject;
import penoles.oraclebdutils.entities.TableDetails;
import penoles.oraclebdutils.entities.ViewObject;
import penoles.oraclebdutils.information_schema.InformationSchemaColumnsController;
import penoles.oraclebdutils.information_schema.InformationSchemaViewsController;
import penoles.oraclebdutils.singleton.DataInstance;
import penoles.oraclebdutils.utils.ReflectUtils;
import penoles.oraclebdutils.utils.StringUtils;

/**
 *
 * @author Jose Luis Chavez
 */
public class DatabaseControllerTools extends DatabaseControllerToolsOperations {

    protected static String getSQL(Map<String, String> mapKeys, Object mObject, String filterVariable, boolean equals, Integer page, Integer pageLength) throws ClassNotFoundException, InstantiationException, IllegalAccessException, IllegalArgumentException, InvocationTargetException, NoSuchMethodException {

        StringBuilder builderResult = new StringBuilder();
        StringBuilder builderParams = new StringBuilder();

        //GET SCHEMA KEY VALUES
        List<KeyColumnObject> columnObjects = InformationSchemaColumnsController.get(mObject);
        List<ViewObject> viewObjects = InformationSchemaViewsController.get(mObject);

        for (ViewObject viewObject : viewObjects) {
            KeyColumnObject columnObject = new KeyColumnObject();
            columnObject.setTable_name(viewObject.getTable_name());
            columnObjects.add(columnObject);
        }

        //INIT BUILDERS
        String parentTable = StringUtils.toLowerScoreCase(mObject.getClass().getSimpleName());
        String parentAlias = StringUtils.getStringUIDD();
        String objectName = StringUtils.toLowerScoreCase(mObject.getClass().getSimpleName());

        for (KeyColumnObject keyObject : columnObjects) {
            if (StringUtils.toLowerScoreCase(keyObject.getTable_name()).equalsIgnoreCase(objectName)) {
                parentTable = keyObject.getTable_name();
                break;
            }
        }

        builderParams.append(" select  ");
        builderResult.append(" from ").append(parentTable).append(" ").append(parentAlias);

        //APPEND JOINS
        appendJoins(mapKeys, mObject, builderParams, builderResult, columnObjects, parentTable, parentAlias, parentTable);

        //APPEND WHERE'S
        appendWheres(mapKeys, mObject, builderResult, parentTable, filterVariable, equals);

        //APPEND LIMIT UPDATE
        //appendLimit(builderResult, page, pageLength);
        return builderParams.substring(0, builderParams.length() - 2) + builderResult.toString();
    }

    protected static String addSQL(Object mObject) throws IllegalArgumentException, IllegalAccessException, ClassNotFoundException {

        String table_name = InformationSchemaColumnsController.getTableName(mObject);
        StringBuilder builderValues = new StringBuilder();
        StringBuilder builder = new StringBuilder();
        String fieldId = getFieldId(mObject);

        builder.append(" insert into ").append(table_name).append(" ( ").append(fieldId).append(", ");
        builderValues.append(" values (?,");

        for (Field f : mObject.getClass().getDeclaredFields()) {
            f.setAccessible(true);
            Object o = f.get(mObject);

            if (o != null && !f.getName().equals(fieldId) && ReflectUtils.isValidField(f)) {
                builder.append(f.getName()).append(",");
                builderValues.append("?,");
            }
        }

        builderValues = builderValues.replace(builderValues.length() - 1, builderValues.length(), "");
        builder = builder.replace(builder.length() - 1, builder.length(), "");

        builderValues.append(")");
        builder.append(")");

        return builder.append(builderValues).toString();
    }

    protected static String updateSQL(Object mObject, String fieldId) throws NoSuchFieldException, IllegalArgumentException, IllegalAccessException {
        StringBuilder builder = new StringBuilder();

        Class aClass = mObject.getClass();
        String table_name = InformationSchemaColumnsController.getTableName(mObject);

        builder.append(" update ");
        builder.append(table_name);
        builder.append(" set ");

        Field field = aClass.getDeclaredField(fieldId);
        field.setAccessible(true);

        if (field.get(mObject) != null) {
            for (Field f : aClass.getDeclaredFields()) {
                f.setAccessible(true);
                Object o = f.get(mObject);
                if (o != null && !f.getName().equalsIgnoreCase(fieldId) && ReflectUtils.isValidField(f)) {
                    builder.append(f.getName());
                    builder.append(" = ? ,");
                }
            }
            builder.replace(builder.length() - 1, builder.length(), "");
        }

        builder.append(" where ");
        builder.append(fieldId);
        builder.append(" = ? ");

        return builder.toString();
    }

    protected static String deleteSQL(Object mObject, String fieldId) throws NoSuchFieldException, IllegalArgumentException, IllegalAccessException {
        StringBuilder builder = new StringBuilder();
        Class aClass = mObject.getClass();
        String table_name = InformationSchemaColumnsController.getTableName(mObject);
        builder.append(" delete from ");
        builder.append(table_name);
        builder.append(" where ");
        builder.append(fieldId);
        builder.append(" = ?");
        return builder.toString();
    }

    //==========================================================================
    protected static void prepareStatementSelect(Map<String, String> mapKeys, PreparedStatement ps, Object mObject, boolean equals) throws IllegalArgumentException, IllegalAccessException, SQLException, InvocationTargetException, NoSuchMethodException {

        int index = 1;

        for (Map.Entry entry : mapKeys.entrySet()) {
            String key = entry.getKey().toString();

            Object referencedObject = getReferencedObject(mObject, key);

            if (referencedObject != null) {
                for (Field field : referencedObject.getClass().getDeclaredFields()) {
                    field.setAccessible(true);
                    if (ReflectUtils.isValidField(field)) {
                        Object o = field.get(referencedObject);
                        if (o != null) {
                            if (field.getType().getSimpleName().equalsIgnoreCase(String.class.getSimpleName()) && !equals) {
                                String newObject = "%" + o + "%";
                                o = newObject;
                            }

                            ps.setObject(index++, o);
                        }
                    }
                }
            }
        }
    }

    protected static void prepareStatementSelect(PreparedStatement ps, Object mObject) throws IllegalArgumentException, IllegalAccessException, SQLException {

        String tableName = InformationSchemaColumnsController.getTableName(mObject);
        List<TableDetails> list = DataInstance.getInstance().getTableDetailsList(tableName);

        int index = 1;
        for (Field field : mObject.getClass().getDeclaredFields()) {
            field.setAccessible(true);
            Object fieldValue = field.get(mObject);
            if (fieldValue != null && ReflectUtils.isValidField(field)) {
                boolean isDate = false;
                for (TableDetails details : list) {
                    if (field.getName().equalsIgnoreCase(details.getField()) && (details.getType().toLowerCase().contains("date") || details.getType().toLowerCase().contains("timestamp"))) {
                        isDate = Boolean.TRUE;
                        break;
                    }
                }

                if (isDate && Long.class.getSimpleName().equals(field.getType().getSimpleName())) {
                    ps.setObject(index++, new Date(Long.valueOf(fieldValue.toString())));
                } else {
                    ps.setObject(index++, fieldValue);
                }

            }
        }
    }

    protected static void prepareStatementInsert(PreparedStatement ps, Object mObject) throws IllegalArgumentException, IllegalAccessException, SQLException, NoSuchFieldException {

        String tableName = InformationSchemaColumnsController.getTableName(mObject);
        List<TableDetails> list = DataInstance.getInstance().getTableDetailsList(tableName);
        String fieldId = getFieldId(mObject);

        Object idVal = fillId(mObject);
        ps.setObject(1, idVal);

        int index = 2;
        for (Field field : mObject.getClass().getDeclaredFields()) {
            field.setAccessible(true);
            Object fieldValue = field.get(mObject);
            if (fieldValue != null && !field.getName().equals(fieldId) && ReflectUtils.isValidField(field)) {
                boolean isDate = false;
                for (TableDetails details : list) {
                    if (field.getName().equalsIgnoreCase(details.getField()) && (details.getType().toLowerCase().contains("date") || details.getType().toLowerCase().contains("timestamp"))) {
                        isDate = Boolean.TRUE;
                        break;
                    }
                }

                if (isDate && Long.class.getSimpleName().equals(field.getType().getSimpleName())) {
                    ps.setObject(index++, new Date((long) fieldValue));
                } else {
                    ps.setObject(index++, fieldValue);
                }

            }
        }
    }

    protected static void prepareStatementUpdate(PreparedStatement ps, Object mObject, String fieldId) throws IllegalArgumentException, IllegalAccessException, SQLException, NoSuchFieldException {

        String tableName = InformationSchemaColumnsController.getTableName(mObject);
        List<TableDetails> list = DataInstance.getInstance().getTableDetailsList(tableName);

        Class aClass = mObject.getClass();
        Field field = aClass.getDeclaredField(fieldId);
        field.setAccessible(true);

        if (field.get(mObject) != null) {

            int index = 1;
            for (Field f : aClass.getDeclaredFields()) {
                f.setAccessible(true);

                Object fieldValue = f.get(mObject);
                if (fieldValue != null && ReflectUtils.isValidField(f) && !f.getName().equalsIgnoreCase(fieldId)) {

                    boolean isDate = Boolean.FALSE;
                    for (TableDetails details : list) {
                        if (f.getName().equalsIgnoreCase(details.getField()) && (details.getType().toLowerCase().contains("date") || details.getType().toLowerCase().contains("timestamp"))) {
                            isDate = Boolean.TRUE;
                            break;

                        }
                    }

                    if (isDate && Long.class
                            .getSimpleName().equals(f.getType().getSimpleName())) {
                        ps.setObject(index++, new Date((long) fieldValue));
                    } else {
                        ps.setObject(index++, fieldValue);
                    }

                }
            }

            ps.setObject(index, field.get(mObject));
        }
    }

    protected static void prepareStatementDelete(PreparedStatement ps, Object mObject, String fieldId) throws NoSuchFieldException, IllegalArgumentException, IllegalAccessException, SQLException {
        Class aClass = mObject.getClass();

        Field field = aClass.getDeclaredField(fieldId);
        field.setAccessible(true);
        if (field.get(mObject) != null) {
            ps.setObject(1, field.get(mObject));
        } else {
            throw new NoSuchFieldException();
        }
    }

    //==========================================================================
    protected static String getLengthSQL(Map<String, String> mapKeys, Object mObject, boolean equals) throws ClassNotFoundException, InstantiationException, IllegalAccessException, IllegalArgumentException, InvocationTargetException, NoSuchMethodException {

        StringBuilder builderResult = new StringBuilder();
        StringBuilder builderParams = new StringBuilder();

        //GET SCHEMA KEY VALUES
        List<KeyColumnObject> columnObjects = InformationSchemaColumnsController.get(mObject.getClass());
        List<ViewObject> viewObjects = InformationSchemaViewsController.get();

        for (ViewObject viewObject : viewObjects) {
            KeyColumnObject columnObject = new KeyColumnObject();
            columnObject.setTable_name(viewObject.getTable_name());
            columnObjects.add(columnObject);
        }

        //INIT BUILDERS
        String parentTable = StringUtils.toLowerScoreCase(mObject.getClass().getSimpleName());
        String parentAlias = StringUtils.getStringUIDD();
        String objectName = StringUtils.toLowerScoreCase(mObject.getClass().getSimpleName());

        for (KeyColumnObject keyObject : columnObjects) {
            if (StringUtils.toLowerScoreCase(keyObject.getTable_name()).equalsIgnoreCase(objectName)) {
                parentTable = keyObject.getTable_name();
            }
        }

        builderParams.append(" select count(*)   ");
        builderResult.append(" from ").append(parentTable).append(" ").append(parentAlias);

        //APPEND JOINS
        appendJoinsLength(mapKeys, mObject, builderResult, columnObjects, parentTable, parentAlias, parentTable);

        //APPEND WHERE'S
        appendWheres(mapKeys, mObject, builderResult, parentTable, null, equals);

        return builderParams.substring(0, builderParams.length() - 2) + builderResult.toString();
    }

    protected static void appendJoinsLength(Map<String, String> mapKeys, Object mObject, StringBuilder builderResult, List<KeyColumnObject> list, String parentTable, String parentAlias, String parentChain) throws ClassNotFoundException, InstantiationException, IllegalAccessException {

        String key = parentChain;
        String value = parentAlias;
        mapKeys.put(key, value);

        for (int i = 0; i < list.size(); i++) {
            if (list.get(i).getTable_name().equalsIgnoreCase(parentTable)) {
                KeyColumnObject keyObject = list.get(i);

                String fieldType = "";
                String fieldName = "";
                String chain = "";

                if (keyObject.getReferenced_table_name() != null) {
                    fieldType = StringUtils.toUpperCamelCase(keyObject.getReferenced_table_name());
                    fieldName = StringUtils.toFirstUpperCased(keyObject.getColumn_name());
                    chain = fieldType + fieldName;
                }

                String newParentAlias = StringUtils.getStringUIDD();
                String newParentTable = keyObject.getReferenced_table_name();
                String newParentChain = parentChain + " " + chain;

                if (newParentTable != null) {

                    String childStringClass = StringUtils.toUpperCamelCase(newParentTable);

                    Object childObjectInstance = ReflectUtils.getChildObjectInstance(mObject, childStringClass);

                    builderResult.append(" join ").append(newParentTable).append(" ").append(newParentAlias);
                    builderResult.append(" on ").append(newParentAlias).append(".").append(keyObject.getReferenced_column_name()).append(" = ");
                    builderResult.append(parentAlias).append(".").append(keyObject.getColumn_name());

                    appendJoinsLength(mapKeys, childObjectInstance, builderResult, list, newParentTable, newParentAlias, newParentChain);
                }
            }
        }
    }

}
