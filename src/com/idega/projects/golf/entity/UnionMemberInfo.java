package com.idega.projects.golf.entity;

import com.idega.data.*;
import java.sql.*;

/**
 * Title:        GolfEntity
 * Description:
 * Copyright:    Copyright (c) 2001
 * Company:      idega margmi�lun hf.
 * @author
 * @version 1.0
 */

public class UnionMemberInfo extends GolfEntity {


    public UnionMemberInfo(){
      super();
    }

    public UnionMemberInfo(int id)throws SQLException{
      super(id);
    }


    public void initializeAttributes() {
        addAttribute(getIDColumnName());
        addAttribute("member_id", "MemberID", true, true, "java.lang.Integer");
        addAttribute("union_id", "ClubID", true, true, "java.lang.Integer");

        addAttribute("member_status","Sta�a me�lims",true,true,"java.lang.String");
        addAttribute("member_number","N�mer me�lims",true,true,"java.lang.Integer");
        addAttribute("family_id","Fj�lskylda",false,false,"java.lang.Integer","many-to-one","com.idega.projects.golf.entity.Family");
        addAttribute("family_status","Fj�lskyldusta�a",true,true,"java.lang.String");
        addAttribute("card_id","Grei�slukort",false,false,"java.lang.Integer","one-to-one","com.idega.projects.golf.entity.Card");

        addAttribute("payment_type_id","Grei�slum�ti",true,true,"java.lang.Integer","one-to-many","com.idega.projects.golf.entity.PaymentType");
        addAttribute("preferred_installment_nr","Grei�slufj�ldi",true,true,"java.lang.Integer");
        addAttribute("comment","Uppl�singar",true,true,"java.lang.String",20000);

        addAttribute("locker_number","Uppl�singar",true,true,"java.lang.String");
        addAttribute("visible","Sj�anlegur � vef",true,true,"java.lang.Boolean");
        addAttribute("first_installment_date","Dagsetning fyrstu borgunar",true,true,"java.sql.Date");

        addAttribute("registration_date", "Stofndagur", true, true, "java.sql.Date");

        addAttribute("membership_type", "Sta�a Kl�bbs", true, true, "java.lang.String", 5);
        addAttribute("price_catalogue_id","Gjaldskr�",true,true,"java.lang.Integer");
    }

    public void setDefaultValues(){
      setColumn("card_id",1);
      setColumn("payment_type_id",1);
    }

    public String getEntityName() {
      return "union_member_info";
    }


    public int getMemberID(){
            return getIntColumnValue("member_id");
    }

    public void setMemberID(Integer member_id){
            setColumn("member_id",member_id);
    }

    public void setMemberID(int member_id){
            setColumn("member_id",member_id);
    }


    public int getUnionID(){
            return getIntColumnValue("union_id");
    }

    public void setUnionID(int union_id){
            setColumn("union_id",union_id);
    }

    public void setUnionID(Integer union_id){
            setColumn("union_id",union_id);
    }


    /**
     * A: active , I:inactive, H:On hold
     */
    public String getMemberStatus(){
            return getStringColumnValue("member_status");
    }

    /**
     * A: active , I:inactive, H:On hold
     */
    public void setMemberStatus(Character member_status){
            setColumn("member_status",member_status);
    }

    public void setMemberStatus(char member_status){
            setColumn("member_status",new Character(member_status));
    }

    /**
     * A: active , I:inactive, H:On hold
     */
    public void setMemberStatus(String member_status){
            setColumn("member_status",member_status);
    }


    public int getMemberNumber(){
            return getIntColumnValue("member_number");
    }

    public void setMemberNumber(Integer member_number){
            setColumn("member_number",member_number);
    }

    public void setMemberNumber(int member_number){
            setColumn("member_number",member_number);
    }


    public int getFamilyId(){
            return getIntColumnValue("family_id");
    }

    public void setFamilyId(int family_id){
            setColumn("family_id",family_id);
    }

    public void setFamilyId(Integer family_id){
            setColumn("family_id",family_id);
    }

    public void setFamily(Family family){
            setColumn("family_id",family);
    }

    public Family getFamily(){
            return (Family) getColumnValue("family_id");
    }

    public String getFamilyStatus() {
        return getStringColumnValue("family_status");
    }

    public void setFamilyStatus(String family_status) {
        setColumn("family_status", family_status);
    }

    public int getCardId() {
        return getIntColumnValue("card_id");
    }

    public void setCardId(int cardId) {
        setColumn("card_id", cardId);
    }

    public Card getCard(){
            return (Card) getColumnValue("card_id");
    }


    public int getPaymentTypeID(){
            return getIntColumnValue("payment_type_id");
    }

    public void setPaymentTypeID(int payment_type_id){
            setColumn("payment_type_id",payment_type_id);
    }

    public void setPaymentTypeID(Integer payment_type_id){
            setColumn("payment_type_id",payment_type_id);
    }

    public int getPreferredInstallmentNr(){
            return getIntColumnValue("preferred_installment_nr");
    }

    public void setPreferredInstallmentNr(int preferred_installment_nr){
            setColumn("preferred_installment_nr",preferred_installment_nr);
    }

    public void setPreferredInstallmentNr(Integer preferred_installment_nr){
            setColumn("preferred_installment_nr",preferred_installment_nr);
    }


    public void setComment(String comment){
      setColumn("comment",comment);
    }

    public String getComment(){
      return getStringColumnValue("comment");
    }


    public String getLockerNumber() {
        return getStringColumnValue("locker_number");
    }

    public void setLockerNumber(String lockerNumber) {
        setColumn("locker_number", lockerNumber);
    }


    public boolean getVisible(){
        return getBooleanColumnValue("visible");
    }

    public void setVisible(boolean visible) {
        this.setColumn("visible", visible);
    }


    public Date getFirstInstallmentDate() {
        return (Date) getColumnValue("first_installment_date");
    }

    public void setFirstInstallmentDate(Date date) {
        setColumn("first_installment_date", date);
    }


    public Date getRegistrationDate(){
      return (Date) getColumnValue("registration_date");
    }

    public void setRegistrationDate(Date regisration_date){
      setColumn("registration_date", regisration_date);
    }

    public String getMembershipType(){
      return getStringColumnValue("membership_type");
    }

    public void setMembershipType(String membership_type){
      setColumn("membership_type",membership_type);
    }

    public void setPriceCatalogueID(Integer price_catalogue_id){
            setColumn("price_catalogue_id",price_catalogue_id);
    }

    public void setPriceCatalogueID(int price_catalogue_id){
            setColumn("price_catalogue_id",price_catalogue_id);
    }

     public int getPriceCatalogueID() {
        return getIntColumnValue("price_catalogue_id");
    }



} // Class UnionMemberInfo





/*	public int getMainUnionID() throws SQLException {
        /**
         * @todo impliment
         */
/*
          int skilari=1;
          Connection conn= null;
          Statement Stmt= null;

          conn = getConnection();
          Stmt = conn.createStatement();
          ResultSet RS = Stmt.executeQuery("select union_id from union_member where member_id = "+this.getID()+" and membership_type='main'");

          if (RS.next()){
                  skilari = RS.getInt("union_id");
          }
          RS.close();


          Stmt.close();

          if (conn != null){
                  freeConnection(conn);
          }

          return skilari;*/
/*          return -1;
	}
*/
/*
	public Union getMainUnion()throws SQLException{
          /**
           * @todo impliment
           */
/*		Union union = new Union(getMainUnionID());
		return union;
	}
*/

   /*     public Card[] getCards()throws SQLException{
                return ((Card[]) findReverseRelated(new Card()));
	}*/


