package com.idega.core.localisation.business;

import com.idega.business.IWEventListener;
import com.idega.presentation.IWContext;
import java.util.Locale;
import com.idega.util.LocaleUtil;

/**
 * Title:        IC loacalisation
 * Description:
 * Copyright:    Copyright (c) 2001
 * Company:      idega.is
 * @author 2000 - idega team - <a href="mailto:gummi@idega.is">Gu�mundur �g�st S�mundsson</a>
 * @version 1.0
 */

public class LocaleSwitcher implements IWEventListener{

  public static String languageParameterString = "iw_language";
  public static String englishParameterString = "en";
  public static String icelandicParameterString = "is_IS";


  public LocaleSwitcher() {
  }




  public void actionPerformed(IWContext iwc){
    String localeValue = iwc.getParameter(languageParameterString);
    if(localeValue!=null){
      Locale locale = LocaleUtil.getLocale(localeValue);
      if(locale!=null){
	iwc.setCurrentLocale(locale);
      }
    }
  }




}