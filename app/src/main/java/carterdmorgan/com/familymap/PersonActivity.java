package carterdmorgan.com.familymap;

import android.content.Intent;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.MenuItem;
import android.widget.TextView;

import carterdmorgan.com.familymap.api.model.Person;

public class PersonActivity extends AppCompatActivity {

    private TextView tvFirstName;
    private TextView tvLastName;
    private TextView tvGender;
    private Person person;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_person);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);

        person = (Person) getIntent().getExtras().get("person");

        tvFirstName = findViewById(R.id.person_first_name_text_view);
        tvLastName = findViewById(R.id.person_last_name_text_view);
        tvGender = findViewById(R.id.person_gender_text_view);

        tvFirstName.setText(person.getFirstName());
        tvLastName.setText(person.getLastName());
        tvGender.setText(person.getGender().equals("m") ? "Male" : "Female");
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case (android.R.id.home):
                Intent intent = new Intent(PersonActivity.this, MainActivity.class);
                intent.putExtra("launchMaps", true);
                startActivity(intent);
                return true;
            default:
                return super.onOptionsItemSelected(item);
        }
    }
}
