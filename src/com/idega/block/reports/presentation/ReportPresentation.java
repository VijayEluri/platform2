package com.idega.block.reports.presentation;

import com.idega.block.finance.data.*;
import com.idega.presentation.IWContext;
import com.idega.presentation.ui.*;
import com.idega.presentation.Table;
import com.idega.presentation.PresentationObject;
import com.idega.presentation.text.*;
import java.sql.SQLException;

/**
 * Title:
 * Description:
 * Copyright:    Copyright (c) 2001
 * Company:      idega multimedia
 * @author       <a href="mailto:aron@idega.is">aron@idega.is</a>
 * @version 1.0
 */

public abstract class ReportPresentation extends com.idega.presentation.Block {

  protected boolean isAdmin = false;
  protected static String MiddleColor,LightColor,DarkColor,WhiteColor,TextFontColor,HeaderFontColor,IndexFontColor;
  protected Table Frame,MainFrame,HeaderFrame;
  protected static int BORDER = 0;
  protected String sHeader = null;
  protected static int fontSize = 1;
  protected static boolean fontBold = false;
  protected static String styleAttribute = "font-size: 8pt";
  private static int iBorder = 2;
	 private final static String IW_BUNDLE_IDENTIFIER="com.idega.block.reports";


	public String getBundleIdentifier(){
    return IW_BUNDLE_IDENTIFIER;
  }
  public ReportPresentation(String sHeader){
    LightColor = "#D7DADF";
    MiddleColor = "#9fA9B3";
    DarkColor = "#27334B";
    WhiteColor = "#FFFFFF";
    TextFontColor = "#000000";
    HeaderFontColor = DarkColor;
    IndexFontColor = "#000000";
    this.sHeader = sHeader;
  }

  public ReportPresentation(){
    LightColor = "#D7DADF";
    MiddleColor = "#9fA9B3";
    DarkColor = "#27334B";
    WhiteColor = "#FFFFFF";
    TextFontColor = "#000000";
    HeaderFontColor = DarkColor;
    IndexFontColor = "#000000";
    this.sHeader = null;
  }

  protected abstract void control(IWContext iwc);

  public void setColors(String LightColor,String MainColor,String DarkColor){
    if(LightColor.startsWith("#"))
      this.LightColor = LightColor;
    if(MainColor.startsWith("#"))
      this.MiddleColor = MainColor;
    if(DarkColor.startsWith("#"))
      this.DarkColor = DarkColor;
  }
  public void setBorder(int border){
    this.iBorder = border;
  }
  public void setHeaderText(String sHeader){
    this.sHeader = sHeader;
  }
  public void setTextFontColor(String color){
    this.TextFontColor = color;
  }
  public void setHeaderFontColor(String color){
    this.HeaderFontColor = color;
  }
  public void setIndexFontColor(String color){
    this.IndexFontColor = color;
  }
  public void setTextFontSize(int size){
    this.fontSize = size;
  }
  public void setTextFontBold(boolean bold){
    this.fontBold = bold;
  }
  public void setStyleAttribute(String style){
    this.styleAttribute = style;
  }
  protected void makeView(){
    this.makeMainFrame();
    this.makeFrame();
    this.makeHeader();
  }
  protected void makeMainFrame(){
    MainFrame = new Table(1,2);
    MainFrame.setWidth("100%");
    MainFrame.setCellspacing(0);
    MainFrame.setCellpadding(0);
    MainFrame.setBorder(BORDER);
    add(MainFrame);
  }
  protected void makeFrame(){
    Frame = new Table(1,2);
    Frame.setCellspacing(0);
    Frame.setCellpadding(0);
    Frame.setWidth("100%");
    Frame.setBorder(BORDER);
    this.addFrame();
  }
  protected void makeHeader(){
    HeaderFrame = new Table();
    if(sHeader != null){
      HeaderFrame = new Table(2,1);
      HeaderFrame.setColumnAlignment(2,"right");
      Text T = new Text(this.sHeader);
      T.setBold();
      T.setFontColor(this.DarkColor);
      HeaderFrame.add(T,1,1);
    }
    HeaderFrame.setBorder(BORDER);
    this.addHeader(HeaderFrame);
  }
  protected void addFrame(){
    Table BorderTable = new Table(1,1);
    BorderTable.setBorder(BORDER);
    BorderTable.setCellpadding(this.iBorder);
    BorderTable.setCellspacing(0);
    BorderTable.setColor(DarkColor);
    BorderTable.setWidth("100%");
    Table whiteTable = new Table(1,1);
    whiteTable.setBorder(BORDER);
    whiteTable.setColor(WhiteColor);
    whiteTable.setCellpadding(2);
    whiteTable.setCellspacing(0);
    whiteTable.setWidth("100%");
    whiteTable.add(Frame);
    BorderTable.add(whiteTable);
    this.MainFrame.add(BorderTable,1,2);
  }
  protected void addMain(PresentationObject T){
    this.Frame.add(T,1,2);
  }

  protected void addLinks(PresentationObject T){
    this.MainFrame.add(T,1,1);
  }
  protected void addHeader(PresentationObject T){
    this.Frame.add(T,1,1);
  }
  protected void addToHeader(PresentationObject T){
    this.HeaderFrame.add(T);
  }
  protected void addToRightHeader(PresentationObject T){
    if(sHeader != null)
    this.HeaderFrame.add(T,2,1);
  }
  protected void addMsg(PresentationObject T){

  }
  public static Text formatText(String s){
    Text T= new Text();
    if(s!=null){
      T= new Text(s);
      if(fontBold)
      T.setBold();
      T.setFontColor(TextFontColor);
      T.setFontSize(fontSize);
    }
    return T;
  }
  public static Text formatText(int i){
    return formatText(String.valueOf(i));
  }
  protected static void setStyle(InterfaceObject O){
    O.setAttribute("style",styleAttribute);
  }
  public void main(IWContext iwc){
    isAdmin = iwc.hasEditPermission(this);

    control(iwc);
  }
}// class TariffKeyEditor
