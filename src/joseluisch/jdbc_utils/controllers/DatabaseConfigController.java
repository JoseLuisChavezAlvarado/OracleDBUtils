package joseluisch.jdbc_utils.controllers;

import joseluisch.jdbc_utils.database.Conexion;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.util.HashMap;
import java.util.Map;
import joseluisch.jdbc_utils.entities.TableDetails;

/**
 *
 * @author joseluischavez
 */
public class DatabaseConfigController {

    public static Map<String, TableDetails> getTableDetails(String tableName) {

        Map<String, TableDetails> map = new HashMap<>();
        Conexion conexion = new Conexion();

        Connection connection = null;
        PreparedStatement ps = null;
        ResultSet rs = null;

        try {
            String sql = "select cols.column_name as field, data_type as type, cols.nullable "
                    + " from all_tab_columns cols"
                    + " where table_name = '" + tableName + "'";

            System.out.println("Excecuting... " + sql);

            connection = conexion.getConexion();
            ps = connection.prepareStatement(sql);
            rs = ps.executeQuery();

            while (rs.next()) {
                TableDetails tableDetails = new TableDetails();
                tableDetails.setField(rs.getString("field").replace("#", "$").toLowerCase());
                tableDetails.setType(rs.getString("type"));
                tableDetails.setNullable(rs.getString("nullable"));
                map.put(tableDetails.getField(), tableDetails);
            }

            fillPrimaryKey(map, tableName);

        } catch (Exception e) {
            e.printStackTrace();
        } finally {
            conexion.closeConexion(ps, rs);
        }

        return map;
    }

    private static void fillPrimaryKey(Map<String, TableDetails> map, String tableName) throws Exception {

        Conexion conexion = new Conexion();
        Connection connection = null;
        PreparedStatement ps = null;
        ResultSet rs = null;

        try {
            connection = conexion.getConexion();

            String sql = "SELECT cols.table_name, cols.column_name"
                    + " FROM all_constraints cons, all_cons_columns cols"
                    + " WHERE 0 = 0"
                    + " AND cons.constraint_type = 'P'"
                    + " AND cons.constraint_name = cols.constraint_name"
                    + " AND cons.owner = cols.owner"
                    + " and cols.table_name = ? ";

            ps = connection.prepareStatement(sql);
            ps.setString(1, tableName);

            System.out.println("Excecuting... " + sql);
            rs = ps.executeQuery();

            while (rs.next()) {
                String column_name = rs.getString("column_name");

                for (Map.Entry entry : map.entrySet()) {
                    TableDetails details = (TableDetails) entry.getValue();
                    if (details.getField().equals(column_name)) {
                        details.setKey("PRIMARY");
                        break;
                    }
                }
            }

        } catch (Exception e) {
            e.printStackTrace();
        } finally {
            conexion.closeConexion(ps, rs);
        }

    }

}
