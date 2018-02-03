package placeme.ru.placemedemo.ui.dialogs;

import android.content.Context;
import android.view.View;
import android.widget.SeekBar;
import android.widget.Switch;
import android.widget.TextView;

import placeme.ru.placemedemo.R;
import placeme.ru.placemedemo.core.Controller;

/**
 * Class that represents methods to help creating dialogs in application
 * Created by Андрей on 03.02.2018.
 */
public class DialogUtils {
    static void initDistanceSwitch(final Switch distanceSwitch, final Context context, final SeekBar seekBar) {
        distanceSwitch.setChecked(Controller.getDistanceSearchStatus(context));
        distanceSwitch.setOnCheckedChangeListener((buttonView, isChecked) -> {
            seekBar.setEnabled(isChecked);
            Controller.setDistanceSearchStatus(context,isChecked);
        });
    }

    static void initRatingSwitch(final Switch ratingSwitch, final Context context, final SeekBar seekBar) {
        ratingSwitch.setChecked(Controller.getRatingSearchStatus(context));
        ratingSwitch.setOnCheckedChangeListener((buttonView, isChecked) -> {
            seekBar.setEnabled(isChecked);
            Controller.setRatingSearchStatus(context, isChecked);
        });
    }

    static void initSeekBarDistance(final SeekBar seekBarDistance, final Context context, final View layout) {
        seekBarDistance.setEnabled(Controller.getDistanceSearchStatus(context));
        seekBarDistance.setProgress(Controller.getDistanceSearchValue(context));
        seekBarDistance.setOnSeekBarChangeListener(new SeekBar.OnSeekBarChangeListener() {
            private final TextView mTextView = (TextView)layout.findViewById(R.id.distance_param);

            @Override
            public void onProgressChanged(SeekBar seekBar, int progress, boolean fromUser) {
                Integer dist =  progress;
                Controller.setDistanceSearchValue(context, progress);
                mTextView.setText("< " + dist.toString() + " km");
            }

            @Override
            public void onStartTrackingTouch(SeekBar seekBar) {}

            @Override
            public void onStopTrackingTouch(SeekBar seekBar) {}
        });
    }

    static void initSeekBarRating(final SeekBar seekBarRating, final Context context, final View layout) {
        seekBarRating.setEnabled(Controller.getRatingSearchStatus(context));
        seekBarRating.setProgress(Controller.getRatingSearchValue(context));
        seekBarRating.setOnSeekBarChangeListener(new SeekBar.OnSeekBarChangeListener() {
            private final TextView mTextView = (TextView) layout.findViewById(R.id.rating_param);

            @Override
            public void onProgressChanged(SeekBar seekBar, int progress, boolean fromUser) {
                String intNumber;
                String afterDotNumber;
                Double rating;

                Controller.setRatingSearchValue(context, progress);
                if (progress == 0) {
                    intNumber = "0";
                    afterDotNumber = "0";
                } else {
                    rating = (double) progress / 20.0;
                    intNumber = rating.toString().split("\\.")[0];
                    afterDotNumber = rating.toString().split("\\.")[1];
                }
                mTextView.setText("> " + intNumber + "." + afterDotNumber + " stars");
            }

            @Override
            public void onStartTrackingTouch(SeekBar seekBar) {}

            @Override
            public void onStopTrackingTouch(SeekBar seekBar) {}
        });
    }
}
