/*
 *
 * Copyright (C) 2001 Idega hf. All Rights Reserved.
 *
 * This software is the proprietary information of Idega hf.
 * Use is subject to license terms.
 *
 */
package com.idega.data;
import java.io.InputStream;
import java.io.OutputStream;
import java.sql.Connection;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.Collection;
import java.util.Enumeration;
import java.util.HashMap;
import java.util.Hashtable;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.Vector;

import javax.ejb.CreateException;
import javax.ejb.EJBException;
import javax.ejb.EJBLocalHome;
import javax.ejb.EJBLocalObject;
import javax.ejb.EntityContext;
import javax.ejb.FinderException;
import javax.ejb.RemoveException;

import com.idega.idegaweb.IWMainApplicationSettings;
import com.idega.util.database.ConnectionBroker;

import dori.jasper.engine.JRException;
import dori.jasper.engine.JRField;
/**
 * A class to serve as a base implementation for objects mapped to persistent data in the IDO Framework.
 *
 *
 * @author <a href="mailto:tryggvi@idega.is">Tryggvi Larusson</a>
 * @version 1.4
 * @modified <a href="mailto:eiki@idega.is">Eirikur Hrafnsson</a>
 */
public abstract class GenericEntity implements java.io.Serializable, IDOEntity, IDOEntityBean, EntityRepresentation {
	public static final String MANY_TO_ONE = "many-to-one";
	public static final String ONE_TO_MANY = "one-to-many";
	public static final String MANY_TO_MANY = "many-to-many";
	public static final String ONE_TO_ONE = "one-to-one";
	private static Map _theAttributes = new Hashtable();
	private static Map _allStaticClasses = new Hashtable();
	private static String DEFAULT_DATASOURCE = "default";
	//private static NullColumnValue nullColumnValue = new NullColumnValue();
	private String _dataStoreType;
	private int _state = IDOLegacyEntity.STATE_NEW;
	private Map _columns = new Hashtable();
	private Map _updatedColumns;
	private String _dataSource;
	String[] _cachedColumnNameList;
	private EntityContext _entityContext;
	//private EJBHome _ejbHome;
	private EJBLocalHome _ejbHome;
	private Object _primaryKey;
	private Hashtable _theMetaDataAttributes;
	private Vector _insertMetaDataVector;
	private Vector _updateMetaDataVector;
	private Vector _deleteMetaDataVector;
	private Hashtable _theMetaDataIds;
	private Hashtable _theMetaDataTypes;
	private boolean _hasMetaDataRelationship = false;
	private boolean _metaDataHasChanged = false;
	public String _lobColumnName;
	private boolean insertStartData = true;
	protected static String COLUMN_VALUE_TRUE = "Y";
	protected static String COLUMN_VALUE_FALSE = "N";

	/*
	  protected static int STATE_NEW = 0;
	  protected static int STATE_IN_SYNCH_WITH_DATASTORE = 1;
	  protected static int STATE_NOT_IN_SYNCH_WITH_DATASTORE = 2;
	  protected static int STATE_NEW_AND_NOT_IN_SYNCH_WITH_DATASTORE = 3;
	  protected static int STATE_DELETED = 4;
	*/
	protected GenericEntity() {
		this(DEFAULT_DATASOURCE);
	}
	protected GenericEntity(String dataSource) {
		setDatasource(dataSource);
		try {
			firstLoadInMemoryCheck();
		} catch (Error e) {
			System.err.println("Error in " + this.getClass().getName() + ".firstLoadInMemoryCheck()");
			e.printStackTrace();
		}
		setDefaultValues();
	}
	protected GenericEntity(int id) throws SQLException {
		this(id, DEFAULT_DATASOURCE);
	}
	protected GenericEntity(int id, String dataSource) throws SQLException {
		//this(dataSource);
		setDatasource(dataSource);
		//setColumn(getIDColumnName(),new Integer(id));
		firstLoadInMemoryCheck();

		//ejbCreate(new Integer(id));
		//ejbLoad();
		this.findByPrimaryKey(id);
	}
	private void firstLoadInMemoryCheck() {
		GenericEntityDefinition entityDefinition = getGenericEntityDefinition();
		if (entityDefinition == null) {
			//IDOEntityDefinition
			entityDefinition = new GenericEntityDefinition();
			entityDefinition.setSQLTableName(this.getEntityName());
			entityDefinition.setUniqueEntityName(this.getEntityName());
			((PrimaryKeyDefinition)entityDefinition.getPrimaryKeyDefinition()).setPrimaryKeyClass(this.getPrimaryKeyClass());
			this.setGenericEntityDefinition(entityDefinition);
			//IDOEntityDefinition ends

			//First store a static instance of this class
			try {
				_allStaticClasses.put(this.getClass(), this.instanciateEntity(this.getClass()));
			} catch (Exception ex) {
				ex.printStackTrace();
			}
			this.getGenericEntityDefinition().setInterfaceClass(getInterfaceClass());
			//call the ializeAttributes that stores information about columns and relationships
			beforeInitializeAttributes();
			initializeAttributes();
			afterInitializeAttributes();
			setLobColumnName();
			if (EntityControl.getIfEntityAutoCreate()) {
				try {
					DatastoreInterface.getInstance(this).createEntityRecord(this);
				} catch (Exception ex) {
					ex.printStackTrace();
				}
			}
		}
	}
	/**
	 * Meant to be overrided in subclasses to add default attributes
	 */
	protected void beforeInitializeAttributes() {
	}
	/**
	 * Meant to be overrided in subclasses to add default attributes
	 */
	protected void afterInitializeAttributes() {
	}
	/**
	 * Override this function to set staring Data into the record of the entity at creation time
	 */
	public void insertStartData() throws Exception {
	}
	public String getTableName() {
		return getEntityName();
	}
	/**
	 * Subclasses have to implement this method - this should return the name of the underlying table
	 */
	public abstract String getEntityName();
	public abstract void initializeAttributes();
	public java.util.Collection getAttributes() {
		//ties the attribute vector to the subclass of IDOLegacyEntity because
		//the theAttributes variable is static.

		//Map m = getAttributesMap();
		//return m.values();
		return getGenericEntityDefinition().getEntityFieldsCollection();
		//Vector theReturn = (Vector) _theAttributes.get(this.getClass().getName());
		//return theReturn;
	}

	protected GenericEntityDefinition getGenericEntityDefinition() {
		return (GenericEntityDefinition)_theAttributes.get(this.getClass());
	}

	protected GenericEntityDefinition setGenericEntityDefinition(GenericEntityDefinition definition) {
		return (GenericEntityDefinition)_theAttributes.put(this.getClass(), definition);
	}

	//	protected Map getAttributesMap()
	//	{
	//		//ties the attribute vector to the subclass of IDOLegacyEntity because
	//		//the theAttributes variable is static.
	//		Map theReturn = (Map) _theAttributes.get(this.getClass());
	//		if(theReturn == null){
	//			theReturn = new HashMap();
	//			_theAttributes.put(this.getClass(),theReturn);
	//		}
	//		return theReturn;
	//	}

	public void setID(int id) {
		setColumn(getIDColumnName(), new Integer(id));
	}
	public void setID(Integer id) {
		setColumn(getIDColumnName(), id);
	}
	public int getID() {
		return getIntColumnValue(getIDColumnName());
	}
	public Object getPrimaryKeyValue() {
		if (this._primaryKey != null) {
			return _primaryKey;
		} else {
			return this.getValue(getIDColumnName());
		}
	}
	public Integer getIDInteger() {
		return (Integer)getPrimaryKeyValue();
	}
	/**
	 * default unimplemented function, gets the name of the record from the datastore
	 */
	public String getName() {
		Object primaryKey = this.getPrimaryKey();
		if (primaryKey != null)
			return primaryKey.toString();
		return null;
	}
	public BlobWrapper getEmptyBlob(String columnName) {
		return new BlobWrapper(this, columnName);
	}
	/**
	 * default unimplemented function, sets the name of the record in the datastore
	 */
	public void setName(String name) {
		//does nothing
	}
	/**
	 * @see java.lang.Object#toString()
	 * @see com.idega.data.GenericEntity#getName()
	 */
	public String toString() {
//		Object pk = this.getPrimaryKey();
//		if (pk != null) {
//			return pk.toString();
//		} else
//			return "null";
		return this.getName();
	}
	/**
	 * @deprecated Replaced with addAttribute()
	 */
	protected void addColumnName(String columnName) {
		addAttribute(columnName);
	}
	protected void addAttribute(String attributeName) {
		EntityAttribute attribute;
		attribute = new EntityAttribute(attributeName);
		attribute.setDeclaredEntity(getGenericEntityDefinition());
		attribute.setAsPrimaryKey(true);
		attribute.setNullable(false);
		addAttribute(attribute);
	}
	/**
	 * @deprecated Replaced with addAttribute()
	 */
	protected void addColumnName(String columnName, String longName, boolean ifVisible, boolean ifEditable, String storageClassName) {
		addAttribute(columnName, longName, ifVisible, ifEditable, storageClassName);
	}
	public void addAttribute(String attributeName, String longName, boolean ifVisible, boolean ifEditable, String storageClassName) {
		try {
			addAttribute(attributeName, longName, ifVisible, ifEditable, Class.forName(storageClassName));
		} catch (ClassNotFoundException e) {
			throw new RuntimeException("Exception in " + this.getClass().getName() + e.getMessage());
		}
	}
	protected void addAttribute(String attributeName, String longName, boolean ifVisible, boolean ifEditable, Class storageClass) {
		EntityAttribute attribute = new EntityAttribute(attributeName.toLowerCase());
		attribute.setAsPrimaryKey(this.getIDColumnName().equalsIgnoreCase(attributeName));
		attribute.setDeclaredEntity(getGenericEntityDefinition());
		attribute.setLongName(longName);
		attribute.setVisible(ifVisible);
		attribute.setEditable(ifEditable);
		attribute.setStorageClass(storageClass);
		addAttribute(attribute);
	}
	/**
	 * Added by Eirikur Hrafnsson
	 *
	 */
	protected void addAttribute(String attributeName, String longName, boolean ifVisible, boolean ifEditable, String storageClassName, int maxLength) {
		try {
			addAttribute(attributeName, longName, ifVisible, ifEditable, Class.forName(storageClassName), maxLength);
		} catch (ClassNotFoundException e) {
			throw new RuntimeException("Exception in " + this.getClass().getName() + e.getMessage());
		}
	}
	protected void addAttribute(String attributeName, String longName, Class storageClass) {
		addAttribute(attributeName, longName, true, true, storageClass);
	}
	protected void addAttribute(String attributeName, String longName, Class storageClass, int maxLength) {
		addAttribute(attributeName, longName, true, true, storageClass, maxLength);
	}
	protected void addAttribute(String attributeName, String longName, boolean ifVisible, boolean ifEditable, Class storageClass, int maxLength) {
		EntityAttribute attribute = new EntityAttribute(attributeName);
		attribute.setAsPrimaryKey(this.getIDColumnName().equalsIgnoreCase(attributeName));
		attribute.setDeclaredEntity(getGenericEntityDefinition());
		attribute.setLongName(longName);
		attribute.setVisible(ifVisible);
		attribute.setEditable(ifEditable);
		attribute.setStorageClass(storageClass);
		attribute.setMaxLength(maxLength);
		addAttribute(attribute);
	}
	/**
	  * @deprecated Replaced with addAttribute()
	  */
	protected void addColumnName(String columnName, String longName, boolean ifVisible, boolean ifEditable, String storageClassName, String relationShipType, String relationShipClassName) {
		addAttribute(columnName, longName, ifVisible, ifEditable, storageClassName, relationShipType, relationShipClassName);
	}
	protected void addAttribute(String attributeName, String longName, boolean ifVisible, boolean ifEditable, String storageClassName, String relationShipType, String relationShipClassName) {
		try {
			addAttribute(attributeName, longName, ifVisible, ifEditable, Class.forName(storageClassName), relationShipType, Class.forName(relationShipClassName));
		} catch (ClassNotFoundException e) {
			throw new RuntimeException("Exception in " + this.getClass().getName() + e.getMessage());
		}
	}
	protected void addAttribute(String attributeName, String longName, boolean ifVisible, boolean ifEditable, Class storageClass, String relationShipType, Class relationShipClass) {
		EntityAttribute attribute = new EntityAttribute(attributeName);
		attribute.setAsPrimaryKey(this.getIDColumnName().equalsIgnoreCase(attributeName));
		attribute.setDeclaredEntity(getGenericEntityDefinition());
		attribute.setLongName(longName);
		attribute.setVisible(ifVisible);
		attribute.setEditable(ifEditable);
		attribute.setRelationShipType(relationShipType);
		attribute.setRelationShipClass(relationShipClass);
		attribute.setStorageClass(storageClass);
		addAttribute(attribute);
	}
	protected void addAttribute(String attributeName, String longName, boolean ifVisible, boolean ifEditable, String storageClassName, int maxLength, String relationShipType, String relationShipClassName) {
		try {
			addAttribute(attributeName, longName, ifVisible, ifEditable, Class.forName(storageClassName), maxLength, relationShipType, Class.forName(relationShipClassName));
		} catch (ClassNotFoundException e) {
			throw new RuntimeException("Exception in " + this.getClass().getName() + e.getMessage());
		}
	}
	protected void addAttribute(String attributeName, String longName, boolean ifVisible, boolean ifEditable, Class storageClass, int maxLength, String relationShipType, Class relationShipClass) {
		EntityAttribute attribute = new EntityAttribute(attributeName);
		attribute.setAsPrimaryKey(this.getIDColumnName().equalsIgnoreCase(attributeName));
		attribute.setDeclaredEntity(getGenericEntityDefinition());
		attribute.setLongName(longName);
		attribute.setVisible(ifVisible);
		attribute.setEditable(ifEditable);
		attribute.setRelationShipType(relationShipType);
		attribute.setRelationShipClass(relationShipClass);
		attribute.setStorageClass(storageClass);
		attribute.setMaxLength(maxLength);
		
		addAttribute(attribute);
	}
	protected void addAttribute(EntityAttribute attribute) {
		//getAttributesMap().put(attribute.getName().toUpperCase(),attribute);
		getGenericEntityDefinition().addFieldEntity(attribute);
		//getAttributes().addElement(attribute);

	}
	protected void addLanguageAttribute() {
		this.addAttribute(getLanguageIDColumnName(), "Tungum�l", true, true, "java.lang.Integer", "one_to_one", "com.idega.core.localisation.data.Language");
	}
	/**
	 * @deprecated Replaced with getAttribute()
	 */
	public EntityAttribute getColumn(String columnName) {
		return getAttribute(columnName);
	}
	public EntityAttribute getAttribute(String attributeName) {
		//return (EntityAttribute) columns.get(columnName.toLowerCase());
		//EntityAttribute theReturn = (EntityAttribute)getAttributesMap().get(attributeName.toUpperCase());
		EntityAttribute theReturn = getGenericEntityDefinition().getEntityAttribute(attributeName.toUpperCase());
		/**EntityAttribute theReturn = null;
		EntityAttribute tempColumn = null;
		for (Enumeration enumeration = getAttributes().elements(); enumeration.hasMoreElements();)
		{
			tempColumn = (EntityAttribute) enumeration.nextElement();
			if (tempColumn.getColumnName().equalsIgnoreCase(attributeName))
			{
				theReturn = tempColumn;
			}
		}*/
		/*    if(theReturn==null){
		      System.err.println("Error in "+this.getClass().getName()+".getAttribute(): ColumnName='"+attributeName+"' exists in table but not in Entity Class");
		    }*/
		return theReturn;
	}
	protected void addOneToOneRelationship(String relationshipColumnName, Class relatingEntityClass) {
		addOneToOneRelationship(relationshipColumnName, relatingEntityClass.getName(), relatingEntityClass);
	}
	protected void addOneToOneRelationship(String relationshipColumnName, String description, Class relatingEntityClass) {
		addAttribute(relationshipColumnName, description, true, true, Integer.class, com.idega.data.GenericEntity.ONE_TO_ONE, relatingEntityClass);
	}
	protected void addManyToOneRelationship(String relationshipColumnName, Class relatingEntityClass) {
		addManyToOneRelationship(relationshipColumnName, relatingEntityClass.getName(), relatingEntityClass);
	}
	protected void addManyToOneRelationship(String relationshipColumnName, String description, Class relatingEntityClass) {
		try {
			Class primaryKeyInRelatedClass = IDOLookup.getEntityDefinitionForClass(relatingEntityClass).getPrimaryKeyDefinition().getPrimaryKeyClass();
			addAttribute(relationshipColumnName, description, true, true, primaryKeyInRelatedClass, com.idega.data.GenericEntity.MANY_TO_ONE, relatingEntityClass);
		} catch (IDOLookupException e) {
			e.printStackTrace();
		}
	}
	protected void addRelationship(String relationshipName, String relationshipType, String relationshipClassName) {
		try {
			EntityAttribute attribute = new EntityAttribute(getGenericEntityDefinition());
			attribute.setName(relationshipName);
			attribute.setAttributeType("relationship");
			attribute.setRelationShipClass(Class.forName(relationshipClassName));
			addAttribute(attribute);
		} catch (ClassNotFoundException e) {
			throw new RuntimeException("Exception in " + this.getClass().getName() + e.getMessage());
		}
	}
	//	/**
	//	*Constructs an array of GenericEntities through an int Array (containing rows of id's from the datastore) - uses the findByPrimaryKey method to instanciate a new object fetched from the database
	//	*@deprecated Only IDOLegacyEntity method, does not work with pure IDOEntity
	//	**/
	//	public IDOLegacyEntity[] constructArray(int[] id_array) {
	//		IDOLegacyEntity[] returnArr = null;
	//		try {
	//			returnArr = (IDOLegacyEntity[])java.lang.reflect.Array.newInstance(this.getClass(), id_array.length);
	//			for (int i = 0; i < id_array.length; i++) {
	//				returnArr[i] = (IDOLegacyEntity)instanciateEntity(this.getClass());
	//				returnArr[i].findByPrimaryKey(id_array[i]);
	//			}
	//		} catch (Exception ex) {
	//			System.err.println("There was an error in com.idega.data.GenericEntity.constructArray(int[] id_array): " + ex.getMessage());
	//		}
	//		return returnArr;
	//	}
	/**
	*Constructs an array of GenericEntities through a String Array (containing only int's for the rows of id's from the datastore) - uses the findByPrimaryKey method to instanciate a new object fetched from the database
	*@deprecated Only IDOLegacyEntity method, does not work with pure IDOEntity
	**/
	public IDOLegacyEntity[] constructArray(String[] id_array) {
		IDOLegacyEntity[] returnArr = null;
		try {
			returnArr = (IDOLegacyEntity[])java.lang.reflect.Array.newInstance(this.getClass(), id_array.length);
			for (int i = 0; i < id_array.length; i++) {
				returnArr[i] = (IDOLegacyEntity)instanciateEntity(this.getClass());
				returnArr[i].findByPrimaryKey(Integer.parseInt(id_array[i]));
			}
		} catch (Exception ex) {
			System.err.println("There was an error in com.idega.data.GenericEntity.constructArray(String[] id_array): " + ex.getMessage());
		}
		return returnArr;
	}
	protected void setValue(String columnName, Object columnValue) {
		if (columnValue != null) {
			//_columns.put(columnName.toLowerCase(),columnValue);
			_columns.put(columnName.toUpperCase(), columnValue);
		} else {
			removeFromColumn(columnName);
		}
		this.flagColumnUpdate(columnName);
		if ((getEntityState() == IDOLegacyEntity.STATE_NEW) || (getEntityState() == IDOLegacyEntity.STATE_NEW_AND_NOT_IN_SYNCH_WITH_DATASTORE)) {
			setEntityState(IDOLegacyEntity.STATE_NEW_AND_NOT_IN_SYNCH_WITH_DATASTORE);
		} else {
			this.setEntityState(IDOLegacyEntity.STATE_NOT_IN_SYNCH_WITH_DATASTORE);
		}
	}
	protected Object getValue(String columnName) {
		//return _columns.get(columnName.toLowerCase());
		return _columns.get(columnName.toUpperCase());
	}
	/**
	 * Sets the column to null
	 */
	public void removeFromColumn(String columnName) {
		//_columns.remove(columnName.toLowerCase());
		_columns.remove(columnName.toUpperCase());
		this.flagColumnUpdate(columnName);
		//setValue(columnName,this.getNullColumnValue());
	}
	public void setColumn(String columnName, Object columnValue) {
		if (this.getRelationShipClass(columnName) == null) {
			setValue(columnName, columnValue);
		} else {
			if (columnValue instanceof Integer) {
				setValue(columnName, (Integer)columnValue);
			} else if (columnValue instanceof String) {
				setValue(columnName, (String)columnValue);
			}
			//else if (columnValue instanceof IDOLegacyEntity){
			else {
				setValue(columnName, ((IDOEntity)columnValue).getPrimaryKey());
			}
		}
	}
	public void setColumn(String columnName, int columnValue) {
		setValue(columnName, new Integer(columnValue));
	}
	public void setColumn(String columnName, Integer columnValue) {
		setValue(columnName, columnValue);
	}
	public void setColumn(String columnName, float columnValue) {
		setValue(columnName, new Float(columnValue));
	}
	public void setColumn(String columnName, Float columnValue) {
		setValue(columnName, columnValue);
	}
	public void setColumn(String columnName, boolean columnValue) {
		setValue(columnName, new Boolean(columnValue));
	}
	public void setColumn(String columnName, Boolean columnValue) {
		setValue(columnName, columnValue);
	}
	/**
	 * @deprecated replaced with removeFromColumn(columnName)
	 * Sets a column value to null
	**/
	public void setColumnAsNull(String columnName) throws SQLException {
		Connection Conn = null;
		try {
			/**
			 * @todo stringbuffer
			 * */
			Conn = getConnection(getDatasource());
			String sql = "update " + this.getEntityName() + " set " + columnName + " = null where " + this.getIDColumnName() + " = " + this.getPrimaryKeyValueSQLString();

			Conn.createStatement().executeUpdate(sql);
			Conn.commit();
		} catch (SQLException e) {
			e.printStackTrace(System.err);
		} finally {
			if (Conn != null) {
				freeConnection(getDatasource(), Conn);
			}
		}
	}
	/**
	 * The Outputstream must be completele written to when insert() or update() is executed on the Entity Class
	 */
	public OutputStream getColumnOutputStream(String columnName) {
		BlobWrapper wrapper = getBlobColumnValue(columnName);
		if (wrapper == null) {
			wrapper = new BlobWrapper(this, columnName);
			setColumn(columnName, wrapper);
		} else {
			this.flagColumnUpdate(columnName);
			if ((getEntityState() == IDOLegacyEntity.STATE_NEW) || (getEntityState() == IDOLegacyEntity.STATE_NEW_AND_NOT_IN_SYNCH_WITH_DATASTORE)) {
				setEntityState(IDOLegacyEntity.STATE_NEW_AND_NOT_IN_SYNCH_WITH_DATASTORE);
			} else {
				this.setEntityState(IDOLegacyEntity.STATE_NOT_IN_SYNCH_WITH_DATASTORE);
			}
		}
		return wrapper.getOutputStreamForBlobWrite();
	}

	public void setColumn(String columnName, InputStream streamForBlobWrite) {
		BlobWrapper wrapper = getBlobColumnValue(columnName);
		if (wrapper != null) {
			wrapper.setInputStreamForBlobWrite(streamForBlobWrite);
			this.flagColumnUpdate(columnName);
			if ((getEntityState() == IDOLegacyEntity.STATE_NEW) || (getEntityState() == IDOLegacyEntity.STATE_NEW_AND_NOT_IN_SYNCH_WITH_DATASTORE)) {
				setEntityState(IDOLegacyEntity.STATE_NEW_AND_NOT_IN_SYNCH_WITH_DATASTORE);
			} else {
				this.setEntityState(IDOLegacyEntity.STATE_NOT_IN_SYNCH_WITH_DATASTORE);
			}
		} else {
			wrapper = new BlobWrapper(this, columnName);
			wrapper.setInputStreamForBlobWrite(streamForBlobWrite);
			setColumn(columnName, wrapper);
		}
	}

	public BlobWrapper getBlobColumnValue(String columnName) {
		BlobWrapper wrapper = (BlobWrapper)getColumnValue(columnName);
		if (wrapper == null) {
			wrapper = new BlobWrapper(this, columnName);
			this.setColumn(columnName, wrapper);
		}
		return wrapper;
	}
	public InputStream getInputStreamColumnValue(String columnName){
		BlobWrapper wrapper = getBlobColumnValue(columnName);
		/*if(wrapper==null){
		  wrapper = new BlobWrapper(this,columnName);
		  this.setColumn(columnName,wrapper);
		}*/
		return wrapper.getBlobInputStream();
	}
	public Object getColumnValue(String columnName) {
		Object returnObj = null;
		Object value = getValue(columnName);
		Class relationClass = this.getRelationShipClass(columnName);
		if (value instanceof com.idega.data.IDOEntity) {
			returnObj = value;
		}
		//else if (value instanceof java.lang.Integer){
		else if (relationClass != null) {
			//if (getRelationShipClass(columnName).getName().indexOf("idega") != -1){
			try {
				//returnObj = this.findByPrimaryInOtherClass(getRelationShipClass(columnName),((Integer)value).intValue());
				if (value != null) {
					IDOHome home = (IDOHome)IDOLookup.getHome(relationClass);
					returnObj = home.findByPrimaryKeyIDO(value);
				}
			} catch (Exception ex) {
				System.err.println("Exception in com.idega.data.GenericEntity.getColumnValue(String columnName): of type+ " + ex.getClass().getName() + " , Message = " + ex.getMessage());
				ex.printStackTrace(System.err);
			} finally {
			}
			//}
			//else{
			//}
		}
		//else{
		//	returnObj = value;
		//}
		//}
		else {
			returnObj = value;
		}
		if (returnObj == null) {
		} else {
		}
		return returnObj;
	}
	public String getStringColumnValue(String columnName) {
		if (getValue(columnName) != null) {
			if (this.getStorageClass(columnName).equals(java.lang.Boolean.class)) {
				if (((Boolean)getColumnValue(columnName)).booleanValue() == true) {
					return "Y";
				} else {
					return "N";
				}
			} else {
				return getValue(columnName).toString();
			}
		} else {
			return null;
		}
	}
	public char getCharColumnValue(String columnName) {
		String tempString = (String)getColumnValue(columnName);
		if(tempString!=null && tempString.length()>0){
			return tempString.charAt(0);
		}
		return ' ';
	}
	public float getFloatColumnValue(String columnName) {
		Float tempFloat = (Float)getColumnValue(columnName);
		if (tempFloat != null) {
			return tempFloat.floatValue();
		} else {
			return -1;
		}
	}
	public double getDoubleColumnValue(String columnName) {
		Double tempDouble = (Double)getColumnValue(columnName);
		if (tempDouble != null) {
			return tempDouble.doubleValue();
		} else {
			return -1;
		}
	}
	public Integer getIntegerColumnValue(String columnName) {
		return (Integer)getValue(columnName);
	}
	public int getIntColumnValue(String columnName) {
		Integer tempInt = (Integer)getValue(columnName);
		if (tempInt != null) {
			return tempInt.intValue();
		} else {
			return -1;
		}
	}
	public boolean getBooleanColumnValue(String columnName) {
		return getBooleanColumnValue(columnName, false);
	}
	public boolean getBooleanColumnValue(String columnName, boolean returnValueIfNull) {
		Object value = getValue(columnName);
		if (value != null) {
			//Boolean tempBool = (Boolean) getValue(columnName);
			//if (tempBool != null){
			//return tempBool.booleanValue();
			if (value instanceof Boolean) {
				return ((Boolean)value).booleanValue();
			} else if (value instanceof String) {
				String sValue = (String)value;
				if (sValue.equals(COLUMN_VALUE_TRUE)) {
					return true;
				} else if (sValue.equals(COLUMN_VALUE_FALSE)) {
					return false;
				}
			}
		}
		return returnValueIfNull;
	}
	public void setLongName(String columnName, String longName) {
		getColumn(columnName).setLongName(longName);
	}
	public String getLongName(String columnName) {
		return getColumn(columnName).getLongName();
	}
	public void setRelationShipType(String columnName, String type) {
		getColumn(columnName).setRelationShipType(type);
	}
	public String getRelationShipType(String columnName) {
		return getColumn(columnName).getRelationShipType();
	}
	/**
	 * @deprecated replaced with getStorageClass
	 */
	public int getStorageClassType(String columnName) {
		EntityAttribute attribute = getColumn(columnName);
		if (attribute != null) {
			return attribute.getStorageClassType();
		} else {
			return 0;
		}
	}
	/**
	 * @deprecated replaced with getStorageClass
	 */
	public String getStorageClassName(String columnName) {
		String theReturn = "";
		if (getColumn(columnName) != null) {
			theReturn = getColumn(columnName).getStorageClassName();
		}
		return theReturn;
	}
	public Class getStorageClass(String columnName) {
		Class theReturn = null;
		if (getColumn(columnName) != null) {
			theReturn = getColumn(columnName).getStorageClass();
		}
		return theReturn;
	}
	/**
	 * @deprecated replaced with setStorageClass
	 */
	public void setStorageClassName(String columnName, String className) {
		try {
			getColumn(columnName).setStorageClass(Class.forName(className));
		} catch (ClassNotFoundException e) {
			throw new RuntimeException("Exception in " + this.getClass().getName() + e.getMessage());
		}
	}
	public void setStorageClass(String columnName, Class javaClass) {
		getColumn(columnName).setStorageClass(javaClass);
	}
	/**
	 * @deprecated replaced with setStorageClass
	 */
	public void setStorageClassType(String columnName, int classType) {
		getColumn(columnName).setStorageClassType(classType);
	}
	public void setEditable(String columnName, boolean ifEditable) {
		getColumn(columnName).setEditable(ifEditable);
	}
	public boolean getIfEditable(String columnName) {
		return getColumn(columnName).getIfEditable();
	}
	public void setVisible(String columnName, boolean ifVisible) {
		getColumn(columnName).setVisible(ifVisible);
	}
	public boolean getIfVisible(String columnName) {
		return getColumn(columnName).getIfVisible();
	}
	/*
	public String getRelationShipClassName(String columnName){
		String theReturn = "";
		if (getColumn(columnName) != null){
		  theReturn = getColumn(columnName).getRelationShipClassName();
		}
	    return theReturn;
	}
	*/
	/**
	 * Returns null if the specified column does have a relationship Class
	 */
	public Class getRelationShipClass(String columnName) {
		EntityAttribute column = getColumn(columnName);
		if (column != null) {
			return column.getRelationShipClass();
		}
		return null;
	}
	public void setRelationShipClassName(String columnName, String className) {
		try {
			getColumn(columnName).setRelationShipClass(Class.forName(className));
		} catch (ClassNotFoundException e) {
			throw new RuntimeException("Exception in " + this.getClass().getName() + e.getMessage());
		}
	}
	public void setMaxLength(String columnName, int maxLength) {
		getColumn(columnName).setMaxLength(maxLength);
	}
	public int getMaxLength(String columnName) {
		return getColumn(columnName).getMaxLength();
	}
	public void setNullable(String columnName, boolean ifNullable) {
		getColumn(columnName).setNullable(ifNullable);
	}
	public boolean getIfNullable(String columnName) {
		return getColumn(columnName).getIfNullable();
	}
	public boolean getIfUnique(String columnName) {
		return getColumn(columnName).getIfUnique();
	}
	public void setUnique(String columnName, boolean ifUnique) {
		getColumn(columnName).setUnique(ifUnique);
	}
	public void setAsPrimaryKey(String columnName, boolean ifPrimaryKey) {
		getColumn(columnName).setAsPrimaryKey(ifPrimaryKey);
	}
	public boolean isPrimaryKey(String columnName) {
		return getColumn(columnName).isPrimaryKey();
	}
	/**
	 * Gets a databaseconnection identified by the datasourceName
	 */
	public Connection getConnection(String datasourceName) throws SQLException {
		return ConnectionBroker.getConnection(datasourceName);
	}
	/**
	 * Gets the default database connection
	 */
	public Connection getConnection() throws SQLException {
		return ConnectionBroker.getConnection(getDatasource());
	}
	/**
	 * Frees the connection used, must be done after using a databaseconnection
	 */
	public void freeConnection(String datasourceName, Connection connection) {
		ConnectionBroker.freeConnection(datasourceName, connection);
	}
	/**
	 * Frees the default connection used, must be done after using a databaseconnection
	 */
	public void freeConnection(Connection connection) {
		ConnectionBroker.freeConnection(getDatasource(), connection);
	}
	/**
	 * Sets the datasource to another datastore.<br>
	 * Can be used to switch the datasource when the entity has been fetched from another datasource (.e.g. when
	 * this instance is inserted into a new datastore with another datasource).
	 */
	public void setDatasource(String dataSource) {
		if (!dataSource.equals(this._dataSource)) {
			try {
				//Connect the blob fields if the datasource is changed
				//System.out.println("[setDataSource()] "+this.getClass().getName()+" EntityState="+this.getEntityState());
				if (getEntityState() == IDOLegacyEntity.STATE_IN_SYNCH_WITH_DATASTORE && this.hasLobColumn()) {
					BlobWrapper wrapper = this.getBlobColumnValue(this.getLobColumnName());
					BlobInputStream inStream = wrapper.getBlobInputStream();
					//inStream.setDataSource(this._dataSource);
					wrapper.setInputStreamForBlobWrite(inStream);
					setEntityState(IDOLegacyEntity.STATE_NEW_AND_NOT_IN_SYNCH_WITH_DATASTORE);
					//System.out.println(this.getClass().getName()+".setDatasource("+dataSource+"), connecting blob fields");
				}
			} catch (Exception e) {
				System.err.println("Exception in connecting blob fields for " + this.getClass().getName() + ", primary value=" + this.getPrimaryKeyValueSQLString());
				e.printStackTrace();
			}
		}
		this._dataSource = dataSource;
	}
	public String getDatasource() {
		return _dataSource;
	}
	/**
	 * @todo add:
	public String getPKColumnName(){
	  String entityName = getEntityName();
	  if (entityName.endsWith("_")){
		  return entityName+"id";
	  }
	  else{
		  return entityName+"_id";
	  }
	}
	*/
	public String getIDColumnName() {
		String entityName = getEntityName();
		if (entityName.endsWith("_")) {
			return entityName + "ID";
			//return entityName+"id";
		} else {
			return entityName + "_ID";
			//return entityName+"_id";
		}
	}
	public static String getLanguageIDColumnName() {
		return "language_id";
	}

	/**
	 * Gets a String array with all the columns defined in this entity bean.
	 * @see com.idega.data.IDOLegacyEntity#getColumnNames()
	 */
	public String[] getColumnNames() {
		String[] theReturn = getCachedColumnNames();

		if (theReturn == null) {
			Vector vector = new Vector();
			int i = 0;
			//for (Enumeration e = columns.keys(); e.hasMoreElements();i++){
			//for (Enumeration e = getAttributes().elements(); e.hasMoreElements(); i++)
			for (Iterator iter = getAttributes().iterator(); iter.hasNext(); i++) {
				EntityAttribute temp = (EntityAttribute)iter.next();
				//EntityAttribute temp = (EntityAttribute) e.nextElement();
				if (temp.getAttributeType().equals("column")) {
					//vector.addElement(temp.getColumnName().toLowerCase());
					vector.addElement(temp.getColumnName());
				}
			}
			if (vector != null) {
				vector.trimToSize();
				theReturn = (String[])vector.toArray(new String[0]);
				//return vector.toArray(new IDOLegacyEntity[0]);
			} else {
				theReturn = new String[0];
			}
			setCachedColumnNames(theReturn);
		}
		return theReturn;
	}

	private String[] getCachedColumnNames() {
		return ((GenericEntity)getIDOEntityStaticInstance())._cachedColumnNameList;
	}

	private void setCachedColumnNames(String[] columnNames) {
		((GenericEntity)getIDOEntityStaticInstance())._cachedColumnNameList = columnNames;
	}

	/**@todo this should not be done every time cache!!**/
	public String[] getVisibleColumnNames() {
		List theColumns = new Vector();
		//Vector theAttributes = getAttributes();
		//for (Enumeration e = getAttributes().elements(); e.hasMoreElements();)
		for (Iterator iter = getAttributes().iterator(); iter.hasNext();) {
			//String tempName = (String) ((EntityAttribute) e.nextElement()).getColumnName();
			String tempName = (String) ((EntityAttribute)iter.next()).getColumnName();
			if (getIfVisible(tempName)) {
				theColumns.add(tempName);
			}
		}
		return (String[])theColumns.toArray(new String[0]);
	}
	public String[] getEditableColumnNames() {
		Collection theColumns = new Vector();
		//Collection theAttributes = getAttributes();

		//for (Enumeration e = getAttributes().elements(); e.hasMoreElements();)
		for (Iterator iter = getAttributes().iterator(); iter.hasNext();) {
			//for (Enumeration e = columns.elements(); e.hasMoreElements();){
			//String tempName = (String) ((EntityAttribute) e.nextElement()).getColumnName();
			String tempName = (String) ((EntityAttribute)iter.next()).getColumnName();
			if (getIfEditable(tempName)) {
				theColumns.add(tempName);
			}
		}
		return (String[])theColumns.toArray(new String[0]);
	}
	public boolean isNull(String columnName) {
		/*if (columns.get(columnName) instanceof java.lang.String){
			String tempString = (String)columns.get(columnName);
			if (tempString.equals("idega_special_null")){
				return true;
			}
			else{
				return false;
			}
		}
		else{
			return false;
		}*/
		/*
				if (getColumnValue(columnName) == null){
					return true;
				}
				else{
					return false;
				}
		*/
		//if (_columns.get(columnName.toLowerCase())== null){
		Object o = _columns.get(columnName.toUpperCase());
		if (o == null) {
			return true;
		} else {
			return false;
		}
	}
	public boolean hasBeenSetNull(String columnName) {
		if (this.hasColumnBeenUpdated(columnName)) {
			return this.isNull(columnName);
		} else {
			return false;
		}
	}
	/*
	 * Returns the type of the underlying datastore - returns: "mysql", "interbase", "oracle", "unimplemented"
	 */
	/*
	public String getDataStoreType(Connection connection){
		if (dataStoreType == null){
			if (connection != null){
				if (connection.getClass().getName().indexOf("oracle") != -1 ){
					dataStoreType = "oracle";
				}
				else if (connection.getClass().getName().indexOf("interbase") != -1 ){
					dataStoreType = "interbase";
				}
				else if (connection.getClass().getName().indexOf("mysql") != -1 ){
					dataStoreType =  "mysql";
				}
	
				else{
					dataStoreType = "unimplemented";
				}
			}
			else{
				dataStoreType =  "";
			}
		}
		return dataStoreType;
	}*/
	/**
	**Override this function to set default values to columns if they have no set values
	**/
	public void setDefaultValues() {
		//default implementation does nothing
	}
	/**
	*Inserts this entity as a record into the datastore
	*/
	public void insert() throws SQLException {
		try {
			DatastoreInterface.getInstance(this).insert(this);
			if (isBeanCachingActive()) {
				IDOContainer.getInstance().getBeanCache(this.getInterfaceClass()).putCachedEntity(getPrimaryKey(), this);
			}
			flushQueryCache();
		} catch (Exception ex) {
			if (isDebugActive())
				ex.printStackTrace();

			try {
				this.closeBlobConnections();
			} catch (Exception e) {
			}
			if (ex instanceof SQLException) {
				//ex.printStackTrace();
				throw (SQLException)ex.fillInStackTrace();
			} else {
				//ex.printStackTrace();
				throw new SQLException("Exception rethrown: " + ex.getClass().getName() + " - " + ex.getMessage());
			}
		}
	}
	/**
	*Inserts/update/removes this entity's metadata into the datastore
	*/
	public void updateMetaData() throws SQLException {
		try {
			if (isIdColumnValueNotEmpty())
				DatastoreInterface.getInstance(this).crunchMetaData(this);
			else
				System.err.println("IDOLegacyEntity: updateMetaData() getID = -1 !");
		} catch (Exception ex) {
			if (ex instanceof SQLException) {
				ex.printStackTrace();
				throw (SQLException)ex.fillInStackTrace();
			} else {
				ex.printStackTrace();
			}
		}
	}
	/**
	*Inserts/update/removes this entity's metadata into the datastore
	*/
	public void insertMetaData() throws SQLException {
		updateMetaData();
	}
	/**
	*deletes all of this entity's metadata
	*/
	public void deleteMetaData() throws SQLException {
		try {
			DatastoreInterface.getInstance(this).deleteMetaData(this);
		} catch (Exception ex) {
			if (ex instanceof SQLException) {
				ex.printStackTrace();
				throw (SQLException)ex.fillInStackTrace();
			}
		}
	}
	/**
	*Inserts this entity as a record into the datastore
	*/
	public void insert(Connection c) throws SQLException {
		try {
			DatastoreInterface.getInstance(c).insert(this, c);
			flushQueryCache();
		} catch (Exception ex) {
			if (ex instanceof SQLException) {
				ex.printStackTrace();
				throw (SQLException)ex.fillInStackTrace();
			}
		}
	}
	/*
	public void insert()throws SQLException{
			EntityControl.insert(this);
		}*/
	/**
	*Updates the entity in the datastore
	*/
	public synchronized void update() throws SQLException {
		try {
			DatastoreInterface.getInstance(this).update(this);
			flushQueryCache();
		} catch (Exception ex) {
			if (isDebugActive())
				ex.printStackTrace();

			if (ex instanceof SQLException) {
				//ex.printStackTrace();
				throw (SQLException)ex.fillInStackTrace();
			}
		}
	}
	/**
	*Updates the entity in the datastore
	*/
	public void update(Connection c) throws SQLException {
		try {
			DatastoreInterface.getInstance(c).update(this, c);
			flushQueryCache();
			if (IDOContainer.getInstance().beanCachingActive(this.getInterfaceClass())) {
				IDOContainer.getInstance().getBeanCache(this.getInterfaceClass()).removeCachedEntity(this.getPrimaryKey());
			}
			this.empty();
		} catch (Exception ex) {
			if (ex instanceof SQLException) {
				//ex.printStackTrace();
				throw (SQLException)ex.fillInStackTrace();
			}
		}
	}
	public void delete() throws SQLException {
		try {
			DatastoreInterface.getInstance(this).delete(this);
			flushQueryCache();
			if (IDOContainer.getInstance().beanCachingActive(this.getInterfaceClass())) {
				IDOContainer.getInstance().getBeanCache(this.getInterfaceClass()).removeCachedEntity(this.getPrimaryKey());
			}
			this.empty();
		} catch (Exception ex) {
			if (ex instanceof SQLException) {
				ex.printStackTrace();
				throw (SQLException)ex.fillInStackTrace();
			}
		}
	}
	public void delete(Connection c) throws SQLException {
		try {
			DatastoreInterface.getInstance(c).delete(this, c);
			flushQueryCache();
		} catch (Exception ex) {
			if (ex instanceof SQLException) {
				ex.printStackTrace();
				throw (SQLException)ex.fillInStackTrace();
			}
		}
	}
	public void deleteMultiple(String columnName, String stringColumnValue) throws SQLException {
		Connection conn = null;
		Statement Stmt = null;
		try {
			conn = this.getConnection();
			Stmt = conn.createStatement();
			Stmt.executeUpdate("delete from " + this.getEntityName() + " where " + columnName + "='" + stringColumnValue + "'");
		} finally {
			if (Stmt != null) {
				Stmt.close();
			}
			if (conn != null) {
				this.freeConnection(conn);
			}
		}
	}
	public void deleteMultiple(String columnName1, String stringColumnValue1, String columnName2, String stringColumnValue2) throws SQLException {
		Connection conn = null;
		Statement Stmt = null;
		try {
			conn = this.getConnection();
			Stmt = conn.createStatement();
			Stmt.executeUpdate("delete from " + this.getEntityName() + " where " + columnName1 + "='" + stringColumnValue1 + "' and " + columnName2 + "='" + stringColumnValue2 + "'");
		} finally {
			if (Stmt != null) {
				Stmt.close();
			}
			if (conn != null) {
				this.freeConnection(conn);
			}
		}
	}
	/**
	*Deletes everything from the table of this entity
	**/
	public void clear() throws SQLException {
		Connection conn = null;
		Statement Stmt = null;
		try {
			conn = this.getConnection();
			Stmt = conn.createStatement();
			Stmt.executeUpdate("delete from " + this.getEntityName());
		} finally {
			if (Stmt != null) {
				Stmt.close();
			}
			if (conn != null) {
				this.freeConnection(conn);
			}
		}
		this.setEntityState(IDOLegacyEntity.STATE_DELETED);
	}
	/**
	**Function to populate a column through a string representation
	**/
	public void setStringColumn(String columnName, String columnValue) {
		int classType = getStorageClassType(columnName);
		if (classType == EntityAttribute.TYPE_JAVA_LANG_INTEGER) {
			if (columnValue != null) {
				setColumn(columnName, new Integer(columnValue));
			}
		} else if (classType == EntityAttribute.TYPE_JAVA_LANG_STRING) {
			if (columnValue != null) {
				setColumn(columnName, columnValue);
			}
		} else if (classType == EntityAttribute.TYPE_JAVA_LANG_BOOLEAN) {
			if (columnValue != null) {
				if (columnValue.equals("Y")) {
					setColumn(columnName, new Boolean(true));
				} else if (columnValue.equals("N")) {
					setColumn(columnName, new Boolean(false));
				} else {
					setColumn(columnName, new Boolean(false));
				}
			}
		} else if (classType == EntityAttribute.TYPE_JAVA_LANG_FLOAT) {
			if (columnValue != null) {
				setColumn(columnName, new Float(columnValue));
			}
		} else if (classType == EntityAttribute.TYPE_JAVA_LANG_DOUBLE) {
			if (columnValue != null) {
				setColumn(columnName, new Double(columnValue));
			}
		} else if (classType == EntityAttribute.TYPE_JAVA_SQL_TIMESTAMP) {
			if (columnValue != null) {
				setColumn(columnName, java.sql.Timestamp.valueOf(columnValue));
			}
		} else if (classType == EntityAttribute.TYPE_JAVA_SQL_DATE) {
			if (columnValue != null) {
				setColumn(columnName, java.sql.Date.valueOf(columnValue));
			}
		} else if (classType == EntityAttribute.TYPE_JAVA_UTIL_DATE) {
			if (columnValue != null) {
				setColumn(columnName, java.sql.Date.valueOf(columnValue));
			}
		} else if (classType == EntityAttribute.TYPE_JAVA_SQL_TIME) {
			if (columnValue != null) {
				setColumn(columnName, java.sql.Time.valueOf(columnValue));
			}
		} else if (classType == EntityAttribute.TYPE_COM_IDEGA_UTIL_GENDER) {
			if (columnValue != null) {
				setColumn(columnName, columnValue.toString());
			}
		}
	}
	public void fillColumn(String columnName, ResultSet RS) throws SQLException {
		DatastoreInterface.getInstance(this).fillColumn(this, columnName, RS);
		/*
		int classType = getStorageClassType(columnName);
		
		if (classType==EntityAttribute.TYPE_JAVA_LANG_INTEGER){
			//if (RS.getInt(columnName) != -1){
			int theInt = RS.getInt(columnName);
			boolean wasNull = RS.wasNull();
			if(!wasNull){
			    setColumn(columnName,new Integer(theInt));
			    //setColumn(columnName.toLowerCase(),new Integer(theInt));
			}
		
			//}
		}
		else if (classType==EntityAttribute.TYPE_JAVA_LANG_STRING){
			if (RS.getString(columnName) != null){
				setColumn(columnName,RS.getString(columnName));
			}
		
		}
		else if (classType==EntityAttribute.TYPE_JAVA_LANG_BOOLEAN){
			String theString = RS.getString(columnName);
			if (theString != null){
				if (theString.equals("Y")){
					setColumn(columnName,new Boolean(true));
				}
				else if (theString.equals("N")){
					setColumn(columnName,new Boolean(false));
				}
			}
		}
		else if (classType==EntityAttribute.TYPE_JAVA_LANG_FLOAT){
			float theFloat = RS.getFloat(columnName);
			boolean wasNull = RS.wasNull();
			if(!wasNull){
			    setColumn(columnName,new Float(theFloat));
			    //setColumn(columnName.toLowerCase(),new Float(theFloat));
			}
		
		}
		else if (classType==EntityAttribute.TYPE_JAVA_LANG_DOUBLE){
			double theDouble = RS.getFloat(columnName);
			boolean wasNull = RS.wasNull();
			if(!wasNull){
			    setColumn(columnName,new Double(theDouble));
			    //setColumn(columnName.toLowerCase(),new Double(theDouble));
			}
		
			double doble = RS.getDouble(columnName);
		}
		else if (classType==EntityAttribute.TYPE_JAVA_SQL_TIMESTAMP){
			if (RS.getTimestamp(columnName) != null){
				setColumn(columnName,RS.getTimestamp(columnName));
			}
		}
		else if (classType==EntityAttribute.TYPE_JAVA_SQL_DATE){
			if (RS.getDate(columnName) != null){
				setColumn(columnName,RS.getDate(columnName));
			}
		}
		else if (classType==EntityAttribute.TYPE_JAVA_SQL_TIME){
			java.sql.Date date = RS.getDate(columnName);
			if (date != null){
				setColumn(columnName,date);
		//setColumn(columnName.toLowerCase(),date);
			}
		}
		else if (classType==EntityAttribute.TYPE_COM_IDEGA_DATA_BLOBWRAPPER){
			//if (RS.getDate(columnName) != null){
			//	setColumn(columnName.toLowerCase(),RS.getTime(columnName));
			//}
			setColumn(columnName,getEmptyBlob(columnName));
			//setColumn(columnName.toLowerCase(),getEmptyBlob(columnName));
		
		}
		else if (classType==EntityAttribute.TYPE_COM_IDEGA_UTIL_GENDER){
			String gender = RS.getString(columnName);
			if (gender != null){
				setColumn(columnName,new Gender(gender));
		//setColumn(columnName.toLowerCase(),new Gender(gender));
		
			}
		}
		*/
	}
	public synchronized void ejbLoad() throws EJBException {
		try {
			if (this.getEntityState() != IDOLegacyEntity.STATE_IN_SYNCH_WITH_DATASTORE) {
				Object pk = this.getPrimaryKey();
				/*if(pk instanceof Integer){
				  findByPrimaryKey(((Integer)pk).intValue());
				}*/
				if (pk == null) {
					throw new EJBException("Cannot load entity " + this.getClass().getName() + " for primary key null");
				}
				ejbLoad(pk);
				setEntityState(IDOLegacyEntity.STATE_IN_SYNCH_WITH_DATASTORE);
			}
		} catch (SQLException e) {
			throw new EJBException(e.getMessage());
		}
	}
	/**
	 * To speed up loading when the ResultSet is already constructed
	 */
	public synchronized void preEjbLoad(ResultSet rs) throws EJBException {
		try {
			//Object pk = this.getPrimaryKey();
			if (rs != null) {
				this.loadFromResultSet(rs);
				setEntityState(IDOLegacyEntity.STATE_IN_SYNCH_WITH_DATASTORE);
			}
		} catch (Exception e) {
			throw new EJBException(e.getMessage());
		}
	}
	private void ejbLoad(Object pk) throws SQLException {
		Connection conn = null;
		Statement Stmt = null;
		try {
			conn = getConnection(getDatasource());
			Stmt = conn.createStatement();
			StringBuffer buffer = new StringBuffer();
			//buffer.append("select * from ");
			buffer.append("select ");
			//System.out.println("COLUMN NAMES : "+DatastoreInterface.getCommaDelimitedColumnNamesForSelect(this));/**@is this where it is supposed to be?**/
			DatastoreInterface dsi = DatastoreInterface.getInstance(this);
			buffer.append(dsi.getCommaDelimitedColumnNamesForSelect(this));
			buffer.append(" from "); //skips lob colums
			buffer.append(getEntityName());
			/*buffer.append(" where ");
			buffer.append(getIDColumnName());
			buffer.append("='");
			buffer.append(pk.toString());
			buffer.append("'");*/
			dsi.appendPrimaryKeyWhereClause(this,buffer);
			
			String sql = buffer.toString();
			ResultSet RS = Stmt.executeQuery(sql);
			//ResultSet RS = Stmt.executeQuery("select * from "+getTableName()+" where "+getIDColumnName()+"="+id);
			//eiki added null check
			if ((RS == null) || !RS.next())
				throw new SQLException("Record with Primary Key = '" + pk + "' not found");
			loadFromResultSet(RS);
			if (RS != null)
				RS.close();
		} finally {
			if (Stmt != null) {
				Stmt.close();
			}
			if (conn != null) {
				freeConnection(getDatasource(), conn);
			}
		}
		setEntityState(IDOLegacyEntity.STATE_IN_SYNCH_WITH_DATASTORE);
	}
	/**
	 * @ deprecated Replaced with ejbLoad(Object value);
	 * @param id
	 * @throws SQLException
	 */
	public void findByPrimaryKey(int id) throws SQLException {
		setPrimaryKey(id);
		Connection conn = null;
		Statement Stmt = null;
		try {
			conn = getConnection(getDatasource());
			Stmt = conn.createStatement();
			StringBuffer buffer = new StringBuffer();
			//buffer.append("select * from ");
			buffer.append("select ");
			//System.out.println("COLUMN NAMES : "+DatastoreInterface.getCommaDelimitedColumnNamesForSelect(this));/**@is this where it is supposed to be?**/
			buffer.append(DatastoreInterface.getInstance(this).getCommaDelimitedColumnNamesForSelect(this));
			buffer.append(" from "); //skips lob colums
			buffer.append(getEntityName());
			buffer.append(" where ");
			buffer.append(getIDColumnName());
			buffer.append("=");
			buffer.append(id);
			ResultSet RS = Stmt.executeQuery(buffer.toString());
			//ResultSet RS = Stmt.executeQuery("select * from "+getTableName()+" where "+getIDColumnName()+"="+id);
			//eiki added null check
			if ((RS == null) || !RS.next())
				throw new SQLException("Record with id=" + id + " not found");
			loadFromResultSet(RS);
			if (RS != null)
				RS.close();
		} finally {
			if (Stmt != null) {
				Stmt.close();
			}
			if (conn != null) {
				freeConnection(getDatasource(), conn);
			}
		}
		setEntityState(IDOLegacyEntity.STATE_IN_SYNCH_WITH_DATASTORE);
	}
	Object getPrimaryKeyFromResultSet(ResultSet rs) throws SQLException {
		Object theReturn = null;
		if (this.getPrimaryKeyClass() == Integer.class) {
			theReturn = new Integer(rs.getInt(this.getIDColumnName()));
		}
		if (this.getPrimaryKeyClass() == String.class) {
			theReturn = rs.getString(this.getIDColumnName());
		} else {
			/**
			 * @todo implement
			 */
		}
		if (rs.wasNull()) {
			return null;
		}
		return theReturn;
	}
	private void loadFromResultSet(ResultSet RS) {
		String[] columnNames = getColumnNames();
		for (int i = 0; i < columnNames.length; i++) {
			try {
				//if (RS.getString(columnNames[i]) != null){
				fillColumn(columnNames[i], RS);
				//}
			} catch (Exception ex) {
				/*//NOCATH
				  try{
				  //if (RS.getString(columnNames[i].toUpperCase()) != null){
					  fillColumn(columnNames[i],RS);
				  //}
				  }
				  catch(SQLException exe){
				    try{
					    //if (RS.getString(columnNames[i].toLowerCase()) != null){
				    fillColumn(columnNames[i],RS);
				    //}
				    }
				    catch(SQLException exep){
				
				     System.err.println("Exception in "+this.getClass().getName()+" findByPrimaryKey, RS.getString( "+columnNames[i]+" ) not found: "+exep.getMessage());
					    //exep.printStackTrace(System.err);
				    }
				  }*/
				System.err.println("Exception in " + this.getClass().getName() + " findByPrimaryKey, RS.getString( " + columnNames[i] + " ) not found: " + ex.getMessage());
				if (!(ex instanceof NullPointerException))
					ex.printStackTrace(System.err);
			}
		}
	}
	public String getNameOfMiddleTable(IDOEntity entity1, IDOEntity entity2) {
		return EntityControl.getNameOfMiddleTable(entity1, entity2);
	}
	/**
	 * @deprecated replaced with idoGetRelated
	 */
	public IDOLegacyEntity[] findRelated(IDOLegacyEntity entity) throws SQLException {
		return findRelated(entity, "", "");
	}
	/**
	 * @deprecated replaced with idoGetRelated
	 */
	public int[] findRelatedIDs(IDOLegacyEntity entity) throws SQLException {
		return findRelatedIDs(entity, "", "");
	}
	private String getFindRelatedSQLQuery(IDOEntity entity, String entityColumnName, String entityColumnValue) {
		String tableToSelectFrom = getNameOfMiddleTable(entity, this);
		String primaryValue = getPrimaryKeyValueSQLString(); //eiki added for string primary key support
		String entityIDColumnName = null;

		try {
			entityIDColumnName = entity.getEntityDefinition().getPrimaryKeyDefinition().getField().getSQLFieldName();
		} catch (IDOCompositPrimaryKeyException e) {
			e.printStackTrace();
		}

		StringBuffer buffer = new StringBuffer();
		buffer.append("select e.* from ");
		buffer.append(tableToSelectFrom + " middle, " + entity.getEntityDefinition().getSQLTableName() + " e");
		buffer.append(" where ");

		if (isColumnValueNotEmpty(primaryValue)) {
			buffer.append("middle." + this.getIDColumnName());
			buffer.append("=");
			buffer.append(primaryValue);
			buffer.append(" and ");
		}

		buffer.append(" middle." + entityIDColumnName);
		buffer.append("=");
		buffer.append("e." + entityIDColumnName);

		primaryValue = this.getKeyValueSQLString(entity.getPrimaryKey());

		if (isColumnValueNotEmpty(primaryValue)) {
			buffer.append(" and ");
			buffer.append("middle." + entityIDColumnName);
			buffer.append("=");
			buffer.append(primaryValue);
		}

		if (entityColumnName != null)
			if (!entityColumnName.equals("")) {
				buffer.append(" and ");
				buffer.append("e." + entityColumnName);
				if (entityColumnValue != null) {
					buffer.append(" = ");
					buffer.append("'" + entityColumnValue + "'");
				} else {
					buffer.append(" is null");
				}
			}
		String SQLString = buffer.toString();
		return SQLString;
	}

	private String getFindReverseRelatedSQLQuery(IDOEntity entity, String entityColumnName, String entityColumnValue) {
		String tableToSelectFrom = getNameOfMiddleTable(entity, this);
		String primaryValue = getPrimaryKeyValueSQLString();

		StringBuffer buffer = new StringBuffer();
		buffer.append("select e.* from ");
		buffer.append(tableToSelectFrom + " middle, " + this.getEntityName() + " e");
		buffer.append(" where ");

		if (isColumnValueNotEmpty(primaryValue)) {
			buffer.append("middle." + this.getIDColumnName());
			buffer.append("=");
			buffer.append(primaryValue);
			buffer.append(" and ");
		}

		buffer.append(" middle." + this.getIDColumnName());
		buffer.append("=");
		buffer.append("e." + this.getIDColumnName());

		primaryValue = this.getKeyValueSQLString(entity.getPrimaryKey());

		try {
			if (isColumnValueNotEmpty(primaryValue)) {
				buffer.append(" and ");
				buffer.append("middle." + entity.getEntityDefinition().getPrimaryKeyDefinition().getField().getSQLFieldName());
				buffer.append("=");
				buffer.append(primaryValue);
			}
		} catch (IDOCompositPrimaryKeyException e) {
			e.printStackTrace();
			return null;
		}

		if (entityColumnName != null)
			if (!entityColumnName.equals("")) {
				buffer.append(" and ");
				buffer.append("e." + entityColumnName);
				if (entityColumnValue != null) {
					buffer.append(" = ");
					buffer.append("'" + entityColumnValue + "'");
				} else {
					buffer.append(" is null");
				}
			}
		String SQLString = buffer.toString();
		return SQLString;
	}

	/**
	 * @deprecated replaced with idoGetRelated()
	 */
	public IDOLegacyEntity[] findRelated(IDOLegacyEntity entity, String entityColumnName, String entityColumnValue) throws SQLException {

		String SQLString = this.getFindRelatedSQLQuery(entity, entityColumnName, entityColumnValue);
		return findRelated(entity, SQLString);
	}
	/**
	 * @deprecated
	 */
	public IDOLegacyEntity[] findReverseRelated(IDOLegacyEntity entity) throws SQLException {
		return findRelated(entity);
	}
	/**
	 * @deprecated replaced with idoGetRelated
	 */
	protected IDOLegacyEntity[] findRelated(IDOLegacyEntity entity, String SQLString) throws SQLException {
		Connection conn = null;
		Statement Stmt = null;
		Vector vector = new Vector();
		/*String tableToSelectFrom = "";
		if (entity.getEntityName().endsWith("_"))
		{
			tableToSelectFrom = entity.getEntityName() + this.getEntityName();
		}
		else
		{
			tableToSelectFrom = entity.getEntityName() + "_" + this.getEntityName();
		}*/

		try {
			conn = getConnection(getDatasource());
			Stmt = conn.createStatement();
			ResultSet RS = Stmt.executeQuery(SQLString);
			while (RS.next()) {
				IDOLegacyEntity tempobj = null;
				try {
					Class relatedClass = entity.getClass();
					tempobj = this.findByPrimaryInOtherClass(relatedClass, RS.getInt(entity.getIDColumnName()));
				} catch (Exception ex) {
					System.err.println("There was an error in com.idega.data.GenericEntity.findRelated(IDOLegacyEntity entity,String SQLString): " + ex.getMessage());
				}
				vector.addElement(tempobj);
			}
			RS.close();
		} finally {
			if (Stmt != null) {
				Stmt.close();
			}
			if (conn != null) {
				freeConnection(getDatasource(), conn);
			}
		}
		if (vector != null) {
			vector.trimToSize();
			return (IDOLegacyEntity[])vector.toArray((Object[])java.lang.reflect.Array.newInstance(entity.getClass(), 0));
			//return vector.toArray(new IDOLegacyEntity[0]);
		} else {
			return null;
		}
	}
	/**
	 * @deprecated replaced with idoGetRelatedPKs
	 */
	public int[] findRelatedIDs(IDOLegacyEntity entity, String entityColumnName, String entityColumnValue) throws SQLException {
		String tableToSelectFrom = getNameOfMiddleTable(entity, this);
		StringBuffer buffer = new StringBuffer();
		buffer.append("select e.* from ");
		buffer.append(tableToSelectFrom + " middle, " + entity.getEntityName() + " e");
		buffer.append(" where ");
		buffer.append("middle." + this.getIDColumnName());
		buffer.append("=");
		//buffer.append(this.getID());
		buffer.append(getPrimaryKeyValueSQLString());
		buffer.append(" and ");
		buffer.append("middle." + entity.getIDColumnName());
		buffer.append("=");
		buffer.append("e." + entity.getIDColumnName());

		///if (entity.getID() != -1)
		if (isColumnValueNotEmpty(getKeyValueSQLString(entity.getPrimaryKeyValue()))) {
			buffer.append(" and ");
			buffer.append("middle." + entity.getIDColumnName());
			buffer.append("=");
			//buffer.append(entity.getID());
			buffer.append(getKeyValueSQLString(entity.getPrimaryKeyValue()));
		}

		if (entityColumnName != null)
			if (!entityColumnName.equals("")) {
				buffer.append(" and ");
				buffer.append("e." + entityColumnName);
				if (entityColumnValue != null) {
					buffer.append(" = ");
					buffer.append("'" + entityColumnValue + "'");
				} else {
					buffer.append(" is null");
				}
			}
		String SQLString = buffer.toString();
		return findRelatedIDs(entity, SQLString);
	}
	/**
	 * @deprecated replaced with idoGetRelatedPKs
	 */
	protected int[] findRelatedIDs(IDOLegacyEntity entity, String SQLString) throws SQLException {
		Connection conn = null;
		Statement Stmt = null;
		int[] toReturn = null;
		int length;
		Vector vector = new Vector();
		/*String tableToSelectFrom = "";
		if (entity.getEntityName().endsWith("_"))
		{
			tableToSelectFrom = entity.getEntityName() + this.getEntityName();
		}
		else
		{
			tableToSelectFrom = entity.getEntityName() + "_" + this.getEntityName();
		}*/
		try {
			conn = getConnection(getDatasource());
			Stmt = conn.createStatement();
			ResultSet RS = Stmt.executeQuery(SQLString);
			length = 0;
			while (RS.next()) {
				try {
					vector.addElement(RS.getObject(entity.getIDColumnName()));
					length++;
				} catch (Exception ex) {
					System.err.println("There was an error in com.idega.data.GenericEntity.findRelatedIDs(IDOLegacyEntity entity,String SQLString): " + ex.getMessage());
				}
			}
			RS.close();
		} finally {
			if (Stmt != null) {
				Stmt.close();
			}
			if (conn != null) {
				freeConnection(getDatasource(), conn);
			}
		}
		if (length > 0) {
			toReturn = new int[length];
			Iterator iter = vector.iterator();
			int index = 0;
			while (iter.hasNext()) {
				Integer item = (Integer)iter.next();
				toReturn[index++] = item.intValue();
			}
		} else {
			toReturn = new int[0];
		}
		return toReturn;
	}
	/**
	*Finds all instances of the current object in the otherEntity
	**/
	public IDOLegacyEntity[] findAssociated(IDOLegacyEntity otherEntity) throws SQLException {
		return otherEntity.findAll("select * from " + otherEntity.getEntityName() + " where " + this.getIDColumnName() + "= " + getPrimaryKeyValueSQLString());
	}
	/**
	 * @deprecated
	 */
	public IDOLegacyEntity[] findAssociatedOrdered(IDOLegacyEntity otherEntity, String column_name) throws SQLException {
		return otherEntity.findAll("select * from " + otherEntity.getEntityName() + " where " + this.getIDColumnName() + "= " + getPrimaryKeyValueSQLString() + " order by " + column_name);
	}
	/**
	 * @deprecated
	 */
	public IDOLegacyEntity[] findAll() throws SQLException {
		return findAll("select * from " + getEntityName());
	}
	/**
	 * @deprecated
	 */
	public IDOLegacyEntity[] findAllOrdered(String orderByColumnName) throws SQLException {
		return findAll("select * from " + getEntityName() + " order by " + orderByColumnName);
	}
	/**
	 * @deprecated
	 */
	public IDOLegacyEntity[] findAllByColumnOrdered(String columnName, String toFind, String orderByColumnName, String condition) throws SQLException {
		return findAll("select * from " + getEntityName() + " where " + columnName + " " + condition + " '" + toFind + "' order by " + orderByColumnName);
	}
	/**
	 * @deprecated
	 */
	public IDOLegacyEntity[] findAllByColumnOrdered(String columnName, String toFind, String orderByColumnName) throws SQLException {
		return findAll("select * from " + getEntityName() + " where " + columnName + " like '" + toFind + "' order by " + orderByColumnName);
	}
	/**
	 * @deprecated
	 */
	public IDOLegacyEntity[] findAllByColumnOrdered(String columnName1, String toFind1, String columnName2, String toFind2, String orderByColumnName, String condition1, String condition2) throws SQLException {
		return findAll("select * from " + getEntityName() + " where " + columnName1 + " " + condition1 + " '" + toFind1 + "' and " + columnName2 + " " + condition2 + " '" + toFind2 + "' order by " + orderByColumnName);
	}
	/**
	 * @deprecated
	 */
	public IDOLegacyEntity[] findAllByColumnOrdered(String columnName1, String toFind1, String columnName2, String toFind2, String orderByColumnName) throws SQLException {
		return findAll("select * from " + getEntityName() + " where " + columnName1 + " like '" + toFind1 + "' and " + columnName2 + " like '" + toFind2 + "' order by " + orderByColumnName);
	}
	/**
	 * @deprecated
	 */
	public IDOLegacyEntity[] findAllByColumnDescendingOrdered(String columnName, String toFind, String orderByColumnName) throws SQLException {
		return findAll("select * from " + getEntityName() + " where " + columnName + " like '" + toFind + "' order by " + orderByColumnName + " desc");
	}
	/**
	 * @deprecated
	 */
	public IDOLegacyEntity[] findAllByColumnDescendingOrdered(String columnName1, String toFind1, String columnName2, String toFind2, String orderByColumnName) throws SQLException {
		return findAll("select * from " + getEntityName() + " where " + columnName1 + " like '" + toFind1 + "' and " + columnName2 + " like '" + toFind2 + "' order by " + orderByColumnName + " desc");
	}
	/**
	 * @deprecated
	 */
	public IDOLegacyEntity[] findAllDescendingOrdered(String orderByColumnName) throws SQLException {
		return findAll("select * from " + getEntityName() + " order by " + orderByColumnName + " desc");
	}
	/**
	 * @deprecated
	 */
	public IDOLegacyEntity[] findAllByColumn(String columnName, String toFind, String condition) throws SQLException {
		return findAll("select * from " + getEntityName() + " where " + columnName + " " + condition + " '" + toFind + "'");
	}
	/**
	 * @deprecated
	 */
	public IDOLegacyEntity[] findAllByColumn(String columnName1, String toFind1, char condition1, String columnName2, String toFind2, char condition2) throws SQLException {
		return findAll("select * from " + getEntityName() + " where " + columnName1 + " " + String.valueOf(condition1) + " '" + toFind1 + "' and " + columnName2 + " " + String.valueOf(condition2) + " '" + toFind2 + "'");
	}
	/**
	 * @deprecated
	 */
	public IDOLegacyEntity[] findAllByColumn(String columnName, String toFind) throws SQLException {
		return findAll("select * from " + getEntityName() + " where " + columnName + " like '" + toFind + "'");
	}
	/**
	 * @deprecated
	 */
	public IDOLegacyEntity[] findAllByColumn(String columnName, int toFind) throws SQLException {
		return findAllByColumn(columnName, Integer.toString(toFind));
	}
	/**
	 * @deprecated
	 */
	public IDOLegacyEntity[] findAllByColumn(String columnName1, String toFind1, String columnName2, String toFind2, String columnName3, String toFind3) throws SQLException {
		return findAll("select * from " + getEntityName() + " where " + columnName1 + " like '" + toFind1 + "' and " + columnName2 + " like '" + toFind2 + "' and " + columnName3 + " like '" + toFind3 + "'");
	}
	/**
	 * @deprecated
	 */
	public IDOLegacyEntity[] findAllByColumn(String columnName1, String toFind1, String columnName2, String toFind2) throws SQLException {
		return findAll("select * from " + getEntityName() + " where " + columnName1 + " like '" + toFind1 + "' and " + columnName2 + " like '" + toFind2 + "'");
	}
	/**
	 * @deprecated
	 */
	public int getNumberOfRecords(String columnName, String columnValue) throws SQLException {
		return getNumberOfRecords("select count(*) from " + getEntityName() + " where " + columnName + " like '" + columnValue + "'");
	}
	public int getNumberOfRecords(String columnName, int columnValue) throws SQLException {
		return getNumberOfRecords("select count(*) from " + getEntityName() + " where " + columnName + " = " + columnValue);
	}
	public int getNumberOfRecordsRelated(IDOLegacyEntity entity) throws SQLException {
		String tableToSelectFrom = getNameOfMiddleTable(entity, this);
		String SQLString = "select count(*) from " + tableToSelectFrom + " where " + this.getIDColumnName() + "=" + getPrimaryKeyValueSQLString();
		//System.out.println(SQLString);
		return getNumberOfRecords(SQLString);
	}
	public int getNumberOfRecordsReverseRelated(IDOLegacyEntity entity) throws SQLException {
		String tableToSelectFrom = getNameOfMiddleTable(this, entity);
		String SQLString = "select count(*) from " + tableToSelectFrom + " where " + this.getIDColumnName() + "=" + getPrimaryKeyValueSQLString();
		//System.out.println(SQLString);
		return getNumberOfRecords(SQLString);
	}
	public int getNumberOfRecords() throws SQLException {
		return getNumberOfRecords("select count(*) from " + getEntityName());
	}
	public int getNumberOfRecords(String CountSQLString) throws SQLException {
		return getIntTableValue(CountSQLString);
	}
	public int getNumberOfRecords(String columnName, String Operator, String columnValue) throws SQLException {
		return getNumberOfRecords("select count(*) from " + getEntityName() + " where " + columnName + " " + Operator + " " + columnValue);
	}
	public int getMaxColumnValue(String columnName) throws SQLException {
		return getIntTableValue("select max(" + columnName + ") from " + getEntityName());
	}
	public int getMaxColumnValue(String columnToGetMaxFrom, String columnCondition, String columnConditionValue) throws SQLException {
		return getIntTableValue("select max(" + columnToGetMaxFrom + ") from " + getEntityName() + " where " + columnCondition + " = '" + columnConditionValue + "'");
	}
	public int getIntTableValue(String CountSQLString) throws SQLException {
		Connection conn = null;
		Statement stmt = null;
		ResultSet rs = null;
		int recordCount = -1;
		try {
			conn = getConnection(this.getDatasource());
			stmt = conn.createStatement();
			rs = stmt.executeQuery(CountSQLString);
			if (rs.next())
				recordCount = rs.getInt(1);
			rs.close();
			//System.out.println(SQLString+"\n");
		} catch (SQLException e) {
			throw new SQLException("There was an error in com.idega.data.GenericEntity.getNumberOfRecords \n" + e.getMessage());
		} catch (Exception e) {
			System.err.println("There was an error in com.idega.data.GenericEntity.getNumberOfRecords " + e.getMessage());
		} finally {
			if (stmt != null) {
				stmt.close();
			}
			if (conn != null) {
				freeConnection(getDatasource(), conn);
			}
		}
		return recordCount;
	}
	/**
	 * @deprecated
	 */
	public IDOLegacyEntity[] findAll(String SQLString) throws SQLException {
		//System.out.println(SQLString);
		return findAll(SQLString, -1);
	}
	/**
	 * @deprecated
	 */
	public IDOLegacyEntity[] findAll(String SQLString, int returningNumberOfRecords) throws SQLException {
		//System.err.println("com.idega.data.GenericEntity.findAll(\""+SQLString+"\");");
		/*
			Connection conn= null;
			Statement Stmt= null;
			ResultSetMetaData metaData;
			Vector vector = new Vector();
			boolean check=true;
			//Vector theIDs = new Vector();
			try{
				conn = getConnection(getDatasource());
				Stmt = conn.createStatement();
				ResultSet RS = Stmt.executeQuery(SQLString);
				metaData = RS.getMetaData();
				int count = 1;
				while (RS.next() && check){
				  count++;
				  if(returningNumberOfRecords!=-1){
				    if(count>returningNumberOfRecords){
				      check=false;
				    }
				  }
		
					IDOLegacyEntity tempobj=null;
					try{
						tempobj = (IDOLegacyEntity)Class.forName(this.getClass().getName()).newInstance();
					}
					catch(Exception ex){
						System.err.println("There was an error in com.idega.data.GenericEntity.findAll "+ex.getMessage());
						ex.printStackTrace(System.err);
					}
					if(tempobj != null){
						for (int i = 1; i <= metaData.getColumnCount(); i++){
		
		
							if ( RS.getObject(metaData.getColumnName(i)) != null){
		
								//System.out.println("ColumName "+i+": "+metaData.getColumnName(i));
								tempobj.fillColumn(metaData.getColumnName(i),RS);
							}
						}
		
					}
					vector.addElement(tempobj);
		
				}
				RS.close();
		
			}
			finally{
				if(Stmt != null){
					Stmt.close();
				}
				if (conn != null){
					freeConnection(getDatasource(),conn);
				}
			}
			/*
			for (Enumeration enum = theIDs.elements();enum.hasMoreElements();){
				Integer tempInt = (Integer) enum.nextElement();
				vector.addElement(new IDOLegacyEntity(tempInt.intValue()));
			}*/
		List list = EntityFinder.findAll((IDOLegacyEntity)this, SQLString, returningNumberOfRecords);
		if (list != null) {
			return (IDOLegacyEntity[])list.toArray((Object[])java.lang.reflect.Array.newInstance(this.getClass(), 0));
			//return vector.toArray(new IDOLegacyEntity[0]);
		} else {
			//Provided for backwards compatability where there was almost never returned null if
			//there was nothing found
			return (IDOLegacyEntity[])java.lang.reflect.Array.newInstance(this.getClass(), 0);
		}
	}
	/**
	 * @deprecated Replaced with idoAddTo
	 */
	public void addTo(IDOLegacyEntity entityToAddTo) throws SQLException {
		Connection conn = null;
		Statement Stmt = null;
		try {
			conn = getConnection(getDatasource());
			Stmt = conn.createStatement();
			//String sql = "insert into "+getNameOfMiddleTable(entityToAddTo,this)+"("+getIDColumnName()+","+entityToAddTo.getIDColumnName()+") values("+getID()+","+entityToAddTo.getID()+")";
			String sql = null;
			//try
			//{
			sql = "insert into " + getNameOfMiddleTable(entityToAddTo, this) + "(" + getIDColumnName() + "," + entityToAddTo.getIDColumnName() + ") values(" + getPrimaryKeyValueSQLString() + "," + getKeyValueSQLString(entityToAddTo.getPrimaryKeyValue()) + ")";
			/*}
			catch (RemoteException rme)
			{
				throw new SQLException("RemoteException in addTo, message: " + rme.getMessage());
			}*/

			//debug("statement: "+sql);

			Stmt.executeUpdate(sql);
		} finally {
			if (Stmt != null) {
				Stmt.close();
			}
			if (conn != null) {
				freeConnection(getDatasource(), conn);
			}
		}
	}
	/**
	* Default move behavior with a tree relationship
	 */
	public void moveChildrenToCurrent(IDOLegacyEntity entityFrom, String entityFromColumName) throws SQLException {
		Connection conn = null;
		Statement Stmt = null;
		try {
			conn = getConnection(getDatasource());
			Stmt = conn.createStatement();
			String sql = "update " + getNameOfMiddleTable(entityFrom, this) + " set " + getIDColumnName() + " = " + getPrimaryKeyValueSQLString() + " where " + getIDColumnName() + " = " + getKeyValueSQLString(entityFrom.getPrimaryKeyValue());
			Stmt.executeUpdate(sql);
		} finally {
			if (Stmt != null) {
				Stmt.close();
			}
			if (conn != null) {
				freeConnection(getDatasource(), conn);
			}
		}
	}
	/**
	 * Default relationship adding behavior with a many-to-many relationship
	 */
	public void addTo(IDOLegacyEntity entityToAddTo, String entityToAddToColumName) throws SQLException {
		Connection conn = null;
		Statement Stmt = null;
		try {
			conn = getConnection(getDatasource());
			Stmt = conn.createStatement();
			String sql = "insert into " + getNameOfMiddleTable(entityToAddTo, this) + "(" + getIDColumnName() + "," + entityToAddToColumName + ") values(" + getPrimaryKeyValueSQLString() + "," + getKeyValueSQLString(entityToAddTo.getPrimaryKeyValue()) + ")";
			Stmt.executeUpdate(sql);
		} finally {
			if (Stmt != null) {
				Stmt.close();
			}
			if (conn != null) {
				freeConnection(getDatasource(), conn);
			}
		}
	}
	public void addToTree(IDOLegacyEntity entityToAddTo, String entityToAddToColumName, String middleTableName) throws SQLException {
		Connection conn = null;
		Statement Stmt = null;
		try {
			conn = getConnection(getDatasource());
			Stmt = conn.createStatement();
			String sql = "insert into " + middleTableName + "(" + getIDColumnName() + "," + entityToAddToColumName + ") values(" + getPrimaryKeyValueSQLString() + "," + getKeyValueSQLString(entityToAddTo.getPrimaryKeyValue()) + ")";
			if (isDebugActive())
				System.out.println(sql);
			Stmt.executeUpdate(sql);
		} finally {
			if (Stmt != null) {
				Stmt.close();
			}
			if (conn != null) {
				freeConnection(getDatasource(), conn);
			}
		}
	}
	/**
	 * Default delete behavior with a tree relationship
	 */
	public void removeFrom(IDOLegacyEntity entityToDelete, String entityToDeleteColumName) throws SQLException {
		Connection conn = null;
		Statement Stmt = null;
		try {
			conn = getConnection(getDatasource());
			Stmt = conn.createStatement();
			String sql = "delete from " + getNameOfMiddleTable(entityToDelete, this) + " where " + entityToDeleteColumName + " = " + getKeyValueSQLString(entityToDelete.getPrimaryKeyValue());
			Stmt.executeUpdate(sql);
		} finally {
			if (Stmt != null) {
				Stmt.close();
			}
			if (conn != null) {
				freeConnection(getDatasource(), conn);
			}
		}
	}
	/**
	**Default insert behavior with a many-to-many relationship and EntityBulkUpdater
	**/
	public void addTo(IDOLegacyEntity entityToAddTo, Connection conn) throws SQLException {
		Statement Stmt = null;
		try {
			Stmt = conn.createStatement();
			Stmt.executeUpdate("insert into " + getNameOfMiddleTable(entityToAddTo, this) + "(" + getIDColumnName() + "," + entityToAddTo.getEntityDefinition().getPrimaryKeyDefinition().getField().getSQLFieldName() + ") values(" + getPrimaryKeyValueSQLString() + "," + getKeyValueSQLString(entityToAddTo.getPrimaryKeyValue()) + ")");
		} catch (IDOCompositPrimaryKeyException e) {
			e.printStackTrace();
		} finally {
			if (Stmt != null) {
				Stmt.close();
			}
		}
	}
	/**
	* Attention: Beta implementation
	*/
	public void addTo(Class entityToAddTo, int id) throws SQLException {
		Connection conn = null;
		Statement Stmt = null;
		try {
			conn = getConnection(getDatasource());
			Stmt = conn.createStatement();
			Stmt.executeUpdate("insert into " + getNameOfMiddleTable((IDOEntity)com.idega.data.GenericEntity.getStaticInstanceIDO(entityToAddTo), this) + "(" + getIDColumnName() + "," + (com.idega.data.GenericEntity.getStaticInstanceIDO(entityToAddTo)).getEntityDefinition().getPrimaryKeyDefinition().getField().getSQLFieldName() + ") values(" + getPrimaryKeyValueSQLString() + "," + id + ")");
		} catch (IDOCompositPrimaryKeyException e) {
			e.printStackTrace();
			throw new SQLException(e.getMessage());
		} finally {
			if (Stmt != null) {
				Stmt.close();
			}
			if (conn != null) {
				freeConnection(getDatasource(), conn);
			}
		}
	}
	/**
	* Attention: Beta implementation
	*/
	public void addTo(Class entityToAddTo, int[] ids) throws SQLException {
		Connection conn = null;
		Statement Stmt = null;
		try {
			conn = getConnection(getDatasource());
			Stmt = conn.createStatement();
			String middleTable = getNameOfMiddleTable(getStaticInstanceIDO(entityToAddTo), this);
			String columnName = (getStaticInstance(entityToAddTo)).getEntityDefinition().getPrimaryKeyDefinition().getField().getSQLFieldName();
			if (ids != null) {
				for (int i = 0; i < ids.length; i++) {
					try {
						Stmt.executeUpdate("insert into " + middleTable + "(" + getIDColumnName() + "," + columnName + ") values(" + getPrimaryKeyValueSQLString() + "," + ids[i] + ")");
					} finally {
					}
				}
			}
		} catch (IDOCompositPrimaryKeyException e) {
			e.printStackTrace();
			throw new SQLException(e.getMessage());
		} finally {
			if (Stmt != null) {
				Stmt.close();
			}
			if (conn != null) {
				freeConnection(getDatasource(), conn);
			}
		}
	}
	public void addTo(IDOLegacyEntity entityToAddTo, String extraColumnName, String extraColumnValue) throws SQLException {
		Connection conn = null;
		Statement Stmt = null;
		try {
			conn = getConnection(getDatasource());
			Stmt = conn.createStatement();
			Stmt.executeUpdate("insert into " + getNameOfMiddleTable(entityToAddTo, this) + "(" + getIDColumnName() + "," + entityToAddTo.getIDColumnName() + "," + extraColumnName + ") values(" + getPrimaryKeyValueSQLString() + "," + getKeyValueSQLString(entityToAddTo.getPrimaryKeyValue()) + ",'" + extraColumnValue + "')");
		} finally {
			if (Stmt != null) {
				Stmt.close();
			}
			if (conn != null) {
				freeConnection(getDatasource(), conn);
			}
		}
	}
	public void addTo(IDOLegacyEntity entityToAddTo, String extraColumnName, String extraColumnValue, String extraColumnName1, String extraColumnValue1) throws SQLException {
		Connection conn = null;
		Statement Stmt = null;
		try {
			conn = getConnection(getDatasource());
			Stmt = conn.createStatement();
			Stmt.executeUpdate("insert into " + getNameOfMiddleTable(entityToAddTo, this) + "(" + getIDColumnName() + "," + entityToAddTo.getIDColumnName() + "," + extraColumnName + "," + extraColumnName1 + ") values(" + getPrimaryKeyValueSQLString() + "," + getKeyValueSQLString(entityToAddTo.getPrimaryKeyValue()) + ",'" + extraColumnValue + "','" + extraColumnValue1 + "')");
		} finally {
			if (Stmt != null) {
				Stmt.close();
			}
			if (conn != null) {
				freeConnection(getDatasource(), conn);
			}
		}
	}
	public void addTo(IDOLegacyEntity entityToAddTo, String extraColumnName, String extraColumnValue, String extraColumnName1, String extraColumnValue1, String extraColumnName2, String extraColumnValue2) throws SQLException {
		Connection conn = null;
		Statement Stmt = null;
		try {
			conn = getConnection(getDatasource());
			Stmt = conn.createStatement();
			Stmt.executeUpdate("insert into " + getNameOfMiddleTable(entityToAddTo, this) + "(" + getIDColumnName() + "," + entityToAddTo.getIDColumnName() + "," + extraColumnName + "," + extraColumnName1 + "," + extraColumnName2 + ") values(" + getPrimaryKeyValueSQLString() + "," + getKeyValueSQLString(entityToAddTo.getPrimaryKeyValue()) + ",'" + extraColumnValue + "','" + extraColumnValue1 + "','" + extraColumnValue2 + "')");
		} finally {
			if (Stmt != null) {
				Stmt.close();
			}
			if (conn != null) {
				freeConnection(getDatasource(), conn);
			}
		}
	}

	/**
	 * @deprecated Replaced with idoRemoveFrom
	 */
	public void removeFrom(IDOLegacyEntity entityToRemoveFrom) throws SQLException {
		removeFrom((IDOEntity)entityToRemoveFrom);
	}

	private void removeFrom(IDOEntity entityToRemoveFrom) throws SQLException {
		Connection conn = null;
		Statement Stmt = null;
		String qry = "";
		try {
			conn = getConnection(getDatasource());
			Stmt = conn.createStatement();
			//try
			//{
			if (!isColumnValueNotEmpty(getKeyValueSQLString(entityToRemoveFrom.getPrimaryKey()))) //removing all in middle table
				qry = "delete from " + getNameOfMiddleTable(entityToRemoveFrom, this) + " where " + this.getIDColumnName() + "= " + getPrimaryKeyValueSQLString();
			else // just removing this particular one
				qry = "delete from " + getNameOfMiddleTable(entityToRemoveFrom, this) + " where " + this.getIDColumnName() + "=" + getPrimaryKeyValueSQLString() + " AND " + entityToRemoveFrom.getEntityDefinition().getPrimaryKeyDefinition().getField().getSQLFieldName() + "= " + getKeyValueSQLString(entityToRemoveFrom.getPrimaryKey());
			//}
			/*catch (RemoteException rme)
			{
				throw new SQLException("RemoteException in removeFrom, message: " + rme.getMessage());
			}*/
			//  System.out.println("GENERIC ENTITY: "+ qry);

			Stmt.executeUpdate(qry);
		} catch (IDOCompositPrimaryKeyException e) {
			e.printStackTrace();
		} catch (EJBException e) {
			e.printStackTrace();
		} finally {
			if (Stmt != null) {
				Stmt.close();
			}
			if (conn != null) {
				freeConnection(getDatasource(), conn);
			}
		}
	}
	/**
	* Attention: Beta implementation
	*/
	public void removeFrom(Class entityToRemoveFrom, int id) throws SQLException {
		Connection conn = null;
		Statement Stmt = null;
		String qry = "";
		try {
			conn = getConnection(getDatasource());
			Stmt = conn.createStatement();
			qry = "delete from " + getNameOfMiddleTable(com.idega.data.GenericEntity.getStaticInstance(entityToRemoveFrom), this) + " where " + this.getIDColumnName() + "= " + getPrimaryKeyValueSQLString() + " AND " + com.idega.data.GenericEntity.getStaticInstance(entityToRemoveFrom).getIDColumnName() + "='" + id + "'";
			//  System.out.println("GENERIC ENTITY: "+ qry);
			Stmt.executeUpdate(qry);
		} finally {
			if (Stmt != null) {
				Stmt.close();
			}
			if (conn != null) {
				freeConnection(getDatasource(), conn);
			}
		}
	}
	/**
	 * @deprecated Replaced with idoRemoveFrom
	 */
	public void removeFrom(Class entityToRemoveFrom) throws SQLException {
		Connection conn = null;
		Statement Stmt = null;
		String qry = "";
		try {
			conn = getConnection(getDatasource());
			Stmt = conn.createStatement();
			qry = "delete from " + getNameOfMiddleTable(getStaticInstanceIDO(entityToRemoveFrom), this) + " where " + this.getIDColumnName() + "= " + getPrimaryKeyValueSQLString();

			//  System.out.println("GENERIC ENTITY: "+ qry);
			Stmt.executeUpdate(qry);
		} finally {
			if (Stmt != null) {
				Stmt.close();
			}
			if (conn != null) {
				freeConnection(getDatasource(), conn);
			}
		}
	}
	/**
	**Default remove behavior with a many-to-many relationship
	** deletes only one line in middle table if the genericentity wa consructed with a value
	** Takes in a connection but does not close it.
	**/
	public void removeFrom(IDOLegacyEntity entityToRemoveFrom, Connection conn) throws SQLException {
		Statement Stmt = null;
		String qry = "";
		try {
			Stmt = conn.createStatement();
			if (isColumnValueNotEmpty(getKeyValueSQLString(entityToRemoveFrom.getPrimaryKeyValue()))) //removing all in middle table
				qry = "delete from " + getNameOfMiddleTable(entityToRemoveFrom, this) + " where " + this.getIDColumnName() + "= " + getPrimaryKeyValueSQLString();
			else // just removing this particular one
				qry = "delete from " + getNameOfMiddleTable(entityToRemoveFrom, this) + " where " + this.getIDColumnName() + "= " + getPrimaryKeyValueSQLString() + " AND " + entityToRemoveFrom.getIDColumnName() + "= " + getKeyValueSQLString(entityToRemoveFrom.getPrimaryKeyValue());

			//  System.out.println("GENERIC ENTITY: "+ qry);
			Stmt.executeUpdate(qry);
		} finally {
			if (Stmt != null) {
				Stmt.close();
			}
		}
	}
	public void removeFrom(IDOLegacyEntity[] entityToRemoveFrom) throws SQLException {
		Connection conn = null;
		Statement Stmt = null;
		try {
			conn = getConnection(getDatasource());
			Stmt = conn.createStatement();
			String idColumnName = this.getIDColumnName();
			String id = getPrimaryKeyValueSQLString();
			int count = 0;
			for (int i = 0; i < entityToRemoveFrom.length; i++) {
				count += Stmt.executeUpdate("delete from " + getNameOfMiddleTable(entityToRemoveFrom[i], this) + " where " + idColumnName + "= " + id);
				if (!isColumnValueNotEmpty(getKeyValueSQLString(entityToRemoveFrom[i].getPrimaryKeyValue()))) //removing all in middle table
					count += Stmt.executeUpdate("delete from " + getNameOfMiddleTable(entityToRemoveFrom[i], this) + " where " + idColumnName + "= " + id);
				else // just removing this particular one
					count += Stmt.executeUpdate("delete from " + getNameOfMiddleTable(entityToRemoveFrom[i], this) + " where " + idColumnName + "= " + id + " AND " + entityToRemoveFrom[i].getIDColumnName() + "= " + getKeyValueSQLString(entityToRemoveFrom[i].getPrimaryKeyValue()));
			}
		} finally {
			if (Stmt != null) {
				Stmt.close();
			}
			if (conn != null) {
				freeConnection(getDatasource(), conn);
			}
		}
	}
	public void reverseRemoveFrom(IDOLegacyEntity entityToRemoveFrom) throws SQLException {
		Connection conn = null;
		Statement Stmt = null;
		try {
			conn = getConnection(getDatasource());
			Stmt = conn.createStatement();
			Stmt.executeUpdate("delete from " + getNameOfMiddleTable(entityToRemoveFrom, this) + " where " + entityToRemoveFrom.getIDColumnName() + "= " + getKeyValueSQLString(entityToRemoveFrom.getPrimaryKeyValue()));
		} finally {
			if (Stmt != null) {
				Stmt.close();
			}
			if (conn != null) {
				freeConnection(getDatasource(), conn);
			}
		}
	}
	public boolean equals(Object obj) {
		if (obj instanceof IDOLegacyEntity) {
			return equals((IDOLegacyEntity)obj);
		} else {
			return super.equals(obj);
		}
	}

	public boolean equals(IDOLegacyEntity entity) {
		return equals((IDOEntity)entity);
	}

	public boolean equals(IDOEntity entity) {
		if (entity != null) {
			if (entity.getClass().equals(this.getClass())) {
				Object entityPK = null;
				//try
				//{
				entityPK = entity.getPrimaryKey();
				//}
				//catch (RemoteException e)
				//{}
				if (entityPK != null && entityPK.equals(this.getPrimaryKey())) {
					return true;
				}
				return false;
			}
			return false;
		}
		return false;
	}
	public void empty() {
		_columns.clear();
	}

	public boolean hasLobColumn() throws Exception {
		String lobColumnName = this.getLobColumnName();
		//String lobColumnName = this.getStaticInstance()._lobColumnName;
		if (lobColumnName == null) {
			return false;
		}
		return true;
	}
	private void setLobColumnName(String lobColumnName) {
		((GenericEntity)this.getIDOEntityStaticInstance())._lobColumnName = lobColumnName;
	}
	private void setLobColumnName() {
		if (getLobColumnName() == null) {
			String[] columnNames = this.getColumnNames();
			for (int i = 0; i < columnNames.length; i++) {
				if (EntityAttribute.TYPE_COM_IDEGA_DATA_BLOBWRAPPER == this.getStorageClassType(columnNames[i])) {
					setLobColumnName(columnNames[i]);
				}
			}
		}
	}

	public String getLobColumnName() {
		return ((GenericEntity)this.getIDOEntityStaticInstance())._lobColumnName;
	}

	public static GenericEntity getStaticInstance(String entityClassName) {
		try {
			return (GenericEntity)getStaticInstanceIDO(Class.forName(entityClassName));
		} catch (Exception e) {
			throw new RuntimeException(e.getClass().getName() + ": " + e.getMessage());
		}
		/*if (_allStaticClasses==null){
		      _allStaticClasses=new Hashtable();
		    }
		    IDOLegacyEntity theReturn = (IDOLegacyEntity)_allStaticClasses.get(entityClassName);
		    if(theReturn==null){
		      try{
			theReturn = (IDOLegacyEntity)Class.forName(entityClassName).newInstance();
			_allStaticClasses.put(entityClassName,theReturn);
		      }
		      catch(Exception ex){
			ex.printStackTrace();
		      }
		    }
		    return theReturn;*/
	}

	private GenericEntity getIDOEntityStaticInstance() {
		//	return getStaticInstance(entityClass.getName());
		return (GenericEntity)getStaticInstanceIDO(this.getClass());
	}

	/**
	 * @deprecated Only for IDOLegacyEntity, does not work with pure IDOEntity, use getStaticInstanceIDO() instead
	 */
	public static IDOLegacyEntity getStaticInstance(Class entityClass) {
		//		//return getStaticInstance(entityClass.getName());
		//		if (entityClass.isInterface()) {
		//			return getStaticInstance(IDOLookup.getBeanClassFor(entityClass));
		//		}
		//		if (_allStaticClasses == null) {
		//			_allStaticClasses = new Hashtable();
		//		}
		//
		//		IDOLegacyEntity theReturn = (IDOLegacyEntity)_allStaticClasses.get(entityClass.getName());
		//
		//		if (theReturn == null) {
		//			try {
		//				//theReturn = (IDOLegacyEntity)entityClass.newInstance();
		//				theReturn = (IDOLegacyEntity)instanciateEntity(entityClass);
		//				_allStaticClasses.put(entityClass, theReturn);
		//			} catch (Exception ex) {
		//				ex.printStackTrace();
		//			}
		//		}
		//		return theReturn;
		return (IDOLegacyEntity)getStaticInstanceIDO(entityClass);
	}

	public static IDOEntity getStaticInstanceIDO(Class entityClass) {
		//return getStaticInstance(entityClass.getName());
		if (entityClass.isInterface()) {
			return getStaticInstanceIDO(IDOLookup.getBeanClassFor(entityClass));
		}
		if (_allStaticClasses == null) {
			_allStaticClasses = new Hashtable();
		}

		IDOEntity theReturn = (IDOEntity)_allStaticClasses.get(entityClass);

		if (theReturn == null) {
			try {

				theReturn = instanciateEntity(entityClass);
				// it might be that the method instanciateEntity(Class) has just put an
				// initialized instance of the specified entityClass into 
				// the _allStaticInstances map.
				// !!!!!!This instance should not be replaced!!!!
				// Therefore get the "right" instance.
				IDOEntity correctInstance = (GenericEntity)_allStaticClasses.get(entityClass);
				if (correctInstance != null) {
					theReturn = correctInstance;
				} else {
					_allStaticClasses.put(entityClass, theReturn);
				}

			} catch (Exception ex) {
				ex.printStackTrace();
			}
		}
		return theReturn;
	}

	public void addManyToManyRelationShip(IDOEntity relatingEntity, String relationShipTableName) {
		//addManyToManyRelationShip(relatingEntity.getClass().getName(), relationShipTableName);
		addManyToManyRelationShip(relatingEntity.getEntityDefinition().getInterfaceClass().getName(), relationShipTableName);
	}
	public void addManyToManyRelationShip(Class relatingEntityClass, String relationShipTableName) {

		EntityControl.addManyToManyRelationShip(this.getClass().getName(), relatingEntityClass.getName(), relationShipTableName);
	}
	public void addManyToManyRelationShip(String relatingEntityClassName, String relationShipTableName) {
		try {
			addManyToManyRelationShip(Class.forName(relatingEntityClassName), relationShipTableName);
		} catch (ClassNotFoundException e) {
			throw new RuntimeException("Exception in " + this.getClass().getName() + e.getMessage());
		}
	}
	public void addManyToManyRelationShip(String relatingEntityClassName) {
		try {
			//relationShipTableName = EntityControl.getMiddleTableString(this,instanciateEntity(relatingEntityClassName) );
			//addManyToManyRelationShip(this.getClass().getName(),relatingEntityClassName);
			EntityControl.addManyToManyRelationShip(this.getClass().getName(), relatingEntityClassName);
		} catch (Exception ex) {
			ex.printStackTrace();
		}
	}
	public void addManyToManyRelationShip(Class relatingEntityClass) {
		addManyToManyRelationShip(relatingEntityClass.getName());
	}
	public void addTreeRelationShip() {
		EntityControl.addTreeRelationShip(this);
	}
	public int getEntityState() {
		return _state;
	}
	public void setEntityState(int state) {
		_state = state;
	}
	public boolean isInSynchWithDatastore() {
		return (getEntityState() == IDOLegacyEntity.STATE_IN_SYNCH_WITH_DATASTORE);
	}
	/**
	 *
	 * @deprecated replaced with IDOLookup.findByPrimaryKeyLegacy();
	 */
	public static IDOLegacyEntity getEntityInstance(Class entityClass, int id) {
		IDOLegacyEntity entity = null;
		try {
			return IDOLookup.findByPrimaryKeyLegacy(entityClass, id);
		} catch (Exception e) {
			e.printStackTrace(System.err);
			System.err.println("IDOLegacyEntity: error initializing entity");
		}
		return entity;
	}
	/**
	 *
	 * @deprecated Replaced with IDOLookup.instanciateEntity(entityClass);
	 */
	public static GenericEntity getEntityInstance(Class entityClass) {
		return (GenericEntity)IDOLookup.instanciateEntity(entityClass);
	}
	public void addMetaDataRelationship() {
		addManyToManyRelationShip(MetaData.class);
		//this.getStaticInstance(this.getClass())._hasMetaDataRelationship=true;
		 ((GenericEntity)this.getIDOEntityStaticInstance())._hasMetaDataRelationship = true;
		// bug in getIDOEntityStaticInstance
		_hasMetaDataRelationship = true;
	}
	public boolean hasMetaDataRelationship() {
		return ((GenericEntity)this.getIDOEntityStaticInstance())._hasMetaDataRelationship;
	}
	// fetches the metadata for this id and puts it in a HashTable
	private void getMetaData() {
		Connection conn = null;
		Statement Stmt = null;
		_theMetaDataAttributes = new Hashtable();
		_theMetaDataIds = new Hashtable();
		_theMetaDataTypes = new Hashtable();
		try {
			conn = getConnection(getDatasource());
			Stmt = conn.createStatement();
			MetaData metadata = (MetaData)getStaticInstance(MetaData.class);
			String metadataIdColumn = metadata.getIDColumnName();
			String tableToSelectFrom = getNameOfMiddleTable(metadata, this);
			StringBuffer buffer = new StringBuffer();
			buffer.append("select ic_metadata.ic_metadata_id,metadata_name,metadata_value,meta_data_type from ");
			buffer.append(tableToSelectFrom);
			buffer.append(",ic_metadata where ");
			buffer.append(tableToSelectFrom);
			buffer.append(".");
			buffer.append(getIDColumnName());
			buffer.append("= ");
			buffer.append(getPrimaryKeyValueSQLString());
			buffer.append(" and ");
			buffer.append(tableToSelectFrom);
			buffer.append(".");
			buffer.append(metadataIdColumn);
			buffer.append("=");
			buffer.append(metadata.getEntityName());
			buffer.append(".");
			buffer.append(metadataIdColumn);
			String query = buffer.toString();
			//System.out.println("MetadataQuery="+query);
			ResultSet RS = Stmt.executeQuery(query);
			while (RS.next()) {
				_theMetaDataAttributes.put(RS.getString("metadata_name"), RS.getString("metadata_value"));
				_theMetaDataIds.put(RS.getString("metadata_name"), new Integer(RS.getInt("ic_metadata_id")));
				if (RS.getString("meta_data_type") != null)
					_theMetaDataTypes.put(RS.getString("metadata_name"), RS.getString("meta_data_type"));
			}
			RS.close();
		} catch (SQLException ex) {
			System.err.println("Exception in " + this.getClass().getName() + " gettingMetaData " + ex.getMessage());
			ex.printStackTrace(System.err);
		} finally {
			try {
				if (Stmt != null) {
					Stmt.close();
				}
			} catch (SQLException ex) {
				System.err.println("Exception in " + this.getClass().getName() + " gettingMetaData " + ex.getMessage());
				ex.printStackTrace(System.err);
			}
			if (conn != null) {
				freeConnection(getDatasource(), conn);
			}
		}
	}
	public String getMetaData(String metaDataKey) {
		if (_theMetaDataAttributes == null)
			getMetaData(); //get all meta data first if null
		return (String)_theMetaDataAttributes.get(metaDataKey);
	}
	public void setMetaDataAttributes(Hashtable metaDataAttribs) {
		String metaDataKey;
		for (Enumeration e = metaDataAttribs.keys(); e.hasMoreElements();) {
			metaDataKey = (String)e.nextElement();
			addMetaData(metaDataKey, (String)metaDataAttribs.get(metaDataKey));
		}
	}
	public void setMetaData(String metaDataKey, String metaDataValue) {
		addMetaData(metaDataKey, metaDataValue);
	}

	public void setMetaData(String metaDataKey, String metaDataValue, String metaDataType) {
		addMetaData(metaDataKey, metaDataValue, metaDataType);
	}

	public void addMetaData(String metaDataKey, String metaDataValue) {
		addMetaData(metaDataKey, metaDataValue, null);
	}

	public void addMetaData(String metaDataKey, String metaDataValue, String metaDataType) {
		if (_theMetaDataAttributes == null)
			getMetaData(); //get all meta data first if null
		if (metaDataValue != null) {
			if (metaDataType != null) {
				if (_theMetaDataTypes == null)
					_theMetaDataTypes = new Hashtable();
				_theMetaDataTypes.put(metaDataKey, metaDataType);
			}
			// change state of the entity bean
			if ((getEntityState() == IDOLegacyEntity.STATE_NEW) || (getEntityState() == IDOLegacyEntity.STATE_NEW_AND_NOT_IN_SYNCH_WITH_DATASTORE)) {
				setEntityState(IDOLegacyEntity.STATE_NEW_AND_NOT_IN_SYNCH_WITH_DATASTORE);
			} else {
				this.setEntityState(IDOLegacyEntity.STATE_NOT_IN_SYNCH_WITH_DATASTORE);
			}
			Object obj = _theMetaDataAttributes.put(metaDataKey, metaDataValue);
			metaDataHasChanged(true);
			if (obj == null) { //is new
				if (_insertMetaDataVector == null) {
					_insertMetaDataVector = new Vector();
				}
				_insertMetaDataVector.add(metaDataKey);
			} else { //is old
				if (_updateMetaDataVector == null) {
					_updateMetaDataVector = new Vector();
				}
				if (_insertMetaDataVector != null) {
					if (_insertMetaDataVector.indexOf(metaDataKey) == -1) { //is old and not in the insertlist
						_updateMetaDataVector.add(metaDataKey);
					}
				} else {
					_updateMetaDataVector.add(metaDataKey);
				}
			}
		}
	}
	public void removeAllMetaData() {
		if (_theMetaDataAttributes == null)
			getMetaData(); //get all meta data first if null
		if (_deleteMetaDataVector == null) {
			_deleteMetaDataVector = new Vector();
		}
		if (_theMetaDataAttributes != null) {
			Set keySet = _theMetaDataAttributes.keySet();
			if (keySet != null) {
				Iterator iter = keySet.iterator();
				while (iter.hasNext()) {
					String metaDataKey = (String)iter.next();
					_deleteMetaDataVector.add(metaDataKey);
					if (_insertMetaDataVector != null)
						_insertMetaDataVector.remove(metaDataKey);
					if (_updateMetaDataVector != null)
						_updateMetaDataVector.remove(metaDataKey);
				}
				metaDataHasChanged(true);
			}
		}
	}
	/**
	* return true if the metadata to delete already exists
	*/
	public boolean removeMetaData(String metaDataKey) {
		if (_theMetaDataAttributes == null)
			getMetaData(); //get all meta data first if null

		if (_deleteMetaDataVector == null) {
			_deleteMetaDataVector = new Vector();
		}
		
		if (_theMetaDataAttributes.get(metaDataKey) != null) {
			_deleteMetaDataVector.add(metaDataKey);
			
			if ((getEntityState() == IDOLegacyEntity.STATE_NEW) || (getEntityState() == IDOLegacyEntity.STATE_NEW_AND_NOT_IN_SYNCH_WITH_DATASTORE)) {
				setEntityState(IDOLegacyEntity.STATE_NEW_AND_NOT_IN_SYNCH_WITH_DATASTORE);
			}
			else {
				this.setEntityState(IDOLegacyEntity.STATE_NOT_IN_SYNCH_WITH_DATASTORE);
			}

			if (_insertMetaDataVector != null)
				_insertMetaDataVector.remove(metaDataKey);
			
			if (_updateMetaDataVector != null)
				_updateMetaDataVector.remove(metaDataKey);
			
			metaDataHasChanged(true);
			return true;
		}
		else
			return false;
	}

	public void clearMetaDataVectors() {
		_insertMetaDataVector = null;
		_updateMetaDataVector = null;
		_deleteMetaDataVector = null;
		_theMetaDataAttributes = null;
		_theMetaDataTypes = null;
	}

	public Hashtable getMetaDataAttributes() {
		if (_theMetaDataAttributes == null)
			getMetaData();
		return _theMetaDataAttributes;
	}
	public Hashtable getMetaDataIds() {
		return _theMetaDataIds;
	}
	public Hashtable getMetaDataTypes() {
		if (_theMetaDataTypes == null)
			getMetaData();
		return _theMetaDataTypes;
	}
	public Vector getMetaDataUpdateVector() {
		return _updateMetaDataVector;
	}
	public Vector getMetaDataInsertVector() {
		return _insertMetaDataVector;
	}
	public Vector getMetaDataDeleteVector() {
		return _deleteMetaDataVector;
	}
	public boolean metaDataHasChanged() {
		return _metaDataHasChanged;
	}
	public void metaDataHasChanged(boolean metaDataHasChanged) {
		_metaDataHasChanged = metaDataHasChanged;

		if (!_metaDataHasChanged) {
			clearMetaDataVectors();
		}
	}

	public void setEJBLocalHome(javax.ejb.EJBLocalHome ejbHome) {
		_ejbHome = ejbHome;
	}

	/*
		public void setEJBHome(javax.ejb.EJBHome ejbHome)
		{
			_ejbHome = ejbHome;
		}
		
		public javax.ejb.EJBHome getEJBHome()
		{
			if(_ejbHome==null){
				try{
				_ejbHome = IDOLookup.getHome(this.getClass());
				}
				catch(Exception e){
					throw new EJBException("Lookup for home for: "+this.getClass().getName()+" failed. Errormessage was: "+e.getMessage());
				}
			}
			return _ejbHome;
		}
		
		public EJBLocalHome getEJBLocalHome()
		{
			return (EJBLocalHome) this.getEJBHome();
		}
		
	
		public javax.ejb.EJBHome getEJBHome()
		{
			return (javax.ejb.EJBHome)getEJBLocalHome();
		}
		
		*/

	public javax.ejb.EJBLocalHome getEJBLocalHome() {
		if (_ejbHome == null) {
			try {
				_ejbHome = IDOLookup.getHome(this.getClass());
			} catch (Exception e) {
				throw new EJBException("Lookup for home for: " + this.getClass().getName() + " failed. Errormessage was: " + e.getMessage());
			}
		}
		return _ejbHome;
	}

	/**
	 * Not implemented
	 * @todo: implement
	 */
	public javax.ejb.Handle getHandle() {
		return null;
	}
	public Object getPrimaryKey() {
		return getPrimaryKeyValue();
	}
	public boolean isIdentical(javax.ejb.EJBObject ejbo) {
		if (ejbo != null) {
			try {
				return ejbo.getPrimaryKey().equals(this.getPrimaryKey());
			} catch (java.rmi.RemoteException rme) {
				rme.printStackTrace();
			}
		}
		return false;
	}
	public void remove() throws RemoveException {
		try {
			delete();
		} catch (Exception e) {
			throw new IDORemoveException(e);
		}
	}
	public void store() throws IDOStoreException {
		try {
			if ((getEntityState() == IDOLegacyEntity.STATE_NEW) || (getEntityState() == IDOLegacyEntity.STATE_NEW_AND_NOT_IN_SYNCH_WITH_DATASTORE)) {
				insert();
			} else if (this.getEntityState() == IDOLegacyEntity.STATE_NOT_IN_SYNCH_WITH_DATASTORE) {
				update();
			}
		} catch (Exception e) {
			//e.printStackTrace();
			throw new IDOStoreException(e.getMessage());
		}
	}
	public void ejbActivate() {
	}
	public void ejbPassivate() {
		if (_columns != null) {
			_columns.clear();
		}
		_dataStoreType = null;
		_dataSource = DEFAULT_DATASOURCE;
		_state = IDOLegacyEntity.STATE_NEW;
		_updatedColumns = null;
		_primaryKey = null;
		_theMetaDataAttributes = null;
		_insertMetaDataVector = null;
		_updateMetaDataVector = null;
		_deleteMetaDataVector = null;
		_theMetaDataIds = null;
		//_hasMetaDataRelationship = false;
		_metaDataHasChanged = false;

	}
	public void ejbRemove() throws javax.ejb.RemoveException {
		remove();
	}
	public void ejbStore() {
		store();
	}
	public void setEntityContext(javax.ejb.EntityContext ctx) {
		this._entityContext = ctx;
	}
	public void unsetEntityContext() {
		this._entityContext = null;
	}
	public Object ejbCreate() throws CreateException {
		if (this.doInsertInCreate()) {
			this.insertForCreate();
		}
		return getPrimaryKey();
	}
	/**
	 * Default create method for IDO
	 **/
	public Object ejbCreateIDO() throws CreateException {
		return ejbCreate();
	}
	/**
	 * Default create method for IDO
	 **/
	public IDOEntity ejbHomeCreateIDO() throws CreateException {
		throw new UnsupportedOperationException("Not implemented");
		//return ejbCreate();
	}
	/**
	 * Default postcreate method for IDO
	 **/
	public void ejbPostCreateIDO() {
		//does nothing
	}

	public void ejbPostCreate() {
	}
	/*public Object ejbCreate(Object primaryKey){this.setPrimaryKey(primaryKey);return primaryKey;}
	
	public Object ejbPostCreate(Object primaryKey){return primaryKey;}
	*/
	public Object ejbFindByPrimaryKey(Object pk) throws FinderException {
		this.setPrimaryKey(pk);
		return getPrimaryKey();
	}

	/**
	 * Default findByPrimaryKey method for IDO
	 **/
	public Object ejbFindByPrimaryKeyIDO(Object pk) throws FinderException {
		return ejbFindByPrimaryKeyIDO(pk);
	}

	void flagColumnUpdate(String columnName) {
		if (this._updatedColumns == null) {
			_updatedColumns = new HashMap();
		}
		_updatedColumns.put(columnName.toUpperCase(), Boolean.TRUE);
	}
	boolean hasColumnBeenUpdated(String columnName) {
		if (this._updatedColumns == null) {
			return false;
		} else {
			return (_updatedColumns.get(columnName.toUpperCase()) != null);
		}
	}
	public boolean columnsHaveChanged() {
		return (_updatedColumns != null);
	}
	/**
	 * This method outputs the outputString to System.out if the Application property
	 * "debug" is set to "TRUE"
	 */
	public void debug(String outputString) {
		if (isDebugActive()) {
			System.out.println("[DEBUG] \"" + outputString + "\" : " + this.getEntityName());
		}
	}
	protected boolean isDebugActive() {
		return IWMainApplicationSettings.isDebugActive();
	}
	public void setToInsertStartData(boolean ifTrue) {
		this.insertStartData = ifTrue;
	}
	public boolean getIfInsertStartData() {
		return insertStartData;
	}
	protected void setPrimaryKey(int pk) {
		Integer id = new Integer(pk);
		this.setPrimaryKey(id);
	}
	protected void setPrimaryKey(Object pk) {
		if (pk instanceof Integer) {
			setColumn(getIDColumnName(), (Integer)pk);
		}
		this._primaryKey = pk;
	}

	private static GenericEntity instanciateEntity(Class entityInterfaceOrBeanClass) {
		try {
			//return IDOLookup.createLegacy(entityInterfaceOrBeanClass);
			return (GenericEntity)IDOLookup.instanciateEntity(entityInterfaceOrBeanClass);
		} catch (Exception e1) {
			//Only for legacy beans;
			e1.printStackTrace();
			try {
				return (GenericEntity)entityInterfaceOrBeanClass.newInstance();
			} catch (Exception e2) {
				e2.printStackTrace();
				throw new RuntimeException(e1.getMessage());
			}
		}
	}
	private IDOLegacyEntity findByPrimaryInOtherClass(Class entityInterfaceOrBeanClass, int id) throws java.sql.SQLException {
		IDOLegacyEntity returnEntity = IDOLookup.findByPrimaryKeyLegacy(entityInterfaceOrBeanClass, id, this.getDatasource());
		//returnEntity.setDatasource(this.getDatasource());
		return returnEntity;
	}
	/**
	 * @deprecated replacced with idoFindPKsBySQL
	 */
	protected Collection idoFindIDsBySQL(String sqlQuery) throws FinderException {
		return idoFindPKsBySQL(sqlQuery);
	}
	protected Collection idoFindPKsBySQL(String sqlQuery) throws FinderException {
		return idoFindPKsBySQL(sqlQuery, -1, -1);
	}

	protected Collection idoFindPKsBySQL(String sqlQuery, String countQuery) throws FinderException, IDOException {
		Collection pkColl = null;
		Class interfaceClass = this.getInterfaceClass();
		boolean queryCachingActive = IDOContainer.getInstance().queryCachingActive(interfaceClass);
		if (queryCachingActive) {
			pkColl = IDOContainer.getInstance().getBeanCache(interfaceClass).getCachedFindQuery(sqlQuery);
		}
		if (pkColl == null) {
			pkColl = this.idoFindPKsBySQLIgnoringCache(sqlQuery, countQuery);
			if (queryCachingActive) {
				IDOContainer.getInstance().getBeanCache(interfaceClass).putCachedFindQuery(sqlQuery, pkColl);
			}
		} else {
			if (this.isDebugActive()) {
				this.debug("Cache hit for SQL query: " + sqlQuery);
			}
		}
		return pkColl;
	}

	/**
	 *
	 * @param sqlQuery
	 * @param countQuery
	 * @return IDOPrimaryKeyList
	 * @throws FinderException
	 */
	protected Collection idoFindPKsBySQLIgnoringCache(String sqlQuery, String countQuery) throws FinderException, IDOException {
		if (this.isDebugActive()) {
			this.debug("Going to Datastore for SQL query: " + sqlQuery);
			this.debug("Going to Datastore for SQL countQuery: " + countQuery);

		}
		int length = idoGetNumberOfRecords(countQuery);
		if (length > 0) {
			if (length < 1000) {
				return idoFindPKsBySQLIgnoringCache(sqlQuery, -1, -1);
			} else {
				//				try
				//				{
				//					conn = getConnection(getDatasource());
				//					Stmt = conn.createStatement(ResultSet.TYPE_SCROLL_INSENSITIVE,ResultSet.CONCUR_UPDATABLE);
				//					ResultSet RS = Stmt.executeQuery(sqlQuery);
				return new IDOPrimaryKeyList(sqlQuery, countQuery, this, length);
				//				}
				//				catch (SQLException sqle)
				//				{
				//					throw new IDOFinderException(sqle);
				//				}
			}
		}
		return new Vector();
	}

	protected Collection idoFindPKsBySQLIgnoringCache(String sqlQuery, int returningNumber, int startingEntry) throws FinderException {
		if (this.isDebugActive()) {
			this.debug("Going to Datastore for SQL query: " + sqlQuery);
		}

		if (startingEntry < 0)
			startingEntry = 0;
		if (returningNumber < 0)
			returningNumber = 0;

		Connection conn = null;
		Statement Stmt = null;
		Vector vector = new Vector();
		try {
			conn = getConnection(getDatasource());
			Stmt = conn.createStatement();
			ResultSet RS = Stmt.executeQuery(sqlQuery);
			int counter = 0;
			boolean addEntity = false;
			while (RS.next()) {
				if (startingEntry <= counter) {
					if (returningNumber > 0) {
						if (counter < (returningNumber + startingEntry))
							addEntity = true;
						else
							addEntity = false;
					} else {
						addEntity = true;
					}

					if (addEntity) {
						Object pk = this.getPrimaryKeyFromResultSet(RS);
						if (pk != null) {
							prefetchBeanFromResultSet(pk, RS);
							vector.addElement(pk);
						}
					}
				}
				counter++;
			}
			RS.close();
		} catch (SQLException sqle) {
			throw new IDOFinderException(sqle);
		} finally {
			if (Stmt != null) {
				try {
					Stmt.close();
				} catch (SQLException e) {
					e.printStackTrace();
				}
			}
			if (conn != null) {
				freeConnection(getDatasource(), conn);
			}
		}
		return vector;
	}
	/**
	 * Finds all relationships this entity bean instane has with ALL returningEntityInterfaceClass  beans
	 * Returns a collection of returningEntity instances
	 *
	 * @throws IDORelationshipException if the returningEntity has no relationship defined with this bean or an error with the query
	 */
	protected Collection idoGetRelatedEntities(Class returningEntityInterfaceClass) throws IDORelationshipException {
		IDOEntity returningEntity = IDOLookup.instanciateEntity(returningEntityInterfaceClass);
		return idoGetRelatedEntities(returningEntity, getFindRelatedSQLQuery(returningEntity, "", ""));
		/*try {
			//return EntityFinder.getInstance().findRelated((IDOLegacyEntity)this, returningEntityInterfaceClass);

			IDOEntity returningEntity = IDOLookup.instanciateEntity(returningEntityInterfaceClass);

			String tableToSelectFrom = EntityControl.getNameOfMiddleTable(returningEntity, this);
			StringBuffer buffer = new StringBuffer();
			buffer.append("select * from ");
			buffer.append(tableToSelectFrom);
			buffer.append(" where ");
			buffer.append(this.getIDColumnName());
			buffer.append("=");
			buffer.append(GenericEntity.getKeyValueSQLString(this.getPrimaryKeyValue()));
			//buffer.append(" order by ");
			//buffer.append(fromEntity.getIDColumnName());
			String SQLString = buffer.toString();

			//

			Connection conn = null;
			Statement Stmt = null;
			//Vector vector = new Vector();
			Vector vector = null;*/
			/*String tableToSelectFrom = "";
			if (returningEntity.getTableName().endsWith("_")){
				tableToSelectFrom = returningEntity.getTableName()+fromEntity.getTableName();
			}
			else{
				tableToSelectFrom = returningEntity.getTableName()+"_"+fromEntity.getTableName();
			}*/
/*
			try {
				conn = this.getConnection();
				Stmt = conn.createStatement();
				ResultSet RS = Stmt.executeQuery(SQLString);
				while (RS != null && RS.next()) {

					IDOEntity tempobj = null;
					try {
						tempobj = (IDOEntity)Class.forName(returningEntity.getClass().getName()).newInstance();

						Object pkObj = RS.getObject(returningEntity.getEntityDefinition().getPrimaryKeyDefinition().getField().getSQLFieldName());
						((IDOEntityBean)tempobj).setDatasource(this.getDatasource());
						tempobj = ((IDOHome)IDOLookup.getHome(returningEntity.getClass())).findByPrimaryKeyIDO(pkObj);
					} catch (Exception ex) {

						System.err.println("There was an error in com.idega.data.GenericEntity#idoGetRelatedEntities(Class returningEntityInterfaceClass)\n returningEntityInterfaceClass=" + returningEntity.getClass() + " : " + ex.getMessage());
						ex.printStackTrace();

					}
					if (vector == null) {
						vector = new Vector();
					}
					vector.addElement(tempobj);

				}
				RS.close();

			} finally {
				if (Stmt != null) {
					Stmt.close();
				}
				if (conn != null) {
					this.freeConnection(conn);
				}
			}

			if (vector != null) {
				vector.trimToSize();
				//return (IDOLegacyEntity[]) vector.toArray((Object[])java.lang.reflect.Array.newInstance(returningEntity.getClass(),0));
				//return vector.toArray(new IDOLegacyEntity[0]);
				return vector;
			} else {
				return null;
			}
		} catch (Exception e) {
			throw new IDORelationshipException(e, this);
		}*/
	}
	/**
	 * Returns a collection of returningEntity instances
	 */
	protected Collection idoGetRelatedEntities(IDOEntity returningEntity, String columnName, String entityColumnValue) throws IDOException {
		String SQLString = this.getFindRelatedSQLQuery(returningEntity, columnName, entityColumnValue);
		return this.idoGetRelatedEntities(returningEntity, SQLString);
	}
	/**
	 * Returns a collection of returningEntity instances
	 *
	 * @throws IDORelationshipException if the returningEntity has no relationship defined with this bean or an error with the query
	 */
	protected Collection idoGetRelatedEntities(IDOEntity returningEntity) throws IDORelationshipException {
		String sqlQuery = this.getFindRelatedSQLQuery(returningEntity, "", "");

		debug(sqlQuery);

		return idoGetRelatedEntities(returningEntity, sqlQuery);
	}

	/**
	 * Returns a collection of entity(this) instances
	 *
	 * @throws IDORelationshipException if the relatedEntity has no relationship defined with this bean or an error with the query
	 */
	protected Collection idoGetReverseRelatedEntities(IDOEntity relatedEntity) throws IDORelationshipException {
		String sqlQuery = this.getFindReverseRelatedSQLQuery(relatedEntity, "", "");

		debug(sqlQuery);

		return idoGetRelatedEntities(this, sqlQuery);

	}

	/**
	 * Returns a collection of returningEntity instances
	 *
	 * @throws IDORelationshipException if the returningEntity has no relationship defined with this bean or an error with the query
	 */
	private Collection idoGetRelatedEntities(IDOEntity returningEntity, String sqlQuery) throws IDORelationshipException {
		Vector vector = new Vector();
		Collection ids = idoGetRelatedEntityPKs(returningEntity, sqlQuery);
		Iterator iter = ids.iterator();
		try {
			IDOHome home = (IDOHome)returningEntity.getEJBLocalHome();
			while (iter.hasNext()) {
				try {
					Object pk = iter.next();
					IDOEntity entityToAdd = home.findByPrimaryKeyIDO(pk);
					vector.addElement(entityToAdd);
				} catch (Exception e) {
					throw new EJBException(e.getMessage());
				}
			}
		} catch (Exception e) {
			throw new IDORelationshipException("Error in idoGetRelatedEntities()" + e.getMessage());
		}
		return vector;
	}
	/**
	 * Returns a collection of returningEntity primary keys
	 *
	 * @throws IDORelationshipException if the returningEntity has no relationship defined with this bean or an error with the query
	 */
	protected Collection idoGetRelatedEntityPKs(IDOEntity returningEntity) throws IDORelationshipException {
		String sqlQuery = this.getFindRelatedSQLQuery(returningEntity, "", "");
		return idoGetRelatedEntityPKs(returningEntity, sqlQuery);
	}
	/**
	 * Returns a collection of returningEntity primary keys
	 */
	private Collection idoGetRelatedEntityPKs(IDOEntity returningEntity, String sqlQuery) throws IDORelationshipException {
		Connection conn = null;
		Statement Stmt = null;
		Vector vector = new Vector();
		try {
			conn = getConnection(getDatasource());
			Stmt = conn.createStatement();
			ResultSet RS = Stmt.executeQuery(sqlQuery);
			while (RS.next()) {
				Object pk = ((GenericEntity)returningEntity).getPrimaryKeyFromResultSet(RS);
				//Integer pk = (Integer)RS.getObject(legacyEntity.getIDColumnName());
				//IDOEntity entityToAdd = home.idoFindByPrimaryKey(pk);
				//vector.addElement(entityToAdd);
				vector.add(pk);
			}
			RS.close();
		} catch (Exception sqle) {
			throw new IDORelationshipException(sqle, this);
		} finally {
			if (Stmt != null) {
				try {
					Stmt.close();
				} catch (SQLException e) {
					e.printStackTrace();
				}
			}
			if (conn != null) {
				freeConnection(getDatasource(), conn);
			}
		}
		return vector;
	}
	protected Collection idoFindAllIDsOrderedBySQL(String oderByColumnName) throws FinderException {
		return this.idoFindIDsBySQL("select * from " + getTableName() + " order by " + oderByColumnName);
	}
	protected Collection idoFindAllIDsBySQL() throws FinderException {
		return this.idoFindIDsBySQL("select * from " + getTableName());
	}
	/**
	 * Finds one primary key by an SQL query
	 */
	protected Object idoFindOnePKBySQL(String sqlQuery) throws FinderException {
		Collection coll = idoFindPKsBySQL(sqlQuery, 1);
		try {
			if (!coll.isEmpty()) {
				return coll.iterator().next();
			}
		} catch (Exception e) {
			throw new IDOFinderException(e);
		}
		throw new IDOFinderException("Nothing found");
	}
	/**
	 * Finds returningNumberOfRecords Primary keys from the specified sqlQuery
	 */
	protected Collection idoFindPKsBySQL(String sqlQuery, int returningNumberOfRecords) throws FinderException {
		return idoFindPKsBySQL(sqlQuery, returningNumberOfRecords, -1);
	}

	/**
	 * Finds returningNumberOfRecords Primary keys from the specified sqlQuery
	 */
	protected Collection idoFindPKsBySQL(String sqlQuery, int returningNumberOfRecords, int startingEntry) throws FinderException {
		Collection pkColl = null;
		Class interfaceClass = this.getInterfaceClass();
		boolean queryCachingActive = IDOContainer.getInstance().queryCachingActive(interfaceClass);
		if (queryCachingActive) {
			pkColl = IDOContainer.getInstance().getBeanCache(interfaceClass).getCachedFindQuery(sqlQuery);
		}
		if (pkColl == null) {
			pkColl = this.idoFindPKsBySQLIgnoringCache(sqlQuery, returningNumberOfRecords, startingEntry);
			if (queryCachingActive) {
				IDOContainer.getInstance().getBeanCache(interfaceClass).putCachedFindQuery(sqlQuery, pkColl);
			}
		} else {
			if (this.isDebugActive()) {
				this.debug("Cache hit for SQL query: " + sqlQuery);
			}
		}
		return pkColl;
	}
	/**
	 *@deprecated replaced with idoFindPKsBySQL
	 */
	protected Collection idoFindIDsBySQL(String SQLString, int returningNumberOfRecords) throws FinderException {
		return idoFindPKsBySQL(SQLString, returningNumberOfRecords);
	}

	/**
	 * @todo why use like? isn't that slower?? also use IDOQuery or at least a StringBuffer!
	 */
	protected Collection idoFindAllIDsByColumnBySQL(String columnName, String toFind) throws FinderException {
		return idoFindIDsBySQL("select * from " + getTableName() + " where " + columnName + " like '" + toFind + "'");
	}

	/**
	 * Finds one entity that has this column value
	 */
	protected Object idoFindOnePKByColumnBySQL(String columnName, String toFind) throws FinderException {
		return idoFindOnePKBySQL("select * from " + getTableName() + " where " + columnName + " like '" + toFind + "'");
	}

	/**
	 * Finds all entities by a metadata key or metadata key and value
	 * @param key, the metadata name cannot be null
	 * @param value, the metadata value can be null
	 * @return all collection of primary keys of the current genericentity
	 * @throws FinderException
	 */
	protected Collection idoFindPKsByMetaData(String key, String value) throws FinderException {
		MetaData metadata = (MetaData)getStaticInstance(MetaData.class);
		final String middleTableName = getNameOfMiddleTable(metadata, this);
		final String tableToSelectFrom = getEntityName();
		final String metadataIdColumnName = metadata.getIDColumnName();
		final String primaryColumnName = getIDColumnName();
		final String keyColumn = ((MetaDataBMPBean)metadata).COLUMN_META_KEY;
		final String valueColumn = ((MetaDataBMPBean)metadata).COLUMN_META_VALUE;

		StringBuffer sql = new StringBuffer();
		sql
			.append("select entity.* from ")
			.append(tableToSelectFrom)
			.append(" entity ,")
			.append(middleTableName)
			.append(" middle ,")
			.append(metadata.getEntityName())
			.append(" meta ")
			.append(" where ")
			.append("entity.")
			.append(primaryColumnName)
			.append("=")
			.append("middle")
			.append(".")
			.append(primaryColumnName)
			.append(" and ")
			.append("middle.")
			.append(metadataIdColumnName)
			.append("=")
			.append("meta")
			.append(".")
			.append(metadataIdColumnName)
			.append(" and ")
			.append("meta.")
			.append(keyColumn)
			.append("=")
			.append("'")
			.append(key)
			.append("'");
		if (value != null) {
			sql.append(" and ").append("meta.").append(valueColumn).append("=").append("'").append(value).append("'");
		}

		return idoFindPKsBySQL(sql.toString());

	}

	/**
	* Finds by two columns
	*/
	protected Collection idoFindAllIDsByColumnsBySQL(String columnName, String toFind, String columnName2, String toFind2) throws FinderException {
		IDOQuery query = new IDOQuery();
		query.appendSelectAllFrom(getTableName());
		query.appendWhere(columnName);
		query.appendEqualSign();
		query.appendWithinSingleQuotes(toFind);
		query.appendAnd();
		query.append(columnName2);
		query.appendEqualSign();
		query.appendWithinSingleQuotes(toFind2);
		return idoFindIDsBySQL(query.toString());
	}

	protected Collection idoFindAllIDsByColumnOrderedBySQL(String columnName, String toFind, String orderByColumnName) throws FinderException {
		return idoFindIDsBySQL("select * from " + getTableName() + " where " + columnName + " = " + toFind + " order by " + orderByColumnName);
	}
	protected Collection idoFindAllIDsByColumnOrderedBySQL(String columnName, int toFind, String orderByColumnName) throws FinderException {
		return idoFindAllIDsByColumnOrderedBySQL(columnName, Integer.toString(toFind), columnName);
	}
	protected Collection idoFindAllIDsByColumnOrderedBySQL(String columnName, String toFind) throws FinderException {
		return idoFindAllIDsByColumnOrderedBySQL(columnName, toFind, columnName);
	}
	protected Collection idoFindAllIDsByColumnOrderedBySQL(String columnName, int toFind) throws FinderException {
		return idoFindAllIDsByColumnOrderedBySQL(columnName, Integer.toString(toFind), columnName);
	}
	protected Class getInterfaceClass() {
		return IDOLookup.getInterfaceClassFor(this.getClass());
	}
	private void flushQueryCache() {
		Class interfaceClass = this.getInterfaceClass();
		boolean queryCachingActive = IDOContainer.getInstance().queryCachingActive(interfaceClass);
		if (queryCachingActive) {
			IDOContainer.getInstance().getBeanCache(interfaceClass).flushAllQueryCache();
		}
	}
	private void flushBeanCache() {
		Class interfaceClass = this.getInterfaceClass();
		boolean beanCachingActive = IDOContainer.getInstance().beanCachingActive(interfaceClass);
		if (beanCachingActive) {
			IDOContainer.getInstance().getBeanCache(interfaceClass).flushAllBeanCache();
		}
	}
	boolean closeBlobConnections() throws Exception {
		if (this.hasLobColumn()) {
			BlobWrapper wrapper = this.getBlobColumnValue(this.getLobColumnName());
			if (wrapper != null) {
				wrapper.close();
				return true;
			}
		}
		return false;
	}
	void prefetchBeanFromResultSet(Object pk, ResultSet rs) {
		try {
			IDOContainer.getInstance().findByPrimaryKey(this.getInterfaceClass(), pk, rs, (IDOHome)this.getEJBLocalHome());
		} catch (Exception e) {
			e.printStackTrace();
		}
	}
	/**
	 * Meant to be overrided in subclasses, returns default Integer.class
	 */
	public Class getPrimaryKeyClass() {
		return Integer.class;
	}
	private String[] getPrimaryKeyColumns() {
		String s = getIDColumnName();
		String[] theReturn = { s };
		return theReturn;
	}
	/**
	 * The default implementation. Returns the number of all records for this
	 * entity.
	 * @return int the count of all records
	 * @throws IDOException if there was an exceptoin accessing the datastore
	 */
	protected int idoGetNumberOfRecords() throws IDOException {
		try {
			return this.getNumberOfRecords();
		} catch (SQLException e) {
			throw new IDOException(e, this);
		}
	}
	/**
	 * Returns the number of recors for the query sql.
	 * @param sql A count SQL query.
	 * @return int the count of the records
	 * @throws IDOException if there was an error with the query or erroraccessing the datastore
	 */
	protected int idoGetNumberOfRecords(String sql) throws IDOException {
		try {
			if (isDebugActive())
				debug(sql);
			return this.getNumberOfRecords(sql);
		} catch (SQLException e) {
			throw new IDOException(e, this);
		}
	}
	/**
	 * Returns the number of recors for the query sql.
	 * @param query A count query.
	 * @return int the count of the records
	 * @throws IDOException if there was an error with the query or erroraccessing the datastore
	 */
	protected int idoGetNumberOfRecords(IDOQuery query) throws IDOException {
		return idoGetNumberOfRecords(query.toString());
	}

	/**
	**Default remove behavior with a many-to-many relationship
	** Deletes <b>ALL</b> records of relation with all instances of entityInterfaceClass with this entity bean instance
	*
	* @throws IDORemoveRelationshipException if there is no relationship defined with the given entity class or there is an error accessing it
	**/
	protected void idoRemoveFrom(Class entityInterfaceClass) throws IDORemoveRelationshipException {
		/**
		 * @todo Change implementation
		 */
		try {
			//removeFrom(this.getStaticInstance(entityInterfaceClass));
			removeFrom(entityInterfaceClass);
		} catch (SQLException ex) {
			//ex.printStackTrace();
			throw new IDORemoveRelationshipException(ex, this);
		}
	}
	/**
	**Default remove behavior with a many-to-many relationship
	** deletes only one line in middle table if the genericentity wa consructed with a value
	*
	* @throws IDORemoveRelationshipException if there is no relationship defined with the given entity class or there is an error accessing it
	**/
	protected void idoRemoveFrom(IDOEntity entity) throws IDORemoveRelationshipException {
		/**
		 * @todo Change implementation
		 */
		try {
			removeFrom(entity);
		} catch (SQLException ex) {
			//ex.printStackTrace();
			throw new IDORemoveRelationshipException(ex, this);
		}
	}
	/**
	**Default insert behavior with a many-to-many relationship
	*
	* * @throws IDOAddRelationshipException if there is no relationship with the given entity or there is an error accessing it
	**/
	protected void idoAddTo(IDOEntity entity) throws IDOAddRelationshipException {
		/**
		 * @todo Change implementation
		 */
		try {
			Connection conn = null;
			Statement Stmt = null;
			try {
				conn = getConnection(getDatasource());
				Stmt = conn.createStatement();
				//String sql = "insert into "+getNameOfMiddleTable(entityToAddTo,this)+"("+getIDColumnName()+","+entityToAddTo.getIDColumnName()+") values("+getID()+","+entityToAddTo.getID()+")";
				String sql = null;
				//try
				//{
				sql = "insert into " + getNameOfMiddleTable(entity, this) + "(" + getIDColumnName() + "," + entity.getEntityDefinition().getPrimaryKeyDefinition().getField().getSQLFieldName() + ") values(" + getPrimaryKeyValueSQLString() + "," + getKeyValueSQLString(entity.getPrimaryKey()) + ")";
				/*}
				catch (RemoteException rme)
				{
					throw new SQLException("RemoteException in addTo, message: " + rme.getMessage());
				}*/

				//debug("statement: "+sql);

				Stmt.executeUpdate(sql);
			} finally {
				if (Stmt != null) {
					Stmt.close();
				}
				if (conn != null) {
					freeConnection(getDatasource(), conn);
				}
			}

		} catch (Exception ex) {
			//ex.printStackTrace();
			throw new IDOAddRelationshipException(ex, this);
		}
	}
	/**
	 * Method to execute an explicit update on the table of this entity bean
	 * <br><br>This method then throws away all cache associated with all instances of <b>THIS</b> entity bean class.
	 *
	 * @throws IDOException if there is an error with the query or accessing the datastore
	 *
	 */
	protected boolean idoExecuteTableUpdate(String sqlUpdateQuery) throws IDOException {
		try {
			if (SimpleQuerier.executeUpdate(sqlUpdateQuery, this.getDatasource(), false)) {
				synchronized (IDOContainer.getInstance().getBeanCache(this.getInterfaceClass())) {
					flushQueryCache();
					flushBeanCache();
				}
				return true;
			}
			return false;
		} catch (SQLException sqle) {
			throw new IDOException(sqle, this);
		}
	}
	/**
	 * Method to execute an explicit update on many (undetermined) tables in an SQL datastore.
	 * <br><br>This method then flushes all cache associated with all instances of <b>ALL</b> entity bean classes.
	 *
	 * @throws IDOException if there is an error with the query or accessing the datastore
	 *
	 */
	protected boolean idoExecuteGlobalUpdate(String sqlUpdateQuery) throws IDOException {
		try {
			return SimpleQuerier.executeUpdate(sqlUpdateQuery, getDatasource(), true);
		} catch (SQLException sqle) {
			throw new IDOException(sqle, this);
		}
	}
	/**
	 * This method will be changed to return true for "non-legacy" ido beans.<br>
	 * Can be overrided to do an insert (insertForCreate()) when ejbCreate(xx) is called
	 */
	protected boolean doInsertInCreate() {
		return false;
	}
	protected Object insertForCreate() throws CreateException {
		try {
			insert();
			return this.getPrimaryKey();
		} catch (SQLException sqle) {
			sqle.printStackTrace();
			throw new IDOCreateException(sqle);
		}
	}
	public IDOEntityDefinition getEntityDefinition() {
		return getGenericEntityDefinition();
	}

	private boolean isBeanCachingActive() {
		try {
			if (IDOContainer.getInstance().beanCachingActive(getInterfaceClass())) {
				return true;
			}
			return false;
		} catch (Exception ex) {
			return false;
		}
	}
	protected IDOUtil getIDOUtil() {
		return IDOUtil.getInstance();
	}
	public boolean isIdentical(EJBLocalObject obj) {
		return this.equals(obj);
	}

	/**
	 * Method getPrimaryKeyValueSQLString. Gets the primarykey for this record and returns it value to be added to an sql query.<br>
	 * e.g. if the primary key of of the type String this method returns the value as = 'value' but if it is an integer as = value .
	 * @return String
	 */
	public String getPrimaryKeyValueSQLString() {
		return getKeyValueSQLString(getPrimaryKeyValue());
	}

	/**
	 * Method getPrimaryKeyValueSQLString.
	 * @return String
	 */
	/**
	 * Method getKeyValueSQLString. Returns the value to be added to an sql query.<br>
	 * e.g. if the value is of the type String this method returns the value as = 'value' but if it is an integer as = value .
	 * @param keyValue
	 *  @return String
	 */
	public static String getKeyValueSQLString(Object value) {
		if (value != null) {
			if (value instanceof String) {
				return "'" + value.toString() + "'";
			} else
				return value.toString();
		} else
			return null;
	}

	/**
	 * Method isIdColumnValueNotEmpty gets the primarykey value and uses isColumnValueNotEmpty to check if it is empty or not
	 * @return boolean
	 */
	public boolean isIdColumnValueNotEmpty() {
		String value = getPrimaryKeyValueSQLString();
		return isColumnValueNotEmpty(value);
	}

	/**
	 * Method isColumnNotEmpty. This methods checks if the value is null,-1,'-1' or "" and return false or true.
	 * @param value
	 * @return boolean return true if the value is non of the above values
	 */
	public static boolean isColumnValueNotEmpty(String value) {
		//not sure if the =0 check is needed
		if ((value != null) && (!value.equals("-1")) && (!value.equals("'-1'")) && (!value.equals("")) && (!value.equals("0"))) {
			return true;
		} else
			return false;
	}

	/**
	 * Gets a new empty query
	*@return IDOQuery which is new and emtpy 
	*/
	protected IDOQuery idoQuery() {
		IDOQuery query = new IDOQuery();
		return query;
	}

	/**
	*@return IDOQuery With a prefixed select statement from this entity record.
	*/
	protected IDOQuery idoQueryGetSelect() {
		IDOQuery query = new IDOQuery();
		query.appendSelectAllFrom(this.getEntityName());
		return query;
	}

	/**
	 * @return IDOQuery With a prefixed select count statement from this entity record.
	 */
	protected IDOQuery idoQueryGetSelectCount() {
		IDOQuery query = new IDOQuery();
		query.appendSelectCountFrom(this.getEntityName());
		return query;
	}
	/**
	 * Method idoFindPKsByQuery. Gets the result of the query.
	 * @param query an IDOQuery for this entity.
	 * @return Collection of Primary keys which is a result from the query.
	 * @throws FinderException if there is an error with the query.
	 */
	protected Collection idoFindPKsByQuery(IDOQuery query) throws FinderException {
		return idoFindPKsBySQL(query.toString());
	}

	/**
	 * Method idoFindPKsByQuery. Gets the result of the query.
	 * @param query an IDOQuery for this entity.
	 * @return Collection of Primary keys which is a result from the query.
	 * @throws FinderException if there is an error with the query.
	 */
	protected Collection idoFindPKsByQuery(IDOQuery query, int returningNumberOfEntities) throws FinderException {
		return idoFindPKsBySQL(query.toString(), returningNumberOfEntities);
	}

	/**
	 * Method idoFindPKsByQuery. Gets the result of the query.
	 * @param query an IDOQuery for this entity.
	 * @return Collection of Primary keys which is a result from the query.
	 * @throws FinderException if there is an error with the query.
	 */
	protected Collection idoFindPKsByQuery(IDOQuery query, int returningNumberOfEntities, int startingEntry) throws FinderException {
		return idoFindPKsBySQL(query.toString(), returningNumberOfEntities, startingEntry);
	}

	/**
	 * Method idoFindOnePKByQuery. Gets the one primary key of the query or the first result if there are many results.* @param query an IDOQuery for this entity.
	 * @return Object which is the primary key of the object found from the query.
	 * @throws FinderException if nothing found or there is an error with the query.
	 */
	protected Object idoFindOnePKByQuery(IDOQuery query) throws FinderException {
		return idoFindOnePKBySQL(query.toString());
	}
	
	/**
	 * Default implimentation for IDOReportableEntity
	 * @param field, IDOEntityField extends JRField and can therefore be used
	 * @return
	 * @throws JRException
	 */
	public Object getFieldValue(JRField field) throws JRException {
		return this.getColumnValue(field.getName());
	}
	
	/**
	 * Convenience method to get a reference to a home for an entity
	 * @param entityClass The entity interface class to get a reference to its home.
	 * @return the home instance
	 */
	protected IDOHome getIDOHome(Class entityClass) throws IDOLookupException{
		return IDOLookup.getHome(entityClass);
	}

}
