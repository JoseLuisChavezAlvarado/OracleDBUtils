package joseluisch.jdbc_utils.controllers;

import java.sql.Connection;
import joseluisch.jdbc_utils.database.Conexion;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import joseluisch.jdbc_utils.database.DatabseControllerTools;
import joseluisch.jdbc_utils.entities.UserTable;
import joseluisch.jdbc_utils.utils.ReflectUtils;

/**
 *
 * @author Jose Luis Ch.
 */
public class UserTableController extends DatabseControllerTools {

    public static List<UserTable> get() {

        List<UserTable> list = new ArrayList<>();

        Conexion conexion = new Conexion();
        PreparedStatement ps = null;
        Connection connection;
        ResultSet rs = null;

        try {

            String sql = " select table_name from user_tables order by table_name ";
            connection = conexion.getConexion();
            ps = connection.prepareStatement(sql);
            System.out.println("Excecuting... " + sql);
            rs = ps.executeQuery();

            Map<String, Object> mapResult = ReflectUtils.fillResultSet(rs, new UserTable());

            mapResult.entrySet().forEach((entry) -> {
                list.add((UserTable) entry.getValue());
            });

        } catch (Exception e) {
            e.printStackTrace();
        } finally {
            try {
                conexion.closeConexion(ps, rs);
            } catch (Exception ex) {
            }
        }

        return list;
    }

}
