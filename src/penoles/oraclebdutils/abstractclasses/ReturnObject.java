package penoles.oraclebdutils.abstractclasses;

/**
 *
 * @author Jose Luis Ch.
 */
public class ReturnObject {

    String token;
    Object data;

    public ReturnObject() {
    }

    public ReturnObject(String token, Object data) {
        this.token = token;
        this.data = data;
    }

    public String getToken() {
        return token;
    }

    public void setToken(String token) {
        this.token = token;
    }

    public Object getData() {
        return data;
    }

    public void setData(Object data) {
        this.data = data;
    }

}
