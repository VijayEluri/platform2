package com.idega.block.finance.data;


public class AccountHomeImpl extends com.idega.data.IDOFactory implements AccountHome
{
 protected Class getEntityInterfaceClass(){
  return Account.class;
 }


 public Account create() throws javax.ejb.CreateException{
  return (Account) super.createIDO();
 }


public java.util.Collection findAllByUserId(int p0)throws javax.ejb.FinderException{
	com.idega.data.IDOEntity entity = this.idoCheckOutPooledEntity();
	java.util.Collection ids = ((AccountBMPBean)entity).ejbFindAllByUserId(p0);
	this.idoCheckInPooledEntity(entity);
	return this.getEntityCollectionForPrimaryKeys(ids);
}

public java.util.Collection findAllByUserIdAndType(int p0,java.lang.String p1)throws javax.ejb.FinderException{
	com.idega.data.IDOEntity entity = this.idoCheckOutPooledEntity();
	java.util.Collection ids = ((AccountBMPBean)entity).ejbFindAllByUserIdAndType(p0,p1);
	this.idoCheckInPooledEntity(entity);
	return this.getEntityCollectionForPrimaryKeys(ids);
}

public java.util.Collection findByAssessmentRound(int p0)throws javax.ejb.FinderException{
	com.idega.data.IDOEntity entity = this.idoCheckOutPooledEntity();
	java.util.Collection ids = ((AccountBMPBean)entity).ejbFindByAssessmentRound(p0);
	this.idoCheckInPooledEntity(entity);
	return this.getEntityCollectionForPrimaryKeys(ids);
}

public java.util.Collection findByAssessmentRound(java.lang.Integer p0,int p1,int p2)throws javax.ejb.FinderException{
	com.idega.data.IDOEntity entity = this.idoCheckOutPooledEntity();
	java.util.Collection ids = ((AccountBMPBean)entity).ejbFindByAssessmentRound(p0,p1,p2);
	this.idoCheckInPooledEntity(entity);
	return this.getEntityCollectionForPrimaryKeys(ids);
}

public java.util.Collection findBySQL(java.lang.String p0)throws javax.ejb.FinderException{
	com.idega.data.IDOEntity entity = this.idoCheckOutPooledEntity();
	java.util.Collection ids = ((AccountBMPBean)entity).ejbFindBySQL(p0);
	this.idoCheckInPooledEntity(entity);
	return this.getEntityCollectionForPrimaryKeys(ids);
}

public java.util.Collection findBySearch(java.lang.String p0,java.lang.String p1,java.lang.String p2,java.lang.String p3,int p5)throws javax.ejb.FinderException{
	com.idega.data.IDOEntity entity = this.idoCheckOutPooledEntity();
	java.util.Collection ids = ((AccountBMPBean)entity).ejbFindBySearch(p0,p1,p2,p3,p5);
	this.idoCheckInPooledEntity(entity);
	return this.getEntityCollectionForPrimaryKeys(ids);
}

 public Account findByPrimaryKey(Object pk) throws javax.ejb.FinderException{
  return (Account) super.findByPrimaryKeyIDO(pk);
 }


public int countByAssessmentRound(java.lang.Integer p0)throws com.idega.data.IDOException{
	com.idega.data.IDOEntity entity = this.idoCheckOutPooledEntity();
	int theReturn = ((AccountBMPBean)entity).ejbHomeCountByAssessmentRound(p0);
	this.idoCheckInPooledEntity(entity);
	return theReturn;
}

public int countByTypeAndCategory(java.lang.String p0,java.lang.Integer p1)throws com.idega.data.IDOException{
	com.idega.data.IDOEntity entity = this.idoCheckOutPooledEntity();
	int theReturn = ((AccountBMPBean)entity).ejbHomeCountByTypeAndCategory(p0,p1);
	this.idoCheckInPooledEntity(entity);
	return theReturn;
}


}