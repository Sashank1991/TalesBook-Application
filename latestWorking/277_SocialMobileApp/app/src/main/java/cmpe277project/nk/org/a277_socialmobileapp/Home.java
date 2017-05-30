package cmpe277project.nk.org.a277_socialmobileapp;

import cmpe277project.nk.org.a277_socialmobileapp.login.SwipeActivity;
import cmpe277project.nk.org.a277_socialmobileapp.posts.Post;
import cmpe277project.nk.org.a277_socialmobileapp.volley.AppController;
import cmpe277project.nk.org.a277_socialmobileapp.volley.CustomListAdapter;
import cmpe277project.nk.org.a277_socialmobileapp.get_path_from_gallery.*;

import android.Manifest;
import android.app.Activity;
import android.app.ProgressDialog;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.database.Cursor;
import android.database.DatabaseUtils;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.Build;
import android.os.Bundle;
import android.provider.MediaStore;
import android.support.v4.app.ActivityCompat;
import android.support.v4.content.ContextCompat;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.View;
import android.view.ViewGroup;
import android.view.Window;
import android.view.WindowManager;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.ListAdapter;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.URL;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.concurrent.ExecutionException;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import android.util.Log;
import android.view.Menu;

import com.amazonaws.auth.BasicAWSCredentials;
import com.amazonaws.services.s3.AmazonS3Client;
import com.amazonaws.services.s3.internal.Constants;
import com.amazonaws.services.s3.model.CannedAccessControlList;
import com.amazonaws.services.s3.model.GetBucketAccelerateConfigurationRequest;
import com.amazonaws.services.s3.model.PutObjectRequest;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.VolleyLog;
import com.android.volley.toolbox.JsonObjectRequest;
import com.android.volley.toolbox.NetworkImageView;
//import android.support.v7.app.ActionBarActivity;
//import android.support.v7.app.AppCompatActivity;
//import android.app.Activity;
import com.android.volley.toolbox.ImageLoader;

import android.os.Bundle;
//import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.View;

import com.amazonaws.auth.CognitoCachingCredentialsProvider;
import com.amazonaws.mobileconnectors.s3.transferutility.TransferListener;
import com.amazonaws.mobileconnectors.s3.transferutility.TransferObserver;
import com.amazonaws.mobileconnectors.s3.transferutility.TransferState;
import com.amazonaws.mobileconnectors.s3.transferutility.TransferUtility;
import com.amazonaws.regions.Region;
import com.amazonaws.regions.Regions;
import com.amazonaws.services.s3.AmazonS3;
import com.amazonaws.services.s3.AmazonS3Client;

import android.os.Bundle;
//import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.View;

import com.amazonaws.auth.CognitoCachingCredentialsProvider;
import com.amazonaws.mobileconnectors.s3.transferutility.TransferListener;
import com.amazonaws.mobileconnectors.s3.transferutility.TransferObserver;
import com.amazonaws.mobileconnectors.s3.transferutility.TransferState;
import com.amazonaws.mobileconnectors.s3.transferutility.TransferUtility;
import com.amazonaws.regions.Region;
import com.amazonaws.regions.Regions;
import com.amazonaws.services.s3.AmazonS3;
import com.amazonaws.services.s3.AmazonS3Client;

//

public class Home extends AppCompatActivity {
    private static final int PERMISSION_REQUEST_CODE = 1;
    TextView tvUserName;
    EditText etUserPost;
    Button bPost, bImageUpload;
    NetworkImageView ivUserImage;
    ImageLoader imageLoader = AppController.getInstance().getImageLoader();
    // Log tag
    private static final String TAG = Home.class.getSimpleName();
    private static String username, password;
    private JSONObject dataFromDatabase;
    private static String s3PostURL = "";


    private ProgressDialog pDialog;
    private List<Post> postList = new ArrayList<Post>();
    private ListView listView;
    private CustomListAdapter adapter;
    private static final int PICK_PHOTO_FOR_AVATAR = 0;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        int permissionCheck = ContextCompat.checkSelfPermission(this, Manifest.permission.WRITE_EXTERNAL_STORAGE);

        if (permissionCheck != PackageManager.PERMISSION_GRANTED) {
            ActivityCompat.requestPermissions(this, new String[]{Manifest.permission.WRITE_EXTERNAL_STORAGE}, PERMISSION_REQUEST_CODE);
        } else {
            //readDataExternal();
        }

        // Hide the Action Bar
        // Toolbar myToolbar = (Toolbar) findViewById(R.id.toolbar);
        //setSupportActionBar(myToolbar);

        /*requestWindowFeature(Window.FEATURE_NO_TITLE);
        getWindow().setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN,
                            WindowManager.LayoutParams.FLAG_FULLSCREEN);
        requestWindowFeature(Window.FEATURE_ACTION_BAR);
*/
        setContentView(R.layout.activity_home);


        // Read the USERNAME set by login activity

        //   if (savedInstanceState == null) {
        Bundle extras = getIntent().getExtras();
        if (extras == null) {
            username = null;
        } else {
            username = extras.getString("username");
            password = extras.getString("password");

        }
        //  } else {
        //    username = (String) savedInstanceState.getSerializable("username");
        //  password = (String) savedInstanceState.getSerializable("password");
        //}

        initiateUIViews();

        setListeners();

        getUserProfileOnLogin();

        getAllPosts();


    }

    @Override
    public void onRequestPermissionsResult(int requestCode, String permissions[], int[] grantResults) {
        switch (requestCode) {
            case PERMISSION_REQUEST_CODE:
                if ((grantResults.length > 0) && (grantResults[0] == PackageManager.PERMISSION_GRANTED)) {
                   // readDataExternal();
                }
                break;

            default:
                break;
        }
    }

    private void initiateUIViews() {
        // Inititatalize the UI views
        tvUserName = (TextView) findViewById(R.id.textView_UserName);
        etUserPost = (EditText) findViewById(R.id.editText_UserPost);
        bPost = (Button) findViewById(R.id.button_Post);
        bImageUpload = (Button) findViewById(R.id.button_UploadImage);
        ivUserImage = (NetworkImageView) findViewById(R.id.imageView_UserImage);
        listView = (ListView) findViewById(R.id.list);
    }

    private void getUserProfileOnLogin() {
        if (imageLoader == null)
            imageLoader = AppController.getInstance().getImageLoader();


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
                            JSONObject objData = response.getJSONObject("data");

                            dataFromDatabase = objData;

//                            data.screenName
                            tvUserName.setText(objData.getString("screenName"));

//                            data.extendedProfile.profilePic

                            if(objData.getJSONObject("extendedProfile").getString("profilePic").length() > 0) {
                                ivUserImage.setImageUrl(objData.getJSONObject("extendedProfile").getString("profilePic"), imageLoader);
                            }
                            else {
                                ivUserImage.setDefaultImageResId(R.drawable.userimageweb);
//                                ivUserImage.setErrorImageResId(R.drawable.error);
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

    private void getAllPosts() {

        adapter = new CustomListAdapter(this, postList);
        listView.setAdapter(adapter);

        pDialog = new ProgressDialog(this);
        // Showing progress dialog before making http request
        pDialog.setMessage("Loading posts. Please wait...");
        pDialog.show();

        // changing action bar color

//        getActionBar().setBackgroundDrawable(
//                new ColorDrawable(Color.parseColor("#1b1b1b")));


        // Creating volley request obj

        final String URL = "http://Sample-env.qjp9mjnymz.us-west-2.elasticbeanstalk.com/getAllPosts";
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
                            JSONArray objData = response.getJSONArray("data");

                            for (int i = 0; i < objData.length() && i < 100; i++) {
                                try {

                                    JSONObject obj = objData.getJSONObject(i);
                                    Post post = new Post();

                                    if (!obj.getString("assImage").equals("")) {
                                        post.setUserPostImageURL(obj.getString("assImage"));
                                        post.setIsUserPostImagePresent(true);
                                    } else {
                                        post.setIsUserPostImagePresent(false);
                                    }
                                    post.setUserPost(obj.getString("content"));

                                    JSONObject profile = obj.getJSONObject("profile");

                                    post.setUserName(profile.getString("screenName"));
//
//                                    Log.v(TAG,"============================");
//                                    Log.v(TAG,obj.getString("assImage"));
//                                    Log.v(TAG,"============================");
//                                    Log.v(TAG,"");
                                    if (!profile.getJSONObject("extendedProfile").getString("profilePic").equals("")) {
                                        post.setUserImageURL(profile.getJSONObject("extendedProfile").getString("profilePic"));
//                                        post.setIsUserPostImagePresent(true);
                                    }
//                                    else {
//                                        // Hide it
//                                        post.setIsUserPostImagePresent(false);
//                                    }

                                    // adding post to posts array
                                    postList.add(post);

                                    setListViewHeightBasedOnChildren(listView);

                                } catch (JSONException e) {
                                    e.printStackTrace();
                                }

                            }


                        } catch (JSONException e) {
                            e.printStackTrace();
                        }


                        // notifying list adapter about data changes
                        // so that it renders the list view with updated data
                        adapter.notifyDataSetChanged();
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


    @Override
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

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.


//        getMenuInflater().inflate(R.menu.main, menu);


        return true;
    }

    // On click methods
    private void setListeners() {

        ivUserImage.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent i = new Intent(getApplicationContext(), SwipeActivity.class);
                i.putExtra("data", dataFromDatabase.toString());
                i.putExtra("password", password);


                startActivity(i);
            }
        });


        bImageUpload.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
//                Toast.makeText(getApplicationContext(),"Image Upload clicked",Toast.LENGTH_SHORT).show();

                /*Image PICK_IMAGE=null;
                Intent intent = new Intent();
                intent.setType("image*//*");
                intent.setAction(Intent.ACTION_GET_CONTENT);
                startActivityForResult(Intent.createChooser(intent, "Select Picture"), PICK_IMAGE);*/
/*
                Intent intent = new Intent();
// Show only images, no videos or anything else
                intent.setType("image*/
/*");
                intent.setAction(Intent.ACTION_GET_CONTENT);
// Always show the chooser (if there are multiple options available)
                startActivityForResult(Intent.createChooser(intent, "Select Picture"), 1);
*/



                Intent intent = new Intent(Intent.ACTION_GET_CONTENT);
                intent.setType("image/*");
                startActivityForResult(intent, PICK_PHOTO_FOR_AVATAR);

            }
        });

        bPost.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
//                Toast.makeText(getApplicationContext(),"Post button clicked",Toast.LENGTH_SHORT).show();
                if (!etUserPost.getText().toString().trim().equals("") || !s3PostURL.equals("")) {
                    sendPostToServer();
                } else {
                    Toast.makeText(getApplicationContext(), "Enter text or upload an image", Toast.LENGTH_SHORT).show();
                }
            }
        });


    }

    private void sendPostToServer() {
        final String URL = "http://Sample-env.qjp9mjnymz.us-west-2.elasticbeanstalk.com/postContent";

        // Post params to be sent to the server
        HashMap<String, String> params = new HashMap<String, String>();
        params.put("screenName", username);
        params.put("content", etUserPost.getText().toString());
        params.put("assImage", s3PostURL);
        s3PostURL = "";

        JsonObjectRequest req = new JsonObjectRequest(URL, new JSONObject(params),
                new Response.Listener<JSONObject>() {
                    @Override
                    public void onResponse(JSONObject response) {
                        Log.d(TAG, response.toString());
                        hidePDialog();

                        try {
                            String code = response.getString("code");
                            String message = response.getString("message");
                            if (code.equals("200")) {

                                Toast.makeText(getApplicationContext(), "Posted Successfully to your timeline", Toast.LENGTH_LONG).show();
                                etUserPost.setText("");
                                postList.clear();
                                getAllPosts();
                            } else {
                                Toast.makeText(getApplicationContext(), "Post failed with message: " + message, Toast.LENGTH_LONG).show();
                            }

                        } catch (JSONException e) {
                            e.printStackTrace();
                            Toast.makeText(getApplicationContext(), "some error occured", Toast.LENGTH_LONG).show();
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

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (requestCode == PICK_PHOTO_FOR_AVATAR && resultCode == Activity.RESULT_OK) {
            if (data == null) {
                //Display an error
                return;
            }

            Uri uri = data.getData();
//            File fileToUpload = new File("/storage/sdcard0/Pictures/Screenshots/photos.png");
            String path = PathUtils.getPath(getApplicationContext(), uri);

            File fileToUpload = new File(path);

            try {
                //String replyFromTask=new InsertImageToS3().execute().get();
                InsertImageToS3 asyncTaskToPushImageToS3 = new InsertImageToS3(fileToUpload);
                asyncTaskToPushImageToS3.file = fileToUpload;
                s3PostURL = asyncTaskToPushImageToS3.execute().get();
                Log.v(TAG, "---------s3 url--------");
                Log.v(TAG, s3PostURL);

                Toast.makeText(getApplicationContext(), "Image upload successful", Toast.LENGTH_SHORT).show();

            } catch (InterruptedException e) {
                e.printStackTrace();
                Toast.makeText(getApplicationContext(), "Image upload failed", Toast.LENGTH_LONG).show();
            } catch (ExecutionException e) {
                e.printStackTrace();
                Toast.makeText(getApplicationContext(), "Image upload failed", Toast.LENGTH_LONG).show();
            }

        }
    }


      /*private class InsertImageToS3 extends AsyncTask<Void,Void,String> {

        String s3URL=new String();
        File file;

        public InsertImageToS3(File file) {
            super();
            // do stuff
            this.file=file;

        }

        @Override
        protected String doInBackground(Void... params) {

            final String MY_ACCESS_KEY_ID="AKIAI5ZVW6YBMCMHVT6A", MY_SECRET_KEY="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";
            final String PICTURE_NAME= new SimpleDateFormat("yyyy-MM-dd-HH-mm-ss-SSS").format(new Date());

            AmazonS3Client s3Client = new AmazonS3Client( new BasicAWSCredentials( MY_ACCESS_KEY_ID, MY_SECRET_KEY ) );
            Region usWest2 = Region.getRegion(Regions.US_WEST_1);
            s3Client.setRegion(usWest2);


            PutObjectRequest por = new PutObjectRequest( "vrbtestcmpe", PICTURE_NAME, file);
            por.setCannedAcl(CannedAccessControlList.PublicRead);
            s3Client.putObject( por );

            Log.d(TAG,"============URL FROM S3============");
            Log.d(TAG,s3Client.getResourceUrl("vrbtestcmpe", PICTURE_NAME));

            s3URL=s3Client.getResourceUrl("vrbtestcmpe", PICTURE_NAME);
            return s3URL;
        }

        @Override
        protected void onPostExecute (String result) {

            super.onPostExecute(result);

        }
    }*/

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
        params.height+=80;
        listView.setLayoutParams(params);
        listView.requestLayout();
    }

}
