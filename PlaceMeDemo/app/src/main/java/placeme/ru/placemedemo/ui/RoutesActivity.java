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
import placeme.ru.placemedemo.ui.views.HorizontalListViewFragment;
import placeme.ru.placemedemo.ui.views.RoutesListViewFragment;

/**
 * Best users roots activity
 */
public class RoutesActivity extends AppCompatActivity {

    final String[][] routes = {
            {"Zurich->St.Petersburg"}, {"Mc Donald's -> Burger King"}, {"London->Zurich"}
    };

    private RecyclerView mRecyclerView;
    private RecyclerView.Adapter mAdapter;
    private RecyclerView.LayoutManager mLayoutManager;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_routes);

        FragmentManager fragmentManager = getSupportFragmentManager();
        android.support.v4.app.Fragment fragment = fragmentManager.findFragmentById(R.id.fragmentContainer2);

        if (fragment == null) {
            fragment = new RoutesListViewFragment();
            fragmentManager.beginTransaction().add(R.id.fragmentContainer2, fragment).commit();
        }

        /*int index = AuthorizationUtils.getLoggedIn(this);

        ListView favouriteList = findViewById(R.id.fav_routes);

        final ArrayAdapter<String> adapter;
        adapter = new ArrayAdapter<>(this, android.R.layout.simple_list_item_checked, routes[index]);
        favouriteList.setAdapter(adapter);


        favouriteList.setOnItemClickListener((parent, itemClicked, position, id) -> {
            CheckedTextView textView = (CheckedTextView)itemClicked;
            textView.setChecked(!textView.isChecked());

            Toast.makeText(getApplicationContext(), ((TextView) itemClicked).getText(),
                    Toast.LENGTH_SHORT).show();
        });

        Toolbar toolbar = findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

        FloatingActionButton fab = findViewById(R.id.fab);
        fab.setOnClickListener(view -> Toast.makeText(getApplicationContext(), "Route added!", Toast.LENGTH_SHORT).show());*/
    }

}
