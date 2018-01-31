package placeme.ru.placemedemo.ui;

import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.support.v4.app.FragmentManager;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.widget.Button;

import com.tomergoldst.tooltips.ToolTip;
import com.tomergoldst.tooltips.ToolTipsManager;

import de.hdodenhof.circleimageview.CircleImageView;
import placeme.ru.placemedemo.R;
import placeme.ru.placemedemo.core.Controller;
import placeme.ru.placemedemo.elements.UserProfileFields;


/**
 * A class that represents a profile of user and his friends
 */
public class ProfileActivity extends AppCompatActivity {
    private static final int PROFILE_CHANGED_CODE = 1;
    private static final int GALLERY_INTENT = 2;
    private static final String IMAGE_INTENT = "image/*";

    private CircleImageView mCircleImageView;

    private ToolTipsManager mToolTipsManager;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_profile);

        mToolTipsManager = new ToolTipsManager();
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
        ToolTip.Builder builder = new ToolTip.Builder(this, mCircleImageView, findViewById(R.id.root_p), "Long tap\nto change avatar", ToolTip.POSITION_LEFT_TO);

        Controller.loadAvatar(mCircleImageView, ProfileActivity.this, Controller.getLoggedInAsString(ProfileActivity.this));

        mCircleImageView.setOnClickListener(v -> mToolTipsManager.show(builder.build()));

        mCircleImageView.setOnLongClickListener(v -> {
            createAlertDialogChangeAvatar().show();
            return true;
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
        ToolTip.Builder builder = new ToolTip.Builder(this, editButton, findViewById(R.id.root_p), "Tap to edit\nprofile", ToolTip.POSITION_LEFT_TO);

        editButton.setOnLongClickListener(v -> {
            mToolTipsManager.show(builder.build());
            return true;
        });

        editButton.setOnClickListener(v -> {
            Intent edit = new Intent(ProfileActivity.this, EditActivity.class);
            startActivityForResult(edit, PROFILE_CHANGED_CODE);
        });
    }

    private void loadUserProfile() {
        FragmentManager fm = getSupportFragmentManager();
        Controller.loadUserProfile(ProfileActivity.this,
                Controller.getLoggedIn(ProfileActivity.this), profileInfoFields(), fm);
    }

    private UserProfileFields profileInfoFields() {
        return new UserProfileFields(findViewById(R.id.name),
                findViewById(R.id.surname), findViewById(R.id.nickname));
    }
}
