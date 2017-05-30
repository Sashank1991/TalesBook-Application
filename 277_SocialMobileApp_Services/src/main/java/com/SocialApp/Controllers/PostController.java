package com.SocialApp.Controllers;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.Date;
import java.util.HashMap;
import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RestController;

import com.SocialApp.Entities.FriendsRelationStatus;
import com.SocialApp.Entities.Profile;
import com.SocialApp.Entities.UserPosts;
import com.SocialApp.Services.PostServices;
import com.SocialApp.Services.ProfileServices;

@RestController
public class PostController {

	@Autowired
	ProfileServices profileServices;

	@Autowired
	PostServices postServices;

	@RequestMapping(value = "/getAllPosts", method = RequestMethod.POST, produces = {
			MediaType.APPLICATION_JSON_VALUE }, consumes = { MediaType.APPLICATION_JSON_VALUE })
	public ResponsePost getAllPosts(@RequestBody HashMap<String, String> screenName) {
		try {
			Profile currentProfile = profileServices.getProfile(screenName.get("screenName"));
			List<Profile> allProfiles = new ArrayList<Profile>();
			List<Profile> friends = new ArrayList<Profile>();
			List<Profile> following = new ArrayList<Profile>();

			for (FriendsRelationStatus temp : currentProfile.getFriends()) {
				if (temp.getStatus().equals("friend")) {
					friends.add(temp.getFriend());
				}
			}

			for (Profile temp : currentProfile.getFollowing()) {
				following.add(temp);
			}
			allProfiles.addAll(friends);
			allProfiles.addAll(following);
			allProfiles.add(currentProfile);
			List<UserPosts> posts = postServices.getPosts(allProfiles);
			Collections.sort(posts, new Comparator<UserPosts>() {
				@Override
				public int compare(UserPosts o1, UserPosts o2) {

					if (o1.getTimestampField() == null || o2.getTimestampField() == null)
						return 0;

					return o2.getTimestampField().compareTo(o1.getTimestampField());
				}
			});
			ResponsePost response = new ResponsePost();
			response.setData(posts);

			return response;
		} catch (Exception e) {
			return null;
		}
	}

	@RequestMapping(value = "/postContent", method = RequestMethod.POST, produces = {
			MediaType.APPLICATION_JSON_VALUE }, consumes = { MediaType.APPLICATION_JSON_VALUE })
	public ResponseEntity<HashMap<String, String>> postContent(@RequestBody HashMap<String, String> postData) {
		try {
			Profile currentProfile = profileServices.getProfile(postData.get("screenName"));
			UserPosts usersPost = new UserPosts();
			usersPost.setContent(postData.get("content"));
			usersPost.setAssImage(postData.get("assImage"));
			usersPost.setProfile(currentProfile);
			usersPost.setTimestampField(new Date());
			UserPosts posts = postServices.updateOrCreatePosts(usersPost);
			if (!posts.equals(null)) {
				HashMap<String, String> response = new HashMap<String, String>();
				response.put("code", "200");
				response.put("message", "posted");
				return new ResponseEntity<HashMap<String, String>>(response, HttpStatus.ACCEPTED);
			}
			HashMap<String, String> response = new HashMap<String, String>();
			response.put("code", "404");
			response.put("message", "error occured");
			return new ResponseEntity<HashMap<String, String>>(response, HttpStatus.ACCEPTED);
		} catch (Exception e) {
			HashMap<String, String> response = new HashMap<String, String>();
			response.put("code", "404");
			response.put("message", "error");
			return new ResponseEntity<HashMap<String, String>>(response, HttpStatus.BAD_REQUEST);
		}
	}

}

class ResponsePost {

	private List<UserPosts> data;

	public List<UserPosts> getData() {
		return data;
	}

	public void setData(List<UserPosts> data) {
		this.data = data;
	}

}