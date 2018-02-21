package placeme.ru.placemedemo.elements;

import org.junit.Before;
import org.junit.Test;

import static org.junit.Assert.*;

/**
 * Created by aliscafo on 07.02.2018.
 */
public class UserTest {
    private User testUser;

    @Before
    public void initUser() {
        testUser = new User(100, "Name",
                "Surname", "nickname");
    }

    @Test
    public void testGetId() throws Exception {
        assertEquals(100, testUser.getId());
        testUser.setId(200);
        assertEquals(200, testUser.getId());
    }

    @Test
    public void testGetName() throws Exception {
        assertEquals("Name", testUser.getName());
        testUser.setName("Name2");
        assertEquals("Name2", testUser.getName());
    }

    @Test
    public void testGetSurname() throws Exception {
        assertEquals("Surname", testUser.getSurname());
        testUser.setSurname("Surname2");
        assertEquals("Surname2", testUser.getSurname());
    }

    @Test
    public void testGetNickname() throws Exception {
        assertEquals("nickname", testUser.getNickname());
        testUser.setNickname("nickname2");
        assertEquals("nickname2", testUser.getNickname());
    }

    @Test
    public void testGetFavouritePlaces() throws Exception {
        assertEquals("", testUser.getFavouritePlaces());
        testUser.addFavouritePlace("newPlace");
        assertEquals(",newPlace", testUser.getFavouritePlaces());
        testUser.setFavouritePlaces("newPlace1,newPlace2");
        assertEquals("newPlace1,newPlace2", testUser.getFavouritePlaces());
    }

    @Test
    public void testGetFriends() throws Exception {
        assertEquals("", testUser.getFriends());
        testUser.addFriend("friend");
        assertEquals(",friend", testUser.getFriends());
        testUser.setFriends("friend1,friend2");
        assertEquals("friend1,friend2", testUser.getFriends());
        assertFalse(testUser.addFriend("friend1"));
    }

    @Test
    public void getFriendsLength() throws Exception {
        assertEquals(0, testUser.getFriendsLength());
        testUser.setFriends("friend1,friend2");
        assertEquals(2, testUser.getFriendsLength());
    }

    @Test
    public void getRoutesLength() throws Exception {
        testUser.setRoutesLength(5);
        assertEquals(5, testUser.getRoutesLength());
    }

    @Test
    public void testDefaultConstructor() throws Exception {
        testUser = new User();

        assertNull(testUser.getName());
        assertNull(testUser.getSurname());
        assertNull(testUser.getNickname());
        assertEquals("", testUser.getFriends());
        assertEquals("", testUser.getFavouritePlaces());
        assertEquals(0, testUser.getRoutesLength());
    }

    @Test
    public void testConstructorWithPlaces() throws Exception {
        testUser = new User(100, "Name",
                "Surname", "nickname",
                "favouritePlace1,favouritePlace2");

        assertEquals("Name", testUser.getName());
        assertEquals("Surname", testUser.getSurname());
        assertEquals("nickname", testUser.getNickname());
        assertEquals("", testUser.getFriends());
        assertEquals("favouritePlace1,favouritePlace2", testUser.getFavouritePlaces());
        assertEquals(0, testUser.getRoutesLength());
    }
}