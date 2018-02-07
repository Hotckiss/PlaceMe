package placeme.ru.placemedemo.elements;

import android.content.Context;
import android.support.test.InstrumentationRegistry;
import android.widget.EditText;
import android.widget.TextView;

import org.junit.Before;
import org.junit.Test;

import static org.junit.Assert.*;

/**
 * Created by aliscafo on 07.02.2018.
 */
public class UserProfileFieldsTest {
    UserProfileFields testUserProfileFields;
    private TextView name;
    private TextView surname;
    private TextView nickname;

    @Before
    public void initUserDataFields() {
        Context appContext = InstrumentationRegistry.getTargetContext();

        name = new TextView(appContext);
        surname = new TextView(appContext);
        nickname = new TextView(appContext);

        testUserProfileFields = new UserProfileFields(name, surname, nickname);
    }

    @Test
    public void testFillAllFields() throws Exception {
        testUserProfileFields.fillAllFields("Name", "Surname", "nickname");
        assertEquals("Name", name.getText().toString());
        assertEquals("Surname", surname.getText().toString());
        assertEquals("nickname", nickname.getText().toString());
    }
}