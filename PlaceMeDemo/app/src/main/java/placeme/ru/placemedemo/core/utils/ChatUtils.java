package placeme.ru.placemedemo.core.utils;

import android.content.Context;

/**
 * Created by Андрей on 11.11.2017.
 */

public class ChatUtils {

    private static final String CHAT_KEY = "dialog";
    private static final String CHAT_PREFERENCES = "Chat";

    public static void setChatPair(Context context, String chatPair) {
        context.getSharedPreferences(CHAT_PREFERENCES, Context.MODE_PRIVATE).edit().putString(CHAT_KEY, chatPair).apply();
    }

    public static String getChatPair(Context context) {
        return context.getSharedPreferences(CHAT_PREFERENCES, Context.MODE_PRIVATE).getString(CHAT_KEY, "null");
    }
}
