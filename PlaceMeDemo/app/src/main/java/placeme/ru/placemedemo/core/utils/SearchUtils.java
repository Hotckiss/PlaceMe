package placeme.ru.placemedemo.core.utils;

import android.content.Context;

/**
 * A class that implements some utilities that helps to get phone settings
 * of searching
 * Created by Андрей on 27.12.2017.
 */
public class SearchUtils {

    private static final String SEARCH_DISTANCE_KEY = "status";
    private static final String SEARCH_VALUE_KEY = "distance";
    private static final String SEARCH_DISTANCE_STATUS_PREFERENCES = "searchDistanceStatus";
    private static final String SEARCH_DISTANCE_PREFERENCES = "searchDistance";
    private static final String SEARCH_RATING_KEY = "status";
    private static final String RATING_VALUE_KEY = "rating";
    private static final String SEARCH_RATING_PREFERENCES = "searchRating";

    /**
     * Method that writes status of searching by distance
     * @param context current context
     * @param status current status to set up
     */
    public static void setDistanceSearchStatus(Context context, boolean status) {
        context.getSharedPreferences(SEARCH_DISTANCE_STATUS_PREFERENCES, Context.MODE_PRIVATE).edit()
                .putBoolean(SEARCH_DISTANCE_KEY, status).apply();
    }

    /**
     *Method that writes length of searching by distance
     * @param context current context
     * @param distance maximum search distance
     */
    public static void setDistanceSearchValue(Context context, int distance) {
        context.getSharedPreferences(SEARCH_DISTANCE_PREFERENCES, Context.MODE_PRIVATE).edit()
                .putInt(SEARCH_VALUE_KEY, distance).apply();
    }

    /**
     * Method that gets status of searching by distance
     * @param context current context
     */
    public static boolean getDistanceSearchStatus(Context context) {
        return context.getSharedPreferences(SEARCH_DISTANCE_STATUS_PREFERENCES, Context.MODE_PRIVATE)
                .getBoolean(SEARCH_DISTANCE_KEY, false);
    }

    /**
     * Method that gets value of searching by distance
     * @param context current context
     */
    public static int getDistanceSearchValue(Context context) {
        return context.getSharedPreferences(SEARCH_DISTANCE_PREFERENCES, Context.MODE_PRIVATE)
                .getInt(SEARCH_VALUE_KEY, 50);
    }

    /**
     * Method that writes status of searching by rating
     * @param context current context
     * @param status current status to set up
     */
    public static void setRatingSearchStatus(Context context, boolean status) {
        context.getSharedPreferences(SEARCH_RATING_PREFERENCES, Context.MODE_PRIVATE).edit()
                .putBoolean(SEARCH_RATING_KEY, status).apply();
    }

    /**
     * Method that writes value of searching by rating
     * @param context current context
     * @param rating minimum search rating
     */
    public static void setRatingSearchValue(Context context, int rating) {
        context.getSharedPreferences(SEARCH_RATING_PREFERENCES, Context.MODE_PRIVATE).edit()
                .putInt(RATING_VALUE_KEY, rating).apply();
    }

    /**
     * Method that reads writes status of searching by rating
     * @param context current context
     */
    public static boolean getRatingSearchStatus(Context context) {
        return context.getSharedPreferences(SEARCH_RATING_PREFERENCES, Context.MODE_PRIVATE)
                .getBoolean(SEARCH_RATING_KEY, false);
    }

    /**
     * Method that reads value of searching by rating
     * @param context current context
     */
    public static int getRatingSearchValue(Context context) {
        return context.getSharedPreferences(SEARCH_RATING_PREFERENCES, Context.MODE_PRIVATE)
                .getInt(RATING_VALUE_KEY, 0);
    }

}
