package is.idega.idegaweb.campus.block.allocation.presentation;


import is.idega.idegaweb.campus.block.allocation.business.ContractBusiness;
import is.idega.idegaweb.campus.presentation.Edit;
import is.idega.idegaweb.campus.block.allocation.data.Contract;
import is.idega.idegaweb.campus.data.SystemProperties;
import com.idega.presentation.IWContext;
import com.idega.presentation.PresentationObject;
import com.idega.presentation.Table;
import com.idega.presentation.PresentationObjectContainer;
import com.idega.presentation.ui.*;
import com.idega.presentation.text.*;
import com.idega.idegaweb.IWBundle;
import com.idega.idegaweb.IWResourceBundle;
import com.idega.block.building.business.BuildingCacher;
import com.idega.block.building.data.*;
import com.idega.block.application.data.Applicant;

import com.idega.util.IWTimestamp;
import java.sql.SQLException;

import com.idega.core.contact.data.Email;
import com.idega.core.user.business.UserBusiness;
import com.idega.block.finance.business.AccountManager;
import com.idega.block.finance.data.Account;
import com.idega.core.accesscontrol.business.LoginCreator;
import com.idega.core.accesscontrol.business.LoginDBHandler;
import com.idega.core.user.data.User;
import com.idega.core.data.GenericGroup;
import com.idega.core.accesscontrol.data.LoginTable;
import java.util.List;
import com.idega.util.SendMail;

/**
 * Title:   idegaclasses
 * Description:
 * Copyright:    Copyright (c) 2001
 * Company:
 * @author  <a href="mailto:aron@idega.is">aron@idega.is
 * @version 1.0
 */

public class ContractEditWindow extends Window{

  protected final int ACT1 = 1,ACT2 = 2, ACT3 = 3,ACT4  = 4,ACT5 = 5;
  private final static String IW_BUNDLE_IDENTIFIER="is.idega.idegaweb.campus";
  protected IWResourceBundle iwrb;
  protected IWBundle iwb;
  private boolean isAdmin;
  private boolean isLoggedOn;
  private String login = null;
  private String passwd = null;
  private boolean print = false;
  private SystemProperties SysProps = null;
  private GenericGroup eGroup = null;
  private User eUser = null;

  /*
    Bl�r litur � topp # 27324B
    Hv�tur litur fyrir ne�an �a� # FFFFFF
    Lj�sbl�r litur � t�flu # ECEEF0
    Auka litur �rl�ti� dekkri (� lagi a� nota l�ka) # CBCFD3
  */

  public ContractEditWindow() {
    setWidth(530);
    setHeight(370);
    setResizable(true);
  }

  protected void control(IWContext iwc){
//    debugParameters(iwc);
    iwrb = getResourceBundle(iwc);
    iwb = getBundle(iwc);


      if(iwc.isParameterSet("save") || iwc.isParameterSet("save.x")){
        doSaveContract(iwc);
      }
      add(getEditTable(iwc));

    //  add(Edit.formatText(iwrb.getLocalizedString("access_denied","Access denied")));

    //add(String.valueOf(iSubjectId));
  }

  public String getBundleIdentifier(){
    return IW_BUNDLE_IDENTIFIER;
  }

  public PresentationObject makeLinkTable(int menuNr){
    Table LinkTable = new Table(6,1);

    return LinkTable;
  }

  private PresentationObject getEditTable(IWContext iwc){
    int iContractId = Integer.parseInt( iwc.getParameter("contract_id"));
    //Table T = new Table(2,8);
    DataTable T = new DataTable();
    T.setWidth("100%");
    T.addTitle(iwrb.getLocalizedString("contract_info","Contract info"));
    T.addButton(new CloseButton(iwrb.getImage("close.gif")));
    T.addButton(new SubmitButton(iwrb.getImage("save.gif"),"save"));

    int row = 1;
    int col = 1;

    try{
      if(iContractId > 0){
        Contract eContract = ((is.idega.idegaweb.campus.block.allocation.data.ContractHome)com.idega.data.IDOLookup.getHomeLegacy(Contract.class)).findByPrimaryKeyLegacy(iContractId);
        Applicant eApplicant = ((com.idega.block.application.data.ApplicantHome)com.idega.data.IDOLookup.getHomeLegacy(Applicant.class)).findByPrimaryKeyLegacy(eContract.getApplicantId().intValue());
        User user = ((com.idega.core.user.data.UserHome)com.idega.data.IDOLookup.getHomeLegacy(User.class)).findByPrimaryKeyLegacy(eContract.getUserId().intValue());
        boolean isContractUser = user.getID() == eUser.getID();
        if(user !=null){
          T.add(new HiddenInput("contract_id",String.valueOf(eContract.getID())),1,row);
          T.add(Edit.formatText(iwrb.getLocalizedString("name","Name")),1,row);
          T.add(Edit.formatText(user.getName()),2,row);
          row++;
          T.add(Edit.formatText(iwrb.getLocalizedString("ssn","SocialNumber")),1,row);
          T.add(Edit.formatText(eApplicant.getSSN()),2,row);
          row++;
          T.add(Edit.formatText(iwrb.getLocalizedString("apartment","Apartment")),1,row);
          T.add(Edit.formatText(getApartmentString(BuildingCacher.getApartment(eContract.getApartmentId().intValue()))),2,row);
          row++;

          IWTimestamp today = IWTimestamp.RightNow();

          DateInput from = new DateInput("from_date",true);
          from.setYearRange(today.getYear()-3,today.getYear()+7);
          if(eContract.getValidFrom()!=null)
            from.setDate(eContract.getValidFrom());
          Edit.setStyle(from);
          T.add(Edit.formatText(iwrb.getLocalizedString("valid_from","Valid from")),1,row);
          T.add(from,2,row);
          row++;
          DateInput to = new DateInput("to_date",true);
          to.setYearRange(today.getYear()-3,today.getYear()+7);
          if(eContract.getValidTo()!=null)
            to.setDate(eContract.getValidTo());
          Edit.setStyle(to);
          T.add(Edit.formatText(iwrb.getLocalizedString("valid_to","Valid to")),1,row);
          T.add(to,2,row);
          row++;
          DateInput moving = new DateInput("moving_date",true);
          moving.setYearRange(today.getYear()-3,today.getYear()+7);
          if(eContract.getMovingDate()!=null)
            moving.setDate(eContract.getMovingDate());
          Edit.setStyle(moving);
          T.add(Edit.formatText(iwrb.getLocalizedString("moving_to","Moving date")),1,row);
          T.add(moving,2,row);
          row++;
          TimestampInput deliver = new TimestampInput("deliver_date",true);
          deliver.setYearRange(today.getYear()-3,today.getYear()+7);
          if(eContract.getDeliverTime()!=null)
            deliver.setTimestamp(eContract.getDeliverTime());
          Edit.setStyle(deliver);
          T.add(Edit.formatText(iwrb.getLocalizedString("deliver_date","Deliver date")),1,row);
          T.add(deliver,2,row);
          row++;
          TimestampInput returnd = new TimestampInput("return_date",true);
          returnd.setYearRange(today.getYear()-3,today.getYear()+7);
          if(eContract.getReturnTime()!=null)
            returnd.setTimestamp(eContract.getReturnTime());
          Edit.setStyle(returnd);
          T.add(Edit.formatText(iwrb.getLocalizedString("return_date","Return date")),1,row);
          T.add(returnd,2,row);
          row++;
          T.add(Edit.formatText(iwrb.getLocalizedString("has_key","Has key")),1,row);
          //T.add(status,2,row);
          T.add(Edit.formatText(getStatus(eContract.getIsRented()?"yes":"no")),2,row);
          row++;
          //DropdownMenu status = getStatusDrop("status",eContract.getStatus());
          //Edit.setStyle(status);
          T.add(Edit.formatText(iwrb.getLocalizedString("status","Status")),1,row);
          //T.add(status,2,row);
          T.add(Edit.formatText(getStatus(eContract.getStatus())),2,row);
          row++;
          T.add(Edit.formatText(iwrb.getLocalizedString("status_date","Status date")),1,row);
          if(eContract.getStatusDate()!=null){
            String sdate = eContract.getStatusDate().toString();
            T.add(Edit.formatText(sdate),2,row);
          }
          row++;
          TextArea info = new TextArea("info");
          if(eContract.getResignInfo()!=null)
            info.setContent(eContract.getResignInfo());
          T.add(Edit.formatText(iwrb.getLocalizedString("resign_info","Resign info")),1,row);
          Edit.setStyle(info);
          T.add(info,2,row);
          row++;

        }
      }
    }
    catch(SQLException ex){}

    Form F = new Form();
    F.add(T);
    return F;
  }

  private void doSaveContract(IWContext iwc){
    try{

      int id = Integer.parseInt(iwc.getParameter("contract_id"));
      Contract eContract = ((is.idega.idegaweb.campus.block.allocation.data.ContractHome)com.idega.data.IDOLookup.getHomeLegacy(Contract.class)).findByPrimaryKeyLegacy(id);

      IWTimestamp from = null,to = null,moving = null,deliver = null, retur = null;
      String sfrom = iwc.getParameter("from_date");
      if(sfrom!=null && sfrom.length() == 10)
        eContract.setValidFrom(new IWTimestamp(sfrom).getSQLDate());
      String to_date = iwc.getParameter("to_date");
      if(to_date!=null && to_date.length() == 10)
        eContract.setValidTo(new IWTimestamp(to_date).getSQLDate());
      String moving_date = iwc.getParameter("moving_date");
      if(moving_date!=null && moving_date.length() == 10)
        eContract.setMovingDate(new IWTimestamp(moving_date).getSQLDate());
      String deliver_date = iwc.getParameter("deliver_date");
      if(deliver_date!=null && deliver_date.length() > 0)
        eContract.setDeliverTime(new IWTimestamp(deliver_date).getTimestamp());
      String return_date = iwc.getParameter("return_date");
      if(return_date!=null && return_date.length() > 0)
        eContract.setReturnTime(new IWTimestamp(return_date).getTimestamp());
      if(iwc.isParameterSet("status")){
        eContract.setStatus((iwc.getParameter("status")));
        eContract.setStatusDate(IWTimestamp.RightNow().getSQLDate());
      }
      if(iwc.isParameterSet("info")){
        eContract.setResignInfo((iwc.getParameter("info")));
      }

      eContract.update();
    }
    catch(Exception ex){
      ex.printStackTrace();
    }

  }

  private void doAddEmail( int iUserId ,IWContext iwc){
    String sEmail = iwc.getParameter("new_email");
    UserBusiness.addNewUserEmail(iUserId,sEmail);
  }

  private PresentationObject getApartmentTable(Apartment A){
    Table T = new Table();
    Floor F = BuildingCacher.getFloor(A.getFloorId());
    Building B = BuildingCacher.getBuilding(F.getBuildingId());
    Complex C = BuildingCacher.getComplex(B.getComplexId());
    T.add(Edit.formatText(A.getName()),1,1);
    T.add(Edit.formatText(F.getName()),2,1);
    T.add(Edit.formatText(B.getName()),3,1);
    T.add(Edit.formatText(C.getName()),4,1);
    return T;
  }

  private String getApartmentString(Apartment A){
    StringBuffer S = new StringBuffer();
    Floor F = BuildingCacher.getFloor(A.getFloorId());
    Building B = BuildingCacher.getBuilding(F.getBuildingId());
    Complex C = BuildingCacher.getComplex(B.getComplexId());
    S.append(A.getName());S.append(" ");
    S.append(F.getName());S.append(" ");
    S.append(B.getName());S.append(" ");
    S.append(C.getName());
    return S.toString();
  }

  public void main(IWContext iwc) throws Exception {
    eUser = iwc.getUser();
    //isStaff = com.idega.core.accesscontrol.business.AccessControl
    isAdmin = iwc.hasEditPermission(this);
    isLoggedOn = com.idega.core.accesscontrol.business.LoginBusinessBean.isLoggedOn(iwc);
    control(iwc);
  }

  private String getStatus(String status){
    String r = "";
    char c = status.charAt(0);
    switch (c) {
      case 'C': r = iwrb.getLocalizedString("created","Created"); break;
      case 'P': r = iwrb.getLocalizedString("printed","Printed"); break;
      case 'S': r = iwrb.getLocalizedString("signed","Signed");   break;
      case 'R': r = iwrb.getLocalizedString("rejected","Rejected");  break;
      case 'T': r = iwrb.getLocalizedString("terminated","Terminated");   break;
      case 'E': r = iwrb.getLocalizedString("ended","Ended");  break;
    }
    return r;
  }

  private DropdownMenu getStatusDrop(String name,String selected){
    DropdownMenu drp = new DropdownMenu(name);
    drp.addMenuElement("C",getStatus("C"));
    drp.addMenuElement("P",getStatus("P"));
    drp.addMenuElement("S",getStatus("S"));
    drp.addMenuElement("R",getStatus("R"));
    drp.addMenuElement("T",getStatus("T"));
    drp.addMenuElement("E",getStatus("E"));
    drp.setSelectedElement(selected);
    return drp;
  }
}
