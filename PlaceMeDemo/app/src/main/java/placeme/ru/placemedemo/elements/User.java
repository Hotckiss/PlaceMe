package placeme.ru.placemedemo.elements;

/**
 * Created by Андрей on 17.11.2017.
 */

/**
 * Class that describes all information about user of the app
 */
public class User {
    private int id;
    private String name;
    private String surname;
    private String nickname;
    private String favouritePlaces = "";
    private String friends = "";
    private int routesLength;

    /**
     * Default constructor that must be implemented for using class in firebase database
     */
    public User() {}

    /**
     * Constrictor of new user from all information about it
     * @param id user identification number
     * @param name user name
     * @param surname user surname
     * @param nickname user nickname
     */
    public User(int id, String name, String surname, String nickname) {
        this.id = id;
        this.name = name;
        this.surname = surname;
        this.nickname = nickname;
    }

    /**
     * Constrictor of new user from all information about it
     * with some favourite places by default
     * @param id user identification number
     * @param name user name
     * @param surname user surname
     * @param nickname user nickname
     * @param favouritePlaces default favourite places
     */
    public User(int id, String name, String surname, String nickname, String favouritePlaces) {
        this.id = id;
        this.name = name;
        this.surname = surname;
        this.nickname = nickname;
        this.favouritePlaces = favouritePlaces;
    }

    /**
     * Method that returns user id
     * @return user id
     */
    public int getId() {return id; }

    /**
     * Method that returns user name
     * @return user name
     */
    public String getName() {
        return name;
    }

    /**
     * Method that returns user surname
     * @return user surname
     */
    public String getSurname() {
        return surname;
    }

    /**
     * Method that returns user nickname
     * @return user nickname
     */
    public String getNickname() {
        return nickname;
    }

    /**
     * Method that returns user favourite places
     * @return user favourite places
     */
    public String getFavouritePlaces() { return favouritePlaces; }

    /**
     * Method that returns list of user friends
     * @return user friends list
     */
    public String getFriends() { return friends; }

    /**
     * Method that changes user id
     * @param newId new user id
     */
    public void setId(int newId) {this.id = newId; }

    /**
     * Method that change user name
     * @param newName new user name
     */
    public void setName(String newName) {
        this.name = newName;
    }

    /**
     * Method that change user surname
     * @param newSurname new user surname
     */
    public void setSurname(String newSurname) {
        this.surname = newSurname;
    }

    /**
     * Method that changes user nickname
     * @param newNickname new nickname of the user
     */
    public void setNickname(String newNickname) {
        this.nickname = newNickname;
    }

    /**
     * Method that change favourite places of the user
     * @param newFavouritePlaces new favourite places of the user
     */
    public void setFavouritePlaces(String newFavouritePlaces) { this.favouritePlaces = newFavouritePlaces; }

    /**
     * Method that change friends of the user
     * @param newFriends new friends of the user
     */
    public void setFriends(String newFriends) { this.friends = newFriends; }

    /**
     * Method that adds favourite place to user list
     * @param favouritePlace new favourite place of the user
     */
    public void addFavouritePlace(String favouritePlace) { favouritePlaces = favouritePlaces + "," + favouritePlace; }

    /**
     * Method that adds friend to user friend list
     * @param newFriend friend that should be added
     * @return true if friend was sucessfully added false otherwise (if it was already in the list, for example)
     */
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

    /**
     * Method that returns current length of the user friends list
     * @return current friends list length
     */
    public int getFriendsLength() {
        if (friends.length() == 0) {
            return 0;
        } else {
            return friends.split(",").length;
        }
    }

    public int getRoutesLength() {
        return routesLength;
    }

    public void setRoutesLength(int newLength) {
        routesLength = newLength;
    }
}
