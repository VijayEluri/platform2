package com.idega.block.finance.presentation;


import com.idega.util.text.Edit;
import com.idega.block.finance.data.*;
import com.idega.block.finance.business.*;
import com.idega.presentation.Block;
import com.idega.presentation.PresentationObject;
import com.idega.presentation.IWContext;
import com.idega.presentation.Table;
import com.idega.presentation.ui.*;
import com.idega.presentation.text.*;
import com.idega.util.idegaTimestamp;
import com.idega.util.text.TextSoap;
import com.idega.idegaweb.IWBundle;
import com.idega.idegaweb.IWResourceBundle;
import java.util.Hashtable;
import java.util.List;
import java.util.Map;

/**
 * Title:   idegaclasses
 * Description:
 * Copyright:    Copyright (c) 2001
 * Company:
 * @author  <a href="mailto:aron@idega.is">aron@idega.is
 * @version 1.0
 */

public class TariffEditor extends Block{


  protected final int ACT1 = 1,ACT2 = 2, ACT3 = 3,ACT4  = 4;
  protected final int ACT5 = 5,ACT6 = 6, ACT7 = 7,ACT8  = 8;
  public  String strAction = "te_action";
  public final   int YEAR=1,MONTH=2,WEEK=3,DAY=4;
  private idegaTimestamp workingPeriod;
  private int period = MONTH;
  private int iPeriod;
  private int iNumberOfDecimals = 0;
  private boolean bRoundAmounts = true;;

  private static String prmGroup = "taed_grp";

  protected boolean isAdmin = false;
  private final static String IW_BUNDLE_IDENTIFIER="com.idega.block.finance";
  protected IWResourceBundle iwrb;
  protected IWBundle iwb,core;

  public TariffEditor() {

  }
  public void setRoundAmounts(boolean round){
    bRoundAmounts = round;
  }

  public void setNumberOfDecimals(int decimals){
    iNumberOfDecimals = decimals;
  }

  protected void control(IWContext iwc){
  /*
    java.util.Enumeration E = iwc.getParameterNames();
    while(E.hasMoreElements()){
      String prm = (String) E.nextElement();
      System.err.println("prm "+prm+" : "+iwc.getParameter(prm));
    }
*/
    int iCategoryId = Finance.parseCategoryId(iwc);
    int iGroupId = -1;
    List groups = FinanceFinder.listOfTariffGroups(iCategoryId);
    TariffGroup group = null;
    if(iwc.isParameterSet(prmGroup))
      iGroupId = Integer.parseInt(iwc.getParameter(prmGroup));
    if(iGroupId > 0 ){
      group = FinanceFinder.getTariffGroup(iGroupId);
    }
    else if(groups !=null){
      group = (TariffGroup) groups.get(0);
      iGroupId = group.getID();
    }

    if(isAdmin){
    try{
      PresentationObject MO = new Text();

      if(iwc.getParameter("updateindex")!=null){
        MO =doUpdateIndex(iwc,iCategoryId,iGroupId);
      }
      else if(iwc.getParameter("savetariffs")!=null){
        MO =doUpdate(iwc,iCategoryId,iGroupId);
      }
      else if(iwc.getParameter(strAction) != null){
        String sAct = iwc.getParameter(strAction);
        int iAct = Integer.parseInt(sAct);
        switch (iAct) {
          case ACT1 : MO =getMain(iwc,iCategoryId,group);        break;
          case ACT2 : MO =getChange(iwc,false,false,iCategoryId,iGroupId);break;
          case ACT3 : MO =doUpdate(iwc,iCategoryId,iGroupId);      break;
          case ACT4 : MO =getChange(iwc,true,false,iCategoryId,iGroupId); break;
          default: MO =getMain(iwc,iCategoryId,group);           break;
        }
      }
      else{
        MO = getMain(iwc,iCategoryId,group);
      }
        Table T = new Table(1,4);
        T.setCellpadding(0);
        T.setCellspacing(0);
        T.add(Edit.headerText(iwrb.getLocalizedString("tariff_editor","Tariff editor"),3),1,1);
        T.add(getGroupLinks(iCategoryId,iGroupId,groups),1,2);
        T.add(makeLinkTable(1,iCategoryId,iGroupId),1,3);
        T.add(MO,1,4);
        T.setWidth("100%");
        add(T);
      }
      catch(Exception S){
        S.printStackTrace();
      }
    }
    else
      add(iwrb.getLocalizedString("access_denied","Access denies"));
  }
  protected PresentationObject makeLinkTable(int menuNr,int iCategoryId,int iGroupId){
    Table LinkTable = new Table(4,1);
    int last = 4;
    LinkTable.setWidth("100%");
    LinkTable.setCellpadding(2);
    LinkTable.setCellspacing(1);
    LinkTable.setColor(Edit.colorDark);
    LinkTable.setWidth(last,"100%");
    Link Link1 = new Link(iwrb.getLocalizedString("view","View"));
    Link1.setFontColor(Edit.colorLight);
    Link1.addParameter(this.strAction,String.valueOf(this.ACT1));
    Link1.addParameter(Finance.getCategoryParameter(iCategoryId));
    Link1.addParameter(prmGroup,iGroupId);
    Link Link2 = new Link(iwrb.getLocalizedString("change","Change"));
    Link2.setFontColor(Edit.colorLight);
    Link2.addParameter(this.strAction,String.valueOf(this.ACT2));
    Link2.addParameter(Finance.getCategoryParameter(iCategoryId));
    Link2.addParameter(prmGroup,iGroupId);
    /*
    Link Link3 = new Link(iwrb.getLocalizedString("new","New"));
    Link3.setFontColor(Edit.colorLight);
    Link3.addParameter(this.strAction,String.valueOf(this.ACT4));
    */
    if(isAdmin){
      LinkTable.add(Link1,1,1);
      LinkTable.add(Link2,2,1);
      //LinkTable.add(Link3,3,1);
    }
    return LinkTable;
  }
  protected void setPeriod(int period){
    this.period = period;
  }

  private PresentationObject getGroupLinks(int iCategoryId , int iGroupId,List groups){
    Table T = new Table();
    T.setCellpadding(0);
    T.setCellspacing(0);
    int col = 1;
    if(groups!=null){
      java.util.Iterator I = groups.iterator();
      TariffGroup group;
      Link tab;
      while (I.hasNext()) {
        group = (TariffGroup) I.next();
        tab = new Link(iwb.getImageTab(group.getName(),true));
        tab.addParameter(Finance.getCategoryParameter(iCategoryId));
        tab.addParameter(prmGroup,group.getID());
        T.add(tab,col++,1);
      }
    }
    Link edit = new Link(iwrb.getLocalizedImageTab("edit","Edit",true));
    edit.setWindowToOpen(TariffGroupWindow.class);
    edit.addParameter(Finance.getCategoryParameter(iCategoryId));
    T.add(edit,col,1);
    return T;
  }

  protected PresentationObject getMain(IWContext iwc,int iCategoryId,TariffGroup group){
    idegaTimestamp today = new idegaTimestamp();
    int iGroupId = -1;
    FinanceHandler handler = null;
    Map attributeMap = null;
    if(group !=null){
      iGroupId = group.getID();
      if(group.getHandlerId() > 0){
        handler = FinanceFinder.getFinanceHandler(group.getHandlerId());
        attributeMap = handler.getAttributeMap();
      }
    }
    List tariffs = FinanceFinder.listOfTariffs(iGroupId);
    List AK = FinanceFinder.listOfAccountKeys(iCategoryId);

    Hashtable hAK = getKeys(AK);

    int count = 0;
    if(tariffs !=null)
      count = tariffs.size();
    Table T2 = new Table(1,2);
    T2.setCellpadding(0);
    T2.setCellspacing(0);
    T2.setWidth("100%");
    Table T = new Table(6,count+1);
    T.setWidth("100%");
    T.setHorizontalZebraColored(Edit.colorLight,Edit.colorWhite);
    T.setRowColor(1,Edit.colorMiddle);
    T.setCellpadding(2);
    T.setCellspacing(1) ;
    int col = 1;
    T.add(Edit.formatText("Nr"),col++,1);
    boolean conn = false;
    if(attributeMap!=null && !attributeMap.isEmpty()){
      T.add(Edit.formatText(iwrb.getLocalizedString("connection","Connection")),col++,1);
    }
    T.add(Edit.formatText(iwrb.getLocalizedString("name","Name")),col++,1);
    T.add(Edit.formatText(iwrb.getLocalizedString("amount","Amount")),col++,1);
    //T.add(Edit.formatText(iwrb.getLocalizedString("info","Info")),5,1);
    T.add(Edit.formatText(iwrb.getLocalizedString("account_key","Account key")),col++,1);

    if(isAdmin){
      if(count != 0){
        Tariff tariff;
        for (int i = 0;i < count;i++){
          col = 1;
          tariff = (Tariff) tariffs.get(i);
          T.add(Edit.formatText( String.valueOf(i+1)),col++,i+2);
          if(attributeMap != null){
            String tatt = tariff.getTariffAttribute();
            String val = "";
            if(tatt !=null && attributeMap.containsKey(tatt))
              val = (String)attributeMap.get(tatt);
            T.add(Edit.formatText(val),col++,i+2);
          }

          //  T.add(Edit.formatText(((GenericEntity)hLodgings.get(tatt)).getName()),col++,i+2);
          T.add(Edit.formatText(tariff.getName()),col++,i+2);
          T.add(Edit.formatText(String.valueOf(tariff.getPrice())),col++,i+2);
          //T.add(Edit.formatText(tariffs[i].getInfo()),col++,i+2);
          Integer I = new Integer(tariff.getAccountKeyId());
          if(hAK.containsKey(I))
            T.add(Edit.formatText((String)hAK.get(I)),col++,i+2);
        }
      }
    }

    T2.add(T,1,1);
    return T2;

  }

  private PresentationObject doUpdateIndex(IWContext iwc,int iCategoryId,int iGroupId){
    /** @todo  *
     *
     */
    return getChange(iwc,false,true,iCategoryId,iGroupId);
  }

  protected PresentationObject getChange(IWContext iwc,boolean ifnew,boolean factor,int iCategoryId,int iGroupId){
    Form myForm = new Form();
    myForm.add(Finance.getCategoryParameter(iCategoryId));
    myForm.maintainAllParameters();
    boolean updateIndex = factor;
    idegaTimestamp today = new idegaTimestamp();
    List tariffs = FinanceFinder.listOfTariffs(iGroupId);
    List AK = FinanceFinder.listOfAccountKeys(iCategoryId);
    List indices = Finder.listOfTypeGroupedIndices();
    Map M = Finder.mapOfIndicesByTypes(indices);



    int count = 0;
    if(tariffs !=null)
      count = tariffs.size();
    int inputcount = count+5;
    Table BorderTable = new Table();
    BorderTable.setCellpadding(1);
    BorderTable.setCellspacing(0);
    BorderTable.setColor(Edit.colorDark);
    BorderTable.setWidth("100%");
    Table T2 = new Table(1,3);
    T2.setColor(Edit.colorWhite);
    T2.setCellpadding(0);
    T2.setCellpadding(0);
    T2.setWidth("100%");
    Table T =  new Table(8,inputcount+1);
    T.setWidth("100%");
    T.setCellpadding(2);
    T.setCellspacing(1);
    T.setColumnAlignment(1,"right");
    T.setHorizontalZebraColored(Edit.colorLight,Edit.colorWhite);
    T.setRowColor(1,Edit.colorMiddle);
    T.add(Edit.formatText("Nr"),1,1);
    T.add(Edit.formatText(iwrb.getLocalizedString("connection","Connection")),2,1);
    T.add(Edit.formatText(iwrb.getLocalizedString("name","Name")),3,1);
    T.add(Edit.formatText(iwrb.getLocalizedString("amount","Amount")),4,1);
    //T.add(Edit.formatText(iwrb.getLocalizedString("info","Info")),5,1);
    T.add(Edit.formatText(iwrb.getLocalizedString("account_key","Account key")),5,1);
    T.add(Edit.formatText(iwrb.getLocalizedString("index","Index")),6,1);
    T.add(Edit.formatText(iwrb.getLocalizedString("updated","Updated")),7,1);
    T.add(Edit.formatText(iwrb.getLocalizedString("delete","Delete")),8,1);

    Tariff tariff;
    for (int i = 1; i <= inputcount ;i++){
      String rownum = String.valueOf(i);
      String s = "";
      int hid = -1;
      TextInput nameInput,priceInput,infoInput;
      DropdownMenu drpAtt,drpAK,drpIx;
      HiddenInput idInput;
      CheckBox delCheck;

      drpAK = drpAccountKeys(AK,("te_akdrp"+i));
      drpIx = drpIndicesByType(indices,"te_ixdrp"+i);

      nameInput  = new TextInput("te_nameinput"+i);
      priceInput = new TextInput("te_priceinput"+i);
      infoInput = new TextInput("te_infoinput"+i);
      //drpAtt = this.drpLodgings("te_attdrp"+i,"",XL,BL,FL,CL,TL);
      //drpAK = this.drpAccountKeys(AK,"te_akdrp"+i,"");


      int pos;
      if(i <= count && !ifnew ){
        pos = i-1;
        tariff = (Tariff) tariffs.get(pos);
        float iPrice = tariff.getPrice();
        String ixType = tariff.getIndexType();
        String ixDate = iwc.getParameter("te_ixdate"+i);
        idegaTimestamp ixdate = null;

        if(ixDate != null){
          ixdate = new idegaTimestamp(ixDate);
        }
        else if(tariff.getIndexUpdated() != null){
          ixdate = new idegaTimestamp(tariff.getIndexUpdated());
          T.add(new HiddenInput("te_ixdate"+i,ixdate.toString()));
        }

        if(updateIndex && ixType != null && M != null && M.containsKey(ixType)){
          TariffIndex ti = (TariffIndex) M.get(ixType);
          java.sql.Timestamp stamp = ti.getDate();
          if(ixdate != null){
            if( !stamp.equals(ixdate.getTimestamp())){
              iPrice = iPrice * getAddFactor(ti.getNewValue(),ti.getOldValue());
            }
            //System.err.println(stamp.toString() +" "+ixdate.toString());
          }
          else
            iPrice = iPrice * getAddFactor(ti.getNewValue(),ti.getOldValue());
        }

        iPrice = new Float(TextSoap.decimalFormat((double)iPrice,iNumberOfDecimals)).floatValue();

        if(bRoundAmounts)
          iPrice = Math.round((double)iPrice);

        nameInput.setContent(tariff.getName());
        if(tariff.getInfo()!=null)
        infoInput.setContent(tariff.getInfo());

        priceInput.setContent(String.valueOf(iPrice));

        //drpAtt.setSelectedElement(tariff.getTariffAttribute());
        drpAK.setSelectedElement(String.valueOf(tariff.getAccountKeyId()));
        drpIx.setSelectedElement(ixType);

        delCheck = new CheckBox("te_delcheck"+i,"true");
        hid = tariff.getID();
        Edit.setStyle(delCheck);

        T.add(delCheck,8,i+1);
      }

      idInput = new HiddenInput("te_idinput"+i,String.valueOf(hid ));

      nameInput.setSize(20);
      priceInput.setSize(8);
      infoInput.setSize(30);

      Edit.setStyle(nameInput);
      Edit.setStyle(priceInput);
      Edit.setStyle(infoInput);
     // Edit.setStyle(drpAtt);
      Edit.setStyle(drpAK);
      Edit.setStyle(drpIx);

      T.add(Edit.formatText(rownum),1,i+1);
      //T.add(drpAtt,2,i+1);
      T.add(nameInput,3,i+1);
      T.add(priceInput,4,i+1);
      //T.add(infoInput,5,i+1);
      T.add(drpAK,5,i+1);
      T.add(drpIx,6,i+1);
      //T.add(indexCheck,6,i+1);
      T.add(idInput);
    }
    Table T3 = new Table(8,1);
    T3.setWidth("100%");
    T3.setWidth(5,1,"100%");
    T3.setColumnAlignment(6,"right");
    T3.setColumnAlignment(7,"right");

    SubmitButton update = new SubmitButton("updateindex",iwrb.getLocalizedString("update","Update"));
    Edit.setStyle(update);

    T3.add(update,8,1);
    SubmitButton save = new SubmitButton("savetariffs",iwrb.getLocalizedString("save","Save"));
    Edit.setStyle(save);
    Table T4 = new Table();
    T4.setAlignment("right");
    T4.add(save);
    T2.add(T3,1,1);
    T2.add(T,1,2);
    T2.add(T4,1,3);
    BorderTable.add(T2);
    myForm.add(new HiddenInput(prmGroup, String.valueOf(iGroupId)));
    myForm.add(new HiddenInput("te_count", String.valueOf(inputcount)));
    //myForm.add(new HiddenInput(this.strAction,String.valueOf(this.ACT3 )));
    myForm.add(BorderTable);

    return (myForm);
  }

  protected PresentationObject doUpdate(IWContext iwc,int iCategoryId,int iGroupId) {
    Map M = Finder.mapOfIndicesByTypes(Finder.listOfTypeGroupedIndices());
    int count = Integer.parseInt(iwc.getParameter("te_count"));
    String sName,sInfo,sDel,sPrice,sAtt,sAK,sTK,sID,sDateFrom,sDateTo,sIndex,sIndexStamp;
    int ID,Attid,AKid,TKid;
    float Price;
    boolean bIndex;
    int TGid = Integer.parseInt(iwc.getParameter(prmGroup));
    Tariff tariff = null;

    for (int i = 1; i < count+1 ;i++){
      sName = iwc.getParameter("te_nameinput"+i);
      sPrice = (iwc.getParameter("te_priceinput"+i));
      sInfo = iwc.getParameter("te_infoinput"+i);
      sAtt = iwc.getParameter("te_attdrp"+i);
      sAK = (iwc.getParameter("te_akdrp"+i));
      sIndex = (iwc.getParameter("te_ixdrp"+i));
      sDel = iwc.getParameter("te_delcheck"+i);
      sID = iwc.getParameter("te_idinput"+i);
      sIndexStamp = iwc.getParameter("te_ixdate"+i);
      idegaTimestamp stamp = sIndexStamp!= null ?new idegaTimestamp(sIndexStamp):null;

      if(stamp == null && sIndex !=null && M!=null && M.containsKey(sIndex)){
        stamp = new idegaTimestamp(((TariffIndex)M.get(sIndex)).getDate());
      }

      if(sIndex != null && !sIndex.equals("-1")){
        bIndex = true;
      }
      else{
        bIndex = false;
        sIndex = "";
      }

      ID = Integer.parseInt(iwc.getParameter("te_idinput"+i));
      if(sDel != null && sDel.equalsIgnoreCase("true")){
          FinanceBusiness.deleteTariff(ID);
      }
      else if(!"".equals(sName)){
        java.sql.Timestamp indexStamp = stamp!=null?stamp.getTimestamp():null;
        FinanceBusiness.saveTariff(ID,sName,sInfo,sAtt,sIndex,bIndex,
                  indexStamp,Float.parseFloat(sPrice),Integer.parseInt(sAK),TGid);
      }
    }// for loop

   return getChange(iwc,false,false,iCategoryId,iGroupId);
  }

  private Hashtable getKeys(List AK){
    Hashtable h = new Hashtable();
    if(AK != null){
      int len = AK.size();
      for (int i = 0; i < len; i++) {
        AccountKey T = (AccountKey) AK.get(i);
        h.put(new Integer(T.getID()),T.getName());
      }
    }
    return h;

  }

  private DropdownMenu drpAccountKeys(List AK,String name){
    DropdownMenu drp = new DropdownMenu(name);
    drp.addMenuElement(0,"--");
    if(AK != null){
      drp.addMenuElements(AK);
    }
    return drp;
  }

  private DropdownMenu drpAttributes(Hashtable hash,String name,String selected){

    DropdownMenu drp = new DropdownMenu(name);
    return drp;
  }

  private DropdownMenu drpIndicesByType(List L,String name){
    DropdownMenu drp = new DropdownMenu(name);
    drp.addMenuElementFirst("-1","--");//iwrb.getLocalizedString("index","Index"));
    if(L!= null){
      int len = L.size();
      for (int i = 0; i < len; i++) {
        TariffIndex ti = (TariffIndex) L.get(i);
        drp.addMenuElement(ti.getType(),ti.getName());
      }
      drp.setSelectedElement("-1");
    }
    return drp;
  }

  private float getDifferenceFactor(float now, float then){
    float factor = (now - then)/then;
    return factor;
  }

  private float getAddFactor(float now, float then){
    return 1+getDifferenceFactor( now,then);
  }

  private float findIndexDifferenceFactor(TariffIndex[] ti){
    float now = 1;
    float then = 1;
    float diff = 1;
    try {

      if(ti.length > 0 ){
        now = ti[0].getIndex();
        if(ti.length > 0 ){
          then = ti[1].getIndex();
          diff = now - then;
        }
      }

    }
    catch (Exception ex) { }
    float factor = (now - then)/then;
    return factor;
  }

  private float findLastTariffIndex(TariffIndex[] ti){
    float f = 1;
    if(ti.length > 0)
      f = ti[0].getIndex();
    return f;
  }


  public String getBundleIdentifier(){
    return IW_BUNDLE_IDENTIFIER;
  }

  public void main(IWContext iwc){
    iwrb = getResourceBundle(iwc);
    iwb = getBundle(iwc);
    core = iwc.getApplication().getCoreBundle();
    //isStaff = com.idega.core.accesscontrol.business.AccessControl
    isAdmin = iwc.hasEditPermission(this);
    control(iwc);
  }

}
