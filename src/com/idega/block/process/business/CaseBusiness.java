package com.idega.block.process.business;

import java.rmi.RemoteException;
import javax.ejb.*;

import com.idega.block.process.data.CaseStatus;

public interface CaseBusiness extends com.idega.business.IBOService
{
 public java.util.Collection getAllCasesForUser(com.idega.user.data.User p0,com.idega.block.process.data.CaseCode p1)throws javax.ejb.FinderException,java.rmi.RemoteException, java.rmi.RemoteException;
 public void changeCaseStatus(com.idega.block.process.data.Case p0,java.lang.String p1,com.idega.user.data.User p2)throws java.rmi.RemoteException, java.rmi.RemoteException;
 public com.idega.block.process.data.Case createSubCase(com.idega.block.process.data.Case p0,com.idega.block.process.data.CaseCode p1)throws javax.ejb.CreateException,java.rmi.RemoteException, java.rmi.RemoteException;
 public java.util.Collection getAllActiveCasesForUser(com.idega.user.data.User p0,java.lang.String p1,java.lang.String p2)throws javax.ejb.FinderException,java.rmi.RemoteException, java.rmi.RemoteException;
 public com.idega.block.process.data.CaseStatus getCaseStatusCancelled()throws java.rmi.RemoteException, java.rmi.RemoteException;
 public java.lang.String getLocalizedCaseStatusDescription(com.idega.block.process.data.CaseStatus p0,java.util.Locale p1) throws java.rmi.RemoteException;
 public com.idega.block.process.data.CaseStatus getCaseStatusDenied()throws java.rmi.RemoteException, java.rmi.RemoteException;
 public com.idega.block.process.data.CaseCode getCaseCode(java.lang.String p0)throws java.rmi.RemoteException,javax.ejb.FinderException, java.rmi.RemoteException;
 public com.idega.block.process.data.CaseStatus getCaseStatusReview()throws java.rmi.RemoteException, java.rmi.RemoteException;
 public java.util.Collection getAllCasesForUser(com.idega.user.data.User p0,com.idega.block.process.data.CaseCode p1,com.idega.block.process.data.CaseStatus p2)throws javax.ejb.FinderException,java.rmi.RemoteException, java.rmi.RemoteException;
 public java.util.Collection getAllCasesForUserExceptCodes(com.idega.user.data.User p0,com.idega.block.process.data.CaseCode[] p1)throws javax.ejb.FinderException,java.rmi.RemoteException, java.rmi.RemoteException;
 public com.idega.block.process.data.CaseStatus getCaseStatusOpen()throws java.rmi.RemoteException, java.rmi.RemoteException;
 public com.idega.block.process.business.CaseBusiness getCaseBusiness(com.idega.block.process.data.CaseCode p0) throws java.rmi.RemoteException;
 public java.util.Collection getAllCasesForUser(com.idega.user.data.User p0)throws javax.ejb.FinderException,java.rmi.RemoteException, java.rmi.RemoteException;
 public com.idega.block.process.data.Case createSubCase(com.idega.block.process.data.Case p0)throws javax.ejb.CreateException,java.rmi.RemoteException, java.rmi.RemoteException;
 public com.idega.block.process.business.CaseBusiness getCaseBusiness(java.lang.String p0) throws java.rmi.RemoteException;
 public java.util.Collection getAllCasesForUser(com.idega.user.data.User p0,java.lang.String p1,java.lang.String p2)throws javax.ejb.FinderException,java.rmi.RemoteException, java.rmi.RemoteException;
 public com.idega.block.process.data.CaseStatus getCaseStatusGranted()throws java.rmi.RemoteException, java.rmi.RemoteException;
 public com.idega.block.process.data.Case createCase(int p0,java.lang.String p1)throws javax.ejb.CreateException,java.rmi.RemoteException, java.rmi.RemoteException;
 public java.lang.String getLocalizedCaseDescription(com.idega.block.process.data.Case p0,java.util.Locale p1) throws java.rmi.RemoteException;
 public java.util.Collection getAllActiveCasesForUser(com.idega.user.data.User p0,com.idega.block.process.data.CaseCode p1)throws javax.ejb.FinderException,java.rmi.RemoteException, java.rmi.RemoteException;
 public void changeCaseStatus(int p0,java.lang.String p1,com.idega.user.data.User p2)throws javax.ejb.FinderException,java.rmi.RemoteException, java.rmi.RemoteException;
 public java.util.Collection getAllCasesForUser(com.idega.user.data.User p0,java.lang.String p1)throws javax.ejb.FinderException,java.rmi.RemoteException, java.rmi.RemoteException;
 public java.lang.String getBundleIdentifier() throws java.rmi.RemoteException;
 public com.idega.block.process.data.Case getCase(int p0)throws java.rmi.RemoteException,javax.ejb.FinderException, java.rmi.RemoteException;
 public java.util.Collection getAllActiveCasesForUser(com.idega.user.data.User p0,com.idega.block.process.data.CaseCode p1,com.idega.block.process.data.CaseStatus p2)throws javax.ejb.FinderException,java.rmi.RemoteException, java.rmi.RemoteException;
 public com.idega.block.process.data.CaseStatus getCaseStatus(java.lang.String p0)throws java.rmi.RemoteException, java.rmi.RemoteException;
 public java.util.Collection getAllActiveCasesForUser(com.idega.user.data.User p0)throws javax.ejb.FinderException,java.rmi.RemoteException, java.rmi.RemoteException;
 public com.idega.block.process.data.Case createCase(com.idega.user.data.User p0,com.idega.block.process.data.CaseCode p1)throws javax.ejb.CreateException,java.rmi.RemoteException, java.rmi.RemoteException;
 public java.util.Collection getAllActiveCasesForUser(com.idega.user.data.User p0,java.lang.String p1)throws javax.ejb.FinderException,java.rmi.RemoteException, java.rmi.RemoteException;
	public CaseStatus getCaseStatusInactive() throws RemoteException;
}
