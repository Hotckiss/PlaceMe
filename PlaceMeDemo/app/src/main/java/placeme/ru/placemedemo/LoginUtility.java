package placeme.ru.placemedemo;

import android.content.Context;

/**
 * Created by Андрей on 11.11.2017.
 */

public class LoginUtility {
    private static final String PREFERENCES_AUTHORIZATION_KEY = "isAuthorized";
    private static final String AUTHORIZATION_PREFERENCES = "AuthData";
    private static final String FRIENDS_LENGTH_KEY = "length";
    private static final String FRIENDS_PREFERENCES = "Friends";
    private static final String FRIENDS_LIST_LENGTH_KEY = "list";
    private static final String FRIENDS_LIST_PREFERENCES = "FriendsStr";
    private static final String CHAT_KEY = "dialog";
    private static final String CHAT_PREFERENCES = "Chat";


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

    public static String getLoggedInAsString(Context context) {
        //get preferences
        //gev value by key
        //return it
        return ((Integer)context.getSharedPreferences(AUTHORIZATION_PREFERENCES, Context.MODE_PRIVATE).getInt(PREFERENCES_AUTHORIZATION_KEY, -1)).toString();
    }

    public static void setFriendsLength(Context context, int length) {
        context.getSharedPreferences(FRIENDS_PREFERENCES, Context.MODE_PRIVATE).edit().putInt(FRIENDS_LENGTH_KEY, length).apply();
    }

    public static int getFriendsLength(Context context) {
        return context.getSharedPreferences(FRIENDS_PREFERENCES, Context.MODE_PRIVATE).getInt(FRIENDS_LENGTH_KEY, 0);
    }

    public static void setFriends(Context context, String friends) {
        context.getSharedPreferences(FRIENDS_LIST_PREFERENCES, Context.MODE_PRIVATE).edit().putString(FRIENDS_LIST_LENGTH_KEY, friends).apply();
    }

    public static String getFriends(Context context) {
        return context.getSharedPreferences(FRIENDS_LIST_PREFERENCES, Context.MODE_PRIVATE).getString(FRIENDS_LIST_LENGTH_KEY, "");
    }

    public static void setChatPair(Context context, String chatPair) {
        context.getSharedPreferences(CHAT_PREFERENCES, Context.MODE_PRIVATE).edit().putString(CHAT_KEY, chatPair).apply();
    }

    public static String getChatPair(Context context) {
        return context.getSharedPreferences(CHAT_PREFERENCES, Context.MODE_PRIVATE).getString(CHAT_KEY, "null");
    }
}
