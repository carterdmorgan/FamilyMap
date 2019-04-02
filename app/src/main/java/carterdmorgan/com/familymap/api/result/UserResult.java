package carterdmorgan.com.familymap.api.result;

import android.os.Parcel;
import android.os.Parcelable;

/**
 * Model class that mimics the response body returned from <code>/user/login</code> and <code>/user/register</code>
 */
public class UserResult implements Parcelable {
    private String authToken;
    private String userName;
    private String personID;

    public UserResult() {}

    public UserResult(String authToken, String userName, String personID) {
        this.authToken = authToken;
        this.userName = userName;
        this.personID = personID;
    }

    public String getAuthToken() {
        return authToken;
    }

    public void setAuthToken(String authToken) {
        this.authToken = authToken;
    }

    public String getUserName() {
        return userName;
    }

    public void setUserName(String userName) {
        this.userName = userName;
    }

    public String getPersonID() {
        return personID;
    }

    public void setPersonID(String personID) {
        this.personID = personID;
    }

    @Override
    public String toString() {
        return "UserResult{" +
                "authToken='" + authToken + '\'' +
                ", userName='" + userName + '\'' +
                ", personID='" + personID + '\'' +
                '}';
    }

    protected UserResult(Parcel in) {
        authToken = in.readString();
        userName = in.readString();
        personID = in.readString();
    }

    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(Parcel dest, int flags) {
        dest.writeString(authToken);
        dest.writeString(userName);
        dest.writeString(personID);
    }

    @SuppressWarnings("unused")
    public static final Parcelable.Creator<UserResult> CREATOR = new Parcelable.Creator<UserResult>() {
        @Override
        public UserResult createFromParcel(Parcel in) {
            return new UserResult(in);
        }

        @Override
        public UserResult[] newArray(int size) {
            return new UserResult[size];
        }
    };
}