//idega 2000 - Eiki



package com.idega.block.poll.data;



//import java.util.*;

import java.sql.SQLException;
import java.sql.Timestamp;

import com.idega.block.text.data.LocalizedText;
import com.idega.data.GenericEntity;
import com.idega.user.data.UserBMPBean;



public class PollQuestionBMPBean extends com.idega.data.GenericEntity implements com.idega.block.poll.data.PollQuestion {



	public PollQuestionBMPBean(){

		super();

	}



	public PollQuestionBMPBean(int id)throws SQLException{

		super(id);

	}



	public void initializeAttributes(){

		addAttribute(getIDColumnName());

    addAttribute(getColumnNameUserID(), "User", true, true, Integer.class);

		addAttribute(getColumnNameStartTime(), "Begins", true, true, "java.sql.Timestamp");

		addAttribute(getColumnNameEndTime(), "Ends", true, true, "java.sql.Timestamp");

    this.addManyToManyRelationShip(LocalizedText.class,"PO_POLL_QUESTION_LOCALIZED_TEXT");

	}



  public static String getColumnNameID(){ return "PO_POLL_QUESTION_ID";}

  public static String getColumnNameUserID(){ return UserBMPBean.getColumnNameUserID();}

  public static String getColumnNameStartTime(){ return "START_TIME";}

  public static String getColumnNameEndTime(){ return "END_TIME";}



	public String getIDColumnName(){

		return getColumnNameID();

	}



	public String getEntityName(){

		return "PO_POLL_QUESTION";

	}



  public int getUserID() {

    return getIntColumnValue(getColumnNameUserID());

  }



	public Timestamp getStartTime(){

		return (Timestamp)getColumnValue(getColumnNameStartTime());

	}



	public Timestamp getEndTime() {

		return (Timestamp)getColumnValue(getColumnNameEndTime());

	}



  public void setUserID(int userID) {

    setColumn(getColumnNameUserID(),userID);

  }



  public void setStartTime(Timestamp from) {

    setColumn(getColumnNameStartTime(),from);

  }

  public void setEndTime(Timestamp from) {

    setColumn(getColumnNameEndTime(),from);

  }



	public void delete() throws SQLException{

    removeFrom(GenericEntity.getStaticInstance(LocalizedText.class));

    GenericEntity.getStaticInstance(PollAnswer.class).deleteMultiple(getColumnNameID(),Integer.toString(this.getID()));

		super.delete();

	}

}

