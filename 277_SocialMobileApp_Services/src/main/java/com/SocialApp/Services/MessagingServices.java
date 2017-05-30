/**
 * 
 */
package com.SocialApp.Services;

import java.util.ArrayList;
import java.util.List;

import javax.transaction.Transactional;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Configurable;
import org.springframework.stereotype.Service;

import com.SocialApp.Entities.MessageDetails;
import com.SocialApp.Repositories.MessagingRepository;

/**
 * @author rahul
 *
 */

@Service
@Configurable
public class MessagingServices {
	
	@Autowired
	MessagingRepository messagingRepository;
	
	@Transactional
	public List<String> getChatUsersFromScreenName(String screenName) {
		String screenNameAppend = "%"+screenName+"%";
		return messagingRepository.getChatUsersFromScreenName(screenName,screenNameAppend);
	}
	
	@Transactional
	public List<String> getChatUsersFromRecepients(String screenName) {
		String screenNameAppend = "%"+screenName+"%";
		return messagingRepository.getChatUsersFromRecepients(screenName,screenNameAppend);
	}
	
	@Transactional
	public List<String> getConversation(String screenName, String recipient) {
		List<String> people = new ArrayList<>();
		people.add(screenName);
		people.add(recipient);
		String screenNameAppend = "%"+screenName+"%";
		return messagingRepository.getConversation(people, screenNameAppend);
	}
	
	@Transactional
	public int getUserId(String screenName) {
		return messagingRepository.getUserId(screenName);
	}
	
	@Transactional
	public int getUserLoginStatus(String screenName) {
		return messagingRepository.getUserLoginStatus(screenName);
	}
	
	@Transactional
	public String getUserEmailId(String screenName) {
		return messagingRepository.getUserEmailId(screenName);
	}
	
	@Transactional
	public MessageDetails saveMessages(MessageDetails messageDetails) {
		return messagingRepository.save(messageDetails);
	}
	
	@Transactional
	public MessageDetails deleteMessages(MessageDetails messageDetails) {
		return messagingRepository.save(messageDetails);
	}
	
	@Transactional
	public List<MessageDetails> getAllMessageDetails(String screenName, String recipient) {
		List<String> people = new ArrayList<>();
		people.add(screenName);
		people.add(recipient);
		List<MessageDetails> allMessageDetails = messagingRepository.getAllMessageDetails(people);
		List<MessageDetails> allMessageDetailsReturn = messagingRepository.getAllMessageDetails(people);
		for(int i=0;i<allMessageDetails.size();i++) {
			MessageDetails md = allMessageDetails.get(i);
			if(md.getDeletedBy().equals("")) {
				md.setDeletedBy(md.getDeletedBy()+screenName);
			} else {
				md.setDeletedBy(md.getDeletedBy()+","+screenName);
			}
			allMessageDetailsReturn.add(md);
			messagingRepository.save(md);
		}
		return allMessageDetailsReturn;
	}
}
