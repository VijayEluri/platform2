/*
 * $Id: IWContext.java,v 1.126.2.2 2007/01/12 19:31:33 idegaweb Exp $
 * Created 2000 by Tryggvi Larusson
 *
 * Copyright (C) 2000-2004 Idega Software hf. All Rights Reserved.
 *
 * This software is the proprietary information of Idega hf.
 * Use is subject to license terms.
 */
package com.idega.presentation;

import java.io.IOException;
import java.io.PrintWriter;
import java.io.UnsupportedEncodingException;
import java.rmi.RemoteException;
import java.security.Principal;
import java.util.Collection;
import java.util.Enumeration;
import java.util.Hashtable;
import java.util.Iterator;
import java.util.List;
import java.util.Locale;
import java.util.Map;
import java.util.WeakHashMap;
import javax.faces.application.Application;
import javax.faces.application.FacesMessage;
import javax.faces.application.FacesMessage.Severity;
import javax.faces.component.UIViewRoot;
import javax.faces.context.ExternalContext;
import javax.faces.context.FacesContext;
import javax.faces.context.ResponseStream;
import javax.faces.context.ResponseWriter;
import javax.faces.render.RenderKit;
import javax.servlet.ServletContext;
import javax.servlet.http.Cookie;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;
import com.idega.core.accesscontrol.business.AccessController;
import com.idega.core.accesscontrol.business.LoginBusinessBean;
import com.idega.core.accesscontrol.business.NotLoggedOnException;
import com.idega.core.builder.business.BuilderService;
import com.idega.core.builder.business.BuilderServiceFactory;
import com.idega.core.builder.business.ICBuilderConstants;
import com.idega.core.builder.data.ICDomain;
import com.idega.core.builder.data.ICPage;
import com.idega.core.component.data.ICObject;
import com.idega.core.idgenerator.business.UUIDGenerator;
import com.idega.core.localisation.business.ICLocaleBusiness;
import com.idega.core.user.data.User;
import com.idega.event.IWEventProcessor;
import com.idega.idegaweb.IWApplicationContext;
import com.idega.idegaweb.IWConstants;
import com.idega.idegaweb.IWMainApplication;
import com.idega.idegaweb.IWMainApplicationSettings;
import com.idega.idegaweb.IWSystemProperties;
import com.idega.idegaweb.IWUserContext;
import com.idega.idegaweb.UnavailableIWContext;
import com.idega.io.UploadFile;
import com.idega.presentation.ui.Parameter;
import com.idega.user.business.UserProperties;
import com.idega.user.util.Converter;
import com.idega.util.FacesUtil;
import com.idega.util.datastructures.HashtableMultivalued;

/**
 * This class is a context information that lives through each user request in an idegaWeb application. 
 * The role of this class is very similar to that of FacesContext in a JSF application. <br>
 * IWContext will be gradually phased out in future versions in favour of just working with the standard FacesContext.
 * <br>
 * This class gives access to Request specific, User specific and Application specific information.
 * <br>
 * An instance of this class should be used under the interfaces com.idega.idegaweb.IWUserContext and
 * com.idega.idegaweb.IWApplicationContext where it is applicable (i.e. when only working with User scoped
 * functionality or Application scoped functionality).
 *<br>
 *
 * Last modified: $Date: 2007/01/12 19:31:33 $ by $Author: idegaweb $
 *
 * @author <a href="mailto:tryggvil@idega.com">Tryggvi Larusson</a>
 * @version $Revision: 1.126.2.2 $
 */
public class IWContext
extends javax.faces.context.FacesContext
implements IWUserContext, IWApplicationContext {

	/**
	 * Comment for <code>serialVersionUID</code>
	 */
	private static final long serialVersionUID = 3761970466885022262L;
	private HttpServletRequest _request;
	private HttpServletResponse _response;
	private final static String LOCALE_ATTRIBUTE = "idegaweb_locale";
	public final static String IDEGA_SESSION_KEY="idega_session_id";
	private final static String WEAK_HASHMAP_KEY = "idegaweb_weak_hashmap";
	private final static String CHARACTER_SET_PREFIX = "; charset=";
	private String markupLanguage; //Variable to set the language i.e. HTML
	private String spokenLanguage;
	private ServletContext servletContext;
	private boolean _doneHandHeldCheck = false;
	private boolean _clientIsHandHeld = false;
	private boolean isCaching = false;
	private PrintWriter cacheWriter;
	private ResponseWriter cacheResponseWriter;
	private PrintWriter writer = null;
	private HashtableMultivalued _multipartParameters = null;
	private UploadFile _uploadedFile = null;
	private FacesContext realFacesContext;
	
	private static final String IWCONTEXT_REQUEST_KEY="iwcontext";
	private static final String PRM_HISTORY_ID = ICBuilderConstants.PRM_HISTORY_ID;
	private static final String SESSION_OBJECT_STATE = ICBuilderConstants.SESSION_OBJECT_STATE;
	
	
	protected static final String IWC_SESSION_ATTR_NEW_USER_KEY = "iwc_new_user";
	public static final String[] WML_USER_AGENTS = new String[] {"nokia", "ericsson", "wapman", "upg1", "symbian", "wap"}; // NB: must be lowercase
	private boolean isRequestCharacterEncodingSet=false;

	/**
	 *Default constructor
	 **/
	public IWContext() {
	}

	private IWContext(FacesContext fc){
		this((HttpServletRequest)fc.getExternalContext().getRequest(),(HttpServletResponse)fc.getExternalContext().getResponse(),(ServletContext) fc.getExternalContext().getContext());
		setRealFacesContext(fc);
	}
	
	/**
	 * @param request
	 * @param response
	 * @param context
	 */
	public IWContext(HttpServletRequest request, HttpServletResponse response, ServletContext context) {
		setRequest(request);
		setResponse(response);
		setServletContext(context);
		//MUST BE DONE BEFORE ANYTHING IS GOTTEN FROM THE REQUEST!
		initializeAfterRequestIsSet(request);
	}
	
	protected void initializeAfterRequestIsSet(HttpServletRequest request){
		//MUST BE DONE BEFORE ANYTHING IS GOTTEN FROM THE REQUEST!
		if(getIfSetRequestCharacterEncoding()){
			try {
				getRequest().setCharacterEncoding(getApplicationSettings().getCharacterEncoding());
				this.isRequestCharacterEncodingSet = true;
			} catch (UnsupportedEncodingException e) {
				e.printStackTrace();
			}
		}
		//CANNOT BE DONE UNTIL AFTER THE CHARACTER ENCODING IS DONE, OTHERWISE THE ENCODING WILL DEFAULT TO ISO-8859-1 BUT DISPLAY ITSELF AS THE PREFERRED ENCODING!
		setMarkupLanguage(getDetectedClientMarkupLanguage(request));
	}
	
	protected boolean getIfSetRequestCharacterEncoding(){
	    //TODO: check if this is ok for multipart forms
	    boolean returner = (!this.isRequestCharacterEncodingSet)&&this.getIWMainApplication().getApplicationServer().getSupportsSettingCharactersetInRequest();
	    return returner;
	}
	
	/**
	 * This is the method to convert/cast a FacesContext instance to a IWContext instance.
	 * if the FacesContext instance is really a IWContext it upcasts the instance, else it 
	 * constructs a new and stores it in the outer facescontext's request map.
	 */
	public static IWContext getIWContext(FacesContext fc){
			if(fc instanceof IWContext){
				return (IWContext)fc;
			}
			IWContext iwc=null;
			//try to look up from requestmap
			iwc = (IWContext)fc.getExternalContext().getRequestMap().get(IWCONTEXT_REQUEST_KEY);
			// reason for the second condition below: 
			// After forwarding the faces context has changed, check if the stored iwc holds the same faces context 
			// or if a new iw context needs to be created.
			// Forwarding is used when applying navigation rules.
			// If iwc is holding an old faces context the response writer might not be set 
			// (that is the response writer is null).
			if(iwc==null || fc != iwc.getRealFacesContext()){
				//put it to the request map if it isn't there already
				iwc = new IWContext(fc);
				fc.getExternalContext().getRequestMap().put(IWCONTEXT_REQUEST_KEY,iwc);
			}
			return iwc;
	}
	public HttpSession getSession() {
		return getRequest().getSession();
	}
	public boolean isMultipartFormData() {
		String contentType = this.getRequestContentType();
		if (contentType != null) {
			return (contentType.indexOf("multipart") != -1);
		} else {
			return false;
		}
	}
	public void setMultipartParameter(String key, String value) {
		if (this._multipartParameters == null) {
			this._multipartParameters = new HashtableMultivalued();
		}
		this._multipartParameters.put(key, value);
	}
	public String getMultipartParameter(String key) {
		if (this._multipartParameters != null) {
			return (String) this._multipartParameters.get(key);
		} else {
			return null;
		}
	}
	public UploadFile getUploadedFile() {
	    if(isMultipartFormData() && this._uploadedFile==null) {
			try {
                IWEventProcessor.getInstance().handleMultipartFormData(this);
            } catch (Exception e) {
                e.printStackTrace();
            }
		}
		return this._uploadedFile;
	}
	public void setUploadedFile(UploadFile file) {
		this._uploadedFile = file;
	}

	public boolean isUploadedFileSet() {
		return this._uploadedFile != null;
	}

	public String getUserAgent() {
		return getRequest().getHeader("User-agent");
	}
	public String getReferer() {
		return getRequest().getHeader("Referer");
	}
	public boolean isMacOS() {
		boolean isMac = false;
		String userAgent = getUserAgent();
		if(userAgent!=null){
			if (userAgent.indexOf("Mac") != -1) {
				isMac = true;
			} else if (userAgent.indexOf("mac") != -1) {
				isMac = true;
			}
		}
		return isMac;
	}
	
	public boolean isWebDavClient() {
		boolean isDav = false;
		String userAgent = getUserAgent();
		if(userAgent!=null){
			if (userAgent.indexOf("DAV") != -1) {
				isDav = true;
			} else if (userAgent.indexOf("dav") != -1) {
				isDav = true;
			} else if (userAgent.indexOf("Dav") != -1) {
				isDav = true;
			}
		}
		return isDav;
	}
	
	public boolean isNetscape() {
		String userAgent = getUserAgent();
		if(userAgent!=null){
			if (userAgent.indexOf("Mozilla") != -1) {
				//if not Internet Explorer then Netscape :)
				if (userAgent.indexOf("MSIE") != -1) {
					return false;
				} else {
					return true;
				}
			}
		}
		return false;
	}
	public boolean isIE() {
		String userAgent = getUserAgent();
		if(userAgent!=null){
			if (userAgent.indexOf("MSIE") != -1) {
				return true;
			}
		}
		return false;
	}
	public boolean isOpera() {
		String userAgent = getUserAgent();
		if(userAgent!=null){
			if (userAgent.indexOf("Opera") != -1) {
				return true;
			}
		}
		return false;
	}
	public boolean isSafari() {
		String userAgent = getUserAgent();
		if(userAgent!=null){
			if (userAgent.indexOf("Safari") != -1) {
				return true;
			}
		}
		return false;
	}
	public boolean isSearchEngine() {
		String userAgent = getUserAgent();
		if(userAgent!=null){
			if (userAgent.indexOf("Ultraseek") != -1) {
				return true;
			}
		}
		return false;
	}
	
	/**
	 * Gets the output language for the request, either <code>IWConstants.MARKUP_LANGUAGE_HTML</code> or <code>IWConstants.MARKUP_LANGUAGE_WML</code>.
	 * This methods just checks the User-agent header to see if the device is a known wap device, otherwise html is assumed
	 * @param request The request, needed to find output
	 */
	private String getDetectedClientMarkupLanguage(HttpServletRequest request) {
		//Todo: set the language to WML when the user-agent is of that type
		//--only implemented for the UPG1 test WAP browser
		// @TODO (jonas) use better method to find content types supported by client. Use rdf docs referenced in request headers.x
		String mlParam = request.getParameter(IWConstants.PARAM_NAME_OUTPUT_MARKUP_LANGUAGE);
		if(mlParam!=null && mlParam.length()>0) {
			return mlParam;
		}
		boolean isWMLAgent = false;
		String user_agent = request.getHeader("User-agent");
		if (user_agent != null) {
			user_agent = user_agent.toLowerCase();
			for(int i=0; i<WML_USER_AGENTS.length; i++) {
				if(user_agent.indexOf(WML_USER_AGENTS[i]) > -1) {
					isWMLAgent = true;
					break;
				}
			}
		}
		if(isWMLAgent) {
			return IWConstants.MARKUP_LANGUAGE_WML;
		} else {
			return IWConstants.MARKUP_LANGUAGE_HTML;
		}
	}
	
	public boolean isParameterSet(String parameterName) {
		if (parameterName == null) {
			return false;
		}
		boolean theReturn = false;
		String value = getParameter(parameterName) ;
		
		if (value!= null && value.length() > 0) {
			theReturn = true;
		}
		
		value = getParameter(parameterName + ".x");
		
		if(  value != null && value.length() > 0) {
			theReturn = true;
		}
		return theReturn;
	}
	
	public boolean isParameterSetAsEmpty(String parameterName) {
		if (parameterName == null) {
			return false;
		}
		boolean theReturn = false;
		String value = getParameter(parameterName) ;
		
		if (value!= null && value.length() == 0) {
			theReturn = true;
		}
		
		value = getParameter(parameterName + ".x");
		
		if(  value != null && value.length() == 0) {
			theReturn = true;
		}
		return theReturn;
	}
	
	
	public boolean isParameterSet(Parameter parameter) {
		return isParameterSet( parameter.getName() );
	}
	
	public boolean parameterEquals(Parameter parameter) {
		boolean theReturn = false;
		if (parameter != null) {
			if (getParameter(parameter.getName()) != null) {
				if (getParameter(parameter.getName()).equals(parameter.getValueAsString())) {
					theReturn = true;
				}
			}
		}
		return theReturn;
	}
	public boolean parameterEquals(String parameterName, String parameterValue) {
		boolean theReturn = false;
		if (getParameter(parameterName) != null) {
			if (getParameter(parameterName).equals(parameterValue)) {
				theReturn = true;
			}
		}
		return theReturn;
	}
	public void setRequest(HttpServletRequest request) {
		if(this._request == null){
			this._request = request;
		} else {
			this._request = request;
			initializeAfterRequestIsSet(request);
		}
			
	}
	protected void setResponse(HttpServletResponse response) {
		this._response = response;
	}
	public void setMarkupLanguage(String language) {
		this.markupLanguage = language;
		if (language.equals(IWConstants.MARKUP_LANGUAGE_HTML)) {
			setContentType("text/html");
		}
		else if (language.equals(IWConstants.MARKUP_LANGUAGE_WML)) {
			setContentType("text/vnd.wap.wml");
		}
		else if (language.equals(IWConstants.MARKUP_LANGUAGE_PDF_XML)) {
			setContentType("application/pdf");
		}
	}
	public void setSpokenLanguage(String spokenLanguage) {
		this.spokenLanguage = spokenLanguage;
	}
	public HttpServletRequest getRequest() {
		return this._request;
	}
	public Cookie[] getCookies() {
		return this.getRequest().getCookies();
	}
	public void addCookies(Cookie cookie) {
		this.getResponse().addCookie(cookie);
	}
	public boolean isCookieSet(String cookieName) {
		Cookie[] cookies = this.getCookies();
		boolean returner = false;
		if (cookies != null) {
			if (cookies.length > 0) {
				for (int i = 0; i < cookies.length; i++) {
					if (cookies[i].getName().equals(cookieName)) {
						returner = true;
						continue;
					}
				}
			}
		}
		return returner;
	}
	public String getParameter(String parameterName) {
		String prm = null;
		if (this._multipartParameters != null) {
			prm = getMultipartParameter(parameterName);
		} else {
			prm = getRequest().getParameter(parameterName);
		}
		return prm;
	}
	public Enumeration getParameterNames() {
		if (this._multipartParameters != null) {
			return this._multipartParameters.keys();
		} else {
			return getRequest().getParameterNames();
		}
	}
	public String[] getParameterValues(String parameterName) {
		if (this._multipartParameters != null) {
			Collection values = this._multipartParameters.getCollection(parameterName);
			if (values != null) {
				return (String[]) values.toArray(new String[values.size()]);
			} else {
				return null;
			}
		} else {
			return getRequest().getParameterValues(parameterName);
		}
	}
	public String getQueryString() {
		return getRequest().getQueryString();
	}
	public HttpServletResponse getResponse() {
		return this._response;
	}
	public Object getSessionAttribute(String attributeName) {
		return getSession().getAttribute(attributeName);
	}
	public void setSessionAttribute(String attributeName, Object attribute) {
		getSession().setAttribute(attributeName, attribute);
	}
	public String getSessionId() {
		return getSession().getId();
	}
	/**
	 * @deprecated Replaced with removeSessionAttribute()
	 */
	public void removeAttribute(String attributeName) {
		removeSessionAttribute(attributeName);
	}
	public void removeSessionAttribute(String attributeName) {
		getSession().removeAttribute(attributeName);
	}
	public String getMarkupLanguage() {
		return this.markupLanguage;
	}
	public String getSpokenLanguage() {
		if (this.spokenLanguage == null) {
			this.setSpokenLanguage("IS");
		}
		return this.spokenLanguage;
	}
	/**
	 * @ deprecated replaced width getApplication
	 */
	public ServletContext getServletContext() {
		return this.servletContext;
	}
	public void setServletContext(ServletContext context) {
		this.servletContext = context;
	}
	/**
	 * @deprecated UNIMPLEMENTED
	 */
	public void maintainParameter(Parameter parameter) {
		Hashtable theParameters = (Hashtable) this.getSessionAttribute("idega_special_maintained_parameters");
		if (theParameters == null) {
			theParameters = new Hashtable();
			theParameters.put(parameter.getName(), parameter);
		} else {
			//Parameter previousParameter = theParameters.get(parameter.getName());
			theParameters.put(parameter.getName(), parameter);
		}
	}
	public String getRequestURI() {
		if(IWMainApplication.useJSF){
			FacesContext facesContext = getRealFacesContext();
			if(facesContext!=null){
				return FacesUtil.getRequestUri(facesContext);
			}
			else{
				return getRequest().getRequestURI();
			}
		}
		else{
			return getRequest().getRequestURI();
		}
	}
	public String getServerName() {
		return getRequest().getServerName();
	}
	
	public String getProtocol(){
		return getRequest().getProtocol();
	}
	
	public int getServerPort() {
		return getRequest().getServerPort();
	}
	public PrintWriter getWriter() throws IOException {
		if (this.isCacheing() && this.cacheWriter!=null) {
			return this.cacheWriter;
		} else {
			if( this.writer == null ){
				this.writer = getResponse().getWriter(); 
			}
			
			return this.writer;
		}
		
	}
	
	public boolean isWriterNull(){
		return (this.writer==null);
	}
	
	public void setWriter(PrintWriter writer){
		this.writer = writer;
	}
	public void sendRedirect(String URL) {
		try {
			getResponse().sendRedirect(getResponse().encodeRedirectURL(URL));
		} catch (IOException e) {
			e.printStackTrace(System.err);
		}
	}
	public void setApplicationAttribute(String attributeName, Object attributeValue) {
		getIWMainApplication().setAttribute(attributeName, attributeValue);
	}
	public Object getApplicationAttribute(String attributeName) {
		return getIWMainApplication().getAttribute(attributeName);
	}
	
	public Object getApplicationAttribute(String attributeName, Object defaultObjectToReturnIfValueIsNull) {
		return getIWMainApplication().getAttribute(attributeName,defaultObjectToReturnIfValueIsNull);
	}
	
	public void removeApplicationAttribute(String attributeName) {
		getIWMainApplication().removeAttribute(attributeName);
	}
	public IWMainApplication getIWMainApplication() {
		return IWMainApplication.getIWMainApplication(getServletContext());
	}
	public IWMainApplicationSettings getApplicationSettings() {
		return getIWMainApplication().getSettings();
	}
	public IWSystemProperties getSystemProperties() {
		return getIWMainApplication().getSystemProperties();
	}
	public UserProperties getUserProperties() {
		return LoginBusinessBean.getUserProperties(this);
	}
	public Locale getCurrentLocale() {
		Locale theReturn = (Locale) this.getSessionAttribute(LOCALE_ATTRIBUTE);
		if (theReturn == null) {
			theReturn = getIWMainApplication().getSettings().getDefaultLocale();
			setCurrentLocale(theReturn);
		}
		return theReturn;
	}
	public int getCurrentLocaleId() {
		return ICLocaleBusiness.getLocaleId(getCurrentLocale());
	}
	public void setCurrentLocale(Locale locale) {
		this.setSessionAttribute(LOCALE_ATTRIBUTE, locale);
	}
	/**
	 * Sets the object with Weak reference so that it could be garbagecollected anytime
	 */
	public void setSessionAttributeWeak(String attributeName, Object attributeValue) {
		getWeakHashMap().put(attributeName, attributeValue);
	}
	public Object getSessionAttributeWeak(String propertyName) {
		return getWeakHashMap().get(propertyName);
	}
	private Map getWeakHashMap() {
		WeakHashMap map = (WeakHashMap) getSessionAttribute(WEAK_HASHMAP_KEY);
		if (map == null) {
			map = new WeakHashMap();
			setSessionAttribute(WEAK_HASHMAP_KEY, map);
		}
		return map;
	}
	
	/**
	 * Only handles http and https, use getServerURLWithoutProtocol() for other stuff.
	 * @return the servername with port and protocol, e.g. http://www.idega.com:8080/
	 */
	public String getServerURL(){
		StringBuffer buf = new StringBuffer();
		if(isSecure()){
			buf.append("https://");
		}
		else{
			buf.append("http://");
		}

		buf.append(getServerName());
		if( getServerPort()!=80 ){
			buf.append(":").append(getServerPort());
		}
		
		buf.append("/");
		
		return buf.toString();
	}
	
	/**
	 * 
	 * @return the servername with port and protocol, e.g. http://www.idega.com:8080/
	 */
	public String getServerURLWithoutProtocol(){
		StringBuffer buf = new StringBuffer();

		buf.append(getServerName());
		if( getServerPort()!=80 ){
			buf.append(":").append(getServerPort());
		}
		
		buf.append("/");
		
		return buf.toString();
	}
	
	public void setContentType(String contentType) {
		
		String encoding = getApplicationSettings().getCharacterEncoding();
		getResponse().setContentType(contentType+CHARACTER_SET_PREFIX+encoding);
		//getResponse().setContentType(contentType);
		//text/html;charset=ISO-8859-1
	}
	void setCacheing(boolean ifCacheing) {
		this.isCaching = ifCacheing;
		if(ifCacheing==false){
			//make sure these are nulled when stopping cacheing
			this.cacheResponseWriter=null;
			this.cacheWriter=null;
		}
	}
	boolean isCacheing() {
		return this.isCaching;
	}
	public void setCacheWriter(PrintWriter writer) {
		this.cacheWriter = writer;
	}
	public void setCacheResponseWriter(ResponseWriter writer) {
		this.cacheResponseWriter = writer;
	}

	/**
	 * @deprecated Replaced with getCurrentUser()
	 **/
	public User getUser() {
		return (LoginBusinessBean.getUser(this));
	}
	public int getUserId() {
		User usr = getUser();
		if (usr != null) {
		    Number id = (Number)usr.getPrimaryKey();
		    if(id!=null){
		        return id.intValue();
		    }
		}
		return -1;
	}
	public AccessController getAccessController() {
		return (this.getIWMainApplication().getAccessController());
	}
	public String getRequestContentType() {
		return getRequest().getContentType();
	}
	public String getRemoteIpAddress() {
		return getRequest().getRemoteAddr();
	}
	public String getRemoteHostName() {
		return getRequest().getRemoteHost();
	}
	public boolean hasPermission(String permissionKey, PresentationObject obj) {
		try {
			return this.getAccessController().hasPermission(permissionKey, obj, this);
		} catch (Exception ex) {
			ex.printStackTrace();
			return false;
		}
	}
	public boolean hasViewPermission(PresentationObject obj) {
		return this.hasPermission(AccessController.PERMISSION_KEY_VIEW, obj);
	}
	public boolean hasEditPermission(PresentationObject obj) {
		return this.hasPermission(AccessController.PERMISSION_KEY_EDIT, obj);
	}
	public boolean hasPermission(List groupIds, String permissionKey, PresentationObject obj) {
		try {
			return this.getAccessController().hasPermission(groupIds, permissionKey, obj, this);
		} catch (Exception ex) {
			ex.printStackTrace();
			return false;
		}
	}
	public boolean hasFilePermission(String permissionKey, int id) {
		try {
			return this.getAccessController().hasFilePermission(permissionKey, id, this);
		} catch (Exception ex) {
			ex.printStackTrace();
			return false;
		}
	}
	public boolean hasDataPermission(String permissionKey, ICObject obj, int entityRecordId) {
		try {
			return this.getAccessController().hasDataPermission(permissionKey, obj, entityRecordId, this);
		} catch (Exception ex) {
			ex.printStackTrace();
			return false;
		}
	}
	public boolean hasViewPermission(List groupIds, PresentationObject obj) {
		return this.hasPermission(groupIds, AccessController.PERMISSION_KEY_VIEW, obj);
	}
	public boolean hasEditPermission(List groupIds, PresentationObject obj) {
		return this.hasPermission(groupIds, AccessController.PERMISSION_KEY_EDIT, obj);
	}
	
	
	public boolean isSuperAdmin() {
		try {
			if (this.isLoggedOn()) {
				return this.getUser().equals(this.getAccessController().getAdministratorUser());
			}
		} catch (Exception ex) {
			ex.printStackTrace();
		}
		return false;
	}
	public boolean isLoggedOn() {
		return com.idega.core.accesscontrol.business.LoginBusinessBean.isLoggedOn(this);
	}
	/**
	 * Expensive method, not recommended to use frequently
	 *
	 * @throws UnavailableIWContext if the IWContext is not set
	 *
	 */
	public static IWContext getInstance() throws UnavailableIWContext {
		IWContext theReturn = com.idega.servlet.IWPresentationServlet.getIWContext();
		if(theReturn==null){
			try{
				//If no IWContext is found then try to get the FacesContext:
				FacesContext fc = FacesContext.getCurrentInstance();
				if(fc!=null){
					theReturn = getIWContext(fc);
				}
			}
			catch(Exception e){
				e.printStackTrace();
				throw new UnavailableIWContext();
			}
		}
		return theReturn;
	}
	public String getCurrentState(PresentationObject obj) {
		if (obj != null) {
			return getCurrentState(obj.getParentObjectInstanceID());
		} else {
			return null;
		}
	}
	/**
	 * @todo implement
	 */
	public String getCurrentState(int instanceId) {
		String historyId = this.getParameter(PRM_HISTORY_ID);
		//System.err.println("in iwc.getCurrentState()");
		if (historyId != null) {
			//System.err.println("historyId != null");
			HttpSession s = this.getSession();
			//System.err.println(" - from Session.hashCode() -> "+s.hashCode());
			List historyList = (List) s.getAttribute(SESSION_OBJECT_STATE);
			//List historyList = (List)this.getSessionAttribute(BuilderLogic.SESSION_OBJECT_STATE);
			if (historyList != null && historyList.contains(historyId)) {
				int index = historyList.indexOf(historyId);
				//System.err.println("current state historyIndex = "+index + " for instance " + instanceId);
				Object ob = ((Hashtable) historyList.get(index + 1)).get(Integer.toString(instanceId));
				//System.err.println("current state = "+ob);
				//System.err.println("iwc.getCurrentState() ends");
				return (String) ob;
			}
		}
		//System.err.println("iwc.getCurrentState() ends");
		return null;
	}
	public IWApplicationContext getApplicationContext() {
		return this.getIWMainApplication().getIWApplicationContext();
	}
	/**
	 * Gets if this object is in "Preview" mode in the Builder or in regular view not inside the Builder.
	 * @return true if in preview mode
	 */
	public boolean isInPreviewMode() {
		boolean preview = false;
		if (isParameterSet("view")) {
			if (isBuilderApplicationRunning()) {
				String view = getParameter("view");
				if (view.equals("preview")) {
					preview = true;
				}
			}
		}
		return (preview);
	}
	/**
	 * Gets if this object is in "Edit" mode in the Builder
	 * @return true if in edit mode
	 */
	public boolean isInEditMode() {
		boolean edit = false;
		if (isParameterSet("view")) {
			if (isBuilderApplicationRunning()) {
				String view = getParameter("view");
				if (view.equals("builder")) {
					edit = true;
				}
			}
		}
		return (edit);
	}
	private boolean isBuilderApplicationRunning(){
		return getIWMainApplication().isBuilderApplicationRunning(this);
	}
	
	/**
	 * @return true if the client is a handheld device such as a PalmPilot, a PocketPC device or a phone
	 */
	public boolean isClientHandheld() {
		if (!this._doneHandHeldCheck) {
			String user_agent = this.getUserAgent();
			if (user_agent.indexOf("Windows CE") != -1) {
				this._clientIsHandHeld = true;
			} else if (user_agent.indexOf("Palm") != -1) {
				this._clientIsHandHeld = true;
			} else if (user_agent.toLowerCase().indexOf("wap") != -1) {
				this._clientIsHandHeld = true;
			} else if (user_agent.toLowerCase().indexOf("nokia") != -1) {
				this._clientIsHandHeld = true;
			} else if (user_agent.toLowerCase().indexOf("ericsson") != -1) {
				this._clientIsHandHeld = true;
			} else if (user_agent.toLowerCase().indexOf("symbian") != -1) {
				this._clientIsHandHeld = true;
			} else if (user_agent.toLowerCase().indexOf("wapman") != -1) {
				this._clientIsHandHeld = true;
			}
			this._doneHandHeldCheck = true;
		}
		return this._clientIsHandHeld;
	}
	public ICDomain getDomain() {
	    ICDomain domain = getIWMainApplication().getIWApplicationContext().getDomain();
	    //if(domain!=null &&  domain.getServerName()!=null)
	    if(domain!=null){
	    		String setServerName = domain.getServerName();
	    		if(setServerName==null){
	    			String newServerURL = getServerURL();
	    			domain.setServerName(newServerURL);
	    		}
	    }
		return domain;
	}
	
	public ICDomain getDomainByServerName(String serverName) {
		return getIWMainApplication().getIWApplicationContext().getDomainByServerName(serverName);
	}
	
	public void forwardToIBPage(Page fromPage, ICPage page){
		forwardToIBPage(fromPage,((Integer) page.getPrimaryKey()).intValue());
	}
	
	public void forwardToIBPage(Page fromPage, int pageID){
		forwardToIBPage(fromPage,pageID,0);
	}
	
	public void forwardToIBPage(Page fromPage, int pageID,int secondInterval) {
		try
		{
			BuilderService bs;
			bs = BuilderServiceFactory.getBuilderService(this.getApplicationContext());
			String url = bs.getPageURI(pageID);
			forwardToURL(fromPage,url,secondInterval);
		}
		catch (RemoteException e)
		{
			e.printStackTrace();
		}
		
	}
	/**
	 * Forwards to the url specified by setting a meta (refresh) header into the page object given by fromPage.
	 * @param fromPage
	 * @param url
	 */
	public void forwardToURL(Page fromPage, String url) {
		forwardToURL(fromPage,url,-1);
	}
	
	/**
	 * Forwards to the url specified by setting a meta (refresh) header into the page object given by fromPage.
	 * @param fromPage
	 * @param url
	 * @param secondInterval
	 */
	public void forwardToURL(Page fromPage, String url,int secondInterval) {
		/**@todo temporary workaround find out why this doesn't work
		 * This is supposed to work but I always get: IllegalStateException: cannot forward because writer or stream has been obtained.
		 */
		/*try{
		 RequestDispatcher req = this.getRequest().getRequestDispatcher(BuilderLogic.getInstance().getIBPageURL(this.getApplicationContext(),((Integer)page.getPrimaryKeyValue()).intValue()));
		 req.forward(this.getRequest(),this.getResponse());
		 }
		 catch(Exception e){
		 e.printStackTrace(System.err);
		 }

		 this does not work either
		 sendRedirect(URL.toString());
		 */
		StringBuffer URL = new StringBuffer(url);
		//try
		//{
			//bs = BuilderServiceFactory.getBuilderService(this.getApplicationContext());
			//URL.append(bs.getPageURI(pageID));
		
			String requestString = getRequest().getQueryString();
			if(requestString!=null){
				if(url.indexOf("?")==-1){
					URL.append('?');
				}
				else{
					URL.append('&');
				}
				URL.append(requestString);
			}
			if(secondInterval>0) {
				fromPage.setToRedirect(URL.toString(),secondInterval);
			}
			else {
				fromPage.setToRedirect(URL.toString());
			}
			fromPage.empty();
		//}
		//catch (RemoteException e)
		//{
		//	e.printStackTrace();
		//}
		//URL.append(BuilderLogic.getInstance().getIBPageURL(this.getApplicationContext(), pageID));
	}

	/*
	 *  Returns null if not found
	 */
	public Cookie getCookie(String cookieName) {
		Cookie[] cookies = this.getCookies();

		if (cookies != null) {
			if (cookies.length > 0) {
				for (int i = 0 ; i < cookies.length ; i++) {
					if ( cookies[i].getName().equals(cookieName) ) {
						return cookies[i];
					}
				}
			}
		}

		return null;
	}


	/**
	 * Gets the current user associated with this context
	 * <br>This method is meant to replace getUser()
	 * @return The current user if there is one associated with the current context. If there is none the method returns null.
	 * @throws NotLoggedOnException if no user is logged on.
	 **/
	public com.idega.user.data.User getCurrentUser(){
		com.idega.core.user.data.User user = getUser();
		if(user!=null){
			try{
				String sessKey = IWC_SESSION_ATTR_NEW_USER_KEY+user.getPrimaryKey().toString();
				com.idega.user.data.User newUser = (com.idega.user.data.User) getSessionAttribute(sessKey);
				if(newUser==null){
					newUser = Converter.convertToNewUser(user);
					setSessionAttribute(sessKey,newUser);
				}
				return newUser;
			}
			catch(Exception e){
				throw new RuntimeException("IWContext.getCurrentUser(): Error getting primary key of user. Exception was: "+e.getClass().getName()+" : "+e.getMessage());
			}
		}
		else{
			throw new NotLoggedOnException();
		}
		//return null;
	}

	/**
	 * Gets the Id of the current user associated with this context
	 * <br>This method is meant to replace getUserId()
	 * @return The Id of the current user. If there is one associated with the current context.
	 * @throws NotLoggedOnException if no user is logged on
	 **/
	public int getCurrentUserId(){
		com.idega.user.data.User user = getCurrentUser();
		//if(user!=null){
			return ((Integer)user.getPrimaryKey()).intValue();
		//}
	}
	
	/**
	 * TODO reimplement
	 * @return The pageId for the current IBPage that is being displayed. Returns -1 if an error occurred.
	 */
	public int getCurrentIBPageID(){
		BuilderService bs;
		try
		{
			bs = BuilderServiceFactory.getBuilderService(this.getApplicationContext());
			return bs.getCurrentPageId(this);
		}
		catch (RemoteException e)
		{
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		return -1;
	}

	public boolean isSecure(){
		return getRequest().isSecure();
	}
	
	/*
	 * BEGIN ABSTRACT METHODS FROM FacesContext
	 */

	/* (non-Javadoc)
	 * @see javax.faces.context.FacesContext#getLocale()
	 */
	public Locale getLocale()
	{
		return this.getCurrentLocale();
	}


	/* (non-Javadoc)
	 * @see javax.faces.context.FacesContext#release()
	 */
	public void release()
	{
		getRealFacesContext().release();
	}

	/* (non-Javadoc)
	 * @see javax.faces.context.FacesContext#renderResponse()
	 */
	public void renderResponse()
	{
		getRealFacesContext().renderResponse();
	}

	/* (non-Javadoc)
	 * @see javax.faces.context.FacesContext#responseComplete()
	 */
	public void responseComplete()
	{
		getRealFacesContext().responseComplete();
	}

	/* (non-Javadoc)
	 * @see javax.faces.context.FacesContext#setLocale(java.util.Locale)
	 */
	public void setLocale(Locale arg0)
	{
		this.setCurrentLocale(arg0);
	}

	/* (non-Javadoc)
	 * @see javax.faces.context.FacesContext#addMessage(java.lang.String, javax.faces.application.FacesMessage)
	 */
	public void addMessage(String arg0, FacesMessage arg1) {
		getRealFacesContext().addMessage(arg0,arg1);
	}

	/* (non-Javadoc)
	 * @see javax.faces.context.FacesContext#getClientIdsWithMessages()
	 */
	public Iterator getClientIdsWithMessages() {
		return getRealFacesContext().getClientIdsWithMessages();
	}

	/* (non-Javadoc)
	 * @see javax.faces.context.FacesContext#getExternalContext()
	 */
	public ExternalContext getExternalContext() {
		return getRealFacesContext().getExternalContext();
	}

	/* (non-Javadoc)
	 * @see javax.faces.context.FacesContext#getMaximumSeverity()
	 */
	public Severity getMaximumSeverity() {
		return getRealFacesContext().getMaximumSeverity();
	}

	/* (non-Javadoc)
	 * @see javax.faces.context.FacesContext#getMessages()
	 */
	public Iterator getMessages() {
		return getRealFacesContext().getMessages();
	}

	/* (non-Javadoc)
	 * @see javax.faces.context.FacesContext#getMessages(java.lang.String)
	 */
	public Iterator getMessages(String arg0) {
		return getRealFacesContext().getMessages(arg0);
	}

	/* (non-Javadoc)
	 * @see javax.faces.context.FacesContext#getRenderKit()
	 */
	public RenderKit getRenderKit() {
		return getRealFacesContext().getRenderKit();
	}

	/* (non-Javadoc)
	 * @see javax.faces.context.FacesContext#getRenderResponse()
	 */
	public boolean getRenderResponse() {
		return getRealFacesContext().getRenderResponse();
	}

	/* (non-Javadoc)
	 * @see javax.faces.context.FacesContext#getResponseComplete()
	 */
	public boolean getResponseComplete() {
		return getRealFacesContext().getResponseComplete();
	}

	/* (non-Javadoc)
	 * @see javax.faces.context.FacesContext#getResponseStream()
	 */
	public ResponseStream getResponseStream() {
		return getRealFacesContext().getResponseStream();
	}

	/* (non-Javadoc)
	 * @see javax.faces.context.FacesContext#getResponseWriter()
	 */
	public ResponseWriter getResponseWriter() {
		if (this.isCacheing() && this.cacheResponseWriter!=null) {
			return this.cacheResponseWriter;
		} else {
			return getRealFacesContext().getResponseWriter();
		}
	}

	/* (non-Javadoc)
	 * @see javax.faces.context.FacesContext#getViewRoot()
	 */
	public UIViewRoot getViewRoot() {
		return getRealFacesContext().getViewRoot();
	}

	/* (non-Javadoc)
	 * @see javax.faces.context.FacesContext#setResponseStream(javax.faces.context.ResponseStream)
	 */
	public void setResponseStream(ResponseStream arg0) {
		getRealFacesContext().setResponseStream(arg0);
	}

	/* (non-Javadoc)
	 * @see javax.faces.context.FacesContext#setResponseWriter(javax.faces.context.ResponseWriter)
	 */
	public void setResponseWriter(ResponseWriter arg0) {
		getRealFacesContext().setResponseWriter(arg0);
	}

	/* (non-Javadoc)
	 * @see javax.faces.context.FacesContext#setViewRoot(javax.faces.component.UIViewRoot)
	 */
	public void setViewRoot(UIViewRoot arg0) {
		getRealFacesContext().setViewRoot(arg0);
	}

	/* (non-Javadoc)
	 * @see javax.faces.context.FacesContext#getApplication()
	 */
	public Application getApplication() {
		return getRealFacesContext().getApplication();
	}
	
	/**
	*Gets the real (underlying) FacesContext instance
	*/
	private FacesContext getRealFacesContext(){
		return this.realFacesContext;
	}
	/**
	 *Sets the real (underlying) FacesContext instance
	 **/
	private void setRealFacesContext(FacesContext fc){
		this.realFacesContext=fc;
	}
	
	/**
	 * This method gets the header value for the attribute "Authorization" which is
	 * used e.g. for getting username and password in BASIC Authorization/Authentication request
	 * @return Returns the header value for "Authorization" attribute
	 */
	public String getAuthorizationHeader(){
		return getRequest().getHeader("Authorization");
	}

	/* (non-Javadoc)
	 * @see com.idega.idegaweb.IWUserContext#getUserPrincipal()
	 */
	public Principal getUserPrincipal() {
		return getRequest().getUserPrincipal();
	}

	/* (non-Javadoc)
	 * @see com.idega.idegaweb.IWUserContext#isUserInRole(java.lang.String)
	 */
	public boolean isUserInRole(String role) {
		return getRequest().isUserInRole(role);
	}
	
	/* (non-Javadoc)
	 * @see com.idega.idegaweb.IWUserContext#getRemoteUser()
	 */
	public String getRemoteUser() {
		return getRequest().getRemoteUser();
	}

	public String getIdegaSessionId(){
		String sessionId = (String) getSessionAttribute(IDEGA_SESSION_KEY);
		if(sessionId==null){
			sessionId = UUIDGenerator.getInstance().generateUUID();
			setSessionAttribute(IDEGA_SESSION_KEY,sessionId);
		}
		return sessionId;
	}
	
}