/*
 * $Id$
 *
 * Copyright (C) 2000-2003 Idega Software. All Rights Reserved.
 *
 * This software is the proprietary information of Idega Software.
 * Use is subject to license terms.
 */
package com.idega.user.data;

import java.util.Collection;

import javax.ejb.FinderException;

import com.idega.data.GenericEntity;

/**
 * @author palli
 *
 * To change this generated comment go to 
 * Window>Preferences>Java>Code Generation>Code and Comments
 */
public class StatusBMPBean extends GenericEntity implements Status {
	private static final String ENTITY_NAME = "ic_user_status";

	private static final String STATUS_LOC_KEY = "status_key";
	private static final String PARENT_STATUS = "parent_id";

	/* (non-Javadoc)
	 * @see com.idega.data.IDOLegacyEntity#getEntityName()
	 */
	public String getEntityName() {
		return ENTITY_NAME;
	}

	/* (non-Javadoc)
	 * @see com.idega.data.IDOLegacyEntity#initializeAttributes()
	 */
	public void initializeAttributes() {
		addAttribute(getIDColumnName());
		addAttribute(STATUS_LOC_KEY,"Status key",true,true,java.lang.String.class);
		
		addManyToOneRelationship(PARENT_STATUS,Status.class);
	}
	
	public void setStatusKey(String key) {
		setColumn(STATUS_LOC_KEY,key);
	}

	public String getStatusKey() {
		return getStringColumnValue(STATUS_LOC_KEY);
	}
	
	public Collection ejbFindAll() throws FinderException {
		return super.idoFindAllIDsBySQL();
	}
}