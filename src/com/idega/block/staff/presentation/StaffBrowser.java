package com.idega.block.staff.presentation;

/**
 * Title:
 * Description:
 * Copyright:    Copyright (c) 2000-2001 idega.is All Rights Reserved
 * Company:      idega
  *@author <a href="mailto:laddi@idega.is">��rhallur "Laddi" Helgason</a>
 * @version 1.2
 */

import java.util.List;
import java.util.Collections;
import java.lang.NumberFormatException;
import com.idega.presentation.text.Text;
import com.idega.presentation.text.Link;
import com.idega.presentation.Table;
import com.idega.presentation.Block;
import com.idega.presentation.IWContext;
import com.idega.presentation.Image;
import com.idega.core.user.presentation.CreateUser;
import com.idega.core.user.presentation.CreateUserGroup;
import com.idega.core.user.data.User;
import com.idega.core.data.GenericGroup;
import com.idega.core.data.Email;
import com.idega.block.IWBlock;
import com.idega.block.staff.data.*;
import com.idega.block.staff.business.*;
import com.idega.core.accesscontrol.business.AccessControl;
import com.idega.core.user.business.UserBusiness;
import com.idega.core.business.UserGroupBusiness;
import com.idega.core.data.Phone;
import com.idega.core.data.PhoneType;
import com.idega.util.idegaTimestamp;
import com.idega.util.GenericUserComparator;
import com.idega.util.text.TextStyler;
import com.idega.util.text.StyleConstants;
import com.idega.core.localisation.business.ICLocaleBusiness;
import com.idega.idegaweb.IWResourceBundle;
import com.idega.idegaweb.IWBundle;
import com.idega.idegaweb.IWMainApplication;


public class StaffBrowser extends Block implements IWBlock {

private boolean _isAdmin=false;
private int _userID = -1;
private int _localeID;

public static final int ALL_STAFF = 1;
public static final int DIVISION_STAFF = 2;
public static final int USER = 3;
private int _layout = ALL_STAFF;

private boolean _addAlphabet = false;
private boolean _hasAlphabetLetter;
private String _alphabetLetter;
private String _selectedLetterColor;
private String _zebraColor1,_zebraColor2;

private int row = -1;
private String _name;
private String _alphabetName;
private boolean _styles = true;

private boolean _showAge;
//private boolean _showGender;
private boolean _showEducation;
private boolean _showTitle;
private boolean _showListTitle;
private boolean _showBeganWork;
private boolean _showArea;
private boolean _showImage;
private boolean _showMetaData;
private boolean _showWorkPhone;
private boolean _showListWorkPhone;
private boolean _showMobilePhone;

private String _imageWidth;
private String _imageHeight;
private String _nameWidth;
private String _attributesWidth;

private String _width;
private String _linkStyle;
private String _visitedStyle;
private String _activeStyle;
private String _hoverStyle;
private String _alphabetLinkStyle;
private String _alphabetVisitedStyle;
private String _alphabetActiveStyle;
private String _alphabetHoverStyle;

private String _textStyle;
private String _headlineStyle;
private String _divisionStyle;

private IWBundle _iwb;
private IWResourceBundle _iwrb;
private final static String IW_BUNDLE_IDENTIFIER="com.idega.block.staff";

private Table _myTable;

  public StaffBrowser(){
    setDefaultValues();
  }

  public void main(IWContext iwc) throws Exception {
    _iwb = iwc.getApplication().getBundle(IW_CORE_BUNDLE_IDENTIFIER);
    _iwrb = getResourceBundle(iwc);
    _isAdmin = iwc.hasEditPermission(this);
    _localeID = ICLocaleBusiness.getLocaleId(iwc.getCurrentLocale());

    row = 1;
    _myTable = new Table();
      _myTable.setWidth(_width);

    if ( _isAdmin ) {
      _myTable.add(getAdminButtons(),1,row);
      row++;
    }

    handleParameters(iwc);
    getStaff(iwc);

    add(_myTable);
  }

  private void getStaff(IWContext iwc) {
    setStyles();

    if ( _addAlphabet && _layout == ALL_STAFF ) {
      _myTable.add(getAlphabetTable(),1,row);
      row++;
    }

    switch (_layout) {
      case ALL_STAFF:
        getAllStaff(iwc);
        break;
      case DIVISION_STAFF:
        getDivisionStaff(iwc);
        break;
      case USER:
        getUser(iwc);
        break;
    }
  }

  private void getAllStaff(IWContext iwc) {
    List users = null;
    if ( _hasAlphabetLetter )
      users = StaffFinder.getAllUsersByLetter(iwc,_alphabetLetter);
    else
      users = StaffFinder.getAllUsers(iwc);

    getStaffTable(iwc,users);
  }

  private void getStaffTable(IWContext iwc, List users) {
    Table table = new Table();
      table.setWidth("100%");
      table.setCellspacing(0);
      table.setCellpadding(3);

    int staffRow = 1;

    Link userLink = null;
    Link emailLink = null;
    Text titleText = null;
    Text phoneText = null;
    StaffHolder holder = null;
    int column = 1;

    if ( users != null ) {
      GenericUserComparator comparator = new GenericUserComparator(GenericUserComparator.NAME);
      Collections.sort(users,comparator);
      for ( int a = 0; a < users.size(); a++ ) {
        column = 1;
        holder = StaffFinder.getStaffHolder((User)users.get(a),_localeID);

        userLink = getStaffLink(holder.getName(),holder.getUserID());
        emailLink = getEmailLink(holder.getEmail());

        if ( holder.getTitle() != null ) {
          titleText = new Text(holder.getTitle());
        }
        else
          titleText = new Text("");
        titleText.setFontStyle(_textStyle);

        if ( holder.getWorkPhone() != null ) {
          phoneText = new Text(holder.getWorkPhone());
        }
        else
          phoneText = new Text("");
        phoneText.setFontStyle(_textStyle);

        table.add(userLink,column++,staffRow);
        if ( _showListTitle )
          table.add(titleText,column++,staffRow);
        if ( _showListWorkPhone )
          table.add(phoneText,column++,staffRow);
        if ( emailLink != null )
          table.add(emailLink,column++,staffRow);

        if ( _isAdmin ) {
          table.add(getEditLink(holder.getUserID()),column,staffRow);
          table.add(getDeleteLink(holder.getUserID()),column,staffRow);
        }

        staffRow++;
      }
    }

    if ( _zebraColor1 != null && _zebraColor2 != null )
      table.setHorizontalZebraColored(_zebraColor1,_zebraColor2);
    int centeredColumn = 2;
    if ( _showListTitle )
      centeredColumn = 3;
    for ( int a = centeredColumn; a <= table.getColumns(); a++ )
      table.setColumnAlignment(a,"center");
    if ( _nameWidth != null )
      table.setWidth(1,_nameWidth);
    _myTable.add(table,1,row);
  }

  private void getDivisionStaff(IWContext iwc) {
    List groups = StaffFinder.getAllGroups(iwc);
    Text divisionText = null;
    if ( groups != null ) {
      for ( int a = 0; a < groups.size(); a++ ) {
        List users = StaffFinder.getUsersInPrimaryGroup((GenericGroup)groups.get(a));
        if ( users != null && users.size() > 0 ) {
          divisionText = new Text(((GenericGroup)groups.get(a)).getName());
            divisionText.setFontStyle(_divisionStyle);
          _myTable.add(divisionText,1,row);
          row++;
          getStaffTable(iwc,users);
          row++;
          _myTable.setHeight(1,row,"6");
          row++;
        }
      }
    }
  }

  private void getUser(IWContext iwc) {
    StaffHolder holder = StaffFinder.getStaffHolder(_userID,iwc);

    Table userTable = new Table();
      userTable.setWidth("100%");
      userTable.setCellpadding(0);
      userTable.setCellspacing(0);

    Table textTable = new Table();
      textTable.setWidth("100%");
      //textTable.setWidth(1,"110");

    int tableRow = 1;
    int column = 1;

    Image image = null;
    if ( holder != null && holder.getImageID() != -1 ) {
      try {
        image = new Image(holder.getImageID());
        if ( _imageWidth != null ) image.setWidth(_imageWidth);
        if ( _imageHeight != null ) image.setHeight(_imageHeight);
        image.setBorder(1);
        image.setVerticalSpacing(3);
        image.setHorizontalSpacing(10);
      }
      catch (Exception e) {
        image = null;
      }

      if ( image != null ) {
        userTable.add(image,1,1);
      }
    }

    if ( holder != null ) {
      Text name = new Text(_iwrb.getLocalizedString("user_name","Name")+":");
        name.setFontStyle(_headlineStyle);
      Text nameText = new Text(holder.getName());
        nameText.setFontStyle(_textStyle);

      textTable.add(name,column,tableRow);
      textTable.add(nameText,column+1,tableRow);
      tableRow++;

      int userAge = holder.getAge();
      Text age = new Text(_iwrb.getLocalizedString("user_age","Age")+":");
        age.setFontStyle(_headlineStyle);
      Text ageText = new Text(Integer.toString(userAge));
        ageText.setFontStyle(_textStyle);

      if ( _showAge && userAge > 0 ) {
        textTable.add(age,column,tableRow);
        textTable.add(ageText,column+1,tableRow);
        tableRow++;
      }

      /*Text gender = new Text(_iwrb.getLocalizedString("user_gender","Gender")+":");
        gender.setFontStyle(_headlineStyle);
      Text genderText = new Text("");
        genderText.setFontStyle(_textStyle);

      if ( _showGender ) {
        textTable.add(gender,column,tableRow);
        textTable.add(genderText,column+1,tableRow);
        tableRow++;
      }*/

      Text title = new Text(_iwrb.getLocalizedString("user_title","Title")+":");
        title.setFontStyle(_headlineStyle);
      Text titleText = new Text("");
        if ( holder.getTitle() != null )
          titleText.setText(holder.getTitle());
        titleText.setFontStyle(_textStyle);

      if ( _showTitle ) {
        textTable.add(title,column,tableRow);
        textTable.add(titleText,column+1,tableRow);
        tableRow++;
      }

      Text workPhone = new Text(_iwrb.getLocalizedString("work_phone","Work phone")+":");
        workPhone.setFontStyle(_headlineStyle);
      Text workPhoneText = new Text("");
        if ( holder.getWorkPhone() != null )
          workPhoneText.setText(holder.getWorkPhone());
        workPhoneText.setFontStyle(_textStyle);

      if ( _showWorkPhone ) {
        textTable.add(workPhone,column,tableRow);
        textTable.add(workPhoneText,column+1,tableRow);
        tableRow++;
      }

      Text mobilePhone = new Text(_iwrb.getLocalizedString("Mobile_phone","Mobile phone")+":");
        mobilePhone.setFontStyle(_headlineStyle);
      Text mobilePhoneText = new Text("");
        if ( holder.getMobilePhone() != null )
          mobilePhoneText.setText(holder.getMobilePhone());
        mobilePhoneText.setFontStyle(_textStyle);

      if ( _showMobilePhone ) {
        textTable.add(mobilePhone,column,tableRow);
        textTable.add(mobilePhoneText,column+1,tableRow);
        tableRow++;
      }

      Text area = new Text(_iwrb.getLocalizedString("user_area","Area")+":");
        area.setFontStyle(_headlineStyle);
      Text areaText = new Text("");
        if ( holder.getArea() != null )
          areaText.setText(holder.getArea());
        areaText.setFontStyle(_textStyle);

      if ( _showArea ) {
        textTable.add(area,column,tableRow);
        textTable.add(areaText,column+1,tableRow);
        tableRow++;
      }

      Text beganWork = new Text(_iwrb.getLocalizedString("user_began_work","Began work")+":");
        beganWork.setFontStyle(_headlineStyle);
      Text beganWorkText = new Text("");
        if ( holder.getBeganWork() != null )
          beganWorkText.setText(holder.getBeganWork().getLocaleDate(iwc));
        beganWorkText.setFontStyle(_textStyle);

      if ( _showBeganWork ) {
        textTable.add(beganWork,column,tableRow);
        textTable.add(beganWorkText,column+1,tableRow);
        tableRow++;
      }

      Text education = new Text(_iwrb.getLocalizedString("user_education","Education")+":");
        education.setFontStyle(_headlineStyle);
      Text educationText = new Text("");
        if ( holder.getEducation() != null )
          educationText.setText(holder.getEducation());
        educationText.setFontStyle(_textStyle);

      if ( _showEducation ) {
        textTable.add(education,column,tableRow);
        textTable.add(educationText,column+1,tableRow);
        tableRow++;
      }

      if ( holder.getMetaAttributes() != null && _showMetaData ) {
        String[] attributes = holder.getMetaAttributes();
        String[] values = holder.getMetaValues();
        for ( int a = 0; a < attributes.length; a++ ) {
          Text meta = new Text(attributes[a]+":");
            meta.setFontStyle(_headlineStyle);
          Text metaText = new Text(values[a]);
            metaText.setFontStyle(_textStyle);

          textTable.add(meta,column,tableRow);
          textTable.add(metaText,column+1,tableRow);
          tableRow++;
        }
      }
    }

    int index = -1;

    List users = StaffFinder.getAllUsers(iwc);
    if ( users != null ) {
      GenericUserComparator comparator = new GenericUserComparator(GenericUserComparator.NAME);
      Collections.sort(users,comparator);
      index = users.indexOf(StaffFinder.getUser(_userID));
    }

    Table linkTable = new Table(3,1);
      linkTable.setWidth(1,"33%");
      linkTable.setWidth(2,"33%");
      linkTable.setWidth(3,"33%");
      linkTable.setWidth("100%");
      linkTable.setAlignment(2,1,"center");

    Link nextLink = getNextUserLink(users,index);
    Link previousLink = getPreviousUserLink(users,index);
    Link backLink = new Link("< "+_iwrb.getLocalizedString("back","Back")+" >");
      backLink.setStyle(_name);

    if ( previousLink != null )
      linkTable.add(previousLink,1,1);
    linkTable.add(backLink,2,1);
    if ( nextLink != null ) {
      linkTable.add(nextLink,3,1);
      linkTable.setAlignment(3,1,"right");
    }

    if ( image != null ) {
      userTable.add(textTable,2,1);
      userTable.setWidth(2,"100%");
      userTable.mergeCells(1,5,2,5);
      userTable.add(linkTable,1,5);
    }
    else {
      userTable.add(textTable,1,1);
      userTable.add(linkTable,1,5);
    }

    textTable.setWidth(1,"100");
    if ( _attributesWidth != null )
      textTable.setWidth(1,_attributesWidth);
    textTable.setColumnVerticalAlignment(1,"top");
    textTable.setColumnVerticalAlignment(2,"top");
    userTable.setColumnVerticalAlignment(1,"top");
    userTable.setColumnVerticalAlignment(2,"top");

    _myTable.add(userTable,1,row);

    if ( _isAdmin ) {
      _myTable.add(getEditLink(_userID),1,row+1);
    }
  }

  private Link getNextUserLink(List users,int index) {
    Link link = new Link(_iwrb.getLocalizedString("next_user","Next")+" >>");
      link.setStyle(_name);

    if ( users.size() > index+1 )
      link.addParameter(StaffBusiness.PARAMETER_USER_ID,((User)users.get(index+1)).getID());
    else
      link = null;

    return link;
  }

  private Link getPreviousUserLink(List users,int index) {
    Link link = new Link("<< "+_iwrb.getLocalizedString("previous_user","Prev"));
      link.setStyle(_name);

    if ( index > 0 )
      link.addParameter(StaffBusiness.PARAMETER_USER_ID,((User)users.get(index-1)).getID());
    else
      link = null;

    return link;
  }

  private Link getStaffLink(String name,int userID) {
    Link link = new Link(name);
      if ( _styles )
        link.setStyle(_name);
      link.addParameter(StaffBusiness.PARAMETER_USER_ID,userID);

    return link;
  }

  private Link getEmailLink(String email) {
    Link link = null;

    if ( email != null ) {
      link = new Link(email);
        if ( _styles )
          link.setStyle(_name);
        link.setURL("mailto:"+email);
    }

    return link;
  }

  private Link getEditLink(int userID) {
    Image adminImage = _iwb.getImage("shared/edit.gif");
    Link adminLink = new Link(adminImage);
      adminLink.setWindowToOpen(StaffEditor.class);
      adminLink.addParameter(StaffBusiness.PARAMETER_USER_ID,userID);

    return adminLink;
  }

  private Link getDeleteLink(int userID) {
    Image adminImage = _iwb.getImage("shared/delete.gif");
    Link adminLink = new Link(adminImage);
      adminLink.setWindowToOpen(StaffEditor.class);
      adminLink.addParameter(StaffBusiness.PARAMETER_USER_ID,userID);
      adminLink.addParameter(StaffBusiness.PARAMETER_MODE,StaffBusiness.PARAMETER_DELETE);

    return adminLink;
  }

  private Table getAlphabetTable() {
    String[] alphabet = {"A","�","B","C","D","E","�","F","G","H","I","�","J","K","L","M","N","O","�","P","Q","R","S","T","U","�","V","W","X","Y","�","Z","�","�","�",_iwrb.getLocalizedString("all","Allir")};
    Table table = new Table();
    int column = 1;

    Link link = null;
    Text divider = new Text(" - ");
      divider.setFontStyle(_alphabetLinkStyle);

    for ( int a = 0; a < alphabet.length; a++ ) {
      if ( _alphabetLetter != null && _alphabetLetter.equalsIgnoreCase(alphabet[a]) ) {
        Text text = new Text(alphabet[a]);
          if ( _styles ) {
            TextStyler styler = new TextStyler(_alphabetLinkStyle);
              styler.setStyleValue(StyleConstants.ATTRIBUTE_COLOR,_selectedLetterColor);
              styler.setStyleValue(StyleConstants.ATTRIBUTE_FONT_WEIGHT,StyleConstants.FONT_WEIGHT_BOLD);
              text.setFontStyle(styler.getStyleString());
          }
        table.add(text,column,1);
      }
      else {
        link = new Link(alphabet[a]);
          if ( _styles )
            link.setStyle(_alphabetName);
          link.addParameter(StaffBusiness.PARAMETER_LETTER,alphabet[a]);
        table.add(link,column,1);
      }
      column++;
    }

    return table;
  }

  private Link getAdminButtons() {
    Image adminImage = _iwb.getImage("shared/edit.gif");
    Link adminLink = new Link(adminImage);
      adminLink.setWindowToOpen(com.idega.core.user.presentation.UserModule.class);

    return adminLink;
  }

  private void handleParameters(IWContext iwc) {
    if ( iwc.getParameter(StaffBusiness.PARAMETER_LETTER) != null ) {
      _alphabetLetter = iwc.getParameter(StaffBusiness.PARAMETER_LETTER);
      _hasAlphabetLetter = true;
      if ( _alphabetLetter != null && _alphabetLetter.equalsIgnoreCase(_iwrb.getLocalizedString("all","Allir")) )
        _hasAlphabetLetter = false;
    }
    else {
      _alphabetLetter = _iwrb.getLocalizedString("all","Allir");
      _hasAlphabetLetter = false;
    }

    if ( iwc.getParameter(StaffBusiness.PARAMETER_USER_ID) != null ) {
      try {
        _userID = Integer.parseInt(iwc.getParameter(StaffBusiness.PARAMETER_USER_ID));
        _layout = USER;
      }
      catch ( NumberFormatException e ) {
        _userID = -1;
        _layout = ALL_STAFF;
      }

    }
  }

  private void setStyles() {
    if ( _name == null )
      _name = this.getName();
    if ( _name == null ) {
      if ( getICObjectInstanceID() != -1 )
        _name = "staff_"+Integer.toString(getICObjectInstanceID());
      else
        _name = "staff_"+Double.toString(Math.random());
    }
    _alphabetName = "alpha_"+_name;

    if ( getParentPage() != null ) {
      getParentPage().setStyleDefinition("A."+_name+":link",_linkStyle);
      getParentPage().setStyleDefinition("A."+_name+":visited",_visitedStyle);
      getParentPage().setStyleDefinition("A."+_name+":active",_activeStyle);
      getParentPage().setStyleDefinition("A."+_name+":hover",_hoverStyle);
      getParentPage().setStyleDefinition("A."+_alphabetName+":link",_alphabetLinkStyle);
      getParentPage().setStyleDefinition("A."+_alphabetName+":visited",_alphabetVisitedStyle);
      getParentPage().setStyleDefinition("A."+_alphabetName+":active",_alphabetActiveStyle);
      getParentPage().setStyleDefinition("A."+_alphabetName+":hover",_alphabetHoverStyle);
    }
    else {
      _styles = false;
    }
  }

  private void setDefaultValues() {
    _width = "100%";
    _selectedLetterColor = "#0000CC";
    _linkStyle = "font-face: Arial, Helvetica,sans-serif;font-size: 8pt;color: #000000;text-decoration: none;";
    _visitedStyle = "font-face: Arial, Helvetica,sans-serif;font-size: 8pt;color: #000000;text-decoration: none;";
    _activeStyle = "font-face: Arial, Helvetica,sans-serif;font-size: 8pt;color: #000000;text-decoration: none;";
    _hoverStyle = "font-face: Arial, Helvetica,sans-serif;font-size: 8pt;color: #000000;text-decoration: underline;";
    _alphabetLinkStyle = "font-face: Arial, Helvetica,sans-serif;font-size: 8pt;color: #000000;text-decoration: none;";
    _alphabetVisitedStyle = "font-face: Arial, Helvetica,sans-serif;font-size: 8pt;color: #000000;text-decoration: none;";
    _alphabetActiveStyle = "font-face: Arial, Helvetica,sans-serif;font-size: 8pt;color: #000000;text-decoration: none;";
    _alphabetHoverStyle = "font-face: Arial, Helvetica,sans-serif;font-size: 8pt;color: #000000;text-decoration: underline;";
    _textStyle = "font-face: Arial, Helvetica,sans-serif;font-size: 8pt;color: #000000;";
    _headlineStyle = "font-face: Arial, Helvetica,sans-serif;font-weight:bold;font-size: 8pt;color: #000000;";
    _headlineStyle = "font-face: Arial, Helvetica,sans-serif;font-weight:bold;font-size: 10pt;color: #000000;";

    _showAge = true;
    //_showGender = true;
    _showEducation = true;
    _showTitle = true;
    _showBeganWork = true;
    _showArea = true;
    _showImage = true;
    _showMetaData = true;
    _showListTitle = false;
    _showWorkPhone = true;
    _showListWorkPhone = false;
    _showMobilePhone = true;
  }

  public void setShowAlphabet(boolean showAlphabet) {
    _addAlphabet = showAlphabet;
  }

  public void setShowAge(boolean showAge) {
    _showAge = showAge;
  }

  public void setShowWorkPhone(boolean showWorkPhone) {
    _showWorkPhone = showWorkPhone;
  }

  public void setShowListWorkPhone(boolean showWorkPhone) {
    _showListWorkPhone = showWorkPhone;
  }

  public void setShowMobilePhone(boolean showMobilePhone) {
    _showMobilePhone = showMobilePhone;
  }

  /*public void setShowGender(boolean showGender) {
    _showGender = showGender;
  }*/

  public void setShowEducation(boolean showEducation) {
    _showEducation = showEducation;
  }

  public void setShowTitle(boolean showTitle) {
    _showTitle = showTitle;
  }

  public void setShowListTitle(boolean showTitle) {
    _showListTitle = showTitle;
  }

  public void setShowBeganWork(boolean showBeganWork) {
    _showBeganWork = showBeganWork;
  }

  public void setShowArea(boolean showArea) {
    _showArea = showArea;
  }

  public void setShowImage(boolean showImage) {
    _showImage = showImage;
  }

  public void setShowExtraInfo(boolean showExtraInfo) {
    _showMetaData = showExtraInfo;
  }

  public void setImageWidth(String width) {
    _imageWidth = width;
  }

  public void setImageHeight(String height) {
    _imageHeight = height;
  }

  public void setNameWidth(String width) {
    _nameWidth = width;
  }

  public void setAttributesWidth(String width) {
    _attributesWidth = width;
  }

  public void setTextStyle(String style) {
    _textStyle = style;
  }

  public void setHeadlineStyle(String style) {
    _headlineStyle = style;
  }

  public void setDivisionStyle(String style) {
    _divisionStyle = style;
  }

  public void setLinkStyle(String style,String style2,String style3,String style4) {
    _linkStyle = style;
    _visitedStyle = style2;
    _activeStyle = style3;
    _hoverStyle = style4;
  }

  public void setAlphabetLinkStyle(String style,String style2,String style3,String style4) {
    _alphabetLinkStyle = style;
    _alphabetVisitedStyle = style2;
    _alphabetActiveStyle = style3;
    _alphabetHoverStyle = style4;
  }

  public void setWidth(String width) {
    _width = width;
  }

  public void setSelectedLetterColor(String color) {
    _selectedLetterColor = color;
  }

  public void setZebraColors(String color1,String color2) {
    _zebraColor1 = color1;
    _zebraColor2 = color2;
  }

  public void setLayout(int layout) {
    _layout = layout;
  }

  public boolean deleteBlock(int ICObjectInstanceId) {
      return true;
  }

  public String getBundleIdentifier(){
    return IW_BUNDLE_IDENTIFIER;
  }

  public Object clone() {
    StaffBrowser obj = null;
    try {
      obj = (StaffBrowser)super.clone();

      if (this._myTable != null) {
        obj._myTable=(Table)this._myTable.clone();
      }
    }
    catch(Exception ex) {
      ex.printStackTrace(System.err);
    }
    return obj;
  }

}
