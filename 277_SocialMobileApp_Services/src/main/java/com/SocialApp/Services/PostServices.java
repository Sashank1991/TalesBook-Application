package com.SocialApp.Services;

import java.util.List;

import javax.transaction.Transactional;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Configurable;
import org.springframework.stereotype.Service;

import com.SocialApp.Entities.Profile;
import com.SocialApp.Entities.UserPosts;
import com.SocialApp.Repositories.PostsRepository;

@Service
@Configurable
public class PostServices {

	@Autowired
	PostsRepository postsRepository;

	@Transactional
	public List<UserPosts> getPosts(List<Profile> profiles) {
		return postsRepository.findByProfileIn(profiles);
	}

	@Transactional
	public UserPosts updateOrCreatePosts(UserPosts post) {
		return postsRepository.save(post);
	}
}
