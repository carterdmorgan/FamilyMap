package carterdmorgan.com.familymap.api.request;

/**
 * Model class that mimics the request body posted to <code>/user/login</code>
 */
public class LoginUserRequest {
    private String userName;
    private String password;

    public String getUserName() {
        return userName;
    }

    public void setUserName(String userName) {
        this.userName = userName;
    }

    public String getPassword() {
        return password;
    }

    public void setPassword(String password) {
        this.password = password;
    }
}
