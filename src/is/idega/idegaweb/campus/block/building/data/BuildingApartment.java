/*
 * $Id: BuildingApartment.java,v 1.1 2001/11/08 14:43:05 aron Exp $
 *
 * Copyright (C) 2001 Idega hf. All Rights Reserved.
 *
 * This software is the proprietary information of Idega hf.
 * Use is subject to license terms.
 *
 */
package is.idega.idegaweb.campus.block.building.data;


import com.idega.data.GenericEntity;
import java.sql.Date;
import java.lang.IllegalStateException;
import java.sql.SQLException;

/**
 * Title:
 * Description:
 * Copyright:    Copyright (c) 2000-2001 idega.is All Rights Reserved
 * Company:      idega
  *@author <a href="mailto:aron@idega.is">Aron Birkir</a>
 * @version 1.1
 */

public class BuildingApartment extends GenericEntity {
 /*
CREATE VIEW "V_BUILDING_APARTMENTS" (
  "BU_APARTMENT_ID",
  "BU_BUILDING_ID",
  "APARTMENT_NAME",
  "BUILDING_NAME"
) AS

select A.BU_APARTMENT_ID,B.BU_BUILDING_ID,A.NAME,B.NAME
from BU_BUILDING B,BU_FLOOR F,BU_APARTMENT A
WHERE A.bu_floor_id = F.BU_FLOOR_ID
AND F.BU_BUILDING_ID = B.BU_BUILDING_ID
*/
  public static String getEntityTableName(){return "V_BUILDING_APARTMENTS";}
  public static String getColumnNameApartmentId(){return "BU_APARTMENT_ID";}
  public static String getColumnNameBuildingId(){return  "BU_BUILDING_ID";}
  public static String getColumnNameApartmentName(){return "APARTMENT_NAME";}
  public static String getColumnNameBuildingName(){return "BUILDING_NAME";}

  public BuildingApartment() {
  }
  public BuildingApartment(int id) throws SQLException {

  }
  public void initializeAttributes() {
    addAttribute(getColumnNameApartmentId(),"Apartment id",true,true,java.lang.Integer.class);
    addAttribute(getColumnNameBuildingId(),"Building id",true,true,java.lang.Integer.class);
    addAttribute(getColumnNameApartmentName(),"Apartment name",true,true,java.lang.String.class);
    addAttribute(getColumnNameBuildingName(),"Building name",true,true,java.lang.String.class);
  }
  public String getEntityName() {
    return(getEntityTableName());
  }
  public String getBuildingName(){
   return getStringColumnValue(getColumnNameBuildingName());
  }
  public String getApartmentName(){
    return getStringColumnValue(getColumnNameApartmentName());
  }
  public int getApartmentId(){
    return getIntColumnValue(getColumnNameApartmentId());
  }

  public int getBuildingId(){
    return getIntColumnValue(getColumnNameBuildingId());
  }
}
