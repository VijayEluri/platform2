/*
 * $Id: RequestView.java,v 1.2 2002/02/06 10:21:17 palli Exp $
 *
 * Copyright (C) 2001 Idega hf. All Rights Reserved.
 *
 * This software is the proprietary information of Idega hf.
 * Use is subject to license terms.
 *
 */
package is.idega.idegaweb.campus.block.request.presentation;

import com.idega.block.login.business.LoginBusiness;
import com.idega.core.user.data.User;
import com.idega.idegaweb.IWBundle;
import com.idega.idegaweb.IWResourceBundle;
import com.idega.presentation.IWContext;
import com.idega.presentation.ui.Window;
import com.idega.presentation.ui.DropdownMenu;
import com.idega.presentation.ui.Form;
import com.idega.presentation.ui.SubmitButton;
import com.idega.presentation.ui.DataTable;
import com.idega.presentation.ui.CloseButton;
import com.idega.presentation.ui.HiddenInput;
import com.idega.presentation.ui.DateInput;
import com.idega.presentation.ui.TextArea;
import com.idega.presentation.ui.TextInput;
import com.idega.presentation.ui.TimeInput;
import com.idega.presentation.ui.RadioButton;
import com.idega.util.idegaTimestamp;
import is.idega.idegaweb.campus.presentation.Edit;
import is.idega.idegaweb.campus.block.request.business.RequestBusiness;

/**
 * @author <a href="mail:palli@idega.is">Pall Helgason</a>
 * @version 1.0
 */
public class RequestView extends Window {
  private final static String IW_BUNDLE_IDENTIFIER = "is.idega.idegaweb.campus";
  protected final static String REQUEST_SEND = "request_send";
  protected final static String REQUEST_TYPE = "request_type";
  public final static String REQUEST_STREET = "request_street";
  public final static String REQUEST_APRT = "request_aprt";
  public final static String REQUEST_NAME = "request_name";
  public final static String REQUEST_TEL = "request_tel";
  public final static String REQUEST_EMAIL = "request_email";
  protected final static String REQUEST_TABLE_TITLE = "request_table_title";

  protected final static String REQUEST_DATE_OF_CRASH = "request_date_of_crash";
  protected final static String REQUEST_COMMENT = "request_comment";
  protected final static String REQUEST_TIME = "request_time";
  protected final static String REQUEST_DAYTIME = "request_daytime";
  protected final static String REQUEST_SPECIAL_TIME = "request_special_time";

  protected final static String REQUEST_NO_COMMENT = "request_no_comment";
  protected final static String REQUEST_NO_DATE_OF_CRASH = "request_no_date_of_crash";
  protected final static String REQUEST_NO_SPECIAL_TIME = "request_no_special_time";

  protected final static String REQUEST_REPAIR = "request_repair";
  protected final static String REQUEST_COMPUTER = "request_computer";

  protected IWResourceBundle _iwrb;
  protected IWBundle _iwb;

  private boolean _isAdmin;
  private boolean _isLoggedOn;
  private User _eUser = null;

  /**
   *
   */
  public RequestView() {
    setWidth(650);
    setHeight(450);
    setResizable(true);
  }

  /**
   *
   */
  public String getBundleIdentifier() {
    return(IW_BUNDLE_IDENTIFIER);
  }

  /**
   *
   */
  protected void control(IWContext iwc) {
    _iwrb = getResourceBundle(iwc);
    _iwb = getBundle(iwc);

    if (_isAdmin || _isLoggedOn){

      if (iwc.isParameterSet(REQUEST_SEND)) {
        boolean check = doSendRequest(iwc);
        if (check) {
          setParentToReload();
          close();
        }
        else
        ; //Do some error checking
      }

      addMainForm(iwc);
    }
    else
      add(Edit.formatText(_iwrb.getLocalizedString("access_denied","Access denied")));
  }

  /**
   *
   */
  protected boolean doSendRequest(IWContext iwc) {
    String comment = iwc.getParameter(REQUEST_COMMENT);
    String dateOfFailureString = iwc.getParameter(REQUEST_DATE_OF_CRASH);
    String type = iwc.getParameter(REQUEST_TYPE);
    if (type.equals(REQUEST_COMPUTER))
      type = RequestBusiness.REQUEST_COMPUTER;
    else
      type = RequestBusiness.REQUEST_REPAIR;
    String special = iwc.getParameter(REQUEST_SPECIAL_TIME);
    idegaTimestamp t = new idegaTimestamp(dateOfFailureString);

    boolean insert = RequestBusiness.insertRequest(_eUser.getID(),comment,t.getTimestamp(),type,special);

    return(insert);
  }

  /**
   *
   */
  protected void addMainForm(IWContext iwc) {
    Form form = new Form();
    add(form);

    DropdownMenu mnu = new DropdownMenu(REQUEST_TYPE);
    mnu.addMenuElement(REQUEST_COMPUTER,"T�lvuvi�ger�");
    mnu.addMenuElement(REQUEST_REPAIR,"Almenn vi�ger�");
    mnu.setToSubmit();
    Edit.setStyle(mnu);
    form.add(mnu);

    String type = iwc.getParameter(REQUEST_TYPE);
    if (type == null)
      type = REQUEST_REPAIR;
    mnu.setSelectedElement(type);

    String street = iwc.getParameter(REQUEST_STREET);
    String aprt = iwc.getParameter(REQUEST_APRT);
    String name = iwc.getParameter(REQUEST_NAME);
    String telephone = iwc.getParameter(REQUEST_TEL);
    String email = iwc.getParameter(REQUEST_EMAIL);

    DataTable data = new DataTable();
    data.setWidth("100%");
    data.addTitle(_iwrb.getLocalizedString(REQUEST_TABLE_TITLE,"Senda bei�ni"));
    data.addButton(new SubmitButton(REQUEST_SEND,"Senda bei�ni"));
    form.add(data);

    int row = 1;
    data.add(Edit.formatText(_iwrb.getLocalizedString(REQUEST_STREET,"G�tuheiti")),1,row);
    data.add(Edit.formatText(street),2,row);
    row++;
    data.add(Edit.formatText(_iwrb.getLocalizedString(REQUEST_APRT,"Herb./�b��")),1,row);
    data.add(Edit.formatText(aprt),2,row);
    row++;
    data.add(Edit.formatText(_iwrb.getLocalizedString(REQUEST_NAME,"Nafn")),1,row);
    data.add(Edit.formatText(name),2,row);
    row++;
    data.add(Edit.formatText(_iwrb.getLocalizedString(REQUEST_TEL,"S�man�mer")),1,row);
    data.add(Edit.formatText(telephone),2,row);
    row++;
    data.add(Edit.formatText(_iwrb.getLocalizedString(REQUEST_EMAIL,"email")),1,row);
    data.add(Edit.formatText(email),2,row);
    row++;

    if (type.equals(REQUEST_REPAIR))
      addRepair(data,row);
    else if (type.equals(REQUEST_COMPUTER))
      addComputer(data,row);

    form.add(new HiddenInput(REQUEST_STREET,street));
    form.add(new HiddenInput(REQUEST_APRT,aprt));
    form.add(new HiddenInput(REQUEST_NAME,name));
    form.add(new HiddenInput(REQUEST_TEL,telephone));
    form.add(new HiddenInput(REQUEST_EMAIL,email));
  }

  /**
   *
   */
  protected void addRepair(DataTable data, int row) {
    data.add(Edit.formatText(_iwrb.getLocalizedString(REQUEST_DATE_OF_CRASH,"Dagsetning bilunar")),1,row);
    DateInput dateOfCrash = new DateInput(REQUEST_DATE_OF_CRASH);
    dateOfCrash.setToCurrentDate();
    Edit.setStyle(dateOfCrash);
    data.add(dateOfCrash,2,row);
    row++;
    data.add(Edit.formatText(_iwrb.getLocalizedString(REQUEST_COMMENT,"Athugasemdir")),1,row);
    TextArea comment = new TextArea(REQUEST_COMMENT,"",60,5);
    Edit.setStyle(comment);
    data.add(comment,2,row);
    row++;
    data.add(new RadioButton(REQUEST_TIME,REQUEST_DAYTIME),1,row);
    data.add(Edit.formatText(_iwrb.getLocalizedString(REQUEST_DAYTIME,"Vi�ger� m� fara fram � dagvinnut�ma, �n �ess a� nokkur s� heima.�ri�judagar eru almennir vi�ger�ardagar.")),2,row);
    row++;
    data.add(new RadioButton(REQUEST_TIME,REQUEST_SPECIAL_TIME),1,row);
    data.add(Edit.formatText(_iwrb.getLocalizedString(REQUEST_SPECIAL_TIME,"�g �ska eftir s�rstakri t�masetningu og a� vi�ger� ver�i framkv�md: ")),2,row);
    data.add(new TextInput(REQUEST_SPECIAL_TIME),2,row);
    row++;
  }

  /**
   *
   */
  protected void addComputer(DataTable data, int row) {
    data.add(Edit.formatText(_iwrb.getLocalizedString(REQUEST_DATE_OF_CRASH,"Dagsetning bilunar")),1,row);
    DateInput dateOfCrash = new DateInput(REQUEST_DATE_OF_CRASH);
    Edit.setStyle(dateOfCrash);
    data.add(dateOfCrash,2,row);
    row++;
    data.add(Edit.formatText(_iwrb.getLocalizedString(REQUEST_COMMENT,"Athugasemdir")),1,row);
    TextArea comment = new TextArea(REQUEST_COMMENT,"",60,5);
    Edit.setStyle(comment);
    data.add(comment,2,row);
    row++;
    data.add(Edit.formatText(_iwrb.getLocalizedString(REQUEST_SPECIAL_TIME,"�g �ska eftir s�rstakri t�masetningu og a� vi�ger� ver�i framkv�md: ")),2,row);
    data.add(new TextInput(REQUEST_SPECIAL_TIME),2,row);
    row++;
  }
  /**
   *
   */
  public void main(IWContext iwc) throws Exception {
    _eUser = iwc.getUser();
    _isAdmin = iwc.hasEditPermission(this);
    _isLoggedOn = LoginBusiness.isLoggedOn(iwc);
    control(iwc);
  }
}