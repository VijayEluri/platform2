package is.idega.idegaweb.member.presentation;

import is.idega.block.family.business.FamilyLogic;
import is.idega.block.family.business.NoChildrenFound;
import is.idega.block.family.business.NoCustodianFound;
import is.idega.block.family.business.NoSiblingFound;
import is.idega.block.family.business.NoSpouseFound;
import java.rmi.RemoteException;
import java.util.Collection;
import java.util.Iterator;
import com.idega.idegaweb.IWApplicationContext;
import com.idega.idegaweb.IWBundle;
import com.idega.idegaweb.IWResourceBundle;
import com.idega.idegaweb.help.presentation.Help;
import com.idega.presentation.IWContext;
import com.idega.presentation.Image;
import com.idega.presentation.Table;
import com.idega.presentation.text.Link;
import com.idega.presentation.text.Text;
import com.idega.user.data.User;
import com.idega.user.presentation.UserPropertyWindow;
import com.idega.user.presentation.UserTab;

/**
 * Title:        User
 * Description:
 * Copyright:    Copyright (c) 2001
 * Company:      idega.is
 * @author 2000 - idega team - <a href="mailto:gummi@idega.is">Gu�mundur �g�st S�mundsson</a>
 * @version 1.0
 */
public class UserFamilyTab extends UserTab {
	private static final String IW_BUNDLE_IDENTIFIER = "is.idega.idegaweb.member";

	private static final String TAB_NAME = "usr_fam_tab_name";
	private static final String DEFAULT_TAB_NAME = "Family";	
	
	private static final String MEMBER_HELP_BUNDLE_IDENTIFIER = "is.idega.idegaweb.member.isi";
	private static final String HELP_TEXT_KEY = "user_family_tab";
	
	private Text spouseText;
	private Text childrenText;
	private Text custodiansText;
	private Text siblingsText;

	private User user;
	private Table custodianTable;
	private Table spouseTable;
	private Table childrenTable;
	private Table siblingsTable;

	public UserFamilyTab() {
		super();
		IWContext iwc = IWContext.getInstance();
		IWResourceBundle iwrb = getResourceBundle(iwc);

		setName(iwrb.getLocalizedString(TAB_NAME, DEFAULT_TAB_NAME));	
//		super.setName("Family");
	}

	public void updateFieldsDisplayStatus() {
	}
	public void initializeFields() {
	}
	public void initializeFieldNames() {
	}
	public void initializeFieldValues() {
	}
	public boolean collect(IWContext iwc) {
		initFieldContents();
		return true;
	}
	public boolean store(IWContext iwc) {
		return true;
	}

	public void initFieldContents() {
		resize(1, 1);
		setCellpadding(5);
		setCellspacing(0);
		this.user = getUser();

		empty();

		IWContext iwc = IWContext.getInstance();
		IWResourceBundle iwrb = getResourceBundle(iwc);
		Link attachLink = new Link(iwrb.getLocalizedString("usr_fam_attach","Attach"));
		attachLink.setStyleClass("styledLink"); 
		attachLink.setWindowToOpen(FamilyConnector.class);
		attachLink.addParameter(FamilyConnector._PARAM_USER_ID, getUserId());
		attachLink.addParameter(FamilyConnector._PARAM_METHOD, FamilyConnector._METHOD_ATTACH);
		attachLink.addParameter(FamilyConnector._PARAM_ACTION, FamilyConnector._ACTION_ATTACH);
		add(attachLink, 1, 1);
		add(Text.getBreak(), 1, 1);
		add(Text.getBreak(), 1, 1);

		if (this.user != null) {
			try {
				this.custodianTable = getCustodianTable();
				this.spouseTable = getSpouseTable();
				this.childrenTable = getChildrenTable();
				this.siblingsTable = getSiblingTable();
			}
			catch (RemoteException re) {
				this.custodianTable = null;
				this.spouseTable = null;
				this.childrenTable = null;
				this.siblingsTable = null;
			}

			if (this.custodianTable != null) {
				add(this.custodianTable, 1, 1);
				add(Text.getBreak(), 1, 1);
			}
			if (this.spouseTable != null) {
				add(this.spouseTable, 1, 1);
				add(Text.getBreak(), 1, 1);
			}
			if (this.childrenTable != null) {
				add(this.childrenTable, 1, 1);
				add(Text.getBreak(), 1, 1);
			}
			if (this.siblingsTable != null) {
				add(this.siblingsTable, 1, 1);
				add(Text.getBreak(), 1, 1);
			}
		}
		else {
			System.out.println("lineUpFields: User is null");
		}
	}

	public void main(IWContext iwc) {
		if (getPanel() != null) {
			getPanel().addHelpButton(getHelpButton());		
		}
	}

	public void initializeTexts() {
		IWContext iwc = IWContext.getInstance();
		IWResourceBundle iwrb = getResourceBundle(iwc);
		
		this.spouseText = new Text(iwrb.getLocalizedString("usr_fam_spouse","Spouse") + ":");
		this.spouseText.setBold();
		
		this.custodiansText = new Text(iwrb.getLocalizedString("usr_fam_custodians","Custodians") + ":");
		this.custodiansText.setBold();
		
		this.childrenText = new Text(iwrb.getLocalizedString("usr_fam_children","Children") + ":");
		this.childrenText.setBold();
		
		this.siblingsText = new Text(iwrb.getLocalizedString("usr_fam_siblings","Siblings") + ":");
		this.siblingsText.setBold();
	}
	
	public Help getHelpButton() {
		IWContext iwc = IWContext.getInstance();
		IWBundle iwb = getBundle(iwc);
		Help help = new Help();
		Image helpImage = iwb.getImage("help.gif");
		help.setHelpTextBundle( MEMBER_HELP_BUNDLE_IDENTIFIER);
		help.setHelpTextKey(HELP_TEXT_KEY);
		help.setImage(helpImage);
		return help;
		
	}

	public void lineUpFields() {
	}

	private Table getSpouseTable() throws RemoteException {
		Table table = new Table();

		User spouse = null;
		try {
			spouse = getMemberFamilyLogic(getIWApplicationContext()).getSpouseFor(this.user);
		}
		catch (NoSpouseFound nsf) {
			spouse = null;
		}

		if (spouse != null) {
			table.add(this.spouseText, 1, 1);
			table.mergeCells(1, 1, 2, 1);
			table.add(getFamilyLink(spouse, getMemberFamilyLogic(getIWApplicationContext()).getSpouseRelationType()), 1, 2);
			table.add(getUserPropertyLink(spouse), 2, 2);
//			table.add(new Text(spouse.getName() + ", " + spouse.getPersonalID()), 2, 2);
			return table;
		}

		return null;
	}

	private Table getCustodianTable() throws RemoteException {
		Table table = new Table();

		Collection custodians = null;
		try {
			custodians = getMemberFamilyLogic(getIWApplicationContext()).getCustodiansFor(this.user);
		}
		catch (NoCustodianFound nsf) {
			custodians = null;
		}

		if (custodians != null && custodians.size() > 0) {
			table.add(this.custodiansText, 1, 1);
			table.mergeCells(1, 1, 2, 1);
			int row = 2;

			Iterator iter = custodians.iterator();
			while (iter.hasNext()) {
				User user = (User) iter.next();
				table.add(getFamilyLink(user, getMemberFamilyLogic(getIWApplicationContext()).getCustodianRelationType()), 1, row);
				table.add(getUserPropertyLink(user), 2, row++);
//				table.add(new Text(user.getName() + ", " + user.getPersonalID()), 2, row++);
			}
			return table;
		}

		return null;
	}

	private Table getChildrenTable() throws RemoteException {
		Table table = new Table();

		Collection children = null;
		try {
			children = getMemberFamilyLogic(getIWApplicationContext()).getChildrenFor(this.user);
		}
		catch (NoChildrenFound nsf) {
			children = null;
		}

		if (children != null && children.size() > 0) {
			table.add(this.childrenText, 1, 1);
			table.mergeCells(1, 1, 2, 1);
			int row = 2;

			Iterator iter = children.iterator();
			while (iter.hasNext()) {
				User user = (User) iter.next();
				table.add(getFamilyLink(user, getMemberFamilyLogic(getIWApplicationContext()).getChildRelationType()), 1, row);
				table.add(getUserPropertyLink(user), 2, row++);
//				table.add(new Text(user.getName() + ", " + user.getPersonalID()), 2, row++);
			}
			return table;
		}

		return null;
	}

	private Table getSiblingTable() throws RemoteException {
		Table table = new Table();

		Collection siblings = null;
		try {
			siblings = getMemberFamilyLogic(getIWApplicationContext()).getSiblingsFor(this.user);
		}
		catch (NoSiblingFound nsf) {
			System.err.println(nsf.getMessage());
//			nsf.printStackTrace(System.err);
			siblings = null;
		}

		if (siblings != null && siblings.size() > 0) {
			table.add(this.siblingsText, 1, 1);
			table.mergeCells(1, 1, 2, 1);
			int row = 2;

			Iterator iter = siblings.iterator();
			while (iter.hasNext()) {
				User user = (User) iter.next();
				table.add(getFamilyLink(user, getMemberFamilyLogic(getIWApplicationContext()).getSiblingRelationType()), 1, row);
				table.add(getUserPropertyLink(user), 2, row++);
//				table.add(new Text(user.getName() + ", " + user.getPersonalID()), 2, row++);
			}
			return table;
		}

		return null;
	}
	
	private Link getUserPropertyLink(User user) {
		Link l = new Link(user.getName() + ", " + user.getPersonalID());
		l.setWindowToOpen(UserPropertyWindow.class);
		l.addParameter(UserPropertyWindow.PARAMETERSTRING_USER_ID,user.getPrimaryKey().toString());
		
		return l;
	}

	private Link getFamilyLink(User user, String relationType) throws RemoteException {
		IWContext iwc = IWContext.getInstance();
		IWResourceBundle iwrb = getResourceBundle(iwc);

		Link link = new Link(iwrb.getLocalizedString("usr_fam_detach","Detach"));
		link.setWindowToOpen(FamilyConnector.class);
		link.setStyleClass("styledLink"); 
		link.addParameter(FamilyConnector._PARAM_USER_ID, getUserId());
		link.addParameter(FamilyConnector._PARAM_RELATED_USER_ID, user.getPrimaryKey().toString());
		link.addParameter(FamilyConnector._PARAM_METHOD, FamilyConnector._METHOD_DETACH);
		link.addParameter(FamilyConnector._PARAM_ACTION, FamilyConnector._ACTION_DETACH);
		link.addParameter(FamilyConnector._PARAM_TYPE, relationType);
		return link;
	}

	public FamilyLogic getMemberFamilyLogic(IWApplicationContext iwc) {
		FamilyLogic familyLogic = null;
		if (familyLogic == null) {
			try {
				familyLogic = (FamilyLogic) com.idega.business.IBOLookup.getServiceInstance(iwc, FamilyLogic.class);
			}
			catch (java.rmi.RemoteException rme) {
				throw new RuntimeException(rme.getMessage());
			}
		}
		return familyLogic;
	}

	/**
	* @see com.idega.presentation.PresentationObject#getBundleIdentifier()
	*/
	public String getBundleIdentifier() {
		return IW_BUNDLE_IDENTIFIER;
	}	
}