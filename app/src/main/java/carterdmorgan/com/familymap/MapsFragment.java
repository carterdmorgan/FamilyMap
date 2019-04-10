package carterdmorgan.com.familymap;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.text.TextUtils;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.SupportMapFragment;
import com.google.android.gms.maps.model.BitmapDescriptorFactory;
import com.google.android.gms.maps.model.CameraPosition;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.Marker;
import com.google.android.gms.maps.model.MarkerOptions;
import com.google.android.gms.maps.model.Polyline;
import com.google.android.gms.maps.model.PolylineOptions;

import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.Map;

import carterdmorgan.com.familymap.api.model.Event;
import carterdmorgan.com.familymap.api.model.Person;
import carterdmorgan.com.familymap.data.MapType;
import carterdmorgan.com.familymap.data.PersonHelper;
import carterdmorgan.com.familymap.data.UserDataStore;


/**
 * A simple {@link Fragment} subclass.
 * Activities that contain this fragment must implement the
 * to handle interaction events.
 */
public class MapsFragment extends Fragment implements OnMapReadyCallback {
    public static final String TAG = MapsFragment.class.getSimpleName();

    private TextView tvEventPerson;
    private TextView tvEventType;
    private TextView tvEventDate;
    private TextView tvEventLocation;
    private ImageView ivGenderIcon;
    private LinearLayout llEventInfo;
    private SupportMapFragment supportMapFragment;
    private Event zoomEvent;
    private ArrayList<Polyline> familyTreeLines;
    private ArrayList<Polyline> spouseLines;
    private ArrayList<Polyline> lifeStoryLines;

    public static final String ARG_ZOOM_EVENT = "zoom_event";

    private static final float STARTING_WIDTH = 15.0f;
    private static final float WIDTH_DECREMENT = 3.0f;

    private Map<Marker, Event> markerEventMap;
    private ArrayList<Marker> markers = new ArrayList<>();
    private Person currentPerson;

    public MapsFragment() { }

    public static MapsFragment newInstance(Event event) {
        MapsFragment fragment = new MapsFragment();
        Bundle args = new Bundle();
        args.putParcelable(ARG_ZOOM_EVENT, event);
        fragment.setArguments(args);
        return fragment;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setHasOptionsMenu(true);

        markerEventMap = new HashMap<>();
        familyTreeLines = new ArrayList<>();
        spouseLines = new ArrayList<>();
        lifeStoryLines = new ArrayList<>();

        if (this.getArguments() != null)
            zoomEvent = (Event) this.getArguments().get(ARG_ZOOM_EVENT);
    }

    @Override
    public void onActivityCreated(@Nullable Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_maps, container, false);

        initializeEventInfoBox(view);
        initializeGoogleMap();

        return view;
    }

    private void initializeEventInfoBox(View view) {
        tvEventPerson = view.findViewById(R.id.event_person_text_view);
        tvEventType = view.findViewById(R.id.event_type_text_view);
        tvEventLocation = view.findViewById(R.id.event_location_text_view);
        tvEventDate = view.findViewById(R.id.event_date_text_view);
        ivGenderIcon = view.findViewById(R.id.gender_icon_image_view);
        llEventInfo = view.findViewById(R.id.event_info_linear_layout);

        llEventInfo.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (!TextUtils.isEmpty(tvEventPerson.getText().toString())) {
                    Intent intent = new Intent(getContext(), PersonActivity.class);
                    intent.putExtra(getString(R.string.person_extra), currentPerson);
                    startActivity(intent);
                }
            }
        });
    }

    private void initializeGoogleMap() {
        supportMapFragment = (SupportMapFragment) getChildFragmentManager()
                .findFragmentById(R.id.support_maps_fragment);

        supportMapFragment.getMapAsync(this);
    }

    @Override
    public void onCreateOptionsMenu(Menu menu, MenuInflater inflater) {
        if (zoomEvent == null) {
            inflater.inflate(R.menu.menu_main, menu);
            super.onCreateOptionsMenu(menu, inflater);
        }
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch(item.getItemId()) {
            case(R.id.menu_main_settings):
                Intent intent = new Intent(getContext(), SettingsActivity.class);
                startActivity(intent);
                break;
            case (R.id.menu_main_search):
                Intent searchIntent = new Intent(getContext(), SearchActivity.class);
                startActivity(searchIntent);
                break;
            case (R.id.menu_main_filter):
                Intent filterIntent = new Intent(getContext(), FilterActivity.class);
                startActivity(filterIntent);
                break;
        }

        return true;
    }

    @Override
    public void onAttach(Context context) {
        super.onAttach(context);
    }

    @Override
    public void onDetach() {
        super.onDetach();
    }

    private void setMapType(GoogleMap googleMap) {
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
    }

    private void generateMarkers(GoogleMap googleMap, ArrayList<Event> events) {
        for (Event event : events) {
            Float color = UserDataStore.getInstance().getMarkerColors().get(event.getEventType().toLowerCase());
            LatLng location = new LatLng(event.getLatitude(), event.getLongitude());
            MarkerOptions markerOptions = new MarkerOptions().position(location).icon(BitmapDescriptorFactory.defaultMarker(color));
            Marker marker = googleMap.addMarker(markerOptions);
            markers.add(marker);
            markerEventMap.put(marker, event);
        }
    }

    private void populateEventInfoBox(Event event, Person person) {
        tvEventPerson.setText(person.getFullName());
        tvEventType.setText(event.getEventType());
        tvEventLocation.setText(event.getCity() + ", " + event.getCountry());
        tvEventDate.setText(Integer.toString(event.getYear()));

        if (person.getGender().equals(PersonHelper.GENDER_MARKER_FEMALE)) {
            ivGenderIcon.setImageDrawable(getContext().getDrawable(R.drawable.ic_person_pink_100_24dp));
        } else {
            ivGenderIcon.setImageDrawable(getContext().getDrawable(R.drawable.ic_person_blue_400_24dp));
        }
    }

    @Override
    public void onMapReady(GoogleMap googleMap) {
        setMapType(googleMap);
        googleMap.setOnMarkerClickListener(getMarkerClickListener(googleMap));
        generateMarkers(googleMap, UserDataStore.getInstance().getFilteredEvents());

        if (zoomEvent != null) {
            Event event = zoomEvent;

            Person person = UserDataStore.getInstance().getPersonForEvent(zoomEvent);
            currentPerson = person;

            populateEventInfoBox(event, person);
            drawLines(googleMap, event, person);
            zoomCameraOnEvent(googleMap, event);
        }
    }

    private void zoomCameraOnEvent(GoogleMap googleMap, Event event) {
        CameraPosition cameraPosition = new CameraPosition.Builder()
                .target(new LatLng(event.getLatitude(), event.getLongitude()))
                .zoom(3)
                .build();
        googleMap.animateCamera(CameraUpdateFactory.newCameraPosition(cameraPosition));
    }

    private GoogleMap.OnMarkerClickListener getMarkerClickListener(final GoogleMap googleMap) {
        return new GoogleMap.OnMarkerClickListener() {
            @Override
            public boolean onMarkerClick(Marker marker) {
                Event event = markerEventMap.get(marker);
                Person person = UserDataStore.getInstance().getPersonForEvent(event);
                currentPerson = person;

                populateEventInfoBox(event, person);
                drawLines(googleMap, event, person);

                return true;
            }
        };
    }

    private void drawLines(GoogleMap googleMap, Event event, Person person) {
        if (UserDataStore.getInstance().isShowLifeStoryLines())
            drawLifeStoryLines(googleMap, event);

        if (UserDataStore.getInstance().isShowSpouseLines()) {
            drawSpouseLines(googleMap, person, event);
        }

        if (UserDataStore.getInstance().isShowFamilyTreeLines()) {
            drawFamilyTreeLines(googleMap, person, event);
        }
    }

    private void drawSpouseLines(GoogleMap googleMap, Person person, Event event) {
        Person spouse = null;
        clearSpouseLines();

        int color = UserDataStore.getInstance().getColorCode(UserDataStore.getInstance().getSpouseLineColor());

        if (person != null && person.getSpouse() != null) {
            for (Person p : UserDataStore.getInstance().getAllPersons()) {
                if (person.getSpouse() != null && p.getPersonID().equals(person.getSpouse())) {
                    spouse = p;
                    break;
                }
            }

            if (spouse != null) {
                ArrayList<Event> spouseEvents = new ArrayList<>();
                ArrayList<Event> allEvents = new ArrayList<>();

                for (Marker m : markers) {
                    allEvents.add(markerEventMap.get(m));
                }

                for (Event e : allEvents) {
                    if (e.getPersonID().equals(spouse.getPersonID())) {
                        spouseEvents.add(e);
                    }
                }

                Event earliestEvent = Event.getEarliestEvent(spouseEvents);

                Polyline line = createPolylineFromEvents(googleMap, event, earliestEvent, STARTING_WIDTH, color);
                spouseLines.add(line);
            }
        }
    }

    private void drawFamilyTreeLines(GoogleMap googleMap, Person person, Event event) {
        clearFamilyTreeLines();
        if (person != null) {
            int color = UserDataStore.getInstance().getColorCode(UserDataStore.getInstance().getFamilyTreeLineColor());
            recursivelyDrawFamilyTreeLines(googleMap, person, event, STARTING_WIDTH, color);
        }
    }

    private void drawLifeStoryLines(GoogleMap googleMap, Event event) {
        clearLifeStoryLines();
        int color = UserDataStore.getInstance().getColorCode(UserDataStore.getInstance().getLifeStoryLineColor());
        ArrayList<Event> events
                = UserDataStore.getInstance().getAllEventsForPerson(UserDataStore.getInstance().getPersonForEvent(event));
        Collections.sort(events, Event.SORT_BY_YEAR_AND_NAME);

        for (int i = 0; i < events.size() - 1; i++) {
            Event event0 = events.get(i);
            Event event1 = events.get(i+1);
            Polyline line = createPolylineFromEvents(googleMap, event0, event1, STARTING_WIDTH, color);
            lifeStoryLines.add(line);
        }
    }

    private void recursivelyDrawFamilyTreeLines(GoogleMap googleMap, Person person, Event event, float width, int color) {
        Person mother = UserDataStore.getInstance().getMotherForPerson(person);
        Person father = UserDataStore.getInstance().getFatherForPerson(person);;

        if (mother != null) {
            ArrayList<Event> events = UserDataStore.getInstance().getAllEventsForPerson(mother);
            Event earliestEvent = Event.getEarliestEvent(events);
            Polyline line = createPolylineFromEvents(googleMap, event, earliestEvent, width, color);
            familyTreeLines.add(line);
            width -= WIDTH_DECREMENT;
            recursivelyDrawFamilyTreeLines(googleMap, mother, earliestEvent, width, color);
        }

        if (father != null) {
            ArrayList<Event> events = UserDataStore.getInstance().getAllEventsForPerson(father);
            Event earliestEvent = Event.getEarliestEvent(events);
            Polyline line = createPolylineFromEvents(googleMap, event, earliestEvent, width, color);
            familyTreeLines.add(line);
            width -= WIDTH_DECREMENT;
            recursivelyDrawFamilyTreeLines(googleMap, father, earliestEvent, width, color);
        }
    }

    private Polyline createPolylineFromEvents(GoogleMap googleMap, Event event1, Event event2, float width, int color) {
        Polyline line = googleMap.addPolyline(new PolylineOptions()
                .add(new LatLng(event1.getLatitude(), event1.getLongitude()),
                        new LatLng(event2.getLatitude(), event2.getLongitude()))
                .width(width)
                .color(color));

        return line;
    }

    private void clearLifeStoryLines() {
        for (Polyline line : lifeStoryLines) {
            line.remove();
        }

        lifeStoryLines.clear();
    }

    private void clearFamilyTreeLines() {
        for (Polyline line : familyTreeLines) {
            line.remove();
        }

        familyTreeLines.clear();
    }

    private void clearSpouseLines() {
        for (Polyline line : spouseLines) {
            line.remove();
        }

        spouseLines.clear();
    }
}
