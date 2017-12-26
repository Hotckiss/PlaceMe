package placeme.ru.placemedemo.core;

import android.app.Activity;
import android.content.Context;
import android.graphics.Bitmap;
import android.media.MediaScannerConnection;
import android.net.Uri;
import android.os.Environment;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.widget.ArrayAdapter;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.RatingBar;
import android.widget.TextView;

import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.Marker;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.util.ArrayList;

import de.hdodenhof.circleimageview.CircleImageView;
import placeme.ru.placemedemo.R;
import placeme.ru.placemedemo.core.database.DatabaseManager;
import placeme.ru.placemedemo.core.map.MapManager;
import placeme.ru.placemedemo.core.utils.AuthorizationUtils;
import placeme.ru.placemedemo.core.utils.ChatUtils;
import placeme.ru.placemedemo.core.utils.FavouritePlacesUtils;
import placeme.ru.placemedemo.core.utils.FriendsDataUtils;
import placeme.ru.placemedemo.core.utils.RoutesUtils;
import placeme.ru.placemedemo.elements.Place;
import placeme.ru.placemedemo.ui.MainActivity;

/**
 * Created by Андрей on 25.12.2017.
 */

//TODO: make all here
public class Controller {

    /**
     * Method that check existence of the user in the database
     * @param context current context
     * @param email user email
     * @param password user password
     */
    public static void findUserAndCheckPassword(final Context context, final String email, final String password) {
        DatabaseManager.findUserAndCheckPassword(context, email, password);
    }

    /**
     * Method that register new user in database with unique id
     * @param context current context
     * @param information information that user input during registration
     */
    public static void registerUser(final Context context, final String[] information) {
        DatabaseManager.registerUser(context, information);
    }

    /**
     * Method that searches places in database within query string
     * @param arrayAdapter adapter with names of founded places
     * @param places array with founded places
     * @param toFind string which contains user query to search
     */
    public static void findPlacesByString(final ArrayAdapter<String> arrayAdapter, final ArrayList<Place> places, final String toFind) {
        DatabaseManager.findPlacesByString(arrayAdapter, places, toFind);
    }

    /**
     * Method that updates place rating after user voting
     * and immediately updates rating bar in UI
     * @param ratingBar rating bar to update
     * @param placeId place that user have rated
     */
    public static void updatePlaceRating(final RatingBar ratingBar, final String placeId) {
        DatabaseManager.updatePlaceRating(ratingBar, placeId);
    }

    /**
     * Method that adds place to the list of favourite places of the user
     * place will not be added if it is already in the favourite list
     * @param userId id of the user who wants to add place to favourite
     * @param placeId id of place that should be added
     */
    public static void addPlaceToFavourite(final String userId, final String placeId) {
       DatabaseManager.addPlaceToFavourite(userId, placeId);
    }

    /**
     * Method that loads all markers from database to the map
     * @param googleMap markers destination map
     */
    public static void loadMarkersToMap(final GoogleMap googleMap) {
        DatabaseManager.loadMarkersToMap(googleMap);
    }

    /**
     * Method that loads markers founded by query from database to the map
     * @param googleMap markers destination map
     * @param query user search query
     */
    public static void addMarkersByQuery(final GoogleMap googleMap, final String query) {
        DatabaseManager.addMarkersByQuery(googleMap, query);
    }

    /**
     * Method that loads user information from the database to the edit fields
     * @param toLoad array of fields to init
     * @param userId user id to search in database
     */
    public static void loadUserDataForEdit(final EditText[] toLoad, String userId) {
        DatabaseManager.loadUserDataForEdit(toLoad, userId);
    }

    /**
     * Method that saves changed user data
     * @param userId user id
     * @param information new user information
     */
    public static void saveProfileChanges(final String userId, final String[] information) {
        DatabaseManager.saveProfileChanges(userId, information);
    }

    /**
     * Method that allows to load user favorite places from database to array adapter
     * @param userId user id
     * @param adapter adapter to put places
     */
    @Deprecated
    public static void loadUserFavouritePlacesList(final String userId, ArrayAdapter<String> adapter) {
        DatabaseManager.loadUserFavouritePlacesList(userId, adapter);
    }

    /**
     * Method that allows to load user favorite places from database to fragment
     * @param userId user id
     * @param context current context
     * @param fragmentManager fragment managet for transaction
     * @param fragment output fragment
     */
    public static void loadUserFavouritePlacesListNew(final String userId, final Context context, final FragmentManager fragmentManager, final Fragment fragment) {
        DatabaseManager.loadUserFavouritePlacesListNew(userId, context, fragmentManager, fragment);
    }

    /**
     * Method that loads all user profile information and initializes list of friends
     * @param context current context
     * @param userId user id
     * @param userProfileInfo fields with user profile information
     * @param fragmentManager fragment manager to load friends list
     */
    public static void loadUserProfile(final Context context, final int userId, final TextView[] userProfileInfo, final FragmentManager fragmentManager) {
        DatabaseManager.loadUserProfile(context, userId, userProfileInfo, fragmentManager);
    }

    /**
     * Method that allows to save created place in database
     * @param uri picture uri
     * @param placeInfo text description of the place
     * @param position place coordinates
     */
    public static void saveCreatedPlace(final Uri uri, final String[] placeInfo, final LatLng position) {
        DatabaseManager.saveCreatedPlace(uri, placeInfo, position);
    }

    /**
     * Method that saves map screenshot with route to database
     * @param uri screenshot uri
     * @param userId user ID
     * @param context current context
     */
    public static void saveRoute(final Uri uri, final String userId, final Context context) {
        DatabaseManager.saveRoute(uri, userId, context);
    }

    /**
     * Method that called after adding new route, that means that ID of next route will be other
     * @param userId user ID
     * @param length current last added route ID
     */
    public static void updateRoutesLength(final String userId, final long length) {
        DatabaseManager.updateRoutesLength(userId, length);
    }

    /**
     * Method gets current number of user routes and adds than to some fragment
     * @param userId used ID
     * @param context current context
     * @param fragmentManager fragment manager for transaction
     * @param fragment output fragment
     */
    public static void getUserRoutesLength(final String userId, final Context context, final FragmentManager fragmentManager, final Fragment fragment) {
        DatabaseManager.getUserRoutesLength(userId, context, fragmentManager, fragment);
    }

    /**
     * Method gets current number of user routes without adding them to the fragment
     * @param userId used ID
     * @param context current context
     */
    public static void getUserRoutesLength2(final String userId, final Context context) {
        DatabaseManager.getUserRoutesLength2(userId, context);
    }

    /**
     * Method that loads place that was converted from Google Places to database
     * @param uri picture uri
     * @param place place to save
     */
    public static void saveConvertedPlace(final Uri uri, final Place place) {
       DatabaseManager.saveConvertedPlace(uri, place);
    }

    /**
     * Method that runs alert dialog with full place description
     * Place loaded from database
     * @param context current context
     * @param marker place position
     */
    public static void runDescriptionDialog(final Context context, final Marker marker, final LatLng myPosition, final GoogleMap googleMap) {
        DatabaseManager.runDescriptionDialog(context, marker, myPosition, googleMap);
    }


    /**
     * Method that loads route to database as sequence of points
     * @param userId user ID who owns this route
     * @param route route to save
     */
    public static void saveRoute(final String userId, final ArrayList<LatLng> route) {
        DatabaseManager.saveRoute(userId, route);
    }

    /**
     * Method that saves route description to database
     * @param userId user Id who owns this route
     * @param descriptionId ID of route that has this description
     * @param description string that contains all description
     */
    public static void saveRouteInfo(final String userId, final Long descriptionId, final String description) {
        DatabaseManager.saveRouteInfo(userId, descriptionId, description);
    }

    /**
     * Method that loads route description from database
     * @param textView text view that should be filled with description
     * @param id route ID
     * @param userId user ID
     */
    public static void fillDescription(final TextView textView, final Integer id, final String userId) {
        DatabaseManager.fillDescription(textView, id, userId);
    }

    /**
     * Method that loads place description from database
     * @param textView text view that should be filled with description
     * @param id place ID
     */
    public static void fillDescriptionPlaces(final TextView textView, final String id) {
        DatabaseManager.fillDescriptionPlaces(textView, id);
    }

    /**
     * Method that loads avatar to the field with picture of user
     * @param circleImageView image view to load avatar
     * @param context current context
     * @param userId id of the user
     */
    public static void loadAvatar(CircleImageView circleImageView, final Context context, final String userId) {
       DatabaseManager.loadAvatar(circleImageView, context, userId);
    }

    /**
     * Method that loads picture of a place
     * @param imageView view where picture should be placed
     * @param place place with all necessary information
     * @param context current context
     */
    public static void loadDescriptionImage(final ImageView imageView, final Place place, final Context context) {
        DatabaseManager.loadDescriptionImage(imageView, place, context);
    }

    /**
     * Method that loads new avatar of user instead of old one
     * @param userId user id who changes avatar
     * @param uri uri of new avatar image
     */
    public static void setNewAvatar(final String userId, final Uri uri) {
        DatabaseManager.setNewAvatar(userId, uri);
    }

    /**
     * Method that sets user with userId logged in
     * @param context current context
     * @param userId id od uset that should be logged in
     */
    public static void setLoggedIn(Context context, int userId) {
        AuthorizationUtils.setLoggedIn(context, userId);
    }

    /**
     * Method that sets user logged out.
     * If no users currently logged in, stored id is equals to -1
     * @param context current context
     */
    public static void setLoggedOut(Context context) {
        AuthorizationUtils.setLoggedOut(context);
    }

    /**
     * Method that returns userId of currently logged user
     * @param context current context
     * @return integer value with id of the user. If nobody logged in, return value is -1
     */
    public static int getLoggedIn(Context context) {
        return AuthorizationUtils.getLoggedIn(context);
    }

    /**
     * Method that returns userId of currently logged user as string value
     * @param context current context
     * @return integer value with id of the user. If nobody logged in, return value is "-1"
     */
    public static String getLoggedInAsString(Context context) {
        return AuthorizationUtils.getLoggedInAsString(context);
    }

    /**
     * Method that allows to set chat companion of the user
     * @param context current context
     * @param chatPair id of companion
     */
    public static void setChatPair(Context context, String chatPair) {
        ChatUtils.setChatPair(context, chatPair);
    }

    /**
     * Method that allows to get id of chat companion of the user
     * @param context current context
     * @return id if the companion if no companion found, return value is "null"
     */
    public static String getChatPair(Context context) {
        return ChatUtils.getChatPair(context);
    }

    /**
     * Method that allows to set al IDs of favourite places that user have
     * @param context current context
     * @param places list of favourite places
     */
    public static void setPlaces(Context context, String places) {
        FavouritePlacesUtils.setPlaces(context, places);
    }

    /**
     * Method that allows to get list of users favoutite places
     * @param context current contest
     * @return list of favourite places
     */
    public static String getPlaces(Context context) {
        return FavouritePlacesUtils.getPlaces(context);
    }

    /**
     * Method that write length of list of friends of current user
     * @param context current context
     * @param length number of friends
     */
    public static void setFriendsLength(Context context, int length) {
        FriendsDataUtils.setFriendsLength(context, length);
    }

    /**
     * Method that read length of list of friends of current user
     * @param context current context
     */
    public static int getFriendsLength(Context context) {
        return FriendsDataUtils.getFriendsLength(context);
    }

    /**
     * Method that write list of friends of current user
     * @param context current context
     * @param friends string that contains a list of friends
     */
    public static void setFriends(Context context, String friends) {
        FriendsDataUtils.setFriends(context, friends);
    }

    /**
     * Method that read list of friends of current user
     * @param context current context
     */
    public static String getFriends(Context context) {
        return FriendsDataUtils.getFriends(context);
    }

    /**
     * Method that allows to set number of user routes
     * @param context current context
     * @param length number of user routes
     */
    public static void setRoutesLength(Context context, long length) {
        RoutesUtils.setRoutesLength(context, length);
    }

    /**
     * Method that allows to get number of user routes
     * @param context current context
     * @return id if the companion if no companion found, return value is "null"
     */
    public static Long getRoutesLength(Context context) {
        return RoutesUtils.getRoutesLength(context);
    }

    /**
     * Method that loads all markers from to the map
     * @param googleMap markers destination map
     */
    public static void addAllMarkers(final GoogleMap googleMap) {
        Controller.loadMarkersToMap(googleMap);
    }

    /**
     * Method that cleans map and loads all markers after that
     * @param googleMap markers destination map
     */
    public static void refreshMarkers(final GoogleMap googleMap) {
        if (googleMap != null) {
            googleMap.clear();
            Controller.loadMarkersToMap(googleMap);
        }
    }

    /**
     * Method that loads all markers by user query to the map
     * @param googleMap markers destination map
     * @param toFind user search query
     */
    public static void addFoundedMarkers(final GoogleMap googleMap, final String toFind) {
        Controller.addMarkersByQuery(googleMap, toFind);
    }

    /**
     * Method that builds multi route between many points and places it to the map.
     * Furthermore it saves it to database immediately and moves camera to the destination point
     * @param listView list with the results of query, where user choose places to visit
     * @param myPosition current user position
     * @param placeArrayList arrau list with descriptions of places
     * @param context current context
     * @param googleMap map where route will be possibly build
     * @param points storage of route points which is important for route in augmented reality
     */
    public static void makeRoute(final ListView listView, final LatLng myPosition, final ArrayList<Place> placeArrayList, final Context context, final GoogleMap googleMap, final ArrayList<LatLng> points) {
        MapManager.makeRoute(listView, myPosition, placeArrayList, context, googleMap, points);
    }

    /**
     * Method that builds single route between two places
     * @param myPosition current user position
     * @param destination destination point of route
     * @param context current context
     * @param googleMap map where route should be build
     * @param points storage of route points which is important for route in augmented reality
     */
    public static void makeSingleRoute(final LatLng myPosition, final LatLng destination, final Context context, final GoogleMap googleMap, final ArrayList<LatLng> points) {
        MapManager.makeSingleRoute(myPosition, destination, context, googleMap, points);
    }

    /**
     * Method that prepares map screenshot for saving to database
     * @param instance current activity
     * @param routeName route name
     * @return returns callback that can do a map screenshot
     */
    public static GoogleMap.SnapshotReadyCallback getRoutePictureCallback(final Activity instance, final String routeName) {
        GoogleMap.SnapshotReadyCallback callback = new GoogleMap.SnapshotReadyCallback() {
            Bitmap bitmap;

            @Override
            public void onSnapshotReady(Bitmap snapshot) {
                try {
                    bitmap = snapshot;
                    File outputDir = new File(Environment.getExternalStoragePublicDirectory(Environment.DIRECTORY_PICTURES), instance.getString(R.string.app_name));

                    if (!outputDir.exists()) {
                        outputDir.mkdir();
                    }

                    File outputFile = new File(outputDir, routeName + ".png");
                    FileOutputStream fileOutputStream = new FileOutputStream(outputFile);
                    bitmap.compress(Bitmap.CompressFormat.PNG, 100, fileOutputStream);
                    MediaScannerConnection.scanFile(instance,
                            new String[] { outputFile.getPath() },
                            new String[] { "image/png" },
                            null);

                    Uri attachment = Uri.fromFile(outputFile);
                    DatabaseManager.getUserRoutesLength2(AuthorizationUtils.getLoggedInAsString(instance.getBaseContext()), instance.getBaseContext());
                    DatabaseManager.saveRoute(attachment, AuthorizationUtils.getLoggedInAsString(instance.getBaseContext()), instance.getBaseContext());
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }
        };

        return callback;
    }

    /**
     * Method that make a snapshot of the map
     * @param map map to take screenshot
     * @param routeName route title
     * @param activity current activity
     */
    public static void sendRoute(GoogleMap map, final String routeName, final Activity activity) {

        Thread myThread = new Thread(() -> {
            map.snapshot(Controller.getRoutePictureCallback(activity, "tmp"));
        });
        myThread.run();
    }
}
