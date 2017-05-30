package cmpe277project.nk.org.a277_socialmobileapp.posts;
/*
import android.app.ProgressDialog;
import android.content.Context;
import android.content.res.Resources;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v4.view.ViewPager;
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
import android.widget.ArrayAdapter;
import android.widget.ListAdapter;
import android.widget.ListView;
import android.widget.Spinner;
import android.widget.TextView;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.VolleyLog;
import com.android.volley.toolbox.ImageLoader;
import com.android.volley.toolbox.JsonObjectRequest;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import cmpe277project.nk.org.a277_socialmobileapp.R;
import cmpe277project.nk.org.a277_socialmobileapp.volley.AppController;
import cmpe277project.nk.org.a277_socialmobileapp.volley.CustomListAdapter;*/

import android.app.ProgressDialog;
import android.content.Context;
import android.content.Intent;
import android.content.res.Resources;
import android.content.res.Resources.Theme;
import android.os.Bundle;
import android.support.design.widget.FloatingActionButton;
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

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

import cmpe277project.nk.org.a277_socialmobileapp.Messaging.MessagingActivity;
import cmpe277project.nk.org.a277_socialmobileapp.R;
import cmpe277project.nk.org.a277_socialmobileapp.login.LoginActivity;
import cmpe277project.nk.org.a277_socialmobileapp.login.SwipeActivity;
import cmpe277project.nk.org.a277_socialmobileapp.posts.MyFriendsList;
import cmpe277project.nk.org.a277_socialmobileapp.posts.Post;
import cmpe277project.nk.org.a277_socialmobileapp.volley.*;
import cmpe277project.nk.org.a277_socialmobileapp.volley.AppController;


public class NonUserProfile extends AppCompatActivity {

    //    private ViewPager mViewPager;
    public static String username, profilePic, recipient;
    static JSONObject objMyPosts;
    static ProgressDialog pDialog;
    static CustomListAdapter adapter;
    private static List<Post> postList = new ArrayList<Post>();
    private static final String TAG = NonUserProfile.class.getSimpleName();

    static TextView emailText = null;
    static TextView aboutMeText = null;
    static TextView professionText = null;
    static TextView interestsText = null;
    static TextView locationText = null;
    static TextView usernameLabel = null;
    static Button buttonMessage = null;
    static View currentView = null;

    static NetworkImageView ivUserImage = null;


    static final ImageLoader imageLoader = AppController.getInstance().getImageLoader();

    //static ImageLoader imageLoader = AppController.getInstance().getImageLoader();
    //EditText emailText = (EditText) findViewById(R.id.editTextEmail);
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_nonuserprofile);

        //EditText emailText = (EditText) findViewById(R.id.editTextEmail);
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar1);
        Bundle bundle = getIntent().getExtras();
        // if (savedInstanceState == null) {
        Bundle extras = getIntent().getExtras();
        if (extras == null) {
            username = null;
            recipient = null;
        } else {
            username = extras.getString("screenName");
            recipient = extras.getString("recipient");
        }
        //  } else {
        //     username= (String) savedInstanceState.getSerializable("screenName");
        //     recipient= (String) savedInstanceState.getSerializable("recipient");
        // }
//        Log.d("body",extendedP.toString());

        //final String extendedP=bundle.getString("data");
        // Setup spinner
        Spinner spinner = (Spinner) findViewById(R.id.spinner1);
        spinner.setAdapter(new NonUserProfile.MyAdapter(
                toolbar.getContext(),
                new String[]{
                        "About",
                        "Posts"
                }));

        spinner.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                // When the given dropdown item is selected, show its contents in the
                // container view.
                getSupportFragmentManager().beginTransaction()
                        .replace(R.id.container1, NonUserProfile.PlaceholderFragment.newInstance(position + 1))
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

        //noinspection SimplifiableIfStatement
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
        public Resources.Theme getDropDownViewTheme() {
            return mDropDownHelper.getDropDownViewTheme();
        }

        @Override
        public void setDropDownViewTheme(Resources.Theme theme) {
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
        public static NonUserProfile.PlaceholderFragment newInstance(int sectionNumber) {
            NonUserProfile.PlaceholderFragment fragment = new NonUserProfile.PlaceholderFragment();
            Bundle args = new Bundle();
            args.putInt(ARG_SECTION_NUMBER, sectionNumber);
            fragment.setArguments(args);
            return fragment;
        }

        @Override
        public View onCreateView(LayoutInflater inflater, ViewGroup container,
                                 Bundle savedInstanceState) {

            //do post again

            View rootView = inflater.inflate(R.layout.fragment_nonuserposts, container, false);
            //View rootView = inflater.inflate(R.layout.fragment_swipe, container, false);
            //TextView textView = (TextView) rootView.findViewById(R.id.section_label);
            //textView.setText(getString(R.string.section_format, getArguments().getInt(ARG_SECTION_NUMBER)));
            if (getArguments().getInt(ARG_SECTION_NUMBER) == 1) {
                rootView = inflater.inflate(R.layout.fragment_nonuserprofile_about, container, false);

                currentView = rootView;
                emailText = (TextView) rootView.findViewById(R.id.editTextEmail1);
                aboutMeText = (TextView) rootView.findViewById(R.id.editTextAboutMe1);
                professionText = (TextView) rootView.findViewById(R.id.editTextProfession1);
                interestsText = (TextView) rootView.findViewById(R.id.editTextInterests1);
                locationText = (TextView) rootView.findViewById(R.id.editTextLocation1);
                usernameLabel = (TextView) rootView.findViewById(R.id.textView_UserName1);
                ivUserImage = (NetworkImageView) rootView.findViewById(R.id.imageView_UserImage1);
                buttonMessage = (Button) rootView.findViewById(R.id.buttonMessage);
//                imageLoader = AppController.getInstance().getImageLoader();

                // Get all my posts


                buttonMessage.setOnClickListener(new View.OnClickListener() {

//                    Log.d("NonUserProfile","NonUserProfile ==========screenName="+username+", recipient="+recipient);

                    @Override
                    public void onClick(View v) {
                        Intent i = new Intent(currentView.getContext(), MessagingActivity.class);
                        i.putExtra("screenName", username);
//                        i.putExtra("username",username);
                        i.putExtra("recipient", recipient);

                        Log.d("NonUserProfile", "NonUserProfile ==========screenName=" + username + ", recipient=" + recipient);

                        startActivity(i);
                    }
                });

                final String URL = "http://Sample-env.qjp9mjnymz.us-west-2.elasticbeanstalk.com/getProfile";
                // Post params to be sent to the server
                HashMap<String, String> params = new HashMap<String, String>();
                params.put("screenName", recipient);


                JsonObjectRequest req = new JsonObjectRequest(URL, new JSONObject(params),
                        new Response.Listener<JSONObject>() {
                            @Override
                            public void onResponse(JSONObject response) {
                                Log.d(TAG, response.toString());
                                hidePDialog();

                                try {

                                    JSONObject jsonData = response.getJSONObject("data");
                                    JSONObject jsonextendedProfile = jsonData.getJSONObject("extendedProfile");
                                    emailText.setText(jsonextendedProfile.getString("email"));
                                    aboutMeText.setText(jsonextendedProfile.getString("aboutMe"));
                                    interestsText.setText(jsonextendedProfile.getString("interests"));
                                    locationText.setText(jsonextendedProfile.getString("location"));
                                    professionText.setText(jsonextendedProfile.getString("profession"));
                                    usernameLabel.setText(recipient);
                                    ivUserImage.setImageUrl(jsonextendedProfile.getString("profilePic"), imageLoader);

                                } catch (JSONException e) {
                                    e.printStackTrace();
                                }
                            }
                        }, new Response.ErrorListener() {
                    @Override
                    public void onErrorResponse(VolleyError error) {
                        VolleyLog.e("Error: ", error.getMessage());

                    }
                });

                // add the request object to the queue to be executed
                cmpe277project.nk.org.a277_socialmobileapp.volley.AppController.getInstance().addToRequestQueue(req);


                return rootView;
            }

            // My Posts
            else if (getArguments().getInt(ARG_SECTION_NUMBER) == 2) {
                rootView = inflater.inflate(R.layout.fragment_nonuserposts, container, false);

                getMyPosts(rootView);


                //open user setting page to update his profile
                return rootView;
            }

            return rootView;
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


            // Get all my posts
            final String URL = "http://Sample-env.qjp9mjnymz.us-west-2.elasticbeanstalk.com/getProfile";
            // Post params to be sent to the server
            HashMap<String, String> params = new HashMap<String, String>();
            params.put("screenName", recipient);


            JsonObjectRequest req = new JsonObjectRequest(URL, new JSONObject(params),
                    new Response.Listener<JSONObject>() {
                        @Override
                        public void onResponse(JSONObject response) {
                            Log.d(TAG, response.toString());
                            hidePDialog();

                            try {
                                profilePic = response.getJSONObject("data").getJSONObject("extendedProfile").getString("profilePic");


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


                                        post.setUserName(recipient);
//
//                                    Log.v(TAG,"============================");
//                                    Log.v(TAG,obj_temp_post.getString("assImage"));
//                                    Log.v(TAG,"============================");
//                                    Log.v(TAG,"");
                                        if (!profilePic.equals("")) {
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

            ViewGroup.LayoutParams params = listView.getLayoutParams();
            params.height = totalHeight + (listView.getDividerHeight() * (listAdapter.getCount() - 1));
            listView.setLayoutParams(params);
            listView.requestLayout();
        }

    }
}
