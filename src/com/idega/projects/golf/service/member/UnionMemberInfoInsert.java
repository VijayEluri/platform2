package com.idega.projects.golf.service.member;

import com.idega.projects.golf.entity.*;
import com.idega.jmodule.object.interfaceobject.*;
import com.idega.jmodule.object.textObject.*;
import com.idega.jmodule.object.Table;
import com.idega.jmodule.object.Image;
import com.idega.jmodule.object.ModuleInfo;
import com.idega.projects.golf.service.Tariffer;
import java.util.Vector;
import java.util.List;
import java.util.ListIterator;
import java.sql.SQLException;
import java.io.IOException;
import java.sql.Date;
import java.util.GregorianCalendar;

/**
 * Title:
 * Description:
 * Copyright:    Copyright (c) 2001
 * Company:
 * @author
 * @version 1.0
 */


 public class UnionMemberInfoInsert extends EntityInsert {

  private UnionMemberInfo eUMI;

  private TextInput inputMemberNumber;
  private DropdownMenu dropMemberShipType;
  private DropdownMenu dropFamilyStatus;
  private DropdownMenu dropNumberOfPaiments;
  private TextInput inputLocker;
  private DateInput dateInputFirstPayday;
  private TextArea areaComment;
  private DropdownMenu dropVisible;
  private DropdownMenu dropPaymentTypes;
  private DropdownMenu dropCatalogue;
  private DropdownMenu dropActive;

  private final String inputMemberNumberName = "UnionMemberInfoInsert_member_number";
  private final String dropMemberShipTypeName = "UnionMemberInfoInsert_mbshiptype";
  private final String dropFamilyStatusName = "UnionMemberInfoInsert_family_status";
  private final String dropNumberOfPaimentsName = "UnionMemberInfoInsert_numpayments";
  private final String inputLockerName = "UnionMemberInfoInsert_locker";
  private final String dateInputFirstPaydayName = "UnionMemberInfoInsert_firstpayday";
  private final String areaCommentName = "UnionMemberInfoInsert_comment";
  private final String dropVisibleName = "UnionMemberInfoInsert_isvisible";
  private final String dropPaymentTypesName = "UnionMemberInfoInsert_paymenttypes";
  private final String dropCatalogueName = "UnionMemberInfoInsert_catalogue";
  private final String dropActiveName = "UnionMemberInfoInsert_isactive";

  private String inputMemberNumberValue;
  private String dropMemberShipTypeValue;
  private String dropFamilyStatusValue;
  private String dropNumberOfPaimentsValue;
  private String inputLockerValue;
  private Date dateInputFirstPaydayValue;
  private String areaCommentValue;
  private String dropVisibleValue;
  private String dropPaymentTypesValue;
  private String dropCatalogueValue;
  private String dropActiveValue;


  public UnionMemberInfoInsert(int iUnionId) throws java.sql.SQLException{
    bUpdate = false;
    this.eUMI = new UnionMemberInfo();
    GregorianCalendar cal = new GregorianCalendar();
    dateInputFirstPayday = new DateInput(dateInputFirstPaydayName, true);
    dateInputFirstPayday.setDay(1);
    dateInputFirstPayday.setMonth(2);
    dateInputFirstPayday.setYear(cal.get(cal.YEAR));

    eUMI.setUnionID( iUnionId);
    eUMI.setRegistrationDate(new Date(System.currentTimeMillis()));

    inputMemberNumber = new TextInput(inputMemberNumberName);
    inputMemberNumber.setLength(4);
    inputLocker = new TextInput(inputLockerName);
    inputLocker.setSize(8);
    areaComment = new TextArea(areaCommentName);
    dropMemberShipType = mbshiptypeDropdown(dropMemberShipTypeName,"main");
    dropVisible = visibleDropdown(dropVisibleName, "Y");
    dropPaymentTypes = paymentTypeDrop(dropPaymentTypesName, "1");
    dropCatalogue = catalogueDrop(dropCatalogueName, "0");
    dropActive = activeDropdown(dropActiveName, "A");
    dropNumberOfPaiments = numberOfPaymentDrop(dropNumberOfPaimentsName, "0");
    dropFamilyStatus = familyStatusDropdown(dropFamilyStatusName, "head");
    init();
  }

  public UnionMemberInfoInsert(UnionMemberInfo umi)throws SQLException {
      this.eUMI  = umi;
      bUpdate = true;
      GregorianCalendar cal = new GregorianCalendar();
      dateInputFirstPayday = new DateInput(dateInputFirstPaydayName, true);
      dateInputFirstPayday.setYear(cal.get(cal.YEAR));

      if(eUMI.getMemberStatus() != null) {
        dropActive = activeDropdown(dropActiveName, eUMI.getMemberStatus());
      }
      else
          dropActive = activeDropdown(dropActiveName, "A");
      if(eUMI.getFamilyStatus() != null)
          dropFamilyStatus = familyStatusDropdown(dropFamilyStatusName, eUMI.getFamilyStatus());
      else
          dropFamilyStatus = familyStatusDropdown(dropFamilyStatusName, "head");
      if(eUMI.getPreferredInstallmentNr() != -1)
          dropNumberOfPaiments = numberOfPaymentDrop(dropNumberOfPaimentsName, String.valueOf(eUMI.getPreferredInstallmentNr()));
      else
          dropNumberOfPaiments = numberOfPaymentDrop(dropNumberOfPaimentsName, "0");
      if(eUMI.getLockerNumber() != null)
          inputLocker = new TextInput(inputLockerName, eUMI.getLockerNumber());
      else
          inputLocker = new TextInput(inputLockerName);
      if(eUMI.getComment() != null)
          areaComment = new TextArea(areaCommentName, eUMI.getComment());
      else
          areaComment = new TextArea(areaCommentName);

      if(eUMI.getVisible())
          dropVisible = visibleDropdown(dropVisibleName, "Y");
      else
          dropVisible = visibleDropdown(dropVisibleName, "N");
      if(eUMI.getPaymentTypeID() != -1)
          dropPaymentTypes = paymentTypeDrop(dropPaymentTypesName, String.valueOf(eUMI.getPaymentTypeID()));
      else
          dropPaymentTypes = paymentTypeDrop(dropPaymentTypesName, "1");
      if(eUMI.getFirstInstallmentDate() != null) {
          dateInputFirstPayday.setDate(eUMI.getFirstInstallmentDate());
      }
      else {
          dateInputFirstPayday.setDay(1);
          dateInputFirstPayday.setMonth(2);
      }
      if(eUMI.getMembershipType()!=null){
        dropMemberShipType = mbshiptypeDropdown(dropMemberShipTypeName,eUMI.getMembershipType());
      }
      else{
        dropMemberShipType = mbshiptypeDropdown(dropMemberShipTypeName,"main");
      }
      String mn = String.valueOf(eUMI.getMemberNumber());
      inputMemberNumber = new TextInput(inputMemberNumberName,mn);
      dropCatalogue = catalogueDrop(dropCatalogueName, String.valueOf(eUMI.getPriceCatalogueID()));
      init();
  }
  private void init(){
    inputMemberNumber.setLength(4);
    inputLocker.setSize(8);
    dateInputFirstPayday.setStyle(this.styleAttribute);
    setStyle(inputMemberNumber);
    setStyle(inputLocker);
    setStyle(areaComment);
    setStyle(dropMemberShipType);
    setStyle(dropPaymentTypes);
    setStyle(dropVisible);
    setStyle(dropCatalogue);
    setStyle(dropActive);
    setStyle(dropNumberOfPaiments);
    setStyle(dropFamilyStatus);
  }

  public TextInput getInputMemberNumber() {
      return inputMemberNumber;
  }
  public UnionMemberInfo getUnionMemberInfo() {
      return this.eUMI;
  }
  public void setUnionId(int id ) {
      this.eUMI.setUnionID(id);
  }
  public void setMemberId(int id ) {
      this.eUMI.setMemberID(id);
  }
  public void setCardId(int id ) {
      this.eUMI.setCardId(id);
  }
  //debug added by eiki
  public void setMemberStatus( String status ) {
      this.eUMI.setMemberStatus(status);
  }
    //debug added by eiki
  public void setMembershipType(String type ) {
      this.eUMI.setMembershipType(type);
  }

  public DropdownMenu getDropdownNumberOfPayments() {
      return this.dropNumberOfPaiments;
  }

  public DropdownMenu getDropdownFamilyStatus() {
      return this.dropFamilyStatus;
  }

  public TextInput getInputLocker() {
      return this.inputLocker;
  }

  public DateInput getDateInputFirstPaymentDate() {
      return dateInputFirstPayday;
  }

  public DropdownMenu getDropVisible() {
      return this.dropVisible;
  }
  public DropdownMenu getDropCatalogue() {
      return dropCatalogue;
  }
  public DropdownMenu getDropMemberStatus() {
      return dropActive;
  }
  public DropdownMenu getDropMemberShipType() {
      return dropMemberShipType;
  }
  public DropdownMenu getDropdownPaymentType() {
      return dropPaymentTypes;
  }
  public TextArea getAreaComment() {
      return areaComment;
  }
  public Vector getEmptyFields() {
    Vector vec = new Vector();

    if ( isInvalid(inputLockerValue)) {
        vec.addElement("Sk�pan�mer");
    }
    if (! isDateInputValid(dateInputFirstPaydayName)) {
        vec.addElement("Dagsetning fyrstu afborgunar");
    }

    return vec;
  }
  public Vector getNeededEmptyFields(ModuleInfo modinfo) {
      return new Vector();
  }
  public boolean areSomeFieldsEmpty(ModuleInfo modinfo) {
      return (getEmptyFields().size() > 0);
  }
  public boolean areNeededFieldsEmpty(ModuleInfo modinfo) {
    return (getNeededEmptyFields(modinfo).size() > 0);
  }
  public void store(ModuleInfo modinfo) throws java.io.IOException, java.sql.SQLException {
      setVariables(modinfo);

      if(isUpdate())
          eUMI.update();
      else {
          if(eUMI.getUnionID() == 0 || eUMI.getUnionID() == -1 || eUMI.getMemberID() == 0 || eUMI.getMemberID() == -1)
              throw new SQLException("Vantar UNION_ID OG/E�A MEMBER_ID GILDI A KALLINN");
          int nextNumber = eUMI.getMaxColumnValue("member_number", "union_id", String.valueOf(eUMI.getUnionID()));
          nextNumber++;
          eUMI.setMemberNumber(nextNumber);
          eUMI.insert();
      }
  }
  public BorderTable getInputTable() {

    Table table = null;

    Link link = Tariffer.getExtraCatalogueLink("Skr� Gjaldflokk", String.valueOf(eUMI.getUnionID()));
    link.setObject(new Image("/pics/formtakks/edit.gif"));

    if(isUpdate()) {
      table = new Table(2, 8);
      table.add(formatText("N�mer"), 1, 1);
      table.add(formatText("Sta�a"), 1, 2);
      table.add(formatText("Grei�slur"), 1, 3);
      table.add(formatText("1.Gjaldd."), 1, 4);
      //table.add(formatText("Grei�slum�ti"), 1, 5);
      table.add(formatText("Vefur"), 1, 5);
      table.add(formatText("Sk�pur"), 1, 6);
      table.add(formatText("Fj.sta�a"), 1, 7);
      table.add(formatText("S�rgjald"), 1, 8);

      table.add(getInputMemberNumber(), 2, 1);
      table.add(getDropMemberStatus(), 2, 2);
      table.add(getDropMemberShipType(),2,2);
      table.add(getDropdownNumberOfPayments(), 2, 3);
      table.add(getDropdownPaymentType(), 2, 3);
      table.add(getDateInputFirstPaymentDate(), 2, 4);

      table.add(getDropVisible(), 2, 5);
      table.add(getInputLocker(), 2, 6);
      table.add(getDropdownFamilyStatus(), 2, 7);
      table.add(getDropCatalogue(), 2,8);
      table.add(link, 2, 8);
    }
    else {
      table = new Table(2, 8);
      table.add(formatText("Sta�a"), 1, 2);
      table.add(formatText("Grei�slur"), 1, 3);
      table.add(formatText("1.Gjalddagi"), 1, 4);
      //table.add(formatText("Grei�slum�ti"), 1, 4);
      table.add(formatText("Vefur"), 1, 5);
      table.add(formatText("Sk�pur"), 1, 6);
      table.add(formatText("Fj.sta�a"), 1, 7);
      table.add(formatText("S�rgjald"), 1, 8);

      table.add(getDropMemberStatus(), 2, 2);
      table.add(getDropMemberShipType(),2,2);
      table.add(getDropdownNumberOfPayments(), 2, 3);
      table.add(getDropdownPaymentType(), 2, 3);
      table.add(getDateInputFirstPaymentDate(), 2, 4);
      table.add(getDropVisible(), 2, 5);
      table.add(getInputLocker(), 2, 6);
      table.add(getDropdownFamilyStatus(), 2, 7);
      table.add(getDropCatalogue(), 2, 8);
      table.add(link, 2, 8);
    }

    BorderTable hTable = new BorderTable();
    hTable.add(table);
    return hTable;
  }

  public void setVariables(ModuleInfo modinfo) {
    inputMemberNumberValue = getValue(modinfo,inputMemberNumberName);
    dropMemberShipTypeValue = getValue(modinfo,dropMemberShipTypeName);
    dropNumberOfPaimentsValue = getValue(modinfo,dropNumberOfPaimentsName);
    inputLockerValue = getValue(modinfo,inputLockerName);
    dateInputFirstPaydayValue = getDateFromInput(modinfo,dateInputFirstPaydayName);
    areaCommentValue = getValue(modinfo,areaCommentName);
    dropVisibleValue = getValue(modinfo,dropVisibleName);
    dropPaymentTypesValue = getValue(modinfo,dropPaymentTypesName);
    dropCatalogueValue = getValue(modinfo,dropCatalogueName);
    dropActiveValue = getValue(modinfo,dropActiveName);
    dropFamilyStatusValue = getValue(modinfo,dropFamilyStatusName);
    setEntity();
  }

  private void setEntity() {
    if (! isInvalid(dropNumberOfPaimentsValue)) {
        if(isDigitOnly(dropNumberOfPaimentsValue))
            eUMI.setPreferredInstallmentNr(new Integer(dropNumberOfPaimentsValue));
    }
    if(inputMemberNumberValue != null){
      try {
        int number = Integer.parseInt(inputMemberNumberValue);
        eUMI.setMemberNumber(number);
      }
      catch (NumberFormatException ex) {
        System.err.println("membernumber not integer:");
        ex.printStackTrace();
      }

    }
    if (inputLockerValue != null) {
        eUMI.setLockerNumber(inputLockerValue);
    }
    if (dateInputFirstPaydayValue != null) {
        eUMI.setFirstInstallmentDate(dateInputFirstPaydayValue);
    }
    /*if (! isInvalid(areaCommentValue)) {
        member.setComment(areaCommentValue);
    }*/
    if (! isInvalid(dropVisibleValue)) {
        if(dropVisibleValue.equalsIgnoreCase("Y"))
            eUMI.setVisible(true);
        else
            eUMI.setVisible(false);
    }
    if (! isInvalid(dropPaymentTypesValue)) {
        eUMI.setPaymentTypeID(new Integer(dropPaymentTypesValue));
    }
    if(! isInvalid(dropCatalogueValue)) {
        eUMI.setPriceCatalogueID(Integer.parseInt(dropCatalogueValue));
    }
    eUMI.setMembershipType( dropMemberShipTypeValue);
    eUMI.setMemberStatus(dropActiveValue);
    eUMI.setFamilyStatus(dropFamilyStatusValue);

  }

  public DropdownMenu paymentTypeDrop(String name, String selected)throws java.sql.SQLException {
    DropdownMenu drp = new DropdownMenu(name);
    PaymentType type = new PaymentType();
    PaymentType[] types = (PaymentType[]) type.findAll();

    for( int i=0;i<types.length;i++){
      String sname = types[i].getName();

      if(sname.length() > 5)
        sname = sname.substring(0,5);
      drp.addMenuElement(types[i].getID(),sname);
    }
    drp.setSelectedElement(selected);
    return drp;
  }

  public DropdownMenu catalogueDrop(String name, String selected)throws java.sql.SQLException {
      if(selected.equals("-1"))
          selected = "0";
      DropdownMenu drp = new DropdownMenu( name);
      drp.addMenuElement(0, "-----");
      List list = Tariffer.getExtraCatalogList(String.valueOf(this.eUMI.getUnionID()));
      if(list != null) {
          ListIterator iter = list.listIterator();
          PriceCatalogue cate = null;
          String sname;
          while(iter.hasNext()) {
              cate = (PriceCatalogue) iter.next();
              sname = cate.getName();
              if(sname.length() > 15)
                sname = sname.substring(0,15)+"..";
              drp.addMenuElement(cate.getID(), sname);
          }
          drp.setSelectedElement(selected);
      }
      return drp;
  }

  public DropdownMenu numberOfPaymentDrop(String name, String selected) {
    DropdownMenu drp = new DropdownMenu( name);
    drp.addMenuElement(0, "--");
    for(int i = 1; i < 13; i++) {
        drp.addMenuElement(i, String.valueOf(i));
    }
    drp.setSelectedElement(selected);
    return drp;
  }

  public DropdownMenu mbshiptypeDropdown(String name,String selected){
    DropdownMenu drp = new DropdownMenu(name);
    drp.addMenuElement("main","A�alkl.");
    drp.addMenuElement("sub","Aukakl.");
    drp.setSelectedElement(selected);
    return drp;
  }

  public DropdownMenu activeDropdown(String name, String selected) {
    DropdownMenu drp = new DropdownMenu(name);
    drp.addMenuElement("A", "Virkur");
    drp.addMenuElement("I", "�virkur");
    drp.addMenuElement("W", "� bi�");
    drp.addMenuElement("Q", "H�ttur");
    drp.addMenuElement("D", "L�tinn");
    drp.setSelectedElement(selected);
    return drp;
  }

  public DropdownMenu familyStatusDropdown(String name, String selected) {
    DropdownMenu drp = new DropdownMenu(name);
    drp.addMenuElement("head", "H�fu�");
    drp.addMenuElement("partner", "Maki");
    drp.addMenuElement("child", "Barn");
    drp.setSelectedElement(selected);
    return drp;
  }

  public DropdownMenu visibleDropdown(String name, String selected) {
    DropdownMenu drp = new DropdownMenu(name);
    drp.addMenuElement("Y", "Sj�st � vef");
    drp.addMenuElement("N", "Ekki sj�st � vef");
    drp.setSelectedElement(selected);
    return drp;
  }
}