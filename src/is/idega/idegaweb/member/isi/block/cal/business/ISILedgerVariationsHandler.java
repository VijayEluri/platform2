/*
 * Created on Apr 6, 2004
 */
package is.idega.idegaweb.member.isi.block.cal.business;

import is.idega.idegaweb.member.business.MemberFamilyLogic;
import is.idega.idegaweb.member.util.IWMemberConstants;

import java.rmi.RemoteException;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Iterator;

import com.idega.block.cal.business.CalBusiness;
import com.idega.block.cal.business.LedgerVariationsHandler;
import com.idega.idegaweb.IWApplicationContext;
import com.idega.idegaweb.IWResourceBundle;
import com.idega.presentation.IWContext;
import com.idega.presentation.Page;
import com.idega.presentation.PresentationObject;
import com.idega.user.business.GroupBusiness;
import com.idega.user.data.Group;
import com.idega.user.data.GroupRelation;
import com.idega.user.data.User;

/**
 * Description: <br>
 * Copyright: Idega Software 2004 <br>
 * Company: Idega Software <br>
 * @author <a href="mailto:birna@idega.is">Birna Iris Jonsdottir</a>
 */
public class ISILedgerVariationsHandler extends PresentationObject implements LedgerVariationsHandler{
	
	public String getParentGroupName(Collection parentGroups) {
			
		Iterator pgIter = parentGroups.iterator();
		while(pgIter.hasNext()) {
			Group g = (Group) pgIter.next();
			String type = g.getGroupType();
			if(IWMemberConstants.GROUP_TYPE_CLUB_DIVISION.equals(type)) {
				return g.getName();
			}
		}
		return null;
	}
	public String getParentOfParentGroupName(Collection parentGroups) {
		Iterator pgIter = parentGroups.iterator();
		while(pgIter.hasNext()) {
			Group g = (Group) pgIter.next();
			String type = g.getGroupType();
			if(IWMemberConstants.GROUP_TYPE_CLUB.equals(type)) {
				return g.getName();
			}
		}
		return null;
	}
	
	public void saveLedger(IWContext iwc, Page parentPage, String name, int groupID, String coachName, int coachGroupID, String date) {
		IWResourceBundle iwrb = getResourceBundle(iwc);
		CalBusiness calBiz = getCalBusiness(iwc);		
		GroupBusiness grBiz =getGroupBusiness(iwc);
		Collection playerGroups = null;
		Group g = null;

		try {
			g = grBiz.getGroupByGroupID(groupID);			
		}catch (Exception e){
			e.printStackTrace();
		}		

		Collection groupTypes = new ArrayList();
		groupTypes.add(IWMemberConstants.GROUP_TYPE_CLUB_PLAYER);
		groupTypes.add(IWMemberConstants.GROUP_TYPE_TEMPORARY);
		groupTypes.add(IWMemberConstants.GROUP_TYPE_GENERAL);
		
		String[] groupTypeDivision = { IWMemberConstants.GROUP_TYPE_CLUB_DIVISION };
		

		if( g!= null) {
			String groupType = g.getGroupType();
			if(groupType.equals(IWMemberConstants.GROUP_TYPE_CLUB_DIVISION) || groupType.equals(IWMemberConstants.GROUP_TYPE_CLUB)) {
				String abbrevation = null;
				if(groupType.equals(IWMemberConstants.GROUP_TYPE_CLUB_DIVISION)) {
					abbrevation = g.getAbbrevation();
					System.out.println("abbrevation: " + g.getAbbrevation());
				}
				try {
					playerGroups = grBiz.getChildGroupsRecursiveResultFiltered(g,groupTypes,true);
				}catch (Exception e) {
					e.printStackTrace();
				}			
				if(playerGroups != null) {
					Iterator playersGroupIter = playerGroups.iterator();
					while(playersGroupIter.hasNext()) {
						Group group = (Group) playersGroupIter.next();
						Collection parentDiv = null;
						Group parentDivision = null;
						try{
							parentDiv = grBiz.getParentGroupsRecursive(group,groupTypeDivision,true);
						}catch(RemoteException re) {
							
						}
						String n = name;
						if(n == null || n.equals("")) {
							n = group.getName();
						}
						Integer grID = (Integer) group.getPrimaryKey();
						calBiz.createNewLedger(n,grID.intValue(),coachName,date,coachGroupID);
					}
					parentPage.close();
					parentPage.setOnLoad("window.opener.parent.location.reload()");
				}
			}			
			else if(groupType.equals(IWMemberConstants.GROUP_TYPE_CLUB_PLAYER) ||
					groupType.equals(IWMemberConstants.GROUP_TYPE_TEMPORARY) ||
					groupType.equals(IWMemberConstants.GROUP_TYPE_GENERAL)){	
				if(name == null || name.equals("")) {
					name = g.getName();
				}
				calBiz.createNewLedger(name,groupID,coachName,date,coachGroupID);
				parentPage.close();
				parentPage.setOnLoad("window.opener.parent.location.reload()");				
			}	
			else {
				parentPage.setAlertOnLoad(iwrb.getLocalizedString("ledgerwindow.choose_div_or_group_errormessage","The chosen group is neither division nor players group!"));
			}
		}		
	}
	
	public Collection getParentGroupRelation(IWContext iwc, User user) {
		GroupRelation groupRelation = null;
		Collection parents =null;
		try {
			parents = getMemberFamilyLogic(iwc).getParentsFor(user);
			System.out.println("parents: " + parents.toString());
		}catch(Exception e) {
			e.printStackTrace();
		}		
		return parents;
	}
	public String getParentKey() {
		String parentKey = null;
		parentKey = GroupRelation.class.getName() + ".IC_GROUP_RELATION_ID|RELATED_IC_GROUP_ID";
		return parentKey;
	}
	public MemberFamilyLogic getMemberFamilyLogic(IWApplicationContext iwc) {
		MemberFamilyLogic famLog = null;
		if(famLog ==null) {
			try {
				famLog = (MemberFamilyLogic) com.idega.business.IBOLookup.getServiceInstance(iwc, MemberFamilyLogic.class);
			} catch (java.rmi.RemoteException rme) {
				throw new RuntimeException(rme.getMessage());
			}
		}
		return famLog;
	}

			
	
	
	
	public CalBusiness getCalBusiness(IWApplicationContext iwc) {
		CalBusiness calBiz = null;
		if (calBiz == null) {
			try {
				calBiz = (CalBusiness) com.idega.business.IBOLookup.getServiceInstance(iwc, CalBusiness.class);
			}
			catch (java.rmi.RemoteException rme) {
				throw new RuntimeException(rme.getMessage());
			}
		}
		return calBiz;
	}
	public GroupBusiness getGroupBusiness(IWApplicationContext iwc) {
		GroupBusiness groupBiz =null;
		if (groupBiz == null) {
			try {
				groupBiz = (GroupBusiness) com.idega.business.IBOLookup.getServiceInstance(iwc, GroupBusiness.class);
			}
			catch (java.rmi.RemoteException rme) {
				throw new RuntimeException(rme.getMessage());
			}
		}
		return groupBiz;
	}
	

}
