package is.idega.idegaweb.travel.presentation;

import com.idega.presentation.IWContext;
import com.idega.block.login.presentation.Login;
import com.idega.presentation.text.*;
import com.idega.presentation.ui.*;
import com.idega.presentation.*;
import com.idega.idegaweb.IWBundle;
import com.idega.idegaweb.IWResourceBundle;
import com.idega.block.login.business.LoginBusiness;
import com.idega.development.presentation.Localizer;
/**
 * Title:        idegaWeb TravelBooking
 * Description:
 * Copyright:    Copyright (c) 2001
 * Company:      idega
 * @author <a href="mailto:gimmi@idega.is">Grimur Jonsson</a>
 * @version 1.0
 */

public class LoginPage extends TravelManager {

  private IWBundle bundle = null;
  private IWResourceBundle iwrb = null;

  private static String GRAY = "#CCCCCC";

  public LoginPage() {
  }

  public void main(IWContext iwc) throws Exception{
    super.main(iwc);
    bundle = super.getBundle();
    iwrb = super.getResourceBundle();

    insertLogin(iwc);
  }

  private void insertLogin(IWContext iwc) {
    add(Text.NON_BREAKING_SPACE);
    add(getLoginTable(iwc, bundle, iwrb));
  }

  protected static Login getLoginObject(IWContext iwc, IWResourceBundle iwrb) {

    Login login = new Login();
      login.setUserTextColor(TravelManager.BLACK);
      login.setPasswordTextColor(TravelManager.BLACK);
      login.setTextStyle("font-face: Verdana, Helvetica, sans-serif; font-size: "+Text.FONT_SIZE_7_STYLE_TAG+"; color: #000000");
      if (iwrb != null) {
        login.setPasswordText(iwrb.getLocalizedString("travel.password","Password "));
        login.setUserText(iwrb.getLocalizedString("travel.username","Username "));
      }
      login.setLoginButton(iwrb.getImage("images/go.gif", 38, 38));
//      login.setVertical();
      login.setStacked();
    return login;
  }

  protected Table getLoginTable(IWContext iwc, IWBundle bundle, IWResourceBundle iwrb) {
    Table table = new Table(3,4);
      table.setWidth(543);
      table.setCellpaddingAndCellspacing(0);
      table.setColor(super.WHITE);
      table.mergeCells(1,1,3,1);
      table.mergeCells(1,4,3,4);

      Image banner = iwrb.getImage("images/travel_banner.gif",543, 212);
      Image welcome = iwrb.getImage("images/welcome.gif",215, 75);
      Image language = iwrb.getImage("images/language.gif",154, 75);
      Image login = iwrb.getImage("images/login.gif",174, 75);
      Image disclaimer = iwrb.getImage("images/disclaimer.gif",543, 18);

      table.add(banner, 1, 1);
      table.add(welcome, 1, 2);
      table.add(language, 2, 2);
      table.add(login, 3, 2);

      Text rightContent = (Text) theText.clone();
        rightContent.setFontColor("#ABABAB");
        rightContent.setText(iwrb.getLocalizedString("travel.usage_rules_1","Kerfi �etta er einungi heimila� a�ilum innan fer�a�j�nustu."));
        String string = iwrb.getLocalizedString("travel.usage_rules_2","Misnotkun var�ar l�g blablabla");
        if (string.length() > 1) {
        rightContent.addToText(string);
        rightContent.addBreak();
        rightContent.addBreak();
        }
        string = iwrb.getLocalizedString("travel.usage_rules_3","h�rna vantar eitthva� dj�si stuff til a� skrifa og hafa t�ff. Kannski �g f�i einhvern annan � �a� :)");
        if (string.length() > 1) {
        rightContent.addToText(string);
        rightContent.addBreak();
        rightContent.addBreak();
        }
        string = iwrb.getLocalizedString("travel.usage_rules_4","Hafir �� gleymt notandanafni og/e�a lykilor�i haf�u �� samband vi� fsdfjli");
        if (string.length() > 1) {
        rightContent.addToText(string);
        }

      Table leftTable = new Table(3, 1);
        leftTable.setCellpaddingAndCellspacing(0);
        leftTable.setWidth(1,1, "20");
        leftTable.setWidth(3,1, "20");
      Table centerTable = new Table(3, 1);
        centerTable.setCellpaddingAndCellspacing(0);
        centerTable.setWidth(1,1, "20");
        centerTable.setWidth(3,1, "20");
      Table rightTable = new Table(3, 1);
        rightTable.setCellpaddingAndCellspacing(0);
        rightTable.setWidth(1,1, "20");
        rightTable.setWidth(3,1, "20");

        leftTable.add(rightContent,2,1);
        centerTable.add(getLocaleSwitcherForm(iwc),2,1);
        rightTable.add(getLoginObject(iwc, iwrb),2,1);
      table.add(leftTable, 1, 3);
      table.add(centerTable, 2, 3);
      table.add(rightTable, 3, 3);

      table.setVerticalAlignment(1,3, "top");
      table.setVerticalAlignment(2,3, "top");
      table.setVerticalAlignment(3,3, "top");

      table.add(disclaimer, 1, 4);
    return table;
  }

  protected Table getLoginTableOld(IWContext iwc, IWBundle bundle, IWResourceBundle iwrb) {
    Table bigTable = new Table(1,1);
        bigTable.setCellspacing(40);
        bigTable.setCellpadding(0);
      Table table = new Table(1,1);
        bigTable.add(table,1,1);
        table.setHeight(210);
        table.setWidth(769);
        table.setColor(TravelManager.WHITE);
        table.setCellspacing(15);
        table.setCellpadding(0);

      Table innerTable = new Table(3,2);
        table.add(innerTable,1,1);
        innerTable.setWidth("100%");
        innerTable.setHeight("100%");
        innerTable.setCellpadding(1);
        innerTable.setCellspacing(1);
        innerTable.setBorder(0);


        innerTable.setWidth(2,1,"177");


        Image imageBanner = bundle.getImage("buttons/login_bannermynd.gif");
          imageBanner.setWidth(337);
          imageBanner.setHeight(28);

        innerTable.add(imageBanner,1,1);
        innerTable.setColor(2,1,GRAY);
//        innerTable.mergeCells(2,1,3,1);

        Table fexTable = new Table(2,1);
          fexTable.setCellpadding(0);
          fexTable.setCellspacing(0);
          fexTable.setWidth(1,1, "179");
          fexTable.setAlignment(1,1,"center");
          fexTable.setWidth("300");
          fexTable.setBorder(0);

          Text leText = (Text) theBoldText.clone();
            leText.setText(iwrb.getLocalizedString("travel.please_select_language","Please select language"));
            leText.setFontColor(super.BLACK);

          fexTable.add(leText);
          fexTable.add(getLocaleSwitcherForm(iwc),2,1);

//        innerTable.add(getLocaleSwitcherForm(iwc),3,1);
        innerTable.mergeCells(2,1,3,1);
        innerTable.add(fexTable,2,1);


        Image logo = bundle.getImage("buttons/login_mynd.jpg");
          logo.setWidth(337);
          logo.setHeight(180);
        innerTable.add(logo,1,2);
        innerTable.setWidth(1,2,"337");
//        innerTable.setHeight(1,1,"180");

        Text middleHeader = (Text) theBigBoldText.clone();
          middleHeader.setText(iwrb.getLocalizedString("travel.welcome","Welcome"));
          middleHeader.setFontColor(super.BLACK);
        Text middleContent = (Text) theText.clone();
          middleContent.setText(iwrb.getLocalizedString("travel.please_enter_your_username_and_password","Please enter your username and password"));
          middleContent.setFontColor(super.BLACK);

        Table middleTextTable = new Table(1,2);
          middleTextTable.setWidth("80%");
          middleTextTable.add(middleHeader,1,1);
          middleTextTable.add(middleContent,1,2);

        if (!LoginBusiness.isLoggedOn(iwc))
        innerTable.add(middleTextTable,2,2);
        innerTable.add(getLoginObject(iwc, iwrb),2,2);
        innerTable.setWidth(2,2,"177");
        innerTable.setColor(2,2,GRAY);
        innerTable.setHeight(2,2,"180");
        innerTable.setAlignment(2,2,"center");


        Text rightHeader = (Text) super.theBigBoldText.clone();
          rightHeader.setText(iwrb.getLocalizedString("travel.usage_rules_header","Usage rules"));
          rightHeader.setFontColor(super.BLACK);
        Text rightContent = (Text) theText.clone();
          rightContent.setFontColor(super.BLACK);
          rightContent.setText(iwrb.getLocalizedString("travel.usage_rules_1","Kerfi �etta er einungi heimila� a�ilum innan fer�a�j�nustu."));
          rightContent.addBreak();
          rightContent.addBreak();
          String string = iwrb.getLocalizedString("travel.usage_rules_2","Misnotkun var�ar l�g blablabla");
          if (string.length() > 1) {
          rightContent.addToText(string);
          rightContent.addBreak();
          rightContent.addBreak();
          }
          string = iwrb.getLocalizedString("travel.usage_rules_3","h�rna vantar eitthva� dj�si stuff til a� skrifa og hafa t�ff. Kannski �g f�i einhvern annan � �a� :)");
          if (string.length() > 1) {
          rightContent.addToText(string);
          rightContent.addBreak();
          rightContent.addBreak();
          }
          string = iwrb.getLocalizedString("travel.usage_rules_4","Hafir �� gleymt notandanafni og/e�a lykilor�i haf�u �� samband vi� fsdfjli");
          if (string.length() > 1) {
          rightContent.addToText(string);
          }

        Table rightTextTable = new Table(1,2);
          rightTextTable.setWidth("100%");
          rightTextTable.setHeight("100%");
          rightTextTable.add(rightHeader,1,1);
          rightTextTable.add(rightContent,1,2);
          rightTextTable.setColor(TravelManager.WHITE);

        innerTable.add(rightTextTable,3,2);
        innerTable.setColor(3,2,GRAY);

    return bigTable;
  }

  private static Form getLocaleSwitcherForm(IWContext iwc) {
      Form myForm = new Form();
      Table table = new Table(1,1);
        table.setCellspacing(0);
        table.setCellpadding(0);
        table.setBorder(0);

        myForm.setEventListener(com.idega.core.localisation.business.LocaleSwitcher.class.getName());
      DropdownMenu dropdown = Localizer.getAvailableLocalesDropdown(iwc);
        dropdown.setAttribute("style","font-family: Verdana; font-size: 8pt; border: 1 solid #000000");
        dropdown.setSelectedElement(iwc.getCurrentLocale().toString());
        table.add(dropdown);

        myForm.add(table);
      return myForm;
  }

}
