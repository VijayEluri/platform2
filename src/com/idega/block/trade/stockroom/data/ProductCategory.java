package com.idega.block.trade.stockroom.data;

import com.idega.data.*;
import java.sql.SQLException;


/**
 * Title:        IW Trade
 * Description:
 * Copyright:    Copyright (c) 2001
 * Company:      idega.is
 * @author       idega team - <br><a href="mailto:gummi@idega.is">Gu�mundur �g�st S�mundsson</a><br><a href="mailto:gimmi@idega.is">Gr�mur J�nsson</a>
 * @version 1.0
 */

public class ProductCategory extends TreeableEntity {

  public ProductCategory() {
  }


  public ProductCategory(int id) throws SQLException {
    super(id);
  }


  public void initializeAttributes() {
    this.addAttribute(this.getIDColumnName());
    this.addAttribute(getCategoryNameColumnName(),"Nafn",true,true,String.class,255);
    this.addAttribute(getDescriptionColumnName(),"L�sing",true,true,String.class,1000);
    this.addAttribute(getIsValidColumnName(),"� notkun",true,true,Boolean.class);
    this.addAttribute(getExtraInfoColumnName(),"Aukauppl�singar",true,true,String.class,1000);
    this.addTreeRelationShip();
  }

  public String getEntityName(){
    return "SR_PRODUCT_CATEGORY";
  }

  public static String getCategoryNameColumnName(){return "category_name";}
  public static String getDescriptionColumnName(){return "description";}
  public static String getExtraInfoColumnName(){return "extra_info";}
  public static String getCategoryTypeColumnName(){return "category_type";}
  public static String getIsValidColumnName(){return "is_valid";}

  public String getName(){
    return getStringColumnValue(getCategoryNameColumnName());
  }
  public String getDescription(){
    return getStringColumnValue(getDescriptionColumnName());
  }
  public String getExtraInfo(){
    return getStringColumnValue(getExtraInfoColumnName());
  }
  public boolean getIsValid(){
    return getBooleanColumnValue(getIsValidColumnName());
  }
  public void setName(String name){
    setColumn(getCategoryNameColumnName(),name);
  }
  public void setDescription(String description){
    setColumn(getDescriptionColumnName(),description);
  }
  public void setExtraInfo(String info){
    setColumn(getExtraInfoColumnName(),info);
  }
  public void setIsValid(boolean valid){
    setColumn(getIsValidColumnName(),valid);
  }


} // Class ProductCategory