package com.idega.block.tpos.data;


public interface TPosAuthorisationEntriesBeanHome extends com.idega.data.IDOHome
{
 public TPosAuthorisationEntriesBean create() throws javax.ejb.CreateException;
 public TPosAuthorisationEntriesBean createLegacy();
 public TPosAuthorisationEntriesBean findByPrimaryKey(int id) throws javax.ejb.FinderException;
 public TPosAuthorisationEntriesBean findByPrimaryKey(Object pk) throws javax.ejb.FinderException;
 public TPosAuthorisationEntriesBean findByPrimaryKeyLegacy(int id) throws java.sql.SQLException;

}