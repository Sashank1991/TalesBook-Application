package cmpe277project.nk.org.a277_socialmobileapp.posts;

/**
 * Created by sasha on 5/21/2017.
 */

public class MyFriendsList {
    private String screenName, status, userId, userPostImageURL,userProfileScreenName,email;

    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        this.email = email;
    }

    public String getUserProfileScreenName() {
        return userProfileScreenName;
    }

    public void setUserProfileScreenName(String userProfileScreenName) {
        this.userProfileScreenName = userProfileScreenName;
    }

    public MyFriendsList() {


    }

    public String getScreenName() {
        return screenName;
    }

    public void setScreenName(String screenName) {
        this.screenName = screenName;
    }

    public String getStatus() {
        return status;
    }

    public void setStatus(String status) {
        this.status = status;
    }

    public String getUserId() {
        return userId;
    }

    public void setUserId(String userId) {
        this.userId = userId;
    }

    public String getUserPostImageURL() {
        return userPostImageURL;
    }

    public void setUserPostImageURL(String userPostImageURL) {
        this.userPostImageURL = userPostImageURL;
    }
}
