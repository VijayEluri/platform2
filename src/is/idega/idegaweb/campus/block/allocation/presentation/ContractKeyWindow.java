package is.idega.idegaweb.campus.block.allocation.presentation;


import is.idega.idegaweb.campus.block.allocation.business.ContractBusiness;
import is.idega.idegaweb.campus.block.allocation.business.ContractFinder;
import is.idega.idegaweb.campus.block.allocation.data.Contract;
import is.idega.idegaweb.campus.data.SystemProperties;
import is.idega.idegaweb.campus.presentation.Edit;
import is.idega.idegaweb.campus.block.mailinglist.business.MailingListBusiness;
import is.idega.idegaweb.campus.block.mailinglist.business.LetterParser;
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
import com.idega.util.idegaTimestamp;
import java.sql.SQLException;
import com.idega.core.data.Email;
import com.idega.core.user.business.UserBusiness;
import com.idega.block.finance.business.AccountManager;
import com.idega.block.finance.data.Account;
import com.idega.block.login.business.LoginCreator;
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

public class ContractKeyWindow extends Window{

  protected final int ACT1 = 1,ACT2 = 2, ACT3 = 3,ACT4  = 4,ACT5 = 5;
  private final static String IW_BUNDLE_IDENTIFIER="is.idega.idegaweb.campus";
  protected IWResourceBundle iwrb;
  protected IWBundle iwb;
  private boolean isAdmin;
  private String login = null;
  private String passwd = null;
  private boolean print = false;
  private SystemProperties SysProps = null;
  private GenericGroup eGroup = null;
  public static String prmContractId = "contract_id";

  /*
    Bl�r litur � topp # 27324B
    Hv�tur litur fyrir ne�an �a� # FFFFFF
    Lj�sbl�r litur � t�flu # ECEEF0
    Auka litur �rl�ti� dekkri (� lagi a� nota l�ka) # CBCFD3
  */

  public ContractKeyWindow() {
    //keepFocus();
    setWidth(300);
    setHeight(250);
    setResizable(true);
  }

  protected void control(IWContext iwc){

    iwrb = getResourceBundle(iwc);
    iwb = getBundle(iwc);
    if(iwc.isLoggedOn()){
      //add(iwrb.getLocalizedString("manual","Instructions"));
      if(iwc.getApplicationAttribute(is.idega.idegaweb.campus.data.SystemPropertiesBMPBean.getEntityTableName())!=null){
      SysProps = (SystemProperties)iwc.getApplicationAttribute(is.idega.idegaweb.campus.data.SystemPropertiesBMPBean.getEntityTableName());
      }

      if(iwc.isParameterSet("save") || iwc.isParameterSet("save.x")){
        doContract(iwc);
        setParentToReload();
      }
      add(getSignatureTable(iwc));
    }
    else
      add(Edit.formatText(iwrb.getLocalizedString("access_denied","Access denied")));

    //add(String.valueOf(iSubjectId));
  }

  public String getBundleIdentifier(){
    return IW_BUNDLE_IDENTIFIER;
  }

  public PresentationObject makeLinkTable(int menuNr){
    Table LinkTable = new Table(6,1);

    return LinkTable;
  }

  private PresentationObject getSignatureTable(IWContext iwc){
    int iContractId = Integer.parseInt( iwc.getParameter(prmContractId));
    try {
      Contract eContract = ((is.idega.idegaweb.campus.block.allocation.data.ContractHome)com.idega.data.IDOLookup.getHomeLegacy(Contract.class)).findByPrimaryKeyLegacy(iContractId);
      List listOfContracts = ContractFinder.listOfApartmentContracts(eContract.getApartmentId().intValue(),true);
      User eUser = ((com.idega.core.user.data.UserHome)com.idega.data.IDOLookup.getHomeLegacy(User.class)).findByPrimaryKeyLegacy(eContract.getUserId().intValue());
      idegaTimestamp from = new idegaTimestamp(eContract.getValidFrom());
      idegaTimestamp to = new idegaTimestamp(eContract.getValidTo());
      Applicant eApplicant = ((com.idega.block.application.data.ApplicantHome)com.idega.data.IDOLookup.getHomeLegacy(Applicant.class)).findByPrimaryKeyLegacy(eContract.getApplicantId().intValue());
      boolean apartmentReturn = eContract.getIsRented();
      DataTable T = new DataTable();
      T.setWidth("100%");
      T.addButton(new CloseButton(iwrb.getImage("close.gif")));
      String val = "";
      SubmitButton save = new SubmitButton(iwrb.getImage("save.gif"),"save");
      if(apartmentReturn){
        T.addTitle(iwrb.getLocalizedString("apartment_return","Apartment return"));
        val = "return";
        if(eContract.getStatus().equals(is.idega.idegaweb.campus.block.allocation.data.ContractBMPBean.statusEnded) || eContract.getStatus().equals(is.idega.idegaweb.campus.block.allocation.data.ContractBMPBean.statusResigned) )
          T.addButton(save);
      }
      else{
        T.addTitle(iwrb.getLocalizedString("apartment_deliver","Apartment deliver"));
        val = "deliver";
        if(eContract.getStatus().equals(is.idega.idegaweb.campus.block.allocation.data.ContractBMPBean.statusSigned) )
          T.addButton(save);
      }
      T.add(new HiddenInput("val",val),1,1);


      int row = 1;
      HiddenInput HI = new HiddenInput(prmContractId,String.valueOf(eContract.getID()));
      T.add(HI,1,row);
      T.add(Edit.formatText(iwrb.getLocalizedString("name","Name")),1,row);
      T.add(Edit.formatText(eApplicant.getFullName()),2,row);
      row++;
      T.add(Edit.formatText(iwrb.getLocalizedString("ssn","SocialNumber")),1,row);
      T.add(Edit.formatText(eApplicant.getSSN()),2,row);
      row++;
      T.add(Edit.formatText(iwrb.getLocalizedString("apartment","Apartment")),1,row);
      T.add(Edit.formatText(getApartmentString(((com.idega.block.building.data.ApartmentHome)com.idega.data.IDOLookup.getHomeLegacy(Apartment.class)).findByPrimaryKeyLegacy(eContract.getApartmentId().intValue()))),2,row);
      row++;
      T.add(Edit.formatText(iwrb.getLocalizedString("valid_from","Valid from")),1,row);
      T.add(Edit.formatText(from.getLocaleDate(iwc)),2,row);
      row++;
      T.add(Edit.formatText(iwrb.getLocalizedString("valid_to","Valid to")),1,row);
      T.add(Edit.formatText(to.getLocaleDate(iwc)),2,row);
      row++;
      T.add(Edit.formatText(iwrb.getLocalizedString("status","Status")),1,row);
      T.add(Edit.formatText(getStatus(eContract.getStatus())),2,row);
      row++;
      T.add(Edit.formatText(iwrb.getLocalizedString("returned","Returned")),1,row);
      java.sql.Timestamp retstamp = eContract.getReturnTime();
      if(retstamp !=null){
        idegaTimestamp ret = new idegaTimestamp(retstamp);
        T.add(Edit.formatText(ret.getLocaleDate(iwc)),2,row);
      }
      row++;
      T.add(Edit.formatText(iwrb.getLocalizedString("delivered","Delivered")),1,row);
      java.sql.Timestamp delstamp = eContract.getDeliverTime();
      if(delstamp !=null){
        idegaTimestamp del = new idegaTimestamp(delstamp);
        T.add(Edit.formatText(del.getLocaleDate(iwc)),2,row);
      }


      row++;
      boolean canSign = true;
      if(listOfContracts != null){
        Contract C = (Contract) listOfContracts.get(0);
        if(C.getID() != eContract.getID())
          canSign = false;
      }

      Form F = new Form();
      F.add(T);
      return F;
    }
    catch (SQLException ex) {
      return new Text("");
    }
  }

  private void doContract(IWContext iwc){

    int id = Integer.parseInt(iwc.getParameter(prmContractId));
    if(iwc.isParameterSet("val")){
      String val = iwc.getParameter("val");
      if(val.equals("return")){
        ContractBusiness.returnKey(id);
      }
      else if(val.equals("deliver")){
        ContractBusiness.deliverKey(id);
      }
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

  public void main(IWContext iwc){
    //isStaff = com.idega.core.accesscontrol.business.AccessControl
    isAdmin = iwc.hasEditPermission(this);
    control(iwc);
  }
}
