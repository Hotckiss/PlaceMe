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
import placeme.ru.placemedemo.ui.views.RoutesListViewFragment;

/**
 * Best routes activity that represents all routes of the user
 */
public class RoutesActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_routes);
        Toolbar toolbar = findViewById(R.id.toolbar_routes);
        setSupportActionBar(toolbar);
        try {
            getSupportActionBar().setTitle(R.string.my_routes_title);
        } catch (Exception ex) {
            Log.d("ERROR", "toolbar not found");
            ex.printStackTrace();
        }

        //toolbar.setBackground(new ColorDrawable(ColorUtils.getColor(this)));
        FragmentManager fragmentManager = getSupportFragmentManager();
        android.support.v4.app.Fragment fragment = fragmentManager.findFragmentById(R.id.fragmentContainer2);

        if (fragment == null) {
            fragment = new RoutesListViewFragment();
            Controller.getUserRoutesLength(Controller.getLoggedInAsString(RoutesActivity.this),
                    RoutesActivity.this, fragmentManager, fragment);
        }
    }
}
