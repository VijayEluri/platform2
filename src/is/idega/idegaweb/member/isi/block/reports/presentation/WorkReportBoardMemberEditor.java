package is.idega.idegaweb.member.isi.block.reports.presentation;

import is.idega.idegaweb.member.isi.block.reports.business.WorkReportBusiness;
import is.idega.idegaweb.member.isi.block.reports.data.WorkReport;
import is.idega.idegaweb.member.isi.block.reports.data.WorkReportBoardMember;
import is.idega.idegaweb.member.isi.block.reports.data.WorkReportGroupHome;
import is.idega.idegaweb.member.isi.block.reports.data.WorkReportBoardMember;
import is.idega.idegaweb.member.isi.block.reports.data.WorkReportGroup;
import is.idega.idegaweb.member.util.IWMemberConstants;

import java.rmi.RemoteException;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.Comparator;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.TreeMap;

import javax.ejb.CreateException;
import javax.ejb.FinderException;
import javax.ejb.RemoveException;

import com.idega.block.entity.business.EntityToPresentationObjectConverter;
import com.idega.block.entity.data.EntityPath;
import com.idega.block.entity.data.EntityPathValueContainer;
import com.idega.block.entity.data.EntityValueHolder;
import com.idega.block.entity.presentation.EntityBrowser;
import com.idega.block.entity.presentation.converters.CheckBoxConverter;
import com.idega.block.entity.presentation.converters.ConverterConstants;
import com.idega.block.entity.presentation.converters.DropDownMenuConverter;
import com.idega.block.entity.presentation.converters.DropDownPostalCodeConverter;
import com.idega.block.entity.presentation.converters.EditOkayButtonConverter;
import com.idega.block.entity.presentation.converters.OptionProvider;
import com.idega.block.entity.presentation.converters.TextEditorConverter;
import com.idega.business.IBOLookup;
import com.idega.core.data.PostalCode;
import com.idega.data.EntityRepresentation;
import com.idega.data.IDOException;
import com.idega.data.IDOLookup;
import com.idega.data.IDOLookupException;
import com.idega.idegaweb.IWApplicationContext;
import com.idega.idegaweb.IWResourceBundle;
import com.idega.presentation.IWContext;
import com.idega.presentation.PresentationObject;
import com.idega.presentation.Table;
import com.idega.presentation.ui.Form;
import com.idega.presentation.ui.SubmitButton;

/**
 * <p>Title: idegaWeb</p>
 * <p>Description: </p>
 * <p>Copyright: Copyright (c) 2003</p>
 * <p>Company: idega Software</p>
 * @author <a href="thomas@idega.is">Thomas Hilbig</a>
 * @version 1.0
 * Created on Jul 3, 2003
 */
public class WorkReportBoardMemberEditor extends WorkReportSelector {
	
	private static final String STEP_NAME_LOCALIZATION_KEY = "workreportboardmembereditor.step_name";
  
  private static final String SUBMIT_CREATE_NEW_ENTRY_KEY = "submit_cr_new_entry_key";
  private static final String SUBMIT_SAVE_NEW_ENTRY_KEY = "submit_sv_new_entry_key";
  private static final String SUBMIT_DELETE_ENTRIES_KEY = "submit_del_new_entry_key";
  private static final String SUBMIT_CANCEL_KEY = "submit_cancel_key";
  
  private static final Integer NEW_ENTRY_ID_VALUE = new Integer(-1);
  protected static final String NO_LEAGUE_VALUE = "no_league_value";
  
  private static final String ACTION_SHOW_NEW_ENTRY = "action_show_new_entry";
  
  private static final String CHECK_BOX = "checkBox";

  // this varible is used by the inner class
  private static final String LEAGUE = "league";
  
  private static final List STATUS_OPTIONS;

  private static final String STATUS = WorkReportBoardMember.class.getName()+".STATUS";
  private static final String NAME = WorkReportBoardMember.class.getName()+".NAME";
  private static final String PERSONAL_ID = WorkReportBoardMember.class.getName()+".PERSONAL_ID";
  private static final String STREET_NAME = WorkReportBoardMember.class.getName()+".STREET_NAME";
  private static final String POSTAL_CODE_ID = PostalCode.class.getName()+".POSTAL_CODE_ID|POSTAL_CODE";
  private static final String HOME_PHONE = WorkReportBoardMember.class.getName()+".HOME_PHONE";
  private static final String WORK_PHONE = WorkReportBoardMember.class.getName()+".WORK_PHONE";
  private static final String FAX = WorkReportBoardMember.class.getName()+".FAX";
  private static final String EMAIL = WorkReportBoardMember.class.getName()+".EMAIL";
  
  // add these columns to this list that should be parsed
  private static List FIELD_LIST;
    
  static { 
    STATUS_OPTIONS = new ArrayList();
    STATUS_OPTIONS.add(IWMemberConstants.MEMBER_BOARD_STATUS_CHAIR_MAN);
    STATUS_OPTIONS.add(IWMemberConstants.MEMBER_BOARD_MEMBER);
    STATUS_OPTIONS.add(IWMemberConstants.MEMBER_CASHIER);
    STATUS_OPTIONS.add(IWMemberConstants.MEMBER_SECRETARY);
    
    FIELD_LIST = new ArrayList();
    FIELD_LIST.add(LEAGUE);
    FIELD_LIST.add(STATUS);
    FIELD_LIST.add(NAME);
    FIELD_LIST.add(PERSONAL_ID);
    FIELD_LIST.add(STREET_NAME);
    FIELD_LIST.add(POSTAL_CODE_ID);
    FIELD_LIST.add(HOME_PHONE);
    FIELD_LIST.add(WORK_PHONE);
    FIELD_LIST.add(FAX);
    FIELD_LIST.add(EMAIL);
  }
    
  public WorkReportBoardMemberEditor() {
    super();
    setStepNameLocalizableKey(STEP_NAME_LOCALIZATION_KEY);
  }  
  
  
  public void main(IWContext iwc) throws Exception {
    super.main(iwc);
    IWResourceBundle resourceBundle = getResourceBundle(iwc);
    
    if (this.getWorkReportId() != -1) {
			//sets this step as bold, if another class calls it this will be overwritten 
			setAsCurrentStepByStepLocalizableKey(STEP_NAME_LOCALIZATION_KEY);
      String action = parseAction(iwc);
      Form form = new Form();
      PresentationObject pres = getContent(iwc, resourceBundle, form, action);
      form.maintainParameters(this.getParametersToMaintain());
      form.add(pres);
      add(form);
    }
  }
  
  private String parseAction(IWContext iwc) {
    String action = "";
    // does the user want to cancel something?
    if (iwc.isParameterSet(SUBMIT_CANCEL_KEY)) {
      return action;
      // !! do nothing else !!
      // do not modify entry
      // do not create an entry
      // do not delete an entry
    }
    // does the user want to delete an existings entries?
    if (iwc.isParameterSet(SUBMIT_DELETE_ENTRIES_KEY)) {
      List entriesToDelete = CheckBoxConverter.getResultByParsingUsingDefaultKey(iwc);
      if (! entriesToDelete.isEmpty())  {
        deleteWorkReportBoardMembers(entriesToDelete, iwc);
        // !! do nothing else !!
        // do not modify entry
        // do not create an entry
        return action;
      }
    }
   // does the user want to edit a new entry?
    if (iwc.isParameterSet(SUBMIT_CREATE_NEW_ENTRY_KEY))  {
      action = ACTION_SHOW_NEW_ENTRY;
    }  
    // does the user want to save a new entry?
    if (iwc.isParameterSet(SUBMIT_SAVE_NEW_ENTRY_KEY))  {
      WorkReportBusiness workReportBusiness = getWorkReportBusiness(iwc);
      WorkReportBoardMember member = createWorkReportBoardMember();
      Iterator iterator = FIELD_LIST.iterator();
      while (iterator.hasNext())  {
        String field = (String) iterator.next();
        EntityPathValueContainer entityPathValueContainerFromTextEditor = 
          TextEditorConverter.getResultByEntityIdAndEntityPathShortKey(NEW_ENTRY_ID_VALUE, field, iwc);
        EntityPathValueContainer entityPathValueContainerFromDropDownMenu = 
          DropDownMenuConverter.getResultByEntityIdAndEntityPathShortKey(NEW_ENTRY_ID_VALUE, field, iwc);
        if (entityPathValueContainerFromTextEditor.isValid()) {
          setValuesOfWorkReportBoardMember(entityPathValueContainerFromTextEditor, member, workReportBusiness);
        }
        if (entityPathValueContainerFromDropDownMenu.isValid()) {
          setValuesOfWorkReportBoardMember(entityPathValueContainerFromDropDownMenu, member, workReportBusiness);
        }
      }
      member.store();
    }  
    // does the user want to modify an existing entity? 
    if (iwc.isParameterSet(ConverterConstants.EDIT_ENTITY_SUBMIT_KEY)) {
      WorkReportBusiness workReportBusiness = getWorkReportBusiness(iwc);
      String id = iwc.getParameter(ConverterConstants.EDIT_ENTITY_SUBMIT_KEY);
      Integer primaryKey = null;
      try {
        primaryKey = new Integer(id);
      }
      catch (NumberFormatException ex)  {
        System.err.println("[WorkReportBoardMemberEditor] Wrong primary key. Message is: " +
          ex.getMessage());
        ex.printStackTrace(System.err);
      }
      WorkReportBoardMember member = findWorkReportBoardMember(primaryKey, iwc);
      Iterator iterator = FIELD_LIST.iterator();
      while (iterator.hasNext())  {
        String field = (String) iterator.next();
        EntityPathValueContainer entityPathValueContainerFromTextEditor = 
          TextEditorConverter.getResultByEntityIdAndEntityPathShortKey(primaryKey, field, iwc);
        EntityPathValueContainer entityPathValueContainerFromDropDownMenu = 
          DropDownMenuConverter.getResultByEntityIdAndEntityPathShortKey(primaryKey, field, iwc);
        if (entityPathValueContainerFromTextEditor.isValid()) {
          setValuesOfWorkReportBoardMember(entityPathValueContainerFromTextEditor, member, workReportBusiness);
        }
        if (entityPathValueContainerFromDropDownMenu.isValid()) {
          setValuesOfWorkReportBoardMember(entityPathValueContainerFromDropDownMenu, member, workReportBusiness);
        }
      }
      member.store();
      return action;
    }  
      
      
//    // does the user want to modify an existing entity?
//    EntityPathValueContainer entityPathValueContainerFromTextEditor = TextEditorConverter.getResultByParsing(iwc);
//    EntityPathValueContainer entityPathValueContainerFromDropDownMenu = DropDownMenuConverter.getResultByParsing(iwc);
//    if (entityPathValueContainerFromTextEditor.isValid()) {
//      updateWorkReportBoardMember(entityPathValueContainerFromTextEditor, iwc);
//    }
//    if (entityPathValueContainerFromDropDownMenu.isValid()) {
//      updateWorkReportBoardMember(entityPathValueContainerFromDropDownMenu, iwc);
//    }
    return action;
  }
  
  private PresentationObject getContent(IWContext iwc, IWResourceBundle resourceBundle, Form form, String action) {
    WorkReportBusiness workReportBusiness = getWorkReportBusiness(iwc);
    try {
      // create data from the database
      workReportBusiness.createWorkReportData(getWorkReportId());
    } catch (RemoteException ex) {
      System.err.println(
        "[WorkReportBoardMemberEditor]: Can't retrieve WorkReportBusiness. Message is: "
          + ex.getMessage());
      ex.printStackTrace(System.err);
      throw new RuntimeException("[WorkReportBoardMemberEditor]: Can't retrieve WorkReportBusiness.");
    } 
    Collection coll;
    try {
      coll =
        workReportBusiness.getAllWorkReportBoardMembersForWorkReportId(
          getWorkReportId());
    } catch (RemoteException e) {
      System.err.println("[WorkReportMemberDataEditor] Can't get members. Message was: "+ e.getMessage());
      e.printStackTrace(System.err);
      coll = new ArrayList();
    }
    // create new entities
    List list = new ArrayList();
    Iterator iterator = coll.iterator();
    while (iterator.hasNext())  {
      WorkReportBoardMember member = (WorkReportBoardMember) iterator.next();
      WorkReportGroup league = null;
      try {
        league = member.getLeague();
      }
      catch (IDOException ex) {
        System.err.println(
          "[WorkReportBoardMemberEditor]: Can't retrieve league. Message is: "
            + ex.getMessage());
        ex.printStackTrace(System.err);
        throw new RuntimeException("[WorkReportBoardMemberEditor]: Can't retrieve league.");
      }
      // special case: league is null
      if (league == null) {
        WorkReportBoardMemberHelper helper = new WorkReportBoardMemberHelper(NO_LEAGUE_VALUE, member);
        list.add(helper);
      }
      else {
        String leagueName = league.getName();
        WorkReportBoardMemberHelper helper = new WorkReportBoardMemberHelper(leagueName, member);
        list.add(helper);
      }
    }
    // add a value holder for a new entry if desired
    if (ACTION_SHOW_NEW_ENTRY.equals(action)) {
      EntityValueHolder valueHolder = new EntityValueHolder(); 
      // trick , because postal code is a "foreign" column 
      valueHolder.setColumnValue("POSTAL_CODE_ID", valueHolder); 
      WorkReportBoardMemberHelper valueHolderHelper = new WorkReportBoardMemberHelper("", valueHolder);
      list.add(valueHolderHelper);
    }
    // sort list
    Comparator comparator = new Comparator()  {
      public int compare(Object first, Object second) {
        // check if the element is a new entity, a new entity should be shown first
        // the element is a new element if the primaryKey is less than zero.
        Integer primaryKey = (Integer) ((WorkReportBoardMemberHelper) first).getPrimaryKey();
        if (primaryKey.intValue() < 0) {
          return -1;
        }
        primaryKey = (Integer) ((WorkReportBoardMemberHelper) second).getPrimaryKey();
        if (primaryKey.intValue() < 0)  {
          return 1;
        }
        // sort according to the name of the league 
        String firstLeague = (String) ((WorkReportBoardMemberHelper) first).getColumnValue(LEAGUE);
        if (NO_LEAGUE_VALUE.equals(firstLeague))  {
          return -1;
        }
        String secondLeague = (String) ((WorkReportBoardMemberHelper) second).getColumnValue(LEAGUE);
        if (NO_LEAGUE_VALUE.equals(secondLeague))  {
          return 1;
        }
        int result = firstLeague.compareTo(secondLeague);
        // if the laegues are equal sort according to the status
        if (result == 0)  {
          String firstStatus = (String) ((WorkReportBoardMemberHelper) first).getColumnValue("STATUS");
          String secondStatus = (String) ((WorkReportBoardMemberHelper) second).getColumnValue("STATUS");
          firstStatus = (firstStatus == null) ? "" : firstStatus;
          secondStatus = (secondStatus == null) ? "" : secondStatus;
          int firstIndex = STATUS_OPTIONS.indexOf(firstStatus);
          int secondIndex = STATUS_OPTIONS.indexOf(secondStatus);
          return firstIndex - secondIndex;
        }
        return result;          
      }
    };
    Collections.sort(list, comparator);
    EntityBrowser browser = getEntityBrowser(list, resourceBundle, form);
    // put browser into a table
    Table mainTable = new Table(1,2);
    mainTable.add(browser, 1,1);
    // add buttons
    PresentationObject newEntryButton = (ACTION_SHOW_NEW_ENTRY.equals(action)) ? 
      getSaveNewEntityButton(resourceBundle) : getCreateNewEntityButton(resourceBundle);
    PresentationObject deleteEntriesButton = getDeleteEntriesButton(resourceBundle);
    PresentationObject cancelButton = getCancelButton(resourceBundle);
    // add buttons
    Table buttonTable = new Table(3,1);
    buttonTable.add(newEntryButton,1,1);
    buttonTable.add(deleteEntriesButton,2,1);
    buttonTable.add(cancelButton, 3,1);
    mainTable.add(buttonTable,1,2);
    return mainTable;
    
  }
  
  private PresentationObject getCreateNewEntityButton(IWResourceBundle resourceBundle) {
    String createNewEntityText = resourceBundle.getLocalizedString("wr_board_member_editor_create_new_entry", "New entry");
    SubmitButton button = new SubmitButton(createNewEntityText, SUBMIT_CREATE_NEW_ENTRY_KEY, "dummy_value");
    button.setAsImageButton(true);
    return button;
  }
  
  private PresentationObject getSaveNewEntityButton(IWResourceBundle resourceBundle)  {
    String saveNewEntityText = resourceBundle.getLocalizedString("wr_board_member_editor_save_new_entry", "Save");
    SubmitButton button = new SubmitButton(saveNewEntityText, SUBMIT_SAVE_NEW_ENTRY_KEY, "dummy_value");
    button.setAsImageButton(true);
    return button;
  }
  
  private PresentationObject getDeleteEntriesButton(IWResourceBundle resourceBundle)  {
    String deleteEntityText = resourceBundle.getLocalizedString("wr_board_member_editor_remove_entries", "Remove");
    SubmitButton button = new SubmitButton(deleteEntityText, SUBMIT_DELETE_ENTRIES_KEY, "dummy_value");
    button.setAsImageButton(true);
    return button;
  }    
 
  private PresentationObject getCancelButton(IWResourceBundle resourceBundle)  {
    String cancelText = resourceBundle.getLocalizedString("wr_board_member_editor_cancel", "Cancel");
    SubmitButton button = new SubmitButton(cancelText, SUBMIT_CANCEL_KEY, "dummy_value");
    button.setAsImageButton(true);
    return button;
  }     

  private EntityBrowser getEntityBrowser(Collection entities, IWResourceBundle resourceBundle, Form form)  {
    // define converter
    CheckBoxConverter checkBoxConverter = new CheckBoxConverter();
    TextEditorConverter textEditorConverter = new TextEditorConverter(form);
    textEditorConverter.maintainParameters(this.getParametersToMaintain());
    EntityToPresentationObjectConverter statusDropDownMenuConverter = getConverterForStatus(resourceBundle, form);
    EntityToPresentationObjectConverter leagueDropDownMenuConverter = getConverterForLeague(resourceBundle, form);
    EntityToPresentationObjectConverter dropDownPostalCodeConverter = getConverterForPostalCode(form);
    // define path short keys and map corresponding converters
    Object[] columns = {
      "okay", new EditOkayButtonConverter(),
      CHECK_BOX, checkBoxConverter,
      LEAGUE, leagueDropDownMenuConverter,
      STATUS, statusDropDownMenuConverter,
      NAME, textEditorConverter,
      PERSONAL_ID, textEditorConverter,
      STREET_NAME, textEditorConverter,
      POSTAL_CODE_ID, dropDownPostalCodeConverter,
      HOME_PHONE, textEditorConverter,
      WORK_PHONE, textEditorConverter,
      FAX, textEditorConverter,
      EMAIL, textEditorConverter};
    EntityBrowser browser = new EntityBrowser();
    browser.setLeadingEntity(WorkReportBoardMember.class);
    browser.setAcceptUserSettingsShowUserSettingsButton(false,false);
    if( entities!=null && !entities.isEmpty()) browser.setDefaultNumberOfRows(entities.size());
    // switch off the internal form of the browser
    browser.setUseExternalForm(true);
    for (int i = 0; i < columns.length; i+=2) {
      String column = (String) columns[i];
      EntityToPresentationObjectConverter converter = (EntityToPresentationObjectConverter) columns[i+1];
      browser.setMandatoryColumn(i, column);
      browser.setEntityToPresentationConverter(column, converter);
    }
    browser.setEntities("dummy_string", entities);
    return browser;
  }
  
  /**
   * Converter for status column
   */
  private EntityToPresentationObjectConverter getConverterForStatus(final IWResourceBundle resourceBundle, Form form) {
    DropDownMenuConverter converter = new DropDownMenuConverter(form);
    OptionProvider optionProvider = new OptionProvider() {
      
      Map optionMap = null;
      
      public Map getOptions(Object entity, EntityPath path, EntityBrowser browser, IWContext iwc) {
        if (optionMap == null)  {
          optionMap = new TreeMap();
          Iterator iterator = STATUS_OPTIONS.iterator();
          while (iterator.hasNext())  {
            String status = (String) iterator.next();
            String display = resourceBundle.getLocalizedString(status, status);
            optionMap.put(status,display);
          }
        }
        return optionMap;
      }
    };     
    converter.setOptionProvider(optionProvider); 
    converter.maintainParameters(getParametersToMaintain());
    return converter;
  }
  
  /**
   * Converter for league column 
   */
  private EntityToPresentationObjectConverter getConverterForLeague(final IWResourceBundle resourceBundle, Form form) {
    DropDownMenuConverter converter = new DropDownMenuConverter(form) {
      protected Object getValue(
        Object entity,
        EntityPath path,
        EntityBrowser browser,
        IWContext iwc)  {
          return ((EntityRepresentation) entity).getColumnValue(LEAGUE);
        }
      };        

        
    OptionProvider optionProvider = new OptionProvider() {
      
    Map optionMap = null;
      
    public Map getOptions(Object entity, EntityPath path, EntityBrowser browser, IWContext iwc) {
      if (optionMap == null)  {
        optionMap = new TreeMap();
        WorkReportBusiness business = getWorkReportBusiness(iwc);
        Collection coll = null;
        try {
          coll = business.getAllLeagueWorkReportGroupsForYear(getYear());
        }
        catch (RemoteException ex) {
          System.err.println(
            "[WorkReportBoardMemberEditor]: Can't retrieve WorkReportBusiness. Message is: "
              + ex.getMessage());
          ex.printStackTrace(System.err);
          throw new RuntimeException("[WorkReportBoardMemberEditor]: Can't retrieve WorkReportBusiness.");
        }
        Iterator collIterator = coll.iterator();
        while (collIterator.hasNext())  {
          WorkReportGroup league = (WorkReportGroup) collIterator.next();
          String name = league.getName(); 
          String display = resourceBundle.getLocalizedString(name, name);
          optionMap.put(name, display);
          }
        }
        // add default value: no league (because main board members are not assigned to a league)
        optionMap.put(NO_LEAGUE_VALUE, resourceBundle.getLocalizedString("wr_board_member_editor_no_league","no league"));
        return optionMap;
      }
    };     
    converter.setOptionProvider(optionProvider); 
    converter.maintainParameters(getParametersToMaintain());
    return converter;
  }   
   
  /**
   * converter for postal code column
   */
  private EntityToPresentationObjectConverter getConverterForPostalCode(Form form) {
    DropDownPostalCodeConverter dropDownPostalCodeConverter = new DropDownPostalCodeConverter(form);
    dropDownPostalCodeConverter.setCountry("Iceland");
    dropDownPostalCodeConverter.maintainParameters(getParametersToMaintain());
    return dropDownPostalCodeConverter;
  }    
  
  
  /** business method: delete WorkReportBoardMembers.
   * @param ids - List of primaryKeys (Integer)
   */
  private void deleteWorkReportBoardMembers(List ids, IWContext iwc) {
    Iterator iterator = ids.iterator();
    while (iterator.hasNext())  {
      Integer id = (Integer) iterator.next();
      WorkReportBoardMember member = findWorkReportBoardMember(id, iwc);
      if (member != null) {
        try {
          member.remove();
        }
        catch (RemoveException ex) {
          System.err.println(
            "[WorkReportBoardMemberEditor]: Can't remove WorkReportBoardMember. Message is: "
              + ex.getMessage());
          ex.printStackTrace(System.err);
          // do nothing
        }
      }
    }
    
  }
    
  
  // business method: create
  private WorkReportBoardMember createWorkReportBoardMember()  {
    WorkReportBoardMember workReportBoardMember = null;
    try {
      workReportBoardMember =
        (WorkReportBoardMember) IDOLookup.create(WorkReportBoardMember.class);
    } catch (IDOLookupException e) {
      System.err.println("[WorkReportBoardMemberEditor] Could not lookup home of WorkReportBoardMember. Message is: "+ e.getMessage());
      e.printStackTrace(System.err);
    } catch (CreateException e) {
      System.err.println("[WorkReportBoardMemberEditor] Could not create new WorkReportBoardMember. Message is: "+ e.getMessage());
      e.printStackTrace(System.err);
    }
    workReportBoardMember.setReportId(getWorkReportId());
    workReportBoardMember.store();
    return workReportBoardMember;
  }
  
  /** business method: find
   * @param primaryKey
   * @return desired WorkReportMember or null if not found
   */
  private WorkReportBoardMember findWorkReportBoardMember(Integer primaryKey, IWContext iwc) {
    WorkReportBoardMember member = null;
    WorkReportBusiness workReportBusiness = getWorkReportBusiness(iwc);
    try {
      member = workReportBusiness.getWorkReportBoardMemberHome().findByPrimaryKey(primaryKey);
    }
    catch (RemoteException ex) {
      System.err.println(
        "[WorkReportBoardMemberEditor]: Can't retrieve WorkReportBoardMember. Message is: "
          + ex.getMessage());
      ex.printStackTrace(System.err);
      throw new RuntimeException("[WorkReportBoardMemberEditor]: Can't retrieve WorkReportBoardMember.");
    }
    catch (FinderException ex)  {
      System.err.println(
      "[WorkReportBoardMemberEditor]: Can't find WorkReportBoardMember, Message is: "
        + ex.getMessage());
      ex.printStackTrace(System.err);
    }  
    return member;
  }
  
  // business method: set values (invoked by 'update' or 'create')
  private void setValuesOfWorkReportBoardMember(EntityPathValueContainer valueContainer, WorkReportBoardMember member, WorkReportBusiness workReportBusiness)  {
    String pathShortKey = valueContainer.getEntityPathShortKey();
    Object value = valueContainer.getValue();
    
    if (pathShortKey.equals(STATUS))  {
      member.setStatus(value.toString());
    }
    else if (pathShortKey.equals(NAME)) {
      member.setName(value.toString());
    }
    else if (pathShortKey.equals(PERSONAL_ID))  {
      member.setPersonalId(value.toString());
    }
    else if (pathShortKey.equals(STREET_NAME))  {
      member.setStreetName(value.toString());
    }
    else if(pathShortKey.equals(POSTAL_CODE_ID))  {
      try {
        int postalCode = Integer.parseInt(value.toString());
        member.setPostalCodeID(postalCode);
      }
      catch (NumberFormatException ex)  {
      }
    }
    else if(pathShortKey.equals(HOME_PHONE))  {
      member.setHomePhone(value.toString());
    }
    else if (pathShortKey.equals(WORK_PHONE)) {
      member.setWorkPhone(value.toString());
    }
    else if (pathShortKey.equals(FAX))  {
      member.setFax(value.toString());
    }
    else if (pathShortKey.equals(EMAIL))  {
      member.setEmail(value.toString());
    }
    else if (pathShortKey.equals(LEAGUE)) {
      // special case, sometimes there is not a previous value
      Object previousValue = valueContainer.getPreviousValue();
      String oldWorkGroupName = (previousValue == null) ? null : previousValue.toString();
      String newWorkGroupName = value.toString();
      if (NO_LEAGUE_VALUE.equals(newWorkGroupName)) {
        // the entry shall not reference a league, set name to null 
        newWorkGroupName = null;
      }
      if (NO_LEAGUE_VALUE.equals(oldWorkGroupName))  {
        // the entry did not reference a league, set name to null
        oldWorkGroupName = null;
      }
      int year = getYear();
      try {
        workReportBusiness.changeWorkReportGroupOfEntity(oldWorkGroupName, year, newWorkGroupName, year, member);
      }
      catch (RemoteException ex) {
        System.err.println(
          "[WorkReportBoardMemberEditor]: Can't retrieve WorkReportBusiness. Message is: "
            + ex.getMessage());
        ex.printStackTrace(System.err);
        throw new RuntimeException("[WorkReportBoardMemberEditor]: Can't retrieve WorkReportBusiness.");
      }
    }
  }


  /** 
   * WorkReportBoardMemberHelper:
   * Inner class, represents a WorkReportBoardMember but with the league as additional value.
   *
   */ 
    
  class WorkReportBoardMemberHelper implements EntityRepresentation {
    
    String league = null;
    Object member = null;
    
    public WorkReportBoardMemberHelper()  {
    }
    
    public WorkReportBoardMemberHelper(String league, Object member) {
      this.league = league;
      this.member = member;
    }
    
    public void setLeague(String league)  {
      this.league = league;
    }
    
    public void setMember(WorkReportBoardMember member) {
      this.member = member;
    }
    
    public Object getColumnValue(String columnName) {
      if (LEAGUE.equals(columnName))  {
        return league;
      }
      return ((EntityRepresentation) member).getColumnValue(columnName);
    }  
    
    public Object getPrimaryKey() {
      return ((EntityRepresentation) member).getPrimaryKey();
    }
  }

} 


  