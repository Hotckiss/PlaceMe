package placeme.ru.placemedemo.elements;

import android.widget.EditText;

/**
 * A class that contains fields filled with user data for edit.
 * Created by Андрей on 31.01.2018.
 */
public class UserDataFields {
    private EditText mLoginField;
    private EditText mPasswordField;
    private EditText mNameField;
    private EditText mSurnameField;
    private EditText mNicknameField;

    /**
     * Constructor that creates array of fields with specific user data
     * @param loginField user login field
     * @param passwordField user password field
     * @param nameField user name field
     * @param surnameField user surname field
     * @param nicknameField user nickname field
     */
    public UserDataFields(EditText loginField, EditText passwordField,
                          EditText nameField, EditText surnameField, EditText nicknameField) {
        mLoginField = loginField;
        mPasswordField = passwordField;
        mNameField = nameField;
        mSurnameField = surnameField;
        mNicknameField = nicknameField;
    }

    /**
     * Method that set all fields that are shown in user profile
     * @param name user name
     * @param surname user surname
     * @param nickname user nickname
     */
    public void setProfileFields(final String name, final String surname, final String nickname) {
        mNameField.setText(name);
        mSurnameField.setText(surname);
        mNicknameField.setText(nickname);
    }

    /**
     * Method that set all fields that are used to identify user
     * @param login user login
     * @param password user password
     */
    public void setAuthFields(final String login, final String password) {
        mLoginField.setText(login);
        mPasswordField.setText(password);
    }
}
