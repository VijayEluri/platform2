/*
 * Created on 26.6.2003
 *
 * To change the template for this generated file go to
 * Window>Preferences>Java>Code Generation>Code and Comments
 */
package com.idega.data;

/**
 * Title:		PrimaryKeyDefinition
 * Description:
 * Copyright:	Copyright (c) 2003
 * Company:		idega Software
 * @author		2003 - idega team - <br><a href="mailto:gummi@idega.is">Gudmundur Agust Saemundsson</a><br>
 * @version		1.0
 */
public class PrimaryKeyDefinition implements IDOPrimaryKeyDefinition {
	
	private IDOEntityField[] _fields = new IDOEntityField[0];
	private Class _primaryKeyClass = Integer.class;
	private GenericEntityDefinition _entityDefinition;	
	
	/**
	 * 
	 */
	public PrimaryKeyDefinition(GenericEntityDefinition def) {
		super();
		this.setDeclaredEntity(def);
	}
	
	
	
	public void addFieldEntity(IDOEntityField field) {
		if (!containsEquivalentField(field)) {
			int length = _fields.length;
			IDOEntityField[] tempArray = new IDOEntityField[length + 1];
			System.arraycopy(_fields, 0, tempArray, 0, length);
			tempArray[length] = field;
			_fields = tempArray;
		} else {
			//System.err.println(getDeclaredEntity().getUniqueEntityName() + ": PrimaryKeyDefinition already contains equivalent field \"" + field.getSQLFieldName() + "\"");
		}
	}

	protected boolean containsEquivalentField(IDOEntityField field) {
		for (int i = 0; i < _fields.length; i++) {
			if (_fields[i].getSQLFieldName().equals(field.getSQLFieldName())) {
				return true;
			}
		}
		return false;
	}
	
	

	/* (non-Javadoc)
	 * @see com.idega.data.IDOPrimaryKeyDefinition#getFields()
	 */
	public IDOEntityField[] getFields() {
		return _fields;
	}

	/* (non-Javadoc)
	 * @see com.idega.data.IDOPrimaryKeyDefinition#isComposite()
	 */
	public boolean isComposite() {
		return (_fields.length>1);
	}
	
	/* (non-Javadoc)
	 * @see com.idega.data.IDOPrimaryKeyDefinition#getPrimaryKeyClass()
	 */
	public Class getPrimaryKeyClass() {
		return _primaryKeyClass;
	}
	
	public void setDeclaredEntity(GenericEntityDefinition definition) {
		this._entityDefinition=definition;
	}

	public IDOEntityDefinition getDeclaredEntity() {
		return this._entityDefinition;
	}



	/* (non-Javadoc)
	 * @see com.idega.data.IDOPrimaryKeyDefinition#getField()
	 */
	public IDOEntityField getField() throws IDOCompositPrimaryKeyException {
		if(!isComposite()){
			try {
				return getFields()[0];
			} catch (ArrayIndexOutOfBoundsException e) {
				System.err.println("PrimaryKey has not been defined");
				throw e;
			}
		} else {
			throw new IDOCompositPrimaryKeyException();
		}
	}

}
