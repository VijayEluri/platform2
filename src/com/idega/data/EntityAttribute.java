//idega 2000 - Tryggvi Larusson
/*
*Copyright 2000 idega.is All Rights Reserved.
*/

package com.idega.data;

/**
*@author <a href="mailto:tryggvi@idega.is">Tryggvi Larusson</a>
*@version 1.2
*/
public class EntityAttribute implements IDOEntityField {

	private String name;
	//private Object value;
	private String longName;
	private String relationShip;
	//private String storageClassName;
	//private int storageClassType;
	private Class storageClass;
	private boolean editable;
	private boolean visible;
	private Class relationShipClass;
	private int maxLength;
	private boolean nullable = true;
	private String attributeType;
	private boolean isPrimaryKey = false;
	private boolean isUnique = false;
	private String _description = null;

	private GenericEntityDefinition entityDefinition;

	public static final int TYPE_JAVA_LANG_INTEGER = 1;
	public static final int TYPE_JAVA_LANG_STRING = 2;
	public static final int TYPE_JAVA_LANG_BOOLEAN = 3;
	public static final int TYPE_JAVA_LANG_FLOAT = 4;
	public static final int TYPE_JAVA_LANG_DOUBLE = 5;
	public static final int TYPE_JAVA_SQL_DATE = 6;
	public static final int TYPE_JAVA_SQL_TIMESTAMP = 7;
	public static final int TYPE_JAVA_SQL_TIME = 8;
	public static final int TYPE_COM_IDEGA_UTIL_GENDER = 9;
	public static final int TYPE_COM_IDEGA_DATA_BLOBWRAPPER = 10;
	public static final int TYPE_JAVA_UTIL_DATE = 11;

	public EntityAttribute() {
		setStorageClass(Integer.class);
		setRelationShipType("unconnected");
		setEditable(false);
		setVisible(false);
		setAttributeType("column");
		setMaxLength(-1);
	}

	public EntityAttribute(GenericEntityDefinition definition) {
		this();
		this.setDeclaredEntity(definition);
	}

	public EntityAttribute(String columnName) {
		setName(columnName);
		setLongName(columnName);
		setStorageClass(Integer.class);
		setRelationShipType("unconnected");
		setEditable(false);
		setVisible(false);
		setAttributeType("column");
		setMaxLength(-1);
	}

	public EntityAttribute(String columnName, Object columnValue) {
		setName(columnName);
		setLongName(columnName);
		setStorageClass(columnValue.getClass());
		setRelationShipType("unconnected");
		setEditable(false);
		setVisible(false);
		setAttributeType("column");
		setMaxLength(-1);
	}

	public void setName(String name) {
		//this.name=name.toLowerCase();
		this.name = name.toUpperCase();
	}

	public String getName() {
		return this.name;
	}

	public void setAttributeType(String attributeType) {
		this.attributeType = attributeType;
	}

	public String getAttributeType() {
		return attributeType;
	}

	public void setLongName(String longName) {
		this.longName = longName;
	}

	public String getLongName() {
		return longName;
	}

	public void setRelationShipType(String type) {
		relationShip = type;
	}

	public String getRelationShipType() {
		return relationShip;
	}

	public String getStorageClassName() {
		String className = this.storageClass.getName();
		/*int classType=getStorageClassType();
		if(classType==TYPE_JAVA_LANG_INTEGER){
		  className="java.lang.Integer";
		}
		else if(classType==TYPE_JAVA_LANG_STRING){
		  className="java.lang.String";
		}
		else if(classType==TYPE_JAVA_LANG_BOOLEAN){
		  className="java.lang.Boolean";
		}
		else if(classType==TYPE_JAVA_LANG_FLOAT){
		  className="java.lang.Float";
		}
		else if(classType==TYPE_JAVA_LANG_DOUBLE){
		  className="java.lang.Double";
		}
		else if(classType==TYPE_JAVA_SQL_TIMESTAMP){
		  className="java.sql.Timestamp";
		}
		else if(classType==TYPE_JAVA_SQL_DATE){
		  className="java.sql.Date";
		}
		else if(classType==TYPE_JAVA_SQL_TIME){
		  className="java.sql.Time";
		}
		else if(classType==TYPE_COM_IDEGA_UTIL_GENDER){
		  className="com.idega.util.Gender";
		}
		else if(classType==TYPE_COM_IDEGA_DATA_BLOBWRAPPER){
		  className="com.idega.data.BlobWrapper";
		}*/
		return className;
		//return storageClassName;
	}

	public void setStorageClass(Class storageClass) {
		this.storageClass = storageClass;
		/*String className = storageClass.getName();
		if(className.equals("java.lang.Integer")){
		  setStorageClassType(TYPE_JAVA_LANG_INTEGER);
		}
		else if(className.equals("java.lang.String")){
		  setStorageClassType(TYPE_JAVA_LANG_STRING);
		}
		else if(className.equals("java.lang.Boolean")){
		  setStorageClassType(TYPE_JAVA_LANG_BOOLEAN);
		}
		else if(className.equals("java.lang.Double")){
		  setStorageClassType(TYPE_JAVA_LANG_DOUBLE);
		}
		else if(className.equals("java.lang.Float")){
		  setStorageClassType(TYPE_JAVA_LANG_FLOAT);
		}
		else if(className.equals("java.sql.Timestamp")){
		  setStorageClassType(TYPE_JAVA_SQL_TIMESTAMP);
		}
		else if(className.equals("java.sql.Date")){
		  setStorageClassType(TYPE_JAVA_SQL_DATE);
		}
		else if(className.equals("java.util.Date")){
		  setStorageClassType(TYPE_JAVA_UTIL_DATE);
		}
		else if(className.equals("java.sql.Time")){
		  setStorageClassType(TYPE_JAVA_SQL_TIME);
		}
		else if(className.equals("com.idega.util.Gender")){
		  setStorageClassType(TYPE_COM_IDEGA_UTIL_GENDER);
		}
		else if(className.equals("com.idega.data.BlobWrapper")){
		  setStorageClassType(TYPE_COM_IDEGA_DATA_BLOBWRAPPER);
		}
		*/
		//storageClassName=className;
	}

	/**
	 * @deprecated Replaced with setStorageClass();
	 */
	public void setStorageClassType(int class_type) {
		if (class_type == TYPE_JAVA_LANG_INTEGER) {
			setStorageClass(java.lang.Integer.class);
		} else if (class_type == TYPE_JAVA_LANG_STRING) {
			setStorageClass(java.lang.String.class);
		} else if (class_type == TYPE_JAVA_LANG_BOOLEAN) {
			setStorageClass(java.lang.Boolean.class);
		} else if (class_type == TYPE_JAVA_LANG_FLOAT) {
			setStorageClass(java.lang.Float.class);
		} else if (class_type == TYPE_JAVA_LANG_DOUBLE) {
			setStorageClass(java.lang.Double.class);
		} else if (class_type == TYPE_JAVA_SQL_TIMESTAMP) {
			setStorageClass(java.lang.Integer.class);
		} else if (class_type == TYPE_JAVA_SQL_DATE) {
			setStorageClass(java.sql.Date.class);
		} else if (class_type == TYPE_JAVA_UTIL_DATE) {
			setStorageClass(java.util.Date.class);
		} else if (class_type == TYPE_JAVA_SQL_TIME) {
			setStorageClass(java.sql.Time.class);
		} else if (class_type == TYPE_COM_IDEGA_UTIL_GENDER) {
			setStorageClass(com.idega.util.Gender.class);
		} else if (class_type == TYPE_COM_IDEGA_DATA_BLOBWRAPPER) {
			setStorageClass(com.idega.data.BlobWrapper.class);
		}
	}

	public Class getStorageClass() {
		return this.storageClass;
	}

	/**
	 * @deprecated replaced with getStorageClass()
	 */
	public int getStorageClassType() {
		//return storageClassType;
		String className = storageClass.getName();
		if (className.equals("java.lang.Integer")) {
			return (TYPE_JAVA_LANG_INTEGER);
		} else if (className.equals("java.lang.String")) {
			return (TYPE_JAVA_LANG_STRING);
		} else if (className.equals("java.lang.Boolean")) {
			return (TYPE_JAVA_LANG_BOOLEAN);
		} else if (className.equals("java.lang.Double")) {
			return (TYPE_JAVA_LANG_DOUBLE);
		} else if (className.equals("java.lang.Float")) {
			return (TYPE_JAVA_LANG_FLOAT);
		} else if (className.equals("java.sql.Timestamp")) {
			return (TYPE_JAVA_SQL_TIMESTAMP);
		} else if (className.equals("java.sql.Date")) {
			return (TYPE_JAVA_SQL_DATE);
		} else if (className.equals("java.util.Date")) {
			return (TYPE_JAVA_UTIL_DATE);
		} else if (className.equals("java.sql.Time")) {
			return (TYPE_JAVA_SQL_TIME);
		} else if (className.equals("com.idega.util.Gender")) {
			return (TYPE_COM_IDEGA_UTIL_GENDER);
		} else if (className.equals("com.idega.data.BlobWrapper")) {
			return (TYPE_COM_IDEGA_DATA_BLOBWRAPPER);
		} else {
			throw new RuntimeException("StorageClassType for " + className + " not defined");
		}
	}

	public void setEditable(boolean ifEditable) {
		editable = ifEditable;
	}

	public boolean getIfEditable() {
		return editable;
	}

	public void setVisible(boolean ifVisible) {
		visible = ifVisible;
	}

	public boolean getIfVisible() {
		return visible;
	}

	public String getRelationShipClassName() {
		if (relationShipClass != null) {
			return relationShipClass.getName();
		}
		return null;
	}

	/**
	 * Returns null if relationshipClass is not set
	 */
	public Class getRelationShipClass() {
		return relationShipClass;
	}

	public void setRelationShipClass(Class relClass) {
		relationShipClass = relClass;
	}

	public void setMaxLength(int maxLength) {
		this.maxLength = maxLength;
	}

	public int getMaxLength() {
		return maxLength;
	}

	public void setNullable(boolean ifNullable) {
		nullable = ifNullable;
	}

	public boolean getIfNullable() {
		return nullable;
	}

	public void setAsPrimaryKey(boolean primaryKey) {
		if (isPrimaryKey != primaryKey) {
			isPrimaryKey = primaryKey;
			if (primaryKey) {
				this.setNullable(false);
				if (this.entityDefinition != null) {
					this.entityDefinition.setFieldAsPartOfPrimaryKey(this);
				}
			}
		}
	}

	public boolean isPrimaryKey() {
		return isPrimaryKey;
	}

	public String getColumnName() {
		return getName().toUpperCase();
	}

	public boolean getIfUnique() {
		return this.isUnique;
	}

	public void setUnique(boolean ifUnique) {
		isUnique = ifUnique;
	}

	public boolean isOneToNRelationship() {
		return (this.relationShipClass != null);
	}

	public void setDeclaredEntity(GenericEntityDefinition definition) {
		this.entityDefinition = definition;
	}

	public IDOEntityDefinition getDeclaredEntity() {
		return this.entityDefinition;
	}
	public String getUniqueFieldName() {
		return this.getColumnName();
	}
	public String getSQLFieldName() {
		return this.getColumnName();
	}
	public Class getDataTypeClass() {
		return this.getStorageClass();
	}
	public boolean isNullAllowed() {
		return this.nullable;
	}
	public boolean isPartOfPrimaryKey() {
		return isPrimaryKey;
	}
	public boolean isPartOfManyToOneRelationship() {
		return (getRelationShipClass() != null);
	}
	public IDOEntityDefinition getManyToOneRelated() throws IDORelationshipException {
		if(isPartOfManyToOneRelationship()){
			return IDOLookup.instanciateEntity(this.getRelationShipClass()).getEntityDefinition();
		} else {
			throw new IDORelationshipException("This field is not part of many-to-one relationship");
		}
	}
}
