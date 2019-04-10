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

        for (Person person : UserDataStore.getInstance().getAllPersons()) {
            if (person.getFirstName().toLowerCase().contains(s) || person.getLastName().toLowerCase().contains(s)) {
                familyContainers.add(new FamilyContainer(person, null));
            }
        }


        ArrayList<Event> events = new ArrayList<>();

        for (Event event : UserDataStore.getInstance().getFilteredEvents()) {
            if (event.getEventType().toLowerCase().contains(s)
                    || event.getCountry().toLowerCase().contains(s)
                    || event.getCity().toLowerCase().contains(s)
                    || Integer.toString(event.getYear()).toLowerCase().contains(s)) {
                events.add(event);
            }
        }

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
}
