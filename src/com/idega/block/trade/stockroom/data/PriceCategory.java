package com.idega.block.trade.stockroom.data;

import java.sql.*;
import java.util.List;
import com.idega.data.*;
import com.idega.core.data.*;


/**
 * Title:        IW Trade
 * Description:
 * Copyright:    Copyright (c) 2001
 * Company:      idega.is
 * @author 2000 - idega team - <br><a href="mailto:gummi@idega.is">Gu�mundur �g�st S�mundsson</a><br><a href="mailto:gimmi@idega.is">Gr�mur J�nsson</a>
 * @version 1.0
 */

public class PriceCategory extends GenericEntity{

  public static final String PRICETYPE_PRICE = "sr_pricetype_price";
  public static final String PRICETYPE_DISCOUNT = "sr_pricetype_discount";

  public PriceCategory(){
    super();
  }
  public PriceCategory(int id)throws SQLException{
    super(id);
  }
  public void initializeAttributes(){
    addAttribute(getIDColumnName());
    addAttribute(getColumnNameName(), "Name", true, true, String.class, 255);
    addAttribute(getColumnNameDescription(), "L�sing", true, true, String.class, 255);
    addAttribute(getColumnNameType(),"Type",true,true,String.class,255);
    addAttribute(getColumnNameExtraInfo(), "A�rar upplysingar", true, true, String.class, 255);
    addAttribute(getColumnNameNetbookingCategory(), "Ver�flokkur fyrir netb�kun", true, true, Boolean.class, 255);
    addAttribute(getColumnNameSupplierId(),"supplier_id (owner)", true, true, Integer.class, "many_to_one", Supplier.class);
    addAttribute(getColumnNameParentId(),"parent_id", true, true, Integer.class, "many_to_one", PriceCategory.class);
    addAttribute(getColumnNameIsValid(), "is valid", true, true, Boolean.class);

    this.addManyToManyRelationShip(Address.class);
    this.addTreeRelationShip();
  }

  public void delete() {
    try {
      setColumn(getColumnNameIsValid(), false);
      this.update();
    }catch (SQLException sql) {
      sql.printStackTrace(System.err);
    }
  }

  public void setDefaultValues() {
    setColumn(getColumnNameIsValid(), true);
  }

  public String getEntityName(){
    return "SR_PRICE_CATEGORY";
  }
  public String getName(){
    return getStringColumnValue(getColumnNameName());
  }

  public void setName(String name){
    setColumn(getColumnNameName(),name);
  }

  public String getDescription() {
    return getStringColumnValue(getColumnNameDescription());
  }

  public void setDescription(String description) {
    setColumn(getColumnNameDescription(),description);
  }

  public String getExtraInfo(){
    return getStringColumnValue(getColumnNameExtraInfo());
  }

  public void setExtraInfo(String extraInfo){
    setColumn(getColumnNameExtraInfo(),extraInfo);
  }


  public String getType(){
    return getStringColumnValue(getColumnNameType());
  }

  public void setType(String type){
    setColumn(getColumnNameType(),type);
  }

  public void isNetbookingCategory(boolean value){
    setColumn(getColumnNameNetbookingCategory(), value);
  }

  public boolean isNetbookingCategory(){
    return getBooleanColumnValue(getColumnNameNetbookingCategory());
  }

  public void setSupplierId(int id){
    setColumn(getColumnNameSupplierId(), id);
  }

  public int getSupplierId(){
    return getIntColumnValue(getColumnNameSupplierId());
  }

  public void setParentId(int id){
    setColumn(getColumnNameParentId(), id);
  }

  public int getParentId(){
    return getIntColumnValue(getColumnNameParentId());
  }

  public static String getColumnNameName() {return "CATEGORY_NAME";}
  public static String getColumnNameDescription() {return "DESCRIPTION";}
  public static String getColumnNameType(){return "CATEGORY_TYPE";}
  public static String getColumnNameExtraInfo() {return "EXTRA_INFO";}
  public static String getColumnNameSupplierId() {return "SUPPLIER_ID";}
  public static String getColumnNameParentId() {return "PARENT_ID";}
  public static String getColumnNameNetbookingCategory() {return "NETBOOKING_CATEGORY";}
  public static String getColumnNameIsValid() {return "IS_VALID";}






}
