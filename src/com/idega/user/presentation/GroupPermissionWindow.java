package com.idega.user.presentation;

import java.rmi.RemoteException;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;

import javax.ejb.FinderException;

import com.idega.block.entity.business.EntityToPresentationObjectConverter;
import com.idega.block.entity.data.EntityPath;
import com.idega.block.entity.presentation.EntityBrowser;
import com.idega.block.help.presentation.Help;
import com.idega.business.IBOLookup;
import com.idega.core.accesscontrol.business.AccessControl;
import com.idega.core.accesscontrol.business.AccessController;
import com.idega.core.accesscontrol.data.ICPermission;
import com.idega.event.IWPresentationState;
import com.idega.idegaweb.IWConstants;
import com.idega.idegaweb.IWResourceBundle;
import com.idega.idegaweb.IWUserContext;
import com.idega.presentation.IWContext;
import com.idega.presentation.PresentationObject;
import com.idega.presentation.StatefullPresentationImplHandler;
import com.idega.presentation.Table;
import com.idega.presentation.text.Link;
import com.idega.presentation.text.Text;
import com.idega.presentation.ui.CheckBox;
import com.idega.presentation.ui.Form;
import com.idega.presentation.ui.HiddenInput;
import com.idega.presentation.ui.SubmitButton;
import com.idega.user.business.GroupBusiness;
import com.idega.user.business.GroupComparator;
import com.idega.user.data.Group;
import com.idega.user.event.SelectGroupEvent;
import com.idega.util.IWColor;

/**
 * Description: An editor window for the selected groups permissions.<br>
 * The diplayed list of groups contains the groups the selected group has<br>
 * permissions to and then the currentUsers owned groups.<br>
 * The selected groups permission groups will be disabled if the currentUser<br>
 * does not own them.<br> 
 * Company: Idega Software<br>
 * Copyright: Idega Software 2003<br>
 * @author <a href="mailto:eiki@idega.is">Eirikur S. Hrafnsson</a>
 * 
 */
public class GroupPermissionWindow extends StyledIWAdminWindow {//implements StatefullPresentation{
	
	private static final String IW_BUNDLE_IDENTIFIER  = "com.idega.user";
	private static final String PARAM_SELECTED_GROUP_ID  = SelectGroupEvent.PRM_GROUP_ID; //todo remove when using event system
	private static final String PARAM_SAVING  = "gpw_save";
	private static final String SESSION_PARAM_PERMISSIONS_BEFORE_SAVE  = "gpw_permissions_b_s";
	
	private static final String HELP_TEXT_KEY = "group_permission_window";
	
	//private static final String PARA  = "com.idega.user";
	
	private String mainStyleClass = "main";
	
	private StatefullPresentationImplHandler stateHandler = null;
	private GroupBusiness groupBiz = null;

	private boolean saveChanges = false;

	
	protected int width = 670;
	protected int height = 545;
	
	private String selectedGroupId = null;
	
	private List permissionType;
	private IWResourceBundle iwrb = null;
	
	
	/**
	 * Constructor for GroupPermissionWindow.
	 */
	public GroupPermissionWindow() {
		super();
		
		
		/*
		stateHandler = new StatefullPresentationImplHandler();
		stateHandler.setPresentationStateClass(GroupPermissionWindowPS.class);
		this.getLocation().setApplicationClass(GroupPermissionWindow.class);
		this.getLocation().isInPopUpWindow(true);*/
			
		setWidth(width);
		setHeight(height);
		setScrollbar(true);
		setResizable(true);
		

	}
	/**
	 * Constructor for GroupPermissionWindow.
	 * @param name
	 */
	public GroupPermissionWindow(String name) {
		super(name);
	}
	/**
	 * Constructor for GroupPermissionWindow.
	 * @param width
	 * @param heigth
	 */
	public GroupPermissionWindow(int width, int heigth) {
		super(width, heigth);
	}
	/**
	 * Constructor for GroupPermissionWindow.
	 * @param name
	 * @param width
	 * @param height
	 */
	public GroupPermissionWindow(String name, int width, int height) {
		super(name, width, height);
	}

	
	public void main(IWContext iwc) throws Exception {
		iwrb = this.getResourceBundle(iwc);
		addTitle(iwrb.getLocalizedString("group_permission_window", "Group Permission Window"), IWConstants.BUILDER_FONT_STYLE_TITLE);
		
		parseAction(iwc);
		//use GroupPermissionWindowPS	
	/*
		GroupPermissionWindowPS listener = (GroupPermissionWindowPS) this.getPresentationState(iwc);
		
		SelectGroupEvent selGroup = new SelectGroupEvent();
		selGroup.setSource(this);
		// set controller (added by Thomas)
		String id = IWMainApplication.getEncryptedClassName(UserApplication.Top.class);
		id = PresentationObject.COMPOUNDID_COMPONENT_DELIMITER + id;
		selGroup.setController(id);
		selGroup.setGroupToSelect(new Integer(selectedGroupId));*/
		

	//get permission, order and use entitybrowser
		
		Collection allPermissions = getAllPermissionForSelectedGroupAndCurrentUser(iwc);
		List permissionTypes = getAllPermissionTypes(allPermissions);
	
		if(saveChanges){
			
			AccessController access = iwc.getAccessController();
	
			try {
				Iterator iterator = permissionTypes.iterator();
				
				while (iterator.hasNext()) {
					String key = (String) iterator.next();
					String[] values = iwc.getParameterValues(key);
					Map permissions = this.getPermissionMapFromSession(iwc,key,false);
					
					//adding new values
					if(values!=null && values.length>0){
						
						for (int i = 0; i < values.length; i++) {
							access.setPermission(AccessController.CATEGORY_GROUP_ID,iwc,selectedGroupId,values[i],key,Boolean.TRUE);
							permissions.remove(values[i]);
						}
						
					}
					
					//does not remove record only set the permission to false
					//todo remove if I am owner (see todo on owner stuff in this class)
					//AccessControl.removePermissionRecords(AccessController.CATEGORY_GROUP_ID,iwc, instanceId,(String)item, groupsToRemove);
				
					Iterator entries = permissions.values().iterator();
					while (entries.hasNext()) {
						ICPermission permission = (ICPermission) entries.next();
						permission.setPermissionValue(false);
						permission.store();
					}
					
				}
				
				
				//refresh permissions PermissionCacher.updatePermissions()
				iwc.getApplicationContext().removeApplicationAttribute("ic_permission_map_"+AccessController.CATEGORY_GROUP_ID);
				
			}
			catch (Exception e) {
				e.printStackTrace();
			}
			
			//refetch
			allPermissions = getAllPermissionForSelectedGroupAndCurrentUser(iwc);
			permissionTypes = getAllPermissionTypes(allPermissions);

			
		}
				
		List entityList = orderAndGroupPermissionsByContextValue(allPermissions,iwc);
		GroupComparator groupComparator = new GroupComparator(iwc.getCurrentLocale());
		groupComparator.setObjectsAreICPermissions(true);
		groupComparator.setGroupBusiness(this.getGroupBusiness(iwc));
		Collections.sort(entityList, groupComparator);//sort alphabetically
	
		EntityBrowser browser = new EntityBrowser();
		browser.setEntities("gpw_"+selectedGroupId,entityList);
		//browser.setDefaultNumberOfRows(entityCollection.size() );
		browser.setDefaultNumberOfRows(16);
		browser.setAcceptUserSettingsShowUserSettingsButton(false,false);
		browser.setWidth(browser.HUNDRED_PERCENT);
		browser.setUseExternalForm(true);
		browser.setUseEventSystem(false);
		//disable top set browser
		browser.setShowNavigation(false,true);
	
//	fonts
		Text columnText = new Text();
		columnText.setBold();
		browser.setColumnTextProxy(columnText);
				
		//		set color of rows
		browser.setColorForEvenRows("#FFFFFF");
		browser.setColorForOddRows(IWColor.getHexColorString(246, 246, 247));
		
		int column = 1;
		String groupIdColumn = "ICPermission.PERMISSION_CONTEXT_VALUE";
		//browser.setLeadingEntity("com.idega.core.accesscontrol.data.ICPermission");
		//browser.setMandatoryColumn(column,"com.idega.core.accesscontrol.data.ICPermission.GROUP_ID");

		
		// define groupname converter
		EntityToPresentationObjectConverter contextValueConverter =
			new EntityToPresentationObjectConverter() {
  
				private com.idega.core.user.data.User administrator = null;
				private boolean loggedInUserIsAdmin;
        
        public PresentationObject getHeaderPresentationObject(EntityPath entityPath, EntityBrowser browser, IWContext iwc) {
          return browser.getDefaultConverter().getHeaderPresentationObject(entityPath, browser, iwc);  
        } 
  
				public PresentationObject getPresentationObject(Object permissions, EntityPath path, EntityBrowser browser, IWContext iwc)  {

					Collection col = (Collection) permissions;
    
					Iterator iterator = col.iterator();
								
					while (iterator.hasNext()) {
						ICPermission perm = (ICPermission) iterator.next();
						Group group;
						try {
							group = getGroupBusiness(iwc).getGroupByGroupID(Integer.parseInt(perm.getContextValue()));

							return new Text( getGroupBusiness(iwc).getNameOfGroupWithParentName(group));
							
						}
						catch (RemoteException e) {
							e.printStackTrace();
						}
						catch (FinderException ex) {
							ex.printStackTrace();
						}
						
					}
					
					return new Text("NO GROUP NAME");
					
				}
			};
			browser.setMandatoryColumn(column++,groupIdColumn);
			browser.setEntityToPresentationConverter(groupIdColumn,contextValueConverter);
			//converter ends
			
		// define checkbox button converter class
			EntityToPresentationObjectConverter permissionTypeConverter =
				new EntityToPresentationObjectConverter() {
  
					private com.idega.core.user.data.User administrator = null;
					private boolean loggedInUserIsAdmin;

          
          //called when going between subsets
          public PresentationObject getHeaderPresentationObject(EntityPath entityPath, EntityBrowser browser, IWContext iwc) {
						getPermissionMapFromSession(iwc,entityPath.getShortKey(),true);//zero the map
            return browser.getDefaultConverter().getHeaderPresentationObject(entityPath, browser, iwc);  
          } 
  
					public PresentationObject getPresentationObject(Object permissions, EntityPath path,EntityBrowser browser, IWContext iwc)  {

						Collection col = (Collection) permissions;

						
						Iterator iterator = col.iterator();
					
						boolean active = false;
						boolean isSet = false;
						boolean isOwner = false;
						
						final String columnName = path.getShortKey();
						final String ownerType = "owner";
						
						Map permissionMap = getPermissionMapFromSession(iwc,columnName,false);
						
						String groupId = null;
						String permissionType = null;
						
						while (iterator.hasNext() && !isSet) {
							ICPermission perm = (ICPermission) iterator.next();
							groupId = perm.getContextValue();
							permissionType = perm.getPermissionString();
							
							isSet = columnName.equals(permissionType);
							if(!isOwner){//isOwner is not always set if the group also has other permissions??
								isOwner = ownerType.equals(permissionType);
							}
							
							if(isSet){
								active = perm.getPermissionValue();
								if( active ){							
									permissionMap.put(groupId, perm);
								}
							}
	
						}
					
						
						PresentationObject returnObj = null;
						
						
						if(isSet || isOwner ){
							returnObj = new CheckBox(columnName,groupId);
							((CheckBox)returnObj).setChecked(active);
						}
						else{
							returnObj = new Text("");
						}
						
						return returnObj;
					
					}
				};
				
				Iterator iter = permissionTypes.iterator();
		
				while (iter.hasNext()) {
					String type = (String) iter.next();
					browser.setMandatoryColumn(column++,type);
					browser.setEntityToPresentationConverter( type, permissionTypeConverter);	
				}
		
				//converter ends
				

				
		
		
		Form form = getGroupPermissionForm(browser);
		form.add(new HiddenInput(PARAM_SELECTED_GROUP_ID,selectedGroupId));
		form.add(new HiddenInput(PARAM_SAVING,"TRUE"));
		add(form,iwc);
		
		
		
		
	
/*
		Link link = new Link("EVENT");
		link.addEventModel(selGroup);
		add(link);
		
				


		if(viewGroupPermissions){
			add(getGroupPermissionForm(iwc));
			Group group = listener.getSelectedGroup();
					add("Selected group is: "+group.getName());
			
		}else{
			
			
		}
		*/
		
	}
	
	
	
	private Collection getAllPermissionForSelectedGroupAndCurrentUser(IWContext iwc){
		Collection allPermissions = null;
		
		try {
				allPermissions = AccessControl.getAllGroupPermissionsForGroup(getGroupBusiness(iwc).getGroupByGroupID(Integer.parseInt(selectedGroupId)));
				Collection ownedPermissions = AccessControl.getAllGroupPermissionsOwnedByGroup( iwc.getCurrentUser().getGroup() );
				//ownedPermissions.removeAll(allPermissions);
			
				allPermissions.addAll(ownedPermissions);

		}
		catch (Exception e) {
			e.printStackTrace();
			System.err.println("GroupPermission selected group ("+selectedGroupId+") not found or remote error!");
		} 
		return allPermissions;
	}
		
	/**
	 * Method getAndOrderAllPermissions orders by groupId and returns the
	 * permissions as a collection of collections.
	 * @param iwc
	 * @return Collection
	 */
	private List orderAndGroupPermissionsByContextValue(Collection allPermissions, IWContext iwc) {
		
		Iterator iter = allPermissions.iterator();
		
		//order the permissions by the groupId and create a List for each one.
		Map map = new HashMap();
		List finalCollection = new ArrayList();
		
		String groupId;
		
		while (iter.hasNext()) {
			ICPermission perm = (ICPermission) iter.next();
			groupId = perm.getContextValue();

			List list = (List)map.get(groupId);
			if(list==null){
				list = new ArrayList();
			}
			
			list.add(perm);
			map.put(groupId,list);				
			
		}
			
		finalCollection = com.idega.util.ListUtil.convertCollectionToList(map.values());
			

		return finalCollection;
		
	}
	
	/**
	 * Gets all the permissiontypes (e.g. read/write) from the collection of
	 * ICPermissions from the permissionString column.
	 * @param permissions
	 * @return List
	 */
	public List getAllPermissionTypes(Collection permissions){
	
		Iterator iter = permissions.iterator();
	
		List permissionTypes = new ArrayList();
		
		permissionTypes.add(0,"view");
		permissionTypes.add(1,"edit");
		permissionTypes.add(2,"create");
		permissionTypes.add(3,"delete");

		
		String permissionType;
		while (iter.hasNext()) {
			ICPermission perm = (ICPermission) iter.next();
			
			permissionType = perm.getPermissionString();
			
			if(!permissionTypes.contains(permissionType)){
				permissionTypes.add(permissionType);
			}
			
		}
		
		permissionTypes.remove("owner");
		
		return permissionTypes;
			
	
	}
	
	
	/**
	 * Method addGroupPermissionForm.
	 * @param iwc
	 */
	private Form getGroupPermissionForm(EntityBrowser browser) throws Exception{
		
		IWContext iwc = IWContext.getInstance();
		Help help = getHelp(HELP_TEXT_KEY);
		
		SubmitButton save = new SubmitButton(iwrb.getLocalizedImageButton("save", "Save"));
		save.setSubmitConfirm(iwrb.getLocalizedString("grouppermissionwindow.confirm_message","Change selected permissions?"));
		
		SubmitButton close = new SubmitButton(iwrb.getLocalizedImageButton("close", "Close"));
		close.setOnClick("window.close()");
		
		Link owners = new Link(iwrb.getLocalizedString("owner.button","Owners"));
		owners.setWindowToOpen(GroupOwnersWindow.class);
		owners.setAsImageButton(true);
		owners.addParameter(PARAM_SELECTED_GROUP_ID,selectedGroupId);
		
				
		Table table = new Table(2,2);
		table.setStyleClass(mainStyleClass);
		table.mergeCells(1,1,2,1);
		table.add(browser,1,1);
		table.setVerticalAlignment(1,2,"bottom");
		table.setVerticalAlignment(2,2,"bottom");
		table.add(help,1,2);
		table.add(owners,2,2);
		table.add(Text.NON_BREAKING_SPACE,2,2);
		table.add(save,2,2);
		table.add(Text.NON_BREAKING_SPACE,2,2);
		table.add(close,2,2);
		table.setWidth(620);
		table.setHeight(480);
		table.setVerticalAlignment(1,1,Table.VERTICAL_ALIGN_TOP);
		table.setAlignment(2,2,Table.HORIZONTAL_ALIGN_RIGHT);

		Form form = new Form();
		form.add(table);

		return form;
	}
	
	private void parseAction(IWContext iwc){
		selectedGroupId = iwc.getParameter(GroupPermissionWindow.PARAM_SELECTED_GROUP_ID);

		if(selectedGroupId == null){
			selectedGroupId = (String) iwc.getSessionAttribute(GroupPermissionWindow.PARAM_SELECTED_GROUP_ID);
		}else{
			iwc.setSessionAttribute(GroupPermissionWindow.PARAM_SELECTED_GROUP_ID,selectedGroupId);
		}
		
		saveChanges = iwc.isParameterSet(PARAM_SAVING);
				
	}

	public String getBundleIdentifier() {
		return IW_BUNDLE_IDENTIFIER;
	}


	/**
	 * @see com.idega.presentation.StatefullPresentation#getPresentationStateClass()
	 */
	public Class getPresentationStateClass() {
		return stateHandler.getPresentationStateClass();
	}


	/**
	 * @see com.idega.presentation.PresentationObject#initializeInMain(com.idega.presentation.IWContext)
	 */
	public void initializeInMain(IWContext iwc) throws Exception{
		
	//	this.addActionListener((IWActionListener)this.getPresentationState(iwc));
		  
	}
	

	public IWPresentationState getPresentationState(IWUserContext iwuc){
		return stateHandler.getPresentationState(this,iwuc);
	}

	public StatefullPresentationImplHandler getStateHandler(){
		return stateHandler;
	}

	

	public String getName(IWContext iwc){
		IWResourceBundle rBundle = this.getBundle(iwc).getResourceBundle(iwc);
		return rBundle.getLocalizedString("group.permissions","Group permissions");
	}

	public PresentationObject getPresentationObject(IWContext iwc){
		return this;
	}
	
	public GroupBusiness getGroupBusiness(IWContext iwc) {
		if(groupBiz==null){
			
			try {
				groupBiz = (GroupBusiness) IBOLookup.getServiceInstance(iwc,GroupBusiness.class);
			}
			catch (RemoteException e) {
				e.printStackTrace();
			}
			
			
		}
		
		return groupBiz;
		
		
	}
	
	
	protected Map getPermissionMapFromSession(IWContext iwc, String permissionKey, boolean emptyMap){
		Map map = (Map) iwc.getSessionAttribute(this.SESSION_PARAM_PERMISSIONS_BEFORE_SAVE+permissionKey);
		
		if( map == null || emptyMap){
			map = new HashMap(); 
			iwc.setSessionAttribute(SESSION_PARAM_PERMISSIONS_BEFORE_SAVE+permissionKey,map);
		}
		return map;
		
	}
	/**
	 * @see com.idega.presentation.PresentationObject#getName()
	 */
	public String getName() {
		return "Group permissions";
	}
	
	
	

}


