/*
 * $Id: Link.java,v 1.49 2001/10/02 19:40:20 tryggvil Exp $
 *
 * Copyright (C) 2001 Idega hf. All Rights Reserved.
 *
 * This software is the proprietary information of Idega hf.
 * Use is subject to license terms.
 *
 */
package com.idega.jmodule.object.textObject;

import java.util.List;
import java.util.Iterator;
import com.idega.jmodule.object.ModuleObject;
import com.idega.jmodule.object.ModuleInfo;
import com.idega.jmodule.object.Page;
import com.idega.jmodule.object.interfaceobject.Window;
import com.idega.jmodule.object.interfaceobject.Form;
import com.idega.jmodule.object.interfaceobject.Parameter;
import com.idega.idegaweb.IWMainApplication;
import com.idega.idegaweb.IWConstants;
import com.idega.event.IWLinkEvent;
import com.idega.event.IWLinkListener;
import com.idega.builder.data.IBPage;
import com.idega.core.data.ICFile;
import com.idega.jmodule.object.Image;

/**
 *@author <a href="mailto:tryggvi@idega.is">Tryggvi Larusson</a>
 *@version 1.2
 *@modified by  <a href="mailto:eiki@idega.is">Eirikur Hrafnsson</a>
 */
public class Link extends Text {
  private ModuleObject _obj;
  private String _objectType;
  private Window _myWindow;
  private StringBuffer _parameterString;
  private boolean _addSessionId = true;
  private static String _sessionStorageName = IWMainApplication.windowOpenerParameter;
  private Form _formToSubmit;
  private Class _windowClass = null;
  private boolean _maintainAllGlobalParameters = false;
  private String displayString;
  private boolean hasClass = false;

  private static final String HASH = "#";
  private static final String JAVASCRIPT = "javascript:";
  private static final String TARGET_ATTRIBUTE = "target";
  private static final String HREF_ATTRIBUTE = "href";

  private static final String OBJECT_TYPE_WINDOW = "Window";
  private static final String OBJECT_TYPE_MODULEOBJECT="ModuleObject";
  private static final String OBJECT_TYPE_TEXT = "Text";

  public static final String TARGET_NEW_WINDOW = "_new";
  public static final String TARGET_SELF_WINDOW = "_self";
  public static final String TARGET_BLANK_WINDOW = "_blank";
  public static final String TARGET_PARENT_WINDOW = "_parent";
  public static final String TARGET_TOP_WINDOW = "_top";

  /**
   *
   */
  public Link() {
    this("");
  }

  /**
   *
   */
  public Link(String text) {
    this( new Text(text) );
    displayString = text;
  }

  /**
   *
   */
  public Link(ModuleObject mo, Window myWindow) {
    _myWindow = myWindow;
    _myWindow.setParentObject(this);
    _objectType = OBJECT_TYPE_WINDOW;
    _obj = mo;
    _obj.setParentObject(this);
  }

  /**
   *
   */
  public Link(Window myWindow) {
    this(new Text(myWindow.getName()),myWindow);
  }

  /**
   *
   */
  public Link(ModuleObject mo) {
    _obj = mo;
    _obj.setParentObject(this);
    _objectType = OBJECT_TYPE_MODULEOBJECT;
  }

  /**
   *
   */
  public Link(Text text) {
    text.setFontColor("");
    _obj = (ModuleObject)text;
    _obj.setParentObject(this);
    _objectType = OBJECT_TYPE_TEXT;
  }

  /**
   *
   */
  public Link(String text, String url) {
    this(new Text(text),url);
  }

  /**
   *
   */
  public Link(ModuleObject mo, String url) {
    _obj = mo;
    setURL(url);
    _obj.setParentObject(this);
    _objectType = OBJECT_TYPE_MODULEOBJECT;
  }

  /**
   *
   */
  public Link(Text text, String url) {
    text.setFontColor("");
    _obj = (ModuleObject)text;
    setURL(url);
    _obj.setParentObject(this);
    _objectType = OBJECT_TYPE_MODULEOBJECT;
  }

  /**
   * For files
   * @deprecated replaced with com.idega.jmodule.object.interfaceobject.FilePresentation
   */
  public Link(int file_id) {
    this(new Text("File"),com.idega.idegaweb.IWMainApplication.MEDIA_SERVLET_URL+"?file_id="+file_id);
  }

  /**
   * @deprecated replaced with com.idega.jmodule.object.interfaceobject.FilePresentation
   */
  public Link(int file_id, String file_name) {
    this(new Text(file_name),com.idega.idegaweb.IWMainApplication.MEDIA_SERVLET_URL+"?file_id="+file_id);
  }

  /**
   * @deprecated replaced with com.idega.jmodule.object.interfaceobject.FilePresentation
   */
  public Link(ModuleObject mo, int file_id) {
    super();
    _obj = mo;
    setURL(com.idega.idegaweb.IWMainApplication.MEDIA_SERVLET_URL+"?file_id="+file_id);
    _obj.setParentObject(this);
    _objectType = OBJECT_TYPE_MODULEOBJECT;
  }

  /**
   * @deprecated replaced with com.idega.jmodule.object.interfaceobject.FilePresentation
   */
  public Link(int file_id, Window myWindow) {
    _myWindow = myWindow;
    _myWindow.setParentObject(this);
    _objectType = OBJECT_TYPE_WINDOW;
  }

  /**
   *
   */
  public Link(ModuleObject mo, Class classToInstanciate) {
    this(mo,IWMainApplication.getObjectInstanciatorURL(classToInstanciate));
    //to avoid double ?
    _parameterString = new StringBuffer();

  }

  /**
   *
   */
  public Link(ModuleObject mo, String classToInstanciate, String template) {
    this(mo,IWMainApplication.getObjectInstanciatorURL(classToInstanciate,template));
        //to avoid double ?
    _parameterString = new StringBuffer();
  }

  /**
   *
   */
  public Link(String displayText, Class classToInstanciate) {
    this(displayText,IWMainApplication.getObjectInstanciatorURL(classToInstanciate));
        //to avoid double ?
    _parameterString = new StringBuffer();
  }

  /**
   *
   */
  public Link(String displayText, Class classToInstanciate, String target) {
    this(displayText,IWMainApplication.getObjectInstanciatorURL(classToInstanciate));
        //to avoid double ?
    _parameterString = new StringBuffer();
    setTarget(target);
  }

  /**
   *
   */
  public Link(String displayText, String classToInstanciate, String template) {
    this(displayText,IWMainApplication.getObjectInstanciatorURL(classToInstanciate,template));
        //to avoid double ?
    _parameterString = new StringBuffer();
  }

  /**
   *
   */
  public void setWindow(Window window) {
    _myWindow = window;
    _objectType = OBJECT_TYPE_WINDOW;
    _myWindow.setParentObject(this);
  }

  /**
   *
   */
  public void setModuleObject(ModuleObject object) {
    _obj = object;
    _objectType = OBJECT_TYPE_MODULEOBJECT;
    object.setParentObject(this);
  }

  /**
   *
   */
  public void main(ModuleInfo modinfo)throws Exception {
    if (_objectType==(OBJECT_TYPE_WINDOW)) {
      if (_myWindow != null) {
        if (_myWindow.getURL(modinfo).indexOf(IWMainApplication.windowOpenerURL) != -1) {
          String sessionParameterName = com.idega.servlet.WindowOpener.storeWindow(modinfo,_myWindow);
          addParameter(_sessionStorageName,sessionParameterName);
        }
      }
    }
    if (_obj != null) {
      _obj.main(modinfo);
    }
  }

  /**
   *
   */
  public void setURL(String url) {
    setAttribute(HREF_ATTRIBUTE,url);
  }

  /**
   *
   */
  public String getURL() {
    return(getAttribute(HREF_ATTRIBUTE));
  }

  /**
   *
   */
  public void addParameter(Parameter parameter) {
    addParameter(parameter.getName(),parameter.getValue());
  }

  /**
   *
   */
  public void addParameter(String parameterName, String parameterValue) {
    if ((parameterName != null) && (parameterValue != null)) {
      parameterName = java.net.URLEncoder.encode(parameterName);
      parameterValue = java.net.URLEncoder.encode(parameterValue);

      if (_parameterString == null) {
        _parameterString = new StringBuffer();
        _parameterString.append("?");
      }
      else  {
        _parameterString.append("&");
      }

      _parameterString.append(parameterName);
      _parameterString.append("=");
      _parameterString.append(parameterValue);
    }
    else if (parameterName != null) {
      parameterName = java.net.URLEncoder.encode(parameterName);
    }
    else if (parameterValue != null) {
      parameterValue = java.net.URLEncoder.encode(parameterValue);
    }
  }

  /**
   *
   */
  public void addParameter(String parameterName, int parameterValue) {
    addParameter(parameterName,Integer.toString(parameterValue));
  }

  /**
   *
   */
  public void maintainParameter(String parameterName, ModuleInfo modinfo) {
    String parameterValue = modinfo.getParameter(parameterName);
    if (parameterValue != null) {
      addParameter(parameterName,parameterValue);
    }
  }

  /*
   *
   */
  private void setOnEvent(String eventType, String eventString) {
    setAttribute(eventType,eventString);
  }

  /**
   *
   */
  public void setOnFocus(String s) {
    setOnEvent("onfocus",s);
  }

  /**
   *
   */
  public void setOnBlur(String s) {
    setOnEvent("onblur",s);
  }

  /**
   *
   */
  public void setOnMouseOver(String s) {
    setOnEvent("onmouseover",s);
  }

  /**
   *
   */
  public void setOnMouseOut(String s) {
    setOnEvent("onmouseout",s);
  }

  /**
   *
   */
  public void setOnSelect(String s) {
    setOnEvent("onselect",s);
  }

  /**
   *
   */
  public void setOnChange(String s) {
    setOnEvent("onchange",s);
  }

  /**
   *
   */
  public void setOnClick(String s) {
    setOnEvent("onclick",s);
  }

  /**
   *
   */
  public String getOnFocus() {
    return getAttribute("onfocus");
  }

  /**
   *
   */
  public String getOnBlur() {
    return getAttribute("onblur");
  }

  /**
   *
   */
  public String getOnSelect() {
    return getAttribute("onselect");
  }

  /**
   *
   */
  public String getOnChange() {
    return getAttribute("onchange");
  }

  /**
   *
   */
  public String getOnClick() {
    return getAttribute("onclick");
  }

  /**
   *
   */
  public void setTarget(String target) {
    setAttribute(TARGET_ATTRIBUTE,target);
  }

  /**
   *
   */
  public String getTarget() {
    return getAttribute(TARGET_ATTRIBUTE);
  }

  /**
   *
   */
  public void setFontSize(String s) {
    if (_objectType==(OBJECT_TYPE_TEXT)) {
      ((Text)_obj).setFontSize(s);
    }
  }

  /**
   *
   */
  public void setFontSize(int i) {
    setFontSize(Integer.toString(i));
  }

  /**
   *
   */
  public void setFontFace(String s) {
    if (_objectType==(OBJECT_TYPE_TEXT)) {
      ((Text)_obj).setFontFace(s);
    }
  }

  /**
   *
   */
  public void setFontColor(String color) {
    if (_objectType==(OBJECT_TYPE_TEXT)) {
      ((Text)_obj).setFontColor(color);
    }
  }

  /**
   *
   */
  public void setFontStyle(String style) {
    if (_objectType==(OBJECT_TYPE_TEXT)) {
      ((Text)_obj).setFontStyle(style);
    }
  }

  /**
   *
   */
  public void setFontClass(String styleClass) {
    if (_objectType==(OBJECT_TYPE_TEXT)) {
      ((Text)_obj).setFontClass(styleClass);
    }
  }

  public void setStyle(String style) {
    super.setStyle(style);
    this.hasClass = true;
  }

  /**
   *
   */
  public void setSessionId(boolean addSessionId) {
    _addSessionId = addSessionId;
  }

  /**
   *
   */
  public void addBreak() {
    if (_objectType==(OBJECT_TYPE_TEXT)) {
	    ((Text)_obj).addBreak();
    }
  }

  /**
   *
   */
  public void setTeleType() {
    if (_objectType==(OBJECT_TYPE_TEXT)) {
	    ((Text)_obj).setTeleType();
    }
  }

  /**
   *
   */
  public void setBold() {
    if (_objectType==(OBJECT_TYPE_TEXT)) {
    	((Text)_obj).setBold();
    }
  }

  /**
   *
   */
  public void setItalic() {
    if (_objectType==(OBJECT_TYPE_TEXT)) {
	    ((Text)_obj).setItalic();
    }
  }

  /**
   *
   */
  public void setUnderline() {
    if (_objectType==(OBJECT_TYPE_TEXT)) {
	    ((Text)_obj).setUnderline();
    }
  }

  /**
   *
   */
  public void setText(String text) {
    if (_objectType==(OBJECT_TYPE_TEXT)) {
      ((Text)_obj).setText(text);
    }
  }

  /**
   *
   */
  public void addToText(String text) {
    if (_objectType==(OBJECT_TYPE_TEXT)) {
      ((Text)_obj).addToText(text);
    }
  }

  /**
   *
   */
  public void setTextOnLink(String text) {
    setText(text);
  }

  /**
   *
   */
  public void setObject(ModuleObject object) {
    _obj = object;
    _objectType = OBJECT_TYPE_MODULEOBJECT;

        _obj.setParentObject(this);
  }

  /**
   * method for adding an image to the link
   */
  public void setImage(Image image) {
    _obj = image;
    _objectType = OBJECT_TYPE_MODULEOBJECT;

        _obj.setParentObject(this);
  }


  /**
   * method for adding a link to a page object
   */
  public void setPage(IBPage page) {
    if( (page!=null) && (page.getID()!=-1) ){
      StringBuffer url = new StringBuffer();
      url.append(IWMainApplication.BUILDER_SERVLET_URL);
      url.append('?');
      url.append(com.idega.builder.business.BuilderLogic.IB_PAGE_PARAMETER);
      url.append('=');
      url.append(page.getID());
      setURL(url.toString());
    }
  }

  public void setPage(int pageID) {
    IBPage page = null;
    try {
      page = new IBPage(pageID);
    }
    catch (Exception e) {
      page = null;
    }
    setPage(page);
  }

  /**
   * method for adding a link to a file object
   */
  public void setFile(ICFile file) {
    if( (file!=null) && (file.getID()!=-1) ){
      StringBuffer url = new StringBuffer();
      url.append(IWMainApplication.MEDIA_SERVLET_URL);
      url.append('/');
      url.append(file.getID());
      url.append("media");
      url.append('?');
      url.append(com.idega.block.media.servlet.MediaServlet.PARAMETER_NAME);
      url.append('=');
      url.append(file.getID());
      setURL(url.toString());
    }
  }

  public void setFile(int fileID) {
    ICFile file = null;
    try {
      file = new ICFile(fileID);
    }
    catch (Exception e) {
      file = null;
    }
    setFile(file);
  }



  /**
   *
   */
  public ModuleObject getObject() {
    return(_obj);
  }

  /*
   *
   */
  private boolean isLinkOpeningOnSamePage() {
    return(!isAttributeSet(TARGET_ATTRIBUTE));
  }

  /**
   *
   */
  public synchronized Object clone() {
    Link linkObj = null;
    try {
      linkObj = (Link)super.clone();

      if (_obj != null) {
        linkObj._obj = (ModuleObject)_obj.clone();
      }
      if (_myWindow != null) {
        linkObj._myWindow = (Window)_myWindow.clone();
      }

      if (_formToSubmit != null) {
        linkObj._formToSubmit = (Form)_formToSubmit.clone();
      }

      if (_windowClass != null) {
        linkObj._windowClass = _windowClass;
      }

      linkObj._objectType = _objectType;
      linkObj._parameterString = _parameterString;
      linkObj._addSessionId = _addSessionId;
      linkObj._maintainAllGlobalParameters = _maintainAllGlobalParameters;

      if (_parameterString != null) {
        linkObj._parameterString = new StringBuffer(_parameterString.toString());
      }
    }
    catch(Exception ex) {
      ex.printStackTrace(System.err);
    }

    return(linkObj);
  }

  /*
   *
   */
  private void addTheMaintainedParameters(ModuleInfo modinfo) {
    List list = com.idega.idegaweb.IWURL.getGloballyMaintainedParameters(modinfo);
    if (list != null) {
      Iterator iter = list.iterator();
      while(iter.hasNext()) {
        String parameterName = (String)iter.next();
        String parameterValue = modinfo.getParameter(parameterName);
        if (parameterValue != null) {
          addParameter(parameterName,parameterValue);
        }
      }
    }
  }

  /**
   *
   */
  public void setToMaintainGlobalParameters() {
    _maintainAllGlobalParameters = true;
  }

  /**
   *
   */
  protected String getParameterString(ModuleInfo modinfo, String URL) {
    if (_maintainAllGlobalParameters) {
      addTheMaintainedParameters(modinfo);
    }
    else {
      if (isLinkOpeningOnSamePage()) {
        addTheMaintainedParameters(modinfo);
      }
    }

    if (URL == null) {
      URL = "";
    }

	  if (_parameterString == null) {
      _parameterString = new StringBuffer();
      if (_addSessionId && (!modinfo.isSearchEngine())) {
    		if (URL.equals("#")) {
          return("");
        }
        else if (URL.indexOf("://") == -1) { //does not include ://
          if (URL.indexOf("?") != -1) {
            _parameterString.append("&idega_session_id=");
            _parameterString.append(modinfo.getSession().getId());
            return(_parameterString.toString());
          }
          else if ((URL.indexOf("//") != -1) && (URL.lastIndexOf("/") == URL.lastIndexOf("//") + 1 )) {
            //the case where the URL is etc. http://www.idega.is
            _parameterString.append("/?idega_session_id=");
            _parameterString.append(modinfo.getSession().getId());
            return(_parameterString.toString());
          }
          else {
            if (URL.indexOf("/") != -1) {
              //If the URL ends with a "/"
              if (URL.lastIndexOf("/") == (URL.length()-1)) {
                _parameterString.append("?idega_session_id=");
                _parameterString.append(modinfo.getSession().getId());
                return(_parameterString.toString());
              }
              else {
                //There is a dot after the last "/" interpreted as a file not a directory
                if (URL.lastIndexOf(".") > URL.lastIndexOf("/")) {
                  _parameterString.append("?idega_session_id=");
                  _parameterString.append(modinfo.getSession().getId());
                  return(_parameterString.toString());
                }
                else {
                  _parameterString.append("/?idega_session_id=");
                  _parameterString.append(modinfo.getSession().getId());
                  return(_parameterString.toString());
                }
              }
            }
            else {
              _parameterString.append("?idega_session_id=");
              _parameterString.append(modinfo.getSession().getId());
              return(_parameterString.toString());
            }
          }
		    }
		    else {
          /**
           * @todo Temporary solution??? :// in link then no idega_session_id
           */
		      return("");
		    }
      }
      else {
        return("");
      }
	  }
	  else {
      /**
       * @todo Temporary solution??? :// in link then no idega_session_id
       */
      if (URL.indexOf("?") == -1) {
        if (_addSessionId && (!modinfo.isSearchEngine())) {
          if ( _parameterString.toString().indexOf("?") == -1) {
            _parameterString.insert(0,'?');
          }
           _parameterString.append("&");

          if (URL.indexOf("://") == -1) {
            _parameterString.append("idega_session_id=");
            _parameterString.append(modinfo.getSession().getId());
          }
        }
      }
      else {
        if (_addSessionId && (!modinfo.isSearchEngine())) {
          _parameterString.append("&");
          if (URL.indexOf("://") == -1) {
            _parameterString.append("idega_session_id=");
            _parameterString.append(modinfo.getSession().getId());
          }
        }
      }

      return(_parameterString.toString());
	  }
  }

  /**
   *
   */
  public void clearParameters() {
    _parameterString = null;
  }

  /**
   *
   */
  public void print(ModuleInfo modinfo) throws Exception {
	  initVariables(modinfo);
    boolean addParameters = true;
    String oldURL = getURL();

    if (oldURL == null) {
      oldURL = modinfo.getRequestURI();
      setURL(oldURL);
    }
    else if (oldURL.equals(com.idega.util.StringHandler.EMPTY_STRING)) {
      oldURL = modinfo.getRequestURI();
      setURL(oldURL);
    }

    if (oldURL.equals(HASH)) {
      addParameters = false;
    }
    else if(oldURL.startsWith(JAVASCRIPT)) {
      addParameters = false;
    }

    if (getLanguage().equals("HTML")) {
      if (_objectType==(OBJECT_TYPE_WINDOW)) {
        if (_windowClass == null) {
          setOnClick(_myWindow.getCallingScriptString(modinfo,_myWindow.getURL(modinfo)+getParameterString(modinfo,_myWindow.getURL(modinfo))));
        }
        else {
          setOnClick(Window.getCallingScriptString(_windowClass,getURL()+getParameterString(modinfo,getURL()),true));
        }
        setURL(HASH);
        print("<a "+getAttributeString()+" >");
        if (_obj == null) {
          Text myText = new Text(_myWindow.getName());
          myText.print(modinfo);
        }
        else {
          if (_objectType==OBJECT_TYPE_TEXT) {
            if ( hasClass ) {
              if ( displayString != null ) {
                print(displayString);
              }
              else {
                if ( ((Text)_obj).getText() != null ) {
                  print(((Text)_obj).getText());
                }
              }
            }
            else {
              _obj.print(modinfo);
            }
          }
          else {
            _obj.print(modinfo);
          }
        }
        print("</a>");
			}
      else {
        if (addParameters) {
          setURL(oldURL+getParameterString(modinfo,oldURL));
        }
        print("<a "+getAttributeString()+" >");
        if (_objectType==OBJECT_TYPE_TEXT) {
          if ( hasClass ) {
            if ( displayString != null ) {
              print(displayString);
            }
            else {
              if ( ((Text)_obj).getText() != null ) {
                print(((Text)_obj).getText());
              }
            }
          }
          else {
            _obj.print(modinfo);
          }
        }
        else {
          _obj.print(modinfo);
        }
        print("</a>");
      }
		}
		else if (getLanguage().equals("WML")) {
      if (_objectType.equals(OBJECT_TYPE_WINDOW)) {
        setURL(_myWindow.getURL(modinfo)+getParameterString(modinfo,oldURL));
        setURL(HASH);
        print("<a "+getAttributeString()+" >");
        print(_myWindow.getName());
        print("</a>");
      }
      else {
        if (addParameters) {
          setURL(oldURL+getParameterString(modinfo,oldURL));
        }
        print("<a "+getAttributeString()+" >");
        _obj.print(modinfo);
        print("</a>");
      }
		}
    /**
     * @todo !!Find out why this is necessary:
     */
    setURL(oldURL);
  }

  /**
   *
   */
  public void addIWLinkListener(IWLinkListener l, ModuleInfo modinfo) {
    if (!listenerAdded()) {
      postIWLinkEvent(modinfo);
    }
    super.addIWLinkListener(l,modinfo);
  }


  /*
   *
   */
  private void postIWLinkEvent(ModuleInfo modinfo) {
    eventLocationString = getID();
    IWLinkEvent event = new IWLinkEvent(this,IWLinkEvent.LINK_ACTION_PERFORMED);
    if (_formToSubmit == null) {
      addParameter(sessionEventStorageName,eventLocationString);
    }
    modinfo.setSessionAttribute(eventLocationString,event);
    listenerAdded(true);
  }

  /**
   *
   */
  public void setToFormSubmit(Form form) {
    setToFormSubmit(form,false);
  }

  /**
   *
   */
  public void setToFormSubmit(Form form, boolean useEvent) {
    _formToSubmit = form;
    setURL(HASH);
    if ((getIWLinkListeners() != null && getIWLinkListeners().length != 0) || useEvent) {
       setOnClick("javascript:document."+form.getID()+"."+IWMainApplication.IWEventSessionAddressParameter+".value=this.id ;document."+form.getID()+".submit()");
    }
    else {
      setOnClick("javascript:document."+form.getID()+".submit()");
    }
  }

  /**
   *
   */
  public void setAsBackLink(int backUpHowManyPages) {
    setOnClick("history.go(-"+backUpHowManyPages+")");
    setURL(HASH);
  }

  /**
   *
   */
  public void setAsBackLink() {
    setAsBackLink(1);
  }

  public void setNoURL(){
    setURL(HASH);
  }

  /**
   *
   */
  public void setProperty(String key, String values[]) {
    if (key.equalsIgnoreCase("text")) {
      setText(values[0]);
    }
    else if (key.equalsIgnoreCase("url")) {
      setURL(values[0]);
    }
  }

  /**
   *
   */
  public void setEventListener(Class eventListenerClass) {
    setEventListener(eventListenerClass.getName());
  }

  /**
   *
   */
  public void setEventListener(String eventListenerClassName) {
    addParameter(IWMainApplication.IdegaEventListenerClassParameter,IWMainApplication.getEncryptedClassName(eventListenerClassName));
  }

  /**
   *
   */
  public void sendToControllerFrame() {
    setTarget(IWConstants.IW_CONTROLLER_FRAME_NAME);
  }

  /**
   *
   */
  public void setWindowToOpen(Class windowClass) {
    _objectType=OBJECT_TYPE_WINDOW;
    _windowClass=windowClass;
    setURL(IWMainApplication.windowOpenerURL);
    addParameter(Page.IW_FRAME_CLASS_PARAMETER,windowClass.getName());
  }

  public void setNoTextObject(boolean noText) {
    if ( _objectType == this.OBJECT_TYPE_TEXT )
      hasClass = noText;
  }
}

