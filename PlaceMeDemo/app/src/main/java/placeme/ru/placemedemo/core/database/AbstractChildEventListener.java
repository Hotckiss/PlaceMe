package placeme.ru.placemedemo.core.database;

/**
 * Created by Андрей on 21.12.2017.
 */

import com.google.firebase.database.ChildEventListener;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;

import util.Log;

/**
 * A class that allows not to implement some not necessary methods
 */
public abstract class AbstractChildEventListener implements ChildEventListener {
    private static final String DATABASE_ERROR_TAG = "DATABASE_ERROR_TAG";

    @Override
    public void onChildChanged(DataSnapshot dataSnapshot, String s) {}

    @Override
    public void onChildRemoved(DataSnapshot dataSnapshot) {}

    @Override
    public void onChildMoved(DataSnapshot dataSnapshot, String s) {}

    @Override
    public void onCancelled(DatabaseError databaseError) {
        Log.d(DATABASE_ERROR_TAG, databaseError.getMessage());
    }
}