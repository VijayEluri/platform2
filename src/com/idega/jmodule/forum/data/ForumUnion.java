package com.idega.jmodule.forum.data;

import com.idega.data.*;
import java.sql.SQLException;

/**
 * Title:        idegaForms
 * Description:
 * Copyright:    Copyright (c) 2000 idega margmi�lun hf.
 * Company:      idega margmi�lun hf.
 * @author idega 2000 - idega team - <a href="mailto:gummi@idega.is">Gu�mundur �g�st S�mundsson</a>
 * @version 1.0
 */

public class ForumUnion extends GenericEntity {

  public ForumUnion() {
    super();
  }

  public ForumUnion(int id) throws SQLException{
    super(id);
  }

  public void initializeAttributes() {
    /**@todo: implement this com.idega.data.GenericEntity abstract method*/
  }
  public String getEntityName() {
    return "forum_union";
  }
}