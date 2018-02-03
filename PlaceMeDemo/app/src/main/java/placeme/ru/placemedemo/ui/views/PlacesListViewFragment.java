package placeme.ru.placemedemo.ui.views;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
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

import java.util.Arrays;

import placeme.ru.placemedemo.R;
import placeme.ru.placemedemo.core.Controller;
import placeme.ru.placemedemo.core.database.DatabaseUtils;

import static placeme.ru.placemedemo.core.database.DatabaseUtils.PLACE_PHOTO_SUFFIX;
import static placeme.ru.placemedemo.ui.views.ShareImageUtility.MESSAGE_DELETE;
import static placeme.ru.placemedemo.ui.views.ShareImageUtility.SHARE_TITLE;
import static placeme.ru.placemedemo.ui.views.ShareImageUtility.STORAGE_DELIMITER;
import static placeme.ru.placemedemo.ui.views.ShareImageUtility.shareImage;

/**
 * Fragment that represents information about favourite places
 * Created by Андрей on 20.12.2017.
 */
public class PlacesListViewFragment extends Fragment {
    private String[] places;
    private boolean isNullLength = false;

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        String list = Controller.getPlaces(getContext());
        if (list.length() == 0) {
            isNullLength = true;
        } else {
            places = Controller.getPlaces(getContext()).split(STORAGE_DELIMITER);

            if (places.length > 1) {
                Arrays.sort(places, (a, b) -> (Integer.parseInt(a) - Integer.parseInt(b)));
            }
        }
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {

        View view = inflater.inflate(R.layout.fragment_horizontal_list_view, container, false);
        RecyclerView mRecyclerView = view.findViewById(R.id.cardView);
        mRecyclerView.setHasFixedSize(true);

        LinearLayoutManager MyLayoutManager = new LinearLayoutManager(getActivity());
        MyLayoutManager.setOrientation(LinearLayoutManager.VERTICAL);
        mRecyclerView.setAdapter(new MyAdapter(places));
        mRecyclerView.setLayoutManager(MyLayoutManager);

        return view;
    }

    @Override
    public void onActivityCreated(Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);

    }

    public class MyAdapter extends RecyclerView.Adapter<MyViewHolder> {
        private String[] data;
        public MyAdapter(String[] places) {
            data = places;
        }

        @Override
        public MyViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
            View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.place_item, parent, false);
            return new MyViewHolder(view);
        }

        @Override
        public void onBindViewHolder(final MyViewHolder holder, final int position) {
            Activity activity = getActivity();
            StorageReference child = FirebaseStorage.getInstance().getReference().child("photos").child(places[position] + PLACE_PHOTO_SUFFIX);
            DatabaseUtils.loadFavouritePicture(child, holder.iv, activity);
            Controller.fillDescriptionPlaces(holder.tv, places[position]);
        }

        @Override
        public int getItemCount() {
            if (isNullLength) {
                return 0;
            }
            return data.length;
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
            b2.setOnClickListener(view -> startActivity(Intent.createChooser(shareImage(iv), SHARE_TITLE)));

            b3 = v.findViewById(R.id.place_button_close);
            b3.setOnClickListener(v1 -> Toast.makeText(getContext(), MESSAGE_DELETE, Toast.LENGTH_LONG).show());
        }
    }
}