/**
 * 
 */
package com.SocialApp.Controllers;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RestController;

import com.SocialApp.Entities.MessageDetails;
import com.SocialApp.Entities.Response;
import com.SocialApp.Services.MailServices;
import com.SocialApp.Services.MessagingServices;

/**
 * @author rahul
 *
 */

@RestController
public class MessageController {

	@Autowired
	MessagingServices messagingServices;

	@Autowired
	MailServices mailServices;

	@RequestMapping(value = "/sendMessage", method = RequestMethod.POST, produces = {
			MediaType.APPLICATION_JSON_VALUE }, consumes = { MediaType.APPLICATION_JSON_VALUE })
	public ResponseEntity<?> saveMessages(@RequestBody HashMap<String, String> messageInformation) {
		int userId = messagingServices.getUserId(messageInformation.get("screenName"));
		int loginStatus = messagingServices.getUserLoginStatus(messageInformation.get("recipient"));
		String emailId = messagingServices.getUserEmailId(messageInformation.get("recipient"));

		MessageDetails messageDetails = new MessageDetails();
		messageDetails.setUserId(userId);
		messageDetails.setScreenName(messageInformation.get("screenName"));
		messageDetails.setRecepient(messageInformation.get("recipient"));
		messageDetails.setSubject(messageInformation.get("subject"));
		messageDetails.setEmailContent(messageInformation.get("email"));
		messageDetails.setDeletedBy("");

		messagingServices.saveMessages(messageDetails);

		if (loginStatus == 0) {
			mailServices.sendSimpleMessage(emailId, messageInformation.get("subject"),
					messageInformation.get("email"));
		}

		return new ResponseEntity<>(new Response(200), HttpStatus.OK);
	}

	@RequestMapping(value = "/getChatList", method = RequestMethod.POST, produces = {
			MediaType.APPLICATION_JSON_VALUE }, consumes = { MediaType.APPLICATION_JSON_VALUE })
	public MessageResponsePost getChatList(@RequestBody HashMap<String, String> screenName) {
		MessageResponsePost response = new MessageResponsePost();
		Set<String> set = new HashSet<String>();
		List<String> recipients = messagingServices.getChatUsersFromRecepients(screenName.get("screenName"));
		List<String> sender = messagingServices.getChatUsersFromScreenName(screenName.get("screenName"));
		set.addAll(sender);
		set.addAll(recipients);
		response.setData(new ArrayList<String>(set));
		return response;
	}

	@RequestMapping(value = "/getConversation", method = RequestMethod.POST, produces = {
			MediaType.APPLICATION_JSON_VALUE }, consumes = { MediaType.APPLICATION_JSON_VALUE })
	public MessageResponsePost getConversation(@RequestBody HashMap<String, String> people) {
		List<String> conversation = messagingServices.getConversation(people.get("screenName"),
				people.get("recipient"));
		MessageResponsePost response = new MessageResponsePost();
		response.setData(conversation);
		return response;
	}

	@RequestMapping(value = "/deleteConversation", method = RequestMethod.POST, produces = {
			MediaType.APPLICATION_JSON_VALUE }, consumes = { MediaType.APPLICATION_JSON_VALUE })
	public ResponseEntity<?> deleteConversation(@RequestBody HashMap<String, String> people) {
		messagingServices.getAllMessageDetails(people.get("screenName"), people.get("recipient"));
		return new ResponseEntity<>(new Response(200), HttpStatus.OK);
	}
}

class MessageResponsePost {

	private List<String> data;

	public List<String> getData() {
		return data;
	}

	public void setData(List<String> data) {
		this.data = data;
	}

}
