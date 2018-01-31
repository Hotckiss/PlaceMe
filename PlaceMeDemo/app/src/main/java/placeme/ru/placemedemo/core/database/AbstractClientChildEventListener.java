package placeme.ru.placemedemo.core.database;

import com.firebase.client.ChildEventListener;
import com.firebase.client.DataSnapshot;
import com.firebase.client.FirebaseError;
import util.Log;

/**
 * Created by Андрей on 26.12.2017.
 */

/**
 * A class that allows not to implement some not necessary methods
 */
public abstract class AbstractClientChildEventListener implements ChildEventListener {
    private static final String DATABASE_CLIENT_CHILD_CHANGED_TAG = "CHILD_CHANGED";
    private static final String DATABASE_CLIENT_CHILD_REMOVED_TAG = "CHILD_REMOVED";
    private static final String DATABASE_CLIENT_CHILD_MOVED_TAG = "CHILD_MOVED";
    private static final String DATABASE_CLIENT_ERROR_TAG = "DATABASE_ERROR";

    /**
     * Default method that logs child change action
     * @param dataSnapshot moved data
     * @param previousChildKey previous child key in the database
     */
    @Override
    public void onChildChanged(DataSnapshot dataSnapshot, String previousChildKey) {
        if (dataSnapshot != null) {
            Log.d(DATABASE_CLIENT_CHILD_CHANGED_TAG, dataSnapshot.getKey());
        }
    }

    /**
     * Default method that logs removing child action
     * @param dataSnapshot removed data
     */
    @Override
    public void onChildRemoved(DataSnapshot dataSnapshot) {
        if (dataSnapshot != null) {
            Log.d(DATABASE_CLIENT_CHILD_REMOVED_TAG, dataSnapshot.getKey());
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
            Log.d(DATABASE_CLIENT_CHILD_MOVED_TAG, dataSnapshot.getKey());
        }
    }

    /**
     * Method that prints error in the log if some errors occurred in database
     * @param databaseError database error
     */
    @Override
    public void onCancelled(FirebaseError databaseError) {
        Log.d(DATABASE_CLIENT_ERROR_TAG, databaseError.getMessage());
    }
}
