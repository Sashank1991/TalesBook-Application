package cmpe277project.nk.org.a277_socialmobileapp.volley;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageButton;
import android.widget.ListAdapter;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.BaseAdapter;
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

import cmpe277project.nk.org.a277_socialmobileapp.Home;
import cmpe277project.nk.org.a277_socialmobileapp.R;
import cmpe277project.nk.org.a277_socialmobileapp.login.SwipeActivity;
import cmpe277project.nk.org.a277_socialmobileapp.posts.MyFriendsList;
import cmpe277project.nk.org.a277_socialmobileapp.posts.NonUserProfile;
import cmpe277project.nk.org.a277_socialmobileapp.posts.Post;

/**
 * Created by sasha on 5/21/2017.
 */

public class DisplayFriends extends BaseAdapter {
    private Activity activity;
    private LayoutInflater inflater;
    private List<MyFriendsList> friendItems = new ArrayList<>();
    ImageLoader imageLoader = AppController.getInstance().getImageLoader();
    public String screenName;
    public View view;
    static JSONObject objMyPosts;
    static DisplayFriends adapter_friends;
    static List<MyFriendsList> friendsLists;
    static ListView lv_listMyFriends;

//    public DisplayFriends(Activity activity, List<MyFriendsList> friendItems) {
//        this.activity = activity;
//        this.friendItems = friendItems;
//    }

    public DisplayFriends(Activity activity, List<MyFriendsList> friendItems,String screenName) {
        this.activity = activity;
        this.friendItems = friendItems;
        this.screenName=screenName;
    }

    @Override
    public int getCount() {
        return friendItems.size();
    }

    @Override
    public Object getItem(int location) {
        return friendItems.get(location);
    }

    @Override
    public long getItemId(int position) {
        return position;
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {

        if (inflater == null)
            inflater = (LayoutInflater) activity
                    .getSystemService(Context.LAYOUT_INFLATER_SERVICE);
        if (convertView == null)
            convertView = inflater.inflate(R.layout.displayfriends, null);

        if (imageLoader == null)
            imageLoader = AppController.getInstance().getImageLoader();

        //Image for the profile pic
        NetworkImageView userImageURL = (NetworkImageView) convertView
                .findViewById(R.id.friendProfile);

        //view profile or accept
        ImageButton viewProfile = (ImageButton) convertView.findViewById(R.id.viewProfile);
        final ImageButton acceptButton = (ImageButton) convertView.findViewById(R.id.acceptRequest);


        //text for the screen name and status
        TextView status = (TextView) convertView.findViewById(R.id.status);
        TextView friendScreenName = (TextView) convertView.findViewById(R.id.friendScreenName);


        // getting post data for the row
        MyFriendsList m = friendItems.get(position);

        // set User Image
        userImageURL.setImageUrl(m.getUserPostImageURL(), imageLoader);


        final String usrProfileScreenName = m.getUserProfileScreenName();

        final String screenName = m.getScreenName();

        status.setText("Friendship Status: " + m.getStatus());
        friendScreenName.setText(screenName);


        viewProfile.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent i = new Intent(activity.getApplicationContext(), NonUserProfile.class);
                i.putExtra("screenName", usrProfileScreenName);
                i.putExtra("recipient", screenName);

                activity.startActivity(i);
            }
        });


        acceptButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                // Get all my posts
                final String URL = "http://Sample-env.qjp9mjnymz.us-west-2.elasticbeanstalk.com/acceptFriend";

                HashMap<String, String> params = new HashMap<String, String>();
                params.put("UserScreenName", usrProfileScreenName);
                params.put("FriendScreenName", screenName);
                JsonObjectRequest req = new JsonObjectRequest(URL, new JSONObject(params),
                        new Response.Listener<JSONObject>() {
                            @Override
                            public void onResponse(JSONObject response) {
                                try {
                                    acceptButton.setVisibility(View.GONE);
                                    Toast.makeText(activity.getApplication(), "Friend request accepted", Toast.LENGTH_SHORT).show();
                                    getMyFriends();
                                } catch (
                                        Exception e) {
                                    e.printStackTrace();
                                    Toast.makeText(activity.getApplication(), "Error Occured", Toast.LENGTH_SHORT).show();
                                }
                            }
                        }, new Response.ErrorListener() {
                    @Override
                    public void onErrorResponse(VolleyError error) {
                        VolleyLog.e("Error: ", error.getMessage());

                        Toast.makeText(activity.getApplication(), "Error Occured", Toast.LENGTH_SHORT).show();
                    }
                });

                // add the request object to the queue to be executed
                cmpe277project.nk.org.a277_socialmobileapp.volley.AppController.getInstance().addToRequestQueue(req);
            }
        });


        if (m.getStatus().equals("received")) {
            acceptButton.setVisibility(View.VISIBLE);
            viewProfile.setVisibility(View.VISIBLE);
        } else if (m.getStatus().equals("friend")) {
            viewProfile.setVisibility(View.VISIBLE);
            acceptButton.setVisibility(View.GONE);
        } else {
            acceptButton.setVisibility(View.GONE);
            viewProfile.setVisibility(View.GONE);
        }


        return convertView;
    }

    public void getMyFriends() {

       /* pDialog = new ProgressDialog(getActivity());
        // Showing progress dialog before making http request
        pDialog.setMessage("Loading friends. Please wait...");
        pDialog.show();*/


        ImageLoader imageLoader = AppController.getInstance().getImageLoader();
        lv_listMyFriends = (ListView) view.findViewById(R.id.listFriends);

        friendsLists = new ArrayList<MyFriendsList>();
        adapter_friends = new DisplayFriends(activity, friendsLists,screenName);

        lv_listMyFriends.setAdapter(adapter_friends);


        final String URL = "http://Sample-env.qjp9mjnymz.us-west-2.elasticbeanstalk.com/getProfile";
        // Post params to be sent to the server
        HashMap<String, String> params = new HashMap<String, String>();
        params.put("screenName", screenName);

        JsonObjectRequest req = new JsonObjectRequest(URL, new JSONObject(params),
                new Response.Listener<JSONObject>() {
                    @Override
                    public void onResponse(JSONObject response) {
                        Log.d("DisplayFollowers", response.toString());
//                        hidePDialog();

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
//                                    hidePDialog();
                                }
                            }
                            setListViewHeightBasedOnChildren(lv_listMyFriends);
                        }
                        catch (JSONException e) {
                            e.printStackTrace();
//                            hidePDialog();
                        }
                        adapter_friends.notifyDataSetChanged();

                    }
                }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {
                VolleyLog.e("Error: ", error.getMessage());
//                hidePDialog();
            }
        });

        // add the request object to the queue to be executed
        AppController.getInstance().addToRequestQueue(req);




    }

//    private void hidePDialog() {
//        if (pDialog != null) {
//            pDialog.dismiss();
//            pDialog = null;
//        }
//    }

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
        listView.setLayoutParams(params);
        listView.requestLayout();
    }

    public void setView(View view) {
        this.view=view;
    }

}