package placeme.ru.placemedemo.core.database;

import android.content.Context;
import android.widget.ArrayAdapter;
import android.widget.RatingBar;

import com.google.firebase.database.ChildEventListener;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;

import java.util.ArrayList;
import java.util.HashMap;

import placeme.ru.placemedemo.AuthData;
import placeme.ru.placemedemo.elements.Place;
import placeme.ru.placemedemo.elements.User;
import placeme.ru.placemedemo.core.utils.AuthorizationUtils;

/**
 * Created by Андрей on 21.12.2017.
 */

/**
 * Class that have all methods connected with working with the database
 */
public class DatabaseManager {

    private static FirebaseDatabase mBase;
    private static DatabaseReference mDatabaseReference;
    private static ChildEventListener childEventListener;

    static {
        mBase = FirebaseDatabase.getInstance();
    }

    /**
     * Method that check existence of the user in the database
     * @param context current context
     * @param email user email
     * @param password user password
     */
    public static void findUserAndCheckPassword(final Context context, final String email, final String password) {
        mDatabaseReference = getDatabaseChild("authdata");

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
        mDatabaseReference = getDatabaseChild("maxid");

        mDatabaseReference.addListenerForSingleValueEvent(new AbstractValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                Integer id = dataSnapshot.getValue(Integer.class);
                AuthData newAuthData = new AuthData(id, information[0], information[1]);
                User newUser = new User(id, information[2], information[3], information[4]);

                getDatabaseChild("users").child(id.toString()).setValue(newUser);
                getDatabaseChild("authdata").child(id.toString()).setValue(newAuthData);
                mDatabaseReference.setValue(id + 1);
            }
        });
    }

    /**
     * Method that searches places in database within query string
     * @param arrayAdapter adapter with names of founded places
     * @param places array with founded places
     * @param toFind string which contains user query to search
     */
    public static void findPlacesByString(final ArrayAdapter<String> arrayAdapter, final ArrayList<Place> places, final String toFind) {
        mDatabaseReference = getDatabaseChild("places");

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

    /**
     * Method that updates place rating after user voting
     * and immediately updates rating bar in UI
     * @param ratingBar rating bar to update
     * @param placeId place that user have rated
     */
    public static void updatePlaceRating(final RatingBar ratingBar, final String placeId) {
        mDatabaseReference = getDatabaseChild("places").child(placeId);

        mDatabaseReference.addListenerForSingleValueEvent(new AbstractValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                HashMap<String, Object> currentPlace = (HashMap<String, Object>) dataSnapshot.getValue();
                float mark = ratingBar.getRating();
                float curSum = Float.parseFloat(currentPlace.get("sumOfMarks").toString());
                long curNumberOfMarks = Long.parseLong(currentPlace.get("numberOfRatings").toString());

                curSum += mark;
                curNumberOfMarks++;

                DatabaseReference mDatabaseReferenceSet = mDatabaseReference.child("sumOfMarks");
                mDatabaseReferenceSet.setValue(curSum);

                mDatabaseReferenceSet = mDatabaseReference.child("numberOfRatings");
                mDatabaseReferenceSet.setValue(curNumberOfMarks);
            }
        });
    }

    /**
     * Method that adds place to the list of favourite places of the user
     * place will not be added if it is already in the favourite list
     * @param userId id of the user who wants to add place to favourite
     * @param placeId id of place that should be added
     */
    public static void addPlaceToFavourite(final String userId, final String placeId) {
        mDatabaseReference = getDatabaseChild("users").child(userId).child("favouritePlaces");
        mDatabaseReference.addListenerForSingleValueEvent(new AbstractValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                String places = dataSnapshot.getValue().toString();
                boolean wasAlreadyAddedToFavourite = false;
                for(String str : places.split(",")) {
                    if(str.equals(placeId)) {
                        wasAlreadyAddedToFavourite = true;
                    }
                }

                if(!wasAlreadyAddedToFavourite) {
                    String newFavouritePlacesList = places + "," + placeId;
                    mDatabaseReference.setValue(newFavouritePlacesList);

                }
            }
        });
    }

    private static DatabaseReference getDatabaseChild(String childName) {
        DatabaseReference databaseReference = getDatabaseReference();
        if(databaseReference != null) {
            return databaseReference.child(childName);
        }

        return null;
    }

    private static DatabaseReference getDatabaseReference() {
        if(mBase != null) {
            return mBase.getReference();
        }

        return null;
    }
}
