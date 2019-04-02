package carterdmorgan.com.familymap;

import android.content.Context;
import android.net.Uri;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.SupportMapFragment;
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
import retrofit2.Retrofit;
import retrofit2.converter.gson.GsonConverterFactory;

import static carterdmorgan.com.familymap.MainActivity.TAG;


/**
 * A simple {@link Fragment} subclass.
 * Activities that contain this fragment must implement the
 * {@link MapsFragment.OnFragmentInteractionListener} interface
 * to handle interaction events.
 * Use the {@link MapsFragment#newInstance} factory method to
 * create an instance of this fragment.
 */
public class MapsFragment extends Fragment implements GoogleMap.OnMarkerClickListener, OnMapReadyCallback {
    // the fragment initialization parameters, e.g. ARG_ITEM_NUMBER
    private static final String ARG_USER = "user";
    private static final String ARG_PERSON = "persons";
    private static final String ARG_EVENT = "events";

    public static final String TAG = MapsFragment.class.getSimpleName();

    // TODO: Rename and change types of parameters
    private UserResult userResult;
    private CurrentPersonResult currentPersonResult;
    private CurrentEventResult currentEventResult;
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

    /**
     * Use this factory method to create a new instance of
     * this fragment using the provided parameters.
     *
     * @return A new instance of fragment MapsFragment.
     */
    public static MapsFragment newInstance(UserResult userResult, CurrentPersonResult currentPersonResult, CurrentEventResult currentEventResult) {
        MapsFragment fragment = new MapsFragment();
        Bundle args = new Bundle();
        args.putParcelable(ARG_USER, userResult);
        args.putParcelable(ARG_PERSON, currentPersonResult);
        args.putParcelable(ARG_EVENT, currentEventResult);
        fragment.setArguments(args);
        return fragment;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        markerEventMap = new HashMap<>();
        if (getArguments() != null) {
            userResult = getArguments().getParcelable(ARG_USER);
            currentPersonResult = getArguments().getParcelable(ARG_PERSON);
            currentEventResult = getArguments().getParcelable(ARG_EVENT);
        }
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

        Log.d(TAG, "onCreateView: userResult: " + userResult.toString());
        Log.d(TAG, "onCreateView: currentPersonResult: " + currentPersonResult.toString());
        Log.d(TAG, "onCreateView: currentEventResult: " + currentEventResult.toString());

        return view;
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
        for (Person person : currentPersonResult.getData()) {
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
        googleMap.setOnMarkerClickListener(this);

        for (Event event : currentEventResult.getData()) {
            LatLng location = new LatLng(event.getLatitude(), event.getLongitude());
            MarkerOptions markerOptions = new MarkerOptions().position(location);
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
