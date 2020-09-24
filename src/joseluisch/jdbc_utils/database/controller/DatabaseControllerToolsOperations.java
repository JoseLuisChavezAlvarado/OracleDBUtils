package joseluisch.jdbc_utils.database.controller;

import java.lang.reflect.Field;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Timestamp;
import java.sql.Types;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.UUID;
import joseluisch.jdbc_utils.controllers.information_schema.InformationSchemaColumnsController;
import joseluisch.jdbc_utils.controllers.information_schema.InformationSchemaViewsController;
import joseluisch.jdbc_utils.database.DatabaseInstance;
import joseluisch.jdbc_utils.entities.KeyColumnObject;
import joseluisch.jdbc_utils.entities.TableDetails;
import joseluisch.jdbc_utils.entities.ViewObject;
import joseluisch.jdbc_utils.entities.VwMultivaluada;
import joseluisch.jdbc_utils.singleton.DataInstance;
import joseluisch.jdbc_utils.utils.ReflectUtils;
import joseluisch.jdbc_utils.utils.StringUtils;

/**
 *
 * @author Jose Luis Chavez
 */
public class DatabaseControllerToolsOperations {

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

                    if (childObjectInstance != null && ReflectUtils.isTableNormalized(childObjectInstance.getClass())) {

                        builderResult.append(" left join ").append(newParentTable).append(" ").append(newParentAlias);
                        builderResult.append(" on ").append(newParentAlias).append(".").append(keyObject.getReferenced_column_name()).append(" = ");
                        builderResult.append(parentAlias).append(".").append(keyObject.getColumn_name());

                        appendJoins(mapKeys, childObjectInstance, builderParams, builderResult, list, newParentTable, newParentAlias, newParentChain);
                    }
                }
            }
        }

        if (DatabaseInstance.getInstance().getMul()) {
            List<VwMultivaluada> vwMultivaluadaList = DataInstance.getInstance().getVwMultivaluadaList();
            String[] fields = ReflectUtils.getValidFields(mObject.getClass());

            for (VwMultivaluada mul : vwMultivaluadaList) {
                if (mul.getLta_nom_tabla().equals(parentTable)) {
                    for (String field : fields) {
                        if (field.equalsIgnoreCase(mul.getLta_nom_campo())) {
                            String tableAlias = StringUtils.getStringUIDD();
                            String mulChain = parentChain + " " + StringUtils.toFirstUpperCased(field) + "_mul";
                            mapKeys.put(mulChain, tableAlias);
                            builderParams.append(tableAlias).append(".eta_cve").append(" as ").append(tableAlias).append("$eta_cve").append(", ");
                            builderParams.append(tableAlias).append(".tab_cve").append(" as ").append(tableAlias).append("$tab_cve").append(", ");
                            builderParams.append(tableAlias).append(".eta_desc").append(" as ").append(tableAlias).append("$eta_desc").append(", ");
                            builderParams.append(tableAlias).append(".lta_nom_campo").append(" as ").append(tableAlias).append("$lta_nom_campo").append(", ");
                            builderParams.append(tableAlias).append(".lta_nom_tabla").append(" as ").append(tableAlias).append("$lta_nom_tabla").append(", ");
                            builderResult.append(" left join vw_multivaluada ").append(tableAlias).append(" on ").append(tableAlias).append(".lta_nom_tabla = '").append(parentTable).append("' and ").append(tableAlias).append(".lta_nom_campo = '").append(field.toUpperCase()).append("' and ").append(tableAlias).append(".eta_cve = ").append(parentAlias).append(".").append(field);
                            break;
                        }
                    }
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

            if (o != null && ReflectUtils.isValidField(f)) {
                builder.append(" AND ").append(key).append(".").append(f.getName()).append(" like ?");
            }
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
                break;
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

                if (referencedObject != null && referencedObject != newObject && ReflectUtils.isTableNormalized(referencedObject.getClass())) {
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
                                        map = DatabaseController.excecuteQuery(sql, params);

                                    } while (map != null && !map.isEmpty());

                                }

                                field.set(object, id);

                            } else if (field.getType().getSimpleName().equals(Integer.class
                                    .getSimpleName())) {
                                String sql = "select nvl(max(" + fieldId + "), 0) + 1  as max from " + keyObject.getTable_name();
                                Map<String, Object> map = DatabaseController.excecuteQuery(sql, null);
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

    //==========================================================================
    protected static List<KeyColumnObject> getCompleteList(Object mObject) {
        List<KeyColumnObject> list = InformationSchemaColumnsController.get(mObject);
        List<ViewObject> viewObjects = InformationSchemaViewsController.get(mObject);
        for (ViewObject viewObject : viewObjects) {
            KeyColumnObject columnObject = new KeyColumnObject();
            columnObject.setTable_name(viewObject.getTable_name());
            list.add(columnObject);
        }
        return list;
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
                    builder.append(subKeys[i]).append(" ");
                }

                return getReferencedObject(newObject, builder.toString().trim());

            } catch (NoSuchMethodException ex) {
                if (selectedKey.contains("_mul")) {
                    System.err.println("La multivaluada " + selectedKey + " no está declarada en el entity");
                } else {
                    ex.printStackTrace();
                }
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

}
