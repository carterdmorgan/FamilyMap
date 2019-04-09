package carterdmorgan.com.familymap;

import android.content.Context;
import android.content.Intent;
import android.graphics.Color;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.text.TextUtils;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.gms.maps.CameraUpdate;
import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.SupportMapFragment;
import com.google.android.gms.maps.model.BitmapDescriptorFactory;
import com.google.android.gms.maps.model.CameraPosition;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.LatLngBounds;
import com.google.android.gms.maps.model.Marker;
import com.google.android.gms.maps.model.MarkerOptions;
import com.google.android.gms.maps.model.Polyline;
import com.google.android.gms.maps.model.PolylineOptions;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.HashMap;
import java.util.Iterator;
import java.util.Map;

import carterdmorgan.com.familymap.api.model.Event;
import carterdmorgan.com.familymap.api.model.Person;
import carterdmorgan.com.familymap.data.MapType;
import carterdmorgan.com.familymap.data.RelationshipLines;
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
    private Person currentPerson;
    private Event zoomEvent;
    private ArrayList<Polyline> familyTreeLines;
    private ArrayList<Polyline> spouseLines;
    private ArrayList<Polyline> lifeStoryLines;

    public static final String ARG_ZOOM_EVENT = "zoom_event";

    private static final float STARTING_WIDTH = 15.0f;
    private static final float WIDTH_DECREMENT = 1.5f;

    private Map<Marker, Event> markerEventMap;
    private ArrayList<Marker> markers = new ArrayList<>();

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

        supportMapFragment = (SupportMapFragment) getChildFragmentManager()
                .findFragmentById(R.id.support_maps_fragment);

        supportMapFragment.getMapAsync(this);

        return view;
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


        googleMap.setOnMarkerClickListener(getMarkerClickListener(googleMap));

        for (Event event : UserDataStore.getInstance().getFilteredEvents()) {
            Float color = UserDataStore.getInstance().getMarkerColors().get(event.getEventType().toLowerCase());
            LatLng location = new LatLng(event.getLatitude(), event.getLongitude());
            MarkerOptions markerOptions = new MarkerOptions().position(location).icon(BitmapDescriptorFactory.defaultMarker(color));
            Marker marker = googleMap.addMarker(markerOptions);
            markers.add(marker);
            markerEventMap.put(marker, event);
        }

        if (zoomEvent != null) {
            Event event = zoomEvent;
            Person spouse = null;

            currentPerson = UserDataStore.getInstance().getPersonForEvent(zoomEvent);

            tvEventPerson.setText(currentPerson.getFullName());
            tvEventType.setText(event.getEventType());
            tvEventLocation.setText(event.getCity() + ", " + event.getCountry());
            tvEventDate.setText(Integer.toString(event.getYear()));

            if (currentPerson.getGender().equals(Person.GENDER_MARKER_FEMALE)) {
                ivGenderIcon.setImageDrawable(getContext().getDrawable(R.drawable.ic_person_pink_100_24dp));
            } else {
                ivGenderIcon.setImageDrawable(getContext().getDrawable(R.drawable.ic_person_blue_400_24dp));
            }

            if (UserDataStore.getInstance().isShowLifeStoryLines())
                drawLifeStoryLines(googleMap, event);

            if (UserDataStore.getInstance().isShowSpouseLines()) {
                clearSpouseLines();

                int color = 0;
                if (UserDataStore.getInstance().getSpouseLineColor().equals(RelationshipLines.RED)) {
                    color = Color.RED;
                } else if (UserDataStore.getInstance().getSpouseLineColor().equals(RelationshipLines.BLUE)) {
                    color = Color.BLUE;
                } else if (UserDataStore.getInstance().getSpouseLineColor().equals(RelationshipLines.GREEN)) {
                    color = Color.GREEN;
                }

                if (currentPerson != null && currentPerson.getSpouse() != null) {
                    for (Person p : UserDataStore.getInstance().getAllPersons()) {
                        if (currentPerson.getSpouse() != null && p.getPersonID().equals(currentPerson.getSpouse())) {
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

                        Event earliestEvent = spouseEvents.get(0);

                        for (Event e : spouseEvents) {
                            if (e.getYear() < earliestEvent.getYear()) {
                                earliestEvent = e;
                            }
                        }


                        Polyline line = googleMap.addPolyline(new PolylineOptions()
                                .add(new LatLng(event.getLatitude(), event.getLongitude()),
                                        new LatLng(earliestEvent.getLatitude(), earliestEvent.getLongitude()))
                                .width(STARTING_WIDTH)
                                .color(color));
                        spouseLines.add(line);
                    }
                }

            }

            if (UserDataStore.getInstance().isShowFamilyTreeLines()) {
                clearFamilyTreeLines();

                if (currentPerson != null) {
                    int color = 0;
                    if (UserDataStore.getInstance().getFamilyTreeLineColor().equals(RelationshipLines.RED)) {
                        color = Color.RED;
                    } else if (UserDataStore.getInstance().getFamilyTreeLineColor().equals(RelationshipLines.BLUE)) {
                        color = Color.BLUE;
                    } else if (UserDataStore.getInstance().getFamilyTreeLineColor().equals(RelationshipLines.GREEN)) {
                        color = Color.GREEN;
                    }

                    recursivelyDrawFamilyTreeLines(googleMap, currentPerson, event, STARTING_WIDTH, color);
                }
            }

            CameraPosition cameraPosition = new CameraPosition.Builder()
                    .target(new LatLng(zoomEvent.getLatitude(), zoomEvent.getLongitude()))
                    .zoom(3)
                    .build();
            googleMap.animateCamera(CameraUpdateFactory.newCameraPosition(cameraPosition));
        }
    }

    private GoogleMap.OnMarkerClickListener getMarkerClickListener(final GoogleMap googleMap) {
        return new GoogleMap.OnMarkerClickListener() {
            @Override
            public boolean onMarkerClick(Marker marker) {
                Event event = markerEventMap.get(marker);
                Person person = null;
                Person spouse = null;

                for (Person p : UserDataStore.getInstance().getAllPersons()) {
                    if (p.getPersonID().equals(event.getPersonID())) {
                        tvEventPerson.setText(p.getFullName());
                        currentPerson = p;
                        person = p;
                    }
                }

                tvEventType.setText(event.getEventType());
                tvEventLocation.setText(event.getCity() + ", " + event.getCountry());
                tvEventDate.setText(Integer.toString(event.getYear()));

                if (currentPerson.getGender().equals(Person.GENDER_MARKER_FEMALE)) {
                    ivGenderIcon.setImageDrawable(getContext().getDrawable(R.drawable.ic_person_pink_100_24dp));
                } else {
                    ivGenderIcon.setImageDrawable(getContext().getDrawable(R.drawable.ic_person_blue_400_24dp));
                }

                if (UserDataStore.getInstance().isShowLifeStoryLines())
                    drawLifeStoryLines(googleMap, event);

                if (UserDataStore.getInstance().isShowSpouseLines()) {
                    clearSpouseLines();

                    int color = 0;
                    if (UserDataStore.getInstance().getSpouseLineColor().equals(RelationshipLines.RED)) {
                        color = Color.RED;
                    } else if (UserDataStore.getInstance().getSpouseLineColor().equals(RelationshipLines.BLUE)) {
                        color = Color.BLUE;
                    } else if (UserDataStore.getInstance().getSpouseLineColor().equals(RelationshipLines.GREEN)) {
                        color = Color.GREEN;
                    }

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

                            Event earliestEvent = spouseEvents.get(0);

                            for (Event e : spouseEvents) {
                                if (e.getYear() < earliestEvent.getYear()) {
                                    earliestEvent = e;
                                }
                            }


                            Polyline line = googleMap.addPolyline(new PolylineOptions()
                                    .add(new LatLng(event.getLatitude(), event.getLongitude()),
                                            new LatLng(earliestEvent.getLatitude(), earliestEvent.getLongitude()))
                                    .width(STARTING_WIDTH)
                                    .color(color));
                            spouseLines.add(line);
                        }
                    }

                }

                if (UserDataStore.getInstance().isShowFamilyTreeLines()) {
                    clearFamilyTreeLines();

                    if (person != null) {
                        int color = 0;
                        if (UserDataStore.getInstance().getFamilyTreeLineColor().equals(RelationshipLines.RED)) {
                            color = Color.RED;
                        } else if (UserDataStore.getInstance().getFamilyTreeLineColor().equals(RelationshipLines.BLUE)) {
                            color = Color.BLUE;
                        } else if (UserDataStore.getInstance().getFamilyTreeLineColor().equals(RelationshipLines.GREEN)) {
                            color = Color.GREEN;
                        }

                        recursivelyDrawFamilyTreeLines(googleMap, person, event, STARTING_WIDTH, color);
                    }
                }

                return true;
            }
        };
    }

    private void drawLifeStoryLines(GoogleMap googleMap, Event event) {
        clearLifeStoryLines();

        int color = 0;
        if (UserDataStore.getInstance().getLifeStoryLineColor().equals(RelationshipLines.RED)) {
            color = Color.RED;
        } else if (UserDataStore.getInstance().getLifeStoryLineColor().equals(RelationshipLines.BLUE)) {
            color = Color.BLUE;
        } else if (UserDataStore.getInstance().getLifeStoryLineColor().equals(RelationshipLines.GREEN)) {
            color = Color.GREEN;
        }


        ArrayList<Event> events
                = UserDataStore.getInstance().getAllEventsForPerson(UserDataStore.getInstance().getPersonForEvent(event));

        Collections.sort(events, Event.SORT_BY_YEAR_AND_NAME);

        for (int i = 0; i < events.size() - 1; i++) {
            Event event0 = events.get(i);
            Event event1 = events.get(i+1);
            Polyline line = googleMap.addPolyline(new PolylineOptions()
                    .add(new LatLng(event0.getLatitude(), event0.getLongitude()),
                            new LatLng(event1.getLatitude(), event1.getLongitude()))
                    .width(STARTING_WIDTH)
                    .color(color));
            lifeStoryLines.add(line);
        }
    }

    private void recursivelyDrawFamilyTreeLines(GoogleMap googleMap, Person person, Event event, float width, int color) {
        Person mother = null;
        Person father = null;

        // TODO: Make helper class for this
        for (Person p : UserDataStore.getInstance().getAllPersons()) {
            if (p.getPersonID().equals(person.getMother())) {
                mother = p;
                break;
            }
        }

        // TODO: Make helper class for this
        for (Person p : UserDataStore.getInstance().getAllPersons()) {
            if (p.getPersonID().equals(person.getFather())) {
                father = p;
                break;
            }
        }

        if (mother != null) {
            ArrayList<Event> events = new ArrayList<>();

            for (Event e : UserDataStore.getInstance().getFilteredEvents()) {
                if (e.getPersonID().equals(mother.getPersonID())) {
                    events.add(e);
                }
            }

            Event earliestEvent = events.get(0);

            for (Event e : events) {
                if (e.getYear() < earliestEvent.getYear()) {
                    earliestEvent = e;
                }
            }

            Polyline line = googleMap.addPolyline(new PolylineOptions()
                    .add(new LatLng(event.getLatitude(), event.getLongitude()),
                            new LatLng(earliestEvent.getLatitude(), earliestEvent.getLongitude()))
                    .width(width)
                    .color(color));

            familyTreeLines.add(line);

            width -= WIDTH_DECREMENT;

            recursivelyDrawFamilyTreeLines(googleMap, mother, earliestEvent, width, color);
        }

        if (father != null) {
            ArrayList<Event> events = new ArrayList<>();

            for (Event e : UserDataStore.getInstance().getFilteredEvents()) {
                if (e.getPersonID().equals(father.getPersonID())) {
                    events.add(e);
                }
            }

            Event earliestEvent = events.get(0);

            for (Event e : events) {
                if (e.getYear() < earliestEvent.getYear()) {
                    earliestEvent = e;
                }
            }

            Polyline line = googleMap.addPolyline(new PolylineOptions()
                    .add(new LatLng(event.getLatitude(), event.getLongitude()),
                            new LatLng(earliestEvent.getLatitude(), earliestEvent.getLongitude()))
                    .width(width)
                    .color(color));

            familyTreeLines.add(line);

            width -= WIDTH_DECREMENT;

            recursivelyDrawFamilyTreeLines(googleMap, father, earliestEvent, width, color);
        }
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
