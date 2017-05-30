package cmpe277project.nk.org.a277_socialmobileapp.login;


import android.app.ProgressDialog;
import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.VolleyLog;
import com.android.volley.toolbox.JsonObjectRequest;

import org.json.JSONException;
import org.json.JSONObject;

import java.util.HashMap;

import cmpe277project.nk.org.a277_socialmobileapp.R;

public class LoginActivity extends AppCompatActivity {

    private static final String TAG = "LoginActivity";
    private static final String URL_FOR_LOGIN = "https://www.gmail.com";//"http://XXX.XXX.X.XX/android_login_example/login.php";
    ProgressDialog progressDialog;
    private EditText loginInputEmail, loginInputPassword;
    private Button btnlogin;
    private Button btnLinkSignup;
    //LoginDataBaseAdapter loginDataBaseAdapter;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);
        loginInputEmail = (EditText) findViewById(R.id.login_input_email);
        loginInputPassword = (EditText) findViewById(R.id.login_input_password);
        btnlogin = (Button) findViewById(R.id.btn_login);
        btnLinkSignup = (Button) findViewById(R.id.btn_link_signup);
        // Progress dialog
        progressDialog = new ProgressDialog(this);
        progressDialog.setCancelable(false);

        btnlogin.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                loginUser(loginInputEmail.getText().toString(),
                        loginInputPassword.getText().toString());
            }
        });

        btnLinkSignup.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent i = new Intent(getApplicationContext(), RegisterActivity.class);
                startActivity(i);
            }
        });
    }

    private void loginUser( final String email, final String password) {
        // Tag used to cancel the request
        String cancel_req_tag = "login";
        progressDialog.setMessage("Logging you in...");
        showDialog();
        //narasa

        final String URL = "http://sample-env.qjp9mjnymz.us-west-2.elasticbeanstalk.com/login";
        // Post params to be sent to the server
        HashMap<String, String> params = new HashMap<String, String>();
        params.put("screenName", loginInputEmail.getText().toString());
        params.put("password", loginInputPassword.getText().toString());
        params.put("verificationCode", "0");


        JsonObjectRequest req = new JsonObjectRequest(URL, new JSONObject(params),
                new Response.Listener<JSONObject>() {
                    @Override
                    public void onResponse(JSONObject response) {
                        Log.d(TAG, response.toString());
                        hideDialog();

                        try {

                            //JSONObject code = response.getJSONObject("code");
                            String code =response.getString("code");
                            String message=response.getString("message");
                            if(code.equals("200")) {
                                 Intent intent = new Intent(
                                        LoginActivity.this,
                                        cmpe277project.nk.org.a277_socialmobileapp.Home.class);

                                intent.putExtra("username", email);
                                intent.putExtra("password", password);

                                startActivity(intent);
                                finish();

                            }
                            else if(code.equals("404") && message.equals("profile does not exists")) {
                                Toast.makeText(LoginActivity.this, "Invalid username", Toast.LENGTH_LONG).show();
                            }
                            else if(code.equals("404") && message.equals("need password")) {
                                Toast.makeText(LoginActivity.this, "Invalid password", Toast.LENGTH_LONG).show();
                            }
                            else if(code.equals("404") && message.equals("need verification code")) {
                                Intent intent = new Intent(
                                        LoginActivity.this,
                                        LoginVerification.class);

                                intent.putExtra("username", email);
                                intent.putExtra("password", password);

                                startActivity(intent);
                                finish();
                            }
                            else {
                                Toast.makeText(LoginActivity.this, "Invalid username or password", Toast.LENGTH_LONG).show();
                            }
                        } catch (JSONException e) {
                            e.printStackTrace();
                        }
                    }
                }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {
                VolleyLog.e("Error: ", error.getMessage());
                hideDialog();
            }
        });


        //previous code
        //Map jsonuser<String,String>=new Map<String,String>;

       /* StringRequest strReq = new StringRequest(Request.Method.GET,
                URL_FOR_LOGIN, new Response.Listener<String>() {

            @Override
            public void onResponse(String response) {
                Log.d(TAG, "Register Response: " + response.toString());
                hideDialog();
                try {
                    JSONObject jObj = new JSONObject(response);
                    boolean error = jObj.getBoolean("error");

                    if (!error) {
                        //String user = //jObj.getJSONObject("user").getString("name");
                        // Launch User activity
                        Intent intent = new Intent(
                                LoginActivity.this,
                                UserActivity.class);
                        intent.putExtra("username", loginInputEmail.getText().toString());
                        startActivity(intent);
                        finish();
                    //} else {

                        //String errorMsg = jObj.getString("error_msg");
                        Toast.makeText(getApplicationContext(),
                                "Logged In", Toast.LENGTH_LONG).show();
                    //}
                } /*catch (JSONException e) {
                    e.printStackTrace();
                }*/

        // }
        /*}, new Response.ErrorListener() {

            @Override
            public void onErrorResponse(VolleyError error) {
                Log.e(TAG, "Login Error: " + error.getMessage());
                Toast.makeText(getApplicationContext(),
                        error.getMessage(), Toast.LENGTH_LONG).show();
                hideDialog();
            }
        }) {

            @Override
            protected Map<String, String> getParams() {
                // Posting params to login url
                Map<String, String> params = new HashMap<String, String>();
                params.put("email", email);
                params.put("password", password);
                return params;
            }

        };*/
        // Adding request to request queue
        AppSingleton.getInstance(getApplicationContext()).addToRequestQueue(req,cancel_req_tag);

    }

    private void showDialog() {
        if (!progressDialog.isShowing())
            progressDialog.show();
    }
    private void hideDialog() {
        if (progressDialog.isShowing())
            progressDialog.dismiss();
    }

}
