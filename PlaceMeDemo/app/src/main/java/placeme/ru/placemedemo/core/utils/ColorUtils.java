package placeme.ru.placemedemo.core.utils;

import android.content.Context;

/**
 * Created by Андрей on 20.02.2018.
 */
public class ColorUtils {
    private static final String COLOR_KEY = "colorIs";
    private static final String COLOR_PREFERENCES = "color";

    public static void setColor(Context context, int is) {
        context.getSharedPreferences(COLOR_PREFERENCES, Context.MODE_PRIVATE).edit()
                .putInt(COLOR_KEY, is).apply();
    }

    public static int getColor(Context context) {
        return context.getSharedPreferences(COLOR_PREFERENCES, Context.MODE_PRIVATE)
                .getInt(COLOR_KEY, 0xff0099cc);
    }
}