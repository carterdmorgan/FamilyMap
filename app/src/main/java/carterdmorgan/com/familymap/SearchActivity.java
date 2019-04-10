package carterdmorgan.com.familymap;

import android.content.Intent;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.SearchView;
import android.view.MenuItem;

import java.util.ArrayList;

import carterdmorgan.com.familymap.containers.FamilyContainer;
import carterdmorgan.com.familymap.containers.LifeEventsContainer;
import carterdmorgan.com.familymap.data.FamilyAdapter;
import carterdmorgan.com.familymap.data.LifeEventsAdapter;
import carterdmorgan.com.familymap.data.Search;

public class SearchActivity extends AppCompatActivity {

    private SearchView searchView;
    private RecyclerView rvSearchPerson;
    private RecyclerView rvSearchEvent;
    private ArrayList<LifeEventsContainer> lifeEventsContainers;
    private ArrayList<FamilyContainer> familyContainers;
    private LifeEventsAdapter lifeEventsAdapter;
    private FamilyAdapter familyAdapter;
    private Search search;
    public static final String TAG = SearchActivity.class.getSimpleName();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_search);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);

        search = new Search();
        initializeRecyclerViews();
        initializeSearchView();
    }

    private void initializeRecyclerViews() {
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
    }

    private void initializeSearchView() {
        searchView = findViewById(R.id.search_view);

        searchView.setOnQueryTextListener(new SearchView.OnQueryTextListener() {
            @Override
            public boolean onQueryTextSubmit(String s) {
                search.executeQuery(s, familyContainers, lifeEventsContainers);

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
