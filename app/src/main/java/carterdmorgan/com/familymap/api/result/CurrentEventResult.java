package carterdmorgan.com.familymap.api.result;

import android.os.Parcel;
import android.os.Parcelable;

import java.util.ArrayList;

import carterdmorgan.com.familymap.api.model.Event;

/**
 * Model class that mimics the response body returned from <code>/event</code>
 */
public class CurrentEventResult implements Parcelable {
    private ArrayList<Event> data;

    public CurrentEventResult() { }

    public CurrentEventResult(ArrayList<Event> data) {
        this.data = data;
    }

    public ArrayList<Event> getData() {
        return data;
    }

    public void setData(ArrayList<Event> data) {
        this.data = data;
    }

    @Override
    public String toString() {
        return "CurrentEventResult{" +
                "data=" + data +
                '}';
    }

    protected CurrentEventResult(Parcel in) {
        if (in.readByte() == 0x01) {
            data = new ArrayList<Event>();
            in.readList(data, Event.class.getClassLoader());
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
    public static final Parcelable.Creator<CurrentEventResult> CREATOR = new Parcelable.Creator<CurrentEventResult>() {
        @Override
        public CurrentEventResult createFromParcel(Parcel in) {
            return new CurrentEventResult(in);
        }

        @Override
        public CurrentEventResult[] newArray(int size) {
            return new CurrentEventResult[size];
        }
    };
}
