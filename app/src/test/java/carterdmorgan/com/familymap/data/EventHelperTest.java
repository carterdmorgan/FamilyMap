package carterdmorgan.com.familymap.data;

import org.junit.Test;

import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.Map;

import carterdmorgan.com.familymap.api.model.Event;
import carterdmorgan.com.familymap.api.model.Person;

import static org.junit.Assert.*;

public class EventHelperTest {

    @Test
    public void sortEventsByYearThenName() {
        Event event0 = new Event();
        Event event1 = new Event();
        Event event2 = new Event();
        event0.setYear(1994);
        event1.setYear(2013);
        event2.setYear(2013);
        event0.setEventType("Birth");
        event1.setEventType("Graduation");
        event2.setEventType("Mission");
        ArrayList<Event> events = new ArrayList<>();
        events.add(event2);
        events.add(event1);
        events.add(event0);
        Collections.sort(events, EventHelper.SORT_BY_YEAR_THEN_NAME);
        assertTrue(events.get(0).equals(event0));
        assertTrue(events.get(1).equals(event1));
        assertTrue(events.get(2).equals(event2));
    }

    @Test
    public void filterEventsByPreferencesSuccess() {
        Event event0 = new Event();
        event0.setEventType("Mission");
        Event event1 = new Event();
        event1.setEventType("Graduation");
        Event event2 = new Event();
        event2.setEventType("Birth");
        ArrayList<Event> events = new ArrayList<>();
        events.add(event0);
        events.add(event1);
        events.add(event2);
        Map<String, Boolean> filterPreferences = new HashMap<>();
        filterPreferences.put("graduation", true);
        filterPreferences.put("birth", true);
        filterPreferences.put("mission", false);
        EventHelper eventHelper = new EventHelper.Builder()
                .setFilterPreferences(filterPreferences)
                .setEvents(events)
                .setAllPersons(new ArrayList<Person>())
                .setMothersSide(new ArrayList<Person>())
                .setFathersSide(new ArrayList<Person>())
                .setShowFemale(true)
                .build();
        ArrayList<Event> filtered = eventHelper.getFilteredEvents();
        assertTrue(filtered.contains(event1));
        assertTrue(filtered.contains(event2));
        assertFalse(filtered.contains(event0));
    }

    @Test
    public void filterEventsByPreferencesFailure() {
        Event event0 = new Event();
        event0.setEventType("Mission");
        Event event1 = new Event();
        event1.setEventType("Graduation");
        Event event2 = new Event();
        event2.setEventType("Birth");
        ArrayList<Event> events = new ArrayList<>();
        events.add(event0);
        events.add(event1);
        events.add(event2);
        Map<String, Boolean> filterPreferences = new HashMap<>();
        EventHelper eventHelper = new EventHelper.Builder()
                .setFilterPreferences(filterPreferences)
                .setEvents(events)
                .setAllPersons(new ArrayList<Person>())
                .setMothersSide(new ArrayList<Person>())
                .setFathersSide(new ArrayList<Person>())
                .setShowFemale(true)
                .build();
        ArrayList<Event> filtered = eventHelper.getFilteredEvents();
        assertTrue(filtered.contains(event1));
        assertTrue(filtered.contains(event2));
        assertTrue(filtered.contains(event0));
    }

}