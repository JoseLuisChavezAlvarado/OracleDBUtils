package joseluisch.jdbc_utils.controllers.information_schema;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import joseluisch.jdbc_utils.database.Conexion;
import joseluisch.jdbc_utils.entities.ViewObject;
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

    public static List<ViewObject> get(Object object) {
        List<ViewObject> list = new ArrayList<>();
        List<ViewObject> originalList = DataInstance.getInstance().getViewObjectList();

        String table_name = StringUtils.toLowerScoreCase(object.getClass().getSimpleName());
        for (ViewObject viewObject : originalList) {
            if (StringUtils.toLowerScoreCase(viewObject.getTable_name()).equalsIgnoreCase(table_name)) {
                list.add(viewObject);
            }
        }

        return list;
    }
}
