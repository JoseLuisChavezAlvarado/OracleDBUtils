package joseluisch.jdbc_utils.controllers.information_schema;

import joseluisch.jdbc_utils.database.Conexion;
import joseluisch.jdbc_utils.entities.ViewObject;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import joseluisch.jdbc_utils.singleton.DataInstance;
import joseluisch.jdbc_utils.utils.ReflectUtils;
import joseluisch.jdbc_utils.utils.StringUtils;

/**
 *
 * @author joseluischavez
 */
public class InformationSchemaViewsController {

    public static List<ViewObject> get() {

        List<ViewObject> list = new ArrayList<>();

        Conexion conexion = new Conexion();
        Connection connection = null;
        PreparedStatement ps = null;
        ResultSet rs = null;

        try {

            String sql = " select usv.view_name as TABLE_NAME from user_views usv ";
            connection = conexion.getConexion();
            ps = connection.prepareStatement(sql);
            System.out.println("Excecuting... " + sql);
            rs = ps.executeQuery();

            Map<String, Object> mapResult = ReflectUtils.fillResultSet(rs, new ViewObject());

            mapResult.entrySet().forEach((entry) -> {
                list.add((ViewObject) entry.getValue());
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

    //==========================================================================
    public static String getTableName(Object object) {

        List<ViewObject> list = DataInstance.getInstance().getViewObjectList();
        String className = object.getClass().getSimpleName();

        for (ViewObject viewObject : list) {
            String tableName = StringUtils.toUpperCamelCase(StringUtils.toLowerScoreCase(viewObject.getTABLE_NAME()));
            if (tableName.equals(className)) {
                return viewObject.getTABLE_NAME();
            }
        }

        return null;
    }

}
