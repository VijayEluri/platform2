package com.idega.jmodule.image.presentation;

/**
 * Title: ImageInserter
 * Description:
 * Copyright:    Copyright (c) 2001
 * Company: idega
 * @author Eirikur Hrafnsson, eiki@idega.is
 * @version 1.0
 *
 */


import java.sql.*;
import java.util.*;
import java.io.*;
import com.idega.util.*;
import com.idega.jmodule.object.textObject.*;
import com.idega.jmodule.object.*;
import com.idega.jmodule.object.interfaceobject.*;
import com.idega.jmodule.image.data.*;
import com.idega.jmodule.image.business.*;
import com.idega.idegaweb.IWResourceBundle;
import com.idega.idegaweb.IWBundle;
import com.idega.idegaweb.IWMainApplication;

public class ImageInserter extends JModuleObject{

private final static String IW_BUNDLE_IDENTIFIER="com.idega.block.image";
private int imageId = -1;
private String imSessionImageName =null;
private String sHiddenInputName = null;
//private String adminURL = "/image/insertimage.jsp";
private String adminURL = null;
private String nameOfWindow;
private String sUseBoxString;
private int maxImageWidth = 140;
private boolean hasUseBox = true;
private boolean selected = false;
private boolean openInWindow = false;
private Class windowClass = SimpleChooserWindow.class;
private Image setImage;

private IWBundle iwb;
private IWResourceBundle iwrb;

public ImageInserter(){
  this.imSessionImageName="image_id";
  this.sHiddenInputName = "image_id";
}

public ImageInserter(Image setImage){
  this();
  this.setImage=setImage;
}

public ImageInserter(int imageId) {
  this.imageId=imageId;
  this.imSessionImageName="image_id";
  this.sHiddenInputName = "image_id";
}

public ImageInserter(String imSessionImageName) {
  this.imSessionImageName=imSessionImageName;
  this.sHiddenInputName = imSessionImageName;
}

public ImageInserter(String imSessionImageName, boolean hasUseBox) {
  this(imSessionImageName);
  setHasUseBox(hasUseBox);
}

public ImageInserter(int imageId, String imSessionImageName) {
  this.imageId=imageId;
  this.imSessionImageName=imSessionImageName;
  this.sHiddenInputName = imSessionImageName;
}

public ImageInserter(Class WindowToOpen) {
  this.imSessionImageName=imSessionImageName;
  this.sHiddenInputName = imSessionImageName;
  windowClass = WindowToOpen;
  openInWindow = true;

}

  public void main(ModuleInfo modinfo)throws Exception{
      this.empty();

      iwb = getBundle(modinfo);
      iwrb = getResourceBundle(modinfo);

      nameOfWindow = iwrb.getLocalizedString("new_image","New image");
      sUseBoxString = iwrb.getLocalizedString("use_image","Use image");

      String imageSessionId = (String) modinfo.getSession().getAttribute(imSessionImageName);


      if ( imageSessionId != null ) {
        imageId = Integer.parseInt(imageSessionId);
        modinfo.removeSessionAttribute(imSessionImageName);
      }

      Image image=setImage;
        if(image==null){
          if ( imageId == -1 ) {
            image = iwrb.getImage("picture.gif",iwrb.getLocalizedString("new_image","New image"),138,90);
          }
          else {
            image = new Image(imageId);
          }
          image.setMaxImageWidth(this.maxImageWidth);
          image.setNoImageLink();
        }

      Link imageAdmin = null;
      if(adminURL == null){
        imageAdmin = new Link(image);
        imageAdmin.setWindowToOpen(windowClass);
      }
      else{
        Window insertNewsImageWindow = new Window(nameOfWindow,ImageBusiness.IM_BROWSER_WIDTH,ImageBusiness.IM_BROWSER_HEIGHT,adminURL);
        imageAdmin = new Link(image,insertNewsImageWindow);
      }
      imageAdmin.addParameter("submit","new");
      imageAdmin.addParameter("im_image_session_name",imSessionImageName);
      if ( imageId != -1 )
        imageAdmin.addParameter(imSessionImageName,imageId);

      HiddenInput hidden = new HiddenInput(sHiddenInputName,imageId+"");
      CheckBox insertImage = new CheckBox("insertImage","Y");
        insertImage.setChecked(selected);

      Text imageText = new Text(sUseBoxString+":&nbsp;");
        imageText.setFontSize(1);

      Table borderTable = new Table(1,1);
        borderTable.setWidth("100%");
        borderTable.setCellspacing(1);
        borderTable.setCellpadding(0);
        borderTable.setColor("#000000");
        borderTable.add(imageAdmin);

      Table imageTable = new Table(1,2);
        imageTable.setAlignment(1,2,"right");
        imageTable.add(borderTable,1,1);
        if(hasUseBox){
          imageTable.add(imageText,1,2);
          imageTable.add(insertImage,1,2);
        }
        imageTable.add(hidden,1,2);

      add(imageTable);
  }

  public void setHasUseBox(boolean useBox){
    this.hasUseBox = useBox;
  }

  public void setSelected(boolean selected){
    this.selected = selected;
  }

  public void setUseBoxString(String sUseBoxString){
    this.sUseBoxString = sUseBoxString;
  }

  public void setHiddenInputName(String name){
    sHiddenInputName = name;
  }

  public String getHiddenInputName(){
    return sHiddenInputName;
  }

  public void setMaxImageWidth(int maxWidth){
    this.maxImageWidth = maxWidth;
  }

  public void setAdminURL(String adminURL) {
    this.adminURL=adminURL;
  }

  public void setImageId(int imageId) {
    this.imageId=imageId;
  }

  public void setImSessionImageName(String imSessionImageName) {
    this.imSessionImageName=imSessionImageName;
    this.sHiddenInputName=imSessionImageName;
  }

  public String getImSessionImageName() {
    return this.imSessionImageName;
  }

  public void setWindowClassToOpen(Class WindowClass){
    windowClass = WindowClass;
    openInWindow = true;
  }

  public String getBundleIdentifier(){
    return IW_BUNDLE_IDENTIFIER;
  }
}
