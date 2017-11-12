package placeme.ru.placemedemo;

import android.content.Context;
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

public class ProfileActivity extends AppCompatActivity {

    private ProfileInfo[] pi = new ProfileInfo[] { new ProfileInfo("Alina", "Erokhina", "@aliscafo"), new ProfileInfo("Andrew", "Kirilenko", "@hotckiss"),
            new ProfileInfo("Vika", "Erokhina", "@kinfsfoill") };

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_profile);

        Log.d("user::::", ((Integer)LoginUtility.getLoggedIn(this)).toString());
        //ImageView iv = (ImageView) findViewById(R.id)
        int index = LoginUtility.getLoggedIn(this);

        TextView tvName = (TextView) findViewById(R.id.name);
        tvName.setText(pi[index].getName());

        TextView tvSurname = (TextView) findViewById(R.id.surname);
        tvSurname.setText(pi[index].getSurname());

        TextView tvNickname = (TextView) findViewById(R.id.nickname);
        tvNickname.setText(pi[index].getNickname());

        CircularProgressButton editButton  = (CircularProgressButton) findViewById(R.id.button_edit);
        //Button mEmailSignInButton = (Button) findViewById(R.id.email_sign_in_button);
        editButton.setIndeterminateProgressMode(true);
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
