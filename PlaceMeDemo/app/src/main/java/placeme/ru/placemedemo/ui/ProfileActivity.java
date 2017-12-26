package placeme.ru.placemedemo.ui;

import android.content.DialogInterface;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.support.v4.app.FragmentManager;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;

import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;
import com.squareup.picasso.Picasso;

import de.hdodenhof.circleimageview.CircleImageView;
import placeme.ru.placemedemo.R;
import placeme.ru.placemedemo.core.database.DatabaseManager;
import placeme.ru.placemedemo.core.utils.AuthorizationUtils;


//TODO: exclude storage
public class ProfileActivity extends AppCompatActivity {

    private StorageReference mStorageRef;
    private static final int GALLERY_INTENT = 2;
    private static final int PROFILE_CHANGED_CODE = 1;

    private CircleImageView civ;
    private Uri uri;
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
        if(requestCode == PROFILE_CHANGED_CODE) {
            loadUserProfile();
        }
        if(requestCode == GALLERY_INTENT && resultCode == RESULT_OK) {
            uri = data.getData();
            civ.setImageURI(uri);
            mStorageRef.child("avatars").child(AuthorizationUtils.getLoggedInAsString(ProfileActivity.this) + "avatar").putFile(uri);
        }
    }

    private void loadProfileImage() {
        mStorageRef = FirebaseStorage.getInstance().getReference();
        civ = findViewById(R.id.profile_image);
        StorageReference child = mStorageRef.child("avatars").child(AuthorizationUtils.getLoggedInAsString(ProfileActivity.this) + "avatar");
        child.getDownloadUrl().addOnSuccessListener(uri -> Picasso.with(ProfileActivity.this).load(uri)
                .placeholder(android.R.drawable.btn_star_big_on)
                .error(android.R.drawable.btn_star_big_on)
                .into(civ));
        civ.setOnLongClickListener(new View.OnLongClickListener() {
            @Override
            public boolean onLongClick(View v) {
                final AlertDialog.Builder builder = new AlertDialog.Builder(ProfileActivity.this);
                builder.setTitle("Changing avatar");
                builder.setMessage("Do you want to change avatar?\n(Gallery will be opened)");

                builder.setPositiveButton(R.string.answer_yes, new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        Intent intent = new Intent(Intent.ACTION_PICK);

                        intent.setType("image/*");
                        startActivityForResult(intent, GALLERY_INTENT);
                    }
                });

                builder.setNegativeButton(R.string.answer_no, new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        dialog.dismiss();
                    }
                });

                builder.setCancelable(true);
                builder.create().show();
                return false;
            }
        });
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
        DatabaseManager.loadUserProfile(ProfileActivity.this, AuthorizationUtils.getLoggedIn(ProfileActivity.this), userProfileFields, fm);
    }

    private TextView[] profileInfoFields() {
        TextView[] result = new TextView[3];
        result[0] = findViewById(R.id.name);
        result[1] = findViewById(R.id.surname);
        result[2] = findViewById(R.id.nickname);

        return result;
    }
}
