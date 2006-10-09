package com.idega.user.presentation;

import java.rmi.RemoteException;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.Iterator;
import com.idega.event.IWLinkEvent;
import com.idega.event.IWLinkListener;
import com.idega.idegaweb.IWBundle;
import com.idega.idegaweb.IWResourceBundle;
import com.idega.idegaweb.help.presentation.Help;
import com.idega.presentation.ExceptionWrapper;
import com.idega.presentation.IWContext;
import com.idega.presentation.Image;
import com.idega.presentation.Table;
import com.idega.presentation.text.Link;
import com.idega.presentation.text.Text;
import com.idega.presentation.ui.DropdownMenu;
import com.idega.presentation.ui.IFrame;
import com.idega.user.business.UserBusiness;
import com.idega.user.data.Group;
import com.idega.user.data.User;
import com.idega.util.Disposable;

/**
 * Title: User Description: Copyright: Copyright (c) 2001 Company: idega.is
 * 
 * @author 2000 - idega team - <a href="mailto:gummi@idega.is">Gudmundur Agust Saemundsson</a>
 * @version 1.0
 */
public class UserGroupList extends UserTab implements Disposable, IWLinkListener {

	private static final String IW_BUNDLE_IDENTIFIER = "com.idega.user";

	private static final String TAB_NAME = "usr_grp_tab_name";

	private static final String DEFAULT_TAB_NAME = "Groups";

	private static final String HELP_TEXT_KEY = "user_group_list";

	private Link addLink;

	private IFrame memberofFrame;

	private DropdownMenu primaryGroupField;

	private String primaryGroupFieldName;

	private Text primaryGroupText;

	public static final String PARAMETER_USER_ID = "ic_user_id";

	public static final String SESSIONADDRESS_USERGROUPS_DIRECTLY_RELATED = "ic_user_ic_group_direct_UGL";

	public static final String SESSIONADDRESS_USERGROUPS_NOT_DIRECTLY_RELATED = "ic_user_ic_group_not_direct_UGL";

	protected Text memberof;

	public UserGroupList() {
		super();
		IWContext iwc = IWContext.getInstance();
		IWResourceBundle iwrb = getResourceBundle(iwc);
		setName(iwrb.getLocalizedString(TAB_NAME, DEFAULT_TAB_NAME));
		//		this.setName("Groups");
	}

	public void initFieldContents() {
	    IWContext iwc = IWContext.getInstance();
		if (iwc.isSuperAdmin()) {
		    addLink.setWindowToOpen(UserGroupSetter.class);
			addLink.addParameter(UserGroupList.PARAMETER_USER_ID, this.getUserId());
		}
		try {
			UserBusiness userBusiness = this.getUserBusiness(this.getEventIWContext());
			Collection userGroups = userBusiness.getUserGroupsDirectlyRelated(this.getUserId());
			if ((userGroups != null) && !userGroups.isEmpty()) {
				Iterator iter = userGroups.iterator();
				while (iter.hasNext()) {
					Group item = (Group) iter.next();
					if (item == null)
						System.out.println("ITEM IS NULL!!!WHY? Temporary check. please fix this, that means you Tryggvi");
					else {
						Object prim = item.getPrimaryKey();
						String groupId = prim.toString();
						String groupName = item.getName();
						if (groupName == null)
							groupName = "untitled";
						primaryGroupField.addMenuElement(groupId, groupName);
					}
				}
			}
			//User user =
			// ((com.idega.user.data.UserHome)com.idega.data.IDOLookup.getHomeLegacy(User.class)).findByPrimaryKeyLegacy(this.getUserId());
			User user = getUser();
			int prgroupid = user.getPrimaryGroupID();
			fieldValues.put(primaryGroupFieldName, (prgroupid != -1) ? Integer.toString(prgroupid) : "");
		}
		catch (Exception ex) {
			add(new ExceptionWrapper(ex, this));
			ex.printStackTrace();
		}
		updateFieldsDisplayStatus();
	}

	public void updateFieldsDisplayStatus() {
		primaryGroupField.setSelectedElement((String) fieldValues.get(primaryGroupFieldName));
	}

	public void initializeFields() {
		memberofFrame = new IFrame("ic_user_memberof_ic_group", GroupList.class);
		memberofFrame.setHeight(280);
		memberofFrame.setWidth(Table.HUNDRED_PERCENT);
		memberofFrame.setScrolling(IFrame.SCROLLING_YES);
		memberofFrame.setStyleAttribute("border", "1px #bbbbbb solid;");

		primaryGroupField = new DropdownMenu(primaryGroupFieldName);
		primaryGroupField.keepStatusOnAction();
		IWContext iwc = IWContext.getInstance();
		IWResourceBundle iwrb = getResourceBundle(iwc);
		if (iwc.isSuperAdmin()) {
			addLink = new Link("  " + iwrb.getLocalizedString("addRemove", "Add/Remove") + "  ");
			addLink.setStyleClass("styledLink");
		}
	}

	public void actionPerformed(IWLinkEvent e) {
		this.collect(e.getIWContext());
	}

	public void initializeTexts() {
		IWContext iwc = IWContext.getInstance();
		IWResourceBundle iwrb = getResourceBundle(iwc);

		memberof = new Text(iwrb.getLocalizedString("usr_grp_memberof","Member of") + ":");
		memberof.setBold();

		primaryGroupText = new Text(iwrb.getLocalizedString(primaryGroupFieldName,"Primarygroup") + ":");
		primaryGroupText.setBold();
	}

	public boolean store(IWContext iwc) {
		try {
			String pr = (String) this.fieldValues.get(this.primaryGroupFieldName);
			UserBusiness userBusiness = this.getUserBusiness(iwc);
			User user = getUser();
			userBusiness.setPermissionGroup(user, ("".equals(pr)) ? null : new Integer(pr));
			return true;
		}
		catch (Exception ex) {
			return false;
		}
	}

	public Help getHelpButton() {
		IWContext iwc = IWContext.getInstance();
		IWBundle iwb = getBundle(iwc);
		Help help = new Help();
		Image helpImage = iwb.getImage("help.gif");
		help.setHelpTextBundle(UserConstants.HELP_BUNDLE_IDENTFIER);
		help.setHelpTextKey(HELP_TEXT_KEY);
		help.setImage(helpImage);
		return help;
	}

	public void lineUpFields() {
		this.resize(1, 1);

		Table table = new Table(1, 3);
		table.setCellspacing(0);
		table.setCellpadding(5);
		table.setWidth(Table.HUNDRED_PERCENT);
		table.setBorder(0);
		
		Table prTable = new Table(3, 1);
		prTable.setCellpaddingAndCellspacing(0);
		prTable.setWidth(2, 3);
		prTable.add(this.primaryGroupText, 1, 1);
		prTable.add(this.primaryGroupField, 3, 1);
		table.add(prTable, 1, 1);

		table.add(memberof, 1, 2);
		table.add(Text.getBreak(), 1, 2);
		table.add(memberofFrame, 1, 2);
		if (addLink != null) {
		    table.add(addLink, 1, 3);
		}
		add(table, 1, 1);
	}

	public boolean collect(IWContext iwc) {
		String prgroup = iwc.getParameter(primaryGroupFieldName);
		if (prgroup != null) {
			fieldValues.put(primaryGroupFieldName, prgroup);
		}
		return true;
	}

	public void initializeFieldNames() {
		this.primaryGroupFieldName = "primary_group";
	}

	public void initializeFieldValues() {
		fieldValues.put(this.primaryGroupFieldName, "");
		this.updateFieldsDisplayStatus();
	}

	public void dispose(IWContext iwc) {
		iwc.removeSessionAttribute(UserGroupList.SESSIONADDRESS_USERGROUPS_DIRECTLY_RELATED);
		iwc.removeSessionAttribute(UserGroupList.SESSIONADDRESS_USERGROUPS_NOT_DIRECTLY_RELATED);
	}

	public void main(IWContext iwc) throws Exception {
		getPanel().addHelpButton(getHelpButton());	

		primaryGroupField.removeElements();
		primaryGroupField.addSeparator();
		UserBusiness userBusiness = this.getUserBusiness(iwc);
		Collection userGroups = Collections.unmodifiableCollection(userBusiness.getUserGroupsDirectlyRelated(this.getUserId()));

		User user = getUser();
		Collection directGroups = userGroups;
		Collection topGroupNodes = userBusiness.getUsersTopGroupNodesByViewAndOwnerPermissions(iwc.getCurrentUser(),iwc);
		directGroups = getFilteredGroups(iwc, directGroups, user,topGroupNodes);
		if (directGroups != null) {
			iwc.setSessionAttribute(UserGroupList.SESSIONADDRESS_USERGROUPS_DIRECTLY_RELATED, directGroups);
		}
		else {
			iwc.removeSessionAttribute(UserGroupList.SESSIONADDRESS_USERGROUPS_DIRECTLY_RELATED);
		}
		Collection indirects = userBusiness.getParentGroupsInDirectForUser(this.getUserId());
		Collection indirectGroups = null;
		if(indirects!=null){
		    indirectGroups = Collections.unmodifiableCollection(indirects);
		    indirectGroups = getFilteredGroups(iwc, indirectGroups, user,topGroupNodes);
		}

		if (indirectGroups != null) {
			iwc.setSessionAttribute(UserGroupList.SESSIONADDRESS_USERGROUPS_NOT_DIRECTLY_RELATED, indirectGroups);
		}
		else {
			iwc.removeSessionAttribute(UserGroupList.SESSIONADDRESS_USERGROUPS_NOT_DIRECTLY_RELATED);
		}
		if (directGroups != null) {
			Iterator iter = directGroups.iterator();
			while (iter.hasNext()) {
				Group item = (Group) iter.next();
				if (item == null)
					System.out.println("ITEM IS NULL!!!WHY? Temporary check. please fix this, that means you Tryggvi");
				else {
					Object prim = item.getPrimaryKey();
					String groupId = prim.toString();
					String groupName = item.getName();
					if (groupName == null)
						groupName = "untitled";
					primaryGroupField.addMenuElement(groupId, groupName);
				}
			}
		}
		primaryGroupField.setSelectedElement((String) fieldValues.get(primaryGroupFieldName));
	}

	private Collection getFilteredGroups(IWContext iwc, Collection groups, User user, Collection topGroupNodes) {
		boolean isAdmin = iwc.isSuperAdmin();
		boolean isSameUser = iwc.getUser().getPrimaryKey().equals(user.getPrimaryKey());
		if (isAdmin || isSameUser) {
			return groups;
		}
		Collection result = new ArrayList();
		UserBusiness userBusiness = this.getUserBusiness(iwc);
		Iterator groupIter = groups.iterator();
		User currentUser = iwc.getCurrentUser();
		while (groupIter.hasNext()) {
			Group group = (Group) groupIter.next();
			boolean ok = false;

			ok = iwc.getAccessController().hasViewPermissionFor(group, iwc);

			if (ok) {
				result.add(group);
			}
			else {
				System.out.println("Group " + group.getName() + " not shown");
			}
		}
		return result;
	}

	/**
	 * Checks if a user is allowed to see membership of a certain user for a
	 * certain group.
	 * 
	 * @param viewer
	 *            The user veiwing membership
	 * @param group
	 *            The group being veiwed for membership
	 * @param user
	 *            The user being veiwed for membership
	 * @return true if <code>viewer</code> is allowed to see <code>user</code>
	 *         s membership in <code>group</code>
	 */
	public boolean isUserAllowedToSeeGroupMembershipForUser(User viewer, Group group, User user) {
		/*
		 * boolean ok = true; boolean isSameUser =
		 * viewer.getPrimaryKey().toString()!=user.getPrimaryKey().toString();
		 * boolean viewerIsAdmin = false; try { viewerIsAdmin =
		 * viewer.equals(.getAdministratorUser()); } catch (Exception e) {
		 * e.printStackTrace(); } if(!(isSameUser || viewerIsAdmin)) { Group
		 * topLevelGroup = viewer.getPrimaryGroup();
		 *  }
		 */
		return true;
	}

	//  public class GroupList extends Page {
	//
	//    private List groups = null;
	//
	//    public GroupList(){
	//      super();
	//    }
	//
	//    public Table getGroupTable(IWContext iwc){
	//
	//      List direct =
	// (List)iwc.getSessionAttribute(UserGroupList.SESSIONADDRESS_USERGROUPS_DIRECTLY_RELATED);
	//      List notDirect =
	// (List)iwc.getSessionAttribute(UserGroupList.SESSIONADDRESS_USERGROUPS_NOT_DIRECTLY_RELATED);
	//
	//      Table table = null;
	//        try{
	//        Iterator iter = null;
	//        int row = 1;
	//        if(direct != null && notDirect != null){
	//          table = new Table(5,direct.size()+notDirect.size());
	//
	//          iter = direct.iterator();
	//          while (iter.hasNext()) {
	//            Object item = iter.next();
	//            table.add("D",1,row);
	//            table.add(((Group)item).getName(),3,row++);
	//          }
	//
	//          iter = notDirect.iterator();
	//          while (iter.hasNext()) {
	//            Object item = iter.next();
	//            table.add("E",1,row);
	//            table.add(((Group)item).getName(),3,row++);
	//          }
	//
	//        } else if(direct != null){
	//          table = new Table(5,direct.size());
	//          iter = direct.iterator();
	//          while (iter.hasNext()) {
	//            Object item = iter.next();
	//            table.add("D",1,row);
	//            table.add(((Group)item).getName(),3,row++);
	//          }
	//        }
	//      }
	//      catch(Exception e){
	//        add("Error: "+e.getMessage());
	//        e.printStackTrace();
	//      }
	//
	//      if(table != null){
	//        table.setWidth("100%");
	//        table.setWidth(1,"10");
	//        table.setWidth(2,"3");
	//        table.setWidth(4,"10");
	//        table.setWidth(5,"10");
	//      }
	//
	//
	//
	//      return table;
	//    }
	//
	//    public void main(IWContext iwc) throws Exception {
	//      this.getParentPage().setAllMargins(0);
	//      Table tb = getGroupTable(iwc);
	//      if(tb != null){
	//        this.add(tb);
	//      }
	//    }
	//
	//
	//
	//  } // InnerClass
	//  public class UserGroupSetter extends Window {
	//
	//    private static final String FIELDNAME_SELECTION_DOUBLE_BOX =
	// "related_groups";
	//
	//    public UserGroupSetter(){
	//      super("add user to groups");
	//      this.setAllMargins(0);
	//      this.setWidth(400);
	//      this.setHeight(300);
	//      this.setBackgroundColor("#d4d0c8");
	//    }
	//
	//
	//    private void LineUpElements(IWContext iwc){
	//
	//      Form form = new Form();
	//        try{
	//        UserBusiness userBusiness = getUserBusiness(iwc);
	//
	//        Table frameTable = new Table(3,3);
	//        frameTable.setWidth("100%");
	//        frameTable.setHeight("100%");
	//        //frameTable.setBorder(1);
	//
	//
	//        SelectionDoubleBox sdb = new
	// SelectionDoubleBox(FIELDNAME_SELECTION_DOUBLE_BOX,"Not in","In");
	//
	//        SelectionBox left = sdb.getLeftBox();
	//        left.setHeight(8);
	//        left.selectAllOnSubmit();
	//
	//
	//        SelectionBox right = sdb.getRightBox();
	//        right.setHeight(8);
	//        right.selectAllOnSubmit();
	//
	//
	//
	//        String stringUserId = iwc.getParameter(UserGroupList.PARAMETER_USER_ID);
	//        int userId = Integer.parseInt(stringUserId);
	//        form.addParameter(UserGroupList.PARAMETER_USER_ID,stringUserId);
	//
	//        Collection directGroups =
	// userBusiness.getUserGroupsDirectlyRelated(userId);
	//
	//        Iterator iter = null;
	//        if(directGroups != null){
	//          iter = directGroups.iterator();
	//          while (iter.hasNext()) {
	//            Object item = iter.next();
	//            right.addElement(((Group)item).getPrimaryKey().toString(),((Group)item).getName());
	//          }
	//        }
	//        Collection notDirectGroups =
	// userBusiness.getAllGroupsNotDirectlyRelated(userId,iwc);
	//        if(notDirectGroups != null){
	//          iter = notDirectGroups.iterator();
	//          while (iter.hasNext()) {
	//            Object item = iter.next();
	//            left.addElement(((Group)item).getPrimaryKey().toString(),((Group)item).getName());
	//          }
	//        }
	//
	//
	//        frameTable.setAlignment(2,2,"center");
	//        frameTable.add("UserId: "+userId,2,1);
	//        frameTable.add(sdb,2,2);
	//        frameTable.add(new SubmitButton(" Save ","save","true"),2,3);
	//        frameTable.setAlignment(2,3,"right");
	//        form.add(frameTable);
	//      }
	//      catch(Exception e){
	//        add(new ExceptionWrapper(e,this));
	//        e.printStackTrace();
	//      }
	//      this.add(form);
	//    }
	//
	//    public void main(IWContext iwc) throws Exception {
	//
	//      UserBusiness userBusiness = getUserBusiness(iwc);
	//      String save = iwc.getParameter("save");
	//      if(save != null){
	//        String stringUserId = iwc.getParameter(UserGroupList.PARAMETER_USER_ID);
	//        int userId = Integer.parseInt(stringUserId);
	//
	//        String[] related =
	// iwc.getParameterValues(UserGroupSetter.FIELDNAME_SELECTION_DOUBLE_BOX);
	//
	//        //User user =
	// ((com.idega.user.data.UserHome)com.idega.data.IDOLookup.getHomeLegacy(User.class)).findByPrimaryKeyLegacy(userId);
	//        User user = userBusiness.getUser(userId);
	//        Collection currentRelationShip =
	// userBusiness.getUserGroupsDirectlyRelated(user);
	//
	//
	//        if(related != null){
	//
	//          if(currentRelationShip != null){
	//            for (int i = 0; i < related.length; i++) {
	//              int id = Integer.parseInt(related[i]);
	//              //Group gr =
	// ((com.idega.user.data.GroupHome)com.idega.data.IDOLookup.getHomeLegacy(Group.class)).findByPrimaryKeyLegacy(id);
	//              Group gr = userBusiness.getGroupHome().findByPrimaryKey(new Integer(id));
	//              if(!currentRelationShip.remove(gr)){
	//                //user.addTo(gr);
	//                gr.addUser(user);
	//              }
	//            }
	//
	//            Iterator iter = currentRelationShip.iterator();
	//            while (iter.hasNext()) {
	//              Object item = iter.next();
	//              //user.removeFrom((Group)item);
	//              ((Group)item).removeUser(user);
	//            }
	//
	//          } else{
	//            for (int i = 0; i < related.length; i++) {
	//              //user.addTo(Group.class,Integer.parseInt(related[i]));
	//              //((com.idega.user.data.GroupHome)com.idega.data.IDOLookup.getHomeLegacy(Group.class)).findByPrimaryKeyLegacy(Integer.parseInt(related[i])).addUser(user);
	//              //com.idega.user.data.GroupBMPBean.addUser(Integer.parseInt(related[i]),user);
	//              Group gr = userBusiness.getGroupHome().findByPrimaryKey(new
	// Integer(related[i]));
	//              gr.addUser(user);
	//            }
	//          }
	//
	//        }else if (currentRelationShip != null){
	//            Iterator iter = currentRelationShip.iterator();
	//            while (iter.hasNext()) {
	//              Object item = iter.next();
	//              ((Group)item).removeUser(user);
	//            }
	//          }
	//
	//        this.close();
	//        this.setParentToReload();
	//      } else {
	//        LineUpElements(iwc);
	//      }
	//
	///*
	//      Enumeration enum = iwc.getParameterNames();
	//       System.err.println("--------------------------------------------------");
	//      if(enum != null){
	//        while (enum.hasMoreElements()) {
	//          Object item = enum.nextElement();
	//          if(item.equals("save")){
	//            this.close();
	//          }
	//          String val[] = iwc.getParameterValues((String)item);
	//          System.err.print(item+" = ");
	//          if(val != null){
	//            for (int i = 0; i < val.length; i++) {
	//              System.err.print(val[i]+", ");
	//            }
	//          }
	//          System.err.println();
	//        }
	//      }
	//*/
	//    }
	//
	//  } // InnerClass
	public String getBundleIdentifier() {
		return IW_BUNDLE_IDENTIFIER;
	}
} // Class
