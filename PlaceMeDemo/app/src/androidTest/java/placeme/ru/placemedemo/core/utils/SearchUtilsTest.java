package placeme.ru.placemedemo.core.utils;

import android.content.Context;
import android.support.test.InstrumentationRegistry;

import org.junit.Before;
import org.junit.Test;

import static org.junit.Assert.*;
import static placeme.ru.placemedemo.core.utils.SearchUtils.*;

/**
 * Tests for SearchUtils class
 * Created by Андрей on 07.02.2018.
 */
public class SearchUtilsTest {
    private Context appContext;

    @Before
    public void init() {
        appContext = InstrumentationRegistry.getTargetContext();

        setDistanceSearchStatus(appContext, false);
        setRatingSearchStatus(appContext, false);
        setDistanceSearchValue(appContext, 50);
        setRatingSearchValue(appContext, 50);
    }

    @Test
    public void testGetSetDistanceSearchStatus() {
        setDistanceSearchStatus(appContext, true);
        assertTrue(getDistanceSearchStatus(appContext));

        setDistanceSearchStatus(appContext, false);
        assertFalse(getDistanceSearchStatus(appContext));
    }

    @Test
    public void testGetSetRatingSearchStatus() {
        setRatingSearchStatus(appContext, true);
        assertTrue(getRatingSearchStatus(appContext));

        setRatingSearchStatus(appContext, false);
        assertFalse(getRatingSearchStatus(appContext));
    }

    @Test
    public void testGetSetDistanceSearchValue() {
        setDistanceSearchValue(appContext, 25);
        assertEquals(25, getDistanceSearchValue(appContext));

        setDistanceSearchValue(appContext, 75);
        assertEquals(75, getDistanceSearchValue(appContext));
    }

    @Test
    public void testGetSetRatingSearchValue() {
        setRatingSearchValue(appContext, 25);
        assertEquals(25, getRatingSearchValue(appContext));

        setRatingSearchValue(appContext, 75);
        assertEquals(75, getRatingSearchValue(appContext));
    }
}