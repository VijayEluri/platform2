package com.idega.jmodule.image.presentation;

import java.sql.*;
import java.util.*;
import java.io.*;
import com.idega.util.*;
import com.idega.jmodule.object.textObject.*;
import com.idega.jmodule.object.*;
import com.idega.jmodule.object.interfaceobject.*;
import com.idega.jmodule.image.data.*;
import com.idega.jmodule.image.presentation.*;
import com.idega.data.*;
import com.idega.util.text.*;

public class ImageBrowser extends JModuleObject{

private String width="100%";

  public void main(ModuleInfo modinfo)throws Exception{
    add(getBrowserTable(modinfo));
  }

  public String getWidth(){
    return this.width;
  }

  public void setWidth(String width){
    this.width =  width;
  }

  private Form getBrowserTable(ModuleInfo modinfo) throws SQLException {

    String mode = modinfo.getRequest().getParameter("mode");
      if ( mode == null ) { mode = "image"; }

    Form categoryForm = new Form();
      categoryForm.add(new HiddenInput("mode","search"));

    Table myTable = new Table(1,3);
      myTable.setWidth(getWidth());
      myTable.setHeight("100%");
      myTable.setCellpadding(0);
      myTable.setCellspacing(0);
      myTable.setColor("#336699");
      myTable.setVerticalAlignment(1,1,"top");
      myTable.setVerticalAlignment(1,2,"top");
      myTable.setHeight(3,"100%");

    Table tileTable = new Table(1,1);
      tileTable.setCellpadding(0);
      tileTable.setCellspacing(0);
      tileTable.setHeight(23);
      tileTable.setWidth("100%");
      tileTable.setBackgroundImage(1,1,new Image("/pics/jmodules/image/myndamodule/footer/foottiler.gif"));

    Table imageTable = new Table(3,2);
      imageTable.setCellpadding(0);
      imageTable.setCellspacing(0);
      imageTable.mergeCells(1,2,2,2);
      imageTable.mergeCells(3,1,3,2);
      imageTable.setHeight("100%");
      imageTable.setWidth("100%");
      imageTable.setWidth(1,1,"150");
      imageTable.setWidth(2,1,"10");
      imageTable.setHeight(1,1,"100%");
      imageTable.setHeight(1,2,"23");
      imageTable.setVerticalAlignment(1,1,"top");
      imageTable.setVerticalAlignment(1,2,"bottom");
      imageTable.add(tileTable,1,2);

    ImageTree tree = new ImageTree();
      tree.setWidth("150");

    imageTable.add(tree,1,1);

    if ( mode.equalsIgnoreCase("image") ) {
      ImageViewer viewer = new ImageViewer();
        viewer.limitImageWidth(true);
        viewer.setNumberOfDisplayedImages(9);
        viewer.setHeaderFooterColor("#336699");
        viewer.setFooterBackgroundImage("/pics/jmodules/image/myndamodule/footer/foottiler.gif");
      imageTable.add(viewer,3,1);
    }
    else {
      imageTable.add(getSearchResults(modinfo),3,1);
    }

    myTable.add(getToolbar(),1,1);
    myTable.addText("",1,2);
    myTable.setHeight(2,"5");
    myTable.add(imageTable,1,3);

    categoryForm.add(myTable);

    return categoryForm;

  }

  private Table getToolbar() throws SQLException {

   Table myTable = new Table(10,1);
    myTable.setCellpadding(0);
    myTable.setCellspacing(0);
    myTable.setWidth("100%");
    myTable.setWidth(1,"100%");
    myTable.setHeight(25);
    myTable.setBackgroundImage(new Image("/pics/jmodules/image/myndamodule/topp/topptiler.gif"));

   Form categoryForm = new Form();
    categoryForm.setMethod("get");

   ImageCatagory[] category = (ImageCatagory[]) (new ImageCatagory()).findAll();

   Image searchImage = new Image("/pics/jmodules/image/myndamodule/topp/topp2.gif");
   Image searchWord = new Image("/pics/jmodules/image/myndamodule/topp/topp3.gif");

   DropdownMenu categoryMenu = new DropdownMenu(category,"catagory_id");
    categoryMenu.setSelectedElement("0");
    categoryMenu.keepStatusOnAction();
    categoryMenu.setAttribute("style","font-size: 7pt");
    categoryMenu.addMenuElement(0,"Allir myndaflokkar");

   TextInput searchText = new TextInput("searchString");
    searchText.setAttribute("style","font-size: 7pt");
    searchText.setLength(40);

   Text spacer = new Text("&nbsp;&nbsp;&nbsp;");

   SubmitButton submit = new SubmitButton(new Image("/pics/jmodules/image/myndamodule/topp/toppleita.gif"));

   myTable.add(searchImage,1,1);
   myTable.add(spacer,2,1);
   myTable.add(categoryMenu,3,1);
   myTable.add(spacer,4,1);
   myTable.add(searchWord,5,1);
   myTable.add(spacer,6,1);
   myTable.add(searchText,7,1);
   myTable.add(spacer,8,1);
   myTable.add(submit,9,1);
   myTable.add(spacer,10,1);

   return myTable;

  }

  private Table getSearchResults(ModuleInfo modinfo) throws SQLException {

    boolean isQuery = true;
    boolean allCategories = false;

    String searchString = modinfo.getRequest().getParameter("searchString");
      if ( searchString == null || searchString.equalsIgnoreCase("") ) {
        searchString = "";
        isQuery = false;
      }
    String category_id = modinfo.getRequest().getParameter("catagory_id");
      if ( category_id.equalsIgnoreCase("0") ) {
        allCategories = true;
      }

    Table myTable = new Table(1,1);
      myTable.setCellpadding(0);
      myTable.setCellspacing(0);
      myTable.setWidth("100%");
      myTable.setHeight("100%");
      myTable.setVerticalAlignment(1,1,"top");

    if ( isQuery ) {

      ImageEntity[] image;

      if ( allCategories ) {
         image = (ImageEntity[]) (new ImageEntity()).findAll("select * from image where image_text like '%"+searchString+"%' or image_name like '%"+searchString+"%'");
      }

      else {
         image = (ImageEntity[]) (new ImageEntity()).findAll("select * from image,image_image_catagory where image_image_catagory.image_catagory_id="+category_id+" and image.image_id=image_image_catagory.image_id and image.image_text like '%"+searchString+"%'  or image.image_name like '%"+searchString+"%'");
      }

      if ( image.length > 0 ) {
        ImageViewer imageViewer = new ImageViewer(image);
          imageViewer.setHeaderFooterColor("#336699");
          imageViewer.setFooterBackgroundImage("/pics/jmodules/image/myndamodule/footer/foottiler.gif");
          imageViewer.setHeaderText("Fann "+image.length+" myndir sem uppfylltu leitarskilyr�i�: <u>"+searchString+"</u>");

        myTable.add(imageViewer);
      }
      else {
        Text noQuery = new Text("Ekkert fannst...");
          noQuery.setFontColor("#FFFFFF");

        myTable.add(noQuery,1,1);
      }

    }

    else {
      Text noQuery = new Text("Engin leitarskilyr�i valin...");
        noQuery.setFontColor("#FFFFFF");

      myTable.add(noQuery,1,1);
    }

    return myTable;

  }

}
