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

import placeme.ru.placemedemo.R;
import placeme.ru.placemedemo.core.database.DatabaseManager;
import placeme.ru.placemedemo.core.utils.AuthorizationUtils;
import placeme.ru.placemedemo.core.utils.RoutesUtils;

//TODO:refactor
public class RoutesListViewFragment extends Fragment {
    RecyclerView MyRecyclerView;
    int length;
    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        length = RoutesUtils.getRoutesLength(getContext()).intValue();
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {

        View view = inflater.inflate(R.layout.fragment_horizontal_list_view, container, false);
        MyRecyclerView = view.findViewById(R.id.cardView);
        MyRecyclerView.setHasFixedSize(false);

        LinearLayoutManager MyLayoutManager = new LinearLayoutManager(getActivity());
        MyLayoutManager.setOrientation(LinearLayoutManager.VERTICAL);

        if (MyRecyclerView != null) {
            MyRecyclerView.setAdapter(new MyAdapter());
        }

        MyRecyclerView.setLayoutManager(MyLayoutManager);

        return view;
    }

    @Override
    public void onActivityCreated(Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);

    }

    public class MyAdapter extends RecyclerView.Adapter<MyViewHolder> {
        public MyAdapter() {
        }

        @Override
        public MyViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
            View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.route_item, parent, false);
            MyViewHolder holder = new MyViewHolder(view);
            return holder;
        }

        @Override
        public void onBindViewHolder(final MyViewHolder holder, final int position) {

            StorageReference child = FirebaseStorage.getInstance().getReference().child("routes").child(AuthorizationUtils.getLoggedInAsString(getContext()))
                    .child(AuthorizationUtils.getLoggedInAsString(getContext()) + "_" + ((Integer)position).toString());
            child.getDownloadUrl().addOnSuccessListener(uri -> Picasso.with(getActivity().getBaseContext()).load(uri)
                    .placeholder(android.R.drawable.btn_star_big_on)
                    .error(android.R.drawable.btn_star_big_on)
                    .into(holder.iv));

            DatabaseManager.fillDescription(holder.tv, position, AuthorizationUtils.getLoggedInAsString(getContext()));
        }

        @Override
        public int getItemCount() {
            return length;
        }
    }

    public class MyViewHolder extends RecyclerView.ViewHolder {
        public ImageView iv;
        public TextView tv;
        public ImageButton b2;
        public ImageButton b3;
        public MyViewHolder(View v) {
            super(v);
            tv = v.findViewById(R.id.route_description);

            b2 = v.findViewById(R.id.routes2);
            b2.setOnClickListener(v1 -> Toast.makeText(getContext(), "b2 pressed", Toast.LENGTH_LONG).show());

            b3 = v.findViewById(R.id.routes3);
            b3.setOnClickListener(v1 -> Toast.makeText(getContext(), "b3 pressed", Toast.LENGTH_LONG).show());

            iv = v.findViewById(R.id.route_photo);

            DatabaseManager.getUserRoutesLength2(AuthorizationUtils.getLoggedInAsString(getContext()), getContext());
        }
    }
}