package com.idega.core.component.data;


public class ICObjectTypeHomeImpl extends com.idega.data.IDOFactory implements ICObjectTypeHome
{
 protected Class getEntityInterfaceClass(){
  return ICObjectType.class;
 }


 public ICObjectType create() throws javax.ejb.CreateException{
  return (ICObjectType) super.createIDO();
 }


 public ICObjectType findByPrimaryKey(Object pk) throws javax.ejb.FinderException{
  return (ICObjectType) super.findByPrimaryKeyIDO(pk);
 }


public java.util.Collection findAll()throws javax.ejb.FinderException{
	com.idega.data.IDOEntity entity = this.idoCheckOutPooledEntity();
	java.util.Collection theReturn = ((ICObjectTypeBMPBean)entity).ejbHomeFindAll();
	this.idoCheckInPooledEntity(entity);
	return theReturn;
}


}