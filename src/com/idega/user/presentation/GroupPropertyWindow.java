package com.idega.user.presentation;

import java.rmi.RemoteException;
import java.util.Collection;
import java.util.Iterator;
import java.util.List;

import com.idega.idegaweb.IWApplicationContext;
import com.idega.idegaweb.IWConstants;
import com.idega.idegaweb.IWResourceBundle;
import com.idega.presentation.IWContext;
import com.idega.presentation.PresentationObject;
import com.idega.presentation.TabbedPropertyPanel;
import com.idega.presentation.TabbedPropertyWindow;
import com.idega.user.business.GroupBusiness;
import com.idega.user.business.UserGroupPlugInBusiness;
import com.idega.user.data.Group;
import com.idega.user.data.UserGroupPlugIn;

/**
 * Title: User Description: Copyright: Copyright (c) 2001 Company: idega.is
 * 
 * @author 2000 - idega team - <a href="mailto:gummi@idega.is">Gu�mundur
 *         �g�st S�mundsson </a>
 * @version 1.0
 */

public class GroupPropertyWindow extends TabbedPropertyWindow {

    public static final String PARAMETERSTRING_GROUP_ID = "ic_group_id";
    public static final String PARENT_GROUP_ID_KEY = "parent_group_id";
    public static final String SESSION_ADDRESS = "ic_group_property_window";
    private final int height = 600;
    private final int width = 500;

    public GroupPropertyWindow() {
        super();
        //      setBackgroundColor(new IWColor(207, 208, 210));
        setWidth(width);
        setHeight(height);
        setResizable(true);
    }

    /**
     * @see com.idega.presentation.TabbedPropertyWindow#disposeOfPanel(com.idega.presentation.IWContext)
     */
    public boolean disposeOfPanel(IWContext iwc) {
        return iwc.isParameterSet(PARAMETERSTRING_GROUP_ID);
    }

    public GroupBusiness getGroupBusiness(IWApplicationContext iwac)
            throws RemoteException {
        return (GroupBusiness) com.idega.business.IBOLookup.getServiceInstance(
                iwac, GroupBusiness.class);
    }

    public String getSessionAddressString() {
        return SESSION_ADDRESS;
    }

    public void initializePanel(IWContext iwc, TabbedPropertyPanel panel) {
        try {
            int count = 0;
            panel.addTab(new GeneralGroupInfoTab(), count++, iwc);

            //	temp shit
            String id = iwc.getParameter(PARAMETERSTRING_GROUP_ID);
            int groupId = Integer.parseInt(id);
            Group group = getGroupBusiness(iwc).getGroupByGroupID(groupId);

            Collection plugins = getGroupBusiness(iwc)
                    .getUserGroupPluginsForGroupTypeString(group.getGroupType());
            Iterator iter = plugins.iterator();

            while (iter.hasNext()) {
                UserGroupPlugIn element = (UserGroupPlugIn) iter.next();

                UserGroupPlugInBusiness pluginBiz = (UserGroupPlugInBusiness) com.idega.business.IBOLookup
                        .getServiceInstance(iwc, Class.forName(element
                                .getBusinessICObject().getClassName()));

                List tabs = pluginBiz.getGroupPropertiesTabs(group);

                if (tabs != null) {
                    Iterator tab = tabs.iterator();
                    while (tab.hasNext()) {
                        UserGroupTab el = (UserGroupTab) tab.next();
                        panel.addTab(el, count++, iwc);
                    }
                }
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public void main(IWContext iwc) throws Exception {
        IWResourceBundle iwrb = getResourceBundle(iwc);
        addTitle(iwrb.getLocalizedString("group_property_window",
                "Group Property Window"), IWConstants.BUILDER_FONT_STYLE_TITLE);
        int parentGroupId = -1;
        if (iwc.isParameterSet(PARENT_GROUP_ID_KEY))
                parentGroupId = Integer.parseInt(iwc
                        .getParameter(PARENT_GROUP_ID_KEY));
        String id = iwc
                .getParameter(GroupPropertyWindow.PARAMETERSTRING_GROUP_ID);
        if (id != null) {
            int newId = Integer.parseInt(id);
            PresentationObject[] obj = this.getAddedTabs();
            for (int i = 0; i < obj.length; i++) {
                PresentationObject mo = obj[i];
                if (mo instanceof UserGroupTab
                        && ((UserGroupTab) mo).getGroupId() != newId) {
                    ((UserGroupTab) mo).setGroupIds(newId, parentGroupId);
                }
            }
        }
    }


	/**
	 * overrides the default behaviour to check for edit permissions
	 */
	protected TabbedPropertyPanel getPanelInstance(IWContext iwc) {
        boolean useOkButton = false;
        boolean useApplyButton = false;
        boolean useCancelButton = true;
        
//		check if we have edit permissions, otherwise disable saving
		String groupId = iwc.getParameter(GroupPropertyWindow.PARAMETERSTRING_GROUP_ID);
		
		boolean isAdmin = iwc.isSuperAdmin();
		if(groupId!=null && !"-1".equals(groupId) && !isAdmin) {
		    try {
                useOkButton = iwc.getAccessController().hasEditPermissionFor(getGroupBusiness(iwc).getGroupByGroupID(Integer.parseInt(groupId)), iwc);
            } catch (Exception e) {
                e.printStackTrace();
            }
            
		    useApplyButton = useOkButton;
		}
		else {
		    if(!isAdmin) {//only super admin can edit without permission
		        useOkButton = false;
		        useApplyButton = false;
		    }
		}
		
		
		return new TabbedPropertyPanel(getSessionAddressString(),iwc,useOkButton,useCancelButton,useApplyButton);
		   
    }

}