/*
 * $Id: Applied.java,v 1.1 2001/11/08 14:43:05 aron Exp $
 *
 * Copyright (C) 2001 Idega hf. All Rights Reserved.
 *
 * This software is the proprietary information of Idega hf.
 * Use is subject to license terms.
 *
 */
package is.idega.idegaweb.campus.block.application.data;

import com.idega.data.GenericEntity;
import java.sql.SQLException;

/**
 *
 * @author <a href="mailto:palli@idega.is">Pall Helgason</a>
 * @version 1.0
 */
public class Applied extends GenericEntity {
  private static final String name_ = "cam_applied";
  private static final String complexId_ = "bu_complex_id";
  private static final String apartmentTypeId_ = "bu_aprt_type_id";
  private static final String applicationId_ = "cam_application_id";
  private static final String order_ = "ordered";

  public Applied() {
    super();
  }

  public Applied(int id) throws SQLException {
    super(id);
  }

  public void initializeAttributes() {
    addAttribute(getIDColumnName());
    addAttribute(complexId_,"Complex",true,true,java.lang.Integer.class.getClass(),"one-to-many",com.idega.block.building.data.Complex.class.getClass());
    addAttribute(apartmentTypeId_,"Apartment type",true,true,java.lang.Integer.class.getClass(),"one-to-many",com.idega.block.building.data.ApartmentType.class.getClass());
    addAttribute(applicationId_,"Application",true,true,java.lang.Integer.class.getClass(),"one-to-many",is.idega.idegaweb.campus.block.application.data.CampusApplication.class.getClass());
    addAttribute(order_,"Order",true,true,java.lang.Integer.class.getClass());
  }

  public String getEntityName() {
    return(name_);
  }

  public String getComplexIdColumnName() {
    return(complexId_);
  }

  public String getApartmentTypeIdColumnName() {
    return(apartmentTypeId_);
  }

  public String getApplicationIdColumnName() {
    return(applicationId_);
  }

  public String getOrderColumnName() {
    return(order_);
  }

  public void setComplexId(int id) {
    setColumn(complexId_,id);
  }

  public void setComplexId(Integer id) {
    setColumn(complexId_,id);
  }

  public Integer getComplexId() {
    return(getIntegerColumnValue(complexId_));
  }

  public void setApartmentTypeId(int id) {
    setColumn(apartmentTypeId_,id);
  }

  public void setApartmentTypeId(Integer id) {
    setColumn(apartmentTypeId_,id);
  }

  public Integer getApartmentTypeId() {
    return(getIntegerColumnValue(apartmentTypeId_));
  }

  public void setApplicationId(int id) {
    setColumn(applicationId_,id);
  }

  public void setApplicationId(Integer id) {
    setColumn(applicationId_,id);
  }

  public Integer getApplicationId() {
    return(getIntegerColumnValue(applicationId_));
  }

  public void setOrder(int order) {
    setColumn(order_,order);
  }

  public void setOrder(Integer order) {
    setColumn(order_,order);
  }

  public Integer getOrder() {
    return(getIntegerColumnValue(order_));
  }
}
