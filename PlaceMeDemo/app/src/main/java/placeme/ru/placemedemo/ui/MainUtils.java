package placeme.ru.placemedemo.ui;

import android.location.Location;

import com.google.android.gms.maps.model.LatLng;

import java.util.ArrayList;

import geo.GeoObj;
import gl.GLFactory;
import worldData.World;

/**
 * Class that helps to initialize main application interface
 * Created by Андрей on 04.02.2018.
 */
public class MainUtils {
    //431-444
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

    //398-418
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
            Location l = new Location("");
            l.setLatitude(start.latitude + (finish.latitude - start.latitude) * i / iter);
            l.setLongitude(start.longitude + (finish.longitude - start.longitude) * i / iter);
            GeoObj next = new GeoObj(l);
            next.setComp(objectFactory.newArrow());

            world.add(next);
        }
    }
}
