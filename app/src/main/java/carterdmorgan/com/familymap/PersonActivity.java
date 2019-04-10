package carterdmorgan.com.familymap;

import android.content.Intent;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.RecyclerView;
import android.view.MenuItem;
import android.view.View;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import java.util.ArrayList;

import carterdmorgan.com.familymap.api.model.Person;
import carterdmorgan.com.familymap.containers.FamilyContainer;
import carterdmorgan.com.familymap.containers.LifeEventsContainer;
import carterdmorgan.com.familymap.data.FamilyAdapter;
import carterdmorgan.com.familymap.data.LifeEventsAdapter;
import carterdmorgan.com.familymap.data.PersonHelper;
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

        displayPerson = (Person) getIntent().getExtras().get(getString(R.string.person_extra));

        initializeTextViews();
        initializeLinearLayouts();
        initializeLifeEventsList();
        initializeFamilyList();
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case (android.R.id.home):
                Intent intent = new Intent(PersonActivity.this, MainActivity.class);
                intent.putExtra(getString(R.string.launch_maps_extra), true);
                intent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
                startActivity(intent);
                return true;
            default:
                return super.onOptionsItemSelected(item);
        }
    }

    private void initializeLifeEventsList() {
        ArrayList<LifeEventsContainer> lifeEventsContainers =
                UserDataStore.getInstance().compileLifeEventsContainersForPerson(displayPerson);

        rvLifeEvents = findViewById(R.id.life_events_recycler_view);
        rvFamily = findViewById(R.id.family_recycler_view);

        LifeEventsAdapter lifeEventsAdapter = new LifeEventsAdapter(PersonActivity.this, lifeEventsContainers);

        rvLifeEvents.setLayoutManager(FamilyContainer.getNoScrollManager(PersonActivity.this));
        rvLifeEvents.setNestedScrollingEnabled(false);
        rvLifeEvents.setAdapter(lifeEventsAdapter);
    }

    private void initializeFamilyList() {
        ArrayList<FamilyContainer> familyContainers =
                UserDataStore.getInstance().compileFamilyContainersForPerson(displayPerson);

        FamilyAdapter familyAdapter = new FamilyAdapter(familyContainers, PersonActivity.this);

        rvFamily.setLayoutManager(FamilyContainer.getNoScrollManager(PersonActivity.this));
        rvFamily.setNestedScrollingEnabled(false);
        rvFamily.setAdapter(familyAdapter);
    }

    private void initializeTextViews() {
        tvFirstName = findViewById(R.id.person_first_name_text_view);
        tvLastName = findViewById(R.id.person_last_name_text_view);
        tvGender = findViewById(R.id.person_gender_text_view);

        tvFirstName.setText(displayPerson.getFirstName());
        tvLastName.setText(displayPerson.getLastName());
        tvGender.setText(displayPerson.getGender()
                .equals(PersonHelper.GENDER_MARKER_MALE) ? PersonHelper.GENDER_TITLE_MALE : PersonHelper.GENDER_TITLE_FEMALE);
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

}
