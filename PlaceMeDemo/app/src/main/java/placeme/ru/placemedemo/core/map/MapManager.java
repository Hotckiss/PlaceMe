package placeme.ru.placemedemo.core.map;

import android.content.Context;
import android.graphics.Color;
import android.util.SparseBooleanArray;
import android.widget.ListView;
import android.widget.Toast;

import com.akexorcist.googledirection.DirectionCallback;
import com.akexorcist.googledirection.GoogleDirection;
import com.akexorcist.googledirection.constant.TransportMode;
import com.akexorcist.googledirection.model.Direction;
import com.akexorcist.googledirection.model.Leg;
import com.akexorcist.googledirection.model.Step;
import com.akexorcist.googledirection.request.DirectionDestinationRequest;
import com.akexorcist.googledirection.util.DirectionConverter;
import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.MarkerOptions;
import com.google.android.gms.maps.model.PolylineOptions;

import java.util.ArrayList;
import java.util.List;

import placeme.ru.placemedemo.core.Controller;
import placeme.ru.placemedemo.core.database.DatabaseManagerPlaces;
import placeme.ru.placemedemo.elements.Place;

/**
 * Class that allows application to work with google maps
 * Created by Андрей on 21.11.2017.
 */
public class MapManager {
    private static final String MAPS_API_KEY = "AIzaSyD_WcUAMqVEVW0H84GsXLKBr0HokiO-v_4";
    private static final String ERROR_MESSAGE = "Error loading route. Try again.";
    /**
     * Method that builds multi route between many points and places it to the map.
     * Furthermore it saves it to database immediately and moves camera to the destination point
     * @param listView list with the results of query, where user choose places to visit
     * @param myPosition current user position
     * @param placeArrayList array list with descriptions of places
     * @param context current context
     * @param googleMap map where route will be possibly build
     * @param points storage of route points which is important for route in augmented reality
     */
    public static void makeRoute(final ListView listView, final LatLng myPosition, final ArrayList<Place> placeArrayList,
                                 final Context context, final GoogleMap googleMap, final ArrayList<LatLng> points) {
        SparseBooleanArray sp = listView.getCheckedItemPositions();
        LatLng destination = myPosition;
        DirectionDestinationRequest gd = getRequest(myPosition);
        int lastPoint = -1;

        for (int i = 0; i < placeArrayList.size(); i++) {
            if (sp.get(i)) {
                lastPoint = i;
            }
        }

        if (lastPoint != -1) {
            destination = getPosition(placeArrayList, lastPoint);
        }

        for (int i = 0; i < placeArrayList.size(); i++) {
            if (sp.get(i) && i != lastPoint) {
                gd.and(getPosition(placeArrayList, i));
            }
        }

        plotRoute(gd, destination, googleMap, points, context);
    }

    /**
     * Method that builds single route between two places
     * @param myPosition current user position
     * @param destination destination point of route
     * @param context current context
     * @param googleMap map where route should be build
     * @param points storage of route points which is important for route in augmented reality
     */
    public static void makeSingleRoute(final LatLng myPosition, final LatLng destination,
                                       final Context context, final GoogleMap googleMap, final ArrayList<LatLng> points) {
        DirectionDestinationRequest gd  = getRequest(myPosition);

        plotRoute(gd, destination, googleMap, points, context);
    }

    /**
     * Method that cleans map and loads all markers after that
     * @param googleMap markers destination map
     */
    public static void refreshMarkers(final GoogleMap googleMap) {
        if (googleMap != null) {
            googleMap.clear();
            addAllMarkers(googleMap);
        }
    }

    /**
     * Method that loads all markers by user query to the map
     * @param googleMap markers destination map
     * @param toFind user search query
     */
    public static void addFoundedMarkers(final GoogleMap googleMap, final String toFind) {
        DatabaseManagerPlaces.addMarkersByQuery(googleMap, toFind);
    }

    private static void plotRoute(DirectionDestinationRequest gd, final LatLng destination,
                                  final GoogleMap googleMap, final ArrayList<LatLng> points, final Context context) {
        try {
            gd.to(destination)
                    .transportMode(TransportMode.WALKING)
                    .execute(new DirectionCallback() {
                        @Override
                        public void onDirectionSuccess(Direction direction, String rawBody) {
                            if (direction.isOK()) {
                                List<Leg> trail = getTrail(direction);
                                for (int i = 0; i < trail.size(); i++) {
                                    Leg leg = trail.get(i);
                                    List<Step> stepList = leg.getStepList();
                                    ArrayList<PolylineOptions> polylineOptionList = DirectionConverter
                                            .createTransitPolyline(context, stepList, 5, Color.RED, 3, Color.BLUE);
                                    addMarker(googleMap, getLegStart(leg));

                                    if (i == trail.size() - 1) {
                                        addMarker(googleMap, getLegEnd(leg));
                                    }

                                    for (PolylineOptions polylineOption : polylineOptionList) {
                                        points.addAll(polylineOption.getPoints());
                                        googleMap.addPolyline(polylineOption);
                                    }
                                }
                            }
                        }

                        @Override
                        public void onDirectionFailure(Throwable t) {
                            t.printStackTrace();
                        }
                    });
        } catch (NullPointerException ex) {
            Toast.makeText(context, ERROR_MESSAGE, Toast.LENGTH_LONG).show();
        }
        googleMap.animateCamera(CameraUpdateFactory.newLatLngZoom(destination, 15.0f));
    }

    private static DirectionDestinationRequest getRequest(final LatLng position) {
        return GoogleDirection.withServerKey(MAPS_API_KEY).from(position);
    }

    private static LatLng getPosition(final ArrayList<Place> placeArrayList, int index) {
        return new LatLng(placeArrayList.get(index).getLatitude(), placeArrayList.get(index).getLongitude());
    }

    private static void addMarker(final GoogleMap googleMap, final LatLng point) {
        googleMap.addMarker(new MarkerOptions().position(point));
    }

    private static LatLng getLegStart(final Leg leg) {
        return leg.getStartLocation().getCoordination();
    }

    private static LatLng getLegEnd(final Leg leg) {
        return leg.getEndLocation().getCoordination();
    }

    private static List<Leg> getTrail(final Direction direction) {
        return direction.getRouteList().get(0).getLegList();
    }

    private static void addAllMarkers(final GoogleMap googleMap) {
        Controller.loadMarkersToMap(googleMap);
    }
}
