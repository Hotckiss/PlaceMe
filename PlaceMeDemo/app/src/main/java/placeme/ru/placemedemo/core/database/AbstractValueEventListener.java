package placeme.ru.placemedemo.core.database;

import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.ValueEventListener;

import util.Log;

/**
 * A class that provides default implementation for some not necessary methods
 * Created by Андрей on 21.12.2017.
 */
public abstract class AbstractValueEventListener extends DatabaseTagsStorage implements ValueEventListener {
    /**
     * Method that prints error in the log if some errors occurred in database
     * @param firebaseError database error
     */
    @Override
    public void onCancelled(DatabaseError firebaseError) {
        Log.d(DATABASE_ERROR_TAG, firebaseError.getMessage());
    }
}
