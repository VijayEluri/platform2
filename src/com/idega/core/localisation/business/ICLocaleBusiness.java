package com.idega.core.localisation.business;

import com.idega.data.EntityFinder;
import com.idega.data.IDOFinderException;
import com.idega.data.IDOLookup;
import com.idega.core.data.ICLocale;
import com.idega.core.data.ICLocaleHome;
import com.idega.util.LocaleUtil;
import com.idega.util.IWTimeStamp;
import com.idega.idegaweb.IWMainApplication;
import com.idega.presentation.ui.DropdownMenu;

import java.util.List;
import java.util.Vector;
import java.util.Hashtable;
import java.util.Map;
import java.util.Locale;
import java.sql.SQLException;
import java.util.Iterator;

/**
 * Title:
 * Description:
 * Copyright:    Copyright (c) 2000-2001 idega.is All Rights Reserved
 * Company:      idega
  *@author <a href="mailto:aron@idega.is">Aron Birkir</a>
 * @version 1.1
 */

public class ICLocaleBusiness {
  private static Hashtable LocaleHashByString = null, LocaleHashById = null;
  private static Hashtable LocaleHashInUseByString = null, LocaleHashInUseById = null;
  private static List allIcLocales = null,usedIcLocales = null,notUsedIcLocales = null;
  private static IWTimeStamp reloadStamp = null;


  private static List listOfAllICLocales(){
    try {
      return EntityFinder.getInstance().findAll(ICLocale.class);
    }
    catch (IDOFinderException ex) {
      return null;
    }
  }

  private static List listOfICLocalesInUse(){
    try {
     return  EntityFinder.getInstance().findAllByColumn(ICLocale.class,com.idega.core.data.ICLocaleBMPBean.getColumnNameInUse(),"Y");
    }
    catch (IDOFinderException ex) {
      ex.printStackTrace();
      return null;
    }
  }

  private static List listOfICLocales(boolean inUse){
    try {
      if(inUse)
        return  EntityFinder.getInstance().findAllByColumn(ICLocale.class,com.idega.core.data.ICLocaleBMPBean.getColumnNameInUse(),"Y");
      else
        return EntityFinder.getInstance().findAllByColumn(ICLocale.class ,com.idega.core.data.ICLocaleBMPBean.getColumnNameInUse(),"N");
    }
    catch (IDOFinderException ex) {
      ex.printStackTrace();
      return null;
    }
  }


  public static List listLocaleCreateIsEn(){
    List L = listOfLocales();
    if(L == null){
      try {
        Vector V = new Vector();
        ICLocale is= ((com.idega.core.data.ICLocaleHome)com.idega.data.IDOLookup.getHomeLegacy(ICLocale.class)).createLegacy();
        is.setLocale("is_IS");
        is.insert();

        ICLocale en= ((com.idega.core.data.ICLocaleHome)com.idega.data.IDOLookup.getHomeLegacy(ICLocale.class)).createLegacy();
        en.setLocale("en");
        en.insert();
        V.add(is);
        V.add(en);
        return V;
      }
      catch (SQLException ex) {
        ex.printStackTrace();
        return null;
      }
    }
    else
      return L;
  }

  public static List listOfLocales(){
    return listOfLocalesInUse();
    /*
    try {
      return EntityFinder.findAll(((com.idega.core.data.ICLocaleHome)com.idega.data.IDOLookup.getHomeLegacy(ICLocale.class)).createLegacy());
    }
    catch (SQLException ex) {
      return null;
    }
    */
  }

  public static List listOfAllLocales(){
    if(allIcLocales==null)
      reload();
    return allIcLocales;
  }



  public static List listOfLocalesInUse(){
    if(usedIcLocales ==null)
      reload();
    return usedIcLocales;
  }


  public static List listOfLocales(boolean inUse){
    if(inUse){
      if(usedIcLocales == null)
        reload();
      return  usedIcLocales;
    }
    else{
      if(notUsedIcLocales == null)
        reload();
      return notUsedIcLocales;
    }
  }

  public static List listOfLocalesJAVA(){
    List list = listOfLocales();
    List localeList = new Vector();

    if ( list != null ) {
      Iterator iter = list.iterator();
      while (iter.hasNext()) {
       ICLocale item = (ICLocale) iter.next();
       Locale locale = getLocaleFromLocaleString(item.getLocale());
       if ( locale != null )
        localeList.add(locale);
      }
    }
    return localeList;
  }

  private static void makeHashtables(){
    List L = listOfAllLocales();
    if(L!=null){
      int len = L.size();
      LocaleHashById = new Hashtable(len);
      LocaleHashByString  = new Hashtable(len);
      LocaleHashInUseByString = new Hashtable();
      LocaleHashInUseById = new Hashtable();
      for (int i = 0; i < len; i++) {
        ICLocale ICL = (ICLocale) L.get(i);
        LocaleHashById.put(new Integer(ICL.getID()),ICL);
        LocaleHashByString.put(ICL.getLocale(),ICL);
        if(ICL.getInUse()){
          LocaleHashInUseById.put(new Integer(ICL.getID()),ICL);
          LocaleHashInUseByString.put(ICL.getLocale(),ICL);
        }
      }
    }
  }

  private static void makeLists(){
    allIcLocales = listOfAllICLocales();
    usedIcLocales = listOfICLocalesInUse();
    notUsedIcLocales = new Vector();
    notUsedIcLocales.addAll(allIcLocales);
    notUsedIcLocales.removeAll(usedIcLocales);
  }

  public static Map mapOfLocalesInUseById(){
    if(LocaleHashInUseById == null)
      reload();
    return LocaleHashInUseById;
  }

  public static Map mapOfLocalesInUseByString(){
   if(LocaleHashInUseByString == null)
      reload();
    return LocaleHashInUseByString;
  }

  public static void reload(){
    makeLists();
    makeHashtables();

    reloadStamp = IWTimeStamp.RightNow();
  }

  public static IWTimeStamp getReloadStamp(){
    if(reloadStamp == null)
      reload();
    return reloadStamp;
  }

  public static Map getMapOfLocalesById(){
    return getLocaleHashById();
  }

  public static Map getMapOfLocalesByString(){
    return getLocaleHashByString();
  }

  public static Hashtable getLocaleHashById(){
    if(LocaleHashById == null)
      reload();
    return LocaleHashById;
  }
  public static Hashtable getLocaleHashByString(){
    if(LocaleHashByString == null)
      reload();
    return LocaleHashByString;
  }

  public static int getLocaleId(Locale locale){
    int r = -1;
    if(LocaleHashByString == null)
      reload();
    if( LocaleHashByString!=null && LocaleHashByString.containsKey(locale.toString()) ){
      ICLocale ICL = (ICLocale) LocaleHashByString.get(locale.toString());
      r = ICL.getID();
    }
    return r;
  }

	public static ICLocale getICLocale(Locale locale){
    if(LocaleHashByString == null)
      reload();
    if( LocaleHashByString!=null && LocaleHashByString.containsKey(locale.toString()) ){
      ICLocale ICL = (ICLocale) LocaleHashByString.get(locale.toString());
      return ICL;
    }
    return null;
  }

  /**
   *  returns ICLocale from Locale string identifier
   */
  public static ICLocale getICLocale(String localeString){
    if(localeString != null){
      if(LocaleHashByString == null){
        reload();
      }
      if( LocaleHashByString!=null && LocaleHashByString.containsKey(localeString) ){
        ICLocale ICL = (ICLocale) LocaleHashByString.get(localeString);
        return ICL;
      }
    }
    return null;
  }

  /**
   * Returns a Locale from a Locale string like Locale.toString();
   * returns null if not found
   */
  public static Locale getLocaleFromLocaleString(String localeString){
    if(localeString.length() == 2){
      return new Locale(localeString,"");
    }
    else if(localeString.length()==5 && localeString.indexOf("_")==2){
      return new Locale(localeString.substring(0,2),localeString.substring(3,5));
    }
    else if(localeString.length() > 5 && localeString.indexOf("_")==2 && localeString.indexOf("_",3)== 5){
      return new Locale(localeString.substring(0,2),localeString.substring(3,5),localeString.substring(6,localeString.length()));
    }
    else
      return null;
      //return Locale.getDefault();
  }

  public static Locale getLocale(int iLocaleId){
    try {
      if(LocaleHashById == null )
        reload();
      Integer i = new Integer(iLocaleId);
      if(LocaleHashById != null && LocaleHashById.containsKey(i)){
        ICLocale ICL = (ICLocale) LocaleHashById.get(i);
        return getLocaleFromLocaleString(ICL.getLocale());
      }
      else return null;
    }
    catch(Exception ex){
      ex.printStackTrace();
      return null;

    }
  }

  public static void makeLocalesInUse(List listOfStringIds){
    if(listOfStringIds != null){
      StringBuffer ids = new StringBuffer();
      Iterator I = listOfStringIds.iterator();
      String id;
      try{
        ICLocaleHome home = (ICLocaleHome)com.idega.data.IDOLookup.getHome(ICLocale.class);
        List currentLocales = listOfICLocalesInUse();
        List oldCurrentLocales = new Vector();
        oldCurrentLocales.addAll(currentLocales);
        while (I.hasNext()) {
          ICLocale locale = home.findByPrimaryKey(Integer.parseInt((String)I.next()));
          locale.setInUse(true);
          locale.store();
          oldCurrentLocales.remove(locale);
        }

        Iterator iter = oldCurrentLocales.iterator();
        while (iter.hasNext()) {
          ICLocale locale = (ICLocale)iter.next();
          locale.setInUse(false);
          locale.store();
        }
      }
      catch(Exception e){
        e.printStackTrace();
      }

      /*if(I.hasNext()){
        id = (String) I.next();
        ids.append(id);
      }
      while(I.hasNext()){
        id = (String) I.next();
        ids.append(",");
        ids.append(id);
      }
      try {
        String sqlA = "update ic_locale set in_use = 'Y' where ic_locale_id in ("+ids.toString()+")";
        System.err.println(sqlA);
        String sqlB = "update ic_locale set in_use = 'N' where ic_locale_id not in ("+ids.toString()+")";
        System.err.println(sqlB);
        com.idega.data.SimpleQuerier.execute(sqlA);
        com.idega.data.SimpleQuerier.execute(sqlB);
      }
      catch (Exception ex) {
        ex.printStackTrace();
      }*/
      reload();
    }
  }


  /**
 * In the DropdownMenu the keys (values) are the locale-stringrepresentations
 * e.g. "en_US" for English/US
 */
  public static DropdownMenu getAvailableLocalesDropdownStringKeyed(IWMainApplication iwma,String name){
    List locales = ICLocaleBusiness.listOfLocalesJAVA();
    DropdownMenu down = new DropdownMenu(name);
    Iterator iter = locales.iterator();
    while (iter.hasNext()) {
      Locale item = (Locale)iter.next();
      down.addMenuElement(item.toString(),item.getDisplayLanguage());
    }
    return down;
  }


}
