//idega 2001 - Tryggvi Larusson
/*

*Copyright 2001 idega.is All Rights Reserved.

*/
package com.idega.idegaweb;
import java.util.Locale;
import java.io.File;
import com.idega.util.FileUtil;
import java.util.List;
import com.idega.util.LocaleUtil;
import com.idega.data.EntityControl;
import java.util.Iterator;
import java.util.Vector;
import com.idega.core.accesscontrol.business.AccessController;
/**

*@author <a href="mailto:tryggvi@idega.is">Tryggvi Larusson</a>

*@version 0.8 - Under development

*/
public class IWMainApplicationSettings extends IWPropertyList {
	private static String IW_SERVICE_CLASS_NAME = "iw_service_class_name";
	private static String DEFAULT_TEMPLATE_NAME = "defaulttemplatename";
	private static String DEFAULT_TEMPLATE_CLASS = "defaulttemplateclass";
	private static String DEFAULT_FONT = "defaultfont";
	private static String DEFAULT_FONT_SIZE = "defaultfontsize";
	private static String DEFAULT_LOCALE = "defaultlocale";
	private static String _SERVICE_CLASSES_KEY = "iw_service_class_key";
	private static final String IDO_ENTITY_BEAN_CACHING_KEY =
		"ido_entity_bean_caching";
	private static final String IDO_ENTITY_QUERY_CACHING_KEY =
		"ido_entity_query_caching";
	public static final String IW_POOLMANAGER_TYPE = "iw_poolmanager";
	public static boolean DEBUG_FLAG = false;
	public static boolean CREATE_STRINGS = false;
	public static boolean CREATE_PROPERTIES = false;
	public IWMainApplicationSettings(IWMainApplication application) {
		super(application.getPropertiesRealPath(), "idegaweb.pxml", true);
	}
	public void setDefaultTemplate(String templateName, String classname) {
		setProperty(DEFAULT_TEMPLATE_NAME, templateName);
		setProperty(DEFAULT_TEMPLATE_CLASS, classname);
	}
	public String getDefaultTemplateName() {
		return getProperty(DEFAULT_TEMPLATE_NAME);
	}
	public String getDefaultTemplateClass() {
		return getProperty(DEFAULT_TEMPLATE_CLASS);
	}
	public String getDefaultFont() {
		return getProperty(DEFAULT_FONT);
	}
	public void setDefaultFont(String fontname) {
		setProperty(DEFAULT_FONT, fontname);
	}
	public int getDefaultFontSize() {
		return Integer.parseInt(getProperty(DEFAULT_FONT_SIZE));
	}
	public void setDefaultFontSize(int size) {
		setProperty(DEFAULT_FONT_SIZE, size);
	}
	/*public void setDefaultLocale(Locale locale){
	
	setProperty(DEFAULT_LOCALE,locale.toString());
	
	}*/
	public void setDefaultLocale(Locale locale) {
		setProperty(DEFAULT_LOCALE, locale.toString());
	}
	/*public Locale getDefaultLocale(){
	
	  return (new Locale(getProperty(DEFAULT_LOCALE)));
	
	}*/
	
	/**
	 * Gets the default locale which is assigned to all users if they have not chosen a locale. 
	 *
	 * @return The set application default locale. If not set it returns the english locale.
	 **/
	public Locale getDefaultLocale() {
		String localeIdentifier = getProperty(DEFAULT_LOCALE);
		Locale locale = null;
		if (localeIdentifier == null) {
			//localeIdentifier=LocaleUtil.getIcelandicLocale().toString();
			//Set default to International English
			localeIdentifier = "en";
			locale = LocaleUtil.getLocale(localeIdentifier);
			setDefaultLocale(locale);
		}
		locale = LocaleUtil.getLocale(localeIdentifier);
		return locale;
	}
	public AccessController getDefaultAccessController() {
		return (AccessController) new com
			.idega
			.core
			.accesscontrol
			.business
			.AccessControl();
	}
	/**
	
	 * Returns false if the removing fails
	
	 */
	public boolean removeIWService(Class serviceClass) {
		return false;
	}
	/**
	
	 * Returns false if the class is wrong or it fails
	
	 */
	public boolean addIWService(Class serviceClass) {
		return false;
	}
	/**
	
	 * Returns a list of Class objects corresponding to the IWService Classes
	
	 */
	public List getServiceClasses() {
		//return null;
		IWPropertyList plist = getIWPropertyList(_SERVICE_CLASSES_KEY);
		if (plist != null) {
			List l = new Vector();
			Iterator iter = plist.iterator();
			while (iter.hasNext()) {
				IWProperty item = (IWProperty) iter.next();
				String serviceClass = item.getValue();
				try {
					l.add(Class.forName(serviceClass));
				} catch (Exception ex) {
					ex.printStackTrace();
				}
			}
			return l;
		}
		return null;
	}
	public void setEntityAutoCreation(boolean ifAutoCreate) {
		this.setProperty("entity-auto-create", ifAutoCreate);
		EntityControl.setAutoCreationOfEntities(ifAutoCreate);
	}
	public boolean getIfEntityAutoCreate() {
		String value = getProperty("entity-auto-create");
		if (value == null) {
			return false;
		} else {
			return Boolean.valueOf(value).booleanValue();
		}
	}
	public boolean getIfEntityBeanCaching() {
		String value = getProperty("ido_entity_bean_caching");
		if (value == null) {
			return false;
		} else {
			return Boolean.valueOf(value).booleanValue();
		}
	}
	public boolean getIfEntityQueryCaching() {
		String value = getProperty("ido_entity_query_caching");
		if (value == null) {
			return false;
		} else {
			return Boolean.valueOf(value).booleanValue();
		}
	}
	public void setDebug(boolean ifDebug) {
		this.setProperty("debug", ifDebug);
		setDebugMode(ifDebug);
	}
	public boolean getIfDebug() {
		String value = getProperty("debug");
		if (value == null) {
			return false;
		} else {
			return Boolean.valueOf(value).booleanValue();
		}
	}
	public static void setDebugMode(boolean debugFlag) {
		DEBUG_FLAG = debugFlag;
		com.idega.data.EntityFinder.debug = debugFlag;
	}
	public static boolean isDebugActive() {
		return DEBUG_FLAG;
	}
	public void setAutoCreateStrings(boolean ifAutoCreate) {
		this.setProperty("auto-create-localized-strings", ifAutoCreate);
		setAutoCreateStringsMode(ifAutoCreate);
	}
	public boolean getIfAutoCreateStrings() {
		String value = getProperty("auto-create-localized-strings");
		if (value == null) {
			return false;
		} else {
			return Boolean.valueOf(value).booleanValue();
		}
	}
	public static void setAutoCreateStringsMode(boolean ifAutoCreate) {
		CREATE_STRINGS = ifAutoCreate;
	}
	public static boolean isAutoCreateStringsActive() {
		return CREATE_STRINGS;
	}
	public void setEntityBeanCaching(boolean onOrOff) {
		this.setProperty(this.IDO_ENTITY_BEAN_CACHING_KEY, onOrOff);
		com.idega.data.IDOContainer.getInstance().setBeanCaching(onOrOff);
		if (!onOrOff) {
			setEntityQueryCaching(false);
		}
	}
	public void setEntityQueryCaching(boolean onOrOff) {
		this.setProperty(this.IDO_ENTITY_QUERY_CACHING_KEY, onOrOff);
		com.idega.data.IDOContainer.getInstance().setQueryCaching(onOrOff);
		if (onOrOff) {
			setEntityBeanCaching(true);
		}
	}
	public void setAutoCreateProperties(boolean ifAutoCreate) {
		this.setProperty("auto-create-properties", ifAutoCreate);
		setAutoCreatePropertiesMode(ifAutoCreate);
	}
	public boolean getIfAutoCreateProperties() {
		String value = getProperty("auto-create-properties");
		if (value == null) {
			return false;
		} else {
			return Boolean.valueOf(value).booleanValue();
		}
	}
	public static void setAutoCreatePropertiesMode(boolean ifAutoCreate) {
		CREATE_PROPERTIES = ifAutoCreate;
	}
	public static boolean isAutoCreatePropertiesActive() {
		return CREATE_PROPERTIES;
	}
	
	/**
	 * Gets the locale set for the current application for application scoped tasks.
	 * @return The set application locale. If not set it returns the default locale of the application
	 **/
	public Locale getApplicationLocale(){
		/**
		 * @todo: implement better
		 */
		return this.getDefaultLocale();
	}
}
