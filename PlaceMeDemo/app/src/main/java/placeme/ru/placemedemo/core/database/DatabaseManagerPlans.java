package placeme.ru.placemedemo.core.database;

import android.content.Context;
import android.net.Uri;
import android.widget.ArrayAdapter;

import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;
import com.squareup.picasso.Picasso;

import de.hdodenhof.circleimageview.CircleImageView;
import placeme.ru.placemedemo.R;

import static placeme.ru.placemedemo.core.database.DatabaseUtils.getDatabaseChild;

/**
 * Class that have all methods connected with plans and reviews in database
 * it allows to save user visit plans and upload them to special plans activity
 * what's more, class allows to save user reviews on places
 * Created by Андрей on 21.12.2017.
 */
public class DatabaseManagerPlans {
    private static final String USER_AVATAR_KEY = "avatars";
    private static final String AVATAR_SUFFIX = "avatar";
    private static final String REVIEWS_KEY = "reviews";
    private static final String PLANS_KEY = "plans";
    private static final String TUPLE_DELIMITER = ";";
    private static final String END_DELIMITER = "\n";

    private static FirebaseDatabase mBase = FirebaseDatabase.getInstance();
    private static StorageReference mStorageRef = FirebaseStorage.getInstance().getReference();

    /**
     * Method that loads new avatar of user instead of old one
     * @param userId user id who changes avatar
     * @param uri uri of new avatar image
     */
    public static void setNewAvatar(final String userId, final Uri uri) {
        mStorageRef.child(USER_AVATAR_KEY).child(userId + AVATAR_SUFFIX).putFile(uri);
    }

    /**
     * Method that adds visiting plan to users plans list
     * @param placeName name of place to visit
     * @param userId id of user who wants to visit place
     * @param date date of planned visit
     */
    public static void addPlan(final String placeName, final String userId, final String date) {
        DatabaseReference reference = getDatabaseChild(mBase, PLANS_KEY);

        if (reference != null) {
            reference = reference.child(userId);
            reference.addListenerForSingleValueEvent(new AbstractValueEventListener() {
                @Override
                public void onDataChange(DataSnapshot dataSnapshot) {
                    Object plan = dataSnapshot.getValue();
                    DatabaseReference referencePlan = getDatabaseChild(mBase, PLANS_KEY);
                    if (referencePlan != null) {
                        referencePlan = referencePlan.child(userId);
                    }

                    if (referencePlan != null) {
                        if (plan != null) {
                            String currentPlan = dataSnapshot.getValue().toString();
                            referencePlan.setValue(currentPlan + TUPLE_DELIMITER + placeName + END_DELIMITER + date);
                        } else {
                            referencePlan.setValue(placeName + END_DELIMITER + date);
                        }
                    }
                }
            });
        }
    }

    /**
     * Method that loads visiting plan to the screen
     * @param userId id of user whose plan should be loaded
     * @param adapter adapter with plans list
     */
    public static void loadPlan(final String userId, final ArrayAdapter<String> adapter) {
        DatabaseReference reference = getDatabaseChild(mBase, PLANS_KEY);

        if (reference != null) {
            reference = reference.child(userId);
            reference.addListenerForSingleValueEvent(new AbstractValueEventListener() {
                @Override
                public void onDataChange(DataSnapshot dataSnapshot) {
                    Object plan = dataSnapshot.getValue();
                    if (plan != null) {
                        String currentPlan = plan.toString();
                        if (currentPlan.length() != 0) {
                            String[] plans = currentPlan.split(TUPLE_DELIMITER);
                            for (String planSingle : plans) {
                                String[] splatted = planSingle.split(END_DELIMITER);
                                if (splatted.length != 0 && DatabaseUtils.validatePlan(splatted[splatted.length - 1])) {
                                    adapter.add(planSingle);
                                }
                            }
                        }
                    }
                }
            });
        }
    }

    /**
     * Method that loads avatar to the field with picture of user
     * @param circleImageView image view to load avatar
     * @param context current context
     * @param userId id of the user
     */
    public static void loadAvatar(CircleImageView circleImageView, final Context context, final String userId) {
        mStorageRef.child(USER_AVATAR_KEY).child(userId + AVATAR_SUFFIX)
                .getDownloadUrl().addOnSuccessListener(uri -> Picasso.with(context).load(uri)
                .placeholder(R.drawable.anonim)
                .error(R.drawable.anonim)
                .into(circleImageView));
    }

    /**
     * Method that adds user review to all reviews about place
     * @param placeId reviewed place
     * @param review user review text
     */
    public static void addReview(final String placeId, final String review) {
        DatabaseReference reference = getDatabaseChild(mBase, REVIEWS_KEY);
        if (reference != null) {
            reference = reference.child(placeId);
            reference.addListenerForSingleValueEvent(new AbstractValueEventListener() {
                @Override
                public void onDataChange(DataSnapshot dataSnapshot) {
                    Object reviews = dataSnapshot.getValue();
                    DatabaseReference referenceNewReviews = getDatabaseChild(mBase, REVIEWS_KEY);
                    if (referenceNewReviews != null) {
                        if (reviews == null) {

                            referenceNewReviews = referenceNewReviews.child(placeId);
                            referenceNewReviews.setValue(review);
                        } else {
                            String allReviews = reviews.toString();
                            if (allReviews.length() == 0) {

                                referenceNewReviews = referenceNewReviews.child(placeId);
                                referenceNewReviews.setValue(review);
                            } else {

                                referenceNewReviews = referenceNewReviews.child(placeId);
                                referenceNewReviews.setValue(allReviews + TUPLE_DELIMITER + review);
                            }
                        }
                    }
                }
            });
        }
    }

    /**
     * Method that loads all users reviews to array adapter
     * @param placeId place which reviews should be extracted
     * @param adapter destination adapter to load reviews
     */
    public static void findReviews(final String placeId, ArrayAdapter<String> adapter) {
        DatabaseReference reference = getDatabaseChild(mBase, REVIEWS_KEY);
        if (reference != null) {
            reference = reference.child(placeId);
            reference.addListenerForSingleValueEvent(new AbstractValueEventListener() {
                @Override
                public void onDataChange(DataSnapshot dataSnapshot) {
                    Object data = dataSnapshot.getValue();
                    if (data != null) {
                        String allReviews = data.toString();
                        String[] arrayReviews = allReviews.split(TUPLE_DELIMITER);

                        for (String singleReview : arrayReviews) {
                            adapter.add(singleReview);
                        }
                    }
                }
            });
        }
    }
}
