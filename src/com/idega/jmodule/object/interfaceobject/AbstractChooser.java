/*
 * $Id: AbstractChooser.java,v 1.5 2001/10/02 10:34:12 palli Exp $
 *
 * Copyright (C) 2001 Idega hf. All Rights Reserved.
 *
 * This software is the proprietary information of Idega hf.
 * Use is subject to license terms.
 *
 */
package com.idega.jmodule.object.interfaceobject;

import com.idega.jmodule.object.ModuleObjectContainer;
import com.idega.jmodule.object.ModuleInfo;
import com.idega.jmodule.object.ModuleObject;
import com.idega.jmodule.object.Table;
import com.idega.jmodule.object.textObject.Link;
import com.idega.jmodule.object.Image;
import com.idega.idegaweb.IWBundle;

/**
 * @author <a href="tryggvi@idega.is">Tryggvi Larusson</a>,<a href="palli@idega.is">Pall Helgason</a>
 * @version 1.0
 */
public abstract class AbstractChooser extends ModuleObjectContainer {
  static final String CHOOSER_SELECTION_PARAMETER = "iw_chooser_sel_par";
  static final String DISPLAYSTRING_PARAMETER = "chooser_displaystring";
  static final String VALUE_PARAMETER = "chooser_value";
  static final String DISPLAYSTRING_PARAMETER_NAME = "chooser_displaystr_n";
  static final String VALUE_PARAMETER_NAME = "chooser_value_n";
  static final String SCRIPT_PREFIX_PARAMETER = "iw_chooser_prefix";
  static final String SCRIPT_SUFFIX_PARAMETER = "iw_chooser_suffix";

  public String chooserParameter = VALUE_PARAMETER;
  public String displayInputName = DISPLAYSTRING_PARAMETER;
  private boolean _addForm = true;
  private Form _form = null;
  private Image _buttonImage = null;
  private String _style;

  /**
   *
   */
  public AbstractChooser() {
  }

  /**
   *
   */
  public abstract Class getChooserWindowClass();

  /**
   *
   */
  public String getChooserParameter() {
    return(chooserParameter);
  }

  /**
   *
   */
  public void setChooserParameter(String parameterName) {
    chooserParameter = parameterName;
    if (displayInputName == DISPLAYSTRING_PARAMETER) {
      displayInputName = parameterName + "_displaystring";
    }
  }

  /**
   *
   */
  public void setName(String name) {
    displayInputName = name;
    if (chooserParameter == VALUE_PARAMETER) {
      chooserParameter = name + "_chooser";
    }
  }

  /**
   *
   */
  public String getName() {
    return(displayInputName);
  }

  /**
   *
   */
  public void main(ModuleInfo modinfo){
    IWBundle bundle = getBundle(modinfo);
    if(_addForm){
      _form = new Form();
      _form.setWindowToOpen(getChooserWindowClass());
      add(_form);
      _form.add(getTable(modinfo,bundle));
    }
    else{
      add(getTable(modinfo,bundle));
      _form = getParentForm();
    }

  }

  /**
   *
   */
  public ModuleObject getTable(ModuleInfo modinfo,IWBundle bundle) {
    Table table = new Table(2,1);
    table.setCellpadding(0);
    table.setCellspacing(0);
    TextInput input = new TextInput(displayInputName);
    input.setDisabled(true);
    if (_style != null) {
      input.setAttribute("style",_style);
    }
    Parameter value = new Parameter(getChooserParameter(),"");
    table.add(value);
    table.add(new Parameter(VALUE_PARAMETER_NAME,value.getName()));
    //GenericButton button = new GenericButton("chooserbutton",bundle.getResourceBundle(modinfo).getLocalizedString(chooserText,"Choose"));
    if (_addForm) {
      SubmitButton button = new SubmitButton("Choose");
      table.add(button,2,1);
      _form.addParameter(CHOOSER_SELECTION_PARAMETER,getChooserParameter());
      _form.addParameter(SCRIPT_PREFIX_PARAMETER,"window.opener.document."+_form.getID()+".");
      _form.addParameter(SCRIPT_SUFFIX_PARAMETER,"value");
    }
    else {
      Link link;
      if (_buttonImage == null)
        link = new Link("Choose");
      else
        link = new Link(_buttonImage);

      link.setWindowToOpen(getChooserWindowClass());
      link.addParameter(CHOOSER_SELECTION_PARAMETER,getChooserParameter());
      //debug skiiiiiiiiiiiiiiiiiiiitamix getParentForm ekki a� virka??
      link.addParameter(SCRIPT_PREFIX_PARAMETER,"window.opener.document."+getParentFormString(this));
      link.addParameter(SCRIPT_SUFFIX_PARAMETER,"value");
      link.addParameter(DISPLAYSTRING_PARAMETER_NAME,input.getName());
      link.addParameter(VALUE_PARAMETER_NAME,value.getName());
      table.add(link,2,1);
    }

    table.add(input,1,1);
    table.add(new Parameter(DISPLAYSTRING_PARAMETER_NAME,input.getName()));
    return(table);
  }

  /*
   *
   */
  private String getParentFormString(ModuleObject obj) {
    String returnString = "";

    if (obj.getParentObject() != null) {
      Object newObj = obj.getParentObject();
      if (!(newObj instanceof Form)) {
        returnString = getParentFormString((ModuleObject)newObj);
      }
      else {
        returnString =  ((ModuleObject)newObj).getID()+".";
      }
    }

    return(returnString);
  }

  public void setInputStyle(String style) {
    _style = style;
  }

  public void addForm(boolean addForm){
    _addForm = addForm;
  }

  public void setChooseButtonImage(Image buttonImage){
    _buttonImage = buttonImage;
  }
}