package is.idega.idegaweb.member.business;

import javax.ejb.*;

public interface KRImportFileHandler extends com.idega.business.IBOService,com.idega.block.importer.business.ImportFileHandler
{
 public void printFailedRecords() throws java.rmi.RemoteException;
 public com.idega.user.data.Group getRootGroup() throws java.rmi.RemoteException;
 public boolean handleRecords()throws java.rmi.RemoteException, java.rmi.RemoteException;
 public void setRootGroup(com.idega.user.data.Group p0) throws java.rmi.RemoteException;
 public void setImportFile(com.idega.block.importer.data.ImportFile p0) throws java.rmi.RemoteException;
}
