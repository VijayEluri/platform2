package com.idega.block.text.presentation;

/**
 * Title:
 * Description:
 * Copyright:    Copyright (c) 2000-2001 idega.is All Rights Reserved
 * Company:      idega
  *@author <a href="mailto:aron@idega.is">Aron Birkir</a>
 * @version 1.2
 */

import java.sql.*;
import java.util.*;
import java.io.*;
import com.idega.util.*;
import com.idega.presentation.text.*;
import com.idega.presentation.*;
import com.idega.presentation.ui.*;
import com.idega.block.text.data.*;
import com.idega.block.text.business.*;
import com.idega.data.*;
import com.idega.core.localisation.business.ICLocaleBusiness;
import com.idega.core.accesscontrol.business.AccessControl;
import com.idega.util.text.*;
import com.idega.idegaweb.IWResourceBundle;
import com.idega.idegaweb.IWBundle;
import com.idega.idegaweb.IWMainApplication;
import com.idega.block.IWBlock;
import com.idega.core.data.ICFile;


public class TextReader extends Block implements IWBlock{

private boolean isAdmin=false;

private String sLocaleId;
private String sAttribute = null;
private String adminURL = "/text/textadmin.jsp";

private int iTextId = -1;
private int textSize = 1;
private int tableTextSize = 1;
private int headlineSize = 2;
private String tableWidth = "";
private String textBgColor = null;
private String textColor = "#000000";
private String headlineBgColor = null;
private String headlineColor = "#000000";
private String tableAlignment = "top";
private String textWidth = "100%";
private String textStyle = "";
private String textAlignment = "left";
private String headlineStyle = "";
private String spaceBetweenHeadlineAndBody = null;
private boolean displayHeadline=true;
private boolean enableDelete=true;
private boolean reverse = false;
private boolean crazy = false;
private boolean viewall = false;
private boolean newobjinst = false;
private boolean newWithAttribute = false;

public static String prmTextId = "txtr.textid";

private IWBundle iwb;
private IWResourceBundle iwrb;
private final static String IW_BUNDLE_IDENTIFIER="com.idega.block.text";

  public TextReader(){
  }

  public TextReader(String sAttribute){
    this();
    this.iTextId = -1;
    this.sAttribute = sAttribute;
  }

  public TextReader(int iTextId){
    this();
    this.iTextId = iTextId;
  }

  public void main(IWContext iwc) throws Exception {
    isAdmin = iwc.hasEditPermission(this);
    iwb = getBundle(iwc);
    iwrb = getResourceBundle(iwc);
    Locale locale = iwc.getCurrentLocale();

    TxText txText = null;
    LocalizedText locText = null;
    ContentHelper ch = null;
    Table T = new Table();
    T.setCellpadding(0);
    T.setCellspacing(0);
    T.setBorder(0);
    T.setWidth(textWidth);

    if(iTextId < 0){
      String sTextId = iwc.getParameter(prmTextId );
      if(sTextId != null)
        iTextId = Integer.parseInt(sTextId);
      else if(getICObjectInstanceID() > 0){
        iTextId = TextFinder.getObjectInstanceTextId(getICObjectInstance());
        if(iTextId <= 0 ){
          newobjinst = true;
        }
      }
    }
    int iLocaleId = ICLocaleBusiness.getLocaleId(locale);

    if(iTextId > 0) {
      txText = new TxText((iTextId));
    }
    else if ( sAttribute != null ){
      txText = TextFinder.getText(sAttribute);
      newWithAttribute = true;
    }

    boolean hasId = false;

    if(txText != null){
      iTextId = txText.getID();
      ch = ContentFinder.getContentHelper(txText.getContentId(),iLocaleId);
      if(ch!=null)
      locText = ch.getLocalizedText();
      //locText = TextFinder.getLocalizedText(txText.getID(),iLocaleId);
      hasId = true;
    }

    if(ch!= null && locText != null){
       T.add(getTextTable(txText,locText,ch),1,1);

    }
    if(isAdmin){
      T.add(getAdminPart(iTextId,enableDelete,newobjinst,newWithAttribute,hasId),1,2);
    }

    T.setBorder(0);

    add(T);
  }

  public PresentationObject getTextTable(TxText txText,LocalizedText locText,ContentHelper contentHelper) throws IOException,SQLException {
    Table T = new Table();
    T.setCellpadding(0);
    T.setCellspacing(0);
    T.setBorder(0);
    int headerRow = 1;
    int bodyRow = 2;
    int row = 1;

    T.setWidth("100%");
    String sHeadline = locText.getHeadline()!= null ? locText.getHeadline():"";
    Text headline = new Text(sHeadline);
    headline.setFontSize(headlineSize);
    headline.setFontColor(headlineColor);
    headline.setBold();
    headline.setAttribute("class","headlinetext");
    headline.setFontStyle(headlineStyle);

    String textBody = locText.getBody()!= null ? locText.getBody():"";

    if ( reverse ) {
      textBody = TextFormatter.textReverse(textBody);
    }
    if ( crazy ) {
      textBody = TextFormatter.textCrazy(textBody);
    }

    textBody = TextFormatter.formatText(textBody,tableTextSize,"100%");

    Text body = new Text(textBody);
    body.setFontSize(textSize);
    body.setFontColor(textColor);
    body.setAttribute("class","bodytext");
    body.setFontStyle(textStyle);

    if(spaceBetweenHeadlineAndBody !=null){
      T.setHeight(spaceBetweenHeadlineAndBody);
      bodyRow = 3;
    }

    Image bodyImage;

    ///////////////// Image /////////////////////

    List files = contentHelper.getFiles();
    if(files!=null){
      try{
      ICFile imagefile = (ICFile)files.get(0);
      int imid = imagefile.getID();
      String att = imagefile.getMetaData(TextEditorWindow.imageAttributeKey);

			//System.err.println("image metadata : "+att+" "+TextEditorWindow.imageAttributeKey);
      Image textImage = new Image(imid);
      if(att != null)
        textImage.setAttributes(getAttributeMap(att));
      T.add(textImage,1,bodyRow);
      }
      catch(SQLException ex){
        ex.printStackTrace();
      }
    }
    ////////////////////////////////////////////
    if ( displayHeadline ) {
      if ( headline.getText() != null ) {
        Anchor headlineAnchor = new Anchor(headline,headline.getText());
        headlineAnchor.setFontColor(headlineColor);
        T.add(headlineAnchor ,1,headerRow);
        T.add(body,1,bodyRow);
      }
    }
    else {
      bodyRow = 1;
      T.add(body,1,bodyRow);
    }

    if( headlineBgColor != null )
      T.setRowColor(headerRow,headlineBgColor);
    if( textBgColor != null )
      T.setRowColor(bodyRow,textBgColor);
    T.setAlignment(1,bodyRow,textAlignment);

    return T;
  }

  public PresentationObject getAdminPart(int iTextId,boolean enableDelete,boolean newObjInst,boolean newWithAttribute,boolean hasId){
    Table T = new Table();
    T.setCellpadding(2);
    //T.setCellspacing(2);
    T.setBorder(0);

    if(iTextId > 0){
    Link breyta = new Link(iwb.getImage("/shared/edit.gif"));
      breyta.setWindowToOpen(TextEditorWindow.class);
      breyta.addParameter(TextEditorWindow.prmTextId,iTextId);
    T.add(breyta,1,1);

      if ( enableDelete ) {
        Link delete = new Link(iwb.getImage("/shared/delete.gif"));
        delete.setWindowToOpen(TextEditorWindow.class);
        delete.addParameter(TextEditorWindow.prmDelete,iTextId);
        T.add(delete,2,1);
      }
    }
    if(newObjInst && !hasId){
      Link newLink = new Link(iwb.getImage("/shared/create.gif"));
      newLink.setWindowToOpen(TextEditorWindow.class);
      if(newObjInst)
        newLink.addParameter(TextEditorWindow.prmObjInstId,getICObjectInstanceID());
      else if(newWithAttribute)
        newLink.addParameter(TextEditorWindow.prmAttribute,sAttribute);

      T.add(newLink,3,1);
    }
    //T.setAlignment(1,1,"left");
    //T.setAlignment(2,1,"right");
    return T;

  }

  public boolean deleteBlock(int instanceid){
    return TextBusiness.deleteBlock(instanceid);
  }

  public void setAdmin(boolean isAdmin){
    this.isAdmin=isAdmin;
  }

  public void setTextBgColor(String textBgColor) {
    this.textBgColor=textBgColor;
  }

  public void setTextColor(String textColor) {
    this.textColor=textColor;
  }

	public void setBackgroundColor(String BgColor) {
    this.headlineBgColor=BgColor;
		this.textBgColor=BgColor;
  }

  public void setHeadlineBgColor(String headlineBgColor) {
    this.headlineBgColor=headlineBgColor;
  }

  public void setHeadlineColor(String headlineColor) {
    this.headlineColor=headlineColor;
  }

  public void setTextSize(int textSize) {
    this.textSize=textSize;
  }

  public void setTableTextSize(int tableTextSize) {
    this.tableTextSize=tableTextSize;
  }

  public void setTableWidth(String tableWidth) {
    this.tableWidth=tableWidth;
  }

  public void setHeadlineSize(int headlineSize) {
    this.headlineSize=headlineSize;
  }

  public void setTextStyle(String textStyle) {
    this.textStyle=textStyle;
  }

  public void setTextAlignment(String alignment) {
    textAlignment = alignment;
  }

  public void setHeadlineStyle(String headlineStyle) {
    this.headlineStyle=headlineStyle;
  }

  public void displayHeadline(boolean displayHeadline) {
    this.displayHeadline=displayHeadline;
  }

  public void setEnableDelete(boolean enableDelete) {
    this.enableDelete=enableDelete;
  }

  /**
   * Sets alignment for the table around the text - added by gimmi@idega.is
   */
  public void setAlignment(String alignment) {
    this.tableAlignment = (alignment);
  }

  public void setWidth(String textWidth) {
    this.textWidth=textWidth;
  }

  public void setSpaceAfterHeadlin(String space){
    spaceBetweenHeadlineAndBody = space;
  }

  public void setReverse() {
    this.reverse=true;
  }

  public void setCrazy() {
    this.crazy=true;
  }

  public void setViewAll() {
    this.viewall=true;
  }

  public String getBundleIdentifier(){
  return IW_BUNDLE_IDENTIFIER;
  }

public synchronized Object clone() {
    TextReader obj = null;
    try {
      obj = (TextReader)super.clone();

      obj.sLocaleId = this.sLocaleId;
      obj.sAttribute = this.sAttribute;
      obj.adminURL = this.adminURL;

      obj.iTextId = this.iTextId;
      obj.textSize = this.textSize;
      obj.tableTextSize = this.tableTextSize;
      obj.headlineSize = this.headlineSize;
      obj.tableWidth = this.tableWidth;

      obj.textBgColor = this.textBgColor;
      obj.textColor = this.textColor;
      obj.headlineBgColor = this.headlineBgColor;
      obj.headlineColor = this.headlineColor;
      obj.tableAlignment = this.tableAlignment;
      obj.textWidth = this.textWidth;
      obj.textStyle = this.textStyle;
      obj.headlineStyle = this.headlineStyle;
      obj.displayHeadline = this.displayHeadline;
      obj.enableDelete = this.enableDelete;
      obj.viewall = this.viewall;
      obj.newobjinst = this.newobjinst;
      obj.newWithAttribute = this.newWithAttribute;
      obj.spaceBetweenHeadlineAndBody = this.spaceBetweenHeadlineAndBody;

    }
    catch(Exception ex) {
      ex.printStackTrace(System.err);
    }
    return obj;
  }
}
