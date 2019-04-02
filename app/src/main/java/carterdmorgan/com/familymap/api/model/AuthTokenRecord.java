package carterdmorgan.com.familymap.api.model;

import java.util.Objects;

/**
 * Model class for the records of user and auth token association that are stored in the database.
 */
public class AuthTokenRecord {
    private String userID;
    private String token;

    public AuthTokenRecord(String userID, String token) {
        this.userID = userID;
        this.token = token;
    }

    public String getUserID() {
        return userID;
    }

    public void setUserID(String userID) {
        this.userID = userID;
    }

    public String getToken() {
        return token;
    }

    public void setToken(String token) {
        this.token = token;
    }

    @Override
    public String toString() {
        return "AuthTokenRecord{" +
                "userID='" + userID + '\'' +
                ", token='" + token + '\'' +
                '}';
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        AuthTokenRecord record = (AuthTokenRecord) o;
        return Objects.equals(userID, record.userID) &&
                Objects.equals(token, record.token);
    }

    @Override
    public int hashCode() {

        return Objects.hash(userID, token);
    }
}
