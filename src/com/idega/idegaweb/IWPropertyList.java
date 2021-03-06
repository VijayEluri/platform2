/*
 * $Id: IWPropertyList.java,v 1.25.2.1 2007/01/12 19:31:59 idegaweb Exp $
 *
 * Copyright (C) 2001 Idega hf. All Rights Reserved.
 *
 * This software is the proprietary information of Idega hf.
 * Use is subject to license terms.
 *
 */
package com.idega.idegaweb;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Vector;
import com.idega.util.FileUtil;
import com.idega.util.ListUtil;
import com.idega.xml.XMLDocument;
import com.idega.xml.XMLElement;
import com.idega.xml.XMLException;
import com.idega.xml.XMLOutput;
import com.idega.xml.XMLParser;

/**
 * @author <a href="mailto:tryggvi@idega.is">Tryggvi Larusson</a>
 * @version 0.8 - Under development
 */
public class IWPropertyList {
	private XMLDocument xmlDocument;
	private File xmlFile;
	private XMLElement parentElement;

	private XMLElement mapElement;
	static String DEFAULT_FILE_ENDING=".pxml";
	private static String rootElementTag = "pxml";
	static String dictTag = "dict";
	static String mapTag = "map";
	static String nameTag = "name";
	static String arrayTag = "array";
	static String keyTag = "key";
	static String valueTag = "value";
	static String typeTag = "type";
	static String stringTag = "string";
	static String stringString = "java.lang.String";
	static String backupEnding = ".bak";

	IWPropertyList() {
	}

	IWPropertyList(XMLElement parentElement) {
		setParentElement(parentElement);
	}

	/**
	 * @param parentElement
	 */
	void setParentElement(XMLElement parentElement)
	{
		this.parentElement = parentElement;
	}
	void setMapElement(XMLElement mapElement){
		getParentElement().removeContent(this.mapElement);
		this.mapElement=mapElement;
		this.getParentElement().addContent(mapElement);
	}

	public IWPropertyList(String fileNameWithPath) {
		load(fileNameWithPath);
	}

	/**
	 * Creates the file and superfolders if createFileAndFolder is true
	 */
	public IWPropertyList(String path, String fileNameWithoutFullPath, boolean createFileAndFolder) {
		File file = null;
		if (createFileAndFolder) {
			file = createFile(path, fileNameWithoutFullPath);
		}
		else {
			file = new File(path + FileUtil.getFileSeparator() + fileNameWithoutFullPath);
		}
		load(file);
	}

	/**
	 * <p>
	 * TODO gimmi describe method createFile
	 * </p>
	 * @param path
	 * @param fileNameWithoutFullPath
	 * @param file
	 * @return
	 */
	protected File createFile(String path, String fileNameWithoutFullPath) {
		File file = null;
		try {
			file = new File(path, fileNameWithoutFullPath);
			// added 08.02.2002 by aron: was before
			// if(!file.exists() )
			if (!file.exists() || file.length() == 0) {
				System.err.println("Creating new " + fileNameWithoutFullPath);
				file = FileUtil.getFileAndCreateIfNotExists(path, fileNameWithoutFullPath);
				FileOutputStream stream = new FileOutputStream(file);
				char[] array = ("<" + rootElementTag + "></" + rootElementTag + ">").toCharArray();
				for (int i = 0; i < array.length; i++) {
					stream.write(array[i]);
				}
				stream.flush();
				stream.close();
			}

		}
		catch (IOException ex) {
			ex.printStackTrace();
		}
		return file;
	}

	public IWPropertyList(File file) {
		load(file);
	}

	XMLElement getParentElement() {
		return this.parentElement;
	}

	XMLElement getMapElement() {
		if (this.mapElement == null) {
			this.mapElement = this.parentElement.getChild(mapTag);
			if (this.mapElement == null) {
				XMLElement dictElement = this.parentElement.getChild(dictTag);
				if (dictElement != null) {
					this.mapElement = new XMLElement(mapTag);
					this.mapElement.setChildren(dictElement.getChildren());
					this.parentElement.removeContent(dictElement);
					setMapElement(this.mapElement);
				}
			}
			if (this.mapElement == null) {
				this.mapElement = new XMLElement(mapTag);
				setMapElement(this.mapElement);
			}
		}
		return this.mapElement;
	}

	public void setProperty(String key, Object value) {
		setProperty(key, value.toString(), value.getClass().getName());
	}

	public void setProperty(String key, int value) {
		setProperty(key, new Integer(value));
	}

	public void setProperty(String key, boolean value) {
		setProperty(key, new Boolean(value));
	}

	public void setProperty(String key, String value) {
		setProperty(key, value, stringString);
	}

	public void setProperty(String key, Object[] value) {
		XMLElement keyElement = findKeyElement(key);
		IWProperty.setProperty(keyElement, key, value, this);
	}

	public IWProperty getNewProperty() {
		return new IWProperty(this);
	}

	public void setProperties(Map properties) {
		if (properties != null) {
			Iterator iter = properties.keySet().iterator();
			while (iter.hasNext()) {
				Object oKey = iter.next();
				String sKey = oKey.toString();
				Object oValue = properties.get(oKey);
				if (oValue instanceof Map) {
					IWPropertyList iwp = this.getPropertyList(sKey);
					if (iwp == null) {
						iwp = this.getNewPropertyList(sKey);
					}
					iwp.setProperties((Map) oValue);
				}
				else {
					//String sValue = oValue.toString();
					setProperty(sKey, oValue);
				}
			}
		}
	}

	/**
	 * Returns null if there is no IWProperty associated with the specific key
	 */
	public IWProperty getIWProperty(String key) {
		XMLElement el = this.findKeyElement(key);
		if (el != null) {
			return new IWProperty(el, this);
		}
		return null;
	}

	/**
	 * Returns null if there is no IWPropertyList associated with the specific key
	 * Throws IWNotPropertyListException if this IWProperty has a Single Property not a PropertyList
	 */
	public IWPropertyList getPropertyList(String key) throws IWNotPropertyListException {
		XMLElement keyElement = this.findKeyElement(key);
		if (keyElement != null) {
			return IWProperty.getPropertyList(keyElement);
		}
		return null;
	}

	/**
	 * Same as getPropertyList(String key)
	 * Returns null if there is no IWPropertyList associated with the specific key
	 * Throws IWNotPropertyListException if this IWProperty has a Single Property not a PropertyList
	 */
	public IWPropertyList getIWPropertyList(String key) throws IWNotPropertyListException {
		return getPropertyList(key);
	}

	/**
	 * Creates a new IWPropertyList associated with the specific key
	 */
	public IWPropertyList getNewPropertyList(String key) {
		XMLElement keyElement = this.findKeyElement(key);
		if (keyElement == null) {
			keyElement = IWProperty.createKeyElement(this, key);
		}
		return IWProperty.getNewPropertyList(keyElement, this);
	}

	/**
	 * Use to set an array property with only one "String" value to begin with
	 */
	public void setArrayProperty(String key, Object value) {
		setProperty(key, value, arrayTag);
	}

	void setProperty(String key, Object value, String type) {
		XMLElement keyElement = findKeyElement(key);
		IWProperty.setProperty(keyElement, key, value, type, this);
	}

	private void addProperty(String key, String value) {
		addProperty(key, value, stringString);
	}

	private void addProperty(String key, Object value) {
		addProperty(key, value.toString(), value.getClass().getName());
	}

	private void addProperty(String key, int value) {
		addProperty(key, new Integer(value));
	}

	private void addProperty(String key, Object value, String type) {
		IWProperty.addProperty(key, value, type, this);
	}

	private void addNewProperty(XMLElement key, String keyName, Object value, String type) {
		IWProperty.addNewProperty(key, keyName, value, type);
	}

	public String getPropertyType(String key) {
		return IWProperty.getPropertyType(this.findKeyElement(key));
	}

	/**
	 * Returns null if key not found
	 */
	public String getProperty(String key) {
		try {
		    XMLElement keyElement = findKeyElement(key);
		    if (keyElement != null) {
		        XMLElement childElement = keyElement.getChild(valueTag);
			    if (childElement != null) {
			        return childElement.getText();
			    }
		    }
		    return null;
		}
		catch (NullPointerException ex) {
			return null;
		}
	}
	
	public String getProperty(String key, String defaultReturnValue) {
		try {
			return findKeyElement(key).getChild(valueTag).getText();
		}
		catch (NullPointerException ex) {
			addProperty(key, defaultReturnValue);
			return defaultReturnValue;
		}
	}

	/**
	 * @return null if no match
	 */
	static XMLElement findKeyElement(XMLElement startElement, String key) {
		List list = startElement.getChildren();
		Iterator iter = list.iterator();
		while (iter.hasNext()) {
			XMLElement keyElement = (XMLElement) iter.next();
			XMLElement nameElement = keyElement.getChild(nameTag);
			if(nameElement != null){
				String childText = nameElement.getText();
				if ( childText!= null && childText.equalsIgnoreCase(key)) {
					return keyElement;
				}
			}
		}
		return null;
	}

	/**
	 * @return null if no match
	 */
	private XMLElement findKeyElement(String key) {
		return findKeyElement(getMapElement(), key);
	}

	// added by Eirikur Hrafnsson eiki@idega.is
	protected List getKeys() {
		XMLElement mapElement = getMapElement();
		if (mapElement != null) {
			List list = mapElement.getChildren();
			Iterator iter = list.iterator();
			List keys = new Vector();

			while (iter.hasNext()) {
				XMLElement keyElement = (XMLElement) iter.next();
				XMLElement nameElement = keyElement.getChild(nameTag);
				keys.add(nameElement.getText());
			}
			return keys;
		}
		else {
			return ListUtil.getEmptyList();
		}
	}

	public void load(String path) {
		File file = new File(path);
		load(file);
	}

	public void load(File file) {
		this.xmlFile = file;
		if(file.exists()){
			try {
				load(new FileInputStream(file));
			}
			catch (FileNotFoundException e) {
				System.err.println("Property file does not exist : "+this.xmlFile);
				e.printStackTrace();
			}
		}
		else{
			System.err.println("Property file does not exist : "+this.xmlFile);
		}
	}
	
	public void load(InputStream stream) {
		XMLParser builder = new XMLParser(false);
		try {
			this.xmlDocument = builder.parse(stream);
			this.parentElement = this.xmlDocument.getRootElement();
			this.mapElement = getMapElement();

		}
		catch (XMLException e) {
			e.printStackTrace();
		}
		catch (Throwable e) {
			e.printStackTrace();
		}
	}

	public void removeProperty(String key) {
		XMLElement element = this.findKeyElement(key);
		if (element != null) {
			if (this.mapElement != null) {
				this.mapElement.removeContent(element);
			}
		}
	}

	/**
	 * Returns null if no match
	 */

	private XMLElement getArrayValueElement(XMLElement arrayElement, Object value) {
		List arrayList = arrayElement.getChildren();
		Iterator iter = arrayList.iterator();
		while (iter.hasNext()) {
			XMLElement item = (XMLElement) iter.next();
			if (IWProperty.valueContains(item, value)) {
				return item;
			}
		}
		return null;
	}

	public Object getValueObject(XMLElement valueElement) {
		return getValueString(valueElement);
	}

	public String getValueString(XMLElement valueElement) {
		return valueElement.getText();
	}

	public Iterator iterator() {
		return getIWPropertyListIterator();
	}

	public IWPropertyListIterator getIWPropertyListIterator() {
		return new IWPropertyListIterator(this.getKeys().iterator(), this);
	}

	public void removeProperty(String key, Object value) {
		XMLElement element = this.findKeyElement(key);
		if (element != null) {
			XMLElement typeElement = element.getChild(typeTag);
			XMLElement valueElement = element.getChild(valueTag);
			/**
			 * if it is an array
			 */
			if (typeElement.getText().equals(arrayTag)) {
				XMLElement arrayElement = valueElement.getChild(arrayTag);
				XMLElement newValueElement = this.getArrayValueElement(arrayElement, value);
				if (newValueElement != null) {
					arrayElement.removeContent(newValueElement);
				}
			}
			else {
				if (valueElement.getText().equals(value.toString())) {
					if (this.mapElement != null) {
						this.mapElement.removeContent(element);
					}
				}
			}
		}
	}

	public void store() {
		try {
			String fileName = this.xmlFile.getName();
			String fileNameBeginning = fileName.substring(0, fileName.lastIndexOf("."));
			String fileNameEnding = fileName.substring(fileName.lastIndexOf(".") + 1);
			String tempFileName = fileNameBeginning + "-temp." + fileNameEnding;
			File tempXMLFile = new File(this.xmlFile.getParentFile(), tempFileName);
			store(new FileOutputStream(tempXMLFile));
			try {
				FileUtil.copyFile(tempXMLFile, this.xmlFile);
				FileUtil.delete(tempXMLFile);
			}
			catch (IOException io) {
				System.err.println("Error storing " + this.xmlFile.getAbsolutePath() + this.xmlFile.getName() + " " + io.getMessage());
			}
		}
		catch (FileNotFoundException e) {
			e.printStackTrace();
		}
	}

	public void delete() {
		try {
			String fileName = this.xmlFile.getName();
			File XMLFile = new File(this.xmlFile.getParentFile(), fileName);
			XMLFile.delete();
		}
		catch (Exception io) {
			System.err.println("Error deleting " + this.xmlFile.getAbsolutePath() + this.xmlFile.getName() + " " + io.getMessage());
		}
	}
			

	public void store(OutputStream stream) {
		if (this.xmlDocument != null) {
			try {
				XMLOutput outputter = new XMLOutput("  ", true);
				outputter.setLineSeparator(System.getProperty("line.separator"));
				//        outputter.setTrimText(true);
				outputter.setTextNormalize(true);
				outputter.output(this.xmlDocument, stream);
			}
			catch (IOException e) {
				e.printStackTrace();
			}
		}
	}
	
	public boolean getBooleanProperty(String propertyName)
	{
		return Boolean.valueOf(getProperty(propertyName)).booleanValue();
	}
	public void setBooleanProperty(String propertyName, boolean setValue){
		setProperty(propertyName,Boolean.toString(setValue));
	}

	public void unload(){
		this.xmlFile=null;
		this.xmlDocument=null;
		this.parentElement=null;
		this.mapElement=null;
	}
	
}
