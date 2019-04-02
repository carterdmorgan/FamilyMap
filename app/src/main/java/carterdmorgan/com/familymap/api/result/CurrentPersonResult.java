package carterdmorgan.com.familymap.api.result;



import android.os.Parcel;
import android.os.Parcelable;

import java.util.ArrayList;

import carterdmorgan.com.familymap.api.model.Person;

/**
 * Model class that mimics the response body returned from <code>/person</code>
 */
public class CurrentPersonResult implements Parcelable {
    private ArrayList<Person> data;

    public CurrentPersonResult() {}

    public CurrentPersonResult(ArrayList<Person> data) {
        this.data = data;
    }

    public ArrayList<Person> getData() {
        return data;
    }

    public void setData(ArrayList<Person> data) {
        this.data = data;
    }

    @Override
    public String toString() {
        return "CurrentPersonResult{" +
                "data=" + data +
                '}';
    }

    protected CurrentPersonResult(Parcel in) {
        if (in.readByte() == 0x01) {
            data = new ArrayList<Person>();
            in.readList(data, Person.class.getClassLoader());
        } else {
            data = null;
        }
    }

    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(Parcel dest, int flags) {
        if (data == null) {
            dest.writeByte((byte) (0x00));
        } else {
            dest.writeByte((byte) (0x01));
            dest.writeList(data);
        }
    }

    @SuppressWarnings("unused")
    public static final Parcelable.Creator<CurrentPersonResult> CREATOR = new Parcelable.Creator<CurrentPersonResult>() {
        @Override
        public CurrentPersonResult createFromParcel(Parcel in) {
            return new CurrentPersonResult(in);
        }

        @Override
        public CurrentPersonResult[] newArray(int size) {
            return new CurrentPersonResult[size];
        }
    };
}