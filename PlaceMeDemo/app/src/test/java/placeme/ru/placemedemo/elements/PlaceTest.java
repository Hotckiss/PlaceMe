package placeme.ru.placemedemo.elements;

import org.junit.Before;
import org.junit.Test;

import static org.junit.Assert.*;

/**
 * Created by aliscafo on 06.02.2018.
 */
public class PlaceTest {
    private Place testPlace;

    @Before
    public void initPlace() {
        testPlace = new Place(100, "TestPlace", "This is the test place.", "tags", 50, 60);
    }

    @Test
    public void testGetId() throws Exception {
        assertEquals(100, testPlace.getId());
        testPlace.setId(101);
        assertEquals(101, testPlace.getId());
    }

    @Test
    public void testGetIdAsString() throws Exception {
        assertEquals("100", testPlace.getIdAsString());
    }

    @Test
    public void testGetName() throws Exception {
        assertEquals("TestPlace", testPlace.getName());
        testPlace.setName("TestPlace2");
        assertEquals("TestPlace2", testPlace.getName());
    }

    @Test
    public void testGetDescription() throws Exception {
        assertEquals("This is the test place.", testPlace.getDescription());
        testPlace.setDescription("This is the test place after setting description.");
        assertEquals("This is the test place after setting description.",
                testPlace.getDescription());
    }

    @Test
    public void testGetTags() throws Exception {
        assertEquals("tags", testPlace.getTags());
        testPlace.setTags("tags,place");
        assertEquals("tags,place", testPlace.getTags());
    }

    @Test
    public void testGetLatitude() throws Exception {
        assertEquals(50, testPlace.getLatitude(), 0);
        testPlace.setLatitude(70);
        assertEquals(70, testPlace.getLatitude(), 0);
    }

    @Test
    public void testGetLongitude() throws Exception {
        assertEquals(60, testPlace.getLongitude(), 0);
        testPlace.setLongitude(30);
        assertEquals(30, testPlace.getLongitude(), 0);
    }

    @Test
    public void testGetNumberOfRatings() throws Exception {
        testPlace.setNumberOfRatings(100);
        assertEquals(100, testPlace.getNumberOfRatings());
    }

    @Test
    public void testGetSumOfMarks() throws Exception {
        testPlace.setSumOfMarks(200);
        assertEquals(200, testPlace.getSumOfMarks(), 0);
    }

    @Test
    public void testGetMark() throws Exception {
        assertEquals(0, testPlace.getMark(), 0);
        testPlace.setSumOfMarks(200);
        testPlace.setNumberOfRatings(100);
        assertEquals(2, testPlace.getMark(), 0);
    }

    @Test
    public void testDefaultConstructor() throws Exception {
        testPlace = new Place();

        assertEquals(0, testPlace.getId());
        assertNull(testPlace.getDescription());
        assertNull(testPlace.getName());
        assertNull(testPlace.getTags());
        assertEquals(0, testPlace.getLatitude(), 0);
        assertEquals(0, testPlace.getLongitude(), 0);
        assertEquals(0, testPlace.getNumberOfRatings(), 0);
        assertEquals(0, testPlace.getSumOfMarks(), 0);
    }
}