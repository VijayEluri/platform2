package com.idega.block.creditcard.data;


public class TPosAuthorisationEntriesBeanHomeImpl extends com.idega.data.IDOFactory implements TPosAuthorisationEntriesBeanHome
{
 protected Class getEntityInterfaceClass(){
  return TPosAuthorisationEntriesBean.class;
 }


 public TPosAuthorisationEntriesBean create() throws javax.ejb.CreateException{
  return (TPosAuthorisationEntriesBean) super.createIDO();
 }


public TPosAuthorisationEntriesBean findByAuthorisationIdRsp(java.lang.String p0)throws javax.ejb.FinderException{
	com.idega.data.IDOEntity entity = this.idoCheckOutPooledEntity();
	Object pk = ((TPosAuthorisationEntriesBeanBMPBean)entity).ejbFindByAuthorisationIdRsp(p0);
	this.idoCheckInPooledEntity(entity);
	return this.findByPrimaryKey(pk);
}

 public TPosAuthorisationEntriesBean findByPrimaryKey(Object pk) throws javax.ejb.FinderException{
  return (TPosAuthorisationEntriesBean) super.findByPrimaryKeyIDO(pk);
 }



}