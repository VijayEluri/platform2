/*
 * $Id: MainPage.java,v 1.5 2001/10/05 08:05:45 tryggvil Exp $
 *
 * Copyright (C) 2001 Idega hf. All Rights Reserved.
 *
 * This software is the proprietary information of Idega hf.
 * Use is subject to license terms.
 *
 */
package is.idegaweb.campus.templates;

import java.io.*;
import com.idega.presentation.ui.*;
import com.idega.jmodule.*;
import com.idega.presentation.*;
import com.idega.presentation.text.*;
import com.idega.data.*;
import com.idega.idegaweb.IWBundle;


/**
 *
 * @author <a href="mailto:aron@idega.is">aron@idega.is</a>
 * @version 1.0
 */
public class MainPage extends Page{

  private Table MainTable,HeaderTable,SubHeaderTable,TitleTable,RightTable,myTable;
  private Table LeftTable,TabTable;
  private Table content;
  private Image LeftHeaderImage,RightHeaderImage;
  private Image HeaderTiler,TitleTiler,SubHeaderTiler;
  private Image Logo,LowerLogo;
  private Image MenuTitle,MainTitle,RightTitle;
  private int BORDER = 0;
  private String sAlignment = "center";
  private Image Face = new Image("/pics/template/face.gif");
  private Image BottomLogo = new Image("/pics/template/bottomlogo.gif");
  protected IWBundle iwb;
  private final static String IW_BUNDLE_IDENTIFIER="is.idegaweb.campus";
  private boolean useRightTable = true;

  public  MainPage(){
    super();
    content = new Table(1,4);
    initContent();

  }

  public String getBundleIdentifier(){
    return IW_BUNDLE_IDENTIFIER;
  }

  public void setToUseRightTable(boolean use){
    useRightTable = use;
  }

  public void setBorder(int iBorder){
    BORDER = iBorder;
  }
  private void initTilers(){

    HeaderTiler = iwb.getImage("/template/headertiler.gif");
    SubHeaderTiler = iwb.getImage("/template/subtiler.gif");
    TitleTiler = iwb.getImage("/template/titletiler.gif");
  }
  private void initLogos(){
    Face = iwb.getImage("/template/face.gif");
    BottomLogo = iwb.getImage("/template/bottomlogo.gif");
    Face.setWidth(130);
    Face.setHeight(79);
    BottomLogo.setWidth(135);
    BottomLogo.setHeight(88);
  }
  public void initContent() {
    setAllMargins(0);
    setAlinkColor("black");
    setVlinkColor("black");
    setLinkColor("black");
    setHoverColor("#4D6476");
    setStyleSheetURL("/style/style.css");
    content.setAlignment(sAlignment);
    content.setCellpadding(0);
    content.setCellspacing(0);
    content.setWidth("100%");
    content.setHeight("100%");
    content.setBorder(BORDER);
    content.setHeight(1,"54");
    content.setHeight(2,"79");
    content.setHeight(3,"18");
    content.setHeight(4,"100%");
    //content.setWidth(1,"100%");
    content.setVerticalAlignment(1,3,"top");
    initTables();

  }

  public void Content(){
    InsertTilers();
    InsertTopLogo();
  }
  public void initTables(){
    initMyTable();
    initMainTable();
    initHeaderTable();
    initSubHeaderTable();
    initTitleTable();
    initTabTable();
  }
  public void initMainTable(){
    int cols = 2;
    if( useRightTable){
      cols = 3;
    }
    MainTable = new Table(cols,1);
    MainTable.setBorder(BORDER);
    MainTable.setVerticalAlignment(1,1,"top");
    MainTable.setVerticalAlignment(2,1,"top");

    MainTable.setCellpadding(0);
    MainTable.setCellspacing(0);
    MainTable.setHeight("100%");
    MainTable.setWidth("100%");
    MainTable.setWidth(1,"130");
    MainTable.setWidth(2,"100%");
  }

  private void mainInit(){
    if(useRightTable){
      MainTable.add(getRightTable(),3,1);
      MainTable.setWidth(3,"130");
      MainTable.setVerticalAlignment(3,1,"top");
    }

    MainTable.add(getMyTable(),2,1);
    MainTable.add(getLeftTable(),1,1);
    content.add(getHeaderTable(),1,1);
    content.add(getSubHeaderTable(),1,2);
    content.add(getTitleTable(),1,3);
    content.add(getMainTable(),1,4);

    initLogos();
    Content();

  }

  public void initMyTable(){
    myTable = new Table(1,1);
    myTable.setBorder(BORDER);
    myTable.setVerticalAlignment(1,1,"top");
    myTable.setCellpadding(12);
    myTable.setCellspacing(0);
    myTable.setHeight("100%");
    myTable.setWidth("100%");
  }

  public Table getMainTable(){
    return MainTable;
  }
  public Table getMyTable(){
    return myTable;
  }
  public void initHeaderTable(){
    HeaderTable = new Table(3,1);
    HeaderTable.setBorder(BORDER);
    HeaderTable.setCellpadding(0);
    HeaderTable.setCellspacing(0);
    HeaderTable.setHeight("54");
    HeaderTable.setWidth("100%");
    HeaderTable.setWidth(1,"130");
    HeaderTable.setWidth(2,"100%");
    HeaderTable.setWidth(3,"130");
    HeaderTable.setAlignment(1,1,"left");
    HeaderTable.setAlignment(3,1,"right");
  }
  public Table getHeaderTable(){
    return HeaderTable;
  }
  public void initSubHeaderTable(){
    SubHeaderTable = new Table(3,1);
    SubHeaderTable.setBorder(BORDER);
    SubHeaderTable.setCellpadding(0);
    SubHeaderTable.setCellspacing(0);
    SubHeaderTable.setHeight("79");
    SubHeaderTable.setWidth("100%");
    SubHeaderTable.setWidth(3,"100%");
    SubHeaderTable.setWidth(1,"130");
    SubHeaderTable.setAlignment(2,1,"left");
    //SubHeaderTable.setAlignment(3,1,"right");
  }
  public Table getSubHeaderTable(){
    return SubHeaderTable;
  }
  public void initTitleTable(){
    TitleTable = new Table(3,1);
    TitleTable.setBorder(BORDER);
    TitleTable.setCellpadding(0);
    TitleTable.setCellspacing(0);
    TitleTable.setHeight("17");
    TitleTable.setWidth("100%");
    TitleTable.setWidth(1,"130");
    TitleTable.setWidth(2,"100%");
    TitleTable.setWidth(3,"135");
    TitleTable.setAlignment(1,1,"left");
    TitleTable.setAlignment(1,1,"left");
    TitleTable.setAlignment(1,1,"right");
  }
  public Table getTitleTable(){
    return TitleTable;
  }
  public Table getLeftTable(){
    LeftTable = new Table(1,3);
    LeftTable.setHeight("100%");
    LeftTable.setHeight(1,2,"100%");
    LeftTable.setWidth("130");
    LeftTable.setCellpadding(0);
    LeftTable.setCellspacing(0);
    ///LeftTable.setWidth(1,2,"100%");
    LeftTable.setHeight(1,3,"88");
    LeftTable.setVerticalAlignment(1,1,"top");
    LeftTable.setVerticalAlignment(1,2,"top");
    LeftTable.setVerticalAlignment(1,3,"bottom");
    LeftTable.setBorder(BORDER);
    return LeftTable;
  }
  public Table getRightTable(){
    RightTable = new Table(1,3);
    RightTable.setWidth("135");
    RightTable.setColumnAlignment(1,"center");
    //RightTable.setHeight("100%");
    //RightTable.setHeight(2,"100%");
    RightTable.setCellpadding(0);
    RightTable.setCellspacing(0);

    RightTable.setVerticalAlignment(1,1,"top");
    RightTable.setVerticalAlignment(1,2,"top");
    RightTable.setVerticalAlignment(1,3,"top");
    RightTable.setBorder(BORDER);
    return RightTable;
  }
  private void initTabTable(){
    TabTable = new Table(1,2);
    TabTable.setBorder(BORDER);
    TabTable.setWidth("100%");
    TabTable.setHeight("79");
    TabTable.setHeight(2,"22");
    TabTable.setAlignment(1,2,"right");
    TabTable.setCellpadding(0);
    TabTable.setCellspacing(0);
    TabTable.setVerticalAlignment(1,2,"bottom");
  }
  private Table getTabTable(){
    return TabTable;
  }
   public void InsertTilers(){
    this.initTilers() ;
    HeaderTable.setBackgroundImage(HeaderTiler);
    SubHeaderTable.setBackgroundImage(SubHeaderTiler);
    TitleTable.setBackgroundImage(TitleTiler);
  }
  public void InsertTopLogo(){
    int num = (int) (Math.random() * 14) ;
    Image image = iwb.getImage("/template/face/face"+(num)+".jpg");
    //Image image = new Image("/pics/template/faces/face"+(num)+".jpg");
    image.setWidth(130) ;
    image.setHeight(79) ;
    this.addLogo(image) ;
  }
  public void InsertBottomLogo(){
    this.addLowerLogo(BottomLogo);
  }

  /** Insert the default logos

   *  integer parameters: 1 for FaceLogo; 2 for Both

   */

  public void InsertDefaultLogos(int count){
    this.addLogo(Face);
    if(count == 2)
      this.addLowerLogo(BottomLogo);
  }
  public void InsertBanners() {
    Image Header = iwb.getImage("/template/header.gif");
    Header.setHeight(54);
    Header.setWidth(130);
    HeaderTable.add(Header,1,1);
    Image RightHeader = iwb.getImage("/template/header2.gif");
    RightHeader.setHeight(54);
    RightHeader.setWidth(250);
    HeaderTable.add(RightHeader,3,1);
    Image Boxes = iwb.getImage("/template/boxes.gif");
    Boxes.setWidth(300);
    Boxes.setHeight(79);
    SubHeaderTable.add(Boxes,2,1);
  }
  public void InsertTitles(){
    MenuTitle = iwb.getImage("/template/menutitle.gif");
    this.addMenuTitle(MenuTitle) ;
    MainTitle = iwb.getImage("/template/maintitle.gif");
    this.addMainTitle(MainTitle) ;
    RightTitle = iwb.getImage("/template/logintitle.gif");
    this.addRightTitle(RightTitle);
  }
  public boolean isAdmin(IWContext iwc) {
    if (iwc.getSessionAttribute("member_access") != null) {
      if (iwc.getSessionAttribute("member_access").equals("admin")) {
        return true;
      }
      else {
        return false;
      }
    }
    else {
      return false;
    }
  }
  /** Width: 130 pixel
   *  Height: 79 pixel
   */

  public Table getDivider() {
    Table dividerTable = new Table(1,1);
      dividerTable.setCellpadding(0);
      dividerTable.setCellspacing(0);
      dividerTable.setAlignment("center");

    Image divider  = iwb.getImage("/template/line.gif",99,3);
    //Image divider = new Image("/pics/line.gif","",99,3);
      divider.setAlignment("center");
      divider.setVerticalSpacing(8);

    dividerTable.add(divider);

    return dividerTable;
  }

  public void main(IWContext iwc)throws Exception {
    iwb = getBundle(iwc);
    //initContent();
    mainInit();
    super.add(content);

  }

  public void setFaceLogo(Image FaceLogo){
    Face = FaceLogo;
  }
  /**
   *
   */
  public void setLeftAlignment(String align){
    MainTable.setAlignment(1,1,align);
  }
  /**
   *
   */
  public void setCenterAlignment(String align){
    MainTable.setAlignment(2,1,align);
  }
  /** Adds a PresentationObject to the middle section
   *
   */

  public void add(PresentationObject objectToAdd){
    myTable.add(objectToAdd,1,1);
  }
  /** Adds a PresentationObject to the center of the left side
   *
   */
  public void addLeft(PresentationObject objectToAdd){
    LeftTable.add(objectToAdd,1,2);
  }
  /** Adds a PresentationObject to the center of the right side
   *
   */
  public void addRight(PresentationObject objectToAdd){
    if(RightTable != null)
      RightTable.add(objectToAdd,1,2);
  }
  /** Adds a PresentationObject to the bottom of the right side
   *
   */
  public void addRightBottom(PresentationObject objectToAdd){
    if(RightTable != null)
      RightTable.add(objectToAdd,1,3);
  }
  /** Adds a PresentationObject to the titlebar on the left side
   *
   */
  public void addMenuTitle(PresentationObject objectToAdd){
    TitleTable.add(objectToAdd,1,1);
  }
  /** Adds a PresentationObject to the titlebar in the middle
   *
   */
  public void addMainTitle(PresentationObject objectToAdd){
    TitleTable.add(objectToAdd,2,1);
  }
  /** Adds a PresentationObject to the titlebar on the right side
   *
   */
  public void addRightTitle(PresentationObject objectToAdd){
    TitleTable.add(objectToAdd,3,1);
  }
  /** Adds a PresentationObject to the top of left side
   *
   */
  public void addTopLeft(PresentationObject objectToAdd){
    LeftTable.add(objectToAdd,1,1);
  }
  /** Adds a PresentationObject to the top of right side
   *
   */
  public void addTopRight(PresentationObject objectToAdd){
    if(RightTable != null)
      RightTable.add(objectToAdd,1,1);
  }
  /** Adds a divider to the top of right side
   *
   */
  public void addTopRightDivider(){
    if(RightTable != null)
      RightTable.add(getDivider(),1,1);
  }
  /** Adds a PresentationObject to the upper logo area on the left
   *
   */
  public void addLogo(PresentationObject objectToAdd){
    SubHeaderTable.add(objectToAdd,1,1);
  }
  /** Adds a PresentationObject to the lower logo area on the left
   *
   */
  public void addLowerLogo(PresentationObject objectToAdd){
    LeftTable.add(objectToAdd,1,3);
  }
  /** Adds a PresentationObject into tab area
   *
   */
  public void addTabs(PresentationObject objectToAdd){
    TabTable.add(objectToAdd,1,2);
   SubHeaderTable.add(TabTable,3,1);
  }
}

