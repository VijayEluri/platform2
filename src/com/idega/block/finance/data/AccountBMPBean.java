package com.idega.block.finance.data;

import java.sql.*;
import com.idega.data.*;
import java.util.Collection;

/**
 * Title:
 * Description:
 * Copyright:    Copyright (c) 2001
 * Company:      idega multimedia
 * @author       <a href="mailto:aron@idega.is">Aron Birkir</a>
 * @version 1.0
 */

public class AccountBMPBean extends CategoryEntityBMPBean implements Account{
/*
"FIN_ACCOUNT_ID"	INTEGER NOT NULL,
  "IC_USER_ID"	INTEGER,
  "FIN_CASHIER_ID"	INTEGER NOT NULL,
  "NAME"	VARCHAR(255),
  "LAST_UPDATED"	TIMESTAMP,
  "BALANCE"	FLOAT,
  "CREATION_DATE"	DATE,
  "EXTRA_INFO"	VARCHAR(4000),
  "VALID"	CHAR(1),
*/
  public AccountBMPBean() {
    super();
  }

  public AccountBMPBean(int id)throws SQLException{
          super(id);
  }
  public void initializeAttributes(){
    addAttribute(getIDColumnName());
    addAttribute(getUserIdColumnName(), "User", true, true, java.lang.Integer.class,"many-to-one",com.idega.core.user.data.User.class);
    addAttribute(getColumnTypeId(), "Account type", true, true, java.lang.Integer.class,"many-to-one",com.idega.block.finance.data.AccountType.class);
    addAttribute(getCashierIdColumnName(),"Gjaldkeri",true,true,java.lang.Integer.class,"many-to-one",com.idega.block.finance.data.Cashier.class);
    addAttribute(getNameColumnName(),"Name",true,true,java.lang.String.class);
    addAttribute(getLastUpdatedColumnName(),"Changed",true,true,java.sql.Timestamp.class);
    addAttribute(getBalanceColumnName(),"Balance",true,true,java.lang.Float.class);
    addAttribute(getCreationDateColumnName(),"Created",true,true,java.sql.Timestamp.class);
    addAttribute(getInfoColumnName(),"Info",true,true,java.lang.String.class);
    addAttribute(getValidColumnName(),"Valid",true,true,java.lang.Boolean.class);
    addAttribute(getTypeColumnName(),"TYPE",true,true,java.lang.String.class);

  }

  public String getEntityName(){
    return getEntityTableName();
  }

  public static String getEntityTableName(){ return "FIN_ACCOUNT";}
  public static String getUserIdColumnName(){ return "IC_USER_ID";}
  public static String getCashierIdColumnName(){ return "FIN_CASHIER_ID";}
  public static String getNameColumnName(){ return "NAME";}
  public static String getLastUpdatedColumnName(){ return "LAST_UPDATED";}
  public static String getBalanceColumnName(){ return "BALANCE";}
  public static String getCreationDateColumnName(){ return "CREATION_DATE";}
  public static String getInfoColumnName(){ return "EXTRA_INFO";}
  public static String getValidColumnName(){ return "VALID";}
  public static String getColumnTypeId(){return "FIN_ACCT_TYPE_ID";}
  public static String getTypeColumnName(){return "ACCOUNT_TYPE";}

  public static String typeFinancial = "FINANCE";
  public static String typePhone = "PHONE";

  public int getUserId(){
    return getIntColumnValue(getUserIdColumnName());
  }
  public void setUserId(Integer user_id){
    setColumn(getUserIdColumnName(), user_id);
  }
  public void setUserId(int user_id){
    setColumn(getUserIdColumnName(), user_id);
  }
  public String getName(){
    return getStringColumnValue(getNameColumnName());
  }
  public void setName(String name){
    setColumn(getNameColumnName(), name);
  }
  public Timestamp getLastUpdated(){
    return (Timestamp) getColumnValue(getLastUpdatedColumnName());
  }
  public void setLastUpdated(Timestamp last_updated){
    setColumn(getLastUpdatedColumnName(), last_updated);
  }
  public int getCashierId(){
    return getIntColumnValue(getCashierIdColumnName());
  }
  public void setCashierId(Integer cashier_id){
    setColumn(getCashierIdColumnName(), cashier_id);
  }
  public void setCashierId(int cashier_id){
    setColumn(getCashierIdColumnName(), cashier_id);
  }
  public float getBalance(){
    //return getFloatColumnValue(getBalanceColumnName());
    return 0;
  }
  public void setBalance(Float balance){
    setColumn(getBalanceColumnName(), balance);
  }
  public void setBalance(float balance){
    setColumn(getBalanceColumnName(), balance);
  }
  public Timestamp getCreationDate(){
    return (Timestamp) getColumnValue(getCreationDateColumnName());
  }
  public void setCreationDate(Timestamp creation_date){
    setColumn(getCreationDateColumnName(), creation_date);
  }
  public String getExtraInfo(){
    return getStringColumnValue(getInfoColumnName());
  }
  public void setExtraInfo(String extra_info){
    setColumn(getInfoColumnName(), extra_info);
  }
  public void addKredit(float amount){
    this.setBalance(this.getBalance()-amount);
  }
  public void addKredit(Float amount){
     this.setBalance(this.getBalance()-amount.intValue());
  }
  public void addDebet(float amount){
    this.setBalance(this.getBalance()+amount);
  }
  public void addDebet(Float amount){
    this.setBalance(this.getBalance()+amount.intValue());
  }
  public void addAmount(Float amount){
    this.setBalance(this.getBalance()+amount.intValue());
  }
  public void addAmount(float amount){
    this.setBalance(this.getBalance()+amount);
  }
  public void setValid(boolean valid){
    setColumn(getValidColumnName(),valid);
  }
  public boolean getValid(){
    return getBooleanColumnValue(getValidColumnName());
  }
  public void setType(String type){
    setColumn(getTypeColumnName(),type);
  }
  public String getType(){
    return getStringColumnValue(getTypeColumnName());
  }
  public void setTypeFinancial(){
    setType(typeFinancial);
  }
  public void setTypePhone(){
    setType(typePhone);
  }
  public int getAccountTypeId(){
    return getIntColumnValue( getColumnTypeId() );
  }
  public void setAccountTypeId(int typeId){
    setColumn(getColumnTypeId(),typeId);
  }

  public String getAccountType(){
    return getType();
  }
  public String getAccountName(){
    return getName();
  }
  public int getAccountId(){
    return ((Integer)getPrimaryKey()).intValue();
  }

  // Finders

  public Collection ejbFindAllByUserId(int userId)throws javax.ejb.FinderException{
    return super.idoFindPKsBySQL("select * from "+getEntityTableName()+" where "+getUserIdColumnName()+" = " +userId);
  }
   public Collection ejbFindAllByUserIdAndType(int userId,String type)throws javax.ejb.FinderException{
    return super.idoFindPKsBySQL("select * from "+getEntityTableName()+" where "+getUserIdColumnName()+" = " +userId +" and "+ getTypeColumnName()+" = '"+type+"'");
  }

  public Collection ejbFindBySearch(String id,String first,String middle,String last,String type,int iCategoryId)throws javax.ejb.FinderException{
    StringBuffer sql = new StringBuffer("select * from ");
    sql.append(com.idega.block.finance.data.AccountBMPBean.getEntityTableName());
    sql.append(" a,ic_user u ");
    sql.append(" where a.ic_user_id = u.ic_user_id");
    sql.append(" and a.");
    sql.append(com.idega.block.finance.data.AccountBMPBean.getColumnCategoryId());
    sql.append(" = ");
    sql.append(iCategoryId);
    if(id !=null && !"".equals(id)){
      sql.append(" and a.name like '%");
      sql.append(id);
      sql.append("%' ");
    }
    if(first !=null && !"".equals(first )){
      sql.append(" and u.first_name like '%");
      sql.append(first);
      sql.append("%' ");
    }
    if(middle !=null && !"".equals(middle)){
      sql.append(" and u.middle_name like '%");
      sql.append(middle);
      sql.append("%' ");
    }
    if(last !=null && !"".equals(last)){
      sql.append(" and u.last_name like '%");
      sql.append(last);
      sql.append("%' ");
    }
    if(type !=null && !"".equals(type)){
      sql.append(" and a.account_type like '");
      sql.append(type);
      sql.append("' ");
    }
    //System.err.println(sql.toString());
    return super.idoFindPKsBySQL(sql.toString());

  }

  public Collection ejbFindByAssessmentRound(int roundid) throws javax.ejb.FinderException{
    StringBuffer sql = new StringBuffer("select distinct a.* ");
    sql.append(" from fin_account a,fin_acc_entry e,fin_assessment_round r ");
    sql.append(" where a.fin_account_id = e.fin_account_id ");
    sql.append(" and e.fin_assessment_round_id = r.fin_assessment_round_id ");
    sql.append(" and r.fin_assessment_round_id = ");
    sql.append(roundid);
    return super.idoFindPKsBySQL(sql.toString());
  }

  public Collection ejbFindBySQL(String sql)throws javax.ejb.FinderException{
    return super.idoFindPKsBySQL(sql);
  }

  public int countByTypeAndCategory(String type,int iCategory)throws com.idega.data.IDOException{
    StringBuffer sql = new StringBuffer("select count(*) from fin_account a");
    sql.append(" where a.");
    sql.append(com.idega.block.finance.data.AccountBMPBean.getTypeColumnName());
    sql.append(" = '");
    sql.append(type);
    sql.append("' and a.");
    sql.append(com.idega.block.finance.data.AccountBMPBean.getColumnCategoryId());
    sql.append(" = ");
    sql.append(iCategory);
    return super.idoGetNumberOfRecords(sql.toString());
  }


}
