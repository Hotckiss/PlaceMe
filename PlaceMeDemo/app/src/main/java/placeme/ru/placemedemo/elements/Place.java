package placeme.ru.placemedemo.elements;

/**
 * Created by Андрей on 20.11.2017.
 */

/**
 * A class that describes information about single place in the app
 */
public class Place {
    private int id;
    private String name;
    private String description;
    private String tags;
    private double latitude;
    private double longitude;
    private long numberOfRatings;
    private float sumOfMarks;

    /**
     * Default constructor which must be implemented for using class
     * within firebase database
     */
    public Place() {}

    /**
     * Constructor of the place with all information
     * @param id id of the place
     * @param name name of the place
     * @param description description of the place
     * @param tags tags associated with the place
     * @param latitude latitude of the place on map
     * @param longitude longitude of the place on map
     */
    public Place(int id, String name, String description, String tags, double latitude, double longitude) {
        this.id = id;
        this.name = name;
        this.description = description;
        this.tags = tags;
        this.latitude = latitude;
        this.longitude = longitude;
        this.numberOfRatings = 0;
        this.sumOfMarks = 0f;
    }

    /**
     * Method that allows to add tags to place
     * @param newTags tags that should be added
     */
    public void AddTags(String newTags) {
        this.description = this.description + "," + newTags;
    }

    /**
     * Method that returns current average rating of the place
     * @return place rating
     */
    public float getMark() {
        if (numberOfRatings == 0) {
            return (float)0;
        }
        return sumOfMarks / numberOfRatings;
    }

    /**
     * Method that returns id of the place
     * @return place id
     */
    public int getId() {
        return id;
    }

    /**
     * Method that returns id of the place
     * @return place id as string value
     */
    public String getIdAsString() {
        return String.valueOf(id);
    }

    /**
     * Method that returns name of the place
     * @return place name
     */
    public String getName() {
        return name;
    }

    /**
     * Method that returns description of the place
     * @return place description
     */
    public String getDescription() {
        return description;
    }

    /**
     * Method that returns tags of the place
     * @return place tags
     */
    public String getTags() { return tags; }

    /**
     * Method that returns latitude of the place
     * @return place latitude
     */
    public double getLatitude() {
        return latitude;
    }

    /**
     * Method that returns longitude of the place
     * @return place longitude
     */
    public double getLongitude() {
        return longitude;
    }

    /**
     * Method that returns total number of ratings of the place
     * @return total number of ratings
     */
    public long getNumberOfRatings() { return numberOfRatings; }

    /**
     * Method that returns total sum of ratings of the place
     * @return total sum of ratings
     */
    public float getSumOfMarks() { return sumOfMarks; }

    /**
     * Method that changes id of the place
     * @param newId new id of place
     */
    public void setId(int newId) {
        this.id = newId;
    }

    /**
     * Method that changes name of the place
     * @param newName new name of place
     */
    public void setName(String newName) {
        this.name = newName;
    }

    /**
     * Method that changes description of the place
     * @param newDescription new description of the place
     */
    public void setDescription(String newDescription) {
        this.description = newDescription;
    }

    /**
     * Method that changes tags of the place
     * @param newTags new tags of the place
     */
    public void setTags(String newTags) {
        this.tags = newTags;
    }

    /**
     * Method that changes latitude of the place
     * @param newLatitude new latitude of the place
     */
    public void setLatitude(double newLatitude) {
        this.latitude = newLatitude;
    }

    /**
     * Method that changes longitude of the place
     * @param newLongitude new longitude of the place
     */
    public void setLongitude(double newLongitude) {
        this.longitude = newLongitude;
    }

    /**
     * Method that changes number of ratings
     * @param newNumberOfRatings new number of ratings
     */
    public void setNumberOfRatings(long newNumberOfRatings) { this.numberOfRatings = newNumberOfRatings; }

    /**
     * Method that changes sum of ratings
     * @param newSumOfMarks new sum of ratings
     */
    public void setSumOfMarks(float newSumOfMarks) { this.sumOfMarks = newSumOfMarks; }
}
