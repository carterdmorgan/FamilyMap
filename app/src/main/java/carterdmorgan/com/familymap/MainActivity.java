package carterdmorgan.com.familymap;

import android.net.Uri;
import android.support.v4.app.FragmentTransaction;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuInflater;
import android.widget.Toast;

import carterdmorgan.com.familymap.api.result.CurrentEventResult;
import carterdmorgan.com.familymap.api.result.CurrentPersonResult;
import carterdmorgan.com.familymap.api.result.UserResult;
import carterdmorgan.com.familymap.data.UserDataStore;

public class MainActivity extends AppCompatActivity implements LoginFragment.OnFragmentInteractionListener, MapsFragment.OnFragmentInteractionListener {

    public static final String TAG = MainActivity.class.getSimpleName();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        if (savedInstanceState == null && getIntent().getExtras() != null) {
            if (getIntent().getExtras().getBoolean("launchMaps")) {
                // Begin the transaction
                FragmentTransaction ft = getSupportFragmentManager().beginTransaction();
                // Replace the contents of the container with the new fragment
                ft.replace(R.id.fragment_placeholder, new MapsFragment());
                ft.addToBackStack("maps");
                ft.commit();
            } else {
                // Begin the transaction
                FragmentTransaction ft = getSupportFragmentManager().beginTransaction();
                // Replace the contents of the container with the new fragment
                ft.replace(R.id.fragment_placeholder, new LoginFragment());
                ft.commit();
            }
        } else {
            // Begin the transaction
            FragmentTransaction ft = getSupportFragmentManager().beginTransaction();
            // Replace the contents of the container with the new fragment
            ft.replace(R.id.fragment_placeholder, new LoginFragment());
            ft.commit();
        }
    }

    @Override
    public void onUserLoaded() {
        UserDataStore.getInstance().initializeUserPreferences();

        // Begin the transaction
        FragmentTransaction ft = getSupportFragmentManager().beginTransaction();
        // Replace the contents of the container with the new fragment
        ft.replace(R.id.fragment_placeholder, new MapsFragment());
        ft.addToBackStack("maps");
        ft.commit();
    }

    @Override
    public void onFragmentInteraction(Uri uri) {

    }
}
