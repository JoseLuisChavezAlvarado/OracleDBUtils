package penoles.oraclebdutils.controllers;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import penoles.oraclebdutils.database.Conexion;
import penoles.oraclebdutils.database.controller.DatabaseControllerTools;
import penoles.oraclebdutils.entities.UserTable;
import penoles.oraclebdutils.utils.ReflectUtils;

/**
 *
 * @author Jose Luis Ch.
 */
public class UserTableController extends DatabaseControllerTools {

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
