package is.idega.idegaweb.golf.entity;

import com.idega.data.GenericEntity;
/**
 * Title:        Offence
 * Description:
 * Copyright:    Copyright (c) Idega margmi�lun hf. 2001
 * Company:
 * @author        �gir
 * @version 1.0
 */

public class OffenceBMPBean extends GenericEntity implements Offence {

  public void initializeAttributes(){
      addAttribute(getIDColumnName());
      addAttribute("offence_name","Brot",true,true,"java.lang.String");
      addAttribute("offence_act","a�ger�",true,true,"java.lang.String");
      addAttribute("extra_info","auka uppl�singar",true,true,"java.lang.String");

  }

  public String getEntityName(){
      return "offence";
  }

  public String getName(){
      return getStringColumnValue("offence_name");
  }

  public void setName(String name){
      setColumn("offence_name", name);
  }

  public String getAct() {
      return getStringColumnValue("offence_act");
  }

  public void setAct(String act) {
      setColumn("offence_act", act);
  }

  public String getExtraInfo() {
      return getStringColumnValue("extra_info");
  }

  public void setExtraInfo(String extraInfo) {
      setColumn("extra_info", extraInfo);
  }

}

