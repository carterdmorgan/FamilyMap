package carterdmorgan.com.familymap.data;

import java.util.ArrayList;

import carterdmorgan.com.familymap.api.model.Event;
import carterdmorgan.com.familymap.api.model.Person;

public class EventHelper {

    public boolean shouldIncludeEvent(Event event, ArrayList<Person> mothersSide, ArrayList<Person> fathersSide) {
        boolean placeMarker = false;

        String eventType = event.getEventType().toLowerCase();


        if (UserDataStore.getInstance().getFilterPreferences().get(eventType)) {
            placeMarker = true;
        }
        if (isMaleEvent(event) && !UserDataStore.getInstance().isShowMale()) {
            placeMarker = false;
        }
        if (!isMaleEvent(event) && !UserDataStore.getInstance().isShowFemale()) {
            placeMarker = false;
        }
        if (personDoesExist(getPersonFromEvent(event), mothersSide) && !UserDataStore.getInstance().isShowMother()) {
            placeMarker = false;
        }

        if (personDoesExist(getPersonFromEvent(event), fathersSide) && !UserDataStore.getInstance().isShowFather()) {
            placeMarker = false;
        }


        return placeMarker;
    }

    private boolean isMaleEvent(Event event) {
        for (Person person : UserDataStore.getInstance().getAllPersons()) {
            if (person.getPersonID().equals(event.getPersonID()) && person.getGender().equals(PersonHelper.GENDER_MARKER_MALE)) {
                return true;
            } else if (person.getPersonID().equals(event.getPersonID()) && person.getGender().equals(PersonHelper.GENDER_MARKER_FEMALE)) {
                return false;
            }
        }

        return false;
    }

    private boolean personDoesExist(Person person, ArrayList<Person> side) {
        for (Person p : side) {
            if (p.equals(person)) {
                return true;

            }
        }

        return false;
    }
}
