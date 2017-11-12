package placeme.ru.placemedemo;

import android.content.Context;

/**
 * Created by Андрей on 11.11.2017.
 */

public class LoginUtility {
    private static final String PREFERENCES_AUTHORIZATION_KEY = "isAuthorized";
    private static final String AUTHORIZATION_PREFERENCES = "AuthData";

    public static void setLoggedIn(Context context) {
        //get preferences
        //get editor of map
        //put flag of auth using key
        //apply changes

        context.getSharedPreferences(AUTHORIZATION_PREFERENCES, Context.MODE_PRIVATE).edit().putBoolean(PREFERENCES_AUTHORIZATION_KEY, true).apply();
    }

    public static void setLoggedOut(Context context) {
        //get preferences
        //get editor of map
        //put flag of auth using key
        //apply changes
        context.getSharedPreferences(AUTHORIZATION_PREFERENCES, Context.MODE_PRIVATE).edit().putBoolean(PREFERENCES_AUTHORIZATION_KEY, false).apply();
    }

    public static boolean checkLoggedIn(Context context) {
        //get preferences
        //gev value by key
        //return it
        return context.getSharedPreferences(AUTHORIZATION_PREFERENCES, Context.MODE_PRIVATE).getBoolean(PREFERENCES_AUTHORIZATION_KEY, false);
    }
}
