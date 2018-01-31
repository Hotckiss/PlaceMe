package placeme.ru.placemedemo.elements;

import android.widget.TextView;

/**
 * Created by Андрей on 31.01.2018.
 */

/**
 * A class that provides to send data between profile and database manager
 */
public class UserProfileFields {
    private TextView mName;
    private TextView mSurname;
    private TextView mNickname;

    /**
     * Constructor that connect user profile fields with fields which need to be send to the database manager
     */
    public UserProfileFields(TextView name, TextView surname, TextView nickname) {
        mName = name;
        mSurname = surname;
        mNickname = nickname;
    }

    /**
     * Method that fills all text fields with currently important data
     * @param name user name
     * @param surname user surname
     * @param nickname user nickname
     */
    public void fillAllFields(final String name, final String surname, final String nickname) {
        mName.setText(name);
        mSurname.setText(surname);
        mNickname.setText(nickname);
    }
}
