package com.idega.block.category.business;


public interface CategoryServiceHome extends com.idega.business.IBOHome
{
 public CategoryService create() throws javax.ejb.CreateException, java.rmi.RemoteException;

}