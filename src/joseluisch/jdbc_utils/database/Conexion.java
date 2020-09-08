//Clase estatica para el manejo de conexiones a base de datos
package joseluisch.jdbc_utils.database;

import java.sql.CallableStatement;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import javax.naming.Context;
import javax.naming.InitialContext;
import javax.sql.DataSource;

/**
 *
 * @author Jose Luis Ch.
 */
public class Conexion {

    private Connection conn = null;

    private final boolean WEBLOGIC_DB = DatabaseInstance.getInstance().isOracle();
    private final String CONNECTION_URL = DatabaseInstance.getConnectionUrl();
    private final String PASSWORD = DatabaseInstance.getInstance().getPassword();
    private final String USER = DatabaseInstance.getInstance().getUser();
    private final String JNDI = DatabaseInstance.getInstance().getJndi();

    public Connection getConexion() throws Exception {

        if (JNDI != null) {
            Context ctx = new InitialContext();
            DataSource ds = (DataSource) ctx.lookup(JNDI);
            conn = ds.getConnection();
        } else if (CONNECTION_URL != null && PASSWORD != null && USER != null) {
            if (!WEBLOGIC_DB) {
                conn = java.sql.DriverManager.getConnection(CONNECTION_URL, USER, PASSWORD);
            } else {
                conn = DriverManager.getConnection(CONNECTION_URL, USER, PASSWORD);
            }
        }

        return conn;
    }

    public boolean test() {

        boolean result = false;
        Connection c = null;

        try {
            c = getConexion();
            result = true;
        } catch (Exception e) {
            e.printStackTrace();
        } finally {
            try {
                c.close();
            } catch (Exception e) {
            }
        }

        return result;
    }

    public void closeConexion() {
        closeConexion(null, null, null);
    }

    public void closeConexion(PreparedStatement ps) {
        closeConexion(ps, null, null);
    }

    public void closeConexion(CallableStatement cs) {
        closeConexion(null, null, cs);
    }

    public void closeConexion(PreparedStatement ps, ResultSet rs) {
        closeConexion(ps, rs, null);
    }

    public void closeConexion(PreparedStatement ps, ResultSet rs, CallableStatement cs) {
        try {
            rs.close();
        } catch (Exception e) {
        }
        try {
            ps.close();
        } catch (Exception e) {
        }
        try {
            cs.close();
        } catch (Exception e) {
        }
        try {
            conn.close();
            conn = null;
        } catch (Exception e) {
        }
    }
}
