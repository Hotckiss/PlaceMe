package placeme.ru.placemedemo.core.utils;

import android.content.Context;

/**
 * A class that provides simple preferences to make a chat easily
 * Created by Андрей on 11.11.2017.
 */
public class ChatUtils {

    private static final String CHAT_KEY = "dialog";
    private static final String CHAT_PREFERENCES = "Chat";
    private static final String DEFAULT_VALUE = "null";

    /**
     * Method that allows to set chat companion of the user
     * @param context current context
     * @param chatPair id of companion
     */
    public static void setChatPair(Context context, String chatPair) {
        context.getSharedPreferences(CHAT_PREFERENCES, Context.MODE_PRIVATE).edit()
                .putString(CHAT_KEY, chatPair).apply();
    }

    /**
     * Method that allows to get id of chat companion of the user
     * @param context current context
     * @return id if the companion if no companion found, return value is "null"
     */
    public static String getChatPair(Context context) {
        return context.getSharedPreferences(CHAT_PREFERENCES, Context.MODE_PRIVATE)
                .getString(CHAT_KEY, DEFAULT_VALUE);
    }
}
