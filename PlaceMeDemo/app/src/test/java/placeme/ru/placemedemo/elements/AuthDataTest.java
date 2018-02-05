package placeme.ru.placemedemo.elements;

import org.junit.Before;
import org.junit.Test;

import static org.junit.Assert.*;

/**
 * Class that contains tests to all methods of AuthData class
 * Created by Андрей on 05.02.2018.
 */
public class AuthDataTest {
    private AuthData testData;

    @Before
    public void initAuthData() {
        testData = new AuthData(7, "login@test.com", "12345");
    }

    @Test
    public void testGetId() throws Exception {
        assertEquals(7, testData.getId());
    }

    @Test
    public void testGetLogin() throws Exception {
        assertEquals("login@test.com", testData.getLogin());
    }

    @Test
    public void testGetPassword() throws Exception {
        assertEquals("12345", testData.getPassword());
    }

    @Test
    public void testSetId() throws Exception {
        testData.setId(17);

        assertEquals(17, testData.getId());
    }

    @Test
    public void testSetLogin() throws Exception {
        testData.setLogin("new@login.com");

        assertEquals("new@login.com", testData.getLogin());
    }

    @Test
    public void testSetPassword() throws Exception {
        testData.setPassword("qwerty");

        assertEquals("qwerty", testData.getPassword());
    }

}