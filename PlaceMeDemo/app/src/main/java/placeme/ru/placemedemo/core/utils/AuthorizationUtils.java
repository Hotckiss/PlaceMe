package placeme.ru.placemedemo.core.utils;

import android.content.Context;
import android.content.SharedPreferences;

/**
 * A class that implements some utilities that helps to get id
 * of currently authorized user.
 * Furthermore, this class allows to change current user and set users logged out
 * Created by Андрей on 21.12.2017.
 */
public class AuthorizationUtils {
    private static final String PREFERENCES_AUTHORIZATION_KEY = "AuthorizedUser";
    private static final String AUTHORIZATION_PREFERENCES = "AuthorizationState";

    /**
     * Method that sets user with userId logged in
     * @param context current context
     * @param userId id of user that should be logged in
     */
    public static void setLoggedIn(Context context, int userId) {
        getSharedPreferences(context).edit().putInt(PREFERENCES_AUTHORIZATION_KEY, userId).apply();
    }

    /**
     * Method that sets user logged out.
     * If no users currently logged in, stored id is equals to -1
     * @param context current context
     */
    public static void setLoggedOut(Context context) {
        setLoggedIn(context, -1);
    }

    /**
     * Method that returns userId of currently logged user
     * @param context current context
     * @return integer value with id of the user. If nobody logged in, return value is -1
     */
    public static int getLoggedIn(Context context) {
        return getSharedPreferences(context).getInt(PREFERENCES_AUTHORIZATION_KEY, -1);
    }

    /**
     * Method that returns userId of currently logged user as string value
     * @param context current context
     * @return integer value with id of the user. If nobody logged in, return value is "-1"
     */
    public static String getLoggedInAsString(Context context) {
        return String.valueOf(getLoggedIn(context));
    }

    /**
     * Method that get current shared preferences of authorization
     * @param context current context
     * @return current shared preferences
     */
    private static SharedPreferences getSharedPreferences(Context context) {
        return context.getSharedPreferences(AUTHORIZATION_PREFERENCES, Context.MODE_PRIVATE);
    }
}
