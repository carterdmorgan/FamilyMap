package carterdmorgan.com.familymap.api.result;

/**
 * Model class that mimics the response body returned from <code>/clear</code>,
 * <code>/fill/{username}/{generations}</code>, and general errors.
 */
public class SimpleResult {

    private String message;

    public SimpleResult() {}

    public SimpleResult(String message) {
        this.message = message;
    }

    public String getMessage() {
        return message;
    }
}
