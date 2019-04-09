package carterdmorgan.com.familymap;

import android.content.Intent;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.MenuItem;
import android.widget.CompoundButton;
import android.widget.Switch;

import carterdmorgan.com.familymap.containers.FamilyContainer;
import carterdmorgan.com.familymap.data.FilterAdapter;
import carterdmorgan.com.familymap.data.UserDataStore;

public class FilterActivity extends AppCompatActivity {

    private Switch switchFathersSide;
    private Switch switchMothersSide;
    private Switch switchMale;
    private Switch switchFemale;
    private RecyclerView rvFilters;
    private RecyclerView.Adapter filterAdapter;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_filter);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        initializeSwitches();

        rvFilters = findViewById(R.id.filter_recycler_view);

        rvFilters.setLayoutManager(FamilyContainer.getNoScrollManager(FilterActivity.this));
        rvFilters.setNestedScrollingEnabled(false);

        filterAdapter = new FilterAdapter(UserDataStore.getInstance().getEventTypes());

        rvFilters.setAdapter(filterAdapter);
    }

    private void initializeSwitches() {
        switchFathersSide = findViewById(R.id.filter_fathers_side_switch);
        switchMothersSide = findViewById(R.id.filter_mothers_side_switch);
        switchMale = findViewById(R.id.filter_male_switch);
        switchFemale = findViewById(R.id.filter_female_switch);

        switchFathersSide.setChecked(UserDataStore.getInstance().isShowFather());
        switchMothersSide.setChecked(UserDataStore.getInstance().isShowMother());
        switchMale.setChecked(UserDataStore.getInstance().isShowMale());
        switchFemale.setChecked(UserDataStore.getInstance().isShowFemale());

        switchFathersSide.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                UserDataStore.getInstance().setShowFather(isChecked);
            }
        });

        switchMothersSide.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                UserDataStore.getInstance().setShowMother(isChecked);
            }
        });

        switchMale.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                UserDataStore.getInstance().setShowMale(isChecked);
            }
        });

        switchFemale.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                UserDataStore.getInstance().setShowFemale(isChecked);
            }
        });
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case (android.R.id.home):
                Intent intent = new Intent(FilterActivity.this, MainActivity.class);
                intent.putExtra(getString(R.string.launch_maps_extra), true);
                startActivity(intent);
                return true;
            default:
                return super.onOptionsItemSelected(item);
        }
    }

    @Override
    public void onBackPressed() {
        Intent intent = new Intent(FilterActivity.this, MainActivity.class);
        intent.putExtra(getString(R.string.launch_maps_extra), true);
        startActivity(intent);
    }
}
