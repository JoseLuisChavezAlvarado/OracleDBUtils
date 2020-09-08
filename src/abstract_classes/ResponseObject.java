package abstract_classes;

/**
 *
 * @author Jose Luis Chavez
 */
public class ResponseObject<Response, Exception> {

    private Object response;
    private Exception exception;

    public ResponseObject() {
    }

    public ResponseObject(Object response, Exception exception) {
        this.response = response;
        this.exception = exception;
    }

    public Object getResponse() {
        return response;
    }

    public void setResponse(Object response) {
        this.response = response;
    }

    public Exception getException() {
        return exception;
    }

    public void setException(Exception exception) {
        this.exception = exception;
    }

}
