package com.idega.user.presentation;

import java.rmi.RemoteException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collection;
import java.util.Iterator;

import javax.ejb.FinderException;

import com.idega.block.entity.business.EntityToPresentationObjectConverter;
import com.idega.block.entity.event.EntityBrowserEvent;
import com.idega.block.entity.presentation.EntityBrowser;
import com.idega.block.entity.presentation.converter.CheckBoxConverter;
import com.idega.business.IBOLookup;
import com.idega.event.IWStateMachine;
import com.idega.idegaweb.IWMainApplication;
import com.idega.idegaweb.IWResourceBundle;
import com.idega.idegaweb.help.presentation.Help;
import com.idega.idegaweb.presentation.StyledIWAdminWindow;
import com.idega.presentation.IWContext;
import com.idega.presentation.Table;
import com.idega.presentation.text.Text;
import com.idega.presentation.ui.Form;
import com.idega.presentation.ui.GenericButton;
import com.idega.presentation.ui.StyledButton;
import com.idega.presentation.ui.SubmitButton;
import com.idega.user.app.Toolbar;
import com.idega.user.app.UserApplicationMenuAreaPS;
import com.idega.user.business.GroupBusiness;
import com.idega.user.data.Group;
import com.idega.util.IWColor;

/**
 * <p>Title: idegaWeb</p>
 * <p>Description: </p>
 * <p>Copyright: Copyright (c) 2003</p>
 * <p>Company: idega Software</p>
 * @author <a href="thomas@idega.is">Thomas Hilbig</a>
 * @version 1.0
 * Created on Apr 14, 2003
 */
public class MassMovingWindow extends StyledIWAdminWindow {
    
  private static final String IW_BUNDLE_IDENTIFIER = "com.idega.user";  
  
  public static final String EVENT_NAME = "mass_moving";
  
  public static final String MOVE_SELECTED_GROUPS = "move_selected_groups";
  public static final String SELECTED_CHECKED_GROUPS_KEY = "selected_checked_groups_key";
  
  private static final String SHOW_CHILDREN_OF_GROUP_ACTION = "show_children_of_group_action";
  private static final String SHOW_ERROR_MESSAGE_ACTION = "error_message";
  
  public static final String GROUP_TYPE_CLUB = "iwme_club";
  public static final String GROUP_TYPE_CLUB_DIVISION = "iwme_club_division";
  public static final String GROUP_TYPE_CLUB_PLAYER = "iwme_club_player";
  private static final String[] VISIBLE_GROUPS = new String[] {GROUP_TYPE_CLUB_DIVISION,GROUP_TYPE_CLUB_PLAYER};
  
  private static final String HELP_TEXT_KEY = "mass_moving_window";
  
  private String mainTableStyle = "main";
  private String parentGroupType;
  public static final String PRM_PARENT_GROUP_TYPE = "par_type";
  
  // display settings
  private final int NUMBER_OF_ROWS = 40;
  
  private Group group;
  private UserApplicationMenuAreaPS groupProviderState;
  private BasicUserOverviewPS actionListener;
  
  public MassMovingWindow() {
  		setWidth(440);
  		setHeight(300);
  }

  public String getBundleIdentifier() {
    return IW_BUNDLE_IDENTIFIER;
  }

  public void main(IWContext iwc) throws Exception {
    IWResourceBundle iwrb = getResourceBundle(iwc);
    setTitle(iwrb.getLocalizedString("massmovingWindow.title", "Mass moving automatic"));
    addTitle(iwrb.getLocalizedString("massmovingWindow.title", "Mass moving automatic"), TITLE_STYLECLASS);
		
    
    String action = parseRequest(iwc);
    if (SHOW_CHILDREN_OF_GROUP_ACTION.equals(action)) {
      showListOfChildren(iwrb, iwc);
    }
    else {
      // show error message
      showErrorContent(iwrb, iwc);
    }
  }

  private String parseRequest(IWContext iwc) {
   // try to get the group 
    if (iwc.isParameterSet(Toolbar.SELECTED_GROUP_PROVIDER_PRESENTATION_STATE_ID_KEY))  {
      String selectedGroupProviderStateId = iwc.getParameter(Toolbar.SELECTED_GROUP_PROVIDER_PRESENTATION_STATE_ID_KEY);
      GroupBusiness groupBusiness = getGroupBusiness(iwc);
      try {
        // try to get the selected group  
        IWStateMachine stateMachine = (IWStateMachine) IBOLookup.getSessionInstance(iwc, IWStateMachine.class);
        this.groupProviderState = (UserApplicationMenuAreaPS) stateMachine.getStateFor(selectedGroupProviderStateId, UserApplicationMenuAreaPS.class);
        Integer selectedGroupId =  this.groupProviderState.getSelectedGroupId();
        if (selectedGroupId == null)  {
          return SHOW_ERROR_MESSAGE_ACTION;
        }
        this.group = groupBusiness.getGroupByGroupID(selectedGroupId.intValue());
        // try to get the action listener
        //TODO thomas change this in the way that actually the userApplicationMainAreaPs is used
        //actionListener = (UserApplicationMainAreaPS) stateMachine.getStateFor(actionListenerStateId, UserApplicationMainAreaPS.class);
        String code = IWMainApplication.getEncryptedClassName(BasicUserOverview.class);
        //String code2 = IWMainApplication.getEncryptedClassName(UserApplicationMainAreaPS.class);
        //String string = IWMainApplication.getHashCodedClassName("6893");
        code = ":" + code;
        this.actionListener = (BasicUserOverviewPS) stateMachine.getStateFor( code , BasicUserOverviewPS.class);
      }
      catch (RemoteException ex)  {
        throw new RuntimeException(ex.getMessage());
      }
      catch (FinderException ex)  {
        throw new RuntimeException(ex.getMessage());
      }
      // type of group correct?
      this.parentGroupType = this.group.getGroupType();
      if (GROUP_TYPE_CLUB.equals(this.parentGroupType) ||
          GROUP_TYPE_CLUB_DIVISION.equals(this.parentGroupType))  {
        return SHOW_CHILDREN_OF_GROUP_ACTION;
      }
    }
    return SHOW_ERROR_MESSAGE_ACTION;
  }
    
  private void  showListOfChildren(IWResourceBundle iwrb, IWContext iwc) {
    // set event
    EntityBrowserEvent event = new EntityBrowserEvent();
    event.setEventName(EVENT_NAME);
    event.setSource(this);
    // set form
    Form form = new Form();
    form.addParameter(MOVE_SELECTED_GROUPS,"w");
    form.setName("mass_form");
    form.addEventModel(event, iwc);
    form.addParameter(PRM_PARENT_GROUP_TYPE,this.parentGroupType);
    // define headline
    String headlineString = iwrb.getLocalizedString("mm_choose_desired_divisions_or_groups_to_age_and_gender_sort", "Choose the desired divisions or groups to age and gender sort");
    Text headline = new Text(headlineString);
    headline.setBold();
    // get entities
    Collection coll = new ArrayList(getChildrenOfGroup(iwc));
    Collection types = Arrays.asList(VISIBLE_GROUPS);
    for (Iterator iter = coll.iterator(); iter.hasNext();) {
		Group gr = (Group) iter.next();
		if(!types.contains(gr.getGroupType())){
			iter.remove();
		}
	}
    
    // define browser
    EntityBrowser browser = getBrowser(coll);
    // define button
    Help help = getHelp(HELP_TEXT_KEY);
    GenericButton moveButton = new GenericButton("move", iwrb.getLocalizedString("move", "Move to"));
    StyledButton move = new StyledButton(moveButton);
    
    SubmitButton closeButton = new SubmitButton(iwrb.getLocalizedString("close", "Close"));
    StyledButton close = new StyledButton(closeButton);
    String wait = iwrb.getLocalizedString("mm_please_wait_processing_request", "Please wait. Processing request");
    wait += "....";
    closeButton.setOnClick("window.close(); return false;");
    moveButton.setOnClick("window.opener.parent.frames['iw_event_frame'].document.write('"+wait+"'); mass_form.submit(); window.close();");
    // assemble table

    Table mainTable = new Table();
		mainTable.setWidth(Table.HUNDRED_PERCENT);
		mainTable.setCellpadding(0);
		mainTable.setCellspacing(0);
    
		Table table = new Table(1,3);
    table.setStyleClass(this.mainTableStyle);
    table.setWidth(Table.HUNDRED_PERCENT);
    table.setHeight(160);
    table.setCellpadding(0);
    table.setCellspacing(5);
    table.setVerticalAlignment(1,1,Table.VERTICAL_ALIGN_TOP);
    table.setVerticalAlignment(1,2,Table.VERTICAL_ALIGN_TOP);
    table.add(headline,1 ,1);
    table.add(browser,1,2);
    
    Table buttons = new Table();
    buttons.setStyleClass(this.mainTableStyle);
    buttons.setCellpadding(0);
		buttons.setCellspacing(5);
		buttons.setWidth(Table.HUNDRED_PERCENT);
		buttons.add(help,1,1);
		buttons.setAlignment(2,1,Table.HORIZONTAL_ALIGN_RIGHT);
		
		Table buttonTable = new Table(3, 1);
		buttonTable.setCellpaddingAndCellspacing(0);
		buttonTable.setWidth(2, 5);
		buttonTable.add(move,1,1);
		buttonTable.add(close,3,1);
		buttons.add(buttonTable, 2, 1);
   
		mainTable.add(table,1,1);
		mainTable.setHeight(2, 5);
		mainTable.add(buttons,1,3);
    
    form.add(mainTable);
    add(form,iwc);
    // add action listener
    addActionListener(this.actionListener);
  }
    
  private void showErrorContent(IWResourceBundle iwrb, IWContext iwc) {
    String errorMessage = iwrb.getLocalizedString("mm_select_club", "Select a club or club division first, please.");
    Text error = new Text(errorMessage);
    error.setBold();
    // define button
    SubmitButton close = new SubmitButton(iwrb.getLocalizedString("close", "Close"));
    close.setOnClick("window.close(); return false;");
    StyledButton closeButton = new StyledButton(close);
    
    // assemble table
    Table table = new Table(1,2);
    table.setWidth(Table.HUNDRED_PERCENT);
    table.setCellspacing(0);
    table.setCellpadding(5);
    table.setStyleClass(this.mainTableStyle);
    table.add(error,1,1);
    table.add(closeButton,1,2);   
    
    Form form = new Form(); 
    form.add(table);
    add(form,iwc);
  }
    

  private Collection getChildrenOfGroup(IWContext iwc) {
    Collection coll = null;
    try {
       coll = getGroupBusiness(iwc).getChildGroups(this.group);
    }
    catch (Exception ex)  {
      throw new RuntimeException(ex.getMessage());
    }
    // if the group is a club show only children that are divisions
    String groupType = this.group.getGroupType();
    if (GROUP_TYPE_CLUB.equals(groupType))  {
      Collection result = new ArrayList();
      Iterator iterator = coll.iterator();
      while (iterator.hasNext())  {
        Group child = (Group) iterator.next();
        if (GROUP_TYPE_CLUB_DIVISION.equals(child.getGroupType()))  {
          result.add(child);
        }
      }
      return result;
    }
    return coll;

  }
        
  // service method  
  private GroupBusiness getGroupBusiness(IWContext iwc) {
    try {
      return (GroupBusiness) IBOLookup.getServiceInstance(iwc,GroupBusiness.class);
    }
    catch (RemoteException ex) {
      throw new RuntimeException(ex.getMessage());
    }
  } 
    
  private EntityBrowser getBrowser(Collection entities)  {
    // define checkbox button converter class
    EntityToPresentationObjectConverter checkBoxConverter = new CheckBoxConverter(SELECTED_CHECKED_GROUPS_KEY); 
    // set default columns
    //String columnName = GroupBMPBean.getNameColumnName();
    String nameKey = Group.class.getName()+".NAME"; //+ GroupBMPBean.getNameColumnName();
    EntityBrowser browser = EntityBrowser.getInstanceUsingExternalForm();
    browser.setAcceptUserSettingsShowUserSettingsButton(false, false);
    // set number of rows
    browser.setDefaultNumberOfRows(this.NUMBER_OF_ROWS);
    browser.setEntities(EVENT_NAME, entities);
    browser.setWidth(Table.HUNDRED_PERCENT);
    // fonts
    Text column = new Text();
    column.setBold();
    browser.setColumnTextProxy(column);
    // set color of rows
    browser.setColorForEvenRows(IWColor.getHexColorString(246, 246, 247));
    browser.setColorForOddRows("#FFFFFF");
    // set columns 
    browser.setDefaultColumn(1, nameKey);
    browser.setMandatoryColumn(1, "Choose");
    // set special converters
    browser.setEntityToPresentationConverter("Choose", checkBoxConverter);
    return browser;
  }    
}