/*
 * $Id: CaseBMPBean.java,v 1.30 2003/10/13 18:21:20 roar Exp $
 *
 * Copyright (C) 2002 Idega hf. All Rights Reserved.
 *
 * This software is the proprietary information of Idega hf.
 * Use is subject to license terms.
 *
 */
package com.idega.block.process.data;
import java.util.Collection;
import java.util.Iterator;
import java.util.Locale;
import java.sql.Timestamp;
import javax.ejb.*;
import com.idega.util.IWTimestamp;
//import com.idega.core.user.data.User;
import com.idega.user.data.Group;
import com.idega.user.data.User;
import com.idega.data.*;
import com.idega.core.data.ICTreeNode;
/**
 *
 * @author <a href="mailto:tryggvi@idega.is">Tryggvi Larusson</a>
 * @version 1.0
 */
public final class CaseBMPBean extends com.idega.data.GenericEntity implements Case, com.idega.core.data.ICTreeNode
{
	public static final String TABLE_NAME = "PROC_CASE";
	public static final String COLUMN_CASE_CODE = "CASE_CODE";
	public static final String COLUMN_CASE_STATUS = "CASE_STATUS";
	static final String COLUMN_CREATED = "CREATED";
	static final String COLUMN_PARENT_CASE = "PARENT_CASE_ID";
	static final String COLUMN_USER = "USER_ID";
	static final String COLUMN_HANDLER = "HANDLER_GROUP_ID";
	static final String PK_COLUMN = TABLE_NAME + "_ID";
	
	static final String CASE_STATUS_OPEN_KEY = "UBEH";
	static final String CASE_STATUS_INACTIVE_KEY = "TYST";
	static final String CASE_STATUS_GRANTED_KEY = "BVJD";
	static final String CASE_STATUS_DENIED_KEY = "AVSL";
	static final String CASE_STATUS_REVIEW_KEY = "OMPR";
	static final String CASE_STATUS_CANCELLED_KEY = "UPPS";
	static final String CASE_STATUS_PRELIMINARY_KEY = "PREL";
	static final String CASE_STATUS_CONTRACT_KEY = "KOUT";
	static final String CASE_STATUS_READY_KEY = "KLAR";
	static final String CASE_STATUS_REDEEM_KEY = "CHIN";
	static final String CASE_STATUS_ERROR = "ERRR";
	
	public void initializeAttributes()
	{
		addAttribute(getIDColumnName());
		addAttribute(COLUMN_CASE_CODE, "Case Code", true, true, String.class, 7, super.MANY_TO_ONE, CaseCode.class);
		addAttribute(COLUMN_CASE_STATUS, "Case status", true, true, String.class, 4, super.MANY_TO_ONE, CaseStatus.class);
		addAttribute(COLUMN_CREATED, "Created when", Timestamp.class);
		addAttribute(COLUMN_PARENT_CASE, "Parent case", true, true, Integer.class, super.MANY_TO_ONE, Case.class);
		addManyToOneRelationship(COLUMN_USER, "Owner", User.class);
		addManyToOneRelationship(COLUMN_HANDLER, "Handler Group/User", Group.class);
	}
	public String getIDColumnName()
	{
		return PK_COLUMN;
	}
	public String getEntityName()
	{
		return (TABLE_NAME);
	}
	protected boolean doInsertInCreate()
	{
		return true;
	}
	/*public void insertStartData()
	{
		try
		{
			//CaseHome chome = (CaseHome)IDOLookup.getHome(Case.class);
			CaseCodeHome cchome = (CaseCodeHome) IDOLookup.getHome(CaseCode.class);
			CaseStatusHome cshome = (CaseStatusHome) IDOLookup.getHome(CaseStatus.class);
			CaseCode code = cchome.create();
			code.setCode("GARENDE");
			code.setDescription("General Case");
			code.store();
			CaseStatus status = cshome.create();
			status.setStatus("UBEH");
			status.setDescription("Open");
			status.store();
			status.setAssociatedCaseCode(code);
			status.store();
			status = cshome.create();
			status.setStatus("TYST");
			status.setDescription("Inactive");
			status.store();
			status.setAssociatedCaseCode(code);
			status.store();
			status = cshome.create();
			status.setStatus("BVJD");
			status.setDescription("Granted");
			status.store();
			status.setAssociatedCaseCode(code);
			status.store();
			status = cshome.create();
			status.setStatus("AVSL");
			status.setDescription("Denied");
			status.store();
			status.setAssociatedCaseCode(code);
			status.store();
			status = cshome.create();
			status.setStatus("OMPR");
			status.setDescription("Review");
			status.store();
			status.setAssociatedCaseCode(code);
			status.store();
			status = cshome.create();
			status.setStatus("KOUT");
			status.setDescription("Contract sent");
			status.store();
			status.setAssociatedCaseCode(code);
			status.store();
			status = cshome.create();
			status.setStatus("UPPS");
			status.setDescription("Cancelled");
			status.store();
			status.setAssociatedCaseCode(code);
			status.store();
			status = cshome.create();
			status.setStatus("PREL");
			status.setDescription("Preliminary Accepted");
			status.store();
			status.setAssociatedCaseCode(code);
			status.store();
			//      status = cshome.create();
			//      status.setStatus("PREL");
			//      status.setDescription("Preliminary Accepted in school");
			//      status.store();
			//      status.setAssociatedCaseCode(code);
			//      status.store();
			//
			//
			//      status = cshome.create();
			//      status.setStatus("PLAC");
			//      status.setDescription("Accepted and placed in school group");
			//      status.store();
			//      status.setAssociatedCaseCode(code);
			//      status.store();
			//
		}
		catch (Exception e)
		{
			System.err.println("Error inserting start data for com.idega.block.process.Case");
			e.printStackTrace();
		}
	}*/
	public void setDefaultValues()
	{
		//System.out.println("Case : Calling setDefaultValues()");
		setCreated(IWTimestamp.getTimestampRightNow());
	}
	protected CaseHome getCaseHome()
	{
		return (CaseHome) this.getEJBLocalHome();
	}
	public void setCode(String caseCode)
	{
		setColumn(this.COLUMN_CASE_CODE, caseCode);
	}
	public String getCode()
	{
		return (this.getStringColumnValue(COLUMN_CASE_CODE));
	}
	public void setCaseCode(CaseCode caseCode)
	{
		setColumn(this.COLUMN_CASE_CODE, caseCode);
	}
	public CaseCode getCaseCode()
	{
		return (CaseCode) (this.getColumnValue(COLUMN_CASE_CODE));
	}
	public void setCaseStatus(CaseStatus status)
	{
		setColumn(this.COLUMN_CASE_STATUS, status);
	}
	public CaseStatus getCaseStatus()
	{
		return (CaseStatus) (this.getColumnValue(COLUMN_CASE_STATUS));
	}
	public void setStatus(String status)
	{
		setColumn(this.COLUMN_CASE_STATUS, status);
	}
	public String getStatus()
	{
		return (this.getStringColumnValue(COLUMN_CASE_STATUS));
	}
	public void setCreated(Timestamp statusChanged)
	{
		setColumn(this.COLUMN_CREATED, statusChanged);
	}
	public Timestamp getCreated()
	{
		return ((Timestamp) getColumnValue(COLUMN_CREATED));
	}
	public void setParentCase(Case theCase)
	{
		//throw new java.lang.UnsupportedOperationException("setParentCase() not implemented yet");
		this.setColumn(this.COLUMN_PARENT_CASE, theCase);
	}
	public Case getParentCase()
	{
		//return (Case)super.getParentNode();
		return (Case) getColumnValue(this.COLUMN_PARENT_CASE);
	}
	public void setOwner(User owner)
	{
		super.setColumn(COLUMN_USER, owner);
	}
	public Group getHandler()
	{
		return (Group) this.getColumnValue(this.COLUMN_HANDLER);
	}
	public int getHandlerId()
	{
		return this.getIntColumnValue(this.COLUMN_HANDLER);
	}
	
	public void setHandler(Group handler)
	{
		super.setColumn(COLUMN_HANDLER, handler);
	}
	public void setHandler(int handlerGroupID)
	{
		super.setColumn(COLUMN_HANDLER, handlerGroupID);
	}
	public User getOwner()
	{
		return (User) this.getColumnValue(this.COLUMN_USER);
	}
	
	
	public ICTreeNode getParentNode()
	{
		return this.getParentCase();
	}
	public ICTreeNode getChildAtIndex(int childIndex)
	{
		try
		{
			return this.getCaseHome().findByPrimaryKey(new Integer(childIndex));
		}
		catch (Exception e)
		{
			throw new EJBException(e.getMessage());
		}
	}
	public int getChildCount()
	{
		try
		{
			return this.getCaseHome().countSubCasesUnder(this);
		}
		catch (Exception e)
		{
			throw new EJBException(e.getMessage());
		}
	}
	public Iterator getChildren()
	{
		try
		{
			return this.getCaseHome().findSubCasesUnder(this).iterator();
		}
		catch (Exception e)
		{
			throw new EJBException(e.getMessage());
		}
	}
	public int getSiblingCount()
	{
		try
		{
			return this.getParentCase().getChildCount();
		}
		catch (Exception e)
		{
			throw new EJBException(e.getMessage());
		}
	}
	/**
	 * Gets the query for finding all the cases for a user ordered in chronological order
	 */
	protected IDOQuery idoQueryGetAllCasesByUserOrdered(User user)
	{
		try{
			IDOQuery query = idoQueryGetAllCasesByUser(user);
			query.appendOrderBy(COLUMN_CREATED);
			return query;
		}
		catch(Exception e){
			throw new IDORuntimeException(e,this);	
		}
	}
	
	/**
	 * Gets the query for finding all the cases for a group ordered in chronological order
	 */
	protected IDOQuery idoQueryGetAllCasesByGroupOrdered(Group group)
	{
		try{
			IDOQuery query = idoQueryGetAllCasesByGroup(group);
			query.appendOrderBy(COLUMN_CREATED);
			return query;
		}
		catch(Exception e){
			throw new IDORuntimeException(e,this);	
		}
	}
		
		

	/**
	 * Gets all the cases of all casetypes for a user and orders in chronological order
	 */
	public Collection ejbFindAllCasesByUser(User user) throws FinderException
	{
		return idoFindPKsByQuery(idoQueryGetAllCasesByUserOrdered(user));
		/*
		return (Collection) super.idoFindPKsBySQL(
			"select * from " 
			+ this.TABLE_NAME 
			+ " where " 
			+ this.COLUMN_USER
			+ "=" 
			+ user.getPrimaryKey().toString()
			+ " order by "
			+ COLUMN_CREATED);
		*/
	}
	
	/**
	 * Gets all the cases of all casetypes for a group and orders in chronological order
	 */	
	public Collection ejbFindAllCasesByGroup(Group group) throws FinderException 
	{
		return idoFindPKsByQuery(idoQueryGetAllCasesByGroupOrdered(group));
	}
			
	/**
	 * Gets all the cases for a user with a specified caseCode and orders in chronological order
	 */
	public Collection ejbFindAllCasesByUser(User user, CaseCode caseCode) throws FinderException
	{
		return ejbFindAllCasesByUser(user, caseCode.getCode());
	}
	/**
	 * Gets the query for finding all the cases for a user with a specified caseCode and orders in chronological order
	 */
	protected IDOQuery idoQueryGetAllCasesByUser(User user, String caseCode)
	{
		try{
			IDOQuery query = idoQueryGetAllCasesByUser(user);
			query.appendAndEqualsQuoted(COLUMN_CASE_CODE,caseCode);
			query.appendOrderBy(COLUMN_CREATED);
			return query;
		}
		catch(Exception e){
			throw new IDORuntimeException(e,this);	
		}
	}
	/**
	 * Gets all the cases for a user with a specified caseCode and orders in chronological order
	 */
	public Collection ejbFindAllCasesByUser(User user, String caseCode) throws FinderException
	{
		return idoFindPKsByQuery(idoQueryGetAllCasesByUser(user,caseCode));
		/*
		return (Collection) super.idoFindPKsBySQL(
			"select * from "
				+ this.TABLE_NAME
				+ " where "
				+ this.COLUMN_USER
				+ "="
				+ user.getPrimaryKey().toString()
				+ " and "
				+ this.COLUMN_CASE_CODE
				+ "='"
				+ caseCode
				+ "'"
				+ " order by "
				+ COLUMN_CREATED);
		*/
	}

	/**
	 * Gets all the cases for a user with a specified caseStatus and caseCode and orders in chronological order
	 */
	public Collection ejbFindAllCasesByUser(User user, CaseCode caseCode, CaseStatus caseStatus)
		throws FinderException
	{
		return ejbFindAllCasesByUser(user, caseCode.getCode(), caseStatus.getStatus());
	}
	/**
	 * Gets the query for finding all the cases for a user with a specified caseStatus and caseCode and orders in chronological order
	 */
	protected IDOQuery idoQueryGetAllCasesByUser(User user, String caseCode, String caseStatus)
	{
		try{
			IDOQuery query = idoQueryGetAllCasesByUser(user);
			query.appendAndEqualsQuoted(COLUMN_CASE_CODE,caseCode);
			query.appendAndEqualsQuoted(COLUMN_CASE_STATUS,caseStatus);
			query.appendOrderBy(COLUMN_CREATED);
			return query;
		}
		catch(Exception e){
			throw new IDORuntimeException(e,this);	
		}
	}
	/**
	 * Gets all the cases for a user with a specified caseStatus and caseCode and orders in chronological order
	 */
	public Collection ejbFindAllCasesByUser(User user, String caseCode, String caseStatus)
		throws FinderException
	{
		return super.idoFindPKsByQuery(idoQueryGetAllCasesByUser(user,caseCode,caseStatus));
		/*
		return (Collection) super.idoFindPKsBySQL(
			"select * from "
				+ this.TABLE_NAME
				+ " where "
				+ this.COLUMN_USER
				+ "="
				+ user.getPrimaryKey().toString()
				+ " and "
				+ this.COLUMN_CASE_CODE
				+ "='"
				+ caseCode
				+ "'"
				+ " and "
				+ this.COLUMN_CASE_STATUS
				+ "='"
				+ caseStatus
				+ "'"
				+ " order by "
				+ COLUMN_CREATED);*/
	}
	protected IDOQuery idoQueryGetSubCasesUnder(Case theCase)
	{
		try{
			IDOQuery query = idoQueryGetSelect();
			query.appendWhereEqualsQuoted(COLUMN_PARENT_CASE,theCase.getPrimaryKey().toString());
			return query;
		}
		catch(Exception e){
			throw new IDORuntimeException(e,this);	
		}
	}
	
	public Collection ejbFindSubCasesUnder(Case theCase) throws FinderException
	{
		return (Collection) super.idoFindPKsByQuery(idoQueryGetSubCasesUnder(theCase));
	}
	
	protected IDOQuery idoQueryGetCountSubCasesUnder(Case theCase)
	{
		try{
			IDOQuery query = idoQueryGetSelectCount();
			query.appendWhereEqualsQuoted(COLUMN_PARENT_CASE,theCase.getPrimaryKey().toString());
			return query;
		}
		catch(Exception e){
			throw new IDORuntimeException(e,this);	
		}
	}
	
	public int ejbHomeCountSubCasesUnder(Case theCase)
	{
		try
		{
			return super.getNumberOfRecords(idoQueryGetCountSubCasesUnder(theCase).toString());
		}
		catch (java.sql.SQLException sqle)
		{
			throw new EJBException(sqle.getMessage());
		}
	}
	public int getNodeID()
	{
		return this.getID();
	}
	public String getNodeName()
	{
		return getName();
	}
	public String getNodeName(Locale locale)
	{
		return getNodeName();
	}
	public boolean isLeaf()
	{
		return (this.getChildCount() == 0);
	}
	public int getIndex(ICTreeNode node)
	{
		return node.getNodeID();
	}
	public boolean getAllowsChildren()
	{
		return true;
	}
	/**
	 * Returns the cASE_STATUS_CANCELLED_KEY.
	 * @return String
	 */
	public String ejbHomeGetCaseStatusCancelled()
	{
		return CASE_STATUS_CANCELLED_KEY;
	} /**
	 * Returns the cASE_STATUS_DENIED_KEY.
	 * @return String
	 */
	public String ejbHomeGetCaseStatusDenied()
	{
		return CASE_STATUS_DENIED_KEY;
	}
	/**
	 * Returns the cASE_STATUS_GRANTED_KEY.
	 * @return String
	 */
	public String ejbHomeGetCaseStatusGranted()
	{
		return CASE_STATUS_GRANTED_KEY;
	}
	/**
	 * Returns the cASE_STATUS_INACTIVE_KEY.
	 * @return String
	 */
	public String ejbHomeGetCaseStatusInactive()
	{
		return CASE_STATUS_INACTIVE_KEY;
	}
	/**
	 * Returns the cASE_STATUS_OPEN_KEY.
	 * @return String
	 */
	public String ejbHomeGetCaseStatusOpen()
	{
		return CASE_STATUS_OPEN_KEY;
	}
	/**
	 * Returns the cASE_STATUS_REVIEW_KEY.
	 * @return String
	 */
	public String ejbHomeGetCaseStatusReview()
	{
		return CASE_STATUS_REVIEW_KEY;
	}

	/**
	 * Returns the CASE_STATUS_PRELIMINARY_KEY.
	 * @return String
	 */
	public String ejbHomeGetCaseStatusPreliminary()
	{
		return CASE_STATUS_PRELIMINARY_KEY;
	}

	/**
	 * Returns the CASE_STATUS_CONTRACT_KEY.
	 * @return String
	 */
	public String ejbHomeGetCaseStatusContract()
	{
		return CASE_STATUS_CONTRACT_KEY;
	}

	/**
	 * Returns the CASE_STATUS_READY_KEY.
	 * @return String
	 */
	public String ejbHomeGetCaseStatusReady()
	{
		return CASE_STATUS_READY_KEY;
	}

	/**
	 * Returns the CASE_STATUS_REDEEM_KEY.
	 * @return String
	 */
	public String ejbHomeGetCaseStatusRedeem()
	{
		return CASE_STATUS_REDEEM_KEY;
	}
	
	/**
		 * Returns the CASE_STATUS_ERROR.
		 * @return String
		 */
		public String ejbHomeGetCaseStatusError()
		{
			return CASE_STATUS_ERROR;
		}

	protected IDOQuery idoQueryGetAllCasesForUserExceptCodes(User user,CaseCode[] codes)
	{
		String notInClause = getIDOUtil().convertArrayToCommaseparatedString(codes);
		IDOQuery query = idoQueryGetAllCasesByUser(user);
		query.appendAnd();
		query.append(COLUMN_CASE_CODE);
		query.appendNotIn(notInClause);
		query.appendOrderBy(COLUMN_CREATED);
		return query;
	}
	
	protected IDOQuery idoQueryGetAllCasesForGroupExceptCodes(Group group,CaseCode[] codes)
	{
		String notInClause = getIDOUtil().convertArrayToCommaseparatedString(codes);
		IDOQuery query = idoQueryGetAllCasesByGroup(group);
		query.appendAnd();
		query.append(COLUMN_CASE_CODE);
		query.appendNotIn(notInClause);
		query.appendOrderBy(COLUMN_CREATED);
		return query;
	}
		

	/**
	 * Gets all the Cases for the User except the ones with one of the CaseCode in the codes[] array and orders in chronological order
	 */
	public Collection ejbFindAllCasesForUserExceptCodes(User user,CaseCode[] codes) throws FinderException
	{
		IDOQuery query = idoQueryGetAllCasesForUserExceptCodes(user,codes);
		return super.idoFindPKsByQuery(query);
		/*
		return (Collection) super.idoFindPKsBySQL(
			"select * from "
				+ this.TABLE_NAME
				+ " where "
				+ this.USER
				+ "="
				+ user.getPrimaryKey().toString()
				+ " and "
				+ this.CASE_CODE
				+ " not in ("
				+ notInClause
				+ ") order by "
				+ CREATED
				);
			*/			
	}
	
	/**
	 * Gets all the Cases for the User except the ones with one of the CaseCode in the codes[] array and orders in chronological order
	 */
	public Collection ejbFindAllCasesForGroupExceptCodes(Group group, CaseCode[] codes) throws FinderException
	{
		IDOQuery query = idoQueryGetAllCasesForGroupExceptCodes(group, codes);
		return super.idoFindPKsByQuery(query);
	}
		
	
	
	/**
	 * Gets the query for selecting all cases by user.	 * @param user the cases has to be owned by	 * @return IDOQuery the resulting query.	 */
	protected IDOQuery idoQueryGetAllCasesByUser(User user){
		try{
			IDOQuery query = this.idoQueryGetSelect();
			query.appendWhereEqualsQuoted(COLUMN_USER,user.getPrimaryKey().toString());
			return query;
		}
		catch(Exception e){
			throw new IDORuntimeException(e,this);	
		}
	}
	
	/**
	 * Gets the query for selecting all cases by group.
	 * @param group the cases will be handled by
	 * @return IDOQuery the resulting query.
	 */
	protected IDOQuery idoQueryGetAllCasesByGroup(Group group){
		try{
			IDOQuery query = this.idoQueryGetSelect();
			query.appendWhereEqualsQuoted(COLUMN_HANDLER, group.getPrimaryKey().toString());
			return query;
		}
		catch(Exception e){
			throw new IDORuntimeException(e, this);	
		}
	}
	


}
