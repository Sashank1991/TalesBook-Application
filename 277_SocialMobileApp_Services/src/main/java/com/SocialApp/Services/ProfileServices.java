package com.SocialApp.Services;

import java.util.List;

import javax.transaction.Transactional;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Configurable;
import org.springframework.stereotype.Service;

import com.SocialApp.Entities.Profile;
import com.SocialApp.Repositories.ProfileRepository;

@Service
@Configurable
public class ProfileServices {

	@Autowired
	ProfileRepository profileRepository;

	@Transactional
	public Profile getProfile(String screenName) {
		return profileRepository.findByscreenName(screenName);

	}
	
	@Transactional
	public Profile getProfileByEmail(String email) {
		return profileRepository.findByExtendedProfileEmail(email);

	}
	
	@Transactional
	public List<Profile> getProfiles() {
		return profileRepository.findAll();

	}

	@Transactional
	public Profile updateOrCreateProfile(Profile profile) {
		return profileRepository.save(profile);

	}

}
