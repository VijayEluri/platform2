/*
 * Created on Mar 11, 2003
 * 
 * To change this generated comment go to Window>Preferences>Java>Code
 * Generation>Code and Comments
 */
package is.idega.idegaweb.member.business.plugins;

import is.idega.idegaweb.member.business.MemberUserBusiness;
import is.idega.idegaweb.member.business.NoAbbreviationException;
import is.idega.idegaweb.member.business.NoLeagueClubCollectionGroup;
import is.idega.idegaweb.member.presentation.ClubInformationTab;
import is.idega.idegaweb.member.util.IWMemberConstants;
import java.rmi.RemoteException;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Iterator;
import java.util.List;
import javax.ejb.CreateException;
import javax.ejb.FinderException;
import javax.ejb.RemoveException;
import javax.transaction.SystemException;
import javax.transaction.UserTransaction;
import com.idega.business.IBOLookupException;
import com.idega.business.IBOServiceBean;
import com.idega.core.accesscontrol.business.AccessControl;
import com.idega.core.accesscontrol.business.AccessController;
import com.idega.core.accesscontrol.data.ICRole;
import com.idega.presentation.IWContext;
import com.idega.presentation.PresentationObject;
import com.idega.user.business.GroupBusiness;
import com.idega.user.business.UserGroupPlugInBusiness;
import com.idega.user.data.Group;
import com.idega.user.data.GroupHome;
import com.idega.user.data.User;

/**
 * @author palli
 * 
 * To change this generated comment go to Window>Preferences>Java>Code
 * Generation>Code and Comments
 */
public class ClubInformationPluginBusinessBean extends IBOServiceBean implements ClubInformationPluginBusiness,
		UserGroupPlugInBusiness {

	/*
	 * (non-Javadoc)
	 * 
	 * @see com.idega.user.business.UserGroupPlugInBusiness#beforeUserRemove(com.idega.user.data.User)
	 */
	public void beforeUserRemove(User user) throws RemoveException, RemoteException {
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see com.idega.user.business.UserGroupPlugInBusiness#afterUserCreate(com.idega.user.data.User)
	 */
	public void afterUserCreateOrUpdate(User user) throws CreateException, RemoteException {
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see com.idega.user.business.UserGroupPlugInBusiness#beforeGroupRemove(com.idega.user.data.Group)
	 */
	public void beforeGroupRemove(Group group) throws RemoveException, RemoteException {
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see com.idega.user.business.UserGroupPlugInBusiness#afterGroupCreate(com.idega.user.data.Group)
	 */
	public void afterGroupCreateOrUpdate(Group group) throws CreateException, RemoteException {
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see com.idega.user.business.UserGroupPlugInBusiness#instanciateEditor(com.idega.user.data.Group)
	 */
	public PresentationObject instanciateEditor(Group group) throws RemoteException {
		return null;
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see com.idega.user.business.UserGroupPlugInBusiness#instanciateViewer(com.idega.user.data.Group)
	 */
	public PresentationObject instanciateViewer(Group group) throws RemoteException {
		return null;
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see com.idega.user.business.UserGroupPlugInBusiness#getUserPropertiesTabs(com.idega.user.data.User)
	 */
	public List getUserPropertiesTabs(User user) throws RemoteException {
		return null;
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see com.idega.user.business.UserGroupPlugInBusiness#getGroupPropertiesTabs(com.idega.user.data.Group)
	 */
	public List getGroupPropertiesTabs(Group group) throws RemoteException {
		List list = new ArrayList();
		list.add(new ClubInformationTab(group));
		return list;
	}

	public String isUserAssignableFromGroupToGroup(User user, Group sourceGroup, Group targetGroup) {
		return null;
	}

	public String isUserSuitedForGroup(User user, Group targetGroup) {
		return null;
	}

	/**
	 * A method to create a connection between a club and a league. Creates a
	 * copy of the groups under the league template in under the club and
	 * aliases to these groups under the league.
	 * 
	 * @param connection
	 *            The primary key id of the league that the club/division is
	 *            being connected to.
	 * @param parentGroupId
	 *            The primary key id of the club/division being connected to the
	 *            league.
	 * @param clubName
	 *            The name of the club.
	 * @param iwc
	 *            The idegaWeb context object.
	 * 
	 * @return Returns true if the groups were created normally, false
	 *         otherwise.
	 */
	public boolean createSpecialConnection(String connection, int parentGroupId, String clubName, IWContext iwc) {
		//Are we connecting to a league.
		
		//TODO OTHER CONNECTION TO CLUB GROUP FOR LEAGUE
		if (connection == null || connection.equals("")) {
			return false;
		}
		try {
			//Get the group that is connecting to the league.
			Group parentGroup = (Group) (((GroupHome) com.idega.data.IDOLookup.getHome(Group.class)).findByPrimaryKey(new Integer(
					parentGroupId)));
			//Get the league
			Group specialGroup = (Group) (((GroupHome) com.idega.data.IDOLookup.getHome(Group.class)).findByPrimaryKey(new Integer(
					connection)));
			/*
			 * Going through the child groups of the league group (specialGroup)
			 * and trying to find the groups there that are the
			 * CLUB_DIVISION_TEMPLATE group and the LEAGUE_CLUB_DIVISION group.
			 */
			Group child = null;
			Group clubDivisionGroup = null;
			Group clubDivisionTemplateGroup = null;
			boolean foundIt = false;
			boolean foundClubDivisionGroup = false;
			List children = specialGroup.getChildGroups();
			Iterator it = children.iterator();
			//Do this while there are still children under the specialGroup
			// and I haven't found the two groups I'm looking for.
			while (it.hasNext() && !(foundIt && foundClubDivisionGroup)) {
				child = (Group) it.next();
				if (child.getGroupType().equals(IWMemberConstants.GROUP_TYPE_CLUB_DIVISION_TEMPLATE)) {
					clubDivisionTemplateGroup = child;
					foundIt = true;
				}
				else if (child.getGroupType().equals(IWMemberConstants.GROUP_TYPE_LEAGUE_CLUB_DIVISION)) {
					clubDivisionGroup = child;
					foundClubDivisionGroup = true;
				}
			}
			//If we don't find the group to store the aliases then we'll just
			// store them directly under the league group.
			if (clubDivisionGroup == null) {
				clubDivisionGroup = specialGroup;
			}
			if (foundIt && clubDivisionTemplateGroup != null) {
				Group topNode = parentGroup;
				//If it's the club creating the connection we have to create a
				// group to put the copies under.
				if (parentGroup.getGroupType().equals(IWMemberConstants.GROUP_TYPE_CLUB)) {
					topNode = getGroupBusiness().createGroupUnder("Flokkar", "",
							IWMemberConstants.GROUP_TYPE_CLUB_DIVISION, parentGroup);
					getGroupBusiness().applyOwnerAndAllGroupPermissionsToNewlyCreatedGroupForUserAndHisPrimaryGroup(topNode, iwc.getCurrentUser());
				}
				//Insert a copy of all the template groups under the
				// club/division and aliases under the league.
				insertCopyOfChild(topNode, clubDivisionTemplateGroup, clubDivisionGroup, clubName, iwc);
				return true;
			}
		}
		catch (Exception e) {
			e.printStackTrace();
		}
		return false;
	}

	/*
	 * A method that inserts copies of the leagues template groups under the
	 * club/division. And also creates aliases back to the league from these
	 * groups.
	 * 
	 * @param parent The group to create the copies under. @param templateParent
	 * The parent group of the template groups. These groups are copied. @param
	 * special The group to store the aliases under. @param clubName The name of
	 * the club. @param iwc The idegaWeb context object.
	 */
	private void insertCopyOfChild(Group parent, Group templateParent, Group special, String clubName, IWContext iwc) {
		try {
			//Get all groups under the template parent group and iterate
			// through them.
			List child = templateParent.getChildGroups();
			Collection templateOwners = getGroupBusiness().getOwnerUsersForGroup(templateParent);
			Iterator it = child.iterator();
			while (it.hasNext()) {
				//Get the group
				Group playerGroup = (Group) it.next();
				//If the groups type is CLUB_PLAYER_TEMPLATE then create a copy
				// of it under the club/division.
				if (playerGroup.getGroupType().equals(IWMemberConstants.GROUP_TYPE_CLUB_PLAYER_TEMPLATE)) {
					//Create a copy of the player group under the
					// club/division.
					Group newGroup = getGroupBusiness().createGroupUnder(playerGroup.getName(), "",
							IWMemberConstants.GROUP_TYPE_CLUB_PLAYER, parent);
					//This is a hack to store the connection between the copy
					// and the original. Should maybe be replaced with some
					// metadata.
					newGroup.setAlias(playerGroup);
					//Copy the metadata
					java.util.Map t = playerGroup.getMetaDataAttributes();
					if (t != null) {
						newGroup.setMetaDataAttributes(t);
					}
					newGroup.store();
					//Setting the correct access controls for the group. Set
					// the owner of the template as the owner of the group, then
					// give the current user all permissions for the group.
					if (templateOwners != null && !templateOwners.isEmpty()) {
						Iterator owners = templateOwners.iterator();
						while (owners.hasNext()) {
							User owner = (User) owners.next();
							getGroupBusiness().applyOwnerAndAllGroupPermissionsToNewlyCreatedGroupForUserAndHisPrimaryGroup(newGroup, owner);
						}
					}
					getGroupBusiness().applyAllGroupPermissionsForGroupToUsersPrimaryGroup(newGroup,
							iwc.getCurrentUser());
					//Try to update the connection to the league. If it does
					// not exist, create a new connection.
					if (!updateSpecial(special, playerGroup, newGroup, clubName, iwc, templateOwners)) {
						//Create a new group under the league_club_division
						// group that links to the playerGroup in the league.
						Group newSpecialPlayerGroup = getGroupBusiness().createGroupUnder(playerGroup.getName(), "",
								IWMemberConstants.GROUP_TYPE_CLUB_PLAYER, special);
						// This is a hack to store the connection between the
						// copy and the original. Should maybe be replaced with
						// some metadata.
						newSpecialPlayerGroup.setAlias(playerGroup);
						newSpecialPlayerGroup.store();
						
						//Setting the correct access controls for the group.
						// Set the owner of the template as the owner of the
						// group.
						if (templateOwners != null && !templateOwners.isEmpty()) {
							Iterator owners = templateOwners.iterator();
							while (owners.hasNext()) {
								User owner = (User) owners.next();
								getGroupBusiness().applyOwnerAndAllGroupPermissionsToNewlyCreatedGroupForUserAndHisPrimaryGroup(newSpecialPlayerGroup, owner);
							}
						}
						//Create a link to the actual group in the club, with
						// a new name and put it under the group created above.
						String name = newGroup.getName();
						if (clubName != null) {
							name += " (" + clubName + ")";
						}
						Group newSpecialPlayerAliasGroup = getGroupBusiness().createGroupUnder(name, "",
								IWMemberConstants.GROUP_TYPE_ALIAS, newSpecialPlayerGroup);
						newSpecialPlayerAliasGroup.setAlias(newGroup);
						newSpecialPlayerAliasGroup.store();
						if (templateOwners != null && !templateOwners.isEmpty()) {
							Iterator owners = templateOwners.iterator();
							while (owners.hasNext()) {
								User owner = (User) owners.next();
								getGroupBusiness().applyOwnerAndAllGroupPermissionsToNewlyCreatedGroupForUserAndHisPrimaryGroup(newSpecialPlayerAliasGroup, owner);
							}
						}
					}
				}
			}
		}
		catch (Exception e) {
			e.printStackTrace();
		}
	}

	/*
	 * A method that adds a new alias connection between a club/division and a
	 * league, if some connection already exists in the league.
	 * 
	 * @param special A Group object representing the league. 
	 * @param playerGroup A Group object representing the template group being copied.
	 * @param newGroup The copy of the template group. 
	 * @param clubName The name of the club. 
	 * @param iwc The idegaWeb context object. 
	 * @param owners A Collection representing the Users that are supposed to be the owners of the created aliases.
	 * 
	 * @return Returns true if the connection already exists and then the alias is created. False otherwise.
	 */
	private boolean updateSpecial(Group special, Group playerGroup, Group newGroup, String clubName, IWContext iwc,
			Collection owners) {
		try {
			//Get all groups under the template parent group and iterate
			// through them.
			List childs = special.getChildGroups();
			Iterator it = childs.iterator();
			while (it.hasNext()) {
				Group child = (Group) it.next();
				if (child.getGroupType().equals(IWMemberConstants.GROUP_TYPE_CLUB_PLAYER)) {
					if (child.getAliasID() == ((Integer) playerGroup.getPrimaryKey()).intValue()) {
						String name = newGroup.getName();
						if (clubName != null) {
							name += " (" + clubName + ")";
						}
						Group newSpecialPlayerAliasGroup = getGroupBusiness().createGroupUnder(name, "",
								IWMemberConstants.GROUP_TYPE_ALIAS, child);
						newSpecialPlayerAliasGroup.setAlias(newGroup);
						newSpecialPlayerAliasGroup.store();
						if (owners != null && !owners.isEmpty()) {
							Iterator o = owners.iterator();
							while (o.hasNext()) {
								User owner = (User) o.next();
								getGroupBusiness().applyOwnerAndAllGroupPermissionsToNewlyCreatedGroupForUserAndHisPrimaryGroup(newSpecialPlayerAliasGroup, owner);
							}
						}
						return true;
					}
				}
			}
		}
		catch (Exception e) {
			e.printStackTrace();
		}
		return false;
	}

	/**
	 * A method to update the player groups created from the templates in a
	 * league.
	 * 
	 * @param special
	 *            The group under the league tree that the update method was
	 *            executed from.

	 * @return True if all the groups are updated, false otherwise.
	 */
	public boolean updateConnectedToSpecial(Group special) {
		Thread updateThread = new SpecialConnectionUpdateThread(special);
		updateThread.start();
		
		return true;
	}

	/*
	 * Get the GroupBusiness.
	 */
	private GroupBusiness getGroupBusiness() {
		GroupBusiness business = null;
		try {
			business = (GroupBusiness) getServiceInstance(GroupBusiness.class);
		}
		catch (IBOLookupException e) {
			e.printStackTrace();
		}
		return business;
	}
	
	/*
	 * Get the MemberUserBusiness.
	 */
	private MemberUserBusiness getMemberUserBusiness() {
		MemberUserBusiness business = null;
		try {
			business = (MemberUserBusiness) getServiceInstance(MemberUserBusiness.class);
		}
		catch (IBOLookupException e) {
			e.printStackTrace();
		}
		return business;
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see com.idega.user.business.UserGroupPlugInBusiness#getMainToolbarElements()
	 */
	public List getMainToolbarElements() throws RemoteException {
		return null;
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see com.idega.user.business.UserGroupPlugInBusiness#getGroupToolbarElements(com.idega.user.data.Group)
	 */
	public List getGroupToolbarElements(Group group) throws RemoteException {
		return null;
	}

	/* (non-Javadoc)
	 * @see com.idega.user.business.UserGroupPlugInBusiness#canCreateSubGroup(com.idega.user.data.Group,java.lang.String)
	 */
	public String canCreateSubGroup(Group group, String groupTypeOfSubGroup) throws RemoteException {
		
		//A fix so we don't always autocreate main committess, only when creating new clubs,league,etc
		String type = group.getGroupType();
		
		if( (type.equals(IWMemberConstants.GROUP_TYPE_CLUB_COMMITTEE) || type.equals(IWMemberConstants.GROUP_TYPE_CLUB_DIVISION_COMMITTEE) 
				|| type.equals(IWMemberConstants.GROUP_TYPE_LEAGUE_COMMITTEE) || type.equals(IWMemberConstants.GROUP_TYPE_FEDERATION_COMMITTEE) 
				|| type.equals(IWMemberConstants.GROUP_TYPE_UNION_COMMITTEE) || type.equals(IWMemberConstants.GROUP_TYPE_REGIONAL_UNION_COMMITTEE))
				&& groupTypeOfSubGroup.equals(IWMemberConstants.GROUP_TYPE_CLUB_COMMITTEE_MAIN)){
			Collection parents = group.getParentGroups();
			if(parents!=null && parents.size()==1){
				Group parent = (Group) parents.iterator().next();
				if(parent.getGroupType().equals(type)){
					return "[This is a fix/hack for the group type templates] Auto creation of main committee stopped for group "+group.getName()+" of type "+ type;
				}
				else if(type.equals(IWMemberConstants.GROUP_TYPE_CLUB_DIVISION_COMMITTEE)){
					return "[This is a fix/hack for the group type templates] Auto creation of main committee stopped for group "+group.getName()+" of type "+ type;
				}
			}else{
				return "[This is a fix/hack for the group type templates] Auto creation of main committee stopped for group "+group.getName()+" of type "+ type+". The parent group has many parents!";
			}
		}
		
		return null;
	}
	
	protected void connectAllClubsUnderTheirLeagues() throws RemoteException {
		UserTransaction trans = this.getSessionContext().getUserTransaction();
		try {
			trans.begin();
		
			String[] leagueType = { IWMemberConstants.GROUP_TYPE_LEAGUE };
			// Get all leagues
			Collection leagues = getGroupBusiness().getGroups(leagueType, true);
			if (leagues != null && !leagues.isEmpty()) {
				for (Iterator iter = leagues.iterator(); iter.hasNext();) {
					Group league = (Group) iter.next();
					ICRole role = createRoleForLeague(league);
					addLeagueRoleToLeagueStaffAndCommitteeGroups(role, league);
					Group clubCollectionGroup = addClubCollectionGroupToLeague(league);
					
					
					// for each create the shortcuts for the clubs and all roles
					// and stuff
					Collection clubs = getGroupBusiness().getGroupsByMetaDataKeyAndValue(
							IWMemberConstants.META_DATA_CLUB_LEAGUE_CONNECTION, league.getPrimaryKey().toString());
					// THESE ONLY SHOULD HAVE ONE DIVISION
					if (clubs != null && !clubs.isEmpty()) {
						for (Iterator clubbies = clubs.iterator(); clubbies.hasNext();) {
							Group club = (Group) clubbies.next();
							addLeagueRoleAccessToClub(role, club);
							// ADD SHORTCUT TO CLUB
							createAliasToClubUnderClubCollectionGroup(clubCollectionGroup,club);
							
							Group division = getMemberUserBusiness().getDivisionForClub(club);
							String metadata = division.getMetaData(IWMemberConstants.META_DATA_DIVISION_LEAGUE_CONNECTION);
							if (metadata == null
									|| club.getMetaData(IWMemberConstants.META_DATA_CLUB_LEAGUE_CONNECTION).equals(
											metadata)) {
								addLeagueRoleAccessToDivision(role, division);
							}
							else {
								System.err.println("[ClubInformationPluginBusiness] Club league connection and division connection do not match for club: "
										+ club.getName()
										+ "and division: "
										+ division.getName()
										+ " (league id "
										+ metadata + ")");
							}
						}
					}
					
					// MULTIDIVISION CLUBS
					Collection divisions = getGroupBusiness().getGroupsByMetaDataKeyAndValue(
							IWMemberConstants.META_DATA_DIVISION_LEAGUE_CONNECTION, league.getPrimaryKey().toString());
					if (divisions != null && !divisions.isEmpty()) {
						for (Iterator divs = divisions.iterator(); divs.hasNext();) {
							Group division = (Group) divs.next();
							addLeagueRoleAccessToDivision(role, division);
							// ADD SHORTCUT TO CLUB
							Group theClub = getMemberUserBusiness().getClubForGroup(division);
							createAliasToClubUnderClubCollectionGroup(clubCollectionGroup,theClub);
						}
					}
				}
			}
			
			trans.commit();
		}
		catch (Exception e1) {
			
			e1.printStackTrace();
			try {
				trans.rollback();
			}
			catch (IllegalStateException e) {
				e.printStackTrace();
			}
			catch (SecurityException e) {
				e.printStackTrace();
			}
			catch (SystemException e) {
				e.printStackTrace();
			}	
		}

	}

	private void createAliasToClubUnderClubCollectionGroup(Group clubCollectionGroup, Group club) throws CreateException, RemoteException {
		boolean needToCreateAlias = true;
		String[] aliasType = {IWMemberConstants.GROUP_TYPE_ALIAS};
		Collection clubAliases = clubCollectionGroup.getChildGroups(aliasType,true);
		
		for (Iterator aliasesToClubs = clubAliases.iterator(); aliasesToClubs.hasNext() && needToCreateAlias; ) {
			Group clubAlias = (Group) aliasesToClubs.next();
			if(clubAlias.getAlias().equals(clubCollectionGroup)){
				needToCreateAlias = false;
			}
		}
		
		if(needToCreateAlias){
			Group alias = getGroupBusiness().createGroupUnder(club.getName(),null,IWMemberConstants.GROUP_TYPE_ALIAS,clubCollectionGroup);
			alias.setAlias(club);
			alias.store();
		}
	}

	protected Group addClubCollectionGroupToLeague(Group league) throws RemoteException, CreateException {
		Group clubColl = null;
		try {
			clubColl = getMemberUserBusiness().getClubCollectionGroupForLeague(league);
			return clubColl;
		}
		catch (NoLeagueClubCollectionGroup e) {
			log("No club collection group for league: "+league.getName()+", creating one...");
	
			//TODO change sloppy none localized group name
			clubColl = getGroupBusiness().createGroupUnder("Aðildarfélög",null,IWMemberConstants.GROUP_TYPE_LEAGUE_CLUB_COLLECTION,league);
			Collection leagueOwners = getGroupBusiness().getOwnerUsersForGroup(league);
			for (Iterator owners = leagueOwners.iterator(); owners.hasNext();) {
				User owner = (User) owners.next();
				getGroupBusiness().applyOwnerAndAllGroupPermissionsToNewlyCreatedGroupForUserAndHisPrimaryGroup(clubColl, owner);
			}	
		}
		
		return clubColl;
		
	}

	protected void addLeagueRoleToLeagueStaffAndCommitteeGroups(ICRole role, Group league) {
		AccessController access = getAccessController();
		List staffType = new ArrayList();
		staffType.add(IWMemberConstants.GROUP_TYPE_LEAGUE_STAFF);
		staffType.add(IWMemberConstants.GROUP_TYPE_LEAGUE_COMMITTEE);
		String roleKey = role.getRoleKey();
		
		try {
			Collection staff = getGroupBusiness().getChildGroupsRecursiveResultFiltered(league,staffType,true);
			if(staff!=null && !staff.isEmpty()){
				for (Iterator groups = staff.iterator(); groups.hasNext();) {
					Group group = (Group) groups.next();
					access.addRoleToGroup(roleKey,(Integer)group.getPrimaryKey(),getIWApplicationContext());
					
					//and for the children, especially the main committee
					if(group.getGroupType().equals(IWMemberConstants.GROUP_TYPE_LEAGUE_COMMITTEE)){
						Collection mainCommitteAndPossibleOthers = getGroupBusiness().getChildGroupsRecursive(group);
						if(mainCommitteAndPossibleOthers!=null && !mainCommitteAndPossibleOthers.isEmpty()){
							for (Iterator committs = mainCommitteAndPossibleOthers.iterator(); committs.hasNext();) {
								Group comm = (Group) committs.next();
								access.addRoleToGroup(roleKey,(Integer)comm.getPrimaryKey(),getIWApplicationContext());
							}
						}
					}
						
					
				}
			}
		}
		catch (RemoteException e) {
			e.printStackTrace();
		}
	}
	
	protected void addLeagueRoleAccessToDivision(ICRole role, Group division) {
		AccessController access = getAccessController();
		String roleKey = role.getRoleKey();
		Integer divisionId = (Integer)division.getPrimaryKey();
		//SET PERMISSIONS TO THE CLUB, then get the main board...
		addRoleAccessToGroup(access, roleKey, divisionId);
		
		String committeeGroupId = division.getMetaData(IWMemberConstants.META_DATA_DIVISION_BOARD);
		if(committeeGroupId!=null && !"".equals(committeeGroupId)){
			addRoleAccessToGroup(access, roleKey, new Integer(committeeGroupId));
		}
		
	}

	protected void addLeagueRoleAccessToClub(ICRole role, Group club) {
		AccessController access = getAccessController();
		String roleKey = role.getRoleKey();
		Integer clubId = (Integer)club.getPrimaryKey();
		//SET PERMISSIONS TO THE CLUB, then get the main board...
		addRoleAccessToGroup(access, roleKey, clubId);
		
		List committee = new ArrayList();
		committee.add(IWMemberConstants.GROUP_TYPE_CLUB_COMMITTEE_MAIN);
		
		try {
			//in two steps so
			Collection committees = getGroupBusiness().getChildGroupsRecursiveResultFiltered(club,committee,true);
			if(committees!=null && !committees.isEmpty()){
				for (Iterator groups = committees.iterator(); groups.hasNext();) {
					Group group = (Group) groups.next();
					Integer groupId = (Integer)group.getPrimaryKey();
					addRoleAccessToGroup(access, roleKey, groupId);
				}
			}
		}
		catch (RemoteException e) {
			e.printStackTrace();
		}
	}

	/**
	 * @param access
	 * @param roleKey
	 * @param clubId
	 */
	protected void addRoleAccessToGroup(AccessController access, String roleKey, Integer groupId) {
		access.addRoleToGroup(roleKey,AccessControl.PERMISSION_KEY_VIEW,groupId,this.getIWApplicationContext());
		access.addRoleToGroup(roleKey,AccessControl.PERMISSION_KEY_EDIT,groupId,this.getIWApplicationContext());
	}

	/**
	 * Creates a role with the name "The leagues abbreviation"+"_access";
	 * @param league
	 * @return the created role
	 * @throws NoAbbreviationException
	 */
	protected ICRole createRoleForLeague(Group league) throws NoAbbreviationException{
		String abbreviation = league.getAbbrevation();
		AccessController access = getAccessController();
		
		if(abbreviation!=null && !"".equals(abbreviation) ){
			String role = abbreviation+"_access";
			try {
				return access.getRoleByRoleKey(role);
			}
			catch (FinderException e) {
				return access.createRoleWithRoleKey(role);
			}
		}
		else throw new NoAbbreviationException(league.getName());
	}
	
	
	
	
}