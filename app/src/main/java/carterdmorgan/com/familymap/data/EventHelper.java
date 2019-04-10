package carterdmorgan.com.familymap.data;

import java.util.ArrayList;
import java.util.Comparator;
import java.util.Map;

import carterdmorgan.com.familymap.api.model.Event;
import carterdmorgan.com.familymap.api.model.Person;

public class EventHelper {

    private ArrayList<Event> events;
    private PersonHelper personHelper = new PersonHelper();
    private ArrayList<Person> mothersSide;
    private ArrayList<Person> fathersSide;
    private ArrayList<Person> allPersons;
    private Map<String, Boolean> filterPreferences;
    private boolean showMale;
    private boolean showFemale;
    private boolean showMother;
    private boolean showFather;

    private EventHelper() {}

    public static class Builder {
        private ArrayList<Event> events;
        private ArrayList<Person> mothersSide;
        private ArrayList<Person> fathersSide;
        private ArrayList<Person> allPersons;
        private Map<String, Boolean> filterPreferences;
        private boolean showMale;
        private boolean showFemale;
        private boolean showMother;
        private boolean showFather;

        public Builder setEvents(ArrayList<Event> events) {
            this.events = events;
            return this;
        }

        public Builder setMothersSide(ArrayList<Person> mothersSide) {
            this.mothersSide = mothersSide;
            return this;
        }

        public Builder setFathersSide(ArrayList<Person> fathersSide) {
            this.fathersSide = fathersSide;
            return this;
        }

        public Builder setAllPersons(ArrayList<Person> allPersons) {
            this.allPersons = allPersons;
            return this;
        }

        public Builder setFilterPreferences(Map<String, Boolean> filterPreferences) {
            this.filterPreferences = filterPreferences;
            return this;
        }

        public Builder setShowMale(boolean showMale) {
            this.showMale = showMale;
            return this;
        }

        public Builder setShowFemale(boolean showFemale) {
            this.showFemale = showFemale;
            return this;
        }

        public Builder setShowMother(boolean showMother) {
            this.showMother = showMother;
            return this;
        }

        public Builder setShowFather(boolean showFather) {
            this.showFather = showFather;
            return this;
        }

        public EventHelper build() {
            EventHelper eventHelper = new EventHelper();
            eventHelper.events = this.events;
            eventHelper.mothersSide = this.mothersSide;
            eventHelper.fathersSide = this.fathersSide;
            eventHelper.allPersons = this.allPersons;
            eventHelper.filterPreferences = this.filterPreferences;
            eventHelper.showMale = this.showMale;
            eventHelper.showFemale = this.showFemale;
            eventHelper.showFather = this.showFather;
            eventHelper.showMother = this.showMother;
            return eventHelper;
        }
    }

    public static final Comparator<Event> SORT_BY_YEAR_THEN_NAME = new Comparator<Event>() {
        @Override
        public int compare(Event o1, Event o2) {
            Integer i1 = new Integer(o1.getYear());
            Integer i2 = new Integer(o2.getYear());

            if (!i2.equals(i1)) {
                return i1.compareTo(i2);
            } else {
                return o1.getEventType().toLowerCase()
                        .compareTo(o2.getEventType().toLowerCase());
            }
        }
    };

    public ArrayList<Event> getFilteredEvents() {
        ArrayList<Event> included = new ArrayList<>();

        for (Event event : events) {
            if (shouldIncludeEvent(event)) {
                included.add(event);
            }
        }

        return included;
    }


    private boolean shouldIncludeEvent(Event event) {
        boolean eventShouldExist = false;

        String eventType = event.getEventType().toLowerCase();


        if (filterPreferences.get(eventType)) {
            eventShouldExist = true;
        }
        if (isMaleEvent(event) && !showMale) {
            eventShouldExist = false;
        }
        if (!isMaleEvent(event) && !showFemale) {
            eventShouldExist = false;
        }
        if (personDoesExist(personHelper.getPersonFromEvent(event, allPersons), mothersSide) && !showMother) {
            eventShouldExist = false;
        }

        if (personDoesExist(personHelper.getPersonFromEvent(event, allPersons), fathersSide) && !showFather) {
            eventShouldExist = false;
        }


        return eventShouldExist;
    }

    private boolean isMaleEvent(Event event) {
        for (Person person : allPersons) {
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

    public PersonHelper getPersonHelper() {
        return personHelper;
    }

    public void setPersonHelper(PersonHelper personHelper) {
        this.personHelper = personHelper;
    }

    public ArrayList<Person> getMothersSide() {
        return mothersSide;
    }

    public void setMothersSide(ArrayList<Person> mothersSide) {
        this.mothersSide = mothersSide;
    }

    public ArrayList<Person> getFathersSide() {
        return fathersSide;
    }

    public void setFathersSide(ArrayList<Person> fathersSide) {
        this.fathersSide = fathersSide;
    }

    public ArrayList<Person> getAllPersons() {
        return allPersons;
    }

    public void setAllPersons(ArrayList<Person> allPersons) {
        this.allPersons = allPersons;
    }

    public Map<String, Boolean> getFilterPreferences() {
        return filterPreferences;
    }

    public void setFilterPreferences(Map<String, Boolean> filterPreferences) {
        this.filterPreferences = filterPreferences;
    }

    public boolean isShowMale() {
        return showMale;
    }

    public void setShowMale(boolean showMale) {
        this.showMale = showMale;
    }

    public boolean isShowFemale() {
        return showFemale;
    }

    public void setShowFemale(boolean showFemale) {
        this.showFemale = showFemale;
    }

    public boolean isShowMother() {
        return showMother;
    }

    public void setShowMother(boolean showMother) {
        this.showMother = showMother;
    }

    public boolean isShowFather() {
        return showFather;
    }

    public void setShowFather(boolean showFather) {
        this.showFather = showFather;
    }
}
