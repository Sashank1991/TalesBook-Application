package cmpe277project.nk.org.a277_socialmobileapp.login;


import android.app.ProgressDialog;
import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.RadioGroup;
import android.widget.Toast;

import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.JsonObjectRequest;

import org.json.JSONException;
import org.json.JSONObject;

import java.util.HashMap;
import java.util.Map;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import cmpe277project.nk.org.a277_socialmobileapp.R;


public class RegisterActivity extends AppCompatActivity {

    private static final String TAG = "RegisterActivity";
    private static final String URL_FOR_REGISTRATION = "http://Sample-env.qjp9mjnymz.us-west-2.elasticbeanstalk.com";
    ProgressDialog progressDialog;

    private EditText signupInputName, signupInputEmail, signupInputPassword, signupInputAge;
    private Button btnSignUp;
    private Button btnLinkLogin;
    private RadioGroup genderRadioGroup;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_register);

        // Progress dialog
        progressDialog = new ProgressDialog(this);
        progressDialog.setCancelable(false);

        signupInputName = (EditText) findViewById(R.id.signup_input_name);
        signupInputEmail = (EditText) findViewById(R.id.signup_input_email);
        signupInputPassword = (EditText) findViewById(R.id.signup_input_password);
        //signupInputAge = (EditText) findViewById(R.id.signup_input_age);

        btnSignUp = (Button) findViewById(R.id.btn_signup);
        btnLinkLogin = (Button) findViewById(R.id.btn_link_login);

        genderRadioGroup = (RadioGroup) findViewById(R.id.gender_radio_group);
        btnSignUp.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                submitForm();
            }
        });
        btnLinkLogin.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                Intent i = new Intent(getApplicationContext(),LoginActivity.class);
                startActivity(i);
            }
        });
    }

    private void submitForm() {

        int selectedId = genderRadioGroup.getCheckedRadioButtonId();
        String profile;
        if(selectedId == R.id.female_radio_btn)
            profile = "friendsOnly";
        else
            profile = "public";

        registerUser(signupInputName.getText().toString(),
                signupInputEmail.getText().toString(),
                signupInputPassword.getText().toString(),profile);
    }

    private void registerUser(final String screenName,  final String email, final String password,
                              String profileType) {
        // Tag used to cancel the request
        String cancel_req_tag = "register";

        if(screenName.trim().equals("") || screenName.trim().isEmpty() || screenName==null) {
            Toast.makeText(getApplicationContext(),"Username can not be empty",Toast.LENGTH_LONG).show();
            return;
        }

        if(email.trim().isEmpty() || email.trim().equals("") || email==null) {
            Toast.makeText(getApplicationContext(),"Email id can not be empty",Toast.LENGTH_LONG).show();
            return;
        }

        if(!isValidEmailAddress(email)) {
            Toast.makeText(getApplicationContext(),"Invalid email id",Toast.LENGTH_LONG).show();
            return;
        }

        if(password.trim().equals("") || password.trim().isEmpty() || password==null) {
            Toast.makeText(getApplicationContext(),"Password can not be empty",Toast.LENGTH_LONG).show();
            return;
        }


        progressDialog.setMessage("Adding you ...");
        showDialog();

        final String URL = "http://Sample-env.qjp9mjnymz.us-west-2.elasticbeanstalk.com/createProfile";
        // Post params to be sent to the server
        HashMap<String, String> params = new HashMap<String, String>();
        params.put("screenName", screenName);
        //params.put("extendedProfile":{"email"}", email);
        params.put("email", email);
        params.put("password", password);
        params.put("profileType", profileType);

        Log.d("profile sent---",new JSONObject(params).toString());

        JsonObjectRequest req = new JsonObjectRequest(URL, new JSONObject(params),
                new Response.Listener<JSONObject>() {

                    @Override
                    public void onResponse(JSONObject response) {
                        Log.d(TAG, "Register Response: " + response.toString());
                        hideDialog();
                        try {
                            String code = response.getString("code");
                            if (code.equals("200")) {
                                //Toast.makeText(getApplicationContext(), "Profile update Successfully!!", Toast.LENGTH_LONG).show();
                                Log.d("Create profile-------->", "Success!!!");
                                Toast.makeText(getApplicationContext(), response.getString("message"), Toast.LENGTH_LONG).show();
                                //Launch login activity
                                Intent intent = new Intent(
                                RegisterActivity.this,
                                LoginVerification.class);
                                intent.putExtra("username",screenName);
                                intent.putExtra("password",password);
                                startActivity(intent);
                                finish();

                            } else {
                                Log.d("Create profile-------->", "Failed!!!");
                                Toast.makeText(getApplicationContext(), response.getString("message"), Toast.LENGTH_LONG).show();

                            }
                        }
                        catch (JSONException e) {
                            e.printStackTrace();
                        }

                    }
                }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {
                Log.e(TAG, "Registration Error: " + error.getMessage());
                hideDialog();
            }
        });
        // Adding request to request queue
        AppSingleton.getInstance(getApplicationContext()).addToRequestQueue(req, cancel_req_tag);
    }

    private void showDialog() {
        if (!progressDialog.isShowing())
            progressDialog.show();
    }

    private void hideDialog() {
        if (progressDialog.isShowing())
            progressDialog.dismiss();
    }

    public static boolean isValidEmailAddress(String email) {

        String regex = "^(.+)@(.+)[.](.+)$";
        Pattern pattern = Pattern.compile(regex);
        Matcher matcher = pattern.matcher(email);
//            System.out.println(email +" : "+ matcher.matches());
        return matcher.matches();
    }

}