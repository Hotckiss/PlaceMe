package placeme.ru.placemedemo.core.database;

import android.content.Context;
import android.graphics.Bitmap;
import android.net.Uri;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.widget.ArrayAdapter;
import android.widget.EditText;
import android.widget.ImageView;
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

import java.io.ByteArrayOutputStream;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;

import de.hdodenhof.circleimageview.CircleImageView;
import placeme.ru.placemedemo.R;
import placeme.ru.placemedemo.core.Controller;
import placeme.ru.placemedemo.core.utils.SearchUtils;
import placeme.ru.placemedemo.elements.AuthData;
import placeme.ru.placemedemo.elements.Place;
import placeme.ru.placemedemo.elements.User;
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
    private static final String PLACES_KEY = "places";
    private static final String USER_NAME_KEY = "name";
    private static final String USER_SURNAME_KEY = "surname";
    private static final String USER_NICKNAME_KEY = "nickname";
    private static final String USER_LOGIN_KEY = "login";
    private static final String USER_PASSWORD_KEY = "password";
    private static final String USER_AVATAR_KEY = "avatars";
    private static final String DOG_CHARACTER = "@";
    
    private static FirebaseDatabase mBase;
    private static DatabaseReference mDatabaseReference;
    private static ChildEventListener mChildEventListener;

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
        mDatabaseReference = getDatabaseChild(AUTH_DATA_KEY);

        Controller.setLoggedIn(context, -1);
        mChildEventListener = new AbstractChildEventListener() {
            @Override
            public void onChildAdded(DataSnapshot dataSnapshot, String s) {
                AuthData authData = dataSnapshot.getValue(AuthData.class);
                if (authData != null) {
                    if (authData.getLogin().equals(email)) {
                        if (authData.getPassword().equals(password)) {
                            Controller.setLoggedIn(context, authData.getId());
                        }
                    }
                }
            }
        };
        mDatabaseReference.addChildEventListener(mChildEventListener);
    }

    /**
     * Method that register new user in database with unique id
     * @param information information that user input during registration
     */
    public static void registerUser(final String[] information) {
        mDatabaseReference = getDatabaseChild(MAX_ID);

        mDatabaseReference.addListenerForSingleValueEvent(new AbstractValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                Integer id = dataSnapshot.getValue(Integer.class);
                if (id != null) {
                    AuthData newAuthData = new AuthData(id, information[0], information[1]);
                    User newUser = new User(id, information[2], information[3], information[4]);
                    DatabaseReference referenceAuth = getDatabaseChild(AUTH_DATA_KEY);
                    DatabaseReference referenceUsers = getDatabaseChild(USERS_KEY);
                    if (referenceUsers != null) {
                        referenceUsers.child(id.toString()).setValue(newUser);
                    }
                    if (referenceAuth != null) {
                        referenceAuth.child(id.toString()).setValue(newAuthData);
                    }
                    mDatabaseReference.setValue(id + 1);
                }
            }
        });
    }

    /**
     * Method that searches places in database within query string
     * @param arrayAdapter adapter with names of founded places
     * @param places array with founded places
     * @param toFind string which contains user query to search
     * @param myPosition current user positions
     * @param context current context
     */
    public static void findPlacesByString(final ArrayAdapter<String> arrayAdapter, final ArrayList<Place> places, final String toFind, final LatLng myPosition, final Context context) {
        mDatabaseReference = getDatabaseChild(PLACES_KEY);

        mChildEventListener = new AbstractChildEventListener() {
            @Override
            public void onChildAdded(DataSnapshot dataSnapshot, String s) {
                Place place = dataSnapshot.getValue(Place.class);
                if (place != null) {
                    boolean distanceEnabled = SearchUtils.getDistanceSearchStatus(context);
                    boolean distanceAccess = (!distanceEnabled) || (Controller.getKilometers(myPosition, new LatLng(place.getLatitude(), place.getLongitude())) <= SearchUtils.getDistanceSearchValue(context));
                    boolean ratingEnabled = SearchUtils.getRatingSearchStatus(context);
                    boolean ratingAccess = (!ratingEnabled) || (place.getMark() > (SearchUtils.getRatingSearchValue(context) / 20.0));

                    if (distanceAccess && ratingAccess) {
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
                }
            }
        };
        mDatabaseReference.addChildEventListener(mChildEventListener);
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
                    float curSum = Float.parseFloat(currentPlace.get("sumOfMarks").toString());
                    long curNumberOfMarks = Long.parseLong(currentPlace.get("numberOfRatings").toString());

                    curSum += mark;
                    curNumberOfMarks++;

                    DatabaseReference mDatabaseReferenceSet = mDatabaseReference.child("sumOfMarks");
                    mDatabaseReferenceSet.setValue(curSum);

                    mDatabaseReferenceSet = mDatabaseReference.child("numberOfRatings");
                    mDatabaseReferenceSet.setValue(curNumberOfMarks);
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
            mDatabaseReference = mDatabaseReference.child(userId).child("favouritePlaces");
        }
        mDatabaseReference.addListenerForSingleValueEvent(new AbstractValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                Object placeObject = dataSnapshot.getValue();
                if (placeObject != null) {
                    String places = placeObject.toString();
                    if (places == null || places.equals("")) {
                        mDatabaseReference.setValue(placeId);
                    } else {
                        boolean wasAlreadyAddedToFavourite = false;
                        for (String str : places.split(",")) {
                            if (str.equals(placeId)) {
                                wasAlreadyAddedToFavourite = true;
                            }
                        }

                        if (!wasAlreadyAddedToFavourite) {
                            String newFavouritePlacesList = places + "," + placeId;
                            mDatabaseReference.setValue(newFavouritePlacesList);

                        }
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
                if (place != null) {
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
            }
        });
    }

    /**
     * Method that loads user information from the database to the edit fields
     * @param toLoad array of fields to init
     * @param userId user id to search in database
     */
    @SuppressWarnings("unchecked")
    public static void loadUserDataForEdit(final EditText[] toLoad, String userId) {
        mDatabaseReference = getDatabaseChild(USERS_KEY);
        if (mDatabaseReference != null) {
            mDatabaseReference = mDatabaseReference.child(userId);
        }
        mDatabaseReference.addListenerForSingleValueEvent(new AbstractValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                HashMap<String, String> currentUser = (HashMap<String, String>) dataSnapshot.getValue();

                if (currentUser != null) {
                    toLoad[2].setText(currentUser.get(USER_NAME_KEY));
                    toLoad[3].setText(currentUser.get(USER_SURNAME_KEY));
                    toLoad[4].setText(currentUser.get(USER_NICKNAME_KEY));
                }
            }
        });

        DatabaseReference mDatabaseReferenceAuth = getDatabaseChild(AUTH_DATA_KEY);

        if (mDatabaseReferenceAuth != null) {
            mDatabaseReferenceAuth = mDatabaseReferenceAuth.child(userId);
        }
        if (mDatabaseReferenceAuth != null) {
            mDatabaseReferenceAuth.addListenerForSingleValueEvent(new AbstractValueEventListener() {
                @Override
                public void onDataChange(DataSnapshot dataSnapshot) {
                    HashMap<String, String> currentUser = (HashMap<String, String>) dataSnapshot.getValue();

                    if (currentUser != null) {
                        toLoad[0].setText(currentUser.get(USER_LOGIN_KEY));
                        toLoad[1].setText(currentUser.get(USER_PASSWORD_KEY));
                    }
                }
            });
        }
    }

    /**
     * Method that saves changed user data
     * @param userId user id
     * @param information new user information
     */
    public static void saveProfileChanges(final String userId, final String[] information) {
        mDatabaseReference = getDatabaseChild(USERS_KEY);
        if (mDatabaseReference != null) {
            mDatabaseReference = mDatabaseReference.child(userId);
        }
        mDatabaseReference.addListenerForSingleValueEvent(new AbstractValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                DatabaseReference referenceAuth = getDatabaseChild(AUTH_DATA_KEY);
                if (referenceAuth != null) {
                    referenceAuth.child(userId).child(USER_LOGIN_KEY).setValue(information[0]);
                    referenceAuth.child(userId).child(USER_PASSWORD_KEY).setValue(information[1]);
                }
                DatabaseReference referenceUsers = getDatabaseChild(USERS_KEY);
                if (referenceUsers != null) {
                    referenceUsers.child(userId).child(USER_NAME_KEY).setValue(information[2]);
                    referenceUsers.child(userId).child(USER_SURNAME_KEY).setValue(information[3]);
                    referenceUsers.child(userId).child(USER_NICKNAME_KEY).setValue(information[4]);
                }
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
        mDatabaseReference = getDatabaseChild(USERS_KEY);
        if (mDatabaseReference != null) {
            mDatabaseReference = mDatabaseReference.child(userId).child("favouritePlaces");
        }
        mDatabaseReference.addListenerForSingleValueEvent(new AbstractValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                Object arrayId = dataSnapshot.getValue();
                if(arrayId != null) {
                    String[] idArray = arrayId.toString().split(",");

                    Arrays.sort(idArray, (a, b) -> (Integer.parseInt(a) - Integer.parseInt(b)));
                    DatabaseReference mDatabaseReferencePlaces = getDatabaseChild(PLACES_KEY);
                    if (mDatabaseReferencePlaces != null) {
                        mDatabaseReferencePlaces.addChildEventListener(new AbstractChildEventListener() {
                            int position = 0;

                            @Override
                            public void onChildAdded(DataSnapshot dataSnapshot, String s) {
                                Place place = dataSnapshot.getValue(Place.class);
                                if (place != null) {
                                    if (position < idArray.length && (place.getIdAsString()).equals(idArray[position])) {
                                        position++;
                                        adapter.add(place.getName());

                                    }
                                }
                            }
                        });
                    }
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
    public static void loadUserFavouritePlacesListNew(final String userId, final Context context, final FragmentManager fragmentManager, final Fragment fragment) {
        mDatabaseReference = getDatabaseChild(USERS_KEY);
        if (mDatabaseReference != null) {
            mDatabaseReference = mDatabaseReference.child(userId).child("favouritePlaces");
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
    public static void loadUserProfile(final Context context, final int userId, final TextView[] userProfileInfo, final FragmentManager fragmentManager) {
        mDatabaseReference = getDatabaseChild(USERS_KEY);

        mDatabaseReference.addChildEventListener(new AbstractChildEventListener() {
            @Override
            public void onChildAdded(DataSnapshot dataSnapshot, String s) {
                User user = dataSnapshot.getValue(User.class);
                if (user == null) {
                    return;
                }

                if (userId == user.getId()) {
                    userProfileInfo[0].setText(user.getName());
                    userProfileInfo[1].setText(user.getSurname());

                    final String nickname = DOG_CHARACTER + user.getNickname();
                    userProfileInfo[2].setText(nickname);

                    //TODO: add friends list
                    Controller.setFriendsLength(context, user.getFriendsLength());
                    Controller.setFriends(context, user.getFriends());
                    android.support.v4.app.Fragment fragment = fragmentManager.findFragmentById(R.id.fragmentContainer);

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
    public static void saveCreatedPlace(final Uri uri, final String[] placeInfo, final LatLng position) {
        mDatabaseReference = getDatabaseChild("maxidplaces");
        mDatabaseReference.addListenerForSingleValueEvent(new AbstractValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                final Integer id = dataSnapshot.getValue(Integer.class);

                if (id != null) {
                    Place newPlace = new Place(id, placeInfo[0], placeInfo[1], placeInfo[2], position.latitude, position.longitude);
                    DatabaseReference reference = getDatabaseChild(PLACES_KEY);

                    if (reference != null) {
                        reference.child(id.toString()).setValue(newPlace);
                        mDatabaseReference.setValue(id + 1);
                    }

                    StorageReference child = mStorageRef.child("photos").child(id.toString() + "place_photo");

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
    public static void saveCreatedPlace2(final Bitmap bitmap, final String[] placeInfo, final LatLng position) {
        mDatabaseReference = getDatabaseChild("maxidplaces");
        mDatabaseReference.addListenerForSingleValueEvent(new AbstractValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                final Integer id = dataSnapshot.getValue(Integer.class);

                if (id != null) {
                    Place newPlace = new Place(id, placeInfo[0], placeInfo[1], placeInfo[2], position.latitude, position.longitude);
                    DatabaseReference reference = getDatabaseChild(PLACES_KEY);

                    if (reference != null) {
                        reference.child(id.toString()).setValue(newPlace);
                        mDatabaseReference.setValue(id + 1);
                    }

                    StorageReference child = mStorageRef.child("photos").child(id.toString() + "place_photo");

                    if (bitmap != null) {
                        ByteArrayOutputStream baos = new ByteArrayOutputStream();
                        bitmap.compress(Bitmap.CompressFormat.JPEG, 100, baos);
                        byte[] data = baos.toByteArray();
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
            mStorageRef.child("routes").child(userId).child(userId + "_" + Controller.getRoutesLength(context)).putFile(uri);
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
           reference.child(userId).child("routesLength").setValue(length + 1);
        }
    }

    /**
     * Method gets current number of user routes and adds than to some fragment
     * @param userId used ID
     * @param context current context
     * @param fragmentManager fragment manager for transaction
     * @param fragment output fragment
     */
    public static void getUserRoutesLength(final String userId, final Context context, final FragmentManager fragmentManager, final Fragment fragment) {
        DatabaseReference reference = getDatabaseChild(USERS_KEY);
        if(reference != null) {
            reference.child(userId).child("routesLength").addListenerForSingleValueEvent(new AbstractValueEventListener() {
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
            reference.child(userId).child("routesLength").addListenerForSingleValueEvent(new AbstractValueEventListener() {
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
        mDatabaseReference = getDatabaseChild("maxidplaces");
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

                        StorageReference child = mStorageRef.child("photos").child(id.toString() + "place_photo");

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
    public static void runDescriptionDialog(final Context context, final Marker marker, final LatLng myPosition, final GoogleMap googleMap) {
        DatabaseReference reference = getDatabaseChild(PLACES_KEY);
        if (reference != null) {
            reference.addChildEventListener(new AbstractChildEventListener() {

                @Override
                public void onChildAdded(DataSnapshot dataSnapshot, String s) {
                    Place place = dataSnapshot.getValue(Place.class);
                    if (place != null) {
                        if ((place.getLatitude() == marker.getPosition().latitude) && (place.getLongitude() == marker.getPosition().longitude)) {
                            AlertDialogCreator.createAlertDescriptionDialog(context, place, myPosition, googleMap).show();
                        }
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
            if (ll == null) {
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

        DatabaseReference reference = getDatabaseChild("routes");
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
        DatabaseReference reference = getDatabaseChild("routes_descriptions");
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
        DatabaseReference reference = getDatabaseChild("routes_descriptions");
        if (reference != null) {
            reference.child(userId).child(id.toString()).addListenerForSingleValueEvent(new AbstractValueEventListener() {
                @Override
                public void onDataChange(DataSnapshot dataSnapshot) {
                    String description = "No description given.";

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
                        stringBuilder.append('\n');
                    }
                }
            });
        }
        DatabaseReference referenceDescription = getDatabaseChild(PLACES_KEY);
        if (referenceDescription != null) {
            referenceDescription.child(id).child("description").addListenerForSingleValueEvent(new AbstractValueEventListener() {
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
        mStorageRef.child(USER_AVATAR_KEY).child(userId + "avatar")
                .getDownloadUrl().addOnSuccessListener(uri -> Picasso.with(context).load(uri)
                .placeholder(android.R.drawable.btn_star_big_on)
                .error(android.R.drawable.btn_star_big_on)
                .into(circleImageView));
    }

    /**
     * Method that loads picture of a place
     * @param imageView view where picture should be placed
     * @param place place with all necessary information
     * @param context current context
     */
    public static void loadDescriptionImage(final ImageView imageView, final Place place, final Context context) {
        mStorageRef.child("photos").child(place.getIdAsString() + "place_photo")
                .getDownloadUrl().addOnSuccessListener(uri -> Picasso.with(context).load(uri)
                .placeholder(android.R.drawable.btn_star_big_on)
                .error(android.R.drawable.btn_star_big_on)
                .into(imageView));
    }

    /**
     * Method that loads new avatar of user instead of old one
     * @param userId user id who changes avatar
     * @param uri uri of new avatar image
     */
    public static void setNewAvatar(final String userId, final Uri uri) {
        mStorageRef.child(USER_AVATAR_KEY).child(userId + "avatar").putFile(uri);
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
