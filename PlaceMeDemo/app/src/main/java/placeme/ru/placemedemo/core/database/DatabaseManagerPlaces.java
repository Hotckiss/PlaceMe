package placeme.ru.placemedemo.core.database;

import android.app.Activity;
import android.content.Context;
import android.graphics.Bitmap;
import android.net.Uri;
import android.os.Build;
import android.support.annotation.RequiresApi;
import android.view.View;
import android.view.WindowManager;
import android.widget.ArrayAdapter;
import android.widget.ImageView;
import android.widget.ProgressBar;
import android.widget.RatingBar;
import android.widget.TextView;

import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.model.BitmapDescriptorFactory;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.Marker;
import com.google.android.gms.maps.model.MarkerOptions;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;
import com.squareup.picasso.Picasso;

import java.util.ArrayList;
import java.util.HashMap;

import placeme.ru.placemedemo.R;
import placeme.ru.placemedemo.elements.Place;
import placeme.ru.placemedemo.ui.dialogs.AlertDialogCreator;

import static placeme.ru.placemedemo.core.database.DatabaseUtils.PLACE_PHOTO_SUFFIX;
import static placeme.ru.placemedemo.core.database.DatabaseUtils.SEPARATOR;
import static placeme.ru.placemedemo.core.database.DatabaseUtils.getDatabaseChild;

/**
 * Class that have all methods connected with places in database
 * it allows to load picture of places and their descriptions
 * in all dialogs that user can see in application
 * Created by Андрей on 01.02.2018.
 */
public class DatabaseManagerPlaces {
    private static final String PLACES_KEY = "places";
    private static final String MARKS_SUM = "sumOfMarks";
    private static final String MARKS_COUNT = "numberOfRatings";
    private static final String MAX_PLACE_ID = "maxidplaces";
    private static final String DESCRIPTION_KEY = "description";
    private static final String PHOTOS_KEY = "photos";
    private static final String PLACE_NAME_KEY = "name";
    private static final char END_LINE = '\n';

    private static FirebaseDatabase mBase = FirebaseDatabase.getInstance();
    private static DatabaseReference mDatabaseReference;
    private static StorageReference mStorageRef = FirebaseStorage.getInstance().getReference();

    /**
     * Method that returns reference to place photo with specific id
     * @param placeId id of place to search
     * @return reference to place photo
     */
    public static StorageReference getFavouritePlaceReference(final String placeId) {
        return mStorageRef.child(PHOTOS_KEY).child(placeId + PLACE_PHOTO_SUFFIX);
    }

    /**
     * Method that searches places in database within query string and lock any actions while loading
     * this makes impossible to build root before loading all places
     * @param arrayAdapter adapter with names of founded places
     * @param places array with founded places
     * @param toFind string which contains user query to search
     * @param myPosition current user positions
     * @param activity UI activity that calls method
     */
    public static void findPlacesByString(final ArrayAdapter<String> arrayAdapter, final ArrayList<Place> places,
                                          final String toFind, final LatLng myPosition,
                                          final Activity activity) {
        activity.getWindow().setFlags(WindowManager.LayoutParams.FLAG_NOT_TOUCHABLE,
                WindowManager.LayoutParams.FLAG_NOT_TOUCHABLE);
        ProgressBar progressBar = new ProgressBar(activity);
        progressBar.setVisibility(View.VISIBLE);
        DatabaseReference databaseReference = getDatabaseChild(mBase, PLACES_KEY);

        databaseReference.addChildEventListener(new AbstractChildEventListener() {
            @Override
            public void onChildAdded(DataSnapshot dataSnapshot, String s) {
                Place place = dataSnapshot.getValue(Place.class);
                if (place != null && DatabaseUtils.checkAccess(place, myPosition, activity) &&
                        DatabaseUtils.isAppropriate(place, toFind)) {
                    DatabaseUtils.addPlaceToList(arrayAdapter, places, place);
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
        mDatabaseReference = DatabaseUtils.getDatabaseChild(mBase, PLACES_KEY + SEPARATOR + placeId);

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
     * Method that allows to save created place in database
     * @param uri picture uri
     * @param placeInfo text description of the place
     * @param position place coordinates
     */
    public static void saveCreatedPlace(final Uri uri, final Place placeInfo, final LatLng position) {
        mDatabaseReference = DatabaseUtils.getDatabaseChild(mBase, MAX_PLACE_ID);
        mDatabaseReference.addListenerForSingleValueEvent(new AbstractValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                final Integer id = dataSnapshot.getValue(Integer.class);

                if (id != null) {
                    if (uploadPlaceInfo(placeInfo, id, position)) {
                        mDatabaseReference.setValue(id + 1);
                    }

                    DatabaseUtils.uploadPicture(uri, mStorageRef, id);
                }
            }
        });
    }

    /**
     * Method that loads place that was converted from Google Places to database
     * @param uri picture uri
     * @param place place to save
     */
    public static void saveConvertedPlace(final Uri uri, final Place place) {
        mDatabaseReference = DatabaseUtils.getDatabaseChild(mBase, MAX_PLACE_ID);
        mDatabaseReference.addListenerForSingleValueEvent(new AbstractValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                final Integer id = dataSnapshot.getValue(Integer.class);
                if (id != null) {
                    place.setId(id);

                    DatabaseReference reference = DatabaseUtils.getDatabaseChild(mBase, PLACES_KEY);
                    if (reference != null) {
                        reference.child(id.toString()).setValue(place);
                        mDatabaseReference.setValue(id + 1);
                        DatabaseUtils.uploadPicture(uri, mStorageRef, id);
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
        mDatabaseReference = DatabaseUtils.getDatabaseChild(mBase, MAX_PLACE_ID);
        mDatabaseReference.addListenerForSingleValueEvent(new AbstractValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                final Integer id = dataSnapshot.getValue(Integer.class);

                if (id != null) {
                    if (uploadPlaceInfo(placeInfo, id, position)) {
                        mDatabaseReference.setValue(id + 1);
                    }

                    DatabaseUtils.uploadBitmap(bitmap, mStorageRef, id);
                }
            }
        });
    }

    /**
     * Method that runs alert dialog with full place description
     * Place loaded from database
     * @param activity current activity
     * @param marker place position
     */
    public static void runDescriptionDialog(final Activity activity, final Marker marker,
                                            final LatLng myPosition, final GoogleMap googleMap) {
        DatabaseReference reference = DatabaseUtils.getDatabaseChild(mBase, PLACES_KEY);

        if (reference != null) {
            reference.addChildEventListener(new AbstractChildEventListener() {
                @RequiresApi(api = Build.VERSION_CODES.M)
                @Override
                public void onChildAdded(DataSnapshot dataSnapshot, String s) {
                    Place place = dataSnapshot.getValue(Place.class);
                    if (place != null && (place.getLatitude() == marker.getPosition().latitude) &&
                            (place.getLongitude() == marker.getPosition().longitude)) {
                        AlertDialogCreator.createAlertDescriptionDialog(activity, place, myPosition, googleMap).show();
                    }
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
        DatabaseReference reference = DatabaseUtils.getDatabaseChild(mBase, PLACES_KEY + SEPARATOR + id);

        if (reference != null) {
            reference.child(PLACE_NAME_KEY).addListenerForSingleValueEvent(new AbstractValueEventListener() {
                @Override
                public void onDataChange(DataSnapshot dataSnapshot) {
                    if (dataSnapshot != null) {
                        stringBuilder.append((String) dataSnapshot.getValue());
                        stringBuilder.append(END_LINE);
                    }
                }
            });
            reference.child(DESCRIPTION_KEY).addListenerForSingleValueEvent(new AbstractValueEventListener() {
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
     * Method that loads picture of a place
     * @param imageView view where picture should be placed
     * @param place place with all necessary information
     * @param context current context
     */
    public static void loadDescriptionImage(final ImageView imageView, final Place place, final Context context) {
        mStorageRef.child(PHOTOS_KEY + SEPARATOR + place.getIdAsString() + PLACE_PHOTO_SUFFIX)
                .getDownloadUrl().addOnSuccessListener(uri -> Picasso.with(context).load(uri)
                .placeholder(R.drawable.noimage)
                .error(R.drawable.noimage)
                .into(imageView));
    }

    /**
     * Method that loads all markers from database to the map
     * @param googleMap markers destination map
     */
    public static void loadMarkersToMap(final GoogleMap googleMap) {
        mDatabaseReference = DatabaseUtils.getDatabaseChild(mBase, PLACES_KEY);
        mDatabaseReference.addChildEventListener(new AbstractChildEventListener() {
            @Override
            public void onChildAdded(DataSnapshot dataSnapshot, String s) {
                Place place = dataSnapshot.getValue(Place.class);
                if (place != null) {
                    googleMap.addMarker(new MarkerOptions()
                            .position(new LatLng(place.getLatitude(), place.getLongitude()))
                            .title(place.getName()).icon(BitmapDescriptorFactory.fromResource(R.drawable.marker7)));
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
        mDatabaseReference = DatabaseUtils.getDatabaseChild(mBase, PLACES_KEY);
        mDatabaseReference.addChildEventListener(new AbstractChildEventListener() {
            @Override
            public void onChildAdded(DataSnapshot dataSnapshot, String s) {
                Place place = dataSnapshot.getValue(Place.class);
                if (place != null && DatabaseUtils.isAppropriate(place, query)) {
                    googleMap.addMarker(new MarkerOptions()
                            .position(new LatLng(place.getLatitude(), place.getLongitude()))
                            .title(place.getName()).icon(BitmapDescriptorFactory.fromResource(R.drawable.marker7)));
                }
            }
        });
    }

    private static boolean uploadPlaceInfo(final Place placeInfo, final Integer placeId, final LatLng position) {
        Place newPlace = new Place(placeId, placeInfo.getName(), placeInfo.getDescription(), placeInfo.getTags(),
                position.latitude, position.longitude);
        DatabaseReference reference = DatabaseUtils.getDatabaseChild(mBase, PLACES_KEY);

        if (reference != null) {
            reference.child(placeId.toString()).setValue(newPlace);
            return true;
        }

        return false;
    }
}
