package placeme.ru.placemedemo;

import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.CheckedTextView;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

import com.google.firebase.database.ChildEventListener;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.util.Arrays;

import placeme.ru.placemedemo.core.utils.AuthorizationUtils;

public class FavouritePlacesActivity extends AppCompatActivity {

    int ptr = 0;
    Integer[] ids;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_favourite_places);

        ListView favouriteList = (ListView) findViewById(R.id.fav_places);

        final ArrayAdapter<String> adapter;
        adapter = new ArrayAdapter<>(this, android.R.layout.simple_list_item_checked);

        final FirebaseDatabase mBase;
        DatabaseReference mDatabaseReference;
        ChildEventListener childEventListener;
        mBase = FirebaseDatabase.getInstance();
        mDatabaseReference = mBase.getReference().child("users").child(AuthorizationUtils.getLoggedInAsString(FavouritePlacesActivity.this)).child("favouritePlaces");
        //Log.d("ddd", mBase.getReference().child("users").child(((Integer)ChatUtils.getLoggedIn(FavouritePlacesActivity.this)).toString()).child("favouritePlaces").getKey());
        mDatabaseReference.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {

                //dataSnapshot.getKey();
                //Log.d("User",dataSnapshot.getRef().toString());
               // Log.d("User",dataSnapshot.getValue().toString());
                String places = dataSnapshot.getValue().toString();
                String[] tmp = places.split(",");
                ids = new Integer[tmp.length];
                int i = 0;

                for (String str : tmp) {
                    ids[i] = Integer.parseInt(str);
                    i++;
                }
                Arrays.sort(ids);
                DatabaseReference mDatabaseReference1;
                ChildEventListener childEventListener1;
                mDatabaseReference1 = mBase.getReference().child("places");
                childEventListener1 = new ChildEventListener() {

                    @Override
                    public void onChildAdded(DataSnapshot dataSnapshot, String s) {
                        Place place = (Place) dataSnapshot.getValue(Place.class);
                        Log.d("PLACE", place.getName());
                        Log.d("s", ((Integer)ptr).toString());
                        if (ptr < ids.length && ((Integer)place.getId()).equals(ids[ptr])) {

                            ptr++;
                            adapter.add(place.getName());

                        }
                        Log.d("s", ((Integer)ptr).toString());

                    }

                    @Override public void onChildChanged(DataSnapshot dataSnapshot, String s) {}
                    @Override public void onChildRemoved(DataSnapshot dataSnapshot) {}
                    @Override public void onChildMoved(DataSnapshot dataSnapshot, String s) {}
                    @Override public void onCancelled(DatabaseError databaseError) {}
                };
                mDatabaseReference1.addChildEventListener(childEventListener1);
               // Log.d("IDS", ids.length);
            }

            @Override
            public void onCancelled(DatabaseError firebaseError) {

                Log.d("User",firebaseError.getMessage() );
            }
        });

        Log.d("size", adapter.toString());
        favouriteList.setAdapter(adapter);


        favouriteList.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View itemClicked, int position, long id) {
                CheckedTextView textView = (CheckedTextView)itemClicked;
                textView.setChecked(!textView.isChecked());

                Toast.makeText(getApplicationContext(), ((TextView) itemClicked).getText(),
                        Toast.LENGTH_SHORT).show();
            }
        });

        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

    }

}
