package placeme.ru.placemedemo.ui;

import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;

import placeme.ru.placemedemo.R;
import placeme.ru.placemedemo.core.database.DatabaseManager;

/**
 * Activity that provides user to register in the application
 */
public class RegisterActivity extends AppCompatActivity {

    private EditText teLogin;
    private EditText tePassword;
    private EditText teName;
    private EditText teSurname;
    private EditText teNickname;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_register);

        initializeTextFields();

        initializeEditTextFields();

        Button submit = findViewById(R.id.submit_up);

        submit.setOnClickListener(v -> {
            //TODO: check correctness of login and password
            DatabaseManager.registerUser(RegisterActivity.this, generateNewUserData());
            finish();
        });
    }

    private String[] generateNewUserData() {
        String[] result = new String[5];
        result[0] = teLogin.getText().toString();
        result[1] = tePassword.getText().toString();
        result[2] = teName.getText().toString();
        result[3] = teSurname.getText().toString();
        result[4] = teNickname.getText().toString();

        return result;
    }

    //TODO: implement
    private boolean checkLogin() {
        return false;
    }

    //TODO: implement
    private boolean checkPassword() {
        return false;
    }

    private void initializeTextFields() {
        TextView tvLogin = findViewById(R.id.tvLogin);
        tvLogin.setText(R.string.register_login);

        TextView tvPassword = findViewById(R.id.tvPassword);
        tvPassword.setText(R.string.register_password);

        TextView tvName = findViewById(R.id.tvName);
        tvName.setText(R.string.register_name);

        TextView tvSurname = findViewById(R.id.tvSurname);
        tvSurname.setText(R.string.register_surname);

        TextView tvNickname = findViewById(R.id.tvNickname);
        tvNickname.setText(R.string.register_nickname);
    }

    private void initializeEditTextFields() {
        teLogin = findViewById(R.id.teLogin);

        tePassword = findViewById(R.id.tePassword);

        teName = findViewById(R.id.teName);

        teSurname = findViewById(R.id.teSurname);

        teNickname = findViewById(R.id.teNickname);
    }
}
