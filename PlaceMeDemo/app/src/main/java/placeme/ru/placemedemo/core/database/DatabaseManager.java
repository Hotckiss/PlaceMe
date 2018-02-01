package placeme.ru.placemedemo.core.database;

import android.app.Activity;
import android.content.Context;
import android.net.Uri;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.view.View;
import android.view.WindowManager;
import android.widget.ArrayAdapter;
import android.widget.ProgressBar;
import android.widget.TextView;

import com.google.android.gms.maps.model.LatLng;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;
import com.squareup.picasso.Picasso;

import java.util.ArrayList;
import java.util.HashMap;

import de.hdodenhof.circleimageview.CircleImageView;
import placeme.ru.placemedemo.R;
import placeme.ru.placemedemo.core.Controller;
import placeme.ru.placemedemo.elements.AuthData;
import placeme.ru.placemedemo.elements.User;
import placeme.ru.placemedemo.elements.UserDataFields;
import placeme.ru.placemedemo.elements.UserProfileFields;
import placeme.ru.placemedemo.ui.views.HorizontalListViewFragment;

/**
 * Class that have all methods connected with working with the database
 * Created by Андрей on 21.12.2017.
 */
public class DatabaseManager {
    private static final String USERS_KEY = "users";
    private static final String AUTH_DATA_KEY = "authdata";
    private static final String MAX_ID = "maxid";
    private static final String USER_NAME_KEY = "name";
    private static final String USER_SURNAME_KEY = "surname";
    private static final String USER_NICKNAME_KEY = "nickname";
    private static final String USER_LOGIN_KEY = "login";
    private static final String USER_PASSWORD_KEY = "password";
    private static final String USER_AVATAR_KEY = "avatars";
    private static final String AVATAR_SUFFIX = "avatar";
    private static final String ROUTES_KEY = "routes";
    private static final String ROUTES_LENGTH_KEY = "routesLength";
    private static final String FRIENDS_KEY = "friends";
    private static final String FRIENDS_LENGTH_KEY = "friendsLength";
    private static final String REVIEWS_KEY = "reviews";
    private static final String PLANS_KEY = "plans";
    private static final String ROUTES_DESCRIPTION_KEY = "routes_descriptions";
    private static final String DEFAULT_DESCRIPTION = "No description given.";
    private static final String DOG_CHARACTER = "@";
    private static final String DATABASE_DELIMITER = ",";
    private static final String TUPLE_DELIMITER = ";";
    private static final String POINT_DELIMITER = ":";
    private static final String NAME_DELIMITER = "_";
    private static final String END_DELIMITER = "\n";
    private static final String FAVOURITE_PLACES_TAG = "favouritePlaces";
    private static final char END_LINE = '\n';

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
                    } else if (!DatabaseUtils.isAlreadyFavourite(places, placeId)) {
                        mDatabaseReference.setValue(places + DATABASE_DELIMITER + placeId);
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
     * Method that saves map screenshot with route to database
     * @param uri screenshot uri
     * @param userId user ID
     * @param context current context
     */
    public static void saveRoute(final Uri uri, final String userId, final Context context) {
        if (uri != null) {
            mStorageRef.child(ROUTES_KEY).child(userId)
                    .child(userId + NAME_DELIMITER + Controller.getRoutesLength(context)).putFile(uri);
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
     * Method that loads route to database as sequence of points
     * @param userId user ID who owns this route
     * @param route route to save
     */
    public static void saveRoute(final String userId, final ArrayList<LatLng> route) {
        StringBuilder routeString = new StringBuilder();
        for (LatLng ll : route) {
            if (ll != null) {
                routeString.append(ll.latitude);
                routeString.append(TUPLE_DELIMITER);
                routeString.append(ll.longitude);
                routeString.append(POINT_DELIMITER);
            }
        }

        if (route.size() > 0) {
            routeString.deleteCharAt(routeString.lastIndexOf(POINT_DELIMITER));
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
                        int length = friends.split(DATABASE_DELIMITER).length;
                        boolean alreadyAdded = false;
                        for (String friend : friends.split(DATABASE_DELIMITER)) {
                            if (friend.equals(friendId)) {
                                alreadyAdded = true;
                            }
                        }
                        if (!alreadyAdded) {
                            DatabaseReference referenceFriends = getDatabaseChild(USERS_KEY);
                            if (referenceFriends != null) {
                                referenceFriends.child(userId).child(FRIENDS_KEY).setValue(friends + DATABASE_DELIMITER + friendId);
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
                            referencePlan.setValue(currentPlan + TUPLE_DELIMITER + placeName + END_LINE + date);
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
                            String[] plans = currentPlan.split(TUPLE_DELIMITER);
                            for (String planSingle : plans) {
                                String[] splitted = planSingle.split(END_DELIMITER);
                                if (splitted.length != 0 && DatabaseUtils.validatePlan(splitted[splitted.length - 1])) {
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
                                referenceNewReviews.setValue(allReviews + TUPLE_DELIMITER + review);
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
                        String[] arrayReviews = allReviews.split(TUPLE_DELIMITER);

                        for (String singleReview : arrayReviews) {
                            adapter.add(singleReview);
                        }
                    }
                }
            });
        }
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
