package placeme.ru.placemedemo.core.utils;

import android.net.Uri;

import com.google.android.gms.location.places.Place;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.LatLngBounds;

import org.junit.Before;
import org.junit.Test;

import java.util.ArrayList;
import java.util.List;
import java.util.Locale;

import static org.junit.Assert.assertEquals;

/**
 * Test converting google place to place
 * Created by Андрей on 07.02.2018.
 */
public class ConverterToPlaceTest {
    private Place googlePlace;

    @Test
    public void testConvert() {
        placeme.ru.placemedemo.elements.Place appPlace = ConverterToPlace.convertGooglePlaceToPlace(googlePlace);

        assertEquals("Test place", appPlace.getName());
        assertEquals("Test place\naddress\n+1234567890", appPlace.getDescription());
        assertEquals("банк,деньги,фитнес,спорт,спортзал,тренажёры", appPlace.getTags());
        assertEquals(7, appPlace.getLatitude(), 0.01);
        assertEquals(7, appPlace.getLongitude(), 0.01);
    }

    @Before
    public void init() {
        googlePlace = new Place() {
            @Override
            public String getId() {
                return "7";
            }

            @Override
            public List<Integer> getPlaceTypes() {
                List<Integer> ids = new ArrayList<>();
                ids.add(8);  // BANK
                ids.add(44); // GYM

                return ids;
            }

            @Override
            public CharSequence getAddress() {
                return "address";
            }

            @Override
            public Locale getLocale() {
                return null;
            }

            @Override
            public CharSequence getName() {
                return "Test place";
            }

            @Override
            public LatLng getLatLng() {
                return new LatLng(7, 7);
            }

            @Override
            public LatLngBounds getViewport() {
                return null;
            }

            @Override
            public Uri getWebsiteUri() {
                return null;
            }

            @Override
            public CharSequence getPhoneNumber() {
                return "+1234567890";
            }

            @Override
            public float getRating() {
                return 0;
            }

            @Override
            public int getPriceLevel() {
                return 0;
            }

            @Override
            public CharSequence getAttributions() {
                return null;
            }

            @Override
            public Place freeze() {
                return null;
            }

            @Override
            public boolean isDataValid() {
                return false;
            }
        };
    }
}