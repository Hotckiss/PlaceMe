package placeme.ru.placemedemo;

import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.MarkerOptions;
import com.google.firebase.database.ChildEventListener;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;

/**
 * Created by Андрей on 21.11.2017.
 */

public class MapUtilities {

    public static void addAllMarkers(final GoogleMap googleMap) {
        FirebaseDatabase mBase;
        DatabaseReference mDatabaseReference;
        ChildEventListener childEventListener;
        mBase = FirebaseDatabase.getInstance();
        mDatabaseReference = mBase.getReference().child("places");
        childEventListener = new ChildEventListener() {

            @Override
            public void onChildAdded(DataSnapshot dataSnapshot, String s) {
                Place place = (Place) dataSnapshot.getValue(Place.class);
                //GoogleMap googleMap =
                googleMap.addMarker(new MarkerOptions()
                        .position(new LatLng(place.getLatitude(), place.getLongitude()))
                        .title(place.getName()));
            }

            @Override public void onChildChanged(DataSnapshot dataSnapshot, String s) {}
            @Override public void onChildRemoved(DataSnapshot dataSnapshot) {}
            @Override public void onChildMoved(DataSnapshot dataSnapshot, String s) {}
            @Override public void onCancelled(DatabaseError databaseError) {}
        };
        mDatabaseReference.addChildEventListener(childEventListener);
    }

    public static void refreshMarkers(final GoogleMap googleMap) {
        if(googleMap != null) {
            googleMap.clear();
        }
        DatabaseReference mDatabaseReference = FirebaseDatabase.getInstance().getReference().child("places");
        ChildEventListener childEventListener1 = new ChildEventListener() {

            @Override
            public void onChildAdded(DataSnapshot dataSnapshot, String s) {
                Place place = (Place) dataSnapshot.getValue(Place.class);
                GoogleMap googleMap1 = googleMap;
                googleMap1.addMarker(new MarkerOptions()
                        .position(new LatLng(place.getLatitude(), place.getLongitude()))
                        .title(place.getName()));

            }
            @Override public void onChildChanged(DataSnapshot dataSnapshot, String s) {}
            @Override public void onChildRemoved(DataSnapshot dataSnapshot) {}
            @Override public void onChildMoved(DataSnapshot dataSnapshot, String s) {}
            @Override public void onCancelled(DatabaseError databaseError) {}
        };
        mDatabaseReference.addChildEventListener(childEventListener1);
    }

    public static void addFindedMarkers(final GoogleMap googleMap, final String toFind) {
        FirebaseDatabase mBase;
        DatabaseReference mDatabaseReference;
        mBase = FirebaseDatabase.getInstance();
        mDatabaseReference = mBase.getReference().child("places");
        ChildEventListener childEventListener = new ChildEventListener() {

            @Override
            public void onChildAdded(DataSnapshot dataSnapshot, String s) {
                Place place = (Place) dataSnapshot.getValue(Place.class);
                //Log.d("MRK", "CLICK11111");
                if (place.getName().indexOf(toFind) != -1) {
                    googleMap.addMarker(new MarkerOptions()
                            .position(new LatLng(place.getLatitude(), place.getLongitude()))
                            .title(place.getName()));

                } else {
                    for (String tag : place.getTags().split(",")) {
                        if (toFind.equals(tag)) {
                            googleMap.addMarker(new MarkerOptions()
                                    .position(new LatLng(place.getLatitude(), place.getLongitude()))
                                    .title(place.getName()));
                            break;
                        }
                    }
                }
            }

            @Override public void onChildChanged(DataSnapshot dataSnapshot, String s) {}
            @Override public void onChildRemoved(DataSnapshot dataSnapshot) {}
            @Override public void onChildMoved(DataSnapshot dataSnapshot, String s) {}
            @Override public void onCancelled(DatabaseError databaseError) {}
        };
        mDatabaseReference.addChildEventListener(childEventListener);
    }
}
