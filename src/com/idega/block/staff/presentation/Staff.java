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
import com.idega.core.data.Email;
import com.idega.block.IWBlock;
import com.idega.block.staff.data.*;
import com.idega.block.staff.business.*;
import com.idega.core.accesscontrol.business.AccessControl;
import com.idega.util.idegaTimestamp;
import com.idega.util.GenericUserComparator;
import com.idega.util.text.TextStyler;
import com.idega.util.text.StyleConstants;
import com.idega.idegaweb.IWResourceBundle;
import com.idega.idegaweb.IWBundle;
import com.idega.idegaweb.IWMainApplication;


public class Staff extends Block implements IWBlock {

private boolean _isAdmin=false;
private int _userID = -1;

public static final int ALL_STAFF = 1;
public static final int DIVISION_STAFF = 2;
public static final int USER = 3;
private int _layout = ALL_STAFF;

private boolean _addAlphabet = true;
private boolean _hasAlphabetLetter;
private String _alphabetLetter;
private String _selectedLetterColor;

private int row = -1;
private String _name;
private String _alphabetName;
private boolean _styles = true;

private boolean _showAge;
private boolean _showGender;
private boolean _showEducation;
private boolean _showTitle;
private boolean _showSchool;
private boolean _showBeganWork;
private boolean _showArea;
private boolean _showImage;
private boolean _showMetaData;

private String _imageWidth;
private String _imageHeight;

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

private IWBundle _iwb;
private IWResourceBundle _iwrb;
private final static String IW_BUNDLE_IDENTIFIER="com.idega.block.staff";

private Table _myTable;

  public Staff(){
    setDefaultValues();
  }

  public void main(IWContext iwc) throws Exception {
    _iwb = iwc.getApplication().getBundle(IW_CORE_BUNDLE_IDENTIFIER);
    _iwrb = getResourceBundle(iwc);
    _isAdmin = iwc.hasEditPermission(this);

    row = 1;
    _myTable = new Table();
      _myTable.setWidth(_width);

    if ( _isAdmin ) {
      _myTable.add(getAdminButtons(),1,row);
      row++;
    }

    handleParameters(iwc);
    _myTable.add(getStaff(iwc),1,row);

    add(_myTable);
  }

  private Table getStaff(IWContext iwc) {
    setStyles();

    Table staffTable = new Table();
      staffTable.setWidth("100%");

    if ( _addAlphabet ) {
      _myTable.add(getAlphabetTable(),1,row);
      row++;
    }

    switch (_layout) {
      case ALL_STAFF:
        getAllStaff(iwc,staffTable);
        break;
      case DIVISION_STAFF:
        break;
      case USER:
        getUser(iwc,staffTable);
        break;
    }

    return staffTable;
  }

  private void getAllStaff(IWContext iwc, Table staffTable) {
    Table table = new Table();
      table.setWidth("100%");

    int staffRow = 1;

    List users = null;
    if ( _hasAlphabetLetter )
      users = StaffFinder.getAllUsersByLetter(iwc,_alphabetLetter);
    else
      users = StaffFinder.getAllUsers(iwc);

    if ( users != null ) {
      GenericUserComparator comparator = new GenericUserComparator(GenericUserComparator.NAME);
      Collections.sort(users,comparator);
      for ( int a = 0; a < users.size(); a++ ) {
        User user = (User)users.get(a);
        Email email = StaffFinder.getUserEmail(user);

        Link userLink = getStaffLink(user);
        Link emailLink = getEmailLink(email);

        table.add(userLink,1,staffRow);
        if ( emailLink != null )
          table.add(emailLink,2,staffRow);

        if ( _isAdmin ) {
          table.add(getEditLink(user),3,staffRow);
        }

        staffRow++;
      }
    }

    table.setWidth(1,"100%");
    staffTable.add(table,1,row);
  }

  private void getUser(IWContext iwc,Table staffTable) {
    User user = StaffFinder.getUser(_userID);
    StaffInfo staffInfo = StaffFinder.getStaffInfo(_userID);
    StaffMetaData[] staffMeta = StaffFinder.getMetaData(_userID);

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
    if ( staffInfo != null && staffInfo.getImageID() != -1 ) {
      try {
        image = new Image(staffInfo.getImageID());
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

    if ( user != null ) {
      Text name = new Text(_iwrb.getLocalizedString("user_name","Name")+":");
        name.setFontStyle(_headlineStyle);
      Text nameText = new Text(user.getName());
        nameText.setFontStyle(_textStyle);

      textTable.add(name,column,tableRow);
      textTable.add(nameText,column+1,tableRow);
      tableRow++;

      idegaTimestamp dateOfBirth = null;
      if ( user.getDateOfBirth() != null )
        dateOfBirth = new idegaTimestamp(user.getDateOfBirth());
      idegaTimestamp dateToday = new idegaTimestamp();

      int userAge = 0;
      if ( dateOfBirth != null )
        userAge = (new idegaTimestamp().getDaysBetween(dateOfBirth,dateToday))/365;

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
        if ( staffInfo != null )
          titleText.setText(staffInfo.getTitle());
        titleText.setFontStyle(_textStyle);

      if ( _showTitle ) {
        textTable.add(title,column,tableRow);
        textTable.add(titleText,column+1,tableRow);
        tableRow++;
      }

      Text school = new Text(_iwrb.getLocalizedString("user_school","School")+":");
        school.setFontStyle(_headlineStyle);
      Text schoolText = new Text("");
        if ( staffInfo != null )
          schoolText.setText(staffInfo.getSchool());
        schoolText.setFontStyle(_textStyle);

      if ( _showSchool ) {
        textTable.add(school,column,tableRow);
        textTable.add(schoolText,column+1,tableRow);
        tableRow++;
      }

      Text education = new Text(_iwrb.getLocalizedString("user_education","Education")+":");
        education.setFontStyle(_headlineStyle);
      Text educationText = new Text("");
        if ( staffInfo != null )
          educationText.setText(staffInfo.getEducation());
        educationText.setFontStyle(_textStyle);

      if ( _showEducation ) {
        textTable.add(education,column,tableRow);
        textTable.add(educationText,column+1,tableRow);
        tableRow++;
      }

      Text area = new Text(_iwrb.getLocalizedString("user_area","Area")+":");
        area.setFontStyle(_headlineStyle);
      Text areaText = new Text("");
        if ( staffInfo != null )
          areaText.setText(staffInfo.getArea());
        areaText.setFontStyle(_textStyle);

      if ( _showArea ) {
        textTable.add(area,column,tableRow);
        textTable.add(areaText,column+1,tableRow);
        tableRow++;
      }

      Text beganWork = new Text(_iwrb.getLocalizedString("user_began_work","Began work")+":");
        beganWork.setFontStyle(_headlineStyle);
      Text beganWorkText = new Text("");
        if ( staffInfo != null && staffInfo.getBeganWork() != null )
          beganWorkText.setText(new idegaTimestamp(staffInfo.getBeganWork()).getLocaleDate(iwc));
        beganWorkText.setFontStyle(_textStyle);

      if ( _showBeganWork ) {
        textTable.add(beganWork,column,tableRow);
        textTable.add(beganWorkText,column+1,tableRow);
        tableRow++;
      }

      if ( staffMeta != null && staffMeta.length > 0 && _showMetaData ) {
        for ( int a = 0; a < staffMeta.length; a++ ) {
          Text meta = new Text(staffMeta[a].getAttribute()+":");
            meta.setFontStyle(_headlineStyle);
          Text metaText = new Text(staffMeta[a].getValue());
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
      index = users.indexOf(user);
    }

    Table linkTable = new Table(3,1);
      linkTable.setWidth(1,"33%");
      linkTable.setWidth(2,"33%");
      linkTable.setWidth(3,"33%");
      linkTable.setWidth("100%");

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

    textTable.setColumnVerticalAlignment(1,"top");
    textTable.setColumnVerticalAlignment(2,"top");
    userTable.setColumnVerticalAlignment(1,"top");
    userTable.setColumnVerticalAlignment(2,"top");

    staffTable.add(userTable,1,row);

    if ( _isAdmin ) {
      staffTable.add(getEditLink(user),1,row+1);
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

  private Link getStaffLink(User user) {
    String name = user.getName();

    Link link = new Link(name);
      if ( _styles )
        link.setStyle(_name);
      link.addParameter(StaffBusiness.PARAMETER_USER_ID,user.getID());

    return link;
  }

  private Link getEmailLink(Email email) {
    Link link = null;

    if ( email != null ) {
      link = new Link(email.getEmailAddress());
        if ( _styles )
          link.setStyle(_name);
        link.setURL("mailto:"+email.getEmailAddress());
    }

    return link;
  }

  private Link getEditLink(User user) {
    Image adminImage = _iwb.getImage("shared/edit.gif");
    Link adminLink = new Link(adminImage);
      adminLink.setWindowToOpen(StaffPropertyWindow.class);
      adminLink.addParameter(StaffPropertyWindow.PARAMETERSTRING_USER_ID,user.getID());

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
    _width = "300";
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

    _showAge = true;
    _showGender = true;
    _showEducation = true;
    _showTitle = true;
    _showSchool = true;
    _showBeganWork = true;
    _showArea = true;
    _showImage = true;
    _showMetaData = true;
  }

  public void setShowAlphabet(boolean showAlphabet) {
    _addAlphabet = showAlphabet;
  }

  public void setShowAge(boolean showAge) {
    _showAge = showAge;
  }

  public void setShowGender(boolean showGender) {
    _showGender = showGender;
  }

  public void setShowEducation(boolean showEducation) {
    _showEducation = showEducation;
  }

  public void setShowTitle(boolean showTitle) {
    _showTitle = showTitle;
  }

  public void setShowSchool(boolean showSchool) {
    _showSchool = showSchool;
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

  public void setTextStyle(String style) {
    _textStyle = style;
  }

  public void setHeadlineStyle(String style) {
    _headlineStyle = style;
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

  public boolean deleteBlock(int ICObjectInstanceId) {
      return true;
  }

  public String getBundleIdentifier(){
    return IW_BUNDLE_IDENTIFIER;
  }

  public Object clone() {
    Staff obj = null;
    try {
      obj = (Staff)super.clone();

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
