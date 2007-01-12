package com.idega.block.image.presentation;



/**

 * Title: ImageViewer

 * Description:

 * Copyright:    Copyright (c) 2001

 * Company: idega

 * @author Eirikur Hrafnsson, eiki@idega.is

 * @version 1.0

 *

 */





import java.sql.SQLException;
import java.util.List;

import com.idega.block.image.business.ImageBusiness;
import com.idega.block.image.business.ImageHandler;
import com.idega.block.image.data.ImageEntity;
import com.idega.core.file.data.ICFileCategory;
import com.idega.presentation.Block;
import com.idega.presentation.IWContext;
import com.idega.presentation.Image;
import com.idega.presentation.Page;
import com.idega.presentation.Table;
import com.idega.presentation.text.Link;
import com.idega.presentation.text.Text;
import com.idega.presentation.ui.CheckBox;
import com.idega.presentation.ui.Form;
import com.idega.presentation.ui.HiddenInput;
import com.idega.presentation.ui.SubmitButton;
import com.idega.presentation.ui.TextInput;
import com.idega.util.text.TextSoap;





public class ImageViewer extends Block{



private int categoryId = -1;

private boolean limitNumberOfImages=true;

private int numberOfDisplayedImages=9;

private int iNumberInRow = 3; //iXXXX for int

private int ifirst = 0;

private int maxImageWidth = 100;

private boolean limitImageWidth=true;

private String callingModule = "image_id";



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

private String lastViewAction = "";





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

private Image reload;



private String language = "IS";



private int textSize = 1;



private String attributeName = "union_id";

private int attributeId = 3;









public ImageViewer(){

  this.continueRefresh.addParameter("refresh","true");

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







public void main(IWContext iwc)throws Exception{

  this.isAdmin= iwc.hasEditPermission(this);



  /**@todo : add localisation support

   *

   */

  ImageEntity[] image =  new ImageEntity[1];

  String imageId = iwc.getParameter("image_id");

  String imageCategoryId = iwc.getParameter("image_catagory_id");



  this.percent = iwc.getParameter("percent");

  String sEdit = iwc.getParameter("edit");

  String action = iwc.getParameter("action");



  String refreshing = (String) iwc.getSessionAttribute("im_refresh");

  String sessionImageId = (String) iwc.getSessionAttribute("im_image_id");

  String imageSessionName = (String) iwc.getParameter("im_image_session_name");

  if( imageSessionName!=null ) {
	this.callingModule = imageSessionName;
}



  if( sessionImageId!=null ){// new uploaded image

     imageId = sessionImageId;

     iwc.removeSessionAttribute("im_image_id");

  }



  if( refreshing!=null ) {
	this.refresh = true;
}



  if( this.refresh ){

    refresh(iwc);

    iwc.removeSessionAttribute("im_refresh");

  }



  this.view = new Image("/pics/jmodules/image/"+this.language+"/view.gif","View all sizes");

  this.delete = new Image("/pics/jmodules/image/"+this.language+"/delete.gif","Delete this image");

  this.use = new Image("/pics/jmodules/image/"+this.language+"/use.gif","Use this image");

  this.copy = new Image("/pics/jmodules/image/"+this.language+"/copy.gif","Copy this image");

  this.cut = new Image("/pics/jmodules/image/"+this.language+"/cut.gif","Cut this image");

  this.edit = new Image("/pics/jmodules/image/"+this.language+"/edit.gif","Edit this image");

  this.text = new Image("/pics/jmodules/image/"+this.language+"/text.gif","Edit this image's text");



  this.save = new Image("/pics/jmodules/image/"+this.language+"/save.gif","Save");

  this.cancel = new Image("/pics/jmodules/image/"+this.language+"/cancel.gif","Cancel");

  this.newImage = new Image("/pics/jmodules/image/"+this.language+"/newimage.gif","Upload a new image");

  this.newCategory = new Image("/pics/jmodules/image/"+this.language+"/newcategory.gif","Edit categories");

  this.reload = new Image("/pics/jmodules/image/"+this.language+"/refresh.gif","Refresh everything");





  Link uploadLink = new Link(this.newImage);

  uploadLink.setWindowToOpen(EditWindow.class);



  uploadLink.addParameter("action","upload");



  Link reloads = new Link(this.reload);

  reloads.addParameter("refresh","true");

  reloads.addParameter("idega","best&"+iwc.getQueryString());



  Link categories = new Link(this.newCategory);

  categories.addParameter("action","editcategories");

  categories.addParameter("image_id","-1");//so it runs smoothly ; )



  if(this.isAdmin && (sEdit==null) ) {

    this.outerTable.add(reloads,2,1);

    this.outerTable.add(uploadLink,2,1);

    this.outerTable.add(categories,2,1);

  }



  this.outerTable.setColor(1,1,this.headerFooterColor);

  this.outerTable.setColor(1,3,this.headerFooterColor);

  this.outerTable.setAlignment(1,1,"left");

  this.outerTable.setAlignment(2,1,"right");

  this.outerTable.setAlignment(1,2,"center");

  this.outerTable.setAlignment(1,3,"center");

  this.outerTable.setVerticalAlignment(1,2,"top");

  this.outerTable.setVerticalAlignment(1,1,"top");

  this.outerTable.mergeCells(1,2,2,2);

  this.outerTable.mergeCells(1,3,2,3);

  this.outerTable.setWidth(this.outerTableWidth);

  this.outerTable.setHeight(this.outerTableHeight);

  this.outerTable.setHeight(1,1,"23");

  this.outerTable.setHeight(1,3,"23");

  this.outerTable.setCellpadding(2);

  this.outerTable.setCellspacing(0);



  Table links = new Table(3,1);

  links.setWidth("100%");

  links.setCellpadding(0);

  links.setCellspacing(0);

  links.setAlignment(1,1,"left");

  links.setAlignment(2,1,"center");

  links.setAlignment(3,1,"right");



  if ( this.headerBackgroundImage != null ) {
	this.outerTable.setBackgroundImage(1,1,this.headerBackgroundImage);
}

  if ( this.footerBackgroundImage != null ) {
	this.outerTable.setBackgroundImage(1,3,this.footerBackgroundImage);
}



  if(sEdit!=null){

    try{

      getEditor(iwc);

      this.outerTable.setColor(1,2,"FFFFFF");

    }

    catch(Throwable e){

      e.printStackTrace(System.err);

    }

  }

  else{

    if(imageId != null){

      try{

        if( action == null){

          this.limitImageWidth = false;

          image[0] = ((com.idega.block.image.data.ImageEntityHome)com.idega.data.IDOLookup.getHomeLegacy(ImageEntity.class)).findByPrimaryKeyLegacy(Integer.parseInt(imageId));

          Text imageName = new Text(image[0].getName());

          imageName.setBold();

          imageName.setFontColor(this.textColor);

          imageName.setFontSize(3);

          this.outerTable.add(imageName,1,1);

          this.outerTable.add(displayImage(image[0]),1,2);

          Text backtext = new Text("Bakka <<");

          backtext.setBold();

          Link backLink = new Link(backtext);

          backLink.setFontColor(this.textColor);

          backLink.setAsBackLink();

          links.add(backLink,1,1);

          this.outerTable.add(links,1,3);

        }

        else{

//continueRefresh.addParameter("idega","best&"+iwc.getQueryString());

          Text texti = new Text("");

          ImageHandler handler = null;

          if( "delete".equalsIgnoreCase(action) ){

             texti = new Text("Image deleted.");

          }

          else if( "save".equalsIgnoreCase(action) ){

             texti = new Text("Image saved.");

             handler = (ImageHandler) iwc.getSessionAttribute("handler");



          }

          else if( "savenew".equalsIgnoreCase(action) ){

             texti = new Text("Image saved as a new image.");

             handler = (ImageHandler) iwc.getSessionAttribute("handler");

          }

          else if( "use".equalsIgnoreCase(action) ){

            iwc.setSessionAttribute(this.callingModule,imageId);

            //debug is this legal? check if opened from another page or not. close or not

            Page parent = getParentPage();

            parent.close();

            parent.setParentToReload();

          }

          else if( "editcategories".equalsIgnoreCase(action) ){

            this.outerTable.add(getCategoryEditForm(),1,2);

            Text flokkar = new Text("Myndaflokkar");

            flokkar.setBold();

            flokkar.setFontColor(this.textColor);

            flokkar.setFontSize(3);

            this.outerTable.add(flokkar,1,1);

          }

          else if( "savecategories".equalsIgnoreCase(action) ){

            texti = new Text("Imagecategories saved.");

            ImageBusiness.storeEditForm(iwc);

          }



          if( !("use".equalsIgnoreCase(action)) ){

            ImageBusiness.handleEvent(iwc,handler);



            texti.setBold();

            texti.setFontColor("#FFFFFF");

            texti.setFontSize(3);

            this.outerTable.add(texti,1,2);

            this.outerTable.add(Text.getBreak(),1,2);

            this.outerTable.add(Text.getBreak(),1,2);

            this.continueRefresh.setFontColor("#FFFFFF");

            this.continueRefresh.setFontSize(3);

            if( !("editcategories".equalsIgnoreCase(action)) ) {
				this.outerTable.add(this.continueRefresh,1,2);
			}

          }



        }

       }

      catch(NumberFormatException e) {

        add(new Text("ImageId must be a number"));

        System.err.println("ImageViewer: ImageId must be a number");

      }

    }

    else{



      try{

        if ( (imageCategoryId != null) || (this.entities!=null) ){

          ImageEntity[] imageEntity;

          String sFirst = iwc.getParameter("iv_first");//browsing from this image

          if (sFirst!=null) {
			this.ifirst = Integer.parseInt(sFirst);
		}



          String previousCatagory =  (String)iwc.getSessionAttribute("image_previous_catagory_id");



          if ( imageCategoryId != null){



            if( (previousCatagory!=null) && (!previousCatagory.equalsIgnoreCase(imageCategoryId)) ){

              iwc.getSession().removeAttribute("image_previous_catagory_id");

            }



            ImageEntity[] inApplication = (ImageEntity[]) iwc.getServletContext().getAttribute("image_entities_"+imageCategoryId);

            iwc.setSessionAttribute("image_previous_catagory_id",imageCategoryId);



            this.categoryId = Integer.parseInt(imageCategoryId);

            ICFileCategory category = ((com.idega.core.file.data.ICFileCategoryHome)com.idega.data.IDOLookup.getHomeLegacy(ICFileCategory.class)).findByPrimaryKeyLegacy(this.categoryId);

            if ( inApplication == null ){

              imageEntity = (ImageEntity[]) category.findRelated(((com.idega.block.image.data.ImageEntityHome)com.idega.data.IDOLookup.getHomeLegacy(ImageEntity.class)).createLegacy());

            }
			else {
				imageEntity = inApplication;
			}



            if( imageEntity!=null ){

              iwc.getServletContext().removeAttribute("image_entities_"+imageCategoryId);

              iwc.getServletContext().setAttribute("image_entities_"+imageCategoryId,imageEntity);

            }



            Text categoryName = new Text(category.getName());

            categoryName.setBold();

            categoryName.setFontColor(this.textColor);

            categoryName.setFontSize(3);

            this.outerTable.add(categoryName,1,1);



            if( this.limitNumberOfImages ) {



              int too = (this.ifirst+this.numberOfDisplayedImages);

              if( this.numberOfDisplayedImages >= imageEntity.length){

                 too = imageEntity.length;

                 this.numberOfDisplayedImages = too;

              }

              Text leftText = new Text("Fyrri myndir <<");

              leftText.setBold();



              Link back = new Link(leftText);

              back.setFontColor(this.textColor);

              int iback = this.ifirst-this.numberOfDisplayedImages;

              if( iback<0 ) {
				this.ifirst = 0;
			}

              back.addParameter("iv_first",this.ifirst);

              back.addParameter("image_catagory_id",category.getID());

              String middle = (this.ifirst+1)+" til "+too+" af "+(imageEntity.length);

              Text middleText = new Text(middle);

              middleText.setBold();

              middleText.setFontColor(this.textColor);





              Text rightText = new Text(">> N�stu myndir");

              rightText.setBold();



              Link forward = new Link(rightText);

              forward.setFontColor(this.textColor);



              int inext = this.ifirst+this.numberOfDisplayedImages;

              if( inext > (imageEntity.length-1)) {
				inext = (imageEntity.length-1)-this.numberOfDisplayedImages;
			}



              forward.addParameter("iv_first",inext);

              forward.addParameter("image_catagory_id",category.getID());



              links.add(back,1,1);

              links.add(middleText,2,1);

              links.add(forward,3,1);





            }

          }

          else{

          //for searches



            Text header = new Text(this.headerText);

            header.setBold();

            header.setFontColor(this.textColor);

            header.setFontSize(3);

            this.outerTable.add(header,1,1);

            this.limitNumberOfImages=false;

            imageEntity=this.entities;





          }

          this.outerTable.add(links,1,3);

          this.outerTable.add(displayCatagory(imageEntity),1,2);

        }



    }

    catch(NumberFormatException e) {

      add(new Text("CategoryId must be a number"));

      System.err.println("ImageViewer: CategoryId must be a number");

    }

  }



  }



  add(this.outerTable);

}





private Table displayImage( ImageEntity image ) throws SQLException

{

  int imageId = ((Integer)image.getPrimaryKey()).intValue();

  Table imageTable = new Table(1,2);



  imageTable.setAlignment(1,1,"center");

  imageTable.setAlignment(1,2,"center");

  imageTable.setCellpadding(0);



  Image theImage = new Image(imageId);

  theImage.setImageLinkZoomView();



  if(this.limitImageWidth) {

    theImage.setMaxImageWidth(this.maxImageWidth);

  }



  imageTable.add(theImage);



  if(this.isAdmin) {

    //String adminPage="/image/imageadmin.jsp";//change to same page and object

    Table editTable = new Table(5,1);

    Link imageEdit = new Link(this.delete);

    imageEdit.addParameter("image_id",imageId);

    imageEdit.addParameter("action","delete");

    Link imageEdit2 = new Link(this.cut);

    imageEdit2.addParameter("image_id",imageId);

    imageEdit2.addParameter("action","cut");

    Link imageEdit3 = new Link(this.copy);

    imageEdit3.addParameter("image_id",imageId);

    imageEdit3.addParameter("action","copy");

    Link imageEdit4 = new Link(this.view);

    imageEdit4.addParameter("image_id",imageId);

    Link imageEdit5 = new Link(this.use);

    imageEdit5.addParameter("image_id",imageId);

    imageEdit5.addParameter("action","use");

    Link imageEdit6 = new Link(this.edit);

    imageEdit6.addParameter("image_id",imageId);

    imageEdit6.addParameter("edit","true");



    Link imageEdit7 = new Link(this.text);

    imageEdit7.setWindowToOpen(EditWindow.class);

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



    imageTable.add(editTable, 1, 2);

  }



return imageTable;

}



private Table displayCatagory( ImageEntity[] imageEntity )  throws SQLException {

  int k = 0;


  this.limitImageWidth = true;



  if( this.limitNumberOfImages ) {
	k = this.numberOfDisplayedImages;
}
else {
	k = imageEntity.length;
}



  int heigth = k/this.iNumberInRow;

  if( k%this.iNumberInRow!=0 ) {
	heigth++;
}

  Table table = new Table(this.iNumberInRow,heigth);

  table.setWidth("100%");



  try {

    if (this.ifirst < 0 ) {

      this.ifirst = (-1)*this.ifirst;

    }

    else if (this.ifirst > (imageEntity.length -1)) {

        this.ifirst = (imageEntity.length -1);

    }

  }

  catch (NumberFormatException n) {

    add(new Text("ImageViewer: sFirst must be a number"));

    System.err.println("ImageViewer: sFirst must be a number");

  }



  int x=0;

  for (int i = this.ifirst ; (x<k) && ( i < imageEntity.length ) ; i++ ) {

    table.setVerticalAlignment((x%this.iNumberInRow)+1,(x/this.iNumberInRow)+1,"bottom");

    table.setAlignment((x%this.iNumberInRow)+1,(x/this.iNumberInRow)+1,"center");

    table.setWidth((x%this.iNumberInRow)+1,Integer.toString((int)(100/this.iNumberInRow))+"%");

    table.add( displayImage(imageEntity[i]) ,(x%this.iNumberInRow)+1,(x/this.iNumberInRow)+1);

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



return this.textProxy;

}



public void setTextProxy(Text textProxy){

	this.textProxy = textProxy;

}



public Text setTextAttributes( Text realText ){

  Text tempText = (Text) this.textProxy.clone();

  tempText.setText( realText.getText() );

return tempText;

}





public void setNumberOfDisplayedImages(int numberOfDisplayedImages){

  this.limitNumberOfImages = true;

  if( numberOfDisplayedImages<0 ) {
	numberOfDisplayedImages = (-1)*numberOfDisplayedImages;
}

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

  this.limitImageWidth=limitImageWidth;

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

  this.view = new Image(imageName);

}



public void setDeleteImage(String imageName){

  this.delete = new Image(imageName);

}



public void setUseImage(String imageName){

  this.use = new Image(imageName);

}



public void setCopyImage(String imageName){

  this.copy = new Image(imageName);

}



public void setCutImage(String imageName){

  this.cut = new Image(imageName);

}



//debug add sets for other images



public void refresh(){

  this.refresh = true;

}



private void refresh(IWContext iwc) throws SQLException{

  iwc.removeSessionAttribute("image_previous_catagory_id");

  /**@todo : use business object to get categories and folders

   *

   */

  ICFileCategory[] catagories = (ICFileCategory[])(((com.idega.core.file.data.ICFileCategoryHome)com.idega.data.IDOLookup.getHomeLegacy(ICFileCategory.class)).createLegacy()).findAll();



  if (catagories != null) {

    if (catagories.length > 0 ) {

      for (int i = 0 ; i < catagories.length ; i++ ) {

        iwc.getIWMainApplication().removeAttribute("image_entities_"+catagories[i].getID());

      }

    }

  }



}



private Table getImageInfoTable(){

 Table table = new Table();

 table.setColor("");

return table;

}

private Form getEditorForm(ImageHandler handler, String ImageId, IWContext iwc) throws Exception{



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



  Link horizontal = new Link(new Image("/pics/jmodules/image/buttons/horizontal.gif","Flip the image horizontally"));

  setAction(horizontal,"horizontal");

  toolbarRight.add(horizontal,2,3);



  Link vertical = new Link(new Image("/pics/jmodules/image/buttons/vertical.gif","Flip the image vertically"));

  setAction(vertical,"vertical");

  toolbarRight.add(vertical,2,4);



  //debug make a better table!

  Table innerTable = new Table(2,1);

  innerTable.setWidth("100%");

  innerTable.setWidth(1,"130");

  innerTable.setAlignment(1,1,"left");

  innerTable.setAlignment(2,1,"center");



  innerTable.add(toolbarRight,1,1);

  imageTable.add(innerTable,1,1);

  imageTable.add(toolbarBelow,1,2);

  imageTable.mergeCells(1,2,2,2);

  imageTable.setWidth("100%");

  imageTable.setHeight("100%");

  imageTable.setAlignment(1,2,"center");

  imageTable.setVerticalAlignment(1,1,"top");

  imageTable.setVerticalAlignment(2,1,"middle");





  if( handler != null) {

    Image myndin = handler.getModifiedImageAsImageObject(iwc);

    String percent2  = iwc.getParameter("percent");

    if (percent2!=null) {
		this.percent = TextSoap.findAndReplace(percent2,"%","");
	}

    int iPercent = 100;

    if(this.percent==null) {
		this.percent = "100";
	}



    try{

      iPercent = Integer.parseInt(this.percent);

    }

    catch (NumberFormatException n) {

      iPercent = 100;

      this.percent = "100";

    }

    myndin.setWidth( (Integer.parseInt(myndin.getWidth())* iPercent)/100  );

    myndin.setHeight( (Integer.parseInt(myndin.getHeight())* iPercent)/100 );



    innerTable.add( myndin ,2,1);



    Text percentText = new Text(Text.getBreak()+"Percent:"+Text.getBreak());

    percentText.setFontSize(1);

    imageTable.add(percentText,1,1);



    TextInput percentInput = new TextInput("percent",this.percent+"%");

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

  theLink.addParameter("percent",this.percent);

}



private Form getCategoryEditForm(){

  Form frameForm = new Form();

  frameForm.add(new HiddenInput("image_id","-1"));

  frameForm.add(new HiddenInput("action","savecategories"));

  Table frameTable = new Table(1,2);

  frameTable.setCellpadding(0);

  frameTable.setCellspacing(0);



  List catagories = ImageBusiness.getImageCategories();

  int catagorieslength = (catagories != null) ? catagories.size() : 0;



  Table contentTable = new Table(3,catagorieslength+2);

  contentTable.setCellpadding(0);

  contentTable.setCellspacing(0);



  int textInputLenth = 20;

  String catagoriTextInputName = "catagory";

  String deleteTextInputName = "delete";



  for (int i = 0; i < catagorieslength; i++) {

    TextInput catagoryInput = new TextInput(catagoriTextInputName,((ICFileCategory)catagories.get(i)).getName());

    catagoryInput.setLength(textInputLenth);

    contentTable.add(catagoryInput,1,i+2);

    contentTable.add(new HiddenInput("ids",Integer.toString(((ICFileCategory)catagories.get(i)).getID())),1,i+2);



    contentTable.setHeight(i+1,"30");

    contentTable.add(new CheckBox(deleteTextInputName, Integer.toString(((ICFileCategory)catagories.get(i)).getID())),3,i+2);

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

  buttonTable.setWidth(2,1,"80");

  buttonTable.setWidth(3,1,"60");

  buttonTable.setAlignment(2,1,"left");

  buttonTable.setAlignment(3,1,"right");

  buttonTable.setVerticalAlignment(2,1,"bottom");

  buttonTable.setVerticalAlignment(3,1,"bottom");



  SubmitButton savebutton = new SubmitButton(this.save);

  //Link cancelLink = new Link(cancel);

  //buttonTable.add(cancelLink,2,1);

  buttonTable.add(savebutton,3,1);



  frameTable.add(buttonTable,1,2);

  frameTable.setAlignment(1,2,"right");

  //Buttons ends



  frameForm.add(frameTable);



  return frameForm;



}





private void getEditor(IWContext iwc) throws Exception{

  String ImageId = iwc.getParameter("image_id");

  String imageInSession = (String) iwc.getSessionAttribute("image_in_session");

  ImageHandler handler = (ImageHandler) iwc.getSessionAttribute("handler");



  if (imageInSession!=null){

    if (ImageId != null) {

      if( !ImageId.equalsIgnoreCase(imageInSession) ){

        iwc.setSessionAttribute("image_in_session",ImageId);

        handler = new ImageHandler(Integer.parseInt(ImageId));

        iwc.setSessionAttribute("handler",handler);

      }

      ImageBusiness.handleEvent(iwc,handler);

      this.outerTable.add(getEditorForm(handler,ImageId,iwc),1,2);

    }

    else {

      ImageId = imageInSession;

      ImageBusiness.handleEvent(iwc,handler);

      this.outerTable.add(getEditorForm(handler,ImageId,iwc),1,2);

    }

  }

   else{

    if( ImageId!=null ) {

      iwc.setSessionAttribute("image_in_session",ImageId);

      handler = new ImageHandler(Integer.parseInt(ImageId));

      iwc.setSessionAttribute("handler",handler);

      ImageBusiness.handleEvent(iwc,handler);

      this.outerTable.add(getEditorForm(handler,ImageId,iwc),1,2);

    }

   }

}





}

