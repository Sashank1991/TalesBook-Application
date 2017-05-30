package cmpe277project.nk.org.a277_socialmobileapp.login;

import android.app.ProgressDialog;
import android.content.Intent;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
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

public class LoginVerification extends AppCompatActivity {

    private static String username,password,verificationCode;
    EditText etVerificationCode;
    Button buttonVerify;
    ProgressDialog progressDialog;
    private static final String TAG = "LoginVerification";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login_verification);


        if (savedInstanceState == null) {
            Bundle extras = getIntent().getExtras();
            if(extras == null) {
                username= null;
            } else {
                username= extras.getString("username");
                password= extras.getString("password");
            }
        } else {
            username= (String) savedInstanceState.getSerializable("username");
            password= (String) savedInstanceState.getSerializable("password");
        }

        etVerificationCode=(EditText) findViewById(R.id.editText_verificationcode);
        buttonVerify=(Button) findViewById(R.id.button_verifyaccount);
        progressDialog = new ProgressDialog(this);
        progressDialog.setCancelable(false);


        buttonVerify.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                verificationCode=etVerificationCode.getText().toString();
                verifyAccount();
            }
        });


    }

    private void verifyAccount() {

        String cancel_req_tag = "login";
        progressDialog.setMessage("Verifying your account...");
        showDialog();
        //narasa

        final String URL = "http://sample-env.qjp9mjnymz.us-west-2.elasticbeanstalk.com/login";
        // Post params to be sent to the server
        final HashMap<String, String> params = new HashMap<String, String>();
        params.put("screenName", username);
        params.put("password", password);
        params.put("verificationCode", verificationCode);


        JsonObjectRequest req = new JsonObjectRequest(URL, new JSONObject(params),
                new Response.Listener<JSONObject>() {
                    @Override
                    public void onResponse(JSONObject response) {
                        Log.d(TAG, response.toString());
                        Log.d("profile verifi ffor-->",new JSONObject(params).toString());
                        hideDialog();

                        try {

                            //JSONObject code = response.getJSONObject("code");
                            String code =response.getString("code");
                            String message=response.getString("message");
                            if(code.equals("200")) {
                                Intent intent = new Intent(
                                        LoginVerification.this,
                                        cmpe277project.nk.org.a277_socialmobileapp.Home.class);

                                //intent.putExtra("profileType", profileSetting);
                                intent.putExtra("username", username);
                                intent.putExtra("password", password);

                                //intent.putExtra("data", extp);
                                startActivity(intent);
                                finish();
                            }
                            else if(code.equals("404") && message.equals("need verification code")) {
                                Toast.makeText(getApplicationContext(),"Invalid Verification Code",Toast.LENGTH_SHORT).show();
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
