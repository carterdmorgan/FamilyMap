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

    public static final String ARG_ZOOM_EVENT = "zoom_event";

//    private ArrayList<Person> mothersSide;
//    private ArrayList<Person> fathersSide;
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

        if (this.getArguments() != null)
            zoomEvent = (Event) this.getArguments().get(ARG_ZOOM_EVENT);

        Person userPerson = new Person();
        Person mother = new Person();
        Person father = new Person();

        Log.d(TAG, "onCreate: current person result: " + UserDataStore.getInstance().toString());

        for (Person person : UserDataStore.getInstance().getCurrentPersonResult().getData()) {
            if (person.getPersonID().equals(UserDataStore.getInstance().getUserResult().getPersonID())) {
                userPerson = person;
                break;
            }
        }

        for (Person person : UserDataStore.getInstance().getCurrentPersonResult().getData()) {
            if (person.getPersonID().equals(userPerson.getMother())) {
                mother = person;
            }
            if (person.getPersonID().equals(userPerson.getFather())) {
                father = person;
            }
        }

//        mothersSide = compileAncestors(mother, new ArrayList<Person>());
//        fathersSide = compileAncestors(father, new ArrayList<Person>());
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
        ivGenderIcon = view.findViewById(R.id.gender_icon_image_view);
        llEventInfo = view.findViewById(R.id.event_info_linear_layout);

        llEventInfo.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (!TextUtils.isEmpty(tvEventPerson.getText().toString())) {
                    Intent intent = new Intent(getContext(), PersonActivity.class);
                    intent.putExtra("person", currentPerson);
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
//            boolean placeMarker = isMarkerPlaced(event);

//            if (placeMarker) {
                Float color = UserDataStore.getInstance().getMarkerColors().get(event.getEventType().toLowerCase());
                LatLng location = new LatLng(event.getLatitude(), event.getLongitude());
                MarkerOptions markerOptions = new MarkerOptions().position(location).icon(BitmapDescriptorFactory.defaultMarker(color));
                Marker marker = googleMap.addMarker(markerOptions);
                markers.add(marker);
                markerEventMap.put(marker, event);
//            }
        }

        if (UserDataStore.getInstance().isShowLifeStoryLines())
            drawLifeStoryLines(googleMap);

        if (zoomEvent != null) {
            Event event = zoomEvent;

            Person person = null;
            Person spouse = null;

            for (Person p : UserDataStore.getInstance().getCurrentPersonResult().getData()) {
                if (p.getPersonID().equals(event.getPersonID())) {
                    tvEventPerson.setText(p.getFirstName() + " " + p.getLastName());
                    currentPerson = p;
                    person = p;
                }
            }

            tvEventType.setText(event.getEventType());
            tvEventLocation.setText(event.getCity() + ", " + event.getCountry());
            tvEventDate.setText(Integer.toString(event.getYear()));

            if (currentPerson.getGender().equals("f")) {
                ivGenderIcon.setImageDrawable(getContext().getDrawable(R.drawable.ic_person_pink_100_24dp));
            } else {
                ivGenderIcon.setImageDrawable(getContext().getDrawable(R.drawable.ic_person_blue_400_24dp));
            }

            if (UserDataStore.getInstance().isShowSpouseLines()) {
                Log.d(TAG, "onMarkerClick: showing lines");
                int color = 0;
                if (UserDataStore.getInstance().getSpouseLineColor().equals(RelationshipLines.RED)) {
                    color = Color.RED;
                } else if (UserDataStore.getInstance().getSpouseLineColor().equals(RelationshipLines.BLUE)) {
                    color = Color.BLUE;
                } else if (UserDataStore.getInstance().getSpouseLineColor().equals(RelationshipLines.GREEN)) {
                    color = Color.GREEN;
                }

                Log.d(TAG, "onMarkerClick: person: " + person.toString());
                if (person != null && person.getSpouse() != null) {
                    Log.d(TAG, "onMarkerClick: second layer");
                    for (Person p : UserDataStore.getInstance().getCurrentPersonResult().getData()) {
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
                                .width(15)
                                .color(color));
                    }
                }

            }

            if (UserDataStore.getInstance().isShowFamilyTreeLines()) {
                if (person != null) {
                    int color = 0;
                    if (UserDataStore.getInstance().getFamilyTreeLineColor().equals(RelationshipLines.RED)) {
                        color = Color.RED;
                    } else if (UserDataStore.getInstance().getFamilyTreeLineColor().equals(RelationshipLines.BLUE)) {
                        color = Color.BLUE;
                    } else if (UserDataStore.getInstance().getFamilyTreeLineColor().equals(RelationshipLines.GREEN)) {
                        color = Color.GREEN;
                    }

                    recursivelyDrawFamilyTreeLines(googleMap, person, event, 15.0f, color);
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

                for (Person p : UserDataStore.getInstance().getCurrentPersonResult().getData()) {
                    if (p.getPersonID().equals(event.getPersonID())) {
                        tvEventPerson.setText(p.getFirstName() + " " + p.getLastName());
                        currentPerson = p;
                        person = p;
                    }
                }

                tvEventType.setText(event.getEventType());
                tvEventLocation.setText(event.getCity() + ", " + event.getCountry());
                tvEventDate.setText(Integer.toString(event.getYear()));

                if (currentPerson.getGender().equals("f")) {
                    ivGenderIcon.setImageDrawable(getContext().getDrawable(R.drawable.ic_person_pink_100_24dp));
                } else {
                    ivGenderIcon.setImageDrawable(getContext().getDrawable(R.drawable.ic_person_blue_400_24dp));
                }

                if (UserDataStore.getInstance().isShowSpouseLines()) {
                    Log.d(TAG, "onMarkerClick: showing lines");
                    int color = 0;
                    if (UserDataStore.getInstance().getSpouseLineColor().equals(RelationshipLines.RED)) {
                        color = Color.RED;
                    } else if (UserDataStore.getInstance().getSpouseLineColor().equals(RelationshipLines.BLUE)) {
                        color = Color.BLUE;
                    } else if (UserDataStore.getInstance().getSpouseLineColor().equals(RelationshipLines.GREEN)) {
                        color = Color.GREEN;
                    }

                    Log.d(TAG, "onMarkerClick: person: " + person.toString());
                    if (person != null && person.getSpouse() != null) {
                        Log.d(TAG, "onMarkerClick: second layer");
                        for (Person p : UserDataStore.getInstance().getCurrentPersonResult().getData()) {
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
                                    .width(15)
                                    .color(color));
                        }
                    }

                }

                if (UserDataStore.getInstance().isShowFamilyTreeLines()) {
                    Log.d(TAG, "onMarkerClick: show family lines triggered");
                    if (person != null) {
                        int color = 0;
                        if (UserDataStore.getInstance().getFamilyTreeLineColor().equals(RelationshipLines.RED)) {
                            color = Color.RED;
                        } else if (UserDataStore.getInstance().getFamilyTreeLineColor().equals(RelationshipLines.BLUE)) {
                            color = Color.BLUE;
                        } else if (UserDataStore.getInstance().getFamilyTreeLineColor().equals(RelationshipLines.GREEN)) {
                            color = Color.GREEN;
                        }

                        recursivelyDrawFamilyTreeLines(googleMap, person, event, 15.0f, color);
                    }
                }

                return true;
            }
        };
    }

    private void drawLifeStoryLines(GoogleMap googleMap) {
        int color = 0;
        if (UserDataStore.getInstance().getLifeStoryLineColor().equals(RelationshipLines.RED)) {
            color = Color.RED;
        } else if (UserDataStore.getInstance().getLifeStoryLineColor().equals(RelationshipLines.BLUE)) {
            color = Color.BLUE;
        } else if (UserDataStore.getInstance().getLifeStoryLineColor().equals(RelationshipLines.GREEN)) {
            color = Color.GREEN;
        }

        Map<Person, ArrayList<Event>> lifeStories = new HashMap<>();
        ArrayList<Event> allEvents = new ArrayList<>();

        for (Marker marker : markers) {
            allEvents.add(markerEventMap.get(marker));
        }

        // Populate life stories
        for (Event event : allEvents) {
            for (Person person : UserDataStore.getInstance().getCurrentPersonResult().getData()) {
                if (person.getPersonID().equals(event.getPersonID())) {
                    if (lifeStories.get(person) == null) {
                        ArrayList<Event> currentEvent = new ArrayList<>();
                        currentEvent.add(event);
                        lifeStories.put(person, currentEvent);
                    } else {
                        lifeStories.get(person).add(event);
                    }
                    break;
                }
            }
        }

        Iterator it = lifeStories.entrySet().iterator();
        while (it.hasNext()) {
            Map.Entry pair = (Map.Entry) it.next();
            Person person = (Person) pair.getKey();
            ArrayList<Event> events = (ArrayList<Event>) pair.getValue();
            it.remove(); // avoids a ConcurrentModificationException

            Collections.sort(events, new Comparator<Event>() {
                @Override
                public int compare(Event o1, Event o2) {
                    Integer year1 = new Integer(o1.getYear());
                    Integer year2 = new Integer(o2.getYear());
                    return year1.compareTo(year2);
                }
            });

            for (int i = 0; i < events.size() - 1; i++) {
                Log.d(TAG, "drawLifeStoryLines: drawing line");
                Event event0 = events.get(i);
                Event event1 = events.get(i+1);
                Polyline line = googleMap.addPolyline(new PolylineOptions()
                        .add(new LatLng(event0.getLatitude(), event0.getLongitude()),
                                new LatLng(event1.getLatitude(), event1.getLongitude()))
                        .width(18)
                        .color(color));
            }
        }
    }

    private void recursivelyDrawFamilyTreeLines(GoogleMap googleMap, Person person, Event event, float width, int color) {
        Log.d(TAG, "recursivelyDrawFamilyTreeLines: begins");
        Person mother = null;
        Person father = null;

        Log.d(TAG, "recursivelyDrawFamilyTreeLines: person: " + person.toString());

        for (Person p : UserDataStore.getInstance().getCurrentPersonResult().getData()) {
            if (p.getPersonID().equals(person.getMother())) {
                Log.d(TAG, "recursivelyDrawFamilyTreeLines: found mother");
                mother = p;
                break;
            }
        }

        for (Person p : UserDataStore.getInstance().getCurrentPersonResult().getData()) {
            if (p.getPersonID().equals(person.getFather())) {
                Log.d(TAG, "recursivelyDrawFamilyTreeLines: found father");
                father = p;
                break;
            }
        }

        if (mother != null) {
            ArrayList<Event> events = new ArrayList<>();

            for (Event e : UserDataStore.getInstance().getCurrentEventResult().getData()) {
                Log.d(TAG, "recursivelyDrawFamilyTreeLines: first loop");
                if (e.getPersonID().equals(mother.getPersonID())) {
                    Log.d(TAG, "recursivelyDrawFamilyTreeLines: add mom event");
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

            width -= 3.0f;

            recursivelyDrawFamilyTreeLines(googleMap, mother, earliestEvent, width, color);
        }

        if (father != null) {
            ArrayList<Event> events = new ArrayList<>();

            for (Event e : UserDataStore.getInstance().getCurrentEventResult().getData()) {
                Log.d(TAG, "recursivelyDrawFamilyTreeLines: second loop");
                if (e.getPersonID().equals(father.getPersonID())) {
                    Log.d(TAG, "recursivelyDrawFamilyTreeLines: add dad events");
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

            width -= 3.0f;

            recursivelyDrawFamilyTreeLines(googleMap, father, earliestEvent, width, color);
        }
    }

//    private boolean isMarkerPlaced(Event event) {
//        boolean placeMarker = false;
//
//        String eventType = event.getEventType().toLowerCase();
//
//
//        if (UserDataStore.getInstance().getFilterPreferences().get(eventType)) {
//            placeMarker = true;
//        }
//        if (isMaleEvent(event) && !UserDataStore.getInstance().isShowMale()) {
//            placeMarker = false;
//        }
//        if (!isMaleEvent(event) && !UserDataStore.getInstance().isShowFemale()) {
//            placeMarker = false;
//        }
//        if (personDoesExist(getPersonFromEvent(event), mothersSide) && !UserDataStore.getInstance().isShowMother()) {
//            placeMarker = false;
//        }
//
//        if (personDoesExist(getPersonFromEvent(event), fathersSide) && !UserDataStore.getInstance().isShowFather()) {
//            placeMarker = false;
//        }
//
//
//        return placeMarker;
//    }

//    private ArrayList<Person> compileAncestors(Person descendant, ArrayList<Person> ancestors) {
//        ancestors.add(descendant);
//
//        for (Person person : UserDataStore.getInstance().getCurrentPersonResult().getData()) {
//            if (person.getPersonID().equals(descendant.getFather())
//                    || person.getPersonID().equals(descendant.getMother())) {
//                ancestors = compileAncestors(person, ancestors);
//            }
//        }
//
//        return ancestors;
//    }

//    private boolean personDoesExist(Person person, ArrayList<Person> side) {
//        for (Person p : side) {
//            if (p.equals(person)) {
//                return true;
//
//            }
//        }
//
//        return false;
//    }
//
//    private Person getPersonFromEvent(Event event) {
//        for (Person person : UserDataStore.getInstance().getCurrentPersonResult().getData()) {
//            if (person.getPersonID().equals(event.getPersonID())) {
//                return person;
//            }
//        }
//
//        return null;
//    }
//
//    private boolean isMaleEvent(Event event) {
//        for (Person person : UserDataStore.getInstance().getCurrentPersonResult().getData()) {
//            if (person.getPersonID().equals(event.getPersonID()) && person.getGender().equals("m")) {
//                return true;
//            } else if (person.getPersonID().equals(event.getPersonID()) && person.getGender().equals("f")) {
//                return false;
//            }
//        }
//
//        return false;
//    }
}
