package com.idega.user.presentation;

import java.rmi.RemoteException;
import java.util.Arrays;
import java.util.Collection;
import java.util.Collections;
import java.util.HashMap;
import java.util.Iterator;
import java.util.Map;
import javax.ejb.FinderException;
import javax.swing.event.ChangeEvent;
import com.idega.block.entity.event.EntityBrowserEvent;
import com.idega.core.builder.data.ICDomain;
import com.idega.data.IDOLookup;
import com.idega.data.IDOLookupException;
import com.idega.event.IWActionListener;
import com.idega.event.IWPresentationEvent;
import com.idega.idegaweb.IWApplicationContext;
import com.idega.idegaweb.IWException;
import com.idega.idegaweb.browser.presentation.IWControlFramePresentationState;
import com.idega.presentation.IWContext;
import com.idega.presentation.event.ResetPresentationEvent;
import com.idega.user.block.search.event.UserSearchEvent;
import com.idega.user.business.GroupBusiness;
import com.idega.user.data.Group;
import com.idega.user.data.GroupHome;
import com.idega.user.event.SelectDomainEvent;
import com.idega.user.event.SelectGroupEvent;

/**
 * <p>
 * Title: idegaWeb
 * </p>
 * <p>
 * Description:
 * </p>
 * <p>
 * Copyright: Copyright (c) 2002
 * </p>
 * <p>
 * Company: idega Software
 * </p>
 * 
 * @author <a href="gummi@idega.is">Gu�mundur �g�st S�mundsson </a>
 * @version 1.0
 */

public class BasicUserOverviewPS extends IWControlFramePresentationState
        implements IWActionListener {

    //  String color1 = "00FF00";
    //  String color2 = "FF0000";
    //  String color = color1;
	
	private GroupBusiness business = null;

    protected Group parentGroupOfSelection = null;

    protected ICDomain parentDomainOfSelection = null;

    protected Group _selectedGroup = null;

    protected ICDomain _selectedDomain = null;

    protected boolean showSearchResult = false;

    private Map resultOfMovingUsers = null;

    private int targetGroupId;

    public BasicUserOverviewPS() {
    }

    public Map getResultOfMovingUsers() {
        return resultOfMovingUsers;
    }

    public boolean showSearchResult() {
        return showSearchResult;
    }

    public int getTargetGroupId() {
        return targetGroupId;
    }

    public Group getSelectedGroup() {
        return _selectedGroup;
    }

    public ICDomain getSelectedDomain() {
        return _selectedDomain;
    }

    public void reset() {
        super.reset();
        _selectedGroup = null;
        _selectedDomain = null;
        resultOfMovingUsers = null;

    }

    //  public String getColor(){
    //    return color;
    //  }

    public void actionPerformed(IWPresentationEvent e) throws IWException {



        try {
			if (e instanceof ResetPresentationEvent) {
			    this.reset();
			    this.fireStateChanged();
			    
			}
			
			if (e instanceof UserSearchEvent) {
			    _selectedGroup = null;
			    resultOfMovingUsers = null;
			} 
			

			if (e instanceof SelectGroupEvent) {
			    _selectedGroup = ((SelectGroupEvent) e).getSelectedGroup();
			    _selectedDomain = null;
			    parentGroupOfSelection = ((SelectGroupEvent) e).getParentGroupOfSelection();
			    parentDomainOfSelection = ((SelectGroupEvent) e).getParentDomainOfSelection();
			    resultOfMovingUsers = null;
			    showSearchResult = false;
			    this.fireStateChanged();
			}

			if (e instanceof SelectDomainEvent) {
			    _selectedDomain = ((SelectDomainEvent) e).getSelectedDomain();
			    _selectedGroup = null;
			    resultOfMovingUsers = null;
			    showSearchResult = false;
			    this.fireStateChanged();
			}

			if (e instanceof EntityBrowserEvent) {
			    IWContext mainIwc = e.getIWContext();
			    String[] userIds;
			    if (mainIwc.isParameterSet(BasicUserOverview.DELETE_USERS_KEY) && mainIwc.isParameterSet(BasicUserOverview.SELECTED_USERS_KEY)) {
			        userIds = mainIwc.getParameterValues(BasicUserOverview.SELECTED_USERS_KEY);
			        // delete users (if something has been chosen)

			        if (_selectedGroup.isAlias()) {
			            BasicUserOverview.removeUsers(Arrays.asList(userIds),_selectedGroup.getAlias(), mainIwc);
			        } else {
			            BasicUserOverview.removeUsers(Arrays.asList(userIds),_selectedGroup, mainIwc);
			        }

			    }
			}
			
			if (e instanceof EntityBrowserEvent) {
			    IWContext mainIwc = e.getIWContext();
			    String[] userIds;
			    if ((mainIwc.isParameterSet(BasicUserOverview.MOVE_USERS_KEY) || mainIwc.isParameterSet(BasicUserOverview.COPY_USERS_KEY)) && mainIwc.isParameterSet(BasicUserOverview.SELECTED_USERS_KEY) && mainIwc.isParameterSet(BasicUserOverview.SELECTED_TARGET_GROUP_KEY)) {
			        userIds = mainIwc.getParameterValues(BasicUserOverview.SELECTED_USERS_KEY);

			        String targetGroupNodeString = mainIwc.getParameter(BasicUserOverview.SELECTED_TARGET_GROUP_KEY);
			        //cut it down because it is in the form "domain_id"_"group_id"
			        targetGroupNodeString = targetGroupNodeString.substring(Math.max(targetGroupNodeString.indexOf("_") + 1, 0),targetGroupNodeString.length());
			        int targetGroupId = Integer.parseInt(targetGroupNodeString);
			        
			        business = getGroupBusiness(mainIwc);
			        
			        try {
			        	 //move to the real group not the alias!
						Group target = business.getGroupByGroupID(targetGroupId);
						if(target.isAlias()){
							targetGroupId = target.getAliasID();
						}
				        // move users to a group
				        if (_selectedGroup!=null && _selectedGroup.isAlias()) {
				            resultOfMovingUsers = BasicUserOverview.moveUsers(Arrays.asList(userIds), _selectedGroup.getAlias(),targetGroupId, mainIwc);
				        } else if (mainIwc.isParameterSet(BasicUserOverview.COPY_USERS_KEY)) {
				            resultOfMovingUsers = BasicUserOverview.moveUsers(Arrays.asList(userIds), _selectedGroup.getAlias(),targetGroupId, mainIwc, true);
				        } else {
				            resultOfMovingUsers = BasicUserOverview.moveUsers(Arrays.asList(userIds), _selectedGroup, targetGroupId,mainIwc);
				        }
				       
				        this.targetGroupId = targetGroupId;
			        
			        }
					catch (FinderException e2) {
						e2.printStackTrace();
					}
			    }
			}

			if (e instanceof EntityBrowserEvent && (MassMovingWindow.EVENT_NAME.equals(((EntityBrowserEvent) e).getEventName()))) {
			    IWContext mainIwc = e.getIWContext();
			    String[] groupIds;
			    if (mainIwc.isParameterSet(MassMovingWindow.SELECTED_CHECKED_GROUPS_KEY) && mainIwc.isParameterSet(MassMovingWindow.MOVE_SELECTED_GROUPS)) {
			        groupIds = mainIwc.getParameterValues(MassMovingWindow.SELECTED_CHECKED_GROUPS_KEY);
			        String parentGroupType = mainIwc.getParameter(MassMovingWindow.PRM_PARENT_GROUP_TYPE);
			        
			        try {
						GroupHome grHome = (GroupHome)IDOLookup.getHome(Group.class);
						Collection groupCollection = grHome.findByPrimaryKeyCollection(grHome.decode(groupIds));
						Collection groupTypes = Collections.singleton(MassMovingWindow.GROUP_TYPE_CLUB_PLAYER);
						
						// move users
						if(parentGroupType.equals(MassMovingWindow.GROUP_TYPE_CLUB_DIVISION)){
							resultOfMovingUsers = BasicUserOverview.moveContentOfGroups(groupCollection,groupTypes, mainIwc);
						} else {
							resultOfMovingUsers = new HashMap();
							for (Iterator iter = groupCollection.iterator(); iter.hasNext();) {
								Group divGroups = (Group) iter.next();
								resultOfMovingUsers.putAll(BasicUserOverview.moveContentOfGroups(Collections.singleton(divGroups),groupTypes, mainIwc));
							}
							
						}
					} catch (IDOLookupException e1) {
						e1.printStackTrace();
					} catch (FinderException e1) {
						e1.printStackTrace();
					}
			        targetGroupId = -1;
			        fireStateChanged();
			    }
			}
		}
		catch (RemoteException e1) {
			//something really bad happened
			e1.printStackTrace();
		}
    }

    /**
     * Returns the parentDomainOfSelection.
     * 
     * @return IBDomain
     */
    public ICDomain getParentDomainOfSelection() {
        return parentDomainOfSelection;
    }

    /**
     * Returns the parentGroupOfSelection.
     * 
     * @return Group
     */
    public Group getParentGroupOfSelection() {
        return parentGroupOfSelection;
    }

    public void stateChanged(ChangeEvent e) {
        Object object = e.getSource();
        if (object instanceof DeleteGroupConfirmWindowPS) {
            // selected group was successfully(!) removed
            // set selected group to null
            _selectedGroup = null;
        }
    }
    
    public GroupBusiness getGroupBusiness(IWApplicationContext iwc) {
        if (business == null) {
            try {
                business = (GroupBusiness) com.idega.business.IBOLookup.getServiceInstance(iwc, GroupBusiness.class);
            }
            catch (java.rmi.RemoteException rme) {
                throw new RuntimeException(rme.getMessage());
            }
        }
        return business;
    }

}