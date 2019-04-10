package carterdmorgan.com.familymap.data;

import android.graphics.Color;

import java.net.HttpURLConnection;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;

import carterdmorgan.com.familymap.api.model.Event;
import carterdmorgan.com.familymap.api.model.Person;
import carterdmorgan.com.familymap.api.network.FamilyMapService;
import carterdmorgan.com.familymap.api.result.CurrentEventResult;
import carterdmorgan.com.familymap.api.result.CurrentPersonResult;
import carterdmorgan.com.familymap.api.result.UserResult;
import carterdmorgan.com.familymap.containers.FamilyContainer;
import carterdmorgan.com.familymap.containers.LifeEventsContainer;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class UserDataStore {
    private static final UserDataStore instance = new UserDataStore();
    private UserResult userResult;
    private CurrentEventResult currentEventResult;
    private CurrentPersonResult currentPersonResult;
    private String serverHost;
    private String serverPort;
    private int mapType = 0;
    private Map<String, Boolean> filterPreferences = new HashMap<>();
    private Map<String, Float> markerColors = new HashMap<>();

    private boolean showFather = true;
    private boolean showMother = true;
    private boolean showMale = true;
    private boolean showFemale = true;

    private String lifeStoryLineColor = "Red";
    private String familyTreeLineColor = "Green";
    private String spouseLineColor = "Blue";

    private boolean showLifeStoryLines = false;
    private boolean showFamilyTreeLines = false;
    private boolean showSpouseLines = false;

    public static UserDataStore getInstance() {
        return instance;
    }

    private UserDataStore() { }

    public void retrieveFamilyData(final FamilyMapService fmService, LoadFamilyDataListener listener) {
        retrieveAllPersons(fmService, listener);
    }

    public Person getUserPerson() {
        for (Person person : currentPersonResult.getData()) {
            if (person.getPersonID().equals(userResult.getPersonID())) {
                return person;
            }
        }

        return null;
    }

    public void initializeUserPreferences() {
        for (String eventType : this.getEventTypes()) {
            filterPreferences.put(eventType, true);
        }

        float baseColor = 0.0f;
        float increment = 15.0f;

        for (String eventType : this.getEventTypes()) {
            this.markerColors.put(eventType, baseColor);
            baseColor += increment;
        }
    }

    private void retrieveAllPersons(final FamilyMapService fmService, final LoadFamilyDataListener listener) {
        Call<CurrentPersonResult> personCall = fmService.getAllPersonsForCurrentUser(userResult.getAuthToken());
        personCall.enqueue(new Callback<CurrentPersonResult>() {
            @Override
            public void onResponse(Call<CurrentPersonResult> call, Response<CurrentPersonResult> response) {
                if (response.code() == HttpURLConnection.HTTP_OK) {
                    CurrentPersonResult currentPersonResult = response.body();
                    UserDataStore.getInstance().setCurrentPersonResult(currentPersonResult);
                    retrieveAllEvents(fmService, listener);
                } else {
                    listener.onFailure();
                }
            }

            @Override
            public void onFailure(Call<CurrentPersonResult> call, Throwable t) {
                t.printStackTrace();
                listener.onFailure();
            }
        });
    }

    private void retrieveAllEvents(FamilyMapService fmService, final LoadFamilyDataListener listener) {
        Call<CurrentEventResult> eventCall = fmService.getAllEventsForPerson(userResult.getAuthToken());
        eventCall.enqueue(new Callback<CurrentEventResult>() {
            @Override
            public void onResponse(Call<CurrentEventResult> call, Response<CurrentEventResult> response) {
                CurrentEventResult currentEventResult = response.body();
                UserDataStore.getInstance().setCurrentEventResult(currentEventResult);
                listener.onSuccess();
            }

            @Override
            public void onFailure(Call<CurrentEventResult> call, Throwable t) {
                t.printStackTrace();
                listener.onFailure();
            }
        });
    }

    public void clearAllData() {
        instance.setUserResult(null);
        instance.setCurrentEventResult(null);
        instance.setCurrentPersonResult(null);

        serverHost = null;
        serverPort = null;
        mapType = 0;
        filterPreferences = new HashMap<>();
        markerColors = new HashMap<>();

        showFather = true;
        showMother = true;
        showMale = true;
        showFemale = true;

        lifeStoryLineColor = "Red";
        familyTreeLineColor = "Green";
        spouseLineColor = "Blue";

        showLifeStoryLines = false;
        showFamilyTreeLines = false;
        showSpouseLines = false;
    }

    public ArrayList<String> getEventTypes() {
        Set<String> types = new HashSet<>();

        for (Event event : currentEventResult.getData()) {
            types.add(event.getEventType().toLowerCase());
        }

        ArrayList<String> listTypes = new ArrayList<>();
        listTypes.addAll(types);

        Collections.sort(listTypes, new Comparator<String>() {
            @Override
            public int compare(String s1, String s2) {
                return s1.compareToIgnoreCase(s2);
            }
        });

        return listTypes;
    }

    public ArrayList<LifeEventsContainer> compileLifeEventsContainersForPerson(Person person) {
        ArrayList<LifeEventsContainer> lifeEventsContainers = new ArrayList<>();

        for (Event event : UserDataStore.getInstance().getFilteredEvents()) {
            if (event.getPersonID().equals(person.getPersonID())) {
                lifeEventsContainers.add(new LifeEventsContainer(event, person.getFullName()));
            }
        }

        Collections.sort(lifeEventsContainers, LifeEventsContainer.SORT_BY_YEAR_AND_NAME);

        return lifeEventsContainers;
    }

    public ArrayList<FamilyContainer> compileFamilyContainersForPerson(Person displayPerson) {
        ArrayList<FamilyContainer> familyContainers = new ArrayList<>();
        PersonHelper personHelper = new PersonHelper();

        for (Person person : UserDataStore.getInstance().getAllPersons()) {
            String relationship = personHelper.determineRelationship(displayPerson, person);
            if (relationship != null) {
                FamilyContainer container = new FamilyContainer(person, relationship);
                familyContainers.add(container);
            }
        }

        return familyContainers;
    }

    public ArrayList<Person> getAllPersons() {
        return currentPersonResult.getData();
    }

    public ArrayList<Event> getFilteredEvents() {
        ArrayList<Event> events = new ArrayList<>();

        Person mother = null;
        Person father = null;
        Person person = getUserPerson();

        for (Person p : UserDataStore.getInstance().getAllPersons()) {
            if (p.getPersonID().equals(person.getMother())) {
                mother = p;
                break;
            }
        }

        for (Person p : UserDataStore.getInstance().getAllPersons()) {
            if (p.getPersonID().equals(person.getFather())) {
                father = p;
                break;
            }
        }

        ArrayList<Person> mothersSide = compileAncestors(mother, new ArrayList<Person>());
        ArrayList<Person> fathersSide = compileAncestors(father, new ArrayList<Person>());

        for (Event event : currentEventResult.getData()) {
            if (shouldIncludeEvent(event, mothersSide, fathersSide)) {
                events.add(event);
            }
        }

        return events;
    }

    public Person getPersonForEvent(Event event) {
        for (Person person : currentPersonResult.getData()) {
            if (event.getPersonID().equals(person.getPersonID())) {
                return person;
            }
        }
        return null;
    }

    public Person getMotherForPerson(Person person) {
        for (Person p : UserDataStore.getInstance().getAllPersons()) {
            if (p.getPersonID().equals(person.getMother())) {
                return p;
            }
        }

        return null;
    }

    public Person getFatherForPerson(Person person) {
        for (Person p : UserDataStore.getInstance().getAllPersons()) {
            if (p.getPersonID().equals(person.getFather())) {
                return p;
            }
        }

        return null;
    }

    public ArrayList<Event> getAllEventsForPerson(Person person) {
        ArrayList<Event> events = new ArrayList<>();

        for (Event event : getFilteredEvents()) {
            if (event.getPersonID().equals(person.getPersonID())) {
                events.add(event);
            }
        }

        Collections.sort(events, Event.SORT_BY_YEAR_AND_NAME);

        return events;
    }

    public int getColorCode(String lineColor) {
        if (lineColor.equals(RelationshipLines.RED)) {
            return Color.RED;
        } else if (lineColor.equals(RelationshipLines.BLUE)) {
            return Color.BLUE;
        } else if (lineColor.equals(RelationshipLines.GREEN)) {
            return Color.GREEN;
        }

        return 0;
    }

    private boolean shouldIncludeEvent(Event event, ArrayList<Person> mothersSide, ArrayList<Person> fathersSide) {
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

    private ArrayList<Person> compileAncestors(Person descendant, ArrayList<Person> ancestors) {
        ancestors.add(descendant);

        for (Person person : UserDataStore.getInstance().getAllPersons()) {
            if (person.getPersonID().equals(descendant.getFather())
                    || person.getPersonID().equals(descendant.getMother())) {
                ancestors = compileAncestors(person, ancestors);
            }
        }

        return ancestors;
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

    private Person getPersonFromEvent(Event event) {
        for (Person person : UserDataStore.getInstance().getAllPersons()) {
            if (person.getPersonID().equals(event.getPersonID())) {
                return person;
            }
        }

        return null;
    }

    public Map<String, Boolean> getFilterPreferences() {
        return filterPreferences;
    }

    public UserResult getUserResult() {
        return userResult;
    }

    public int getMapType() {
        return mapType;
    }

    public void setMapType(int mapType) {
        this.mapType = mapType;
    }

    public void setUserResult(UserResult userResult) {
        this.userResult = userResult;
    }

    public void setCurrentEventResult(CurrentEventResult currentEventResult) {
        this.currentEventResult = currentEventResult;
    }

    public void setCurrentPersonResult(CurrentPersonResult currentPersonResult) {
        this.currentPersonResult = currentPersonResult;
    }

    public void setServerHost(String serverHost) {
        this.serverHost = serverHost;
    }

    public void setServerPort(String serverPort) {
        this.serverPort = serverPort;
    }

    public String getServerHost() {
        return serverHost;
    }

    public String getServerPort() {
        return serverPort;
    }

    public interface LoadFamilyDataListener {
        void onSuccess();
        void onFailure();
    }

    public boolean isShowFather() {
        return showFather;
    }

    public void setShowFather(boolean showFather) {
        this.showFather = showFather;
    }

    public boolean isShowMother() {
        return showMother;
    }

    public void setShowMother(boolean showMother) {
        this.showMother = showMother;
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

    public Map<String, Float> getMarkerColors() {
        return markerColors;
    }

    public String getLifeStoryLineColor() {
        return lifeStoryLineColor;
    }

    public void setLifeStoryLineColor(String lifeStoryLineColor) {
        this.lifeStoryLineColor = lifeStoryLineColor;
    }

    public String getFamilyTreeLineColor() {
        return familyTreeLineColor;
    }

    public void setFamilyTreeLineColor(String familyTreeLineColor) {
        this.familyTreeLineColor = familyTreeLineColor;
    }

    public String getSpouseLineColor() {
        return spouseLineColor;
    }

    public void setSpouseLineColor(String spouseLineColor) {
        this.spouseLineColor = spouseLineColor;
    }

    public boolean isShowLifeStoryLines() {
        return showLifeStoryLines;
    }

    public void setShowLifeStoryLines(boolean showLifeStoryLines) {
        this.showLifeStoryLines = showLifeStoryLines;
    }

    public boolean isShowFamilyTreeLines() {
        return showFamilyTreeLines;
    }

    public void setShowFamilyTreeLines(boolean showFamilyTreeLines) {
        this.showFamilyTreeLines = showFamilyTreeLines;
    }

    public boolean isShowSpouseLines() {
        return showSpouseLines;
    }

    public void setShowSpouseLines(boolean showSpouseLines) {
        this.showSpouseLines = showSpouseLines;
    }

    @Override
    public String toString() {
        return "UserDataStore{" +
                "userResult=" + userResult +
                ", currentEventResult=" + currentEventResult +
                ", currentPersonResult=" + currentPersonResult +
                ", serverHost='" + serverHost + '\'' +
                ", serverPort='" + serverPort + '\'' +
                ", mapType=" + mapType +
                ", filterPreferences=" + filterPreferences +
                ", markerColors=" + markerColors +
                ", showFather=" + showFather +
                ", showMother=" + showMother +
                ", showMale=" + showMale +
                ", showFemale=" + showFemale +
                ", lifeStoryLineColor='" + lifeStoryLineColor + '\'' +
                ", familyTreeLineColor='" + familyTreeLineColor + '\'' +
                ", spouseLineColor='" + spouseLineColor + '\'' +
                ", showLifeStoryLines=" + showLifeStoryLines +
                ", showFamilyTreeLines=" + showFamilyTreeLines +
                ", showSpouseLines=" + showSpouseLines +
                '}';
    }
}
