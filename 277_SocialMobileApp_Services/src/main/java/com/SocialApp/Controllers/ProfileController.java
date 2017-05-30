package com.SocialApp.Controllers;

import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Random;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RestController;

import com.SocialApp.Entities.ExtendedProfile;
import com.SocialApp.Entities.FriendsRelationStatus;
import com.SocialApp.Entities.Profile;
import com.SocialApp.Services.MailServices;
import com.SocialApp.Services.PostServices;
import com.SocialApp.Services.ProfileServices;

@RestController
public class ProfileController {

	@Autowired
	ProfileServices profileServices;
	@Autowired
	PostServices postServices;

	@Autowired
	MailServices mailServices;

	@RequestMapping(value = "/login", method = RequestMethod.POST, produces = {
			MediaType.APPLICATION_JSON_VALUE }, consumes = { MediaType.APPLICATION_JSON_VALUE })
	public ResponseEntity<HashMap<String, String>> login(@RequestBody HashMap<String, String> cred) {

		try {
			if (cred.get("screenName").equals("") || cred.get("password").equals("")) {
				HashMap<String, String> response = new HashMap<String, String>();
				response.put("code", "404");
				response.put("message", "error in request");
				return new ResponseEntity<HashMap<String, String>>(response, HttpStatus.ACCEPTED);
			}
			int verificationCode = 0;
			if (cred.get("verificationCode").equals("")) {
				verificationCode = 0;
			} else {
				verificationCode = Integer.parseInt(cred.get("verificationCode"));
			}
			Profile currentProfile = profileServices.getProfile(cred.get("screenName"));
			if (!(currentProfile == null)) {
				if (currentProfile.getPassword().equals(cred.get("password"))) {
					if (currentProfile.getStatus() != 0 || currentProfile.getVerificationCode() == verificationCode) {
						if (currentProfile.getStatus() == 0) {
							currentProfile.setStatus(1);
						}
						currentProfile.setLoginStatus(1);
						profileServices.updateOrCreateProfile(currentProfile);
						HashMap<String, String> response = new HashMap<String, String>();
						response.put("code", "200");
						response.put("message", "accepted");
						return new ResponseEntity<HashMap<String, String>>(response, HttpStatus.ACCEPTED);

					} else {
						HashMap<String, String> response = new HashMap<String, String>();
						response.put("code", "404");
						response.put("message", "need verification code");
						return new ResponseEntity<HashMap<String, String>>(response, HttpStatus.ACCEPTED);

					}
				} else {

					HashMap<String, String> response = new HashMap<String, String>();
					response.put("code", "404");
					response.put("message", "need password");
					return new ResponseEntity<HashMap<String, String>>(response, HttpStatus.ACCEPTED);
				}

			}

			HashMap<String, String> response = new HashMap<String, String>();
			response.put("code", "404");
			response.put("message", "profile does not exists");
			return new ResponseEntity<HashMap<String, String>>(response, HttpStatus.ACCEPTED);
		} catch (Exception e) {
			HashMap<String, String> response = new HashMap<String, String>();
			response.put("code", "404");
			response.put("message", "error");
			return new ResponseEntity<HashMap<String, String>>(response, HttpStatus.BAD_REQUEST);
		}
	}

	@RequestMapping(value = "/logout", method = RequestMethod.POST, produces = {
			MediaType.APPLICATION_JSON_VALUE }, consumes = { MediaType.APPLICATION_JSON_VALUE })
	public ResponseEntity<HashMap<String, String>> logout(@RequestBody HashMap<String, String> cred) {
		try {
			if (cred.get("screenName").equals("")) {
				HashMap<String, String> response = new HashMap<String, String>();
				response.put("code", "404");
				response.put("message", "error in request");
				return new ResponseEntity<HashMap<String, String>>(response, HttpStatus.ACCEPTED);
			}
			Profile currentProfile = profileServices.getProfile(cred.get("screenName"));
			if (!(currentProfile == null)) {
				currentProfile.setLoginStatus(0);
				profileServices.updateOrCreateProfile(currentProfile);
				HashMap<String, String> response = new HashMap<String, String>();
				response.put("code", "200");
				response.put("message", "logged out");
				return new ResponseEntity<HashMap<String, String>>(response, HttpStatus.ACCEPTED);
			}
			HashMap<String, String> response = new HashMap<String, String>();
			response.put("code", "404");
			response.put("message", "error");
			return new ResponseEntity<HashMap<String, String>>(response, HttpStatus.ACCEPTED);

		} catch (Exception e) {
			HashMap<String, String> response = new HashMap<String, String>();
			response.put("code", "404");
			response.put("message", "error");
			return new ResponseEntity<HashMap<String, String>>(response, HttpStatus.BAD_REQUEST);
		}

	}

	@RequestMapping(value = "/getProfile", method = RequestMethod.POST, produces = {
			MediaType.APPLICATION_JSON_VALUE }, consumes = { MediaType.APPLICATION_JSON_VALUE })
	public ResponseProfile getProfile(@RequestBody HashMap<String, String> screenName) {
		try {
			Profile currentProfile = profileServices.getProfile(screenName.get("screenName"));
			ResponseProfile response = new ResponseProfile();
			response.setData(currentProfile);
			return response;

		} catch (Exception e) {
			return null;
		}
	}

	@RequestMapping(value = "/createProfile", method = RequestMethod.POST, produces = {
			MediaType.APPLICATION_JSON_VALUE }, consumes = { MediaType.APPLICATION_JSON_VALUE })
	public ResponseEntity<HashMap<String, String>> createProfile(@RequestBody HashMap<String, String> profile) {

		try {
			Profile currentProfile = profileServices.getProfile(profile.get("screenName"));

			if (currentProfile == null) {

				Profile checkForEmail = profileServices.getProfileByEmail(profile.get("email"));

				if (checkForEmail == null) {
					Profile newProfile = new Profile();
					ExtendedProfile extendedProfile = new ExtendedProfile();

					int verifyCode = (new Random()).nextInt(900000) + 100000;
					newProfile.setScreenName(profile.get("screenName"));
					newProfile.setProfileType(profile.get("profileType"));
					newProfile.setPassword(profile.get("password"));
					newProfile.setStatus(0);
					newProfile.setLoginStatus(0);
					newProfile.setVerificationCode(verifyCode);
					extendedProfile.setAboutMe("");
					extendedProfile.setEmail(profile.get("email"));
					extendedProfile.setInterests("");
					extendedProfile.setLocation("");
					extendedProfile.setProfession("");
					extendedProfile.setProfilePic("");
					newProfile.setExtendedProfile(extendedProfile);
					profileServices.updateOrCreateProfile(newProfile);
					mailServices.sendSimpleMessage(profile.get("email"), "Welcome to 277_ SocialApp",
							"Please verify your account with following code" + verifyCode);
					HashMap<String, String> response = new HashMap<String, String>();
					response.put("code", "200");
					response.put("message", "profile Created");
					return new ResponseEntity<HashMap<String, String>>(response, HttpStatus.ACCEPTED);
				}
				HashMap<String, String> response = new HashMap<String, String>();
				response.put("code", "404");
				response.put("message", "profile registered with this email");
				return new ResponseEntity<HashMap<String, String>>(response, HttpStatus.ACCEPTED);
			}
			HashMap<String, String> response = new HashMap<String, String>();
			response.put("code", "404");
			response.put("message", "screen Name already exists");
			return new ResponseEntity<HashMap<String, String>>(response, HttpStatus.ACCEPTED);
		} catch (Exception e) {
			HashMap<String, String> response = new HashMap<String, String>();
			response.put("code", "404");
			response.put("message", e.getMessage());
			return new ResponseEntity<HashMap<String, String>>(response, HttpStatus.BAD_REQUEST);
		}
	}

	@RequestMapping(value = "/updateProfile", method = RequestMethod.POST, produces = {
			MediaType.APPLICATION_JSON_VALUE }, consumes = { MediaType.APPLICATION_JSON_VALUE })
	public ResponseEntity<HashMap<String, String>> updateProfile(@RequestBody HashMap<String, String> profile) {
		try {
			Profile currentProfile = profileServices.getProfile(profile.get("screenName"));

			if (!(currentProfile == null)) {
				Profile NewCurrentProfile = profileServices.getProfile(profile.get("screenNameNew"));
				if (NewCurrentProfile == null || profile.get("screenNameNew").equals(profile.get("screenName"))) {
					currentProfile.setScreenName(profile.get("screenNameNew"));
					currentProfile.setProfileType(profile.get("profileType"));
					currentProfile.setPassword(profile.get("password"));
					currentProfile.getExtendedProfile().setAboutMe(profile.get("aboutMe"));
					currentProfile.getExtendedProfile().setEmail(profile.get("email"));
					currentProfile.getExtendedProfile().setInterests(profile.get("interests"));
					currentProfile.getExtendedProfile().setLocation(profile.get("location"));
					currentProfile.getExtendedProfile().setProfession(profile.get("profession"));
					currentProfile.getExtendedProfile().setProfilePic(profile.get("profilePic"));
					profileServices.updateOrCreateProfile(currentProfile);
					HashMap<String, String> response = new HashMap<String, String>();
					response.put("code", "200");
					response.put("message", "profile Updated");
					return new ResponseEntity<HashMap<String, String>>(response, HttpStatus.ACCEPTED);
				}

				HashMap<String, String> response = new HashMap<String, String>();
				response.put("code", "404");
				response.put("message", "screen Name already exists");
				return new ResponseEntity<HashMap<String, String>>(response, HttpStatus.ACCEPTED);
			}

			HashMap<String, String> response = new HashMap<String, String>();
			response.put("code", "404");
			response.put("message", "profile does not exists");
			return new ResponseEntity<HashMap<String, String>>(response, HttpStatus.ACCEPTED);
		} catch (Exception e) {
			HashMap<String, String> response = new HashMap<String, String>();
			response.put("code", "404");
			response.put("message", "error");
			return new ResponseEntity<HashMap<String, String>>(response, HttpStatus.BAD_REQUEST);
		}
	}

	@RequestMapping(value = "/addAsFriend", method = RequestMethod.POST, produces = {
			MediaType.APPLICATION_JSON_VALUE }, consumes = { MediaType.APPLICATION_JSON_VALUE })
	public ResponseEntity<HashMap<String, String>> addAsFriend(@RequestBody HashMap<String, String> profile) {
		try {
			if (profile.get("email").equals("") || profile.get("screenName").equals("")) {
				HashMap<String, String> response = new HashMap<String, String>();
				response.put("code", "404");
				response.put("message", "error in request");
				return new ResponseEntity<HashMap<String, String>>(response, HttpStatus.ACCEPTED);
			}

			Profile currentProfile = profileServices.getProfile(profile.get("screenName"));

			FriendsRelationStatus currentFriend = new FriendsRelationStatus();

			List<FriendsRelationStatus> frnds = currentProfile.getFriends();

			Profile friendRequestto = profileServices.getProfileByEmail(profile.get("email"));

			if (friendRequestto != null) {

				List<FriendsRelationStatus> currentPorfileFriendProfile = friendRequestto.getFriends();

				for (FriendsRelationStatus frnd : frnds) {
					if (frnd.getFriend().getExtendedProfile().getEmail().equals(profile.get("email"))) {
						if (frnd.getStatus().equals("received")) {
							frnd.setStatus("friend");

							for (FriendsRelationStatus frnd1 : currentPorfileFriendProfile) {

								if (frnd1.getFriend().getScreenName().equals(profile.get("screenName"))) {
									if (frnd1.getStatus().equals("pending")) {
										frnd1.setStatus("friend");
									}
								}
							}
						}
						currentFriend = frnd;
					}
				}

				profileServices.updateOrCreateProfile(currentProfile);
				profileServices.updateOrCreateProfile(friendRequestto);
				if (currentFriend.getStatus() == null) {
					currentFriend.setStatus("pending");
					currentFriend.setFriend(friendRequestto);
					currentFriend.setUser(currentProfile);
					currentFriend.setTimestampField(new Date());
					frnds.add(currentFriend);
					currentProfile.setFriends(frnds);
					profileServices.updateOrCreateProfile(currentProfile);
					currentFriend.setStatus("received");
					currentFriend.setFriend(currentProfile);
					currentFriend.setUser(friendRequestto);
					currentFriend.setTimestampField(new Date());
					friendRequestto.getFriends().add(currentFriend);
					profileServices.updateOrCreateProfile(friendRequestto);

				}

				HashMap<String, String> response = new HashMap<String, String>();
				response.put("code", "200");
				response.put("message", "profile Updated");
				return new ResponseEntity<HashMap<String, String>>(response, HttpStatus.ACCEPTED);
			}

			mailServices.sendSimpleMessage(profile.get("email"), "Invitation to connect with SocialApp",
					profile.get("screenName") + " wants you to connect with him using social app");
			HashMap<String, String> response = new HashMap<String, String>();
			response.put("code", "200");
			response.put("message", "Invitation sent because he is not a registered member");
			return new ResponseEntity<HashMap<String, String>>(response, HttpStatus.ACCEPTED);

		} catch (Exception e) {
			HashMap<String, String> response = new HashMap<String, String>();
			response.put("code", "404");
			response.put("message", "error");
			return new ResponseEntity<HashMap<String, String>>(response, HttpStatus.BAD_REQUEST);
		}
	}

	@RequestMapping(value = "/acceptFriend", method = RequestMethod.POST, produces = {
			MediaType.APPLICATION_JSON_VALUE }, consumes = { MediaType.APPLICATION_JSON_VALUE })
	public ResponseEntity<HashMap<String, String>> acceptFriend(@RequestBody HashMap<String, String> profile) {
		try {
			if (profile.get("UserScreenName").equals("") || profile.get("FriendScreenName").equals("")) {
				HashMap<String, String> response = new HashMap<String, String>();
				response.put("code", "404");
				response.put("message", "error in request");
				return new ResponseEntity<HashMap<String, String>>(response, HttpStatus.ACCEPTED);
			}

			Profile currentProfile = profileServices.getProfile(profile.get("UserScreenName"));

			Profile friendProfile = profileServices.getProfile(profile.get("FriendScreenName"));

			for (FriendsRelationStatus frnd : friendProfile.getFriends()) {
				if (frnd.getFriend().getScreenName().equals(profile.get("UserScreenName"))) {
					if (frnd.getStatus().equals("pending")) {
						frnd.setStatus("friend");
					}

				}
			}
			profileServices.updateOrCreateProfile(friendProfile);

			List<FriendsRelationStatus> currentProfileFriendNeedToBeAdded = currentProfile.getFriends();

			for (FriendsRelationStatus frnd : currentProfileFriendNeedToBeAdded) {
				if (frnd.getFriend().getScreenName().equals(profile.get("FriendScreenName"))) {
					if (frnd.getStatus().equals("received")) {
						frnd.setStatus("friend");
					}

				}
			}
			profileServices.updateOrCreateProfile(currentProfile);

			HashMap<String, String> response = new HashMap<String, String>();
			response.put("code", "200");
			response.put("message", "profile Updated");
			return new ResponseEntity<HashMap<String, String>>(response, HttpStatus.ACCEPTED);
		} catch (

		Exception e) {
			HashMap<String, String> response = new HashMap<String, String>();
			response.put("code", "404");
			response.put("message", "error");
			return new ResponseEntity<HashMap<String, String>>(response, HttpStatus.BAD_REQUEST);
		}
	}

	@RequestMapping(value = "/getpublicprofiles", method = RequestMethod.POST, produces = {
			MediaType.APPLICATION_JSON_VALUE }, consumes = { MediaType.APPLICATION_JSON_VALUE })
	public ResponseProfiles getpublicprofiles(@RequestBody HashMap<String, String> screenName) {
		try {
			if (!screenName.get("screenName").equals("")) {
				List<Profile> currentProfile = profileServices.getProfiles();
				List<Profile> returnProfiles = new ArrayList<Profile>();

				for (Profile _profile : currentProfile) {

					if (_profile.getProfileType().toLowerCase().equals("public")
							&& !_profile.getScreenName().toLowerCase().equals(screenName.get("screenName"))) {
						returnProfiles.add(_profile);
					}
				}

				ResponseProfiles responseProfiles = new ResponseProfiles();
				responseProfiles.setData(returnProfiles);

				return responseProfiles;
			} else {
				return null;
			}

		} catch (Exception e) {
			return null;
		}
	}

	@RequestMapping(value = "/followpublicprofiles", method = RequestMethod.POST, produces = {
			MediaType.APPLICATION_JSON_VALUE }, consumes = { MediaType.APPLICATION_JSON_VALUE })
	public ResponseEntity<HashMap<String, String>> followpublicprofiles(@RequestBody HashMap<String, String> profile) {

		try {
			if (profile.get("UserScreenName").equals("") || profile.get("FollowScreenName").equals("")) {
				HashMap<String, String> response = new HashMap<String, String>();
				response.put("code", "404");
				response.put("message", "error in request");
				return new ResponseEntity<HashMap<String, String>>(response, HttpStatus.ACCEPTED);
			}

			Profile currentProfile = profileServices.getProfile(profile.get("UserScreenName"));

			Profile followProfile = profileServices.getProfile(profile.get("FollowScreenName"));

			boolean exists = false;

			if (currentProfile.getFollowing().contains(followProfile)) {
				exists = true;
			}

			if (!exists) {
				currentProfile.getFollowing().add(followProfile);
				profileServices.updateOrCreateProfile(currentProfile);
				HashMap<String, String> response = new HashMap<String, String>();
				response.put("code", "200");
				response.put("message", "accepted");
				return new ResponseEntity<HashMap<String, String>>(response, HttpStatus.ACCEPTED);
			} else {
				HashMap<String, String> response = new HashMap<String, String>();
				response.put("code", "404");
				response.put("message", "alreadyFollowing");
				return new ResponseEntity<HashMap<String, String>>(response, HttpStatus.ACCEPTED);

			}

		} catch (

		Exception e) {
			HashMap<String, String> response = new HashMap<String, String>();
			response.put("code", "404");
			response.put("message", "error");
			return new ResponseEntity<HashMap<String, String>>(response, HttpStatus.BAD_REQUEST);
		}
	}

}

class ResponseProfile {

	private Profile data;

	public Profile getData() {
		return data;
	}

	public void setData(Profile data) {
		this.data = data;
	}

}

class ResponseProfiles {

	private List<Profile> data;

	public List<Profile> getData() {
		return data;
	}

	public void setData(List<Profile> data) {
		this.data = data;
	}

}
