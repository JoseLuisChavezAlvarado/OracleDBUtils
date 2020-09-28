package penoles.oraclebdutils.database.controller;

import java.lang.reflect.Field;
import java.sql.Date;
import java.sql.PreparedStatement;
import java.sql.SQLException;
import java.util.List;
import java.util.Map;
import penoles.oraclebdutils.information_schema.InformationSchemaColumnsController;
import penoles.oraclebdutils.information_schema.InformationSchemaViewsController;
import penoles.oraclebdutils.entities.KeyColumnObject;
import penoles.oraclebdutils.entities.TableDetails;
import penoles.oraclebdutils.entities.ViewObject;
import penoles.oraclebdutils.singleton.DataInstance;
import penoles.oraclebdutils.utils.ReflectUtils;
import penoles.oraclebdutils.utils.StringUtils;

/**
 *
 * @author Jose Luis Chavez
 */
public class DatabaseControllerTools extends DatabaseControllerToolsOperations {

    //==========================================================================
    protected static String getSQL(Map<String, String> mapKeys, Object mObject) throws ClassNotFoundException, InstantiationException, IllegalAccessException {

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

            if (o != null) {
                if (ReflectUtils.isValidField(f)) {
                    builder.append(f.getName()).append(",");
                    builderValues.append("?,");
                }
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
    protected static void prepareStatementSelect(PreparedStatement ps, Object mObject) throws IllegalArgumentException, IllegalAccessException, SQLException {

        Class aClass = mObject.getClass();

        int index = 1;
        for (Field f : aClass.getDeclaredFields()) {
            f.setAccessible(true);
            Object object = f.get(mObject);
            if (object != null && ReflectUtils.isValidField(f)) {
                ps.setObject(index++, object);
            }
        }
    }

    protected static void prepareStatementInsert(PreparedStatement ps, Object mObject) throws IllegalArgumentException, IllegalAccessException, SQLException {

        String tableName = InformationSchemaColumnsController.getTableName(mObject);
        List<TableDetails> list = DataInstance.getInstance().getTableDetailsList(tableName);

        fillId(mObject);
        Class aClass = mObject.getClass();

        int index = 1;
        for (Field f : aClass.getDeclaredFields()) {
            f.setAccessible(true);
            Object fieldValue = f.get(mObject);
            if (fieldValue != null && ReflectUtils.isValidField(f)) {

                boolean isDate = false;
                for (TableDetails details : list) {
                    if (f.getName().equalsIgnoreCase(details.getField())) {
                        if (details.getType().toLowerCase().contains("date") || details.getType().toLowerCase().contains("timestamp")) {
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
}
