package is.idega.idegaweb.golf.service.member;

import is.idega.idegaweb.golf.entity.*;

import com.idega.presentation.*;

import com.idega.presentation.ui.*;

import com.idega.util.*;

import com.idega.util.text.*;

import java.util.*;

import java.sql.Date;

import java.sql.*;

import java.io.*;



import com.idega.presentation.*;



import com.idega.presentation.text.*;

import is.idega.idegaweb.golf.*;

import com.idega.util.*;

import com.idega.data.*;

/**

 * Title:

 * Description:

 * Copyright:    Copyright (c) 2001

 * Company:

 * @author

 * @version 1.0

 */



public class MemberInsert extends EntityInsert {



  private Member eMember;



  private TextInput inputSocial;

  private TextInput inputName;

  private TextInput inputEmail;

  private TextInput inputJob;

  private TextInput inputWorkPlace;

  private DropdownMenu dropGender;



  private final String inputSocialName = "MemberInsert_sociasecuritynumber";

  private final String inputNameName = "MemberInsert_name";

  private final String inputEmailName = "MemberInsert_email";

  private final String inputJobName = "MemberInsert_job";

  private final String inputWorkPlaceName = "MemberInsert_workplace";

  private final String dropGenderName = "MemberInsert_gender";



  private String inputSocialValue;

  private String inputNameValue;

  private String inputEmailValue;

  private String inputJobValue;

  private String inputWorkPlaceValue;

  private String dropGenderValue;



  public boolean debug = true;



  public MemberInsert() throws java.sql.SQLException{

    bUpdate = false;

    eMember = ((is.idega.idegaweb.golf.entity.MemberHome)com.idega.data.IDOLookup.getHomeLegacy(Member.class)).createLegacy();

    if(debug) {

        eMember.setDefaultValues();

    }

    inputSocial = new TextInput(inputSocialName);

    inputName = new TextInput(inputNameName);

    inputName.setAsNotEmpty("Vinsamelgast settu nafn");

    inputSocial.setAsNotEmpty("Vinsamelgast settu Kennit�lu");

    inputEmail = new TextInput(inputEmailName);

    inputJob = new TextInput(inputJobName);

    inputWorkPlace = new TextInput(inputWorkPlaceName);

    dropGender = genderDrop(dropGenderName, "M");

    init();

  }



  public MemberInsert(Member eMember)throws SQLException {

    bUpdate = true;

    this.eMember = eMember;



    if(eMember.getSocialSecurityNumber() != null)

        inputSocial = new TextInput(inputSocialName, eMember.getSocialSecurityNumber());

    else

        inputSocial = new TextInput(inputSocialName);

    if(eMember.getName() != null)

        inputName = new TextInput(inputNameName, eMember.getName());

    else

        inputName = new TextInput(inputNameName);

    if(eMember.getEmail() != null)

        inputEmail = new TextInput(inputEmailName, eMember.getEmail());

    else

        inputEmail = new TextInput(inputEmailName);



    if(eMember.getJob() != null)

        inputJob = new TextInput(inputJobName, eMember.getJob());

    else

        inputJob = new TextInput(inputJobName);

    if(eMember.getWorkPlace() != null)

        inputWorkPlace = new TextInput(inputWorkPlaceName, eMember.getWorkPlace());

    else

        inputWorkPlace = new TextInput(inputWorkPlaceName);



    if(eMember.getGender() != null)

        dropGender = genderDrop(dropGenderName, eMember.getGender());

    else

        dropGender = genderDrop(dropGenderName, "M");



    inputName.setAsNotEmpty("Vinsamelgast settu nafn");

    init();

  }



  private void init(){

    setStyle(inputSocial);

    setStyle(inputName);

    setStyle(inputName);

    setStyle(inputSocial);

    setStyle(inputEmail);

    setStyle(inputJob);

    setStyle(inputWorkPlace);

    setStyle(dropGender);

  }



  public TextInput getInputSocialSecurityNumber() {

      return this.inputSocial;

  }



  public TextInput getInputMemberName() {

      return this.inputName;

  }



  public TextInput getInputEmail() {

      return this.inputEmail;

  }





  public TextInput getInputJob() {

      return this.inputJob;

  }



  public TextInput getInputWorkPlace() {

      return this.inputWorkPlace;

  }



  public DropdownMenu getDropdownGender() {

      return dropGender;

  }



  public Vector getEmptyFields() {

      Vector vec = new Vector();



      if ( isInvalid(inputSocialValue)) {

          vec.addElement("Kennit�lu");

      }

      else if(! ErrorChecker.isValidSosialSecurityNumber(inputSocialValue)) {

          vec.addElement("Kennitala er ekki r�tt");

      }

      if ( isInvalid(inputNameValue)) {

          vec.addElement("Nafn");

      }

      else {

          Name name = new Name(inputNameValue);

          if(name.getLastName().equals(""))

              vec.addElement("Eftirnafn");

      }

      if ( isInvalid(inputEmailValue)) {

          vec.addElement("Netfang");

      }

      if ( isInvalid(inputJobValue)) {

          vec.addElement("Starfsheiti");

      }

      if ( isInvalid(inputWorkPlaceValue)) {

          vec.addElement("Vinnusta�ur");

      }

      if ( isInvalid(dropGenderValue)) {

          vec.addElement("Kyn");

      }

      return vec;

  }



  public Vector getNeededEmptyFields(IWContext iwc) {

    setVariables(iwc);

    Vector vec = new Vector();



      if ( isInvalid(inputSocialValue)) {

          vec.addElement("Kennit�lu");

      }

      else if(! ErrorChecker.isValidSosialSecurityNumber(inputSocialValue)) {

          vec.addElement("Kennitala er ekki r�tt");

      }

      if ( isInvalid(inputNameValue)) {

          vec.addElement("Nafn");

      }

      if ( isInvalid(dropGenderValue)) {

          vec.addElement("Kyn");

      }



      return vec;

  }



  public boolean areSomeFieldsEmpty(IWContext iwc) {

      return (getEmptyFields().size() > 0);

  }



  public Member getMember() {

      return this.eMember;

  }



  public boolean areNeededFieldsEmpty(IWContext iwc) {

    return (getNeededEmptyFields(iwc).size() > 0);

  }



  //precondition Have to call getNetedEmptyFields() !!!

  public void store(IWContext iwc) throws java.io.IOException, java.sql.SQLException {

    if(isUpdate())

      eMember.update();

    else

      eMember.insert();

  }



  public BorderTable getInputTable() {

    BorderTable hTable = new BorderTable();

    Table table = new Table(2, 6);

    hTable.add(table);



    table.add(formatText("Nafn"), 1, 1);

    table.add(formatText("Kennitala"), 1, 2);

    table.add(formatText("Kyn"), 1, 3);

    table.add(formatText("Netfang"), 1, 4);

    table.add(formatText("Starf"), 1, 5);

    table.add(formatText("Vinna"), 1, 6);

    table.add(getInputMemberName(), 2, 1);

    table.add(getInputSocialSecurityNumber(), 2, 2);

    table.add(getDropdownGender(), 2, 3);

    table.add(getInputEmail(), 2, 4);

    table.add(getInputJob(), 2, 5);

    table.add(getInputWorkPlace(), 2, 6);



    return hTable;

  }



  public void setVariables(IWContext iwc) {

      inputSocialValue = getValue(iwc,inputSocialName);

      inputNameValue = getValue(iwc,inputNameName);

      inputEmailValue = getValue(iwc,inputEmailName);

      inputJobValue = getValue(iwc,inputJobName);

      inputWorkPlaceValue = getValue(iwc,inputWorkPlaceName);

      dropGenderValue = getValue(iwc,dropGenderName);

      setEntity();

  }



  private void setEntity() {



      if (! isInvalid(inputSocialValue)) {

          if(ErrorChecker.isValidSosialSecurityNumber(inputSocialValue)) {

              eMember.setSocialSecurityNumber(inputSocialValue);

              eMember.setDateOfBirth(DateManipulator.getDateSQLFromSocialSecurityNumber(inputSocialValue));

          }

      }

      if (! isInvalid(inputNameValue)) {

          Name name = new Name(inputNameValue);

          eMember.setFirstName(name.getFirstName());

          eMember.setMiddleName(name.getMiddleName());

          eMember.setLastName(name.getLastName());

      }

      if (inputEmailValue != null) {

          eMember.setEmail(inputEmailValue);

      }

      if (inputJobValue != null) {

          eMember.setJob(inputJobValue);

      }

      if (inputWorkPlaceValue != null) {

          eMember.setWorkPlace(inputWorkPlaceValue);

      }

      if (dropGenderValue != null) {

          eMember.setGender(dropGenderValue);

      }

  }



  public DropdownMenu genderDrop(String name, String selected) {

    DropdownMenu drp = new DropdownMenu(name);

    drp.addMenuElement("M", "KK");

    drp.addMenuElement("F", "KVK");

    drp.setSelectedElement(selected);

    return drp;

  }

}
