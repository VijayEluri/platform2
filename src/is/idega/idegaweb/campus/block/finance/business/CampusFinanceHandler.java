package is.idega.idegaweb.campus.block.finance.business;

import com.idega.block.finance.business.FinanceHandler;
import com.idega.block.finance.business.FinanceFinder;
import com.idega.block.finance.business.AccountManager;
import com.idega.block.finance.business.AssessmentTariffPreview;
import com.idega.block.finance.data.Account;
import com.idega.block.finance.data.AccountEntry;
import com.idega.block.finance.data.Tariff;
import com.idega.block.finance.data.AssessmentRound;
import com.idega.block.building.business.BuildingCacher;
import com.idega.util.idegaTimestamp;
import is.idega.idegaweb.campus.data.ContractAccountApartment;
import com.idega.data.EntityBulkUpdater;
import java.util.Map;
import java.util.Hashtable;
import java.util.List;
import java.util.Vector;
import java.util.Collection;
import java.sql.SQLException;
import java.text.NumberFormat;

/**
 * Title:
 * Description:
 * Copyright:    Copyright (c) 2001
 * Company:
 * @author <a href="mailto:aron@idega.is">aron@idega.is
 * @version 1.0
 */

public class CampusFinanceHandler implements FinanceHandler{
  int count = 0;
  NumberFormat nf = NumberFormat.getPercentInstance();
  public CampusFinanceHandler() {
  }

  public String getAccountType(){
    return Account.typeFinancial;
  }

  public boolean rollbackAssessment(int iAssessmentRoundId){
    /*
    EntityBulkUpdater bulk = new EntityBulkUpdater();
    Hashtable H = new Hashtable();
    Vector V = new Vector();
    if(iAssessmentRoundId > 0){
      AssessmentRound AR = new AssessmentRound();
      try{
        AR = new AssessmentRound(iAssessmentRoundId);

      List L = AccountManager.listOfAccountEntries(AR.getID());

      if(L!=null){
        java.util.Iterator I = L.iterator();
        AccountEntry ae;

        Integer Aid;
        float Amount;
        while(I.hasNext()){
          ae = (AccountEntry) I.next();
          if(ae.getStatus().equals(ae.statusCreated)){
            Amount = ae.getTotal();
            Aid = new Integer(ae.getAccountId());
            bulk.add(ae,bulk.delete);
            // lowering the account

          }
        }
      }
      }
      catch(Exception ex){ ex.printStackTrace();}
      bulk.addAll(H.values(),bulk.update);
      bulk.add(AR,bulk.delete);
      bulk.execute();
    }
    return false;
    */
    StringBuffer sql = new StringBuffer("delete from ");
    sql.append(AccountEntry.getEntityTableName());
    sql.append(" where ").append(AccountEntry.getRoundIdColumnName());
    sql.append(" = ").append(iAssessmentRoundId);
    System.err.println(sql.toString());

    javax.transaction.TransactionManager t = com.idega.transaction.IdegaTransactionManager.getInstance();

    try{
      t.begin();
      AssessmentRound AR = new AssessmentRound(iAssessmentRoundId);
      com.idega.data.SimpleQuerier.execute(sql.toString());
      AR.delete();
      t.commit();
      return true;
    } // Try block
    catch(Exception e) {
      try {
        t.rollback();
      }
      catch(javax.transaction.SystemException ex) {
        ex.printStackTrace();
      }
      e.printStackTrace();
    }
    return false;
  }

  public boolean executeAssessment(int iCategoryId,int iTariffGroupId,String roundName,int iCashierId,int iAccountKeyId,idegaTimestamp paydate,idegaTimestamp start,idegaTimestamp end){
    List listOfTariffs = FinanceFinder.listOfTariffs(iTariffGroupId);
    //List listOfUsers = CampusAccountFinder.listOfRentingUserAccountsByType(getAccountType());
    List listOfUsers = CampusAccountFinder.listOfContractAccounts(getAccountType(),start,end);
    //Map mapOfContracts = ContractFinder.mapOfApartmentUsersBy();
    int iAccountCount = 0;
    if(listOfTariffs !=null){
      if(listOfUsers!=null){
        NumberFormat nf = NumberFormat.getPercentInstance();
        int rlen = listOfUsers.size();
        int tlen = listOfTariffs.size();
        Tariff eTariff;
        char cAttribute;
        ContractAccountApartment user;
        Vector vEntries = new Vector();
        int iAttributeId = -1;
        int iRoundId  = -1;
        AssessmentRound AR = null;
        try {
          AR = new AssessmentRound();
          AR.setAsNew(roundName);
          AR.setCategoryId(iCategoryId);
          //AR.setCategoryId(iCategoryId);
          AR.setTariffGroupId(iTariffGroupId);
          AR.setType(Account.typeFinancial);
          AR.insert();
          iRoundId = AR.getID();
        }
        catch (SQLException ex) {
          ex.printStackTrace();
          try {
            AR.delete();
          }
          catch (SQLException ex2) {
            ex2.printStackTrace();
            AR = null;
          }
        }

        if(AR != null){
        javax.transaction.TransactionManager t = com.idega.transaction.IdegaTransactionManager.getInstance();

        try{
          t.begin();
          int totals = 0;
          int totalAmount = 0;
          double factor = 1;
          // All tenants accounts (Outer loop)
          for(int o = 0; o < rlen ; o++){
            user = (ContractAccountApartment)listOfUsers.get(o);
            factor = getFactor(user,start,end);
            ///Account eAccount = new Account(user.getAccountId());
            if(factor >0){
            totalAmount = 0;
            float Amount = 0;
            // For each tariff (Inner loop)
            for (int i=0; i < tlen ;i++ ) {
              Amount = 0;
              eTariff = (Tariff) listOfTariffs.get(i);
              String sAttribute = eTariff.getTariffAttribute();
              // If we have an tariff attribute
              if(sAttribute != null){
                iAttributeId = -1;
                cAttribute = sAttribute.charAt(0);
                // If All
                if(cAttribute == BuildingCacher.CHARALL){
                  Amount = insertEntry(vEntries,eTariff,user.getAccountId(),iRoundId,paydate,iCashierId,factor);
                }
                // other than all
                else{
                  // attribute check
                  if(sAttribute.length() >= 3){
                  iAttributeId = Integer.parseInt(sAttribute.substring(2));
                    switch (cAttribute) {
                      case BuildingCacher.CHARTYPE: // Apartment type
                        if(iAttributeId == user.getApartmentTypeId())
                          Amount = insertEntry(vEntries,eTariff,user.getAccountId(),iRoundId,paydate,iCashierId,factor);
                      break;
                      case BuildingCacher.CHARCATEGORY  : // Apartment category
                        if(iAttributeId == user.getApartmentCategoryId())
                          Amount = insertEntry(vEntries,eTariff,user.getAccountId(),iRoundId,paydate,iCashierId,factor);
                      break;
                      case BuildingCacher.CHARBUILDING  : // Building
                        if(iAttributeId == user.getBuildingId())
                          Amount = insertEntry(vEntries,eTariff,user.getAccountId(),iRoundId,paydate,iCashierId,factor);
                      break;
                      case BuildingCacher.CHARFLOOR     : // Floor
                        if(iAttributeId == user.getFloorId())
                          Amount = insertEntry(vEntries,eTariff,user.getAccountId(),iRoundId,paydate,iCashierId,factor);
                      break;
                      case BuildingCacher.CHARCOMPLEX : // Complex
                        if(iAttributeId == user.getComplexId())
                          Amount = insertEntry(vEntries,eTariff,user.getAccountId(),iRoundId,paydate,iCashierId,factor);
                      break;
                      case BuildingCacher.CHARAPARTMENT : // Apartment
                        if(iAttributeId == user.getApartmentId())
                          Amount = insertEntry(vEntries,eTariff,user.getAccountId(),iRoundId,paydate,iCashierId,factor);
                      break;
                    }// switch
                  } // attribute check
                }// other than all
                if(sAttribute.length() >= 3){
                  iAttributeId = Integer.parseInt(sAttribute.substring(2));
                }
                totalAmount += Amount;

              }
            } // Inner loop block
          }
            totals += totalAmount*-1;

          } // Outer loop block

          AR.update();
          t.commit();
          return true;
        } // Try block
        catch(Exception e) {
          try {
            t.rollback();
          }
          catch(javax.transaction.SystemException ex) {
            ex.printStackTrace();
          }
          e.printStackTrace();

        }
        }
      }
    }
    return false;
  }

  /**
   *
   */
  public double getFactor(ContractAccountApartment con,idegaTimestamp start,idegaTimestamp end   ){
    double ret = 0;
    long begin = start.getTimestamp().getTime();
    long endin = end.getTimestamp().getTime();
    long del = endin-begin;
    long valfr = con.getValidFrom().getTime();
    long valto = con.getValidTo().getTime();

    if(con.getDeliverTime()!=null ){
      valfr = con.getDeliverTime().getTime();
    }
    if(con.getReturnTime() !=null){
      valto = con.getReturnTime().getTime();
    }


      System.err.print("Valfr: "+con.getValidFrom().toString());
      System.err.print(" Valto: "+con.getValidTo().toString());
      System.err.print(" start: "+start.toString());
      System.err.print(" end: "+end.toString());


    // if contract ends within period
    if(begin <= valto && valto <= endin){
      endin = valto;
    }
    // if contract begins within period
    else if (begin <= valfr && valfr <= endin){
      begin = valfr;
    }
    // if contract begins and ends within period
    else if(begin <= valfr && valto <= endin){
      begin = valfr;
      endin = valto;
    }
    // if contract begins and ends outside period
    else if(valfr < begin && endin < valto){
      // donothing
    }

    System.err.println();
    double diff = endin-begin;
    System.err.println("Endin - Begin" + endin +" - "+begin+" = "+diff +" / "+del );


    ret = (diff)/del;
    System.err.println("Factor : "+ ret);

    return ret;
  }


  public Collection listOfAssessmentTariffPreviews(int iTariffGroupId,idegaTimestamp start,idegaTimestamp end){
    List listOfTariffs = FinanceFinder.listOfTariffs(iTariffGroupId);
    List listOfUsers = CampusAccountFinder.listOfRentingUserAccountsByType(getAccountType());

    if(listOfTariffs !=null && listOfUsers!=null){
      Hashtable H = new Hashtable(listOfTariffs.size());
      int rlen = listOfUsers.size();
      int tlen = listOfTariffs.size();
      Tariff eTariff;
      char cAttribute;
      ContractAccountApartment user;

      int iAttributeId = -1;
      String sAttribute;

      // All tenants accounts (Outer loop)
      for(int o = 0; o < rlen ; o++){
        user = (ContractAccountApartment)listOfUsers.get(o);
        double factor = getFactor(user,end,start);
        // For each tariff (Inner loop)
        if(factor > 0){
          for (int i=0; i < tlen ;i++ ) {
            eTariff = (Tariff) listOfTariffs.get(i);
            sAttribute = eTariff.getTariffAttribute();
            // If we have an tariff attribute
            if(sAttribute != null){
              iAttributeId = -1;
              cAttribute = sAttribute.charAt(0);
              //System.err.println("att "+String.valueOf(cAttribute));
              // If All
              if(cAttribute == BuildingCacher.CHARALL){
                addAmount(H,eTariff,factor);
              }
              // other than all
              else{
                // attribute check
                if(sAttribute.length() >= 3){
                iAttributeId = Integer.parseInt(sAttribute.substring(2));
                  switch (cAttribute) {
                    case BuildingCacher.CHARTYPE: // Apartment type
                      if(iAttributeId == user.getApartmentTypeId())
                        addAmount(H,eTariff,factor);
                    break;
                    case BuildingCacher.CHARCATEGORY  : // Apartment category
                      if(iAttributeId == user.getApartmentCategoryId())
                        addAmount(H,eTariff,factor);
                    break;
                    case BuildingCacher.CHARBUILDING  : // Building
                      if(iAttributeId == user.getBuildingId())
                        addAmount(H,eTariff,factor);
                    break;
                    case BuildingCacher.CHARFLOOR     : // Floor
                      if(iAttributeId == user.getFloorId())
                        addAmount(H,eTariff,factor);
                    break;
                    case BuildingCacher.CHARCOMPLEX : // Complex
                      if(iAttributeId == user.getComplexId())
                        addAmount(H,eTariff,factor);
                    break;
                    case BuildingCacher.CHARAPARTMENT : // Apartment
                      if(iAttributeId == user.getApartmentId())
                        addAmount(H,eTariff,factor);
                    break;
                  }// switch
                } // attribute check
              }// other than all
              if(sAttribute.length() >= 3){
                iAttributeId = Integer.parseInt(sAttribute.substring(2));
              }
            }
          } // Inner loop block
        } // factor check
      } // Outer loop block
      //System.err.println("count "+count);
      if(H!=null){
        return H.values();
      }
    } // listcheck
    else
      System.err.println("nothing to preview");
    return null;
  }

  private synchronized void addAmount(Map map,Tariff tariff,double factor){
    //System.err.println("map size "+map.size());
    Integer id = new Integer(tariff.getID());
    AssessmentTariffPreview preview;
    if(map.containsKey(id)){
      preview = (AssessmentTariffPreview) map.get(id);
    }
    else{
      preview = new AssessmentTariffPreview(tariff.getName());
    }
    preview.addAmount((float)(tariff.getPrice()*factor));
    map.put(id,preview);
    count++;
  }

  private float insertEntry(Vector V,Tariff T,int iAccountId,int iRoundId,idegaTimestamp itPaydate,int iCashierId,double factor)
  throws SQLException{

    if(factor > 0){
    AccountEntry AE = new AccountEntry();
    AE.setAccountId(iAccountId);
    AE.setAccountKeyId(T.getAccountKeyId());
    AE.setCashierId(iCashierId);
    AE.setLastUpdated(idegaTimestamp.getTimestampRightNow());
    /** @todo  skeptical precision cut */
    AE.setTotal((int)(-T.getPrice()*factor));
    AE.setRoundId(iRoundId);
    AE.setName(T.getName());
    if(T.getInfo()!=null)
      AE.setInfo(T.getInfo()+" "+nf.format(factor));
    else
      AE.setInfo(nf.format(factor));
    AE.setStatus(AE.statusCreated);
    AE.setCashierId(1);
    AE.setPaymentDate(itPaydate.getTimestamp());
    AE.insert();
    if(V!=null)
      V.add(AE);

    return AE.getTotal();
    }
    return 0;
    /*
    System.err.println("totals before"+totals);
    totals = totals + AE.getPrice();
    System.err.println("price"+AE.getPrice());
    System.err.println("totals after"+totals);
    */
  }

  public Map getAttributeMap(){
    Map map = BuildingCacher.mapOfLodgingsNames();
    map.put("a","All");
    return map;
  }

  public List listOfAttributes(){
    List list = BuildingCacher.listOfMapEntries();
    list.add(0,"a");
    return list;
  }


}