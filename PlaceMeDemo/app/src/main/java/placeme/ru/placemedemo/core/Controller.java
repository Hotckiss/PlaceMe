package placeme.ru.placemedemo.core;

import android.app.Activity;
import android.content.Context;
import android.graphics.Bitmap;
import android.media.MediaScannerConnection;
import android.net.Uri;
import android.os.Environment;

import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.model.LatLng;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;

import placeme.ru.placemedemo.R;
import placeme.ru.placemedemo.core.database.DatabaseManager;
import placeme.ru.placemedemo.core.utils.AuthorizationUtils;
import placeme.ru.placemedemo.ui.MainActivity;

/**
 * Created by Андрей on 25.12.2017.
 */

//TODO: make all here
public class Controller {

    public static GoogleMap.SnapshotReadyCallback getRoutePictureCallback(final Activity instance, final String routeName) {
        GoogleMap.SnapshotReadyCallback callback = new GoogleMap.SnapshotReadyCallback() {
            Bitmap bitmap;

            @Override
            public void onSnapshotReady(Bitmap snapshot) {
                try {
                    bitmap = snapshot;
                    File outputDir = new File(Environment.getExternalStoragePublicDirectory(Environment.DIRECTORY_PICTURES), instance.getString(R.string.app_name));

                    if (!outputDir.exists()) {
                        outputDir.mkdir();
                    }

                    File outputFile = new File(outputDir, routeName + ".png");
                    FileOutputStream fileOutputStream = new FileOutputStream(outputFile);
                    bitmap.compress(Bitmap.CompressFormat.PNG, 100, fileOutputStream);
                    MediaScannerConnection.scanFile(instance,
                            new String[] { outputFile.getPath() },
                            new String[] { "image/png" },
                            null);

                    Uri attachment = Uri.fromFile(outputFile);
                    DatabaseManager.getUserRoutesLength(AuthorizationUtils.getLoggedInAsString(instance.getBaseContext()), instance.getBaseContext());
                    DatabaseManager.saveRoute(attachment, AuthorizationUtils.getLoggedInAsString(instance.getBaseContext()), instance.getBaseContext());
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }
        };

        return callback;
    }

    public static void sendRoute(GoogleMap map, final String routeName, final Activity activity) {

        //map.moveCamera(CameraUpdateFactory.newLatLngZoom(position, 14.0f));

        Thread myThread = new Thread(() -> {
            map.snapshot(Controller.getRoutePictureCallback(activity, "tmp"));
        });
        myThread.run();
    }
}
