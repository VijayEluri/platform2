/*
 * $Id$
 *
 * Copyright (C) 2000-2003 Idega Software. All Rights Reserved.
 *
 * This software is the proprietary information of Idega Software.
 * Use is subject to license terms.
 */
package com.idega.user.data;

import java.sql.Timestamp;
import java.util.Collection;

import javax.ejb.FinderException;

import com.idega.data.GenericEntity;

/**
 * @author palli
 *
 * To change this generated comment go to 
 * Window>Preferences>Java>Code Generation>Code and Comments
 */
public class UserStatusBMPBean extends GenericEntity implements UserStatus {
	private static final String ENTITY_NAME = "ic_usergroup_status";
	private static final String STATUS_ID = "status_id";
	private static final String IC_USER = "ic_user_id";
	private static final String IC_GROUP = "ic_group_id";
	private static final String DATE_FROM = "date_from";
	private static final String DATE_TO = "date_to";

	public UserStatusBMPBean() {
		super();
	}

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
	
		addManyToOneRelationship(STATUS_ID,Status.class);
		addManyToOneRelationship(IC_USER,User.class);
		addManyToOneRelationship(IC_GROUP,Group.class);
		
		addAttribute(DATE_FROM,"Date from",true,true,java.sql.Timestamp.class);
		addAttribute(DATE_TO,"Date to",true,true,java.sql.Timestamp.class);
	}

	public int getStatusId() {
		return getIntColumnValue(STATUS_ID);
	}
	
	public Status getStatus() {
		return (Status)getColumn(STATUS_ID);
	}

	public void setStatusId(String id) {
		setColumn(STATUS_ID,id);
	}
	
	public void setStatus(Status status) {
		setColumn(STATUS_ID,status);
	}
	
	public int getUserId() {
		return getIntColumnValue(IC_USER);
	}
	
	public User getUser() {
		return (User)getColumn(IC_USER);
	}
	
	public void setUserId(int id) {
		setColumn(IC_USER,id);
	}
	
	public void setUser(User user) {
		setColumn(IC_USER, user);
	}
	
	public int getGroupId() {
		return getIntColumnValue(IC_GROUP);
	}
	
	public Group getGroup() {
		return (Group)getColumn(IC_GROUP);
	}
	
	public void setGroupId(int id) {
		setColumn(IC_GROUP,id);
	}
	
	public void setGroup(Group group) {
		setColumn(IC_GROUP, group);
	}	
	
	public void setDateFrom(Timestamp from) {
		setColumn(DATE_FROM,from);
	}
	
	public Timestamp getDateFrom() {
		return (Timestamp) getColumnValue(DATE_FROM);
	}
	
	public void setDateTo(Timestamp to) {
		setColumn(DATE_TO,to);
	}
	
	public Timestamp getDateTo() {
		return (Timestamp) getColumnValue(DATE_TO);
	}

	public Collection ejbFindAll() throws FinderException {
		return super.idoFindAllIDsBySQL();
	}
	
	public Collection ejbFindAllByUserId(int id) throws FinderException {
		StringBuffer sql = new StringBuffer("select * from ");
		sql.append(ENTITY_NAME);
		sql.append(" where ");
		sql.append(IC_USER);
		sql.append(" = ");
		sql.append(id);
		
		return super.idoFindIDsBySQL(sql.toString());
	}
	
	public Collection ejbFindAllByGroupId(int id) throws FinderException {
		StringBuffer sql = new StringBuffer("select * from ");
		sql.append(ENTITY_NAME);
		sql.append(" where ");
		sql.append(IC_GROUP);
		sql.append(" = ");
		sql.append(id);
		
		return super.idoFindIDsBySQL(sql.toString());
	}	
	
	public Collection ejbFindAllByUserIdAndGroupId(int user_id, int group_id) throws FinderException {
		StringBuffer sql = new StringBuffer("select * from ");
		sql.append(ENTITY_NAME);
		sql.append(" where ");
		sql.append(IC_USER);
		sql.append(" = ");
		sql.append(user_id);
		sql.append(" and ");
		sql.append(IC_GROUP);
		sql.append(" = ");
		sql.append(group_id);
		
		return super.idoFindIDsBySQL(sql.toString());
	}	
}