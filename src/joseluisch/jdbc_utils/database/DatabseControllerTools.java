package joseluisch.jdbc_utils.database;

import java.lang.reflect.Field;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.sql.Date;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Timestamp;
import java.sql.Types;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.UUID;
import static joseluisch.jdbc_utils.controllers.DatabaseController.excecuteQuery;
import joseluisch.jdbc_utils.controllers.information_schema.InformationSchemaColumnsController;
import joseluisch.jdbc_utils.entities.KeyColumnObject;
import joseluisch.jdbc_utils.entities.TableDetails;
import joseluisch.jdbc_utils.singleton.DataInstance;
import joseluisch.jdbc_utils.utils.ReflectUtils;
import joseluisch.jdbc_utils.utils.StringUtils;

/**
 *
 * @author Jose Luis Chavez
 */
public class DatabseControllerTools {

    //==========================================================================
    protected static String getSQL(Map<String, String> mapKeys, Object mObject) throws ClassNotFoundException, InstantiationException, IllegalAccessException {

        StringBuilder builderResult = new StringBuilder();
        StringBuilder builderParams = new StringBuilder();

        //GET SCHEMA KEY VALUES
        List<KeyColumnObject> columnObjects = InformationSchemaColumnsController.get(mObject);
        //List<ViewObject> viewObjects = InformationSchemaViewsController.get();

//        for (ViewObject viewObject : viewObjects) {
//            KeyColumnObject columnObject = new KeyColumnObject();
//            columnObject.setTable_name(viewObject.getTABLE_NAME());
//            columnObjects.add(columnObject);
//        }
//INIT BUILDERS
        String parentTable = StringUtils.toLowerScoreCase(mObject.getClass().getSimpleName());
        String parentAlias = StringUtils.getStringUIDD();
        String objectName = StringUtils.toLowerScoreCase(mObject.getClass().getSimpleName());

        for (KeyColumnObject keyObject : columnObjects) {
            if (StringUtils.toLowerScoreCase(keyObject.getTable_name()).equalsIgnoreCase(objectName)) {
                parentTable = keyObject.getTable_name();
            }
        }

        builderParams.append(" select  ");
        builderResult.append(" from ").append(parentTable).append(" ").append(parentAlias);

        //APPEND JOINS
        appendJoins(mapKeys, mObject, builderParams, builderResult, columnObjects, parentTable, parentAlias, parentTable);

        //APPEND WHERE'S
        appendWheres(mapKeys, mObject, builderResult);

        return builderParams.substring(0, builderParams.length() - 2) + builderResult.toString();
    }

    protected static String addSQL(Object mObject) throws IllegalArgumentException, IllegalAccessException, ClassNotFoundException {

        String table_name = InformationSchemaColumnsController.getTableName(mObject);
        StringBuilder builder = new StringBuilder();
        StringBuilder builderValues = new StringBuilder();

        builder.append(" insert into ").append(table_name).append(" ( ");
        builderValues.append(" values ( ");

        for (Field f : mObject.getClass().getDeclaredFields()) {
            f.setAccessible(true);
            Object o = f.get(mObject);

            //if (o != null) {
            if (ReflectUtils.isValidField(f)) {
                builder.append(f.getName()).append(",");
                builderValues.append("?,");
            }
            //}
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
    protected static void prepareStatementSelect(PreparedStatement ps, Object mObject) throws IllegalArgumentException, IllegalAccessException, SQLException {

        Class aClass = mObject.getClass();

        int index = 1;
        for (Field f : aClass.getDeclaredFields()) {
            f.setAccessible(true);

            if (f.get(mObject) != null) {
                Object o = f.get(mObject);
                if (f.getType().getSimpleName().equalsIgnoreCase(String.class.getSimpleName())) {
                    ps.setObject(index++, o);
                } else if (f.getType().getSimpleName().equalsIgnoreCase(Double.class.getSimpleName())) {
                    ps.setObject(index++, o);
                } else if (f.getType().getSimpleName().equalsIgnoreCase(Long.class.getSimpleName())) {
                    ps.setObject(index++, o);
                } else if (f.getType().getSimpleName().equalsIgnoreCase(Float.class.getSimpleName())) {
                    ps.setObject(index++, o);
                } else if (f.getType().getSimpleName().equalsIgnoreCase(Long.class.getSimpleName())) {
                    ps.setObject(index++, o);
                } else if (f.getType().getSimpleName().equalsIgnoreCase(Boolean.class.getSimpleName())) {
                    ps.setObject(index++, o);
                } else if (f.getType().getSimpleName().equalsIgnoreCase(Integer.class.getSimpleName())) {
                    ps.setObject(index++, o);
                }
            }

        }
    }

    protected static void prepareStatementInsert(PreparedStatement ps, Object mObject) throws IllegalArgumentException, IllegalAccessException, SQLException {

        String table_name = InformationSchemaColumnsController.getTableName(mObject);
        List<TableDetails> list = DataInstance.getInstance().getTableDetailsList(table_name);

        fillId(mObject);
        Class aClass = mObject.getClass();

        int index = 1;
        for (Field f : aClass.getDeclaredFields()) {
            f.setAccessible(true);
            Object fieldValue = f.get(mObject);
            //if (fieldValue != null) {
            if (ReflectUtils.isValidField(f)) {

                boolean isDate = false;
                for (TableDetails details : list) {
                    if (f.getName().equalsIgnoreCase(details.getField())) {
                        if (details.getType().contains("date") || details.getType().contains("timestamp")) {
                            isDate = Boolean.TRUE;
                            break;
                        }
                    }
                }

                if (isDate && Long.class.getSimpleName().equals(f.getType().getSimpleName())) {
                    ps.setObject(index++, new Date((long) fieldValue));
                } else {
                    ps.setObject(index++, fieldValue);
                }

            }
            //}
        }
    }

    protected static void prepareStatementUpdate(PreparedStatement ps, Object mObject, String fieldId) throws IllegalArgumentException, IllegalAccessException, SQLException, NoSuchFieldException {

        String table_name = InformationSchemaColumnsController.getTableName(mObject);
        List<TableDetails> list = DataInstance.getInstance().getTableDetailsList(table_name);

        Class aClass = mObject.getClass();
        Field field = aClass.getDeclaredField(fieldId);
        field.setAccessible(true);

        if (field.get(mObject) != null) {
            int index = 1;

            for (Field f : aClass.getDeclaredFields()) {
                f.setAccessible(true);

                Object fieldValue = f.get(mObject);
                if (fieldValue != null && !f.getName().equalsIgnoreCase(fieldId) && ReflectUtils.isValidField(f)) {

                    boolean isDate = false;
                    for (TableDetails details : list) {
                        if (f.getName().equalsIgnoreCase(details.getField())) {
                            if (details.getType().contains("date") || details.getType().contains("timestamp")) {
                                isDate = Boolean.TRUE;
                                break;
                            }
                        }
                    }

                    if (isDate && Long.class.getSimpleName().equals(f.getType().getSimpleName())) {
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
    protected static Map<String, Object> fillResultSet(ResultSet resultSet, Object mObject, Map<String, String> mapKeys) throws InstantiationException, IllegalArgumentException, IllegalAccessException, SQLException, InvocationTargetException, NoSuchMethodException {
        Map<String, Object> map = new HashMap<>();

        String parenValue = null;

        String loweScoreClassName = StringUtils.toLowerScoreCase(mObject.getClass().getSimpleName());
        for (Map.Entry entry : mapKeys.entrySet()) {
            String key = entry.getKey().toString();
            if (key.equalsIgnoreCase(loweScoreClassName)) {
                parenValue = (String) entry.getValue();
            }
        }

        parenValue = parenValue == null ? mapKeys.remove(mObject.getClass().getSimpleName()) : parenValue;

        while (resultSet.next()) {

            Object newObject = mObject.getClass().newInstance();

            for (Field field : newObject.getClass().getDeclaredFields()) {
                field.setAccessible(true);
                if (ReflectUtils.isValidField(field)) {

                    Object value = null;
                    for (int i = 1; i <= resultSet.getMetaData().getColumnCount(); i++) {
                        if (resultSet.getMetaData().getColumnLabel(i).toLowerCase().contains(field.getName().toLowerCase())) {
                            if (resultSet.getMetaData().getColumnType(i) == Types.TIMESTAMP) {
                                try {
                                    value = resultSet.getTimestamp(parenValue + "$" + field.getName());
                                    value = value != null ? ((Timestamp) value).getTime() : null;
                                } catch (Exception e) {
                                }
                                break;
                            } else if (resultSet.getMetaData().getColumnType(i) == Types.DATE) {
                                try {
                                    value = resultSet.getDate(parenValue + "$" + field.getName());
                                    value = value != null ? ((Timestamp) value).getTime() : null;
                                } catch (Exception e) {
                                }
                                break;
                            } else if (resultSet.getMetaData().getColumnType(i) == Types.NUMERIC) {
                                if (resultSet.getMetaData().getColumnClassName(i).equals("java.math.BigDecimal")) {
                                    value = resultSet.getInt(parenValue + "$" + field.getName());
                                }
                                break;
                            }
                        }
                    }

                    if (field.getName().length() < 26) {
                        value = value == null ? resultSet.getObject(parenValue + "$" + field.getName()) : value;
                    }

                    try {
                        field.set(newObject, value);
                    } catch (Exception e) {
                        field.set(newObject, value.toString());
                    }
                }
            }

            for (Map.Entry entry : mapKeys.entrySet()) {
                String key = entry.getKey().toString();
                String value = entry.getValue().toString();

                Object referencedObject = getReferencedObject(newObject, key);

                if (referencedObject != null && referencedObject != newObject) {
                    for (Field field : referencedObject.getClass().getDeclaredFields()) {
                        field.setAccessible(true);
                        if (ReflectUtils.isValidField(field)) {

                            Object val = null;
                            for (int i = 1; i <= resultSet.getMetaData().getColumnCount(); i++) {
                                if (resultSet.getMetaData().getColumnLabel(i).toLowerCase().contains(field.getName().toLowerCase())) {
                                    if (resultSet.getMetaData().getColumnType(i) == Types.TIMESTAMP) {
                                        try {
                                            val = resultSet.getTimestamp(value + "$" + field.getName());
                                            val = val != null ? ((Timestamp) val).getTime() : 0L;
                                        } catch (Exception e) {
                                        }
                                        break;
                                    } else if (resultSet.getMetaData().getColumnType(i) == Types.DATE) {
                                        try {
                                            val = resultSet.getDate(value + "$" + field.getName());
                                            val = val != null ? ((Timestamp) val).getTime() : 0L;
                                        } catch (Exception e) {
                                        }
                                        break;
                                    } else if (resultSet.getMetaData().getColumnType(i) == Types.NUMERIC) {
                                        if (resultSet.getMetaData().getColumnClassName(i).equals("java.math.BigDecimal")) {
                                            val = resultSet.getInt(value + "$" + field.getName());
                                        }
                                        break;
                                    }
                                }
                            }

                            if (field.getName().length() < 26) {
                                val = val == null ? resultSet.getObject(value + "$" + field.getName()) : val;
                            }

                            try {
                                field.set(referencedObject, val);
                            } catch (Exception e) {
                                field.set(referencedObject, val.toString());
                            }
                        }
                    }
                }
            }

            map.put(newObject.toString(), newObject);
        }

        return map;
    }

    protected static void fillId(Object object) throws IllegalArgumentException, IllegalAccessException {
        if (!object.getClass().getSimpleName().equalsIgnoreCase("Usuario")) {
            List<KeyColumnObject> list = DataInstance.getInstance().getKeyColumnObjectList();
            String className = object.getClass().getSimpleName();

            for (KeyColumnObject keyObject : list) {
                if (keyObject.getConstraint_type().equals("P")) {
                    String tableName = StringUtils.toUpperCamelCase(StringUtils.toLowerScoreCase(keyObject.getTable_name()));
                    if (className.equals(tableName)) {
                        String fieldId = keyObject.getColumn_name();
                        for (Field field : object.getClass().getDeclaredFields()) {
                            field.setAccessible(true);

                            if (field.getName().equals(fieldId.toLowerCase())) {
                                if (field.getType().getSimpleName().equals(String.class
                                        .getSimpleName())) {

                                    String table_name = InformationSchemaColumnsController.getTableName(object);
                                    List<TableDetails> detailList = DataInstance.getInstance().getTableDetailsList(table_name);

                                    Integer intLength = 0;
                                    for (TableDetails details : detailList) {
                                        if (details.getField().equals(fieldId)) {
                                            String length = details.getType().replaceAll("\\D+", "");
                                            if (length != null && !length.isEmpty()) {
                                                intLength = Integer.valueOf(length);
                                            }
                                            break;
                                        }
                                    }

                                    String id = UUID.randomUUID().toString();
                                    if (id.length() > intLength) {

                                        Map<String, Object> map = null;

                                        do {
                                            id = UUID.randomUUID().toString();
                                            id = id.replace("-", "");
                                            id = id.substring(id.length() - intLength, id.length());

                                            String sql = "select " + fieldId + " FROM " + table_name + " where " + fieldId + " = ?";
                                            List<Object> params = new ArrayList<>();
                                            params.add(id);
                                            map = excecuteQuery(sql, params);

                                        } while (map != null && !map.isEmpty());

                                    }

                                    field.set(object, id);

                                } else if (field.getType().getSimpleName().equals(Integer.class
                                        .getSimpleName())) {
                                    String sql = "select nvl(max(" + fieldId + "), 0) + 1  as max from " + keyObject.getTable_name();
                                    Map<String, Object> map = excecuteQuery(sql, null);
                                    for (Map.Entry entry : map.entrySet()) {
                                        Map<String, Object> m = (Map<String, Object>) entry.getValue();
                                        field.set(object, Integer.valueOf(m.get("MAX").toString()));
                                        break;
                                    }
                                }
                                return;
                            }
                        }
                    }
                }
            }
        }
    }

    protected static void appendJoins(Map<String, String> mapKeys, Object mObject, StringBuilder builderParams, StringBuilder builderResult, List<KeyColumnObject> list, String parentTable, String parentAlias, String parentChain) throws ClassNotFoundException, InstantiationException, IllegalAccessException {

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
                    fieldName = StringUtils.toFirstUpperCased(keyObject.getColumn_name().toLowerCase());
                    chain = fieldType + fieldName;
                }

                String newParentAlias = StringUtils.getStringUIDD();
                String newParentTable = keyObject.getReferenced_table_name();
                String newParentChain = parentChain + " " + chain;

                String[] fields = ReflectUtils.getValidFields(mObject.getClass());
                for (String field : fields) {
                    if (field.length() >= 26) {
                        System.out.println("El nombre campo " + field + " es demasiado largo y producirá un error en la consulta SQL; Será omitido de la consulta. Para solucionarlo cambie el nombre del campo a una longitud menor a 27 caracteres");
                    } else {
                        builderParams.append(parentAlias).append(".").append(field).append(" as ").append(parentAlias).append("$").append(field).append(", ");
                    }
                }

                if (newParentTable != null) {

                    String childStringClass = StringUtils.toUpperCamelCase(newParentTable);

                    Object childObjectInstance = ReflectUtils.getChildObjectInstance(mObject, childStringClass);

                    builderResult.append(" left join ").append(newParentTable).append(" ").append(newParentAlias);
                    builderResult.append(" on ").append(newParentAlias).append(".").append(keyObject.getReferenced_column_name()).append(" = ");
                    builderResult.append(parentAlias).append(".").append(keyObject.getColumn_name());

                    appendJoins(mapKeys, childObjectInstance, builderParams, builderResult, list, newParentTable, newParentAlias, newParentChain);
                }
            }
        }
    }

    protected static void appendWheres(Map<String, String> mapKeys, Object mObject, StringBuilder builder) throws IllegalArgumentException, IllegalAccessException {

        List<KeyColumnObject> columnObjects = InformationSchemaColumnsController.get(mObject);
        String objectName = StringUtils.toLowerScoreCase(mObject.getClass().getSimpleName());

        String parentTable = null;
        for (KeyColumnObject keyObject : columnObjects) {
            if (StringUtils.toLowerScoreCase(keyObject.getTable_name()).equalsIgnoreCase(objectName)) {
                parentTable = keyObject.getTable_name();
                break;
            }
        }

        builder.append(" WHERE 1 = 1 ");
        String key = mapKeys.get(parentTable);

        for (Field f : mObject.getClass().getDeclaredFields()) {
            f.setAccessible(true);
            Object o = f.get(mObject);

            if (o != null) {
                if (ReflectUtils.isValidField(f)) {
                    builder.append(" AND ").append(key).append(".").append(f.getName()).append(" like ?");
                }
            }

        }
    }

    protected static Object getReferencedObject(Object mObject, String keyValue) throws IllegalAccessException, IllegalArgumentException, InvocationTargetException {

        String[] subKeys = keyValue.split(" ");
        String selectedKey = subKeys != null && subKeys.length > 1 ? subKeys[1] : null;

        if (selectedKey != null) {

            try {
                Method fieldGetter = mObject.getClass().getMethod("get" + selectedKey);
                Object newObject = fieldGetter.invoke(mObject);

                StringBuilder builder = new StringBuilder();
                for (int i = 1; i < subKeys.length; i++) {
                    builder.append(subKeys[i]);
                    builder.append(" ");
                }

                return getReferencedObject(newObject, builder.toString().trim());

            } catch (NoSuchMethodException ex) {
                return null;
            }
        }

        return mObject;
    }

    protected static String getFieldId(Object object) {
        List<KeyColumnObject> list = DataInstance.getInstance().getKeyColumnObjectList();
        String className = object.getClass().getSimpleName();
        for (KeyColumnObject keyObject : list) {
            if (keyObject.getConstraint_type().equals("P")) {
                String tableName = StringUtils.toUpperCamelCase(StringUtils.toLowerScoreCase(keyObject.getTable_name()));
                if (className.equals(tableName)) {
                    return keyObject.getColumn_name().toLowerCase();
                }
            }
        }
        return null;
    }

    //==========================================================================
}
