package is.idega.idegaweb.campus.block.allocation.presentation;


import is.idega.idegaweb.campus.presentation.Edit;
import is.idega.idegaweb.campus.block.allocation.business.ContractBusiness;
import is.idega.idegaweb.campus.block.allocation.data.Contract;
import is.idega.idegaweb.campus.data.SystemProperties;
import is.idega.idegaweb.campus.block.application.data.WaitingList;
import is.idega.idegaweb.campus.block.allocation.business.WaitingListFinder;
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
import java.util.Iterator;
import com.idega.util.SendMail;
/**
 * Title:
 * Description:
 * Copyright:    Copyright (c) 2001
 * Company:
 * @author
 * @version 1.0
 */

public class ContractSigner extends PresentationObjectContainer{

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

  /*
    Bl�r litur � topp # 27324B
    Hv�tur litur fyrir ne�an �a� # FFFFFF
    Lj�sbl�r litur � t�flu # ECEEF0
    Auka litur �rl�ti� dekkri (� lagi a� nota l�ka) # CBCFD3
  */

  public ContractSigner() {
  }

  protected void control(IWContext iwc){
    iwrb = getResourceBundle(iwc);
    iwb = getBundle(iwc);

    if(isAdmin){
      if(iwc.getApplicationAttribute(SysProps.getEntityTableName())!=null){
      SysProps = (SystemProperties)iwc.getApplicationAttribute(SysProps.getEntityTableName());
      }

      if(iwc.getParameter("sign")!=null || iwc.getParameter("save")!=null){
        doSignContract(iwc);
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
    int iContractId = Integer.parseInt( iwc.getParameter("signed_id"));
    try {
      Contract eContract = new Contract(iContractId);
      User eUser = new User(eContract.getUserId().intValue());
      idegaTimestamp from = new idegaTimestamp(eContract.getValidFrom());
      idegaTimestamp to = new idegaTimestamp(eContract.getValidTo());
      Applicant eApplicant = new Applicant(eContract.getApplicantId().intValue());
      List lEmails = UserBusiness.listOfUserEmails(eContract.getUserId().intValue());
      List lFinanceAccounts = AccountManager.listOfAccounts(eContract.getUserId().intValue(),Account.typeFinancial);
      List lPhoneAccounts = AccountManager.listOfAccounts(eContract.getUserId().intValue(),Account.typePhone);

      if(SysProps != null){
        int groupId = SysProps.getDefaultGroup();
        try {
          eGroup = new GenericGroup(groupId);
        }
        catch (SQLException ex) {
          eGroup = null;
        }
      }
      LoginTable loginTable = LoginDBHandler.getUserLogin(eContract.getUserId().intValue());
      Table T = new Table();

      SubmitButton save = new SubmitButton("save",iwrb.getLocalizedString("save","Save"));
      SubmitButton signed = new SubmitButton("sign",iwrb.getLocalizedString("signed","Signed"));
      CloseButton close = new CloseButton(iwrb.getLocalizedString("close","Close"));
      PrintButton PB = new PrintButton(iwrb.getLocalizedString("print","Print"));
      TextInput email = new TextInput("new_email");
      CheckBox accountCheck = new CheckBox("new_fin_account");
      accountCheck.setChecked(true);
      CheckBox phoneAccountCheck = new CheckBox("new_phone_account");
      phoneAccountCheck.setChecked(true);
      CheckBox loginCheck = new CheckBox("new_login");
      loginCheck.setChecked(true);

      int row = 2;
      T.add(Edit.titleText(iwrb.getLocalizedString("name","Name")+" : "),1,row);
      T.add(eApplicant.getFullName(),2,row);
      row++;
      T.add(Edit.titleText(iwrb.getLocalizedString("ssn","SocialNumber")+" : "),1,row);
      T.add(eApplicant.getSSN(),2,row);
      row++;
      T.add(Edit.titleText(iwrb.getLocalizedString("apartment","Apartment")+" : "),1,row);
      T.add(Edit.formatText(getApartmentString(new Apartment(eContract.getApartmentId().intValue()))),2,row);
      row++;
      T.add(Edit.titleText(iwrb.getLocalizedString("contractdate","Contract date")+" :"),1,row);
      T.add(Edit.formatText(from.getLocaleDate(iwc)+" "+to.getLocaleDate(iwc)),2,row);
      row++;
      T.add(Edit.titleText(iwrb.getLocalizedString("email","Email")+" : "),1,row);
      if(lEmails !=null){
        T.add(Edit.formatText( ((Email)lEmails.get(0)).getEmailAddress()),2,row);
      }
      else{
        T.add(email,2,row);
      }
      row++;
      row++;
      if(eGroup != null){
        HiddenInput Hgroup = new HiddenInput("user_group",String.valueOf(eGroup.getID()));
        T.add(Hgroup);
        if(lFinanceAccounts == null){
          T.add(accountCheck,2,row);
          T.add(Edit.titleText(iwrb.getLocalizedString("fin_account","New finance account")),2,row);
        }
        else{
          int len = lFinanceAccounts.size();
          for (int i = 0; i < len; i++) {
            T.add(Edit.titleText(iwrb.getLocalizedString("fin_account","Finance account")+" : "),1,row);
            T.add(Edit.formatText( ((Account)lFinanceAccounts.get(i)).getName() +" "),2,row);
          }
        }
        row++;
        if(lPhoneAccounts == null){
          T.add(phoneAccountCheck,2,row);
          T.add(Edit.titleText(iwrb.getLocalizedString("phone_account","New phone account")),2,row);
        }
        else{
          int len = lPhoneAccounts.size();
          for (int i = 0; i < len; i++) {
            T.add(Edit.titleText(iwrb.getLocalizedString("phone_account","Phone account")+" : "),1,row);
            T.add(Edit.formatText( ((Account)lPhoneAccounts.get(i)).getName() +" "),2,row);
          }
        }
        row++;
        if(loginTable != null ){
          T.add(Edit.titleText(iwrb.getLocalizedString("login","Login")+" : "),1,row);
          T.add(Edit.formatText(loginTable.getUserLogin()),2,row);
          row++;
          T.add(Edit.titleText(iwrb.getLocalizedString("passwd","Passwd")+" : "),1,row);
          if(passwd != null)
            T.add(Edit.formatText(passwd),2,row++);
        }
        else{
          T.add(loginCheck,2,row);
          T.add(Edit.titleText(iwrb.getLocalizedString("new_login","New login")),2,row);
        }
        row++;
        HiddenInput HI = new HiddenInput("signed_id",String.valueOf(eContract.getID()));
        if(eContract.getStatus().equalsIgnoreCase(eContract.statusSigned))
          T.add(save,2,row);
        else
          T.add(signed,2,row);
        if(print){
          T.add(PB,2,row);
        }
        T.add(close,2,row);

        T.add(HI,1,row);
      }
      else{
        T.add(Edit.formatText(iwrb.getLocalizedString("syspropserror","System property error")),1,row);
      }
      Form F = new Form();
      F.add(T);
      return F;
    }
    catch (SQLException ex) {
      return new Text("");
    }
  }

  /**
   *  Signing contracts included creation of financial account,email and login
   *  returns id of login
   */
  private void doSignContract(IWContext iwc){

    int id = Integer.parseInt(iwc.getParameter("signed_id"));
    String sEmail = iwc.getParameter("new_email");
    String sSendMail = iwc.getParameter("send_mail");
    String sFinAccount = iwc.getParameter("new_fin_account");
    String sPhoneAccount = iwc.getParameter("new_phone_account");
    String sCreateLogin = iwc.getParameter("new_login");
    String sUserGroup = iwc.getParameter("user_group");
    String sSigned =  iwc.getParameter("sign");
    int iGroupId = sUserGroup != null ? Integer.parseInt(sUserGroup):-1;
    boolean sendMail =  sSendMail != null ? true:false;
    sendMail = true;
    boolean newAccount =  sFinAccount != null ? true:false;
    boolean newPhoneAccount =   sPhoneAccount != null ? true:false;
    boolean createLogin =   sCreateLogin != null ? true:false;
    passwd = ContractBusiness.signCampusContract(id,iGroupId ,1,sEmail,sendMail,
      newAccount ,newPhoneAccount ,createLogin ,false,iwrb,login,passwd  );
    if(login !=null && passwd !=null)
      print = true;
    else
      print = false;
    //add(passwd);
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

  public void main(IWContext iwc){
    //isStaff = com.idega.core.accesscontrol.business.AccessControl
    isAdmin = iwc.hasEditPermission(this);
    control(iwc);
  }
}
