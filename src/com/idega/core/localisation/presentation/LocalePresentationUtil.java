/*
 * Created on 23.6.2003
 *
 * To change this generated comment go to 
 * Window>Preferences>Java>Code Generation>Code Template
 */
package com.idega.core.localisation.presentation;

import java.util.Locale;

import com.idega.core.localisation.business.ICLocaleBusiness;
import com.idega.core.localisation.business.LocaleSwitcher;
import com.idega.idegaweb.IWMainApplication;
import com.idega.presentation.IWContext;
import com.idega.presentation.ui.DropdownMenu;

/**
 * A class for utility functions for the Localization framework
 * Copyright:    Copyright (c) 2003
 * Company:      idega
 * @author <a href=mailto:"tryggvi@idega.is">Tryggvi Larusson</a>
 * @version 1.0
 */
public class LocalePresentationUtil
{

	/**
	 * 
	 */
	private LocalePresentationUtil()
	{
		super();
		// TODO Auto-generated constructor stub
	}

	public static DropdownMenu getAvailableLocalesDropdown(IWContext iwc) {
		IWMainApplication iwma = iwc.getApplication();
	
		DropdownMenu down = LocalePresentationUtil.getAvailableLocalesDropdown(iwma, LocaleSwitcher.languageParameterString);
		Locale l = iwc.getCurrentLocale();
		if (l != null) {
			down.setSelectedElement(l.toString());
		}
	
		down.setToSubmit();
	
		return down;
	}

	/*
	  public static DropdownMenu getAvailableLocalesDropdown(IWMainApplication iwma,String name){
	    List locales = ICLocaleBusiness.listOfLocalesJAVA();
	    DropdownMenu down = new DropdownMenu(name);
	    Iterator iter = locales.iterator();
	    while (iter.hasNext()) {
	      Locale item = (Locale)iter.next();
	      down.addMenuElement(item.toString(),item.getDisplayLanguage());
	    }
	    return down;
	  }
	*/
	
	public static DropdownMenu getAvailableLocalesDropdown(IWMainApplication iwma, String name) {
		return ICLocaleBusiness.getAvailableLocalesDropdownStringKeyed(iwma, name);
	}

}
