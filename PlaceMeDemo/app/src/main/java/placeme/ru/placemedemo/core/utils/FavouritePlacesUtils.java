package placeme.ru.placemedemo.core.utils;

import android.content.Context;

/**
 * Created by Андрей on 25.12.2017.
 */

public class FavouritePlacesUtils {

    private static final String PLACES_KEY = "places";
    private static final String PLACES_PREFERENCES = "FavouritePlaces";

    public static void setPlaces(Context context, String places) {
        context.getSharedPreferences(PLACES_PREFERENCES, Context.MODE_PRIVATE).edit().putString(PLACES_KEY, places).apply();
    }

    public static String getPlaces(Context context) {
        return context.getSharedPreferences(PLACES_PREFERENCES, Context.MODE_PRIVATE).getString(PLACES_KEY, "");
    }
}
