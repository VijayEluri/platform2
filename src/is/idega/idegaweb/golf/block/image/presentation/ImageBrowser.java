package is.idega.idegaweb.golf.block.image.presentation;

import is.idega.idegaweb.golf.block.image.data.ImageCatagory;
import is.idega.idegaweb.golf.block.image.data.ImageEntity;

import java.sql.SQLException;
import java.util.StringTokenizer;

import com.idega.data.IDOLookup;
import com.idega.presentation.Block;
import com.idega.presentation.IWContext;
import com.idega.presentation.Image;
import com.idega.presentation.Table;
import com.idega.presentation.text.Text;
import com.idega.presentation.ui.DropdownMenu;
import com.idega.presentation.ui.Form;
import com.idega.presentation.ui.HiddenInput;
import com.idega.presentation.ui.SubmitButton;
import com.idega.presentation.ui.TextInput;

public class ImageBrowser extends Block{

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

  public void main(IWContext modinfo)throws Exception{

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
        imageTable.add(tree,1,1);
      }
    }
    else if( mode.equalsIgnoreCase("search") ){
      imageTable.add(getSearchResults(modinfo),3,1);
      imageTable.add(tree,1,1);
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

   ImageCatagory[] category = (ImageCatagory[]) ((ImageCatagory)IDOLookup.instanciateEntity(ImageCatagory.class)).findAll();

   Image searchImage = new Image("/pics/jmodules/image/myndamodule/topp/topp2.gif");
   Image searchWord = new Image("/pics/jmodules/image/myndamodule/topp/topp3.gif");

   DropdownMenu categoryMenu = new DropdownMenu(category,"catagory_id");
    categoryMenu.addMenuElement(0,"Allir myndaflokkar");
    categoryMenu.setSelectedElement("0");
    categoryMenu.keepStatusOnAction();
    categoryMenu.setStyleAttribute("font-size: 7pt");

   TextInput searchText = new TextInput("searchString");
    searchText.setStyleAttribute("font-size: 7pt");
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

  private Table getSearchResults(IWContext modinfo) throws SQLException {

    boolean isQuery = true;
    boolean allCategories = false;

    String category_id = modinfo.getParameter("catagory_id");
      if ( category_id.equalsIgnoreCase("0") ) {
        allCategories = true;
      }

    String searchString = modinfo.getParameter("searchString");
      if ( searchString == null || searchString.equalsIgnoreCase("") ) {
        searchString = "";
        isQuery = false;
      }
    String queryString = "select * from image,image_image_catagory where image.image_id=image_image_catagory.image_id and image_image_catagory.image_catagory_id="+category_id+" and ";
    if ( allCategories ) {
      queryString = "select * from image where ";
    }

    StringTokenizer tokens = new StringTokenizer(searchString);
    while ( tokens.hasMoreTokens() ) {
      String token = tokens.nextToken();
      queryString += "(image_text like '%"+token+"%' or image_name like'%"+token+"%' or image_link like'%"+token+"%')";
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
         image = (ImageEntity[]) ((ImageEntity)IDOLookup.instanciateEntity(ImageEntity.class)).findAll(queryString);
      }

      else {
         image = (ImageEntity[]) ((ImageEntity)IDOLookup.instanciateEntity(ImageEntity.class)).findAll(queryString);
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
