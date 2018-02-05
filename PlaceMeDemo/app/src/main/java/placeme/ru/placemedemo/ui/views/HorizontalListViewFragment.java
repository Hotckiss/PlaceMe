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
import android.widget.ImageView;
import android.widget.TextView;

import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;

import java.util.ArrayList;

import placeme.ru.placemedemo.R;
import placeme.ru.placemedemo.core.Controller;
import placeme.ru.placemedemo.core.chat.Chat;
import placeme.ru.placemedemo.core.database.DatabaseUtils;
import placeme.ru.placemedemo.elements.cards.FriendCard;

import static placeme.ru.placemedemo.core.database.DatabaseManagerUsers.loadFriendName;
import static placeme.ru.placemedemo.core.database.DatabaseManagerUsers.showUserInfo;

/**
 * Fragment that represents information about friends
 * Created by Андрей on 20.12.2017.
 */
public class HorizontalListViewFragment extends Fragment {
    private static final String FRIEND_PREFIX = "friend ";
    private static final String LIST_DELIMITER = ",";
    private ArrayList<FriendCard> itemsList = new ArrayList<>();

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        itemsList.clear();
        Integer finish = Controller.getFriendsLength(getActivity().getBaseContext());
        String[] friends = Controller.getFriends(getActivity().getBaseContext()).split(LIST_DELIMITER);
        for(int i = 0; i < finish; i++){
            FriendCard item = new FriendCard();
            item.setmCardName(FRIEND_PREFIX + (i + 1));
            item.setmImageResourceId(R.drawable.grey);
            item.setId(Integer.parseInt(friends[i]));
            itemsList.add(item);
        }
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {

        View view = inflater.inflate(R.layout.fragment_horizontal_list_view, container, false);
        RecyclerView mRecyclerView = view.findViewById(R.id.cardView);
        mRecyclerView.setHasFixedSize(true);
        LinearLayoutManager MyLayoutManager = new LinearLayoutManager(getActivity());
        MyLayoutManager.setOrientation(LinearLayoutManager.HORIZONTAL);
        if (itemsList.size() > 0 & mRecyclerView != null) {
            mRecyclerView.setAdapter(new MyAdapter(itemsList));
        }
        mRecyclerView.setLayoutManager(MyLayoutManager);

        return view;
    }

    @Override
    public void onActivityCreated(Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);

    }

    public class MyAdapter extends RecyclerView.Adapter<MyViewHolder> {
        private static final String AVATARS_KEY = "avatars";
        private static final String AVATAR_SUFFIX = "avatar";
        private ArrayList<FriendCard> list;

        public MyAdapter(ArrayList<FriendCard> Data) {
            list = Data;
        }

        @Override
        public MyViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
            View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.recycle_items, parent, false);
            return new MyViewHolder(view);
        }

        @Override
        public void onBindViewHolder(final MyViewHolder holder, final int position) {
            holder.pos = position;
            int userId = list.get(position).getId();
            loadFriendName(userId, holder.titleTextView);

            Activity activity = getActivity();
            DatabaseUtils.loadFavouritePicture(getUserAvatarReference(userId), holder.coverImageView, activity);
        }

        @Override
        public int getItemCount() {
            return list.size();
        }

        private StorageReference getStorageReference() {
            return FirebaseStorage.getInstance().getReference();
        }

        private StorageReference getUserAvatarReference(int userId) {
            return getStorageReference().child(AVATARS_KEY).child(String.valueOf(userId) + AVATAR_SUFFIX);
        }
    }

    public class MyViewHolder extends RecyclerView.ViewHolder {

        public TextView titleTextView;
        public ImageView coverImageView;
        public ImageView likeImageView;
        public ImageView shareImageView;
        public Integer pos;

        public MyViewHolder(View v) {
            super(v);
            titleTextView = v.findViewById(R.id.titleTextView);
            coverImageView = v.findViewById(R.id.coverImageView);
            likeImageView = v.findViewById(R.id.infoImageView);
            shareImageView = v.findViewById(R.id.messageImageView);
            likeImageView.setOnClickListener(v1 -> showUserInfo(itemsList.get(pos).getId(), getActivity()));

            shareImageView.setOnClickListener(v12 -> {
                Controller.setChatPair(getContext(), Controller.getLoggedInAsString(getContext()) + LIST_DELIMITER + String.valueOf(itemsList.get(pos).getId()));
                startActivity(new Intent(getActivity().getBaseContext(), Chat.class));
            });
        }

    }
}