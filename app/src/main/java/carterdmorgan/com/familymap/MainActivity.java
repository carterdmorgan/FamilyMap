package carterdmorgan.com.familymap;

import android.net.Uri;
import android.support.v4.app.FragmentTransaction;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.widget.Toast;

import carterdmorgan.com.familymap.api.result.CurrentEventResult;
import carterdmorgan.com.familymap.api.result.CurrentPersonResult;
import carterdmorgan.com.familymap.api.result.UserResult;

public class MainActivity extends AppCompatActivity implements LoginFragment.OnFragmentInteractionListener, MapsFragment.OnFragmentInteractionListener {

    public static final String TAG = MainActivity.class.getSimpleName();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        // Begin the transaction
        FragmentTransaction ft = getSupportFragmentManager().beginTransaction();
        // Replace the contents of the container with the new fragment
        ft.replace(R.id.fragment_placeholder, new LoginFragment());
        ft.commit();
    }

    @Override
    public void onUserLoaded(UserResult userResult, CurrentPersonResult currentPersonResult, CurrentEventResult currentEventResult) {
        // Begin the transaction
        FragmentTransaction ft = getSupportFragmentManager().beginTransaction();
        // Replace the contents of the container with the new fragment
        ft.replace(R.id.fragment_placeholder, MapsFragment.newInstance(userResult, currentPersonResult, currentEventResult));
        ft.commit();
    }

    @Override
    public void onFragmentInteraction(Uri uri) {

    }
}
