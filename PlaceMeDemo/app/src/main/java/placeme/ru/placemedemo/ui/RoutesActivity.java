package placeme.ru.placemedemo.ui;

import android.os.Bundle;
import android.support.design.widget.FloatingActionButton;
import android.support.v4.app.FragmentManager;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.Toolbar;
import android.widget.ArrayAdapter;
import android.widget.CheckedTextView;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

import placeme.ru.placemedemo.R;
import placeme.ru.placemedemo.core.utils.AuthorizationUtils;
import placeme.ru.placemedemo.core.utils.RoutesUtils;
import placeme.ru.placemedemo.ui.views.HorizontalListViewFragment;
import placeme.ru.placemedemo.ui.views.RoutesListViewFragment;

/**
 * Best users roots activity
 */
public class RoutesActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_routes);
        RoutesUtils.getRoutesLength(this);
        FragmentManager fragmentManager = getSupportFragmentManager();
        android.support.v4.app.Fragment fragment = fragmentManager.findFragmentById(R.id.fragmentContainer2);

        if (fragment == null) {
            fragment = new RoutesListViewFragment();
            fragmentManager.beginTransaction().add(R.id.fragmentContainer2, fragment).commit();
        }
    }

}
