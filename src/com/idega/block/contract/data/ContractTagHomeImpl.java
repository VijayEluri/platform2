package com.idega.block.contract.data;


public class ContractTagHomeImpl extends com.idega.data.IDOFactory implements ContractTagHome
{
 protected Class getEntityInterfaceClass(){
  return ContractTag.class;
 }

 public ContractTag create() throws javax.ejb.CreateException{
  return (ContractTag) super.idoCreate();
 }

 public ContractTag createLegacy(){
	try{
		return create();
	}
	catch(javax.ejb.CreateException ce){
		throw new RuntimeException("CreateException:"+ce.getMessage());
	}

 }

 public ContractTag findByPrimaryKey(int id) throws javax.ejb.FinderException{
  return (ContractTag) super.idoFindByPrimaryKey(id);
 }

 public ContractTag findByPrimaryKey(Object pk) throws javax.ejb.FinderException{
  return (ContractTag) super.idoFindByPrimaryKey(pk);
 }

 public ContractTag findByPrimaryKeyLegacy(int id) throws java.sql.SQLException{
	try{
		return findByPrimaryKey(id);
	}
	catch(javax.ejb.FinderException fe){
		throw new java.sql.SQLException(fe.getMessage());
	}

 }


}