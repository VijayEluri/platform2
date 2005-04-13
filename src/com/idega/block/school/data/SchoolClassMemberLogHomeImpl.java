/*
 * $Id: SchoolClassMemberLogHomeImpl.java,v 1.8 2005/04/13 09:53:43 laddi Exp $
 * Created on 13.4.2005
 *
 * Copyright (C) 2005 Idega Software hf. All Rights Reserved.
 *
 * This software is the proprietary information of Idega hf.
 * Use is subject to license terms.
 */
package com.idega.block.school.data;

import java.sql.Date;
import java.util.Collection;
import javax.ejb.FinderException;
import com.idega.data.IDOFactory;


/**
 * <p>
 * TODO laddi Describe Type SchoolClassMemberLogHomeImpl
 * </p>
 *  Last modified: $Date: 2005/04/13 09:53:43 $ by $Author: laddi $
 * 
 * @author <a href="mailto:laddi@idega.com">laddi</a>
 * @version $Revision: 1.8 $
 */
public class SchoolClassMemberLogHomeImpl extends IDOFactory implements SchoolClassMemberLogHome {

	protected Class getEntityInterfaceClass() {
		return SchoolClassMemberLog.class;
	}

	public SchoolClassMemberLog create() throws javax.ejb.CreateException {
		return (SchoolClassMemberLog) super.createIDO();
	}

	public SchoolClassMemberLog findByPrimaryKey(Object pk) throws javax.ejb.FinderException {
		return (SchoolClassMemberLog) super.findByPrimaryKeyIDO(pk);
	}

	public Collection findAllBySchoolClassMember(SchoolClassMember member) throws FinderException {
		com.idega.data.IDOEntity entity = this.idoCheckOutPooledEntity();
		java.util.Collection ids = ((SchoolClassMemberLogBMPBean) entity).ejbFindAllBySchoolClassMember(member);
		this.idoCheckInPooledEntity(entity);
		return this.getEntityCollectionForPrimaryKeys(ids);
	}

	public SchoolClassMemberLog findOpenLogByUser(SchoolClassMember member) throws FinderException {
		com.idega.data.IDOEntity entity = this.idoCheckOutPooledEntity();
		Object pk = ((SchoolClassMemberLogBMPBean) entity).ejbFindOpenLogByUser(member);
		this.idoCheckInPooledEntity(entity);
		return this.findByPrimaryKey(pk);
	}

	public SchoolClassMemberLog findOpenLogByUserAndSchoolClass(SchoolClassMember member, SchoolClass schoolClass)
			throws FinderException {
		com.idega.data.IDOEntity entity = this.idoCheckOutPooledEntity();
		Object pk = ((SchoolClassMemberLogBMPBean) entity).ejbFindOpenLogByUserAndSchoolClass(member, schoolClass);
		this.idoCheckInPooledEntity(entity);
		return this.findByPrimaryKey(pk);
	}

	public SchoolClassMemberLog findLatestLogByUser(SchoolClassMember member) throws FinderException {
		com.idega.data.IDOEntity entity = this.idoCheckOutPooledEntity();
		Object pk = ((SchoolClassMemberLogBMPBean) entity).ejbFindLatestLogByUser(member);
		this.idoCheckInPooledEntity(entity);
		return this.findByPrimaryKey(pk);
	}

	public SchoolClassMemberLog findClosedLogByUserAndSchoolClass(SchoolClassMember member, SchoolClass schoolClass)
			throws FinderException {
		com.idega.data.IDOEntity entity = this.idoCheckOutPooledEntity();
		Object pk = ((SchoolClassMemberLogBMPBean) entity).ejbFindClosedLogByUserAndSchoolClass(member, schoolClass);
		this.idoCheckInPooledEntity(entity);
		return this.findByPrimaryKey(pk);
	}

	public SchoolClassMemberLog findByPlacementAndDate(SchoolClassMember member, Date date) throws FinderException {
		com.idega.data.IDOEntity entity = this.idoCheckOutPooledEntity();
		Object pk = ((SchoolClassMemberLogBMPBean) entity).ejbFindByPlacementAndDate(member, date);
		this.idoCheckInPooledEntity(entity);
		return this.findByPrimaryKey(pk);
	}

	public SchoolClassMemberLog findFutureLogByPlacementAndDate(SchoolClassMember member, Date date)
			throws FinderException {
		com.idega.data.IDOEntity entity = this.idoCheckOutPooledEntity();
		Object pk = ((SchoolClassMemberLogBMPBean) entity).ejbFindFutureLogByPlacementAndDate(member, date);
		this.idoCheckInPooledEntity(entity);
		return this.findByPrimaryKey(pk);
	}

	public SchoolClassMemberLog findByPlacementAndEndDate(SchoolClassMember member, Date date) throws FinderException {
		com.idega.data.IDOEntity entity = this.idoCheckOutPooledEntity();
		Object pk = ((SchoolClassMemberLogBMPBean) entity).ejbFindByPlacementAndEndDate(member, date);
		this.idoCheckInPooledEntity(entity);
		return this.findByPrimaryKey(pk);
	}

	public SchoolClassMemberLog findByPlacementAndDateBack(SchoolClassMember member, Date date) throws FinderException {
		com.idega.data.IDOEntity entity = this.idoCheckOutPooledEntity();
		Object pk = ((SchoolClassMemberLogBMPBean) entity).ejbFindByPlacementAndDateBack(member, date);
		this.idoCheckInPooledEntity(entity);
		return this.findByPrimaryKey(pk);
	}

	public Collection findByPlacementAndDates(SchoolClassMember member, Date fromDate, Date toDate)
			throws FinderException {
		com.idega.data.IDOEntity entity = this.idoCheckOutPooledEntity();
		java.util.Collection ids = ((SchoolClassMemberLogBMPBean) entity).ejbFindByPlacementAndDates(member, fromDate,
				toDate);
		this.idoCheckInPooledEntity(entity);
		return this.getEntityCollectionForPrimaryKeys(ids);
	}

	public Collection findAllByPlacementAndDates(SchoolClassMember member, Date fromDate, Date toDate)
			throws FinderException {
		com.idega.data.IDOEntity entity = this.idoCheckOutPooledEntity();
		java.util.Collection ids = ((SchoolClassMemberLogBMPBean) entity).ejbFindAllByPlacementAndDates(member, fromDate,
				toDate);
		this.idoCheckInPooledEntity(entity);
		return this.getEntityCollectionForPrimaryKeys(ids);
	}

	public Collection findAllByPlacement(SchoolClassMember member) throws FinderException {
		com.idega.data.IDOEntity entity = this.idoCheckOutPooledEntity();
		java.util.Collection ids = ((SchoolClassMemberLogBMPBean) entity).ejbFindAllByPlacement(member);
		this.idoCheckInPooledEntity(entity);
		return this.getEntityCollectionForPrimaryKeys(ids);
	}
}
