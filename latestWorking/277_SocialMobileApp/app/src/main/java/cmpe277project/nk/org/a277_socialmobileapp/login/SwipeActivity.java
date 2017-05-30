package cmpe277project.nk.org.a277_socialmobileapp.login;

import android.app.Activity;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.Intent;
import android.content.res.Resources.Theme;
import android.net.Uri;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.ThemedSpinnerAdapter;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemSelectedListener;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.CompoundButton;
import android.widget.EditText;
import android.widget.ListAdapter;
import android.widget.ListView;
import android.widget.Spinner;
import android.widget.Switch;
import android.widget.TextView;
import android.widget.Toast;

import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.VolleyLog;
import com.android.volley.toolbox.ImageLoader;
import com.android.volley.toolbox.JsonObjectRequest;
import com.android.volley.toolbox.NetworkImageView;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.File;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.concurrent.ExecutionException;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import cmpe277project.nk.org.a277_socialmobileapp.Home;
import cmpe277project.nk.org.a277_socialmobileapp.InsertImageToS3;
import cmpe277project.nk.org.a277_socialmobileapp.Messaging.MessagingActivity;
import cmpe277project.nk.org.a277_socialmobileapp.R;
import cmpe277project.nk.org.a277_socialmobileapp.get_path_from_gallery.PathUtils;
import cmpe277project.nk.org.a277_socialmobileapp.posts.MyFriendsList;
import cmpe277project.nk.org.a277_socialmobileapp.posts.Post;
import cmpe277project.nk.org.a277_socialmobileapp.volley.AppController;
import cmpe277project.nk.org.a277_socialmobileapp.volley.CustomListAdapter;
import cmpe277project.nk.org.a277_socialmobileapp.volley.DisplayFollowers;
import cmpe277project.nk.org.a277_socialmobileapp.volley.DisplayFriends;

public class SwipeActivity extends AppCompatActivity {
    //private EditText loginInputEmail;
    static JSONObject obj, objMyPosts;
    private String email = null;
    private Button btnUpdate;
    static ProgressDialog pDialog;
    static String username, profilePic, password, newImageS3URL = "";
    static CustomListAdapter adapter;
    private static List<Post> postList = new ArrayList<Post>();
    JSONObject fullprofile;
    String profileType;
    String extendedP;
    static ImageLoader imageLoader;
    static NetworkImageView ivUserImage;
    private static final int PICK_PHOTO_FOR_AVATAR = 0;
    private static final String TAG = SwipeActivity.class.getSimpleName();
    static TextView emailText;
    static EditText aboutMeText;
    static EditText professionText;
    static EditText interestsText;
    static EditText locationText;
    static EditText passwordText;
    static EditText userNameEditText;
    static Switch profileTypeSwitch;
    static Button buttonUpdate;
    static String profileTypeSwitchText;
    static DisplayFriends adapter_friends;
    static List<MyFriendsList> friendsLists;
    static ListView lv_listMyFriends;

    //static ImageLoader imageLoader = AppController.getInstance().getImageLoader();
    //EditText emailText = (EditText) findViewById(R.id.editTextEmail);
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_swipe);

        if (imageLoader == null)
            imageLoader = AppController.getInstance().getImageLoader();

        //EditText emailText = (EditText) findViewById(R.id.editTextEmail);

        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        //setSupportActionBar(toolbar);
        //getSupportActionBar().setDisplayShowTitleEnabled(false);
        Bundle bundle = getIntent().getExtras();
//        final String extendedP=bundle.getString("data");
        final String extendedProfile = bundle.getString("data");
//        String profileType=bundle.getString("profileType");
        try {
            JSONObject extendedP = new JSONObject(extendedProfile);
            String remainingP = extendedP.getString("extendedProfile");
            obj = new JSONObject(remainingP);
            Log.d("My extended P App", obj.toString());
            profileTypeSwitchText = extendedP.getString("profileType");

        } catch (JSONException e) {
            e.printStackTrace();
        }
        try {
            // JSONObject


            objMyPosts = new JSONObject(bundle.getString("data"));
            password = bundle.getString("password");

            username = objMyPosts.getString("screenName");
            profilePic = objMyPosts.getJSONObject("extendedProfile").getString("profilePic");

        } catch (JSONException e) {
            e.printStackTrace();
        }
//        Log.d("body",extendedP.toString());


        //final String extendedP=bundle.getString("data");
        // Setup spinner
        Spinner spinner = (Spinner) findViewById(R.id.spinner);
        spinner.setAdapter(new MyAdapter(
                toolbar.getContext(),
                new String[]{
                        "About Me",
                        "My Posts",
                        "Friends",
                        "Follow Someone",
                        "Messages"
                }));

        spinner.setOnItemSelectedListener(new OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                // When the given dropdown item is selected, show its contents in the
                // container view.
                getSupportFragmentManager().beginTransaction()
                        .replace(R.id.container, PlaceholderFragment.newInstance(position + 1))
                        .commit();
            }

            @Override
            public void onNothingSelected(AdapterView<?> parent) {
            }
        });
    }


    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.menu_basic, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        int id = item.getItemId();

        if (id == R.id.action_logout) {
            try {
                final String URL = "http://Sample-env.qjp9mjnymz.us-west-2.elasticbeanstalk.com/logout";
                HashMap<String, String> params = new HashMap<String, String>();
                params.put("screenName", objMyPosts.getString("screenName"));
                JsonObjectRequest req = new JsonObjectRequest(URL, new JSONObject(params),
                        new Response.Listener<JSONObject>() {
                            @Override
                            public void onResponse(JSONObject response) {
                                Log.d("response for put", response.toString());
                                //hideDialog();
                                try {
                                    String code = response.getString("code");
                                    String message = response.getString("message");
                                    if (code.equals("200")) {
                                        Toast.makeText(getApplication().getApplicationContext(), message, Toast.LENGTH_LONG).show();
                                        Intent i = new Intent(getApplication().getApplicationContext(), LoginActivity.class);
                                        i.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
                                        startActivity(i);
                                    } else {
                                        Toast.makeText(getApplication().getApplicationContext(), message, Toast.LENGTH_LONG).show();
                                    }
                                } catch (JSONException e) {
                                    e.printStackTrace();
                                }
                            }
                        }, new Response.ErrorListener() {
                    @Override
                    public void onErrorResponse(VolleyError error) {
                        VolleyLog.e("Error: ", error.getMessage());
                        //hideDialog();
                    }
                });

                cmpe277project.nk.org.a277_socialmobileapp.volley.AppController.getInstance().addToRequestQueue(req);

            } catch (JSONException e) {
                e.printStackTrace();
            }
            return true;
        }

        return super.onOptionsItemSelected(item);
    }


    private static void updateUserProfile(final Context appContext, HashMap<String, String> params) {

        final String URL = "http://Sample-env.qjp9mjnymz.us-west-2.elasticbeanstalk.com/updateProfile";
        Log.d("updateUserProfile", "called");
        Log.d("params sent", new JSONObject(params).toString());

        JsonObjectRequest req = new JsonObjectRequest(URL, new JSONObject(params),
                new Response.Listener<JSONObject>() {
                    @Override
                    public void onResponse(JSONObject response) {
                        Log.d("response for put", response.toString());
                        //hideDialog();
                        try {
                            String code = response.getString("code");
                            String message = response.getString("message");
                            if (code.equals("200")) {

                                Toast.makeText(appContext, "Profile update Successfully!!", Toast.LENGTH_LONG).show();
                                username = userNameEditText.getText().toString();

                                objMyPosts.remove("screenName");
                                objMyPosts.put("screenName", username);


                                Log.d("update profile-------->", "Success!!!");


                            } else if (code.equals("404") && message.equals("screen Name already exists")) {
                                Toast.makeText(appContext, "Username exists. Try with different username", Toast.LENGTH_LONG).show();
                            } else {
                                Log.d("update profile-------->", "Failed!!!");

                                Toast.makeText(appContext, "Username or password is wrong", Toast.LENGTH_LONG).show();
                                //progressDialog.setMessage("Username or password is wrong!");
                                //showDialog();
                            }
                        } catch (JSONException e) {
                            e.printStackTrace();
                        }
                    }
                }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {
                VolleyLog.e("Error: ", error.getMessage());
                //hideDialog();
            }
        });

        cmpe277project.nk.org.a277_socialmobileapp.volley.AppController.getInstance().addToRequestQueue(req);

    }

    private static class MyAdapter extends ArrayAdapter<String> implements ThemedSpinnerAdapter {
        private final ThemedSpinnerAdapter.Helper mDropDownHelper;

        public MyAdapter(Context context, String[] objects) {
            super(context, android.R.layout.simple_list_item_1, objects);
            mDropDownHelper = new ThemedSpinnerAdapter.Helper(context);
        }

        @Override
        public View getDropDownView(int position, View convertView, ViewGroup parent) {
            View view;

            if (convertView == null) {
                // Inflate the drop down using the helper's LayoutInflater
                LayoutInflater inflater = mDropDownHelper.getDropDownViewInflater();
                view = inflater.inflate(android.R.layout.simple_list_item_1, parent, false);
            } else {
                view = convertView;
            }

            TextView textView = (TextView) view.findViewById(android.R.id.text1);
            textView.setText(getItem(position));

            return view;
        }

        @Override
        public Theme getDropDownViewTheme() {
            return mDropDownHelper.getDropDownViewTheme();
        }

        @Override
        public void setDropDownViewTheme(Theme theme) {
            mDropDownHelper.setDropDownViewTheme(theme);
        }
    }

    /**
     * A placeholder fragment containing a simple view.
     */
    public static class PlaceholderFragment extends Fragment {
        /**
         * The fragment argument representing the section number for this
         * fragment.
         */
        private static final String ARG_SECTION_NUMBER = "section_number";

        public PlaceholderFragment() {
        }

        /**
         * Returns a new instance of this fragment for the given section
         * number.
         */
        public static PlaceholderFragment newInstance(int sectionNumber) {
            PlaceholderFragment fragment = new PlaceholderFragment();
            Bundle args = new Bundle();
            args.putInt(ARG_SECTION_NUMBER, sectionNumber);
            fragment.setArguments(args);
            return fragment;
        }

        @Override
        public View onCreateView(LayoutInflater inflater, ViewGroup container,
                                 Bundle savedInstanceState) {

            //do post again

            View rootView = inflater.inflate(R.layout.fragment_posts, container, false);
            //View rootView = inflater.inflate(R.layout.fragment_swipe, container, false);
            //TextView textView = (TextView) rootView.findViewById(R.id.section_label);
            //textView.setText(getString(R.string.section_format, getArguments().getInt(ARG_SECTION_NUMBER)));
            if (getArguments().getInt(ARG_SECTION_NUMBER) == 1) {
                rootView = inflater.inflate(R.layout.fragment_swipe, container, false);

                final View currentView = rootView;

                emailText = (TextView) rootView.findViewById(R.id.editTextEmail);
                aboutMeText = (EditText) rootView.findViewById(R.id.editTextAboutMe);
                professionText = (EditText) rootView.findViewById(R.id.editTextProfession);
                interestsText = (EditText) rootView.findViewById(R.id.editTextInterests);
                locationText = (EditText) rootView.findViewById(R.id.editTextLocation);
                passwordText = (EditText) rootView.findViewById(R.id.editTextPassword);
                userNameEditText = (EditText) rootView.findViewById(R.id.editTextUserNameProfile);
                profileTypeSwitch = (Switch) rootView.findViewById(R.id.switch1);
                buttonUpdate = (Button) rootView.findViewById(R.id.buttonUpdate);
                ivUserImage = (NetworkImageView) rootView.findViewById(R.id.imageView_UserImage);

                ivUserImage.setDefaultImageResId(R.drawable.userimageweb);

                ivUserImage.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        Intent intent = new Intent(Intent.ACTION_GET_CONTENT);
                        intent.setType("image/*");
                        startActivityForResult(intent, PICK_PHOTO_FOR_AVATAR);
                    }
                });
                getProfile();

                Log.d(TAG, "===========OBJ info in About Me ==========");
                Log.d(TAG, objMyPosts.toString());

                profileTypeSwitch.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
                    public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                        // do something, the isChecked will be
                        // true if the switch is in the On position
                        if (isChecked) {
                            profileTypeSwitchText = "friendsOnly";
                        } else {
                            profileTypeSwitchText = "Public";
                        }
                    }
                });

                buttonUpdate.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
//                         Toast.makeText(getApplicationContext(),"Post button clicked",Toast.LENGTH_SHORT).show();
                        Log.d("button", "clicked");

                        String userNameTemp=userNameEditText.getText().toString();
                        String paswordTemp=passwordText.getText().toString();

                        if(userNameTemp.trim().equals("") || userNameTemp.trim().isEmpty() || userNameTemp==null) {
                            Toast.makeText(getActivity().getApplicationContext(),"Username can not be empty",Toast.LENGTH_LONG).show();
                            return;
                        }

                        if(paswordTemp.trim().equals("") || paswordTemp.trim().isEmpty() || paswordTemp==null) {
                            Toast.makeText(getActivity().getApplicationContext(),"Password can not be empty",Toast.LENGTH_LONG).show();
                            return;
                        }


                        HashMap<String, String> params = new HashMap<String, String>();
                        params.put("email", emailText.getText().toString());
                        params.put("aboutMe", aboutMeText.getText().toString());
                        params.put("interests", interestsText.getText().toString());
                        params.put("location", locationText.getText().toString());
                        params.put("profession", professionText.getText().toString());


                        params.put("profileType", profileTypeSwitchText);
                        try {
                            // username????
                            params.put("screenName", objMyPosts.getString("screenName"));
                        } catch (JSONException e) {
                            e.printStackTrace();
                        }
                        params.put("screenNameNew", userNameEditText.getText().toString());//screenNameText.getText().toString());--->change this to fetch val from body
                        params.put("password", passwordText.getText().toString());


                        try {
                            if (newImageS3URL.equals("")) {
                                params.put("profilePic", objMyPosts.getJSONObject("extendedProfile").getString("profilePic"));
                            } else {
                                params.put("profilePic", newImageS3URL);
                            }
                        } catch (JSONException e) {
                            e.printStackTrace();
                        }

                        //final String URL = "http://Sample-env.qjp9mjnymz.us-west-2.elasticbeanstalk.com/updateProfile";

                        updateUserProfile(getActivity().getApplicationContext(), params);

                    }
                });
                return rootView;
            }

            // My Posts
            else if (getArguments().getInt(ARG_SECTION_NUMBER) == 2) {
                rootView = inflater.inflate(R.layout.fragment_posts, container, false);

                getMyPosts(rootView);


                //open user setting page to update his profile
                return rootView;
            }

            // Friends Tab
            else if (getArguments().getInt(ARG_SECTION_NUMBER) == 3) {
                rootView = inflater.inflate(R.layout.fragment_friends, container, false);


                lv_listMyFriends = (ListView) rootView.findViewById(R.id.listFriends);

                friendsLists = new ArrayList<MyFriendsList>();
                adapter_friends = new DisplayFriends(getActivity(), friendsLists,username);

                adapter_friends.setView(rootView);
//                getProfile();
                /*TextView textView = (TextView) rootView.findViewById(R.id.section_label);
                textView.setText(getString(R.string.section_format, getArguments().getInt(ARG_SECTION_NUMBER)));
                textView.setText("Friends are..");*/

                /*TextView textView = (TextView) rootView.findViewById(R.id.section_label);
                textView.setText(getString(R.string.section_format, getArguments().getInt(ARG_SECTION_NUMBER)));
                textView.setText("Friends are..");*/
                try {


                    getMyFriends();
                    final View friendsView=rootView;

                    Button btnAddFriend = (Button) rootView.findViewById(R.id.btnAddasFriend);
                    final EditText getEmail = (EditText) rootView.findViewById(R.id.txtfriendEmail);

                    final String screenName = objMyPosts.getString("screenName");
                    btnAddFriend.setOnClickListener(new View.OnClickListener() {
                        @Override
                        public void onClick(View v) {
                            String email = getEmail.getText().toString();
                            if (email.equals("")) {
                                Toast.makeText(getActivity().getApplication(), "Please enter email", Toast.LENGTH_SHORT).show();
                            }
                            else if(!isValidEmailAddress(email)) {
                                Toast.makeText(getActivity().getApplicationContext(),"Invalid email id",Toast.LENGTH_LONG).show();
                                return;
                            }

                            else {
                                final String URL = "http://Sample-env.qjp9mjnymz.us-west-2.elasticbeanstalk.com/addAsFriend";

                                HashMap<String, String> params = new HashMap<String, String>();
                                params.put("screenName", screenName);
                                params.put("email", email);
                                JsonObjectRequest req = new JsonObjectRequest(URL, new JSONObject(params),
                                        new Response.Listener<JSONObject>() {
                                            @Override
                                            public void onResponse(JSONObject response) {
                                                try {
                                                    getEmail.setText("");
//                                                    Toast.makeText(getActivity().getApplication(), "Message :" + response.getString("message"), Toast.LENGTH_SHORT).show();
                                                    Toast.makeText(getActivity().getApplication(), "Friend request sent", Toast.LENGTH_LONG).show();

                                                    friendsLists.clear();
                                                    getMyFriends();

//                                                    getProfile();
//                                                    Log.d(TAG,"--------objMyposts----------After getProfile()");
//                                                    Log.d(TAG,objMyPosts.getJSONArray("friends").toString());
//                                                    getMyFriends(friendsView);
//
//                                                    Log.d(TAG,"--------objMyposts----------After getFriends()");
//                                                    Log.d(TAG,objMyPosts.getJSONArray("friends").toString());

                                                } catch (
                                                        Exception e) {
                                                    e.printStackTrace();
                                                    Toast.makeText(getActivity().getApplication(), "Error Occured", Toast.LENGTH_SHORT).show();
                                                }
                                                adapter_friends.notifyDataSetChanged();
                                            }
                                        }, new Response.ErrorListener() {
                                    @Override
                                    public void onErrorResponse(VolleyError error) {
                                        VolleyLog.e("Error: ", error.getMessage());

                                        Toast.makeText(getActivity().getApplication(), "Error Occured", Toast.LENGTH_SHORT).show();
                                    }
                                });


                                // add the request object to the queue to be executed
                                cmpe277project.nk.org.a277_socialmobileapp.volley.AppController.getInstance().addToRequestQueue(req);

                            }

                        }
                    });
                } catch (
                        JSONException e) {
                    e.printStackTrace();
                    Toast.makeText(getActivity().getApplication(), "Error Occured", Toast.LENGTH_SHORT).show();
                }
                return rootView;
                //open user setting page to update his profile
            }

            // Follow someone tab
            else if (getArguments().getInt(ARG_SECTION_NUMBER) == 4) {
                rootView = inflater.inflate(R.layout.fragment_followsomeone, container, false);

                /*TextView textView = (TextView) rootView.findViewById(R.id.section_label);
                textView.setText(getString(R.string.section_format, getArguments().getInt(ARG_SECTION_NUMBER)));
                textView.setText("Friends are..");*/

                /*TextView textView = (TextView) rootView.findViewById(R.id.section_label);
                textView.setText(getString(R.string.section_format, getArguments().getInt(ARG_SECTION_NUMBER)));
                textView.setText("Friends are..");*/
                funFollowSomeone(rootView);

                return rootView;
                //open user setting page to update his profile
            }
            // Follow someone tab
            else if (getArguments().getInt(ARG_SECTION_NUMBER) == 5) {
                rootView = inflater.inflate(R.layout.fragment_followsomeone, container, false);

                /*TextView textView = (TextView) rootView.findViewById(R.id.section_label);
                textView.setText(getString(R.string.section_format, getArguments().getInt(ARG_SECTION_NUMBER)));
                textView.setText("Friends are..");*/

                /*TextView textView = (TextView) rootView.findViewById(R.id.section_label);
                textView.setText(getString(R.string.section_format, getArguments().getInt(ARG_SECTION_NUMBER)));
                textView.setText("Friends are..");*/
                Intent i = new Intent(rootView.getContext(), MessagingActivity.class);
                i.putExtra("screenName", username);
//                i.putExtra("username",username);
                i.putExtra("recipient", "");


                startActivity(i);

//                funFollowSomeone(rootView);

                return rootView;
                //open user setting page to update his profile
            }
//            LoginActivity

            return rootView;
        }

        public static boolean isValidEmailAddress(String email) {

            String regex = "^(.+)@(.+)[.](.+)$";
            Pattern pattern = Pattern.compile(regex);
            Matcher matcher = pattern.matcher(email);
//            System.out.println(email +" : "+ matcher.matches());
            return matcher.matches();
        }

        private void getProfile() {

            final String URL = "http://Sample-env.qjp9mjnymz.us-west-2.elasticbeanstalk.com/getProfile";
            // Post params to be sent to the server
            HashMap<String, String> params = new HashMap<String, String>();
            params.put("screenName", username);

            JsonObjectRequest req = new JsonObjectRequest(URL, new JSONObject(params),
                    new Response.Listener<JSONObject>() {
                        @Override
                        public void onResponse(JSONObject response) {
                            Log.d(TAG, response.toString());
                            hidePDialog();

                            try {
                                objMyPosts = response.getJSONObject("data");
                                JSONObject extendedProfile = objMyPosts.getJSONObject("extendedProfile");

                                emailText.setText(extendedProfile.getString("email"));
                                aboutMeText.setText(extendedProfile.getString("aboutMe"));
                                interestsText.setText(extendedProfile.getString("interests"));
                                locationText.setText(extendedProfile.getString("location"));
                                professionText.setText(extendedProfile.getString("profession"));
                                userNameEditText.setText(objMyPosts.getString("screenName"));
                                passwordText.setText(objMyPosts.getString("password"));
                                profilePic = extendedProfile.getString("profilePic");
                                if(profilePic.length() > 0) {
                                    ivUserImage.setImageUrl(profilePic, imageLoader);
                                }
                                else {
                                    ivUserImage.setDefaultImageResId(R.drawable.userimageweb);
//                                ivUserImage.setErrorImageResId(R.drawable.error);
                                }


                                if (profileTypeSwitchText.equals("friendsOnly")) {
                                    profileTypeSwitch.setChecked(true);
                                } else {
                                    profileTypeSwitch.setChecked(false);
                                }


                            } catch (JSONException e) {
                                e.printStackTrace();
                            }

                        }
                    }, new Response.ErrorListener() {
                @Override
                public void onErrorResponse(VolleyError error) {
                    VolleyLog.e("Error: ", error.getMessage());
                    // hidePDialog();
                }
            });

            // add the request object to the queue to be executed
            AppController.getInstance().addToRequestQueue(req);
        }

        private void funFollowSomeone(View rootView) {


            ImageLoader imageLoader = AppController.getInstance().getImageLoader();
            final List<MyFriendsList> friendsLists = new ArrayList<MyFriendsList>();
            final ListView lv_listMyFriends = (ListView) rootView.findViewById(R.id.listpublicprofiles);
            DisplayFollowers adapter = new DisplayFollowers(getActivity(), friendsLists,username);
//            adapter.setView(rootView);

            lv_listMyFriends.setAdapter(adapter);

            try {
                String username = objMyPosts.getString("screenName");


                // Get all my posts
                final String URL = "http://Sample-env.qjp9mjnymz.us-west-2.elasticbeanstalk.com/getpublicprofiles";
                // Post params to be sent to the server
                HashMap<String, String> params = new HashMap<String, String>();
                params.put("screenName", username);


                JsonObjectRequest req = new JsonObjectRequest(URL, new JSONObject(params),
                        new Response.Listener<JSONObject>() {
                            @Override
                            public void onResponse(JSONObject response) {
                                Log.d(TAG, response.toString());
                                hidePDialog();

                                try {
                                    JSONArray posts = response.getJSONArray("data");

                                    for (int i = 0; i < posts.length(); i++) {
                                        try {
                                            JSONObject myObject = posts.getJSONObject(i);
                                            MyFriendsList friend = new MyFriendsList();
                                            friend.setUserProfileScreenName(objMyPosts.getString("screenName"));
                                            friend.setScreenName(myObject.getString("screenName"));
                                            friend.setStatus("public");
                                            friend.setEmail(myObject.getJSONObject("extendedProfile").getString("email"));
                                            friend.setUserId(myObject.getString("userId"));
                                            friend.setUserPostImageURL(myObject.getJSONObject("extendedProfile").getString("profilePic"));

                                            friendsLists.add(friend);

                                        } catch (JSONException e) {
                                            e.printStackTrace();
                                            hidePDialog();
                                        }
                                    }
                                    setListViewHeightBasedOnChildren(lv_listMyFriends);

                                } catch (JSONException e) {
                                    e.printStackTrace();
                                }
                            }
                        }, new Response.ErrorListener() {
                    @Override
                    public void onErrorResponse(VolleyError error) {
                        VolleyLog.e("Error: ", error.getMessage());
                        hidePDialog();
                    }
                });

                // add the request object to the queue to be executed
                cmpe277project.nk.org.a277_socialmobileapp.volley.AppController.getInstance().addToRequestQueue(req);

            } catch (
                    JSONException e)

            {
                e.printStackTrace();
                hidePDialog();
            }

        }

        public void getMyFriends() {

            ImageLoader imageLoader = AppController.getInstance().getImageLoader();

            friendsLists.clear();
            lv_listMyFriends.setAdapter(adapter_friends);


            final String URL = "http://Sample-env.qjp9mjnymz.us-west-2.elasticbeanstalk.com/getProfile";
            // Post params to be sent to the server
            HashMap<String, String> params = new HashMap<String, String>();
            params.put("screenName", username);

            JsonObjectRequest req = new JsonObjectRequest(URL, new JSONObject(params),
                    new Response.Listener<JSONObject>() {
                        @Override
                        public void onResponse(JSONObject response) {
                            Log.d(TAG, response.toString());
                            hidePDialog();

                            try {
                                objMyPosts = response.getJSONObject("data");
                                String username = objMyPosts.getString("screenName");
                                JSONArray friends = objMyPosts.getJSONArray("friends");
                                for (int i = 0; i < friends.length() && i < 50; i++) {
                                    try {

                                        JSONObject myObject = friends.getJSONObject(i);
                                        MyFriendsList friend = new MyFriendsList();
                                        friend.setUserProfileScreenName(objMyPosts.getString("screenName"));
                                        friend.setScreenName(myObject.getJSONObject("friend").getString("screenName"));
                                        friend.setStatus(myObject.getString("status"));
                                        friend.setUserId(myObject.getJSONObject("friend").getString("userId"));
                                        friend.setEmail(myObject.getJSONObject("friend").getJSONObject("extendedProfile").getString("email"));
                                        friend.setUserPostImageURL(myObject.getJSONObject("friend").getJSONObject("extendedProfile").getString("profilePic"));

                                        friendsLists.add(friend);

                                    } catch (JSONException e) {
                                        e.printStackTrace();
                                        hidePDialog();
                                    }
                                }
                                setListViewHeightBasedOnChildren(lv_listMyFriends);
                            }
                            catch (JSONException e) {
                                e.printStackTrace();
                                hidePDialog();
                            }

                        }
                    }, new Response.ErrorListener() {
                @Override
                public void onErrorResponse(VolleyError error) {
                    VolleyLog.e("Error: ", error.getMessage());
                     hidePDialog();
                }
            });

            // add the request object to the queue to be executed
            AppController.getInstance().addToRequestQueue(req);




        }

        private void getMyPosts(View rootView) {

            ImageLoader imageLoader = AppController.getInstance().getImageLoader();


            final ListView lv_listMyPosts = (ListView) rootView.findViewById(R.id.list_myposts);
            postList = new ArrayList<Post>();
            adapter = new CustomListAdapter(getActivity(), postList);
            lv_listMyPosts.setAdapter(adapter);

            pDialog = new ProgressDialog(getActivity());
            // Showing progress dialog before making http request
            pDialog.setMessage("Loading posts. Please wait...");
            pDialog.show();


            try {
                username = objMyPosts.getString("screenName");
                profilePic = objMyPosts.getJSONObject("extendedProfile").getString("profilePic");
            } catch (JSONException e) {
                e.printStackTrace();
            }


            // changing action bar color

//        getActionBar().setBackgroundDrawable(
//                new ColorDrawable(Color.parseColor("#1b1b1b")));


            // Creating volley request obj

            //Has data of use profile
            //obj
            /*try {
                String username = obj.getString("screenName");
                String profilePic = obj.getJSONObject("extendedProfile").getString("profilePic");
                JSONArray posts=obj.getJSONArray("posts");

                for (int i = 0; i < posts.length() && i < 50; i++) {
                    try {

                        JSONObject obj = posts.getJSONObject(i);
                        Post post = new Post();

                        if (obj.getString("assImage") != "") {
                            post.setUserPostImageURL(obj.getString("assImage"));
                            post.setIsUserPostImagePresent(true);
                        } else {
                            post.setIsUserPostImagePresent(false);
                        }
                        post.setUserPost(obj.getString("content"));


                        post.setUserName(username);
//
//                                    Log.v(TAG,"============================");
//                                    Log.v(TAG,obj.getString("assImage"));
//                                    Log.v(TAG,"============================");
//                                    Log.v(TAG,"");
                        if (profilePic != "") {
                            post.setUserImageURL(profilePic);
//                                        post.setIsUserPostImagePresent(true);
                        }

                        // adding post to posts array
                        postList.add(post);

                    } catch (JSONException e) {
                        e.printStackTrace();
                        hidePDialog();
                    }

                }
            }
            catch (JSONException e)  {
                e.printStackTrace();
                hidePDialog();
            }

            hidePDialog();
*/

            // Get all my posts
            final String URL = "http://Sample-env.qjp9mjnymz.us-west-2.elasticbeanstalk.com/getProfile";
            // Post params to be sent to the server
            HashMap<String, String> params = new HashMap<String, String>();
            params.put("screenName", username);


            JsonObjectRequest req = new JsonObjectRequest(URL, new JSONObject(params),
                    new Response.Listener<JSONObject>() {
                        @Override
                        public void onResponse(JSONObject response) {
                            Log.d(TAG, response.toString());
                            hidePDialog();

                            try {
                                objMyPosts=response.getJSONObject("data");

                                JSONArray posts = response.getJSONObject("data").getJSONArray("posts");

                                for (int i = 0; i < posts.length() && i < 50; i++) {
                                    try {

                                        JSONObject obj_temp_post = posts.getJSONObject(i);
                                        Post post = new Post();

                                        if (!obj_temp_post.getString("assImage").equals("")) {
                                            post.setUserPostImageURL(obj_temp_post.getString("assImage"));
                                            post.setIsUserPostImagePresent(true);
                                        } else {
                                            post.setIsUserPostImagePresent(false);
                                        }
                                        post.setUserPost(obj_temp_post.getString("content"));


                                        post.setUserName(username);
//
//                                    Log.v(TAG,"============================");
//                                    Log.v(TAG,obj_temp_post.getString("assImage"));
//                                    Log.v(TAG,"============================");
//                                    Log.v(TAG,"");
                                        if (!objMyPosts.getJSONObject("extendedProfile").getString("profilePic").equals("")) {
                                            post.setUserImageURL(objMyPosts.getJSONObject("extendedProfile").getString("profilePic"));
//                                        post.setIsUserPostImagePresent(true);
                                        }

                                        // adding post to posts array
                                        postList.add(post);

                                    } catch (JSONException e) {
                                        e.printStackTrace();
                                        hidePDialog();
                                    }

                                }
                                setListViewHeightBasedOnChildren(lv_listMyPosts);

                                // notifying list adapter about data changes
                                // so that it renders the list view with updated data
                                adapter.notifyDataSetChanged();
                            } catch (JSONException e) {
                                e.printStackTrace();
                            }
                        }
                    }, new Response.ErrorListener() {
                @Override
                public void onErrorResponse(VolleyError error) {
                    VolleyLog.e("Error: ", error.getMessage());
                    hidePDialog();
                }
            });

            // add the request object to the queue to be executed
            cmpe277project.nk.org.a277_socialmobileapp.volley.AppController.getInstance().addToRequestQueue(req);
        }

        public void onDestroy() {
            super.onDestroy();
            hidePDialog();
        }

        private void hidePDialog() {
            if (pDialog != null) {
                pDialog.dismiss();
                pDialog = null;
            }
        }

        public static void setListViewHeightBasedOnChildren(ListView listView) {
            ListAdapter listAdapter = listView.getAdapter();
            if (listAdapter == null) {
                // pre-condition
                return;
            }

            int totalHeight = 0;
            for (int i = 0; i < listAdapter.getCount(); i++) {
                View listItem = listAdapter.getView(i, null, listView);
                listItem.measure(0, 0);
                totalHeight += listItem.getMeasuredHeight();
            }
            totalHeight+=50;
            ViewGroup.LayoutParams params = listView.getLayoutParams();
            params.height = totalHeight + (listView.getDividerHeight() * (listAdapter.getCount() - 1));
            params.height+=40;
            listView.setLayoutParams(params);
            listView.requestLayout();
        }

        @Override
        public void onActivityResult(int requestCode, int resultCode, Intent data) {
            super.onActivityResult(requestCode, resultCode, data);
            if (requestCode == PICK_PHOTO_FOR_AVATAR && resultCode == Activity.RESULT_OK) {
                if (data == null) {
                    //Display an error
                    Toast.makeText(getActivity().getApplicationContext(), "Error occured in selecting image", Toast.LENGTH_LONG);
                    return;
                }

                Uri uri = data.getData();
//            File fileToUpload = new File("/storage/sdcard0/Pictures/Screenshots/photos.png");
                String path = PathUtils.getPath(getActivity().getApplicationContext(), uri);

                File fileToUpload = new File(path);

                try {
                    //String replyFromTask=new InsertImageToS3().execute().get();
                    InsertImageToS3 asyncTaskToPushImageToS3 = new InsertImageToS3(fileToUpload);
                    asyncTaskToPushImageToS3.file = fileToUpload;
                    newImageS3URL = asyncTaskToPushImageToS3.execute().get();
                    Log.d(TAG, "---------s3 url--------");
                    Log.d(TAG, newImageS3URL);

                    Toast.makeText(getActivity().getApplicationContext(), "New profile image selected", Toast.LENGTH_SHORT).show();

                    ivUserImage.setImageUrl(newImageS3URL, imageLoader);


                } catch (InterruptedException e) {
                    e.printStackTrace();
                    Toast.makeText(getActivity().getApplicationContext(), "Error in image selection", Toast.LENGTH_LONG).show();
                } catch (ExecutionException e) {
                    e.printStackTrace();
                    Toast.makeText(getActivity().getApplicationContext(), "Error in image selection", Toast.LENGTH_LONG).show();
                }

            }
        }


    }
}
