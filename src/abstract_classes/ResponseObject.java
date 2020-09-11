package abstract_classes;

/**
 *
 * @author Jose Luis Chavez
 */
public class ResponseObject<Response, Exception> {

    private Response response;
    private Exception exception;

    public ResponseObject() {
    }

    public ResponseObject(Response response, Exception exception) {
        this.response = response;
        this.exception = exception;
    }

    public Response getResponse() {
        return response;
    }

    public void setResponse(Response response) {
        this.response = response;
    }

    public Exception getException() {
        return exception;
    }

    public void setException(Exception exception) {
        this.exception = exception;
    }

}
