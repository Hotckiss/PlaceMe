package placeme.ru.placemedemo;

import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.widget.EditText;
import android.widget.TextView;

import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.util.HashMap;

public class EditActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_edit);

        TextView tvLogin = (TextView) findViewById(R.id.tvEditLogin);
        tvLogin.setText("Login:");

        TextView tvPassword = (TextView) findViewById(R.id.tvEditPassword);
        tvPassword.setText("Password:");

        TextView tvName = (TextView) findViewById(R.id.tvEditName);
        tvName.setText("Name:");

        TextView tvSurname = (TextView) findViewById(R.id.tvEditSurname);
        tvSurname.setText("Surname:");

        TextView tvNickname = (TextView) findViewById(R.id.tvEditNickname);
        tvNickname.setText("Nickname:");

        FirebaseDatabase mBase;
        DatabaseReference mDatabaseReferenceUser;

        mBase = FirebaseDatabase.getInstance();
        mDatabaseReferenceUser = mBase.getReference().child("users").child((((Integer)LoginUtility
                .getLoggedIn(this)).toString()));

        mDatabaseReferenceUser.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                HashMap<String, String> currentUser = (HashMap<String, String>) dataSnapshot.getValue();

                EditText name = (EditText) findViewById(R.id.teEditName);
                name.setText(currentUser.get("name"));

                EditText surname = (EditText) findViewById(R.id.teEditSurname);
                surname.setText(currentUser.get("surname"));

                EditText nickname = (EditText) findViewById(R.id.teEditNickname);
                nickname.setText(currentUser.get("nickname"));
            }

            @Override
            public void onCancelled(DatabaseError databaseError) {

            }
        });

        mDatabaseReferenceUser = mBase.getReference().child("authdata").child((((Integer)LoginUtility
                .getLoggedIn(this)).toString()));

        mDatabaseReferenceUser.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                HashMap<String, String> currentUser = (HashMap<String, String>) dataSnapshot.getValue();

                EditText name = (EditText) findViewById(R.id.teEditLogin);
                name.setText(currentUser.get("login"));

                EditText surname = (EditText) findViewById(R.id.teEditPassword);
                surname.setText(currentUser.get("password"));

            }

            @Override
            public void onCancelled(DatabaseError databaseError) {

            }
        });


    }
}
