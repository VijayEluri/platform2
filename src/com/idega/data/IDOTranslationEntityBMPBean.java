/*
 * Created on 13.6.2003
 *
 * To change the template for this generated file go to
 * Window>Preferences>Java>Code Generation>Code and Comments
 */
package com.idega.data;


import javax.ejb.FinderException;

import com.idega.core.localisation.data.ICLocale;


/**
 * Title:		IDOTranslationEntity
 * Description:
 * Copyright:	Copyright (c) 2003
 * Company:		idega Software
 * @author		2003 - idega team - <br><a href="mailto:gummi@idega.is">Gudmundur Agust Saemundsson</a><br>
 * @version		1.0
 */
public abstract class IDOTranslationEntityBMPBean extends GenericEntity {
	
	/**
	 * 
	 */
	public IDOTranslationEntityBMPBean() {
		super();
	}
	

	protected void beforeInitializeAttributes(){
		//TMP - COLUMNNAME_TRANSLATED_ENTITY_ID and COLUMNNAME_LOCALE should be the primarykey
		addAttribute(getIDColumnName());
		
		addManyToOneRelationship(IDOTranslationEntity.COLUMNNAME_TRANSLATED_ENTITY_ID, "Translated entity id", getTranslatedEntityClass());
		setNullable(IDOTranslationEntity.COLUMNNAME_TRANSLATED_ENTITY_ID,false);
		addManyToOneRelationship(IDOTranslationEntity.COLUMNNAME_LOCALE, "Locale id", ICLocale.class);
	}	
	
	protected abstract Class getTranslatedEntityClass();
	
	public String getEntityName(){
		try {
			return IDOLookup.getEntityDefinitionForClass(getTranslatedEntityClass()).getSQLTableName()+"_TR";
		} catch (IDOLookupException e) {
			System.err.println(this.getClass().getName()+"[Error in IDOTranslationEntityBMPBean#getEntityName()]: could not find the entity name because of an IDOLookupException!!!!!");
			return null;
		}
	}
	
	protected Object idoFindTranslation(Object entityToTranslate, ICLocale locale) throws FinderException{
		IDOQuery query = idoQueryGetSelect();
		query.appendWhereEquals(IDOTranslationEntity.COLUMNNAME_TRANSLATED_ENTITY_ID,entityToTranslate);
		query.appendAndEquals(IDOTranslationEntity.COLUMNNAME_LOCALE,locale);
		
		return idoFindOnePKByQuery(query);
	}
	
	public void setPrimaryKey(IDOEntity translatedEntity, ICLocale locale){
		setColumn(IDOTranslationEntity.COLUMNNAME_TRANSLATED_ENTITY_ID,translatedEntity);
		setColumn(IDOTranslationEntity.COLUMNNAME_LOCALE,locale);
	}
	
	public void setTransletedEntity(IDOEntity translatedEntity){
		setColumn(IDOTranslationEntity.COLUMNNAME_TRANSLATED_ENTITY_ID,translatedEntity);
	}
	
	public void setLocale(ICLocale locale){
		setColumn(IDOTranslationEntity.COLUMNNAME_LOCALE,locale);
	}

}
