package placeme.ru.placemedemo;

/**
 * Created by DNS on 17.11.2017.
 */

public class User {

    private int id;
    private String name;
    private String surname;
    private String nickname;
    private String favouritePlaces;

    public User() {}

    public User(int id, String name, String surname, String nickname) {
        this.id = id;
        this.name = name;
        this.surname = surname;
        this.nickname = nickname;
        this.favouritePlaces = "";
    }

    public User(int id, String name, String surname, String nickname, String favouritePlaces) {
        this.id = id;
        this.name = name;
        this.surname = surname;
        this.nickname = nickname;
        this.favouritePlaces = favouritePlaces;
    }

    public int getId() {return id; }

    public String getName() {
        return name;
    }

    public String getSurname() {
        return surname;
    }

    public String getNickname() {
        return nickname;
    }

    public String getFavouritePlaces() { return favouritePlaces; }

    public void setId(int newId) {this.id = newId; }

    public void setName(String newName) {
        this.name = newName;
    }

    public void setSurname(String newBio) {
        this.surname = newBio;
    }

    public void setNickname(String newProfileImg) {
        this.nickname = newProfileImg;
    }

    public void setFavouritePlaces(String newFavouritePlaces) { this.favouritePlaces = newFavouritePlaces; }

    public void addFavouritePlace(String favPlace) { favouritePlaces = favouritePlaces + "," + favPlace; }

}
