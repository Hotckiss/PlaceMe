package placeme.ru.placemedemo.core.database;

import android.app.Activity;
import android.content.Context;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.view.View;
import android.view.WindowManager;
import android.widget.ArrayAdapter;
import android.widget.ProgressBar;

import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;

import java.util.ArrayList;
import java.util.HashMap;

import placeme.ru.placemedemo.R;
import placeme.ru.placemedemo.core.Controller;
import placeme.ru.placemedemo.elements.AuthData;
import placeme.ru.placemedemo.elements.User;
import placeme.ru.placemedemo.elements.UserDataFields;
import placeme.ru.placemedemo.elements.UserProfileFields;
import placeme.ru.placemedemo.ui.views.HorizontalListViewFragment;

import static placeme.ru.placemedemo.core.database.DatabaseUtils.getDatabaseChild;

/**
 * Class that have all methods connected with working with users in database
 * Created by Андрей on 02.02.2018.
 */
public class DatabaseManagerUsers {
    private static final String USERS_KEY = "users";
    private static final String AUTH_DATA_KEY = "authdata";
    private static final String MAX_ID = "maxid";
    private static final String USER_NAME_KEY = "name";
    private static final String USER_SURNAME_KEY = "surname";
    private static final String USER_NICKNAME_KEY = "nickname";
    private static final String USER_LOGIN_KEY = "login";
    private static final String USER_PASSWORD_KEY = "password";
    private static final String FRIENDS_KEY = "friends";
    private static final String FRIENDS_LENGTH_KEY = "friendsLength";
    private static final String DOG_CHARACTER = "@";
    private static final String DATABASE_DELIMITER = ",";
    private static final String FAVOURITE_PLACES_TAG = "favouritePlaces";

    private static FirebaseDatabase mBase = FirebaseDatabase.getInstance();;
    private static DatabaseReference mDatabaseReference;

    /**
     * Method that register new user in database with unique id
     * @param newAuthData authentication data that user input during registration
     * @param newUserData user data that user input during registration
     */
    public static void registerUser(final AuthData newAuthData, final User newUserData) {
        DatabaseReference databaseReference = getDatabaseChild(mBase, MAX_ID);

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
                    DatabaseReference referenceAuth = getDatabaseChild(mBase, AUTH_DATA_KEY);
                    DatabaseReference referenceUsers = getDatabaseChild(mBase, USERS_KEY);

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
     * Method that searches users in database within query string
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
        mDatabaseReference = getDatabaseChild(mBase, USERS_KEY);
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
        DatabaseReference mDatabaseReferenceUser = getDatabaseChild(mBase, USERS_KEY);
        DatabaseReference mDatabaseReferenceAuth = getDatabaseChild(mBase, AUTH_DATA_KEY);

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
        mDatabaseReference = getDatabaseChild(mBase, USERS_KEY);
        if (mDatabaseReference != null) {
            mDatabaseReference = mDatabaseReference.child(userId);
        }

        mDatabaseReference.addListenerForSingleValueEvent(new AbstractValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                DatabaseReference referenceAuth = getDatabaseChild(mBase, AUTH_DATA_KEY);
                DatabaseReference referenceUsers = getDatabaseChild(mBase, USERS_KEY);

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
        mDatabaseReference = getDatabaseChild(mBase, USERS_KEY);
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
        mDatabaseReference = getDatabaseChild(mBase, USERS_KEY);

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
     * Method that adds friend to users friend list if it wasn't already added
     * @param userId id of user who wants to add new friend
     * @param friendId id of friend to be added
     */
    public static void addFriend(final String userId, final String friendId) {
        DatabaseReference reference = getDatabaseChild(mBase, USERS_KEY);
        if (reference != null) {
            reference = reference.child(userId).child(FRIENDS_KEY);
            reference.addListenerForSingleValueEvent(new AbstractValueEventListener() {
                @Override
                public void onDataChange(DataSnapshot dataSnapshot) {
                    final String friends = dataSnapshot.getValue(String.class);
                    if (friends.length() == 0) {
                        DatabaseReference referenceFriends = getDatabaseChild(mBase, USERS_KEY);
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
                            DatabaseReference referenceFriends = getDatabaseChild(mBase, USERS_KEY);
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
}
