//idega 2001 - Laddi



package com.idega.block.calendar.data;



import java.sql.SQLException;
import java.util.Locale;

import com.idega.block.text.business.TextFinder;
import com.idega.block.text.data.LocalizedText;
import com.idega.data.EntityBulkUpdater;



public class CalendarEntryTypeBMPBean extends com.idega.data.GenericEntity implements com.idega.block.calendar.data.CalendarEntryType {



	public CalendarEntryTypeBMPBean(){

		super();

	}



	public CalendarEntryTypeBMPBean(int id)throws SQLException{

		super(id);

	}



  public void insertStartData()throws Exception{

    String[] entries = { "Afm�li","Fundir","Vi�bur�ur","Fr�dagur","Skiladagur","Tilkynning","Birthday","Meeting","Event","Holiday","Deadline","Announcement" };



    for ( int a = 0; a < 6; a++ ) {

      EntityBulkUpdater bulk = new EntityBulkUpdater();

      CalendarEntryType type = ((com.idega.block.calendar.data.CalendarEntryTypeHome)com.idega.data.IDOLookup.getHomeLegacy(CalendarEntryType.class)).createLegacy();



      LocalizedText text = ((com.idega.block.text.data.LocalizedTextHome)com.idega.data.IDOLookup.getHomeLegacy(LocalizedText.class)).createLegacy();

        text.setLocaleId(TextFinder.getLocaleId(new Locale("is","IS")));

        text.setHeadline(entries[a]);



      LocalizedText text2 = ((com.idega.block.text.data.LocalizedTextHome)com.idega.data.IDOLookup.getHomeLegacy(LocalizedText.class)).createLegacy();

        text2.setLocaleId(TextFinder.getLocaleId(Locale.ENGLISH));

        text2.setHeadline(entries[a+6]);



      bulk.add(type,EntityBulkUpdater.insert);

      bulk.add(text,EntityBulkUpdater.insert);

      bulk.add(text2,EntityBulkUpdater.insert);

      bulk.execute();



      text.addTo(type);

      text2.addTo(type);

    }

  }



	public void initializeAttributes(){

		addAttribute(getIDColumnName());

    addAttribute(getColumnNameImageID(), "ICFileID", true, true, Integer.class);

    addManyToManyRelationShip(LocalizedText.class,"CA_CALENDAR_TYPE_LOCALIZED_TEXT");

	}



	public static String getEntityTableName() { return "CA_CALENDAR_TYPE"; }



	public static String getColumnNameCalendarTypeID() { return "CA_CALENDAR_TYPE_ID"; }

	public static String getColumnNameImageID() { return "IC_FILE_ID"; }



  public String getIDColumnName(){

		return getColumnNameCalendarTypeID();

	}



	public String getEntityName(){

		return getEntityTableName();

	}



  public int getImageID() {

    return getIntColumnValue(getColumnNameImageID());

  }



  public void setImageID(int imageID) {

    setColumn(getColumnNameImageID(),imageID);

  }





  //DELETE

	public void delete() throws SQLException{

    removeFrom(com.idega.block.text.data.LocalizedTextBMPBean.getStaticInstance(LocalizedText.class));

		super.delete();

	}

}
