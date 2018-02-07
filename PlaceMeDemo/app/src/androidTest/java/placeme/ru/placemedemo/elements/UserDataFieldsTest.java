package placeme.ru.placemedemo.elements;

import android.content.Context;
import android.support.test.InstrumentationRegistry;
import android.widget.EditText;

import org.junit.Before;
import org.junit.Test;

import util.Log;

import static org.junit.Assert.*;

/**
 * Created by aliscafo on 07.02.2018.
 */
public class UserDataFieldsTest {
    private UserDataFields testUserDataFields;
    private EditText loginField;
    private EditText passwordField;
    private EditText nameField;
    private EditText surnameField;
    private EditText nicknameField;

    @Before
    public void initUserDataFields() {
        Context appContext = InstrumentationRegistry.getTargetContext();

        loginField = new EditText(appContext);
        passwordField = new EditText(appContext);
        nameField = new EditText(appContext);
        surnameField = new EditText(appContext);
        nicknameField = new EditText(appContext);

        testUserDataFields = new UserDataFields(loginField, passwordField,
                nameField, surnameField, nicknameField);
    }

    @Test
    public void testSetProfileFields() throws Exception {
        testUserDataFields.setProfileFields("Name", "Surname", "nickname");
        assertEquals("Name", nameField.getText().toString());
        assertEquals("Surname", surnameField.getText().toString());
        assertEquals("nickname", nicknameField.getText().toString());
    }

    @Test
    public void testSetAuthFields() throws Exception {
        testUserDataFields.setAuthFields("login@login", "password");
        assertEquals("login@login", loginField.getText().toString());
        assertEquals("password", passwordField.getText().toString());
    }
}