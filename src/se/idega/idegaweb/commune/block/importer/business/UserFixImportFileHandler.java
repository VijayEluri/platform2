package se.idega.idegaweb.commune.block.importer.business;

import com.idega.block.importer.business.ImportFileHandler;


public interface UserFixImportFileHandler extends com.idega.business.IBOService,ImportFileHandler
{
 public java.util.List getFailedRecords() throws java.rmi.RemoteException;
 public boolean handleRecords()throws java.rmi.RemoteException, java.rmi.RemoteException;
 public void setImportFile(com.idega.block.importer.data.ImportFile p0) throws java.rmi.RemoteException;
 public void setRootGroup(com.idega.user.data.Group p0) throws java.rmi.RemoteException;
}
