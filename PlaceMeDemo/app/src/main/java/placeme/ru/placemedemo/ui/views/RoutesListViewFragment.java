package placeme.ru.placemedemo.ui.views;

/**
 * Created by Андрей on 20.12.2017.
 */

import android.content.Context;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.support.v7.app.AlertDialog;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.MapView;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.database.ChildEventListener;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;
import com.squareup.picasso.Picasso;

import java.util.ArrayList;

import placeme.ru.placemedemo.R;
import placeme.ru.placemedemo.core.chat.Chat;
import placeme.ru.placemedemo.core.database.AbstractChildEventListener;
import placeme.ru.placemedemo.core.utils.AuthorizationUtils;
import placeme.ru.placemedemo.core.utils.ChatUtils;
import placeme.ru.placemedemo.core.utils.FriendsDataUtils;
import placeme.ru.placemedemo.elements.cards.FriendCard;
import placeme.ru.placemedemo.elements.User;

//TODO:refactor
public class RoutesListViewFragment extends Fragment {
    RecyclerView MyRecyclerView;
    ArrayList<String> routes = new ArrayList<>();
    GoogleMap googleMap;
    Bundle bundle;
    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        bundle = savedInstanceState;
        routes.clear();
        routes.add("59.9474625;30.2621412:59.94752510000001;30.263584899999998");
        routes.add("59.9474625;30.2621412:59.947285;30.259531");
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {

        View view = inflater.inflate(R.layout.fragment_horizontal_list_view, container, false);
        MyRecyclerView = view.findViewById(R.id.cardView);
        MyRecyclerView.setHasFixedSize(false);

        LinearLayoutManager MyLayoutManager = new LinearLayoutManager(getActivity());
        MyLayoutManager.setOrientation(LinearLayoutManager.VERTICAL);

        if (routes.size() > 0 & MyRecyclerView != null) {
            MyRecyclerView.setAdapter(new MyAdapter(routes));
        }

        MyRecyclerView.setLayoutManager(MyLayoutManager);

        return view;
    }

    @Override
    public void onActivityCreated(Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);

    }

    public class MyAdapter extends RecyclerView.Adapter<MyViewHolder> {
        private ArrayList<String> list;
        public GoogleMap map;
        public MyAdapter(ArrayList<String> data) {
            list = data;
        }

        @Override
        public MyViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
            View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.route_item, parent, false);
            MyViewHolder holder = new MyViewHolder(view);
            return holder;
        }

        @Override
        public void onBindViewHolder(final MyViewHolder holder, final int position) {
        }

        @Override
        public int getItemCount() {
            return list.size();
        }
    }

    public class MyViewHolder extends RecyclerView.ViewHolder {
        public ImageView iv;
        public ImageButton b1;
        public ImageButton b2;
        public ImageButton b3;
        public MyViewHolder(View v) {
            super(v);
            b1 = v.findViewById(R.id.routes1);
            b1.setOnClickListener(v1 -> Toast.makeText(getContext(), "b1 pressed", Toast.LENGTH_LONG).show());

            b2 = v.findViewById(R.id.routes2);
            b2.setOnClickListener(v1 -> Toast.makeText(getContext(), "b2 pressed", Toast.LENGTH_LONG).show());

            b3 = v.findViewById(R.id.routes3);
            b3.setOnClickListener(v1 -> Toast.makeText(getContext(), "b3 pressed", Toast.LENGTH_LONG).show());

            iv = v.findViewById(R.id.route_photo);
        }
    }
}