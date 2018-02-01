package placeme.ru.placemedemo.core.database;

import android.content.Context;
import android.support.annotation.Nullable;
import android.widget.ArrayAdapter;

import com.google.android.gms.maps.model.LatLng;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;

import java.util.ArrayList;
import java.util.Calendar;

import placeme.ru.placemedemo.core.Controller;
import placeme.ru.placemedemo.elements.Place;

/**
 * Created by Андрей on 01.02.2018.
 */

public class DatabaseUtils {
    public static final String DATABASE_DELIMITER = ",";
    private static final String SPACE_DELIMITER = " ";
    private static final String DASH_DELIMITER = "-";
    private static final double PERCENT_TO_RATING = 20.0;

    public static boolean checkAccess(final Place place, final LatLng myPosition, final Context context) {
        boolean distanceEnabled = Controller.getDistanceSearchStatus(context);
        boolean ratingEnabled = Controller.getRatingSearchStatus(context);
        boolean distanceAccess = (!distanceEnabled) ||
                (Controller.getKilometers(myPosition, new LatLng(place.getLatitude(), place.getLongitude())) <= Controller.getDistanceSearchValue(context));
        boolean ratingAccess = (!ratingEnabled) ||
                (place.getMark() > (Controller.getRatingSearchValue(context) / PERCENT_TO_RATING));

        return distanceAccess && ratingAccess;
    }

    public static boolean isAppropriate(final Place place, final String toFind) {
        return containsIgnoreCase(place.getName(), toFind) || containsTag(place, toFind);
    }

    public static boolean containsIgnoreCase(final String text, final String word) {
        return text.toLowerCase().contains(word.toLowerCase());
    }

    public static boolean containsTag(final Place place, final String tagToSearch) {
        for (String tag : place.getTags().split(DATABASE_DELIMITER)) {
            if (tagToSearch.equalsIgnoreCase(tag)) {
                return true;
            }
        }
        return false;
    }

    public static void addPlaceToList(final ArrayAdapter<String> arrayAdapter, final ArrayList<Place> places, final Place place) {
        arrayAdapter.add(place.getName());
        places.add(place);
    }

    public static boolean isAlreadyFavourite(final String places, final String favouritePlaceId) {
        for (String placeId : places.split(DATABASE_DELIMITER)) {
            if (placeId.equals(favouritePlaceId)) {
                return true;
            }
        }
        return false;
    }

    public static boolean validatePlan(final String planDate) {
        Calendar calendar = Calendar.getInstance();
        String[] dateTime = planDate.split(SPACE_DELIMITER);
        String[] data = dateTime[0].split(DASH_DELIMITER);

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
    public static DatabaseReference getDatabaseChild(FirebaseDatabase database, String childName) {
        DatabaseReference databaseReference = getDatabaseReference(database);
        if (databaseReference != null) {
            return databaseReference.child(childName);
        }

        return null;
    }

    @Nullable
    public static DatabaseReference getDatabaseReference(FirebaseDatabase database) {
        if (database != null) {
            return database.getReference();
        }

        return null;
    }
}
