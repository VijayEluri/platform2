package com.idega.block.boxoffice.presentation;





import java.sql.*;

import java.util.*;

import java.io.*;

import com.idega.util.*;

import com.idega.presentation.text.*;

import com.idega.presentation.*;

import com.idega.presentation.ui.*;

import com.idega.core.localisation.presentation.ICLocalePresentation;

import com.idega.core.localisation.business.ICLocaleBusiness;

import com.idega.core.data.ICLocale;

import com.idega.block.IWBlock;

import com.idega.block.boxoffice.data.*;

import com.idega.block.boxoffice.business.*;

import com.idega.core.accesscontrol.business.AccessControl;

import com.idega.block.login.business.LoginBusiness;

import com.idega.block.text.business.TextFinder;

import com.idega.block.text.data.LocalizedText;

import com.idega.idegaweb.presentation.IWAdminWindow;

import com.idega.idegaweb.IWResourceBundle;

import com.idega.idegaweb.IWBundle;

import com.idega.idegaweb.IWMainApplication;



public class BoxCategoryChooser extends IWAdminWindow{



private final static String IW_BUNDLE_IDENTIFIER="com.idega.block.boxoffice";

private boolean _isAdmin = false;

private boolean _save = false;

private boolean _update = false;

private int _userID = -1;

private int _boxID = -1;

private int _boxCategoryID = -1;



private IWBundle _iwb;

private IWResourceBundle _iwrb;



public BoxCategoryChooser(){

  setWidth(380);

  setHeight(180);

  setMethod("get");

}



  public void main(IWContext iwc) throws Exception {

    /**

     * @todo permission

     */

    _isAdmin = true; //AccessControl.hasEditPermission(this,iwc);

    _iwb = iwc.getApplication().getBundle(IWBlock.IW_CORE_BUNDLE_IDENTIFIER);

    _iwrb = getResourceBundle(iwc);

    addTitle(_iwrb.getLocalizedString("box_category_choose","Category Chooser"));

    Locale currentLocale = iwc.getCurrentLocale(),chosenLocale;



    try {

      _userID = LoginBusiness.getUser(iwc).getID();

    }

    catch (Exception e) {

      _userID = -1;

    }



    String sLocaleId = iwc.getParameter(BoxBusiness.PARAMETER_LOCALE_DROP);



    int iLocaleId = -1;

    if(sLocaleId!= null){

      iLocaleId = Integer.parseInt(sLocaleId);

      chosenLocale = TextFinder.getLocale(iLocaleId);

    }

    else{

      chosenLocale = currentLocale;

      iLocaleId = ICLocaleBusiness.getLocaleId(chosenLocale);

    }



    if ( _isAdmin ) {

      processForm(iwc, iLocaleId, sLocaleId);

    }

    else {

      noAccess();

    }

  }



  private void processForm(IWContext iwc, int iLocaleId, String sLocaleId) {

    if ( iwc.getParameter(BoxBusiness.PARAMETER_BOX_ID) != null ) {

      try {

        _boxID = Integer.parseInt(iwc.getParameter(BoxBusiness.PARAMETER_BOX_ID));

      }

      catch (NumberFormatException e) {

        _boxID = -1;

      }

    }



    if ( iwc.getParameter(BoxBusiness.PARAMETER_MODE) != null ) {

      if ( iwc.getParameter(BoxBusiness.PARAMETER_MODE).equalsIgnoreCase(BoxBusiness.PARAMETER_CLOSE) ) {

        close();

      }

      else if ( iwc.getParameter(BoxBusiness.PARAMETER_MODE).equalsIgnoreCase(BoxBusiness.PARAMETER_SAVE) ) {

        save(iwc);

      }

    }



    initializeFields(iLocaleId);

  }



  private void initializeFields(int iLocaleID) {

    Form myForm = new Form();

      myForm.setMethod("get");



    Table formTable = new Table(1,2);

      formTable.setAlignment("center");

      formTable.setAlignment(1,2,"right");



    SelectionDoubleBox sdb = new SelectionDoubleBox(BoxBusiness.CATEGORY_SELECTION,"Not in","In");



    SelectionBox left = sdb.getLeftBox();

    left.setAttribute("style",STYLE);

    left.setHeight(6);

    left.selectAllOnSubmit();



    SelectionBox right = sdb.getRightBox();

    right.setAttribute("style",STYLE);

    right.setHeight(6);

    right.selectAllOnSubmit();



    List categoriesInBox = BoxFinder.getCategoriesInBox(_boxID);

    Iterator iter = null;

    if(categoriesInBox != null){

      iter = categoriesInBox.iterator();

      while (iter.hasNext()) {

        BoxCategory item = (BoxCategory) iter.next();

        LocalizedText locText = TextFinder.getLocalizedText(item,iLocaleID);

        String locString = "$language$";

        if ( locText != null ) {

          locString = locText.getHeadline();

        }

        right.addElement(Integer.toString(item.getID()),locString);

      }

    }



    List categoriesNotInBox = BoxFinder.getCategoriesNotInBox(_boxID);

    if(categoriesNotInBox != null){

      iter = categoriesNotInBox.iterator();

      while (iter.hasNext()) {

        BoxCategory item = (BoxCategory) iter.next();

        LocalizedText locText = TextFinder.getLocalizedText(item,iLocaleID);

        String locString = "$language$";

        if ( locText != null ) {

          locString = locText.getHeadline();

        }

        left.addElement(Integer.toString(item.getID()),locString);

      }

    }



    formTable.add(new SubmitButton(_iwrb.getLocalizedImageButton("close","CLOSE"),BoxBusiness.PARAMETER_MODE,BoxBusiness.PARAMETER_CLOSE),1,2);

    formTable.add(new SubmitButton(_iwrb.getLocalizedImageButton("save","SAVE"),BoxBusiness.PARAMETER_MODE,BoxBusiness.PARAMETER_SAVE),1,2);

    myForm.add(new HiddenInput(BoxBusiness.PARAMETER_BOX_ID,Integer.toString(_boxID)));



    formTable.add(sdb,1,1);

    myForm.add(formTable);

    add(myForm);

  }



  private void save(IWContext iwc) {

    BoxEntity box = BoxFinder.getBox(_boxID);



    String[] detach = iwc.getParameterValues(BoxBusiness.CATEGORY_SELECTION+"_left");

    if ( detach != null ) {

      for ( int a = 0; a < detach.length; a++ ) {

        BoxBusiness.detachCategory(_boxID,Integer.parseInt(detach[a]));

      }

    }



    String[] attach = iwc.getParameterValues(BoxBusiness.CATEGORY_SELECTION);

    if ( attach != null ) {

      for ( int a = 0; a < attach.length; a++ ) {

        BoxBusiness.addToBox(box,Integer.parseInt(attach[a]));

      }

    }



    setParentToReload();

    close();

  }



  private void noAccess() throws IOException,SQLException {

    close();

  }



  public String getBundleIdentifier(){

    return IW_BUNDLE_IDENTIFIER;

  }



}
