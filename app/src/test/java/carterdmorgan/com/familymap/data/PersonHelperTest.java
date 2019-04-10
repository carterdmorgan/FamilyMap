package carterdmorgan.com.familymap.data;

import org.junit.Test;

import carterdmorgan.com.familymap.api.model.Person;

import static org.junit.Assert.*;

public class PersonHelperTest {

    @Test
    public void determineRelationshipMomSuccess() {
        PersonHelper personHelper = new PersonHelper();
        Person person = new Person();
        person.setMother("123");
        Person relative = new Person();
        relative.setPersonID("123");
        String relationship = personHelper.determineRelationship(person, relative);
        assertEquals(relationship, PersonHelper.RELATIONSHIP_MOTHER);
    }

    @Test
    public void determineRelationshipMomFailure() {
        PersonHelper personHelper = new PersonHelper();
        Person person = new Person();
        person.setMother("124");
        Person relative = new Person();
        relative.setPersonID("123");
        String relationship = personHelper.determineRelationship(person, relative);
        assertNull(relationship);
    }

    @Test
    public void determineRelationshipDadSuccess() {
        PersonHelper personHelper = new PersonHelper();
        Person person = new Person();
        person.setFather("123");
        Person relative = new Person();
        relative.setPersonID("123");
        String relationship = personHelper.determineRelationship(person, relative);
        assertEquals(relationship, PersonHelper.RELATIONSHIP_FATHER);
    }

    @Test
    public void determineRelationshipDadFailure() {
        PersonHelper personHelper = new PersonHelper();
        Person person = new Person();
        person.setFather("124");
        Person relative = new Person();
        relative.setPersonID("123");
        String relationship = personHelper.determineRelationship(person, relative);
        assertNull(relationship);
    }

    @Test
    public void determineRelationshipSpouseSuccess() {
        PersonHelper personHelper = new PersonHelper();
        Person person = new Person();
        person.setSpouse("123");
        Person relative = new Person();
        relative.setPersonID("123");
        String relationship = personHelper.determineRelationship(person, relative);
        assertEquals(relationship, PersonHelper.RELATIONSHIP_SPOUSE);
    }

    @Test
    public void determineRelationshipSpouseFailure() {
        PersonHelper personHelper = new PersonHelper();
        Person person = new Person();
        person.setSpouse("124");
        Person relative = new Person();
        relative.setPersonID("123");
        String relationship = personHelper.determineRelationship(person, relative);
        assertNull(relationship);
    }

    @Test
    public void determineRelationshipChildSuccess() {
        PersonHelper personHelper = new PersonHelper();
        Person person = new Person();
        person.setPersonID("123");
        Person relative = new Person();
        relative.setFather("123");
        relative.setPersonID("abc");
        String relationship = personHelper.determineRelationship(person, relative);
        assertEquals(relationship, PersonHelper.RELATIONSHIP_CHILD);
    }

    @Test
    public void determineRelationshipChildFailure() {
        PersonHelper personHelper = new PersonHelper();
        Person person = new Person();
        person.setPersonID("123");
        Person relative = new Person();
        relative.setFather("124");
        relative.setPersonID("abc");
        String relationship = personHelper.determineRelationship(person, relative);
        assertNull(relationship);
    }
}