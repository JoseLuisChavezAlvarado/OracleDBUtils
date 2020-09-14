package joseluisch.jdbc_utils.controllers.information_schema;

import joseluisch.jdbc_utils.database.Conexion;
import joseluisch.jdbc_utils.entities.KeyColumnObject;
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
public class InformationSchemaColumnsController {

    public static List<KeyColumnObject> get() {

        List<KeyColumnObject> list = new ArrayList<>();

        Conexion conexion = new Conexion();
        PreparedStatement ps = null;
        Connection connection;
        ResultSet rs = null;

        try {

            String sql = "select usc.table_name, usc.constraint_name, usc.constraint_type, usc.owner as table_schema, uccl.column_name, "
                    + "    uccr.table_name as referenced_table_name, uccr.column_name as referenced_column_name"
                    + " from user_constraints usc"
                    + " left join user_cons_columns uccl on uccl.constraint_name = usc.constraint_name"
                    + " left join user_cons_columns uccr on uccr.constraint_name = usc.r_constraint_name"
                    + " where 0 = 0"
                    + " and usc.constraint_name not like 'SYS_%'"
                    + " and usc.constraint_name not like '%==$0'"
                    + " and (usc.constraint_type = 'P' or usc.constraint_type = 'R')"
                    + " order by usc.table_name";

            connection = conexion.getConexion();
            ps = connection.prepareStatement(sql);
            System.out.println("Excecuting... " + sql);
            rs = ps.executeQuery();

            Map<String, Object> mapResult = ReflectUtils.fillResultSet(rs, new KeyColumnObject());

            mapResult.entrySet().forEach((entry) -> {
                list.add((KeyColumnObject) entry.getValue());
            });

        } catch (Exception e) {
            e.printStackTrace();
        } finally {
            conexion.closeConexion(ps, rs);
        }

        return list;
    }

    public static List<KeyColumnObject> get(Object object) {
        List<KeyColumnObject> list = new ArrayList<>();
        List<KeyColumnObject> originalList = DataInstance.getInstance().getKeyColumnObjectList();
        List<String> nodes = new ArrayList<>();

        nodes = ReflectUtils.getChildClasesUpdated(object.getClass());

        for (String node : nodes) {
            String objectName = StringUtils.toLowerScoreCase(node);
            for (KeyColumnObject keyObject : originalList) {
                if (StringUtils.toLowerScoreCase(keyObject.getTable_name()).equalsIgnoreCase(objectName)) {
                    list.add(keyObject);
                }
            }
        }

        return list;
    }

    //==========================================================================
    public static String getTableName(Object object) {

        List<KeyColumnObject> list = DataInstance.getInstance().getKeyColumnObjectList();
        String className = object.getClass().getSimpleName();

        for (KeyColumnObject keyObject : list) {
            String tableName = StringUtils.toUpperCamelCase(StringUtils.toLowerScoreCase(keyObject.getTable_name()));
            if (tableName.equals(className)) {
                return keyObject.getTable_name();
            }
        }

        return null;
    }
}
