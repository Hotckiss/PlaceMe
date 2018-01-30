package placeme.ru.placemedemo.ui;

import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;

import placeme.ru.placemedemo.R;
import placeme.ru.placemedemo.core.Controller;

/**
 * Activity that provides to edit user profile
 */
public class EditActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_edit);

        initializeTextFields();
        final EditText[] toLoad = initializeEditFields();

        Controller.loadUserDataForEdit(toLoad, Controller.getLoggedInAsString(this));

        Button saveButton = findViewById(R.id.saveEdit);

        saveButton.setOnClickListener(v -> {
            Controller.saveProfileChanges(Controller.getLoggedInAsString(EditActivity.this), generateNewUserData(toLoad));
            finish();
        });
    }

    @Override
    public void onBackPressed() {
        finish();
    }

    private String[] generateNewUserData(final EditText[] fields) {
        String[] result = new String[5];
        result[0] = fields[0].getText().toString();
        result[1] = fields[1].getText().toString();
        result[2] = fields[2].getText().toString();
        result[3] = fields[3].getText().toString();
        result[4] = fields[4].getText().toString();

        return result;
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

    private EditText[] initializeEditFields() {
        EditText[] edits = new EditText[5];

        edits[0] = findViewById(R.id.teEditLogin);
        edits[1] = findViewById(R.id.teEditPassword);
        edits[2] = findViewById(R.id.teEditName);
        edits[3] = findViewById(R.id.teEditSurname);
        edits[4] = findViewById(R.id.teEditNickname);

        return edits;
    }
}
