package placeme.ru.placemedemo.elements;

/**
 * Created by Андрей on 18.11.2017.
 */

//TODO: javadoc
public class AuthData {

    private int id;
    private String login;
    private String password;

    public AuthData() {}

    public AuthData(int id, String login, String password) {
        this.id = id;
        this.login = login;
        this.password = password;
    }

    public int getId() {return id; }

    public String getLogin() {
        return login;
    }

    public String getPassword() {
        return password;
    }

    public void setId(int newId) {this.id = newId; }

    public void setLogin(String newLogin) {
        this.login = newLogin;
    }

    public void setPassword(String newPassword) {
        this.password = newPassword;
    }

}
