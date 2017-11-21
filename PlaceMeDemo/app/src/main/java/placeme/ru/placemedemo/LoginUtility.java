package placeme.ru.placemedemo;

import android.content.Context;

/**
 * Created by Андрей on 11.11.2017.
 */

public class LoginUtility {
    private static final String PREFERENCES_AUTHORIZATION_KEY = "isAuthorized";
    private static final String AUTHORIZATION_PREFERENCES = "AuthData";

    public static void setLoggedIn(Context context, int user_number) {
        //get preferences
        //get editor of map
        //put flag of auth using key
        //apply changes

        //context.getSharedPreferences(AUTHORIZATION_PREFERENCES, Context.MODE_PRIVATE).edit().putBoolean(PREFERENCES_AUTHORIZATION_KEY, true).apply();
        context.getSharedPreferences(AUTHORIZATION_PREFERENCES, Context.MODE_PRIVATE).edit().putInt(PREFERENCES_AUTHORIZATION_KEY, user_number).apply();
    }

    public static void setLoggedOut(Context context) {
        //get preferences
        //get editor of map
        //put flag of auth using key
        //apply changes
        context.getSharedPreferences(AUTHORIZATION_PREFERENCES, Context.MODE_PRIVATE).edit().putInt(PREFERENCES_AUTHORIZATION_KEY, -1).apply();
    }

    public static int getLoggedIn(Context context) {
        //get preferences
        //gev value by key
        //return it
        return context.getSharedPreferences(AUTHORIZATION_PREFERENCES, Context.MODE_PRIVATE).getInt(PREFERENCES_AUTHORIZATION_KEY, -1);
    }
}
