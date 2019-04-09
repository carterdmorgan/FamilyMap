package carterdmorgan.com.familymap;

import android.content.Intent;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.MenuItem;
import android.view.View;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;

import carterdmorgan.com.familymap.api.model.Event;
import carterdmorgan.com.familymap.api.model.Person;
import carterdmorgan.com.familymap.data.Constants;
import carterdmorgan.com.familymap.data.FamilyAdapter;
import carterdmorgan.com.familymap.data.LifeEventsAdapter;
import carterdmorgan.com.familymap.data.UserDataStore;

public class PersonActivity extends AppCompatActivity {

    private TextView tvFirstName;
    private TextView tvLastName;
    private TextView tvGender;
    private Person displayPerson;
    private LinearLayout llLifeEvents;
    private LinearLayout llFamily;
    private ImageView ivLifeEventsArrow;
    private ImageView ivFamilyArrow;
    private RecyclerView rvLifeEvents;
    private RecyclerView rvFamily;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_person);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);

        displayPerson = (Person) getIntent().getExtras().get(Constants.PERSON_EXTRA);

        initializeTextViews();
        initializeLinearLayouts();

        ArrayList<LifeEventsContainer> lifeEventsContainers = new ArrayList<>();

        for (Event event : UserDataStore.getInstance().getFilteredEvents()) {
            if (event.getPersonID().equals(displayPerson.getPersonID())) {
                lifeEventsContainers.add(new LifeEventsContainer(event, displayPerson.getFirstName() + " " + displayPerson.getLastName()));
            }
        }

        Collections.sort(lifeEventsContainers, new Comparator<LifeEventsContainer>() {
            @Override
            public int compare(LifeEventsContainer o1, LifeEventsContainer o2) {
                Integer i1 = new Integer(o1.getEvent().getYear());
                Integer i2 = new Integer(o2.getEvent().getYear());

                if (!i2.equals(i1)) {
                    return i1.compareTo(i2);
                } else {
                    return o1.getEvent().getEventType().toLowerCase()
                            .compareTo(o2.getEvent().getEventType().toLowerCase());
                }
            }
        });

//        for (LifeEventsContainer container : lifeEventsContainers) {
//            if (container.getEvent().getEventType().toLowerCase().equals("birth")) {
//                lifeEventsContainers.remove(container);
//                lifeEventsContainers.add(0, container);
//            } else if (container.getEvent().getEventType().toLowerCase().equals("death")) {
//                lifeEventsContainers.remove(container);
//                lifeEventsContainers.add(lifeEventsContainers.size() - 1, container);
//            }
//        }

        ArrayList<FamilyContainer> familyContainers = new ArrayList<>();
        ArrayList<Person> children = new ArrayList<>();
        ArrayList<Person> momsSide = new ArrayList<>();
        ArrayList<Person> dadsSide = new ArrayList<>();
//        ArrayList<Person> descendants = new ArrayList<>();

        for (Person person : UserDataStore.getInstance().getCurrentPersonResult().getData()) {
            if (person.getPersonID().equals(displayPerson.getSpouse())) {
                FamilyContainer container = new FamilyContainer(person, "Spouse");
                familyContainers.add(container);
            } else if (person.getPersonID().equals(displayPerson.getMother())) {
                FamilyContainer container = new FamilyContainer(person, "Mother");
                familyContainers.add(container);
            } else if (person.getPersonID().equals(displayPerson.getFather())) {
                FamilyContainer container = new FamilyContainer(person, "Father");
                familyContainers.add(container);
            } else if ((person.getMother() != null && person.getMother().equals(displayPerson.getPersonID())) ||
                    (person.getFather() != null && person.getFather().equals(displayPerson.getPersonID()))) {
                FamilyContainer container = new FamilyContainer(person, "Child");
                children.add(person);
                familyContainers.add(container);
            }
        }

        for (Person person : momsSide) {
            FamilyContainer container = new FamilyContainer(person, "Ancestor");
            familyContainers.add(container);
        }

        for (Person person : dadsSide) {
            FamilyContainer container = new FamilyContainer(person, "Ancestor");
            familyContainers.add(container);
        }

//        for (Person person : children) {
//            descendants.addAll(compileDescendants(person, new ArrayList<Person>()));
//        }

//        for (Person person : descendants) {
//            FamilyContainer container = new FamilyContainer(person, "Descendant");
//            familyContainers.add(container);
//        }

        rvLifeEvents = findViewById(R.id.life_events_recycler_view);
        rvFamily = findViewById(R.id.family_recycler_view);

        RecyclerView.LayoutManager familyLayoutManager = new LinearLayoutManager(this) {
            @Override
            public boolean canScrollVertically() {
                return false;
            }

            @Override
            public boolean canScrollHorizontally() {
                return false;
            }
        };

        RecyclerView.LayoutManager lifeEventsLayoutManager = new LinearLayoutManager(this) {
            @Override
            public boolean canScrollHorizontally() {
                return false;
            }

            @Override
            public boolean canScrollVertically() {
                return false;
            }
        };

        LifeEventsAdapter lifeEventsAdapter = new LifeEventsAdapter(PersonActivity.this, lifeEventsContainers);

        rvLifeEvents.setLayoutManager(lifeEventsLayoutManager);
        rvLifeEvents.setNestedScrollingEnabled(false);
        rvLifeEvents.setAdapter(lifeEventsAdapter);

        FamilyAdapter familyAdapter = new FamilyAdapter(familyContainers, PersonActivity.this);

        rvFamily.setLayoutManager(familyLayoutManager);
        rvFamily.setNestedScrollingEnabled(false);
        rvFamily.setAdapter(familyAdapter);
    }

//    private ArrayList<Person> compileDescendants(Person ancestor, ArrayList<Person> descendants) {
//        for (Person person : UserDataStore.getInstance().getCurrentPersonResult().getData()) {
//            if ((person.getFather() != null &&person.getFather().equals(ancestor.getPersonID()))
//                    || (person.getMother() != null && person.getMother().equals(ancestor.getPersonID()))) {
//                descendants.add(person);
//                descendants = compileDescendants(person, descendants);
//            }
//        }
//
//        return descendants;
//    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case (android.R.id.home):
                Intent intent = new Intent(PersonActivity.this, MainActivity.class);
                intent.putExtra("launchMaps", true);
                intent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
                startActivity(intent);
                return true;
            default:
                return super.onOptionsItemSelected(item);
        }
    }

    private void initializeTextViews() {
        tvFirstName = findViewById(R.id.person_first_name_text_view);
        tvLastName = findViewById(R.id.person_last_name_text_view);
        tvGender = findViewById(R.id.person_gender_text_view);

        tvFirstName.setText(displayPerson.getFirstName());
        tvLastName.setText(displayPerson.getLastName());
        tvGender.setText(displayPerson.getGender().equals("m") ? "Male" : "Female");
    }

    private void initializeLinearLayouts() {
        ivLifeEventsArrow = findViewById(R.id.life_events_arrow_image_view);
        ivFamilyArrow = findViewById(R.id.family_arrow_image_view);

        llLifeEvents = findViewById(R.id.life_events_linear_layout);
        llFamily = findViewById(R.id.family_linear_layout);

        llLifeEvents.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (ivLifeEventsArrow.getDrawable().getConstantState()
                        == getDrawable(R.drawable.ic_arrow_drop_up_black_24dp).getConstantState()) {
                    rvLifeEvents.setVisibility(View.GONE);
                    ivLifeEventsArrow.setImageDrawable(getDrawable(R.drawable.ic_arrow_drop_down_black_24dp));
                } else {
                    rvLifeEvents.setVisibility(View.VISIBLE);
                    ivLifeEventsArrow.setImageDrawable(getDrawable(R.drawable.ic_arrow_drop_up_black_24dp));
                }


            }
        });

        llFamily.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (ivFamilyArrow.getDrawable().getConstantState()
                        == getDrawable(R.drawable.ic_arrow_drop_up_black_24dp).getConstantState()) {
                    rvFamily.setVisibility(View.GONE);
                    ivFamilyArrow.setImageDrawable(getDrawable(R.drawable.ic_arrow_drop_down_black_24dp));
                } else {
                    rvFamily.setVisibility(View.VISIBLE);
                    ivFamilyArrow.setImageDrawable(getDrawable(R.drawable.ic_arrow_drop_up_black_24dp));
                }
            }
        });
    }

    public static class LifeEventsContainer {
        private Event event;
        private String personName;

        public LifeEventsContainer(Event event, String personName) {
            this.event = event;
            this.personName = personName;
        }

        public Event getEvent() {
            return event;
        }

        public void setEvent(Event event) {
            this.event = event;
        }

        public String getPersonName() {
            return personName;
        }

        public void setPersonName(String personName) {
            this.personName = personName;
        }

        @Override
        public String toString() {
            return "LifeEventsContainer{" +
                    "event=" + event +
                    ", personName='" + personName + '\'' +
                    '}';
        }
    }

    public static class FamilyContainer {
        private Person person;
        private String relationship;

        public FamilyContainer(Person person, String relationship) {
            this.person = person;
            this.relationship = relationship;
        }

        public Person getPerson() {
            return person;
        }

        public void setPerson(Person person) {
            this.person = person;
        }

        public String getRelationship() {
            return relationship;
        }

        public void setRelationship(String relationship) {
            this.relationship = relationship;
        }

        @Override
        public String toString() {
            return "FamilyContainer{" +
                    "person=" + person +
                    ", relationship='" + relationship + '\'' +
                    '}';
        }
    }
}
