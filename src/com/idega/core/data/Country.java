package com.idega.core.data;

import com.idega.data.GenericEntity;
import java.sql.SQLException;
import java.util.Locale;

/**
 * Title:        IW Core
 * Description:
 * Copyright:    Copyright (c) 2001
 * Company:      idega.is
 * @author 2000 - idega team - <a href="mailto:gummi@idega.is">Gu�mundur �g�st S�mundsson</a>
 * @version 1.0
 */

public class Country extends GenericEntity{

  public Country(){
    super();
  }

  public Country(int id)throws SQLException{
    super(id);
  }

  public void initializeAttributes() {
    this.addAttribute(this.getIDColumnName());
    this.addAttribute(getColumnNameName(),"Nafn",true,true,String.class,255);
    this.addAttribute(getColumnNameDescription(),"L�sing",true,true,String.class,500);
    this.addAttribute(getColumnNameIsoAbbreviation(),"ISO skammst�fun",true,true,String.class,10);
  }

  public String getEntityName() {
    return "ic_country";
  }

  public static String getColumnNameName(){return "country_name";}
  public static String getColumnNameDescription(){return "country_description";}
  public static String getColumnNameIsoAbbreviation(){return "iso_abbreviation";}


  public void insertStartData()throws Exception{
    String[] JavaLocales = java.util.Locale.getISOCountries();
    Country country;
    Locale locale = Locale.ENGLISH;
    Locale l = null;
    String lang = Locale.ENGLISH.getISO3Language();
    for (int i = 0; i < JavaLocales.length; i++) {
      country = new Country();
      l = new Locale(lang,JavaLocales[i]);
      country.setName(l.getDisplayCountry(locale));
      country.setIsoAbbreviation(JavaLocales[i]);
      country.insert();
    }
  }


  public String getName(){
    return this.getStringColumnValue(getColumnNameName());
  }

  public String getDescription(){
    return this.getStringColumnValue(getColumnNameDescription());
  }

 public String getIsoAbbreviation(){
    return this.getStringColumnValue(getColumnNameIsoAbbreviation());
  }



  public void setName(String Name){
    this.setColumn(getColumnNameName(),Name);
  }

  public void setDescription(String Description){
    this.setColumn(getColumnNameDescription(),Description);
  }


  public void setIsoAbbreviation(String IsoAbbreviation){
    this.setColumn(getColumnNameIsoAbbreviation(),IsoAbbreviation);
  }


  public static Country getStaticInstance(){
    return(Country)getStaticInstance(Country.class);
  }

}
