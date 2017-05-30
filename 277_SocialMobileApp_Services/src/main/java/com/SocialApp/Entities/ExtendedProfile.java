package com.SocialApp.Entities;

import javax.persistence.Column;
import javax.persistence.Embeddable;

@Embeddable
public class ExtendedProfile {
	@Column
	private String email;
	@Column
	private String profilePic;
	@Column
	private String Location;
	@Column
	private String profession;
	@Column
	private String aboutMe;
	@Column
	private String interests;

	public String getEmail() {
		return email;
	}

	public void setEmail(String email) {
		this.email = email;
	}

	public String getProfilePic() {
		return profilePic;
	}

	public void setProfilePic(String profilePic) {
		this.profilePic = profilePic;
	}

	public String getLocation() {
		return Location;
	}

	public void setLocation(String location) {
		Location = location;
	}

	public String getProfession() {
		return profession;
	}

	public void setProfession(String profession) {
		this.profession = profession;
	}

	public String getAboutMe() {
		return aboutMe;
	}

	public void setAboutMe(String aboutMe) {
		this.aboutMe = aboutMe;
	}

	public String getInterests() {
		return interests;
	}

	public void setInterests(String interests) {
		this.interests = interests;
	}
}
