package com.idega.block.finance.business;


import com.idega.block.finance.business.*;
import com.idega.block.finance.data.*;
import com.idega.util.IWTimestamp;
import com.idega.data.SimpleQuerier;
import com.idega.data.EntityBulkUpdater;
import java.util.*;

import com.idega.business.IBOServiceBean;
import com.idega.data.IDOLookup;

/**
 * Title:   idegaclasses
 * Description:
 * Copyright:    Copyright (c) 2001
 * Company:
 * @author  <a href="mailto:aron@idega.is">aron@idega.is
 * @version 1.0
 */

public class AssessmentBusinessBean extends IBOServiceBean implements AssessmentBusiness{

  public static final char cComplex = 'x';
  public static final char cAll = 'a';
  public static final char cBuilding = 'b';
  public static final char cFloor = 'f';
  public static final char cCategory = 'c';
  public static final char cType = 't';
  public static final char cApartment = 'p';

  public void groupEntriesWithSQL(IWTimestamp from,
                                         IWTimestamp to) throws Exception{

    javax.transaction.UserTransaction t = this.getSessionContext().getUserTransaction();
    //TransactionManager t = IdegaTransactionManager.getInstance();
    try{


      t.begin();
      ///////////////////////////
      AccountEntryHome ehome = (AccountEntryHome) IDOLookup.getHome(AccountEntry.class);
      AccountEntry ae = ehome.create();
      EntryGroup EG = null;
      int gid = -1;
      try {
        EG = ((EntryGroupHome)IDOLookup.getHome(EntryGroup.class)).create();
        EG.setGroupDate(IWTimestamp.RightNow().getSQLDate());
        EG.store();
        gid = EG.getID();
      }
      catch (Exception ex) {
        ex.printStackTrace();
        EG = null;
      }

      if(EG !=null){
        String dateColummn = com.idega.block.finance.data.AccountEntryBMPBean.getPaymentDateColumnName();
        StringBuffer sql = new StringBuffer("update ");
        sql.append(com.idega.block.finance.data.AccountEntryBMPBean.getEntityTableName());
        sql.append(" set ");
        sql.append(com.idega.block.finance.data.AccountEntryBMPBean.getEntryGroupIdColumnName());
        sql.append(" = ");
        sql.append(gid);
        sql.append(" where ");
        sql.append(com.idega.block.finance.data.AccountEntryBMPBean.getEntryGroupIdColumnName());
        sql.append(" is null ");
        if(from !=null){
          sql.append(" and ").append(dateColummn).append(" >= '").append(from.getSQLDate());
          sql.append(" 00:00:00' ");
        }
        if(to !=null){
          sql.append(" and ").append(dateColummn).append(" <= ");
          sql.append('\'');
          sql.append(to.getSQLDate());
          sql.append(" 23:59:59'");
        }

        String where = " where "+com.idega.block.finance.data.AccountEntryBMPBean.getEntryGroupIdColumnName()+" = "+gid;
        String sMinSql = "select min("+ae.getIDColumnName()+") from "+com.idega.block.finance.data.AccountEntryBMPBean.getEntityTableName()+where;
        String sMaxSql = "select max("+ae.getIDColumnName()+") from "+com.idega.block.finance.data.AccountEntryBMPBean.getEntityTableName()+where;
/*
        System.err.println(sql.toString());
        System.err.println(sMinSql);
        System.err.println(sMaxSql);
*/
        SimpleQuerier.execute(sql.toString());
        String[] mi = SimpleQuerier.executeStringQuery(sMinSql);
        String[] ma = SimpleQuerier.executeStringQuery(sMaxSql);
        if(mi!=null && mi.length > 0 && mi[0]!=null){
          EG.setEntryIdFrom(new Integer(mi[0]).intValue());
        }
        if(ma!=null && ma.length > 0 && mi[0]!=null
        ){
          EG.setEntryIdTo(new Integer(ma[0]).intValue());
        }
        EG.store();
      }
      t.commit();
      ///////////////////////////

    }
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

  public void groupEntries(IWTimestamp from,
                                  IWTimestamp to) throws Exception{
    List L = Finder.listOfFinanceEntriesWithoutGroup(from,to);
    if(L!=null){
      int min = 0,max = 0;
      EntryGroup EG = null;
      try {
        EG = ((com.idega.block.finance.data.EntryGroupHome)com.idega.data.IDOLookup.getHomeLegacy(EntryGroup.class)).createLegacy();
        EG.setGroupDate(IWTimestamp.RightNow().getSQLDate());
        EG.insert();
        int gid = EG.getID();
        //System.err.println(" gid "+gid);
      }
      catch (Exception ex) {
        ex.printStackTrace();
        try {
          EG.delete();
        }
        catch (Exception ex2) {
          ex2.printStackTrace();
          EG = null;
        }
      }
      if(EG !=null){
        javax.transaction.UserTransaction transaction = this.getSessionContext().getUserTransaction();
        //javax.transaction.TransactionManager t = com.idega.transaction.IdegaTransactionManager.getInstance();
        try{
          //t.begin();
          transaction.begin();
          ////////////////////////
          Iterator It = L.iterator();

          AccountEntry AE;
          int aeid = 0;
          AE = (AccountEntry) It.next();
          aeid = AE.getID();
          min = aeid;
          max = aeid;

          AE.setEntryGroupId(EG.getID());
          AE.update();

          while(It.hasNext()){
            AE = (AccountEntry) It.next();
            aeid = AE.getID();
            min = aeid < min ? aeid : min ;
            max = aeid > min ? aeid : max ;
            AE.setEntryGroupId(EG.getID());
            AE.update();

          }

          EG.setEntryIdFrom(min);
          EG.setEntryIdTo(max);
          EG.update();
          //////////////////////////////
          transaction.commit();

        }
        catch(Exception e) {
          try {
            transaction.rollback();

          }
          catch(javax.transaction.SystemException ex) {
            ex.printStackTrace();
          }
          try {
            EG.delete();
          }
          catch (Exception ex) {

          }
          e.printStackTrace();

        }
      }//if EG null
    }
  }

  public int getGroupEntryCount(EntryGroup entryGroup){
    int count = 0;
    if(entryGroup !=null ){
      StringBuffer sql = new StringBuffer("select count(*) from ");
      sql.append(com.idega.block.finance.data.AccountEntryBMPBean.getEntityTableName());
      sql.append(" where ");
      sql.append(com.idega.block.finance.data.AccountEntryBMPBean.getEntryGroupIdColumnName());
      sql.append(" = ");
      sql.append(entryGroup.getID());
      //System.err.println(sql.toString());
      try {
        count = entryGroup.getNumberOfRecords(sql.toString());
      }
      catch (Exception ex) {
        ex.printStackTrace();
        count = 0;
      }
    }
    return count;
  }

  public void assessTariffsToAccount(float price,String name,String info,int iAccountId,int iAccountKeyId,Date paydate,int tariffGroupId,int financeCategory,boolean save){
    javax.transaction.UserTransaction transaction = this.getSessionContext().getUserTransaction();
    try{
      transaction.begin();
      AssessmentRoundHome arh = (AssessmentRoundHome) IDOLookup.getHome(AssessmentRound.class);
      AssessmentRound AR = arh.create();
      AR.setAsNew( "account "+iAccountId);;
      AR.setTariffGroupId(tariffGroupId);
      AR.setCategoryId(financeCategory);
      AR.store();

      storeAccountEntry(iAccountId,iAccountKeyId,1,((Integer)AR.getPrimaryKey()).intValue(),price,0,price,paydate,name,info,"C");
      if(save){
        Tariff t = ((TariffHome) IDOLookup.getHome(Tariff.class)).create();
        t.setAccountKeyId(iAccountKeyId);
        t.setInfo(info);
        t.setName(name);
        t.setPrice(price);
        t.setTariffGroupId(tariffGroupId);
        t.store();
      }
      transaction.commit();
    }
    catch(Exception e){
      try {
        transaction.rollback();
      }
      catch(Exception ex) {
        ex.printStackTrace();
      }
      e.printStackTrace();
    }


  }

  public void assessTariffsToAccount(String[] tariffIds,int iAccountId,Date paydate,int discount,int tariffGroupId,int financeCategory)throws java.rmi.RemoteException{
    try{
      Collection tariffs = ((TariffHome) IDOLookup.getHome(Tariff.class)).findAllByPrimaryKeyArray(tariffIds);
      assessTariffsToAccount(tariffs,iAccountId,paydate,discount,tariffGroupId,financeCategory);
    }catch(javax.ejb.FinderException ex){
      throw new java.rmi.RemoteException(ex.getMessage());
    }

  }




  public void assessTariffsToAccount(Collection tariffs,int iAccountId,Date paydate,int discount,int tariffGroupId,int financeCategory){
    javax.transaction.UserTransaction transaction = this.getSessionContext().getUserTransaction();
    try{
      transaction.begin();
      AssessmentRoundHome arh = (AssessmentRoundHome) IDOLookup.getHome(AssessmentRound.class);
      AssessmentRound AR = arh.create();
      String name = "account "+iAccountId;
      AR.setTariffGroupId(tariffGroupId);
      AR.setCategoryId(financeCategory);
      AR.setAsNew(name);
      AR.store();

      Iterator iter = tariffs.iterator();
      Tariff tariff;
      while(iter.hasNext()){
        tariff = (Tariff) iter.next();
        float price = tariff.getPrice();
        String info = tariff.getInfo();
        if(discount > 0 && discount < 100){
          price = ((discount/100)+1)*price;
          info += "("+discount +" %)";
        }
         storeAccountEntry(iAccountId,
                    tariff.getAccountKeyId(),
                    1,((Integer)AR.getPrimaryKey()).intValue(),
                    price,0,price,paydate,tariff.getName(),tariff.getInfo(),"C");

      }
      transaction.commit();

    }
    catch(Exception e){
       try {
            transaction.rollback();

          }
          catch(Exception ex) {
            ex.printStackTrace();
          }
          e.printStackTrace();
    }
  }



  public AccountEntry storeAccountEntry(
          int iAccountId,
          int iAccountKeyId,
          int iCashierId,
          int iRoundId,

          float netto,
          float VAT,
          float total,

          Date paydate,
          String Name,
          String Info,
          String status
    )throws java.rmi.RemoteException,javax.ejb.CreateException{

      AccountEntry AE = ((AccountEntryHome)IDOLookup.getHome(AccountEntry.class)).create();
      AE.setAccountId(iAccountId);
      AE.setAccountKeyId(iAccountKeyId);
      AE.setCashierId(iCashierId);
      AE.setRoundId(iRoundId);
      AE.setTotal(-total);
      AE.setVAT(-VAT);
      AE.setNetto(-netto);
      AE.setLastUpdated(new java.sql.Timestamp(new Date().getTime()));
      AE.setPaymentDate(new java.sql.Timestamp(paydate.getTime()));
      AE.setName(Name);
      AE.setInfo(Info);
      AE.setStatus(status);
      AE.store();
    return AE;
  }

  public boolean rollBackAssessment(int iAssessmentRoundId){
    StringBuffer sql = new StringBuffer("delete from ");
    sql.append(com.idega.block.finance.data.AccountEntryBMPBean.getEntityTableName());
    sql.append(" where ").append(com.idega.block.finance.data.AccountEntryBMPBean.getRoundIdColumnName());
    sql.append(" = ").append(iAssessmentRoundId);
    System.err.println(sql.toString());

     javax.transaction.UserTransaction t = this.getSessionContext().getUserTransaction();

    try{
      t.begin();
      AssessmentRound AR = ((com.idega.block.finance.data.AssessmentRoundHome)com.idega.data.IDOLookup.getHomeLegacy(AssessmentRound.class)).findByPrimaryKeyLegacy(iAssessmentRoundId);
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
}
