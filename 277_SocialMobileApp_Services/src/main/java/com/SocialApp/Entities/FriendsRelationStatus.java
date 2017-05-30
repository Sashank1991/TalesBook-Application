package com.SocialApp.Entities;

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

import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.annotation.JsonIgnoreProperties;

@Entity
@Table(name = "FriendsRelationStatus")
public class FriendsRelationStatus {

	@Column
	@Id
	@GeneratedValue(strategy = GenerationType.AUTO)
	private int FriendsRelationStatusId;

	@JsonIgnore
	@ManyToOne
	@JoinColumn(name = "user_id")
	private Profile user;

	@ManyToOne
	@JoinColumn(name = "friend_id")
	private Profile friend;

	@Column(name = "status")
	private String status;

	@Column(name = "TIMESTAMP_FIELD")
	@Temporal(TemporalType.TIMESTAMP)
	private java.util.Date timestampField;

	public Profile getUser() {
		return user;
	}

	public void setUser(Profile user) {
		this.user = user;
	}

	@JsonIgnoreProperties({ "friends", "posts", "password", "profileType", "following", "verificationCode",
			"status" })
	public Profile getFriend() {
		return friend;
	}

	public void setFriend(Profile friend) {
		this.friend = friend;
	}

	public String getStatus() {
		return status;
	}

	public void setStatus(String status) {
		this.status = status;
	}

	public java.util.Date getTimestampField() {
		return timestampField;
	}

	public void setTimestampField(java.util.Date timestampField) {
		this.timestampField = timestampField;
	}

}
