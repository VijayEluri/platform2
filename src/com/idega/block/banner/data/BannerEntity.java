//idega 2001 - Laddi

package com.idega.block.banner.data;


import com.idega.core.data.ICObjectInstance;
import java.sql.*;
import com.idega.data.*;

public class BannerEntity extends GenericEntity{

	public BannerEntity(){
		super();
	}

	public BannerEntity(int id)throws SQLException{
		super(id);
	}

	public void initializeAttributes(){
		addAttribute(getIDColumnName());
		addAttribute(getColumnNameAttribute(),"Attribute",true,true,String.class);
    addManyToManyRelationShip(ICObjectInstance.class,"BA_BANNER_IC_OBJECT_INSTANCE");
    addManyToManyRelationShip(AdEntity.class,"BA_BANNER_AD");
	}

	public static String getColumnNameBannerID() { return "BA_BANNER_ID"; }
	public static String getColumnNameAttribute() { return "ATTRIBUTE"; }
	public static String getEntityTableName() { return "BA_BANNER"; }

  public String getIDColumnName(){
		return getColumnNameBannerID();
	}

	public String getEntityName(){
		return getEntityTableName();
	}

  public void setAttribute(String attribute) {
    setColumn(getColumnNameAttribute(),attribute);
  }


  public String getAttribute() {
    return (String) getColumnValue(getColumnNameAttribute());
  }

  public void delete() throws SQLException {
    removeFrom(ICObjectInstance.getStaticInstance(ICObjectInstance.class));
    removeFrom(AdEntity.getStaticInstance(AdEntity.class));
    super.delete();
  }
}