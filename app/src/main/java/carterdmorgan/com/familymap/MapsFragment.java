package carterdmorgan.com.familymap;

import android.content.Context;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.SupportMapFragment;
import com.google.android.gms.maps.model.BitmapDescriptorFactory;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.Marker;
import com.google.android.gms.maps.model.MarkerOptions;

import java.util.HashMap;
import java.util.Map;

import carterdmorgan.com.familymap.api.model.Event;
import carterdmorgan.com.familymap.api.model.Person;
import carterdmorgan.com.familymap.api.network.FamilyMapService;
import carterdmorgan.com.familymap.api.result.CurrentEventResult;
import carterdmorgan.com.familymap.api.result.CurrentPersonResult;
import carterdmorgan.com.familymap.api.result.UserResult;
import carterdmorgan.com.familymap.data.MapType;
import carterdmorgan.com.familymap.data.UserDataStore;
import retrofit2.Retrofit;
import retrofit2.converter.gson.GsonConverterFactory;

import static carterdmorgan.com.familymap.MainActivity.TAG;


/**
 * A simple {@link Fragment} subclass.
 * Activities that contain this fragment must implement the
 * {@link MapsFragment.OnFragmentInteractionListener} interface
 * to handle interaction events.
 */
public class MapsFragment extends Fragment implements GoogleMap.OnMarkerClickListener, OnMapReadyCallback {
    public static final String TAG = MapsFragment.class.getSimpleName();

    private Map<Marker, Event> markerEventMap;

    private TextView tvEventPerson;
    private TextView tvEventType;
    private TextView tvEventDate;
    private TextView tvEventLocation;
    private SupportMapFragment supportMapFragment;

    private OnFragmentInteractionListener mListener;

    public MapsFragment() {
        // Required empty public constructor
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setHasOptionsMenu(true);
        markerEventMap = new HashMap<>();
    }

    @Override
    public void onActivityCreated(@Nullable Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View view = inflater.inflate(R.layout.fragment_maps, container, false);

        tvEventPerson = view.findViewById(R.id.event_person_text_view);
        tvEventType = view.findViewById(R.id.event_type_text_view);
        tvEventLocation = view.findViewById(R.id.event_location_text_view);
        tvEventDate = view.findViewById(R.id.event_date_text_view);

        supportMapFragment = (SupportMapFragment) getChildFragmentManager()
                .findFragmentById(R.id.support_maps_fragment);

        supportMapFragment.getMapAsync(this);

        return view;
    }

    @Override
    public void onCreateOptionsMenu(Menu menu, MenuInflater inflater) {
        inflater.inflate(R.menu.menu_main, menu);
        super.onCreateOptionsMenu(menu, inflater);
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch(item.getItemId()) {
            case(R.id.menu_main_settings):
                Intent intent = new Intent(getContext(), SettingsActivity.class);
                startActivity(intent);
                break;
            case (R.id.menu_main_search):
                Toast.makeText(getContext(), "Will launch search activity.", Toast.LENGTH_SHORT).show();
                break;
            case (R.id.menu_main_filter):
                Toast.makeText(getContext(), "Will launch filter activity.", Toast.LENGTH_SHORT).show();
                break;
        }

        return true;
    }

    @Override
    public void onAttach(Context context) {
        super.onAttach(context);
        if (context instanceof OnFragmentInteractionListener) {
            mListener = (OnFragmentInteractionListener) context;
        } else {
            throw new RuntimeException(context.toString()
                    + " must implement OnFragmentInteractionListener");
        }
    }

    @Override
    public void onDetach() {
        super.onDetach();
        mListener = null;
    }

    @Override
    public boolean onMarkerClick(Marker marker) {
        Event event = markerEventMap.get(marker);
        for (Person person : UserDataStore.getInstance().getCurrentPersonResult().getData()) {
            if (person.getPersonID().equals(event.getPersonID())) {
                tvEventPerson.setText(person.getFirstName() + " " + person.getLastName());
            }
        }

        tvEventType.setText(event.getEventType());
        tvEventLocation.setText(event.getCity() + ", " + event.getCountry());
        tvEventDate.setText(Integer.toString(event.getYear()));

        return true;
    }

    @Override
    public void onMapReady(GoogleMap googleMap) {
        switch (UserDataStore.getInstance().getMapType()) {
            case MapType.NORMAL:
                googleMap.setMapType(GoogleMap.MAP_TYPE_NORMAL);
                break;
            case MapType.HYBRID:
                googleMap.setMapType(GoogleMap.MAP_TYPE_HYBRID);
                break;
            case MapType.SATELLITE:
                googleMap.setMapType(GoogleMap.MAP_TYPE_SATELLITE);
                break;
            case MapType.TERRAIN:
                googleMap.setMapType(GoogleMap.MAP_TYPE_TERRAIN);
                break;
            default:
                googleMap.setMapType(GoogleMap.MAP_TYPE_NORMAL);
                break;
        }


        googleMap.setOnMarkerClickListener(this);
        Map<String, Float> typeColorMap = Event.getTypeColorMap();

        for (Event event : UserDataStore.getInstance().getCurrentEventResult().getData()) {
            Float color = typeColorMap.get(event.getEventType());
            LatLng location = new LatLng(event.getLatitude(), event.getLongitude());
            MarkerOptions markerOptions = new MarkerOptions().position(location).icon(BitmapDescriptorFactory.defaultMarker(color));
            Marker marker = googleMap.addMarker(markerOptions);
            markerEventMap.put(marker, event);
        }
    }

    /**
     * This interface must be implemented by activities that contain this
     * fragment to allow an interaction in this fragment to be communicated
     * to the activity and potentially other fragments contained in that
     * activity.
     * <p>
     * See the Android Training lesson <a href=
     * "http://developer.android.com/training/basics/fragments/communicating.html"
     * >Communicating with Other Fragments</a> for more information.
     */
    public interface OnFragmentInteractionListener {
        void onFragmentInteraction(Uri uri);
    }


}
