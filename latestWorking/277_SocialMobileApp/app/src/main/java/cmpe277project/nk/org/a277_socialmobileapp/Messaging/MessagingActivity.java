package cmpe277project.nk.org.a277_socialmobileapp.Messaging;

import android.app.TabActivity;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.TabHost;
import android.widget.TextView;

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

public class MessagingActivity extends TabActivity implements TabHost.OnTabChangeListener {

    private static final String LIST1_TAB_TAG = "Compose Message";
    private static final String LIST2_TAB_TAG = "View Messages";

    // The two views in our tabbed example
    private TextView listView1;
    private ListView listView2;

    private TabHost tabHost;
    private Button sendButton;
    private Button refreshButton;
    private EditText textTo;
    private EditText subject;
    private EditText message;
    private List<String> chatUsers;
    private List<String> chatList;
    private List<String> conversationList;
    private String userName;
    private String recipient;

    public String getUserName() {
        return userName;
    }

    public void setUserName(String userName) {
        this.userName = userName;
    }

    public String getRecipient() {
        return recipient;
    }

    public void setRecipient(String recipient) {
        this.recipient = recipient;
    }

    final String URLChatUsers = "http://sample-env.qjp9mjnymz.us-west-2.elasticbeanstalk.com/getChatList";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_messaging);

        textTo = (EditText) findViewById(R.id.editTextEmailTo);
        subject = (EditText) findViewById(R.id.editTextEmailSubject);
        message = (EditText) findViewById(R.id.editTextEmailContent);


        Bundle extras = getIntent().getExtras();
        setUserName(extras.getString("screenName"));
        setRecipient(extras.getString("recipient"));
        textTo.setText(getRecipient());
        tabHost = getTabHost();
        tabHost.setOnTabChangedListener(this);

        // add views to tab host
        tabHost.addTab(tabHost.newTabSpec(LIST1_TAB_TAG).setIndicator(LIST1_TAB_TAG).setContent(new TabHost.TabContentFactory() {
            public View createTabContent(String arg0) {
                findViewById(R.id.list2).setVisibility(LinearLayout.GONE);
                return findViewById(R.id.Compose_Message);
            }
        }));

        listView2 = (ListView) findViewById(R.id.list2);
        chatUsers = getChatUsers(userName);
        listView2.setAdapter(new ArrayAdapter(this, android.R.layout.simple_list_item_1, chatUsers));

        tabHost.addTab(tabHost.newTabSpec(LIST2_TAB_TAG).setIndicator(LIST2_TAB_TAG).setContent(new TabHost.TabContentFactory() {
            public View createTabContent(String arg0) {
                return listView2;
            }
        }));

        listView2.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            public void onItemClick(AdapterView parent, View view, int position, long id) {
                String item = (String) listView2.getAdapter().getItem(position);
                if(item != null) {
                    Intent intent = new Intent(MessagingActivity.this, ConversationActivity.class);
                    intent.putExtra("screenName",userName);
                    intent.putExtra("recipient",item);
                    startActivity(intent);
                }
            }
        });

        sendButton = (Button) findViewById(R.id.SendMessage);
        sendButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                sendMessage(textTo.getText().toString(), subject.getText().toString(), message.getText().toString());
            }
        });

        refreshButton = (Button) findViewById(R.id.refreshButton);
        refreshButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                final ListView listView = (ListView) findViewById(R.id.list2);
                findViewById(R.id.refreshButton).setVisibility(LinearLayout.VISIBLE);
                Map<String, String> params = new HashMap<>();
                chatList = new ArrayList<>();
                params.put("screenName",userName);

                JsonObjectRequest req = new JsonObjectRequest(URLChatUsers, new JSONObject(params),
                        new Response.Listener<JSONObject>() {
                            @Override
                            public void onResponse(JSONObject response) {
                                try {
                                    JSONArray objData = response.getJSONArray("data");
                                    for(int i=0;i<objData.length();i++) {
                                        chatList.add(objData.getString(i));
                                    }
                                    ArrayAdapter adapter = new ArrayAdapter<String>(listView.getContext(), android.R.layout.simple_list_item_1, chatList);
                                    listView.setAdapter(adapter);
                                } catch (JSONException e) {
                                    e.printStackTrace();
                                }
                            }
                        }, new Response.ErrorListener() {
                    @Override
                    public void onErrorResponse(VolleyError error) {
                        VolleyLog.e("Error: ", error.getStackTrace());
                    }
                });
                AppController.getInstance().addToRequestQueue(req);
            }
        });
    }

    @Override
    public void onTabChanged(String tabId) {
        final ListView listView = (ListView) findViewById(R.id.list2);
        if (tabId.equals(LIST2_TAB_TAG)) {
            findViewById(R.id.refreshButton).setVisibility(LinearLayout.VISIBLE);
            Map<String, String> params = new HashMap<String, String>();
            chatList = new ArrayList<>();
            params.put("screenName",userName);
            RequestQueue queue = Volley.newRequestQueue(this);

            JsonObjectRequest req = new JsonObjectRequest(URLChatUsers, new JSONObject(params),
                    new Response.Listener<JSONObject>() {
                        @Override
                        public void onResponse(JSONObject response) {
                            try {
                                JSONArray objData = response.getJSONArray("data");
                                for(int i=0;i<objData.length();i++) {
                                    chatList.add(objData.getString(i));
                                }
                                ArrayAdapter adapter = new ArrayAdapter<String>(listView.getContext(), android.R.layout.simple_list_item_1, chatList);
                                listView.setAdapter(adapter);
                            } catch (JSONException e) {
                                e.printStackTrace();
                            }
                        }
                    }, new Response.ErrorListener() {
                @Override
                public void onErrorResponse(VolleyError error) {
                    VolleyLog.e("Error: ", error.getStackTrace());
                }
            });
            queue.add(req);
        } else if (tabId.equals(LIST1_TAB_TAG)) {
            findViewById(R.id.refreshButton).setVisibility(LinearLayout.GONE);
        }
    }

    public void sendMessage(String recipient, String subject, String emailContent) {
        final String URL = "http://Sample-env.qjp9mjnymz.us-west-2.elasticbeanstalk.com/sendMessage";
        Map<String, String> params = new HashMap<>();
        params.put("screenName", userName);
        params.put("recipient", recipient);
        params.put("subject", subject);
        params.put("email", emailContent);
        JsonObjectRequest req = new JsonObjectRequest(URL, new JSONObject(params),
                new Response.Listener<JSONObject>() {
                    @Override
                    public void onResponse(JSONObject response) {
                        try {
                            String objData = response.getString("statusCode");
                            if(objData.equals("200")) {

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

    public List<String> getChatUsers(String screenName) {
        final String URL = "http://sample-env.qjp9mjnymz.us-west-2.elasticbeanstalk.com/getChatList";
        Map<String, String> params = new HashMap<>();
        chatList = new ArrayList<>();
        params.put("screenName",screenName);
        JsonObjectRequest req = new JsonObjectRequest(URL, new JSONObject(params),
                new Response.Listener<JSONObject>() {
                    @Override
                    public void onResponse(JSONObject response) {
                        try {
                            JSONArray objData = response.getJSONArray("data");
                            for(int i=0;i<objData.length();i++) {
                                chatList.add(objData.getString(i));
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
        return chatList;
    }
}
