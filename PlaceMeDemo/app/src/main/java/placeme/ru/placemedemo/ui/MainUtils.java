package placeme.ru.placemedemo.ui;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.location.Location;
import android.support.v7.app.AlertDialog;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.EditText;
import android.widget.TextView;

import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.model.LatLng;

import java.util.ArrayList;

import de.hdodenhof.circleimageview.CircleImageView;
import geo.GeoObj;
import gl.GLFactory;
import placeme.ru.placemedemo.R;
import placeme.ru.placemedemo.core.Controller;
import placeme.ru.placemedemo.ui.dialogs.AlertDialogCreator;
import worldData.World;

import static placeme.ru.placemedemo.ui.dialogs.DialogUtils.setUpDialog;

/**
 * Class that helps to initialize main application interface
 * Created by Андрей on 04.02.2018.
 */
//TODO: doc
public class MainUtils {
    private static final String EMPTY_LINE = "";

    public static void plot(ArrayList<LatLng> points, final World world, GLFactory objectFactory, Location mLastKnownLocation) {
        if (points != null && points.size() > 0) {
            putLineToPoint(world, objectFactory, new LatLng(mLastKnownLocation.getLatitude(), mLastKnownLocation.getLongitude()), points.get(0));

            for (int i = 0; i < points.size() - 1; i++) {
                putLineToPoint(world, objectFactory, points.get(i), points.get(i + 1));
            }
            Location ls = new Location("");
            LatLng end = points.get(points.size() - 1);
            ls.setLatitude(end.latitude);
            ls.setLongitude(end.longitude);
            GeoObj endObj = new GeoObj(ls);
            endObj.setComp(objectFactory.newArrow());
            world.add(endObj);
        }
    }

    public static void checkLogin(MainActivity activity) {
        if (Controller.getLoggedIn(activity) == -1) {
            login(activity);
        }
    }

    public static void login(MainActivity activity) {
        Intent login = new Intent(activity, LoginActivity.class);
        login.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
        activity.startActivity(login);
        activity.finish();
    }

    public static AlertDialog saveRoute(final Activity activity, final GoogleMap googleMap) {
        final AlertDialog.Builder builder = new AlertDialog.Builder(activity);
        LayoutInflater inflater = (LayoutInflater) activity.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
        final View layout = inflater.inflate(R.layout.save_route, null);

        builder.setPositiveButton(R.string.answer_finish, (dialog, id) -> {
            EditText editTextDescription = layout.findViewById(R.id.route_description);
            String description = editTextDescription.getText().toString();
            if (description == null || description.length() == 0) {
                description = "No description given.";
            }
            Controller.saveRouteInfo(Controller.getLoggedInAsString(activity), Controller.getRoutesLength(activity), description);
            Controller.sendRoute(googleMap, activity);
            Controller.updateRoutesLength(Controller.getLoggedInAsString(activity), Controller.getRoutesLength(activity));
        }).setNegativeButton(R.string.answer_back, (dialog, arg1) -> {});

        builder.setCancelable(true);
        builder.setView(layout);
        return builder.create();
    }

    public static String getFieldValue(final EditText editText) {
        return editText.getText().toString();
    }

    public static void showInfo(final Context context) {
        AlertDialog.Builder builder = new AlertDialog.Builder(context);

        builder.setTitle("Information");
        builder.setMessage("1) Long tap on map creates marker\n2) Tap on created marker to create new place\n3) Tap on existing marker to load place info\n4) Most interface elements have tooltip by long click!");
        builder.setPositiveButton(R.string.answer_ok, (dialog, which) -> dialog.dismiss());

        builder.setCancelable(false);
        builder.show();
    }

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

    public static void loadProfileAvatar(final Context context, View view) {
        CircleImageView circleImageView = view.findViewById(R.id.profile_image);
        if (circleImageView != null) {
            Controller.loadAvatar(circleImageView, context, Controller.getLoggedInAsString(context));
        }
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
