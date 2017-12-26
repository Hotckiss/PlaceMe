package placeme.ru.placemedemo.ui;

import android.app.ProgressDialog;
import android.os.Bundle;
import android.support.v4.app.FragmentManager;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.widget.ArrayAdapter;
import android.widget.CheckedTextView;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

import java.util.ArrayList;

import placeme.ru.placemedemo.R;
import placeme.ru.placemedemo.core.database.DatabaseManager;
import placeme.ru.placemedemo.core.utils.AuthorizationUtils;
import placeme.ru.placemedemo.core.utils.FavouritePlacesUtils;
import placeme.ru.placemedemo.core.utils.RoutesUtils;
import placeme.ru.placemedemo.ui.views.PlacesListViewFragment;
import placeme.ru.placemedemo.ui.views.RoutesListViewFragment;
import util.Log;

/**
 * Activity that shows to user his favourite places
 */
public class FavouritePlacesActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_favourite_places);

        FragmentManager fragmentManager = getSupportFragmentManager();
        android.support.v4.app.Fragment fragment = fragmentManager.findFragmentById(R.id.places_fragment);

        if (fragment == null) {
            fragment = new PlacesListViewFragment();
            DatabaseManager.loadUserFavouritePlacesListNew(AuthorizationUtils.getLoggedInAsString(FavouritePlacesActivity.this), FavouritePlacesActivity.this, fragmentManager, fragment);

        }

    }

}