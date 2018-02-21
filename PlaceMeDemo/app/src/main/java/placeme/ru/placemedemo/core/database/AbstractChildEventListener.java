package placeme.ru.placemedemo.core.database;

import com.google.firebase.database.ChildEventListener;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;

import util.Log;

/**
 * A class that allows not to implement some not necessary methods
 * Created by Андрей on 21.12.2017.
 */
public abstract class AbstractChildEventListener extends DatabaseTagsStorage implements ChildEventListener {
    /**
     * Default method that logs child change action
     * @param dataSnapshot moved data
     * @param previousChildKey previous child key in the database
     */
    @Override
    public void onChildChanged(DataSnapshot dataSnapshot, String previousChildKey) {
        if (dataSnapshot != null) {
            Log.d(DATABASE_CHILD_CHANGED_TAG, dataSnapshot.getKey());
        }
    }

    /**
     * Default method that logs removing child action
     * @param dataSnapshot removed data
     */
    @Override
    public void onChildRemoved(DataSnapshot dataSnapshot) {
        if (dataSnapshot != null) {
            Log.d(DATABASE_CHILD_REMOVED_TAG, dataSnapshot.getKey());
        }
    }

    /**
     * Default method that logs moving child action
     * @param dataSnapshot moved data
     * @param previousChildKey previous child key in the database
     */
    @Override
    public void onChildMoved(DataSnapshot dataSnapshot, String previousChildKey) {
        if (dataSnapshot != null) {
            Log.d(DATABASE_CHILD_MOVED_TAG, dataSnapshot.getKey());
        }
    }

    /**
     * Method that prints error in the log if some errors occurred in database
     * @param databaseError database error
     */
    @Override
    public void onCancelled(DatabaseError databaseError) {
        Log.d(DATABASE_ERROR_TAG, databaseError.getMessage());
    }
}