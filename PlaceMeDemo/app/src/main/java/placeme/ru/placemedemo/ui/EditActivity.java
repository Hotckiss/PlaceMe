package placeme.ru.placemedemo.ui;

import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;

import placeme.ru.placemedemo.R;
import placeme.ru.placemedemo.core.Controller;
import placeme.ru.placemedemo.elements.AuthData;
import placeme.ru.placemedemo.elements.User;
import placeme.ru.placemedemo.elements.UserDataFields;

/**
 * Activity that provides to edit user profile
 */
public class EditActivity extends AppCompatActivity {
    private static final int NO_ID = -1;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_edit);

        initializeTextFields();
        Controller.loadUserDataForEdit(initializeEditFields(), Controller.getLoggedInAsString(this));

        Button saveButton = findViewById(R.id.saveEdit);

        saveButton.setOnClickListener(v -> {
            Controller.saveProfileChanges(Controller.getLoggedInAsString(EditActivity.this), generateNewAuthData(), generateNewUserData());
            finish();
        });
    }

    @Override
    public void onBackPressed() {
        finish();
    }

    private AuthData generateNewAuthData() {
        return new AuthData(NO_ID, getFieldValue(R.id.teEditLogin), getFieldValue(R.id.teEditPassword));
    }

    private User generateNewUserData() {
        return new User(NO_ID, getFieldValue(R.id.teEditName),
                getFieldValue(R.id.teEditSurname), getFieldValue(R.id.teEditNickname));
    }

    private String getFieldValue(int fieldId) {
        return ((EditText)findViewById(fieldId)).getText().toString();
    }
    private void initializeTextFields() {
        TextView tvLogin = findViewById(R.id.tvEditLogin);
        tvLogin.setText(R.string.register_login);

        TextView tvPassword = findViewById(R.id.tvEditPassword);
        tvPassword.setText(R.string.register_password);

        TextView tvName = findViewById(R.id.tvEditName);
        tvName.setText(R.string.register_name);

        TextView tvSurname = findViewById(R.id.tvEditSurname);
        tvSurname.setText(R.string.register_surname);

        TextView tvNickname = findViewById(R.id.tvEditNickname);
        tvNickname.setText(R.string.register_nickname);
    }

    private UserDataFields initializeEditFields() {
        return new UserDataFields(findViewById(R.id.teEditLogin), findViewById(R.id.teEditPassword),
                findViewById(R.id.teEditName), findViewById(R.id.teEditSurname), findViewById(R.id.teEditNickname));
    }
}
