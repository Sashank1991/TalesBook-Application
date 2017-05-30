package cmpe277project.nk.org.a277_socialmobileapp.Messaging;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.ListView;

import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.VolleyLog;
import com.android.volley.toolbox.JsonObjectRequest;
import com.android.volley.toolbox.Volley;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import cmpe277project.nk.org.a277_socialmobileapp.R;
import cmpe277project.nk.org.a277_socialmobileapp.volley.AppController;

/**
 * Created by rahul on 5/22/2017.
 */

public class ConversationActivity extends Activity {
    private Button deleteButton;
    private String userName;
    private String recipient;

    public String getRecipient() {
        return recipient;
    }

    public void setRecipient(String recipient) {
        this.recipient = recipient;
    }

    public String getUserName() {
        return userName;
    }

    public void setUserName(String userName) {
        this.userName = userName;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.conversation);

        Bundle extras = getIntent().getExtras();
        final ListView listView = (ListView) findViewById(R.id.conversation_list);
        final String URL = "http://sample-env.qjp9mjnymz.us-west-2.elasticbeanstalk.com/getConversation";
        RequestQueue queue = Volley.newRequestQueue(this);
        Map<String, String> params = new HashMap<>();
        params.put("screenName",extras.getString("screenName"));
        params.put("recipient", extras.getString("recipient"));
        setUserName(extras.getString("screenName"));
        setRecipient(extras.getString("recipient"));

        JsonObjectRequest req = new JsonObjectRequest(URL, new JSONObject(params),
                new Response.Listener<JSONObject>() {
                    @Override
                    public void onResponse(JSONObject response) {
                        List<String> conversationList = new ArrayList<>();
                        try {
                            JSONArray objData = response.getJSONArray("data");
                            for(int i=0;i<objData.length();i++) {
                                Log.d("Data:",objData.getString(i));
                                conversationList.add(objData.getString(i));
                            }
                            ArrayAdapter adapter = new ArrayAdapter<>(listView.getContext(), android.R.layout.simple_list_item_1, conversationList);
                            listView.setAdapter(adapter);
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

        queue.add(req);

        deleteButton = (Button) findViewById(R.id.DeleteMessage);
        deleteButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                final String URL = "http://Sample-env.qjp9mjnymz.us-west-2.elasticbeanstalk.com/deleteConversation";
                Map<String, String> params = new HashMap<String, String>();
                params.put("screenName", getUserName());
                params.put("recipient", getRecipient());
                JsonObjectRequest req = new JsonObjectRequest(URL, new JSONObject(params),
                        new Response.Listener<JSONObject>() {
                            @Override
                            public void onResponse(JSONObject response) {
                                try {
                                    String objData = response.getString("statusCode");
                                    if(objData.equals("200")) {
                                        Intent intent = new Intent(ConversationActivity.this, MessagingActivity.class);
                                        intent.putExtra("screenName",getUserName());
                                        intent.putExtra("recipient",getRecipient());
                                        startActivity(intent);
                                    } else {

                                    }
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
                AppController.getInstance().addToRequestQueue(req);
            }
        });
    }
}
