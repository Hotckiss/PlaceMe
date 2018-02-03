package placeme.ru.placemedemo.core.chat;

import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.view.Gravity;
import android.view.View;
import android.view.ViewGroup;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ScrollView;
import android.widget.TextView;

import com.firebase.client.DataSnapshot;
import com.firebase.client.Firebase;

import java.util.HashMap;
import java.util.Map;

import placeme.ru.placemedemo.R;
import placeme.ru.placemedemo.core.Controller;
import placeme.ru.placemedemo.core.database.AbstractClientChildEventListener;

/**
 * A class that represents chat dialog between two users in the application
 */
public class Chat extends AppCompatActivity {
    private static final String MESSAGES_LOCATION = "https://placemedemo-676f5.firebaseio.com/messages/";
    private static final String DELIMITER = "_";
    private static final String PAIR_DELIMITER = ",";
    private static final String MESSAGE_KEY = "message";
    private static final String USER_KEY = "user";
    private static final String EMPTY_MESSAGE = "";
    private static final int USER_MESSAGE = 1;
    private static final int FRIEND_MESSAGE = 2;

    private LinearLayout mLinearLayout;
    private ImageView mSendButton;
    private EditText mMessageArea;
    private ScrollView mScrollView;
    private Firebase mUserMessagesReference, mFriendMessagesReference;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_chat);

        initializeViews();

        Firebase.setAndroidContext(this);
        final String[] chatPair = Controller.getChatPair(Chat.this).split(PAIR_DELIMITER);

        if (chatPair.length < 2) {
            finish();
        }

        mUserMessagesReference = new Firebase(MESSAGES_LOCATION + chatPair[0] + DELIMITER + chatPair[1]);
        mFriendMessagesReference = new Firebase(MESSAGES_LOCATION + chatPair[1] + DELIMITER + chatPair[0]);

        mSendButton.setOnClickListener(v -> {
            String messageText = mMessageArea.getText().toString();

            if (checkMessage(messageText)){
                Map<String, String> map = new HashMap<>();
                map.put(MESSAGE_KEY, messageText);
                map.put(USER_KEY, chatPair[0]);

                mUserMessagesReference.push().setValue(map);
                mFriendMessagesReference.push().setValue(map);

                mMessageArea.setText(EMPTY_MESSAGE);
            }
        });

        mUserMessagesReference.addChildEventListener(new AbstractClientChildEventListener() {
            @Override
            public void onChildAdded(DataSnapshot dataSnapshot, String s) {
                Map map = dataSnapshot.getValue(Map.class);

                String message = map.get(MESSAGE_KEY).toString();
                String userName = map.get(USER_KEY).toString();

                if (userName.equals(Controller.getLoggedInAsString(Chat.this))){
                    addMessageBox(message, USER_MESSAGE);
                } else {
                    addMessageBox(message, FRIEND_MESSAGE);
                }
            }
        });
    }

    private void addMessageBox(String message, int type){
        TextView textView = new TextView(Chat.this);
        textView.setText(message);

        LinearLayout.LayoutParams layoutParams = new LinearLayout.LayoutParams(ViewGroup.LayoutParams.WRAP_CONTENT, ViewGroup.LayoutParams.WRAP_CONTENT);
        layoutParams.weight = 1f;

        if (type == USER_MESSAGE) {
            layoutParams.gravity = Gravity.RIGHT;
            textView.setBackgroundResource(R.drawable.bubble_in);
        } else {
            layoutParams.gravity = Gravity.LEFT;
            textView.setBackgroundResource(R.drawable.bubble_out);
        }

        textView.setLayoutParams(layoutParams);
        mLinearLayout.addView(textView);
        mScrollView.fullScroll(View.FOCUS_DOWN);
    }

    private void initializeViews() {
        mLinearLayout = findViewById(R.id.layout1);
        mSendButton = findViewById(R.id.sendButton);
        mMessageArea = findViewById(R.id.messageArea);
        mScrollView = findViewById(R.id.scrollView);
    }

    private boolean checkMessage(final String message) {
        if (message.length() == 0) {
            return false;
        }

        for (int i = 0; i < message.length(); i++) {
            if ((message.charAt(i) != ' ') && (message.charAt(i) != '\n')) {
                return true;
            }
        }

        return false;
    }
}