/*
 * Created on Feb 17, 2004
 *
 * To change the template for this generated file go to
 * Window - Preferences - Java - Code Generation - Code and Comments
 */
package is.idega.idegaweb.member.isi.block.members.data;

import is.idega.idegaweb.member.util.IWMemberConstants;

import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.Iterator;
import java.util.List;

import com.idega.data.IDOLookup;
import com.idega.idegaweb.IWResourceBundle;
import com.idega.user.data.Group;
import com.idega.user.data.GroupRelation;
import com.idega.user.data.GroupRelationHome;
import com.idega.user.data.User;
import com.idega.user.data.UserStatus;
import com.idega.user.data.UserStatusHome;
import com.idega.util.IWTimestamp;

/**
 * A container for all data about a users registration. Containt data needed by {@see is.idega.idegaweb.member.isi.block.members.presentation.MemberOverview}
 */
public class MemberGroupData {
	
	public static final String LOCALIZE_KEY_PREFIX_GROUP_CATEGORY = "group_category_";
	//public static final String LOCALIZE_KEY_PREFIX_STATUS = "usr_stat_";
	
	/**
	 * @param user The user to create the MemberGroupData for
	 * @param iwrb
	 */
	public MemberGroupData(User user, IWResourceBundle iwrb, IWResourceBundle comUserBundle) {
		this._comUserBundle = comUserBundle;
		this._iwrb = iwrb;
		this._user = user;
		int userId = user.getID();
		Collection history = Collections.EMPTY_LIST;
		try {
			history = ((GroupRelationHome) com.idega.data.IDOLookup.getHome(GroupRelation.class)).findAllGroupsRelationshipsByRelatedGroupOrderedByInitiationDate(userId,"GROUP_PARENT");
		} catch (Exception e) {
			e.printStackTrace();
		}
		System.out.println("Got " + history.size() + " group relations");

		Iterator iter = history.iterator();
		StringBuffer buf = new StringBuffer();
		while(iter.hasNext()) {
			GroupRelation groupRel = (GroupRelation) iter.next();
			processGroupRelation(groupRel);
		}
	}

	/**
	 * Gets info on users registration for a group and inserts it into <code>_groupInfoList</code>
	 * @param groupRel The GroupRelation to add
	 */
	private void processGroupRelation(GroupRelation groupRel) {
		boolean isActive = groupRel.getStatus().equals(GroupRelation.STATUS_ACTIVE);
		Group user = groupRel.getRelatedGroup();
		Group group = groupRel.getGroup();
		String groupTypeName = getGroupTypeLocalizedName(group.getGroupType());
		if(groupTypeName==null || groupTypeName.equals("Unknown")) {
			groupTypeName="";
			System.out.println("Name for group type not defined, skipping group (key=\"" + LOCALIZE_KEY_PREFIX_GROUP_CATEGORY + group.getGroupType() + "\")");
			return;
		}
		this._buf.setLength(0);
		processGroup(group, this._buf, true, isActive);
		if(this._buf.length()>0) {
			IWTimestamp begin = new IWTimestamp(groupRel.getInitiationDate());
			IWTimestamp end = groupRel.getTerminationDate()==null?null:(new IWTimestamp(groupRel.getTerminationDate()));
			List userStatuses = null;
			try {
				userStatuses = (List) ((UserStatusHome)IDOLookup.getHome(UserStatus.class)).findAllActiveByUserIdAndGroupId(Integer.parseInt(user.getPrimaryKey().toString()),Integer.parseInt(group.getPrimaryKey().toString()));
			} catch (Exception e) {
				System.out.println(e.getMessage());
			}
			String statusName = "";
			if (!userStatuses.isEmpty()) {
				UserStatus userStatus =(UserStatus)userStatuses.iterator().next();
				String statusKey = userStatus.getStatus().getStatusKey();
				statusName = getStatusLocalizedName(statusKey);
			}
			addGroupInfo(this._buf.toString(), groupTypeName, statusName, begin, end);
		}
	}
	
	/**
	 * Inserts info on membership based on a group into a StringBuffer. Searches all ascendant groups and includes clubs, 
	 * leagues, regional unions, unions, federations and divisions, if found.
	 * @param group the group to categorize
	 * @param buf a StringBuffer to collect the member group info into
	 * @param isFirstGroup if the group is an imediate parent of the member
	 */
	private void processGroup(Group group, StringBuffer buf, boolean isFirstGroup, boolean isActive) {
		
		String type = group.getGroupType();
		String name = group.getName();
		
		boolean isFlock = IWMemberConstants.GROUP_TYPE_CLUB_PLAYER.equals(type);
		boolean isClub = IWMemberConstants.GROUP_TYPE_CLUB.equals(type);
		if(isClub && !this._clubList.contains(group) && isActive) {
			this._clubList.add(group);
		}
		boolean isFinalGroup = IWMemberConstants.GROUP_TYPE_CLUB.equals(type) ||
		                       IWMemberConstants.GROUP_TYPE_LEAGUE.equals(type) ||
		                       IWMemberConstants.GROUP_TYPE_REGIONAL_UNION.equals(type) ||
		                       IWMemberConstants.GROUP_TYPE_UNION.equals(type) ||
		                       IWMemberConstants.GROUP_TYPE_FEDERATION.equals(type);
		boolean isDivision = IWMemberConstants.GROUP_TYPE_CLUB_DIVISION.equals(type);
		
		if(isFirstGroup) {
			buf.append(name);
		} else if(isDivision) {
			buf.append(" - ").append(name);
		}
		
		if(!isFinalGroup) {
			Collection groups = group.getParentGroups();
			if(groups!=null && groups.size()>0) {
				Iterator iter = groups.iterator();
				while(iter.hasNext()) {
					processGroup((Group) iter.next(), buf, false, isActive);
				}
			}
		} else {
			if(!isFirstGroup) {
				buf.append(" - ").append(name);
			}
		}
	}
	
	/**
	 * gets the user whos registration data this MemberGroupData holds
	 * @return the user
	 */
	public User getUser() {
		return this._user;
	}
	
	/**
	 * A List of String arrays containing the registration data. The Strings in the array are as following
	 * <ul>
	 *   <li>[0]: The name of the group and it's relevant ancestors</li>
	 *   <li>[1]: The name of the group type, empty if not known</li>
	 *   <li>[2]: The status of the user in the group, emtpy if no status</li>
	 *   <li>[3]: The time the user was registered in the group</li>
	 *   <li>[4]: The time the user war unregistered from the group, empty if user is still in the group</li>
	 * </ul>
	 * @return list of membeship info entries
	 */
	public List getGroupInfoList() {
		return this._groupInfoList;
	}
	
	/**
	 * Gets a list of the clubs (Group instances)
	 * @return list of the clubs the user is inb
	 */
	public List getClubList() {
		return this._clubList;
	}
	
	/**
	 * Adds info on group into <code>_groupInfoList</code>
	 * @param name Name of group
	 * @param type Type of group
	 * @param status Status of user in group
	 * @param begin Date when user joined group
	 * @param end Date when user left group
	 */
	private void addGroupInfo(String name, String type, String status, IWTimestamp begin, IWTimestamp end) {
		String[] result = new String[5];
		result[0] = this._buf.toString();
		result[1] = type;
		result[2] = status;
		result[3] = begin.getDateString("dd.MM.yyyy");
		if(end!=null) {
			result[4] = end.getDateString("dd.MM.yyyy");
		} else {
			result[4] = "";
		}
		this._groupInfoList.add(result);
	}
	
	private String getStatusLocalizedName(String statusKey) {
		String value = this._comUserBundle.getLocalizedString(statusKey, statusKey);
		return value;
	}
	
	private String getGroupTypeLocalizedName(String groupTypeKey) {
		String value = this._iwrb.getLocalizedString(LOCALIZE_KEY_PREFIX_GROUP_CATEGORY + groupTypeKey, LOCALIZE_KEY_PREFIX_GROUP_CATEGORY + groupTypeKey);
		return value;
	}
	
	
	private List _groupInfoList = new ArrayList();
	private List _clubList = new ArrayList();
	private User _user;
	private StringBuffer _buf = new StringBuffer();
	private IWResourceBundle _iwrb;
	private IWResourceBundle _comUserBundle;
}
