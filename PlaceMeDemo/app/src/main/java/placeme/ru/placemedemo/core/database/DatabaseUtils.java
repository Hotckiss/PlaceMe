package placeme.ru.placemedemo.core.database;

import android.app.Activity;
import android.content.Context;
import android.graphics.Bitmap;
import android.net.Uri;
import android.support.annotation.Nullable;
import android.widget.ArrayAdapter;
import android.widget.ImageView;

import com.google.android.gms.maps.model.LatLng;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.storage.StorageReference;
import com.squareup.picasso.Picasso;

import java.io.ByteArrayOutputStream;
import java.util.ArrayList;
import java.util.Calendar;

import placeme.ru.placemedemo.R;
import placeme.ru.placemedemo.core.Controller;
import placeme.ru.placemedemo.elements.Place;

/**
 * Class that contains common utils to database managers
 * Created by Андрей on 01.02.2018.
 */
public class DatabaseUtils {
    public static final String PLACE_PHOTO_SUFFIX = "place_photo";
    private static final String DATABASE_DELIMITER = ",";
    private static final String PLACES_KEY = "places";
    private static final String SPACE_DELIMITER = " ";
    private static final String DASH_DELIMITER = "-";
    private static final double PERCENT_TO_RATING = 20.0;

    public static void loadFavouritePicture(final StorageReference storageReference,
                                            final ImageView imageView, final Activity activity) {
        if (activity != null) {
            storageReference.getDownloadUrl().addOnSuccessListener(uri -> Picasso.with(activity.getBaseContext()).load(uri)
                    .placeholder(R.drawable.grey)
                    .error(R.drawable.noimage)
                    .into(imageView));
        }
    }

    /**
     * method that checks place with current search parameters
     * @param place place to check
     * @param myPosition current user position
     * @param context current context
     * @return true if place is allowed with current settings false otherwise
     */
    public static boolean checkAccess(final Place place, final LatLng myPosition, final Context context) {
        boolean distanceEnabled = Controller.getDistanceSearchStatus(context);
        boolean ratingEnabled = Controller.getRatingSearchStatus(context);
        boolean distanceAccess = (!distanceEnabled) ||
                (Controller.getKilometers(myPosition, new LatLng(place.getLatitude(), place.getLongitude())) <=
                        Controller.getDistanceSearchValue(context));
        boolean ratingAccess = (!ratingEnabled) ||
                (place.getMark() > (Controller.getRatingSearchValue(context) / PERCENT_TO_RATING));

        return distanceAccess && ratingAccess;
    }

    /**
     * Method checks that place is good for current search query
     * @param place place to check
     * @param toFind query
     * @return true if place is matches to query false otherwise
     */
    public static boolean isAppropriate(final Place place, final String toFind) {
        return containsIgnoreCase(place.getName(), toFind) || containsTag(place, toFind);
    }

    /**
     * Method that adds place to UI adapters
     * @param arrayAdapter adapter where place should be added
     * @param places array list with all added places
     * @param place place to add
     */
    public static void addPlaceToList(final ArrayAdapter<String> arrayAdapter, final ArrayList<Place> places, final Place place) {
        arrayAdapter.add(place.getName());
        places.add(place);
    }

    /**
     * Method checks if place is already added to the list of favourite places
     * @param places already added places
     * @param favouritePlaceId place to add
     * @return true if place was already added false otherwise
     */
    public static boolean isAlreadyFavourite(final String places, final String favouritePlaceId) {
        for (String placeId : places.split(DATABASE_DELIMITER)) {
            if (placeId.equals(favouritePlaceId)) {
                return true;
            }
        }
        return false;
    }

    /**
     * Method that validates plan date with current date
     * @param planDate plan to validate
     * @return true if plan is valid false otherwise
     */
    public static boolean validatePlan(final String planDate) {
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

    /**
     * Method that uploads picture with specific uri to database
     * @param uri picture uri
     * @param storageReference reference to database storage
     * @param placeId id of place that have this picture
     */
    public static void uploadPicture(final Uri uri, final StorageReference storageReference, final Integer placeId) {
        StorageReference child = storageReference.child(PLACES_KEY).child(placeId.toString() + PLACE_PHOTO_SUFFIX);

        if (uri != null) {
            child.putFile(uri);
        }
    }

    /**
     * Method that uploads picture with specific bitmap to database
     * @param bitmap picture bitmap
     * @param storageReference reference to database storage
     * @param placeId id of place that have this picture
     */
    public static void uploadBitmap(final Bitmap bitmap, final StorageReference storageReference, final Integer placeId) {
        StorageReference child = storageReference.child(PLACES_KEY).child(placeId.toString() + PLACE_PHOTO_SUFFIX);

        if (bitmap != null) {
            ByteArrayOutputStream byteArrayOutputStream = new ByteArrayOutputStream();
            bitmap.compress(Bitmap.CompressFormat.JPEG, 100, byteArrayOutputStream);
            byte[] data = byteArrayOutputStream.toByteArray();
            child.putBytes(data);
        }
    }

    /**
     * Method that returns database child of any firebase database
     * @param database database where child should be extracted
     * @param childName name of database child
     * @return database reference for child if it exists null otherwise
     */
    @Nullable
    public static DatabaseReference getDatabaseChild(FirebaseDatabase database, String childName) {
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
