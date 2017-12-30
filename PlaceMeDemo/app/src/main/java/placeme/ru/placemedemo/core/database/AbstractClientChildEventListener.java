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
    private static final String DATABASE_ERROR_TAG = "DATABASE_ERROR_TAG";

    @Override
    public void onChildChanged(DataSnapshot dataSnapshot, String s) {}

    @Override
    public void onChildRemoved(DataSnapshot dataSnapshot) {}

    @Override
    public void onChildMoved(DataSnapshot dataSnapshot, String s) {}

    /**
     * Method that prints error in the log if some errors occurred in database
     * @param databaseError database error
     */
    @Override
    public void onCancelled(FirebaseError databaseError) {
        Log.d(DATABASE_ERROR_TAG, databaseError.getMessage());
    }
}
