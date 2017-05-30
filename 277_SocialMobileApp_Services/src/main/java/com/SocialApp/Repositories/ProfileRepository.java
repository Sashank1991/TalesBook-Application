package com.SocialApp.Repositories;

import java.util.List;

import org.springframework.data.repository.CrudRepository;

import com.SocialApp.Entities.Profile;

public interface ProfileRepository extends CrudRepository<Profile, Long> {

	public Profile findByscreenName(String screenName);

	public Profile findByUserId(int userId);

	public Profile findByExtendedProfileEmail(String email);

	public List<Profile> findAll();
}
