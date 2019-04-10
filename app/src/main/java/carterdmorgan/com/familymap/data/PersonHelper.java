package carterdmorgan.com.familymap.data;

import java.util.ArrayList;

import carterdmorgan.com.familymap.api.model.Event;
import carterdmorgan.com.familymap.api.model.Person;

public class PersonHelper {

    public static final String RELATIONSHIP_SPOUSE = "Spouse";
    public static final String RELATIONSHIP_CHILD = "Child";
    public static final String RELATIONSHIP_MOTHER = "Mother";
    public static final String RELATIONSHIP_FATHER = "Father";
    public static final String GENDER_MARKER_MALE = "m";
    public static final String GENDER_MARKER_FEMALE = "f";
    public static final String GENDER_TITLE_MALE = "Male";
    public static final String GENDER_TITLE_FEMALE = "Female";

    public String determineRelationship(Person person, Person relative) {
        if (relative.getPersonID().equals(person.getSpouse())) {
            return RELATIONSHIP_SPOUSE;
        } else if (relative.getPersonID().equals(person.getMother())) {
            return RELATIONSHIP_MOTHER;
        } else if (relative.getPersonID().equals(person.getFather())) {
            return RELATIONSHIP_FATHER;
        } else if ((relative.getMother() != null && relative.getMother().equals(person.getPersonID())) ||
                (relative.getFather() != null && relative.getFather().equals(person.getPersonID()))) {
            return RELATIONSHIP_CHILD;
        } else {
            return null;
        }
    }

    public Person getPersonFromEvent(Event event, ArrayList<Person> persons) {
        for (Person person : persons) {
            if (person.getPersonID().equals(event.getPersonID())) {
                return person;
            }
        }

        return null;
    }
}
