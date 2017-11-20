package placeme.ru.placemedemo;

import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.KeyEvent;
import android.view.View;
import android.view.inputmethod.EditorInfo;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;

import com.google.firebase.database.ChildEventListener;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

public class RegisterActivity extends AppCompatActivity {

    private EditText teLogin;
    private EditText tePassword;
    private EditText teName;
    private EditText teSurname;
    private EditText teNickname;

    private FirebaseDatabase mBase;

    private DatabaseReference mDatabaseReference;

    private DatabaseReference mDatabaseReference1;

    private Integer iid = -1;

    private ChildEventListener childEventListener;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_register);

        TextView tvLogin = (TextView) findViewById(R.id.tvLogin);
        tvLogin.setText("Login:");

        TextView tvPassword = (TextView) findViewById(R.id.tvPassword);
        tvPassword.setText("Password:");

        TextView tvName = (TextView) findViewById(R.id.tvName);
        tvName.setText("Name:");

        TextView tvSurname = (TextView) findViewById(R.id.tvSurname);
        tvSurname.setText("Surname:");

        TextView tvNickname = (TextView) findViewById(R.id.tvNickname);
        tvNickname.setText("Nickname:");

        teLogin = (EditText) findViewById(R.id.teLogin);

        tePassword = (EditText) findViewById(R.id.tePassword);

        teName = (EditText) findViewById(R.id.teName);

        teSurname = (EditText) findViewById(R.id.teSurname);

        teNickname = (EditText) findViewById(R.id.teNickname);

        Button submit = (Button) findViewById(R.id.submit_up);

        submit.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                mBase = FirebaseDatabase.getInstance();
                mDatabaseReference = mBase.getReference().child("maxid");
                Log.d("tst", "start");
                mDatabaseReference.addListenerForSingleValueEvent(new ValueEventListener() {
                    @Override
                    public void onDataChange(DataSnapshot dataSnapshot) {
                        Integer id = (Integer) dataSnapshot.getValue(Integer.class);
                        iid = id;
                        AuthData newAuthData = new AuthData(id, teLogin.getText().toString(), tePassword.getText().toString());
                        User newUser = new User(id, teName.getText().toString(), teSurname.getText().toString(), teNickname.getText().toString());
                        mDatabaseReference1 = mBase.getReference().child("users");
                        mDatabaseReference1.child(id.toString()).setValue(newUser);
                        mDatabaseReference1 = mBase.getReference().child("authdata");
                        mDatabaseReference1.child(id.toString()).setValue(newAuthData);
                        mDatabaseReference.setValue(iid + 1);
                        finish();
                    }

                    @Override
                    public void onCancelled( DatabaseError firebaseError) {

                        Log.d("User", "-1" );
                    }
                });
            }

        });
    }
}
