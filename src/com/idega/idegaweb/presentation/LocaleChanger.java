package com.idega.idegaweb.presentation;

//import com.idega.presentation.ui.*;
import com.idega.presentation.Block;
import com.idega.presentation.Image;
import com.idega.presentation.PresentationObject;
import com.idega.presentation.Table;
import com.idega.presentation.IWContext;
import com.idega.presentation.text.Link;

import com.idega.idegaweb.IWBundle;
import com.idega.idegaweb.IWResourceBundle;
import com.idega.presentation.ui.DropdownMenu;
import java.util.Locale;
import java.util.List;
import java.util.Iterator;
import java.util.Enumeration;
import java.util.Map;
import java.util.HashMap;
import com.idega.util.LocaleUtil;
import com.idega.core.localisation.business.ICLocaleBusiness;


/**
 * Title:
 * Description:
 * Copyright:    Copyright (c) 2001
 * Company:      idega.is
 * @author 2000 - idega team - <br><a href="mailto:aron@idega.is">Aron Birkir</a><br>
 * @version 1.0
 */

public class LocaleChanger extends Block {


  public static String localesParameter="iw_localeswitcher_locale";
  private static String action = "iw_localeswitcher_sub_action";
  private Map ImageLocalizationMap ;
  protected boolean showLinks = false;


  public void main(IWContext iwc){
    make(iwc);

  }

  public void make(IWContext iwc){
    doLinkView(iwc);
  }

  protected void doLinkView(IWContext iwc){
    Table T = new Table();
    T.setCellpadding(0);
    T.setCellspacing(0);
    int row = 1;
    int col = 1;
    Iterator iter = ICLocaleBusiness.listOfLocalesJAVA().iterator();
    Locale locale;
    Locale currentLocale = iwc.getCurrentLocale();
    if(ImageLocalizationMap !=null){
      while(iter.hasNext()){

        locale = (Locale) iter.next();
        if(!locale.equals(currentLocale)){
          Integer imageId = (Integer)ImageLocalizationMap.get(locale);
          if(imageId !=null){
            try{
            Image image = new Image(imageId.intValue());
            T.add(getSwitcherLink(image,locale),col,row++);
            }
            catch(Exception ex){
              ex.printStackTrace();
            }
          }
        }
      }
    }
    add(T);

  }

  private Link getSwitcherLink(PresentationObject object,Locale locale){
    Link L = new Link(object);
    L.addParameter(com.idega.core.localisation.business.LocaleSwitcher.languageParameterString,locale.toString());
    L.setEventListener(com.idega.core.localisation.business.LocaleSwitcher.class.getName());

    return L;
  }

  public void setLocalizedImage(String localeString,int imageID){
      setLocalizedImage(ICLocaleBusiness.getLocaleFromLocaleString(localeString),imageID);
  }

  public void setLocalizedImage(Locale locale,int imageID){
    showLinks = true;
    getImageLocalizationMap().put(locale,new Integer(imageID));
  }

  private Map getImageLocalizationMap(){
    if(ImageLocalizationMap==null){
      ImageLocalizationMap=new HashMap();
    }
    return ImageLocalizationMap;
  }

  public void setUseImageView(boolean view){
    showLinks = view;
  }
}
