//idega 2000 - Laddi



package com.idega.block.boxoffice.data;



import java.sql.SQLException;
import java.util.Locale;

import com.idega.block.text.business.TextFinder;
import com.idega.block.text.data.LocalizedText;
import com.idega.data.EntityBulkUpdater;



public class BoxCategoryBMPBean extends com.idega.data.GenericEntity implements com.idega.block.boxoffice.data.BoxCategory {



  public BoxCategoryBMPBean(){

    super();

  }



  public BoxCategoryBMPBean(int id)throws SQLException{

    super(id);

  }



  public void insertStartData()throws Exception{

    String[] entries = { "�mislegt","Tenglar","Greinar","St�riskj�l","Lei�beiningar","Misc","Links","Articles","Documents","Instructions" };



    for ( int a = 0; a < 5; a++ ) {

      EntityBulkUpdater bulk = new EntityBulkUpdater();

      BoxCategory cat = ((com.idega.block.boxoffice.data.BoxCategoryHome)com.idega.data.IDOLookup.getHomeLegacy(BoxCategory.class)).createLegacy();



      LocalizedText text = ((com.idega.block.text.data.LocalizedTextHome)com.idega.data.IDOLookup.getHomeLegacy(LocalizedText.class)).createLegacy();

        text.setLocaleId(TextFinder.getLocaleId(new Locale("is","IS")));

        text.setHeadline(entries[a]);



      LocalizedText text2 = ((com.idega.block.text.data.LocalizedTextHome)com.idega.data.IDOLookup.getHomeLegacy(LocalizedText.class)).createLegacy();

        text2.setLocaleId(TextFinder.getLocaleId(Locale.ENGLISH));

        text2.setHeadline(entries[a+5]);



      bulk.add(cat,EntityBulkUpdater.insert);

      bulk.add(text,EntityBulkUpdater.insert);

      bulk.add(text2,EntityBulkUpdater.insert);

      bulk.execute();



      text.addTo(cat);

      text2.addTo(cat);

    }

  }



  public void initializeAttributes(){

    addAttribute(getIDColumnName());

    addAttribute(getColumnNameUserID(), "User", true, true, Integer.class);

    this.addManyToManyRelationShip(LocalizedText.class,"BX_CATEGORY_LOCALIZED_TEXT");

  }



  public static String getColumnNameBoxCategoryID() { return "BX_CATEGORY_ID"; }

  public static String getColumnNameUserID(){ return com.idega.core.user.data.UserBMPBean.getColumnNameUserID();}

  public static String getEntityTableName() { return "BX_CATEGORY"; }



  public String getIDColumnName(){

    return getColumnNameBoxCategoryID();

  }



  public String getEntityName(){

    return getEntityTableName();

  }



  public int getUserID() {

    return getIntColumnValue(getColumnNameUserID());

  }



  public void setUserID(int userID) {

    setColumn(getColumnNameUserID(),userID);

  }



  public void delete() throws SQLException {

    BoxLink[] link = (BoxLink[]) com.idega.block.boxoffice.data.BoxLinkBMPBean.getStaticInstance(BoxLink.class).findAllByColumn(getColumnNameBoxCategoryID(),getID());

    if ( link != null ) {

      for ( int a = 0; a < link.length; a++ ) {

        link[a].delete();

      }

    }

    removeFrom(com.idega.block.text.data.LocalizedTextBMPBean.getStaticInstance(LocalizedText.class));

    removeFrom(com.idega.block.boxoffice.data.BoxEntityBMPBean.getStaticInstance(BoxEntity.class));

    super.delete();

  }

}

