package placeme.ru.placemedemo;

import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.support.v4.app.FragmentManager;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;

import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.database.ChildEventListener;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;
import com.squareup.picasso.Picasso;

import de.hdodenhof.circleimageview.CircleImageView;
import placeme.ru.placemedemo.core.utils.AuthorizationUtils;
import placeme.ru.placemedemo.core.utils.FriendsDataUtils;

public class ProfileActivity extends AppCompatActivity {

    private FirebaseDatabase mBase;
    private DatabaseReference mDatabaseReference;
    private ChildEventListener childEventListener;
    private StorageReference mStorageRef;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_profile);

        //TODO: make good placeholder for profile image: instead of star or lens
        loadProfileImage();

        setEditButton();

        loadUserProfile();
    }

    private void loadProfileImage() {
        mStorageRef = FirebaseStorage.getInstance().getReference();
        final CircleImageView civ = findViewById(R.id.profile_image);
        StorageReference child = mStorageRef.child("avatars").child(AuthorizationUtils.getLoggedInAsString(ProfileActivity.this) + "avatar");
        child.getDownloadUrl().addOnSuccessListener(new OnSuccessListener<Uri>() {
            @Override
            public void onSuccess(Uri uri) {
                Picasso.with(ProfileActivity.this).load(uri)
                        .placeholder(android.R.drawable.btn_star_big_on)
                        .error(android.R.drawable.btn_star_big_on)
                        .into(civ);

            }
        });
    }

    private void setEditButton() {
        Button editButton  = findViewById(R.id.button_edit);
        editButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent edit = new Intent(ProfileActivity.this, EditActivity.class);
                startActivity(edit);
            }
        });
    }

    private void loadUserProfile() {
        mBase = FirebaseDatabase.getInstance();
        mDatabaseReference = mBase.getReference().child("users");

        if( childEventListener == null ) {
            childEventListener = new ChildEventListener() {
                @Override
                public void onChildAdded(DataSnapshot dataSnapshot, String s) {
                    User user = dataSnapshot.getValue(User.class);
                    if(user == null) {
                        return;
                    }
                    if(AuthorizationUtils.getLoggedIn(ProfileActivity.this) == user.getId()) {
                        TextView tvName = findViewById(R.id.name);
                        tvName.setText(user.getName());

                        TextView tvSurname = findViewById(R.id.surname);
                        tvSurname.setText(user.getSurname());

                        //TODO: move string constant to values/strings
                        TextView tvNickname = findViewById(R.id.nickname);
                        tvNickname.setText("@" + user.getNickname());

                        //TODO: add friends list
                        FriendsDataUtils.setFriendsLength(ProfileActivity.this, user.getFriendsLength());
                        FriendsDataUtils.setFriends(ProfileActivity.this, user.getFriends());
                        //Log.d("prrrrrr1r", user.getFriends());
                        //Log.d("prrrrrr2r", ((Integer)user.getFriendsLength()).toString());
                        FragmentManager fm = getSupportFragmentManager();
                        android.support.v4.app.Fragment fragment = fm.findFragmentById(R.id.fragmentContainer);

                        if (fragment == null) {
                            fragment = new HorizontalListViewFragment();
                            fm.beginTransaction().add(R.id.fragmentContainer, fragment).commit();
                        }


                    }
                }
                @Override
                public void onChildChanged(DataSnapshot dataSnapshot, String s) {}

                @Override
                public void onChildRemoved(DataSnapshot dataSnapshot) {}

                @Override
                public void onChildMoved(DataSnapshot dataSnapshot, String s) {}

                @Override
                public void onCancelled(DatabaseError databaseError) {}
            };
        }
        mDatabaseReference.addChildEventListener(childEventListener);
        mDatabaseReference.child("users").removeEventListener(childEventListener);
        childEventListener = null;
    }


}
