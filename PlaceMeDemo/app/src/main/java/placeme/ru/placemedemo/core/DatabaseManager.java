package placeme.ru.placemedemo.core;

import com.google.firebase.database.ChildEventListener;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;

/**
 * Created by Андрей on 21.12.2017.
 */

public class DatabaseManager {

    private static FirebaseDatabase mBase;
    private static DatabaseReference mDatabaseReference;
    private static ChildEventListener childEventListener;
}
