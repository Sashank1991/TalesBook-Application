package com.SocialApp.Repositories;

import java.util.List;

import org.springframework.data.repository.CrudRepository;

import com.SocialApp.Entities.Profile;
import com.SocialApp.Entities.UserPosts;

public interface PostsRepository extends CrudRepository<UserPosts, Long> {

	public List<UserPosts> findByProfileIn(List<Profile> profiles);

}
