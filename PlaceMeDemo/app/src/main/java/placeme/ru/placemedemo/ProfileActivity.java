package placeme.ru.placemedemo;

import android.content.Context;
import android.graphics.Canvas;
import android.graphics.Path;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.dd.CircularProgressButton;

public class ProfileActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_profile);

        //ImageView iv = (ImageView) findViewById(R.id)
        TextView tv = (TextView) findViewById(R.id.profile);
        tv.setText("Here is your profile");

        CircularProgressButton editButton  = (CircularProgressButton) findViewById(R.id.button_edit);
        //Button mEmailSignInButton = (Button) findViewById(R.id.email_sign_in_button);
        editButton.setIndeterminateProgressMode(true);
        editButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Toast.makeText(getApplicationContext(), "Button pressed!", Toast.LENGTH_SHORT).show();
            }
        });
        /*ImageButton editButton = (ImageButton) findViewById(R.id.button_edit);
        editButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Toast.makeText(getApplicationContext(), "Button pressed!", Toast.LENGTH_SHORT).show();
            }
        });*/
    }

   /* private class CircleImageView extends android.support.v7.widget.AppCompatImageView {
        public CircleImageView(Context context) {
            super(context);
        }

        @Override
        protected void onDraw(Canvas canvas) {
            //создаем круг
            final float halfWidth = canvas.getWidth()/2;
            final float halfHeight = canvas.getHeight()/2;
            final float radius = Math.max(halfWidth, halfHeight);
            final Path path = new Path();
            path.addCircle(halfWidth, halfHeight, radius, Path.Direction.CCW);

            //обрезаем
            canvas.clipPath(path);

            super.onDraw(canvas);
        }
    }*/
}
