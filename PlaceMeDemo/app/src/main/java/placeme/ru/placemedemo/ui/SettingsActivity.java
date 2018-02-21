package placeme.ru.placemedemo.ui;

import android.app.Activity;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.res.Configuration;
import android.content.res.Resources;
import android.os.Build;
import android.support.annotation.RequiresApi;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.DisplayMetrics;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.CompoundButton;
import android.widget.ListView;
import android.widget.Spinner;
import android.widget.Switch;

import com.madrapps.pikolo.HSLColorPicker;
import com.madrapps.pikolo.listeners.SimpleColorSelectionListener;

import java.util.Locale;

import placeme.ru.placemedemo.R;
import placeme.ru.placemedemo.core.utils.ColorUtils;
import placeme.ru.placemedemo.core.utils.HelpUtils;

public class SettingsActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_settings);

        Switch switchHelp = findViewById(R.id.switch1);

        switchHelp.setChecked(HelpUtils.getHelp(this));

        switchHelp.setOnCheckedChangeListener((buttonView, isChecked) -> HelpUtils.setHelp(SettingsActivity.this, isChecked));

        final HSLColorPicker colorPicker = findViewById(R.id.colorPicker);
        colorPicker.setColor(ColorUtils.getColor(this));
        colorPicker.setColorSelectionListener(new SimpleColorSelectionListener() {
            @Override
            public void onColorSelected(int color) {
                ColorUtils.setColor(SettingsActivity.this, color);
            }
        });

        Button b = findViewById(R.id.language);
        b.setOnClickListener(v -> {
            AlertDialog.Builder builder = new AlertDialog.Builder(SettingsActivity.this);
            builder.setTitle("Change language");
            builder.setMessage("Select language");
            builder.setPositiveButton("Русский", new DialogInterface.OnClickListener() {
                @RequiresApi(api = Build.VERSION_CODES.JELLY_BEAN_MR1)
                @Override
                public void onClick(DialogInterface dialog, int which) {
                    Resources res = SettingsActivity.this.getResources();
                    DisplayMetrics dm = res.getDisplayMetrics();
                    Configuration conf = res.getConfiguration();
                    conf.setLocale(new Locale("ru"));
                    res.updateConfiguration(conf, dm);
                    dialog.dismiss();
                    SettingsActivity.this.recreate();
                }
            });
            builder.setNegativeButton("English", new DialogInterface.OnClickListener() {
                @RequiresApi(api = Build.VERSION_CODES.JELLY_BEAN_MR1)
                @Override
                public void onClick(DialogInterface dialog, int which) {
                    Resources res = SettingsActivity.this.getResources();
                    DisplayMetrics dm = res.getDisplayMetrics();
                    Configuration conf = res.getConfiguration();
                    conf.setLocale(new Locale("en"));
                    res.updateConfiguration(conf, dm);
                    dialog.dismiss();
                    SettingsActivity.this.recreate();
                }
            });
            builder.setNeutralButton(R.string.answer_cancel, (dialog, which) -> dialog.dismiss());
            builder.show();
        });
    }
}
