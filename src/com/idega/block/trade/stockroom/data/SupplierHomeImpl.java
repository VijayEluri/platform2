/*
 * $Id: SupplierHomeImpl.java,v 1.7 2005/05/20 01:11:06 gimmi Exp $
 * Created on 20.5.2005
 *
 * Copyright (C) 2005 Idega Software hf. All Rights Reserved.
 *
 * This software is the proprietary information of Idega hf.
 * Use is subject to license terms.
 */
package com.idega.block.trade.stockroom.data;

import java.util.Collection;
import javax.ejb.FinderException;
import com.idega.data.IDOFactory;
import com.idega.data.IDORelationshipException;
import com.idega.user.data.Group;


/**
 * 
 *  Last modified: $Date: 2005/05/20 01:11:06 $ by $Author: gimmi $
 * 
 * @author <a href="mailto:gimmi@idega.com">gimmi</a>
 * @version $Revision: 1.7 $
 */
public class SupplierHomeImpl extends IDOFactory implements SupplierHome {

	protected Class getEntityInterfaceClass() {
		return Supplier.class;
	}

	public Supplier create() throws javax.ejb.CreateException {
		return (Supplier) super.createIDO();
	}

	public Supplier findByPrimaryKey(Object pk) throws javax.ejb.FinderException {
		return (Supplier) super.findByPrimaryKeyIDO(pk);
	}

	public Supplier createLegacy() {
		try {
			return create();
		}
		catch (javax.ejb.CreateException ce) {
			throw new RuntimeException("CreateException:" + ce.getMessage());
		}
	}

	public Supplier findByPrimaryKey(int id) throws javax.ejb.FinderException {
		return (Supplier) super.findByPrimaryKeyIDO(id);
	}

	public Supplier findByPrimaryKeyLegacy(int id) throws java.sql.SQLException {
		try {
			return findByPrimaryKey(id);
		}
		catch (javax.ejb.FinderException fe) {
			throw new java.sql.SQLException("FinderException:" + fe.getMessage());
		}
	}

	public Collection findAll(Group supplierManager) throws FinderException {
		com.idega.data.IDOEntity entity = this.idoCheckOutPooledEntity();
		java.util.Collection ids = ((SupplierBMPBean) entity).ejbFindAll(supplierManager);
		this.idoCheckInPooledEntity(entity);
		return this.getEntityCollectionForPrimaryKeys(ids);
	}

	public Collection findWithTPosMerchant() throws FinderException {
		com.idega.data.IDOEntity entity = this.idoCheckOutPooledEntity();
		java.util.Collection ids = ((SupplierBMPBean) entity).ejbFindWithTPosMerchant();
		this.idoCheckInPooledEntity(entity);
		return this.getEntityCollectionForPrimaryKeys(ids);
	}

	public Collection findAllByGroupID(int groupID) throws FinderException {
		com.idega.data.IDOEntity entity = this.idoCheckOutPooledEntity();
		java.util.Collection ids = ((SupplierBMPBean) entity).ejbFindAllByGroupID(groupID);
		this.idoCheckInPooledEntity(entity);
		return this.getEntityCollectionForPrimaryKeys(ids);
	}

	public Collection findByPostalCodes(Group supplierManager, String[] from, String[] to, Collection criterias)
			throws IDORelationshipException, FinderException {
		com.idega.data.IDOEntity entity = this.idoCheckOutPooledEntity();
		java.util.Collection ids = ((SupplierBMPBean) entity).ejbFindByPostalCodes(supplierManager, from, to, criterias);
		this.idoCheckInPooledEntity(entity);
		return this.getEntityCollectionForPrimaryKeys(ids);
	}
}
