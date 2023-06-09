package com.polus.core.externaluser.dao;

import java.util.List;

import javax.transaction.Transactional;

import org.springframework.stereotype.Service;

import com.polus.core.externaluser.pojo.ExternalUser;
import com.polus.core.externaluser.pojo.ExternalUserFeed;
import com.polus.core.person.pojo.Person;

@Transactional
@Service
public interface ExternalUserDao {

	public List<ExternalUser> fetchAllExternalUserDetails(List<String> unit);

	public void updateApproveReject(ExternalUser externalUser);

	public ExternalUser getExternalUserByUserName(String userName);

	public ExternalUser getExternalUserByPersonId(Integer personId);

	public ExternalUserFeed saveExternalUserFeed(ExternalUserFeed userFeed);

	public List<ExternalUserFeed> getExternalUserFeedDetailsForAdd();

	public void saveOrUpdate(ExternalUserFeed userDetails);

	public List<ExternalUserFeed> getExternalUserFeedDetailsForDelete();

	public List<ExternalUserFeed> getExternalUserFeeds();

	public void assignPersonRole(Person person, Integer roleId);

	public void assignPersonRoleRT(Person person, Integer roleId);

	public String getHomeUnitFromOrganizationId(String organizationId);

	public List<ExternalUser> getPendingExternalUserList();

	public String getNextSequenceId(String nextSequenceId);

	public List<String> getUnitsListByPersonIdAndRights(String personId);
	
}
