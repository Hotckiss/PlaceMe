package placeme.ru.placemedemo.ui.views;

/**
 * Created by Андрей on 20.12.2017.
 */

import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.drawable.BitmapDrawable;
import android.graphics.drawable.Drawable;
import android.net.Uri;
import android.os.Bundle;
import android.os.Environment;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;
import com.squareup.picasso.Picasso;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.util.Arrays;

import placeme.ru.placemedemo.R;
import placeme.ru.placemedemo.core.Controller;
import placeme.ru.placemedemo.core.database.DatabaseManager;
import placeme.ru.placemedemo.core.utils.FavouritePlacesUtils;

/**
 * Fragment that represents information about favourite places
 */
public class PlacesListViewFragment extends Fragment {
    RecyclerView mRecyclerView;
    String[] places;
    boolean isNullLength = false;

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        String list = Controller.getPlaces(getContext());
        if (list.length() == 0) {
            isNullLength = true;
        } else {
            places = Controller.getPlaces(getContext()).split(",");

            if (places.length > 1) {
                Arrays.sort(places, (a, b) -> (Integer.parseInt(a) - Integer.parseInt(b)));
            }
        }
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {

        View view = inflater.inflate(R.layout.fragment_horizontal_list_view, container, false);
        mRecyclerView = view.findViewById(R.id.cardView);
        mRecyclerView.setHasFixedSize(false);

        LinearLayoutManager MyLayoutManager = new LinearLayoutManager(getActivity());
        MyLayoutManager.setOrientation(LinearLayoutManager.VERTICAL);

        if (mRecyclerView != null) {
            mRecyclerView.setAdapter(new MyAdapter(places));
        }

        mRecyclerView.setLayoutManager(MyLayoutManager);

        return view;
    }

    @Override
    public void onActivityCreated(Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);

    }

    private Uri getLocalBitmapUri(ImageView imageView) throws IOException {
        // Extract Bitmap from ImageView drawable
        Drawable drawable = imageView.getDrawable();
        Bitmap bmp = null;

        if (drawable instanceof BitmapDrawable){
            bmp = ((BitmapDrawable)imageView.getDrawable()).getBitmap();
        } else {
            return null;
        }

        // Store image to default external storage directory
        Uri bmpUri = null;
        try {
            File file =  new File(Environment.getExternalStoragePublicDirectory(
                    Environment.DIRECTORY_DOWNLOADS), "share_image_" + System.currentTimeMillis() + ".png");

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

    public class MyAdapter extends RecyclerView.Adapter<MyViewHolder> {
        String[] data;
        public MyAdapter(String[] places) {
            data = places;
        }

        @Override
        public MyViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
            View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.place_item, parent, false);
            MyViewHolder holder = new MyViewHolder(view);
            return holder;
        }

        @Override
        public void onBindViewHolder(final MyViewHolder holder, final int position) {
            StorageReference child = FirebaseStorage.getInstance().getReference().child("photos").child(places[position]+"place_photo");
            child.getDownloadUrl().addOnSuccessListener(uri -> Picasso.with(getActivity().getBaseContext()).load(uri)
                    .placeholder(android.R.drawable.btn_star_big_on)
                    .error(android.R.drawable.btn_star_big_on)
                    .into(holder.iv));

            Controller.fillDescriptionPlaces(holder.tv, places[position]);
        }

        @Override
        public int getItemCount() {
            if (isNullLength) {
                return 0;
            }
            return places.length;
        }
    }

    public class MyViewHolder extends RecyclerView.ViewHolder {
        public ImageView iv;
        public TextView tv;
        public ImageButton b2;
        public ImageButton b3;
        public MyViewHolder(View v) {
            super(v);
            tv = v.findViewById(R.id.place_description);
            iv = v.findViewById(R.id.place_photo);

            b2 = v.findViewById(R.id.place_button_share);
            b2.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    final Intent shareIntent = new Intent(Intent.ACTION_SEND);
                    shareIntent.setType("image/*");

                    Uri bmpUri = null;
                    try {
                        bmpUri = getLocalBitmapUri(iv);
                    } catch (IOException e) {
                        e.printStackTrace();
                    }

                    shareIntent.putExtra(Intent.EXTRA_STREAM, bmpUri);
                    startActivity(Intent.createChooser(shareIntent, "Share image using"));
                }
            });

            b3 = v.findViewById(R.id.place_button_close);
            b3.setOnClickListener(v1 -> Toast.makeText(getContext(), "close pressed", Toast.LENGTH_LONG).show());



        }
    }
}