/*
 * $Id: IBPageChooserWindow.java,v 1.13 2002/09/24 08:51:42 laddi Exp $
 *
 * Copyright (C) 2001 Idega hf. All Rights Reserved.
 *
 * This software is the proprietary information of Idega hf.
 * Use is subject to license terms.
 *
 */
package com.idega.builder.presentation;

import com.idega.idegaweb.*;
import com.idega.presentation.text.*;
import com.idega.presentation.Script;
import com.idega.presentation.Table;
import com.idega.presentation.ui.AbstractChooserWindow;
import com.idega.presentation.ui.SubmitButton;
import com.idega.presentation.ui.TextInput;
import com.idega.presentation.IWContext;
import com.idega.presentation.ui.TreeViewer;
import com.idega.builder.data.IBDomain;
import com.idega.builder.business.PageTreeNode;
import com.idega.builder.business.BuilderLogic;

/**
 * @author <a href="tryggvi@idega.is">Tryggvi Larusson</a>
 * @modified by <a href=eiki@idega.is">Eirikur Hrafnsson</a>
 * @version 1.0
 */
public class IBPageChooserWindow extends AbstractChooserWindow {

	private boolean fromEditor = false;
	private static final int _width = 280;
	private static final int _height = 400;
	private static final String _linkStyle = "font-family:Arial,Helvetica,sans-serif;font-size:8pt;color:#000000;text-decoration:none;";

	/**
	 *
	 */
	public IBPageChooserWindow() {
		setTitle("Page chooser");
		setWidth(_width);
		setHeight(_height);
		setCellpadding(5);
		setScrollbar(true);
		this.getLocation().setApplicationClass(this.getClass());
		this.getLocation().isInPopUpWindow(true);
	}

	/**
	 *
	 */
	public void displaySelection(IWContext iwc) {
		if (iwc.isParameterSet("from_editor")) {
			addScript();
			fromEditor = true;
		}
		
		IWResourceBundle iwrb = iwc.getApplication().getBundle(BuilderLogic.IW_BUNDLE_IDENTIFIER).getResourceBundle(iwc);
		addTitle(iwrb.getLocalizedString("select_page", "Select page"), IWConstants.BUILDER_FONT_STYLE_TITLE);
		setStyles();

		if ( fromEditor ) {
			Table table = new Table(2,3);
			table.setCellpadding(1);
			table.setCellspacing(0);
			TextInput URL = new TextInput("URL");
			URL.setLength(27);
			URL.setStyleAttribute(IWConstants.BUILDER_FONT_STYLE_INTERFACE);
			TextInput target = new TextInput("TARGET");
			target.setLength(7);
			target.setStyleAttribute(IWConstants.BUILDER_FONT_STYLE_INTERFACE);
			SubmitButton save = new SubmitButton(iwrb.getLocalizedString("save", "Save"));
			save.setAsImageButton(true);
			save.setToEncloseByForm(false);
			save.setOnClick("javascript:save2();");
			
			Text URLText = new Text(iwrb.getLocalizedString("url", "URL") + ":");
			URLText.setFontStyle(IWConstants.BUILDER_FONT_STYLE_LARGE);
			Text targetText = new Text(iwrb.getLocalizedString("target", "Target") + ":");
			targetText.setFontStyle(IWConstants.BUILDER_FONT_STYLE_LARGE);

			table.add(URLText,1,1);
			table.add(URL,2,1);
			table.add(targetText,1,2);
			table.add(target,2,2);
			table.add(save,1,3);
			add(table);
			add(new Break());
		}
		
		Text text = new Text(iwrb.getLocalizedString("select_page", "Select page") + ":");
		text.setFontStyle(IWConstants.BUILDER_FONT_STYLE_LARGE);
		add(text);

		try {
			TreeViewer viewer = com.idega.builder.business.IBPageHelper.getInstance().getPageTreeViewer(iwc);
			viewer.setLocation((IWLocation) this.getLocation().clone());
			viewer.getLocation().setSubID(1);

			add(viewer);

			viewer.setToMaintainParameter(SCRIPT_PREFIX_PARAMETER, iwc);
			viewer.setToMaintainParameter(SCRIPT_SUFFIX_PARAMETER, iwc);
			viewer.setToMaintainParameter(DISPLAYSTRING_PARAMETER_NAME, iwc);
			viewer.setToMaintainParameter(VALUE_PARAMETER_NAME, iwc);
			viewer.setDefaultOpenLevel(999);
			if (fromEditor) {
				viewer.setToMaintainParameter("from_editor", iwc);
			}

			Link link = new Link();
			link.setURL("#");
			link.setNoTextObject(true);
			viewer.setLinkPrototype(link);
			viewer.setTreeStyle(_linkStyle);
			viewer.setToUseOnClick();
			//sets the hidden input and textinput of the choosing page
			viewer.setOnClick(SELECT_FUNCTION_NAME + "(" + viewer.ONCLICK_DEFAULT_NODE_NAME_PARAMETER_NAME + "," + viewer.ONCLICK_DEFAULT_NODE_ID_PARAMETER_NAME + ")");
		}
		catch (Exception e) {
			e.printStackTrace();
		}
	}

	private void setStyles() {
		String _linkStyle = "font-family:Arial,Helvetica,sans-serif;font-size:8pt;color:#000000;text-decoration:none;";
		String _linkHoverStyle = "font-family:Arial,Helvetica,sans-serif;font-size:8pt;color:#FF8008;text-decoration:none;";
		if (getParentPage() != null) {
			getParentPage().setStyleDefinition("A", _linkStyle);
			//getParentPage().setStyleDefinition("A."+STYLE_NAME+":visited",_linkStyle);
			//getParentPage().setStyleDefinition("A."+STYLE_NAME+":active",_linkStyle);
			getParentPage().setStyleDefinition("A:hover", _linkHoverStyle);
		}
	}

	private void addScript() {
		Script script = getParentPage().getAssociatedScript();
		script.addVariable("args","window.dialogArguments");
		script.addFunction("save2", "function save2() {   args['href']=URL.value; args['target']=TARGET.value; window.returnValue = args; window.close(); }");
		script.addFunction("initialize", "function initialize() {\n var args = window.dialogArguments;\n foo = args[\"href\"];\n URL.value=(foo == undefined) ? \"\" : foo;\n foo = args[\"target\"];\n TARGET.value=(foo == undefined) ? \"\" : foo;\n }\n");
		getParentPage().setAssociatedScript(script);
		getParentPage().setOnLoad("initialize()");
	}

}
