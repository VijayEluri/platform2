/*
 * $Id: Application.java,v 1.1 2001/06/15 01:31:22 palli Exp $
 *
 * Copyright (C) 2001 Idega hf. All Rights Reserved.
 *
 * This software is the proprietary information of Idega hf.
 * Use is subject to license terms.
 *
 */
package is.idegaweb.campus.entity;

import com.idega.data.GenericEntity;
import com.idega.data.EntityAttribute;
import java.sql.SQLException;

/**
 * A specific application for the campus system.
 *
 * @author <a href="mailto:palli@idega.is">Pall Helgason</a>
 * @version 1.0
 */
public class Application extends GenericEntity {
  private static String name_ = "cam_application";
  private static String applicationId_ = "app_application_id";

  public Application() {
    super();
  }

  public Application(int id) throws SQLException {
    super(id);
  }

  public void initializeAttributes() {
    addAttribute(getIDColumnName());
    addAttribute(applicationId_,"Ums�knarn�mer",true,true,"java.lang.Integer","one-to-one","com.idega.block.application.data.Application");
  }

  public String getEntityName() {
    return(name_);
  }

  public void setAppApplicationId(int id) {
    setColumn(applicationId_,id);
  }

  public void setAppApplicationId(Integer id) {
    setColumn(applicationId_,id);
  }

  public Integer getAppApplicationId() {
    return((Integer)getColumnValue(applicationId_));
  }
}