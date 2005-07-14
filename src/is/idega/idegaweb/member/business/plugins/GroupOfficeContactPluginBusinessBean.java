package is.idega.idegaweb.member.business.plugins;

import is.idega.idegaweb.member.presentation.GroupOfficeContactTab;
import java.rmi.RemoteException;
import java.util.ArrayList;
import java.util.List;
import javax.ejb.CreateException;
import javax.ejb.RemoveException;
import com.idega.business.IBOServiceBean;
import com.idega.presentation.PresentationObject;
import com.idega.user.business.UserGroupPlugInBusiness;
import com.idega.user.data.Group;
import com.idega.user.data.User;

/**
 *@author     <a href="mailto:thomas@idega.is">Thomas Hilbig</a>
 *@version    1.0
 */
public class GroupOfficeContactPluginBusinessBean extends IBOServiceBean implements GroupOfficeContactPluginBusiness, UserGroupPlugInBusiness {

	/**
	 * @see com.idega.user.business.UserGroupPlugInBusiness#afterGroupCreateOrUpdate(com.idega.user.data.Group, Group)
	 */
	public void afterGroupCreateOrUpdate(Group group, Group parentGroup) throws CreateException, RemoteException {
	}

	/**
	 * @see com.idega.user.business.UserGroupPlugInBusiness#afterUserCreateOrUpdate(com.idega.user.data.User, Group)
	 */
	public void afterUserCreateOrUpdate(User user, Group parentGroup) throws CreateException, RemoteException {
	}

	/**
	 * @see com.idega.user.business.UserGroupPlugInBusiness#beforeGroupRemove(com.idega.user.data.Group, Group)
	 */
	public void beforeGroupRemove(Group group, Group parentGroup) throws RemoveException, RemoteException {
	}

	/**
	 * @see com.idega.user.business.UserGroupPlugInBusiness#beforeUserRemove(com.idega.user.data.User, Group)
	 */
	public void beforeUserRemove(User user, Group parentGroup) throws RemoveException, RemoteException {
	}

	/**
	 * @see com.idega.user.business.UserGroupPlugInBusiness#getGroupPropertiesTabs(com.idega.user.data.Group)
	 */
	public List getGroupPropertiesTabs(Group group) throws RemoteException {
    List list = new ArrayList();
    list.add(new GroupOfficeContactTab(group));  
    return list;  
	}

	/**
	 * @see com.idega.user.business.UserGroupPlugInBusiness#getUserPropertiesTabs(com.idega.user.data.User)
	 */
	public List getUserPropertiesTabs(User user) throws RemoteException {
    return null;
    
	}

	/**
	 * @see com.idega.user.business.UserGroupPlugInBusiness#instanciateEditor(com.idega.user.data.Group)
	 */
	public PresentationObject instanciateEditor(Group group) throws RemoteException {
		return null;
	}

	/**
	 * @see com.idega.user.business.UserGroupPlugInBusiness#instanciateViewer(com.idega.user.data.Group)
	 */
	public PresentationObject instanciateViewer(Group group) throws RemoteException {
		return null;
	}

  public String isUserAssignableFromGroupToGroup(User user, Group sourceGroup, Group targetGroup) {
    return null;
  }
  
  public String isUserSuitedForGroup(User user, Group targetGroup)  {
    return null;
  }

/* (non-Javadoc)
 * @see com.idega.user.business.UserGroupPlugInBusiness#getMainToolbarElements()
 */
public List getMainToolbarElements() throws RemoteException {
	return null;
}

/* (non-Javadoc)
 * @see com.idega.user.business.UserGroupPlugInBusiness#getGroupToolbarElements(com.idega.user.data.Group)
 */
public List getGroupToolbarElements(Group group) throws RemoteException {
	return null;
}

/* (non-Javadoc)
 * @see com.idega.user.business.UserGroupPlugInBusiness#canCreateSubGroup(com.idega.user.data.Group,java.lang.String)
 */
public String canCreateSubGroup(Group group, String groupTypeOfSubGroup) throws RemoteException {
	// TODO Auto-generated method stub
	return null;
}

}
