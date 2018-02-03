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
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.RatingBar;
import android.widget.TextView;

import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.Marker;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.util.ArrayList;

import de.hdodenhof.circleimageview.CircleImageView;
import placeme.ru.placemedemo.R;
import placeme.ru.placemedemo.core.database.DatabaseManagerPlans;
import placeme.ru.placemedemo.core.database.DatabaseManagerPlaces;
import placeme.ru.placemedemo.core.database.DatabaseManagerRoutes;
import placeme.ru.placemedemo.core.database.DatabaseManagerUsers;
import placeme.ru.placemedemo.core.map.MapManager;
import placeme.ru.placemedemo.core.utils.AuthorizationUtils;
import placeme.ru.placemedemo.core.utils.ChatUtils;
import placeme.ru.placemedemo.core.utils.FavouritePlacesUtils;
import placeme.ru.placemedemo.core.utils.FriendsDataUtils;
import placeme.ru.placemedemo.core.utils.RoutesUtils;
import placeme.ru.placemedemo.core.utils.SearchUtils;
import placeme.ru.placemedemo.elements.AuthData;
import placeme.ru.placemedemo.elements.Place;
import placeme.ru.placemedemo.elements.User;
import placeme.ru.placemedemo.elements.UserDataFields;
import placeme.ru.placemedemo.elements.UserProfileFields;

/**
 * Class that provides connectivity between all parts of application
 * Created by Андрей on 25.12.2017.
 */
public class Controller {

    /**
     * Method that register new user in database with unique id
     * @param newAuthData authentication data that user input during registration
     * @param newUserData user data that user input during registration
     */
    public static void registerUser(final AuthData newAuthData, final User newUserData) {
        DatabaseManagerUsers.registerUser(newAuthData, newUserData);
    }

    /**
     * Method that updates place rating after user voting
     * and immediately updates rating bar in UI
     * @param ratingBar rating bar to update
     * @param placeId place that user have rated
     */
    public static void updatePlaceRating(final RatingBar ratingBar, final String placeId) {
        DatabaseManagerPlaces.updatePlaceRating(ratingBar, placeId);
    }

    /**
     * Method that adds place to the list of favourite places of the user
     * place will not be added if it is already in the favourite list
     * @param userId id of the user who wants to add place to favourite
     * @param placeId id of place that should be added
     */
    public static void addPlaceToFavourite(final String userId, final String placeId) {
       DatabaseManagerUsers.addPlaceToFavourite(userId, placeId);
    }

    /**
     * Method that loads user information from the database to the edit fields
     * @param toLoad array of fields to init
     * @param userId user id to search in database
     */
    public static void loadUserDataForEdit(final UserDataFields toLoad, String userId) {
        DatabaseManagerUsers.loadUserDataForEdit(toLoad, userId);
    }

    /**
     * Method that saves changed user data
     * @param userId user id
     * @param newAuthData authentication data that user input during editing profile
     * @param newUserData user data that user input during editing profile
     */
    public static void saveProfileChanges(final String userId, final AuthData newAuthData, final User newUserData) {
        DatabaseManagerUsers.saveProfileChanges(userId, newAuthData, newUserData);
    }

    /**
     * Method that allows to load user favorite places from database to fragment
     * @param userId user id
     * @param context current context
     * @param fragmentManager fragment managet for transaction
     * @param fragment output fragment
     */
    public static void loadUserFavouritePlacesListV2(final String userId, final Context context, final FragmentManager fragmentManager, final Fragment fragment) {
        DatabaseManagerUsers.loadUserFavouritePlacesList(userId, context, fragmentManager, fragment);
    }

    /**
     * Method that loads all user profile information and initializes list of friends
     * @param context current context
     * @param userId user id
     * @param userProfileInfo fields with user profile information
     * @param fragmentManager fragment manager to load friends list
     */
    public static void loadUserProfile(final Context context, final int userId, final UserProfileFields userProfileInfo, final FragmentManager fragmentManager) {
        DatabaseManagerUsers.loadUserProfile(context, userId, userProfileInfo, fragmentManager);
    }

    /**
     * Method that allows to save created place in database
     * @param uri picture uri
     * @param placeInfo text description of the place
     * @param position place coordinates
     */
    public static void saveCreatedPlace(final Uri uri, final Place placeInfo, final LatLng position) {
        DatabaseManagerPlaces.saveCreatedPlace(uri, placeInfo, position);
    }

    /**
     * Method that called after adding new route, that means that ID of next route will be other
     * @param userId user ID
     * @param length current last added route ID
     */
    public static void updateRoutesLength(final String userId, final long length) {
        DatabaseManagerRoutes.updateRoutesLength(userId, length);
    }

    /**
     * Method gets current number of user routes and adds than to some fragment
     * @param userId used ID
     * @param context current context
     * @param fragmentManager fragment manager for transaction
     * @param fragment output fragment
     */
    public static void getUserRoutesLength(final String userId, final Context context, final FragmentManager fragmentManager, final Fragment fragment) {
        DatabaseManagerRoutes.getUserRoutesLength(userId, context, fragmentManager, fragment);
    }

    /**
     * Method gets current number of user routes without adding them to the fragment
     * @param userId used ID
     * @param context current context
     */
    public static void getUserRoutesLength2(final String userId, final Context context) {
        DatabaseManagerRoutes.getUserRoutesLength2(userId, context);
    }

    /**
     * Method that loads place that was converted from Google Places to database
     * @param uri picture uri
     * @param place place to save
     */
    public static void saveConvertedPlace(final Uri uri, final Place place) {
       DatabaseManagerPlaces.saveConvertedPlace(uri, place);
    }

    /**
     * Method that runs alert dialog with full place description
     * Place loaded from database
     * @param context current context
     * @param marker place position
     */
    public static void runDescriptionDialog(final Context context, final Marker marker, final LatLng myPosition, final GoogleMap googleMap) {
        DatabaseManagerPlaces.runDescriptionDialog(context, marker, myPosition, googleMap);
    }

    /**
     * Method that saves route description to database
     * @param userId user Id who owns this route
     * @param descriptionId ID of route that has this description
     * @param description string that contains all description
     */
    public static void saveRouteInfo(final String userId, final Long descriptionId, final String description) {
        DatabaseManagerRoutes.saveRouteInfo(userId, descriptionId, description);
    }

    /**
     * Method that loads route description from database
     * @param textView text view that should be filled with description
     * @param id route ID
     * @param userId user ID
     */
    public static void fillDescription(final TextView textView, final Integer id, final String userId) {
        DatabaseManagerRoutes.fillDescription(textView, id, userId);
    }

    /**
     * Method that loads place description from database
     * @param textView text view that should be filled with description
     * @param id place ID
     */
    public static void fillDescriptionPlaces(final TextView textView, final String id) {
        DatabaseManagerPlaces.fillDescriptionPlaces(textView, id);
    }

    /**
     * Method that loads avatar to the field with picture of user
     * @param circleImageView image view to load avatar
     * @param context current context
     * @param userId id of the user
     */
    public static void loadAvatar(CircleImageView circleImageView, final Context context, final String userId) {
       DatabaseManagerPlans.loadAvatar(circleImageView, context, userId);
    }

    /**
     * Method that loads picture of a place
     * @param imageView view where picture should be placed
     * @param place place with all necessary information
     * @param context current context
     */
    public static void loadDescriptionImage(final ImageView imageView, final Place place, final Context context) {
        DatabaseManagerPlaces.loadDescriptionImage(imageView, place, context);
    }

    /**
     * Method that loads new avatar of user instead of old one
     * @param userId user id who changes avatar
     * @param uri uri of new avatar image
     */
    public static void setNewAvatar(final String userId, final Uri uri) {
        DatabaseManagerPlans.setNewAvatar(userId, uri);
    }

    /**
     * Method that sets user with userId logged in
     * @param context current context
     * @param userId id od used that should be logged in
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
        MapManager.refreshMarkers(googleMap);
        MapManager.makeSingleRoute(myPosition, destination, context, googleMap, points);
    }

    /**
     * Method that make a snapshot of the map
     * @param map map to take screenshot
     * @param activity current activity
     */
    public static void sendRoute(GoogleMap map, final Activity activity) {

        Thread myThread = new Thread(() -> map.snapshot(Controller.getRoutePictureCallback(activity)));
        myThread.start();
    }

    /**
     * Method that searches places in database within query string and lock any actions while loading
     * this makes impossible to build root before loading all places
     * @param arrayAdapter adapter with names of founded places
     * @param places array with founded places
     * @param toFind string which contains user query to search
     * @param myPosition current user positions
     * @param context current context
     * @param activity UI activity that calls method
     */
    public static void findPlacesByStringV2(final ArrayAdapter<String> arrayAdapter, final ArrayList<Place> places,
                                            final String toFind, final LatLng myPosition, final Context context, final Activity activity) {
        DatabaseManagerPlaces.findPlacesByString(arrayAdapter, places, toFind, myPosition, context, activity);
    }

    /**
     * Method that adds user review to all reviews about place
     * @param placeId reviewed place
     * @param review user review text
     */
    public static void addReview(final String placeId, final String review) {
        DatabaseManagerPlans.addReview(placeId, review);
    }

    /**
     * Method that loads all users reviews to array aadapter
     * @param placeId place which reviews should be extracted
     * @param adapter destination adapter to load reviews
     */
    public static void findReviews(final String placeId, ArrayAdapter<String> adapter) {
        DatabaseManagerPlans.findReviews(placeId, adapter);
    }

    /**
     * Method that adds visiting plan to users plans list
     * @param placeName name of place to visit
     * @param userId id of user who wants to visit place
     * @param date date of planned visit
     */
    public static void addPlan(final String placeName, final String userId, final String date) {
        DatabaseManagerPlans.addPlan(placeName, userId, date);
    }

    /**
     * Method that searches places in database within query string and maked impossible to add friends
     * before load would be finished
     * @param arrayAdapter adapter with names of founded places
     * @param users array with founded users
     * @param toFind string which contains user query to search
     * @param activity UI activity that calls method
     */
    public static void findUsersByStringV2(final ArrayAdapter<String> arrayAdapter, final ArrayList<User> users, final String toFind, final Context context, final Activity activity) {
        DatabaseManagerUsers.findUsersByString(arrayAdapter, users, toFind, context, activity);
    }

    /**
     * Method that adds friend to users friend list if it wasn't already added
     * @param userId id of user who wants to add new friend
     * @param friendId id of friend to be added
     */
    public static void addFriend(final String userId, final String friendId) {
        DatabaseManagerUsers.addFriend(userId, friendId);
    }

    /**
     * Method that allows to save created place in database
     * @param bitmap picture bitmap
     * @param placeInfo text description of the place
     * @param position place coordinates
     */
    public static void saveCreatedPlace2(final Bitmap bitmap, final Place placeInfo, final LatLng position) {
        DatabaseManagerPlaces.saveCreatedPlace2(bitmap, placeInfo, position);
    }

    /**
     * Method that loads visiting plan to the screen
     * @param userId id of user whose plan should be loaded
     * @param adapter adapter with plans list
     */
    public static void loadPlan(final String userId, final ArrayAdapter<String> adapter) {
        DatabaseManagerPlans.loadPlan(userId, adapter);
    }

    /**
     * Method that writes status of searching by distance
     * @param context current context
     * @param status current status to set up
     */
    public static void setDistanceSearchStatus(Context context, boolean status) {
        SearchUtils.setDistanceSearchStatus(context, status);
    }

    /**
     *Method that writes length of searching by distance
     * @param context current context
     * @param distance maximum search distance
     */
    public static void setDistanceSearchValue(Context context, int distance) {
        SearchUtils.setDistanceSearchValue(context, distance);
    }

    /**
     * Method that gets status of searching by distance
     * @param context current context
     */
    public static boolean getDistanceSearchStatus(Context context) {
        return SearchUtils.getDistanceSearchStatus(context);
    }

    /**
     * Method that gets value of searching by distance
     * @param context current context
     */
    public static int getDistanceSearchValue(Context context) {
        return SearchUtils.getDistanceSearchValue(context);
    }

    /**
     * Method that writes status of searching by rating
     * @param context current context
     * @param status current status to set up
     */
    public static void setRatingSearchStatus(Context context, boolean status) {
        SearchUtils.setRatingSearchStatus(context, status);
    }

    /**
     * Method that writes value of searching by rating
     * @param context current context
     * @param rating minimum search rating
     */
    public static void setRatingSearchValue(Context context, int rating) {
        SearchUtils.setRatingSearchValue(context, rating);
    }

    /**
     * Method that reads writes status of searching by rating
     * @param context current context
     */
    public static boolean getRatingSearchStatus(Context context) {
        return SearchUtils.getRatingSearchStatus(context);
    }

    /**
     * Method that reads value of searching by rating
     * @param context current context
     */
    public static int getRatingSearchValue(Context context) {
        return SearchUtils.getRatingSearchValue(context);
    }

    /**
     * Method that converts distance between lat lng to kilometers
     * @param start first point
     * @param finish second point
     * @return distance in kilometers
     */
    public static double getKilometers(LatLng start, LatLng finish) {
        try {
            double radius = 6371;
            double dLatitude = degreeToRadian(finish.latitude - start.latitude);
            double dLongitude = degreeToRadian(finish.longitude - start.longitude);

            double a = Math.sin(dLatitude / 2) * Math.sin(dLatitude / 2) + Math.cos(degreeToRadian(start.latitude)) * Math.cos(degreeToRadian(finish.latitude)) * Math.sin(dLongitude / 2) * Math.sin(dLongitude / 2);
            double b = 2 * Math.atan2(Math.sqrt(a), Math.sqrt(1 - a));

            return radius * b;
        } catch (NullPointerException ex) {
            return 0;
        }
    }

    /**
     * Method that loads all markers from database to the map
     * @param googleMap markers destination map
     */
    public static void loadMarkersToMap(final GoogleMap googleMap) {
        DatabaseManagerPlaces.loadMarkersToMap(googleMap);
    }

    /**
     * Method that loads markers founded by query from database to the map
     * @param googleMap markers destination map
     * @param query user search query
     */
    public static void addMarkersByQuery(final GoogleMap googleMap, final String query) {
        MapManager.addFoundedMarkers(googleMap, query);
    }

    /**
     * Method that saves map screenshot with route to database
     * @param uri screenshot uri
     * @param userId user ID
     * @param context current context
     */
    private static void saveRoute(final Uri uri, final String userId, final Context context) {
        DatabaseManagerRoutes.saveRoute(uri, userId, context);
    }

    private static GoogleMap.SnapshotReadyCallback getRoutePictureCallback(final Activity instance) {
        return new GoogleMap.SnapshotReadyCallback() {
            Bitmap bitmap;

            @Override
            public void onSnapshotReady(Bitmap snapshot) {
                try {
                    bitmap = snapshot;
                    File outputDir = new File(Environment.getExternalStoragePublicDirectory(Environment.DIRECTORY_PICTURES), instance.getString(R.string.app_name));

                    if (!outputDir.exists()) {
                        outputDir.mkdir();
                    }

                    File outputFile = new File(outputDir, "tmp.png");
                    FileOutputStream fileOutputStream = new FileOutputStream(outputFile);
                    bitmap.compress(Bitmap.CompressFormat.PNG, 100, fileOutputStream);
                    MediaScannerConnection.scanFile(instance,
                            new String[] { outputFile.getPath() },
                            new String[] { "image/png" },
                            null);

                    Uri attachment = Uri.fromFile(outputFile);
                    Controller.getUserRoutesLength2(AuthorizationUtils.getLoggedInAsString(instance.getBaseContext()), instance.getBaseContext());
                    Controller.saveRoute(attachment, AuthorizationUtils.getLoggedInAsString(instance.getBaseContext()), instance.getBaseContext());
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }
        };
    }

    private static double degreeToRadian(double degree) {
        return degree * (Math.PI / 180);
    }
}
