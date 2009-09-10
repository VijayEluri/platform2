package com.idega.user.app;

import java.rmi.RemoteException;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.Comparator;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import com.idega.event.IWPresentationEvent;
import com.idega.idegaweb.IWApplicationContext;
import com.idega.idegaweb.IWBundle;
import com.idega.idegaweb.IWLocation;
import com.idega.idegaweb.IWResourceBundle;
import com.idega.idegaweb.browser.presentation.IWBrowserView;
import com.idega.presentation.IWContext;
import com.idega.presentation.Image;
import com.idega.presentation.Page;
import com.idega.presentation.PresentationObject;
import com.idega.presentation.Table;
import com.idega.presentation.text.Link;
import com.idega.presentation.text.Text;
import com.idega.presentation.ui.DropdownMenu;
import com.idega.presentation.ui.Form;
import com.idega.presentation.ui.SelectOption;
import com.idega.user.block.search.presentation.SearchForm;
import com.idega.user.block.search.presentation.SearchWindow;
import com.idega.user.business.GroupBusiness;
import com.idega.user.business.UserGroupPlugInBusiness;
import com.idega.user.data.User;
import com.idega.user.event.ChangeClassEvent;
import com.idega.user.presentation.CreateGroupWindow;
import com.idega.user.presentation.CreateUser;
import com.idega.user.presentation.MassMovingWindowPlugin;
import com.idega.user.presentation.RoleMastersWindow;

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
 * 
 * @version 1.0
 */

public class Toolbar extends Page implements IWBrowserView {
	
	public static final String SELECTED_GROUP_PROVIDER_PRESENTATION_STATE_ID_KEY = "selected_group_mm_id_key";

	protected String title;

	protected IWBundle iwb;
	protected IWResourceBundle iwrb;

	protected String _controlTarget = null;

	protected IWPresentationEvent _controlEvent = null;
	private SearchForm searchForm = new SearchForm();

	private String selectedGroupProviderStateId = null;
	private String userApplicationMainAreaStateId = null;

	private String menuTableStyle = "menu";
	private String styledLink = "styledLink";
	private String styledText = "styledText";

	public Toolbar() {
		// default constructor
	}

	public void setSelectedGroupProviderStateId(String selectedGroupProviderStateId) {
		this.selectedGroupProviderStateId = selectedGroupProviderStateId;
	}

	public String getBundleIdentifier() {
		return "com.idega.user";
	}

	public void setControlEventModel(IWPresentationEvent model) {
		this._controlEvent = model;
		this.searchForm.setControlEventModel(model);
	}

	public void setControlTarget(String controlTarget) {
		this._controlTarget = controlTarget;
		this.searchForm.setControlTarget(controlTarget);

	}

	public void main(IWContext iwc) throws Exception {
		this.empty();
		this.iwb = getBundle(iwc);
		this.iwrb = getResourceBundle(iwc);
		boolean useDropdown = this.iwb.getBooleanProperty("use_dropdown_in_toolbar", false);
		
		Table controlTable = new Table(1, 3);
		controlTable.setCellpadding(0);
		controlTable.setCellspacing(0);
		controlTable.setWidth(Table.HUNDRED_PERCENT);
		controlTable.setHeight(29);
		controlTable.setColor(1, 1, "#FFFFFF");
		controlTable.setColor(1, 3, "#D0D0D0");

		Table toolbarTable = new Table(4, 1);
		toolbarTable.setCellpadding(0);
		toolbarTable.setCellspacing(0);
		toolbarTable.setBorder(0);
		toolbarTable.setHeight(27);
		toolbarTable.setStyleClass(this.menuTableStyle);
		toolbarTable.setWidth(Table.HUNDRED_PERCENT);
		toolbarTable.setWidth(4, Table.HUNDRED_PERCENT);
		toolbarTable.setHeight(1, Table.HUNDRED_PERCENT);
		toolbarTable.setAlignment(4, 1, Table.HORIZONTAL_ALIGN_RIGHT);
		controlTable.add(toolbarTable, 1, 2);
		add(controlTable);

		Table toolbar1 = new Table();
		toolbar1.setCellpadding(0);
		toolbar1.setCellspacing(0);
		toolbar1.setBorder(0);
		toolbarTable.add(toolbar1, 1, 1);
		int toolbarColumn = 1;
		
		//User
		Image iconCrUser = this.iwb.getImage("new_user.gif");
		Link tLink11 = getToolbarLink(new Text(this.iwrb.getLocalizedString("new.member", "New member")), CreateUser.class, null);
		Link newUserImageLink = getToolbarLink(iconCrUser, CreateUser.class, null);
		
		if (this.selectedGroupProviderStateId != null){
			tLink11.addParameter(CreateGroupWindow.SELECTED_GROUP_PROVIDER_PRESENTATION_STATE_ID_KEY, this.selectedGroupProviderStateId);
			newUserImageLink.addParameter(CreateGroupWindow.SELECTED_GROUP_PROVIDER_PRESENTATION_STATE_ID_KEY, this.selectedGroupProviderStateId);
		}

		tLink11.setStyleClass(this.styledLink);
		toolbar1.setCellpaddingLeft(toolbarColumn, 1, 12);
		toolbar1.setCellpaddingRight(toolbarColumn, 1, 3);
		toolbar1.add(newUserImageLink, toolbarColumn++, 1);
		toolbar1.setVerticalAlignment(toolbarColumn, 1, Table.VERTICAL_ALIGN_TOP);
		toolbar1.setCellpaddingTop(toolbarColumn, 1, 3);
		toolbar1.add(tLink11, toolbarColumn++, 1);

		//Group
		Image iconCrGroup = this.iwb.getImage("new_group.gif");
		Link tLink12 = getToolbarLink(new Text(this.iwrb.getLocalizedString("new.group", "New group")), CreateGroupWindow.class, null);
		Link imageLink = getToolbarLink(iconCrGroup, CreateGroupWindow.class, null);
		
		tLink12.setStyleClass(this.styledLink);
		toolbar1.setCellpaddingLeft(toolbarColumn, 1, 7);
		toolbar1.setCellpaddingRight(toolbarColumn, 1, 3);
		
		if (this.selectedGroupProviderStateId != null){
			tLink12.addParameter(CreateGroupWindow.SELECTED_GROUP_PROVIDER_PRESENTATION_STATE_ID_KEY, this.selectedGroupProviderStateId);
			imageLink.addParameter(CreateGroupWindow.SELECTED_GROUP_PROVIDER_PRESENTATION_STATE_ID_KEY, this.selectedGroupProviderStateId);
		}
		
		toolbar1.add(imageLink, toolbarColumn++, 1);
		toolbar1.setVerticalAlignment(toolbarColumn, 1, Table.VERTICAL_ALIGN_TOP);
		toolbar1.setCellpaddingTop(toolbarColumn, 1, 3);
		toolbar1.add(tLink12, toolbarColumn++, 1);

		if (iwc.isSuperAdmin()) {
			Image iconRoleMasters = this.iwb.getImage("key_icon.gif");
			Link tLink14 = getToolbarLink(new Text(this.iwrb.getLocalizedString("button.role_masters", "Role Masters")), RoleMastersWindow.class, null);
			tLink14.setStyleClass(this.styledLink);
			toolbar1.setCellpaddingLeft(toolbarColumn, 1, 7);
			toolbar1.setCellpaddingRight(toolbarColumn, 1, 3);
			toolbar1.add(getToolbarLink(iconRoleMasters, RoleMastersWindow.class, null), toolbarColumn++, 1);
			toolbar1.setVerticalAlignment(toolbarColumn, 1, Table.VERTICAL_ALIGN_TOP);
			toolbar1.setCellpaddingTop(toolbarColumn, 1, 3);
			toolbar1.add(tLink14, toolbarColumn++, 1);
		}

		//Search temp
		Image iconSearch = this.iwb.getImage("search.gif");
		Link tLink13 = getToolbarLink(new Text(this.iwrb.getLocalizedString("button.search", "Search")), SearchWindow.class, null);
		Link searchLink = getToolbarLink(iconSearch, SearchWindow.class, null);
		
		tLink13.setStyleClass(this.styledLink);
		if (this.userApplicationMainAreaStateId != null){
			tLink13.addParameter(UserApplicationMainArea.USER_APPLICATION_MAIN_AREA_PS_KEY, this.userApplicationMainAreaStateId);
			searchLink.addParameter(UserApplicationMainArea.USER_APPLICATION_MAIN_AREA_PS_KEY, this.userApplicationMainAreaStateId);
		}
		toolbar1.setCellpaddingLeft(toolbarColumn, 1, 7);
		toolbar1.setCellpaddingRight(toolbarColumn, 1, 3);
		toolbar1.add(searchLink, toolbarColumn++, 1);
		toolbar1.setVerticalAlignment(toolbarColumn, 1, Table.VERTICAL_ALIGN_TOP);
		toolbar1.setCellpaddingTop(toolbarColumn, 1, 3);
		toolbar1.add(tLink13, toolbarColumn++, 1);

		// adding all plugins that implement the interface ToolbarElement
		List  toolbarElements = new ArrayList();
		User user = iwc.getCurrentUser();
		Collection plugins = getGroupBusiness(iwc).getUserGroupPluginsForUser(user);
		Iterator iter = plugins.iterator();
		while (iter.hasNext()) {
		    UserGroupPlugInBusiness pluginBiz = (UserGroupPlugInBusiness) iter.next();
			List list = pluginBiz.getMainToolbarElements();
			if (list != null) {
				toolbarElements.addAll(list);
			}
		}
		// adding some toolbar elements that belong to this bundle
		toolbarElements.add(new MassMovingWindowPlugin());
		
		boolean addPlugins = false;
		if (!toolbarElements.isEmpty()) {
			Table toolbar2 = new Table();
			toolbar2.setCellpadding(0);
			toolbar2.setCellspacing(0);
			toolbar2.setBorder(0);
			toolbarTable.add(toolbar2, 3, 1);
			toolbarColumn = 1;

			DropdownMenu menu  = new DropdownMenu("other_choices");
			final IWContext finalIwc = iwc;
			Comparator priorityComparator = new Comparator() {
				
				public int compare(Object toolbarElementA, Object toolbarElementB) {
					int priorityA = ((ToolbarElement) toolbarElementA).getPriority(finalIwc);
					int priorityB = ((ToolbarElement) toolbarElementB).getPriority(finalIwc);
					if (priorityA == -1  && priorityB == -1) {
						return 0;
					}
					else if (priorityA == -1) {
						return 1;
					}
					else if (priorityB ==  -1) {
						return -1;
					}
					return priorityA - priorityB;
				}
			};
			Collections.sort(toolbarElements, priorityComparator);
			Iterator toolbarElementsIterator = toolbarElements.iterator();
			while (toolbarElementsIterator.hasNext()) {
				ToolbarElement toolbarElement = (ToolbarElement) toolbarElementsIterator.next();
				if (toolbarElement.isValid(iwc)) {
					addPlugins = true;
					Class toolPresentationClass = toolbarElement.getPresentationObjectClass(iwc);
					Map parameterMap = toolbarElement.getParameterMap(iwc);
					// a special parameter, very few plugins are using it
					if (this.selectedGroupProviderStateId != null) {
						if (parameterMap == null) {
							parameterMap = new HashMap();
						}
						parameterMap.put(SELECTED_GROUP_PROVIDER_PRESENTATION_STATE_ID_KEY, this.selectedGroupProviderStateId );
					}
					String toolName = toolbarElement.getName(iwc);
					if (useDropdown && (! toolbarElement.isButton(iwc))) { 
						SelectOption toolOption = new SelectOption(toolName, "1");
						toolOption.setWindowToOpenOnSelect(toolPresentationClass, parameterMap);
						menu.addOption(toolOption);
					}
					else {
						Image toolImage = toolbarElement.getButtonImage(finalIwc);
						toolbar2.setCellpaddingLeft(toolbarColumn, 1, 7);
						if (toolImage != null) {
							toolbar2.setCellpaddingRight(toolbarColumn, 1, 3);
							toolbar2.add(getToolbarLink(toolImage, toolPresentationClass, parameterMap), toolbarColumn++, 1);
						}
						Link toolLink = getToolbarLink(new Text(toolName), toolPresentationClass, parameterMap);
						toolLink.setStyleClass(this.styledLink);
						
						toolbar2.setVerticalAlignment(toolbarColumn, 1, Table.VERTICAL_ALIGN_TOP);
						toolbar2.setCellpaddingTop(toolbarColumn, 1, 3);
						toolbar2.add(toolLink, toolbarColumn++, 1);
					}
				}
			}		

			if (addPlugins) {
				Image dottedImage = this.iwb.getImage("dotted.gif");
				toolbarTable.setCellpaddingLeft(2, 1, 10);
				toolbarTable.setCellpaddingRight(2, 1, 3);
				toolbarTable.add(dottedImage, 2, 1);
				
				if (useDropdown) {
					Form form = new Form();
					menu.addMenuElementFirst("", "");
					form.add(menu);
	
					int handbookFileID = Integer.parseInt(this.iwb.getProperty("handbook_file_id", "-1"));
					if (handbookFileID != -1) {
						SelectOption option = new SelectOption(this.iwrb.getLocalizedString("toolbar.handbook", "Handbook"));
						option.setFileToOpenOnSelect(handbookFileID);
						menu.add(option);
					}
					
					Image iconOtherChanges = this.iwb.getImage("other_choises.gif");
					Text menuText =  new Text(this.iwrb.getLocalizedString("button.other_choices", "Other choices"));
					menuText.setStyleClass(this.styledText);
					toolbar2.setCellpaddingLeft(toolbarColumn, 1, 7);
					toolbar2.setCellpaddingRight(toolbarColumn, 1, 3);
					toolbar2.add(iconOtherChanges, toolbarColumn++, 1);
					toolbar2.add(menuText, toolbarColumn++, 1);
					toolbar2.setCellpaddingLeft(toolbarColumn, 1, 7);
					toolbar2.add(form, toolbarColumn++, 1);
				}
			}
		}
		
		//search
		Table button9 = new Table(2, 1);
		Text text9 = new Text(this.iwrb.getLocalizedString("fast_search", "Fast search"));
		button9.add(text9, 1, 1);
		button9.setHorizontalAlignment("right");
		IWLocation location = (IWLocation) this.getLocation().clone();
		location.setSubID(1);
		this.searchForm.setLocation(location, iwc);
		this.searchForm.setArtificialCompoundId(getCompoundId(), iwc);
		this.searchForm.setHorizontalAlignment("right");
		this.searchForm.setTextInputValue(this.iwrb.getLocalizedString("insert_search_string", "Insert a search string"));
		toolbarTable.setCellpaddingRight(4, 1, 6);
		toolbarTable.add(this.searchForm, 4, 1);
	}
	
	protected Link getToolbarLink(PresentationObject obj, Class windowClass, Map parameters) {
		Link link = new Link(obj);
		link.setWindowToOpen(windowClass);
		if (parameters != null) {
			link.setParameter(parameters);
		}
		
		return link;
	}
	
	protected Table getToolbarButtonWithChangeClassEvent(String textOnButton, Image icon, Class changeClass) {
		Table button = new Table(2, 1);
		button.setCellpadding(0);
		Text text = new Text(textOnButton);
		text.setFontFace(Text.FONT_FACE_VERDANA);
		text.setFontSize(Text.FONT_SIZE_7_HTML_1);
		Link eventLink = new Link(text);
		button.add(icon, 1, 1);
		button.add(eventLink, 2, 1);
		eventLink.addEventModel(new ChangeClassEvent(changeClass));
		if (this._controlEvent != null) {
			eventLink.addEventModel(this._controlEvent);
		}
		if (this._controlTarget != null) {
			eventLink.setTarget(this._controlTarget);
		}

		return button;
	}

	public void setTitle(String title) {
		this.title = title;
	}

	/**
	 * @param string
	 */
	public void setUserApplicationMainAreaStateId(String string) {
		this.userApplicationMainAreaStateId = string;
	}

	public GroupBusiness getGroupBusiness(IWApplicationContext iwac) throws RemoteException {
		return (GroupBusiness) com.idega.business.IBOLookup.getServiceInstance(iwac, GroupBusiness.class);
	}
}