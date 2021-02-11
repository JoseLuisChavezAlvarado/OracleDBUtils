package penoles.oraclebdutils.database.controller;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.TreeMap;
import java.util.UUID;
import penoles.oraclebdutils.abstractclasses.ResponseObject;
import penoles.oraclebdutils.database.Conexion;
import penoles.oraclebdutils.utils.StringUtils;

/**
 *
 * @author Jose Luis Ch.
 */
public class DatabaseController extends DatabaseControllerTools {

    public static ResponseObject<List<Object>, Exception> select(Object mObject) {
        return select(mObject, null);
    }

    public static ResponseObject<List<Object>, Exception> select(Object mObject, boolean equals) {
        return select(mObject, null, null, null, equals);
    }

    public static ResponseObject<List<Object>, Exception> select(Object mObject, String filterVariable) {
        return select(mObject, filterVariable, null, null, false);
    }

    public static ResponseObject<List<Object>, Exception> select(Object mObject, String filterVariable, Integer page, Integer pageLength) {
        return select(mObject, filterVariable, page, pageLength, false);
    }

    public static ResponseObject<List<Object>, Exception> select(Object mObject, String filterVariable, Integer page, Integer pageLength, boolean equals) {

        List<Object> list = null;
        Exception exception = null;

        ResultSet rs = null;
        PreparedStatement ps = null;
        Conexion conexion = new Conexion();

        try {
            Map<String, String> mapKeys = new HashMap<>();
            String sql = getSQL(mapKeys, mObject, filterVariable, equals, page, pageLength);
            ps = conexion.getConexion().prepareStatement(sql);
            prepareStatementSelect(mapKeys, ps, mObject, equals);
            System.out.println("Excecuting... " + sql);
            rs = ps.executeQuery();
            list = fillResultSet(rs, mObject, mapKeys);
        } catch (Exception e) {
            e.printStackTrace();
            exception = e;
        } finally {
            conexion.closeConexion(ps, rs);
        }

        return new ResponseObject<>(list, exception);
    }

    //==========================================================================
    public static ResponseObject<List<Object>, Exception> insert(Object mObject) {

        List<Object> list = null;
        Exception exception = null;
        PreparedStatement ps = null;
        Conexion conexion = new Conexion();

        try {
            String sql = addSQL(mObject);
            ps = conexion.getConexion().prepareStatement(sql);
            //System.out.print.println("Excecuting... " + sql);
            prepareStatementInsert(ps, mObject);
            ps.executeUpdate();

            list = new ArrayList<>();
            list.add(mObject);
        } catch (Exception e) {
            e.printStackTrace();
            exception = e;
        } finally {
            conexion.closeConexion(ps);
        }

        if (exception != null) {
            return new ResponseObject<>(list, exception);
        } else {
            return select(mObject);
        }

        //======================================================================
    }

    public static ResponseObject<Object, Exception> update(Object mObject) {

        Object result = false;
        Exception exception = null;
        PreparedStatement ps = null;
        Conexion conexion = new Conexion();

        try {
            String fieldId = getFieldId(mObject);
            String sql = updateSQL(mObject, fieldId);
            ps = conexion.getConexion().prepareStatement(sql);
            prepareStatementUpdate(ps, mObject, fieldId);
            //System.out.print.println("Excecuting... " + sql);
            ps.executeUpdate();
            result = mObject;
        } catch (Exception e) {
            e.printStackTrace();
            exception = e;
        } finally {
            conexion.closeConexion(ps);
        }

        return new ResponseObject<>(result, exception);
    }

    public static ResponseObject<Object, Exception> delete(Object mObject) {

        Object result = false;
        Exception exception = null;
        PreparedStatement ps = null;
        Conexion conexion = new Conexion();

        try {
            String fieldId = getFieldId(mObject);
            String sql = deleteSQL(mObject, fieldId);
            ps = conexion.getConexion().prepareStatement(sql);
            prepareStatementDelete(ps, mObject, fieldId);
            //System.out.print.println("Excecuting... " + sql);
            ps.executeUpdate();
            result = mObject;
        } catch (Exception e) {
            e.printStackTrace();
            exception = e;
        } finally {
            conexion.closeConexion(ps);
        }

        return new ResponseObject<>(result, exception);
    }

    //==========================================================================
    public static ResponseObject<Object[], Exception> insert(Object[] objects) {

        Object[] result = null;
        Exception exception = null;

        PreparedStatement ps = null;
        Connection connection = null;
        Conexion conexion = new Conexion();

        if (objects != null && objects.length > 0) {
            try {
                connection = conexion.getConexion();
                connection.setAutoCommit(false);

                String sql = addSQL(objects[0]);
                ps = connection.prepareStatement(sql);
                //System.out.print.println("Excecuting Bulk... " + sql);

                for (Object object : objects) {
                    prepareStatementInsert(ps, object);
                    ps.addBatch();
                }

                ps.executeBatch();
                connection.commit();
                result = objects;

            } catch (Exception e) {
                e.printStackTrace();
                exception = e;
            } finally {
                conexion.closeConexion(ps);
            }
        }

        return new ResponseObject<>(result, exception);
    }

    public static ResponseObject<Object[], Exception> update(Object[] objects) {

        Object[] result = null;
        Exception exception = null;

        PreparedStatement ps = null;
        Connection connection = null;
        Conexion conexion = new Conexion();

        if (objects != null && objects.length > 0) {
            try {
                connection = conexion.getConexion();
                connection.setAutoCommit(false);

                String fieldId = getFieldId(objects[0]);
                String sql = updateSQL(objects[0], fieldId);
                ps = connection.prepareStatement(sql);
                //System.out.print.println("Excecuting Bulk... " + sql);

                for (Object object : objects) {
                    prepareStatementUpdate(ps, object, fieldId);
                    ps.addBatch();
                }

                ps.executeBatch();
                connection.commit();
                result = objects;

            } catch (Exception e) {
                e.printStackTrace();
                exception = e;
            } finally {
                conexion.closeConexion(ps);
            }
        }

        return new ResponseObject<>(result, exception);
    }

    public static ResponseObject<Object[], Exception> delete(Object[] objects) {

        Object[] result = null;
        Exception exception = null;

        PreparedStatement ps = null;
        Connection connection = null;
        Conexion conexion = new Conexion();

        if (objects != null && objects.length > 0) {
            try {

                connection = conexion.getConexion();
                connection.setAutoCommit(false);

                String fieldId = getFieldId(objects[0]);
                String sql = deleteSQL(objects[0], fieldId);
                ps = connection.prepareStatement(sql);
                //System.out.print.println("Excecuting Bulk... " + sql);

                for (Object object : objects) {
                    prepareStatementDelete(ps, object, fieldId);
                    ps.addBatch();
                }

                ps.executeBatch();
                connection.commit();
                result = objects;
            } catch (Exception e) {
                e.printStackTrace();
                exception = e;
            } finally {
                conexion.closeConexion(ps);
            }
        }

        return new ResponseObject<>(result, exception);
    }

    //==========================================================================
    public static ResponseObject<Integer, Exception> getLength(Object mObject) {
        return getLength(mObject, false);
    }

    public static ResponseObject<Integer, Exception> getLength(Object mObject, boolean equals) {

        Integer result = null;
        Exception exception = null;

        ResultSet rs = null;
        PreparedStatement ps = null;
        Conexion conexion = new Conexion();

        try {

            Map<String, String> mapKeys = new HashMap<>();
            String sql = getLengthSQL(mapKeys, mObject, equals);
            ps = conexion.getConexion().prepareStatement(sql);
            prepareStatementSelect(mapKeys, ps, mObject, equals);
            System.out.println("Excecuting... " + sql);
            rs = ps.executeQuery();

            while (rs.next()) {
                result = rs.getInt(1);
            }

        } catch (Exception e) {
            e.printStackTrace();
            exception = e;
        } finally {
            conexion.closeConexion(ps, rs);
        }

        return new ResponseObject<>(result, exception);
    }

    //==========================================================================
    public static Map<String, Object> excecuteQuery(String query, List<Object> parameters) {

        Map<String, Object> map = new HashMap<>();

        ResultSet rs = null;
        PreparedStatement ps = null;
        Conexion conexion = new Conexion();

        try {
            ps = conexion.getConexion().prepareStatement(query);

            if (parameters != null) {
                int i = 1;
                for (Object parameter : parameters) {
                    ps.setObject(i++, parameter);
                }
            }

            System.out.println("Excecuting... " + query);
            rs = ps.executeQuery();

            while (rs.next()) {
                Map<String, Object> currentMap = new TreeMap<>();
                for (int i = 1; i <= rs.getMetaData().getColumnCount(); i++) {
                    String className = "";
                    try {
                        className = StringUtils.toUpperCamelCase(rs.getMetaData().getTableName(i));
                    } catch (Exception e) {
                    }
                    String key = !className.isEmpty() ? (className + "_" + rs.getMetaData().getColumnLabel(i)) : rs.getMetaData().getColumnLabel(i);
                    Object value = rs.getObject(rs.getMetaData().getColumnLabel(i));
                    currentMap.put(key, value);
                }

                map.put(UUID.randomUUID().toString(), currentMap);
            }

        } catch (Exception e) {
            e.printStackTrace();
        } finally {
            conexion.closeConexion(ps, rs);
        }

        return map;
    }

    public static boolean excecute(String query, List<Object> parameters) {

        boolean result = false;
        PreparedStatement ps = null;
        Conexion conexion = new Conexion();

        try {
            ps = conexion.getConexion().prepareStatement(query);
            if (parameters != null) {
                int i = 1;
                for (Object parameter : parameters) {
                    ps.setObject(i++, parameter);
                }
            }

            System.out.println("Excecuting... " + query);
            result = ps.execute();

        } catch (Exception e) {
            e.printStackTrace();
        } finally {
            conexion.closeConexion(ps);
        }

        return result;
    }

}
