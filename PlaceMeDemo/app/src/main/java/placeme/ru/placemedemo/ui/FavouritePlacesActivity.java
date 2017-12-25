package placeme.ru.placemedemo.ui;

import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.widget.ArrayAdapter;
import android.widget.CheckedTextView;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

import placeme.ru.placemedemo.R;
import placeme.ru.placemedemo.core.database.DatabaseManager;
import placeme.ru.placemedemo.core.utils.AuthorizationUtils;

/**
 * Activity that shows to user his favourite places
 */
public class FavouritePlacesActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_favourite_places);

        ListView favouriteList = findViewById(R.id.fav_places);

        ArrayAdapter<String> adapter;
        adapter = new ArrayAdapter<>(this, android.R.layout.simple_list_item_checked);

        DatabaseManager.loadUserFavouritePlacesList(AuthorizationUtils.getLoggedInAsString(FavouritePlacesActivity.this), adapter);
        favouriteList.setAdapter(adapter);


        favouriteList.setOnItemClickListener((parent, itemClicked, position, id) -> {
            CheckedTextView textView = (CheckedTextView)itemClicked;
            textView.setChecked(!textView.isChecked());

            Toast.makeText(getApplicationContext(), ((TextView) itemClicked).getText(),
                    Toast.LENGTH_SHORT).show();
        });

        //Toolbar toolbar = findViewById(R.id.toolbar);
        //setSupportActionBar(toolbar);

    }

}
