package com.idega.block.media.presentation;

import java.sql.*;
import java.util.*;
import java.io.*;
import com.idega.util.*;
import com.idega.jmodule.object.textObject.*;
import com.idega.jmodule.object.*;
import com.idega.jmodule.object.interfaceobject.*;
import com.idega.block.media.data.*;
import com.idega.block.media.presentation.*;
import com.idega.data.*;
import com.idega.util.text.*;
import com.idega.core.data.ICFileCategory;

public class ImageBrowser extends JModuleObject{

private String width="100%";
private String treeWidth = "170";

private boolean showAll = false;
private boolean refresh = false;
private ImageTree tree = new ImageTree();
private ImageViewer viewer = new ImageViewer();

  public String getWidth(){
    return this.width;
  }

  public void setWidth(String width){
    this.width =  width;
  }

  public void setTreeWidth(String width){
    this.treeWidth =  width;
  }

  public void setShowAll(boolean showAll){
    this.showAll =  showAll;
  }

  public void refresh(){
    this.refresh=true;
  }

  public ImageViewer getImageViewer(){
    return this.viewer;
  }

  public ImageTree getImageTree(){
    return this.tree;
  }

  public void main(ModuleInfo modinfo)throws Exception{

    String mode = modinfo.getParameter("mode");
    String edit = modinfo.getParameter("edit");//so it doesn't conflict with imageviewer
    String action = modinfo.getParameter("action");//so it doesn't conflict with imageviewer

    String refreshing = (String) modinfo.getSessionAttribute("refresh");
    if( refreshing!=null ) refresh = true;

    if ( refresh ) {
      tree.refresh();
      viewer.refresh();
      modinfo.removeSessionAttribute("refresh");
    }

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
      imageTable.setVerticalAlignment(3,1,"top");

      imageTable.add(tileTable,1,2);


      tree.setWidth(treeWidth);
      tree.setShowAll(showAll);

    if ( mode.equalsIgnoreCase("image") || (edit!=null)  || (action!=null) ) {
      viewer.limitImageWidth(true);
      viewer.setNumberOfDisplayedImages(9);
      viewer.setHeaderFooterColor("#336699");
      viewer.setFooterBackgroundImage("/pics/jmodules/image/myndamodule/footer/foottiler.gif");
      imageTable.add(viewer,3,1);
      //debug because of refresh problem could solve with an invisible businenss class that is added first
      if( (!"delete".equalsIgnoreCase(action)) && (!"save".equalsIgnoreCase(action)) && (!"savenew".equalsIgnoreCase(action)) && (!"savecategories".equalsIgnoreCase(action)) ){
        //imageTable.add(tree,1,1);
      }
    }
    else if( mode.equalsIgnoreCase("search") ){
      imageTable.add(getSearchResults(modinfo),3,1);
      //imageTable.add(tree,1,1);
    }

    myTable.add(getToolbar(),1,1);
    myTable.addText("",1,2);
    myTable.setHeight(2,"5");
    myTable.add(imageTable,1,3);

    categoryForm.add(myTable);

    add( categoryForm );

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

   ICFileCategory[] category = (ICFileCategory[]) (new ICFileCategory()).findAll();

   Image searchImage = new Image("/pics/jmodules/image/myndamodule/topp/topp2.gif");
   Image searchWord = new Image("/pics/jmodules/image/myndamodule/topp/topp3.gif");

   DropdownMenu categoryMenu = new DropdownMenu(category,"category_id");
    categoryMenu.addMenuElement(0,"Allir myndaflokkar");
    categoryMenu.setSelectedElement("0");
    categoryMenu.keepStatusOnAction();
    categoryMenu.setAttribute("style","font-size: 7pt");

   TextInput searchText = new TextInput("searchString");
    searchText.setAttribute("style","font-size: 7pt");
    searchText.setLength(40);
    searchText.keepStatusOnAction();

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

    String category_id = modinfo.getParameter("category_id");
      if ( category_id.equalsIgnoreCase("0") ) {
        allCategories = true;
      }

    String searchString = modinfo.getParameter("searchString");
      if ( searchString == null || searchString.equalsIgnoreCase("") ) {
        searchString = "";
        isQuery = false;
      }
    String queryString = "select * from ic_file,ic_file_file_category where ic_file.ic_file_id=ic_file_file_category.ic_file_id and ic_file_file_category.ic_file_category_id="+category_id+" and ";
    if ( allCategories ) {
      queryString = "select * from ic_file where ";
    }

    StringTokenizer tokens = new StringTokenizer(searchString);
    while ( tokens.hasMoreTokens() ) {
      String token = tokens.nextToken();
      queryString += "(description like '%"+token+"%' or name like'%"+token+"%')";
      if ( tokens.hasMoreTokens() ) {
        queryString += " and ";
      }
    }

    //add(queryString);

    Table myTable = new Table(1,1);
      myTable.setCellpadding(0);
      myTable.setCellspacing(0);
      myTable.setWidth("100%");
      myTable.setHeight("100%");
      myTable.setVerticalAlignment(1,1,"top");

    if ( isQuery ) {

      ImageEntity[] image;

      if ( allCategories ) {
         image = (ImageEntity[]) (new ImageEntity()).findAll(queryString);
      }

      else {
         image = (ImageEntity[]) (new ImageEntity()).findAll(queryString);
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
