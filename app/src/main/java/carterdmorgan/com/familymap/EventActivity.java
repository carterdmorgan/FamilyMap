package carterdmorgan.com.familymap;

import android.content.Intent;
import android.support.v4.app.FragmentTransaction;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuItem;

import carterdmorgan.com.familymap.api.model.Event;

public class EventActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_event);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);

        if (savedInstanceState == null && getIntent().getExtras() != null) {
            Event event = (Event) getIntent().getExtras().get(getString(R.string.event_extra));
            if (event != null) {
                FragmentTransaction ft = getSupportFragmentManager().beginTransaction();
                ft.replace(R.id.fragment_placeholder_event, MapsFragment.newInstance(event));
                ft.commit();
            }
        }
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case (android.R.id.home):
                Intent intent = new Intent(EventActivity.this, MainActivity.class);
                intent.putExtra(getString(R.string.launch_maps_extra), true);
                startActivity(intent);
                return true;
            default:
                return super.onOptionsItemSelected(item);
        }
    }
}
