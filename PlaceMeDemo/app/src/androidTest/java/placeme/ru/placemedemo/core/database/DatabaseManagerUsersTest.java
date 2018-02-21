package placeme.ru.placemedemo.core.database;

import android.content.Context;
import android.support.test.InstrumentationRegistry;
import android.widget.TextView;

import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;

import org.junit.Test;


import placeme.ru.placemedemo.elements.AuthData;
import placeme.ru.placemedemo.elements.User;

import static org.junit.Assert.*;
import static placeme.ru.placemedemo.core.database.DatabaseManagerUsers.*;

/**
 * Testing database manager users
 * Created by Андрей on 07.02.2018.
 */
public class DatabaseManagerUsersTest {
    private Context context = InstrumentationRegistry.getTargetContext();
    private String current = null;

    @Test
    public void testRegisterUser() throws Exception {
        current = null;
        registerUser(new AuthData(-1, "a@b", "12345"), new User(-1, "test", "tst", "test"));
        Thread.sleep(2000);
        DatabaseReference reference = FirebaseDatabase.getInstance().getReference().child("maxid");
        reference.addListenerForSingleValueEvent(new AbstractValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                current = dataSnapshot.getValue().toString();
            }
        });
        Thread.sleep(2000);
        if (current != null) {
            DatabaseReference reference1 = FirebaseDatabase.getInstance().getReference().child("users/" + (Integer.parseInt(current) - 1) + "/name");

            reference1.addListenerForSingleValueEvent(new AbstractValueEventListener() {
                @Override
                public void onDataChange(DataSnapshot dataSnapshot) {
                    Object obj = dataSnapshot.getValue();

                    assertEquals("test", obj.toString());
                }
            });
            Thread.sleep(2000);
        }
    }

    @Test
    public void testLoadFriendName() throws Exception {
        TextView tv = new TextView(context);

        loadFriendName(1, tv);
        Thread.sleep(2000);
        assertEquals("Andrey", tv.getText().toString());
    }

    @Test
    public void testAddPlaceToFavourite() throws Exception {
        addPlaceToFavourite("1000", "121");
        Thread.sleep(2000);

        DatabaseReference reference = FirebaseDatabase.getInstance().getReference().child("users/1000/favouritePlaces");

        reference.addListenerForSingleValueEvent(new AbstractValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                Object obj = dataSnapshot.getValue();

                if (obj != null) {
                    assertEquals("121", obj.toString());
                }
            }
        });
        Thread.sleep(2000);
    }
}