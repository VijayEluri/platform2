// idega 2000 - Tryggvi Larusson
/*
 * Copyright 2000 idega.is All Rights Reserved.
 */

package com.idega.presentation.ui;

import java.rmi.RemoteException;
import java.util.ArrayList;
import java.util.Enumeration;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Vector;

import com.idega.core.builder.business.BuilderService;
import com.idega.core.builder.business.ICBuilderConstants;
import com.idega.core.builder.data.ICPage;
import com.idega.core.localisation.business.LocaleSwitcher;
import com.idega.event.IWPresentationEvent;
import com.idega.idegaweb.IWConstants;
import com.idega.idegaweb.IWMainApplication;
import com.idega.idegaweb.IWStyleManager;
import com.idega.presentation.IWContext;
import com.idega.presentation.Page;
import com.idega.presentation.PresentationObject;
import com.idega.presentation.PresentationObjectContainer;
import com.idega.presentation.Script;

/**
 * @author <a href="mailto:tryggvi@idega.is">Tryggvi Larusson </a>
 * @version 1.2
 */
public class Form
// TODO: Move to extend UIForm
		// extends UIForm{
		extends InterfaceObject {

	public static final String ACTION_ON_BLUR = "onblur";

	public static final String ACTION_ON_CHANGE = "onchange";

	public static final String ACTION_ON_CLICK = "onclick";

	public static final String ACTION_ON_FOCUS = "onfocus";

	public static final String ACTION_ON_KEY_PRESS = "onkeypress";

	public static final String ACTION_ON_KEY_DOWN = "onkeydown";

	public static final String ACTION_ON_KEY_UP = "onkeyup";

	public static final String ACTION_ON_SELECT = "onselect";

	public static final String ACTION_ON_SUBMIT = "onsubmit";

	private static final String IB_PAGE_PARAMETER = ICBuilderConstants.IB_PAGE_PARAMETER;

	private Window window;

	private Vector maintainedParameters;

	private boolean maintainAllParameters;

	private PresentationObject submitToObject;

	// private Parameter controlParameter;
	private Map controlParameters;

	private Class windowClass;

	private int icObjectInstanceIDForWindow = -1;

	private Class classToInstanciateAndSubmitTo;

	private int _submitToPage = -1;

	private Script associatedScript = null;

	private boolean sendToHTTPS = false;

	private static String FORM_EVENT_PARAMETER = "idega_special_form_event";

	private static String HTTP = "http";

	private static String HTTPS = "https";

	private static String COLONSLASHSLASH = "://";

	private static String SLASH = "/";

	private boolean _disableObject;

	private Map _objectsToDisable;

	private boolean _disableOnSubmit;

	private boolean showLoadingLayerOnSubmit = true;

	private boolean printLoadingLayer = false;

	/**
	 * Defaults to send to the page itself and the POST method
	 */
	public Form() {
		// super();
		setName(getID());
		setMethod("post");
		maintainAllParameters = false;
		initialize();
	}

	/**
	 * Defaults to the POST method
	 */
	public Form(String action) {
		this(action, "post");
	}

	public Form(Class classToInstanciateAndSubmitTo) {
		// this(IWMainApplication.getObjectInstanciatorURL(classToInstanciateAndSubmitTo));
	}

	public Form(String actionURL, String method) {
		// super();
		setName(getID());
		setMethod(method);
		setAction(actionURL);
		maintainAllParameters = false;
		initialize();
	}

	public Form(Class classToInstanciateAndSubmitTo, String method) {
		// this(IWMainApplication.getObjectInstanciatorURL(classToInstanciateAndSubmitTo),method);
		this.setMethod(method);
		this.setClassToInstanciateAndSendTo(classToInstanciateAndSubmitTo);
	}

	/**
	 * Use this constructor to submit this form to a "pop-up" window
	 */
	public Form(Window myWindow) {
		setName(getID());
		setWindow(myWindow);
		maintainAllParameters = false;
		initialize();
	}

	public void initialize() {
		add(new Parameter(FORM_EVENT_PARAMETER, this.getID()));
		this.add(new HiddenInput(IWMainApplication.IWEventSessionAddressParameter, ""));
	}

	private void setOnAction(String actionType, String action) {
		/*
		 * String attributeName = actionType; String previousAttribute =
		 * getAttribute(attributeName); if (previousAttribute == null) {
		 * setAttribute(attributeName, action);
		 *  } else { if (getAttribute(attributeName).indexOf(action) != -1)
		 * setAttribute(attributeName, previousAttribute + ";" + action); }
		 */
		setMarkupAttributeMultivalued(actionType, action);
	}

	public void setAction(String actionURL) {
		setMarkupAttribute("action", actionURL);
	}

	protected String getAction() {
		return getMarkupAttribute("action");
	}

	public void setMethod(String method) {
		setMarkupAttribute("method", method);
	}

	public String getMethod() {
		return getMarkupAttribute("method");
	}

	public void setTarget(String target) {
		setMarkupAttribute("target", target);
	}

	public String getTarget() {
		return getMarkupAttribute("target");
	}

	protected void setWindow(Window window) {
		this.window = window;
	}

	public void setMultiPart() {
		setMarkupAttribute("enctype", "multipart/form-data");
	}

	public void setOnReset(String script) {
		setOnAction("onreset", script);
	}

	public void setOnSubmit(String script) {
		setOnAction("onsubmit", script);
	}

	public void setOnClick(String script) {
		setOnAction("onclick", script);
	}

	private List findAllfindAllInterfaceObjectsHelper(List vector, PresentationObjectContainer cont) {
		List objects = cont.getChildren();
		if (objects != null) {
			// for (Enumeration enum = objects.elements();enum.hasMoreElements();){
			for (Iterator iter = objects.iterator(); iter.hasNext();) {
				PresentationObject mo = (PresentationObject) iter.next();
				if (mo instanceof PresentationObjectContainer) {
					if (mo instanceof InterfaceObject) {
						vector.add((InterfaceObject) mo);
					}
					else {
						vector = findAllfindAllInterfaceObjectsHelper(vector, (PresentationObjectContainer) mo);
					}
				}
			}
		}
		return vector;
	}

	public InterfaceObject[] findAllInterfaceObjects() {

		List vector = new Vector();

		vector = findAllfindAllInterfaceObjectsHelper(vector, this);

		return (InterfaceObject[]) vector.toArray(new InterfaceObject[1]);

	}

	private String getIdegaSpecialRequestURI(IWContext iwc) {
		if (iwc.getParameter("idegaspecialrequesttype") == null) {
			return iwc.getRequestURI();
		}
		else {

			// return
			// encodeSpecialRequestString(iwc.getRequest().getParameter("idegaspecialrequesttype"),iwc.getRequest().getParameter("idegaspecialrequestname"),iwc);
			add(new Parameter("idegaspecialrequesttype", iwc.getParameter("idegaspecialrequesttype")));
			add(new Parameter("idegaspecialrequestname", iwc.getParameter("idegaspecialrequestname")));
			return iwc.getRequestURI();
		}
	}

	public void main(IWContext iwc) throws Exception {
		// Chech if there is some class set
		setActionToInstanciatedClass(iwc);

		if (this._submitToPage != -1) {
			// Set a builder page as the action
			BuilderService bservice = getBuilderService(iwc);
			this.setAction(bservice.getPageURI(_submitToPage));
		}
		if (window != null) {
			// iwc.setSessionAttribute(IdegaWebHandler.windowOpenerParameter,window);
			com.idega.servlet.WindowOpener.storeWindow(iwc, window);
		}
		if (_disableObject) {
			getScript().addFunction("disableObject", "function disableObject (inputs,value) {\n	if (inputs.length > 1) {\n	\tfor(var i=0;i<inputs.length;i++)\n	\t\tinputs[i].disabled=eval(value);\n	\t}\n	else\n	inputs.disabled=eval(value);\n}");
			if (_disableOnSubmit) {

				Iterator iter = _objectsToDisable.keySet().iterator();
				while (iter.hasNext()) {
					String name = (String) iter.next();
					String values = (String) _objectsToDisable.get(name);
					setCheckSubmit();
					getScript().addToFunction("checkSubmit", "disableObject(findObj('" + name + "'),'" + String.valueOf(values) + "')");
				}
			}
		}
		// temporary not allowing when event handler used
		handleLoadingLayer(iwc);
		handleKeepStatus(iwc);

	}

	private void handleLoadingLayer(IWContext iwc) {
		if (getParentPage() != null && showLoadingLayerOnSubmit && getTarget() == null) {// &&
																																											// !("iw_event_frame").equals(target)){

			if (getParentPage().getAssociatedBodyScript().getFunction("showLoadingLayer") == null)
				getParentPage().getAssociatedScript().addFunction("showLoadingLayer", getShowLoadingLayerScript(iwc));

			if (getParentPage().getAssociatedBodyScript().getFunction("createLoadingLayer") == null)
				getParentPage().getAssociatedBodyScript().addFunction("createLoadingLayer", getCreateLoadingLayerScript(iwc));

			// getParentPage().setStyleDefinition("DIV.LoadLayer","visibility:hidden;position:absolute;");
			IWStyleManager manager = IWStyleManager.getInstance();
			if (!manager.isStyleSet("DIV.LoadLayer"))
				IWStyleManager.getInstance().setStyle("DIV.LoadLayer", "visibility:hidden;position:absolute;font-family: arial;" + "font-size: 9pt;font-weight: bold; background: #ffffff; border-style: ridge;border-color: #cbcbcb;" + "border-width: 2px;padding-top: 4px;padding-left: 8px;padding-right: 12px;padding-bottom:4px; top:48%;left:45%;");
			// setOnClick("this.disabled=true;showLoadingLayer();this.form.submit();");
			// getForm().setOnSubmit("showLoadingLayer();");
			// setOnSubmitFunction("displayLoadingLayer","function
			// displayLoadingLayer(theInput,message){ \n\t showLoadingLayer();\n\t
			// return true;\n}","");
			// getParentPage().setOnUnLoad("showLoadingLayer();");
			// /setOnSubmit("showLoadingLayer();");
			//setCheckSubmit();
			printLoadingLayer = true;
		}
	}

	private String getShowLoadingLayerScript(IWContext iwc) {
		StringBuffer script = new StringBuffer();
		// String imageUrl =
		// iwc.getIWMainApplication().getCoreBundle().getImageURI("loading_notext.gif");
		script.append(" var loaded = false;\n");
		script.append("function showLoadingLayer(){ ").append("\n");
		script.append("  var theDiv = findObj( \"busybuddy\" );").append("\n");
		script.append("  if( !theDiv ) { ").append("\n");
		script.append("     return; ").append("\n");
		script.append("   }").append("\n");
		String target = getTarget();
		if (target != null) {
			// script.append("alert(window.opener.top['"+target+"'].ReadyState);\n");
			script.append("   theTargetFrame = findObj('" + target + "',window.opener);\n");
			script.append("   if(theTargetFrame){\n");
			// script.append(" window.status='target found';");

		}
		script.append("    if( theDiv.style ) { ").append("\n");
		script.append("      theDiv.style.visibility = 'visible';").append("\n");
		script.append("    } else {").append("\n");
		script.append("      theDiv.visibility = 'show' ;").append("\n");
		// script.append(" theDiv.visibility = 'visible';").append("\n");
		script.append("    }").append("\n");

		if (target != null) {

			script.append("   }\n");
		}
		script.append("}").append("\n");

		return script.toString();
	}

	private String getCreateLoadingLayerScript(IWContext iwc) {
		StringBuffer script = new StringBuffer();
		String imageUrl = iwc.getIWMainApplication().getCoreBundle().getImageURI("loading_notext.gif");

		script.append("	if(!loaded){ ").append("\n");
		script.append("document.write('<div id=\"busybuddy\" class=\"LoadLayer\" >");
		script.append("<div id=\"busybuddy-contents\" class=\"LoadLayerContents\" >");
		script.append("<img src=\"" + imageUrl + "\" onError=\"this.width=1;this.height=1;\">&nbsp;");
		script.append(iwc.getIWMainApplication().getCoreBundle().getResourceBundle(iwc).getLocalizedString("loading_text", "Loading"));
		script.append("</div>");
		script.append("</div>') ").append("\n");
		script.append("loaded = true; ").append("\n");
		script.append("	} ").append("\n");

		return script.toString();
	}

	/**
	 * Set to show a loading image in middle of window, when button pressed
	 * 
	 * @param show
	 */
	public void setToShowLoadingOnSubmit(boolean show) {
		this.showLoadingLayerOnSubmit = show;
	}

	/**
	 * Converts the set action POST/GET to an HTTPS url
	 */
	private void convertActionToHTTPS(IWContext iwc) {
		String action = getAction();
		if (action != null) {
			if (action.startsWith(HTTP)) {
				if (action.startsWith(HTTPS)) {
					// nothing
				}
				else {
					setAction(HTTPS + action.substring(4, action.length()));
				}
			}
			else {
				setAction(HTTPS + COLONSLASHSLASH + iwc.getRequest().getServerName() + iwc.getRequestURI());
			}
		}
	}

	public void addParameter(String parameterName, String parameterValue) {
		add(new Parameter(parameterName, parameterValue));
	}

	/**
	 * 
	 */
	public void addParameter(String parameterName, int parameterValue) {
		addParameter(parameterName, Integer.toString(parameterValue));
	}

	public void maintainAllParameters() {
		maintainAllParameters = true;
	}

	/**
	 * Creates a hidden field if there is an action on the form again
	 */
	public void maintainParameter(String parameterName) {
		if (maintainedParameters == null) {
			maintainedParameters = new Vector();
		}
		maintainedParameters.addElement(parameterName);
	}

	/**
	 * Creates a hidden fields for each param if there is an action on the form
	 * again
	 */
	public void maintainParameters(List params) {
		if (params != null) {
			if (maintainedParameters == null) {
				maintainedParameters = new Vector();
			}
			maintainedParameters.addAll(params);
		}
	}

	protected void setCheckSubmit() {
		if (getScript().getFunction("checkSubmit") == null) {
			getScript().addFunction("checkSubmit", "function checkSubmit(inputs){\n\n}");
			setOnSubmit("return checkSubmit(this)");
		}
	}

	/*
	 * 
	 */
	private void addGloballyMaintainedParameters(IWContext iwc) {
		List list = com.idega.idegaweb.IWURL.getGloballyMaintainedParameters(iwc);
		if (list != null) {
			Iterator iter = list.iterator();
			while (iter.hasNext()) {
				String parameterName = (String) iter.next();
				String parameterValue;
				if (parameterName.equals(IB_PAGE_PARAMETER) && _submitToPage > 0) {
					// fix for multipart forms
					parameterValue = Integer.toString(_submitToPage);
				}
				else {
					parameterValue = iwc.getParameter(parameterName);
				}
				if (parameterValue != null) {
					if (!this.isParameterSet(parameterName)) {
						addParameter(parameterName, parameterValue);
					}
				}
			}
		}
	}

	/*
	 * 
	 */
	private void addTheMaintainedBuilderParameters(IWContext iwc) {
		List list = com.idega.idegaweb.IWURL.getGloballyMaintainedBuilderParameters(iwc);
		// System.out.println("--------------------------------------");
		// System.out.println("builderPrm");
		if (list != null) {
			Iterator iter = list.iterator();
			while (iter.hasNext()) {
				String parameterName = (String) iter.next();
				String parameterValue;
				// System.out.print("parameterName = "+parameterName+" , parameterValue
				// = "+parameterValue+" parameterSet = ");
				if (parameterName.equals(IB_PAGE_PARAMETER) && _submitToPage > 0) {
					// fix for multipart forms
					parameterValue = Integer.toString(_submitToPage);
				}
				else {
					parameterValue = iwc.getParameter(parameterName);
				}
				if (parameterValue != null) {
					if (!this.isParameterSet(parameterName)) {
						// System.out.println("false");
						addParameter(parameterName, parameterValue);
					}
					else {
						// System.out.println("true");
					}
				}
				else {
					// System.out.println("null");
				}
			}
		}
	}

	/**
	 * temp implementation
	 */
	public boolean isParameterSet(String prmName) {
		// if (this.theObjects != null) {
		Iterator iter = getChildren().iterator();
		while (iter.hasNext()) {
			PresentationObject item = (PresentationObject) iter.next();
			if (prmName.equals(item.getName())) {
				return true;
			}
		}
		// }
		return false;
	}

	/**
	 * For printing out the maintained hidden parameters
	 * 
	 * Currently not implemented well enough, parameters should be dynamically
	 * added
	 */
	private void addTheMaintainedParameters(IWContext iwc) {

		/**
		 * Should be probably deprecated if the "submitTo()" function is deprecated
		 */
		/*
		 * if(submitToObject!= null){ String treeID=submitToObject.getTreeID();
		 * if(treeID!=null){ this.add(new
		 * Parameter("idega_special_tree_node",treeID)); } }
		 */

		// setEventListener(LocaleSwitcher.class.getName());
		addParameter(LocaleSwitcher.languageParameterString, iwc.getCurrentLocale().toString());

		this.addGloballyMaintainedParameters(iwc);
		this.addTheMaintainedBuilderParameters(iwc);

		if (maintainAllParameters) {
			/*
			 * if (iwc.getParameter("idega_special_form_parameter") != null) {
			 * PresentationObjectContainer cont = (PresentationObjectContainer)
			 * iwc.getSessionAttribute("idega_special_form_parameters"); if (cont !=
			 * null) { this.add(cont); } } else {
			 */

			PresentationObjectContainer cont = new PresentationObjectContainer();
			for (Enumeration enumer = iwc.getParameterNames(); enumer.hasMoreElements();) {
				String tempString = (String) enumer.nextElement();
				cont.add(new Parameter(tempString, iwc.getParameter(tempString)));
			}
			cont.add(new Parameter("idega_special_form_parameter", ""));
			this.add(cont);
			// iwc.setSessionAttribute("idega_special_form_parameters", cont);
			// }
		}
		else if (maintainedParameters != null) {
			for (Enumeration e = maintainedParameters.elements(); e.hasMoreElements();) {

				String tempParameter = (String) e.nextElement();

				if (iwc.getParameter(tempParameter) != null) {
					String[] strings = iwc.getParameterValues(tempParameter);
					for (int i = 0; i < strings.length; i++) {
						add(new Parameter(tempParameter, strings[i]));
					}

					// this.add(new
					// Parameter(tempParameter,iwc.getParameter(tempParameter)));
				}

			}
		}

		/*
		 * Map globalMaintainedParameters = iwc.getTheMaintainedParameters(); if
		 * (globalMaintainedParameters!=null){ for (Enumeration enum =
		 * globalMaintainedParameters.getE.getParameterNames();enum.hasMoreElements();){
		 * String tempString = (String)enum.nextElement(); add(); } }
		 */
	}

	public void print(IWContext iwc) throws Exception {
		if (getScript().doesFunctionExist("checkSubmit")) {
			if (printLoadingLayer) {
				getScript().addToFunction("checkSubmit", "showLoadingLayer();");
			}
			getScript().addToFunction("checkSubmit", "return true;");
		}
		else if (printLoadingLayer) {
			this.setOnSubmit("showLoadingLayer()");
		}
		// if ( doPrint(iwc) ){

		addTheMaintainedParameters(iwc);
		if (window != null) {
			// setAction(window.getURL(iwc)+"?idega_session_id="+iwc.getSession().getId());
			setAction(window.getURL(iwc));
			setTarget(window.getTarget());
			// setTarget("#");
			setOnSubmit(window.getCallingScriptStringForForm(iwc));
		}

		if (getAction() == null) {
			// setAction(getIdegaSpecialRequestURI(iwc)+"?idega_session_id="+iwc.getSession().getId());
			setAction(getIdegaSpecialRequestURI(iwc));
		}
		if (sendToHTTPS) {
			convertActionToHTTPS(iwc);
		}

		if (getMarkupLanguage().equals("HTML")) {
			String markup = iwc.getApplicationSettings().getProperty(Page.MARKUP_LANGUAGE, Page.HTML);
			// String Action = getAction();
			// if (Action.indexOf("idega_session_id") == -1){
			// setAction(Action+"?idega_session_id="+iwc.getSession().getId());
			// }
			if (getAssociatedFormScript() != null)
				add(getAssociatedFormScript());

			println("<form " + (markup.equals(Page.HTML) ? "name=\"" + getName() + "\"" : "") + getMarkupAttributesString() + " >");
			super.print(iwc);
			print("</form>");
		}
		else if (getMarkupLanguage().equals("WML")) {
			if (getAction() == null || _submitToPage != -1) {
				// setAction(getIdegaSpecialRequestURI(iwc)+"?idega_session_id="+iwc.getSession().getId());
				setAction(getIdegaSpecialRequestURI(iwc));
			}
			// print("<onevent type=\"onenterforward\" >");
			// print("<refresh>");

			InterfaceObject[] allInterfaceObjects = findAllInterfaceObjects();
			StringBuffer postFields = new StringBuffer();

			SubmitButton theButton = new SubmitButton();
			List parameters = new ArrayList();
			boolean useFieldset = false;

			for (int j = 0; j < allInterfaceObjects.length; j++) {
				if (!(allInterfaceObjects[j] instanceof Parameter) && !(allInterfaceObjects[j] instanceof GenericButton)) {
					String name = allInterfaceObjects[j].getName();
					if (name != null && !"null".equals(name)) {
						// print("<setvar name=\"" + name);
						String value = allInterfaceObjects[j].getValueAsString();
						if (value != null) {
							// print("\" value=\""+value+"\" />");
						}
						else {
							// print("\" value=\"\" />");
						}
						postFields.append("<postfield name=\"" + name + "\" value=\"$" + name + "\" />");
					}
					useFieldset = true;
				}
				else if (allInterfaceObjects[j] instanceof Parameter || allInterfaceObjects[j] instanceof HiddenInput) {
					parameters.add(allInterfaceObjects[j]);
				}
				if (allInterfaceObjects[j] instanceof SubmitButton) {
					theButton = (SubmitButton) allInterfaceObjects[j];
				}
			}
			// print("</refresh>");
			// print("</onevent>");
			if (useFieldset) {
				print("<p><fieldset>");
			}
			super.print(iwc);
			if (useFieldset) {
				print("</fieldset></p>");
			}

			// print("<do type=\"accept\" label=\""+theButton.getContent()+"\">");
			print("<p><strong><anchor>" + theButton.getContent());
			String url = getAction();
			url = iwc.getResponse().encodeURL(url);
			print("<go href=\"" + url + "\" method=\"" + getMethod() + "\" >");
			print(postFields.toString());

			for (Iterator iter = parameters.iterator(); iter.hasNext();) {
				Parameter child = (Parameter) iter.next();
				child.printWML(iwc);
			}
			print("</go>");
			print("</anchor></strong></p>");
			// print("</do>");

		}
		// };
		// else{
		// super.print(iwc);
		// }
	}

	public Object clone() {
		Form obj = null;
		try {
			obj = (Form) super.clone();

			if (this.submitToObject != null) {
				obj.submitToObject = (PresentationObject) this.submitToObject.clone();
			}
			if (window != null) {
				obj.window = (Window) this.window.clone();
			}

			if (maintainedParameters != null) {
				obj.maintainedParameters = (Vector) this.maintainedParameters.clone();
			}

			// if(controlParameter != null){
			// obj.controlParameter = (Parameter)this.controlParameter.clone();
			// }

			if (controlParameters != null) {
				obj.controlParameters = (Map) ((HashMap) this.controlParameters).clone();
			}

			if (windowClass != null) {
				obj.windowClass = this.windowClass;
			}

			obj.maintainAllParameters = this.maintainAllParameters;
			obj.classToInstanciateAndSubmitTo = this.classToInstanciateAndSubmitTo;
		}
		catch (Exception ex) {
			ex.printStackTrace(System.err);
		}

		return obj;
	}

	/*
	 * protected Parameter getControlParameter(){ return controlParameter; }
	 */

	protected Map getControlParameters() {
		if (controlParameters == null) {
			controlParameters = new HashMap();
		}
		return controlParameters;
	}

	/**
	 * @deprecated replaced with addControlParameter
	 */
	protected void setControlParameter(String parameterName, String parameterValue) {
		addControlParameter(new Parameter(parameterName, parameterValue));
	}

	/**
	 * @deprecated replaced with addControlParameter
	 */
	protected void setControlParameter(Parameter parameter) {
		addControlParameter(parameter);
		/*
		 * if (controlParameter==null){ controlParameter=parameter;
		 * add(controlParameter); }
		 */
	}

	/**
	 * Sets the interface object(s) with the given name to be enabled when this
	 * object receives the action specified.
	 * 
	 * @param action
	 *          The action to perform on.
	 * @param objectToEnable
	 *          The name of the interface object(s) to enable.
	 * @param enable
	 *          Set to true to disable, false will enable.
	 */
	public void setToDisableOnAction(String action, String objectName, boolean disable) {
		_disableObject = true;
		setOnAction(ACTION_ON_SUBMIT, "disableObject(findObj('" + objectName + "'),'" + String.valueOf(objectName) + "')");
	}

	/**
	 * Sets the interface object(s) to be enabled when this object receives the
	 * action specified.
	 * 
	 * @param action
	 *          The action to perform on.
	 * @param objectToEnable
	 *          The interface object(s) to enable.
	 * @param enable
	 *          Set to true to disable, false will enable.
	 */
	public void setToDisableOnAction(String action, InterfaceObject object, boolean disable) {
		setToDisableOnAction(action, object.getName(), disable);
	}

	/**
	 * Sets the interface object(s) to be enabled when this <code>Form</code> is
	 * submitted.
	 * 
	 * @param objectToEnable
	 *          The interface object(s) to enable.
	 * @param enable
	 *          Set to true to disable, false will enable.
	 */
	public void setToDisableOnSubmit(InterfaceObject object, boolean disable) {
		_disableOnSubmit = true;
		_disableObject = true;
		if (_objectsToDisable == null)
			_objectsToDisable = new HashMap();
		_objectsToDisable.put(object.getName(), String.valueOf(disable));
	}

	protected void addControlParameter(String parameterName, String parameterValue) {
		addControlParameter(new Parameter(parameterName, parameterValue));
	}

	protected void addControlParameter(Parameter parameter) {
		Parameter param = (Parameter) getControlParameters().get(parameter.getName());
		if (param == null) {
			getControlParameters().put(parameter.getName(), parameter);
			add(parameter);
		}
	}

	public void setEventListener(Class eventListenerClass) {
		setEventListener(eventListenerClass.getName());
	}

	public void setEventListener(String eventListenerClassName) {
		add(new Parameter(IWMainApplication.IdegaEventListenerClassParameter, IWMainApplication.getEncryptedClassName(eventListenerClassName)));
	}

	public void sendToControllerFrame() {
		this.setTarget(IWConstants.IW_CONTROLLER_FRAME_NAME);
	}

	public void setWindowToOpen(Class windowClass) {
		this.windowClass = windowClass;
		// setAction(IWMainApplication.windowOpenerURL);
		if (IWMainApplication.useNewURLScheme) {
			this.setAction(getIWApplicationContext().getIWMainApplication().getWindowOpenerURI(windowClass));
		}
		else {
			addParameter(Page.IW_FRAME_CLASS_PARAMETER, IWMainApplication.getEncryptedClassName(windowClass));
		}
		setWindow(Window.getStaticInstance(windowClass));
	}

	public void setWindowToOpen(Class windowClass, int instanceId) {
		setWindowToOpen(windowClass);
		// setAction(IWMainApplication.windowOpenerURL);
		// addParameter(Page.IW_FRAME_CLASS_PARAMETER,windowClass.getName());
		// this.addParameter(IWMainApplication._PARAMETER_IC_OBJECT_INSTANCE_ID,instanceId);
		this.icObjectInstanceIDForWindow = instanceId;
	}

	public void setPageToSubmitTo(int ibPageID) {
		// this.setAction(com.idega.idegaweb.IWMainApplication.BUILDER_SERVLET_URL+"?"+com.idega.builder.business.BuilderLogic.IB_PAGE_PARAMETER+"="+ibPageID);
		if (IWMainApplication.useNewURLScheme) {
			try {
				setAction(this.getBuilderService(getIWApplicationContext()).getPageURI(ibPageID));
			}
			catch (RemoteException e) {
				e.printStackTrace();
			}
		}
		else {
			this._submitToPage = ibPageID;
		}
	}

	public void setPageToSubmitTo(ICPage page) {
		setPageToSubmitTo(((Integer) page.getPrimaryKey()).intValue());
	}

	public void setPadding(int padding) {
		setMarkupAttribute("padding", padding);
	}

	public void setPaddingLeft(int padding) {
		setMarkupAttribute("padding-left", padding);
	}

	public void setPaddingRight(int padding) {
		setMarkupAttribute("padding-right", padding);
	}

	public void setPaddingTop(int padding) {
		setMarkupAttribute("padding-top", padding);
	}

	public void setPaddingBottom(int padding) {
		setMarkupAttribute("padding-bottom", padding);
	}

	public void setClassToInstanciateAndSendTo(Class presentationObjectClass) {
		this.classToInstanciateAndSubmitTo = presentationObjectClass;
	}

	public void setClassToInstanciateAndSendTo(Class presentationObjectClass, IWContext iwc) {
		this.setClassToInstanciateAndSendTo(presentationObjectClass);
		setActionToInstanciatedClass(iwc);
	}

	private void setActionToInstanciatedClass(IWContext iwc) {
		if (this.classToInstanciateAndSubmitTo != null) {
			this.setAction(iwc.getIWMainApplication().getObjectInstanciatorURI(classToInstanciateAndSubmitTo));
		}
	}

	private void setURIToWindowOpenerClass(IWContext iwc) {
		if (this.windowClass != null) {
			// setURL(iwc.getApplication().getWindowOpenerURI());
			// addParameter(Page.IW_FRAME_CLASS_PARAMETER,_windowClass);
			if (this.icObjectInstanceIDForWindow <= 0) {
				setAction(iwc.getIWMainApplication().getWindowOpenerURI(windowClass));
			}
			else {
				setAction(iwc.getIWMainApplication().getWindowOpenerURI(windowClass, icObjectInstanceIDForWindow));
				// this.addParameter(IWMainApplication._PARAMETER_IC_OBJECT_INSTANCE_ID,icObjectInstanceIDForWindow);
			}
		}
	}

	public void addEventModel(IWPresentationEvent model, IWContext iwc) {
		Iterator iter = model.getParameters();
		while (iter.hasNext()) {
			Parameter prm = (Parameter) iter.next();
			this.add(prm);
		}
		setTarget(model.getEventTarget());
		setAction(model.getEventHandlerURL(iwc));
	}

	/**
	 * Returns the associatedScript.
	 * 
	 * @return Script
	 */
	public Script getAssociatedFormScript() {
		if (associatedScript == null) {
			setAssociatedFormScript(new Script());
		}
		return associatedScript;
	}

	protected Script getScript() {
		return getAssociatedFormScript();
	}

	/**
	 * Sets the associatedScript.
	 * 
	 * @param associatedScript
	 *          The associatedScript to set
	 */
	public void setAssociatedFormScript(Script associatedScript) {
		this.associatedScript = associatedScript;
	}

	/**
	 * Set the form to automatically send over to a corresponding HTTPS address
	 */
	public void setToSendToHTTPS() {
		setToSendToHTTPS(true);
	}

	/**
	 * Set if the form should automatically send over to a corresponding HTTPS
	 * address
	 */
	public void setToSendToHTTPS(boolean doSendToHTTPS) {
		sendToHTTPS = doSendToHTTPS;
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see com.idega.presentation.PresentationObject#getName()
	 */
	public String getName() {
		return super.getID();
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see com.idega.presentation.PresentationObject#setName(java.lang.String)
	 */
	public void setName(String name) {
		super.setID(name);
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see com.idega.presentation.ui.InterfaceObject#handleKeepStatus(com.idega.presentation.IWContext)
	 */
	public void handleKeepStatus(IWContext iwc) {
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see com.idega.presentation.PresentationObject#isContainer()
	 */
	public boolean isContainer() {
		return true;
	}

	// TODO: Commit in these methods when extension is moved to be UIForm
	/*
	 * 
	 * 
	 * //Legacy methods reimplemented from PresentationObject -> UIComponent move
	 * public void add(UIComponent component){ getChildren().add(component); }
	 * 
	 * public String getID(){ return getId(); }
	 * 
	 * public void empty() { getChildren().clear(); }
	 * 
	 * public void add(String text){ add(new Text(text)); }
	 * 
	 * public void addBreak(){ add(Text.getBreak()); }
	 * 
	 * private Map markupAttributesMap;
	 * 
	 * protected Map getMarkupAttributes(){ if (markupAttributesMap==null){
	 * markupAttributesMap=new HashMap(); } return markupAttributesMap; }
	 * 
	 * protected void setMarkupAttribute(String key,String value){
	 * getMarkupAttributes().put(key,value); }
	 * 
	 * protected void setMarkupAttribute(String key,int value){
	 * getMarkupAttributes().put(key,new Integer(value)); }
	 * 
	 * protected String getMarkupAttribute(String key){ return
	 * (String)getMarkupAttributes().get(key); } public Page getParentPage() {
	 * return PresentationObjectUtil.getParentPage(this); }
	 * 
	 */

} // Class ends
