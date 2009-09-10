package com.idega.user.presentation;
import java.rmi.RemoteException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import javax.ejb.FinderException;
import com.idega.idegaweb.IWApplicationContext;
import com.idega.presentation.IWContext;
import com.idega.presentation.TabbedPropertyPanel;
import com.idega.presentation.Table;
import com.idega.presentation.text.Text;
import com.idega.user.business.GroupBusiness;
import com.idega.user.business.UserBusiness;
import com.idega.user.data.Group;
import com.idega.user.data.User;
import com.idega.util.datastructures.Collectable;
/**
 * Title: User Copyright: Copyright (c) 2001 Company: idega.is
 * 
 * @author <a href="mailto:gummi@idega.is">Gu�mundur �g�st S�mundsson</a>
 * @version 1.0
 */
public abstract class UserTab extends Table implements Collectable {
	private int userId = -1;
	private int selectedGroupId = -1;
	private List errorStrings;
	protected int fontSize = 2;
	protected Text proxyText;
	protected UserBusiness business = null;
	protected GroupBusiness groupBusiness = null;
	//protected UserBusiness business;
	protected Map fieldValues;
	private boolean _isInitialized = false;
	private TabbedPropertyPanel panel;
	
	public UserTab() {
		super();
		this.errorStrings = new ArrayList();
		//business = new UserBusiness();
		this.fieldValues = new HashMap();
		init();
		this.setStyleClass("main"); //added for isi styles - birna
		this.setCellpadding(0);
		this.setCellspacing(0);
		this.setWidth(Table.HUNDRED_PERCENT); //changed from 370
	}
	
	/*public void main(IWContext iwc) throws Exception {
		if(!_isInitialized) {
			initializeFieldNames();
			initializeFields();
			initializeTexts();
			initializeFieldValues();
			lineUpFields();
			_isInitialized = true;
		}
	}*/
	
	public UserTab(int userId) {
		this();
		this.setUserID(userId);
	}
	
	/**
	 * Calls all the "necessary" setup methods
	 * The calling order goes like this: <br>
	 * 1. initializeFieldNames();<br>
	 * 2. initializeFields();<br>
	 * 3. initializeTexts();<br>
	 * 4. initializeFieldValues();<br>
	 * 5. lineUpFields();<br>
	 */
	public void init() {
		initializeFieldNames();
		initializeFields();
		initializeTexts();
		initializeFieldValues();
		lineUpFields();
	}
	
	public abstract void initializeFieldNames();
	public abstract void initializeFieldValues();
	public abstract void updateFieldsDisplayStatus();
	public abstract void initializeFields();
	public abstract void initializeTexts();
	public abstract void lineUpFields();
	public abstract boolean collect(IWContext iwc);
	public abstract boolean store(IWContext iwc);
	public abstract void initFieldContents();
	
	public void setPanel(TabbedPropertyPanel panel) {
		this.panel = panel;
	}
	
	public TabbedPropertyPanel getPanel() {
		return this.panel;
	}
	
	private void initProxyText() {
		this.proxyText = new Text("");
		this.proxyText.setFontSize(this.fontSize);
	}
	
	public Text getTextObject() {
		if (this.proxyText == null) {
			initProxyText();
		}
		return (Text) this.proxyText.clone();
	}
	
	public void setGroupID(int id) {
		this.selectedGroupId = id;
		initFieldContents();
	}
	
	public int getGroupID() {
		return this.selectedGroupId;
	}
	
	public void setUserID(int id) {
		this.userId = id;
		initFieldContents();
	}
	
	public int getUserId() {
		return this.userId;
	}
	
	public void setUserIDAndGroupID(int userID, int groupID) {
		this.userId = userID;
		this.selectedGroupId = groupID;
		initFieldContents();
	}

	//cannot store it because some tabs might update it via the userid and not
	// by this bean
	protected User getUser() {
		try {
			if (getUserId() < 1) {
				return null;
			}
			else {
				return this.getUserBusiness(
					this.getIWApplicationContext()).getUser(
					getUserId());
			}
		} catch (RemoteException e) {
			e.printStackTrace();
		}
		return null;
	}
	
	protected Group getGroup() {
		try {
			if (getGroupID() < 1) {
				return null;
			}
			else {
				return this.getGroupBusiness(
					this.getIWApplicationContext()).getGroupByGroupID(
					getGroupID());
			}
		} catch (RemoteException e) {
			e.printStackTrace();
		} catch (FinderException e) {
			e.printStackTrace();
		}
		return null;
	}
	
	public void addErrorMessage(String message) {
		this.errorStrings.add(message);
	}
	
	public String[] clearErrorMessages() {
		String[] st = new String[this.errorStrings.size()];
		Iterator iter = this.errorStrings.iterator();
		int index = 0;
		while (iter.hasNext()) {
			st[index++] = (String) iter.next();
		}
		this.errorStrings.clear();
		return st;
	}
	
	public boolean someErrors() {
		return (0 < this.errorStrings.size());
	}
	
	public UserBusiness getUserBusiness(IWApplicationContext iwc) {
		if (this.business == null) {
			try {
				this.business =
					(
						UserBusiness) com
							.idega
							.business
							.IBOLookup
							.getServiceInstance(
						iwc,
						UserBusiness.class);
			} catch (java.rmi.RemoteException rme) {
				throw new RuntimeException(rme.getMessage());
			}
		}
		return this.business;
	}
	
	public GroupBusiness getGroupBusiness(IWApplicationContext iwc) {
		if (this.groupBusiness == null) {
			try {
				this.groupBusiness =
					(
						GroupBusiness) com
							.idega
							.business
							.IBOLookup
							.getServiceInstance(
						iwc,
						GroupBusiness.class);
			} catch (java.rmi.RemoteException rme) {
				throw new RuntimeException(rme.getMessage());
			}
		}
		return this.groupBusiness;
	}
} // Class GeneralUserInfoTab
