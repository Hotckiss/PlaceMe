package placeme.ru.placemedemo.elements;

/**
 * Created by DNS on 17.11.2017.
 */

public class User {

    private int id;
    private String name;
    private String surname;
    private String nickname;
    private String favouritePlaces;
    private String friends = "";

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

    public String getFriends() { return friends; }

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

    public void setFriends(String newFriends) { this.friends = newFriends; }

    public void addFavouritePlace(String favPlace) { favouritePlaces = favouritePlaces + "," + favPlace; }

    public boolean addFriend(String newFriend) {
        String[] currentFriends = friends.split(",");
        for (String friend : currentFriends) {
            if(newFriend.equals(friend)) {
                return false;
            }
        }

        friends = friends + "," + newFriend;

        return true;
    }

    public int getFriendsLength() {
        if (friends.length() == 0) {
            return 0;
        } else {
            return friends.split(",").length;
        }
    }
}
