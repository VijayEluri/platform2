package com.idega.block.finance.data;

import java.sql.*;
import com.idega.data.*;

/**
 * Title:
 * Description:
 * Copyright:    Copyright (c) 2001
 * Company:      idega multimedia
 * @author       <a href="mailto:aron@idega.is">Aron Birkir</a>
 * @version 1.0
 */

public class AccountPhoneEntry extends GenericEntity {

  public AccountPhoneEntry() {
    super();
  }
  public AccountPhoneEntry(int id)throws SQLException{
    super(id);
  }
  public void initializeAttributes() {
    addAttribute(getIDColumnName());
    addAttribute(getColumnNameAccountId(),"Account", true, true, Integer.class,"many-to-one",com.idega.block.finance.data.Account.class);
    addAttribute(getColumnNameCashierId(),"Cashier",true,true,Integer.class,"many-to-one",com.idega.block.finance.data.Cashier.class);
    addAttribute(getColumnNameMainNumber(),"main number",true,true,String.class);
    addAttribute(getColumnNameSubNumber(),"sub number",true,true,String.class);
    addAttribute(getColumnNamePhonedNumber(),"main number",true,true,String.class);
    addAttribute(getColumnNamePhonedStamp(),"phone stamp",true,true,java.sql.Timestamp.class);
    addAttribute(getColumnNameDayDuration(),"main number",true,true,Integer.class);
    addAttribute(getColumnNameNightDuration(),"phone stamp",true,true,Integer.class);
    addAttribute(getColumnNameDuration(),"sub number",true,true,Integer.class);
    addAttribute(getColumnNamePrice(),"price",true,true,Float.class);
    addAttribute(getColumnNameLastUpdated(),"Last updated",true,true,java.sql.Timestamp.class);
    addAttribute(getColumnNameCashierId(),"Cashier",true,true,Integer.class,"many-to-one",com.idega.block.finance.data.Cashier.class);
  }

  public static String getEntityTableName(){ return "FIN_ACC_ENTRY"; }
  public static String getColumnNameAccountId(){ return "FIN_ACCOUNT_ID"; }
  public static String getColumnNameCashierId(){ return "FIN_CASHIER_ID"; }
  public static String getColumnNameMainNumber(){ return "MAIN_NUMBER"; }
  public static String getColumnNameSubNumber(){ return "SUB_NUMBER"; }
  public static String getColumnNamePhonedNumber(){ return "PHONED_NUMBER"; }
  public static String getColumnNamePhonedStamp(){ return "PHONED_STAMP"; }
  public static String getColumnNameDayDuration(){ return "DAY_DURATION";}
  public static String getColumnNameNightDuration(){ return "NIGHT_DURATION";}
  public static String getColumnNameDuration(){ return "DURATION";}
  public static String getColumnNamePrice(){ return "TOTAL_PRICE"; }
  public static String getColumnNameLastUpdated(){ return "LAST_UPDATED"; }

  public String getEntityName() {
    return getEntityTableName();
  }
  public int getAccountId(){
    return getIntColumnValue(getColumnNameAccountId());
  }
  public void setAccountId(Integer account_id){
    setColumn(getColumnNameAccountId(), account_id);
  }
  public void setAccountId(int account_id){
    setColumn(getColumnNameAccountId(), account_id);
  }

  public int getCashierId(){
    return getIntColumnValue(getColumnNameCashierId());
  }
  public void setCashierId(Integer member_id){
    setColumn(getColumnNameCashierId(), member_id);
  }
  public void setCashierId(int member_id){
    setColumn(getColumnNameCashierId(), member_id);
  }
  public String getMainNumber(){
    return getStringColumnValue(getColumnNameMainNumber());
  }
  public void setMainNumber(String number){
    setColumn(getColumnNameMainNumber(), number);
  }
  public String getSubNumber(){
    return getStringColumnValue(getColumnNameSubNumber());
  }
  public void setSubNumber(String number){
    setColumn(getColumnNameSubNumber(), number);
  }
  public String getPhonedNumber(){
    return getStringColumnValue(getColumnNamePhonedNumber());
  }
  public void setPhoneNumber(String number){
    setColumn(getColumnNamePhonedNumber(), number);
  }

  public Timestamp getPhonedStamp(){
    return (Timestamp) getColumnValue(getColumnNamePhonedStamp());
  }
  public void setPhonedStamp(Timestamp stamp){
    setColumn(getColumnNamePhonedStamp(), stamp);
  }
  public Timestamp getLastUpdated(){
    return (Timestamp) getColumnValue(getColumnNameLastUpdated());
  }
  public void setLastUpdated(Timestamp last_updated){
    setColumn(getColumnNameLastUpdated(), last_updated);
  }
  public void setPrice(Float price){
    setColumn(getColumnNamePrice(), price);
  }
  public float getPrice(){
    return getIntColumnValue(getColumnNamePrice());
  }

  public void setDayDuration(int seconds){
    setColumn(getColumnNameDayDuration(),seconds);
  }
  public void setDayDuration(Integer seconds){
    setColumn(getColumnNameDayDuration(),seconds);
  }
  public int getDayDuration(){
    return getIntColumnValue(getColumnNameDayDuration());
  }

  public void setDuration(int seconds){
    setColumn(getColumnNameDuration(),seconds);
  }
  public void setDuration(Integer seconds){
    setColumn(getColumnNameDuration(),seconds);
  }
  public int getDuration(){
    return getIntColumnValue(getColumnNameDuration());
  }

  public void setNightDuration(int seconds){
    setColumn(getColumnNameNightDuration(),seconds);
  }
  public void setNightDuration(Integer seconds){
    setColumn(getColumnNameNightDuration(),seconds);
  }
  public int getNightDuration(){
    return getIntColumnValue(getColumnNameNightDuration());
  }

}
