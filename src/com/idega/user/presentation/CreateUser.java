package com.idega.user.presentation;

import java.rmi.RemoteException;

import javax.ejb.CreateException;
import javax.ejb.FinderException;

import com.idega.block.help.presentation.Help;
import com.idega.idegaweb.IWApplicationContext;
import com.idega.idegaweb.IWBundle;
import com.idega.idegaweb.IWConstants;
import com.idega.idegaweb.IWResourceBundle;
import com.idega.presentation.IWContext;
import com.idega.presentation.Image;
import com.idega.presentation.Table;
import com.idega.presentation.text.Link;
import com.idega.presentation.text.Text;
import com.idega.presentation.ui.BackButton;
import com.idega.presentation.ui.CloseButton;
import com.idega.presentation.ui.Form;
import com.idega.presentation.ui.HiddenInput;
import com.idega.presentation.ui.PasswordInput;
import com.idega.presentation.ui.SubmitButton;
import com.idega.presentation.ui.TextInput;
import com.idega.user.business.GroupBusiness;
import com.idega.user.business.UserBusiness;
import com.idega.user.data.Group;
import com.idega.user.data.User;
/**
 * Title:        User
 * Description:
 * Copyright:    Copyright (c) 2001
 * Company:      idega.is
 * @author 2000 - idega team - <a href="mailto:gummi@idega.is">Gu�mundur �g�st S�mundsson</a>
 * @version 1.0
 */

public class CreateUser extends StyledIWAdminWindow { 
	private GroupBusiness groupBiz;

	private static final String IW_BUNDLE_IDENTIFIER = "com.idega.user";

	private static final String TAB_NAME = "usr_create_tab_name";
	private static final String DEFAULT_TAB_NAME = "Create member";

	private Text fullNameText;
	private Text userLoginText;
	private Text passwordText;
	private Text confirmPasswordText;
	private Text ssnText;

	private Text generateLoginText;
	private Text generatePasswordText;
	private Text mustChangePasswordText;
	private Text cannotChangePasswordText;
	private Text passwordNeverExpiresText;
	private Text disableAccountText;
	private Text goToPropertiesText;
	private Text primaryGroupText;

	private TextInput fullNameField;
	private TextInput userLoginField;
	private PasswordInput passwordField;
	private PasswordInput confirmPasswordField;
	private TextInput ssnField;

/*	private CheckBox generateLoginField;
	private CheckBox generatePasswordField;
	private CheckBox mustChangePasswordField;
	private CheckBox cannotChangePasswordField;
	private CheckBox passwordNeverExpiresField;
	private CheckBox disableAccountField;
	*/
	private HiddenInput goToPropertiesField;
	
	private GroupChooser primaryGroupField;

	private SubmitButton okButton;
	private SubmitButton continueButton;
	private CloseButton cancelButton;
	private BackButton backButton;
	
	private Help help; 
	private static final String HELP_TEXT_KEY = "create_user";

	private Form myForm;
	private Table mainTable; 
	private Table inputTable;
	private Table buttonTable;
	private Table helpTable;
	private Table warningTable;

	private String selectedGroupId = null;

	public static String PARAMETERSTRING_GROUP_ID = "default_group";

	public static String okButtonParameterValue = "ok";
	public static String submitButtonParameterValue = "submit";
	public static String cancelButtonParameterValue = "cancel";
	public static String submitButtonParameterName = "submit";

	public static String fullNameFieldParameterName = "fullName";
	public static String userLoginFieldParameterName = "login";
	public static String passwordFieldParameterName = "password";
	public static String confirmPasswordFieldParameterName = "confirmPassword";
	public static String ssnFieldParameterName = "ssn";
	
	private String ssn = null;
	private String fullName = null;
	private String primaryGroup = null;
	
	private TextInput groupInput = null;
/*
	public static String generateLoginFieldParameterName = "generateLogin";
	public static String generatePasswordFieldParameterName = "generatePassword";
	public static String mustChangePasswordFieldParameterName = "mustChange";
	public static String cannotChangePasswordFieldParameterName = "cannotChange";
	public static String passwordNeverExpiresFieldParameterName = "neverExpires";
	public static String disableAccountFieldParameterName = "disableAccount";*/
	public static String goToPropertiesFieldParameterName = "gotoProperties";
	public static String primaryGroupFieldParameterName = "primarygroup";

	private String rowHeight = "37";

	private UserBusiness userBiz;
	
	private boolean isSetToClose = false;
	private boolean ssnWarningDisplay = false;
	private boolean fullNameWarningDisplay = false;
	private boolean formNotComplete = false;
	
	private String inputTextStyle = "text";
	private String backgroundTableStyle = "back";
	private String mainTableStyle = "main";
	private String bannerTableStyle = "banner";

	public CreateUser() {
		super();
		setHeight(250);
		setWidth(380);
	//	setBackgroundColor(new IWColor(207, 208, 210));
		setScrollbar(false);
		setResizable(true);
	}

	protected void initializeTexts() {
		IWContext iwc = IWContext.getInstance();
		IWResourceBundle iwrb = getResourceBundle(iwc);

  	fullNameText = new Text(iwrb.getLocalizedString(fullNameFieldParameterName,"Name"));
  	fullNameText.setBold();
		ssnText = new Text(iwrb.getLocalizedString(ssnFieldParameterName,"Personal ID (SSN)"));
		ssnText.setBold();
		primaryGroupText = new Text(iwrb.getLocalizedString(primaryGroupFieldParameterName,"Primarygroup"));
		primaryGroupText.setBold();
	}

	protected void initializeFields(IWContext iwc) {
		
		IWResourceBundle iwrb = getResourceBundle(iwc);
		
		fullNameField = new TextInput(fullNameFieldParameterName);
		fullNameField.setLength(20);
		fullNameField.setStyleClass("text");
		fullNameField.setOnFocus("");
//		fullNameField.setAsNotEmpty(iwrb.getLocalizedString("new_user.full_name_required","Full name must be selected"));
		ssnField = new TextInput(ssnFieldParameterName);
		ssnField.setLength(20);
		ssnField.setMaxlength(12);
//		ssnField.setAsNotEmpty(iwrb.getLocalizedString("new_user.personal_id_required","Personal ID must be selected"));
		//ssnField.setAsIcelandicSSNumber();


		goToPropertiesField = new HiddenInput(goToPropertiesFieldParameterName,"TRUE");
		//goToPropertiesField.setChecked(true);
		
		primaryGroupField = new GroupChooser(primaryGroupFieldParameterName);
		groupInput = (TextInput)primaryGroupField.getPresentationObject(iwc);
//		groupInput.setAsNotEmpty(iwrb.getLocalizedString("new_user.group_required","Group must be selected"));
//		if(primaryGroupField.isEmpty()) {
//			this.setErrorMessage(iwrb.getLocalizedString("new_user.group_required","Group must be selected"));
//			this.setToLoadAlert(iwrb.getLocalizedString("new_user.group_required","Group must be selected"));
//		}
//		primaryGroupField.setAsNotEmpty(iwrb.getLocalizedString(primaryGroupFieldParameterName,"A group must be selected"));

		help = getHelp(HELP_TEXT_KEY);
		
		okButton = new SubmitButton(iwrb.getLocalizedString("save", "Save"), submitButtonParameterName, okButtonParameterValue);
    okButton.setAsImageButton(true);
    continueButton = new SubmitButton(iwrb.getLocalizedString("yes", "Yes"), submitButtonParameterName, submitButtonParameterValue);
    continueButton.setAsImageButton(true);
		//cancelButton = new SubmitButton(" Cancel ", submitButtonParameterName, cancelButtonParameterValue);
		cancelButton = new CloseButton(iwrb.getLocalizedString("close", "Close"));
		cancelButton.setAsImageButton(true);
		backButton = new BackButton(iwrb.getLocalizedString("back", "Back"));

	}

	public void lineUpElements(IWContext iwc) {
		//IWResourceBundle iwrb = getResourceBundle(iwc);
		
		//mainTable begin	
		mainTable = new Table(2,2);
		mainTable.setStyleClass(mainTableStyle);
		mainTable.setCellspacing(10);
		mainTable.setCellpadding(0);
		mainTable.setWidth(300);
		mainTable.setHeight(180);
		//mainTable end
		
		//inputTable begin
		inputTable = new Table(1, 6);
		inputTable.setCellpadding(0);
		inputTable.setCellspacing(0);		
		inputTable.add(fullNameText,1,1);
		inputTable.add(fullNameField,1,2);
		inputTable.add(ssnText,1,3);
		inputTable.add(ssnField,1,4);
		inputTable.add(primaryGroupText, 1, 5);
		inputTable.add(primaryGroupField, 1, 6);
		//inputTable end
	
		// buttonTable begin
		buttonTable = new Table(5, 1);
		buttonTable.setCellpadding(0);
		buttonTable.setCellspacing(0);
		buttonTable.setHeight(1, rowHeight);
		buttonTable.setWidth(2, "5");
		buttonTable.setAlignment("right");
		buttonTable.setVerticalAlignment("bottom");
		buttonTable.add(okButton, 1, 1);
		buttonTable.add(cancelButton, 3, 1);			
		// buttonTable end
		
		helpTable = new Table(1,1);
		helpTable.setCellpadding(0);
		helpTable.setCellspacing(0);
		helpTable.setHeight(1,rowHeight);
		helpTable.setVerticalAlignment("bottom");
		helpTable.add(help,1,1);
		
		//warningTable begin
		warningTable = new Table(1,1);
		warningTable.setCellpadding(0);
		warningTable.setCellspacing(0);
		mainTable.add(inputTable, 1,1);
		mainTable.add(buttonTable, 2,2);
		mainTable.add(helpTable,1,2);
		
		myForm.add(mainTable);
	}

	public void commitCreation(IWContext iwc) {
		
		IWResourceBundle iwrb = getResourceBundle(iwc);

		User newUser = null;
		Group group = null;
								
		Integer primaryGroupId = null;
			
		try {			
			if (primaryGroup != null && !primaryGroup.equals("")) {
				
				primaryGroupId = new Integer(primaryGroup);
				
				if((ssn != null || !ssn.equals("")) && (fullName == null || fullName.equals(""))) {
					try { 
						newUser = getUserBusiness(iwc).getUser(ssn);
					}
					catch (Exception e) {
						newUser = null;
					}
					
					if(newUser != null) {
						fullName = newUser.getName();
					}
					else {
						fullName = ssn; 
					}
				}
				newUser = getUserBusiness(iwc).createUserByPersonalIDIfDoesNotExist(fullName,ssn,null,null);					
				group = getGroupBusiness(iwc).getGroupByGroupID(primaryGroupId.intValue());
				group.addGroup(newUser);
				newUser.setPrimaryGroupID(primaryGroupId);
				newUser.store();	
				Link gotoLink = new Link();
				gotoLink.setWindowToOpen(UserPropertyWindow.class);
				gotoLink.addParameter(UserPropertyWindow.PARAMETERSTRING_USER_ID, newUser.getPrimaryKey().toString());
				String script = "window.opener." + gotoLink.getWindowToOpenCallingScript(iwc);
				setOnLoad(script);
				isSetToClose = true;							
			}
			else {
				setAlertOnLoad(iwrb.getLocalizedString("new_user.group_required","Group must be selected"));
				ssnField.setContent(ssn);
				fullNameField.setContent(fullName);
			}
				
			if(ssn == null || ssn.equals("")) {
				newUser.setPersonalID(Integer.toString(((Integer)newUser.getPrimaryKey()).intValue()));				
				newUser.store();
			}
			
			}//try ends
			catch (RemoteException e) {
				e.printStackTrace();
			}
			catch (CreateException e) {
				e.printStackTrace();
			}
			catch (FinderException e) {
				e.printStackTrace();
			}
	}
	public void main(IWContext iwc) throws Exception {
		this.empty();
		IWResourceBundle iwrb = getResourceBundle(iwc);
		IWBundle iwb = getBundle(iwc);
	  	
		setName(iwrb.getLocalizedString(TAB_NAME, DEFAULT_TAB_NAME));
		addTitle(iwrb.getLocalizedString("create_new_user", "Create a new User"), IWConstants.BUILDER_FONT_STYLE_TITLE);

		myForm = new Form();
		add(myForm,iwc);
		initializeTexts();
		initializeFields(iwc);
		lineUpElements(iwc);
						
		//added to set a new image for the groupChooser
		Image groupChooseImage = iwb.getImage("magnify.gif");
		primaryGroupField.setChooseButtonImage(groupChooseImage);

		
		selectedGroupId = iwc.getParameter(PARAMETERSTRING_GROUP_ID);
		if (selectedGroupId != null) {
			primaryGroupField.setSelectedNode(new GroupTreeNode(this.getGroupBusiness(iwc).getGroupByGroupID(Integer.parseInt(selectedGroupId))));
			myForm.add(new HiddenInput(PARAMETERSTRING_GROUP_ID, selectedGroupId));
		}
		
		String submit = iwc.getParameter("submit");
		ssn = iwc.getParameter(ssnFieldParameterName);
		fullName = iwc.getParameter(fullNameFieldParameterName);
		primaryGroup = iwc.getParameter(primaryGroupFieldParameterName);
		
		if(primaryGroup == null || primaryGroup.equals(""))
			primaryGroup = "";
		else
			primaryGroup = primaryGroup.substring(primaryGroup.lastIndexOf("_")+1);
	

		if(ssn == null || ssn.equals("") || fullName == null || fullName.equals("")) 
			formNotComplete = true;			
					
			if(submit != null) {
				//is addressed if the okButton is pressed and the user has:
				//1. not entered anything in the form,
				//2. entered only the name
				//3. entered only the social security number
				if (submit.equals("ok") && formNotComplete) {
					//is addressed if both name and social security number are empty
					if((ssn == null || ssn.equals("")) && (fullName == null || fullName.equals("")))
						setAlertOnLoad(iwrb.getLocalizedString("new_user.ssn_or_fullName_required","Personal ID or name is required"));
					//is addressed if only the name is entered
					else if(ssn == null || ssn.equals("") && (fullName != null || !fullName.equals(""))) {
						warningTable.add(iwrb.getLocalizedString("new_user.ssn_warning","You have selected to create a user with no Personal ID, do you want to continue?"));
						mainTable.add(warningTable,2,1);
						ssnWarningDisplay = true;
						fullNameField.setContent(fullName);
						formNotComplete = false;
						buttonTable.remove(okButton);
						buttonTable.add(continueButton,1,1);
						if(primaryGroup != null || !primaryGroup.equals("")) {
	//						primaryGroupField.setValue(primaryGroup);
							Integer primaryGroupId = new Integer(primaryGroup);
							primaryGroupField.setSelectedGroup(primaryGroup,getGroupBusiness(iwc).getGroupByGroupID(primaryGroupId.intValue()).getName());
						}
													
					}
					//is addressed if the only the social security number is entered
					else if((ssn != null || !ssn.equals("")) && (fullName == null || fullName.equals(""))) {
						warningTable.add(iwrb.getLocalizedString("new_user.fullName_warning","You have selected to create a user with no name, do you want to continue?"));
						mainTable.add(warningTable,2,1);
						fullNameWarningDisplay = true;
						ssnField.setContent(ssn);
						formNotComplete = false;
						buttonTable.remove(okButton);
						buttonTable.add(continueButton,1,1);
						if(primaryGroup != null || !primaryGroup.equals("")) {
//							primaryGroupField.setValue(primaryGroup);	
							Integer primaryGroupId = new Integer(primaryGroup);
							primaryGroupField.setSelectedGroup(primaryGroup,getGroupBusiness(iwc).getGroupByGroupID(primaryGroupId.intValue()).getName());	
								
						}				
					}		
				}
				//is addressed if both name and social security number are entered
				else if (submit.equals("ok") && !formNotComplete) {
					commitCreation(iwc);
					if(isSetToClose) {
						close();
						setParentToReload();
					}				
				}
				//is addressed if the user submits entering only ssn or name
				//then name is set = ssn or ssn set = the primary key of the user (see commitCreation(iwc))
				else if (submit.equals("submit")) {
					commitCreation(iwc);
					if(isSetToClose) {
						close();
						setParentToReload();
					}
				}	
				else if (submit.equals("cancel")) {
					close();
				}
			}
		}		

	

	public UserBusiness getUserBusiness(IWApplicationContext iwc) {
		if (userBiz == null) {
			try {
				userBiz = (UserBusiness) com.idega.business.IBOLookup.getServiceInstance(iwc, UserBusiness.class);
			}
			catch (java.rmi.RemoteException rme) {
				throw new RuntimeException(rme.getMessage());
			}
		}
		return userBiz;
	}
	
	public GroupBusiness getGroupBusiness(IWApplicationContext iwc) {
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

	public String getBundleIdentifier() {
		return IW_BUNDLE_IDENTIFIER;
	}

}

