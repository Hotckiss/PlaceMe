package placeme.ru.placemedemo.core.database;

import android.support.test.InstrumentationRegistry;
import android.support.test.runner.AndroidJUnit4;
import android.widget.RatingBar;

import com.google.android.gms.maps.model.LatLng;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;

import org.junit.Test;
import org.junit.runner.RunWith;

import placeme.ru.placemedemo.elements.Place;

import static org.junit.Assert.*;
import static placeme.ru.placemedemo.core.database.DatabaseManagerPlaces.getFavouritePlaceReference;
import static placeme.ru.placemedemo.core.database.DatabaseManagerPlaces.saveCreatedPlace;
import static placeme.ru.placemedemo.core.database.DatabaseManagerPlaces.updatePlaceRating;

/**
 * Test database places part
 * Created by Андрей on 06.02.2018.
 */
@RunWith(AndroidJUnit4.class)
public class DatabaseManagerPlacesTest {
    @Test
    public void testGetFavouritePlaceReference() throws Exception{
        StorageReference actual = getFavouritePlaceReference("121");
        StorageReference expected = FirebaseStorage.getInstance().getReference().child("photos/121place_photo");

        assertEquals(expected, actual);
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