package com.idega.user.data;

import java.rmi.RemoteException;
import java.util.Collection;

import javax.ejb.FinderException;

import com.idega.data.IDOLookupException;
import com.idega.util.IWTimestamp;


public interface UserHome extends com.idega.data.IDOHome
{
 public User create() throws javax.ejb.CreateException;
 public User findByPrimaryKey(Object pk) throws javax.ejb.FinderException;
 public User findByPersonalID(java.lang.String p0)throws javax.ejb.FinderException;
 public User findByPartOfPersonalIDAndFirstName(java.lang.String p0,java.lang.String p1)throws javax.ejb.FinderException;
 public User findUserForUserGroup(int p0)throws javax.ejb.FinderException;
 public java.util.Collection findAllUsers()throws javax.ejb.FinderException;
 public java.util.Collection findByNames(java.lang.String p0,java.lang.String p1,java.lang.String p2)throws javax.ejb.FinderException;
 public java.util.Collection findUsersForUserRepresentativeGroups(java.util.Collection p0)throws javax.ejb.FinderException;
 public User findUserForUserRepresentativeGroup(com.idega.user.data.Group p0)throws javax.ejb.FinderException;
 public java.util.Collection findUsersInPrimaryGroup(com.idega.user.data.Group p0)throws javax.ejb.FinderException,java.rmi.RemoteException;
 public java.util.Collection findUsersBySearchCondition(java.lang.String p0, boolean orderLastFirst)throws javax.ejb.FinderException,java.rmi.RemoteException;
 public java.util.Collection findUsersBySearchConditionAndMaxAge(java.lang.String p0, boolean orderLastFirst, int maxAge)throws javax.ejb.FinderException,java.rmi.RemoteException;
 public Collection findUsersBySearchCondition(String condition, String[] userIds, boolean orderLastFirst) throws FinderException, RemoteException;
 public User findUserForUserGroup(com.idega.user.data.Group p0)throws javax.ejb.FinderException;
 public User findUserFromEmail(java.lang.String p0)throws javax.ejb.FinderException,java.rmi.RemoteException;
 public java.util.Collection findUsers(java.lang.String[] userIDs)throws javax.ejb.FinderException;
 public java.util.Collection findAllUsersOrderedByFirstName()throws javax.ejb.FinderException;
 public java.util.Collection findUsersByYearOfBirth (int minYear, int maxYear)  throws  FinderException,RemoteException;
 public int getUserCount()throws com.idega.data.IDOException;
 public java.lang.String getGroupType();
 public java.util.Collection findUsersInQuery(com.idega.data.IDOQuery query)throws javax.ejb.FinderException;
 public Collection findUsersByConditions(String userName, String personalId, String streetName, String groupName, int genderId, int statusId, int startAge, int endAge, String[] allowedGroupIds, String[] allowedUserIds, boolean useAnd, boolean orderLastFirst) throws FinderException, RemoteException;
 public Collection findUsersByConditions(String firstName, String middleName, String lastName, String personalId, String streetName, String groupName, int genderId, int statusId, int startAge, int endAge, String[] allowedGroupIds, String[] allowedUserIds, boolean useAnd, boolean orderLastFirst) throws FinderException, RemoteException;
 public Collection findUsersByMetaData(String key, String value) throws FinderException;
 public java.util.Collection findUsersByCreationTime(IWTimestamp firstCreationTime, IWTimestamp lastCreationTime) throws FinderException, IDOLookupException;
 public java.util.Collection findByDateOfBirthAndGroupRelationInitiationTimeAndStatus(java.sql.Date firstBirthDateInPeriode, java.sql.Date lastBirthDateInPeriode, Group relatedGroup, java.sql.Timestamp firstInitiationDateInPeriode, java.sql.Timestamp lastInitiationDateInPeriode, String[] relationStatus) throws IDOLookupException, FinderException;
 public java.util.Collection findByGroupRelationInitiationTimeAndStatus(Group relatedGroup, java.sql.Timestamp firstInitiationDateInPeriode, java.sql.Timestamp lastInitiationDateInPeriode, String[] relationStatus) throws IDOLookupException, FinderException;
 public java.util.Collection findUsersWithContract() throws IDOLookupException, FinderException;

}