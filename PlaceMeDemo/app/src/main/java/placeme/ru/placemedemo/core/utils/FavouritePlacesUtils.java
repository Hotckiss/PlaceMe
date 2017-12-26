package placeme.ru.placemedemo.core.utils;

import android.content.Context;

/**
 * Created by Андрей on 25.12.2017.
 */

/**
 * A class that provides simple preferences to correct work with favourite places of the user
 * Implemented methods to write some data to use it between the activities: list of ID of users favourite places
 */
public class FavouritePlacesUtils {

    private static final String FAVOURITE_PLACES_KEY = "places";
    private static final String FAVOURITE_PLACES_PREFERENCES = "FavouritePlaces";

    /**
     * Method that allows to set al IDs of favourite places that user have
     * @param context current context
     * @param places list of favourite places
     */
    public static void setPlaces(Context context, String places) {
        context.getSharedPreferences(FAVOURITE_PLACES_PREFERENCES, Context.MODE_PRIVATE).edit().putString(FAVOURITE_PLACES_KEY, places).apply();
    }

    /**
     * Method that allows to get list of users favoutite places
     * @param context current contest
     * @return list of favourite places
     */
    public static String getPlaces(Context context) {
        return context.getSharedPreferences(FAVOURITE_PLACES_PREFERENCES, Context.MODE_PRIVATE).getString(FAVOURITE_PLACES_KEY, "");
    }
}
