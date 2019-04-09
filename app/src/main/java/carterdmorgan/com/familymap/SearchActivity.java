package carterdmorgan.com.familymap;

import android.content.Intent;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.SearchView;
import android.view.MenuItem;

import java.util.ArrayList;

import carterdmorgan.com.familymap.api.model.Event;
import carterdmorgan.com.familymap.api.model.Person;
import carterdmorgan.com.familymap.containers.FamilyContainer;
import carterdmorgan.com.familymap.containers.LifeEventsContainer;
import carterdmorgan.com.familymap.data.FamilyAdapter;
import carterdmorgan.com.familymap.data.LifeEventsAdapter;
import carterdmorgan.com.familymap.data.UserDataStore;

public class SearchActivity extends AppCompatActivity {

    private SearchView searchView;
    private RecyclerView rvSearchPerson;
    private RecyclerView rvSearchEvent;
    private ArrayList<LifeEventsContainer> lifeEventsContainers;
    private ArrayList<FamilyContainer> familyContainers;
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

        lifeEventsContainers = new ArrayList<>();
        familyContainers = new ArrayList<>();

        lifeEventsAdapter = new LifeEventsAdapter(SearchActivity.this, lifeEventsContainers);

        rvSearchEvent.setLayoutManager(FamilyContainer.getNoScrollManager(SearchActivity.this));
        rvSearchEvent.setNestedScrollingEnabled(false);
        rvSearchEvent.setAdapter(lifeEventsAdapter);

        familyAdapter = new FamilyAdapter(familyContainers, SearchActivity.this);

        rvSearchPerson.setLayoutManager(FamilyContainer.getNoScrollManager(SearchActivity.this));
        rvSearchPerson.setNestedScrollingEnabled(false);
        rvSearchPerson.setAdapter(familyAdapter);

        searchView.setOnQueryTextListener(new SearchView.OnQueryTextListener() {
            @Override
            public boolean onQueryTextSubmit(String s) {
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
                intent.putExtra(getString(R.string.launch_maps_extra), true);
                startActivity(intent);
                return true;
            default:
                return super.onOptionsItemSelected(item);
        }
    }
}
