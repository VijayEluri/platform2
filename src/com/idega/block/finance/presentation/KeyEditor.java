package com.idega.block.finance.presentation;

import com.idega.block.finance.data.*;
import com.idega.jmodule.object.ModuleInfo;
import com.idega.jmodule.object.interfaceobject.*;
import com.idega.jmodule.object.Table;
import com.idega.jmodule.object.ModuleObject;
import com.idega.jmodule.object.textObject.*;
import java.sql.SQLException;

/**
 * Title:
 * Description:
 * Copyright:    Copyright (c) 2001
 * Company:      idega multimedia
 * @author       <a href="mailto:aron@idega.is">aron@idega.is</a>
 * @version 1.0
 */

public abstract class KeyEditor extends com.idega.jmodule.object.ModuleObjectContainer {

  protected final static int ACT1 = 1,ACT2 = 2, ACT3 = 3,ACT4  = 4;
  protected boolean isAdmin = false;
  protected String MiddleColor,LightColor,DarkColor,WhiteColor,TextFontColor,HeaderFontColor,IndexFontColor;
  protected Table Frame,MainFrame,HeaderFrame;
  protected final int BORDER = 0;
  protected String sHeader;
  protected int fontSize = 2;
  protected boolean fontBold = false;
  protected String styleAttribute = "font-size: 8pt";
  private int iBorder = 2;

  public KeyEditor(String sHeader){
    LightColor = "#D7DADF";
    MiddleColor = "#9fA9B3";
    DarkColor = "#27334B";
    WhiteColor = "#FFFFFF";
    TextFontColor = "#000000";
    HeaderFontColor = DarkColor;
    IndexFontColor = "#000000";
    this.sHeader = sHeader;
  }

  protected abstract void control(ModuleInfo modinfo);
  protected abstract ModuleObject makeLinkTable(int menuNr);

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
    MainFrame = new Table(1,4);
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
    Table HeaderFrame = new Table(2,1);
    HeaderFrame.setColumnAlignment(2,"right");
    Text T = new Text(this.sHeader);
    T.setBold();
    T.setFontColor(this.DarkColor);
    HeaderFrame.add(T,1,1);
    this.addHeader(HeaderFrame);
  }
  protected void addFrame(){
    Table BorderTable = new Table();
    BorderTable.setCellpadding(this.iBorder);
    BorderTable.setCellspacing(0);
    BorderTable.setColor(DarkColor);
    BorderTable.setWidth("100%");
    Table whiteTable = new Table();
    whiteTable.setColor(WhiteColor);
    whiteTable.setCellpadding(2);
    whiteTable.setCellspacing(0);
    whiteTable.setWidth("100%");
    whiteTable.add(Frame);
    BorderTable.add(whiteTable);
    this.MainFrame.add(BorderTable,1,2);
  }
  protected void addMain(ModuleObject T){
    this.Frame.add(T,1,2);
  }
  protected void addLinks(ModuleObject T){
    this.MainFrame.add(T,1,1);
  }
  protected void addHeader(ModuleObject T){
    this.Frame.add(T,1,1);
  }
  protected void addToRightHeader(ModuleObject T){
    this.HeaderFrame.add(T,2,1);
  }
  protected void addMsg(ModuleObject T){

  }
  public Text formatText(String s){
    Text T= new Text();
    if(s!=null){
      T= new Text(s);
      if(this.fontBold)
      T.setBold();
      T.setFontColor(this.TextFontColor);
      T.setFontSize(this.fontSize);
    }
    return T;
  }
  public Text formatText(int i){
    return formatText(String.valueOf(i));
  }
  protected void setStyle(InterfaceObject O){
    O.setAttribute("style",this.styleAttribute);
  }
  public void main(ModuleInfo modinfo){
    try{
    isAdmin = com.idega.jmodule.login.business.AccessControl.isAdmin(modinfo);
    }
    catch(SQLException sql){ isAdmin = false;}
    control(modinfo);
  }
}// class TariffKeyEditor