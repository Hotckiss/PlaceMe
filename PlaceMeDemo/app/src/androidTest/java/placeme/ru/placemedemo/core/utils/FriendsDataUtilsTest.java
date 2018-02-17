package placeme.ru.placemedemo.core.utils;

import android.content.Context;
import android.support.test.InstrumentationRegistry;

import org.junit.Before;
import org.junit.Test;

import static org.junit.Assert.*;
import static placeme.ru.placemedemo.core.utils.FriendsDataUtils.*;

/**
 * Tests for FriendsDataUtils class
 * Created by Андрей on 07.02.2018.
 */
public class FriendsDataUtilsTest {
    private Context appContext;

    @Before
    public void setOut() {
        appContext = InstrumentationRegistry.getTargetContext();

        setFriendsLength(appContext, 0);
        setFriends(appContext, "");
    }

    @Test
    public void testGetSetFriendsLength() {
        setFriendsLength(appContext, 3);
        assertEquals(3, getFriendsLength(appContext));

        setFriendsLength(appContext, 7);
        assertEquals(7, getFriendsLength(appContext));
    }

    @Test
    public void testGetSetFriends() {
        setFriends(appContext, "3,4,5");
        assertEquals("3,4,5", getFriends(appContext));

        setFriends(appContext, "7,8,9");
        assertEquals("7,8,9", getFriends(appContext));
    }
}