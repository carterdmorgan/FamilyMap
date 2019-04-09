package carterdmorgan.com.familymap;

import android.content.Intent;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.SearchView;
import android.util.Log;
import android.view.MenuItem;

import java.util.ArrayList;

import carterdmorgan.com.familymap.api.model.Event;
import carterdmorgan.com.familymap.api.model.Person;
import carterdmorgan.com.familymap.data.FamilyAdapter;
import carterdmorgan.com.familymap.data.LifeEventsAdapter;
import carterdmorgan.com.familymap.data.UserDataStore;

public class SearchActivity extends AppCompatActivity {

    private SearchView searchView;
    private RecyclerView rvSearchPerson;
    private RecyclerView rvSearchEvent;
    private ArrayList<PersonActivity.LifeEventsContainer> lifeEventsContainers;
    private ArrayList<PersonActivity.FamilyContainer> familyContainers;
    private LifeEventsAdapter lifeEventsAdapter;
    private FamilyAdapter familyAdapter;
    public static final String TAG = SearchActivity.class.getSimpleName();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_search);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);

        searchView = findViewById(R.id.search_view);
        rvSearchPerson = findViewById(R.id.person_search_recycler_view);
        rvSearchEvent = findViewById(R.id.event_search_recycler_view);

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

        lifeEventsContainers = new ArrayList<>();
        familyContainers = new ArrayList<>();

        lifeEventsAdapter = new LifeEventsAdapter(SearchActivity.this, lifeEventsContainers);

        rvSearchEvent.setLayoutManager(lifeEventsLayoutManager);
        rvSearchEvent.setNestedScrollingEnabled(false);
        rvSearchEvent.setAdapter(lifeEventsAdapter);

        familyAdapter = new FamilyAdapter(familyContainers, SearchActivity.this);

        rvSearchPerson.setLayoutManager(familyLayoutManager);
        rvSearchPerson.setNestedScrollingEnabled(false);
        rvSearchPerson.setAdapter(familyAdapter);

        searchView.setOnQueryTextListener(new SearchView.OnQueryTextListener() {
            @Override
            public boolean onQueryTextSubmit(String s) {
                s = s.toLowerCase();
                familyContainers.clear();
                lifeEventsContainers.clear();

                for (Person person : UserDataStore.getInstance().getCurrentPersonResult().getData()) {
                    if (person.getFirstName().toLowerCase().contains(s) || person.getLastName().toLowerCase().contains(s)) {
                        familyContainers.add(new PersonActivity.FamilyContainer(person, null));
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
                    for (Person person : UserDataStore.getInstance().getCurrentPersonResult().getData()) {
                        if (person.getPersonID().equals(event.getPersonID())) {
                            PersonActivity.LifeEventsContainer container
                                    = new PersonActivity.LifeEventsContainer(event, person.getFirstName() + " " + person.getLastName());
                            lifeEventsContainers.add(container);
                        }
                    }
                }

                familyAdapter.notifyDataSetChanged();
                lifeEventsAdapter.notifyDataSetChanged();
                return false;
            }

            @Override
            public boolean onQueryTextChange(String s) {
                return false;
            }
        });
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case (android.R.id.home):
                Intent intent = new Intent(SearchActivity.this, MainActivity.class);
                intent.putExtra("launchMaps", true);
                startActivity(intent);
                return true;
            default:
                return super.onOptionsItemSelected(item);
        }
    }
}
