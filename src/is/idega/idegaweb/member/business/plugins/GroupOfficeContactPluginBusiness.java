/*
 * $Id: GroupOfficeContactPluginBusiness.java,v 1.6 2005/07/14 01:02:25 eiki Exp $
 * Created on Dec 7, 2004
 *
 * Copyright (C) 2004 Idega Software hf. All Rights Reserved.
 *
 * This software is the proprietary information of Idega hf.
 * Use is subject to license terms.
 */
package is.idega.idegaweb.member.business.plugins;

import java.rmi.RemoteException;
import java.util.List;
import javax.ejb.CreateException;
import javax.ejb.RemoveException;
import com.idega.business.IBOService;
import com.idega.presentation.PresentationObject;
import com.idega.user.business.UserGroupPlugInBusiness;
import com.idega.user.data.Group;
import com.idega.user.data.User;


/**
 * 
 *  Last modified: $Date: 2005/07/14 01:02:25 $ by $Author: eiki $
 * 
 * @author <a href="mailto:eiki@idega.com">eiki</a>
 * @version $Revision: 1.6 $
 */
public interface GroupOfficeContactPluginBusiness extends IBOService, UserGroupPlugInBusiness {

	/**
	 * @see is.idega.idegaweb.member.business.plugins.GroupOfficeContactPluginBusinessBean#afterGroupCreateOrUpdate
	 */
	public void afterGroupCreateOrUpdate(Group group, Group parentGroup) throws CreateException, RemoteException;

	/**
	 * @see is.idega.idegaweb.member.business.plugins.GroupOfficeContactPluginBusinessBean#afterUserCreateOrUpdate
	 */
	public void afterUserCreateOrUpdate(User user, Group parentGroup) throws CreateException, RemoteException;

	/**
	 * @see is.idega.idegaweb.member.business.plugins.GroupOfficeContactPluginBusinessBean#beforeGroupRemove
	 */
	public void beforeGroupRemove(Group group, Group parentGroup) throws RemoveException, RemoteException;

	/**
	 * @see is.idega.idegaweb.member.business.plugins.GroupOfficeContactPluginBusinessBean#beforeUserRemove
	 */
	public void beforeUserRemove(User user, Group parentGroup) throws RemoveException, RemoteException;

	/**
	 * @see is.idega.idegaweb.member.business.plugins.GroupOfficeContactPluginBusinessBean#getGroupPropertiesTabs
	 */
	public List getGroupPropertiesTabs(Group group) throws RemoteException;

	/**
	 * @see is.idega.idegaweb.member.business.plugins.GroupOfficeContactPluginBusinessBean#getUserPropertiesTabs
	 */
	public List getUserPropertiesTabs(User user) throws RemoteException;

	/**
	 * @see is.idega.idegaweb.member.business.plugins.GroupOfficeContactPluginBusinessBean#instanciateEditor
	 */
	public PresentationObject instanciateEditor(Group group) throws RemoteException;

	/**
	 * @see is.idega.idegaweb.member.business.plugins.GroupOfficeContactPluginBusinessBean#instanciateViewer
	 */
	public PresentationObject instanciateViewer(Group group) throws RemoteException;

	/**
	 * @see is.idega.idegaweb.member.business.plugins.GroupOfficeContactPluginBusinessBean#isUserAssignableFromGroupToGroup
	 */
	public String isUserAssignableFromGroupToGroup(User user, Group sourceGroup, Group targetGroup)
			throws java.rmi.RemoteException;

	/**
	 * @see is.idega.idegaweb.member.business.plugins.GroupOfficeContactPluginBusinessBean#isUserSuitedForGroup
	 */
	public String isUserSuitedForGroup(User user, Group targetGroup) throws java.rmi.RemoteException;

	/**
	 * @see is.idega.idegaweb.member.business.plugins.GroupOfficeContactPluginBusinessBean#getMainToolbarElements
	 */
	public List getMainToolbarElements() throws RemoteException;

	/**
	 * @see is.idega.idegaweb.member.business.plugins.GroupOfficeContactPluginBusinessBean#getGroupToolbarElements
	 */
	public List getGroupToolbarElements(Group group) throws RemoteException;

	/**
	 * @see is.idega.idegaweb.member.business.plugins.GroupOfficeContactPluginBusinessBean#canCreateSubGroup
	 */
	public String canCreateSubGroup(Group group, String groupTypeOfSubGroup) throws RemoteException;
}
