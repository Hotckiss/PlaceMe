package placeme.ru.placemedemo.ui.dialogs;

import android.content.Context;
import android.support.v7.app.AlertDialog;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.ArrayAdapter;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.RatingBar;
import android.widget.TextView;

import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.model.LatLng;

import java.util.ArrayList;

import placeme.ru.placemedemo.R;
import placeme.ru.placemedemo.core.database.DatabaseManager;
import placeme.ru.placemedemo.core.map.MapManager;
import placeme.ru.placemedemo.core.utils.AuthorizationUtils;
import placeme.ru.placemedemo.elements.Place;

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

        DatabaseManager.findPlacesByString(arrayAdapter, placeArrayList, toFind);

        builder.setNegativeButton(R.string.answer_cancel, (dialog, which) -> dialog.dismiss());

        lv.setAdapter(arrayAdapter);

        lv.setOnItemLongClickListener((parent, view, position, id) -> {
            AlertDialog alertDialog = createInnerDescriptionDialog(position, context, placeArrayList);
            alertDialog.show();
            return false;
        });

        builder.setPositiveButton(R.string.answer_make_route, (dialog, which) -> {
            MapManager.makeRoute(lv, myPosition, placeArrayList, context, googleMap, points);
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
        DatabaseManager.loadDescriptionImage(imageView, place, context);

        RatingBar ratingBar = layout.findViewById(R.id.total_rating);
        ratingBar.setRating(place.getMark());

        builder.setPositiveButton(R.string.answer_go_here, (dialog, arg1) -> {
            MapManager.makeSingleRoute(myPosition, new LatLng(place.getLatitude(), place.getLongitude()), context, googleMap, points);
        }).setNeutralButton(R.string.answer_rate_place,
                (dialog, id) -> {
                    AlertDialog alert = AlertDialogCreator.createAlertRateDialog(place,context);
                    alert.show();

                }).setNegativeButton(R.string.answer_ok, (dialog, arg1) -> dialog.cancel());

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

        builder.setPositiveButton(R.string.answer_rate, (dialog, arg1) -> DatabaseManager.updatePlaceRating(rb, place.getIdAsString()));
        builder.setNeutralButton(R.string.answer_add, (dialog, id) -> DatabaseManager.addPlaceToFavourite(AuthorizationUtils.getLoggedInAsString(context), place.getIdAsString()));
        builder.setNegativeButton(R.string.answer_ok, (dialog, arg1) -> dialog.cancel());
        builder.setCancelable(true);
        builder.setView(layout);
        return builder.create();
    }

    private static AlertDialog createInnerDescriptionDialog(final int position, final Context context, final ArrayList<Place> placeArrayList) {
        AlertDialog.Builder builderInner = new AlertDialog.Builder(context);
        builderInner.setMessage(placeArrayList.get(position).getDescription());
        builderInner.setTitle(placeArrayList.get(position).getName());
        builderInner.setPositiveButton(R.string.answer_ok, (dialog, which) -> dialog.dismiss());
        return builderInner.create();
    }
}
