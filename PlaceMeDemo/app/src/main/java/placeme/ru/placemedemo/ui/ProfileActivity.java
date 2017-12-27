package placeme.ru.placemedemo.ui;

import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.support.v4.app.FragmentManager;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.widget.Button;
import android.widget.TextView;

import de.hdodenhof.circleimageview.CircleImageView;
import placeme.ru.placemedemo.R;
import placeme.ru.placemedemo.core.Controller;
import placeme.ru.placemedemo.core.database.DatabaseManager;
import placeme.ru.placemedemo.core.utils.AuthorizationUtils;


/**
 * A class that represents a profile of user and his friends
 */
public class ProfileActivity extends AppCompatActivity {
    private static final int PROFILE_CHANGED_CODE = 1;
    private static final int GALLERY_INTENT = 2;
    private static final String IMAGE_INTENT = "image/*";

    private CircleImageView mCircleImageView;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_profile);

        //TODO: make good placeholder for profile image: instead of star
        loadProfileImage();

        setEditButton();

        loadUserProfile();
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (requestCode == PROFILE_CHANGED_CODE) {
            loadUserProfile();
        }
        if (requestCode == GALLERY_INTENT && resultCode == RESULT_OK) {
            Uri uri = data.getData();
            mCircleImageView.setImageURI(uri);

            Controller.setNewAvatar(Controller.getLoggedInAsString(ProfileActivity.this), uri);
        }
    }

    private void loadProfileImage() {

        mCircleImageView = findViewById(R.id.profile_image);

        Controller.loadAvatar(mCircleImageView, ProfileActivity.this,
                Controller.getLoggedInAsString(ProfileActivity.this));

        mCircleImageView.setOnLongClickListener(v -> {
            createAlertDialogChangeAvatar().show();
            return false;
        });
    }

    private AlertDialog createAlertDialogChangeAvatar() {
        final AlertDialog.Builder builder = new AlertDialog.Builder(ProfileActivity.this);
        builder.setTitle(R.string.profile_changing_avatar);
        builder.setMessage(R.string.profile_changing_avatar_message);

        builder.setPositiveButton(R.string.answer_yes, (dialog, which) -> {
            Intent intent = new Intent(Intent.ACTION_PICK);

            intent.setType(IMAGE_INTENT);
            startActivityForResult(intent, GALLERY_INTENT);
        });

        builder.setNegativeButton(R.string.answer_no, (dialog, which) -> dialog.dismiss());

        builder.setCancelable(true);
        return builder.create();
    }
    private void setEditButton() {
        Button editButton  = findViewById(R.id.button_edit);
        editButton.setOnClickListener(v -> {
            Intent edit = new Intent(ProfileActivity.this, EditActivity.class);
            startActivityForResult(edit, PROFILE_CHANGED_CODE);
        });
    }

    private void loadUserProfile() {
        TextView[] userProfileFields = profileInfoFields();
        FragmentManager fm = getSupportFragmentManager();
        Controller.loadUserProfile(ProfileActivity.this, Controller.getLoggedIn(ProfileActivity.this), userProfileFields, fm);
    }

    private TextView[] profileInfoFields() {
        TextView[] result = new TextView[3];
        result[0] = findViewById(R.id.name);
        result[1] = findViewById(R.id.surname);
        result[2] = findViewById(R.id.nickname);

        return result;
    }
}
