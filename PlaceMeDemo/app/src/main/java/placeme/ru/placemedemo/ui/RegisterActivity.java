package placeme.ru.placemedemo.ui;

import android.os.Bundle;
import android.support.v7.app.AlertDialog;
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
    private static final String DOG_CHARACTER = "@";

    private EditText teLogin;
    private EditText tePassword;
    private EditText teConfirmPassword;
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
            if (checkLogin() && checkPassword()) {
                DatabaseManager.registerUser(RegisterActivity.this, generateNewUserData());
                finish();
            } else {
                createAlertDialogProblems().show();
            }
        });
    }

    private AlertDialog createAlertDialogProblems() {
        final AlertDialog.Builder builder = new AlertDialog.Builder(RegisterActivity.this);
        builder.setTitle(R.string.register_problems);
        String password = tePassword.getText().toString();
        if (!checkLogin()) {
            builder.setMessage(R.string.register_login_dog);
        } else if (!hasThreeDigits(password)) {
            builder.setMessage(R.string.register_digits);
        } else if (!hasFiveLetters(password)) {
            builder.setMessage(R.string.register_letters);
        } else if (!hasBigLetter(password)) {
            builder.setMessage(R.string.register_big_letter);
        } else {
            builder.setMessage(R.string.register_equals);
        }

        builder.setPositiveButton(R.string.answer_ok, (dialog, which) -> {
            dialog.dismiss();
        });
        builder.setCancelable(true);
        return builder.create();
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

    private boolean checkLogin() {
        String login = teLogin.getText().toString();
        return login.contains(DOG_CHARACTER);
    }

    private boolean checkPassword() {
        String password = tePassword.getText().toString();

        return hasBigLetter(password) && hasFiveLetters(password) && hasThreeDigits(password) && confirm(password);
    }

    private boolean hasThreeDigits(final String password) {
        int numberOfDigits = 0;
        for (int i = 0; i < password.length(); i++) {
            if (Character.isDigit(password.charAt(i))) {
                numberOfDigits++;
            }
        }

        return numberOfDigits >= 3;
    }

    private boolean confirm(final String password) {
        return password.equals(teConfirmPassword.getText().toString());
    }

    private boolean hasFiveLetters(final String password) {
        int numberOfLetters = 0;
        for (int i = 0; i < password.length(); i++) {
            if (Character.isLetter(password.charAt(i))) {
                numberOfLetters++;
            }
        }

        return numberOfLetters >= 5;
    }

    private boolean hasBigLetter(final String password) {

        for (int i = 0; i < password.length(); i++) {
            if (Character.isLetter(password.charAt(i))) {
                return true;
            }
        }

        return false;
    }

    private void initializeTextFields() {
        TextView tvLogin = findViewById(R.id.tvLogin);
        tvLogin.setText(R.string.register_login);

        TextView tvPassword = findViewById(R.id.tvPassword);
        tvPassword.setText(R.string.register_password);

        TextView tvConfirmPassword = findViewById(R.id.tvConfirmPassword);
        tvConfirmPassword.setText(R.string.register_confirm_password);

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

        teConfirmPassword = findViewById(R.id.teConfirmPassword);

        teName = findViewById(R.id.teName);

        teSurname = findViewById(R.id.teSurname);

        teNickname = findViewById(R.id.teNickname);
    }
}
