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
            FragmentTransaction ft = getSupportFragmentManager().beginTransaction();
            ft.replace(R.id.fragment_placeholder_main, new MapsFragment());
            ft.addToBackStack(getString(R.string.map_back_stack_indicator));
            ft.commit();
        } else {
            FragmentTransaction ft = getSupportFragmentManager().beginTransaction();
            ft.replace(R.id.fragment_placeholder_main, new LoginFragment());
            ft.commit();
        }
    }

    @Override
    public void onUserLoaded() {
        UserDataStore.getInstance().initializeUserPreferences();
        FragmentTransaction ft = getSupportFragmentManager().beginTransaction();
        ft.replace(R.id.fragment_placeholder_main, new MapsFragment());
        ft.addToBackStack(getString(R.string.map_back_stack_indicator));
        ft.commit();
    }
}
