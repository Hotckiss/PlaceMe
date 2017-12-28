package placeme.ru.placemedemo.ui.dialogs;

import android.content.Context;
import android.content.DialogInterface;
import android.os.Build;
import android.support.annotation.RequiresApi;
import android.support.v7.app.AlertDialog;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.ArrayAdapter;
import android.widget.DatePicker;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.RatingBar;
import android.widget.SeekBar;
import android.widget.Switch;
import android.widget.TextView;
import android.widget.TimePicker;
import android.widget.Toast;

import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.model.LatLng;
import com.google.firebase.database.DatabaseReference;

import java.util.ArrayList;

import placeme.ru.placemedemo.R;
import placeme.ru.placemedemo.core.Controller;
import placeme.ru.placemedemo.core.database.DatabaseManager;
import placeme.ru.placemedemo.core.utils.AuthorizationUtils;
import placeme.ru.placemedemo.core.utils.SearchUtils;
import placeme.ru.placemedemo.elements.Place;
import placeme.ru.placemedemo.elements.User;

/**
 * Created by Андрей on 21.11.2017.
 */

/**
 * Class that contains methods to create most of the dialogs
 * between user and application
 */
public class AlertDialogCreator {
    private static ArrayList<LatLng> points = new ArrayList<>();

    /**
     * Helper method that returns last created route
     * It helps augmented reality part to build augmented reality
     * route between places
     * @return array of points that represents way through the places
     */
    public static ArrayList<LatLng> getPoints() {
        return points;
    }

    /**
     * Method that creates alert dialog with the results of user query
     * @param context current context
     * @param toFind user query
     * @param googleMap map where route will be possibly build
     * @param myPosition current user position
     * @return returns created alert dialog
     */
    public static AlertDialog createAlertDialogFounded(final Context context, final String toFind, final GoogleMap googleMap, final LatLng myPosition) {
        AlertDialog.Builder builder = new AlertDialog.Builder(context);
        LayoutInflater inflater = (LayoutInflater) context.getSystemService( Context.LAYOUT_INFLATER_SERVICE );
        final View layout = inflater.inflate(R.layout.list, null);

        builder.setView(layout);
        builder.setIcon(R.drawable.icon);
        builder.setTitle(R.string.query_result);

        final ListView lv = layout.findViewById(R.id.lv);
        lv.setChoiceMode(ListView.CHOICE_MODE_MULTIPLE);

        final ArrayAdapter<String> arrayAdapter = new ArrayAdapter<>(context, android.R.layout.select_dialog_multichoice);
        final ArrayList<Place> placeArrayList = new ArrayList<>();

        Controller.findPlacesByString(arrayAdapter, placeArrayList, toFind, myPosition, context);

        builder.setNegativeButton(R.string.answer_cancel, (dialog, which) -> dialog.dismiss());

        lv.setAdapter(arrayAdapter);

        lv.setOnItemLongClickListener((parent, view, position, id) -> {
            AlertDialog alertDialog = createInnerDescriptionDialog(position, context, placeArrayList);
            alertDialog.show();
            return false;
        });

        builder.setPositiveButton(R.string.answer_make_route, (dialog, which) -> {
            Controller.makeRoute(lv, myPosition, placeArrayList, context, googleMap, points);
            dialog.dismiss();
        });

        return builder.create();
    }

    /**
     * Method that creates alert dialog with the description of the place
     * and it's photo
     * @param context current context
     * @param place place that needs description
     * @param myPosition current user position
     * @param googleMap map where route will be possibly build
     * @return created alert dialog
     */
    public static AlertDialog createAlertDescriptionDialog(final Context context, final Place place, final LatLng myPosition, final GoogleMap googleMap) {
        final AlertDialog.Builder builder = new AlertDialog.Builder(context);
        LayoutInflater inflater = (LayoutInflater) context.getSystemService( Context.LAYOUT_INFLATER_SERVICE );
        final View layout = inflater.inflate(R.layout.dialog_description, null);

        builder.setTitle(place.getName());
        TextView descriptionText = layout.findViewById(R.id.descriptionText);
        descriptionText.setText(place.getDescription());

        final ImageView imageView = layout.findViewById(R.id.description_picture);
        Controller.loadDescriptionImage(imageView, place, context);

        RatingBar ratingBar = layout.findViewById(R.id.total_rating);
        ratingBar.setRating(place.getMark());

        builder.setPositiveButton(R.string.answer_go_here, (dialog, arg1) -> {
            Controller.makeSingleRoute(myPosition, new LatLng(place.getLatitude(), place.getLongitude()), context, googleMap, points);
        }).setNeutralButton("Actions",
                (dialog, id) -> {
                    AlertDialog alert = AlertDialogCreator.createAlertRateDialog(place,context);
                    alert.show();

                }).setNegativeButton("Reviews", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                AlertDialog.Builder reviewDialog = new AlertDialog.Builder(context);
                LayoutInflater inflater = (LayoutInflater) context.getSystemService( Context.LAYOUT_INFLATER_SERVICE );
                final View layoutInner = inflater.inflate(R.layout.add_review, null);
                final EditText reviewText = layoutInner.findViewById(R.id.review);
                reviewDialog.setTitle("Add review");
                reviewDialog.setPositiveButton("Submit review", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        String newReview = reviewText.getText().toString();
                        if(newReview.length() > 0) {
                            DatabaseManager.addReview(place.getIdAsString(), newReview);
                        }
                        dialog.dismiss();
                    }
                });

                reviewDialog.setNegativeButton(R.string.answer_cancel, new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        dialog.dismiss();
                    }
                });
                reviewDialog.setNeutralButton("Watch rewiews", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {

                        //***
                        AlertDialog.Builder builderList = new AlertDialog.Builder(context);
                        LayoutInflater inflaterList = (LayoutInflater) context.getSystemService( Context.LAYOUT_INFLATER_SERVICE );
                        final View layoutList = inflaterList.inflate(R.layout.list, null);

                        builderList.setView(layoutList);
                        builderList.setTitle("All reviews");

                        final ListView listView = layoutList.findViewById(R.id.lv);
                        //listView.setChoiceMode(ListView.CHOICE_MODE_MULTIPLE);

                        final ArrayAdapter<String> arrayAdapter = new ArrayAdapter<>(context, android.R.layout.simple_list_item_1);
                        //final ArrayList<Place> placeArrayList = new ArrayList<>();

                        DatabaseManager.findReviews(((Integer)place.getId()).toString(), arrayAdapter);

                        builderList.setNegativeButton(R.string.answer_cancel, (dialog1, which1) -> {});

                        listView.setAdapter(arrayAdapter);
                        builderList.create().show();
                        //***

                        dialog.dismiss();
                    }
                });
                reviewDialog.setCancelable(true);
                reviewDialog.setView(layoutInner);
                reviewDialog.create().show();
            }
        });

        builder.setCancelable(true);
        builder.setView(layout);
        return builder.create();

    }

    /**
     * Method that creates subdialog where user can rate any place with the mark between 0 and 5
     * @param place place to be rated
     * @param context current context
     * @return returns alert dialog with offer to rate place or add it to favourite
     */
    public static AlertDialog createAlertRateDialog(final Place place, final Context context) {
        final AlertDialog.Builder builder = new AlertDialog.Builder(context);
        LayoutInflater inflater = (LayoutInflater) context.getSystemService( Context.LAYOUT_INFLATER_SERVICE );
        final View layout = inflater.inflate(R.layout.dialog_rate_place, null);
        final RatingBar rb = layout.findViewById(R.id.rate_place);

        builder.setPositiveButton(R.string.answer_rate, (dialog, arg1) -> Controller.updatePlaceRating(rb, place.getIdAsString()));
        builder.setNeutralButton(R.string.answer_add, (dialog, id) -> Controller.addPlaceToFavourite(Controller.getLoggedInAsString(context), place.getIdAsString()));
        builder.setNegativeButton("Plan to visit", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                AlertDialog.Builder dataPick = new AlertDialog.Builder(context);
                LayoutInflater inflater = (LayoutInflater) context.getSystemService( Context.LAYOUT_INFLATER_SERVICE );
                final View layoutInner = inflater.inflate(R.layout.data_set, null);

                final DatePicker datePicker = layoutInner.findViewById(R.id.datePicker);

                dataPick.setTitle("Select date");
                dataPick.setNegativeButton(R.string.answer_cancel, new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        dialog.dismiss();
                    }
                });

                dataPick.setPositiveButton("Select", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        Integer day = datePicker.getDayOfMonth();
                        Integer month = datePicker.getMonth() + 1;
                        Integer year = datePicker.getYear();
                        //Log.d("dddd", ((Integer)month).toString());
                        dialog.dismiss();
                        //***
                        AlertDialog.Builder timePick = new AlertDialog.Builder(context);
                        LayoutInflater inflater = (LayoutInflater) context.getSystemService( Context.LAYOUT_INFLATER_SERVICE );
                        final View layoutInnerTime = inflater.inflate(R.layout.time_set, null);

                        final TimePicker timePicker = layoutInnerTime.findViewById(R.id.timePicker);

                        timePick.setTitle("Select time");
                        timePick.setNegativeButton(R.string.answer_cancel, new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialog, int which) {
                                dialog.dismiss();
                            }
                        });

                        timePick.setPositiveButton("Select", new DialogInterface.OnClickListener() {
                            @RequiresApi(api = Build.VERSION_CODES.M)
                            @Override
                            public void onClick(DialogInterface dialog, int which) {
                                Integer hours = timePicker.getHour();
                                Integer minutes = timePicker.getMinute();

                                DatabaseManager.addPlan(place.getName(), Controller.getLoggedInAsString(context), day.toString() + "-" + month.toString() + "-" + year.toString() + " "
                                + hours.toString() + ":" + minutes.toString());
                                dialog.dismiss();
                            }
                        });
                        timePick.setCancelable(true);
                        timePick.setView(layoutInnerTime);
                        timePick.create().show();
                        //***
                    }
                });
                dataPick.setCancelable(true);
                dataPick.setView(layoutInner);
                dataPick.create().show();
            }

        });
        builder.setCancelable(true);
        builder.setView(layout);
        return builder.create();
    }

    public static AlertDialog createSearchParametersDialog(final Context context) {
        final AlertDialog.Builder builder = new AlertDialog.Builder(context);
        LayoutInflater inflater = (LayoutInflater) context.getSystemService( Context.LAYOUT_INFLATER_SERVICE );
        final View layout = inflater.inflate(R.layout.dialog_search_parameters, null);

        builder.setNegativeButton(R.string.answer_ok, (dialog, arg1) -> dialog.cancel());


        ((TextView)layout.findViewById(R.id.distance_param)).setText(String.valueOf(SearchUtils.getDistanceSearchValue(context)));
        ((TextView)layout.findViewById(R.id.rating_param)).setText("> " + String.valueOf(SearchUtils.getRatingSearchValue(context) / 20.0) + " stars");

        final SeekBar seekBarDistance = layout.findViewById(R.id.seek_bar_distance);
        final SeekBar seekBarRating = layout.findViewById(R.id.seek_bar_rating);
        final Switch distanceSwitch = layout.findViewById(R.id.switch_dist);
        final Switch ratingSwitch = layout.findViewById(R.id.switch_rating);


        distanceSwitch.setChecked(SearchUtils.getDistanceSearchStatus(context));
        ratingSwitch.setChecked(SearchUtils.getRatingSearchStatus(context));
        seekBarDistance.setEnabled(SearchUtils.getDistanceSearchStatus(context));
        seekBarRating.setEnabled(SearchUtils.getRatingSearchStatus(context));

        seekBarDistance.setProgress(SearchUtils.getDistanceSearchValue(context));
        seekBarRating.setProgress(SearchUtils.getRatingSearchValue(context));

        distanceSwitch.setOnCheckedChangeListener((buttonView, isChecked) -> {
            if (isChecked) {
                seekBarDistance.setEnabled(true);
                SearchUtils.setDistanceSearchStatus(context,true);
            } else {
                seekBarDistance.setEnabled(false);
                SearchUtils.setDistanceSearchStatus(context,false);
            }
        });


        ratingSwitch.setOnCheckedChangeListener((buttonView, isChecked) -> {
            if (isChecked) {
                seekBarRating.setEnabled(true);
                SearchUtils.setRatingSearchStatus(context,true);
            } else {
                seekBarRating.setEnabled(false);
                SearchUtils.setRatingSearchStatus(context,false);
            }
        });

        seekBarDistance.setOnSeekBarChangeListener(new SeekBar.OnSeekBarChangeListener() {
            private final TextView mTextView = (TextView)layout.findViewById(R.id.distance_param);

            @Override
            public void onProgressChanged(SeekBar seekBar, int progress, boolean fromUser) {
                Integer dist =  progress;
                SearchUtils.setDistanceSearchValue(context, progress);
                mTextView.setText("< " + dist.toString() + " km");
            }

            @Override
            public void onStartTrackingTouch(SeekBar seekBar) {}

            @Override
            public void onStopTrackingTouch(SeekBar seekBar) {}
        });

        seekBarRating.setOnSeekBarChangeListener(new SeekBar.OnSeekBarChangeListener() {
            private final TextView mTextView = (TextView) layout.findViewById(R.id.rating_param);

            @Override
            public void onProgressChanged(SeekBar seekBar, int progress, boolean fromUser) {
                String intNumber;
                String afterDotNumber;
                Double rating;

                SearchUtils.setRatingSearchValue(context, progress);
                if (progress == 0) {
                    intNumber = "0";
                    afterDotNumber = "0";
                } else {
                    rating = (double) progress / 20.0;
                    intNumber = rating.toString().split("\\.")[0];
                    afterDotNumber = rating.toString().split("\\.")[1];//.charAt(0);
                }
                mTextView.setText("> " + intNumber + "." + afterDotNumber + " stars");
            }

            @Override
            public void onStartTrackingTouch(SeekBar seekBar) {}

            @Override
            public void onStopTrackingTouch(SeekBar seekBar) {}
        });

        builder.setCancelable(true);
        builder.setView(layout);
        return builder.create();
    }

    /**
     * Method that creates alert dialog with the results of user query
     * @param context current context
     * @param toFind user query
     * @return returns created alert dialog
     */
    public static AlertDialog createAlertDialogFoundedFriends(final Context context, final String toFind) {
        AlertDialog.Builder builder = new AlertDialog.Builder(context);
        LayoutInflater inflater = (LayoutInflater) context.getSystemService( Context.LAYOUT_INFLATER_SERVICE );
        final View layout = inflater.inflate(R.layout.list, null);

        builder.setView(layout);
        builder.setIcon(R.drawable.icon);
        builder.setTitle(R.string.query_result);

        final ListView lv = layout.findViewById(R.id.lv);
        lv.setChoiceMode(ListView.CHOICE_MODE_SINGLE);

        final ArrayAdapter<String> arrayAdapter = new ArrayAdapter<>(context, android.R.layout.select_dialog_multichoice);
        final ArrayList<User> users = new ArrayList<>();

        DatabaseManager.findUsersByString(arrayAdapter, users, toFind);

        builder.setNegativeButton(R.string.answer_cancel, (dialog, which) -> dialog.dismiss());

        lv.setAdapter(arrayAdapter);

        lv.setOnItemLongClickListener((parent, view, position, id) -> {
            AlertDialog alertDialog = createInnerDescriptionDialogUser(position, context, users);
            alertDialog.show();
            return false;
        });

        builder.setPositiveButton("Add to friends!", (dialog, which) -> {
            int position = lv.getCheckedItemPosition();
            if (position != -1) {
                DatabaseManager.addFriend(Controller.getLoggedInAsString(context), String.valueOf(users.get(position).getId()));
            }
            dialog.dismiss();
        });

        return builder.create();
    }

    private static AlertDialog createInnerDescriptionDialogUser(final int position, final Context context, final ArrayList<User> users) {
        AlertDialog.Builder builderInner = new AlertDialog.Builder(context);
        builderInner.setMessage(users.get(position).getName() + " " + users.get(position).getSurname());
        builderInner.setTitle("User info");
        builderInner.setPositiveButton(R.string.answer_ok, (dialog, which) -> dialog.dismiss());
        return builderInner.create();
    }

    private static AlertDialog createInnerDescriptionDialog(final int position, final Context context, final ArrayList<Place> placeArrayList) {
        AlertDialog.Builder builderInner = new AlertDialog.Builder(context);
        builderInner.setMessage(placeArrayList.get(position).getDescription());
        builderInner.setTitle(placeArrayList.get(position).getName());
        builderInner.setPositiveButton(R.string.answer_ok, (dialog, which) -> dialog.dismiss());
        return builderInner.create();
    }
}
