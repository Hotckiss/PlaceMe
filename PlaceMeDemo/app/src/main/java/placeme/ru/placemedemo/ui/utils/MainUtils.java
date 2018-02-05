package placeme.ru.placemedemo.ui.utils;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.location.Location;
import android.support.v7.app.AlertDialog;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.gms.location.places.Place;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.model.LatLng;

import java.util.ArrayList;

import de.hdodenhof.circleimageview.CircleImageView;
import geo.GeoObj;
import gl.GLFactory;
import placeme.ru.placemedemo.R;
import placeme.ru.placemedemo.core.Controller;
import placeme.ru.placemedemo.core.utils.ConverterToPlace;
import placeme.ru.placemedemo.ui.LoginActivity;
import placeme.ru.placemedemo.ui.MainActivity;
import placeme.ru.placemedemo.ui.dialogs.AlertDialogCreator;
import worldData.World;

import static com.google.android.gms.location.places.ui.PlaceAutocomplete.getPlace;
import static placeme.ru.placemedemo.ui.dialogs.DialogUtils.setUpDialog;

/**
 * Class that helps to initialize main application interface
 * Created by Андрей on 04.02.2018.
 */
public class MainUtils {
    private static final String INTENT_TYPE = "text/plain";
    private static final String SUCCESS_ADDITION = "Successfully added!";
    private static final String DEFAULT_DESCRIPTION = "No description given.";
    private static final String EMPTY_LINE = "";
    private static final String SHARE_TITLE = "Share using...";
    private static final String SHARE_MESSAGE = "Я пользуюсь приложением PlaceMe! Присоединяйся и ты: placeme.com :)";
    private static final String INFORMATION_MESSAGE = "1) Long tap on map creates marker\n" +
            "2) Tap on created marker to create new place\n" +
            "3) Tap on existing marker to load place info\n" +
            "4) Most interface elements have tooltip by long click!";

    /**
     * Method that plots route between list of points through AR world
     * @param points points to build route through them
     * @param world AR world
     * @param objectFactory factory that produce visible AR objects
     * @param mLastKnownLocation start point of the route
     */
    public static void plot(ArrayList<LatLng> points, final World world, GLFactory objectFactory, Location mLastKnownLocation) {
        if (points != null && points.size() > 0) {
            putLineToPoint(world, objectFactory, new LatLng(mLastKnownLocation.getLatitude(), mLastKnownLocation.getLongitude()), points.get(0));

            for (int i = 0; i < points.size() - 1; i++) {
                putLineToPoint(world, objectFactory, points.get(i), points.get(i + 1));
            }

            Location ls = new Location(EMPTY_LINE);
            LatLng end = points.get(points.size() - 1);
            ls.setLatitude(end.latitude);
            ls.setLongitude(end.longitude);
            GeoObj endObj = new GeoObj(ls);
            endObj.setComp(objectFactory.newArrow());
            world.add(endObj);
        }
    }

    /**
     * Method that checks whether user is logged in
     * @param activity current activity
     */
    public static void checkLogin(MainActivity activity) {
        if (Controller.getLoggedIn(activity) == -1) {
            login(activity);
        }
    }

    /**
     * Method that calls login activity if user currently not logged in
     * @param activity current activity
     */
    public static void login(MainActivity activity) {
        Intent login = new Intent(activity, LoginActivity.class);
        login.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
        activity.startActivity(login);
        activity.finish();
    }

    /**
     * Method that creates dialog to get info about route to save
     * @param activity current activity
     * @param googleMap map with route to save
     * @return created dialog
     */
    public static AlertDialog saveRoute(final Activity activity, final GoogleMap googleMap) {
        final AlertDialog.Builder builder = new AlertDialog.Builder(activity);
        LayoutInflater inflater = (LayoutInflater) activity.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
        final View layout = inflater.inflate(R.layout.save_route, null);

        builder.setPositiveButton(R.string.answer_finish, (dialog, id) -> {
            EditText editTextDescription = layout.findViewById(R.id.route_description);
            String description = editTextDescription.getText().toString();
            if (description == null || description.length() == 0) {
                description = DEFAULT_DESCRIPTION;
            }
            Controller.saveRouteInfo(Controller.getLoggedInAsString(activity), Controller.getRoutesLength(activity), description);
            Controller.sendRoute(googleMap, activity);
            Controller.updateRoutesLength(Controller.getLoggedInAsString(activity), Controller.getRoutesLength(activity));
        }).setNegativeButton(R.string.answer_back, (dialog, arg1) -> {});

        builder.setCancelable(true);
        builder.setView(layout);
        return builder.create();
    }

    /**
     * Method that returns string value from edit text field
     * @param editText field to extract text information
     * @return text from the field
     */
    public static String getFieldValue(final EditText editText) {
        return editText.getText().toString();
    }

    /**
     * Method that shows helpful information for beginners
     * @param context current context
     */
    public static void showInfo(final Context context) {
        AlertDialog.Builder builder = new AlertDialog.Builder(context);

        builder.setTitle(R.string.info_title);
        builder.setMessage(INFORMATION_MESSAGE);
        builder.setPositiveButton(R.string.answer_ok, (dialog, which) -> dialog.dismiss());

        builder.setCancelable(false);
        builder.show();
    }

    /**
     * Method that creates dialog where user can search friends
     * @param activity current activity
     * @return created dialog
     */
    public static AlertDialog searchFriends(final Activity activity) {
        final AlertDialog.Builder builder = new AlertDialog.Builder(activity);
        LayoutInflater inflater = (LayoutInflater) activity.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
        final View layout = inflater.inflate(R.layout.save_route, null);

        final TextView textView = layout.findViewById(R.id.route_title);

        textView.setText(R.string.search_friends);
        builder.setPositiveButton(R.string.search_start, (dialog, id) -> {
            EditText editTextDescription = layout.findViewById(R.id.route_description);
            String description = editTextDescription.getText().toString();
            if (description.length() == 0) {
                dialog.dismiss();
            }
            AlertDialogCreator.createAlertDialogFoundedFriends(activity, editTextDescription.getText().toString(), activity).show();
        });
        builder.setNegativeButton(R.string.answer_back, (dialog, arg1) -> dialog.dismiss());

        return setUpDialog(builder, layout);
    }

    /**
     * Method that loads user avatar into specific image view
     * @param context current context
     * @param view view that contains destination image view
     */
    public static void loadProfileAvatar(final Context context, View view) {
        CircleImageView circleImageView = view.findViewById(R.id.profile_image);
        if (circleImageView != null) {
            Controller.loadAvatar(circleImageView, context, Controller.getLoggedInAsString(context));
        }
    }

    /**
     * Method that converts and adds google place into application database
     * @param context current context
     * @param data intent with google place data
     */
    public static void addGooglePlace(Context context, Intent data) {
        Place place = getPlace(context, data);
        placeme.ru.placemedemo.elements.Place toAdd = ConverterToPlace.convertGooglePlaceToPlace(place);

        Controller.saveConvertedPlace(null, toAdd);
        Toast.makeText(context, SUCCESS_ADDITION, Toast.LENGTH_LONG).show();
    }

    /**
     * Method that extracts place information from the dialog where user input it
     * @param layout layout with edit text fields
     * @return returns place with no id but with all information about place
     */
    public static placeme.ru.placemedemo.elements.Place getPlaceInfo(final View layout) {
        EditText edName = layout.findViewById(R.id.place_name);
        EditText edDescription = layout.findViewById(R.id.place_description);
        EditText edTags = layout.findViewById(R.id.place_tags);

        return new placeme.ru.placemedemo.elements.Place(-1, getFieldValue(edName), getFieldValue(edDescription), getFieldValue(edTags), 0, 0);
    }

    /**
     * Method that allows user to share app with outer applications
     * @param activity current activity
     */
    public static void shareApplication(final Activity activity) {
        Intent sendIntent = new Intent();
        sendIntent.setAction(Intent.ACTION_SEND);
        sendIntent.putExtra(Intent.EXTRA_TEXT, SHARE_MESSAGE);
        sendIntent.setType(INTENT_TYPE);
        activity.startActivity(Intent.createChooser(sendIntent, SHARE_TITLE));
    }

    private static void putLineToPoint(final World world, GLFactory objectFactory, LatLng start, LatLng finish) {
        double maxx = Math.max(start.latitude, finish.latitude);
        double maxy = Math.max(start.longitude, finish.longitude);
        double minx = Math.min(start.latitude, finish.latitude);
        double miny = Math.min(start.longitude, finish.longitude);
        double dx = maxx - minx;
        double dy = maxy - miny;
        int iter = (int)(100 * Math.max(dx, dy)) + 3;
        if (dx * dx + dy * dy < 1e-14) {
            iter = 1;
        }
        for (int i = 0; i <= iter; i++) {
            Location l = new Location(EMPTY_LINE);
            l.setLatitude(start.latitude + (finish.latitude - start.latitude) * i / iter);
            l.setLongitude(start.longitude + (finish.longitude - start.longitude) * i / iter);
            GeoObj next = new GeoObj(l);
            next.setComp(objectFactory.newArrow());
            world.add(next);
        }
    }
}
