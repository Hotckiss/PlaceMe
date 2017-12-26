package placeme.ru.placemedemo.core.map;

import android.content.Context;
import android.graphics.Color;
import android.util.Log;
import android.util.SparseBooleanArray;
import android.widget.ListView;

import com.akexorcist.googledirection.DirectionCallback;
import com.akexorcist.googledirection.GoogleDirection;
import com.akexorcist.googledirection.constant.TransportMode;
import com.akexorcist.googledirection.model.Direction;
import com.akexorcist.googledirection.model.Leg;
import com.akexorcist.googledirection.model.Route;
import com.akexorcist.googledirection.model.Step;
import com.akexorcist.googledirection.request.DirectionDestinationRequest;
import com.akexorcist.googledirection.util.DirectionConverter;
import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.MarkerOptions;
import com.google.android.gms.maps.model.PolylineOptions;
import com.google.firebase.database.ChildEventListener;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;

import java.util.ArrayList;
import java.util.List;

import placeme.ru.placemedemo.core.database.DatabaseManager;
import placeme.ru.placemedemo.core.utils.AuthorizationUtils;
import placeme.ru.placemedemo.elements.Place;

/**
 * Created by Андрей on 21.11.2017.
 */

/**
 * Class that allows application to work with google maps
 */
public class MapManager {
    private static final String MAPS_API_KEY = "AIzaSyD_WcUAMqVEVW0H84GsXLKBr0HokiO-v_4";
    /**
     * Method that loads all markers from to the map
     * @param googleMap markers destination map
     */
    public static void addAllMarkers(final GoogleMap googleMap) {
        DatabaseManager.loadMarkersToMap(googleMap);
    }

    /**
     * Method that cleans map and loads all markers after that
     * @param googleMap markers destination map
     */
    public static void refreshMarkers(final GoogleMap googleMap) {
        if (googleMap != null) {
            googleMap.clear();
            DatabaseManager.loadMarkersToMap(googleMap);
        }
    }

    /**
     * Method that loads all markers by user query to the map
     * @param googleMap markers destination map
     * @param toFind user search query
     */
    public static void addFoundedMarkers(final GoogleMap googleMap, final String toFind) {
        DatabaseManager.addMarkersByQuery(googleMap, toFind);
    }

    /**
     * Method that builds multi route between many points and places it to the map.
     * Furthermore it saves it to database immediately and moves camera to the destination point
     * @param listView list with the results of query, where user choose places to visit
     * @param myPosition current user position
     * @param placeArrayList arrau list with descriptions of places
     * @param context current context
     * @param googleMap map where route will be possibly build
     * @param points storage of route points which is important for roite in augmented reality
     */
    public static void makeRoute(final ListView listView, final LatLng myPosition, final ArrayList<Place> placeArrayList, final Context context, final GoogleMap googleMap, final ArrayList<LatLng> points) {
        SparseBooleanArray sp = listView.getCheckedItemPositions();

        final LatLng origin = myPosition;
        LatLng destination = myPosition;
        DirectionDestinationRequest gd = GoogleDirection.withServerKey(MAPS_API_KEY).from(origin);
        ArrayList<LatLng> route = new ArrayList<>();
        route.add(myPosition);
        int lastPoint = -1;
        for (int i = 0; i < placeArrayList.size(); i++) {
            if (sp.get(i)) {
                lastPoint = i;
                route.add(new LatLng(placeArrayList.get(i).getLatitude(), placeArrayList.get(i).getLongitude()));
            }
        }

        DatabaseManager.saveRoute(AuthorizationUtils.getLoggedInAsString(context), route);
        if (lastPoint != -1) {
            destination = new LatLng(placeArrayList.get(lastPoint).getLatitude(), placeArrayList.get(lastPoint).getLongitude());
        }

        for(int i = 0; i < placeArrayList.size(); i++) {
            if (sp.get(i)) {
                if (i != lastPoint) {
                    gd.and(new LatLng(placeArrayList.get(i).getLatitude(), placeArrayList.get(i).getLongitude()));
                }
            }
            Log.d(((Integer)i).toString(), ((Boolean)sp.get(i)).toString());
        }

        gd.to(destination)
                .transportMode(TransportMode.WALKING)
                .execute(new DirectionCallback() {
                    @Override
                    public void onDirectionSuccess(Direction direction, String rawBody) {
                        if (direction.isOK()) {

                            Route route = direction.getRouteList().get(0);
                            int legCount = route.getLegList().size();
                            for (int index = 0; index < legCount; index++) {
                                Leg leg = route.getLegList().get(index);
                                googleMap.addMarker(new MarkerOptions().position(leg.getStartLocation().getCoordination()));
                                if (index == legCount - 1) {
                                    googleMap.addMarker(new MarkerOptions().position(leg.getEndLocation().getCoordination()));
                                }
                                List<Step> stepList = leg.getStepList();
                                ArrayList<PolylineOptions> polylineOptionList = DirectionConverter.createTransitPolyline(context, stepList, 5, Color.RED, 3, Color.BLUE);

                                for (PolylineOptions polylineOption : polylineOptionList) {
                                    points.addAll(polylineOption.getPoints());
                                    googleMap.addPolyline(polylineOption);
                                }
                            }
                        }
                    }

                    @Override
                    public void onDirectionFailure(Throwable t) {}
                });
        googleMap.animateCamera(CameraUpdateFactory.newLatLngZoom(destination, 15.0f));
    }

    /**
     * Method that builds single route between two places
     * @param myPosition current user position
     * @param destination destination point of route
     * @param context current context
     * @param googleMap map where route should be build
     * @param points storage of route points which is important for roite in augmented reality
     */
    public static void makeSingleRoute(final LatLng myPosition, final LatLng destination, final Context context, final GoogleMap googleMap, final ArrayList<LatLng> points) {

        final LatLng origin = myPosition;
        DirectionDestinationRequest gd = GoogleDirection.withServerKey(MAPS_API_KEY).from(origin);
        ArrayList<LatLng> route = new ArrayList<>();
        route.add(myPosition);
        route.add(destination);

        DatabaseManager.saveRoute(AuthorizationUtils.getLoggedInAsString(context), route);

        gd.to(destination)
                .transportMode(TransportMode.WALKING)
                .execute(new DirectionCallback() {
                    @Override
                    public void onDirectionSuccess(Direction direction, String rawBody) {
                        if (direction.isOK()) {

                            Route route = direction.getRouteList().get(0);
                            int legCount = route.getLegList().size();
                            for (int index = 0; index < legCount; index++) {
                                Leg leg = route.getLegList().get(index);
                                googleMap.addMarker(new MarkerOptions().position(leg.getStartLocation().getCoordination()));
                                if (index == legCount - 1) {
                                    googleMap.addMarker(new MarkerOptions().position(leg.getEndLocation().getCoordination()));
                                }
                                List<Step> stepList = leg.getStepList();
                                ArrayList<PolylineOptions> polylineOptionList = DirectionConverter.createTransitPolyline(context, stepList, 5, Color.RED, 3, Color.BLUE);

                                for (PolylineOptions polylineOption : polylineOptionList) {
                                    points.addAll(polylineOption.getPoints());
                                    googleMap.addPolyline(polylineOption);
                                }
                            }
                        }
                    }

                    @Override
                    public void onDirectionFailure(Throwable t) {}
                });
        googleMap.animateCamera(CameraUpdateFactory.newLatLngZoom(destination, 15.0f));
    }
}
