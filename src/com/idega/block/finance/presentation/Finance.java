package com.idega.block.finance.presentation;

import java.util.List;

import com.idega.block.presentation.CategoryBlock;
import com.idega.idegaweb.IWBundle;
import com.idega.idegaweb.IWResourceBundle;
import com.idega.idegaweb.block.presentation.Builderaware;
import com.idega.presentation.Block;
import com.idega.presentation.IWContext;
import com.idega.presentation.PresentationObject;
import com.idega.presentation.Table;
import com.idega.presentation.text.Link;
import com.idega.presentation.ui.Parameter;
import com.idega.presentation.util.Edit;
import com.idega.presentation.util.TextFormat;

/**
 * Title:   idegaclasses
 * Description:
 * Copyright:    Copyright (c) 2001
 * Company:
 * @author  <a href="mailto:aron@idega.is">aron@idega.is
 * @version 1.0
 */

public class Finance extends CategoryBlock implements Builderaware{

  protected final int ACT1 = 1,ACT2 = 2, ACT3 = 3,ACT4  = 4,ACT5 = 5;
  private final static String IW_BUNDLE_IDENTIFIER="com.idega.block.finance";
  public final static String CATEGORY_PROPERTY="finance_category";
  protected boolean isAdmin = false;
  protected IWResourceBundle iwrb;
  protected IWBundle iwb,core;
  boolean newobjinst = false;
  boolean administrative = true;
  private List FinanceObjects = null;
  public final static String FRAME_NAME = "fin_frame";
  public static final String prmFinanceClass = "fin_clss";
  public static final String prmAccountId = "fin_acc_id";
  public int iCategoryId = -1;
  protected TextFormat textFormat;

  //public static final String prmCategoryId = "fin_cat_id";

 public Finance(){
 	setAutoCreate(false);
 }

  public boolean getMultible(){
    return false;
  }
  public String getCategoryType(){
    return "Finance";
  }

  public void main(IWContext iwc)throws java.rmi.RemoteException{
    control(iwc);
  }

  public void initializeInMain(IWContext iwc){
    super.initializeInMain(iwc);
    init(iwc);
    if(isAdmin && administrative && getICObjectInstanceID() > 0){
      add(getAdminPart(getCategoryId(),false,newobjinst,false,iwc));
    }

  }

  public void init(IWContext iwc){
    iwrb = getResourceBundle(iwc);
    iwb = getBundle(iwc);
    core = iwc.getApplication().getCoreBundle();
    isAdmin = this.hasEditPermission();
    textFormat = TextFormat.getInstance();
    initCategoryId(iwc);
  }
  protected void control(IWContext iwc)throws java.rmi.RemoteException{

    Table T = new Table();
    T.setWidth("100%");
   // T.setHeight("100%");
    T.setCellpadding(0);
    T.setCellspacing(0);
    
    FinanceIndex index = new FinanceIndex(getCategoryId());
    if(FinanceObjects !=null)
      index.addFinanceObjectAll(FinanceObjects);
    T.add(index,1,2);
    add(T);
  }

  public String getBundleIdentifier(){
    return IW_BUNDLE_IDENTIFIER;
  }

  private PresentationObject getAdminPart(int iCategoryId,boolean enableDelete,boolean newObjInst,boolean info,IWContext iwc){
    Table T = new Table(3,1);
    T.setCellpadding(2);
    T.setCellspacing(2);

    IWBundle core = iwc.getApplication().getBundle(IW_CORE_BUNDLE_IDENTIFIER);
    //if(iCategoryId > 0)
    {
      /*
      Link ne = new Link(core.getImage("/shared/create.gif","create"));
      ne.setWindowToOpen(FinanceEditorWindow.class);
      ne.addParameter(FinanceEditorWindow.prmCategory,iCategoryId);
      T.add(ne,1,1);
      T.add(T.getTransparentCell(iwc),1,1);
      */

      Link change = getCategoryLink();
      change.setImage(core.getImage("/shared/edit.gif","edit"));
      T.add(change,1,1);
      }
    T.setWidth("100%");
    return T;
  }


  public static Parameter getCategoryParameter(int iCategoryId){
    return new Parameter(prmCategoryId,String.valueOf(iCategoryId));
  }

  public static int parseCategoryId(IWContext iwc){

    if(iwc.isParameterSet(prmCategoryId))
      return Integer.parseInt(iwc.getParameter(prmCategoryId));
    else if(iwc.getApplication().getBundle(IW_BUNDLE_IDENTIFIER).getProperty(CATEGORY_PROPERTY)!=null)
      return Integer.parseInt(iwc.getApplication().getBundle(IW_BUNDLE_IDENTIFIER).getProperty(CATEGORY_PROPERTY));
    else
      return -1;
  }

  private void initCategoryId(IWContext iwc){
    iCategoryId = getCategoryId();
    if(iCategoryId <= 0){
      if(iwc.getApplication().getBundle(IW_BUNDLE_IDENTIFIER).getProperty(CATEGORY_PROPERTY)!=null)
        iCategoryId =  Integer.parseInt(iwc.getApplication().getBundle(IW_BUNDLE_IDENTIFIER).getProperty(CATEGORY_PROPERTY));
    }
  }

  public Link getLink(Class cl,String name){
    Link L = new Link(name);
    L.addParameter(Finance.getCategoryParameter(getCategoryId()));
    L.addParameter(getFinanceObjectParameter(cl));
    L.setFontSize(1);
    L.setFontColor(Edit.colorDark);
    return L;
  }

  public Parameter getFinanceObjectParameter(Class financeClass){
    return new Parameter(prmFinanceClass,financeClass.getName());
  }

  public void addFinanceObject(Block obj){
    if(FinanceObjects == null)
      FinanceObjects = new java.util.Vector();
    FinanceObjects.add(obj);
  }
/*
  public void main(IWContext iwc){
    isAdmin = iwc.hasEditPermission(this);
    core = iwc.getApplication().getBundle(IW_CORE_BUNDLE_IDENTIFIER);
    control(iwc);
  }
*/
  public void setAdministrative(boolean administrative){
    this.administrative = administrative;
  }

   public synchronized Object clone() {
    Finance obj = null;
    try {
      obj = (Finance)super.clone();
      obj.FinanceObjects  = FinanceObjects;

    }
    catch(Exception ex) {
      ex.printStackTrace(System.err);
    }
    return obj;
  }
}
