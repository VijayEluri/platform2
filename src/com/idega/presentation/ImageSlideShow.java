package com.idega.presentation;

import java.sql.SQLException;
import java.util.*;
import java.util.Iterator;
import com.idega.core.data.ICFile;
import com.idega.core.data.ICMimeType;
import com.idega.presentation.Block;
import com.idega.presentation.Image;
import com.idega.presentation.Table;
import com.idega.presentation.text.Link;
import com.idega.presentation.IWContext;
import com.idega.presentation.Script;
import com.idega.idegaweb.IWResourceBundle;
import com.idega.idegaweb.IWBundle;
import com.idega.block.media.business.MediaBusiness;
import com.idega.idegaweb.IWUserContext;
import com.idega.presentation.ui.SubmitButton;

public class ImageSlideShow extends Block {
private static final String IMAGE_SLIDE_SHOW = "image_slide_show";
public static int TOP = 1;
public static int BOTTOM = 2;
public static int OUTER = 4;
public static int INNER = 8;
private ICFile fileFolder;
private List listOfFiles = null;
private int width;
private int height;
private int buttonAlign = BOTTOM+OUTER;
private int fileId = -1;
private int delay = 0;
private String alt;
private boolean useJavascript = true;
private boolean showButtons = true;
private PresentationObject leftObject,rightObject;
  public void main(IWContext iwc) {
    IWBundle iwb = getBundle(iwc);
    if(leftObject==null)
      leftObject = iwb.getImage("arrowleft.gif");
    if(rightObject==null)
      rightObject = iwb.getImage("arrowright.gif");
    if(fileFolder == null && fileId > 1){
      try {
        fileFolder = ((com.idega.core.data.ICFileHome)com.idega.data.IDOLookup.getHomeLegacy(ICFile.class)).findByPrimaryKeyLegacy(fileId);
      }
      catch (Exception ex) {
      }
    }
    else if(listOfFiles !=null){
      fileFolder = (ICFile)listOfFiles.get(0);
    }
    if(fileFolder !=null){
    Table T = new Table(2,2);
    String name = "";
    Vector urls = new Vector();
    ICFile fileImage = null;
    int buttonRow = 2;
    int imageRow = 1;
    if((buttonAlign & TOP)!=0){
      imageRow = 2;
      buttonRow = 1;
    }
    // iterator init
    Iterator iter = null;
    int size = 1;
    int folderSize = fileFolder.getChildCount();
    if (  fileFolder.getChildCount() > 0 && fileFolder.getChildren() != null ) {
      iter = fileFolder.getChildren();
      size += folderSize;
    }
    else if(listOfFiles!=null){
      iter = listOfFiles.iterator();
      size = listOfFiles.size();
    }
    // iterator work

      Image image = new Image();
      name = "p_"+String.valueOf(fileFolder.getID());

      try{

      if(!MediaBusiness.isFolder(fileFolder))

        fileImage = fileFolder;

      else if(iter !=null && iter.hasNext()){

        fileImage = (ICFile)iter.next();

      }
      if(fileImage !=null){
        image = new Image(fileImage.getID());
        if ( image != null ) {
          if ( width > 0 )
            image.setWidth(width);
          if ( height > 0 )
            image.setHeight(height);
          if ( alt != null )
            image.setAlt(alt);
        }
        urls.add(MediaBusiness.getMediaURL(fileImage.getID(),iwc.getApplication()));
        image.setName(name);
        T.add(image,1,imageRow);
        }
      }
      catch(SQLException sql){
      }

    if(iter !=null && iter.hasNext()){
      while (iter.hasNext()) {
        fileImage = (ICFile)iter.next();
        try{
          String url = MediaBusiness.getMediaURL(fileImage.getID(),iwc.getApplication());
          if(!urls.contains(url))
            urls.add(url);
        }
        catch(Exception ex){
          ex.printStackTrace(System.err);
        }
      }

      // layout
        T.mergeCells(1,imageRow,2,imageRow);
        if( size >1 ){
          if(getParentPage()!=null)
            getParentPage().getAssociatedScript().addFunction("slide"+name,getSlideScript(name,urls));
          if(showButtons ){
            T.add(getLeftLink(name),1,buttonRow);
            T.add(getRightLink(name),2,buttonRow);
            //T.add(getLeftButton(name),1,buttonRow);
            //T.add(getRightButton(name),2,buttonRow);
            if((buttonAlign & INNER) != 0){
             T.setAlignment(1,buttonRow,"right");
             T.setAlignment(2,buttonRow,"left");
            }
            else if((buttonAlign & OUTER) != 0){
              T.setAlignment(1,buttonRow,"left");
              T.setAlignment(2,buttonRow,"right");
            }
          }
        }
        else
          T.mergeCells(1,2,2,2);
        T.setCellpadding(0);
        T.setCellspacing(0);
      }
      else
        T.mergeCells(1,2,2,2);

      add(T);
      if(delay > 0)
      add(getDelayScript(name));
    }
  }

  private Link getLeftLink(String imageName){
    return getLink(imageName,-1,leftObject);
  }

  private Link getRightLink(String imageName){
    return getLink(imageName,1,rightObject);
  }

  private SubmitButton getLeftButton(String imageName){
    return getButton(imageName,-1,(Image)leftObject);
  }

  private SubmitButton getRightButton(String imageName){
    return getButton(imageName,1,(Image)rightObject);
  }

  private SubmitButton getButton(String imageName,int step,Image object){
    SubmitButton button = new SubmitButton(object);
    button.setOnClick(getCallingScript(imageName,step));
    return button;
  }

  private Link getLink(String imageName,int step,PresentationObject object){
   Link link =new Link(object);
   link.setURL("javascript://");
   link.setOnClick(getCallingScript(imageName,step));
   return link;
  }

  public void setFileFolder(ICFile imagefile) {
    this.fileFolder = imagefile;
  }

  public void setFileId(int iImageFileId){
    this.fileId = iImageFileId;
  }

  public void setFiles(List listOfImageFiles){
    this.listOfFiles = listOfImageFiles;
  }

  public void setDelay(int seconds){
    if(seconds > 0)
      delay = 1000*seconds;
  }

  public void setWidth(String width) {
    this.width = Integer.parseInt(width);
  }

  public void setWidth(int width) {
    this.width = width;
  }

  public void setHeight(String height) {
    this.height = Integer.parseInt(height);
  }

   public void setHeight(int height) {
    this.width = height;
  }

  public void setAlt(String alt) {
    this.alt = alt;
  }

  public void setLeftImage(Image leftImage){
    this.leftObject = leftImage;
  }
  public void setRightImage(Image rightImage){
    this.rightObject = rightImage;
  }

  public void setShowButtons(boolean showButtons){
    this.showButtons = showButtons;
  }

  public void setButtonsInnerAlignment(boolean innerAlign){
    if(innerAlign)
      buttonAlign |= INNER;
    else
      setButtonsOuterAlignment(true);
  }

  public void setButtonsOuterAlignment(boolean outerAlign){
    if(outerAlign){
      buttonAlign |= OUTER;
    }
    else
      setButtonsInnerAlignment(true);
  }

  public void setButtonsTopAlignment(boolean topAlign){
    if(topAlign)
      buttonAlign |= TOP;
    else
      setButtonsBottomAlignment(true);
  }

  public void setButtonsBottomAlignment(boolean bottomAlign){
    if(bottomAlign)
      buttonAlign |= TOP;
    else
      setButtonsTopAlignment(true);
  }

  private String getCallingScript(String name,int step){
    return "Check"+name+"("+step+")";
  }

  private Script getDelayScript(String name){
    StringBuffer addPics = new StringBuffer();
    addPics.append("setInterval('").append(getCallingScript(name,1)).append("',").append(delay).append(");");
    Script s = new Script();
    s.addFunction(name+"load",addPics.toString());
    return s;
  }

  private String getSlideScript(String name,List urls){
    String sCurrent = "Current_"+name;
    String sPicArray = "Pics_"+name;
    String sAddPic = "addPic"+name;
    String sCheck = "Check"+name;
    StringBuffer addPics = new StringBuffer();
    //addPics.append("var ").append(sDelay).append(" = 0;\n");
    addPics.append("var ").append(sCurrent).append(" = ").append(delay).append(";\n");
    addPics.append("var ").append(sPicArray).append(" = new Array();\n");
    addPics.append("function ").append(sAddPic).append("(_p) {\n\t");
    addPics.append(sPicArray).append("[").append(sPicArray).append(".length?");
    addPics.append(sPicArray).append(".length:0] = new Image();\n\t");
    addPics.append(sPicArray).append("[").append(sPicArray).append(".length-1]=_p;\n}\n");
    if(urls !=null){
      for (int i = 0; i < urls.size(); i++) {
        String url = (String) urls.get(i);
        addPics.append(sAddPic).append("(\"").append(url).append("\");\n");
      }
    }
    addPics.append("\n");
    addPics.append("function ").append(sCheck).append("(val) {\n\t");
    addPics.append(sCurrent).append(" = Math.abs((").append(sCurrent).append("+parseInt(val))%");
    addPics.append(sPicArray).append(".length);\n\t");
    addPics.append("document.").append(name).append(".src = ");
    addPics.append(sPicArray).append("[").append(sCurrent).append("];\n");
    addPics.append("}\n");
    return addPics.toString();
  }
  public Object clone(IWUserContext iwc){
    ImageSlideShow obj = null;
    try {
      obj = (ImageSlideShow)super.clone();
    }
    catch(Exception ex){
    }
    return obj;
  }

}// class ImageRotater
