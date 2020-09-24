package joseluisch.jdbc_utils.database;

/**
 *
 * @author joseluischavez
 */
public class DatabaseInstance {

    private static DatabaseInstance instance;

    private String connectionUrl;
    private String password;
    private String user;
    private String jndi;
    private Boolean oracle;
    private Boolean mul;

    public static DatabaseInstance getInstance() {
        if (instance == null) {
            instance = new DatabaseInstance();
        }
        return instance;
    }

    public void init(String JNDI) {
        init(JNDI, false);
    }

    public void init(String JNDI, boolean mul) {
        getInstance().setJndi(JNDI);
        getInstance().setMul(mul);
    }

    public void init(String connectionUrl, String user, String password) {
        init(connectionUrl, user, password, false, false);
    }

    public void init(String connectionUrl, String user, String password, Boolean oracleDB, Boolean mul) {
        getInstance().setConnectionUrl(connectionUrl);
        getInstance().setPassword(password);
        getInstance().setOracle(oracleDB);
        getInstance().setUser(user);
        getInstance().setMul(mul);
    }

    public String getConnectionUrl() {
        return connectionUrl;
    }

    public void setConnectionUrl(String connectionUrl) {
        this.connectionUrl = connectionUrl;
    }

    public String getPassword() {
        return password;
    }

    public void setPassword(String password) {
        this.password = password;
    }

    public String getUser() {
        return user;
    }

    public void setUser(String user) {
        this.user = user;
    }

    public String getJndi() {
        return jndi;
    }

    public void setJndi(String jndi) {
        this.jndi = jndi;
    }

    public Boolean getOracle() {
        return oracle;
    }

    public void setOracle(Boolean oracle) {
        this.oracle = oracle;
    }

    public Boolean getMul() {
        return mul;
    }

    public void setMul(Boolean mul) {
        this.mul = mul;
    }

   

}
