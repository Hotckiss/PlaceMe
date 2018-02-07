package placeme.ru.placemedemo.core.utils;

import android.content.Context;
import android.support.test.InstrumentationRegistry;

import org.junit.After;
import org.junit.Before;
import org.junit.Test;

import static org.junit.Assert.*;
import static placeme.ru.placemedemo.core.utils.AuthorizationUtils.*;

/**
 * Test all methods of AuthorizationUtils class
 * Created by Андрей on 07.02.2018.
 */
public class AuthorizationUtilsTest {
    private Context appContext;

    @Before
    public void setOut() {
        appContext = InstrumentationRegistry.getTargetContext();

        setLoggedOut(appContext);
    }

    @After
    public void testFinish() {
        setLoggedOut(appContext);
    }

    @Test
    public void testGetLoggedInDefault() throws Exception {
        assertEquals(-1, getLoggedIn(appContext));
    }

    @Test
    public void testGetSetLoggedIn() throws Exception {
        setLoggedIn(appContext, 3);
        assertEquals(3, getLoggedIn(appContext));

        setLoggedIn(appContext, 7);
        assertEquals(7, getLoggedIn(appContext));
    }

    @Test
    public void testSetLoggedOut() throws Exception {
        setLoggedIn(appContext, 3);
        assertEquals(3, getLoggedIn(appContext));

        setLoggedOut(appContext);
        assertEquals(-1, getLoggedIn(appContext));
    }

    @Test
    public void testGetLoggedInAsString() throws Exception {
        setLoggedIn(appContext, 3);
        assertEquals("3", getLoggedInAsString(appContext));

        setLoggedOut(appContext);
        assertEquals("-1", getLoggedInAsString(appContext));
    }
}