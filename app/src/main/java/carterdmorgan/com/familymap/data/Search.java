package carterdmorgan.com.familymap.data;

import java.util.ArrayList;

import carterdmorgan.com.familymap.api.model.Event;
import carterdmorgan.com.familymap.api.model.Person;
import carterdmorgan.com.familymap.containers.FamilyContainer;
import carterdmorgan.com.familymap.containers.LifeEventsContainer;

public class Search {

    public void executeQuery(String s, ArrayList<FamilyContainer> familyContainers, ArrayList<LifeEventsContainer> lifeEventsContainers) {
        s = s.toLowerCase();
        familyContainers.clear();
        lifeEventsContainers.clear();

        ArrayList<Person> persons = findAllPeopleMatchingQuery(s, UserDataStore.getInstance().getAllPersons());

        for (Person person : persons) {
            familyContainers.add(new FamilyContainer(person, null));
        }

        ArrayList<Event> events = findAllEventsMatchingQuery(s, UserDataStore.getInstance().getFilteredEvents());

        for (Event event : events) {
            for (Person person : UserDataStore.getInstance().getAllPersons()) {
                if (person.getPersonID().equals(event.getPersonID())) {
                    LifeEventsContainer container
                            = new LifeEventsContainer(event, person.getFullName());
                    lifeEventsContainers.add(container);
                }
            }
        }
    }

    public ArrayList<Event> findAllEventsMatchingQuery(String s, ArrayList<Event> events) {
        ArrayList<Event> included = new ArrayList<>();

        for (Event event : events) {
            if (event.getEventType().toLowerCase().contains(s)
                    || event.getCountry().toLowerCase().contains(s)
                    || event.getCity().toLowerCase().contains(s)
                    || Integer.toString(event.getYear()).toLowerCase().contains(s)) {
                included.add(event);
            }
        }

        return included;
    }

    public ArrayList<Person> findAllPeopleMatchingQuery(String s, ArrayList<Person> persons) {
        ArrayList<Person> included = new ArrayList<>();

        for (Person person : persons) {
            if (person.getFirstName().toLowerCase().contains(s) || person.getLastName().toLowerCase().contains(s)) {
                included.add(person);
            }
        }

        return included;
    }
}
