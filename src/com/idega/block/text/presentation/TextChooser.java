package com.idega.block.text.presentation;

import com.idega.presentation.IWContext;
import com.idega.core.data.ICFile;
import com.idega.presentation.ui.AbstractChooser;
import com.idega.idegaweb.IWBundle;
import com.idega.builder.business.BuilderLogic;
import com.idega.block.text.data.*;
import com.idega.presentation.Image;
/**
 * Title: com.idega.block.text.presentation.TextChooser
 * Description: The chooser object for localized text
 * Copyright:    Copyright (c) 2001
 * Company:      idega software
 * @author Eirikur S. Hrafnsson eiki@idega.is
 * @version 1.0
 */

public class TextChooser extends AbstractChooser {
  private String style;
  private Image _chooseButtonImage;
  public static String RELOAD_PARENT_PARAMETER = "tx_no_reload";


  public TextChooser(String chooserName) {
    addForm(false);
    addTextInput(false);
    setChooserParameter(chooserName);
    super.setParameterValue("a","b");
  }

  public TextChooser(String chooserName,String style) {
    this(chooserName);
    setInputStyle(style);
  }

  public Class getChooserWindowClass() {
    return TextEditorWindow.class;
  }

  public void main(IWContext iwc){
    IWBundle iwb = iwc.getApplication().getBundle(BuilderLogic.IW_BUNDLE_IDENTIFIER);
    if (_chooseButtonImage != null) {
      setChooseButtonImage(_chooseButtonImage);
    }else {
      setChooseButtonImage(iwb.getImage("open.gif","Choose file"));
    }
    iwc.setSessionAttribute(RELOAD_PARENT_PARAMETER, "true");
    if( getChooserValue()!= null ){
      super.setParameterValue(getChooserParameter(), getChooserValue());
//      iwc.setSessionAttribute(MediaBusiness.getMediaParameterNameInSession(iwc),getChooserValue());
    }
  }

  public void setSelectedText(TxText text){
    super.setChooserValue("",text.getID());
  }

  public void setValue(Object text){
    setSelectedText((TxText)text);
  }

  public void setChooseImage(Image image) {
    _chooseButtonImage = image;
  }

}
