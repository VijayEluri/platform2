package com.idega.block.process.data;


public class CaseHomeImpl extends com.idega.data.IDOFactory implements CaseHome
{
 protected Class getEntityInterfaceClass(){
  return Case.class;
 }


 public Case create() throws javax.ejb.CreateException{
  return (Case) super.createIDO();
 }


public java.util.Collection findAllCasesByGroup(com.idega.user.data.Group p0)throws javax.ejb.FinderException{
	com.idega.data.IDOEntity entity = this.idoCheckOutPooledEntity();
	java.util.Collection ids = ((CaseBMPBean)entity).ejbFindAllCasesByGroup(p0);
	this.idoCheckInPooledEntity(entity);
	return this.getEntityCollectionForPrimaryKeys(ids);
}

public java.util.Collection findAllCasesByUser(com.idega.user.data.User p0,java.lang.String p1,java.lang.String p2)throws javax.ejb.FinderException{
	com.idega.data.IDOEntity entity = this.idoCheckOutPooledEntity();
	java.util.Collection ids = ((CaseBMPBean)entity).ejbFindAllCasesByUser(p0,p1,p2);
	this.idoCheckInPooledEntity(entity);
	return this.getEntityCollectionForPrimaryKeys(ids);
}

public java.util.Collection findAllCasesByUser(com.idega.user.data.User p0)throws javax.ejb.FinderException{
	com.idega.data.IDOEntity entity = this.idoCheckOutPooledEntity();
	java.util.Collection ids = ((CaseBMPBean)entity).ejbFindAllCasesByUser(p0);
	this.idoCheckInPooledEntity(entity);
	return this.getEntityCollectionForPrimaryKeys(ids);
}

public java.util.Collection findAllCasesByUser(com.idega.user.data.User p0,com.idega.block.process.data.CaseCode p1)throws javax.ejb.FinderException{
	com.idega.data.IDOEntity entity = this.idoCheckOutPooledEntity();
	java.util.Collection ids = ((CaseBMPBean)entity).ejbFindAllCasesByUser(p0,p1);
	this.idoCheckInPooledEntity(entity);
	return this.getEntityCollectionForPrimaryKeys(ids);
}

public java.util.Collection findAllCasesByUser(com.idega.user.data.User p0,com.idega.block.process.data.CaseCode p1,com.idega.block.process.data.CaseStatus p2)throws javax.ejb.FinderException{
	com.idega.data.IDOEntity entity = this.idoCheckOutPooledEntity();
	java.util.Collection ids = ((CaseBMPBean)entity).ejbFindAllCasesByUser(p0,p1,p2);
	this.idoCheckInPooledEntity(entity);
	return this.getEntityCollectionForPrimaryKeys(ids);
}

public java.util.Collection findAllCasesByUser(com.idega.user.data.User p0,java.lang.String p1)throws javax.ejb.FinderException{
	com.idega.data.IDOEntity entity = this.idoCheckOutPooledEntity();
	java.util.Collection ids = ((CaseBMPBean)entity).ejbFindAllCasesByUser(p0,p1);
	this.idoCheckInPooledEntity(entity);
	return this.getEntityCollectionForPrimaryKeys(ids);
}

public java.util.Collection findAllCasesForGroupExceptCodes(com.idega.user.data.Group p0,com.idega.block.process.data.CaseCode[] p1)throws javax.ejb.FinderException{
	com.idega.data.IDOEntity entity = this.idoCheckOutPooledEntity();
	java.util.Collection ids = ((CaseBMPBean)entity).ejbFindAllCasesForGroupExceptCodes(p0,p1);
	this.idoCheckInPooledEntity(entity);
	return this.getEntityCollectionForPrimaryKeys(ids);
}

public java.util.Collection findAllCasesForGroupsAndUserExceptCodes(com.idega.user.data.User p0,java.util.Collection p1,com.idega.block.process.data.CaseCode[] p2,int p3,int p4)throws javax.ejb.FinderException{
	com.idega.data.IDOEntity entity = this.idoCheckOutPooledEntity();
	java.util.Collection ids = ((CaseBMPBean)entity).ejbFindAllCasesForGroupsAndUserExceptCodes(p0,p1,p2,p3,p4);
	this.idoCheckInPooledEntity(entity);
	return this.getEntityCollectionForPrimaryKeys(ids);
}

public java.util.Collection findAllCasesForUserExceptCodes(com.idega.user.data.User p0,com.idega.block.process.data.CaseCode[] p1,int p2,int p3)throws javax.ejb.FinderException{
	com.idega.data.IDOEntity entity = this.idoCheckOutPooledEntity();
	java.util.Collection ids = ((CaseBMPBean)entity).ejbFindAllCasesForUserExceptCodes(p0,p1,p2,p3);
	this.idoCheckInPooledEntity(entity);
	return this.getEntityCollectionForPrimaryKeys(ids);
}

public java.util.Collection findSubCasesUnder(com.idega.block.process.data.Case p0)throws javax.ejb.FinderException{
	com.idega.data.IDOEntity entity = this.idoCheckOutPooledEntity();
	java.util.Collection ids = ((CaseBMPBean)entity).ejbFindSubCasesUnder(p0);
	this.idoCheckInPooledEntity(entity);
	return this.getEntityCollectionForPrimaryKeys(ids);
}

 public Case findByPrimaryKey(Object pk) throws javax.ejb.FinderException{
  return (Case) super.findByPrimaryKeyIDO(pk);
 }


public int countSubCasesUnder(com.idega.block.process.data.Case p0){
	com.idega.data.IDOEntity entity = this.idoCheckOutPooledEntity();
	int theReturn = ((CaseBMPBean)entity).ejbHomeCountSubCasesUnder(p0);
	this.idoCheckInPooledEntity(entity);
	return theReturn;
}

public java.lang.String getCaseStatusCancelled(){
	com.idega.data.IDOEntity entity = this.idoCheckOutPooledEntity();
	java.lang.String theReturn = ((CaseBMPBean)entity).ejbHomeGetCaseStatusCancelled();
	this.idoCheckInPooledEntity(entity);
	return theReturn;
}

public java.lang.String getCaseStatusContract(){
	com.idega.data.IDOEntity entity = this.idoCheckOutPooledEntity();
	java.lang.String theReturn = ((CaseBMPBean)entity).ejbHomeGetCaseStatusContract();
	this.idoCheckInPooledEntity(entity);
	return theReturn;
}

public java.lang.String getCaseStatusDenied(){
	com.idega.data.IDOEntity entity = this.idoCheckOutPooledEntity();
	java.lang.String theReturn = ((CaseBMPBean)entity).ejbHomeGetCaseStatusDenied();
	this.idoCheckInPooledEntity(entity);
	return theReturn;
}

public java.lang.String getCaseStatusError(){
	com.idega.data.IDOEntity entity = this.idoCheckOutPooledEntity();
	java.lang.String theReturn = ((CaseBMPBean)entity).ejbHomeGetCaseStatusError();
	this.idoCheckInPooledEntity(entity);
	return theReturn;
}

public java.lang.String getCaseStatusGranted(){
	com.idega.data.IDOEntity entity = this.idoCheckOutPooledEntity();
	java.lang.String theReturn = ((CaseBMPBean)entity).ejbHomeGetCaseStatusGranted();
	this.idoCheckInPooledEntity(entity);
	return theReturn;
}

public java.lang.String getCaseStatusInactive(){
	com.idega.data.IDOEntity entity = this.idoCheckOutPooledEntity();
	java.lang.String theReturn = ((CaseBMPBean)entity).ejbHomeGetCaseStatusInactive();
	this.idoCheckInPooledEntity(entity);
	return theReturn;
}

public java.lang.String getCaseStatusOpen(){
	com.idega.data.IDOEntity entity = this.idoCheckOutPooledEntity();
	java.lang.String theReturn = ((CaseBMPBean)entity).ejbHomeGetCaseStatusOpen();
	this.idoCheckInPooledEntity(entity);
	return theReturn;
}

public java.lang.String getCaseStatusPreliminary(){
	com.idega.data.IDOEntity entity = this.idoCheckOutPooledEntity();
	java.lang.String theReturn = ((CaseBMPBean)entity).ejbHomeGetCaseStatusPreliminary();
	this.idoCheckInPooledEntity(entity);
	return theReturn;
}

public java.lang.String getCaseStatusReady(){
	com.idega.data.IDOEntity entity = this.idoCheckOutPooledEntity();
	java.lang.String theReturn = ((CaseBMPBean)entity).ejbHomeGetCaseStatusReady();
	this.idoCheckInPooledEntity(entity);
	return theReturn;
}

public java.lang.String getCaseStatusRedeem(){
	com.idega.data.IDOEntity entity = this.idoCheckOutPooledEntity();
	java.lang.String theReturn = ((CaseBMPBean)entity).ejbHomeGetCaseStatusRedeem();
	this.idoCheckInPooledEntity(entity);
	return theReturn;
}

public java.lang.String getCaseStatusReview(){
	com.idega.data.IDOEntity entity = this.idoCheckOutPooledEntity();
	java.lang.String theReturn = ((CaseBMPBean)entity).ejbHomeGetCaseStatusReview();
	this.idoCheckInPooledEntity(entity);
	return theReturn;
}

public int getNumberOfCasesByGroupsOrUserExceptCodes(com.idega.user.data.User p0,java.util.Collection p1,com.idega.block.process.data.CaseCode[] p2)throws com.idega.data.IDOException{
	com.idega.data.IDOEntity entity = this.idoCheckOutPooledEntity();
	int theReturn = ((CaseBMPBean)entity).ejbHomeGetNumberOfCasesByGroupsOrUserExceptCodes(p0,p1,p2);
	this.idoCheckInPooledEntity(entity);
	return theReturn;
}

public int getNumberOfCasesForUserExceptCodes(com.idega.user.data.User p0,com.idega.block.process.data.CaseCode[] p1)throws com.idega.data.IDOException{
	com.idega.data.IDOEntity entity = this.idoCheckOutPooledEntity();
	int theReturn = ((CaseBMPBean)entity).ejbHomeGetNumberOfCasesForUserExceptCodes(p0,p1);
	this.idoCheckInPooledEntity(entity);
	return theReturn;
}


}