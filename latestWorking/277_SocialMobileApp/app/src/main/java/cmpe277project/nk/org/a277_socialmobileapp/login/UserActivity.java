package cmpe277project.nk.org.a277_socialmobileapp.login;


import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;

import cmpe277project.nk.org.a277_socialmobileapp.R;


public class UserActivity extends AppCompatActivity {

    private static final String TAG = "UserActivity";

    private TextView greetingTextView;
    private Button btnLogOut;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_user);
        Bundle bundle = getIntent().getExtras();
        String user = bundle.getString("username");
        final String profileType = bundle.getString("profileType");
        final String extendedP=bundle.getString("data");
      /*  try {
            JSONObject json=new JSONObject(extendedP);
        } catch (JSONException e) {
            e.printStackTrace();
        }*/
        //Log.d("data in Uactivity:",extendedP);

        greetingTextView = (TextView) findViewById(R.id.greeting_text_view);
        btnLogOut = (Button) findViewById(R.id.logout_button);
        greetingTextView.setText("Hello "+ user);
        // Progress dialog
        btnLogOut.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent i = new Intent(getApplicationContext(), SwipeActivity.class);
                i.putExtra("profileType",profileType);
                i.putExtra("data",extendedP);
                i.putExtra("profileType",profileType);

                startActivity(i);
            }
        });
    }
}


