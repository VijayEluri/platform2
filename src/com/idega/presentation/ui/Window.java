//idega 2000 - Tryggvi Larusson
/*
*Copyright 2000 idega.is All Rights Reserved.
*/


/**
*@author <a href="mailto:tryggvi@idega.is">Tryggvi Larusson</a>
*@version 1.2
*/
package com.idega.presentation.ui;

import java.util.Hashtable;
import java.util.Map;

import com.idega.idegaweb.IWApplicationContext;
import com.idega.presentation.IWContext;
import com.idega.presentation.Image;
import com.idega.presentation.Page;

/**
 * Class to create pop-up windows and Windows to open with various settings
 * */
public class Window extends Page{

  private String title;
  private int width;
  private int height;
  private String url;
  private String xlocation = "0";
  private String ylocation = "0";
  //private boolean newURL;
  //private Script theAssociatedScript;
  private static String emptyString="";

  //settings for the window:
  private boolean toolbar;
  private boolean location;
  private boolean scrollbar;
  private boolean directories;
  private boolean menubar;
  private boolean status;
  private boolean titlebar;
  private boolean resizable;
  private boolean fullscreen;

  private static Map allOpenedWindowClasses = new Hashtable();

  //If this window is constructed to open an instance of an object in a new Window via ObjectInstanciator
  private Class classToInstanciate;
  private Class templatePageClass;
  private String templateForObjectInstanciation;

public Window(){
	this(emptyString);
	String className = this.getClass().getName();
	setTitle(className.substring(className.lastIndexOf(".")+1));
}



public Window(String name){
	this(name,400,400);
}

public Window(int width, int heigth) {
	this(emptyString,width,heigth);
	String className = this.getClass().getName();
	setTitle(className.substring(className.lastIndexOf(".")+1));
}

public Window(String name,int width,int height){
	//super();
	//setTitle(name);
	//this.height=height;
	//this.width=width;
	//newURL=false;
	//setSettings();
	//this(name,width,height,IWMainApplication.windowOpenerURL);

    this.setName(name);
    this.setWidth(width);
    this.setHeight(height);
 	
}

public Window(String name,String url){
	this(name,400,400,url);
}

public Window(String name, int width, int height, String url){
	//super();
	setTitle(name);
	this.height=height;
	this.width=width;
	this.url = url;
	//newURL=true;
	setSettings();
}

public Window(String name,String classToInstanciate,String template){
	//this(name,400,400,IWMainApplication.getObjectInstanciatorURL(classToInstanciate,template));
    this(name,400,400);
    try{
      this.setClassToInstanciate(Class.forName(classToInstanciate),template);
    }
    catch(Exception e){
      throw new RuntimeException(e.toString()+e.getMessage());
    }
}

public Window(String name,Class classToInstanciate,Class template){
	//this(name,400,400,IWMainApplication.getObjectInstanciatorURL(classToInstanciate,template));
    this(name,400,400);
    this.setClassToInstanciate(classToInstanciate,template);
}

public Window(String name,Class classToInstanciate){
	//this(name,400,400,IWMainApplication.getObjectInstanciatorURL(classToInstanciate));
}

private void setSettings(){
	setID();
	setToolbar(false);
	setLocation(false);
	setScrollbar(true);
	setDirectories(false);
	setMenubar(false);
	setStatus(false);
	setTitlebar(false);
	setResizable(false);
	
}

public void setToolbar(boolean ifToolbar){
	toolbar=ifToolbar;
}

public void setLocation(boolean ifLocation){
	location=ifLocation;
}

public void setScrollbar(boolean ifScrollbar){
	scrollbar=ifScrollbar;
}

public void setDirectories(boolean ifDirectories){
	directories=ifDirectories;
}

public void setMenubar(boolean ifMenubar){
	menubar=ifMenubar;
}

public void setStatus(boolean ifStatus){
	status=ifStatus;
}

public void setTitlebar(boolean ifTitlebar){
	titlebar=ifTitlebar;
}

public void setResizable(boolean ifResizable){
	resizable=ifResizable;
}

public void setFullScreen(boolean ifFullScreen){
  fullscreen=ifFullScreen;
}


/*returns if the window is a reference to a new url or is created in the same page*/
//private boolean isNewURL(){
//	//return newURL;
//        return true;
//}

  public String getURL(IWContext iwc){
    String ret = null;
    if (url == null) {
      ret = iwc.getApplication().getWindowOpenerURI();
    }
    else {
      ret = url;
    }

//System.out.println("ret1 = " + ret);

    com.idega.builder.data.IBDomain d = com.idega.builder.business.BuilderLogic.getInstance().getCurrentDomain(iwc);

    if (d.getURL() != null) {
      if (ret.startsWith("/")) {
	ret = d.getURL() + ret;
      }
    }
//System.out.println("ret2 = " + ret);

    return ret;
  }

public void setBackgroundColor(String color){
	setAttribute("bgcolor",color);
}

public void setTextColor(String color){
	setAttribute("text",color);
}

public void setAlinkColor(String color){
	setAttribute("alink",color);
}

public void setVlinkColor(String color){
	setAttribute("vlink",color);
}

public void setLinkColor(String color){
	setAttribute("link",color);
}

public void setMarginWidth(int width){
	setAttribute("marginwidth",Integer.toString(width));
}

public void setMarginHeight(int height){
	setAttribute("marginheight",Integer.toString(height));
}

public void setLeftMargin(int leftmargin){
	setAttribute("leftmargin",Integer.toString(leftmargin));
}

public void setTopMargin(int topmargin){
	setAttribute("topmargin",Integer.toString(topmargin));
}

public void setTitle(String title){
	this.title=title;
}

public String getTitle(){
	return this.title;
}

public String getName(){
	return this.getTitle();
}

public int getWindowWidth(){
	return this.width;
}

public String getWidth(){
  return String.valueOf(width);
}

public void setWidth(int width){
  this.width=width;
}

/**
 * This method overrides the one in PresentationObject and width must be and integer
 * @param width the int width
 */
public void setWidth(String width){
  this.width=Integer.parseInt(width);
}

public int getWindowHeight(){
	return this.height;
}

public String getHeight(){
  return String.valueOf(height);
}


public void setHeight(int height){
	this.height=height;
}

/**
 * This method overrides the one in PresentationObject and height must be and integer
 * @param height the int height
 */
public void setHeight(String height){
  this.height=Integer.parseInt(height);
}

/*
public String getUrl(){
	return this.url;
}
*/

public void setURL(String url){
	this.url=url;
	//newURL=true;
}

/*public void setAssociatedScript(Script myScript){
	theAssociatedScript = myScript;
}

public Script getAssociatedScript(){
	return theAssociatedScript;
}*/

private String returnCheck(boolean checkBool){
	if (checkBool == true){
		return "yes";
	}
	else{
		return "no";
	}
}

private String returnFullScreen() {
  if ( fullscreen == true )
    return ",fullscreen";
  else
    return "";
}

public static String getWindowURL(Class windowClass,IWApplicationContext iwc){
  //String url = IWMainApplication.windowOpenerURL;
  return iwc.getApplication().getWindowOpenerURI(windowClass);
}

public static String getCallingScriptString(Class windowClass,IWApplicationContext iwac){
  return getCallingScriptString(windowClass,true,iwac);
}

public static Window getStaticInstance(Class windowClass){
  Window windowInstance = (Window)allOpenedWindowClasses.get(windowClass);
  if(windowInstance==null){
    try{
      windowInstance = (Window)windowClass.newInstance();
      allOpenedWindowClasses.put(windowClass,windowInstance);
    }
    catch(Exception e){

    }
  }
  return windowInstance;
}

public static String getCallingScriptString(Class windowClass,boolean includeURL,IWApplicationContext iwac){
  String url = getWindowURL(windowClass,iwac);
  return getCallingScriptString(windowClass,url,includeURL,iwac);
}


public static String getCallingScriptString(Class windowClass,String url,boolean includeURL,IWApplicationContext iwac){
  String theURL=null;
  Window win = getStaticInstance(windowClass);
  if(includeURL){
    theURL=url;
  }
  else{
    theURL="";
  }
  if(win==null){
      //return "window.open('"+theURL+"','tempwindow','resizable=yes,toolbar=yes,location=no,directories=no,status=yes,scrollbars=yes,menubar=yes,titlebar=yes,width=500,height=500')";
      return getWindowCallingScript(theURL,"tempwindow",true,true,true,true,true,true,true,true,false,500,500);
  }
  //return "window.open('"+theURL+"','"+win.getTarget()+"','resizable="+win.returnCheck(windowInstance.resizable)+",toolbar="+win.returnCheck(windowInstance.toolbar)+",location="+win.returnCheck(win.location)+",directories="+win.returnCheck(win.directories)+",status="+win.returnCheck(win.status)+",scrollbars="+win.returnCheck(win.scrollbar)+",menubar="+win.returnCheck(win.menubar)+",titlebar="+win.returnCheck(win.titlebar)+win.returnFullScreen()+",width="+win.getWidth()+",height="+win.getHeight()+"')";
  return getWindowCallingScript(theURL,win.getTarget(),win.toolbar,win.location,win.directories,win.status,win.menubar,win.titlebar,win.scrollbar,win.resizable,win.fullscreen,win.getWindowWidth(),win.getWindowHeight());
}

public static String getCallingScript(String URL, int width, int height) {
	return getWindowCallingScript(URL,"Window",true,true,true,true,true,true,true,true,false,width,height);
}

public static String getCallingScript(String URL) {
	return getCallingScript(URL,500,500);
}

public String getCallingScriptString(IWContext iwc,String url){
  //return "window.open('"+url+"','"+getTarget()+"','resizable="+returnCheck(resizable)+",toolbar="+returnCheck(toolbar)+",location="+returnCheck(location)+",directories="+returnCheck(directories)+",status="+returnCheck(status)+",scrollbars="+returnCheck(scrollbar)+",menubar="+returnCheck(menubar)+",titlebar="+returnCheck(titlebar)+returnFullScreen()+",width="+getWidth()+",height="+getHeight()+"')";
  return getWindowCallingScript(url,getTarget(),toolbar,location,directories,status,menubar,titlebar,scrollbar,resizable,fullscreen,getWindowWidth(),getWindowHeight());
}

public String getCallingScriptString(IWContext iwc){
  return getCallingScriptString(iwc,getURL(iwc));
}

protected String getCallingScriptStringForForm(IWContext iwc){
	//return "window.open('"+getURL(iwc)+"','"+getName()+"','resizable="+returnCheck(resizable)+",toolbar="+returnCheck(toolbar)+",location="+returnCheck(location)+",directories="+returnCheck(directories)+",status="+returnCheck(status)+",scrollbars="+returnCheck(scrollbar)+",menubar="+returnCheck(menubar)+",titlebar="+returnCheck(titlebar)+",width="+getWidth()+",height="+getHeight()+"')";
	  /*if (this.getName().equalsIgnoreCase("untitled")){
	    setID();
	    setName(getID());
	  }*/
  //return "window.open('','"+getTarget()+"','resizable="+returnCheck(resizable)+",toolbar="+returnCheck(toolbar)+",location="+returnCheck(location)+",directories="+returnCheck(directories)+",status="+returnCheck(status)+",scrollbars="+returnCheck(scrollbar)+",menubar="+returnCheck(menubar)+",titlebar="+returnCheck(titlebar)+returnFullScreen()+",width="+getWidth()+",height="+getHeight()+"')";
  return getWindowCallingScript("",getTarget(),toolbar,location,directories,status,menubar,titlebar,scrollbar,resizable,fullscreen,getWindowWidth(),getWindowHeight());
}

/**
 *
 * using js function openwindow from global.js
 * (Address,Name,ToolBar,Location,Directories,Status,Menubar,Titlebar,Scrollbars,Resizable,Width,Height)
 */
public static String getWindowCallingScript(String url,String name,boolean tool,
	      boolean loc,boolean dir,boolean stat,boolean menu,boolean title
	      ,boolean scroll,boolean resize,boolean fullscr,int theWidth,int theHeight ){

  String no = "0";
  String yes = "1";
  String sp = "'";
  StringBuffer buf = new StringBuffer("openwindow('").append(url).append("','").append(name).append("',");
  buf.append(sp).append(tool?yes:no).append("','").append(loc?yes:no).append("',");
  buf.append(sp).append(dir?yes:no).append("','").append(stat?yes:no).append("',");
  buf.append(sp).append(menu?yes:no).append("','").append(title?yes:no).append("',");
  buf.append(sp).append(scroll?yes:no).append("','").append(resize?yes:no).append("',");
  buf.append(sp).append(theWidth).append("','").append(theHeight).append("')");
  return buf.toString();
}

public static String windowScript(){
  StringBuffer js = new StringBuffer();
  js.append("\tfunction openwindow(Address,Name,ToolBar,Location,Directories,Status,Menubar,Titlebar,Scrollbars,Resizable,Width,Height) {  \n");
  js.append("\t\t// usage openwindow(addr,name,yes/no,yes/no,yes/no,yes/no,yes/no,yes/no,yes/no,yes/no,width,height) \n");

	js.append("\t\tvar option = \"toolbar=\" + ToolBar ");
	js.append("+ \",location=\" + Location  ");
	js.append("+ \",directories=\" + Directories  ");
	js.append("+ \",status=\" + Status  ");
	js.append("+ \",menubar=\" + Menubar  ");
	js.append("+ \",titlebar=\" + Titlebar  ");
	js.append("+ \",scrollbars=\" + Scrollbars  ");
	js.append("+ \",resizable=\"  + Resizable  ");
  //js.append("+ \",fullscreen=\"  + FullScreen  \n");
	js.append("+ \",width=\" + Width  ");
	js.append("+ \",height=\" + Height; \n");

	js.append("\t\tvar new_win = window.open(Address, Name, option );\n");
  //js.append("new_win.document.write(option)");
  js.append("\t}");
  return js.toString();
}



public void setBackgroundImage(String imageURL){
	setAttribute("background",imageURL);
}

public void setBackgroundImage(Image backgroundImage){
	setBackgroundImage(backgroundImage.getURL());
}

public boolean doPrint(IWContext iwc){
	boolean returnBoole;
	if (iwc.getParameter("idegaspecialrequesttype") == null){
	/*no special request*/
		/*Check if there is a parent object*/
		if (getParentObject() == null){
		/*if there is no parent object then do print directly out*/
			returnBoole = true;
		}
		else{
		/*if there is a parent object then do not print directly out*/
			returnBoole = false;
		}
	}
	else if (iwc.getParameter("idegaspecialrequesttype").equals("window") && iwc.getParameter("idegaspecialrequestname").equals(this.getName()) ){
		returnBoole = true;
	}
	else{
		returnBoole = false;
	}

	return returnBoole;
}


public String getTarget(){
  return getID();
}



  public Object clone() {
    Window obj = null;
    try {
      obj = (Window)super.clone();
      obj.title = this.title;
      obj.width = this.width;
      obj.height = this.height;
      obj.url = this.url;
      obj.classToInstanciate=this.classToInstanciate;
      obj.templateForObjectInstanciation=this.templateForObjectInstanciation;
      obj.templatePageClass=this.templatePageClass;
      obj.toolbar = this.toolbar;
      obj.location = this.location;
      obj.scrollbar = this.scrollbar;
      obj.directories = this.directories;
      obj.menubar = this.menubar;
      obj.status = this.status;
      obj.titlebar = this.titlebar;
      obj.resizable = this.resizable;
    }
    catch(Exception ex) {
      ex.printStackTrace(System.err);
    }

    return obj;
  }

  public void setClassToInstanciate(Class presentationObjectClass){
    this.classToInstanciate=presentationObjectClass;
    this.setURL(IWContext.getInstance().getApplication().getObjectInstanciatorURI(presentationObjectClass));
  }

  public void setClassToInstanciate(Class presentationObjectClass,Class pageTemplateClass){
    setClassToInstanciate(presentationObjectClass);
    this.templatePageClass=pageTemplateClass;
    this.setURL(IWContext.getInstance().getApplication().getObjectInstanciatorURI(presentationObjectClass,pageTemplateClass));
  }

  public void setClassToInstanciate(Class presentationObjectClass,String template){
    setClassToInstanciate(presentationObjectClass);
    this.templateForObjectInstanciation=template;
    this.setURL(IWContext.getInstance().getApplication().getObjectInstanciatorURI(presentationObjectClass,template));
  }


/*
public void print(IWContext iwc)throws IOException{

	if ( doPrint(iwc) ){
		if (! isAttributeSet("bgcolor")){
			setBackgroundColor(iwc.getDefaultPrimaryInterfaceColor());
		}

		if (getLanguage().equals("HTML")){

			//if (getInterfaceStyle().equals(" something ")){
			//}
			//else{
			if (this.url == null){
				println("<!DOCTYPE HTML PUBLIC \"-//W3C//DTD HTML 4.0 Transitional//EN\" \"http://www.w3.org/TR/REC-html40/loose.dtd\">\n<html>");
				println("\n<head>");
				if ( getAssociatedScript() != null){
					getAssociatedScript()._print(iwc);
				}
				println("\n<meta http-equiv=\"Content-Type\" content=\"text/html; charset=iso-8859-1\">\n<META NAME=\"generator\" CONTENT=\"idega arachnea\">\n");
				println("<title>"+getTitle()+"</title>");
				println("</head>\n<body  "+getAttributeString()+" >\n");
				super.print(iwc);
				println("\n</body>\n</html>");
			}
			//}
		}
	}
}*/


	/* (non-Javadoc)
	 * @see com.idega.presentation.PresentationObject#initInMain(com.idega.presentation.IWContext)
	 */
	protected void initInMain(IWContext iwc) throws Exception {
		
		if( !isChildOfOtherPage() && !isInFrameSet()) {
			setOnLoad("focus()");
		} 

		super.initInMain(iwc);
	}

}//End class
