package is.idega.idegaweb.golf.service;

import java.sql.*;
import com.idega.data.*;
import is.idega.idegaweb.golf.entity.*;
import com.idega.util.*;
import com.idega.presentation.ui.*;
import java.io.*;
import com.idega.util.text.TextSoap;
import com.idega.data.EntityFinder;
import java.util.List;

/**
 * Title:        Golf<p>
 * Description:  <p>
 * Copyright:    Copyright (c) idega 2000 <p>
 * Company:      idega margmi�lun<p>
 * @author idega 2000 - idega team - gummi
 * @version 1.0
 */


public class StartService{

  private Startingtime startTime;                 //nota�ur sem almennur hlutur til a� nalgast almenn f�ll oha� id
  private StartingtimeFieldConfig fieldConfig;    //nota�ur sem almennur hlutur til a� nalgast almenn f�ll oha� id
  private Field field;		//nota�ur sem almennur hlutur til a� nalgast almenn f�ll oha� id
  private Union union;		//nota�ur sem almennur hlutur til a� nalgast almenn f�ll oha� id

//  private Connection conn;


  //  ####  smi�ir  #####

  public StartService(){              //throws SQLException{
  super();
  startTime = new Startingtime();
  fieldConfig = new StartingtimeFieldConfig();
  field = new Field();
  union = new Union();


//  conn = getConnection();
  }


  // #### Private - F�ll  ####




  // #### Public - F�ll  ####


    // Start.jsp

/*  public int getFirstField(int union_id)throws SQLException{
    return ((Field[])field.findAll("SELECT * FROM " + field.getEntityName() + " WHERE union_id = " + union_id ))[0].getID();
  }*/
  public synchronized int getFirstField(String union_id)throws SQLException{
    return ((Field[])field.findAll("SELECT * FROM " + field.getEntityName() + " WHERE union_id = " + union_id + " AND  ONLINE_STARTINGTIME='Y' " ))[0].getID();
  }

  // ##%%##%%##%%#%%## var a� breyta DESC til a� fa fyrstu menn fyrst, kanna hvort virkar eftir a� Innskraning virkar
  public synchronized Startingtime[] getTableEntries(String date, int first_group, int last_group, int field_id )throws SQLException{
    return (Startingtime[])startTime.findAll("SELECT * FROM " + startTime.getEntityName() + " WHERE startingtime_date = '" + date + " 00:00:00.0' and grup_num >= " + first_group + " and grup_num < " + last_group + " and field_id = " + field_id + " order by grup_num DESC, startingtime_id");
//    return (Startingtime[])startTime.findAll("SELECT * FROM " + startTime.getEntityName() + " WHERE startingtime_date = '" + date + " 00:00:00.0' and grup_num >= " + first_group + " and grup_num < " + last_group + " and field_id = " + field_id + " order by grup_num DESC");
  }

  public synchronized List getStartingtimeTableEntries(idegaTimestamp date, String field_id, int firstGroup, int lastGroup )throws SQLException{
    date.setHour(0);
    date.setMinute(0);
    date.setSecond(0);
    return EntityFinder.findAll(startTime,"SELECT * FROM " + startTime.getEntityName() + " WHERE startingtime_date = '" + date.toSQLDateString() + "' and field_id = " + field_id + " and grup_num >= " + firstGroup + " and grup_num <= " + lastGroup + " order by grup_num DESC, startingtime_id");
  }

  public synchronized List getStartingtimeTableEntries(idegaTimestamp date, String field_id)throws SQLException{
    date.setHour(0);
    date.setMinute(0);
    date.setSecond(0);
    return EntityFinder.findAll(startTime,"SELECT * FROM " + startTime.getEntityName() + " WHERE startingtime_date = '" + date.toSQLDateString() + "' and field_id = " + field_id + " order by grup_num DESC, startingtime_id");
  }



  public synchronized Field[] getFields(String union_id)throws SQLException{
    return (Field[])field.findAll("SELECT * FROM " + field.getEntityName() + " WHERE union_id = " + union_id + " AND  ONLINE_STARTINGTIME='Y' ORDER BY " + field.getIDColumnName());
  }


  public synchronized StartingtimeFieldConfig getFieldConfig(int field_id, String date)throws SQLException{
    StartingtimeFieldConfig[] temp = (StartingtimeFieldConfig[])fieldConfig.findAll("SELECT * FROM " + fieldConfig.getEntityName() + " WHERE begin_date <= '"+ date +" 23:59:59.0' and field_id = " + field_id + " ORDER BY begin_date DESC");
    return temp[0];
  }

  public synchronized StartingtimeFieldConfig getFieldConfig(int field_id, idegaTimestamp date)throws SQLException{
    date.setHour(23);
    date.setMinute(59);
    date.setSecond(59);
    StartingtimeFieldConfig[] temp = (StartingtimeFieldConfig[])fieldConfig.findAll("SELECT * FROM " + fieldConfig.getEntityName() + " WHERE begin_date <= '"+ date.toSQLString() +"' and field_id = " + field_id + " ORDER BY begin_date DESC");
    return temp[0];
  }


  // innskraning1.jsp


  public synchronized void preSetStartingtime(int group_num, String date, String field_id)throws SQLException{
    Startingtime insert = new Startingtime();

    insert.setGroupNum(new Integer(group_num));
    insert.setStartingtimeDate( new idegaTimestamp(date).getSQLDate() );
    insert.setFieldID( new Integer(field_id) );

    insert.insert();
  }


  public synchronized void setStartingtime(int group_num, idegaTimestamp date, String field_id, String member_id, String owner_id, String player_name, String handicap, String union, String card, String card_no )throws SQLException{
    Startingtime insert = new Startingtime();

    if(card != null){
      insert.setCardName(card);
    }
    if(card_no != null){
      insert.setCardNum(card_no);
    }

    if(union != null){
      insert.setClubName(union);
    }else{
      insert.setClubName("-");
    }

//    if(field_id != null){
      insert.setFieldID( new Integer(field_id) );
//    }

    insert.setGroupNum(new Integer(group_num));

    try{
      insert.setHandicap(Float.parseFloat(TextSoap.findAndReplace(handicap,",",".")));
    }catch(NumberFormatException e){
      //System.err.println("forgj�f r�ng : "  );
    }catch(NullPointerException  e){
      //System.err.println("forgj�f null");
    }

    if(member_id != null){
      insert.setMemberID(new Integer(member_id));
    }

    if(member_id != null){
      insert.setOwnerID(new Integer(owner_id));
    }
//    if(date != null){
      insert.setPlayerName(player_name);
//    }
//    if(date != null){
      insert.setStartingtimeDate( date.getSQLDate() );
//    }

    insert.insert();
  }

  public synchronized void setStartingtime(int group_num, idegaTimestamp date, String field_id, String member_id, String player_name, String handicap, String union, String card, String card_no )throws SQLException{
     setStartingtime(group_num, date, field_id, member_id, null, player_name, handicap, union, card, card_no );
  }

  public synchronized void setStartingtime(int group_num, String date, String field_id, String member_id, String player_name, String handicap, String union, String card, String card_no )throws SQLException{
     setStartingtime(group_num, new idegaTimestamp(date), field_id, member_id, player_name, handicap, union, card, card_no );
  }



  public int countEntriesInGroup( int group_num, String field_id, idegaTimestamp date )throws SQLException{
    return this.startTime.getNumberOfRecords("SELECT count(*) FROM " + startTime.getEntityName() + " WHERE grup_num = '" + group_num + "' AND field_id = '" + field_id + "' AND startingtime_date = '" + date.toString() + "'");
  }

  public int entriesInGroup( int group_num, String field_id, String date )throws SQLException{
    return countEntriesInGroup( group_num, field_id, new idegaTimestamp(date) );
  }

  public int countOwnersEntries( int owner_id, String field_id, idegaTimestamp date )throws SQLException{
    return this.startTime.getNumberOfRecords("SELECT count(*) FROM " + startTime.getEntityName() + " WHERE owner_id = '" + owner_id + "' AND field_id = '" + field_id + "' AND startingtime_date = '" + date.toString() + "'");
  }

  public int countMembersEntries( int member_id, String field_id, idegaTimestamp date )throws SQLException{
    return this.startTime.getNumberOfRecords("SELECT count(*) FROM " + startTime.getEntityName() + " WHERE member_id = '" + member_id + "' AND field_id = '" + field_id + "' AND startingtime_date = '" + date.toString() + "'");
  }

  public Startingtime getStartingtime(int member_id, idegaTimestamp date )throws SQLException{
    GenericEntity[] time = this.startTime.findAllByColumn("member_id",Integer.toString(member_id),"startingtime_date",date.toString());
    if(time != null && time.length > 0){
      return (Startingtime)time[0];
    }else{
      return null;
    }
  }


   //  search.jsp

    // nota getTableEntries(String date, int first_group, int last_group, int field_id ) fra start.jsp



  public synchronized Union[] getStartingEntryUnion()throws SQLException{
    return (Union[])union.findAll("SELECT distinct union_.name, union_.union_id FROM union_,field where field.ONLINE_STARTINGTIME='Y' and union_.union_id=field.union_id");
  }


  public synchronized Field[] getStartingEntryField()throws SQLException{
    return (Field[])field.findAll("SELECT * FROM " + field.getEntityName() + " WHERE ONLINE_STARTINGTIME = 'Y' ORDER BY name");
  }

  public synchronized String getFieldName(int field_id)throws SQLException{
    return new Field(field_id).getName();
  }

  public synchronized idegaTimestamp getFirstOpentime()throws SQLException{
    return new idegaTimestamp(((StartingtimeFieldConfig[])fieldConfig.findAll("SELECT * FROM " + fieldConfig.getEntityName() + " ORDER BY open_time" ))[0].getOpenTime() );
  }

  public synchronized int getMax_days_shown()throws SQLException{
    return ((StartingtimeFieldConfig[])fieldConfig.findAll("SELECT * FROM " + fieldConfig.getEntityName() + " ORDER BY days_shown" ))[0].getDaysShown();
  }

  public synchronized idegaTimestamp getLastClosetime()throws SQLException{
    return new idegaTimestamp(((StartingtimeFieldConfig[])fieldConfig.findAll("SELECT * FROM " + fieldConfig.getEntityName() + " ORDER BY close_time" ))[0].getCloseTime() );
  }

  public synchronized int get_field_union( int field_id )throws SQLException{
    return new Field(field_id).getUnionID();
  }

public Startingtime[] findAllPlayersInFieldOrdered(String field_id, String orderby_clause)throws IOException, SQLException{

	Startingtime stime = new Startingtime();
	Startingtime[] startingtimeimeArray = null;
	try
	{
		startingtimeimeArray = (Startingtime[]) stime.findAll("select * from startingtime where field_id = "+field_id+" and startingtime_date >= '"+idegaTimestamp.RightNow().toSQLDateString()+"' order by "+orderby_clause);
	}
	catch (SQLException E) {
		E.printStackTrace();
	}
	return startingtimeimeArray;
}

public Startingtime[] findAllPlayersByMemberOrdered(String field_id, String member_id, String orderby_clause)throws IOException, SQLException{

	Startingtime stime = new Startingtime();
	Startingtime[] startingtimeimeArray = null;
	try
	{
		startingtimeimeArray = (Startingtime[]) stime.findAll("select * from startingtime where field_id = "+field_id+" and member_id = "+member_id+" and startingtime_date >= '"+idegaTimestamp.RightNow().toSQLDateString()+"' order by "+orderby_clause);
	}
	catch (SQLException E) {
		E.printStackTrace();
	}
	return startingtimeimeArray;
}

public Startingtime[] getPlayersStartingToDay(String columnName, String toFind)throws SQLException
{
	Startingtime stime = new Startingtime();
	Startingtime[] startArray = null;

	try
	{
		startArray = (Startingtime[]) stime.findAll("select * from "+stime.getEntityName()+" where "+columnName+" like '"+toFind+"' and startingtime_date >= '"+idegaTimestamp.RightNow().toSQLDateString()+"'");
	}
	catch (SQLException E) {
		E.printStackTrace();
    }
	return startArray;
}

public Startingtime[] getPlayersStartingToDay(String column1, String toFind1, String column2, String toFind2)throws SQLException
{
	Startingtime stime = new Startingtime();
	Startingtime[] startArray = null;

	try
	{
		startArray = (Startingtime[]) stime.findAll("select * from "+stime.getEntityName()+" where "+column1+" like '"+toFind1+"' and "+column2+" like '"+toFind2+"' and startingtime_date >= '"+idegaTimestamp.RightNow().toSQLDateString()+"'");
	}
	catch (SQLException E) {
		E.printStackTrace();
    }
	return startArray;
}

}   // class StartService









