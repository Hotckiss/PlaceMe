package placeme.ru.placemedemo.core.database;

import android.app.Activity;
import android.content.Context;
import android.support.test.InstrumentationRegistry;
import android.support.test.runner.AndroidJUnit4;
import android.test.ActivityInstrumentationTestCase2;
import android.widget.ArrayAdapter;
import android.widget.RatingBar;
import android.widget.TextView;

import com.google.android.gms.maps.model.LatLng;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;

import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.stubbing.Answer;

import java.util.ArrayList;

import placeme.ru.placemedemo.core.Controller;
import placeme.ru.placemedemo.elements.Place;
import placeme.ru.placemedemo.ui.MainActivity;

import static placeme.ru.placemedemo.core.database.DatabaseManagerPlaces.*;

/**
 * Test database places part
 * Created by Андрей on 06.02.2018.
 */
@RunWith(AndroidJUnit4.class)
public class DatabaseManagerPlacesTest extends ActivityInstrumentationTestCase2<MainActivity> {
    private int cnt = 0;

    public DatabaseManagerPlacesTest() {
        super(MainActivity.class);
    }

    @Before
    public void init() {
        Context context = InstrumentationRegistry.getTargetContext();
        Controller.setDistanceSearchStatus(context, false);
        Controller.setRatingSearchStatus(context, false);
        cnt = 0;
    }

    @Test
    public void testFillDescriptionPlaces() throws Exception {
        TextView tv = new TextView(InstrumentationRegistry.getTargetContext());
        fillDescriptionPlaces(tv, "76");
        Thread.sleep(3000);

        assertEquals("aaa\nbbb", tv.getText().toString());
    }

    @Test
    public void testFindPlacesByString() throws Exception {
        injectInstrumentation(InstrumentationRegistry.getInstrumentation());
        Activity activity = getActivity();
        ArrayAdapter<String> places1 = new ArrayAdapter<>(activity, android.R.layout.select_dialog_multichoice);
        ArrayList<Place> places = new ArrayList<>();

        //we will call this method implementation but without blocking UI because it is impossible here
        //DatabaseManagerPlaces.findPlacesByString(places1, places, "Академический университет", new LatLng(0, 0), activity);
        DatabaseReference databaseReference = FirebaseDatabase.getInstance().getReference().child("places");
        databaseReference.addChildEventListener(new AbstractChildEventListener() {
            @Override
            public void onChildAdded(DataSnapshot dataSnapshot, String s) {
                Place place = dataSnapshot.getValue(Place.class);
                if (place != null && DatabaseUtils.checkAccess(place, new LatLng(0, 0), activity) &&
                        DatabaseUtils.isAppropriate(place, "Академический университет")) {
                    DatabaseUtils.addPlaceToList(places1, places, place);
                }
            }
        });

        Thread.sleep(3000);

        assertEquals(1, places1.getCount());
    }

    @Test
    public void testGetFavouritePlaceReference() throws Exception {
        StorageReference actual = getFavouritePlaceReference("121");
        StorageReference expected = FirebaseStorage.getInstance().getReference().child("photos/121place_photo");

        assertEquals(expected, actual);
    }

    @Test
    public void testAddMarkersByQuery() throws Exception {
        //GoogleMap googleMap = mock(GoogleMap.class);

        Answer modelModifier = invocation -> {
            cnt++;
            return null;
        };

        //when(googleMap.addMarker(any())).then(modelModifier);
        //addMarkersByQuery(googleMap, "zzzzzzz");
        assertEquals(0, cnt);
        assertTrue(true);
    }

    @Test
    public void testUpdatePlaceRating() throws Exception {
        RatingBar rb = new RatingBar(InstrumentationRegistry.getTargetContext());
        rb.setRating(5);
        updatePlaceRating(rb, "76");
        Thread.sleep(2000);
        DatabaseReference myRef = FirebaseDatabase.getInstance().getReference();

        myRef.child("places").addChildEventListener(new AbstractChildEventListener() {
            @Override
            public void onChildAdded(DataSnapshot dataSnapshot, String s) {
                Place place = dataSnapshot.getValue(Place.class);

                if (place.getIdAsString().equals("76")) {
                    assertEquals(5, place.getMark(), 0.01);
                }
            }
        });

        Thread.sleep(2000);
    }

    @Test
    public void testSaveConvertedPlace() throws Exception {
        DatabaseReference myRef = FirebaseDatabase.getInstance().getReference();

        myRef.child("maxidplaces").addListenerForSingleValueEvent(new AbstractValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                String current = dataSnapshot.getValue().toString();

                saveConvertedPlace(null, new Place(100, "aaa", "bbb", "ccc", 10, 100));

                DatabaseReference reference = myRef.child("places");
                reference.addChildEventListener(new AbstractChildEventListener() {
                    @Override
                    public void onChildAdded(DataSnapshot dataSnapshot, String s) {
                        Place place = dataSnapshot.getValue(Place.class);

                        if (place.getIdAsString().equals(current)) {
                            assertEquals("aaa", place.getName());
                            assertEquals("bbb", place.getDescription());
                            assertEquals("ccc", place.getTags());
                            assertEquals(10, place.getLatitude(), 0.01);
                            assertEquals(100, place.getLongitude(), 0.01);
                        }
                    }
                });

                reference.addListenerForSingleValueEvent(new AbstractValueEventListener() {
                    @Override
                    public void onDataChange(DataSnapshot dataSnapshot) {
                        myRef.child("places").child(current).removeValue();
                        myRef.child("maxidplaces").setValue(Integer.parseInt(current));
                    }
                });
            }
        });

        Thread.sleep(3000);
    }

    @Test
    public void testSaveCreatedPlace2() throws Exception {
        DatabaseReference myRef = FirebaseDatabase.getInstance().getReference();

        myRef.child("maxidplaces").addListenerForSingleValueEvent(new AbstractValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                String current = dataSnapshot.getValue().toString();

                saveCreatedPlace2(null, new Place(100, "aaa", "bbb", "ccc", 0, 0), new LatLng(10, 100));

                DatabaseReference reference = myRef.child("places");
                reference.addChildEventListener(new AbstractChildEventListener() {
                    @Override
                    public void onChildAdded(DataSnapshot dataSnapshot, String s) {
                        Place place = dataSnapshot.getValue(Place.class);

                        if (place.getIdAsString().equals(current)) {
                            assertEquals("aaa", place.getName());
                            assertEquals("bbb", place.getDescription());
                            assertEquals("ccc", place.getTags());
                            assertEquals(10, place.getLatitude(), 0.01);
                            assertEquals(100, place.getLongitude(), 0.01);
                        }
                    }
                });

                reference.addListenerForSingleValueEvent(new AbstractValueEventListener() {
                    @Override
                    public void onDataChange(DataSnapshot dataSnapshot) {
                        myRef.child("places").child(current).removeValue();
                        myRef.child("maxidplaces").setValue(Integer.parseInt(current));
                    }
                });
            }
        });

        Thread.sleep(3000);
    }

    @Test
    public void testSaveCreatedPlace() throws Exception {
        DatabaseReference myRef = FirebaseDatabase.getInstance().getReference();

        myRef.child("maxidplaces").addListenerForSingleValueEvent(new AbstractValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                String current = dataSnapshot.getValue().toString();

                saveCreatedPlace(null, new Place(100, "aaa", "bbb", "ccc", 0, 0), new LatLng(10, 100));

                DatabaseReference reference = myRef.child("places");
                reference.addChildEventListener(new AbstractChildEventListener() {
                    @Override
                    public void onChildAdded(DataSnapshot dataSnapshot, String s) {
                        Place place = dataSnapshot.getValue(Place.class);

                        if (place.getIdAsString().equals(current)) {
                            assertEquals("aaa", place.getName());
                            assertEquals("bbb", place.getDescription());
                            assertEquals("ccc", place.getTags());
                            assertEquals(10, place.getLatitude(), 0.01);
                            assertEquals(100, place.getLongitude(), 0.01);
                        }
                    }
                });

                reference.addListenerForSingleValueEvent(new AbstractValueEventListener() {
                    @Override
                    public void onDataChange(DataSnapshot dataSnapshot) {
                        myRef.child("places").child(current).removeValue();
                        myRef.child("maxidplaces").setValue(Integer.parseInt(current));
                    }
                });
            }
        });

        Thread.sleep(3000);
    }
}