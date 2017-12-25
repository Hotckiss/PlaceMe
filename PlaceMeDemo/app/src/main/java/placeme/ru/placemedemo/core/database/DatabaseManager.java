package placeme.ru.placemedemo.core.database;

import android.content.Context;
import android.net.Uri;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.widget.ArrayAdapter;
import android.widget.EditText;
import android.widget.RatingBar;
import android.widget.TextView;

import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.Marker;
import com.google.android.gms.maps.model.MarkerOptions;
import com.google.firebase.database.ChildEventListener;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;
import com.squareup.picasso.Picasso;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;

import de.hdodenhof.circleimageview.CircleImageView;
import placeme.ru.placemedemo.core.utils.FavouritePlacesUtils;
import placeme.ru.placemedemo.core.utils.RoutesUtils;
import placeme.ru.placemedemo.ui.FavouritePlacesActivity;
import placeme.ru.placemedemo.ui.dialogs.AlertDialogCreator;
import placeme.ru.placemedemo.ui.views.HorizontalListViewFragment;
import placeme.ru.placemedemo.R;
import placeme.ru.placemedemo.core.utils.AuthorizationUtils;
import placeme.ru.placemedemo.core.utils.FriendsDataUtils;
import placeme.ru.placemedemo.elements.*;
import placeme.ru.placemedemo.ui.views.RoutesListViewFragment;
import util.Log;

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

    private static StorageReference mStorageRef;

    static {
        mBase = FirebaseDatabase.getInstance();
        mStorageRef = FirebaseStorage.getInstance().getReference();
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

    /**
     * Method that loads all markers from database to the map
     * @param googleMap markers destination map
     */
    public static void loadMarkersToMap(final GoogleMap googleMap) {
        mDatabaseReference = getDatabaseChild("places");
        mDatabaseReference.addChildEventListener(new AbstractChildEventListener() {
            @Override
            public void onChildAdded(DataSnapshot dataSnapshot, String s) {
                Place place = dataSnapshot.getValue(Place.class);
                googleMap.addMarker(new MarkerOptions()
                         .position(new LatLng(place.getLatitude(), place.getLongitude()))
                         .title(place.getName()));
            }

        });
    }

    /**
     * Method that loads markers founded by query from database to the map
     * @param googleMap markers destination map
     * @param query user search query
     */
    public static void addMarkersByQuery(final GoogleMap googleMap, final String query) {
        mDatabaseReference = getDatabaseChild("places");
        mDatabaseReference.addChildEventListener(new AbstractChildEventListener() {
            @Override
            public void onChildAdded(DataSnapshot dataSnapshot, String s) {
                Place place = dataSnapshot.getValue(Place.class);
                if (place.getName().toLowerCase().contains(query.toLowerCase())) {
                    googleMap.addMarker(new MarkerOptions()
                             .position(new LatLng(place.getLatitude(), place.getLongitude()))
                             .title(place.getName()));
                } else {
                    for (String tag : place.getTags().split(",")) {
                        if (query.toLowerCase().equals(tag.toLowerCase())) {
                            googleMap.addMarker(new MarkerOptions()
                                     .position(new LatLng(place.getLatitude(), place.getLongitude()))
                                     .title(place.getName()));
                            break;
                        }
                    }
                }
            }
        });
    }

    /**
     * Method that loads user information from the database to the edit fields
     * @param toLoad array of fields to init
     * @param userId user id to search in database
     */
    public static void loadUserDataForEdit(final EditText[] toLoad, String userId) {
        mDatabaseReference = getDatabaseChild("users").child(userId);

        mDatabaseReference.addListenerForSingleValueEvent(new AbstractValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                HashMap<String, String> currentUser = (HashMap<String, String>) dataSnapshot.getValue();

                toLoad[2].setText(currentUser.get("name"));
                toLoad[3].setText(currentUser.get("surname"));
                toLoad[4].setText(currentUser.get("nickname"));
            }
        });

        DatabaseReference mDatabaseReferenceAuth = getDatabaseChild("authdata").child(userId);

        mDatabaseReferenceAuth.addListenerForSingleValueEvent(new AbstractValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                HashMap<String, String> currentUser = (HashMap<String, String>) dataSnapshot.getValue();

                toLoad[0].setText(currentUser.get("login"));
                toLoad[1].setText(currentUser.get("password"));
            }
        });
    }

    /**
     * Method that saves changed user data
     * @param userId user id
     * @param information new user information
     */
    public static void saveProfileChanges(final String userId, final String[] information) {
        mDatabaseReference = getDatabaseChild("users").child(userId);

        mDatabaseReference.addListenerForSingleValueEvent(new AbstractValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                getDatabaseChild("authdata").child(userId).child("login").setValue(information[0]);
                getDatabaseChild("authdata").child(userId).child("password").setValue(information[1]);
                getDatabaseChild("users").child(userId).child("name").setValue(information[2]);
                getDatabaseChild("users").child(userId).child("surname").setValue(information[3]);
                getDatabaseChild("users").child(userId).child("nickname").setValue(information[4]);
            }
        });
    }

    /**
     * Method that allows to load user favorite places from database to array adapter
     * @param userId user id
     * @param adapter adapter to put places
     */
    @Deprecated
    public static void loadUserFavouritePlacesList(final String userId, ArrayAdapter<String> adapter) {
        mDatabaseReference = getDatabaseChild("users").child(userId).child("favouritePlaces");
        mDatabaseReference.addListenerForSingleValueEvent(new AbstractValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                String[] idArray = dataSnapshot.getValue().toString().split(",");

                Arrays.sort(idArray, (a, b) -> (Integer.parseInt(a) - Integer.parseInt(b)));
                DatabaseReference mDatabaseReferencePlaces= getDatabaseChild("places");
                mDatabaseReferencePlaces.addChildEventListener(new AbstractChildEventListener() {
                    int position = 0;
                    @Override
                    public void onChildAdded(DataSnapshot dataSnapshot, String s) {
                        Place place = dataSnapshot.getValue(Place.class);
                        if (position < idArray.length && (place.getIdAsString()).equals(idArray[position])) {
                            position++;
                            adapter.add(place.getName());

                        }
                    }
                });
            }
        });
    }

    public static void loadUserFavouritePlacesListNew(final String userId, final Context context, final FragmentManager fragmentManager, final Fragment fragment) {
        mDatabaseReference = getDatabaseChild("users").child(userId).child("favouritePlaces");
        mDatabaseReference.addListenerForSingleValueEvent(new AbstractValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                String idArray = dataSnapshot.getValue().toString();
                FavouritePlacesUtils.setPlaces(context, idArray);
                fragmentManager.beginTransaction().add(R.id.places_fragment, fragment).commit();
            }
        });
    }

    /**
     * Method that loads all user profile information and initializes list of friends
     * @param context current context
     * @param userId user id
     * @param userProfileInfo fields with user profile information
     * @param fragmentManager fragment manager to load friends list
     */
    public static void loadUserProfile(final Context context, final int userId, final TextView[] userProfileInfo, final FragmentManager fragmentManager) {
        mDatabaseReference = getDatabaseChild("users");

        mDatabaseReference.addChildEventListener(new AbstractChildEventListener() {
            @Override
            public void onChildAdded(DataSnapshot dataSnapshot, String s) {
                User user = dataSnapshot.getValue(User.class);
                if(user == null) {
                    return;
                }

                if(userId == user.getId()) {
                    userProfileInfo[0].setText(user.getName());
                    userProfileInfo[1].setText(user.getSurname());
                    //TODO: move string constant to values/strings
                    userProfileInfo[2].setText("@" + user.getNickname());

                    //TODO: add friends list
                    FriendsDataUtils.setFriendsLength(context, user.getFriendsLength());
                    FriendsDataUtils.setFriends(context, user.getFriends());
                    android.support.v4.app.Fragment fragment = fragmentManager.findFragmentById(R.id.fragmentContainer);

                    if (fragment == null) {
                        fragment = new HorizontalListViewFragment();
                        fragmentManager.beginTransaction().add(R.id.fragmentContainer, fragment).commit();
                    }
                }
            }
        });
    }

    public static void saveCreatedPlace(final Uri uri, final String[] placeInfo, final LatLng position) {
        mDatabaseReference = getDatabaseChild("maxidplaces");
        mDatabaseReference.addListenerForSingleValueEvent(new AbstractValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                final Integer id = dataSnapshot.getValue(Integer.class);

                Place newPlace = new Place(id, placeInfo[0], placeInfo[1], placeInfo[2], position.latitude, position.longitude);

                getDatabaseChild("places").child(id.toString()).setValue(newPlace);
                mDatabaseReference.setValue(id + 1);

                StorageReference child = mStorageRef.child("photos").child(id.toString() + "place_photo");

                if(uri != null) {
                    child.putFile(uri);
                }
            }
        });
    }

    public static void saveRoute(final Uri uri, final String userId, final Context context) {
        if(uri != null) {
            mStorageRef.child("routes").child(userId).child(userId + "_" + RoutesUtils.getRoutesLength(context)).putFile(uri);
        }
    }

    public static void updateRoutesLength(final String userId, final long length) {
        getDatabaseChild("users").child(userId).child("routesLength").setValue(length + 1);
    }

    public static void getUserRoutesLength(final String userId, final Context context, final FragmentManager fragmentManager, final Fragment fragment) {
        getDatabaseChild("users").child(userId).child("routesLength").addListenerForSingleValueEvent(new AbstractValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                Long length = (Long) dataSnapshot.getValue();
                android.util.Log.d("bbbbbb", RoutesUtils.getRoutesLength(context).toString());
                RoutesUtils.setRoutesLength(context, length);
                fragmentManager.beginTransaction().add(R.id.fragmentContainer2, fragment).commit();
            }

        });
    }

    public static void getUserRoutesLength2(final String userId, final Context context) {
        getDatabaseChild("users").child(userId).child("routesLength").addListenerForSingleValueEvent(new AbstractValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                Long length = (Long) dataSnapshot.getValue();
                RoutesUtils.setRoutesLength(context, length);
            }

        });
    }

    public static void saveConvertedPlace(final Uri uri, final Place place) {
        mDatabaseReference = getDatabaseChild("maxidplaces");
        mDatabaseReference.addListenerForSingleValueEvent(new AbstractValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                final Integer id = dataSnapshot.getValue(Integer.class);
                place.setId(id);

                getDatabaseChild("places").child(id.toString()).setValue(place);
                mDatabaseReference.setValue(id + 1);

                StorageReference child = mStorageRef.child("photos").child(id.toString() + "place_photo");

                if(uri != null) {
                    child.putFile(uri);
                }
            }
        });
    }

    public static void runDescriptionDialog(final Context context, final Marker marker) {
        getDatabaseChild("places").addChildEventListener(new AbstractChildEventListener() {

            @Override
            public void onChildAdded(DataSnapshot dataSnapshot, String s) {
                Place place = dataSnapshot.getValue(Place.class);
                if ((place.getLatitude() == marker.getPosition().latitude) && (place.getLongitude() == marker.getPosition().longitude)) {
                    AlertDialogCreator.createAlertDescriptionDialog(context, place).show();
                }
            }
        });
    }


    public static void saveRoute(final String userId, final ArrayList<LatLng> route) {
        StringBuilder routeString = new StringBuilder();
        for (LatLng ll : route) {
            if (ll == null) {
                Log.d("wtff", "null");
                continue;
            }
            routeString.append(ll.latitude);
            routeString.append(";");
            routeString.append(ll.longitude);
            routeString.append(":");
        }

        if (route.size() > 0) {
            routeString.deleteCharAt(routeString.lastIndexOf(":"));
        }

        getDatabaseChild("routes").child(userId).push().setValue(routeString.toString());
    }

    public static void saveRouteInfo(final String userId, final Long descriptionId, final String description) {
        getDatabaseChild("routes_descriptions").child(userId).child(descriptionId.toString()).setValue(description);
    }

    public static void fillDescription(final TextView tv, final Integer id, final String userId) {
        getDatabaseChild("routes_descriptions").child(userId).child(id.toString()).addListenerForSingleValueEvent(new AbstractValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                String description = "No description given.";

                if (dataSnapshot != null) {
                    description = (String) dataSnapshot.getValue();
                }

                tv.setText(description);
            }
        });
    }

    public static void fillDescriptionPlaces(final TextView tv, final String id) {
        final StringBuilder stringBuilder = new StringBuilder();
        getDatabaseChild("places").child(id).child("name").addListenerForSingleValueEvent(new AbstractValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {

                if (dataSnapshot != null) {
                    stringBuilder.append((String) dataSnapshot.getValue());
                    stringBuilder.append('\n');
                }
            }
        });
        getDatabaseChild("places").child(id).child("description").addListenerForSingleValueEvent(new AbstractValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {

                if (dataSnapshot != null) {
                    stringBuilder.append((String) dataSnapshot.getValue());
                }
                tv.setText(stringBuilder.toString());
            }
        });


    }

    public static void loadAvatar(CircleImageView circleImageView, final Context context) {
        mStorageRef.child("avatars").child(AuthorizationUtils.getLoggedInAsString(context) + "avatar")
                .getDownloadUrl().addOnSuccessListener(uri -> Picasso.with(context).load(uri)
                .placeholder(android.R.drawable.btn_star_big_on)
                .error(android.R.drawable.btn_star_big_on)

                .into(circleImageView));
    }

    @Nullable
    private static DatabaseReference getDatabaseChild(String childName) {
        DatabaseReference databaseReference = getDatabaseReference();
        if(databaseReference != null) {
            return databaseReference.child(childName);
        }

        return null;
    }

    @Nullable
    private static DatabaseReference getDatabaseReference() {
        if(mBase != null) {
            return mBase.getReference();
        }

        return null;
    }
}
