package com.idega.block.navigation.presentation;

import com.idega.user.business.UserBusiness;
import com.idega.user.data.User;
import com.idega.business.IBOLookup;
import com.idega.presentation.Table;
import com.idega.presentation.Image;
import com.idega.presentation.IWContext;
import com.idega.presentation.Block;
import com.idega.presentation.text.Link;
import com.idega.presentation.text.Text;


/**
 * Title: UserHomeLink
 * Description: A component that displays a user "homepage" if he has one assigned.
 * Copyright:    Copyright (c) 2000-2001 idega.is All Rights Reserved
 * Company:      idega
  *@author <a href="mailto:tryggvi@idega.is">Tryggvi Larusson</a>
 * @version 1.0
 */

public class UserHomeLink extends Block {

	protected static final String IW_BUNDLE_IDENTIFIER="com.idega.block.navigation";
	private static final String HOME_PAGE_KEY = "user_home_link.text";
	private static final String HOME_PAGE_KEY_VALUE = "My page";

	private String _linkStyleClass;
	private String _linkStyle;
	private String _selectedLinkStyleClass;
	private String _selectedLinkStyle;
	private String _loggedOffStyle;

	private boolean _showNotLoggedOn = false;

	private Image _iconImage;
	private Image _loggedOffIconImage;

	private int _spaceBetween = 0;

	public UserHomeLink(){
	}

	public void main(IWContext iwc){
		
		Table table = new Table();
		table.setCellpadding(0);
		table.setCellspacing(0);
		int column = 1;
		int pageID = this.getParentPageID();
		boolean addTable = false;
		
		if(iwc.isLoggedOn()){
			try{
				User newUser = iwc.getCurrentUser();
				int homePageID = getUserBusiness(iwc).getHomePageIDForUser(newUser);
				if(homePageID!=-1){
					Link link = new Link();
					link.setPage(homePageID);
					link.setText(getResourceBundle(iwc).getLocalizedString(HOME_PAGE_KEY,HOME_PAGE_KEY_VALUE));
					if (homePageID == pageID) {
						if (this._selectedLinkStyleClass != null) {
							link.setStyleClass(this._selectedLinkStyleClass);
						}
						else if (this._selectedLinkStyle != null) {
							link.setStyleAttribute(this._selectedLinkStyle);
						}
					}
					else {
						if (this._linkStyleClass != null) {
							link.setStyleClass(this._linkStyleClass);
						}
						else if (this._linkStyle != null) {
							link.setStyleAttribute(this._linkStyle);
						}
					}
					
					if (this._iconImage != null) {
						addTable = true;
						table.add(this._iconImage,column++,1);
						if (this._spaceBetween > 0) {
							table.setWidth(column++, 1, String.valueOf(this._spaceBetween));
						}
					}
					table.add(link,column,1);
					if (!addTable) {
						add(link);
					}
				}
			}
			catch(Exception e){
				e.printStackTrace();	
			}
		}
		else {
			if (this._showNotLoggedOn) {
				Text text = new Text(getResourceBundle(iwc).getLocalizedString(HOME_PAGE_KEY,HOME_PAGE_KEY_VALUE));
				if (this._loggedOffStyle != null) {
					text.setFontStyle(this._loggedOffStyle);
				}

				if (this._loggedOffIconImage != null) {
					addTable = true;
					table.add(this._loggedOffIconImage,column++,1);
					if (this._spaceBetween > 0) {
						table.setWidth(column++, 1, String.valueOf(this._spaceBetween));
					}
				}
				table.add(text,column,1);
				if (!addTable) {
					add(text);
				}
			}	
		}
		if (addTable) {
			add(table);
		}
	}
	
	protected UserBusiness getUserBusiness(IWContext iwc)throws java.rmi.RemoteException{
		return (UserBusiness)IBOLookup.getServiceInstance(iwc,UserBusiness.class);
	}	
	
	public String getBundleIdentifier(){
		return IW_BUNDLE_IDENTIFIER;
	}
	
	public void setShowWhenLoggedOff(boolean showWhenLoggedOff) {
		this._showNotLoggedOn = showWhenLoggedOff;
	}
	
	public void setLinkStyle(String style) {
		this._linkStyle = style;	
	}
	
	public void setLinkStyleClass(String styleClass) {
		this._linkStyleClass = styleClass;
	}
	
	public void setLoggedOffTextStyle(String style) {
		this._loggedOffStyle = style;	
	}
	
	public void setIconImage(Image image) {
		this._iconImage = image;	
	}
	
	public void setLoggedOffIconImage(Image image) {
		this._loggedOffIconImage = image;
	}
	
	public void setSpaceBetweenIconAndLink(int spaceBetween) {
		this._spaceBetween = spaceBetween;	
	}
	/**
	 * Sets the selectedLinkStyleClass.
	 * @param selectedLinkStyleClass The selectedLinkStyleClass to set
	 */
	public void setSelectedLinkStyleClass(String selectedLinkStyleClass) {
		this._selectedLinkStyleClass = selectedLinkStyleClass;
	}

	/**
	 * Sets the selectedLinkStyle.
	 * @param selectedLinkStyle The selectedLinkStyle to set
	 */
	public void setSelectedLinkStyle(String selectedLinkStyle) {
		this._selectedLinkStyle = selectedLinkStyle;
	}

}