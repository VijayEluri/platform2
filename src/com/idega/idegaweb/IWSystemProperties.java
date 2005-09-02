package com.idega.idegaweb;

import java.io.File;
import java.io.FileInputStream;
import java.util.Locale;
import javax.ejb.FinderException;
import com.idega.core.data.ICApplicationBinding;
import com.idega.core.data.ICApplicationBindingHome;
import com.idega.core.file.data.ICFile;
import com.idega.core.file.data.ICFileHome;
import com.idega.data.IDOLookup;

/**
 * @author Laddi
 *
 * To change this generated comment edit the template variable "typecomment":
 * Window>Preferences>Java>Templates.
 * To enable and disable the creation of type comments go to
 * Window>Preferences>Java>Code Generation.
 */
public class IWSystemProperties extends IWPropertyList {

	private final static String IW_BUNDLE_IDENTIFIER = "com.idega.idegaweb";
	private IWMainApplication _application;
	private final static String PROPERTY_NAME = "system_property_file_id";
	private ICFile systemProperties;
	
	public IWSystemProperties(IWMainApplication application) {
//		super();
		_application = application;
		loadPropertiesFile(application);
	}
	
	private void loadPropertiesFile(IWMainApplication application) {
		try {
			ICApplicationBindingHome iab = (ICApplicationBindingHome) IDOLookup.getHome(ICApplicationBinding.class);
			String icFileID = null;
			try {
				ICApplicationBinding binding = iab.findByPrimaryKey(PROPERTY_NAME);
				icFileID = binding.getValue();
			} catch (FinderException e) {
			}
			
			if (icFileID != null) {
				ICFileHome home = (ICFileHome) IDOLookup.getHome(ICFile.class);
				ICFile icFile = home.findByPrimaryKey(new Integer(icFileID));
				systemProperties = icFile;
				super.load(icFile.getFileValue());
			} else {
				File file = createFile(application.getPropertiesRealPath(), "system_properties.pxml");
				
				ICFileHome home = (ICFileHome) IDOLookup.getHome(ICFile.class);
				ICFile icFile = home.create();
				icFile.setFileValue(new FileInputStream(file));
				icFile.setName(file.getName());
				icFile.store();

				ICApplicationBinding binding = iab.create();
				binding.setKey(PROPERTY_NAME);
				binding.setValue(icFile.getPrimaryKey().toString());
				binding.store();
				
				systemProperties = icFile;
				file.delete();
				super.load(icFile.getFileValue());
			}
		} catch (Exception e) {
			e.printStackTrace();
		}
	}

	public String getLocalizedName(Locale locale,String propertyName) {
		IWProperty property = getIWProperty(propertyName);
		if ( property != null )
			return getLocalizedName(locale,property);
		return null;
	}
	
	public String getLocalizedName(Locale locale,IWProperty property) {
		return _application.getBundle(IW_BUNDLE_IDENTIFIER).getResourceBundle(locale).getLocalizedString(property.getName(), property.getName());
	}
	
	public IWPropertyList getProperties(String propertyListName) {
		IWPropertyList list = getPropertyList(propertyListName);
		if ( list == null )
			list = this.getNewPropertyList(propertyListName);
		return list;
	}
	
	public void store() {
		super.store(systemProperties.getFileValueForWrite());
		systemProperties.store();
	}
	
	public void unload() {
		systemProperties = null;
		super.unload();
	}
}
