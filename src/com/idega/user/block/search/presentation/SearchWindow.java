package com.idega.user.block.search.presentation;

import java.rmi.RemoteException;
import java.util.Collection;
import java.util.Iterator;

import com.idega.business.IBOLookup;
import com.idega.event.IWActionListener;
import com.idega.event.IWStateMachine;
import com.idega.idegaweb.IWBundle;
import com.idega.idegaweb.IWConstants;
import com.idega.idegaweb.IWMainApplication;
import com.idega.idegaweb.IWResourceBundle;
import com.idega.idegaweb.presentation.IWAdminWindow;
import com.idega.presentation.IWContext;
import com.idega.presentation.Image;
import com.idega.presentation.PresentationObject;
import com.idega.presentation.Table;
import com.idega.presentation.text.Text;
import com.idega.presentation.ui.DropdownMenu;
import com.idega.presentation.ui.Form;
import com.idega.presentation.ui.HiddenInput;
import com.idega.presentation.ui.SelectionBox;
import com.idega.presentation.ui.SubmitButton;
import com.idega.presentation.ui.TextInput;
import com.idega.user.app.ToolbarElement;
import com.idega.user.app.UserApplication;
import com.idega.user.app.UserApplicationMainArea;
import com.idega.user.app.UserApplicationMainAreaPS;
import com.idega.user.block.search.event.UserSearchEvent;
import com.idega.user.business.GroupBusiness;
import com.idega.user.business.UserBusiness;
import com.idega.user.data.Group;
import com.idega.user.presentation.UserStatusDropdown;


/**
 * <p>Title: idegaWeb User</p>
 * <p>Description: The standard advances search window of the IW User system</p>
 * <p>Copyright: Idega Software Copyright (c) 2002</p>
 * <p>Company: Idega Software</p>
 * @author <a href="eiki@idega.is">Eirikur Hrafnsson</a>
 * @version 1.0 
 */
public class SearchWindow extends IWAdminWindow implements ToolbarElement {
	
	private UserBusiness userBiz;
	private GroupBusiness groupBiz;

	private static final String IW_BUNDLE_IDENTIFIER = "com.idega.user";
  
	private UserSearchEvent searchEvent;
  private String userApplicationMainAreaPSId = null; 
  private Group selectedGroup = null;


	public SearchWindow() {
		setWidth(620);
		setHeight(380);
		setScrollbar(false);
		setResizable(true);
	}

	public void initializeInMain(IWContext iwc) {    
		userApplicationMainAreaPSId = iwc.getParameter(UserApplicationMainArea.USER_APPLICATION_MAIN_AREA_PS_KEY);
		
		// add action listener
		IWStateMachine stateMachine;
   
		try {
			stateMachine = (IWStateMachine) IBOLookup.getSessionInstance(iwc, IWStateMachine.class);
			if (userApplicationMainAreaPSId != null) {
				addActionListener( (IWActionListener)stateMachine.getStateFor(userApplicationMainAreaPSId, UserApplicationMainAreaPS.class));
			}
		}
		catch (RemoteException e) {
			e.printStackTrace();
		}
  
	}
	

	public void main(IWContext iwc) throws Exception {
		//this.debugParameters(iwc);
		IWResourceBundle iwrb = getResourceBundle(iwc);
		searchEvent = new UserSearchEvent();
		searchEvent.setSource(this);
					
		// set controller (added by Thomas) NOT NEEDED
		String id = IWMainApplication.getEncryptedClassName( UserApplication.Top.class );
		id = PresentationObject.COMPOUNDID_COMPONENT_DELIMITER + id;
		searchEvent.setController(id);

		Form form = new Form();
		form.addEventModel(searchEvent, iwc);

		setTitle(iwrb.getLocalizedString("searchwindow.title", "Search"));
		addTitle(iwrb.getLocalizedString("searchwindow.title", "Search"), IWConstants.BUILDER_FONT_STYLE_TITLE);
		setName(iwrb.getLocalizedString("searchwindow.title", "Search"));
		
		add(form);
		Table tab = new Table(4,13);
		form.add(tab);
		
		tab.setColumnVerticalAlignment(1, Table.VERTICAL_ALIGN_TOP);
		tab.setColumnVerticalAlignment(2, Table.VERTICAL_ALIGN_TOP);

		tab.setCellspacing(2);
		tab.setAlignment(4, 12, Table.HORIZONTAL_ALIGN_RIGHT);
		tab.mergeCells(1,4,2,11);
		tab.setWidth(Table.HUNDRED_PERCENT);
		tab.setHeight(Table.HUNDRED_PERCENT);
		
		//names params
		//first name
		TextInput firstName = new TextInput(searchEvent.SEARCH_FIELD_FIRST_NAME);
		firstName.setStyleAttribute(IWConstants.BUILDER_FONT_STYLE_INTERFACE);

		Text firstNameText = new Text();
		firstNameText.setText(iwrb.getLocalizedString("user.search.window.user_first_name", "First name"));
		firstNameText.setFontStyle(IWConstants.BUILDER_FONT_STYLE_LARGE);
		tab.add(firstNameText, 1, 1);
		tab.add(firstName, 1, 2);

		//middle name
		TextInput middleName = new TextInput(searchEvent.SEARCH_FIELD_MIDDLE_NAME);
		middleName.setStyleAttribute(IWConstants.BUILDER_FONT_STYLE_INTERFACE);

		Text middleNameText = new Text();
		middleNameText.setText(iwrb.getLocalizedString("user.search.window.user_middle_name", "Middle name"));
		middleNameText.setFontStyle(IWConstants.BUILDER_FONT_STYLE_LARGE);
		tab.add(middleNameText, 2, 1);
		tab.add(middleName, 2, 2);
		
		//middle name
		TextInput lastName = new TextInput(searchEvent.SEARCH_FIELD_LAST_NAME);
		lastName.setStyleAttribute(IWConstants.BUILDER_FONT_STYLE_INTERFACE);
		
		Text lastNameText = new Text();
		lastNameText.setText(iwrb.getLocalizedString("user.search.window.user_last_name", "Last name"));
		lastNameText.setFontStyle(IWConstants.BUILDER_FONT_STYLE_LARGE);
		tab.add(lastNameText, 3, 1);
		tab.add(lastName, 3, 2);
		
		
		//user status dropdown
		DropdownMenu statusMenu = new UserStatusDropdown(UserSearchEvent.SEARCH_FIELD_STATUS_ID);
		statusMenu.setStyleAttribute(IWConstants.BUILDER_FONT_STYLE_INTERFACE);
		statusMenu.addMenuElement(-1,iwrb.getLocalizedString("user.search.window.all_statuses", "All statuses"));
		statusMenu.setSelectedElement(-1);
		
		
		Text status = new Text(iwrb.getLocalizedString("user.search.window.status", "Status"));
		status.setFontStyle(IWConstants.BUILDER_FONT_STYLE_LARGE);
		tab.add(status, 3, 3);
		tab.add(statusMenu, 3, 4);
		
		//group selectionbox
		
		SelectionBox groupSel = new SelectionBox(UserSearchEvent.SEARCH_FIELD_GROUPS);
		groupSel.setHeight(14); 
		groupSel.setWidth("240");

		Collection groupsCol = getUserBusiness(iwc).getAllGroupsWithViewPermission(iwc.getCurrentUser(),iwc);
		
		Iterator nodes = groupsCol.iterator();
		while (nodes.hasNext()) {
			Group group = (Group) nodes.next();
			groupSel.addMenuElement( ((Integer)group.getPrimaryKey()).intValue(), getGroupBusiness(iwc).getNameOfGroupWithParentName(group) );
			//getchildren
		}
		

		
		Text groups = new Text(iwrb.getLocalizedString("user.search.window.groups", "Groups"));
		groups.setFontStyle(IWConstants.BUILDER_FONT_STYLE_LARGE);
		tab.add(groups, 1, 3);
		tab.add(groupSel, 1, 4); 
		

		//age
		Table ageTable = new Table(3,1);
		
		TextInput ageFloor = new TextInput(searchEvent.SEARCH_FIELD_AGE_FLOOR,"0");
		ageFloor.setLength(3);
		ageFloor.setStyleAttribute(IWConstants.BUILDER_FONT_STYLE_INTERFACE);
		
		TextInput ageCeil = new TextInput(searchEvent.SEARCH_FIELD_AGE_CEILING,"120");
		ageCeil.setLength(3);
		ageCeil.setStyleAttribute(IWConstants.BUILDER_FONT_STYLE_INTERFACE);

		ageTable.add(ageFloor,1,1);
		ageTable.add(" - ",2,1);
		ageTable.add(ageCeil,3,1);
		
		Text ages = new Text(iwrb.getLocalizedString("user.search.window.ages", "Age"));
		ages.setFontStyle(IWConstants.BUILDER_FONT_STYLE_LARGE);
		tab.add(ages, 3,5);
		tab.add(ageTable, 3, 6); 
		
		//gender
		Integer maleId = getUserBusiness(iwc).getGenderId("male");
		Integer femaleId = getUserBusiness(iwc).getGenderId("female");	
		DropdownMenu genders = new DropdownMenu(UserSearchEvent.SEARCH_FIELD_GENDER_ID);
		genders.addMenuElement(femaleId.intValue(),iwrb.getLocalizedString("user.search.window.females", "Women"));
		genders.addMenuElement(maleId.intValue(),iwrb.getLocalizedString("user.search.window.males", "Men"));
		genders.addMenuElement(-1,iwrb.getLocalizedString("user.search.window.both.genders", "Both genders"));
		genders.setSelectedElement(-1);
		
		Text gender = new Text(iwrb.getLocalizedString("user.search.window.gender", "Gender"));
		gender.setFontStyle(IWConstants.BUILDER_FONT_STYLE_LARGE);
		tab.add(gender, 3,7);
		tab.add(genders, 3, 8); 
		
//	personal id
		TextInput ssn = new TextInput(searchEvent.SEARCH_FIELD_PERSONAL_ID);
		ssn.setStyleAttribute(IWConstants.BUILDER_FONT_STYLE_INTERFACE);


		Text ssnText = new Text();
		ssnText.setText(iwrb.getLocalizedString("user.search.window.personal_id", "SSN"));
		ssnText.setFontStyle(IWConstants.BUILDER_FONT_STYLE_LARGE);
		tab.add(ssnText, 3, 9);
		tab.add(ssn, 3, 10);
			
			
//	streetname search
		TextInput address = new TextInput(searchEvent.SEARCH_FIELD_ADDRESS);
		address.setStyleAttribute(IWConstants.BUILDER_FONT_STYLE_INTERFACE);


		Text addressText = new Text();
		addressText.setText(iwrb.getLocalizedString("user.search.window.address", "Address"));
		addressText.setFontStyle(IWConstants.BUILDER_FONT_STYLE_LARGE);
		tab.add(addressText, 3, 11);
		tab.add(address, 3, 12);

		//buttons
		SubmitButton save = new SubmitButton(iwrb.getLocalizedImageButton("user.search.window.search", "Search"));
   	SubmitButton close = new SubmitButton(iwrb.getLocalizedImageButton("user.search.window.close", "Close") );
    close.setOnClick("window.close();return false;");
    
		HiddenInput type = new HiddenInput(UserSearchEvent.SEARCH_FIELD_SEARCH_TYPE, Integer.toString(UserSearchEvent.SEARCHTYPE_ADVANCED));
	
		tab.add(close, 3, 13);
		tab.add(type,3,13);
		tab.add(Text.getNonBrakingSpace(), 3, 13);
		tab.add(save, 3, 13);
				
	}


	public Image getButtonImage(IWContext iwc) {
		IWBundle bundle = this.getBundle(iwc);
		return bundle.getImage("create_group.gif", "Create group");
	}
	
	public String getBundleIdentifier() {
		return IW_BUNDLE_IDENTIFIER;
	}


	public String getName(IWContext iwc) {
		IWResourceBundle rBundle = this.getBundle(iwc).getResourceBundle(iwc);
		return rBundle.getLocalizedString("searchwindow.name", "Search");
	}

	public PresentationObject getPresentationObject(IWContext iwc) {
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
	
	public UserBusiness getUserBusiness(IWContext iwc) {
		if(userBiz==null){	
			try {
				userBiz = (UserBusiness) IBOLookup.getServiceInstance(iwc,UserBusiness.class);
			}
			catch (RemoteException e) {
				e.printStackTrace();
			}	
		}	
		return userBiz;
	}
  
}