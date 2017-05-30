package cmpe277project.nk.org.a277_socialmobileapp.volley;

/**
 * Created by narasakumar on 5/19/17.
 */



import java.util.ArrayList;
import java.util.List;

import android.app.Activity;
import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.TextView;

import com.android.volley.toolbox.ImageLoader;
import com.android.volley.toolbox.NetworkImageView;

import cmpe277project.nk.org.a277_socialmobileapp.R;
import cmpe277project.nk.org.a277_socialmobileapp.posts.Post;

public class CustomListAdapter extends BaseAdapter {
    private Activity activity;
    private LayoutInflater inflater;
    private List<Post> postItems= new ArrayList<>();
    ImageLoader imageLoader = AppController.getInstance().getImageLoader();

    public CustomListAdapter(Activity activity, List<Post> postItems) {
        this.activity = activity;
        this.postItems = postItems;
    }

    @Override
    public int getCount() {
        return postItems.size();
    }

    @Override
    public Object getItem(int location) {
        return postItems.get(location);
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
            convertView = inflater.inflate(R.layout.list_row, null);

        if (imageLoader == null)
            imageLoader = AppController.getInstance().getImageLoader();

        // Fields
        //        userName, userPost, userImageURL, userPostImageURL

        NetworkImageView userImageURL = (NetworkImageView) convertView
                .findViewById(R.id.post_userImage);

        NetworkImageView userPostImageURL = (NetworkImageView) convertView
                .findViewById(R.id.post_image);

        TextView userName = (TextView) convertView.findViewById(R.id.post_username);
        TextView userPost = (TextView) convertView.findViewById(R.id.post_text);


        // getting post data for the row
        Post m = postItems.get(position);

        // set User Image
        userImageURL.setImageUrl(m.getUserImageURL(), imageLoader);

        // set User Posted (for Post) Image
        userPostImageURL.setImageUrl(m.getUserPostImageURL(), imageLoader);

        // set User Name
        userName.setText(m.getUserName());

        // set User post
        userPost.setText(m.getUserPost());

        // Hide the post image if not present
        if(!m.getIsUserPostImagePresent()) {
            userPostImageURL.setVisibility(View.GONE);
        }
        else {
            userPostImageURL.setVisibility(View.VISIBLE);
        }

        return convertView;
    }

}