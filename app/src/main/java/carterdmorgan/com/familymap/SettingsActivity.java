package carterdmorgan.com.familymap;

import android.content.Intent;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.MenuItem;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.LinearLayout;
import android.widget.Spinner;
import android.widget.Switch;
import android.widget.Toast;

import carterdmorgan.com.familymap.api.network.FamilyMapService;
import carterdmorgan.com.familymap.data.UserDataStore;
import retrofit2.Retrofit;
import retrofit2.converter.gson.GsonConverterFactory;

public class SettingsActivity extends AppCompatActivity {

    private Spinner spnLifeStoryLines;
    private Spinner spnFamilyTreeLines;
    private Spinner spnSpouseLines;
    private Spinner spnMapType;
    private LinearLayout llLogout;
    private LinearLayout llReSync;
    private Switch switchLifeStory;
    private Switch switchFamilyTree;
    private Switch switchSpouse;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_settings);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        initializeSpinners();
        initializeLogout();
        initializeReSync();
        initializeRadioButtons();
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case (android.R.id.home):
                Intent intent = new Intent(SettingsActivity.this, MainActivity.class);
                intent.putExtra("launchMaps", true);
                startActivity(intent);
                return true;
            default:
                return super.onOptionsItemSelected(item);
        }
    }

    @Override
    public void onBackPressed() {
        Intent intent = new Intent(SettingsActivity.this, MainActivity.class);
        intent.putExtra("launchMaps", true);
        startActivity(intent);
    }

    private void initializeSpinners() {
        spnLifeStoryLines = findViewById(R.id.life_story_line_spinner);
        spnFamilyTreeLines = findViewById(R.id.family_tree_line_spinner);
        spnSpouseLines = findViewById(R.id.spouse_line_spinner);
        spnMapType = findViewById(R.id.map_type_spinner);

        ArrayAdapter<CharSequence> colorAdapter = ArrayAdapter.createFromResource(this,
                R.array.line_color_array, android.R.layout.simple_spinner_item);
        colorAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);

        ArrayAdapter<CharSequence> mapTypeAdapter = ArrayAdapter.createFromResource(this,
                R.array.map_type_array, android.R.layout.simple_spinner_item);
        colorAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);

        spnLifeStoryLines.setAdapter(colorAdapter);
        spnFamilyTreeLines.setAdapter(colorAdapter);
        spnSpouseLines.setAdapter(colorAdapter);
        spnMapType.setAdapter(mapTypeAdapter);

        spnLifeStoryLines.setSelection(0);
        spnFamilyTreeLines.setSelection(1);
        spnSpouseLines.setSelection(2);

        spnMapType.setSelection(UserDataStore.getInstance().getMapType());
        spnMapType.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                UserDataStore.getInstance().setMapType(position);
            }

            @Override
            public void onNothingSelected(AdapterView<?> parent) {

            }
        });
    }

    private void initializeRadioButtons() {
        switchFamilyTree = findViewById(R.id.family_tree_switch);
        switchLifeStory = findViewById(R.id.life_story_switch);
        switchSpouse = findViewById(R.id.spouse_switch);
    }

    private void initializeLogout() {
        llLogout = findViewById(R.id.logout_linear_layout);

        llLogout.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                UserDataStore.getInstance().clearAllData();
                Intent intent = new Intent(getBaseContext(), MainActivity.class);
                startActivity(intent);
            }
        });
    }

    private void initializeReSync() {
        llReSync = findViewById(R.id.re_sync_data_layout);

        llReSync.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                reSyncData();
            }
        });
    }

    private void reSyncData() {
        Retrofit retrofit = new Retrofit.Builder()
                .baseUrl(String.format("http://%s:%s", UserDataStore.getInstance().getServerHost(),
                        UserDataStore.getInstance().getServerPort()))
                .addConverterFactory(GsonConverterFactory.create())
                .build();
        FamilyMapService fmService = retrofit.create(FamilyMapService.class);
        UserDataStore.getInstance().retrieveFamilyData(fmService, new UserDataStore.LoadFamilyDataListener() {
            @Override
            public void onSuccess() {
                Intent intent = new Intent(SettingsActivity.this, MainActivity.class);
                intent.putExtra("launchMaps", true);
                startActivity(intent);
            }

            @Override
            public void onFailure() {
                Toast.makeText(SettingsActivity.this, "Failed to re-sync data.", Toast.LENGTH_SHORT).show();
            }
        });
    }
}
