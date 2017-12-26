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
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

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
public class HorizontalListViewFragment extends Fragment {

    ArrayList<FriendCard> listitems = new ArrayList<>();
    RecyclerView MyRecyclerView;

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        listitems.clear();
        Integer finish = FriendsDataUtils.getFriendsLength(getActivity().getBaseContext());
        String[] friends = FriendsDataUtils.getFriends(getActivity().getBaseContext()).split(",");
        for(int i = 0; i < finish; i++){
            FriendCard item = new FriendCard();
            //TODO: delete string
            item.setCardName("friend " + (i + 1));

            item.setImageResourceId(android.R.drawable.star_big_on);
            item.setId(Integer.parseInt(friends[i]));
            listitems.add(item);
        }
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {

        View view = inflater.inflate(R.layout.fragment_horizontal_list_view, container, false);
        MyRecyclerView = (RecyclerView) view.findViewById(R.id.cardView);
        MyRecyclerView.setHasFixedSize(true);
        LinearLayoutManager MyLayoutManager = new LinearLayoutManager(getActivity());
        MyLayoutManager.setOrientation(LinearLayoutManager.HORIZONTAL);
        if (listitems.size() > 0 & MyRecyclerView != null) {
            MyRecyclerView.setAdapter(new MyAdapter(listitems));
        }
        MyRecyclerView.setLayoutManager(MyLayoutManager);

        return view;
    }

    @Override
    public void onActivityCreated(Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);

    }

    public class MyAdapter extends RecyclerView.Adapter<MyViewHolder> {
        private ArrayList<FriendCard> list;

        public MyAdapter(ArrayList<FriendCard> Data) {
            list = Data;
        }

        @Override
        public MyViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
            View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.recycle_items, parent, false);
            MyViewHolder holder = new MyViewHolder(view);
            return holder;
        }

        @Override
        public void onBindViewHolder(final MyViewHolder holder, final int position) {
            holder.pos = position;

            final FirebaseDatabase mBase;
            DatabaseReference mDatabaseReference;
            mBase = FirebaseDatabase.getInstance();
            mDatabaseReference = mBase.getReference().child("users").child(((Integer)list.get(position).getId()).toString()).child("name");
            mDatabaseReference.addListenerForSingleValueEvent(new ValueEventListener() {
                @Override
                public void onDataChange(DataSnapshot dataSnapshot) {
                    String name = dataSnapshot.getValue().toString();
                    holder.titleTextView.setText(name);
                }

                @Override
                public void onCancelled(DatabaseError firebaseError) {}
            });

            StorageReference child = FirebaseStorage.getInstance().getReference().child("avatars").child(((Integer)list.get(position).getId()).toString() + "avatar");
            child.getDownloadUrl().addOnSuccessListener(uri -> Picasso.with(getActivity().getBaseContext()).load(uri)
                    .placeholder(android.R.drawable.btn_star_big_on)
                    .error(android.R.drawable.btn_star_big_on)
                    .into(holder.coverImageView));
        }

        @Override
        public int getItemCount() {
            return list.size();
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
            likeImageView.setOnClickListener(v1 -> {

                //TODO: illegal state exception, want alert dialog
                //createInfoDialog(pos);

                DatabaseReference mDatabaseReference = FirebaseDatabase.getInstance().getReference().child("users");

                ChildEventListener childEventListener = new AbstractChildEventListener() {
                    @Override
                    public void onChildAdded(DataSnapshot dataSnapshot, String s) {
                        User user = dataSnapshot.getValue(User.class);
                        if(user == null) {
                            return;
                        }
                        if(listitems.get(pos).getId() == user.getId()) {
                            Toast.makeText(getActivity(),user.getName() + " " + user.getSurname() + "\n@" + user.getNickname(), Toast.LENGTH_LONG).show();
                        }
                    }
                };
                mDatabaseReference.addChildEventListener(childEventListener);
            });

            shareImageView.setOnClickListener(v12 -> {
                ChatUtils.setChatPair(getContext(), AuthorizationUtils.getLoggedInAsString(getContext()) + "," + ((Integer)listitems.get(pos).getId()).toString());
                startActivity(new Intent(getActivity().getBaseContext(), Chat.class));
            });
        }


    }

    /*private void createInfoDialog(final int pos) {
        final AlertDialog.Builder builder = new AlertDialog.Builder(getActivity().getBaseContext());
        LayoutInflater inflater = (LayoutInflater) getActivity().getBaseContext().getSystemService( Context.LAYOUT_INFLATER_SERVICE );
        final View layout = inflater.inflate(R.layout.dialog_description, null);

        builder.setTitle("Info");
        StorageReference child = FirebaseStorage.getInstance().getReference().child("avatars").child(((Integer)listitems.get(pos).getId()).toString() + "avatar");
        final ImageView imgView = (ImageView) layout.findViewById(R.id.profile_imagef);
        child.getDownloadUrl().addOnSuccessListener(uri -> Picasso.with(getActivity().getBaseContext()).load(uri)
                .placeholder(android.R.drawable.btn_star_big_on)
                .error(android.R.drawable.btn_star_big_on)
                .into(imgView));

        DatabaseReference mDatabaseReference = FirebaseDatabase.getInstance().getReference().child("users");

        ChildEventListener childEventListener = new AbstractChildEventListener() {
            @Override
            public void onChildAdded(DataSnapshot dataSnapshot, String s) {
                User user = dataSnapshot.getValue(User.class);
                if(user == null) {
                    return;
                }
                if(listitems.get(pos).getId() == user.getId()) {
                    TextView tvName = layout.findViewById(R.id.namef);
                    tvName.setText(user.getName());

                    TextView tvSurname = layout.findViewById(R.id.surnamef);
                    tvSurname.setText(user.getSurname());

                    //TODO: move string constant to values/strings
                    TextView tvNickname = layout.findViewById(R.id.nicknamef);
                    tvNickname.setText("@" + user.getNickname());
                }
            }
        };
        mDatabaseReference.addChildEventListener(childEventListener);
        builder.setCancelable(true);
        builder.setView(layout);
        builder.create().show();
    }*/
}