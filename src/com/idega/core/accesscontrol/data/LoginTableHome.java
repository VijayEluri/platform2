package com.idega.core.accesscontrol.data;


public interface LoginTableHome extends com.idega.data.IDOHome
{
 public LoginTable create() throws javax.ejb.CreateException;
 public LoginTable createLegacy();
 public LoginTable findByPrimaryKey(int id) throws javax.ejb.FinderException;
 public LoginTable findByPrimaryKey(Object pk) throws javax.ejb.FinderException;
 public LoginTable findByPrimaryKeyLegacy(int id) throws java.sql.SQLException;

}