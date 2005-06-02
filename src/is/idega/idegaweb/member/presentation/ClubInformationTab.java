/*
 * Created on Mar 11, 2003
 * 
 * To change this generated comment go to Window>Preferences>Java>Code
 * Generation>Code and Comments
 */
package is.idega.idegaweb.member.presentation;

import is.idega.idegaweb.member.business.MemberUserBusiness;
import is.idega.idegaweb.member.business.plugins.ClubInformationPluginBusiness;
import is.idega.idegaweb.member.util.IWMemberConstants;

import java.rmi.RemoteException;
import java.util.GregorianCalendar;
import java.util.Hashtable;
import java.util.Iterator;
import java.util.List;

import javax.ejb.FinderException;

import com.idega.data.IDOLookupException;
import com.idega.idegaweb.IWApplicationContext;
import com.idega.idegaweb.IWBundle;
import com.idega.idegaweb.IWResourceBundle;
import com.idega.idegaweb.help.presentation.Help;
import com.idega.presentation.IWContext;
import com.idega.presentation.Image;
import com.idega.presentation.Table;
import com.idega.presentation.text.Text;
import com.idega.presentation.ui.CheckBox;
import com.idega.presentation.ui.DateInput;
import com.idega.presentation.ui.DropdownMenu;
import com.idega.presentation.ui.TextInput;
import com.idega.user.data.Group;
import com.idega.user.data.GroupHome;
import com.idega.user.presentation.UserGroupTab;
import com.idega.util.IWTimestamp;

/**
 * @author palli
 * 
 * To change this generated comment go to Window>Preferences>Java>Code
 * Generation>Code and Comments
 */
public class ClubInformationTab extends UserGroupTab {

	private static final String IW_BUNDLE_IDENTIFIER = "is.idega.idegaweb.member";

	private static final String TAB_NAME = "cit_tab_name";

	private static final String DEFAULT_TAB_NAME = "Club Information";

	private static final String MEMBER_HELP_BUNDLE_IDENTIFIER = "is.idega.idegaweb.member.isi";

	private static final String HELP_TEXT_KEY = "club_information_tab";

	private TextInput _numberField;

	private TextInput _ssnField;

	private DateInput _foundedField;

	//	private DropdownMenu _typeField;
	private CheckBox _memberUMFIField;

	private DropdownMenu _makeField;

//	private DropdownMenu _connectionToSpecialField;

	private Text _regionalUnionField;

	private DropdownMenu _statusField;

	private CheckBox _inOperationField;

	private CheckBox _usingMemberSystemField;

	private Text _numberText;

	private Text _ssnText;

	private Text _foundedText;

	//	private Text _typeText;
	private Text _memberUMFIText;

	private Text _makeText;

//	private Text _connectionToSpecialText;

	private Text _regionalUnionText;

	private Text _statusText;

	private Text _inOperationText;

	private Text _usingMemberSystemText;

	private String _numberFieldName;

	private String _ssnFieldName;

	private String _foundedFieldName;

	private String _typeFieldName;

	private String _memberUMFIFieldName;

	private String _makeFieldName;

//	private String _connectionToSpecialFieldName;

	private String _regionalUnionFieldName;

	private String _statusFieldName;

	private String _inOperationFieldName;

	private String _usingMemberSystemFieldName;

	private IWResourceBundle iwrb;

	public ClubInformationTab() {
		super();
	}

	public ClubInformationTab(Group group) {
		this();
		setGroupId(((Integer) group.getPrimaryKey()).intValue());
	}

	public String getBundleIdentifier() {
		return IW_BUNDLE_IDENTIFIER;
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see com.idega.user.presentation.UserGroupTab#initializeFieldNames()
	 */
	public void initializeFieldNames() {
		_numberFieldName = "cit_number";
		_ssnFieldName = "cit_ssn";
		_foundedFieldName = "cit_founded";
		_typeFieldName = "cit_type";
		_memberUMFIFieldName = "cit_memberOfUMFI";
		_makeFieldName = "cit_make";
//		_connectionToSpecialFieldName = "cit_special";
		_regionalUnionFieldName = "cit_regional";
		_statusFieldName = "cit_status";
		_inOperationFieldName = "cit_operation";
		_usingMemberSystemFieldName = "cit_usingSystem";
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see com.idega.user.presentation.UserGroupTab#initializeFieldValues()
	 */
	public void initializeFieldValues() {
		fieldValues = new Hashtable();
		fieldValues.put(_numberFieldName, "");
		fieldValues.put(_ssnFieldName, "");
		fieldValues.put(_foundedFieldName, new IWTimestamp().getDate().toString());
		fieldValues.put(_typeFieldName, "");
		fieldValues.put(_memberUMFIFieldName, new Boolean(false));
		fieldValues.put(_makeFieldName, "");
//		fieldValues.put(_connectionToSpecialFieldName, "");
		fieldValues.put(_regionalUnionFieldName, "");
		fieldValues.put(_statusFieldName, "");
		fieldValues.put(_inOperationFieldName, new Boolean(false));
		fieldValues.put(_usingMemberSystemFieldName, new Boolean(false));
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see com.idega.user.presentation.UserGroupTab#updateFieldsDisplayStatus()
	 */
	public void updateFieldsDisplayStatus() {
		lineUpFields();
		String number = (String) fieldValues.get(_numberFieldName);
		_numberField.setContent(number);
		
		_ssnField.setContent((String) fieldValues.get(_ssnFieldName));
		_foundedField.setContent((String) fieldValues.get(_foundedFieldName));
		//		_typeField.setSelectedElement((String)
		// fieldValues.get(_typeFieldName));
		_memberUMFIField.setChecked(((Boolean) fieldValues.get(_memberUMFIFieldName)).booleanValue());
		String make = (String) fieldValues.get(_makeFieldName);
//		String connection = (String) fieldValues.get(_connectionToSpecialFieldName);
//		_connectionToSpecialField.setSelectedElement(connection);
		_makeField.removeElements();
/*		if (connection != null && !connection.equals("")
				&& make.equals(IWMemberConstants.META_DATA_CLUB_STATUS_SINGLE_DIVISION_CLUB)) {
			_connectionToSpecialField.setDisabled(false);
			_connectionToSpecialField.setOnChange("alert('"
					+ iwrb.getLocalizedString("clubinformationtab.cannot_change_msg", "You can not change this field!")
					+ "')");
			_connectionToSpecialField.setToSubmit();
			_makeField.addMenuElement(IWMemberConstants.META_DATA_CLUB_STATUS_SINGLE_DIVISION_CLUB,
					iwrb.getLocalizedString("clubinformationtab.single_division_club", "Single division"));
		}
		else {*/
/*			if (make.equals(IWMemberConstants.META_DATA_CLUB_STATUS_MULTI_DIVISION_CLUB)
					|| make.equals(IWMemberConstants.META_DATA_CLUB_STATUS_NO_MEMBERS_CLUB)) {
				_connectionToSpecialField.setDisabled(true);
			}
			else {
				_connectionToSpecialField.setDisabled(false);
			}*/
			_makeField.addMenuElement("-1", iwrb.getLocalizedString("clubinformationtab.choose_make", "Choose type..."));
			//		_makeField.addMenuElement(IWMemberConstants.META_DATA_CLUB_STATUS_MULTI_DIVISION_CLUB,
			// iwrb.getLocalizedString(
			//				"clubinformationtab.empty", "Empty"));
			_makeField.addMenuElement(IWMemberConstants.META_DATA_CLUB_STATUS_MULTI_DIVISION_CLUB,
					iwrb.getLocalizedString("clubinformationtab.multi_division_club", "Multi divisional"));
/*			_makeField.addMenuElement(IWMemberConstants.META_DATA_CLUB_STATUS_SINGLE_DIVISION_CLUB,
					iwrb.getLocalizedString("clubinformationtab.single_division_club", "Single division"));*/
			_makeField.addMenuElement(IWMemberConstants.META_DATA_CLUB_STATUS_NO_MEMBERS_CLUB, iwrb.getLocalizedString(
					"clubinformationtab.club_with_no_players", "No players"));
//			_makeField.setToEnableWhenSelected(_connectionToSpecialFieldName,
//					IWMemberConstants.META_DATA_CLUB_STATUS_SINGLE_DIVISION_CLUB);
//		}
		_makeField.setSelectedElement(make);
		_regionalUnionField.setText((String) fieldValues.get(_regionalUnionFieldName));
		_statusField.setSelectedElement((String) fieldValues.get(_statusFieldName));
		_inOperationField.setChecked(((Boolean) fieldValues.get(_inOperationFieldName)).booleanValue());
		_usingMemberSystemField.setChecked(((Boolean) fieldValues.get(_usingMemberSystemFieldName)).booleanValue());
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see com.idega.user.presentation.UserGroupTab#initializeFields()
	 */
	public void initializeFields() {
		IWContext iwc = IWContext.getInstance();
		iwrb = getResourceBundle(iwc);
		setName(iwrb.getLocalizedString(TAB_NAME, DEFAULT_TAB_NAME));
		_numberField = new TextInput(_numberFieldName);
		_ssnField = new TextInput(_ssnFieldName);
		_foundedField = new DateInput(_foundedFieldName);
		_foundedField.setYearRange(1900, GregorianCalendar.getInstance().get(GregorianCalendar.YEAR));
		//_typeField = new DropdownMenu(_typeFieldName);
		_memberUMFIField = new CheckBox(_memberUMFIFieldName);
		_memberUMFIField.setWidth("10");
		_memberUMFIField.setHeight("10");
		_makeField = new DropdownMenu(_makeFieldName);
//		_connectionToSpecialField = new DropdownMenu(_connectionToSpecialFieldName);
		_regionalUnionField = new Text();
		_statusField = new DropdownMenu(_statusFieldName);
		_inOperationField = new CheckBox(_inOperationFieldName);
		_inOperationField.setWidth("10");
		_inOperationField.setHeight("10");
		_usingMemberSystemField = new CheckBox(_usingMemberSystemFieldName);
		_usingMemberSystemField.setWidth("10");
		_usingMemberSystemField.setHeight("10");
		_statusField.addMenuElement(IWMemberConstants.META_DATA_CLUB_STATE_ACTIVE, iwrb.getLocalizedString(
				"clubinformationtab.state_active", "Active"));
		_statusField.addMenuElement(IWMemberConstants.META_DATA_CLUB_STATE_INACTIVE, iwrb.getLocalizedString(
				"clubinformationtab.state_inactive", "Inactive"));
		_statusField.addMenuElement(IWMemberConstants.META_DATA_CLUB_STATE_COMPETITION_BAN, iwrb.getLocalizedString(
				"clubinformationtab.state_banned_from_comp", "Competition ban"));
		_statusField.setSelectedElement(IWMemberConstants.META_DATA_CLUB_STATE_ACTIVE);
/*		List special = null;
		try {
			special = (List) ((GroupHome) com.idega.data.IDOLookup.getHome(Group.class)).findGroupsByType(IWMemberConstants.GROUP_TYPE_LEAGUE);
		}
		catch (Exception ex) {
			ex.printStackTrace();
		}
		if (special != null) {
			final Collator collator = Collator.getInstance(iwc.getLocale());
			Collections.sort(special, new Comparator() {

				public int compare(Object arg0, Object arg1) {
					return collator.compare(((Group) arg0).getName(), ((Group) arg1).getName());
				}
			});
			_connectionToSpecialField.addMenuElement("-1", iwrb.getLocalizedString("clubinformationtab.choose_reg_un",
					"Choose a regional union..."));
			Iterator it = special.iterator();
			while (it.hasNext()) {
				Group spec = (Group) it.next();
				_connectionToSpecialField.addMenuElement(((Integer) spec.getPrimaryKey()).intValue(), spec.getName());
			}
		}*/
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see com.idega.user.presentation.UserGroupTab#initializeTexts()
	 */
	public void initializeTexts() {
		IWContext iwc = IWContext.getInstance();
		IWResourceBundle iwrb = getResourceBundle(iwc);
		_numberText = new Text(iwrb.getLocalizedString(_numberFieldName, "Number"));
		_numberText.setBold();
		_ssnText = new Text(iwrb.getLocalizedString(_ssnFieldName, "SSN"));
		_ssnText.setBold();
		_foundedText = new Text(iwrb.getLocalizedString(_foundedFieldName, "Founded"));
		_foundedText.setBold();
		_memberUMFIText = new Text(iwrb.getLocalizedString(_memberUMFIFieldName, "UMFI membership"));
		_memberUMFIText.setBold();
		_makeText = new Text(iwrb.getLocalizedString(_makeFieldName, "Make"));
		_makeText.setBold();
/*		_connectionToSpecialText = new Text(iwrb.getLocalizedString(_connectionToSpecialFieldName,
				"Connection to special"));
		_connectionToSpecialText.setBold();*/
		_regionalUnionText = new Text(iwrb.getLocalizedString(_regionalUnionFieldName, "Regional union"));
		_regionalUnionText.setBold();
		_statusText = new Text(iwrb.getLocalizedString(_statusFieldName, "Status"));
		_statusText.setBold();
		_inOperationText = new Text(iwrb.getLocalizedString(_inOperationFieldName, "In operation"));
		_inOperationText.setBold();
		_usingMemberSystemText = new Text(iwrb.getLocalizedString(_usingMemberSystemFieldName, "In member system"));
		_usingMemberSystemText.setBold();
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see com.idega.user.presentation.UserGroupTab#lineUpFields()
	 */
	public void lineUpFields() {
		String type = "";
		try {
			if(getGroupId()>0){
				Group group = (Group) (((GroupHome) com.idega.data.IDOLookup.getHome(Group.class)).findByPrimaryKey(new Integer(getGroupId())));
				type = group.getGroupType();
			}
		}
		catch (IDOLookupException e) {
			e.printStackTrace();
		}
		catch (FinderException e) {
			e.printStackTrace();
		}
		empty();
		Table t = new Table(2, 7);
		t.setWidth(Table.HUNDRED_PERCENT);
		t.setCellpadding(5);
		t.setCellspacing(0);
		t.add(_numberText, 1, 1);
		t.add(Text.getBreak(), 1, 1);
		t.add(_numberField, 1, 1);
		t.add(_ssnText, 2, 1);
		t.add(Text.getBreak(), 2, 1);
		t.add(_ssnField, 2, 1);
		t.add(_foundedText, 1, 2);
		t.add(Text.getBreak(), 1, 2);
		t.add(_foundedField, 1, 2);
		
		if(IWMemberConstants.GROUP_TYPE_CLUB.equals(type)){
			t.add(_makeText, 2, 2);
			t.add(Text.getBreak(), 2, 2);
			t.add(_makeField, 2, 2);
//			t.add(_connectionToSpecialText, 1, 3);
//			t.add(Text.getBreak(), 1, 3);
//			t.add(_connectionToSpecialField, 1, 3);
			t.add(_regionalUnionText, 2, 3);
			t.add(Text.getBreak(), 2, 3);
			t.add(_regionalUnionField, 2, 3);
		}
		
		t.add(_statusText, 1, 4);
		t.add(Text.getBreak(), 1, 4);
		t.add(_statusField, 1, 4);
		t.mergeCells(1, 5, 2, 5);
		t.add(_memberUMFIField, 1, 5);
		t.add(_memberUMFIText, 1, 5);
		t.mergeCells(1, 6, 2, 6);
		t.add(_inOperationField, 1, 6);
		t.add(_inOperationText, 1, 6);
		t.mergeCells(1, 7, 2, 7);
		t.add(_usingMemberSystemField, 1, 7);
		t.add(_usingMemberSystemText, 1, 7);
		add(t);
	}

	public void main(IWContext iwc) {
		getPanel().addHelpButton(getHelpButton());
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see com.idega.util.datastructures.Collectable#collect(com.idega.presentation.IWContext)
	 */
	public boolean collect(IWContext iwc) {
		if (iwc != null) {
			String number = iwc.getParameter(_numberFieldName);
			String ssn = iwc.getParameter(_ssnFieldName);
			String founded = iwc.getParameter(_foundedFieldName);
			String type = iwc.getParameter(_typeFieldName);
			String member = iwc.getParameter(_memberUMFIFieldName);
			String make = iwc.getParameter(_makeFieldName);
//			String connection = iwc.getParameter(_connectionToSpecialFieldName);
			String status = iwc.getParameter(_statusFieldName);
			String inOperation = iwc.getParameter(_inOperationFieldName);
			String using = iwc.getParameter(_usingMemberSystemFieldName);
			if (number != null) {
				fieldValues.put(_numberFieldName, number);
			}
			else {
				fieldValues.put(_numberFieldName, "");
			}
			if (ssn != null) {
				fieldValues.put(_ssnFieldName, ssn);
			}
			else {
				fieldValues.put(_ssnFieldName, "");
			}
			if (founded != null) {
				fieldValues.put(_foundedFieldName, founded);
			}
			else {
				fieldValues.put(_foundedFieldName, "");
			}
			if (type != null) {
				fieldValues.put(_typeFieldName, type);
			}
			else {
				fieldValues.put(_typeFieldName, "");
			}
			fieldValues.put(_memberUMFIFieldName, new Boolean(member != null));
			if (make != null) {
				fieldValues.put(_makeFieldName, make);
			}
			else {
				fieldValues.put(_makeFieldName, "");
			}
/*			if (connection != null) {
				fieldValues.put(_connectionToSpecialFieldName, connection);
			}
			else {
				fieldValues.put(_connectionToSpecialFieldName, "");
			}*/
			if (status != null) {
				fieldValues.put(_statusFieldName, status);
			}
			else {
				fieldValues.put(_statusFieldName, "");
			}
			fieldValues.put(_inOperationFieldName, new Boolean(inOperation != null));
			fieldValues.put(_usingMemberSystemFieldName, new Boolean(using != null));
			updateFieldsDisplayStatus();
		}
		return true;
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see com.idega.util.datastructures.Collectable#store(com.idega.presentation.IWContext)
	 */
	public boolean store(IWContext iwc) {
		Group group;
		try {
			group = (Group) (((GroupHome) com.idega.data.IDOLookup.getHome(Group.class)).findByPrimaryKey(new Integer(
					getGroupId())));
			String groupType = group.getGroupType();
			
			String number = (String) fieldValues.get(_numberFieldName);
			String ssn = (String) fieldValues.get(_ssnFieldName);
			String founded = (String) fieldValues.get(_foundedFieldName);
			
			Boolean memberUMFI = (Boolean) fieldValues.get(_memberUMFIFieldName);
			String status = (String) fieldValues.get(_statusFieldName);
			Boolean inOperation = (Boolean) fieldValues.get(_inOperationFieldName);
			Boolean usingSystem = (Boolean) fieldValues.get(_usingMemberSystemFieldName);
			group.setMetaData(IWMemberConstants.META_DATA_CLUB_NUMBER, number);
			group.setMetaData(IWMemberConstants.META_DATA_CLUB_SSN, ssn);
			group.setMetaData(IWMemberConstants.META_DATA_CLUB_FOUNDED, founded);
			
			if (memberUMFI != null) {
				group.setMetaData(IWMemberConstants.META_DATA_CLUB_IN_UMFI, memberUMFI.toString());
			}
			
			if(IWMemberConstants.GROUP_TYPE_CLUB.equals(groupType)){
				String make = (String) fieldValues.get(_makeFieldName);
//				String connection = (String) fieldValues.get(_connectionToSpecialFieldName);
				String type = (String) fieldValues.get(_typeFieldName);
				group.setMetaData(IWMemberConstants.META_DATA_CLUB_TYPE, type);
				group.setMetaData(IWMemberConstants.META_DATA_CLUB_MAKE, make);
/*				if (make.equals(IWMemberConstants.META_DATA_CLUB_STATUS_SINGLE_DIVISION_CLUB)) {
					String oldConnection = group.getMetaData(IWMemberConstants.META_DATA_CLUB_LEAGUE_CONNECTION);
					if ((oldConnection == null || oldConnection.trim().equals("")) && connection != null) {
						group.setMetaData(IWMemberConstants.META_DATA_CLUB_LEAGUE_CONNECTION, connection);
						group.store();
						getClubInformationPluginBusiness(iwc).createSpecialConnection(connection, getGroupId(),
								group.getName(), iwc);
					}
				}
				else {
					group.setMetaData(IWMemberConstants.META_DATA_CLUB_LEAGUE_CONNECTION, "");
				}*/
				
			}
			
			group.setMetaData(IWMemberConstants.META_DATA_CLUB_STATUS, status);
			if (inOperation != null) {
				group.setMetaData(IWMemberConstants.META_DATA_CLUB_OPERATION, inOperation.toString());
			}
			else {
				group.setMetaData(IWMemberConstants.META_DATA_CLUB_OPERATION, Boolean.FALSE.toString());
			}
			if (usingSystem != null) {
				group.setMetaData(IWMemberConstants.META_DATA_CLUB_USING_SYSTEM, usingSystem.toString());
			}
			else {
				group.setMetaData(IWMemberConstants.META_DATA_CLUB_USING_SYSTEM, Boolean.FALSE.toString());
			}
			//and store everything
			group.store();
		}
		catch (RemoteException e) {
			e.printStackTrace(System.err);
			return false;
		}
		catch (FinderException e) {
			e.printStackTrace(System.err);
			return false;
		}
		return true;
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see com.idega.user.presentation.UserGroupTab#initFieldContents()
	 */
	public void initFieldContents() {
		Group group;
		try {
			group = (Group) (((GroupHome) com.idega.data.IDOLookup.getHome(Group.class)).findByPrimaryKey(new Integer(
					getGroupId())));
			List parents = group.getParentGroups();
			Iterator it = parents.iterator();
			String regional = null;
			if (it != null) {
				while (it.hasNext()) {
					Group parent = (Group) it.next();
					if (parent.getGroupType().equals(IWMemberConstants.GROUP_TYPE_REGIONAL_UNION)) {
						regional = parent.getName();
					}
				}
			}
			String number = group.getMetaData(IWMemberConstants.META_DATA_CLUB_NUMBER);
			String ssn = group.getMetaData(IWMemberConstants.META_DATA_CLUB_SSN);
			String founded = group.getMetaData(IWMemberConstants.META_DATA_CLUB_FOUNDED);
			String type = group.getMetaData(IWMemberConstants.META_DATA_CLUB_TYPE);
			String member = group.getMetaData(IWMemberConstants.META_DATA_CLUB_IN_UMFI);
			String make = group.getMetaData(IWMemberConstants.META_DATA_CLUB_MAKE);
//			String connection = group.getMetaData(IWMemberConstants.META_DATA_CLUB_LEAGUE_CONNECTION);
			String status = group.getMetaData(IWMemberConstants.META_DATA_CLUB_STATUS);
			String inOperation = group.getMetaData(IWMemberConstants.META_DATA_CLUB_OPERATION);
			String using = group.getMetaData(IWMemberConstants.META_DATA_CLUB_USING_SYSTEM);
			if (number != null) {
				fieldValues.put(_numberFieldName, number);
			}
			if (ssn != null) {
				fieldValues.put(_ssnFieldName, ssn);
			}
			if (founded != null) {
				fieldValues.put(_foundedFieldName, founded);
			}
			if (type != null) {
				fieldValues.put(_typeFieldName, type);
			}
			fieldValues.put(_memberUMFIFieldName, new Boolean(member));
			if (make != null) {
				fieldValues.put(_makeFieldName, make);
			}
/*			if (connection != null) {
				fieldValues.put(_connectionToSpecialFieldName, connection);
			}*/
			if (regional != null) {
				fieldValues.put(_regionalUnionFieldName, regional);
			}
			if (status != null) {
				fieldValues.put(_statusFieldName, status);
			}
			fieldValues.put(_inOperationFieldName, new Boolean(inOperation));
			fieldValues.put(_usingMemberSystemFieldName, new Boolean(using));
			updateFieldsDisplayStatus();
		}
		catch (RemoteException e) {
			e.printStackTrace(System.err);
		}
		catch (FinderException e) {
			e.printStackTrace(System.err);
		}
	}

	public ClubInformationPluginBusiness getClubInformationPluginBusiness(IWApplicationContext iwc) {
		ClubInformationPluginBusiness business = null;
		try {
			business = (ClubInformationPluginBusiness) com.idega.business.IBOLookup.getServiceInstance(iwc,
					ClubInformationPluginBusiness.class);
		}
		catch (java.rmi.RemoteException rme) {
			throw new RuntimeException(rme.getMessage());
		}
		return business;
	}

	public MemberUserBusiness getMemberUserBusiness(IWApplicationContext iwc) {
		MemberUserBusiness business = null;
		try {
			business = (MemberUserBusiness) com.idega.business.IBOLookup.getServiceInstance(iwc,
					MemberUserBusiness.class);
		}
		catch (java.rmi.RemoteException rme) {
			throw new RuntimeException(rme.getMessage());
		}
		return business;
	}

	public Help getHelpButton() {
		IWContext iwc = IWContext.getInstance();
		IWBundle iwb = getBundle(iwc);
		Help help = new Help();
		Image helpImage = iwb.getImage("help.gif");
		help.setHelpTextBundle(MEMBER_HELP_BUNDLE_IDENTIFIER);
		help.setHelpTextKey(HELP_TEXT_KEY);
		help.setImage(helpImage);
		return help;
	}
}