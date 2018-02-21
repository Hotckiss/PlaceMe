package placeme.ru.placemedemo.core.utils;

import android.content.Context;
import android.support.test.InstrumentationRegistry;

import org.junit.Before;
import org.junit.Test;

import static org.junit.Assert.*;
import static placeme.ru.placemedemo.core.utils.FavouritePlacesUtils.*;
import static placeme.ru.placemedemo.core.utils.FavouritePlacesUtils.getPlaces;

/**
 * Tests for FavouritePlacesUtils class
 * Created by Андрей on 07.02.2018.
 */
public class FavouritePlacesUtilsTest {
    private Context appContext;

    @Before
    public void setOut() {
        appContext = InstrumentationRegistry.getTargetContext();
        setPlaces(appContext, "");
    }

    @Test
    public void testGetSetPlaces() {
        setPlaces(appContext, "3,4,5");
        assertEquals("3,4,5", getPlaces(appContext));

        setPlaces(appContext, "7,8,9");
        assertEquals("7,8,9", getPlaces(appContext));
    }
}