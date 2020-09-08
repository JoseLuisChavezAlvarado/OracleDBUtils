package joseluisch.jdbc_utils.database;

/**
 *
 * @author joseluischavez
 */
public class DatabaseInstance {

    private static DatabaseInstance instance;

    private static String connectionUrl;
    private static String password;
    private static boolean oracle;
    private static String user;
    private static String jndi;

    public static DatabaseInstance getInstance() {
        if (instance == null) {
            instance = new DatabaseInstance();
        }
        return instance;
    }

    public static void init(String JNDI) {
        getInstance().setJndi(JNDI);
    }

    public static void init(String connectionUrl, String user, String password) {
        init(connectionUrl, user, password, false);
    }

    public static void init(String connectionUrl, String user, String password, boolean oracleDB) {
        getInstance().setConnectionUrl(connectionUrl);
        getInstance().setPassword(password);
        getInstance().setOracle(oracleDB);
        getInstance().setUser(user);
    }

    public static String getJndi() {
        return jndi;
    }

    private static void setJndi(String jndi) {
        DatabaseInstance.jndi = jndi;
    }

    public static String getConnectionUrl() {
        return connectionUrl;
    }

    public static void setConnectionUrl(String connectionUrl) {
        DatabaseInstance.connectionUrl = connectionUrl;
    }

    public static String getPassword() {
        return password;
    }

    public static void setPassword(String password) {
        DatabaseInstance.password = password;
    }

    public static String getUser() {
        return user;
    }

    public static void setUser(String user) {
        DatabaseInstance.user = user;
    }

    public static boolean isOracle() {
        return oracle;
    }

    public static void setOracle(boolean oracle) {
        DatabaseInstance.oracle = oracle;
    }

}
