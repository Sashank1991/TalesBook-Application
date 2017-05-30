/**
 * 
 */
package com.SocialApp.Repositories;

import java.util.List;

import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.CrudRepository;
import org.springframework.data.repository.query.Param;

import com.SocialApp.Entities.MessageDetails;

/**
 * @author rahul
 *
 */
public interface MessagingRepository extends CrudRepository<MessageDetails, Long>{
	
	@Query(value="select distinct recepient from messages where screen_name = :screenName and deleted_by not like :screenNameAppended", nativeQuery=true)
	public List<String> getChatUsersFromRecepients(@Param("screenName") String screenName, @Param("screenNameAppended") String screenNameAppended);
	
	@Query(value="select distinct screen_name from messages where recepient = :screenName and deleted_by not like :screenNameAppended", nativeQuery=true)
	public List<String> getChatUsersFromScreenName(@Param("screenName") String screenName, @Param("screenNameAppended") String screenNameAppended);
	
	@Query(value="select user_id from profiles where screen_name = :screenName", nativeQuery=true)
	public int getUserId(@Param("screenName") String screenName);
	
	@Query(value="select login_status from profiles where screen_name = :screenName", nativeQuery=true)
	public int getUserLoginStatus(@Param("screenName") String screenName);
	
	@Query(value="select email from profiles where screen_name = :screenName", nativeQuery=true)
	public String getUserEmailId(@Param("screenName") String screenName);
	
	@Query(value="select concat(screen_name,': ',email_content) as conversation from messages where screen_name in :conversationList and recepient in :conversationList and deleted_by not like :screenNameAppend order by timestamp", nativeQuery=true)
	public List<String> getConversation(@Param("conversationList") List<String> conversationList, @Param("screenNameAppend") String screenNameAppend);

	@Query(value="select * from messages where screen_name in :conversationList and recepient in :conversationList", nativeQuery=true)
	public List<MessageDetails> getAllMessageDetails(@Param("conversationList") List<String> conversationList);
}
