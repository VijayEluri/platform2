package com.idega.core.user.presentation;

import java.util.StringTokenizer;

import com.idega.core.user.data.Gender;
import com.idega.core.user.data.User;
import com.idega.data.GenericEntity;
import com.idega.presentation.IWContext;
import com.idega.presentation.Table;
import com.idega.presentation.text.Text;
import com.idega.presentation.ui.DateInput;
import com.idega.presentation.ui.DropdownMenu;
import com.idega.presentation.ui.TextArea;
import com.idega.presentation.ui.TextInput;
import com.idega.util.IWTimestamp;


/**
 * Title:        User
 * Copyright:    Copyright (c) 2001
 * Company:      idega.is
 * @author 2000 - idega team - <a href="mailto:gummi@idega.is">Gu�mundur �g�st S�mundsson</a>
 * @version 1.0
 */

public class GeneralUserInfoTab extends UserTab{



  private TextInput firstNameField;
  private TextInput middleNameField;
  private TextInput lastNameField;
  private TextInput displayNameField;
  private TextArea descriptionField;
  private DateInput dateOfBirthField;
  private DropdownMenu genderField;
  private TextInput ssnField;

  private String firstNameFieldName;
  private String middleNameFieldName;
  private String lastNameFieldName;
  private String displayNameFieldName;
  private String descriptionFieldName;
  private String dateOfBirthFieldName;
  private String genderFieldName;
	private String ssnFieldName;


  private Text firstNameText;
  private Text middleNameText;
  private Text lastNameText;
  private Text displayNameText;
  private Text descriptionText;
  private Text dateOfBirthText;
  private Text genderText;
  private Text ssnText;

  public GeneralUserInfoTab() {
    super();
    this.setName("General");
  }

  public GeneralUserInfoTab(int userId){
    this();
    this.setUserID(userId);
  }



  public void initializeFieldNames(){
    this.firstNameFieldName = "UMfname";
    this.middleNameFieldName = "UMmname";
    this.lastNameFieldName = "UMlname";
    this.displayNameFieldName = "UMdname";
    this.descriptionFieldName = "UMdesc";
    this.dateOfBirthFieldName = "UMdateofbirth";
    this.genderFieldName = "UMgender";
    this.ssnFieldName = "UMssn";
  }

  public void initializeFieldValues(){
    this.fieldValues.put(this.firstNameFieldName,"");
    this.fieldValues.put(this.middleNameFieldName,"");
    this.fieldValues.put(this.lastNameFieldName,"");
    this.fieldValues.put(this.displayNameFieldName,"");
    this.fieldValues.put(this.descriptionFieldName,"");
    this.fieldValues.put(this.dateOfBirthFieldName,"");
    this.fieldValues.put(this.genderFieldName,"");
    this.fieldValues.put(this.ssnFieldName,"");

    this.updateFieldsDisplayStatus();
  }

  public void updateFieldsDisplayStatus(){
    this.firstNameField.setContent((String)this.fieldValues.get(this.firstNameFieldName));

    this.middleNameField.setContent((String)this.fieldValues.get(this.middleNameFieldName));

    this.lastNameField.setContent((String)this.fieldValues.get(this.lastNameFieldName));

    this.displayNameField.setContent((String)this.fieldValues.get(this.displayNameFieldName));

    this.descriptionField.setContent((String)this.fieldValues.get(this.descriptionFieldName));
    
		this.ssnField.setContent((String)this.fieldValues.get(this.ssnFieldName));

    StringTokenizer date = new StringTokenizer((String)this.fieldValues.get(this.dateOfBirthFieldName)," -");
//    StringTokenizer date2 = new StringTokenizer((String)fieldValues.get(this.dateOfBirthFieldName)," -");

    if(date.hasMoreTokens()){
//      System.err.println("Year: "+ date2.nextToken());
      this.dateOfBirthField.setYear(date.nextToken());
    }
    if(date.hasMoreTokens()){
//      System.err.println("Month: "+ date2.nextToken());
      this.dateOfBirthField.setMonth(date.nextToken());
    }
    if(date.hasMoreTokens()){
//      System.err.println("Day: "+ date2.nextToken());
      this.dateOfBirthField.setDay(date.nextToken());
    }

    this.genderField.setSelectedElement((String)this.fieldValues.get(this.genderFieldName));    
  }


  public void initializeFields(){
    this.firstNameField = new TextInput(this.firstNameFieldName);
    this.firstNameField.setLength(12);

    this.middleNameField = new TextInput(this.middleNameFieldName);
    this.middleNameField.setLength(5);

    this.lastNameField = new TextInput(this.lastNameFieldName);
    this.lastNameField.setLength(12);

    this.displayNameField = new TextInput(this.displayNameFieldName);
    this.displayNameField.setLength(12);
    this.displayNameField.setMaxlength(20);
    
    this.ssnField = new TextInput(this.ssnFieldName);
    this.ssnField.setLength(10);
    this.ssnField.setMaxlength(20);

    this.descriptionField = new TextArea(this.descriptionFieldName);
    this.descriptionField.setHeight(7);
    this.descriptionField.setWidth(42);
    this.descriptionField.setWrap(true);

    this.dateOfBirthField = new DateInput(this.dateOfBirthFieldName);
    IWTimestamp time = IWTimestamp.RightNow();
    this.dateOfBirthField.setYearRange(time.getYear(),time.getYear()-100);

    this.genderField = new DropdownMenu(this.genderFieldName);
    this.genderField.addMenuElement("","Gender");

    Gender[] genders = null;
    try {
      Gender g = (Gender)GenericEntity.getStaticInstance(Gender.class);
      genders = (Gender[])g.findAll();
    }
    catch (Exception ex) {
      // do nothing
    }

    if(genders != null){
      for (int i = 0; i < genders.length; i++) {
        this.genderField.addMenuElement(genders[i].getID(),genders[i].getName());
      }

    }
  }

  public void initializeTexts(){
    this.firstNameText = getTextObject();
    this.firstNameText.setText("First name");

    this.middleNameText = getTextObject();
    this.middleNameText.setText("Middle name");

    this.lastNameText = getTextObject();
    this.lastNameText.setText("Last name");

    this.displayNameText = getTextObject();
    this.displayNameText.setText("Display name");

    this.descriptionText = getTextObject();
    this.descriptionText.setText("Description : ");

    this.dateOfBirthText = getTextObject();
    this.dateOfBirthText.setText("Date of birth : ");

    this.genderText = getTextObject();
    this.genderText.setText("Gender");
    
    this.ssnText = getTextObject();
    this.ssnText.setText("Personal ID : ");

  }


  public void lineUpFields(){
    this.resize(1,3);

    //First Part (names)
    Table nameTable = new Table(4,3);
    nameTable.setWidth("100%");
    nameTable.setCellpadding(0);
    nameTable.setCellspacing(0);
    nameTable.setHeight(1,this.columnHeight);
    nameTable.setHeight(2,this.columnHeight);
    nameTable.setHeight(3,this.columnHeight);

    nameTable.add(this.firstNameText,1,1);
    nameTable.add(this.firstNameField,2,1);
    nameTable.add(this.middleNameText,3,1);
    nameTable.add(this.middleNameField,4,1);
    nameTable.add(this.lastNameText,1,2);
    nameTable.add(this.lastNameField,2,2);
    nameTable.add(this.displayNameText,1,3);
    nameTable.add(this.displayNameField,2,3);
    nameTable.add(this.genderText,3,3);
    nameTable.add(this.genderField,4,3);
    this.add(nameTable,1,1);
    //First Part ends

    //Second Part (Date of birth)
    Table dateofbirthTable = new Table(2,2);
    dateofbirthTable.setCellpadding(0);
    dateofbirthTable.setCellspacing(0);
    dateofbirthTable.setHeight(1,this.columnHeight);
		dateofbirthTable.setHeight(2,this.columnHeight);
    dateofbirthTable.add(this.dateOfBirthText,1,1);
    dateofbirthTable.add(this.dateOfBirthField,2,1);
    dateofbirthTable.add(this.ssnText,1,2);
    dateofbirthTable.add(this.ssnField,2,2);
    this.add(dateofbirthTable,1,2);
    //Second Part Ends

    //Third Part (description)
    Table descriptionTable = new Table(1,2);
    descriptionTable.setCellpadding(0);
    descriptionTable.setCellspacing(0);
    descriptionTable.setHeight(1,this.columnHeight);
    descriptionTable.add(this.descriptionText,1,1);
    descriptionTable.add(this.descriptionField,1,2);
    this.add(descriptionTable,1,3);
    //Third Part ends
  }


  public boolean collect(IWContext iwc){
    if(iwc != null){

      String fname = iwc.getParameter(this.firstNameFieldName);
      String mname = iwc.getParameter(this.middleNameFieldName);
      String lname = iwc.getParameter(this.lastNameFieldName);

      String dname = iwc.getParameter(this.displayNameFieldName);
      String desc = iwc.getParameter(this.descriptionFieldName);
      String dateofbirth = iwc.getParameter(this.dateOfBirthFieldName);
      String gender = iwc.getParameter(this.genderFieldName);
      String ssn = iwc.getParameter(this.ssnFieldName);

      if(fname != null){
        this.fieldValues.put(this.firstNameFieldName,fname);
      }
      if(mname != null){
        this.fieldValues.put(this.middleNameFieldName,mname);
      }
      if(lname != null){
        this.fieldValues.put(this.lastNameFieldName,lname);
      }
      if(dname != null){
        this.fieldValues.put(this.displayNameFieldName,dname);
      }
      if(desc != null){
        this.fieldValues.put(this.descriptionFieldName,desc);
      }
      if(dateofbirth != null){
        this.fieldValues.put(this.dateOfBirthFieldName,dateofbirth);
      }
      if(gender != null){
        this.fieldValues.put(this.genderFieldName,gender);
      }
      
      if (ssn != null) {	
				this.fieldValues.put(this.ssnFieldName,ssn);
      }

      this.updateFieldsDisplayStatus();

      return true;
    }
    return false;
  }

  public boolean store(IWContext iwc){
    try{
      if(getUserId() > -1){
        IWTimestamp dateOfBirthTS = null;
        String st = (String)this.fieldValues.get(this.dateOfBirthFieldName);
        Integer gen = (this.fieldValues.get(this.genderFieldName).equals(""))? null : new Integer((String)this.fieldValues.get(this.genderFieldName));
        if( st != null && !st.equals("")){
          dateOfBirthTS = new IWTimestamp(st);
        }
        this.business.updateUser(getUserId(),(String)this.fieldValues.get(this.firstNameFieldName),
                            (String)this.fieldValues.get(this.middleNameFieldName),(String)this.fieldValues.get(this.lastNameFieldName),
                            (String)this.fieldValues.get(this.displayNameFieldName),(String)this.fieldValues.get(this.descriptionFieldName),
                            gen,dateOfBirthTS,null,(String)this.fieldValues.get(this.ssnFieldName));
                      
      }
    }catch(Exception e){
      //return false;
      e.printStackTrace(System.err);
      throw new RuntimeException("update user exception");
    }
    return true;
  }


  public void initFieldContents(){

    try{
      User user = ((com.idega.core.user.data.UserHome)com.idega.data.IDOLookup.getHomeLegacy(User.class)).findByPrimaryKeyLegacy(getUserId());

      this.fieldValues.put(this.firstNameFieldName,(user.getFirstName() != null) ? user.getFirstName():"" );
      this.fieldValues.put(this.middleNameFieldName,(user.getMiddleName() != null) ? user.getMiddleName():"" );
      this.fieldValues.put(this.lastNameFieldName,(user.getLastName() != null) ? user.getLastName():"" );
      this.fieldValues.put(this.displayNameFieldName,(user.getDisplayName() != null) ? user.getDisplayName():"" );
      this.fieldValues.put(this.descriptionFieldName,(user.getDescription() != null) ? user.getDescription():"" );
      this.fieldValues.put(this.dateOfBirthFieldName,(user.getDateOfBirth()!= null) ? new IWTimestamp(user.getDateOfBirth()).toSQLDateString() : "");
      this.fieldValues.put(this.genderFieldName,(user.getGenderID() != -1) ? Integer.toString(user.getGenderID()):"" );
			this.fieldValues.put(this.ssnFieldName,(user.getPersonalID() != null) ? user.getPersonalID():"" );
      this.updateFieldsDisplayStatus();

    }catch(Exception e){
      System.err.println("GeneralUserInfoTab error initFieldContents, userId : " + getUserId());
    }


  }


} // Class GeneralUserInfoTab
