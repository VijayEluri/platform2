package is.idega.idegaweb.member.business.plugins;

import com.idega.user.data.Group;


public interface DeildirPluginBusiness extends com.idega.business.IBOService,com.idega.user.business.UserGroupPlugInBusiness
{
 public void afterGroupCreateOrUpdate(com.idega.user.data.Group p0, Group parentGroup)throws javax.ejb.CreateException,java.rmi.RemoteException, java.rmi.RemoteException;
 public void afterUserCreateOrUpdate(com.idega.user.data.User p0, Group parentGroup)throws javax.ejb.CreateException,java.rmi.RemoteException, java.rmi.RemoteException;
 public void beforeGroupRemove(com.idega.user.data.Group p0, Group parentGroup)throws javax.ejb.RemoveException,java.rmi.RemoteException, java.rmi.RemoteException;
 public void beforeUserRemove(com.idega.user.data.User p0, Group parentGroup)throws javax.ejb.RemoveException,java.rmi.RemoteException, java.rmi.RemoteException;
 public java.util.List getGroupPropertiesTabs(com.idega.user.data.Group p0)throws java.rmi.RemoteException, java.rmi.RemoteException;
 public java.util.List getUserPropertiesTabs(com.idega.user.data.User p0)throws java.rmi.RemoteException, java.rmi.RemoteException;
 public com.idega.presentation.PresentationObject instanciateEditor(com.idega.user.data.Group p0)throws java.rmi.RemoteException, java.rmi.RemoteException;
 public com.idega.presentation.PresentationObject instanciateViewer(com.idega.user.data.Group p0)throws java.rmi.RemoteException, java.rmi.RemoteException;
}
