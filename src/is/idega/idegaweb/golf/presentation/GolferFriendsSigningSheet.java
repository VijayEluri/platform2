package is.idega.idegaweb.golf.presentation;

import com.idega.presentation.Block;
import com.idega.presentation.Table;
import com.idega.presentation.IWContext;
import com.idega.presentation.text.Link;
import com.idega.presentation.Image;
import com.idega.presentation.ui.Form;
import com.idega.presentation.ui.SubmitButton;
import com.idega.presentation.text.Text;
import com.idega.presentation.ui.TextArea;
import com.idega.presentation.ui.TextInput;
import com.idega.presentation.ui.CheckBox;
import com.idega.presentation.ui.RadioButton;
import com.idega.presentation.ui.DropdownMenu;
import com.idega.presentation.ui.Parameter;
import com.idega.presentation.ui.Window;
import com.idega.jmodule.text.presentation.TextReader;
import com.idega.presentation.Script;
import com.idega.presentation.ui.DateInput;
import is.idega.idegaweb.golf.business.GolferFriendsDataBusiness;
import java.sql.Date;
import com.idega.idegaweb.IWBundle;
import com.idega.idegaweb.IWResourceBundle;
import java.sql.SQLException;

/**
 * Title:        idegaWeb Classes
 * Description:
 * Copyright:    Copyright (c) 2001
 * Company:
 * @author <a href="bjarni@idega.is">Bjarni Viljhalmsson</a>
 * @version 1.0
 */

public class GolferFriendsSigningSheet extends Block {

  public static final String nameInputName = "nameInputName";
  public static final String sSNumberInputName = "sSNumberInputName";
  public static final String anotherAmountInputName = "anotherAmountInputName";
  public static final String anotherDurationInputName = "anotherDurationInputName";
  public static final String cretidCardTypeInputName = "cretidCardTypeInputName";
  public static final String creditCardNumberInputName = "creditCardNumberInputName";
  public static final String creditCardExpDateInputName = "creditCardExpDateInputName";

  public static final String billingNameSSNInputName = "billingNameSSNInputName";
  public static final String appearNameInputName = "appearNameInputName";
  public static final String billingNameInputName = "billingNameInputName";

  public static final String amountRadioButtonName = "amountRadioButtonName";
  public static final String durationRadioButtonName = "durationRadioButtonName";

  public static final String viewSignedFormsWindowName = "viewSignedFormsWindowName";

  public static final String amountButtonValue1 = "amountButtonValue1";
  public static final String amountButtonValue2 = "amountButtonValue2";
  public static final String amountButtonValue3 = "amountButtonValue3";
  public static final String amountButtonValue4 = "amountButtonValue4";
  public static final String amountButtonValue5 = "amountButtonValue5";
  public static final String durationButtonValue1 = "durationButtonValue1";
  public static final String durationButtonValue2 = "durationButtonValue2";
  public static final String durationButtonValue3 = "durationButtonValue3";
  public static final String durationButtonValue4 = "durationButtonValue4";
  public static final String durationButtonValue5 = "durationButtonValue5";
  public static final String yesOrNoButtonName = "yesOrNoButtonName";
  public static final String noValue = "noValue";
  public static final String yesValue = "yesValue";
  public String submitButtonName;
  public String submitButtonValue;
  public String controlParameterValue;
  public static final String hiddenInputName = "hiddenInputName";
  public static final String cardTypeMenuName = "cardTypeMenuName";
  public static final String billCheckBoxName = "billCheckBoxName";
  public static final String golferParamterName = "golferParamterName";

  public String fullGolferName = "";

  protected Form form;
  {
    form = new Form();
  }

  protected Script script;

  protected String golferName;
  protected boolean isAdmin;

  public final String adressAreaName = "adressAreaName";
  public final String billingAdressAreaName = "billingAdressAreaName";

  private final static String IW_BUNDLE_IDENTIFIER="com.idega.idegaweb.golf";

  public int headlineTextReaderId;
  public TextReader headlineText;
  protected IWResourceBundle iwrb;
  protected IWBundle iwb;

  public GolferFriendsSigningSheet(int headlineTextReaderId, String golferName,
    String submitName, String submitValue, String controlParameterValue, String fullGolferName) {
    this.submitButtonName = submitName;
    this.submitButtonValue = submitValue;
    this.golferName = golferName;
    this.headlineTextReaderId = headlineTextReaderId;
    this.controlParameterValue = controlParameterValue;
    this.fullGolferName = fullGolferName;
  }

  public String getBundleIdentifier(){
    return IW_BUNDLE_IDENTIFIER;
  }

  public void drawSigningForm(){

   //B�ta inn m�guleikanum �egar �a� vantar ID
    if (headlineTextReaderId != 0){
      headlineText = new TextReader(headlineTextReaderId);
    }
    else{
      headlineText = new TextReader(1);  //temporarily
    }

/*0*/ Text[] sheetTableStrings = {new Text(iwrb.getLocalizedString("golferpage.supporter","Stu�ningsa�ili")+":"),
      new Text(iwrb.getLocalizedString("golferpage.name","Nafn:")+":"),
      new Text(iwrb.getLocalizedString("golferpage.social_security_number","Kennitala")+":"),
      new Text(iwrb.getLocalizedString("golferpage.adress","Heimilisfang")+":"),
/*4*/    new Text(iwrb.getLocalizedString("golferpage.support_amount","Stu�ningsupph�� kr.")+":"),
      new Text(iwrb.getLocalizedString("golferpage.support_duration","Stu�ningst�mabil / skuldf�rsla � hversu marga m�nu�i")+":"),
      new Text(iwrb.getLocalizedString("golferpage.payment_type","Grei�slufyrirkomulag")+":"),
      new Text(iwrb.getLocalizedString("golferpage.cash_payment","Sta�greitt")),
/*8*/    new Text(iwrb.getLocalizedString("golferpage.into_an_account","H�gt er a� leggja � reikning:")),
      new Text(iwrb.getLocalizedString("golferpage.s_s_number","kennitala reiknings:")),
      new Text(iwrb.getLocalizedString("golferpage.pay_director","Einnig er h�gt a� koma grei�slu til framkv�mdarstj�ra GK")),
      new Text(iwrb.getLocalizedString("golferpage.giro_payment","G�r�se�ill")),
/*12*/   new Text(iwrb.getLocalizedString("golferpage.require_giro_payment","�ska eftir a� f� sendan g�r�se�il")),
      new Text(iwrb.getLocalizedString("golferpage.credit_card","Kreditkort")),
      new Text(iwrb.getLocalizedString("golferpage.credit_card_type","Kortategund")),
      new Text(iwrb.getLocalizedString("golferpage.credit_card_number","Kortan�mer")),
/*16*/   new Text(iwrb.getLocalizedString("golferpage.credit_card_exp_date","Gildist�mi")),
      new Text(iwrb.getLocalizedString("golferpage.another","Anna�")+":"),
      new Text(iwrb.getLocalizedString("golferpage.name_may_appear","Nafn mitt m� koma fram � heimas��u ")
      +golferName), new Text(iwrb.getLocalizedString("golferpage.yes","J�")),
/*20*/   new Text(iwrb.getLocalizedString("golferpage.no","Nei")),
      new Text(iwrb.getLocalizedString("golferpage.once","Eitt skipti")),
      new Text("2 "+iwrb.getLocalizedString("golferpage.months","m�n.")),
      new Text("3 "+iwrb.getLocalizedString("golferpage.months","m�n.")),
/*24*/   new Text("4 "+iwrb.getLocalizedString("golferpage.months","m�n.")),
      new Text("5 "+iwrb.getLocalizedString("golferpage.months","m�n.")),
      new Text("1000"),
      new Text("2000"),
/*28*/   new Text("3000"),
      new Text("4000"),
      new Text("5000"),
      new Text(iwrb.getLocalizedString("golferpage.name_to_appear","Nafn sem birta m�")),
/*32*/    new Text("1101-05-400733"),
      new Text("6801696919"),
      new Text(iwrb.getLocalizedString("golferpage.another_bold","Anna�"+":")),
      new Text(iwrb.getLocalizedString("golferpage.get_bill_sent","F� sendan reikning:")),
/*36*/    new Text(iwrb.getLocalizedString("golferpage.billing_name","Nafn grei�anda:")),
      new Text(iwrb.getLocalizedString("golferpage.billing_ss_number","Kennitala grei�anda:")),
      new Text(iwrb.getLocalizedString("golferpage.billing_adress","Heimilisfang grei�anda:")),
      new Text(iwrb.getLocalizedString("golferpage.cashpayment_notice","Athugi� a� ekki er b�i� a� ganga fr� grei�slu �egar smellt er � \"skr� mig\" her a� ne�an.  Vinsamlegast geri� skil � grei�slu sem allra fyrst, sbr. a� ofan.")),
/*40*/    new Text(iwrb.getLocalizedString("golferpage.credit_card_notice","Athugi� a� gengi� ver�ur fr� skuldf�rslu � korti� eftir a� smellt hefur veri� � \"skr� mig\" h�r a� ne�an."))};

    for (int i = 0; i < sheetTableStrings.length; i++) {
      sheetTableStrings[i].setFontSize(1);
//      sheetTableStrings[i].setFontColor("FFFFFF");
    }

    String headlinesColor = new String("FF6000");

    sheetTableStrings[0].setFontSize(2);
    sheetTableStrings[0].setBold();
    sheetTableStrings[0].setFontColor(headlinesColor);
    sheetTableStrings[4].setFontSize(2);
    sheetTableStrings[4].setBold();
    sheetTableStrings[4].setFontColor(headlinesColor);
    sheetTableStrings[5].setFontSize(2);
    sheetTableStrings[5].setBold();
    sheetTableStrings[5].setFontColor(headlinesColor);
    sheetTableStrings[6].setFontSize(2);
    sheetTableStrings[6].setBold();
    sheetTableStrings[6].setFontColor(headlinesColor);
    sheetTableStrings[34].setBold();
    sheetTableStrings[34].setFontColor(headlinesColor);
    sheetTableStrings[34].setFontSize(2);

    sheetTableStrings[7].setBold();
    sheetTableStrings[11].setBold();
    sheetTableStrings[13].setBold();


    TextArea addressArea = new TextArea(adressAreaName,20,2);
    TextArea billingAddressArea = new TextArea(billingAdressAreaName,20,2);

    addressArea.keepStatusOnAction();
    billingAddressArea.keepStatusOnAction();


    TextInput nameInput = new TextInput(nameInputName);
    nameInput.keepStatusOnAction();
    nameInput.setAsNotEmpty(iwrb.getLocalizedString(
      "golferpage.please_give_name","Vinsamlegast gefi� nafn"));
    nameInput.setAsAlphabeticText(iwrb.getLocalizedString(
      "golferpage.please_write_letters","Vinsamlegast skrifi� b�kstafi � nafni"));
    TextInput sSNumberInput = new TextInput(sSNumberInputName);
    sSNumberInput.setAsNotEmpty(iwrb.getLocalizedString(
      "golferpage.please_give_ss_number","Vinsamlegast gefi� gilda �slenska kennit�lu"));
    sSNumberInput.setAsIcelandicSSNumber(iwrb.getLocalizedString(
      "golferpage.please_give_ssn_integers","Vinsamlegast gefi� kennit�lu"));
    TextInput anotherAmountInput = new TextInput(anotherAmountInputName);
    anotherAmountInput.setAsIntegers(iwrb.getLocalizedString(
      "golferpage.please_give_amount_integers","Vinsamlegast a�eins t�lustafi i upph��"));
    TextInput anotherDurationInput = new TextInput(anotherDurationInputName);

    TextInput creditCardNumberInput = new TextInput(creditCardNumberInputName);
    creditCardNumberInput.setAsIntegers(iwrb.getLocalizedString(
      "golferpage.please_give_card_number_integers","Vinsamlegast a�eins t�lustafi i kortan�meri"));
    creditCardNumberInput.setAsCredidCardNumber(iwrb.getLocalizedString(
      "golferpage.please_give_valid_card","Vinsamlegast gefi� gilt kortan�mer"));
    TextInput creditCardExpDateInput = new TextInput(creditCardExpDateInputName);


    TextInput billingNameInput = new TextInput(billingNameInputName);
    billingNameInput.setAsAlphabeticText(iwrb.getLocalizedString(
      "golferpage.please_no_numbers_in_billing_name","Vinsamlegast enga t�lustafi i nafni reikningseiganda"));
    TextInput billingNameSSNInput = new TextInput(billingNameSSNInputName);
    billingNameSSNInput.setAsIcelandicSSNumber(iwrb.getLocalizedString(
      "golferpage.please_a_valid_ssn_in_bill","Vinsamlegast gefi� gilda kennit�lu i kennit�lu reikningseiganda"));
    TextInput appearNameInput = new TextInput(appearNameInputName);
    appearNameInput.setAsAlphabeticText(iwrb.getLocalizedString(
      "golferpage.please_no_numbers_in_apearance_name","Vinsamlegast enga t�lustafi i birtingar nafni"));

    sSNumberInput.setSize(10);
    anotherAmountInput.setSize(10);
    anotherDurationInput.setSize(15);
    billingNameSSNInput.setSize(10);

    billingNameInput.keepStatusOnAction();
    billingNameSSNInput.keepStatusOnAction();
    appearNameInput.keepStatusOnAction();
    nameInput.keepStatusOnAction();
    sSNumberInput.keepStatusOnAction();
    anotherAmountInput.keepStatusOnAction();
    anotherDurationInput.keepStatusOnAction();
    creditCardNumberInput.keepStatusOnAction();
    creditCardExpDateInput.keepStatusOnAction();

    appearNameInput.setDisabled(true);
    billingNameInput.setDisabled(true);
    billingNameSSNInput.setDisabled(true);
    billingAddressArea.setDisabled(true);

    RadioButton yesButton = new RadioButton(yesOrNoButtonName, yesValue);
    RadioButton noButton = new RadioButton(yesOrNoButtonName, noValue);
    noButton.setToDisableOnClick(appearNameInput);
    noButton.setSelected();
    yesButton.setToEnableOnClick(appearNameInput);
    yesButton.setOnClick("this.form."+appearNameInputName+".value = this.form."+nameInputName+".value");

    RadioButton amountRadioButton1 = new RadioButton( amountRadioButtonName, amountButtonValue1);
    RadioButton amountRadioButton2 = new RadioButton( amountRadioButtonName, amountButtonValue2);
    RadioButton amountRadioButton3 = new RadioButton( amountRadioButtonName, amountButtonValue3);
    RadioButton amountRadioButton4 = new RadioButton( amountRadioButtonName, amountButtonValue4);
    RadioButton durationRadioButton1 = new RadioButton(durationRadioButtonName, durationButtonValue1);
    RadioButton durationRadioButton2 = new RadioButton(durationRadioButtonName, durationButtonValue2);
    RadioButton durationRadioButton3 = new RadioButton(durationRadioButtonName, durationButtonValue3);
    RadioButton durationRadioButton4 = new RadioButton(durationRadioButtonName, durationButtonValue4);

    Parameter hiddenInput = new Parameter(hiddenInputName, "false");

    amountRadioButton1.setOnClick("this.form.hiddenInputName.value=true");
    amountRadioButton2.setOnClick("this.form.hiddenInputName.value=true");
    amountRadioButton3.setOnClick("this.form.hiddenInputName.value=true");
    amountRadioButton4.setOnClick("this.form.hiddenInputName.value=true");

    amountRadioButton1.keepStatusOnAction();
    amountRadioButton2.keepStatusOnAction();
    amountRadioButton3.keepStatusOnAction();
    amountRadioButton4.keepStatusOnAction();
    yesButton.keepStatusOnAction();
    noButton.keepStatusOnAction();
    durationRadioButton1.keepStatusOnAction();
    durationRadioButton2.keepStatusOnAction();
    durationRadioButton3.keepStatusOnAction();
    durationRadioButton4.keepStatusOnAction();

    DropdownMenu cardTypeMenu = new DropdownMenu(cardTypeMenuName);
    cardTypeMenu.keepStatusOnAction();
    cardTypeMenu.addMenuElement("Visa", "Visa");
    cardTypeMenu.addMenuElement("Eurocard", "Eurocard");

    CheckBox billCheckBox = new CheckBox(billCheckBoxName);
    billCheckBox.keepStatusOnAction();
    billCheckBox.setOnClick("toggleHandler(this)");
    billCheckBox.setOnClick("autoFillHandler(this)");

    Image submitImage = new Image();
    submitImage = iwrb.getImage("/golferpage/register.gif");

    DateInput expDateInput = new DateInput(creditCardExpDateInputName);
    expDateInput.keepStatusOnAction();
    expDateInput.setNoDayView();
    expDateInput.setToCurrentDate();

    SubmitButton submitButton = new SubmitButton(submitImage,
      //iwrb.getLocalizedString("golferpage.sign_myself","Skr� mig"),
      submitButtonName, submitButtonValue);


    Image signFormBackground;

    signFormBackground = iwb.getImage("/shared/formGrunn.gif");

    Table mainTable = new Table(2,30);

//    mainTable.setBackgroundImage(signFormBackground);

    mainTable.setCellpadding(4);
    mainTable.setCellspacing(4);

    mainTable.mergeCells(1,1,2,1);
    mainTable.mergeCells(1,2,2,2);
    mainTable.mergeCells(1,6,2,6);
    mainTable.mergeCells(1,8,2,8);
    mainTable.mergeCells(1,9,2,9);
    mainTable.mergeCells(1,14,2,14);
    mainTable.mergeCells(1,11,2,11);
    mainTable.mergeCells(1,12,2,12);
    mainTable.mergeCells(1,18,2,18);
    mainTable.mergeCells(1,19,2,19);
    mainTable.mergeCells(1,24,2,24);

    mainTable.setAlignment(1, 1, "center");

    headlineText.setAlignment("center");

    mainTable.add(headlineText,1,1);
    mainTable.addBreak(1,2);
    mainTable.add(sheetTableStrings[0],1,2);
    mainTable.add(sheetTableStrings[1],1,3);
    mainTable.add(nameInput,2,3);
    mainTable.add(sheetTableStrings[2],1,4);
    mainTable.add(sSNumberInput,2,4);
    mainTable.add(sheetTableStrings[3],1,5);
    mainTable.add(addressArea,2,5);
    Table dummyTable1 = new Table(4,2);
    dummyTable1.setCellpadding(2);
    dummyTable1.mergeCells(1,1,4,1);
    dummyTable1.setAlignment(1,2, "right");
    dummyTable1.setAlignment(3,2, "right");
    dummyTable1.add(sheetTableStrings[18],1,1);
    dummyTable1.add(sheetTableStrings[19],2,2);
    dummyTable1.add(yesButton,1,2);
    dummyTable1.add(sheetTableStrings[20],4,2);
    dummyTable1.add(noButton,3,2);
    mainTable.add(dummyTable1,1,6);
    mainTable.add(sheetTableStrings[31],1,7);
    mainTable.add(appearNameInput,2,7);
    mainTable.addBreak(1,8);
    mainTable.add(sheetTableStrings[4],1,8);

    Table dummyTable2 = new Table(8,1);
    dummyTable2.setWidth("100%");
    dummyTable2.setAlignment(7,1,"right");
    dummyTable2.setAlignment(5,1,"right");
    dummyTable2.setAlignment(3,1,"right");
    dummyTable2.setAlignment(1,1,"right");
    dummyTable2.add(sheetTableStrings[26],2,1);
    dummyTable2.add(amountRadioButton1,1,1);
    dummyTable2.add(sheetTableStrings[27],4,1);
    dummyTable2.add(amountRadioButton2,3,1);
    dummyTable2.add(sheetTableStrings[28],6,1);
    dummyTable2.add(amountRadioButton3,5,1);
    dummyTable2.add(sheetTableStrings[29],8,1);
    dummyTable2.add(amountRadioButton4,7,1);
    mainTable.add(dummyTable2,1,9);
    mainTable.add(sheetTableStrings[17],1,10);
    mainTable.setAlignment(1,10, "right");
    mainTable.add(anotherAmountInput,2,10);

    mainTable.addBreak(1,11);
    mainTable.add(sheetTableStrings[5],1,11);
    Table dummyTable3 = new Table(8,1);
    dummyTable3.setWidth("100%");
    dummyTable3.setAlignment(7,1,"right");
    dummyTable3.setAlignment(5,1,"right");
    dummyTable3.setAlignment(3,1,"right");
    dummyTable3.setAlignment(1,1,"right");
    dummyTable3.add(sheetTableStrings[21],2,1);
    dummyTable3.add(durationRadioButton1,1,1);
    dummyTable3.add(sheetTableStrings[22],4,1);
    dummyTable3.add(durationRadioButton2,3,1);
    dummyTable3.add(sheetTableStrings[23],6,1);
    dummyTable3.add(durationRadioButton3,5,1);
    dummyTable3.add(sheetTableStrings[24],8,1);
    dummyTable3.add(durationRadioButton4,7,1);
    mainTable.add(dummyTable3,1,12);
    mainTable.setAlignment(1,13, "right");
    mainTable.add(sheetTableStrings[17],1,13);
    mainTable.add(anotherDurationInput,2,13);
    mainTable.addBreak(1,14);
    mainTable.add(sheetTableStrings[6],1,14);

    Table dummyTable4 = new Table(1,1);
    dummyTable4.add(sheetTableStrings[7],1,1);
    mainTable.add(dummyTable4,1,15);
    mainTable.add(sheetTableStrings[8],1,16);
    mainTable.add(sheetTableStrings[32],2,16);
    mainTable.add(sheetTableStrings[9],1,17);
    mainTable.add(sheetTableStrings[33],2,17);
    mainTable.add(sheetTableStrings[10],1,18);
    mainTable.add(sheetTableStrings[39],1,19);

    Table bottomSheetTable = new Table(3,4);
    bottomSheetTable.setCellpadding(0);
    bottomSheetTable.setCellspacing(0);
    bottomSheetTable.mergeCells(2,1,3,1);
/*    Table dummyTable5 = new Table(1,1);
    dummyTable5.add(sheetTableStrings[11],1,1);
    mainTable.add(dummyTable5,1,17);
    mainTable.add(sheetTableStrings[12],1,18);
    mainTable.add(giroPaymentCheckBox,2,18);*/
    Table dummyTable6 = new Table(1,1);
    dummyTable6.add(sheetTableStrings[13],1,1);
    mainTable.add(dummyTable6,1,20);
    mainTable.add(sheetTableStrings[14],1,21);
    mainTable.add(cardTypeMenu,2,21);
    mainTable.add(sheetTableStrings[15],1,22);
    mainTable.add(creditCardNumberInput,2,22);
    mainTable.add(sheetTableStrings[16],1,23);
    mainTable.add(expDateInput,2,23);
    mainTable.add(sheetTableStrings[40],1,24);

    mainTable.addBreak(1,25);
    mainTable.add(sheetTableStrings[34],1,25);
    mainTable.add(sheetTableStrings[35],1,26);
    mainTable.add(billCheckBox,2,26);
    mainTable.add(sheetTableStrings[36],1,27);
    mainTable.add(billingNameInput,2,27);
    mainTable.add(sheetTableStrings[37],1,28);
    mainTable.add(billingNameSSNInput,2,28);
    mainTable.add(sheetTableStrings[38],1,29);
    mainTable.add(billingAddressArea,2,29);

    Table dummyTable7 = new Table(1,1);
    dummyTable7.add(submitButton,1,1);
    if (isAdmin){
      Window viewSignedFormsWindow = new Window( viewSignedFormsWindowName, "/golfers/viewFriendsData.jsp");
      viewSignedFormsWindow.setResizable(true);
      viewSignedFormsWindow.setHeight(500);
      String dummyString = iwrb.getLocalizedString("golferpage.view_signing_table","Sko�a Skr�ningart�flu");
      Text text = new Text(dummyString);

     // Image linkImage = new Image(iwrb.getImage());
      Link viewFormButton = new Link( text, viewSignedFormsWindow);
//      viewFormButton.addParameter( golferParamterName, fullGolferName);
      dummyTable7.add(viewFormButton,1,1);
    }
    dummyTable7.setCellpadding(5);
    mainTable.add(dummyTable7,2,30);
    mainTable.add(hiddenInput,1,1);

    form.add(mainTable);
    add(form);
  }

  public void emptyForm(){
    //not implemented yet
  }

  private void setScript(Script script){
    this.script = script;
    setAssociatedScript(script);
  }

  private Script getScript(){

    if (getAssociatedScript() == null){
      setScript(new Script());
    }
    else{
      script = getAssociatedScript();
    }
    return script;
  }

  private void setCheckSubmit(){
    if ( getScript().getFunction("checkSubmit") == null){
      getScript().addFunction("checkSubmit","function checkSubmit(inputs){\n\n}");
    }
  }

  private void earlyWarningMessages(){
    form.setOnSubmit("return checkSubmit(this)");
    setCheckSubmit();
    getScript().addToFunction("checkSubmit","if (warnIfNotValid (inputs) == false ){\n return false;\n}\n");
    getScript().addFunction("warnIfNotValid",
         "function warnIfNotValid (inputs) {\n"
        +" isPaymentSet=((inputs."+anotherAmountInputName+".value!= '') || (inputs."+hiddenInputName+".value != 'false'));\n"
  //      +"   isCardPayment=(inputs."+creditCardNumberInputName+".value!= ''); "
        +"  if (isPaymentSet) {\n "
  /*      +"     if (isCardPayment){\n "*/
        +"      return true;\n "
  /*      +"    }\n"
        +"     else{\n"
        +"     alert ( ' Vinsamlegast veldu tegund grei�slu.' );\n "
        +"    return false;\n "
        +"    }\n "*/
        +"  }\n "
        +"  alert ( ' Vinsamlegast veldu grei�sluupph��.' );\n"
        +"   return false;\n"
        +" }\n");

      getScript().addFunction("toggleHandler",
        "function toggleHandler(checkbox){\n"
        +" \n"
        +"  if (checkbox.form."+billCheckBoxName+".checked) {\n"
        +"    checkbox.form."+billingNameSSNInputName+".disabled = false; \n"
        +"    checkbox.form."+billingNameInputName+".disabled = false; \n"
        +"    checkbox.form."+billingAdressAreaName+".disabled = false; \n"
        +"  } \n"
        +"  else {\n"
        +"    checkbox.form."+billingNameSSNInputName+".disabled = true; \n"
        +"    checkbox.form."+billingNameInputName+".disabled = true; \n"
        +"    checkbox.form."+billingAdressAreaName+".disabled = true; \n"
        +"  }\n"
        +"}\n");

      getScript().addFunction("autoFillHandler",
        "function autoFillHandler(checkbox){\n"
        +" \n"
        +"  if (checkbox.form."+billCheckBoxName+".checked) {\n"
        +"    if (checkbox.form."+billingNameInputName+".value == '')  {\n "
        +"    checkbox.form."+billingNameInputName+".value = checkbox.form."+nameInputName+".value;\n"
        +"    }\n"
        +"    if (checkbox.form."+billingNameSSNInputName+".value == '')  {checkbox.form."+billingNameSSNInputName+".value = checkbox.form."+sSNumberInputName+".value;}\n"
        +"    if (checkbox.form."+billingAdressAreaName+".value == '')  {checkbox.form."+billingAdressAreaName+".value = checkbox.form."+adressAreaName+".value;}\n"
        +"  } \n"
        +"}\n");

  }

  private String getPaymentAmount(IWContext iwc){
    String amount ="";

    if (iwc.isParameterSet(amountRadioButtonName)){
//      System.err.println("ARRRRRRG �ETTA � A� VERA J� " + iwc.isParameterSet(amountRadioButtonName)+"    og parameterinn er " +iwc.getParameter(amountRadioButtonName));
      if (iwc.getParameter(amountRadioButtonName).equalsIgnoreCase(amountButtonValue1)) amount = "1000";
      else if (iwc.getParameter(amountRadioButtonName).equalsIgnoreCase(amountButtonValue2)) amount = "2000";
      else if (iwc.getParameter(amountRadioButtonName).equalsIgnoreCase(amountButtonValue3)) amount = "3000";
      else if (iwc.getParameter(amountRadioButtonName).equalsIgnoreCase(amountButtonValue4)) amount = "4000";
      amount = amount+" or ";
    }
    else amount="";
    return amount+iwc.getParameter(anotherAmountInputName);
  }

  private String getPaymentDuration(IWContext iwc){
    String duration ="";

    if (iwc.isParameterSet(durationRadioButtonName)){
//      System.err.println("ARRRRRRG �ETTA � A� VERA J� " + iwc.isParameterSet(durationRadioButtonName)+"    og parameterinn er " +iwc.getParameter(durationRadioButtonName));
      if (iwc.getParameter(durationRadioButtonName).equalsIgnoreCase(durationButtonValue1)) duration = "1";
      else if (iwc.getParameter(durationRadioButtonName).equalsIgnoreCase(durationButtonValue2)) duration = "2";
      else if (iwc.getParameter(durationRadioButtonName).equalsIgnoreCase(durationButtonValue3)) duration = "3";
      else if (iwc.getParameter(durationRadioButtonName).equalsIgnoreCase(durationButtonValue4)) duration = "4";
      duration = duration+" months or ";
    }
    else duration="";
    return duration+iwc.getParameter(anotherDurationInputName);
  }

  private void viewChooser(IWContext iwc){
    String[] parameterValues;
    parameterValues = iwc.getParameterValues(submitButtonName);

    if (parameterValues.length == 1){
      //Changes possible here, hence. what window to get when submitted the form.
      if (parameterValues[0].equalsIgnoreCase(submitButtonValue)){
        String name;
        String sSNumber;
        String email;
        String adress;
        String cardType;
        String cardNumber;
        String cardExpDate;
        boolean nameAppearance;
        String paymentDuration;
        String paymentAmount;

        String billingName;
        String billingNameSSN;
        String appearName;
        String billingAdress;

        name = iwc.getParameter(nameInputName);
        sSNumber = iwc.getParameter(sSNumberInputName);
        adress = iwc.getParameter(adressAreaName);
        email = "";  //temporarily
        cardType = iwc.getParameter(cardTypeMenuName);
        cardNumber = iwc.getParameter(creditCardNumberInputName);
        cardExpDate = iwc.getParameter(creditCardExpDateInputName);
        nameAppearance = (iwc.getParameter(yesOrNoButtonName) == yesValue);
        paymentDuration = getPaymentDuration(iwc);
        paymentAmount = getPaymentAmount(iwc);
        billingName = iwc.getParameter(billingNameInputName);
        billingNameSSN = iwc.getParameter(billingNameSSNInputName);
        appearName = iwc.getParameter(appearNameInputName);
        billingAdress = iwc.getParameter(billingAdressAreaName);

        System.err.println("Vir�i billingAdress er "+billingAdress+" !!!!!!!!!!!!!");
        System.err.println("Vir�i name er "+name+" !!!!!!!!!!!!!");
        System.err.println("Vir�i sSNumber er "+sSNumber+" !!!!!!!!!!!!!");
        System.err.println("Vir�i adress er "+adress+" !!!!!!!!!!!!!");
        System.err.println("Vir�i cardType er "+cardType+" !!!!!!!!!!!!!");
        System.err.println("Vir�i cardNumber er "+cardNumber+" !!!!!!!!!!!!!");
        System.err.println("Vir�i cardExpDate er "+cardExpDate+" !!!!!!!!!!!!!");

        try {
          GolferFriendsDataBusiness.insertFriendsData(name, sSNumber, email, adress,
           cardType, cardNumber, cardExpDate, nameAppearance, paymentAmount,
           paymentDuration, billingAdress, billingName, billingNameSSN, appearName,
           (String) iwc.getSessionAttribute("fullGolferName"));
        }
        catch (Exception ex) {

        }

        Table table = new Table(1,1);
        table.setAlignment(1,1,"center");
        String dummyString = iwrb.getLocalizedString("golferpage.you_have_signed","�� hefur veri� skr��ur");
        table.add(" "+dummyString);
        add(table);
      }
    }
    else{
      //not submitted here
      emptyForm();
      drawSigningForm();
    }
  }

  public boolean isAdmin(){
    return isAdmin;
  }

  public void main(IWContext iwc) {

    try {
      isAdmin =  com.idega.jmodule.login.business.AccessControl.isAdmin(iwc);
    }
    catch(SQLException E) {    }

    iwrb = getResourceBundle(iwc);
    iwb = getBundle(iwc);

    viewChooser(iwc);
    earlyWarningMessages();

  }
}
