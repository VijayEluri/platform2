/*
 * Copyright (C) 2003 Idega software. All Rights Reserved.
 *
 * This software is the proprietary information of Idega software.
 * Use is subject to license terms.
 *
 */
package is.idega.idegaweb.member.isi.block.accounting.presentation;

import is.idega.idegaweb.member.isi.block.accounting.business.AccountingBusiness;
import is.idega.idegaweb.member.util.IWMemberConstants;

import java.rmi.RemoteException;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Iterator;

import com.idega.business.IBOLookup;
import com.idega.idegaweb.IWApplicationContext;
import com.idega.presentation.Block;
import com.idega.user.data.Group;
import com.idega.user.data.User;

/**
 * @author palli
 */
public class CashierSubWindowTemplate extends Block {
	public static final String IW_BUNDLE_IDENTIFIER = "is.idega.idegaweb.member.isi.block.accounting";
	
	protected static final String ERROR_COULD_NOT_SAVE = "isi_acc_could_not_save";
	protected static final String ENTRY_ENTERED = "isi_acc_entry_entered";
	
	protected static final String STRING_TYPE_MANUAL = "isi_acc_fin_entry_manual_type";
	protected static final String STRING_TYPE_AUTOMATIC = "isi_acc_fin_entry_auto_type";
	protected static final String STRING_TYPE_PAYMENT = "isi_acc_fin_entry_pay_type";
	
	protected static final String NOT_DONE ="isi_todo";
	
	protected Group _club = null;
	protected Group _division = null;
	protected User _user = null;
	
	protected ArrayList _errorList = null;
	
	/**
	 * 
	 */
	public CashierSubWindowTemplate() {
		super();
	}

	public void setClub(Group club) {
		_club = club;
	}
	
	public void setDivision(Group division) {
		_division = division;
	}
	
	public void setUser(User user) {
		_user = user;
	}
	
	public Group getClub() {
		return _club;
	}
	
	public Group getDivision() {
		return _division;
	}
	
	public User getUser() {
		return _user;
	}
	
	
	/* (non-Javadoc)
	 * @see com.idega.presentation.PresentationObject#getBundleIdentifier()
	 */
	public String getBundleIdentifier() {
		return IW_BUNDLE_IDENTIFIER;
	}
	
	protected AccountingBusiness getAccountingBusiness(IWApplicationContext iwc) {
		try {
			return (AccountingBusiness) IBOLookup.getServiceInstance(iwc, AccountingBusiness.class);
		}
		catch (RemoteException e) {
			e.printStackTrace();
		}

		return null;
	}	
	
	protected void getClubDivisions(Collection divisions, Group group) {
		if (divisions == null)
			divisions = new ArrayList();
		
		if (group.getGroupType().equals(IWMemberConstants.GROUP_TYPE_CLUB_DIVISION)) {
			divisions.add(group);
		}

		Iterator it = group.getChildren();
		if (it != null) {
			while (it.hasNext()) {
				Group child = (Group) it.next();
				getClubDivisions(divisions, child);
			}
		}
	}
}