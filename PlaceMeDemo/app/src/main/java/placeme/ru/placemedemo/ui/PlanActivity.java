package placeme.ru.placemedemo.ui;

import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.widget.ArrayAdapter;
import android.widget.ListView;

import placeme.ru.placemedemo.R;
import placeme.ru.placemedemo.core.Controller;

public class PlanActivity extends AppCompatActivity {


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_plan);
        ListView planList = findViewById(R.id.plans);

        ArrayAdapter<String> adapter;
        adapter = new ArrayAdapter<>(this, android.R.layout.simple_list_item_1);

        Controller.loadPlan(Controller.getLoggedInAsString(PlanActivity.this), adapter);

        planList.setAdapter(adapter);
    }
}
