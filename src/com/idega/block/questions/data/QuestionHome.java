package com.idega.block.questions.data;


public interface QuestionHome extends com.idega.data.IDOHome
{
 public Question create() throws javax.ejb.CreateException, java.rmi.RemoteException;
 public Question findByPrimaryKey(Object pk) throws javax.ejb.FinderException, java.rmi.RemoteException;
 public java.util.Collection findAllByCategory(int p0)throws javax.ejb.FinderException, java.rmi.RemoteException;

}