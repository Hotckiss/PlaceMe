package placeme.ru.placemedemo.ui.views;

import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.drawable.BitmapDrawable;
import android.graphics.drawable.Drawable;
import android.net.Uri;
import android.os.Environment;
import android.widget.ImageView;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;

/**
 * A class that allows to share image using other applications
 * Created by Андрей on 03.02.2018.
 */

public class ShareImageUtility {
    public static final String SHARE_TITLE = "Share image using";
    public static final String STORAGE_DELIMITER = ",";
    public static final String MESSAGE_DELETE = "Successfully deleted";
    private static final String INTENT_TYPE = "image/*";
    private static final String PICTURE_FORMAT = ".png";
    private static final String PICTURE_NAME = "share_image_";

    public static Intent shareImage(final ImageView imageView) {
        final Intent shareIntent = new Intent(Intent.ACTION_SEND);
        shareIntent.setType(INTENT_TYPE);

        Uri bmpUri = null;
        try {
            bmpUri = getLocalBitmapUri(imageView);
        } catch (IOException e) {
            e.printStackTrace();
        }

        shareIntent.putExtra(Intent.EXTRA_STREAM, bmpUri);
        return shareIntent;
    }

    private static Uri getLocalBitmapUri(ImageView imageView) throws IOException {
        Drawable drawable = imageView.getDrawable();
        Bitmap bmp;

        if (drawable instanceof BitmapDrawable){
            bmp = ((BitmapDrawable)imageView.getDrawable()).getBitmap();
        } else {
            return null;
        }

        Uri bmpUri = null;
        try {
            File file =  new File(Environment.getExternalStoragePublicDirectory(
                    Environment.DIRECTORY_DOWNLOADS), PICTURE_NAME + System.currentTimeMillis() + PICTURE_FORMAT);

            file.getParentFile().mkdirs();
            FileOutputStream out = new FileOutputStream(file);
            bmp.compress(Bitmap.CompressFormat.PNG, 90, out);
            out.close();
            bmpUri = Uri.fromFile(file);
        } catch (IOException e) {
            e.printStackTrace();
        }
        return bmpUri;
    }
}
