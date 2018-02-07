package placeme.ru.placemedemo;

import android.content.Context;
import android.support.test.InstrumentationRegistry;
import android.support.test.runner.AndroidJUnit4;
import android.widget.RatingBar;
import android.widget.TextView;

import com.google.android.gms.maps.model.LatLng;
import com.google.firebase.database.ChildEventListener;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import org.junit.Test;
import org.junit.runner.RunWith;

import placeme.ru.placemedemo.core.database.AbstractChildEventListener;
import placeme.ru.placemedemo.core.database.AbstractValueEventListener;
import placeme.ru.placemedemo.elements.Place;
import util.Log;

import static org.junit.Assert.*;
import static placeme.ru.placemedemo.core.database.DatabaseManagerPlaces.saveCreatedPlace;

/**
 * Instrumentation test, which will execute on an Android device.
 *
 * @see <a href="http://d.android.com/tools/testing">Testing documentation</a>
 */
@RunWith(AndroidJUnit4.class)
public class ExampleInstrumentedTest {
    @Test
    public void useAppContext() throws Exception {
        // Context of the app under test.
        Context appContext = InstrumentationRegistry.getTargetContext();

        assertEquals("placeme.ru.placemedemo", appContext.getPackageName());
    }

    @Test
    public void useAppContext2() throws Exception {
        // Context of the app under test.
        Context appContext = InstrumentationRegistry.getTargetContext();
        FirebaseDatabase database = FirebaseDatabase.getInstance();
        DatabaseReference myRef = database.getReference().child("test");

        System.out.println("ttt0");
        myRef.setValue("Do you have data? You'll love Firebase. - 4");
        myRef.addListenerForSingleValueEvent(new AbstractValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                String txt = dataSnapshot.getValue().toString();
                System.out.println("ttt1");
                assertEquals("Do you have data? You'll love Firebase. - 3", txt);
            }
        });
        //Thread.sleep(2000);
        assertEquals("placeme.ru.placemedemo", appContext.getPackageName());
    }
}
