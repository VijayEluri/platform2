//idega 2000 - Tryggvi Larusson
/*
*Copyright 2000 idega.is All Rights Reserved.
*/

package is.idega.idegaweb.golf.presentation;

import is.idega.idegaweb.golf.block.login.business.AccessControl;

import com.idega.presentation.IWContext;
import com.idega.presentation.Image;
import com.idega.presentation.PresentationObject;
import com.idega.presentation.Table;
import com.idega.presentation.ui.MenuBar;
import com.idega.presentation.ui.Window;

/**
*@author <a href="mailto:tryggvi@idega.is">Tryggvi Larusson</a>
*@version 1.3
*/
public class JModuleAdminWindow extends Window{


private Table tafla;
public String URI = null;

public String headerColor ="#8AB490";
public String backgroundColor = "#D4D4D4";
//public String foregroundColor= "#8AB490";
public String foregroundColor= headerColor;
public String topLeftImage = "/common/pics/idegaweb/idegaweb_standard.gif";
//public String topRightImage;
public String backgroundImage = "/common/pics/idegaweb/idegawebBackground.gif";
private IWContext modinfo;
private boolean useIWMenuBar=false;


public boolean first;

private MenuBar Menu;
public String MenuAlignment = "&nbsp;&nbsp;&nbsp;&nbsp;";


  public JModuleAdminWindow(){
    super(600,500);
    first = true;
    tafla = new Table(2,2);
    super.add(tafla);
    initialize();
  }

  public JModuleAdminWindow(int x,int y){
    super(x,y);
  }

  /*public void main(ModuleInfo modinfo)throws Exception{
    super.main(modinfo);
    //initialize(modinfo);
  }*/

  public boolean isAdministrator(IWContext modinfo)throws Exception{
    return AccessControl.isAdmin(modinfo);
  }

  /**
   * <H2>Unimplemented</H2>
   */
  public boolean isDeveloper(IWContext modinfo)throws Exception{
    return false;
  }

  /**
   * <H2>Unimplemented</H2>
   */
  public boolean isUser(IWContext modinfo)throws Exception{
    return false;
  }


  /**
   * <H2>Unimplemented</H2>
   */
  public boolean isMemberOf(IWContext modinfo,String groupName)throws Exception{
    return false;
  }

  /**
   * <H2>Unimplemented</H2>
   */
/*  public boolean hasPermission(String permissionType,ModuleInfo modinfo,ModuleObject obj)throws Exception{
    return AccessControl.hasPermission(permissionType,modinfo,obj);
  }

 public boolean hasPermission(String permissionType,ModuleInfo modinfo)throws Exception{
    return hasPermission(permissionType,modinfo,this);
  }*/


  public String getModuleName(){
    return this.getClass().getName();
  }


  public void setIWMenuBar(boolean ifInUse){
    this.useIWMenuBar=ifInUse;
  }














	/*public void setWindow(Window window){
		setPage(window);
	}*/

        public void initialize(){
	//public void initialize(ModuleInfo modinfo){

        //System.out.println("I initialize");
	/*HttpServletRequest request = getRequest();
          if (request != null) {
	    URI = request.getRequestURI();
         }*/

          //setPage(new Window());
	  Window window = this;

          if(useIWMenuBar){

            Menu = new MenuBar();
            MenuBar();
            super.add(Menu);
          }


	  window.setMarginHeight(0);
	  window.setMarginWidth(0);
	  window.setLeftMargin(0);
	  window.setTopMargin(0);
	  window.setAlinkColor("black");
	  window.setVlinkColor("black");
	  window.setLinkColor("black");
          window.setScrollbar(false);
          //jmodule.setBackgroundColor(backgroundColor);
          window.setBackgroundImage(new Image(backgroundImage));
          //jmodule.add(gluggi(modinfo));
          initTable();
	}



	public Window getWindow(){
		return this;
	}

        public void initTable() {
	//public Table gluggi(ModuleInfo modinfo) {
          //URI=modinfo.getRequest().getRequestURI();
          URI="";
	  tafla.setWidth("100%");
          tafla.setVerticalAlignment("top");
	  tafla.setHeight("100%");
          int widthOfHeader=58;
	  tafla.setHeight(1,Integer.toString(widthOfHeader));
          tafla.setHeight(2,Integer.toString(Integer.parseInt(getHeight())-widthOfHeader));
	  //tafla.setHeight(2,"17");
	  //tafla.setBorder(0);
	  //tafla.setAlignment("center");
	  tafla.setCellpadding(0);
	  tafla.setCellspacing(0);
	  tafla.mergeCells(1,2,2,2);

          tafla.setVerticalAlignment(1,2,"top");
          Image top = new Image(topLeftImage);

	  tafla.add(top,1,1);

	  //tafla.add(new BackButton(new Image("/common/pics/idegaweb/back.gif")),2,2);
	  //Link linkLoka = new Link(new Image("/common/pics/idegaweb/close.gif","Loka"),URI);
          //linkLoka.addParameter("action","exit");
          //tafla.add(linkLoka,2,2);

          //Image topTiler = new Image("/common/pics/idegaweb/idegawebTiler.gif");

	  //tafla.setBackgroundImage(1,1,topTiler);
	  //tafla.setBackgroundImage(2,1,topTiler);



	}


	public void add(PresentationObject objectToAdd){
	  tafla.add(objectToAdd,1,2);
	}


        public void empty(){
	  tafla.emptyCell(1,2);
	}


    private void MenuBar(){

      Menu.setPosition(0,40);
      Menu.setSizes(1,1,0);
      Menu.setColors("#444444", "#FFFFFF", "#BDBDBD" , "#444444" , foregroundColor, "#444444" , "#BDBDBD" , foregroundColor, "#444444");
      Menu.setFonts("Arial", "Helvetica" , "sans-serif", "normal", "normal", 8,"Arial", "Helvetica", "sans-serif", "normal", "normal", 8);
      Menu.scaleNavBar();

      Menu.addMenu("file", 80, 120);
      Menu.addMenu("addons", 80, 120);
      Menu.addMenu("tools", 80, 120);
      Menu.addMenu("options", 80, 120);
      Menu.addMenu("help", 80, 120);

      Menu.addItem("file", MenuAlignment+"File");
      Menu.addItem("addons", MenuAlignment+"Add-ons");
      Menu.addItem("tools", MenuAlignment+"Tools");
      Menu.addItem("options", MenuAlignment+"Options");
      Menu.addItem("help", MenuAlignment+"Help");

      this.addToOptionsMenu("Themes", "");
      this.addToOptionsMenu("Language","");
      this.addToHelpMenu("Help", "");

    }



    public void addToFileMenu(String ItemName, String Url){
      Menu.addItem("file", MenuAlignment+ItemName, Url);
    }

    public void addToAddOnsMenu(String ItemName, String Url){
      Menu.addItem("addons", MenuAlignment+ItemName, Url);
    }

    public void addToToolsMenu(String ItemName, String Url){
      Menu.addItem("tools", MenuAlignment+ItemName, Url);
    }

    public void addToOptionsMenu(String ItemName, String Url){
      Menu.addItem("options", MenuAlignment+ItemName, Url);
    }

    public void addToHelpMenu(String ItemName, String Url){
      Menu.addItem("help", MenuAlignment+ItemName, Url);
    }


    protected MenuBar getMenu(){
      return Menu;
    }


    public void checkSettings() throws Exception{

    }

    public void main(IWContext modinfo) throws Exception {
      this.modinfo = modinfo;
      checkSettings();
    }




}
