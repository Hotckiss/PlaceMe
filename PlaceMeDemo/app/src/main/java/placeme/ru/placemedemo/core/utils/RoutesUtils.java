package placeme.ru.placemedemo.core.utils;

import android.content.Context;

/**
 * Created by Андрей on 25.12.2017.
 */

public class RoutesUtils {
    private static final String ROUTES_KEY = "routesLength";
    private static final String ROUTES_PREFERENCES = "Routes";

    /**
     * Method that allows to set number of user routes
     * @param context current context
     * @param length number of user routes
     */
    public static void setRoutesLength(Context context, long length) {
        context.getSharedPreferences(ROUTES_PREFERENCES, Context.MODE_PRIVATE).edit().putLong(ROUTES_KEY, length).apply();
    }

    /**
     * Method that allows to get number of user routes
     * @param context current context
     * @return id if the companion if no companion found, return value is "null"
     */
    public static Long getRoutesLength(Context context) {
        return context.getSharedPreferences(ROUTES_PREFERENCES, Context.MODE_PRIVATE).getLong(ROUTES_KEY, 0);
    }
}
