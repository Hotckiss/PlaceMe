package placeme.ru.placemedemo.core.utils;

import android.content.Context;
import android.support.test.InstrumentationRegistry;

import org.junit.Before;
import org.junit.Test;

import static org.junit.Assert.*;
import static placeme.ru.placemedemo.core.utils.ChatUtils.*;

/**
 * Class that tests methods of ChatUtils class
 * Created by Андрей on 07.02.2018.
 */
public class ChatUtilsTest {
    private Context appContext;

    @Before
    public void setOut() {
        appContext = InstrumentationRegistry.getTargetContext();
        setChatPair(appContext, "0");
    }

    @Test
    public void testGetSetChatPair() {
        setChatPair(appContext, "3");
        assertEquals("3", getChatPair(appContext));

        setChatPair(appContext, "7");
        assertEquals("7", getChatPair(appContext));
    }
}