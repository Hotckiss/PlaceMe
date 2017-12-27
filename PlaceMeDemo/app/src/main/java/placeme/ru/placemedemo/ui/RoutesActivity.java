package placeme.ru.placemedemo.ui;

import android.os.Bundle;
import android.support.v4.app.FragmentManager;
import android.support.v7.app.AppCompatActivity;

import placeme.ru.placemedemo.R;
import placeme.ru.placemedemo.core.Controller;
import placeme.ru.placemedemo.ui.views.RoutesListViewFragment;

/**
 * Best routes activity that represents all routes of the user
 */
public class RoutesActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_routes);

        FragmentManager fragmentManager = getSupportFragmentManager();
        android.support.v4.app.Fragment fragment = fragmentManager.findFragmentById(
                R.id.fragmentContainer2);

        if (fragment == null) {
            fragment = new RoutesListViewFragment();
            Controller.getUserRoutesLength(Controller.getLoggedInAsString(
                    RoutesActivity.this), RoutesActivity.this,
                    fragmentManager, fragment);
        }
    }

}
