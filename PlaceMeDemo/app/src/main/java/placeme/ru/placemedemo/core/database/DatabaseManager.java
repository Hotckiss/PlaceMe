package placeme.ru.placemedemo.core.database;

import android.content.Context;
import android.net.Uri;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.widget.ArrayAdapter;
import android.widget.TextView;

import com.google.android.gms.maps.model.LatLng;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;
import com.squareup.picasso.Picasso;

import java.util.ArrayList;

import de.hdodenhof.circleimageview.CircleImageView;
import placeme.ru.placemedemo.R;
import placeme.ru.placemedemo.core.Controller;

/**
 * Class that have all methods connected with working in database
 * Created by Андрей on 21.12.2017.
 */
public class DatabaseManager {
    private static final String USERS_KEY = "users";
    private static final String USER_AVATAR_KEY = "avatars";
    private static final String AVATAR_SUFFIX = "avatar";
    private static final String ROUTES_KEY = "routes";
    private static final String ROUTES_LENGTH_KEY = "routesLength";
    private static final String REVIEWS_KEY = "reviews";
    private static final String PLANS_KEY = "plans";
    private static final String ROUTES_DESCRIPTION_KEY = "routes_descriptions";
    private static final String DEFAULT_DESCRIPTION = "No description given.";
    private static final String TUPLE_DELIMITER = ";";
    private static final String POINT_DELIMITER = ":";
    private static final String NAME_DELIMITER = "_";
    private static final String END_DELIMITER = "\n";
    private static final char END_LINE = '\n';

    private static FirebaseDatabase mBase;

    private static StorageReference mStorageRef;

    static {
        mBase = FirebaseDatabase.getInstance();
        mStorageRef = FirebaseStorage.getInstance().getReference();
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
