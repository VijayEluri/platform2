package com.idega.jmodule.image.presentation;

/**
 * Title: ImageViewer
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
import com.idega.util.text.*;
import com.idega.data.GenericEntity;
import com.oreilly.servlet.MultipartRequest;


public class ImageViewer extends JModuleObject{

private int categoryId = -1;
private boolean limitNumberOfImages=true;
private int numberOfDisplayedImages=9;
private int iNumberInRow = 3; //iXXXX for int
private int ifirst = 0;
private int maxImageWidth =100;
private boolean limitImageWidth=true;
private String callingModule = "";

private boolean backbutton = false;
private boolean refresh = false;
private Table outerTable = new Table(2,3);
private boolean isAdmin = false;
private String outerTableWidth = "100%";
private String outerTableHeight = "100%";
private String headerFooterColor = "#8AA7D6";
private String textColor = "#FFFFFF";
private String headerText = "";
private Image footerBackgroundImage;
private Image headerBackgroundImage;
private ImageEntity[] entities;
private String percent = "100";
private Link continueRefresh = new Link("Click here to continue...");


private Text textProxy = new Text();

private Image view;
private Image delete;
private Image use;
private Image copy;
private Image cut;
private Image edit;
private Image save;
private Image cancel;
private Image newImage;
private Image newCategory;
private Image text;

private String language = "IS";

private int textSize = 1;

private String attributeName = "union_id";
//private int attributeId = -1;
private int attributeId = 3;

//private ImageBusiness business;


public ImageViewer(){
//  business = new ImageBusiness();
  continueRefresh.addParameter("refresh","true");
}

public ImageViewer(int categoryId){
  this();
  this.categoryId=categoryId;
}

public ImageViewer(ImageEntity[] entities){
  this();
  this.entities=entities;
}


public void setConnectionAttributes(String attributeName, int attributeId) {
  this.attributeName = attributeName;
  this.attributeId = attributeId;
}

public void setConnectionAttributes(String attributeName, String attributeId) {
  this.attributeName = attributeName;
  this.attributeId = Integer.parseInt(attributeId);
}


/*
**
** not needed for single user mode
**
*/
public String getColumnString(ImageCatagoryAttributes[] attribs){
  String values = "";
  for (int i = 0 ; i < attribs.length ; i++) {
    values += " image_catagory_id = '"+attribs[i].getImageCatagoryId()+"'" ;
    if( i!= (attribs.length-1) ) values += " OR ";
  }
return values;
}

/*
**
** old stuff..replace
**
*/
private void setSpokenLanguage(ModuleInfo modinfo){
 String language2 = modinfo.getParameter("language");
    if (language2==null) language2 = ( String ) modinfo.getSessionAttribute("language");
    if ( language2 != null) language = language2;
}

public void main(ModuleInfo modinfo)throws Exception{
  //isAdmin= isAdministrator(modinfo);
   isAdmin= true;
  setSpokenLanguage(modinfo);
  ImageEntity[] image =  new ImageEntity[1];

  String refreshing = (String) modinfo.getSession().getAttribute("refresh");
  if( refreshing!=null ) refresh = true;

  /*  ImageBusiness.storeEditForm(modinfo);

  outerTable.add(getEditForm(),1,2);

*/
  if( refresh ){
    refresh(modinfo);
    modinfo.getSession().removeAttribute("refresh");
  }

  view = new Image("/pics/jmodules/image/"+language+"/view.gif","View all sizes");
  delete = new Image("/pics/jmodules/image/"+language+"/delete.gif","Delete this image");
  use = new Image("/pics/jmodules/image/"+language+"/use.gif","Use this image");
  copy = new Image("/pics/jmodules/image/"+language+"/copy.gif","Copy this image");
  cut = new Image("/pics/jmodules/image/"+language+"/cut.gif","Cut this image");
  edit = new Image("/pics/jmodules/image/"+language+"/edit.gif","Edit this image");
  text = new Image("/pics/jmodules/image/"+language+"/text.gif","Edit this image's text");

  save = new Image("/pics/jmodules/image/"+language+"/save.gif","Edit this image");
  cancel = new Image("/pics/jmodules/image/"+language+"/cancel.gif","Edit this image");
  newImage = new Image("/pics/jmodules/image/"+language+"/newimage.gif","Upload a new image");
  newCategory = new Image("/pics/jmodules/image/"+language+"/newcategory.gif","Edit this image");


  Window window = new Window("IdegaWeb : Image",800,600,"/image/editWindow.jsp");
  window.setAllMargins(0);
  window.setResizable(true);
  Link uploadLink = new Link(newImage,window);
  uploadLink.addParameter("action","upload");


  if(isAdmin) {
    outerTable.add(uploadLink,2,1);
    outerTable.add(newCategory,2,1);
  }

  outerTable.setColor(1,1,headerFooterColor);
  outerTable.setColor(1,3,headerFooterColor);
  outerTable.setAlignment(1,1,"left");
  outerTable.setAlignment(2,1,"right");
  outerTable.setAlignment(1,2,"center");
  outerTable.setAlignment(1,3,"center");
  outerTable.setVerticalAlignment(1,2,"top");
  outerTable.mergeCells(1,2,2,2);
  outerTable.mergeCells(1,3,2,3);
  outerTable.setWidth(outerTableWidth);
  outerTable.setHeight(outerTableHeight);
  outerTable.setHeight(1,1,"23");
  outerTable.setHeight(1,3,"23");
  outerTable.setCellpadding(2);
  outerTable.setCellspacing(0);

  Table links = new Table(3,1);
  links.setWidth("100%");
  links.setCellpadding(0);
  links.setCellspacing(0);
  links.setAlignment(1,1,"left");
  links.setAlignment(2,1,"center");
  links.setAlignment(3,1,"right");

  if ( headerBackgroundImage != null ) outerTable.setBackgroundImage(1,1,headerBackgroundImage);
  if ( footerBackgroundImage != null ) outerTable.setBackgroundImage(1,3,footerBackgroundImage);

  String imageId = modinfo.getParameter("image_id");
  String imageCategoryId = modinfo.getParameter("image_catagory_id");
  percent = modinfo.getParameter("percent");
  String edit = modinfo.getParameter("edit");
  String action = modinfo.getParameter("action");

  if(edit!=null){
    try{
      getEditor(modinfo);
      outerTable.setColor(1,2,"FFFFFF");
      add(outerTable);
    }
    catch(Throwable e){
      e.printStackTrace(System.err);
    }
  }
  else{
    if(imageId != null){
      try{
        if( action == null){
          limitImageWidth = false;
          image[0] = new ImageEntity(Integer.parseInt(imageId));
          Text imageName = new Text(image[0].getName());
          imageName.setBold();
          imageName.setFontColor(textColor);
          imageName.setFontSize(3);
          outerTable.add(imageName,1,1);
          outerTable.add(displayImage(image[0]),1,2);
          Text backtext = new Text("Bakka <<");
          backtext.setBold();
          Link backLink = new Link(backtext);
          backLink.setFontColor(textColor);
          backLink.setAsBackLink();
          links.add(backLink,1,1);
          outerTable.add(links,1,3);
          add(outerTable);
        }
        else{
          System.out.println("ImageViewer: action but not editing!");
          Text texti;
          ImageHandler handler = null;
          if( "delete".equalsIgnoreCase(action) ){
             texti = new Text("Image deleted.");
          }
          else if( "save".equalsIgnoreCase(action) ){
             texti = new Text("Image saved.");
             handler = (ImageHandler) modinfo.getSessionAttribute("handler");

          }
          else if( "savenew".equalsIgnoreCase(action) ){
             texti = new Text("Image saved as a new image.");
             handler = (ImageHandler) modinfo.getSessionAttribute("handler");
          }
          else texti = new Text("NO ACTION?");

          ImageBusiness.handleEvent(modinfo,handler);

          texti.setBold();
          texti.setFontColor("#FFFFFF");
          texti.setFontSize(3);
          outerTable.add(texti,1,2);
          outerTable.add(Text.getBreak(),1,2);
          outerTable.add(Text.getBreak(),1,2);
          continueRefresh.setFontColor("#FFFFFF");
          continueRefresh.setFontSize(3);
          outerTable.add(continueRefresh,1,2);

          add(outerTable);
        }
       }
      catch(NumberFormatException e) {
        add(new Text("ImageId must be a number"));
        System.err.println("ImageViewer: ImageId must be a number");
      }
    }
    else{

      try{
        if ( (imageCategoryId != null) || (entities!=null) ){
          ImageEntity[] imageEntity;
          String sFirst = modinfo.getParameter("iv_first");//browsing from this image
          if (sFirst!=null) ifirst = Integer.parseInt(sFirst);

          String previousCatagory =  (String)modinfo.getSessionAttribute("image_previous_catagory_id");

          if ( imageCategoryId != null){

            if( (previousCatagory!=null) && (!previousCatagory.equalsIgnoreCase(imageCategoryId)) ){
              modinfo.getSession().removeAttribute("image_previous_catagory_id");
            }

          ImageEntity[] inApplication = (ImageEntity[]) modinfo.getServletContext().getAttribute("image_entities_"+imageCategoryId);
          modinfo.setSessionAttribute("image_previous_catagory_id",imageCategoryId);

            categoryId = Integer.parseInt(imageCategoryId);
            ImageCatagory category = new ImageCatagory(categoryId);
            if ( inApplication == null ){
              imageEntity = (ImageEntity[]) category.findRelated(new ImageEntity());
            }
            else imageEntity = inApplication;

            if( imageEntity!=null ){
              modinfo.getServletContext().removeAttribute("image_entities_"+imageCategoryId);
              modinfo.getServletContext().setAttribute("image_entities_"+imageCategoryId,imageEntity);
            }

            Text categoryName = new Text(category.getName());
            categoryName.setBold();
            categoryName.setFontColor(textColor);
            categoryName.setFontSize(3);
            outerTable.add(categoryName,1,1);

            if( limitNumberOfImages ) {

              int too = (ifirst+numberOfDisplayedImages);
              if( numberOfDisplayedImages >= imageEntity.length){
                 too = imageEntity.length;
                 numberOfDisplayedImages = too;
              }
              Text leftText = new Text("Fyrri myndir <<");
              leftText.setBold();

              Link back = new Link(leftText);
              back.setFontColor(textColor);
              int iback = ifirst-numberOfDisplayedImages;
              if( iback<0 ) ifirst = 0;
              back.addParameter("iv_first",ifirst);
              back.addParameter("image_catagory_id",category.getID());
              String middle = (ifirst+1)+" til "+too+" af "+(imageEntity.length);
              Text middleText = new Text(middle);
              middleText.setBold();
              middleText.setFontColor(textColor);


              Text rightText = new Text(">> N�stu myndir");
              rightText.setBold();

              Link forward = new Link(rightText);
              forward.setFontColor(textColor);

              int inext = ifirst+numberOfDisplayedImages;
              if( inext > (imageEntity.length-1)) inext = (imageEntity.length-1)-numberOfDisplayedImages;

              forward.addParameter("iv_first",inext);
              forward.addParameter("image_catagory_id",category.getID());

              links.add(back,1,1);
              links.add(middleText,2,1);
              links.add(forward,3,1);


            }
          }
          else{
          //for searches

            Text header = new Text(headerText);
            header.setBold();
            header.setFontColor(textColor);
            header.setFontSize(3);
            outerTable.add(header,1,1);
            limitNumberOfImages=false;
            imageEntity=entities;


          }
          outerTable.add(links,1,3);
          outerTable.add(displayCatagory(imageEntity),1,2);
          add(outerTable);

        }

    }
    catch(NumberFormatException e) {
      add(new Text("CategoryId must be a number"));
      System.err.println("ImageViewer: CategoryId must be a number");
    }
  }

  }
}

public static Table getImageInTable(int imageId) throws SQLException {
    Table table = new Table();
    com.idega.jmodule.object.Image image = new com.idega.jmodule.object.Image(imageId);
    table.add(image);
return table;
}

private Table displayImage( ImageEntity image ) throws SQLException
{
  String texti = image.getText();

  int imageId = image.getID();
  Table imageTable = new Table(1, 3);

  imageTable.setAlignment("center");
  imageTable.setAlignment(1,1,"center");
  imageTable.setAlignment(1,2,"center");
  imageTable.setAlignment(1,3,"center");
  imageTable.setVerticalAlignment("top");
  imageTable.setCellpadding(0);

  Image theImage = new Image(imageId);
  if( limitImageWidth ) theImage.setWidth(maxImageWidth);
  Link bigger = new Link(theImage);
  bigger.addParameter("image_id",imageId);

  imageTable.add(bigger, 1, 1);

  if ( (texti!=null) && (!"".equalsIgnoreCase(texti)) ){
    Text imageText = new Text(texti);
    getTextProxy().setFontSize(1);
    imageText = setTextAttributes( imageText );
    imageTable.add(imageText, 1, 2);
    imageTable.setColor(1,2,"#CCCCCC");
  }

  if(isAdmin) {
    //String adminPage="/image/imageadmin.jsp";//change to same page and object
    Table editTable = new Table(5,1);
    Link imageEdit = new Link(delete);
    imageEdit.addParameter("image_id",imageId);
    imageEdit.addParameter("action","delete");
    Link imageEdit2 = new Link(cut);
    imageEdit2.addParameter("image_id",imageId);
    imageEdit2.addParameter("action","cut");
    Link imageEdit3 = new Link(copy);
    imageEdit3.addParameter("image_id",imageId);
    imageEdit3.addParameter("action","copy");
    Link imageEdit4 = new Link(view);
    imageEdit4.addParameter("image_id",imageId);
    Link imageEdit5 = new Link(use);
    imageEdit5.addParameter("image_id",imageId);
    imageEdit5.addParameter("action","use");
    Link imageEdit6 = new Link(edit);
    imageEdit6.addParameter("image_id",imageId);
    imageEdit6.addParameter("edit","true");

    Window window = new Window("IdegaWeb : Image",350,300,"/image/editWindow.jsp");
    window.setAllMargins(0);
    window.setResizable(true);
    Link imageEdit7 = new Link(text,window);
    imageEdit7.addParameter("image_id",imageId);
    imageEdit7.addParameter("action","text");




    editTable.add(imageEdit,1,1);
   // debug eiki add later
   //editTable.add(imageEdit2,2,1);
   // editTable.add(imageEdit3,3,1);
    editTable.add(imageEdit4,4,1);
    editTable.add(imageEdit5,5,1);
    editTable.add(imageEdit6,2,1);

    editTable.add(imageEdit7,3,1);

    imageTable.add(editTable, 1, 3);
  }

return imageTable;
}

private Table displayCatagory( ImageEntity[] imageEntity )  throws SQLException {
  int k = 0;
  Image image;

  if( limitNumberOfImages ) k = numberOfDisplayedImages;
  else k = imageEntity.length;

  int heigth = k/iNumberInRow;
  if( k%iNumberInRow!=0 ) heigth++;
  Table table = new Table(iNumberInRow,heigth);
  table.setWidth("100%");

  try {
    if (ifirst < 0 ) {
      ifirst = (-1)*ifirst;
    }
    else if (ifirst > (imageEntity.length -1)) {
        ifirst = (imageEntity.length -1);
    }
  }
  catch (NumberFormatException n) {
    add(new Text("ImageViewer: sFirst must be a number"));
    System.err.println("ImageViewer: sFirst must be a number");
  }

  int x=0;
  for (int i = ifirst ; (x<k) && ( i < imageEntity.length ) ; i++ ) {
    table.setVerticalAlignment((x%iNumberInRow)+1,(x/iNumberInRow)+1,"bottom");
    table.setAlignment((x%iNumberInRow)+1,(x/iNumberInRow)+1,"center");
    table.setWidth((x%iNumberInRow)+1,Integer.toString((int)(100/iNumberInRow))+"%");
    table.add( displayImage(imageEntity[i]) ,(x%iNumberInRow)+1,(x/iNumberInRow)+1);
    x++;
  }




return table;
}

/**
** return a proxy for the main text. Use the standard
** set methods on this object such as .setFontSize(1) etc.
** and it will set the property for all texts.
*/
public Text getTextProxy(){

return textProxy;
}

public void setTextProxy(Text textProxy){
	this.textProxy = textProxy;
}

public Text setTextAttributes( Text realText ){
  Text tempText = (Text) textProxy.clone();
  tempText.setText( realText.getText() );
return tempText;
}


public void setNumberOfDisplayedImages(int numberOfDisplayedImages){
  this.limitNumberOfImages = true;
  if( numberOfDisplayedImages<0 ) numberOfDisplayedImages = (-1)*numberOfDisplayedImages;
  this.numberOfDisplayedImages = numberOfDisplayedImages;
}

public void setNumberInRow(int NumberOfImagesInOneRow){
  this.iNumberInRow = NumberOfImagesInOneRow;
}

public void setHeaderText(String headerText){
  this.headerText = headerText;
}

public void setHeaderFooterColor(String headerFooterColor){
  this.headerFooterColor=headerFooterColor;
}

public void setHeaderBackgroundImage(Image headerBackgroundImage){
  this.headerBackgroundImage=headerBackgroundImage;
}

public void setHeaderBackgroundImage(String headerBackgroundImageURL){
  setHeaderBackgroundImage(new Image(headerBackgroundImageURL));
}

public void setFooterBackgroundImage(Image footerBackgroundImage){
  this.footerBackgroundImage=footerBackgroundImage;
}

public void setFooterBackgroundImage(String footerBackgroundImageURL){
  setFooterBackgroundImage(new Image(footerBackgroundImageURL));
}

public void setTextColor(String textColor){
  this.textColor=textColor;
}

public void setMaxImageWidth(int maxImageWidth){
  this.limitImageWidth=true;
  this.maxImageWidth = maxImageWidth;
}

public void limitImageWidth( boolean limitImageWidth ){
  this.limitImageWidth=true;
}

public void setTableWidth(int width){
  setTableWidth(Integer.toString(width));
}

public void setTableWidth(String width){
  this.outerTableWidth=width;
}

public void setTableHeight(int height){
  setTableHeight(Integer.toString(height));
}

public void setTableHeight(String height){
  this.outerTableHeight=height;
}
public void setViewImage(String imageName){
  view = new Image(imageName);
}

public void setDeleteImage(String imageName){
  delete = new Image(imageName);
}



public void setUseImage(String imageName){
  use = new Image(imageName);
}

public void setCopyImage(String imageName){
  copy = new Image(imageName);
}

public void setCutImage(String imageName){
  cut = new Image(imageName);
}

public void refresh(){
  this.refresh = true;
}

private void refresh(ModuleInfo modinfo) throws SQLException{
  modinfo.removeSessionAttribute("image_previous_catagory_id");
  ImageCatagory[] catagories = (ImageCatagory[])(new ImageCatagory()).findAll();

  if (catagories != null) {
    if (catagories.length > 0 ) {
      for (int i = 0 ; i < catagories.length ; i++ ) {
        modinfo.getServletContext().removeAttribute("image_entities_"+catagories[i].getID());
      }
    }
  }

}

public void setCallingModule(String callingModule){
  this.callingModule = callingModule;
}

private Table getImageInfoTable(){
 Table table = new Table();
 table.setColor("");
return table;
}
private Form getEditorForm(ImageHandler handler, String ImageId, ModuleInfo modinfo) throws Exception{

  Table toolbarBelow = new Table(4,2);
  Table toolbarRight = new Table(2,5);
  Table imageTable = new Table(2,2);
  Form form = new Form();
  form.setMethod("GET");

  Link gray = new Link(new Image("/pics/jmodules/image/buttons/grayscale.gif","Convert the image to grayscale"));
  setAction(gray,"grayscale");
  toolbarBelow.add(gray,1,1);

  Link emboss = new Link(new Image("/pics/jmodules/image/buttons/emboss.gif","Emboss the image"));
  setAction(emboss,"emboss");
  toolbarBelow.add(emboss,1,1);

  Link sharpen = new Link(new Image("/pics/jmodules/image/buttons/sharpen.gif","Sharpen the image"));
  setAction(sharpen,"sharpen");
  toolbarBelow.add(sharpen,1,1);

  Link invert = new Link(new Image("/pics/jmodules/image/buttons/invert.gif","Invert the image"));
  setAction(invert,"invert");
  toolbarBelow.add(invert,1,1);

  Text widthtext = new Text("Width:"+Text.getBreak());
  widthtext.setFontSize(1);
  toolbarBelow.add(widthtext,1,2);
//debug
  TextInput widthInput = new TextInput("width",""+handler.getModifiedWidth());

  widthInput.setSize(5);
  toolbarBelow.add(widthInput,1,2);

  Text heighttext = new Text("Height:"+Text.getBreak());
  heighttext.setFontSize(1);
  toolbarBelow.add(heighttext,2,2);
//debug
 TextInput heightInput = new TextInput("height",""+handler.getModifiedHeight());

  heightInput.setSize(5);
  toolbarBelow.add(heightInput,2,2);


  Text con = new Text(Text.getBreak()+"Constrain?");
  con.setFontSize(1);
  toolbarBelow.add(con,3,2);
  CheckBox constrained = new CheckBox("constraint","true");
  constrained.setChecked(true);
  toolbarBelow.add(constrained,3,2);
  toolbarBelow.add(new SubmitButton(new Image("/pics/jmodules/image/buttons/scale.gif"),"scale","true"),4,2);
  toolbarBelow.add(new HiddenInput("edit","true"),4,2);

  Link undo = new Link(new Image("/pics/jmodules/image/buttons/undo.gif","Undo the last changes"));
  setAction(undo,"undo");
  toolbarBelow.add(undo,3,1);

  Link save = new Link(new Image("/pics/jmodules/image/buttons/save.gif","Save the image"));
  save.addParameter("action","save");
  save.addParameter("image_id",ImageId);
  toolbarBelow.add(save,4,1);

  Link savenew = new Link(new Image("/pics/jmodules/image/buttons/savenew.gif","Save as a new image"));
  savenew.addParameter("action","savenew");
  savenew.addParameter("image_id",ImageId);
  toolbarBelow.add(savenew,4,1);

  Link brightness = new Link(new Image("/pics/jmodules/image/buttons/brightness.gif","Adjust the brightness of the image"));
  setAction(brightness,"brightness");
  toolbarRight.add(brightness,1,1);

  Link contrast = new Link(new Image("/pics/jmodules/image/buttons/contrast.gif","Adjust the contrast of the image"));
  setAction(contrast,"contrast");
  toolbarRight.add(contrast,1,2);

  Link color = new Link(new Image("/pics/jmodules/image/buttons/color.gif","Adjust the color of the image"));
  setAction(color,"color");
  toolbarRight.add(color,1,3);

  Link quality = new Link(new Image("/pics/jmodules/image/buttons/quality.gif","Adjust the quality of the image"));
  setAction(quality,"quality");
  toolbarRight.add(quality,1,4);

  Link revert = new Link(new Image("/pics/jmodules/image/buttons/revert.gif","Revert to the last saved version of the image"));
  setAction(revert,"revert");
  toolbarRight.add(revert,2,1);

  Link rotate = new Link(new Image("/pics/jmodules/image/buttons/rotate.gif","Rotate the image CCW/CW"));
  setAction(rotate,"rotate");
  toolbarRight.add(rotate,2,2);

  Link horizontal = new Link(new Image("/pics/jmodules/image/buttons/horizontal.gif","Flip the image horizontaly"));
  setAction(horizontal,"horizontal");
  toolbarRight.add(horizontal,2,3);

  Link vertical = new Link(new Image("/pics/jmodules/image/buttons/vertical.gif","Flip the image verticaly"));
  setAction(vertical,"vertical");
  toolbarRight.add(vertical,2,4);


  imageTable.add(toolbarRight,1,1);
  imageTable.add(toolbarBelow,1,2);
  imageTable.mergeCells(1,2,2,2);
  imageTable.setWidth("100%");
  imageTable.setHeight("100%");
  imageTable.setAlignment(1,1,"left");
  imageTable.setAlignment(2,1,"left");
  imageTable.setAlignment(1,2,"center");
  imageTable.setVerticalAlignment(1,1,"top");
  imageTable.setVerticalAlignment(2,1,"middle");


  if( handler != null) {
  //debug
    Image myndin = handler.getModifiedImageAsImageObject(modinfo);

    String percent2  = modinfo.getParameter("percent");
    if (percent2!=null) percent = TextSoap.findAndReplace(percent2,"%","");
    int iPercent = 100;
    try{
      iPercent = Integer.parseInt(percent);
    }
    catch (NumberFormatException n) {
      iPercent = 100;
      percent = "100";
    }
    myndin.setWidth( (myndin.getWidth()* iPercent)/100  );
    myndin.setHeight( (myndin.getHeight()* iPercent)/100 );

    imageTable.add( myndin ,2,1);

    Text percentText = new Text(Text.getBreak()+"Percent:"+Text.getBreak());
    percentText.setFontSize(1);
    imageTable.add(percentText,1,1);
    TextInput percentInput = new TextInput("percent",percent+"%");
    percentInput.setSize(4);
    imageTable.add(percentInput,1,1);

  }

  toolbarBelow.mergeCells(1,1,2,1);

  toolbarBelow.setAlignment(1,1,"center");
  toolbarBelow.setAlignment(1,2,"left");
  toolbarBelow.setAlignment(2,2,"left");
  toolbarBelow.setAlignment(3,2,"left");
  toolbarBelow.setAlignment(4,2,"left");
  toolbarBelow.setAlignment(3,1,"right");
  toolbarBelow.setAlignment(4,1,"right");


  form.add(imageTable);

  return form;
  }

private void setAction(Link theLink, String action){
  theLink.addParameter("action",action);
  theLink.addParameter("edit","true");
  theLink.addParameter("percent",percent);
}

private Form getEditForm(){
  Form frameForm = new Form();
  Table frameTable = new Table(1,2);
  frameTable.setCellpadding(0);
  frameTable.setCellspacing(0);

  List catagories = ImageBusiness.getAllImageCatagories();
  int catagorieslength = (catagories != null) ? catagories.size() : 0;

  Table contentTable = new Table(3,catagorieslength+2);
  contentTable.setCellpadding(0);
  contentTable.setCellspacing(0);

  int textInputLenth = 20;
  String catagoriTextInputName = "catagory";
  String deleteTextInputName = "delete";

  for (int i = 0; i < catagorieslength; i++) {
    TextInput catagoryInput = new TextInput(catagoriTextInputName,((ImageCatagory)catagories.get(i)).getImageCatagoryName());
    catagoryInput.setLength(textInputLenth);
    contentTable.add(catagoryInput,1,i+2);
    contentTable.setHeight(i+1,"30");
    contentTable.add(new CheckBox(deleteTextInputName, Integer.toString(((ImageCatagory)catagories.get(i)).getID())),3,i+2);
  }

  Text catagoryText = new Text("Flokkur");
  catagoryText.setBold();
  catagoryText.setFontColor("#FFFFFF");

  Text deleteText = new Text("Ey�a");
  deleteText.setBold();
  deleteText.setFontColor("#FFFFFF");

  contentTable.add(catagoryText,1,1);
  contentTable.add(deleteText,3,1);

  TextInput catagoryInput = new TextInput(catagoriTextInputName);
  catagoryInput.setLength(textInputLenth);
  contentTable.add(catagoryInput,1,catagorieslength+2);

  frameTable.add(contentTable,1,1);

  //Buttons
  Table buttonTable = new Table(3,1);
  buttonTable.setHeight(40);
  buttonTable.setCellpadding(0);
  buttonTable.setCellspacing(0);
  SubmitButton save = new SubmitButton("Vista","catagory_edit_form","save");
  buttonTable.add(save,3,1);
  buttonTable.setWidth(3,1,"60");
  SubmitButton cancel = new SubmitButton("H�tta vi�","catagory_edit_form", "cancel");
  buttonTable.add(cancel,2,1);
  buttonTable.setWidth(2,1,"60");
  frameTable.add(buttonTable,1,2);
  frameTable.setAlignment(1,2,"right");
  //Buttons ends

  frameForm.add(frameTable);

  return frameForm;

}


public void getEditor(ModuleInfo modinfo) throws Throwable{

  String whichButton = modinfo.getParameter("submit");
  String ImageId = modinfo.getParameter("image_id");
  String imageInSession = (String) modinfo.getSessionAttribute("image_in_session");
  ImageHandler handler = (ImageHandler) modinfo.getSessionAttribute("handler");

  if (imageInSession!=null){
    if (ImageId != null) {
      if( !ImageId.equalsIgnoreCase(imageInSession) ){
        modinfo.setSessionAttribute("image_in_session",ImageId);
        handler = new ImageHandler(Integer.parseInt(ImageId));
        modinfo.setSessionAttribute("handler",handler);

      }
      ImageBusiness.handleEvent(modinfo,handler);
      outerTable.add(getEditorForm(handler,ImageId,modinfo),1,2);
    }
    else {
      ImageId = imageInSession;
      ImageBusiness.handleEvent(modinfo,handler);
      outerTable.add(getEditorForm(handler,ImageId,modinfo),1,2);
    }
  }
   else{
    if( ImageId!=null ) {
      modinfo.setSessionAttribute("image_in_session",ImageId);
      handler = new ImageHandler(Integer.parseInt(ImageId));
      modinfo.setSessionAttribute("handler",handler);
      ImageBusiness.handleEvent(modinfo,handler);
      outerTable.add(getEditorForm(handler,ImageId,modinfo),1,2);
    }
   }



////
/*
 try {

  Conn = GenericEntity.getStaticInstance("com.idega.jmodule.image.data.ImageEntity").getConnection();

    if (Conn != null) {
        if (whichButton!=null && !(whichButton.equals(""))){
          // opening a new upload window
          if( whichButton.equals("new")){
          //getSession().removeAttribute("ImageId");


            //debug eiki was in main(....
            if( ImageId!=null){//var til fyrir
              drawUploadTable(ImageId,true);
            }
            else{
              drawUploadTable( ImageBusiness.getImageID(Conn) ,false);
            }

            //


            Upload(Conn,modinfo);
          }
          // done uploading
          else if (whichButton.equals("Vista")){

          //debug eiki make this work!
          //  myWindow.setParentToReload();

            //debug eiki added
            int imageId = Integer.parseInt((String)modinfo.getSessionAttribute("image_id"));
            String[] categories = modinfo.getParameterValues("category");
            ImageBusiness.saveImageToCatagories(imageId,categories);

            //debug eiki make this work!

            //myWindow.close();

          }
          // updating
          else{
            ImageId = (String)modinfo.getSessionAttribute("image_id");

            //debug eiki
            if( ImageId!=null){//var til fyrir
              drawUploadTable(ImageId,true);
            }
            else{
              drawUploadTable( ImageBusiness.getImageID(Conn) ,false);
            }

          }
          }
          else {
                  //Upload(Conn);//no if here because of the multipart-request
          }
    }

  }
  catch (Throwable E){
          E.printStackTrace(System.err);
          add(E.getMessage());
  }
  finally{
    if( Conn!=null) GenericEntity.getStaticInstance("com.idega.jmodule.image.data.ImageEntity").freeConnection(Conn);
  }

*/



}


}
