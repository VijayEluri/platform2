/*
 * $Id: PresentationObject.java,v 1.71 2003/09/17 09:06:42 palli Exp $
 *
 * Copyright (C) 2001 Idega hf. All Rights Reserved.
 *
 * This software is the proprietary information of Idega hf.
 * Use is subject to license terms.
 *
 */
package com.idega.presentation;

import java.io.IOException;
import java.io.PrintWriter;
import java.rmi.RemoteException;
import java.sql.Connection;
import java.sql.SQLException;
import java.util.Hashtable;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.StringTokenizer;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.swing.event.EventListenerList;

import com.idega.business.GenericState;
import com.idega.business.IBOLookup;
import com.idega.core.builder.business.BuilderConstants;
import com.idega.core.builder.business.BuilderService;
import com.idega.core.builder.business.BuilderServiceFactory;
import com.idega.core.data.ICObject;
import com.idega.core.data.ICObjectHome;
import com.idega.core.data.ICObjectInstance;
import com.idega.core.data.ICObjectInstanceHome;
import com.idega.core.file.business.ICFileSystem;
import com.idega.core.file.business.ICFileSystemFactory;
import com.idega.event.IWActionListener;
import com.idega.event.IWEvent;
import com.idega.event.IWEventMachine;
import com.idega.event.IWLinkEvent;
import com.idega.event.IWLinkListener;
import com.idega.event.IWPresentationState;
import com.idega.event.IWStateMachine;
import com.idega.event.IWSubmitEvent;
import com.idega.event.IWSubmitListener;
import com.idega.exception.ICObjectNotInstalledException;
import com.idega.idegaweb.IWApplicationContext;
import com.idega.idegaweb.IWBundle;
import com.idega.idegaweb.IWConstants;
import com.idega.idegaweb.IWException;
import com.idega.idegaweb.IWLocation;
import com.idega.idegaweb.IWMainApplication;
import com.idega.idegaweb.IWMainApplicationSettings;
import com.idega.idegaweb.IWPresentationLocation;
import com.idega.idegaweb.IWPropertyList;
import com.idega.idegaweb.IWResourceBundle;
import com.idega.idegaweb.IWUserContext;
import com.idega.presentation.ui.Form;
import com.idega.util.database.ConnectionBroker;
import com.idega.util.text.TextStyler;


/**
 * The base class for objects that present themselves to a user on screen in idegaWeb.
 *
 *@author <a href="mailto:tryggvi@idega.is">Tryggvi Larusson</a>
 *@version 1.3
 */
public class PresentationObject extends Object implements Cloneable {
  //private final static String IW_BUNDLE_IDENTIFIER="com.idega.idegaweb";
  public final static String IW_BUNDLE_IDENTIFIER="com.idega.core";
  public final static String WIDTH ="width";
  public final static String HEIGHT ="height";
  public final static String HORIZONTAL_ALIGNMENT ="align";


  protected static final String slash = "/";

  private HttpServletRequest _request;
  private HttpServletResponse _response;
  private PrintWriter out;
  private String interfaceStyle;
  private String language;
  public Hashtable attributes;
  private String name;
  protected PresentationObject parentObject;
  private boolean doPrint = true;
  private String errorMessage;
  protected boolean hasBeenAdded = false;
  protected String treeID;
  private boolean goneThroughMain = false;
  private int ic_object_instance_id;
  private int ic_object_id;
  private static String emptyString = "";
  public static String sessionEventStorageName = IWMainApplication.IWEventSessionAddressParameter;
  /**
   * @deprecated Do not use this function
   */
  public EventListenerList listenerList = null;

  public EventListenerList _listenerList = null;
  private Hashtable eventAttributes = null;
  private static long InstanceUniqueID;
  private String UniqueInstanceName;
  private boolean listenerAdded = false;
  public String eventLocationString = "";
  private IWContext eventIWContext = null;
  public static final PresentationObject NULL_CLONE_OBJECT = new PresentationObject();
  //public static final PresentationObject NULL_CLONE_OBJECT = new com.idega.presentation.text.Text("NULL_OBJECT",true,false,false);

  protected boolean initializedInMain = false;
  private IWApplicationContext _iwac;
  private IWUserContext _iwuc;

  public static final String TARGET_OBJ_INS = "tois";

  private boolean _useBuilderObjectControl = true;
  private boolean _belongsToParent = false;
  private boolean _changeInstanceIDOnInheritance = false;
  private boolean _allowPagePermissionInheritance = true;

  private IWLocation _location = new IWPresentationLocation();

  private GenericState defaultState = null;
  
  // constant for compoundId 
  public static String COMPOUNDID_COMPONENT_DELIMITER = ":";
  // constant for compoundId
  public static String COMPOUNDID_CHILD_NUMBER_DELIMITER = "_";
  // artificial prefix compound_id
  private String artificialCompoundId = null;
  // former compound id (necessary to watch changes of the compoundId)
  private String formerCompoundId = null;

  /**
   * Default constructor
   */
  public PresentationObject() {
  }

  /**
   * @return The parent (subclass of PresentationObjectContainer) of the current object
   */
  public PresentationObject getParentObject(){
    return parentObject;
  }

  public String generateID() {
    int hashCode = hashCode();
    String code;
    if (hashCode < 0) {
      hashCode = -hashCode;
    }
    code = "id"+hashCode;
    return code;
  }

  public void setID(){
   setID(generateID());
  }


  public String getID() {
    String theReturn = getAttribute("id");
    if (theReturn == null || this.emptyString.equals(theReturn)) {
      setID();
      theReturn = getAttribute("id");
    }

    return theReturn;
  }

  public PresentationObject getRootParent() {
    PresentationObject tempobj = getParentObject();
    if (tempobj == null) {
      return null;
    }
    else {
      while (tempobj.getParentObject() != null ) {
	tempobj = tempobj.getParentObject();
      }
      return tempobj;
    }
  }

  public void setParentObject(PresentationObject modobj) {
    parentObject = modobj;
  }

  /**
   * Initializes variables contained in the IWContext object
   */
  public void initVariables(IWContext iwc) throws IOException {
    this._request = iwc.getRequest();
    this._response = iwc.getResponse();
    this.language = iwc.getLanguage();
    this.interfaceStyle = iwc.getInterfaceStyle();
    if (language == null) {
      language = IWConstants.MARKUP_LANGUAGE_HTML;
    }
    if (interfaceStyle == null) {
      interfaceStyle = "default";
    }

  }

  protected void initInMain(IWContext iwc) throws Exception{
    initializeInMain(iwc);
    initializedInMain = true;
  }

  /**
   *
   */
  public void initializeInMain(IWContext iwc) throws Exception{

  }

  public void setDoPrint(boolean ifDoPrint) {
    this.doPrint = ifDoPrint;
  }

  public boolean doPrint(IWContext iwc) {
    if (this.doPrint) {
      PresentationObject parent = getParentObject();
      if (parent == null) {
	return this.doPrint;
      }
      else {
	return parent.doPrint(iwc);
      }
    }
    else {
      return false;
    }
  }

  protected void setAttribute(Hashtable attributes) {
    this.attributes = attributes;
  }

  public void setAttribute(String attributeName,String attributeValue) {
    if (this.attributes == null) {
      this.attributes = new Hashtable();
    }

    this.attributes.put((Object) attributeName,(Object) attributeValue);
  }

  public void removeAttribute(String attributeName){
    if( attributeName!=null ){
      if (this.attributes != null) {
	this.attributes.remove(attributeName);
      }
    }
  }

  public void setAttribute(String attributeName,boolean attributeValue) {
    setAttribute( attributeName, String.valueOf(attributeValue) );
  }

	public void setAttribute(String attributeName,int attributeValue) {
		setAttribute( attributeName, Integer.toString(attributeValue) );
	}


  public void setAttributeMultivalued(String attributeName,String attributeValue){
    String previousAttribute = getAttribute(attributeName);
    if(previousAttribute == null){
      setAttribute(attributeName,attributeValue);
    }
    else{
    	if (previousAttribute.indexOf(attributeValue) == -1) {
    		String parameterValue = previousAttribute;
    		if (previousAttribute.endsWith(";"))
    			parameterValue = parameterValue + attributeValue;
    		else
    			parameterValue = parameterValue + ";" + attributeValue;
    			
      	setAttribute(attributeName,parameterValue);
    	}
    }
  }

  public void setAttributeMultivalued(String attributeName,boolean attributeValue) {
    setAttributeMultivalued( attributeName, String.valueOf(attributeValue) );
  }

  public void setAttribute(String attributeName){
    setAttribute(attributeName,slash);
  }

	public void setStyleClass(String styleName) {
		setAttribute("class",styleName);	
	}
	
	/**
	 * Sets or adds to the style tag of this object
	 * <br><br>Preserves previous set values with this method
	 * */
  public void setStyleAttribute(String style){
  	TextStyler styler = new TextStyler(getStyleAttribute()+style);
  	setAttribute("style",styler.getStyleString());
  }

  public String getStyleAttribute(){
  	if (isAttributeSet("style"))
    	return this.getAttribute("style");
    return "";
  }
  
  public void setToolTip(String toolTip) {
  	setAttribute("title",toolTip);
  }
  
	public String getToolTip() {
  	if (isAttributeSet("title"))
    	return this.getAttribute("title");
    return "";
	}

  /** Copies all of the attribute mappings from the specified map to attributes.
   *  These mappings will replace attibutes that this map had for any of the
   *  keys currently in the specified map.
   */
  public void setAttributes(Map attributeMap){
    if (this.attributes == null) {
      this.attributes = new Hashtable();
    }
    attributes.putAll(attributeMap);
  }

  /**
   *
   */
  public static Map getAttributeMap(String attributeString){
    Hashtable map = new Hashtable();
    if(attributeString !=  null && attributeString.length() > 1){
      StringTokenizer tokens = new StringTokenizer(attributeString),tok;
      while(tokens.hasMoreTokens()){
	String s = tokens.nextToken();//.replace('"',' ');
	tok = new StringTokenizer(s,"=\"");
	if(tok.countTokens() == 2){
	  map.put(tok.nextToken(),tok.nextToken());
	}
      }
    }
    return map;
  }

  public String getAttribute(String attributeName) {
    if (this.attributes != null){
      return (String)this.attributes.get((Object)attributeName);
    }
    else {
      return null;
    }
  }

  public static String getAttribute(String attributeName,Map map){
     if (map != null){
      return (String)map.get((Object)attributeName);
    }
    else {
      return null;
    }
  }

  public boolean isAttributeSet(String attributeName) {
    if (getAttribute(attributeName) == null) {
      return false;
    }
    else {
      return true;
    }
  }

  public Hashtable getAttributes() {
    return this.attributes;
  }

  public static String getAttributeString(Map map){
    StringBuffer returnString = new StringBuffer();
    String Attribute ="";
    String attributeValue = "";
    Map.Entry mapEntry;

    if (map != null) {
      Iterator i = map.entrySet().iterator();
      while (i.hasNext()) {
	mapEntry = (Map.Entry) i.next();
	Attribute = (String) mapEntry.getKey();
	returnString.append(" ");
	returnString.append(Attribute);
	attributeValue = (String) mapEntry.getValue();
	if(!attributeValue.equals(slash)){
	  returnString.append("=\"");
	  returnString.append(attributeValue);
	  returnString.append("\"");
	}
	returnString.append("");
      }
    }
    return returnString.toString();
  }

  public String getAttributeString() {
/*
    StringBuffer returnString = new StringBuffer();
    String Attribute ="";

    if (this.attributes != null) {
      Enumeration e = attributes.keys();
      while (e.hasMoreElements()) {
	Attribute = (String)e.nextElement();
	returnString.append(" ");
	returnString.append(Attribute);
	String attributeValue=getAttribute(Attribute);
	if(!attributeValue.equals(slash)){
	  returnString.append("=\"");
	  returnString.append(attributeValue);
	  returnString.append("\"");
	}
	returnString.append("");
      }
    }

    return returnString.toString();
*/
    return getAttributeString(this.attributes);
  }

  /**
   * Gets the name of this object
   */
  public String getName() {
    return this.name;
  }

  /**
   * Sets the name of this object
   */
  public void setName(String name) {
    this.name = name;
  }

  /**
   * Flushes the buffer in the printwriter out
   */
  public void flush() {
    out.flush();
  }

  /**
   * Uses the default PrintWriter object to print out a string
   */
  public void print(String string) {
    out.print(string);
  }

  /**
   * Uses the default PrintWriter object to print out a string with the endline character
   */
  public void println(String string) {
    out.println(string);
  }

  public void _initPrinting(IWContext iwc) throws Exception {
    //this.out = iwc.getWriter();

    _print(iwc);
  }

  public void _print(IWContext iwc) throws Exception {
    initVariables(iwc);
    this.out = iwc.getWriter();
    print(iwc);
  }

  /**
   * The default implementation for the print function
   *
   * This function is invoked on each request by the user for each PresentationObject instance (after main(iwc)).
   *
   * Override this function where it is needed to print out the specified content.
   * This function should only be overrided in idegaWeb Elements.
   */
  public void print(IWContext iwc) throws Exception {
    if (iwc.getLanguage().equals("WML")) {
      iwc.setContentType("text/vnd.wap.wml");
    }
  }

  /**
   * @deprecated Do not use this function, it is not safe
   * @resturn The Response object for the page
   */
  public HttpServletRequest getRequest() {
    return this._request;
  }

  /**
   * @deprecated Do not use this function, it is not safe
   * @return The Request object for the page
   */
  public HttpServletResponse getResponse() {
    return this._response;
  }

  /**
   * @return The "layout" language used and supplied by the IWContext
   */
  public String getLanguage() {
  	if ( language != null )
	    return this.language;
	  return IWConstants.MARKUP_LANGUAGE_HTML;
  }

  public void setID(String ID) {
    setAttribute("id",ID);
  }

  public void setID(int ID) {
    setAttribute("id",Integer.toString(ID));
  }

  /**
   * @return The interface style supplied by the IWContext (optional)
   */
  public String getInterfaceStyle() {
    return this.interfaceStyle;
  }

  public PrintWriter getPrintWriter() {
    return out;
  }

  /**
   * @return The default DatabaseConnection
   */
  public Connection getConnection() {
    return ConnectionBroker.getConnection();
  }

  public void freeConnection(Connection conn) {
    ConnectionBroker.freeConnection(conn);
  }

  /**
   * @return The Class name of the Object
   */
  public String getClassName() {
    return this.getClass().getName();
  }

  /**
   * Encodes a string to call special request such as pop-up windows in HTML
   */
  public String encodeSpecialRequestString(String RequestType,String RequestName, IWContext iwc) {
    String theOutput = "";
    theOutput = iwc.getRequestURI();
    theOutput = theOutput + "?idegaspecialrequesttype=" + RequestType + "&idegaspecialrequestname=" + RequestName;
    return theOutput;
  }

  /**
   * Sets the associated (attached) script object to this object
   */
  public void setAssociatedScript(Script myScript) {
    if (getRootParent() != null) {
      getRootParent().setAssociatedScript(myScript);
    }
  }

  /**
   * @return The associated (attached) script or null if there is no Script associated
   */
  public Script getAssociatedScript() {
    if (getRootParent() != null) {
      return getRootParent().getAssociatedScript();
    }
    else {
      return null;
    }
  }

  /**
   * @return The enclosing Page object
   */
  public Page getParentPage() {
    Page returnPage = null;
    PresentationObject obj = null;
    if(this instanceof IFrameContent){
      obj = ((IFrameContent)this).getOwnerInstance();
    }else {
      obj = this.getParentObject();
    }
    while (obj != null) {
      if (obj instanceof Page) {
	returnPage = (Page)obj;
      }
      if(obj instanceof IFrameContent){
	obj = ((IFrameContent)obj).getOwnerInstance();
      }else {
	obj = obj.getParentObject();
      }
    }

    if((returnPage == null) && (this instanceof Page)){
      return (Page)this;
    }

    return returnPage;
  }

  public int getParentPageID(){
    Page obj = getParentPage();
    if(obj != null){
      return obj.getPageID();
    }else{
      return 0;
    }
  }

  /**
   * @return The enclosing Form object
   */
  public Form getParentForm() {
    Form returnForm = null;
    PresentationObject obj = getParentObject();
    while (obj != null) {
      if (obj instanceof Form) {
	returnForm = (Form)obj;
      }
      obj = obj.getParentObject();
    }
    return returnForm;
  }

  /**
   * returns the objectinstance this object is part of
   */
    // getContainingObjectInstance
  public PresentationObject getParentObjectInstance() {
    PresentationObject obj = this;
    while (obj != null) {
      if (obj.getICObjectInstanceID() != 0) {
	return obj;
      }
      if(obj instanceof IFrameContent){
	obj = ((IFrameContent)obj).getOwnerInstance();
      }else {
	obj = obj.getParentObject();
      }
    }
    return null;
  }
	// getContainingObjectInstanceID
  public int getParentObjectInstanceID() {
    PresentationObject obj = getParentObjectInstance();
    if(obj != null){
      return obj.getICObjectInstanceID();
    }else{
      return 0;
    }
  }


  /**
   * Override this function for needed funcionality.
   *
   * This funcion is invoked on each request by the user (before print(iwc) ) on a PresentationObject Instance.
   */
  public void main(IWContext iwc) throws Exception {
  }

  protected void prepareClone(PresentationObject newObjToCreate) {
  }

  public Object _clone(IWUserContext iwc, boolean askForPermission){
    if ( iwc != null ) {
      this.setIWApplicationContext(iwc.getApplicationContext());
      this.setIWUserContext(iwc);
    }
    if(askForPermission||iwc!=null){
      if(iwc.hasViewPermission(this)){
	//return this.clone(iwc,askForPermission);
	return this.clone();
      } else {
	return NULL_CLONE_OBJECT;
      }
    } else {
      return this.clone();
    }
  }

  public Object clone(IWUserContext iwc) {
    return this._clone(iwc,true);
  }

/*  public synchronized Object clone(IWContext iwc, boolean askForPermission) {
    return this.clone();
  }
*/
  /*
  public synchronized Object clone() {
    PresentationObject obj = null;
    try {
      //This is forbidden in clone i.e. "new":
      //obj = (PresentationObject)Class.forName(this.getClassName()).newInstance();
      obj = (PresentationObject)super.clone();
      if (this.attributes != null) {
	obj.setAttribute((Hashtable)this.attributes.clone());
      }
      obj.setName(this.name);
      //obj.setParentObject(this.parentObject);
      this.prepareClone(obj);
      Vector vector;

    }
    catch(Exception ex) {
      ex.printStackTrace(System.err);
    }

    return obj;
  }
  */

  public Object clone() {
    PresentationObject obj = null;
/*    System.err.println("--");
    System.err.println("Cloning class of type: "+ this.getClassName());
    System.err.println("--");
*/
    try {
      //This is forbidden in clone i.e. "new":
      //obj = (PresentationObject)Class.forName(this.getClassName()).newInstance();
      obj = (PresentationObject)super.clone();
      if (this.attributes != null) {
	obj.setAttribute((Hashtable)this.attributes.clone());
      }
      obj.setName(this.name);
      //obj.setParentObject(this.parentObject);
      this.prepareClone(obj);
      obj.initializedInMain = this.initializedInMain;
      obj.ic_object_instance_id = this.ic_object_instance_id;
      obj.ic_object_id = this.ic_object_id;
      obj._location = this._location;


      //obj.defaultState = this.defaultState;  //same object, unnecessary to clone

    }
    catch(Exception ex) {
      ex.printStackTrace(System.err);
    }



    return obj;
  }


/*
  protected void initICObjectInstanceId(IWContext iwc){
    String sID = iwc.getParameter(_PARAMETER_IC_OBJECT_INSTANCE_ID);
    try {
      if(sID != null){
	System.err.println("sID: "+sID);
	this.setICObjectInstanceID(Integer.parseInt(sID));
	//this.ic_object_instance_id = Integer.parseInt(sID);
	System.err.println("Integer.parseInt(sID): "+Integer.parseInt(sID));
	System.err.println("getICObjectInstanceID: "+this.getICObjectInstanceID());
      }else{
	System.err.println("sID == null");
      }
    }
    catch (NumberFormatException ex) {
      System.err.println(this+": cannot init ic_object_instance_id");
    }

  }
*/
  /**
   * Function invoked before the print function
   */
  public void _main(IWContext iwc) throws Exception {
    /*if(this.ic_object_instance_id == 0){
      initICObjectInstanceId(iwc);
    }*/
    initVariables(iwc);

    if(_iwuc == null){
      this.setIWUserContext(iwc);
    }

    if(!initializedInMain){
      this.initInMain(iwc);
    }

    if (!goneThroughMain) {
      main(iwc);
    }

    goneThroughMain = true;
  }


  protected void setErrorMessage(String errorMessage) {
    this.errorMessage = errorMessage;
  }

  protected String getErrorMessage() {
    return this.errorMessage;
  }

  public void setAsPrinted(boolean printed) {
    doPrint = printed;
  }

  /*public void setTreeID(String treeID) {
    this.treeID = treeID;
  }

  public String getTreeID() {
    return treeID;
  }*/


  public void setICObjectInstanceID(int id) {
    this.ic_object_instance_id = id;
    this.getLocation().setICObjectInstanceID(id);
  }


  public void setICObjectInstance(ICObjectInstance instance) {
    setICObjectInstanceID(instance.getID());
  }

  public void setICObjectID(int id) {
    this.ic_object_id = id;
  }

  public void setICObject(ICObject obj) {
    this.ic_object_id = obj.getID();
  }

  /**
   * owerwrite in module
   */

  public int getICObjectInstanceID(IWContext iwc) throws SQLException {
    return getICObjectInstanceID();
  }

  public int getICObjectInstanceID(){
    return this.ic_object_instance_id;
  }

  public ICObjectInstance getICObjectInstance(IWContext iwc) throws SQLException {
    return getICObjectInstance();
  }

  public ICObjectInstance getICObjectInstance()throws SQLException{
    if(getICObjectInstanceID() != 0){
      return ((com.idega.core.data.ICObjectInstanceHome)com.idega.data.IDOLookup.getHomeLegacy(ICObjectInstance.class)).findByPrimaryKeyLegacy(getICObjectInstanceID());
    } else {
      return null;
    }
  }

  public int getICObjectID(IWContext iwc) throws SQLException {
    return ic_object_id;
    /*ICObjectInstance inst = this.getICObjectInstance();

    System.out.println("Getting ICObjectInstance");
    if(inst != null){

      ICObject ob = inst.getObject();
      if(ob != null){
	return ob.getID();
      }
    }
    return 0;*/
  }

  public int getICObjectID(){
    return ic_object_id;
  }

  public ICObject getICObject() throws Exception {
    return this.getICObject(this.getClass());
  }

  protected ICObject getICObject(Class c) throws Exception {
  	try{
		ICObjectHome icohome = (ICObjectHome)com.idega.data.IDOLookup.getHome(ICObject.class);
		ICObject ico = icohome.findByClassName(c.getName());
		return ico;
	}
	catch(Exception e){
		throw new ICObjectNotInstalledException(c.getName());
	}
  }


  public ICObjectInstance getICInstance(IWContext iwc) throws IWException {
    try {
    	ICObjectInstanceHome icoihome = (ICObjectInstanceHome)com.idega.data.IDOLookup.getHome(ICObjectInstance.class);
      	return icoihome.findByPrimaryKey(getICObjectInstanceID(iwc));
    }
    catch (Exception excep) {
      IWException iwexcep = new IWException("Exception in PresentationObject.getICInstance(): "+excep.getClass().getName()+" : "+excep.getMessage());
      throw iwexcep;
      //throw (IWException) excep.fillInStackTrace();
    }
  }
  /**
   * @deprecated Do not use this function
   */
  public void addIWLinkListener(IWLinkListener l,IWContext iwc) {
    //System.err.println(this.getClass().getName() + " : listener added of type -> " + l.getClass().getName());
    /**
     * temp
     */
    getEventListenerList().remove(IWLinkListener.class,l);

    getEventListenerList().add(IWLinkListener.class,l);
  }

  /**
   * @deprecated Do not use this function
   */
  public IWLinkListener[] getIWLinkListeners() {
    return (IWLinkListener[])getEventListenerList().getListeners(IWLinkListener.class);
  }

  /**
   * @deprecated Do not use this function
   */
  public void addIWSubmitListener(IWSubmitListener l,IWContext iwc){
    getEventListenerList().add(IWSubmitListener.class,l);
  }

    /**
     * @deprecated Do not use this function
     */
    public IWSubmitListener[] getIWSubmitListeners(){
      if (listenerList == null){
	listenerList = new EventListenerList();
      }
      return (IWSubmitListener[])listenerList.getListeners(IWSubmitListener.class);
    }


    public void setEventAttribute(String attributeName,Object attributeValue){
    if (this.eventAttributes == null){
      this.eventAttributes = new Hashtable();
    }
    this.eventAttributes.put((Object) attributeName,(Object) attributeValue);
    }

    public Object getEventAttribute(String attributeName){
      if (this.eventAttributes != null){
	return this.eventAttributes.get((Object) attributeName);
      }
      else{
	return null;
      }
    }



     public void dispatchEvent(IWEvent e) {
	  processEvent(e);
    }


      protected void processEvent(IWEvent e) {

	  if (e instanceof IWLinkEvent) {
	      processIWLinkEvent((IWLinkEvent)e);

	  } else if (e instanceof IWSubmitEvent) {
	      processIWSubmitEvent((IWSubmitEvent)e);
	  } else{
	      System.err.println("unable to prosess event: " + e);
	  }
      }

      protected void processIWLinkEvent(IWLinkEvent e) {
	PresentationObject obj = (PresentationObject)e.getSource();
	// Guaranteed to return a non-null array
	IWLinkListener[] listeners = obj.getIWLinkListeners();
	// Process the listeners last to first, notifying
	// those that are interested in this event
	for (int i = listeners.length-1; i>=0; i--) {
	  ((IWLinkListener)listeners[i]).actionPerformed(e);
	}
      }

      protected void processIWSubmitEvent(IWSubmitEvent e) {
	PresentationObject obj = (PresentationObject)e.getSource();
	// Guaranteed to return a non-null array
	IWSubmitListener[] listeners = obj.getIWSubmitListeners();
	// Process the listeners last to first, notifying
	// those that are interested in this event
	for (int i = listeners.length-1; i>=0; i--) {
	  ((IWSubmitListener)listeners[i]).actionPerformed(e);
	}
      }




    /**
     * unimplemented
     */
    public void fireEvent(){
    }

    public void endEvent(IWContext iwc){
      iwc.removeSessionAttribute(eventLocationString);
    }

    public void listenerAdded(boolean added){
      listenerAdded = added;
    }

    public boolean listenerAdded(){
      return listenerAdded;
    }

  public void setIWContext(IWContext iwc){
  //  System.err.println(this.getClass().getName() + ": iwc set");
    eventIWContext = iwc;
  }

  /**
   * @deprecated Do not use this function
   */
  public EventListenerList getEventListenerList(){
    if (listenerList == null){
	listenerList = new EventListenerList();
    }
    return listenerList;
  }

  /**
   * @deprecated Do not use this function, it is not safe
   */
  public IWContext getEventIWContext(){
    return eventIWContext;
  }

  public void _setIWContext(IWContext iwc){
    setIWContext(iwc);
  }

  public void setProperty(String key, String values[]){
  }

  /**
   * Needs to be overrided to get the right IWBundle identifier for the object
   */
  public String getBundleIdentifier(){
    return IW_BUNDLE_IDENTIFIER;
  }

  public IWBundle getBundle(IWUserContext iwuc){
    IWMainApplication iwma = iwuc.getApplicationContext().getApplication();
    return iwma.getBundle(getBundleIdentifier());
  }

  public IWResourceBundle getResourceBundle(IWUserContext iwuc){
    IWBundle bundle = getBundle(iwuc);
    if(bundle!=null){
      return bundle.getResourceBundle(iwuc.getCurrentLocale());
    }
    return null;
  }

  public IWPropertyList getUserProperties(IWUserContext iwuc){
    IWBundle bundle = getBundle(iwuc);
    if(bundle!=null){
      return bundle.getUserProperties(iwuc);
    }
    return null;
  }

  public String getLocalizedString(String key,IWUserContext iwuc){
    IWResourceBundle bundle = getResourceBundle(iwuc);
    if(bundle!=null){
      return bundle.getLocalizedString(key);
    }
    return null;
  }

  public String getLocalizedString(String key,String defaultValue,IWUserContext iwuc){
    IWResourceBundle bundle = getResourceBundle(iwuc);
    if(bundle!=null){
      return bundle.getLocalizedString(key,defaultValue);
    }
    return null;
  }

  public void setUseBuilderObjectControl(boolean use) {
    _useBuilderObjectControl = use;
  }

  public boolean getUseBuilderObjectControl() {
    return(_useBuilderObjectControl);
  }

  public void setBelongsToParent(boolean belongs) {
    _belongsToParent = belongs;
  }

  public boolean getBelongsToParent() {
    return(_belongsToParent);
  }

  public boolean getChangeInstanceIDOnInheritance() {
    return(_changeInstanceIDOnInheritance);
  }

  public void setChangeInstanceIDOnInheritance(boolean change) {
    _changeInstanceIDOnInheritance = change;
  }

  public boolean allowPagePermissionInheritance(){
    return _allowPagePermissionInheritance;
  }



  /*
    New Event system
  */


  public String changeState(PresentationObject source, IWContext iwc){
    System.err.println(this+" state not changed, method not implemented");
    System.err.println("source = "+ source +" : "+source.getParentPageID()+"_"+source.getParentObjectInstanceID());
    return null;
  }

  public GenericState getStateInstance(IWContext iwc){
    return new GenericState(this,iwc);
  }

  public GenericState getState(IWContext iwc){
    GenericState state = null;
    String stateString = null;
    if(this instanceof IFrameContent){
      stateString = iwc.getCurrentState(((IFrameContent)this).getOwnerInstance().getICObjectInstanceID());
      //System.err.println("stateString = iwc.getCurrentState("+((IFrameContent)this).getOwnerInstance().getICObjectInstanceID()+");");
      //System.err.println(this.getClassName()+" - stateString:"+((stateString==null)?"objectNull":stateString)+" for instance "+((IFrameContent)this).getOwnerInstance().getICObjectInstanceID());
      //System.err.println("IWContext.hashCode(): "+iwc.hashCode());
    } else {
      stateString = iwc.getCurrentState(this.getICObjectInstanceID());
      //System.err.println(this.getClassName()+" - stateString:"+((stateString==null)?"objectNull":stateString)+" for instance "+this.getICObjectInstanceID());
      //System.err.println("IWContext.hashCode(): "+iwc.hashCode());
    }

    if(stateString != null){
      state = getStateInstance(iwc);
      if(state != null){
	state.updateState(stateString);
      }
    }else {
      state = getDefaultState();
    }

    return state;
  }

  public void setDefaultState(GenericState state){
    defaultState = state;
  }

  public GenericState getDefaultState(){
    return (GenericState)defaultState.clone();
  }

  public boolean equals(PresentationObject obj){
    if(this.getICObjectInstanceID() == obj.getICObjectInstanceID() && this.getICObjectInstanceID() != 0){
      return true;
    }
    return super.equals(obj);
  }

  public boolean equals(Object obj){
    if(obj instanceof PresentationObject){
      return this.equals((PresentationObject)obj);
    } else {
      return super.equals(obj);
    }
  }

  /**
   *  Parameter debugger
   */
   public void debugParameters(IWContext iwc){
    System.err.println("DEBUG: Parameter debugging : "+this.getClassName());
    System.err.println("AT  :"+new java.util.Date(System.currentTimeMillis()).toString());
    java.util.Enumeration enum = iwc.getParameterNames();
    String prm;
	while(enum.hasMoreElements()){
    	prm = (String) enum.nextElement();
      	String[] values =  iwc.getParameterValues(prm);
      	for (int i = 0; i < values.length; i++) {
      		//debug("Name: "+prm+(values.length>1?" # "+(i+1):"")+"\t Value: "+values[i]);
			System.err.println("Name: "+prm+(values.length>1?" # "+(i+1):"")+"\t Value: "+values[i]);
	  	}
	  //System.err.println("Name: "+prm+"\t Value: "+iwc.getParameter(prm));
    }
    System.err.println();
   }

   /**
    * This method outputs the outputString to System.out if the Application property
    * "debug" is set to "TRUE"
    */
   public void debug(String outputString){
    if( IWMainApplicationSettings.isDebugActive() ){
      System.out.println("[DEBUG] \""+outputString+"\" : "+this.getClassName());
    }
   }

   public String getBuilderName(IWUserContext iwuc) {
      //return this.getClassName().substring(this.getClassName().lastIndexOf(".")+1);
      return this.getBundle(iwuc).getComponentName(this.getClass(),iwuc.getCurrentLocale());
   }

   /**
    * Returns the page parameter used by idegaWeb Builder
    */
   public String getIBPageParameterName(){
     return BuilderConstants.IB_PAGE_PARAMETER;
   }


  public void setWidth(String width){
    setAttribute(WIDTH,width);
  }
  
  public void setVisible(boolean visible) {
  	if (visible)
  		setStyleAttribute("visibility:visible");	
  	else
  		setStyleAttribute("visibility:hidden");
  }

  public void setHeight(String height){
    setAttribute(HEIGHT,height);
  }

  public String getHeight(){
   return getAttribute(HEIGHT);
  }

  public String getWidth(){
    return getAttribute(WIDTH);
  }

  public void setHorizontalAlignment(String align){
    setAttribute(HORIZONTAL_ALIGNMENT,align);
  }

  public String getHorizontalAlignment(){
   return getAttribute(HORIZONTAL_ALIGNMENT);
  }

  /**
   * Returns the IWApplicationContext that this object is running in.
   *
   * @throws RuntimeException if the context is not set
   */
  protected IWApplicationContext getIWApplicationContext(){
    if(_iwac==null){
      setIWApplicationContext(IWContext.getInstance());
    }
    return _iwac;
  }

  protected void setIWApplicationContext(IWApplicationContext iwac){
    _iwac=iwac;
  }


  /**
   * Returns the IWUserContext that this object is running in.
   *
   * @throws RuntimeException if the context is not set
   */
  protected IWUserContext getIWUserContext(){
    if(_iwuc==null){
     setIWUserContext(IWContext.getInstance());
    }
    return _iwuc;
  }

  protected void setIWUserContext(IWUserContext iwuc){
    _iwuc=iwuc;
  }


  public void setLocation(IWLocation location){
    this.setLocation(location,this.getIWUserContext());
  }

  public void setLocation(IWLocation location, IWUserContext iwuc){
    _location = location;
    if(this instanceof StatefullPresentation){
      IWPresentationState state = ((StatefullPresentation)this).getPresentationState(iwuc);
      if(state != null){
	state.setLocation(location);
      }
    }
  }

  public IWLocation getLocation(){
//    if(this instanceof StatefullPresentation){
//      return ((StatefullPresentation)this).getPresentationState().getLocation();
//    } else {
      return _location;
//    }
  }

  public EventListenerList getEventListenerList(IWUserContext iwuc){
    if(_listenerList != null){
      return _listenerList;
    } else {
      try {
//<<<<<<< PresentationObject.java
        IWEventMachine machine = (IWEventMachine)IBOLookup.getSessionInstance(iwuc,IWEventMachine.class);
//        System.out.println();
//=======
//	IWEventMachine machine = (IWEventMachine)IBOLookup.getSessionInstance(iwuc,IWEventMachine.class);
//	System.out.println();
//>>>>>>> 1.52
//        System.out.println("getEventListenerList: machine = "+ machine);
//        System.out.println("getEventListenerList: location = "+this.getLocation());
//<<<<<<< PresentationObject.java
//        System.out.println();
        if(this.getICObjectInstanceID() == 0){
          if(this.getLocation() != null){
            _listenerList = machine.getListenersFor(this.getLocation());
            return _listenerList;
          } else {
            throw new RuntimeException("ERROR: "+this+".getEventListenerList(IWUserContext iwuc): Object has neither instanceId nor IWLocationObject");
          }
        } else {
          _listenerList = machine.getListenersFor(this.getICObjectInstance());
          return _listenerList;
        }
//=======
//	System.out.println();
//	if(this.getICObjectInstanceID() == 0){
//	  if(this.getLocation() != null){
//	    _listenerList = machine.getListenersFor(this.getLocation());
//	    return _listenerList;
//	  } else {
//	    throw new RuntimeException("ERROR: "+this+".getEventListenerList(IWUserContext iwuc): Object has neither instanceId nor IWLocationObject");
//	  }
//	} else {
//	  _listenerList = machine.getListenersFor(this.getICObjectInstance());
//	  return _listenerList;
//	}
//>>>>>>> 1.52
      }
      catch (RemoteException ex) {
	throw new RuntimeException(ex.getMessage());
			}
      catch (SQLException sql) {
	throw new RuntimeException(sql.getMessage());
			}

		}
	}	
	
	
	

/** This method is similar to the method getEventListenerList but uses the
 * compoundId as key instead of the location object to fetch the listener list 
 * @param iwuc
 * @return EventListenerList
 */
  public EventListenerList getEventListener(IWUserContext iwuc){
    if(_listenerList != null){
      return _listenerList;
    } else {
      try {
        IWEventMachine machine = (IWEventMachine)IBOLookup.getSessionInstance(iwuc,IWEventMachine.class);
        return machine.getListenersForCompoundId(getCompoundId()); 
      }
      catch (RemoteException ex) {
  throw new RuntimeException(ex.getMessage()); 
      }
	  }
  }

  public void addIWActionListener(IWActionListener l){
//    getEventListenerList(this.getIWUserContext()).remove(IWActionListener.class,l);

    Object[] list = getEventListenerList(this.getIWUserContext()).getListenerList();

    boolean hasBeenAdded = false;
    // Is l on the list?
    for (int i = list.length-2; i>=0; i-=2) {
	if ((list[i]==IWActionListener.class) && (list[i+1].equals(l) == true)) {
	    hasBeenAdded = true;
	    break;
	}
    }
    if(!hasBeenAdded){
      getEventListenerList(this.getIWUserContext()).add(IWActionListener.class,l);
    }
//    System.out.println();
//    System.out.println("addIWActionListener: _listenerList = "+getEventListenerList(this.getIWUserContext()));
//    System.out.println("addIWActionListener: IWActionListener = " + l);
//    System.out.println("addIWActionListener: location = "+this.getLocation());
//    System.out.println();

  }

  /** This method is similar to the method addIWActionListener but uses the
   * method getEventListener() instead of getEventListenerList()
   * @param l
   */  
  public void addActionListener(IWActionListener l){

    Object[] list = getEventListener(this.getIWUserContext()).getListenerList();

    boolean hasBeenAdded = false;
    // Is l on the list?
    for (int i = list.length-2; i>=0; i-=2) {
  if ((list[i]==IWActionListener.class) && (list[i+1].equals(l) == true)) {
      hasBeenAdded = true;
      break;
  }
    }
    if(!hasBeenAdded){
      getEventListener(this.getIWUserContext()).add(IWActionListener.class,l);
    }

  }


  public void removeIWActionListener(IWActionListener l){
    getEventListenerList(this.getIWUserContext()).remove(IWActionListener.class,l);
  }
  
  /** This method is similar to the method addIWActionListener but uses the
   * method getListener() instead of getEventListenerList()
   * 
   */
  public void removeActionListener(IWActionListener l){
    getEventListener(this.getIWUserContext()).remove(IWActionListener.class,l);
  }  
  


  public void debugEventListanerList(IWContext iwc){
    System.out.println("--[DEBUG: "+this+"]: listenerList values:");
    EventListenerList list = this.getEventListenerList(iwc);
    Object[] listeners = (Object[])list.getListenerList();
    if(listeners != null ){
      for (int i = 0; i < listeners.length; i++) {
        System.out.println(listeners[i]);
      }
    }
    System.out.println("--[DEBUG: "+this+"]: listenerList: DONE");
  }
  
  	/**
	 * Sets the width in pixels or percents
	 * Sets the width inside a style attribute
	 */
	protected void setWidthStyle(String width)
	{
		try
		{
			this.setStyleAttribute("width:" + Integer.parseInt(width) + "px");
		}
		catch (NumberFormatException e)
		{
			this.setStyleAttribute("width:" + width);
		}
	}
	/**
	 * Sets the height in pixels or percents
	 * Sets the height inside a style attribute
	 */
	protected void setHeightStyle(String height)
	{
		try
		{
			this.setStyleAttribute("height:" + Integer.parseInt(height) + "px");
		}
		catch (NumberFormatException e)
		{
			this.setStyleAttribute("height:" + height);
		}
	}
  
  
  /**
   * @see javax.faces.component.UIComponent#getComponentId()
   * @return String
   */
  public String getComponentId() {
    String id = IWMainApplication.getEncryptedClassName(this.getClass());
    
    if (artificialCompoundId != null)
      return id;
    
    PresentationObject mother = getParentObject();
    // keep in mind that a parent object is not necessarily a container.
    // Therefore check if a cast is possible. 
    if (mother != null && (PresentationObjectContainer.class).isAssignableFrom(mother.getClass())) {
      StringBuffer buffer = new StringBuffer(id);
      List list = ((PresentationObjectContainer)mother).getAllContainingObjects();
      int myIndex = list.indexOf(this);
      // add underscore 
      buffer.append(PresentationObject.COMPOUNDID_CHILD_NUMBER_DELIMITER);
      buffer.append(myIndex);
      return buffer.toString();
    }
    else  {
      return id;
    }
  }
  
  /**
   * @see javax.faces.component.UIComponent#getComponentId()
   * 
   * Create a path (e.g. "/1234/123/123") that corresponds to the path of the
   * parent - child relation using the encrypted class name.
   * 
   * @return String
   */
  private String calculateCompoundId() {
    StringBuffer buffer = new StringBuffer();
    // if artificial compound id is set use this one
    if (artificialCompoundId != null) {
      buffer.
        append(artificialCompoundId).
        append(PresentationObject.COMPOUNDID_COMPONENT_DELIMITER).
        append(getComponentId());
      return buffer.toString();
    }
    // if there is a object instance id use this one
    int instanceId;
    if ((instanceId = getICObjectInstanceID()) != 0)
      return Integer.toString(instanceId);
    // first fetch my component id
    buffer.append(PresentationObject.COMPOUNDID_COMPONENT_DELIMITER);
    buffer.append(getComponentId());
    // now add the compound id of my mother at the beginning
    PresentationObject mother = getParentObject();
    if (mother != null)
      buffer.insert(0, mother.calculateCompoundId());
    return buffer.toString();  
  }

  /**
   * something has changed, therefore change ALSO the value of the presentation
   * state object
    if formerCompoundId equals null the compoundId has never been fetched and
    therefore the
     presentation state object was not set yet (if so everything is fine).
   	usually you should first set the artificialCompoundId (if necessary)
		 and then call the getPresentationState method.
     The part of this method that calls the state mashine is only for old code!

   * @param artificialCompoundId
   * @param iwuc
   */

  public void setArtificialCompoundId(String artificialCompoundId, IWUserContext iwuc) {
    this.artificialCompoundId = artificialCompoundId;
    
    // something has changed, therefore change also the value of the presentation state object
    // if formerCompoundId equals null the compoundId has never been fetched and therefore the
    // presentation state object was not set yet (if so everything is fine).
    // Usually you should first set the artificialCompoundId (if necessary) 
    // and then call the getPresentationState() method. 
    // The part of this method that calls the state mashine is only for old code!     
    if (formerCompoundId != null && this instanceof StatefullPresentation)  {
      try {
        Class stateClass = ((StatefullPresentation)this).getPresentationStateClass();
        IWStateMachine stateMachine = (IWStateMachine)IBOLookup.getSessionInstance(iwuc,IWStateMachine.class);
        IWPresentationState state =
          stateMachine.getStateFor(formerCompoundId,stateClass);
        // update compoundId 
        state.setArtificialCompoundId(artificialCompoundId);
      }
      catch (RemoteException re) {
        throw new RuntimeException(re.getMessage());
      }
      
    }
  }

  
  public String getCompoundId()  {
    // wrap the private method to do something...
    // at the moment there is nothing to do.
    String calculatedCompoundId = calculateCompoundId();
    // Do not forget: set former compound id to the new one 
    formerCompoundId = calculatedCompoundId;
    return calculatedCompoundId;  
  }
  
  /**
   * Convenience method to return the instance of BuilderService
   * @param iwc
   * @return
   * @throws RemoteException
   */
  protected BuilderService getBuilderService(IWApplicationContext iwc)throws RemoteException{
  	return BuilderServiceFactory.getBuilderService(iwc);
  }
  
  /**
   * Convenience method to return the instance of ICFileSystem
   * @param iwc
   * @return
   * @throws RemoteException
   */
  protected ICFileSystem getICFileSystem(IWApplicationContext iwc)throws RemoteException{
	return ICFileSystemFactory.getFileSystem(iwc);
  }
  
}
