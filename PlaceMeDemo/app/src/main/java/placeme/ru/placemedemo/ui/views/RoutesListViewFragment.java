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

import placeme.ru.placemedemo.R;
import placeme.ru.placemedemo.core.Controller;
import placeme.ru.placemedemo.core.database.DatabaseManagerRoutes;
import placeme.ru.placemedemo.core.database.DatabaseUtils;

import static placeme.ru.placemedemo.ui.views.ShareImageUtility.MESSAGE_DELETE;
import static placeme.ru.placemedemo.ui.views.ShareImageUtility.SHARE_TITLE;
import static placeme.ru.placemedemo.ui.views.ShareImageUtility.shareImage;

/**
 * Fragment that represents information about routes
 * Created by Андрей on 20.12.2017.
 */
public class RoutesListViewFragment extends Fragment {
    private int length;

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        length = Controller.getRoutesLength(getContext()).intValue();
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {

        View view = inflater.inflate(R.layout.fragment_horizontal_list_view, container, false);
        RecyclerView mRecyclerView = view.findViewById(R.id.cardView);
        mRecyclerView.setHasFixedSize(true);

        LinearLayoutManager MyLayoutManager = new LinearLayoutManager(getActivity());
        MyLayoutManager.setOrientation(LinearLayoutManager.VERTICAL);
        mRecyclerView.setAdapter(new MyAdapter());
        mRecyclerView.setLayoutManager(MyLayoutManager);

        return view;
    }

    @Override
    public void onActivityCreated(Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);

    }

    public class MyAdapter extends RecyclerView.Adapter<MyViewHolder> {

        @Override
        public MyViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
            View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.route_item, parent, false);
            return new MyViewHolder(view);
        }

        @Override
        public void onBindViewHolder(final MyViewHolder holder, final int position) {
            Activity activity = getActivity();
            String userId = Controller.getLoggedInAsString(getContext());
            DatabaseUtils.loadFavouritePicture(DatabaseManagerRoutes.getFavouriteRoutesReference(userId, position), holder.iv, activity);
            Controller.fillDescription(holder.tv, position, Controller.getLoggedInAsString(getContext()));
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

            iv = v.findViewById(R.id.route_photo);

            b2 = v.findViewById(R.id.routes2);
            b2.setOnClickListener(view -> startActivity(Intent.createChooser(shareImage(iv), SHARE_TITLE)));

            b3 = v.findViewById(R.id.routes3);
            b3.setOnClickListener(v1 -> Toast.makeText(getContext(), MESSAGE_DELETE, Toast.LENGTH_LONG).show());

            Controller.getUserRoutesLength2(Controller.getLoggedInAsString(getContext()), getContext());
        }
    }
}