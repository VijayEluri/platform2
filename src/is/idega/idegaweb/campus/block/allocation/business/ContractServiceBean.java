/*
 * $Id: ContractServiceBean.java,v 1.5 2004/02/02 11:14:31 aron Exp $
 *
 * Copyright (C) 2001 Idega hf. All Rights Reserved.
 *
 * This software is the proprietary information of Idega hf.
 * Use is subject to license terms.
 *
 */
package is.idega.idegaweb.campus.block.allocation.business;


import com.idega.block.application.data.*;
import com.idega.block.building.business.*;
import com.idega.block.building.data.*;
import com.idega.block.finance.business.*;
import com.idega.business.*;
import com.idega.core.data.*;
import com.idega.data.*;
import com.idega.idegaweb.*;
import com.idega.user.business.*;
import com.idega.user.data.*;
import com.idega.util.*;
import is.idega.idegaweb.campus.block.allocation.data.*;
import is.idega.idegaweb.campus.block.application.data.*;
import is.idega.idegaweb.campus.block.building.data.*;
import is.idega.idegaweb.campus.block.mailinglist.business.*;
import is.idega.idegaweb.campus.data.*;
import is.idega.idegaweb.campus.presentation.*;
import java.sql.*;
import java.util.*;

/**
 * Title: Service Bean for the campus contract system
 * Description:
 * Copyright:    Copyright (c) 2000-2001 idega.is All Rights Reserved
 * Company:      idega
 * @author <a href="mailto:aron@idega.is">Aron Birkir</a>
 */

public class ContractServiceBean extends IBOServiceBean implements ContractService {

  public  String signContract(int iContractId,int iGroupId,int iCashierId,String sEmail,boolean sendMail,
                boolean newAccount,boolean newPhoneAccount,boolean newLogin ,boolean generatePasswd,IWResourceBundle iwrb,String login,String passwd){
    Contract eContract = null;
    String pass = null;
    javax.transaction.TransactionManager t = com.idega.transaction.IdegaTransactionManager.getInstance();

    try{
     t.begin();

      eContract = ((is.idega.idegaweb.campus.block.allocation.data.ContractHome)com.idega.data.IDOLookup.getHomeLegacy(Contract.class)).findByPrimaryKeyLegacy(iContractId );
      if(eContract != null ){
        int iUserId = eContract.getUserId().intValue();
				System.err.println("Signing user "+iUserId +" contract id : "+iContractId);
        if(sEmail !=null && sEmail.trim().length() >0){
          getUserService().addNewUserEmail(iUserId,sEmail);
        }
        if(newAccount){
          String prefix = iwrb.getLocalizedString("finance","Finance");
          AccountManager.makeNewFinanceAccount(iUserId,prefix+" - "+String.valueOf(iUserId),"",iCashierId,1);
        }
        if(newPhoneAccount){
          String prefix = iwrb.getLocalizedString("phone","Phone");
          AccountManager.makeNewPhoneAccount(iUserId,prefix+" - "+String.valueOf(iUserId),"",iCashierId,1);
        }
        if(newLogin  && iGroupId > 0){
          createUserLogin( iUserId,iGroupId,login,pass,generatePasswd );
        }
        deleteFromWaitingList(eContract);
        changeApplicationStatus( eContract);
        eContract.setStatusSigned();
        eContract.store();
        MailingListBusiness.processMailEvent(getIWApplicationContext(),iContractId,LetterParser.SIGNATURE);
      }
     t.commit();
    }
    catch(Exception e) {
      e.printStackTrace();
      try {
        t.rollback();
      }
      catch(javax.transaction.SystemException ex) {
        ex.printStackTrace();
      }
    }
    return pass;
  }

  public void createUserLogin(int iUserId,int iGroupId,String login,String pass,boolean generatePasswd) throws Exception{
    User user = getUserService().getUser(iUserId);
    getUserService().generateUserLogin(user);
    Group group = getUserService().getGroupHome().findByPrimaryKey(new Integer(iGroupId));
    group.addGroup(user);
  }

  public void changeApplicationStatus(Contract eContract)throws Exception{
    String status = ApplicationBMPBean.STATUS_SIGNED;
    List L = null;
    L = EntityFinder.getInstance().findAllByColumn(Application.class,ApplicationBMPBean.getApplicantIdColumnName(),eContract.getApplicantId().intValue());
    if(L!=null){
			Iterator I = L.iterator();
      while(I.hasNext()){
        Application A = (Application) I.next();
        A.setStatusSigned();
        A.update();
      }

    }
  }

  public  void deleteFromWaitingList(Contract eContract){
    List L = WaitingListFinder.listOfWaitingList(WaitingListFinder.APPLICANT,eContract.getApplicantId().intValue(),0,0);
      if(L!=null){
        Iterator I = L.iterator();
        while(I.hasNext()){
          try{
          ((WaitingList) I.next()).delete();
          }
          catch(SQLException ex){
           ex.printStackTrace();
          }
        }
      }
  }

  public void endContract(int iContractId,IWTimestamp movingDate,String info,boolean datesync){
    try {
      Contract C = ((ContractHome)com.idega.data.IDOLookup.getHomeLegacy(Contract.class)).findByPrimaryKeyLegacy(iContractId );
      C.setMovingDate(movingDate.getSQLDate());
      if(datesync)
        C.setValidTo(movingDate.getSQLDate());
      C.setResignInfo(info);
      C.setStatusEnded();
      C.update();
    }
    catch (SQLException ex) {
      ex.printStackTrace( );
    }
  }

  public  void returnKey(IWApplicationContext iwac,int iContractId){
    try {
      Contract C = ((ContractHome)com.idega.data.IDOLookup.getHomeLegacy(Contract.class)).findByPrimaryKeyLegacy(iContractId );
      C.setEnded();
      C.update();
      MailingListBusiness.processMailEvent(iwac,iContractId,LetterParser.RETURN);
    }
    catch (SQLException ex) {
      ex.printStackTrace( );
    }
  }

  public  void deliverKey(IWApplicationContext iwac,int iContractId, Timestamp when) {
     try {
      Contract C = ((is.idega.idegaweb.campus.block.allocation.data.ContractHome)com.idega.data.IDOLookup.getHomeLegacy(Contract.class)).findByPrimaryKeyLegacy(iContractId );
      if (when == null)
	      C.setStarted();
	    else
	    	C.setStarted(when);
      C.update();
       MailingListBusiness.processMailEvent(iwac,iContractId,LetterParser.DELIVER);
    }
    catch (SQLException ex) {
      ex.printStackTrace( );
    }
  }

  public  void deliverKey(IWApplicationContext iwac,int iContractId){
  	deliverKey(iwac,iContractId,null);
  }

  public  void resignContract(IWApplicationContext iwac,int iContractId,IWTimestamp movingDate,String info,boolean datesync){
    try {
      Contract C = ((is.idega.idegaweb.campus.block.allocation.data.ContractHome)com.idega.data.IDOLookup.getHomeLegacy(Contract.class)).findByPrimaryKeyLegacy(iContractId );
      C.setMovingDate(movingDate.getSQLDate());
      if(datesync)
        C.setValidTo(movingDate.getSQLDate());
      C.setResignInfo(info);
      C.setStatusResigned();
      C.update();
      MailingListBusiness.processMailEvent(iwac,iContractId,LetterParser.RESIGN);
    }
    catch (SQLException ex) {
      ex.printStackTrace();
    }
  }

  public  boolean makeNewContract(IWApplicationContext iwc,User eUser,Applicant eApplicant,int iApartmentId,IWTimestamp from,IWTimestamp to)throws java.rmi.RemoteException{
    try{
      Contract eContract = getContractHome().create();
      eContract.setApartmentId(iApartmentId);
      eContract.setApplicantId(eApplicant.getID());
      eContract.setUserId(((Integer)eUser.getPrimaryKey()).intValue());
      eContract.setStatusCreated();
      eContract.setValidFrom(from.getSQLDate());
      eContract.setValidTo(to.getSQLDate());

        eContract.store();
        MailingListBusiness.processMailEvent(iwc, eContract.getID(),LetterParser.ALLOCATION);
        return true;
      }
      catch(Exception ex){
        return false;
      }
  }

  public User makeNewUser(Applicant A,String[] emails){

    try{
    	User u = null;
    	
    	String ssn = A.getSSN();
    	if(ssn!=null){
    		
    			try {
					u = ((UserHome) IDOLookup.getHome(User.class)).findByPersonalID(ssn);
				}
				catch (RuntimeException e) {
					e.printStackTrace();
				}
    		
    		
    	}
    	else{
    		u = getUserService().insertUser(A.getFirstName(),A.getMiddleName(),A.getLastName(),A.getFirstName(),"",null,null,null);
    	}
    if(emails !=null && emails.length >0)
      getUserService().addNewUserEmail( ((Integer) u.getPrimaryKey()).intValue(),emails[0]);

    return u;
    }
    catch(Exception ex){
      ex.printStackTrace();
    }
    return null;
  }

  public  boolean deleteAllocation(int iContractId, User currentUser){
    try {
      Contract eContract =  getContractHome().findByPrimaryKey(iContractId);
      getUserService().deleteUser(eContract.getUserId().intValue(), currentUser);
      eContract.remove();

      return true;
    }
    catch (Exception ex) {
      ex.printStackTrace();
      return false;
    }
  }

  public  IWTimestamp[] getContractStampsForApartment(int apartmentId){
    Apartment ap = BuildingCacher.getApartment(apartmentId);
    return getContractStampsForApartment(ap);
  }

  public  IWTimestamp[] getContractStampsForApartment(Apartment apartment){
    ApartmentTypePeriods ATP = ContractFinder.getPeriod(apartment.getApartmentTypeId());
    return getContractStampsFromPeriod(ATP,1);

  }

   public  IWTimestamp[] getContractStampsFromPeriod(ApartmentTypePeriods ATP,int monthOverlap){
     IWTimestamp contractDateFrom = IWTimestamp.RightNow();
     IWTimestamp contractDateTo = IWTimestamp.RightNow();
     if(ATP!=null){
        // Period checking
        //System.err.println("ATP exists");
        boolean first = ATP.hasFirstPeriod();
        boolean second = ATP.hasSecondPeriod();
         IWTimestamp today = new IWTimestamp();

        // Two Periods
        if(first && second){

          if(today.getMonth() > ATP.getFirstDateMonth()+monthOverlap && today.getMonth() <= ATP.getSecondDateMonth()+monthOverlap ){
            contractDateFrom = new IWTimestamp(ATP.getSecondDateDay(),ATP.getSecondDateMonth(),today.getYear());
            contractDateTo = new IWTimestamp(ATP.getFirstDateDay(),ATP.getFirstDateMonth(),today.getYear()+1);
          }
          else if(today.getMonth() <= 12){
            contractDateFrom = new IWTimestamp(ATP.getFirstDateDay(),ATP.getFirstDateMonth(),today.getYear()+1);
            contractDateTo = new IWTimestamp(ATP.getSecondDateDay(),ATP.getSecondDateMonth(),today.getYear()+1);
          }
          else{
            contractDateFrom = new IWTimestamp(ATP.getFirstDateDay(),ATP.getFirstDateMonth(),today.getYear());
            contractDateTo = new IWTimestamp(ATP.getSecondDateDay(),ATP.getSecondDateMonth(),today.getYear());
          }

        }
        // One Periods
        else if(first && !second){
          //System.err.println("two sectors");
          contractDateFrom = new IWTimestamp(ATP.getFirstDateDay(),ATP.getFirstDateMonth(),today.getYear());
          contractDateTo = new IWTimestamp(ATP.getFirstDateDay(),ATP.getFirstDateMonth(),today.getYear()+1);
        }
        else if(!first && second){
          //System.err.println("two sectors");
          contractDateFrom = new IWTimestamp(ATP.getSecondDateDay(),ATP.getSecondDateMonth(),today.getYear());
          contractDateTo = new IWTimestamp(ATP.getSecondDateDay(),ATP.getSecondDateMonth(),today.getYear()+1);
        }
     }

      IWTimestamp[] stamps = {contractDateFrom,contractDateTo};
      return stamps;
  }

  public String getLocalizedStatus(com.idega.idegaweb.IWResourceBundle iwrb,String status){
    String r = "";
    char c = status.charAt(0);
    switch (c) {
      case 'C': r = iwrb.getLocalizedString("created","Created"); break;
      case 'P': r = iwrb.getLocalizedString("printed","Printed"); break;
      case 'S': r = iwrb.getLocalizedString("signed","Signed");   break;
      case 'R': r = iwrb.getLocalizedString("rejected","Rejected");  break;
      case 'T': r = iwrb.getLocalizedString("terminated","Terminated");   break;
      case 'E': r = iwrb.getLocalizedString("ended","Ended");  break;
      case 'G': r = iwrb.getLocalizedString("garbage","Garbage");  break;
    }
    return r;
  }

  public   boolean  doGarbageContract(int iContract){
    int id = iContract;
    try {
      Contract eContract = getContractHome().findByPrimaryKey(iContract);
      eContract.setStatusGarbage();
      eContract.store();
	  return true;
    }
    catch (Exception ex) {
		return false;
    }
    
  }

  public UserBusiness getUserService() throws java.rmi.RemoteException{
    return (UserBusiness) getServiceInstance(UserBusiness.class);
  }

  public ContractHome getContractHome()throws java.rmi.RemoteException{
    return (ContractHome)IDOLookup.getHome(Contract.class);
  }
}
