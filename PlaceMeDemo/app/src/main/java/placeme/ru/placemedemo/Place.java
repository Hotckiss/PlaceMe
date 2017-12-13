package placeme.ru.placemedemo;

/**
 * Created by Андрей on 20.11.2017.
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

    Place() {
    }

    Place(int id, String name, String description, String tags, double latitude, double longitude) {
        this.id = id;
        this.name = name;
        this.description = description;
        this.tags = tags;
        this.latitude = latitude;
        this.longitude = longitude;
        this.numberOfRatings = 0;
        this.sumOfMarks = (float)0.0;
    }

    public void AddTags(String newTags) {
        this.description = this.description + "," + newTags;
    }

    public float getMark() {
        if (numberOfRatings == 0) {
            return (float)0;
        }
        return (float)sumOfMarks/ numberOfRatings;
    }

    public int getId() {
        return id;
    }

    public String getName() {
        return name;
    }

    public String getDescription() {
        return description;
    }

    public String getTags() { return tags; }

    public double getLatitude() {
        return latitude;
    }

    public double getLongitude() {
        return longitude;
    }

    public long getNumberOfRatings() { return numberOfRatings; }

    public float getSumOfMarks() { return sumOfMarks; }

    public void setId(int newId) {
        this.id = newId;
    }

    public void setName(String newName) {
        this.name = newName;
    }

    public void setDescription(String newDescription) {
        this.description = newDescription;
    }

    public void setTags(String newTags) {
        this.tags = newTags;
    }

    public void setLatitude(double newLatitude) {
        this.latitude = newLatitude;
    }

    public void setLongitude(double newLongitude) {
        this.longitude = newLongitude;
    }

    public void setNumberOfRatings(long newNumberOfRatings) { this.numberOfRatings = newNumberOfRatings; }

    public void setSumOfMarks(float newSumOfMarks) { this.sumOfMarks = newSumOfMarks; }
}
