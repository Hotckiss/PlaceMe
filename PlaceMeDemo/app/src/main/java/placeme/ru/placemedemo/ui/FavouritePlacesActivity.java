package placeme.ru.placemedemo.ui;

import android.os.Bundle;
import android.support.v4.app.FragmentManager;
import android.support.v7.app.AppCompatActivity;

import placeme.ru.placemedemo.R;
import placeme.ru.placemedemo.core.Controller;
import placeme.ru.placemedemo.ui.views.PlacesListViewFragment;

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
            Controller.loadUserFavouritePlacesListV2(Controller.getLoggedInAsString(FavouritePlacesActivity.this),
                    FavouritePlacesActivity.this, fragmentManager, fragment);
        }
    }
}