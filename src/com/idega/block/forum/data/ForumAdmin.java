package com.idega.block.forum.data;

import com.idega.data.*;
import java.sql.*;

/**
 * Title:        idegaForms
 * Description:
 * Copyright:    Copyright (c) 2000 idega margmi�lun hf.
 * Company:      idega margmi�lun hf.
 * @author idega 2000 - idega team - <a href="mailto:gummi@idega.is">Gu�mundur �g�st S�mundsson</a>
 * @version 1.0
 */

public class ForumAdmin extends GenericEntity {

  public ForumAdmin(){
    super();
  }

  public ForumAdmin(int id) throws SQLException{
    super(id);
  }

  public void initializeAttributes() {
    addAttribute(getIDColumnName());
    addAttribute("use_forums","Nota umr��uflokka valmynd",true,true,"java.lang.Boolean");
  }

  public String getEntityName() {
    return "fo_forum_admin";
  }

  public boolean getUseForums(){
    return this.getBooleanColumnValue("use_forums");
  }

  public void setUseForums(boolean bool){
    this.setColumn("use_forums", bool );
  }

} // class ForumAdmin
