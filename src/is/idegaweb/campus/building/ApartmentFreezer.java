package is.idegaweb.campus.building;

import com.idega.jmodule.object.ModuleObjectContainer;

import com.idega.idegaweb.IWBundle;
import com.idega.idegaweb.IWResourceBundle;
import com.idega.jmodule.object.ModuleInfo;
import com.idega.jmodule.object.ModuleObject;
import com.idega.jmodule.object.ModuleObjectContainer;
import com.idega.jmodule.object.Table;
import com.idega.jmodule.object.textObject.*;
import com.idega.jmodule.object.interfaceobject.*;
import is.idegaweb.campus.presentation.Edit;
import com.idega.block.building.data.*;
import com.idega.block.building.business.*;
import java.util.List;
import com.idega.util.idegaTimestamp;
import java.sql.SQLException;
/**
 * Title:
 * Description:
 * Copyright:    Copyright (c) 2001
 * Company:
 * @author
 * @version 1.0
 */

public class ApartmentFreezer extends ModuleObjectContainer {



  protected final int ACT1 = 1,ACT2 = 2, ACT3 = 3,ACT4  = 4,ACT5 = 5;
  private final String strAction = "fin_action";
  protected boolean isAdmin = false;
  private final static String IW_BUNDLE_IDENTIFIER="is.idegaweb.campus.building";
  protected IWResourceBundle iwrb;
  protected IWBundle iwb;

  public ApartmentFreezer() {

  }

  protected void control(ModuleInfo modinfo){


      if(isAdmin){
        Table T = new Table();
        T.add((makeSearchTable()),1,1);
        if(modinfo.getParameter("search")!= null){
          String searchId = modinfo.getParameter("ap_search").trim();
          T.add(makeResultTable(searchId,modinfo),1,2);
        }
        else if( modinfo.getParameter("apartment_id")!= null){
          T.add(makeEditTable(Integer.parseInt(modinfo.getParameter("apartment_id")),modinfo),1,3);
        }
        else if(modinfo.getParameter("freeze")!=null){
          T.add(this.freezeApartment(modinfo),1,3);
          add("freeze");
        }
        add(T);
      }
      else
        add(new Text("Ekki R�ttindi"));

  }

  public ModuleObject makeLinkTable(int menuNr){
    Table LinkTable = new Table(6,1);

    return LinkTable;
  }

  public ModuleObject makeSearchTable(){

    Table Frame = new Table(3,2);
      Frame.setCellpadding(0);
      Frame.setCellspacing(0);
    Table Left = new Table();
      Left.setCellpadding(0);
      Left.setCellspacing(0);
    Table Right = new Table();
      Right.setCellpadding(0);
      Right.setCellspacing(0);
    Frame.add(Left,1,1);
    Frame.add(Right,3,1);

    Table T = new Table(2,1);
    TextInput SearchInput = new TextInput("ap_search");
    Edit.setStyle(SearchInput);
    SubmitButton SearchButton = new SubmitButton("search","Search");
    Edit.setStyle(SearchButton);
    T.add(SearchInput,1,1);
    T.add(SearchButton,2,1);
    Form F = new Form();
    F.add(T);
    Right.add(F);

    return Frame;

  }
  public ModuleObject makeResultTable(String searchName,ModuleInfo modinfo){
     Table Frame = new Table(3,2);
      Frame.setCellpadding(0);
      Frame.setCellspacing(0);
    Table Left = new Table();
      Left.setCellpadding(0);
      Left.setCellspacing(0);
    Table Right = new Table();
      Right.setCellpadding(0);
      Right.setCellspacing(0);
    Frame.add(Left,1,1);
    Frame.add(Right,3,1);
    List L = BuildingFinder.searchApartment(searchName);
    if(L != null){

      int len = L.size();

      Table T = new Table();
      for (int i = 0; i < len; i++) {
        Apartment A = (Apartment) L.get(i);
        Floor F = BuildingCacher.getFloor( A.getFloorId());
        Building B = BuildingCacher.getBuilding( F.getBuildingId());
        Link l = new Link(A.getName());
        l.addParameter("apartment_id",A.getID());
        T.add(l,1,i+1);
        T.add(Edit.formatText(F.getName()),2,i+1);
        T.add(Edit.formatText(B.getName()),3,i+1);
        if(A.getUnavailableUntil()!=null)
          T.add(Edit.formatText((new idegaTimestamp(A.getUnavailableUntil())).getLocaleDate(modinfo)),4,i+1);
        else
          T.add(Edit.formatText("Unfrozen"),4,i+1);
      }
      Right.add(T);
    }
    return Frame;
  }

  private ModuleObject makeEditTable(int id,ModuleInfo modinfo){
    Table Frame = new Table(3,2);
      Frame.setCellpadding(0);
      Frame.setCellspacing(0);
    Apartment A = BuildingCacher.getApartment(id);
    Floor F = BuildingCacher.getFloor( A.getFloorId());
    Building B = BuildingCacher.getBuilding( F.getBuildingId());

    DateInput DI = new DateInput("frozen_date",true);
    DI.setModuleInfo(modinfo);
    if(A.getUnavailableUntil()!=null)
      DI.setDate(A.getUnavailableUntil());
    //else
    //  DI.setToCurrentDate();
    DI.setStyle(Edit.styleAttribute);
    HiddenInput hid = new HiddenInput("app_id",String.valueOf(id));
    SubmitButton sb = new SubmitButton("freeze","Freeze");
      Edit.setStyle(sb);
    Form myForm = new Form();
    Table T = new Table();
    T.add(Edit.formatText(A.getName()),1,1);
    T.add(Edit.formatText(F.getName()),2,1);
    T.add(Edit.formatText(B.getName()),3,1);
    T.add(DI,4,1);
    T.add(sb,5,1);
    T.add(hid,5,1);
    myForm.add(T);
    Frame.add(myForm);
    return Frame;
  }

  private ModuleObject freezeApartment(ModuleInfo modinfo){
    Table T = new Table();
    String appId = modinfo.getParameter("app_id");
    String frozenDate = modinfo.getParameter("frozen_date");

    try{
      if(frozenDate != null && frozenDate.length()==10){
      int id = Integer.parseInt(appId);
      Apartment A = BuildingCacher.getApartment(id);
      idegaTimestamp iT = new idegaTimestamp(frozenDate);
      A.setUnavailableUntil(iT.getSQLDate());
      A.update();
      }
    }
    catch(Exception e){}

    return T;
  }

  public String getBundleIdentifier(){
    return IW_BUNDLE_IDENTIFIER;
  }

  public void main(ModuleInfo modinfo){
    iwrb = getResourceBundle(modinfo);
    iwb = getBundle(modinfo);
    try{
    //isStaff = com.idega.core.accesscontrol.business.AccessControl
    isAdmin = com.idega.core.accesscontrol.business.AccessControl.isAdmin(modinfo);
    }
    catch(SQLException sql){ isAdmin = false;}
    control(modinfo);
  }
}