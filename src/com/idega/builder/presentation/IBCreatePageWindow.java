/*
 * $Id: IBCreatePageWindow.java,v 1.36 2002/10/10 13:37:10 laddi Exp $
 *
 * Copyright (C) 2001 Idega hf. All Rights Reserved.
 *
 * This software is the proprietary information of Idega hf.
 * Use is subject to license terms.
 *
 */
package com.idega.builder.presentation;

import com.idega.idegaweb.IWConstants;
import com.idega.builder.business.IBPropertyHandler;
import com.idega.builder.business.IBXMLPage;
import com.idega.builder.business.BuilderLogic;
import com.idega.builder.business.PageTreeNode;
import com.idega.builder.business.IBPageHelper;
import com.idega.builder.data.IBPage;
import com.idega.builder.data.IBDomain;
import com.idega.presentation.IWContext;
import com.idega.presentation.PresentationObject;
import com.idega.presentation.Table;
import com.idega.presentation.Page;
import com.idega.presentation.text.Text;
import com.idega.presentation.ui.Form;
import com.idega.presentation.ui.TextInput;
import com.idega.presentation.ui.SubmitButton;
import com.idega.presentation.ui.RadioGroup;
import com.idega.presentation.ui.DropdownMenu;
import com.idega.presentation.ui.Window;
import com.idega.presentation.ui.CheckBox;
import com.idega.idegaweb.IWResourceBundle;
import com.idega.idegaweb.presentation.IWAdminWindow;
import java.util.List;
import java.util.Iterator;
import java.util.Map;

/**
 * @author <a href="mailto:palli@idega.is">Pall Helgason</a>,<a href="mailto:tryggvi@idega.is">Tryggvi Larusson</a>
 * @version 1.0
*/
public class IBCreatePageWindow extends IBPageWindow {


  private static final String TOP_LEVEL = "top_level";

  public IBCreatePageWindow() {
    super();
  }

  public void main(IWContext iwc) throws Exception {
    IWResourceBundle iwrb = getResourceBundle(iwc);
    Form form = new Form();
    String type = iwc.getParameter(PAGE_TYPE);
    String topLevelString = iwc.getParameter(TOP_LEVEL);

    if (type == null) {
      String currPageType = BuilderLogic.getInstance().getCurrentIBXMLPage(iwc).getType();
      if (currPageType.equals(IBXMLPage.TYPE_TEMPLATE))
      	type = IBPageHelper.TEMPLATE;
      else
      	type = IBPageHelper.PAGE;
    }

    if (type.equals(IBPageHelper.TEMPLATE)) {
      setTitle(iwrb.getLocalizedString("create_new_template","Create a new Template"));
      addTitle(iwrb.getLocalizedString("create_new_template","Create a new Template"),IWConstants.BUILDER_FONT_STYLE_TITLE);
    }
    else {
      setTitle(iwrb.getLocalizedString("create_new_page","Create a new Page"));
      addTitle(iwrb.getLocalizedString("create_new_page","Create a new Page"),IWConstants.BUILDER_FONT_STYLE_TITLE);
    }

    add(form);
    Table tab = new Table(2,6);
    tab.setColumnAlignment(1,"right");
    tab.setWidth(1,"110");
    tab.setCellspacing(3);
    tab.setAlignment(2,6,"right");
    form.add(tab);
    TextInput inputName = new TextInput(PAGE_NAME_PARAMETER);
    inputName.setStyleAttribute(IWConstants.BUILDER_FONT_STYLE_INTERFACE);
    inputName.setAsNotEmpty(iwrb.getLocalizedString("must_supply_name", "Must supply a name"));
    Text inputText = new Text();
    if (type.equals(IBPageHelper.TEMPLATE)) {
      inputText.setText(iwrb.getLocalizedString("template_name","Template name")+":");
    }
    else {
      inputText.setText(iwrb.getLocalizedString("page_name","Page name")+":");
    }
    inputText.setFontStyle(IWConstants.BUILDER_FONT_STYLE_LARGE);
    tab.add(inputText,1,1);
    tab.add(inputName,2,1);

    DropdownMenu mnu = new DropdownMenu(PAGE_TYPE);
    mnu.addMenuElement(IBPageHelper.PAGE,"Page");
    mnu.addMenuElement(IBPageHelper.TEMPLATE,"Template");
    mnu.setSelectedElement(type);
    mnu.setStyleAttribute(IWConstants.BUILDER_FONT_STYLE_INTERFACE);

    Text typeText = new Text(iwrb.getLocalizedString("select_type","Select type")+":");
    typeText.setFontStyle(IWConstants.BUILDER_FONT_STYLE_LARGE);
    tab.add(typeText,1,2);
    tab.add(mnu,2,2);

    mnu.setToSubmit();

    CheckBox topLevel = new CheckBox(TOP_LEVEL);
    Text topLevelText = new Text(iwrb.getLocalizedString(TOP_LEVEL,"Top level")+":");
    topLevelText.setFontStyle(IWConstants.BUILDER_FONT_STYLE_LARGE);
    tab.add(topLevelText,1,3);
    tab.add(topLevel,2,3);
    topLevel.setOnClick("this.form.submit()");

    IBPageChooser pageChooser = getPageChooser(PAGE_CHOOSER_NAME,iwc);

    if (!type.equals(IBPageHelper.TEMPLATE)) {
      if (topLevelString == null) {
        Text createUnderText = new Text(iwrb.getLocalizedString("parent_page","Create page under")+":");
	      createUnderText.setFontStyle(IWConstants.BUILDER_FONT_STYLE_LARGE);
        tab.add(createUnderText,1,4);
        tab.add(pageChooser,2,4);
      }
    }

    if ((topLevelString == null) || (topLevelString != null && type.equals(IBPageHelper.PAGE))) {
      Text usingText = new Text(iwrb.getLocalizedString("using_template","Using template")+":");
      usingText.setFontStyle(IWConstants.BUILDER_FONT_STYLE_LARGE);
      tab.add(usingText,1,5);
      IBTemplateChooser templateChooser = getTemplateChooser(TEMPLATE_CHOOSER_NAME,iwc,type);
      tab.add(templateChooser,2,5);
    }

    SubmitButton button = new SubmitButton(iwrb.getLocalizedImageButton("save","Save"),"submit");
    SubmitButton close = new SubmitButton(iwrb.getLocalizedImageButton("close","Close"),"close");
    tab.add(close,2,6);
    tab.add(Text.getNonBrakingSpace(),2,6);
    tab.add(button,2,6);

    boolean submit = iwc.isParameterSet("submit");
    boolean quit = iwc.isParameterSet("close");

    if (submit) {
      String parentPageId = iwc.getParameter(PAGE_CHOOSER_NAME);
      String name = iwc.getParameter(PAGE_NAME_PARAMETER);
      type = iwc.getParameter(PAGE_TYPE);
      String templateId = iwc.getParameter(TEMPLATE_CHOOSER_NAME);
      if (type.equals(IBPageHelper.TEMPLATE))
      	parentPageId = templateId;
//      String topLevelString = iwc.getParameter(TOP_LEVEL);

      Map tree = PageTreeNode.getTree(iwc);
      int id = -1;
      if (topLevelString == null) {
        if (parentPageId != null) {
          id = IBPageHelper.getInstance().createNewPage(parentPageId,name,type,templateId,tree,iwc);
        }
      }
      else {
        int domainId = BuilderLogic.getInstance().getCurrentDomain(iwc).getID();
        if (type.equals(IBPageHelper.TEMPLATE)) {
          id = IBPageHelper.getInstance().createNewPage(null,name,type,null,tree,iwc,null,domainId);
        }
        else {
          id = IBPageHelper.getInstance().createNewPage(null,name,type,templateId,tree,iwc,null,domainId);
        }
      }

      iwc.setSessionAttribute("ib_page_id",Integer.toString(id));
      setOnUnLoad("window.opener.parent.parent.location.reload()");
    }
    else if (quit) {
      close();
    }
    else {
      String name = iwc.getParameter(PAGE_NAME_PARAMETER);
      type = iwc.getParameter(PAGE_TYPE);
      String templateId = iwc.getParameter(TEMPLATE_CHOOSER_NAME);
      String templateName = iwc.getParameter(TEMPLATE_CHOOSER_NAME+"_displaystring");

      if (topLevelString != null)
        topLevel.setChecked(true);

      if (name != null)
      	inputName.setValue(name);

      if (type != null)
      	mnu.setSelectedElement(type);
    }
  }


}