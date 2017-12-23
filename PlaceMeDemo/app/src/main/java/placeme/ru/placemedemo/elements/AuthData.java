package placeme.ru.placemedemo.elements;

/**
 * Created by Андрей on 18.11.2017.
 */

/**
 * Class that represents authentication data of the user
 */
public class AuthData {

    private int id;
    private String login;
    private String password;

    /**
     * Default constructor that must be implemented for using class in firebase database
     */
    public AuthData() {}

    /**
     * Constructor that creates new authentication data
     * @param id user id
     * @param login user login
     * @param password user password
     */
    public AuthData(int id, String login, String password) {
        this.id = id;
        this.login = login;
        this.password = password;
    }

    /**
     * Method that returns id of user, who owns this authentication data
     * @return user id
     */
    public int getId() {return id; }

    /**
     * Method that returns user login
     * @return user login
     */
    public String getLogin() {
        return login;
    }

    /**
     * Method that returns user password
     * @return user password
     */
    public String getPassword() {
        return password;
    }

    /**
     * Method that changes user id
     * @param newId new id
     */
    public void setId(int newId) {this.id = newId; }

    /**
     * Method that changes user login
     * @param newLogin new user login
     */
    public void setLogin(String newLogin) {
        this.login = newLogin;
    }

    /**
     * Method that changes user password
     * @param newPassword new user password
     */
    public void setPassword(String newPassword) {
        this.password = newPassword;
    }

}
