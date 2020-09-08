package joseluisch.jdbc_utils.database;

import java.sql.Connection;
import javax.naming.Context;
import javax.naming.InitialContext;
import javax.sql.DataSource;

/**
 *
 * @author Jose Luis Ch.
 */
public class StaticConection {

    public static Connection getConexion() throws Exception {
        Context ctx = new InitialContext();
        DataSource ds = (DataSource) ctx.lookup("java:/escdesa");
        return ds.getConnection();
    }

}
