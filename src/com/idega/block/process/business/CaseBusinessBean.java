package com.idega.block.process.business;
import java.rmi.RemoteException;
import java.sql.Timestamp;
import java.util.Collection;
import java.util.Locale;

import javax.ejb.CreateException;
import javax.ejb.EJBException;
import javax.ejb.FinderException;

import com.idega.block.process.data.Case;
import com.idega.block.process.data.CaseCode;
import com.idega.block.process.data.CaseCodeHome;
import com.idega.block.process.data.CaseHome;
import com.idega.block.process.data.CaseLog;
import com.idega.block.process.data.CaseLogHome;
import com.idega.block.process.data.CaseStatus;
import com.idega.block.process.data.CaseStatusHome;
import com.idega.business.IBOServiceBean;
import com.idega.core.component.data.ICObject;
import com.idega.data.IDOException;
import com.idega.data.IDOStoreException;
import com.idega.idegaweb.IWBundle;
import com.idega.user.data.Group;
import com.idega.user.data.User;
import com.idega.user.data.UserHome;
import com.idega.util.IWTimestamp;
/**
 * Title:        idegaWeb
 * Description:
 * Copyright:    Copyright (c) 2001
 * Company:      idega software
 * @author <a href="mailto:tryggvi@idega.is">Tryggvi Larusson</a>
 * @version 1.0
 */
public class CaseBusinessBean extends IBOServiceBean implements CaseBusiness
{
	/*private  final String CASE_STATUS_OPEN_KEY = "UBEH";
	private  final String CASE_STATUS_INACTIVE_KEY = "TYST";
	private  final String CASE_STATUS_GRANTED_KEY = "BVJD";
	private  final String CASE_STATUS_DENIED_KEY = "AVSL";
	private  final String CASE_STATUS_REVIEW_KEY = "OMPR";
	private  final String CASE_STATUS_CANCELLED_KEY = "UPPS";*/
	private String CASE_STATUS_OPEN_KEY;
	private String CASE_STATUS_INACTIVE_KEY;
	private String CASE_STATUS_GRANTED_KEY;
	private String CASE_STATUS_DELETED_KEY;
	private String CASE_STATUS_DENIED_KEY;
	private String CASE_STATUS_REVIEW_KEY;
	private String CASE_STATUS_CANCELLED_KEY;
	private String CASE_STATUS_PRELIMINARY_KEY;
	private String CASE_STATUS_CONTRACT_KEY;
	private String CASE_STATUS_READY_KEY;
	private String CASE_STATUS_REDEEM_KEY;
	private String CASE_STATUS_ERROR;
	private String CASE_STATUS_MOVED;
	private String CASE_STATUS_PLACED;
	public CaseBusinessBean()
	{
		try
		{
			CASE_STATUS_OPEN_KEY = this.getCaseHome().getCaseStatusOpen();
			CASE_STATUS_INACTIVE_KEY = this.getCaseHome().getCaseStatusInactive();
			CASE_STATUS_GRANTED_KEY = this.getCaseHome().getCaseStatusGranted();
			CASE_STATUS_DELETED_KEY = this.getCaseHome().getCaseStatusDeleted();
			CASE_STATUS_DENIED_KEY = this.getCaseHome().getCaseStatusDenied();
			CASE_STATUS_REVIEW_KEY = this.getCaseHome().getCaseStatusReview();
			CASE_STATUS_CANCELLED_KEY = this.getCaseHome().getCaseStatusCancelled();
			CASE_STATUS_PRELIMINARY_KEY = this.getCaseHome().getCaseStatusPreliminary();
			CASE_STATUS_CONTRACT_KEY = this.getCaseHome().getCaseStatusContract();
			CASE_STATUS_READY_KEY = this.getCaseHome().getCaseStatusReady();
			CASE_STATUS_REDEEM_KEY = this.getCaseHome().getCaseStatusRedeem();
			CASE_STATUS_ERROR = this.getCaseHome().getCaseStatusError();
			CASE_STATUS_MOVED = this.getCaseHome().getCaseStatusMoved();
			CASE_STATUS_PLACED = this.getCaseHome().getCaseStatusPlaced();
		}
		catch (RemoteException e)
		{
			System.err.println("CaseBusinessBean : Error initializing case codes : Error : " + e.getMessage());
		}
	}
	/**
	 * Returns the correct CaseBusiness implementation instance for the specified case code.<br>
	 * If there is no specified the default (this) is returned;
	 **/
	public CaseBusiness getCaseBusiness(String caseCode)
	{
		try
		{
			CaseCode code = this.getCaseCodeHome().findByPrimaryKey(caseCode);
			return this.getCaseBusiness(code);
		}
		catch (Exception e)
		{
			throw new EJBException(e.getMessage());
		}
	}
	/**
	 * Returns the correct CaseBusiness implementation instance for the specified case code.<br>
	 * If there is no specified the default (this) is returned;
	 **/
	public CaseBusiness getCaseBusiness(CaseCode code)
	{
		/**
		 * @todo: implement
		 */
		try
		{
			ICObject handler = code.getBusinessHandler();
			if (handler != null)
			{
				Class objClass = handler.getObjectClass();
				return (CaseBusiness) this.getServiceInstance(objClass);
			}
			/**
			 *TODO: Remove hardcoding of CaseBusinessBeans
			 */
			try{
				Class c = Class.forName("se.idega.idegaweb.commune.school.business.SchoolChoiceBusiness");
				if(code.getCode().equals("MBSKOLV")){
					return (CaseBusiness) this.getServiceInstance(c);
				}
			}
			catch(ClassNotFoundException e){}
			try{
				Class c = Class.forName("se.idega.idegaweb.commune.childcare.business.ChildCareBusiness");
				if(code.getCode().equals("MBANBOP") || code.getCode().equals("MBFRITV")){
					return (CaseBusiness) this.getServiceInstance(c);
				}
			}
			catch(ClassNotFoundException e){}
			return this;
		}
		catch (ClassNotFoundException cnfe)
		{
			throw new EJBException(cnfe.getMessage());
		}
		catch (RemoteException rme)
		{
			throw new EJBException(rme.getMessage());
		}
	}
	public Case createCase(int userID, String caseCode) throws CreateException, RemoteException
	{
		try
		{
			User user = this.getUserHome().findByPrimaryKey(new Integer(userID));
			CaseCode code = this.getCaseCode(caseCode);
			return createCase(user, code);
		}
		catch (FinderException fe)
		{
			throw new CreateException(fe.getMessage());
		}
	}
	public Case createCase(User user, CaseCode code) throws CreateException, RemoteException
	{
		try
		{
			Case newCase = this.getCaseHome().create();
			newCase.setOwner(user);
			newCase.setCaseCode(code);
			newCase.setCreated(new IWTimestamp().getTimestamp());
			newCase.store();
			return newCase;
		}
		catch (IDOStoreException se)
		{
			throw new CreateException(se.getMessage());
		}
	}
	/**
	 * Creates a new case that is a result of the previous case with the same case code.
	 */
	public Case createSubCase(Case oldCase) throws CreateException, RemoteException
	{
		return createSubCase(oldCase, oldCase.getCaseCode());
	}
	/**
	 * Creates a new case with a specified case code that is a result of the previous case .
	 */
	public Case createSubCase(Case oldCase, CaseCode newCaseCode) throws CreateException, RemoteException
	{
		try
		{
			Case newCase = this.getCaseHome().create();
			newCase.setOwner(oldCase.getOwner());
			newCase.setCaseCode(newCaseCode);
			newCase.setCreated(new IWTimestamp().getTimestamp());
			newCase.store();
			return newCase;
		}
		catch (IDOStoreException se)
		{
			throw new CreateException(se.getMessage());
		}
	}
	/**
	 * Gets all the active Cases for the User
	 */
	public Collection getAllActiveCasesForUser(User user) throws FinderException, RemoteException
	{
		return this.getCaseHome().findAllCasesByUser(user);
	}
	/**
	 * Gets all the active Cases for the User with a specificed code
	 */
	public Collection getAllActiveCasesForUser(User user, CaseCode code) throws FinderException, RemoteException
	{
		return this.getCaseHome().findAllCasesByUser(user, code);
	}
	/**
	 * Gets all the active Cases for the User with a specificed code
	 */
	public Collection getAllActiveCasesForUser(User user, String caseCode) throws FinderException, RemoteException
	{
		return this.getCaseHome().findAllCasesByUser(user, caseCode);
	}
	/**
	 * Gets all the active Cases for the User with a specificed code and status
	 */
	public Collection getAllActiveCasesForUser(User user, CaseCode code, CaseStatus status)
		throws FinderException, RemoteException
	{
		return this.getCaseHome().findAllCasesByUser(user, code, status);
	}
	/**
	 * Gets all the active Cases for the User with a specificed code and status
	 */
	public Collection getAllActiveCasesForUser(User user, String caseCode, String caseStatus)
		throws FinderException, RemoteException
	{
		return this.getCaseHome().findAllCasesByUser(user, caseCode, caseStatus);
	}
	/**
	 * Gets all the Cases for the User
	 */
	public Collection getAllCasesForUser(User user) throws FinderException, RemoteException
	{
		return this.getCaseHome().findAllCasesByUser(user);
	}
	
	/**
	 * Gets all the Cases for the User
	 */
	public Collection getAllCasesForGroup(Group group) throws FinderException, RemoteException
	{
		return this.getCaseHome().findAllCasesByGroup(group);
	}	
	

	
	/**
	 * Gets all the Cases for the User except the ones with one of the CaseCode in the codes[] array.
	 */
	public Collection getAllCasesForUserExceptCodes(User user,CaseCode[] codes, int startingCase, int numberOfCases) throws FinderException, RemoteException
	{
		return this.getCaseHome().findAllCasesForUserExceptCodes(user,codes, startingCase, numberOfCases);
	}
	
	public int getNumberOfCasesForUserExceptCodes(User user, CaseCode[] codes) {
		try {
			return this.getCaseHome().getNumberOfCasesForUserExceptCodes(user, codes);
		}
		catch (RemoteException e) {
			return 0;
		}
		catch (IDOException e) {
			return 0;
		}
	}
		
	/**
	 * Gets all the Cases for the Group except the ones with one of the CaseCode in the codes[] array.
	 */
	public Collection getAllCasesForGroupExceptCodes(Group group,CaseCode[] codes) throws FinderException, RemoteException
	{
		return this.getCaseHome().findAllCasesForGroupExceptCodes(group,codes);
	}
		
	/**
	 * Gets all the Cases for the Group except the ones with one of the CaseCode in the codes[] array.
	 */
	public Collection getAllCasesForUserAndGroupsExceptCodes(User user, Collection groups,CaseCode[] codes, int startingCase, int numberOfCases) throws FinderException, RemoteException
	{
		return this.getCaseHome().findAllCasesForGroupsAndUserExceptCodes(user, groups, codes, startingCase, numberOfCases);
	}
	
	public int getNumberOfCasesForUserAndGroupsExceptCodes(User user, Collection groups, CaseCode[] codes) {
		try {
			return this.getCaseHome().getNumberOfCasesByGroupsOrUserExceptCodes(user, groups, codes);
		}
		catch (RemoteException e) {
			return 0;
		}
		catch (IDOException e) {
			return 0;
		}
	}
		
	/**
	 * Gets all the Cases for the User with a specificed code
	 */
	public Collection getAllCasesForUser(User user, CaseCode code) throws FinderException, RemoteException
	{
		return this.getCaseHome().findAllCasesByUser(user, code);
	}
	/**
	 * Gets all the Cases for the User with a specificed code
	 */
	public Collection getAllCasesForUser(User user, String caseCode) throws FinderException, RemoteException
	{
		return this.getCaseHome().findAllCasesByUser(user, caseCode);
	}
	/**
	 * Gets all the Cases for the User with a specificed code and status
	 */
	public Collection getAllCasesForUser(User user, String caseCode, String caseStatus)
		throws FinderException, RemoteException
	{
		return this.getCaseHome().findAllCasesByUser(user, caseCode, caseStatus);
	}
	/**
	 * Gets all the Cases for the User with a specificed code and status
	 */
	public Collection getAllCasesForUser(User user, CaseCode code, CaseStatus status)
		throws FinderException, RemoteException
	{
		return this.getCaseHome().findAllCasesByUser(user, code, status);
	}
	
	public Collection getCaseLogsByDates(Timestamp fromDate, Timestamp toDate) throws FinderException, RemoteException {
		return getCaseLogHome().findAllCaseLogsByDate(fromDate, toDate);
	}
	
	public Collection getCaseLogsByCaseCodeAndDates(CaseCode caseCode, Timestamp fromDate, Timestamp toDate) throws FinderException, RemoteException {
		return getCaseLogsByCaseCodeAndDates(caseCode.getCode(), fromDate, toDate);
	}
	
	public Collection getCaseLogsByCaseCodeAndDates(String caseCode, Timestamp fromDate, Timestamp toDate) throws FinderException, RemoteException {
		return getCaseLogHome().findAllCaseLogsByCaseAndDate(caseCode, fromDate, toDate);
	}
	
	public Collection getCaseLogsByDatesAndStatusChange(Timestamp fromDate, Timestamp toDate, CaseStatus statusBefore, CaseStatus statusAfter) throws FinderException, RemoteException {
		return getCaseLogsByDatesAndStatusChange(fromDate, toDate, statusBefore.getStatus(), statusAfter.getStatus());
	}
	
	public Collection getCaseLogsByDatesAndStatusChange(Timestamp fromDate, Timestamp toDate, String statusBefore, String statusAfter) throws FinderException, RemoteException {
		return getCaseLogHome().findAllCaseLogsByDateAndStatusChange(fromDate, toDate, statusBefore, statusAfter);
	}
	
	public Collection getCaseLogsByCaseAndDatesAndStatusChange(CaseCode caseCode, Timestamp fromDate, Timestamp toDate, String statusBefore, String statusAfter) throws FinderException, RemoteException {
		return getCaseLogsByCaseAndDatesAndStatusChange(caseCode.getCode(), fromDate, toDate, statusBefore, statusAfter);
	}
	
	public Collection getCaseLogsByCaseAndDatesAndStatusChange(String caseCode, Timestamp fromDate, Timestamp toDate, String statusBefore, String statusAfter) throws FinderException, RemoteException {
		return getCaseLogHome().findAllCaseLogsByCaseAndDateAndStatusChange(caseCode, fromDate, toDate, statusBefore, statusAfter);
	}
	
	public Case getCase(int caseID) throws RemoteException, FinderException
	{
		return getCaseHome().findByPrimaryKey(new Integer(caseID));
	}
	public CaseCode getCaseCode(String caseCode) throws RemoteException, FinderException
	{
		return getCaseCodeHome().findByPrimaryKey(caseCode);
	}
	protected UserHome getUserHome() throws RemoteException
	{
		return (UserHome) com.idega.data.IDOLookup.getHome(User.class);
	}
	protected User getUser(int userID) throws RemoteException, FinderException
	{
		return this.getUserHome().findByPrimaryKey(new Integer(userID));
	}
	protected CaseHome getCaseHome() throws RemoteException
	{
		return (CaseHome) com.idega.data.IDOLookup.getHome(Case.class);
	}
	protected CaseCodeHome getCaseCodeHome() throws RemoteException
	{
		return (CaseCodeHome) com.idega.data.IDOLookup.getHome(CaseCode.class);
	}
	protected CaseLogHome getCaseLogHome() throws RemoteException
	{
		return (CaseLogHome) com.idega.data.IDOLookup.getHome(CaseLog.class);
	}
	protected CaseStatusHome getCaseStatusHome() throws RemoteException
	{
		return (CaseStatusHome) com.idega.data.IDOLookup.getHome(CaseStatus.class);
	}
	public CaseStatus getCaseStatus(String StatusCode) throws RemoteException
	{
		try
		{
			return this.getCaseStatusHome().findByPrimaryKey(StatusCode);
		}
		catch (FinderException e)
		{
			throw new EJBException("CaseStatus " + StatusCode + " is not installed or does not exist");
		}
	}
	public CaseStatus getCaseStatusOpen() throws RemoteException
	{
		try
		{
			return this.getCaseStatusHome().findByPrimaryKey(CASE_STATUS_OPEN_KEY);
		}
		catch (FinderException e)
		{
			throw new EJBException("CaseStatus " + CASE_STATUS_OPEN_KEY + " is not installed or does not exist");
		}
	}
	public CaseStatus getCaseStatusGranted() throws RemoteException
	{
		try
		{
			return this.getCaseStatusHome().findByPrimaryKey(this.CASE_STATUS_GRANTED_KEY);
		}
		catch (FinderException e)
		{
			throw new EJBException(
				"CaseStatus " + this.CASE_STATUS_GRANTED_KEY + " is not installed or does not exist");
		}
	}
	public CaseStatus getCaseStatusDeleted() throws RemoteException
	{
		try
		{
			return this.getCaseStatusHome().findByPrimaryKey(CASE_STATUS_DELETED_KEY);
		}
		catch (FinderException e)
		{
			throw new EJBException("CaseStatus " + this.CASE_STATUS_DELETED_KEY + " is not installed or does not exist");
		}
	}
	public CaseStatus getCaseStatusDenied() throws RemoteException
	{
		try
		{
			return this.getCaseStatusHome().findByPrimaryKey(CASE_STATUS_DENIED_KEY);
		}
		catch (FinderException e)
		{
			throw new EJBException("CaseStatus " + this.CASE_STATUS_DENIED_KEY + " is not installed or does not exist");
		}
	}
	public CaseStatus getCaseStatusReview() throws RemoteException
	{
		try
		{
			return this.getCaseStatusHome().findByPrimaryKey(this.CASE_STATUS_REVIEW_KEY);
		}
		catch (FinderException e)
		{
			throw new EJBException("CaseStatus " + this.CASE_STATUS_REVIEW_KEY + " is not installed or does not exist");
		}
	}
	public CaseStatus getCaseStatusMoved() throws RemoteException
	{
		try
		{
			return this.getCaseStatusHome().findByPrimaryKey(this.CASE_STATUS_MOVED);
		}
		catch (FinderException e)
		{
			throw new EJBException("CaseStatus " + this.CASE_STATUS_MOVED + " is not installed or does not exist");
		}
	}
	public CaseStatus getCaseStatusPlaced() throws RemoteException
	{
		try
		{
			return this.getCaseStatusHome().findByPrimaryKey(this.CASE_STATUS_PLACED);
		}
		catch (FinderException e)
		{
			throw new EJBException("CaseStatus " + this.CASE_STATUS_PLACED + " is not installed or does not exist");
		}
	}
	public CaseStatus getCaseStatusCancelled() throws RemoteException
	{
		try
		{
			return this.getCaseStatusHome().findByPrimaryKey(this.CASE_STATUS_CANCELLED_KEY);
		}
		catch (FinderException e)
		{
			throw new EJBException(
				"CaseStatus " + this.CASE_STATUS_CANCELLED_KEY + " is not installed or does not exist");
		}
	}
	public CaseStatus getCaseStatusInactive() throws RemoteException
	{
		try
		{
			return this.getCaseStatusHome().findByPrimaryKey(this.CASE_STATUS_INACTIVE_KEY);
		}
		catch (FinderException e)
		{
			throw new EJBException(
				"CaseStatus " + this.CASE_STATUS_INACTIVE_KEY + " is not installed or does not exist");
		}
	}
	public CaseStatus getCaseStatusPreliminary() throws RemoteException
	{
		try
		{
			return this.getCaseStatusHome().findByPrimaryKey(this.CASE_STATUS_PRELIMINARY_KEY);
		}
		catch (FinderException e)
		{
			throw new EJBException(
				"CaseStatus " + this.CASE_STATUS_PRELIMINARY_KEY + " is not installed or does not exist");
		}
	}
	public CaseStatus getCaseStatusContract() throws RemoteException
	{
		try
		{
			return this.getCaseStatusHome().findByPrimaryKey(this.CASE_STATUS_CONTRACT_KEY);
		}
		catch (FinderException e)
		{
			throw new EJBException(
				"CaseStatus " + this.CASE_STATUS_CONTRACT_KEY + " is not installed or does not exist");
		}
	}
	public CaseStatus getCaseStatusReady() throws RemoteException
	{
		return getCaseStatusAndInstallIfNotExists(CASE_STATUS_READY_KEY);
	}

	public CaseStatus getCaseStatusRedeem() throws RemoteException
	{
		try
		{
			return this.getCaseStatusHome().findByPrimaryKey(CASE_STATUS_REDEEM_KEY);
		}
		catch (FinderException e)
		{
			throw new EJBException(
				"CaseStatus " + CASE_STATUS_REDEEM_KEY + " is not installed or does not exist");
		}
	}
	
	public CaseStatus getCaseStatusError() throws RemoteException
		{
			return getCaseStatusAndInstallIfNotExists(CASE_STATUS_ERROR);
		}
	
	protected CaseStatus getCaseStatusAndInstallIfNotExists(String caseStatusString)throws EJBException,RemoteException{
		try
		{
			return this.getCaseStatusHome().findByPrimaryKey(caseStatusString);
		}
		catch (FinderException fe)
		{
			try{
				CaseStatus status = getCaseStatusHome().create();
				status.setStatus(caseStatusString);
				status.store();
				return status;
			}
			catch(Exception e){
				throw new EJBException(
					"Error creating CaseStatus " + caseStatusString + " is not installed or does not exist. Message: "+e.getMessage());
			}
		}
	}
	
	protected Locale getDefaultLocale()
	{
		//return com.idega.util.LocaleUtil.getLocale("en");
		return getIWApplicationContext().getApplication().getSettings().getDefaultLocale();
	}
	protected String getLocalizedString(String key, String defaultValue)
	{
		return getLocalizedString(key, defaultValue, this.getDefaultLocale());
	}
	protected String getLocalizedString(String key, String defaultValue, Locale locale)
	{
		return getBundle().getResourceBundle(locale).getLocalizedString(key, defaultValue);
	}
	public void changeCaseStatus(int theCaseID, String newCaseStatus, User performer)
		throws FinderException, RemoteException
	{
		Case theCase = this.getCase(theCaseID);
		changeCaseStatus(theCase, newCaseStatus, performer);
	}
	public void changeCaseStatus(Case theCase, String newCaseStatus, User performer) throws RemoteException
	{
		String oldCaseStatus = "";
		try{
			oldCaseStatus = theCase.getStatus();
			
			theCase.setStatus(newCaseStatus);
			theCase.setHandler(performer);
			theCase.store();
		
			if ( oldCaseStatus != newCaseStatus ) {
				CaseLog log = getCaseLogHome().create();
				log.setCase(Integer.parseInt(theCase.getPrimaryKey().toString()));
				log.setCaseStatusBefore(oldCaseStatus);
				log.setCaseStatusAfter(newCaseStatus);
				log.setPerformer(performer);
				log.store();
			}
		}
		catch(Exception e){
				throw new RemoteException("Error changing case status: "+oldCaseStatus+" to "+newCaseStatus+":"+e.getMessage());	
		}
	}
	public String getLocalizedCaseDescription(Case theCase, Locale locale)throws RemoteException
	{
		return getLocalizedCaseDescription(theCase.getCaseCode(),locale);
	}
	public String getLocalizedCaseDescription(CaseCode theCaseCode, Locale locale)throws RemoteException
	{
		return getLocalizedString("case_code_key." + theCaseCode.toString(), theCaseCode.toString());
	}
	public String getLocalizedCaseStatusDescription(CaseStatus status, Locale locale)
	{
		return getLocalizedString("case_status_key." + status.toString(), status.toString());
	}
	private static final String PROC_CASE_BUNDLE_IDENTIFIER = "com.idega.block.process";
	/**
	 * Can be overrided in subclasses
	 */
	protected String getBundleIdentifier()
	{
		return PROC_CASE_BUNDLE_IDENTIFIER;
	}
	protected IWBundle getBundle()
	{
		return getIWApplicationContext().getApplication().getBundle(getBundleIdentifier());
	}
	
	/**
	 * Gets the last modifier of the Case. Returns null if not modification found.
	 **/
	public User getLastModifier(Case aCase){
		try{
			CaseLog log = this.getCaseLogHome().findLastCaseLogForCase(aCase);
			return log.getPerformer();
		}
		catch(Exception e){
			
		}
		return null;
	}
	
	public String getCaseStatusOpenString() {		
		return CASE_STATUS_OPEN_KEY;	
	}
	
	public String getCaseStatusCancelledString() {		
		return CASE_STATUS_CANCELLED_KEY;
	}
	
	public String getCaseStatusInactiveString() {
			return CASE_STATUS_INACTIVE_KEY;
	}

	public String getCaseStatusReadyString() {
			return CASE_STATUS_READY_KEY;
	}
	
	public String getCaseStatusDeletedString() {
				return CASE_STATUS_DELETED_KEY;
	}
}