package placeme.ru.placemedemo.ui.views;

/**
 * Created by Андрей on 20.12.2017.
 */

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
import com.squareup.picasso.Picasso;

import java.util.Arrays;

import placeme.ru.placemedemo.R;
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
        String list = FavouritePlacesUtils.getPlaces(getContext());
        if (list.length() == 0) {
            isNullLength = true;
        } else {
            places = FavouritePlacesUtils.getPlaces(getContext()).split(",");

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

            DatabaseManager.fillDescriptionPlaces(holder.tv, places[position]);
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

            b2 = v.findViewById(R.id.place_button_share);
            b2.setOnClickListener(v1 -> Toast.makeText(getContext(), "share pressed", Toast.LENGTH_LONG).show());

            b3 = v.findViewById(R.id.place_button_close);
            b3.setOnClickListener(v1 -> Toast.makeText(getContext(), "close pressed", Toast.LENGTH_LONG).show());

            iv = v.findViewById(R.id.place_photo);

        }
    }
}