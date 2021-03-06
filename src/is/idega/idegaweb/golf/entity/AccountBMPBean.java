
package is.idega.idegaweb.golf.entity;

import java.sql.*;
import com.idega.data.*;

/**
 * Title:
 * Description:
 * Copyright:    Copyright (c) 2001
 * Company:      idega multimedia
 * @author       <a href="mailto:aron@idega.is">aron@idega.is</a>
 * @version 1.0
 */

public class AccountBMPBean extends GenericEntity implements Account {

    public void initializeAttributes(){
      addAttribute(getIDColumnName());
      addAttribute("member_id", "F�lagi", true, true, "java.lang.Integer","one-to-many","is.idega.idegaweb.golf.entity.Member");
      addAttribute("name","Heiti",true,true,"java.lang.String");
      addAttribute("union_id", "Kl�bbn�mer", true, true, "java.lang.Integer");
      addAttribute("last_updated","S��ast Breytt",true,true,"java.sql.Timestamp");
      addAttribute("cashier_id","Gerandi",true,true,"java.lang.Integer");
      addAttribute("balance","Sta�a",true,true,"java.lang.Integer");
      addAttribute("extra_info","Athugasemd",true,true,"java.lang.String");
      addAttribute("creation_date","Grei�sludagur",true,true,"java.sql.Timestamp");
      addAttribute("valid","� Gildi",true,true,"java.lang.Boolean");
      addAttribute("account_year_id", "Account year", true, true, "java.lang.Integer");

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

    public int getUnionId(){
            return getIntColumnValue("union_id");
    }

    public void setUnionId(Integer union_id){
      setColumn("union_id", union_id);
    }

    public void setUnionId(int union_id){
      setColumn("union_id", union_id);
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

    public int getRealBalance() throws Exception{
      if(this.getID() > 0){
        String[] balance = com.idega.data.SimpleQuerier.executeStringQuery("select balance from v_acc_bal where account_id = "+this.getID());
        if(balance.length > 0)
          return Integer.parseInt(balance[0]);
      }
      return 0;
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

    public void addToBalance(int amount){
      this.setBalance(this.getBalance()+amount);
    }

    public void addToBalance(Integer amount){
      this.setBalance(this.getBalance()+amount.intValue());
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

    public void setAccountYear(int year){
      setColumn("account_year_id",year);
    }

     public int getAccountYear(){
      return getIntColumnValue("account_year_id");
    }

}
