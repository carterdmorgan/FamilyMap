package carterdmorgan.com.familymap;

import android.support.v4.app.FragmentTransaction;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;

import carterdmorgan.com.familymap.data.UserDataStore;

public class MainActivity extends AppCompatActivity implements LoginFragment.OnFragmentInteractionListener {

    public static final String TAG = MainActivity.class.getSimpleName();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        if (UserDataStore.getInstance().getUserResult() != null) {
            // Begin the transaction
            FragmentTransaction ft = getSupportFragmentManager().beginTransaction();
            // Replace the contents of the container with the new fragment
            ft.replace(R.id.fragment_placeholder_main, new MapsFragment());
            ft.addToBackStack("maps");
            ft.commit();
        } else {
            // Begin the transaction
            FragmentTransaction ft = getSupportFragmentManager().beginTransaction();
            // Replace the contents of the container with the new fragment
            ft.replace(R.id.fragment_placeholder_main, new LoginFragment());
            ft.commit();
        }
    }

    @Override
    public void onUserLoaded() {
        UserDataStore.getInstance().initializeUserPreferences();

        // Begin the transaction
        FragmentTransaction ft = getSupportFragmentManager().beginTransaction();
        // Replace the contents of the container with the new fragment
        ft.replace(R.id.fragment_placeholder_main, new MapsFragment());
        ft.addToBackStack("maps");
        ft.commit();
    }
}
