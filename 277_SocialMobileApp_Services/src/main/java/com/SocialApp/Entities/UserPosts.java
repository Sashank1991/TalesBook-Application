package com.SocialApp.Entities;

import javax.persistence.CascadeType;
import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.JoinColumn;
import javax.persistence.ManyToOne;
import javax.persistence.Table;
import javax.persistence.Temporal;
import javax.persistence.TemporalType;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;

@Entity
@Table(name = "Posts")
public class UserPosts {
	@Column
	@Id
	@GeneratedValue(strategy = GenerationType.AUTO)
	private int postId;
	@Column
	private String assImage;
	@Column
	private String content;

	@ManyToOne(cascade = CascadeType.ALL)
	@JoinColumn(name = "userId")
	private Profile profile;

	@Column(name = "TIMESTAMP_FIELD")
	@Temporal(TemporalType.TIMESTAMP)
	private java.util.Date timestampField;

	@JsonIgnoreProperties({ "posts", "friends", "following", "status", "verificationCode", "profileType" })
	public Profile getProfile() {
		return profile;
	}

	public java.util.Date getTimestampField() {
		return timestampField;
	}

	public void setTimestampField(java.util.Date timestampField) {
		this.timestampField = timestampField;
	}

	public void setProfile(Profile profile) {
		this.profile = profile;
	}

	public int getPostId() {
		return postId;
	}

	public void setPostId(int postId) {
		this.postId = postId;
	}

	public String getAssImage() {
		return assImage;
	}

	public void setAssImage(String assImage) {
		this.assImage = assImage;
	}

	public String getContent() {
		return content;
	}

	public void setContent(String content) {
		this.content = content;
	}

}
