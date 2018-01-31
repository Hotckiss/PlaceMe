package placeme.ru.placemedemo.core.database;

import android.app.Activity;
import android.content.Context;
import android.graphics.Bitmap;
import android.net.Uri;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.view.View;
import android.view.WindowManager;
import android.widget.ArrayAdapter;
import android.widget.ImageView;
import android.widget.ProgressBar;
import android.widget.RatingBar;
import android.widget.TextView;

import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.Marker;
import com.google.android.gms.maps.model.MarkerOptions;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;
import com.squareup.picasso.Picasso;

import java.io.ByteArrayOutputStream;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.HashMap;

import de.hdodenhof.circleimageview.CircleImageView;
import placeme.ru.placemedemo.R;
import placeme.ru.placemedemo.core.Controller;
import placeme.ru.placemedemo.elements.AuthData;
import placeme.ru.placemedemo.elements.Place;
import placeme.ru.placemedemo.elements.User;
import placeme.ru.placemedemo.elements.UserDataFields;
import placeme.ru.placemedemo.elements.UserProfileFields;
import placeme.ru.placemedemo.ui.dialogs.AlertDialogCreator;
import placeme.ru.placemedemo.ui.views.HorizontalListViewFragment;

/**
 * Class that have all methods connected with working with the database
 * Created by Андрей on 21.12.2017.
 */
public class DatabaseManager {
    private static final String USERS_KEY = "users";
    private static final String AUTH_DATA_KEY = "authdata";
    private static final String MAX_ID = "maxid";
    private static final String MAX_PLACE_ID = "maxidplaces";
    private static final String PLACES_KEY = "places";
    private static final String USER_NAME_KEY = "name";
    private static final String USER_SURNAME_KEY = "surname";
    private static final String USER_NICKNAME_KEY = "nickname";
    private static final String USER_LOGIN_KEY = "login";
    private static final String USER_PASSWORD_KEY = "password";
    private static final String USER_AVATAR_KEY = "avatars";
    private static final String PHOTOS_KEY = "photos";
    private static final String PLACE_PHOTO_SUFFIX = "place_photo";
    private static final String AVATAR_SUFFIX = "avatar";
    private static final String ROUTES_KEY = "routes";
    private static final String ROUTES_LENGTH_KEY = "routesLength";
    private static final String FRIENDS_KEY = "friends";
    private static final String FRIENDS_LENGTH_KEY = "friendsLength";
    private static final String REVIEWS_KEY = "reviews";
    private static final String PLANS_KEY = "plans";
    private static final String ROUTES_DESCRIPTION_KEY = "routes_descriptions";
    private static final String DEFAULT_DESCRIPTION = "No description given.";
    private static final String DESCRIPTION_KEY = "description";
    private static final String DOG_CHARACTER = "@";
    private static final String DATABASE_DELIMER = ",";
    private static final String TUPLE_DELIMER = ";";
    private static final String POINT_DELIMER = ":";
    private static final String NAME_DELIMER = "_";
    private static final String SPACE_DELIMER = " ";
    private static final String END_DELIMER = "\n";
    private static final String DASH_DELIMER = "-";
    private static final String MARKS_SUM = "sumOfMarks";
    private static final String MARKS_COUNT = "numberOfRatings";
    private static final String FAVOURITE_PLACES_TAG = "favouritePlaces";
    private static final char END_LINE = '\n';
    private static final double PERCENT_TO_RATING = 20.0;
    
    private static FirebaseDatabase mBase;
    private static DatabaseReference mDatabaseReference;

    private static StorageReference mStorageRef;

    static {
        mBase = FirebaseDatabase.getInstance();
        mStorageRef = FirebaseStorage.getInstance().getReference();
    }

    /**
     * Method that register new user in database with unique id
     * @param newAuthData authentication data that user input during registration
     * @param newUserData user data that user input during registration
     */
    public static void registerUser(final AuthData newAuthData, final User newUserData) {
        DatabaseReference databaseReference = getDatabaseChild(MAX_ID);

        if (databaseReference == null) {
            return;
        }

        databaseReference.addListenerForSingleValueEvent(new AbstractValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                Integer id = dataSnapshot.getValue(Integer.class);
                if (id != null) {
                    newAuthData.setId(id);
                    newUserData.setId(id);
                    DatabaseReference referenceAuth = getDatabaseChild(AUTH_DATA_KEY);
                    DatabaseReference referenceUsers = getDatabaseChild(USERS_KEY);

                    if (referenceUsers != null) {
                        referenceUsers.child(id.toString()).setValue(newUserData);
                    }
                    if (referenceAuth != null) {
                        referenceAuth.child(id.toString()).setValue(newAuthData);
                    }
                    databaseReference.setValue(id + 1);
                }
            }
        });
    }

    /**
     * Method that searches places in database within query string and lock any actions while loading
     * this makes impossible to build root before loading all places
     * @param arrayAdapter adapter with names of founded places
     * @param places array with founded places
     * @param toFind string which contains user query to search
     * @param myPosition current user positions
     * @param context current context
     * @param activity UI activity that calls method
     */
    public static void findPlacesByString(final ArrayAdapter<String> arrayAdapter, final ArrayList<Place> places,
                                          final String toFind, final LatLng myPosition,
                                          final Context context, final Activity activity) {
        activity.getWindow().setFlags(WindowManager.LayoutParams.FLAG_NOT_TOUCHABLE,
                WindowManager.LayoutParams.FLAG_NOT_TOUCHABLE);
        ProgressBar progressBar = new ProgressBar(context);
        progressBar.setVisibility(View.VISIBLE);
        DatabaseReference databaseReference = FirebaseDatabase.getInstance().getReference().child(PLACES_KEY);

        databaseReference.addChildEventListener(new AbstractChildEventListener() {
            @Override
            public void onChildAdded(DataSnapshot dataSnapshot, String s) {
                Place place = dataSnapshot.getValue(Place.class);
                if (place != null && checkAccess(place, myPosition, context) && isAppropriate(place, toFind)) {
                    addPlaceToList(arrayAdapter, places, place);
                }
            }
        });

        databaseReference.addListenerForSingleValueEvent(new AbstractValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                progressBar.setVisibility(View.GONE);
                activity.getWindow().clearFlags(WindowManager.LayoutParams.FLAG_NOT_TOUCHABLE);
            }
        });
    }

    /**
     * Method that searches places in database within query string and maked impossible to add friends
     * before load would be finished
     * @param arrayAdapter adapter with names of founded places
     * @param users array with founded users
     * @param toFind string which contains user query to search
     * @param activity UI activity that calls method
     */
    public static void findUsersByString(final ArrayAdapter<String> arrayAdapter, final ArrayList<User> users,
                                         final String toFind, final Context context, final Activity activity) {
        activity.getWindow().setFlags(WindowManager.LayoutParams.FLAG_NOT_TOUCHABLE,
                WindowManager.LayoutParams.FLAG_NOT_TOUCHABLE);
        ProgressBar progressBar = new ProgressBar(context);
        progressBar.setVisibility(View.VISIBLE);

        DatabaseReference databaseReference = FirebaseDatabase.getInstance().getReference().child(USERS_KEY);
        databaseReference.addChildEventListener(new AbstractChildEventListener() {
            @Override
            public void onChildAdded(DataSnapshot dataSnapshot, String s) {
                User user = dataSnapshot.getValue(User.class);
                if (user != null && user.getNickname().contains(toFind)) {
                    arrayAdapter.add(user.getName());
                    users.add(user);
                }
            }
        });

        databaseReference.addListenerForSingleValueEvent(new AbstractValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                progressBar.setVisibility(View.GONE);
                activity.getWindow().clearFlags(WindowManager.LayoutParams.FLAG_NOT_TOUCHABLE);
            }
        });
    }

    /**
     * Method that updates place rating after user voting
     * and immediately updates rating bar in UI
     * @param ratingBar rating bar to update
     * @param placeId place that user have rated
     */
    @SuppressWarnings("unchecked")
    public static void updatePlaceRating(final RatingBar ratingBar, final String placeId) {
        mDatabaseReference = getDatabaseChild(PLACES_KEY);
        if (mDatabaseReference != null) {
            mDatabaseReference = mDatabaseReference.child(placeId);
        }

        mDatabaseReference.addListenerForSingleValueEvent(new AbstractValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                HashMap<String, Object> currentPlace = (HashMap<String, Object>) dataSnapshot.getValue();
                if (currentPlace != null) {
                    float mark = ratingBar.getRating();
                    float currentSum = Float.parseFloat(currentPlace.get(MARKS_SUM).toString());
                    long currentNumberOfMarks = Long.parseLong(currentPlace.get(MARKS_COUNT).toString());

                    currentSum += mark;
                    currentNumberOfMarks++;

                    DatabaseReference mDatabaseReferenceSet = mDatabaseReference.child(MARKS_SUM);
                    mDatabaseReferenceSet.setValue(currentSum);

                    mDatabaseReferenceSet = mDatabaseReference.child(MARKS_COUNT);
                    mDatabaseReferenceSet.setValue(currentNumberOfMarks);
                }
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
        mDatabaseReference = getDatabaseChild(USERS_KEY);
        if (mDatabaseReference != null) {
            mDatabaseReference = mDatabaseReference.child(userId).child(FAVOURITE_PLACES_TAG);
        }

        mDatabaseReference.addListenerForSingleValueEvent(new AbstractValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                Object placeObject = dataSnapshot.getValue();
                if (placeObject != null) {
                    String places = placeObject.toString();
                    if (places == null || places.isEmpty()) {
                        mDatabaseReference.setValue(placeId);
                    } else if (!isAlreadyFavourite(places, placeId)) {
                        mDatabaseReference.setValue(places + DATABASE_DELIMER + placeId);
                    }
                }
            }
        });
    }

    /**
     * Method that loads all markers from database to the map
     * @param googleMap markers destination map
     */
    public static void loadMarkersToMap(final GoogleMap googleMap) {
        mDatabaseReference = getDatabaseChild(PLACES_KEY);
        mDatabaseReference.addChildEventListener(new AbstractChildEventListener() {
            @Override
            public void onChildAdded(DataSnapshot dataSnapshot, String s) {
                Place place = dataSnapshot.getValue(Place.class);
                if (place != null) {
                    googleMap.addMarker(new MarkerOptions()
                            .position(new LatLng(place.getLatitude(), place.getLongitude()))
                            .title(place.getName()));
                }
            }

        });
    }

    /**
     * Method that loads markers founded by query from database to the map
     * @param googleMap markers destination map
     * @param query user search query
     */
    public static void addMarkersByQuery(final GoogleMap googleMap, final String query) {
        mDatabaseReference = getDatabaseChild(PLACES_KEY);
        mDatabaseReference.addChildEventListener(new AbstractChildEventListener() {
            @Override
            public void onChildAdded(DataSnapshot dataSnapshot, String s) {
                Place place = dataSnapshot.getValue(Place.class);
                if (place != null && isAppropriate(place, query)) {
                    googleMap.addMarker(new MarkerOptions()
                            .position(new LatLng(place.getLatitude(), place.getLongitude()))
                            .title(place.getName()));
                }
            }
        });
    }

    /**
     * Method that loads user information from the database to the edit fields
     * @param toLoad array of fields to init
     * @param userId user id to search in database
     */
    @SuppressWarnings("unchecked")
    public static void loadUserDataForEdit(final UserDataFields toLoad, String userId) {
        DatabaseReference mDatabaseReferenceUser = getDatabaseChild(USERS_KEY);
        DatabaseReference mDatabaseReferenceAuth = getDatabaseChild(AUTH_DATA_KEY);

        if (mDatabaseReferenceUser != null) {
            mDatabaseReferenceUser = mDatabaseReferenceUser.child(userId);
        }

        if (mDatabaseReferenceAuth != null) {
            mDatabaseReferenceAuth = mDatabaseReferenceAuth.child(userId);
        }

        if (mDatabaseReferenceUser != null) {
            mDatabaseReferenceUser.addListenerForSingleValueEvent(new AbstractValueEventListener() {
                @Override
                public void onDataChange(DataSnapshot dataSnapshot) {
                    HashMap<String, String> currentUser = (HashMap<String, String>) dataSnapshot.getValue();

                    if (currentUser != null) {
                        toLoad.setProfileFields(currentUser.get(USER_NAME_KEY),
                                currentUser.get(USER_SURNAME_KEY), currentUser.get(USER_NICKNAME_KEY));
                    }
                }
            });
        }

        if (mDatabaseReferenceAuth != null) {
            mDatabaseReferenceAuth.addListenerForSingleValueEvent(new AbstractValueEventListener() {
                @Override
                public void onDataChange(DataSnapshot dataSnapshot) {
                    HashMap<String, String> currentUser = (HashMap<String, String>) dataSnapshot.getValue();

                    if (currentUser != null) {
                        toLoad.setAuthFields(currentUser.get(USER_LOGIN_KEY), currentUser.get(USER_PASSWORD_KEY));
                    }
                }
            });
        }
    }

    /**
     * Method that saves changed user data
     * @param userId user id
     * @param newAuthData authentication data that user input during editing profile
     * @param newUserData user data that user input during editing profile
     */
    public static void saveProfileChanges(final String userId, final AuthData newAuthData, final User newUserData) {
        mDatabaseReference = getDatabaseChild(USERS_KEY);
        if (mDatabaseReference != null) {
            mDatabaseReference = mDatabaseReference.child(userId);
        }

        mDatabaseReference.addListenerForSingleValueEvent(new AbstractValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                DatabaseReference referenceAuth = getDatabaseChild(AUTH_DATA_KEY);
                DatabaseReference referenceUsers = getDatabaseChild(USERS_KEY);

                if (referenceAuth != null) {
                    referenceAuth.child(userId).child(USER_LOGIN_KEY).setValue(newAuthData.getLogin());
                    referenceAuth.child(userId).child(USER_PASSWORD_KEY).setValue(newAuthData.getPassword());
                }

                if (referenceUsers != null) {
                    referenceUsers.child(userId).child(USER_NAME_KEY).setValue(newUserData.getName());
                    referenceUsers.child(userId).child(USER_SURNAME_KEY).setValue(newUserData.getSurname());
                    referenceUsers.child(userId).child(USER_NICKNAME_KEY).setValue(newUserData.getNickname());
                }
            }
        });
    }

    /**
     * Method that allows to load user favorite places from database to fragment
     * @param userId user id
     * @param context current context
     * @param fragmentManager fragment managet for transaction
     * @param fragment output fragment
     */
    public static void loadUserFavouritePlacesList(final String userId, final Context context,
                                                   final FragmentManager fragmentManager, final Fragment fragment) {
        mDatabaseReference = getDatabaseChild(USERS_KEY);
        if (mDatabaseReference != null) {
            mDatabaseReference = mDatabaseReference.child(userId).child(FAVOURITE_PLACES_TAG);
        }

        mDatabaseReference.addListenerForSingleValueEvent(new AbstractValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                String idArray = (String) dataSnapshot.getValue();
                Controller.setPlaces(context, idArray);
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
    public static void loadUserProfile(final Context context, final int userId,
                                       final UserProfileFields userProfileInfo, final FragmentManager fragmentManager) {
        mDatabaseReference = getDatabaseChild(USERS_KEY);

        mDatabaseReference.addChildEventListener(new AbstractChildEventListener() {
            @Override
            public void onChildAdded(DataSnapshot dataSnapshot, String s) {
                User user = dataSnapshot.getValue(User.class);
                if (user == null) {
                    return;
                }

                if (userId == user.getId()) {
                    userProfileInfo.fillAllFields(user.getName(), user.getSurname(),
                            DOG_CHARACTER + user.getNickname());

                    Controller.setFriendsLength(context, user.getFriendsLength());
                    Controller.setFriends(context, user.getFriends());
                    Fragment fragment = fragmentManager.findFragmentById(R.id.fragmentContainer);

                    if (fragment == null) {
                        fragment = new HorizontalListViewFragment();
                        fragmentManager.beginTransaction().add(R.id.fragmentContainer, fragment).commit();
                    }
                }
            }
        });
    }

    /**
     * Method that allows to save created place in database
     * @param uri picture uri
     * @param placeInfo text description of the place
     * @param position place coordinates
     */
    public static void saveCreatedPlace(final Uri uri, final Place placeInfo, final LatLng position) {
        mDatabaseReference = getDatabaseChild(MAX_PLACE_ID);
        mDatabaseReference.addListenerForSingleValueEvent(new AbstractValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                final Integer id = dataSnapshot.getValue(Integer.class);

                if (id != null) {
                    if (uploadPlaceInfo(placeInfo, id, position)) {
                        mDatabaseReference.setValue(id + 1);
                    }

                    StorageReference child = mStorageRef.child(PLACES_KEY).child(id.toString() + PLACE_PHOTO_SUFFIX);

                    if (uri != null) {
                        child.putFile(uri);
                    }
                }
            }
        });
    }

    /**
     * Method that allows to save created place in database
     * @param bitmap picture bitmap
     * @param placeInfo text description of the place
     * @param position place coordinates
     */
    public static void saveCreatedPlace2(final Bitmap bitmap, final Place placeInfo, final LatLng position) {
        mDatabaseReference = getDatabaseChild(MAX_PLACE_ID);
        mDatabaseReference.addListenerForSingleValueEvent(new AbstractValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                final Integer id = dataSnapshot.getValue(Integer.class);

                if (id != null) {
                    if (uploadPlaceInfo(placeInfo, id, position)) {
                        mDatabaseReference.setValue(id + 1);
                    }

                    StorageReference child = mStorageRef.child(PLACES_KEY).child(id.toString() + PLACE_PHOTO_SUFFIX);

                    if (bitmap != null) {
                        ByteArrayOutputStream byteArrayOutputStream = new ByteArrayOutputStream();
                        bitmap.compress(Bitmap.CompressFormat.JPEG, 100, byteArrayOutputStream);
                        byte[] data = byteArrayOutputStream.toByteArray();
                        child.putBytes(data);
                    }
                }
            }
        });
    }

    /**
     * Method that saves map screenshot with route to database
     * @param uri screenshot uri
     * @param userId user ID
     * @param context current context
     */
    public static void saveRoute(final Uri uri, final String userId, final Context context) {
        if (uri != null) {
            mStorageRef.child(ROUTES_KEY).child(userId)
                    .child(userId + NAME_DELIMER + Controller.getRoutesLength(context)).putFile(uri);
        }
    }

    /**
     * Method that called after adding new route, that means that ID of next route will be other
     * @param userId user ID
     * @param length current last added route ID
     */
    public static void updateRoutesLength(final String userId, final long length) {
        DatabaseReference reference = getDatabaseChild(USERS_KEY);

        if (reference != null) {
           reference.child(userId).child(ROUTES_LENGTH_KEY).setValue(length + 1);
        }
    }

    /**
     * Method gets current number of user routes and adds than to some fragment
     * @param userId used ID
     * @param context current context
     * @param fragmentManager fragment manager for transaction
     * @param fragment output fragment
     */
    public static void getUserRoutesLength(final String userId, final Context context,
                                           final FragmentManager fragmentManager, final Fragment fragment) {
        DatabaseReference reference = getDatabaseChild(USERS_KEY);

        if (reference != null) {
            reference.child(userId).child(ROUTES_LENGTH_KEY).addListenerForSingleValueEvent(new AbstractValueEventListener() {
                @Override
                public void onDataChange(DataSnapshot dataSnapshot) {
                    Long length = (Long) dataSnapshot.getValue();
                    if (length != null) {
                        Controller.setRoutesLength(context, length);
                        fragmentManager.beginTransaction().add(R.id.fragmentContainer2, fragment).commit();
                    }
                }

            });
        }
    }

    /**
     * Method gets current number of user routes without adding them to the fragment
     * @param userId used ID
     * @param context current context
     */
    public static void getUserRoutesLength2(final String userId, final Context context) {
        DatabaseReference reference = getDatabaseChild(USERS_KEY);

        if (reference != null) {
            reference.child(userId).child(ROUTES_LENGTH_KEY).addListenerForSingleValueEvent(new AbstractValueEventListener() {
                @Override
                public void onDataChange(DataSnapshot dataSnapshot) {
                    Long length = (Long) dataSnapshot.getValue();
                    if (length != null) {
                        Controller.setRoutesLength(context, length);
                    }
                }

            });
        }
    }

    /**
     * Method that loads place that was converted from Google Places to database
     * @param uri picture uri
     * @param place place to save
     */
    public static void saveConvertedPlace(final Uri uri, final Place place) {
        mDatabaseReference = getDatabaseChild(MAX_PLACE_ID);
        mDatabaseReference.addListenerForSingleValueEvent(new AbstractValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                final Integer id = dataSnapshot.getValue(Integer.class);
                if (id != null) {
                    place.setId(id);

                    DatabaseReference reference = getDatabaseChild(PLACES_KEY);
                    if (reference != null) {
                        reference.child(id.toString()).setValue(place);
                        mDatabaseReference.setValue(id + 1);

                        StorageReference child = mStorageRef.child(PHOTOS_KEY).child(id.toString() + PLACE_PHOTO_SUFFIX);

                        if (uri != null) {
                            child.putFile(uri);
                        }
                    }
                }
            }
        });
    }

    /**
     * Method that runs alert dialog with full place description
     * Place loaded from database
     * @param context current context
     * @param marker place position
     */
    public static void runDescriptionDialog(final Context context, final Marker marker,
                                            final LatLng myPosition, final GoogleMap googleMap) {
        DatabaseReference reference = getDatabaseChild(PLACES_KEY);

        if (reference != null) {
            reference.addChildEventListener(new AbstractChildEventListener() {

                @Override
                public void onChildAdded(DataSnapshot dataSnapshot, String s) {
                    Place place = dataSnapshot.getValue(Place.class);
                    if (place != null && (place.getLatitude() == marker.getPosition().latitude) && (place.getLongitude() == marker.getPosition().longitude)) {
                        AlertDialogCreator.createAlertDescriptionDialog(context, place, myPosition, googleMap).show();
                    }
                }
            });
        }
    }

    /**
     * Method that loads route to database as sequence of points
     * @param userId user ID who owns this route
     * @param route route to save
     */
    public static void saveRoute(final String userId, final ArrayList<LatLng> route) {
        StringBuilder routeString = new StringBuilder();
        for (LatLng ll : route) {
            if (ll != null) {
                routeString.append(ll.latitude);
                routeString.append(TUPLE_DELIMER);
                routeString.append(ll.longitude);
                routeString.append(POINT_DELIMER);
            }
        }

        if (route.size() > 0) {
            routeString.deleteCharAt(routeString.lastIndexOf(POINT_DELIMER));
        }

        DatabaseReference reference = getDatabaseChild(ROUTES_KEY);

        if (reference != null) {
            reference.child(userId).push().setValue(routeString.toString());
        }
    }

    /**
     * Method that saves route description to database
     * @param userId user Id who owns this route
     * @param descriptionId ID of route that has this description
     * @param description string that contains all description
     */
    public static void saveRouteInfo(final String userId, final Long descriptionId, final String description) {
        DatabaseReference reference = getDatabaseChild(ROUTES_DESCRIPTION_KEY);
        if (reference != null) {
            reference.child(userId).child(descriptionId.toString()).setValue(description);
        }
    }

    /**
     * Method that loads route description from database
     * @param textView text view that should be filled with description
     * @param id route ID
     * @param userId user ID
     */
    public static void fillDescription(final TextView textView, final Integer id, final String userId) {
        DatabaseReference reference = getDatabaseChild(ROUTES_DESCRIPTION_KEY);
        if (reference != null) {
            reference.child(userId).child(id.toString()).addListenerForSingleValueEvent(new AbstractValueEventListener() {
                @Override
                public void onDataChange(DataSnapshot dataSnapshot) {
                    String description = DEFAULT_DESCRIPTION;

                    if (dataSnapshot != null) {
                        description = (String) dataSnapshot.getValue();
                    }

                    textView.setText(description);
                }
            });
        }
    }

    /**
     * Method that loads place description from database
     * @param textView text view that should be filled with description
     * @param id place ID
     */
    public static void fillDescriptionPlaces(final TextView textView, final String id) {
        final StringBuilder stringBuilder = new StringBuilder();
        DatabaseReference referenceName = getDatabaseChild(PLACES_KEY);
        if (referenceName != null) {
            referenceName.child(id).child(USER_NAME_KEY).addListenerForSingleValueEvent(new AbstractValueEventListener() {
                @Override
                public void onDataChange(DataSnapshot dataSnapshot) {
                    if (dataSnapshot != null) {
                        stringBuilder.append((String) dataSnapshot.getValue());
                        stringBuilder.append(END_LINE);
                    }
                }
            });
        }
        DatabaseReference referenceDescription = getDatabaseChild(PLACES_KEY);
        if (referenceDescription != null) {
            referenceDescription.child(id).child(DESCRIPTION_KEY).addListenerForSingleValueEvent(new AbstractValueEventListener() {
                @Override
                public void onDataChange(DataSnapshot dataSnapshot) {
                    if (dataSnapshot != null) {
                        stringBuilder.append((String) dataSnapshot.getValue());
                    }
                    textView.setText(stringBuilder.toString());
                }
            });
        }
    }

    /**
     * Method that loads avatar to the field with picture of user
     * @param circleImageView image view to load avatar
     * @param context current context
     * @param userId id of the user
     */
    public static void loadAvatar(CircleImageView circleImageView, final Context context, final String userId) {
        mStorageRef.child(USER_AVATAR_KEY).child(userId + AVATAR_SUFFIX)
                .getDownloadUrl().addOnSuccessListener(uri -> Picasso.with(context).load(uri)
                .placeholder(R.drawable.anonim)
                .error(R.drawable.anonim)
                .into(circleImageView));
    }

    /**
     * Method that loads picture of a place
     * @param imageView view where picture should be placed
     * @param place place with all necessary information
     * @param context current context
     */
    public static void loadDescriptionImage(final ImageView imageView, final Place place, final Context context) {
        mStorageRef.child(PHOTOS_KEY).child(place.getIdAsString() + PLACE_PHOTO_SUFFIX)
                .getDownloadUrl().addOnSuccessListener(uri -> Picasso.with(context).load(uri)
                .placeholder(R.drawable.noimage)
                .error(R.drawable.noimage)
                .into(imageView));
    }

    /**
     * Method that loads new avatar of user instead of old one
     * @param userId user id who changes avatar
     * @param uri uri of new avatar image
     */
    public static void setNewAvatar(final String userId, final Uri uri) {
        mStorageRef.child(USER_AVATAR_KEY).child(userId + AVATAR_SUFFIX).putFile(uri);
    }

    /**
     * Method that adds friend to users friend list if it wasn't already added
     * @param userId id of user who wants to add new friend
     * @param friendId id of friend to be added
     */
    public static void addFriend(final String userId, final String friendId) {
        DatabaseReference reference = getDatabaseChild(USERS_KEY);
        if (reference != null) {
            reference = reference.child(userId).child(FRIENDS_KEY);
            reference.addListenerForSingleValueEvent(new AbstractValueEventListener() {
                @Override
                public void onDataChange(DataSnapshot dataSnapshot) {
                    final String friends = dataSnapshot.getValue(String.class);
                    if (friends.length() == 0) {
                        DatabaseReference referenceFriends = getDatabaseChild(USERS_KEY);
                        if (referenceFriends != null) {
                            referenceFriends.child(userId).child(FRIENDS_KEY).setValue(friendId);
                            referenceFriends.child(userId).child(FRIENDS_LENGTH_KEY).setValue(1);
                        }
                    } else {
                        int length = friends.split(DATABASE_DELIMER).length;
                        boolean alreadyAdded = false;
                        for (String friend : friends.split(DATABASE_DELIMER)) {
                            if (friend.equals(friendId)) {
                                alreadyAdded = true;
                            }
                        }
                        if (!alreadyAdded) {
                            DatabaseReference referenceFriends = getDatabaseChild(USERS_KEY);
                            if (referenceFriends != null) {
                                referenceFriends.child(userId).child(FRIENDS_KEY).setValue(friends + DATABASE_DELIMER + friendId);
                                referenceFriends.child(userId).child(FRIENDS_LENGTH_KEY).setValue(length + 1);
                            }
                        }
                    }
                }
            });
        }
    }

    /**
     * Method that adds visiting plan to users plans list
     * @param placeName name of place to visit
     * @param userId id of user who wants to visit place
     * @param date date of planned visit
     */
    public static void addPlan(final String placeName, final String userId, final String date) {
        DatabaseReference reference = getDatabaseChild(PLANS_KEY);

        if (reference != null) {
            reference = reference.child(userId);
            reference.addListenerForSingleValueEvent(new AbstractValueEventListener() {
                @Override
                public void onDataChange(DataSnapshot dataSnapshot) {
                    Object plan = dataSnapshot.getValue();
                    DatabaseReference referencePlan = getDatabaseChild(PLANS_KEY);
                    if (referencePlan != null) {
                        referencePlan = referencePlan.child(userId);
                    }

                    if (referencePlan != null) {
                        if (plan != null) {
                            String currentPlan = dataSnapshot.getValue().toString();
                            referencePlan.setValue(currentPlan + TUPLE_DELIMER + placeName + END_LINE + date);
                        } else {
                            referencePlan.setValue(placeName + END_LINE + date);
                        }
                    }
                }
            });
        }
    }

    /**
     * Method that loads visiting plan to the screen
     * @param userId id of user whose plan should be loaded
     * @param adapter adapter with plans list
     */
    public static void loadPlan(final String userId, final ArrayAdapter<String> adapter) {
        DatabaseReference reference = getDatabaseChild(PLANS_KEY);

        if (reference != null) {
            reference = reference.child(userId);
            reference.addListenerForSingleValueEvent(new AbstractValueEventListener() {
                @Override
                public void onDataChange(DataSnapshot dataSnapshot) {
                    Object plan = dataSnapshot.getValue();
                    if (plan != null) {
                        String currentPlan = plan.toString();
                        if (currentPlan.length() != 0) {
                            String[] plans = currentPlan.split(TUPLE_DELIMER);
                            for (String planSingle : plans) {
                                String[] splitted = planSingle.split(END_DELIMER);
                                if (splitted.length != 0 && validatePlan(splitted[splitted.length - 1])) {
                                    adapter.add(planSingle);
                                }
                            }
                        }
                    }
                }
            });
        }
    }

    /**
     * Method that adds user review to all reviews about place
     * @param placeId reviewed place
     * @param review user review text
     */
    public static void addReview(final String placeId, final String review) {
        DatabaseReference reference = getDatabaseChild(REVIEWS_KEY);
        if (reference != null) {
            reference = reference.child(placeId);
            reference.addListenerForSingleValueEvent(new AbstractValueEventListener() {
                @Override
                public void onDataChange(DataSnapshot dataSnapshot) {
                    Object reviews = dataSnapshot.getValue();
                    DatabaseReference referenceNewReviews = getDatabaseChild(REVIEWS_KEY);
                    if (referenceNewReviews != null) {
                        if (reviews == null) {

                            referenceNewReviews = referenceNewReviews.child(placeId);
                            referenceNewReviews.setValue(review);
                        } else {
                            String allReviews = reviews.toString();
                            if (allReviews.length() == 0) {

                                referenceNewReviews = referenceNewReviews.child(placeId);
                                referenceNewReviews.setValue(review);
                            } else {

                                referenceNewReviews = referenceNewReviews.child(placeId);
                                referenceNewReviews.setValue(allReviews + TUPLE_DELIMER + review);
                            }
                        }
                    }
                }
            });
        }
    }

    /**
     * Method that loads all users reviews to array aadapter
     * @param placeId place which reviews should be extracted
     * @param adapter destination adapter to load reviews
     */
    public static void findReviews(final String placeId, ArrayAdapter<String> adapter) {
        DatabaseReference reference = getDatabaseChild(REVIEWS_KEY);
        if (reference != null) {
            reference = reference.child(placeId);
            reference.addListenerForSingleValueEvent(new AbstractValueEventListener() {
                @Override
                public void onDataChange(DataSnapshot dataSnapshot) {
                    Object data = dataSnapshot.getValue();
                    if (data != null) {
                        String allReviews = data.toString();
                        String[] arrayReviews = allReviews.split(TUPLE_DELIMER);

                        for (String singleReview : arrayReviews) {
                            adapter.add(singleReview);
                        }
                    }
                }
            });
        }
    }

    private static boolean uploadPlaceInfo(final Place placeInfo, final Integer placeId, final LatLng position) {
        Place newPlace = new Place(placeId, placeInfo.getName(), placeInfo.getDescription(), placeInfo.getTags(),
                position.latitude, position.longitude);
        DatabaseReference reference = getDatabaseChild(PLACES_KEY);

        if (reference != null) {
            reference.child(placeId.toString()).setValue(newPlace);
            return true;
        }
        return false;
    }

    private static boolean checkAccess(final Place place, final LatLng myPosition, final Context context) {
        boolean distanceEnabled = Controller.getDistanceSearchStatus(context);
        boolean ratingEnabled = Controller.getRatingSearchStatus(context);
        boolean distanceAccess = (!distanceEnabled) ||
                (Controller.getKilometers(myPosition, new LatLng(place.getLatitude(), place.getLongitude())) <= Controller.getDistanceSearchValue(context));
        boolean ratingAccess = (!ratingEnabled) ||
                (place.getMark() > (Controller.getRatingSearchValue(context) / PERCENT_TO_RATING));

        return distanceAccess && ratingAccess;
    }

    private static boolean isAppropriate(final Place place, final String toFind) {
        return containsIgnoreCase(place.getName(), toFind) || containsTag(place, toFind);
    }

    private static boolean containsIgnoreCase(final String text, final String word) {
        return text.toLowerCase().contains(word.toLowerCase());
    }

    private static boolean containsTag(final Place place, final String tagToSearch) {
        for (String tag : place.getTags().split(DATABASE_DELIMER)) {
            if (tagToSearch.equalsIgnoreCase(tag)) {
                return true;
            }
        }
        return false;
    }

    private static void addPlaceToList(final ArrayAdapter<String> arrayAdapter, final ArrayList<Place> places, final Place place) {
        arrayAdapter.add(place.getName());
        places.add(place);
    }

    private static boolean isAlreadyFavourite(final String places, final String favouritePlaceId) {
        for (String placeId : places.split(DATABASE_DELIMER)) {
            if (placeId.equals(favouritePlaceId)) {
                return true;
            }
        }
        return false;
    }

    private static boolean validatePlan(final String planDate) {
        Calendar calendar = Calendar.getInstance();
        String[] dateTime = planDate.split(SPACE_DELIMER);
        String[] data = dateTime[0].split(DASH_DELIMER);

        if (calendar.get(Calendar.YEAR) > Integer.parseInt(data[2])) {
            return false;
        } else if (calendar.get(Calendar.YEAR) == Integer.parseInt(data[2])) {
            if (calendar.get(Calendar.MONTH) > Integer.parseInt(data[1])) {
                return false;
            } else if ((calendar.get(Calendar.MONTH) + 1) == Integer.parseInt(data[1])) {
                return calendar.get(Calendar.DAY_OF_MONTH) <= Integer.parseInt(data[0]);
            }
        }
        return true;
    }

    @Nullable
    private static DatabaseReference getDatabaseChild(String childName) {
        DatabaseReference databaseReference = getDatabaseReference();
        if (databaseReference != null) {
            return databaseReference.child(childName);
        }

        return null;
    }

    @Nullable
    private static DatabaseReference getDatabaseReference() {
        if (mBase != null) {
            return mBase.getReference();
        }

        return null;
    }
}
