package placeme.ru.placemedemo.core.utils;

import android.content.Context;
import android.support.test.InstrumentationRegistry;

import org.junit.Before;
import org.junit.Test;

import static org.junit.Assert.*;
import static placeme.ru.placemedemo.core.utils.RoutesUtils.*;

/**
 * Tests for RoutesUtils class
 * Created by Андрей on 07.02.2018.
 */
public class RoutesUtilsTest {
    private Context appContext;

    @Before
    public void setOut() {
        appContext = InstrumentationRegistry.getTargetContext();

        setRoutesLength(appContext, 0);
    }

    @Test
    public void testGetSetRoutesLength() {
        setRoutesLength(appContext, 3);
        assertEquals(3, getRoutesLength(appContext).intValue());

        setRoutesLength(appContext, 7);
        assertEquals(7, getRoutesLength(appContext).intValue());
    }
}