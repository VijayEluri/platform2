package com.idega.core.data;


public interface ICFileTypeHandlerHome extends com.idega.data.IDOHome
{
 public ICFileTypeHandler create() throws javax.ejb.CreateException;
 public ICFileTypeHandler createLegacy();
 public ICFileTypeHandler findByPrimaryKey(int id) throws javax.ejb.FinderException;
 public ICFileTypeHandler findByPrimaryKey(Object pk) throws javax.ejb.FinderException;
 public ICFileTypeHandler findByPrimaryKeyLegacy(int id) throws java.sql.SQLException;

}