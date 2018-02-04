package placeme.ru.placemedemo.ui.dialogs;

import android.app.Activity;
import android.content.Context;
import android.os.Build;
import android.support.annotation.RequiresApi;
import android.support.v7.app.AlertDialog;
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

import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.model.LatLng;

import java.util.ArrayList;

import placeme.ru.placemedemo.R;
import placeme.ru.placemedemo.core.Controller;
import placeme.ru.placemedemo.elements.Place;
import placeme.ru.placemedemo.elements.User;

import static placeme.ru.placemedemo.ui.dialogs.DialogUtils.initDistanceSwitch;
import static placeme.ru.placemedemo.ui.dialogs.DialogUtils.initRatingSwitch;
import static placeme.ru.placemedemo.ui.dialogs.DialogUtils.initSeekBarDistance;
import static placeme.ru.placemedemo.ui.dialogs.DialogUtils.initSeekBarRating;
import static placeme.ru.placemedemo.ui.dialogs.DialogUtils.setUpDialog;

/**
 * Class that contains methods to create most of the dialogs
 * between user and application
 * Created by Андрей on 21.11.2017.
 */
public class AlertDialogCreator {
    private static final String GREATER = "> ";
    private static final String RATING_SUFFIX = " stars";
    private static final String DATE_DASH = "-";
    private static final String TIME_DOTS = ":";
    private static final String DATE_DELIMITER = " ";
    private static final double CONVERT_TO_RATING = 20.0;

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
    public static AlertDialog createAlertDialogFounded(final Context context, final String toFind,
                                                       final GoogleMap googleMap, final LatLng myPosition, final Activity activity) {
        AlertDialog.Builder builder = new AlertDialog.Builder(context);
        LayoutInflater inflater = (LayoutInflater) context.getSystemService( Context.LAYOUT_INFLATER_SERVICE );
        final View layout = inflater.inflate(R.layout.list, null);

        builder.setIcon(R.drawable.icon);
        builder.setTitle(R.string.query_result);

        final ListView listView = layout.findViewById(R.id.lv);
        final ArrayAdapter<String> arrayAdapter = new ArrayAdapter<>(context, android.R.layout.select_dialog_multichoice);
        final ArrayList<Place> placeArrayList = new ArrayList<>();

        Controller.findPlacesByStringV2(arrayAdapter, placeArrayList, toFind, myPosition, context, activity);
        initResultsList(listView, arrayAdapter, placeArrayList, context);

        builder.setNegativeButton(R.string.answer_cancel, (dialog, which) -> dialog.dismiss());
        builder.setPositiveButton(R.string.answer_make_route, (dialog, which) -> {
            Controller.makeRoute(listView, myPosition, placeArrayList, context, googleMap, points);
            dialog.dismiss();
        });

        return setUpDialog(builder, layout);
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
    @RequiresApi(api = Build.VERSION_CODES.M)
    public static AlertDialog createAlertDescriptionDialog(final Context context, final Place place, final LatLng myPosition, final GoogleMap googleMap) {
        final AlertDialog.Builder builder = new AlertDialog.Builder(context);
        LayoutInflater inflater = (LayoutInflater) context.getSystemService( Context.LAYOUT_INFLATER_SERVICE );
        final View layout = inflater.inflate(R.layout.dialog_description, null);
        final ImageView imageView = layout.findViewById(R.id.description_picture);
        RatingBar ratingBar = layout.findViewById(R.id.total_rating);

        builder.setTitle(place.getName());
        TextView descriptionText = layout.findViewById(R.id.descriptionText);
        descriptionText.setText(place.getDescription());
        Controller.loadDescriptionImage(imageView, place, context);
        ratingBar.setRating(place.getMark());

        builder.setPositiveButton(R.string.answer_go_here, (dialog, arg1) ->
                Controller.makeSingleRoute(myPosition, new LatLng(place.getLatitude(), place.getLongitude()), context, googleMap, points));
        builder.setNeutralButton(R.string.actions_button_text, (dialog, id) ->
                createAlertRateDialog(place,context).show());
        builder.setNegativeButton(R.string.reviews_button_text, (dialog, which) ->
                createReviewDialog(place, context).show());

        return setUpDialog(builder, layout);
    }

    /**
     * Method that creates dialog with settings of search parameters
     * @param context current context
     * @return created dialog with settings
     */
    public static AlertDialog createSearchParametersDialog(final Context context) {
        final AlertDialog.Builder builder = new AlertDialog.Builder(context);
        LayoutInflater inflater = (LayoutInflater) context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
        final View layout = inflater.inflate(R.layout.dialog_search_parameters, null);
        final SeekBar seekBarDistance = layout.findViewById(R.id.seek_bar_distance);
        final SeekBar seekBarRating = layout.findViewById(R.id.seek_bar_rating);
        final Switch distanceSwitch = layout.findViewById(R.id.switch_dist);
        final Switch ratingSwitch = layout.findViewById(R.id.switch_rating);

        String currentRating = GREATER +
                String.valueOf(Controller.getRatingSearchValue(context) / CONVERT_TO_RATING) + RATING_SUFFIX;
        ((TextView)layout.findViewById(R.id.distance_param))
                .setText(String.valueOf(Controller.getDistanceSearchValue(context)));
        ((TextView)layout.findViewById(R.id.rating_param))
                .setText(currentRating);
        initDistanceSwitch(distanceSwitch, context, seekBarDistance);
        initRatingSwitch(ratingSwitch, context, seekBarRating);
        initSeekBarDistance(seekBarDistance, context, layout);
        initSeekBarRating(seekBarRating, context, layout);
        builder.setNegativeButton(R.string.answer_ok, (dialog, arg) -> dialog.cancel());

        return setUpDialog(builder, layout);
    }

    /**
     * Method that creates alert dialog with the results of user query
     * @param context current context
     * @param toFind user query
     * @return returns created alert dialog
     */
    public static AlertDialog createAlertDialogFoundedFriends(final Context context, final String toFind, final Activity activity) {
        AlertDialog.Builder builder = new AlertDialog.Builder(context);
        LayoutInflater inflater = (LayoutInflater) context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
        final View layout = inflater.inflate(R.layout.list, null);

        builder.setIcon(R.drawable.icon);
        builder.setTitle(R.string.query_result);

        final ListView listView = layout.findViewById(R.id.lv);
        final ArrayAdapter<String> arrayAdapter = new ArrayAdapter<>(context, android.R.layout.select_dialog_multichoice);
        final ArrayList<User> users = new ArrayList<>();

        Controller.findUsersByStringV2(arrayAdapter, users, toFind, context, activity);
        listView.setChoiceMode(ListView.CHOICE_MODE_SINGLE);
        listView.setAdapter(arrayAdapter);
        listView.setOnItemLongClickListener((parent, view, position, id) -> {
            createInnerDescriptionDialogUser(position, context, users).show();
            return false;
        });

        builder.setNegativeButton(R.string.answer_cancel, (dialog, which) -> dialog.dismiss());
        builder.setPositiveButton(R.string.add_friend, (dialog, which) -> {
            int position = listView.getCheckedItemPosition();
            if (position != -1) {
                Controller.addFriend(Controller.getLoggedInAsString(context), String.valueOf(users.get(position).getId()));
            }
            dialog.dismiss();
        });

        return setUpDialog(builder, layout);
    }

    private static AlertDialog createInnerDescriptionDialogUser(final int position, final Context context, final ArrayList<User> users) {
        AlertDialog.Builder builderInner = new AlertDialog.Builder(context);
        builderInner.setMessage(users.get(position).getName() + DATE_DELIMITER + users.get(position).getSurname());
        builderInner.setTitle(R.string.user_info);
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

    private static void initResultsList(final ListView listView, final ArrayAdapter<String> arrayAdapter,
                                        final ArrayList<Place> placeArrayList, final Context context) {
        listView.setChoiceMode(ListView.CHOICE_MODE_MULTIPLE);
        listView.setAdapter(arrayAdapter);
        listView.setOnItemLongClickListener((parent, view, position, id) -> {
            createInnerDescriptionDialog(position, context, placeArrayList).show();
            return false;
        });
    }

    private static AlertDialog createReviewDialog(final Place place, final Context context) {
        AlertDialog.Builder reviewDialog = new AlertDialog.Builder(context);
        LayoutInflater inflater = (LayoutInflater) context.getSystemService( Context.LAYOUT_INFLATER_SERVICE );
        final View layoutInner = inflater.inflate(R.layout.add_review, null);
        final EditText reviewText = layoutInner.findViewById(R.id.review);

        reviewDialog.setTitle(R.string.reviews_add);
        reviewDialog.setPositiveButton(R.string.reviews_submit, (dialog14, which14) -> {
            uploadReview(reviewText, place);
            dialog14.dismiss();
        });
        reviewDialog.setNegativeButton(R.string.answer_cancel, (dialog12, which12) -> dialog12.dismiss());
        reviewDialog.setNeutralButton(R.string.reviews_watch, (dialog13, which13) -> {
            loadAllReviews(place, context).show();
            dialog13.dismiss();
        });

        return setUpDialog(reviewDialog, layoutInner);
    }

    private static AlertDialog loadAllReviews(final Place place, final Context context) {
        AlertDialog.Builder builder = new AlertDialog.Builder(context);
        LayoutInflater inflater = (LayoutInflater) context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
        final View layoutList = inflater.inflate(R.layout.list, null);
        final ListView listView = layoutList.findViewById(R.id.lv);
        final ArrayAdapter<String> arrayAdapter = new ArrayAdapter<>(context, android.R.layout.simple_list_item_1);

        builder.setTitle(R.string.reviews_all);
        Controller.findReviews(place.getIdAsString(), arrayAdapter);
        builder.setNegativeButton(R.string.answer_cancel, (dialog, which) -> dialog.dismiss());
        listView.setAdapter(arrayAdapter);

        return setUpDialog(builder, layoutList);
    }

    private static void uploadReview(final EditText editText, final Place place) {
        String review = editText.getText().toString();
        if(review.length() > 0) {
            Controller.addReview(place.getIdAsString(), review);
        }
    }

    @RequiresApi(api = Build.VERSION_CODES.M)
    private static AlertDialog createAlertRateDialog(final Place place, final Context context) {
        final AlertDialog.Builder builder = new AlertDialog.Builder(context);
        LayoutInflater inflater = (LayoutInflater) context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
        final View layout = inflater.inflate(R.layout.dialog_rate_place, null);
        final RatingBar rb = layout.findViewById(R.id.rate_place);

        builder.setPositiveButton(R.string.answer_rate, (dialog, arg1) ->
                Controller.updatePlaceRating(rb, place.getIdAsString()));
        builder.setNeutralButton(R.string.answer_add, (dialog, id) ->
                Controller.addPlaceToFavourite(Controller.getLoggedInAsString(context), place.getIdAsString()));
        builder.setNegativeButton(R.string.plan_visit, (dialog, which) ->
                createDatePickDialog(place, context).show());

        return setUpDialog(builder, layout);
    }

    @RequiresApi(api = Build.VERSION_CODES.M)
    private static AlertDialog createDatePickDialog(final Place place, final Context context) {
        AlertDialog.Builder builder = new AlertDialog.Builder(context);
        LayoutInflater inflater = (LayoutInflater) context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
        final View layoutInner = inflater.inflate(R.layout.data_set, null);
        final DatePicker datePicker = layoutInner.findViewById(R.id.datePicker);

        builder.setTitle(R.string.date_select);
        builder.setNegativeButton(R.string.answer_cancel, (dialog, which) -> dialog.dismiss());

        builder.setPositiveButton(R.string.button_select, (dialog, which) -> {
            Integer day = datePicker.getDayOfMonth();
            Integer month = datePicker.getMonth() + 1;
            Integer year = datePicker.getYear();

            createTimePickDialog(place, context, day.toString() + DATE_DASH +
                    month.toString() + DATE_DASH + year.toString()).show();
            dialog.dismiss();
        });

        return setUpDialog(builder, layoutInner);
    }

    @RequiresApi(api = Build.VERSION_CODES.M)
    private static AlertDialog createTimePickDialog(final Place place, final Context context, final String date) {
        AlertDialog.Builder timePick = new AlertDialog.Builder(context);
        LayoutInflater inflater1 = (LayoutInflater) context.getSystemService( Context.LAYOUT_INFLATER_SERVICE );
        final View layoutInnerTime = inflater1.inflate(R.layout.time_set, null);

        final TimePicker timePicker = layoutInnerTime.findViewById(R.id.timePicker);

        timePick.setTitle(R.string.time_select);
        timePick.setNegativeButton(R.string.answer_cancel, (dialog, which) -> dialog.dismiss());

        timePick.setPositiveButton(R.string.button_select, (dialog, which) -> {
            Integer hours = timePicker.getHour();
            Integer minutes = timePicker.getMinute();

            Controller.addPlan(place.getName(), Controller.getLoggedInAsString(context), date + DATE_DELIMITER
                    + hours.toString() + TIME_DOTS + minutes.toString());
            dialog.dismiss();
        });

        return setUpDialog(timePick, layoutInnerTime);
    }
}
