package placeme.ru.placemedemo;

import android.os.Bundle;
import android.support.design.widget.FloatingActionButton;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.CheckedTextView;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

import placeme.ru.placemedemo.core.utils.AuthorizationUtils;

public class RoutesActivity extends AppCompatActivity {

    final String[][] routes = {
            {"Zurich->St.Petersburg"}, {"Mc Donald's -> Burger King"}, {"London->Zurich"}
    };

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_routes);

        int index = AuthorizationUtils.getLoggedIn(this);

        ListView favouriteList = (ListView) findViewById(R.id.fav_routes);

        final ArrayAdapter<String> adapter;
        adapter = new ArrayAdapter<>(this, android.R.layout.simple_list_item_checked, routes[index]);
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

        FloatingActionButton fab = (FloatingActionButton) findViewById(R.id.fab);
        fab.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Toast.makeText(getApplicationContext(), "Route added!", Toast.LENGTH_SHORT).show();
            }
        });
    }

}
