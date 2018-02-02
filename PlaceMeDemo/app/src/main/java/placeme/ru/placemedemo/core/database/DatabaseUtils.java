package placeme.ru.placemedemo.core.database;

import android.content.Context;
import android.graphics.Bitmap;
import android.net.Uri;
import android.support.annotation.Nullable;
import android.widget.ArrayAdapter;

import com.google.android.gms.maps.model.LatLng;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.storage.StorageReference;

import java.io.ByteArrayOutputStream;
import java.util.ArrayList;
import java.util.Calendar;

import placeme.ru.placemedemo.core.Controller;
import placeme.ru.placemedemo.elements.Place;

/**
 * Class that contains common utils to database managers
 * Created by Андрей on 01.02.2018.
 */
class DatabaseUtils {
    static final String PLACE_PHOTO_SUFFIX = "place_photo";
    private static final String DATABASE_DELIMITER = ",";
    private static final String PLACES_KEY = "places";
    private static final String SPACE_DELIMITER = " ";
    private static final String DASH_DELIMITER = "-";
    private static final double PERCENT_TO_RATING = 20.0;

    static boolean checkAccess(final Place place, final LatLng myPosition, final Context context) {
        boolean distanceEnabled = Controller.getDistanceSearchStatus(context);
        boolean ratingEnabled = Controller.getRatingSearchStatus(context);
        boolean distanceAccess = (!distanceEnabled) ||
                (Controller.getKilometers(myPosition, new LatLng(place.getLatitude(), place.getLongitude())) <= Controller.getDistanceSearchValue(context));
        boolean ratingAccess = (!ratingEnabled) ||
                (place.getMark() > (Controller.getRatingSearchValue(context) / PERCENT_TO_RATING));

        return distanceAccess && ratingAccess;
    }

    static boolean isAppropriate(final Place place, final String toFind) {
        return containsIgnoreCase(place.getName(), toFind) || containsTag(place, toFind);
    }

    static void addPlaceToList(final ArrayAdapter<String> arrayAdapter, final ArrayList<Place> places, final Place place) {
        arrayAdapter.add(place.getName());
        places.add(place);
    }

    static boolean isAlreadyFavourite(final String places, final String favouritePlaceId) {
        for (String placeId : places.split(DATABASE_DELIMITER)) {
            if (placeId.equals(favouritePlaceId)) {
                return true;
            }
        }
        return false;
    }

    static boolean validatePlan(final String planDate) {
        Calendar calendar = Calendar.getInstance();
        String[] dateTime = planDate.split(SPACE_DELIMITER);
        String[] data = dateTime[0].split(DASH_DELIMITER);

        if (calendar.get(Calendar.YEAR) > Integer.parseInt(data[2])) {
            return false;
        } else if (calendar.get(Calendar.YEAR) == Integer.parseInt(data[2])) {
            if ((calendar.get(Calendar.MONTH) + 1) > Integer.parseInt(data[1])) {
                return false;
            } else if ((calendar.get(Calendar.MONTH) + 1) == Integer.parseInt(data[1])) {
                return calendar.get(Calendar.DAY_OF_MONTH) <= Integer.parseInt(data[0]);
            }
        }
        return true;
    }

    static void uploadPicture(final Uri uri, final StorageReference storageReference, final Integer placeId) {
        StorageReference child = storageReference.child(PLACES_KEY).child(placeId.toString() + PLACE_PHOTO_SUFFIX);

        if (uri != null) {
            child.putFile(uri);
        }
    }

    static void uploadBitmap(final Bitmap bitmap, final StorageReference storageReference, final Integer placeId) {
        StorageReference child = storageReference.child(PLACES_KEY).child(placeId.toString() + PLACE_PHOTO_SUFFIX);

        if (bitmap != null) {
            ByteArrayOutputStream byteArrayOutputStream = new ByteArrayOutputStream();
            bitmap.compress(Bitmap.CompressFormat.JPEG, 100, byteArrayOutputStream);
            byte[] data = byteArrayOutputStream.toByteArray();
            child.putBytes(data);
        }
    }

    @Nullable
    static DatabaseReference getDatabaseChild(FirebaseDatabase database, String childName) {
        DatabaseReference databaseReference = getDatabaseReference(database);
        if (databaseReference != null) {
            return databaseReference.child(childName);
        }

        return null;
    }

    private static boolean containsIgnoreCase(final String text, final String word) {
        return text.toLowerCase().contains(word.toLowerCase());
    }

    private static boolean containsTag(final Place place, final String tagToSearch) {
        for (String tag : place.getTags().split(DATABASE_DELIMITER)) {
            if (tagToSearch.equalsIgnoreCase(tag)) {
                return true;
            }
        }
        return false;
    }

    @Nullable
    private static DatabaseReference getDatabaseReference(FirebaseDatabase database) {
        if (database != null) {
            return database.getReference();
        }

        return null;
    }
}
