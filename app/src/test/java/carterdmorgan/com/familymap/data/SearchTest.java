package carterdmorgan.com.familymap.data;

import org.junit.Test;

import java.util.ArrayList;

import carterdmorgan.com.familymap.api.model.Event;
import carterdmorgan.com.familymap.api.model.Person;

import static org.junit.Assert.*;

public class SearchTest {

    @Test
    public void findAllEventsMatchingQuerySuccess() {
        ArrayList<Event> events = new ArrayList<>();
        Event event0 = new Event();
        event0.setEventType("123");
        event0.setCountry("abc");
        event0.setCity("abc");
        event0.setYear(1994);
        Event event1 = new Event();
        event1.setEventType("abc");
        event1.setCountry("123");
        event1.setCity("abc");
        event1.setYear(1994);
        Event event2 = new Event();
        event2.setEventType("abc");
        event2.setCountry("abc");
        event2.setCity("123");
        event2.setYear(1994);
        Event event3 = new Event();
        event3.setEventType("abc");
        event3.setCountry("abc");
        event3.setCity("abc");
        event3.setYear(123);
        events.add(event0);
        events.add(event1);
        events.add(event2);
        events.add(event3);
        Search search = new Search();
        ArrayList<Event> found = search.findAllEventsMatchingQuery("12", events);
        assertTrue(found.contains(event0));
        assertTrue(found.contains(event1));
        assertTrue(found.contains(event2));
        assertTrue(found.contains(event3));
    }

    @Test
    public void findAllEventsMatchingQueryFailure() {
        ArrayList<Event> events = new ArrayList<>();
        Event event0 = new Event();
        event0.setEventType("abc");
        event0.setCountry("abc");
        event0.setCity("abc");
        event0.setYear(1994);
        Event event1 = new Event();
        event1.setEventType("abc");
        event1.setCountry("abc");
        event1.setCity("abc");
        event1.setYear(1994);
        Event event2 = new Event();
        event2.setEventType("abc");
        event2.setCountry("abc");
        event2.setCity("abc");
        event2.setYear(1994);
        Event event3 = new Event();
        event3.setEventType("abc");
        event3.setCountry("abc");
        event3.setCity("abc");
        event3.setYear(1994);
        events.add(event0);
        events.add(event1);
        events.add(event2);
        events.add(event3);
        Search search = new Search();
        ArrayList<Event> found = search.findAllEventsMatchingQuery("12", events);
        assertTrue(found.isEmpty());
    }

    @Test
    public void findAllPeopleMatchingQuerySuccess() {
        Person person0 = new Person();
        person0.setFirstName("abc");
        person0.setLastName("def");
        Person person1 = new Person();
        person1.setFirstName("def");
        person1.setLastName("abc");
        ArrayList<Person> persons = new ArrayList<>();
        persons.add(person0);
        persons.add(person1);
        Search search = new Search();
        ArrayList<Person> found = search.findAllPeopleMatchingQuery("ab", persons);
        assertTrue(found.contains(person0));
        assertTrue(found.contains(person1));
    }

    @Test
    public void findAllPeopleMatchingQueryFailure() {
        Person person0 = new Person();
        person0.setFirstName("abc");
        person0.setLastName("def");
        Person person1 = new Person();
        person1.setFirstName("def");
        person1.setLastName("abc");
        ArrayList<Person> persons = new ArrayList<>();
        persons.add(person0);
        persons.add(person1);
        Search search = new Search();
        ArrayList<Person> found = search.findAllPeopleMatchingQuery("xyz", persons);
        assertTrue(found.isEmpty());
    }
}