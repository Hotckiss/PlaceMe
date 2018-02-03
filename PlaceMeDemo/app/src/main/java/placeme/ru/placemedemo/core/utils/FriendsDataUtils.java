package placeme.ru.placemedemo.core.utils;

import android.content.Context;

/**
 * A class that implements some utilities that helps to get id of friends
 * of currently authorized user.
 * Created by Андрей on 21.12.2017.
 */
public class FriendsDataUtils {

    private static final String FRIENDS_LENGTH_KEY = "length";
    private static final String FRIENDS_PREFERENCES = "Friends";
    private static final String FRIENDS_LIST_LENGTH_KEY = "list";
    private static final String FRIENDS_LIST_PREFERENCES = "FriendsString";

    /**
     * Method that write length of list of friends of current user
     * @param context current context
     * @param length number of friends
     */
    public static void setFriendsLength(Context context, int length) {
        context.getSharedPreferences(FRIENDS_PREFERENCES, Context.MODE_PRIVATE).edit().putInt(FRIENDS_LENGTH_KEY, length).apply();
    }

    /**
     * Method that read length of list of friends of current user
     * @param context current context
     */
    public static int getFriendsLength(Context context) {
        return context.getSharedPreferences(FRIENDS_PREFERENCES, Context.MODE_PRIVATE).getInt(FRIENDS_LENGTH_KEY, 0);
    }

    /**
     * Method that write list of friends of current user
     * @param context current context
     * @param friends string that contains a list of friends
     */
    public static void setFriends(Context context, String friends) {
        context.getSharedPreferences(FRIENDS_LIST_PREFERENCES, Context.MODE_PRIVATE).edit().putString(FRIENDS_LIST_LENGTH_KEY, friends).apply();
    }

    /**
     * Method that read list of friends of current user
     * @param context current context
     */
    public static String getFriends(Context context) {
        return context.getSharedPreferences(FRIENDS_LIST_PREFERENCES, Context.MODE_PRIVATE).getString(FRIENDS_LIST_LENGTH_KEY, "");
    }

}
