package com.idega.block.school.data;

import java.util.Collection;

import javax.ejb.FinderException;

import com.idega.data.GenericEntity;
import com.idega.data.IDOQuery;

/**
 * Title:
 * Description:
 * Copyright:    Copyright (c) 2001
 * Company:      idega.is
 * @author 2002 - idega team - <br><a href="mailto:aron@idega.is">Aron Birkir</a><br>
 * @version 1.0
 */

public class SchoolTypeBMPBean extends GenericEntity implements SchoolType{

  public static final String NAME = "type_name";
  public static final String INFO = "type_info";
  public static final String LOC_KEY = "loc_key";
  public static final String SCHOOLCATEGORY = "school_category";
  public static final String SCHOOLTYPE = "sch_school_type";
  public static final String MAXSCHOOLAGE = "max_school_age";
	public static final String IS_FREETIME_TYPE = "is_freetime_type";

  public void initializeAttributes() {
    this.addAttribute(getIDColumnName());
    this.addAttribute(NAME,"Schooltype",true,true,String.class);
    this.addAttribute(INFO,"Info",true,true,String.class);
    this.addAttribute(MAXSCHOOLAGE,"Max school age",true,true,Integer.class);
    this.addAttribute(LOC_KEY,"Localization key",String.class);
		this.addAttribute(IS_FREETIME_TYPE,"Is freetime type",Boolean.class);
    
    addManyToOneRelationship(SCHOOLCATEGORY, SchoolCategory.class);
  }

  public String getEntityName() {
    return SCHOOLTYPE;
  }

  public String getName(){
    return getSchoolTypeName();
  }

  public void setSchoolTypeName(String name){
    setColumn(NAME,name);
  }

  public String getSchoolTypeName(){
    return getStringColumnValue(NAME);
  }

   public void setSchoolTypeInfo(String info){
    setColumn(INFO,info);
  }

  public String getSchoolTypeInfo(){
    return getStringColumnValue(INFO);
  }

	public SchoolCategory getCategory(){
		return (SchoolCategory) getColumnValue(SCHOOLCATEGORY);
	}

  public String getSchoolCategory(){
    return getStringColumnValue(SCHOOLCATEGORY);
  }

	public void setCategory(SchoolCategory category){
		setColumn(SCHOOLCATEGORY,category);
	}

  public void setSchoolCategory(String category){
    setColumn(SCHOOLCATEGORY,category);
  }

  public String getLocalizationKey(){
    return getStringColumnValue(LOC_KEY);
  }

  public void setLocalizationKey(String key){
    setColumn(LOC_KEY,key);
  }

  public int getMaxSchoolAge(){
    return getIntColumnValue(MAXSCHOOLAGE);
  }

  public void setMaxSchoolAge(int maxAge){
    setColumn(MAXSCHOOLAGE,maxAge);
  }
  
  public boolean getIsFreetimeType() {
  	return getBooleanColumnValue(IS_FREETIME_TYPE, false);
  }
  
  public void setIsFreetimeType(boolean isFreetimeType) {
  	setColumn(IS_FREETIME_TYPE, isFreetimeType);
  }

  public Collection ejbFindAllSchoolTypes() throws javax.ejb.FinderException{
	IDOQuery sql = idoQuery();
	sql.appendSelectAllFrom(this);
	sql.appendOrderBy();
	sql.append(NAME);
	return idoFindPKsByQuery(sql);
  }

  public Collection ejbFindAllByCategory(String category) throws javax.ejb.FinderException {
    return super.idoFindAllIDsByColumnBySQL(SCHOOLCATEGORY,category);
  }

	/**
	 *	Finds one SchoolType from a typeKey.
	 *	@throws javax.ejb.FinderException if no SchoolType is found.	
	 */
  public Integer ejbFindByTypeKey(String typeKey) throws javax.ejb.FinderException{
  	IDOQuery query = this.idoQueryGetSelect();
  	query.appendWhereEqualsQuoted(LOC_KEY,typeKey);
  	return (Integer)super.idoFindOnePKByQuery(query);
  }
  
  public Collection ejbFindAllFreetimeTypes() throws FinderException {
  	IDOQuery query = this.idoQueryGetSelect();
  	query.appendWhereEquals(IS_FREETIME_TYPE, true).appendOrderBy(NAME);
  	return idoFindPKsByQuery(query);
  }
}