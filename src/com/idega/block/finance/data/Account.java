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

public class Account extends GenericEntity {

  public Account() {
    super();
  }

  public Account(int id)throws SQLException{
          super(id);
  }
  public void initializeAttributes(){
    addAttribute(getIDColumnName());
    addAttribute("member_id", "F�lagi", true, true, "java.lang.Integer","many-to-one","com.idega.data.genericentity.Member");
    addAttribute("name","Heiti",true,true,"java.lang.String");
    addAttribute("last_updated","S��ast Breytt",true,true,"java.sql.Timestamp");
    addAttribute("balance","Sta�a",true,true,"java.lang.Integer");
    addAttribute("cashier_id","Gjaldkeri",true,true,"java.lang.Integer");
    addAttribute("creation_date","Stofndags",true,true,"java.sql.Timestamp");
    addAttribute("extra_info","Athugasemd",true,true,"java.lang.String");
    addAttribute("valid","� Gildi",true,true,"java.lang.Boolean");

  }

  public String getEntityName(){
          return "account";
  }

  public int getMemberId(){
          return getIntColumnValue("member_id");
  }

  public void setMemberId(Integer member_id){
    setColumn("member_id", member_id);
  }

  public void setMemberId(int member_id){
    setColumn("member_id", member_id);
  }

  public String getName(){
    return getStringColumnValue("name");
  }

  public void setName(String name){
    setColumn("name", name);
  }

  public Timestamp getLastUpdated(){
    return (Timestamp) getColumnValue("last_updated");
  }

  public void setLastUpdated(Timestamp last_updated){
    setColumn("last_updated", last_updated);
  }

  public int getCashierId(){
          return getIntColumnValue("cashier_id");
  }

  public void setCashierId(Integer member_id){
          setColumn("cashier_id", member_id);
  }

  public void setCashierId(int member_id){
          setColumn("cashier_id", member_id);
  }

  public int getBalance(){
    return getIntColumnValue("balance");
  }

  public void setBalance(Integer balance){
    setColumn("balance", balance);
  }

  public void setBalance(int balance){
    setColumn("balance", balance);
  }

  public Timestamp getCreationDate(){
    return (Timestamp) getColumnValue("creation_date");
  }

  public void setCreationDate(Timestamp creation_date){
    setColumn("creation_date", creation_date);
  }

  public String getExtraInfo(){
    return getStringColumnValue("extra_info");
  }

  public void setExtraInfo(String extra_info){
    setColumn("extra_info", extra_info);
  }

  public void addKredit(int amount){
    this.setBalance(this.getBalance()-amount);
  }

  public void addKredit(Integer amount){
     this.setBalance(this.getBalance()-amount.intValue());
  }

  public void addDebet(int amount){
    this.setBalance(this.getBalance()+amount);
  }

  public void addDebet(Integer amount){
    this.setBalance(this.getBalance()+amount.intValue());
  }

  public void setValid(boolean valid){
    setColumn("valid",valid);
  }
  public boolean getValid(){
    return getBooleanColumnValue("valid");
  }
}