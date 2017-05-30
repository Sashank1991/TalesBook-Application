package com.SocialApp.Entities;

import java.util.ArrayList;
import java.util.List;

import javax.persistence.CascadeType;
import javax.persistence.Column;
import javax.persistence.Embedded;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.JoinColumn;
import javax.persistence.JoinTable;
import javax.persistence.ManyToMany;
import javax.persistence.OneToMany;
import javax.persistence.OrderBy;
import javax.persistence.Table;

import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.annotation.JsonIgnoreProperties;

@Entity
@Table(name = "Profiles")
public class Profile {

	@Column
	@Id
	@GeneratedValue(strategy = GenerationType.AUTO)
	private int userId;
	@Column(unique = true)
	private String screenName;
	@Column
	private String password;
	@Column
	private String profileType;
	
	@Column
	private int loginStatus;

	public int getLoginStatus() {
		return loginStatus;
	}

	public void setLoginStatus(int loginStatus) {
		this.loginStatus = loginStatus;
	}

	@Embedded
	private ExtendedProfile extendedProfile;

	@OneToMany(mappedBy = "profile")
	@OrderBy("timestampField Desc")
	private List<UserPosts> posts;

	public String getPassword() {
		return password;
	}

	public void setPassword(String password) {
		this.password = password;
	}

	@OneToMany(mappedBy = "user", cascade = CascadeType.ALL)
	private List<FriendsRelationStatus> friends = new ArrayList<FriendsRelationStatus>();

	@JsonIgnore
	@OneToMany(mappedBy = "friend")
	private List<FriendsRelationStatus> friendsDummy = new ArrayList<FriendsRelationStatus>();

	@ManyToMany
	@JoinTable(name = "userFollowings", joinColumns = { @JoinColumn(name = "userId") }, inverseJoinColumns = {
			@JoinColumn(name = "followingId") })
	private List<Profile> following;

	@JsonIgnoreProperties({ "friends", "following", "extendedProfile", "posts" })
	public List<FriendsRelationStatus> getFriends() {
		return friends;
	}

	public void setFriends(List<FriendsRelationStatus> friends) {
		this.friends = friends;
	}

	@JsonIgnoreProperties({ "friends", "following", "extendedProfile", "posts" })
	public List<Profile> getFollowing() {
		return following;
	}

	public void setFollowing(List<Profile> following) {
		this.following = following;
	}

	@JsonIgnoreProperties({ "profile" })
	public List<UserPosts> getPosts() {
		return posts;
	}

	public void setPosts(List<UserPosts> posts) {
		this.posts = posts;
	}

	public ExtendedProfile getExtendedProfile() {
		return extendedProfile;
	}

	public void setExtendedProfile(ExtendedProfile extendedProfile) {
		this.extendedProfile = extendedProfile;
	}

	public String getProfileType() {
		return profileType;
	}

	public void setProfileType(String profileType) {
		this.profileType = profileType;
	}

	private int status;
	private int verificationCode;

	public int getUserId() {
		return userId;
	}

	public void setUserId(int userId) {
		this.userId = userId;
	}

	public String getScreenName() {
		return screenName;
	}

	public void setScreenName(String screenName) {
		this.screenName = screenName;
	}

	public int getStatus() {
		return status;
	}

	public void setStatus(int status) {
		this.status = status;
	}

	public int getVerificationCode() {
		return verificationCode;
	}

	public void setVerificationCode(int verificationCode) {
		this.verificationCode = verificationCode;
	}

}
