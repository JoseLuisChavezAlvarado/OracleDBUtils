package penoles.oraclebdutils.controllers;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import penoles.oraclebdutils.database.Conexion;
import penoles.oraclebdutils.entities.VwMultivaluada;
import penoles.oraclebdutils.utils.ReflectUtils;

/**
 *
 * @author Jose Luis Ch.
 */
public class WMultivluadaController {

    public static List<VwMultivaluada> get() {

        List<VwMultivaluada> list = new ArrayList<>();

        Conexion conexion = new Conexion();
        PreparedStatement ps = null;
        Connection connection;
        ResultSet rs = null;

        try {

            String query = " select * from vw_multivaluada ";

            connection = conexion.getConexion();
            ps = connection.prepareStatement(query);
            System.out.println("Excecuting... " + query);
            rs = ps.executeQuery();

            Map<String, Object> mapResult = ReflectUtils.fillResultSet(rs, new VwMultivaluada());

            mapResult.entrySet().forEach((entry) -> {
                VwMultivaluada vm = (VwMultivaluada) entry.getValue();
                boolean isAdded = false;
                for (VwMultivaluada multivaluada : list) {
                    if (multivaluada.getLta_nom_campo().equals(vm.getLta_nom_campo()) && multivaluada.getLta_nom_tabla().equals(vm.getLta_nom_tabla())) {
                        isAdded = true;
                        break;
                    }
                }

                if (!isAdded) {
                    list.add(vm);
                }
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
