package placeme.ru.placemedemo.core.database;

import android.content.Context;
import android.support.test.InstrumentationRegistry;
import android.widget.TextView;

import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;

import org.junit.Test;

import java.util.HashMap;

import placeme.ru.placemedemo.core.Controller;

import static org.junit.Assert.*;
import static placeme.ru.placemedemo.core.database.DatabaseManagerRoutes.*;

/**
 * Test routes database manager
 * Created by Андрей on 07.02.2018.
 */
public class DatabaseManagerRoutesTest {
    private Context context = InstrumentationRegistry.getTargetContext();

    @Test
    public void testUpdateRoutesLength() throws Exception {
        updateRoutesLength("1000", 7);
        Thread.sleep(2000);

        DatabaseReference myRef = FirebaseDatabase.getInstance().getReference().child("users/1000");
        myRef.addListenerForSingleValueEvent(new AbstractValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                HashMap<String, Object> data = (HashMap<String, Object>) dataSnapshot.getValue();
                assertEquals("8", data.get("routesLength").toString());
            }
        });

        Thread.sleep(2000);
    }

    @Test
    public void testSaveRouteInfo() throws Exception {
        updateRoutesLength("1000", 7);
        Thread.sleep(2000);
        getUserRoutesLength2("1000", context);
        Thread.sleep(2000);

        assertEquals(8, Controller.getRoutesLength(context).intValue());
    }

    @Test
    public void testGetUserRoutesLength() throws Exception {
        saveRouteInfo("1000", 1L, "aaa");
        Thread.sleep(2000);

        DatabaseReference myRef = FirebaseDatabase.getInstance().getReference().child("routes_descriptions/1000/1");
        myRef.addListenerForSingleValueEvent(new AbstractValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                String data = dataSnapshot.getValue().toString();

                assertEquals("aaa", data);
            }
        });
    }

    @Test
    public void testFillDescription() throws Exception {
        saveRouteInfo("1000", 1L, "aaa");
        Thread.sleep(2000);

        TextView tv = new TextView(context);
        fillDescription(tv, 1, "1000");
        Thread.sleep(2000);

        assertEquals("aaa", tv.getText().toString());
    }
}