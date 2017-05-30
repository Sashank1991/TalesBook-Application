package cmpe277project.nk.org.a277_socialmobileapp.posts;

/**
 * Created by narasakumar on 5/19/17.
 */

import java.util.ArrayList;

public class Post {
    private String userName, userPost, userImageURL, userPostImageURL;
    boolean isUserPostImagePresent;

    public Post() {
    }

    public Post(String userName, String userPost, String userImageURL, String userPostImageURL,boolean isUserPostImagePresent) {
        this.userName = userName;
        this.userPost = userPost;
        this.userImageURL = userImageURL;
        this.userPostImageURL = userPostImageURL;
        this.isUserPostImagePresent=isUserPostImagePresent;
    }

    public String getUserName() {
        return userName;
    }

    public void setUserName(String userName) {
        this.userName = userName;
    }

    public String getUserPost() {
        return userPost;
    }

    public void setUserPost(String userPost) {
        this.userPost = userPost;
    }

    public String getUserImageURL() {
        return userImageURL;
    }

    public void setUserImageURL(String userImageURL) {
        this.userImageURL = userImageURL;
    }

    public String getUserPostImageURL() {
        return userPostImageURL;
    }

    public void setUserPostImageURL(String userPostImageURL) {
        this.userPostImageURL = userPostImageURL;
    }

    public boolean getIsUserPostImagePresent() {
        return isUserPostImagePresent;
    }

    public void setIsUserPostImagePresent(boolean userPostImagePresent) {
        isUserPostImagePresent = userPostImagePresent;
    }
}