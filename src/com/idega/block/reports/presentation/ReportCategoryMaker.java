package com.idega.block.reports.presentation;

import com.idega.block.reports.data.*;
import com.idega.presentation.Block;
import com.idega.presentation.IWContext;
import java.sql.SQLException;
import com.idega.presentation.Table;
import com.idega.presentation.ui.*;
import com.idega.presentation.Script;

public class ReportCategoryMaker extends Block{

  private boolean isAdmin;
  private final int ACT0 = 0,ACT1=1,ACT2=2,ACT3=3,ACT4=4;
  private String sAction = "rcm_action";
  private String sActPrm = "0";
  private int iAction = 0;
  private String sName,sInfo;
  private String sIndex;

  public ReportCategoryMaker(){
    sIndex = "0";
    sName = "";
    sInfo = "";
  }

  private void control(IWContext iwc){

    try{
        doSome(iwc);

        if(iwc.getParameter(sAction) != null){
          sActPrm = iwc.getParameter(sAction);
          try{
            iAction = Integer.parseInt(sActPrm);
            switch(iAction){
              case ACT0: doSome(iwc); break;
              case ACT1: doAct1(iwc); break;
              case ACT2: doAct2(iwc); break;
              case ACT3: break;
              case ACT4: break;
            }
          }
          catch(Exception e){

          }
        }
    }
    catch(Exception S){
      S.printStackTrace();
    }
    doMain(iwc);
  }

  private void doSome(IWContext iwc){
    int id = 0;
    String sIndex = iwc.getParameter("rep_cat_drp");
    if(sIndex != null){
      id = Integer.parseInt(sIndex);
      if(id != 0){
        try {
          ReportCategory RC = new ReportCategory(id);
          sName = RC.getName();
          sInfo = RC.getDescription();
        }
        catch (Exception ex) {
        }
      }
    }
  }

  private void doMain(IWContext iwc){
    String sIndex = iwc.getParameter("rep_cat_drp");
    Table T = new Table();
    Form myForm = new Form();
    if(sIndex == null)
      sIndex = "0";
    DropdownMenu drp = this.drpCategories("rep_cat_drp",sIndex);
    drp.setToSubmit();
    TextInput tiName = new TextInput("rep_cat_name",sName);
    TextInput tiInfo = new TextInput("rep_cat_info",sInfo);
    SubmitButton submit= new SubmitButton("Save",this.sAction,String.valueOf(this.ACT1));
    SubmitButton delete= new SubmitButton("Del",this.sAction,String.valueOf(this.ACT2));
    Table T2 = new Table();

    T2.add("Flokkur",1,1);
    T2.add(drp,1,2);
    T2.add("Name:",2,1);
    T2.add(tiName,2,2);
    T2.add("Info:",3,1);
    T2.add(tiInfo,3,2);
    T2.add(submit,4,2);
    T2.add(delete,5,2);
    myForm.add(T2);
    T.add(myForm);
    add(T);
  }

  private void doAct1(IWContext iwc){
    int id = -1;
    String sIndex = iwc.getParameter("rep_cat_drp");
    if(sIndex != null)
       id = Integer.parseInt(sIndex);
    sName = iwc.getParameter("rep_cat_name");
    sInfo = iwc.getParameter("rep_cat_info");
    if(id == 0)
      this.saveCategory(sName,sInfo);
    else
      this.updateCategory(id,sName,sInfo);
  }

  private void doAct2(IWContext iwc){
    int id = 0;
    String sIndex = iwc.getParameter("rep_cat_drp");
    if(sIndex != null)
       id = Integer.parseInt(sIndex);
    if(id != 0)
      this.deleteCategory(id);
    sName = "";
    sInfo = "";
  }

  private boolean saveCategory(String name,String info){
    try {
      ReportCategory rc = new ReportCategory();
      rc.setName(name);
      rc.setDescription(info);
      rc.insert();
      return true;
    }
    catch (Exception ex) {
      return false;
    }
  }
  private boolean updateCategory(int id,String name,String info){
     try {
      if(id != -1){
      ReportCategory rc = new ReportCategory(id);
      rc.setName(name);
      rc.setDescription(info);
      rc.update();
      return true;
      }
      else
        return false;
    }
    catch (Exception ex) {
      return false;
    }
  }
  private boolean deleteCategory(int id){
    try {
      ReportCategory rc = new ReportCategory(id);
      rc.delete();
      return true;
    }
    catch (Exception ex) {
      return false;
    }
  }

  private DropdownMenu drpCategories(String sPrm,String selected) {
    ReportCategory[] cat = new ReportCategory[0];
    try{
      cat = (ReportCategory[]) (new ReportCategory()).findAll();
    }
    catch(SQLException sql){}
    DropdownMenu drp = new DropdownMenu(sPrm);
    drp.addMenuElement("0","Category");
    for (int i = 0; i < cat.length; i++) {
      drp.addMenuElement(cat[i].getID(),cat[i].getName());
    }
    if(!selected.equalsIgnoreCase(""))
      drp.setSelectedElement(selected);
    return drp;
  }

  public void main(IWContext iwc) {
    /* try{
      isAdmin = com.idega.core.accesscontrol.business.AccessControl.isAdmin(iwc);
    }
    catch(SQLException e){
      isAdmin = false;
    }
    */
    isAdmin = true;
    control(iwc);
  }

}