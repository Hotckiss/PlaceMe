package placeme.ru.placemedemo;

import android.content.Context;
import android.content.Intent;
import android.graphics.Canvas;
import android.graphics.Path;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.dd.CircularProgressButton;
import com.google.firebase.database.ChildEventListener;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;

public class ProfileActivity extends AppCompatActivity {

    private ProfileInfo[] pi = new ProfileInfo[] { new ProfileInfo("Alina", "Erokhina", "@aliscafo"), new ProfileInfo("Andrew", "Kirilenko", "@hotckiss"),
            new ProfileInfo("Vika", "Erokhina", "@kinfsfoill") };

    private FirebaseDatabase mBase;
    private DatabaseReference mDatabaseReference;
    private ChildEventListener childEventListener;
    private User userInfo;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_profile);

        Log.d("user::::", ((Integer)LoginUtility.getLoggedIn(this)).toString());

        mBase = FirebaseDatabase.getInstance();
        mDatabaseReference = mBase.getReference().child("users");

        if( childEventListener == null ) {
            childEventListener = new ChildEventListener() {
                @Override
                public void onChildAdded(DataSnapshot dataSnapshot, String s) {
                    User user = (User)dataSnapshot.getValue(User.class);
                    if(user == null) {
                        Log.d("cdbbdbcccc", "dfbdkgbd");
                        return;
                    }
                    if(((Integer)LoginUtility.getLoggedIn(ProfileActivity.this)).equals(user.getId())) {
                        //Log.d("ccccc", user.getName());
                       // userInfo = user;
                        TextView tvName = (TextView) findViewById(R.id.name);
                        tvName.setText(user.getName());

                        TextView tvSurname = (TextView) findViewById(R.id.surname);
                        tvSurname.setText(user.getSurname());

                        TextView tvNickname = (TextView) findViewById(R.id.nickname);
                        tvNickname.setText("@" + user.getNickname());
                    }
                    // if (LoginUtility.getLoggedIn())
                    Button editButton  = (Button) findViewById(R.id.button_edit);
                    //Button mEmailSignInButton = (Button) findViewById(R.id.email_sign_in_button);
                    //editButton.setIndeterminateProgressMode(true);
                    editButton.setOnClickListener(new View.OnClickListener() {
                        @Override
                        public void onClick(View v) {
                            Intent edit = new Intent(ProfileActivity.this, EditActivity.class);
                            //profile.putExtra("jjlkn", "kjhgkjbjhbj,kh");
                            startActivity(edit);
                        }
                    });
                }

                @Override
                public void onChildChanged(DataSnapshot dataSnapshot, String s) {

                }

                @Override
                public void onChildRemoved(DataSnapshot dataSnapshot) {

                }

                @Override
                public void onChildMoved(DataSnapshot dataSnapshot, String s) {

                }

                @Override
                public void onCancelled(DatabaseError databaseError) {

                }
            };
        }
        mDatabaseReference.addChildEventListener(childEventListener);
        mDatabaseReference.child("users").removeEventListener(childEventListener);
        childEventListener = null;
        //ImageView iv = (ImageView) findViewById(R.id)
        //int index = LoginUtility.getLoggedIn(this);

        Button editButton  = (Button) findViewById(R.id.button_edit);
        //Button mEmailSignInButton = (Button) findViewById(R.id.email_sign_in_button);
        //editButton.setIndeterminateProgressMode(true);
        editButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Toast.makeText(getApplicationContext(), "Button pressed!", Toast.LENGTH_SHORT).show();
            }
        });
    }

    private class ProfileInfo {
        private String name;
        private String surname;
        private String nickname;
        public ProfileInfo(String name, String surname, String nickname) {
            this.name = name;
            this.surname = surname;
            this.nickname = nickname;
        }

        public String getName() {
            return name;
        }

        public String getSurname() {
            return surname;
        }

        public String getNickname() {
            return nickname;
        }

    }
}
