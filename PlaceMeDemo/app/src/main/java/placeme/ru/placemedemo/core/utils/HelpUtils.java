package placeme.ru.placemedemo.core.utils;

import android.content.Context;

/**
 * Created by Андрей on 19.02.2018.
 */

public class HelpUtils {
    private static final String HELP_KEY = "helpIs";
    private static final String HELP_PREFERENCES = "help";

    public static void setHelp(Context context, boolean is) {
        context.getSharedPreferences(HELP_PREFERENCES, Context.MODE_PRIVATE).edit()
                .putBoolean(HELP_KEY, is).apply();
    }

    public static boolean getHelp(Context context) {
        return context.getSharedPreferences(HELP_PREFERENCES, Context.MODE_PRIVATE)
                .getBoolean(HELP_KEY, true);
    }
}