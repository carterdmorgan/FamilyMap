package carterdmorgan.com.familymap.api.model;

import android.os.Parcel;
import android.os.Parcelable;

import com.google.android.gms.maps.model.BitmapDescriptorFactory;

import java.util.HashMap;
import java.util.Map;
import java.util.Objects;

public class Event implements Parcelable {
    private String eventID;
    private String descendant;
    private String personID;
    private float latitude;
    private float longitude;
    private String country;
    private String city;
    private String eventType;
    private int year;

    public Event() {}

    public Event(String eventID, String descendant, String personID, float latitude, float longitude, String country,
                 String city, String eventType, int year) {
        this.eventID = eventID;
        this.descendant = descendant;
        this.personID = personID;
        this.latitude = latitude;
        this.longitude = longitude;
        this.country = country;
        this.city = city;
        this.eventType = eventType;
        this.year = year;
    }

    public static Map<String, Float> getTypeColorMap() {
        Map<String, Float> map = new HashMap<>();
        map.put("Birth", BitmapDescriptorFactory.HUE_AZURE);
        map.put("Christening", BitmapDescriptorFactory.HUE_ORANGE);
        map.put("Baptism", BitmapDescriptorFactory.HUE_YELLOW);
        map.put("Joined Military", BitmapDescriptorFactory.HUE_GREEN);
        map.put("First Communion", BitmapDescriptorFactory.HUE_CYAN);
        map.put("Won Lottery", BitmapDescriptorFactory.HUE_RED);
        map.put("Bought First Home", BitmapDescriptorFactory.HUE_BLUE);
        map.put("Saved a Life", BitmapDescriptorFactory.HUE_VIOLET);
        map.put("Started First Job", BitmapDescriptorFactory.HUE_MAGENTA);
        map.put("Death", BitmapDescriptorFactory.HUE_ROSE);
        map.put("Marriage", 45.0f);

        return map;
    }

    public String getEventID() {
        return eventID;
    }

    public void setEventID(String eventID) {
        this.eventID = eventID;
    }

    public String getDescendant() {
        return descendant;
    }

    public void setDescendant(String descendant) {
        this.descendant = descendant;
    }

    public String getPersonID() {
        return personID;
    }

    public void setPersonID(String personID) {
        this.personID = personID;
    }

    public float getLatitude() {
        return latitude;
    }

    public void setLatitude(float latitude) {
        this.latitude = latitude;
    }

    public float getLongitude() {
        return longitude;
    }

    public void setLongitude(float longitude) {
        this.longitude = longitude;
    }

    public String getCountry() {
        return country;
    }

    public void setCountry(String country) {
        this.country = country;
    }

    public String getCity() {
        return city;
    }

    public void setCity(String city) {
        this.city = city;
    }

    public String getEventType() {
        return eventType;
    }

    public void setEventType(String eventType) {
        this.eventType = eventType;
    }

    public int getYear() {
        return year;
    }

    public void setYear(int year) {
        this.year = year;
    }

    public static Event randomLifeEvent(Person person) {
        return null;
    }

    @Override
    public String toString() {
        return "Event{" +
                "eventID='" + eventID + '\'' +
                ", descendant='" + descendant + '\'' +
                ", personID='" + personID + '\'' +
                ", latitude=" + latitude +
                ", longitude=" + longitude +
                ", country='" + country + '\'' +
                ", city='" + city + '\'' +
                ", eventType='" + eventType + '\'' +
                ", year=" + year +
                '}';
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        Event event = (Event) o;
        return Float.compare(event.latitude, latitude) == 0 &&
                Float.compare(event.longitude, longitude) == 0 &&
                year == event.year &&
                Objects.equals(eventID, event.eventID) &&
                Objects.equals(descendant, event.descendant) &&
                Objects.equals(personID, event.personID) &&
                Objects.equals(country, event.country) &&
                Objects.equals(city, event.city) &&
                Objects.equals(eventType, event.eventType);
    }

    @Override
    public int hashCode() {
        return Objects.hash(eventID, descendant, personID, latitude, longitude, country, city, eventType, year);
    }

    protected Event(Parcel in) {
        eventID = in.readString();
        descendant = in.readString();
        personID = in.readString();
        latitude = in.readFloat();
        longitude = in.readFloat();
        country = in.readString();
        city = in.readString();
        eventType = in.readString();
        year = in.readInt();
    }

    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(Parcel dest, int flags) {
        dest.writeString(eventID);
        dest.writeString(descendant);
        dest.writeString(personID);
        dest.writeFloat(latitude);
        dest.writeFloat(longitude);
        dest.writeString(country);
        dest.writeString(city);
        dest.writeString(eventType);
        dest.writeInt(year);
    }

    @SuppressWarnings("unused")
    public static final Parcelable.Creator<Event> CREATOR = new Parcelable.Creator<Event>() {
        @Override
        public Event createFromParcel(Parcel in) {
            return new Event(in);
        }

        @Override
        public Event[] newArray(int size) {
            return new Event[size];
        }
    };
}