/*
 * $Id: Help.java,v 1.4.2.1 2007/01/12 19:31:57 idegaweb Exp $
 *
 * Copyright (C) 2002 Idega hf. All Rights Reserved.
 *
 * This software is the proprietary information of Idega hf.
 * Use is subject to license terms.
 *
 */
package com.idega.idegaweb.help.presentation;

import java.util.Locale;
import com.idega.idegaweb.IWBundle;
import com.idega.idegaweb.IWResourceBundle;
import com.idega.idegaweb.IWUserContext;
import com.idega.presentation.Block;
import com.idega.presentation.IWContext;
import com.idega.presentation.Image;
import com.idega.presentation.text.Link;

/**
 * A class to create help for any part of the idegaWeb system. Maintains a set
 * of helptext for each locale.......
 * 
 * @author <a href="palli@idega.is">Pall Helgason</a>
 * @version 1.0
 */
public class Help extends Block {
	private static final String BUNDLE_IDENTIFIER = "com.idega.block.help";
	
	private static final String DEFAULT_HELP_TEXT = "hlp_default_text";
	private static final String DEFAULT_HELP_IMAGE = "/help/help.gif";
	private static final String DEFAULT_HELP_KEY = "hlp_";

	private static final String CORE_BUNDLE = "com.idega.core";
	public static final String HELP_KEY = "hlp_key";
	public static final String HELP_BUNDLE = "hlp_bundle";

	protected String _helpTextKey = null;
	protected String _helpTextBundle = null;

	protected boolean _showAsText = false;
	protected boolean _showInNewWindow = true;
	protected Link _helpLink = null;

	private IWBundle _iwb = null;
	private IWBundle _iwbCore = null;
	private IWResourceBundle _iwrbCore = null;
	
	private Image image = null;

	public Help() {
		this._helpLink = new Link();
	}
	
	public void main(IWContext iwc) throws Exception {
		this.empty();
		if (this._iwbCore == null) {
			this._iwbCore = iwc.getIWMainApplication().getBundle(CORE_BUNDLE);
			this._iwrbCore = this._iwbCore.getResourceBundle(iwc);
		}

		if (this._showAsText) {
			if (this._helpLink.isText()) {
				if (!this._helpLink.isLabelSet()) {
					this._helpLink.setText(this._iwrbCore.getLocalizedString(DEFAULT_HELP_TEXT, "Help"));
				}
			}
			else {
				this._helpLink.setText(this._iwrbCore.getLocalizedString(DEFAULT_HELP_TEXT, "Help"));
			}
		}
		else {
			if (this._helpLink.isImage()) {
				if (!this._helpLink.isLabelSet()) {
					this.image = this._iwrbCore.getImage(DEFAULT_HELP_IMAGE);
					this.image.setAlignment(Image.ALIGNMENT_ABSOLUTE_MIDDLE);
					this._helpLink.setImage(this.image);
				}
			}
			else {
				if(this.image != null) {
					this.image.setAlignment(Image.ALIGNMENT_ABSOLUTE_MIDDLE);
					this._helpLink.setImage(this.image);
				}
				else {
					this.image = this._iwrbCore.getImage(DEFAULT_HELP_IMAGE);
					this.image.setAlignment(Image.ALIGNMENT_ABSOLUTE_MIDDLE);
					this._helpLink.setImage(this.image);
				}
			}			
		}

		if (this._showInNewWindow) {
			this._helpLink.setWindowToOpen(HelpWindow.class);
		}

		if (this._helpTextKey != null) {
			this._helpLink.addParameter(HELP_KEY,this._helpTextKey);
		}
			
		if (this._helpTextBundle != null) {
			this._helpLink.addParameter(HELP_BUNDLE,this._helpTextBundle);
		}
			
		add(this._helpLink);	
	}

	public void setShowAsText(boolean showAsText) {
		this._showAsText = showAsText;
	}

	public boolean getShowAsText() {
		return this._showAsText;
	}

	public void setLocalizedLinkText(String localeString, String text) {
		this._helpLink.setLocalizedText(localeString, text);
	}

	public void setLocalizedLinkText(int icLocaleID, String text) {
		this._helpLink.setLocalizedText(icLocaleID, text);
	}

	public void setLocalizedLinkText(Locale locale, String text) {
		this._helpLink.setLocalizedText(locale, text);
	}

	public String getLocalizedLinkText(IWContext iwc) {
		return this._helpLink.getLocalizedText(iwc);
	}

	public void setLinkText(String linkText) {
		this._helpLink.setText(linkText);
	}

	public String getLinkText() {
		return this._helpLink.getText();
	}

	public void setLocalizedImage(String localeString, int imageID) {
		this._helpLink.setLocalizedImage(localeString, imageID);
	}

	public void setLocalizedImage(Locale locale, int imageID) {
		this._helpLink.setLocalizedImage(locale, imageID);
	}
	
	public void setImage(Image image) {
		this.image = image;
		this._helpLink.setImage(image);
	}
	
	public void setImage(String url){
		setImage(new Image(url));
	}
	
	public void setImageId(int imageId) {
		this._helpLink.setImageId(imageId);
	}
	
//	public void setShowTextInNewWindow(boolean showInNewWindow) {
//		_showInNewWindow = showInNewWindow;
//	}
//
//	public boolean getShowTextInNewWindow() {
//		return _showInNewWindow;
//	}
	
	public Object clone(IWUserContext iwc, boolean askForPermission) {
		Help obj = null;
		try {
			obj = (Help) super.clone(iwc, askForPermission);

			obj._helpTextKey = this._helpTextKey;

			obj._showAsText = this._showAsText;
			obj._showInNewWindow = this._showInNewWindow;
		}
		catch (Exception ex) {
			ex.printStackTrace(System.err);
		}
		return obj;
	}	
	
	public String getHelpTextKey() {
		return this._helpTextKey;	
	}
	
	public void setHelpTextKey(String key) {
		this._helpTextKey = key;
	}	
	
	public String getHelpTextBundle() {
		return this._helpTextBundle;	
	}
	
	public void setHelpTextBundle(String bundleString) {
		this._helpTextBundle = bundleString;	
	}

	/**
	 * @see com.idega.presentation.PresentationObject#getBundleIdentifier()
	 */
	public String getBundleIdentifier() {
		if (this._helpTextBundle != null && !this._helpTextBundle.equals("")) {
			return this._helpTextBundle;
		}
			
		return BUNDLE_IDENTIFIER;
	}
}