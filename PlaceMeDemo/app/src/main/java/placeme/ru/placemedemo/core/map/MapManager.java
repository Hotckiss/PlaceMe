package placeme.ru.placemedemo.core.map;

import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.MarkerOptions;
import com.google.firebase.database.ChildEventListener;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;

import placeme.ru.placemedemo.core.database.DatabaseManager;
import placeme.ru.placemedemo.elements.Place;

/**
 * Created by Андрей on 21.11.2017.
 */

/**
 * Class that allows application to work with google maps
 */
public class MapManager {

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
        if(googleMap != null) {
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
}
