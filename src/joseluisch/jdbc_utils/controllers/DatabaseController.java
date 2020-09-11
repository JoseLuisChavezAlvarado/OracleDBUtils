package joseluisch.jdbc_utils.controllers;

import abstract_classes.ResponseObject;
import joseluisch.jdbc_utils.database.Conexion;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.TreeMap;
import java.util.UUID;
import java.util.logging.Level;
import java.util.logging.Logger;
import joseluisch.jdbc_utils.database.DatabseControllerTools;
import joseluisch.jdbc_utils.utils.StringUtils;

/**
 *
 * @author Jose Luis Ch.
 */
public class DatabaseController extends DatabseControllerTools {

    public static ResponseObject<Map<String, Object>, Exception> select(Object mObject) {
        Map<String, Object> map = null;
        Exception exception = null;

        ResultSet rs = null;
        PreparedStatement ps = null;
        Conexion conexion = new Conexion();

        try {
            Map<String, String> mapKeys = new HashMap<>();
            String sql = getSQL(mapKeys, mObject);
            ps = conexion.getConexion().prepareStatement(sql);
            prepareStatementSelect(ps, mObject);
            System.out.println("Excecuting... " + sql);
            rs = ps.executeQuery();
            map = fillResultSet(rs, mObject, mapKeys);
        } catch (Exception e) {
            e.printStackTrace();
            exception = e;
        } finally {
            conexion.closeConexion(ps, rs);
        }

        return new ResponseObject<>(map, exception);
    }

    public static ResponseObject<Boolean, Exception> insert(Object mObject) {

        boolean result = false;
        Exception exception = null;
        PreparedStatement ps = null;
        Conexion conexion = new Conexion();

        try {
            String sql = addSQL(mObject);
            ps = conexion.getConexion().prepareStatement(sql);
            System.out.println("Excecuting... " + sql);
            prepareStatementInsert(ps, mObject);
            result = ps.executeUpdate() > 0;
        } catch (Exception e) {
            e.printStackTrace();
            exception = e;
        } finally {
            conexion.closeConexion(ps);
        }

        return new ResponseObject<>(result, exception);
    }

    public static ResponseObject<Boolean, Exception> update(Object mObject) {

        boolean result = false;
        Exception exception = null;
        PreparedStatement ps = null;
        Conexion conexion = new Conexion();

        try {
            String fieldId = getFieldId(mObject);
            String sql = updateSQL(mObject, fieldId);
            ps = conexion.getConexion().prepareStatement(sql);
            prepareStatementUpdate(ps, mObject, fieldId);
            System.out.println("Excecuting... " + sql);
            result = ps.executeUpdate() > 0;
        } catch (Exception e) {
            e.printStackTrace();
            exception = e;
        } finally {
            conexion.closeConexion(ps);
        }

        return new ResponseObject<>(result, exception);
    }

    public static ResponseObject<Boolean, Exception> delete(Object mObject) {

        boolean result = false;
        Exception exception = null;
        PreparedStatement ps = null;
        Conexion conexion = new Conexion();

        try {

            String fieldId = getFieldId(mObject);
            String sql = deleteSQL(mObject, fieldId);
            ps = conexion.getConexion().prepareStatement(sql);
            prepareStatementDelete(ps, mObject, fieldId);

            System.out.println("Excecuting... " + sql);

            result = ps.executeUpdate() > 0;
        } catch (Exception e) {
            e.printStackTrace();
            exception = e;
        } finally {
            conexion.closeConexion(ps);
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
