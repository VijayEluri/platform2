/*
 * $Id: LDAPReplicationBusiness.java,v 1.4 2004/10/31 22:35:21 eiki Exp $
 * Created on Oct 31, 2004
 *
 * Copyright (C) 2004 Idega Software hf. All Rights Reserved.
 *
 * This software is the proprietary information of Idega hf.
 * Use is subject to license terms.
 */
package com.idega.core.ldap.replication.business;

import java.io.IOException;
import java.util.Properties;
import com.idega.business.IBOService;
import com.idega.core.ldap.util.IWLDAPConstants;


/**
 * 
 *  Last modified: $Date: 2004/10/31 22:35:21 $ by $Author: eiki $
 * 
 * @author <a href="mailto:eiki@idega.com">eiki</a>
 * @version $Revision: 1.4 $
 */
public interface LDAPReplicationBusiness extends IBOService, LDAPReplicationConstants, IWLDAPConstants {

	/**
	 * @see com.idega.core.ldap.replication.business.LDAPReplicationBusinessBean#deleteReplicator
	 */
	public void deleteReplicator(int replicatorNumber) throws IOException, java.rmi.RemoteException;

	/**
	 * @see com.idega.core.ldap.replication.business.LDAPReplicationBusinessBean#copyPropertyBetweenReplicators
	 */
	public void copyPropertyBetweenReplicators(String key, int copyFromReplicatorNumber, int copyToReplicatorNumber)
			throws IOException, java.rmi.RemoteException;

	/**
	 * @see com.idega.core.ldap.replication.business.LDAPReplicationBusinessBean#setReplicationProperty
	 */
	public void setReplicationProperty(String key, int replicatorNumber, String value) throws IOException,
			java.rmi.RemoteException;

	/**
	 * @see com.idega.core.ldap.replication.business.LDAPReplicationBusinessBean#getReplicationProperty
	 */
	public String getReplicationProperty(String key, int replicatorNumber) throws IOException, java.rmi.RemoteException;

	/**
	 * @see com.idega.core.ldap.replication.business.LDAPReplicationBusinessBean#removeAllPropertiesOfReplicator
	 */
	public void removeAllPropertiesOfReplicator(int replicatorNumber) throws IOException, java.rmi.RemoteException;

	/**
	 * @see com.idega.core.ldap.replication.business.LDAPReplicationBusinessBean#getNumberOfReplicators
	 */
	public int getNumberOfReplicators() throws IOException, java.rmi.RemoteException;

	/**
	 * @see com.idega.core.ldap.replication.business.LDAPReplicationBusinessBean#setNumberOfReplicators
	 */
	public void setNumberOfReplicators(int number) throws IOException, java.rmi.RemoteException;

	/**
	 * @see com.idega.core.ldap.replication.business.LDAPReplicationBusinessBean#startReplicator
	 */
	public boolean startReplicator(int replicatorNumber) throws IOException, java.rmi.RemoteException;

	/**
	 * @see com.idega.core.ldap.replication.business.LDAPReplicationBusinessBean#stopReplicator
	 */
	public boolean stopReplicator(int replicatorNumber) throws IOException, java.rmi.RemoteException;

	/**
	 * @see com.idega.core.ldap.replication.business.LDAPReplicationBusinessBean#startAllReplicators
	 */
	public void startAllReplicators() throws IOException, java.rmi.RemoteException;

	/**
	 * @see com.idega.core.ldap.replication.business.LDAPReplicationBusinessBean#startOrStopAllReplicators
	 */
	public void startOrStopAllReplicators(String startOrStop) throws IOException, java.rmi.RemoteException;

	/**
	 * @see com.idega.core.ldap.replication.business.LDAPReplicationBusinessBean#stopAllReplicators
	 */
	public void stopAllReplicators() throws IOException, java.rmi.RemoteException;

	/**
	 * @see com.idega.core.ldap.replication.business.LDAPReplicationBusinessBean#createNewReplicationSettings
	 */
	public void createNewReplicationSettings() throws IOException, java.rmi.RemoteException;

	/**
	 * @see com.idega.core.ldap.replication.business.LDAPReplicationBusinessBean#getReplicationSettings
	 */
	public Properties getReplicationSettings() throws IOException, java.rmi.RemoteException;

	/**
	 * @see com.idega.core.ldap.replication.business.LDAPReplicationBusinessBean#storeReplicationProperties
	 */
	public void storeReplicationProperties() throws IOException, java.rmi.RemoteException;
}
