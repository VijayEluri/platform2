package com.idega.core.accesscontrol.data;

import java.sql.SQLException;
import java.sql.Timestamp;
import java.util.Collection;

import javax.ejb.FinderException;

import com.idega.data.IDOException;
import com.idega.user.data.User;

/**
 * Title:   idegaclasses
 * Description:
 * Copyright:    Copyright (c) 2001
 * Company:
 * @author  <a href="mailto:aron@idega.is">aron@idega.is
 * @version 1.0
 */

public class LoginRecordBMPBean extends com.idega.data.GenericEntity implements com.idega.core.accesscontrol.data.LoginRecord {

    public static String getEntityTableName(){return "IC_LOGIN_REC";}
    public static String getColumnLoginId(){return "IC_LOGIN_ID";}
    public static String getColumnInStamp(){return "IN_STAMP";}
    public static String getColumnOutStamp(){return "OUT_STAMP";}
    public static String getColumnIPAddress(){return "IP";}
	public static String getColumnLoginAsUser(){return "USER_ID";}

    public LoginRecordBMPBean(){
      super();
    }

    public LoginRecordBMPBean(int id)throws SQLException{
      super(id);
    }

    public void initializeAttributes(){
      addAttribute(this.getIDColumnName());
      addAttribute(getColumnLoginId(),"Login id",true,true,Integer.class,"many-to-one",LoginTable.class);
      addAttribute(getColumnInStamp(),"Login Stamp",true,true,Timestamp.class);
      addAttribute(getColumnOutStamp(),"Logout Stamp",true,true,Timestamp.class);
      addAttribute(getColumnIPAddress(),"IP address",true,true,String.class,16);
      addManyToOneRelationship(getColumnLoginAsUser(),User.class);
      setNullable(getColumnLoginAsUser(),true);
    }

    public String getEntityName(){
      return getEntityTableName();
    }
    public void setLoginId(int Id) {
      setColumn(getColumnLoginId(),Id);
    }
    public int getLoginId(){
      return getIntColumnValue(getColumnLoginId());
    }
    public Timestamp getLogInStamp(){
      return (Timestamp) getColumnValue(getColumnInStamp());
    }
    public void setLogInStamp(Timestamp stamp){
      setColumn(getColumnInStamp(),stamp);
    }
    public Timestamp getLogOutStamp(){
      return (Timestamp) getColumnValue(getColumnOutStamp());
    }
    public void setLogOutStamp(Timestamp stamp){
      setColumn(getColumnOutStamp(),stamp);
    }
    public String getIPAdress(){
      return getStringColumnValue(getColumnIPAddress());
    }
    public void setIPAdress(String ip){
      setColumn(getColumnIPAddress(),ip);
    }
    
	public int getLoginAsUserID(){
		return getIntColumnValue(getColumnLoginAsUser());
	}
	public void setLoginAsUserID(int userId){
		setColumn(getColumnLoginAsUser(),userId);
	}
	
	public User getLoginAsUser(){
		return (User)getColumnValue(getColumnLoginAsUser());
	}
	public void setLoginAsUser(User user){
		setColumn(getColumnLoginAsUser(),user);
	}

    public Collection ejbFindAllLoginRecords(int loginID)throws FinderException{
      String sql = "select * from "+this.getTableName()+" where "+this.getColumnLoginId()+" = "+loginID;
      System.out.println("----------------");
      System.out.println(sql);
      System.out.println("----------------");
      return super.idoFindIDsBySQL(sql);
    }
    
    public int ejbHomeGetNumberOfLoginsByLoginID(int loginID) throws IDOException {
      String sql = "select count(*) from "+this.getTableName()+" where "+this.getColumnLoginId()+" = "+loginID;
      return super.idoGetNumberOfRecords(sql);
    }
    
    public Integer ejbFindByLoginID(int loginID)throws FinderException{
      Collection loginRecords = idoFindAllIDsByColumnOrderedBySQL(this.getColumnLoginId(),loginID);
      if(!loginRecords.isEmpty()){
        return (Integer)loginRecords.iterator().next();
      }
      else throw new FinderException("File was not found");
  }
    
    public java.sql.Date ejbHomeGetLastLoginByLoginID(Integer loginID) throws FinderException{
    	StringBuffer sql = new StringBuffer();
    	sql.append(" select max(in_stamp) from ic_login_rec  ");
    	sql.append(" where ic_login_id =  ").append(loginID);
    	sql.append(" and in_stamp < ");
    	sql.append(" (select max(in_stamp) from ic_login_rec where ic_login_id =").append(loginID).append( ") ");
    	try {
			return getDateTableValue(sql.toString());
		} catch (SQLException e) {
			throw new FinderException(e.getMessage());
		}
    }
    
    public java.sql.Date ejbHomeGetLastLoginByUserID(Integer userID) throws FinderException{
    	StringBuffer sql = new StringBuffer();
    	sql.append(" select max(in_stamp) from ic_login_rec r, ic_login l  ");
    	sql.append(" where l.ic_login_id = r.ic_login_id ");
    	sql.append(" and l.ic_user_id = ").append(userID);
    	sql.append(" and in_stamp < ");
    	sql.append(" (select max(in_stamp) from ic_login_rec r2,ic_login l2 where r2.ic_login_id = l2.ic_login_id  ");
    	sql.append(" and l2.ic_user_id = ").append(userID).append(" ) ");
    	
    	try {
			return getDateTableValue(sql.toString());
		} catch (SQLException e) {
			throw new FinderException(e.getMessage());
		}
    }


}
