package com.idega.idegaweb.presentation;

import com.idega.presentation.Block;
import java.sql.*;
import java.io.StringWriter;
import com.idega.presentation.IWContext;
import com.idega.presentation.ui.Form;
import com.idega.presentation.ui.SubmitButton;
import com.idega.util.database.ConnectionBroker;
import com.idega.util.SendMail;

/**
 * Title:
 * Description:
 * Copyright:    Copyright (c) 2001
 * Company:
 * @author <br><a href="mailto:aron@idega.is">Aron Birkir</a><br>
 * @version 1.0
 */

public class DataEmailer extends Block {

  private String emailAddress = null;
  private String host = null;
  private String subject = "";
  private String from = "";
  private String delimiter = ":";
  private String startData = "";
  private String endData = "";
  private String sql = null;

  private int errorId = 0;

  public DataEmailer() {
  }

  public void main(IWContext iwc){
    int icid = getICObjectInstanceID();
    String action = "send"+icid;
    if( iwc.isParameterSet(action) )
      sendLetter();
    if(emailAddress!=null && sql !=null){
      Form myForm = new Form();
      myForm.add(new SubmitButton(action,"Mail to: "+emailAddress));
      add(myForm);
    }
  }

  public void setEmailAddress(String email){
    this.emailAddress = email;
  }

  public void setDelimiter(String delim){
    this.delimiter = delim;
  }

  public void setStartData(String start){
    this.startData = start;
  }

  public void setEndData(String end){
    this.endData = end;
  }

  public void setSql(String sql){
    this.sql = sql;
  }

  public void setFrom(String from){
    this.from = from;
  }

  public void setHost(String host){
    this.host = host;
  }

   public void setSubject(String subject){
    this.subject = subject;
  }

  /**
   *  sends letter and returns error id
   */
  private void sendLetter(){
    if(emailAddress!=null && host!=null){
      String letter = createLetter();
      if(letter !=null){
        try{
          /*
          System.err.println("from :"+ from);
          System.err.println("to   :"+emailAddress);
          System.err.println("host :"+host);
          System.err.println("subj :"+subject);
          //System.err.println(letter);
          */
          SendMail.send(from,emailAddress,"","",host,subject,letter);
          //System.err.println("mail sent");
        }
        catch(Exception ex){
          ex.printStackTrace();
        }
      }
      else
        System.err.println("letter was null");
    }
    else
      System.err.println("did not send");
  }

  private String createLetter(){
    if(sql !=null){
      Statement Stmt= null;
      Connection conn = null;
      try{
        System.err.println("trying to fetch data");
        conn = ConnectionBroker.getConnection();
			  Stmt = conn.createStatement();
        ResultSet RS = Stmt.executeQuery(sql);
        int count = RS.getMetaData().getColumnCount();
        StringBuffer letter = new StringBuffer();
        String newLine = "\n";
        letter.append(startData).append(newLine);
        while(RS.next()){
          for (int i = 1; i <= count; i++) {
            letter.append(RS.getString(i)).append(delimiter);
          }
          letter.append(newLine);
        }
        letter.append(endData).append(newLine);
			  RS.close();

        return letter.toString();
		  }
      catch(SQLException sql){
        sql.printStackTrace();
      }
		  finally{

        try{
          if(Stmt != null)
            Stmt.close();
          if(conn !=null)
            ConnectionBroker.freeConnection(conn);
        }
        catch(SQLException sql){
          sql.printStackTrace();
        }

		  }
    }
    else
      System.err.println("sql is  null");
    return null;
  }


  public final static int HOST = 1;
  public final static int FROM = 2;
  public final static int MAILADDRESS = 3;
  public final static int SQL = 4;

}