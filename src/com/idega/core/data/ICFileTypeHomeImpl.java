package com.idega.core.data;


public class ICFileTypeHomeImpl extends com.idega.data.IDOFactory implements ICFileTypeHome
{
 protected Class getEntityInterfaceClass(){
  return ICFileType.class;
 }

 public ICFileType create() throws javax.ejb.CreateException{
  return (ICFileType) super.idoCreate();
 }

 public ICFileType createLegacy(){
	try{
		return create();
	}
	catch(javax.ejb.CreateException ce){
		throw new RuntimeException("CreateException:"+ce.getMessage());
	}

 }

 public ICFileType findByPrimaryKey(int id) throws javax.ejb.FinderException{
  return (ICFileType) super.idoFindByPrimaryKey(id);
 }

 public ICFileType findByPrimaryKey(Object pk) throws javax.ejb.FinderException{
  return (ICFileType) super.idoFindByPrimaryKey(pk);
 }

 public ICFileType findByPrimaryKeyLegacy(int id) throws java.sql.SQLException{
	try{
		return findByPrimaryKey(id);
	}
	catch(javax.ejb.FinderException fe){
		throw new java.sql.SQLException(fe.getMessage());
	}

 }


}