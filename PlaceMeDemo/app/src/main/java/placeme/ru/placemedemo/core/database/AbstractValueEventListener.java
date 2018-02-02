package placeme.ru.placemedemo.core.database;

/**
 * Created by Андрей on 21.12.2017.
 */

import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.ValueEventListener;

import util.Log;

/**
 * A class that provides default implementation for some not necessary methods
 */
public abstract class AbstractValueEventListener implements ValueEventListener {
    private static final String DATABASE_ERROR_TAG = "DATABASE_ERROR";

    /**
     * Method that prints error in the log if some errors occurred in database
     * @param firebaseError database error
     */
    @Override
    public void onCancelled(DatabaseError firebaseError) {
        Log.d(DATABASE_ERROR_TAG, firebaseError.getMessage());
    }
}