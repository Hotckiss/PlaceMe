package placeme.ru.placemedemo.ui;

import android.graphics.drawable.ColorDrawable;
import android.os.Bundle;
import android.support.v4.app.FragmentManager;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.util.Log;

import placeme.ru.placemedemo.R;
import placeme.ru.placemedemo.core.Controller;
import placeme.ru.placemedemo.core.utils.ColorUtils;
import placeme.ru.placemedemo.ui.views.PlacesListViewFragment;

/**
 * Activity that shows to user his favourite places
 */
public class FavouritePlacesActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_favourite_places);
        Toolbar toolbar = findViewById(R.id.toolbar_places);
        setSupportActionBar(toolbar);
        try {
            getSupportActionBar().setTitle(R.string.my_places);
        } catch (Exception ex) {
            Log.d("ERROR", "toolbar not found");
            ex.printStackTrace();
        }
        //toolbar.setBackground(new ColorDrawable(ColorUtils.getColor(this)));
        FragmentManager fragmentManager = getSupportFragmentManager();
        android.support.v4.app.Fragment fragment = fragmentManager.findFragmentById(R.id.places_fragment);

        if (fragment == null) {
            fragment = new PlacesListViewFragment();
            Controller.loadUserFavouritePlacesListV2(Controller.getLoggedInAsString(FavouritePlacesActivity.this),
                    FavouritePlacesActivity.this, fragmentManager, fragment);
        }
    }
}