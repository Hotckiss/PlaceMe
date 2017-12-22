package placeme.ru.placemedemo.core.database;

import android.content.Context;
import android.view.View;
import android.widget.ArrayAdapter;
import android.widget.RatingBar;

import com.google.firebase.database.ChildEventListener;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.util.ArrayList;
import java.util.HashMap;

import placeme.ru.placemedemo.AuthData;
import placeme.ru.placemedemo.Place;
import placeme.ru.placemedemo.R;
import placeme.ru.placemedemo.User;
import placeme.ru.placemedemo.core.utils.AuthorizationUtils;

/**
 * Created by Андрей on 21.12.2017.
 */

public class DatabaseManager {

    private static FirebaseDatabase mBase;
    private static DatabaseReference mDatabaseReference;
    private static ChildEventListener childEventListener;

    public static void findUserAndCheckPassword(final Context context, final String email, final String password) {
        mBase = FirebaseDatabase.getInstance();
        mDatabaseReference = mBase.getReference().child("authdata");

        AuthorizationUtils.setLoggedIn(context, -1);
        childEventListener = new AbstractChildEventListener() {
            @Override
            public void onChildAdded(DataSnapshot dataSnapshot, String s) {
                AuthData authData = dataSnapshot.getValue(AuthData.class);
                if(authData.getLogin().equals(email)) {
                    if(authData.getPassword().equals(password)) {
                        AuthorizationUtils.setLoggedIn(context, authData.getId());
                    }
                }
            }
        };
        mDatabaseReference.addChildEventListener(childEventListener);
    }

    /**
     * Method that register new user in database with unique id
     * @param context current context
     * @param information information that user input during registration
     */
    public static void registerUser(final Context context, final String[] information) {
        mBase = FirebaseDatabase.getInstance();
        mDatabaseReference = mBase.getReference().child("maxid");

        mDatabaseReference.addListenerForSingleValueEvent(new AbstractValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                Integer id = dataSnapshot.getValue(Integer.class);
                AuthData newAuthData = new AuthData(id, information[0], information[1]);
                User newUser = new User(id, information[2], information[3], information[4]);

                mBase.getReference().child("users").child(id.toString()).setValue(newUser);
                mBase.getReference().child("authdata").child(id.toString()).setValue(newAuthData);
                mDatabaseReference.setValue(id + 1);
            }
        });
    }

    public static void findPlacesByString(final ArrayAdapter<String> arrayAdapter, final ArrayList<Place> places, final String toFind) {
        mBase = FirebaseDatabase.getInstance();
        mDatabaseReference = mBase.getReference().child("places");

        childEventListener = new AbstractChildEventListener() {
            @Override
            public void onChildAdded(DataSnapshot dataSnapshot, String s) {
                Place place = dataSnapshot.getValue(Place.class);
                if (place.getName().toLowerCase().contains(toFind.toLowerCase())) {
                    arrayAdapter.add(place.getName());
                    places.add(place);

                } else {
                    for (String tag : place.getTags().split(",")) {
                        if (toFind.toLowerCase().equals(tag.toLowerCase())) {
                            arrayAdapter.add(place.getName());
                            places.add(place);
                            break;
                        }
                    }
                }
            }
        };
        mDatabaseReference.addChildEventListener(childEventListener);
    }

    public static void updatePlaceRating(final RatingBar ratingBar, final String placeId) {
        mBase = FirebaseDatabase.getInstance();
        mDatabaseReference = mBase.getReference().child("places").child(placeId);
        mDatabaseReference.addListenerForSingleValueEvent(new AbstractValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                HashMap<String, Object> currentPlace = (HashMap<String, Object>) dataSnapshot.getValue();
                Float mark = ratingBar.getRating();
                Float curSum = Float.parseFloat(currentPlace.get("sumOfMarks").toString());
                Long curNumOfMarks = Long.parseLong(currentPlace.get("numberOfRatings").toString());

                curSum += mark;
                curNumOfMarks++;

                DatabaseReference mDatabaseReferenceSet = mBase.getReference().child("places").child(placeId).child("sumOfMarks");
                mDatabaseReferenceSet.setValue(curSum);

                mDatabaseReferenceSet = mBase.getReference().child("places").child(placeId).child("numberOfRatings");
                mDatabaseReferenceSet.setValue(curNumOfMarks);
            }
        });
    }
}
