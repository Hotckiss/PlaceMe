package placeme.ru.placemedemo.core.database;

import android.content.Context;
import android.net.Uri;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.widget.TextView;

import com.google.android.gms.maps.model.LatLng;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;

import java.util.ArrayList;

import placeme.ru.placemedemo.R;
import placeme.ru.placemedemo.core.Controller;

import static placeme.ru.placemedemo.core.database.DatabaseUtils.getDatabaseChild;

/**
 * Class that have all methods connected with routes in database
 * Created by Андрей on 02.02.2018.
 */

public class DatabaseManagerRoutes {
    private static final String USERS_KEY = "users";
    private static final String ROUTES_KEY = "routes";
    private static final String ROUTES_LENGTH_KEY = "routesLength";
    private static final String ROUTES_DESCRIPTION_KEY = "routes_descriptions";
    private static final String DEFAULT_DESCRIPTION = "No description given.";
    private static final String TUPLE_DELIMITER = ";";
    private static final String POINT_DELIMITER = ":";
    private static final String NAME_DELIMITER = "_";
    private static FirebaseDatabase mBase = FirebaseDatabase.getInstance();

    private static StorageReference mStorageRef  = FirebaseStorage.getInstance().getReference();

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
        DatabaseReference reference = getDatabaseChild(mBase, USERS_KEY);

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
        DatabaseReference reference = getDatabaseChild(mBase, USERS_KEY);

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
        DatabaseReference reference = getDatabaseChild(mBase, USERS_KEY);

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

        DatabaseReference reference = getDatabaseChild(mBase, ROUTES_KEY);

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
        DatabaseReference reference = getDatabaseChild(mBase, ROUTES_DESCRIPTION_KEY);
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
        DatabaseReference reference = getDatabaseChild(mBase, ROUTES_DESCRIPTION_KEY);
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
}