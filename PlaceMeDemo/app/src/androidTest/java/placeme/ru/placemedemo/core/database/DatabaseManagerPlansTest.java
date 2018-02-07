package placeme.ru.placemedemo.core.database;

import android.content.Context;
import android.support.test.InstrumentationRegistry;
import android.widget.ArrayAdapter;

import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;

import org.junit.Test;

import static org.junit.Assert.*;
import static placeme.ru.placemedemo.core.database.DatabaseManagerPlans.*;

/**
 * Testing database manager plans
 * Created by Андрей on 07.02.2018.
 */
public class DatabaseManagerPlansTest {
    private Context context = InstrumentationRegistry.getTargetContext();

    @Test
    public void testAddPlan() throws Exception {
        DatabaseReference myRef = FirebaseDatabase.getInstance().getReference().child("plans/7");
        myRef.removeValue();
        addPlan("aaa", "7", "18-7-2018 12:00");
        Thread.sleep(2000);

        myRef.addListenerForSingleValueEvent(new AbstractValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                String data = dataSnapshot.getValue().toString();
                assertEquals("aaa\n18-7-2018 12:00", data);
            }
        });

        Thread.sleep(3000);
    }

    @Test
    public void testLoadPlan() throws Exception {
        DatabaseReference myRef = FirebaseDatabase.getInstance().getReference().child("plans/7");
        myRef.removeValue();
        addPlan("aaa", "7", "18-7-2018 12:00");
        Thread.sleep(2000);

        ArrayAdapter<String> array = new ArrayAdapter<>(context, android.R.layout.simple_list_item_1);
        loadPlan("7", array);
        Thread.sleep(2000);

        assertEquals(1, array.getCount());
        assertEquals("aaa\n18-7-2018 12:00", array.getItem(0));
    }

    @Test
    public void testAddReview() throws Exception {
        DatabaseReference myRef = FirebaseDatabase.getInstance().getReference().child("reviews/1000");
        myRef.removeValue();
        addReview("1000", "test");
        Thread.sleep(2000);

        myRef.addListenerForSingleValueEvent(new AbstractValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                String data = dataSnapshot.getValue().toString();
                assertEquals("test", data);
            }
        });

        Thread.sleep(2000);
    }

    @Test
    public void testFindReviews() throws Exception {
        DatabaseReference myRef = FirebaseDatabase.getInstance().getReference().child("reviews/1000");
        myRef.removeValue();
        addReview("1000", "test;test1;test3");
        Thread.sleep(2000);

        ArrayAdapter<String> array = new ArrayAdapter<>(context, android.R.layout.simple_list_item_1);
        findReviews("1000", array);
        Thread.sleep(2000);

        assertEquals(3, array.getCount());
        assertEquals("test", array.getItem(0));
        assertEquals("test1", array.getItem(1));
        assertEquals("test3", array.getItem(2));
    }
}