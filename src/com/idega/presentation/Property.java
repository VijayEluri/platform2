/*
 * $Id: Property.java,v 1.1 2004/12/23 21:34:31 tryggvil Exp $ Created on 21.12.2004
 * 
 * Copyright (C) 2004 Idega Software hf. All Rights Reserved.
 * 
 * This software is the proprietary information of Idega hf. Use is subject to
 * license terms.
 */
package com.idega.presentation;

import java.lang.reflect.Method;
import com.idega.core.builder.data.ICPage;
import com.idega.core.file.data.ICFile;
import com.idega.user.data.Group;
import com.idega.user.data.GroupHome;
import com.idega.util.reflect.MethodFinder;

/**
 * 
 * Last modified: $Date: 2004/12/23 21:34:31 $ by $Author: tryggvil $
 * 
 * @author <a href="mailto:tryggvil@idega.com">Tryggvi Larusson </a>
 * @version $Revision: 1.1 $
 */
public class Property {

	private String propertyName;

	private Method method;

	private Object[] propertyValues;

	public Property(String methodIdentifier) {
		this(methodIdentifier, null);
	}

	public Property(String methodIdentifier, Class declaringClass) {
		if (declaringClass != null) {
			setMethod(getMethodFinder().getMethod(methodIdentifier, declaringClass));
		}
		else {
			setMethod(getMethodFinder().getMethod(methodIdentifier));
		}
	}

	public Property(Method method) {
	}

	private MethodFinder getMethodFinder() {
		return MethodFinder.getInstance();
	}

	/**
	 * @return Returns the method.
	 */
	public Method getMethod() {
		return method;
	}

	/**
	 * @param method
	 *            The method to set.
	 */
	public void setMethod(Method method) {
		this.method = method;
	}

	/**
	 * @return Returns the propertyName.
	 */
	public String getPropertyName() {
		return propertyName;
	}

	/**
	 * @param propertyName
	 *            The propertyName to set.
	 */
	public void setPropertyName(String propertyName) {
		this.propertyName = propertyName;
	}

	/**
	 * @return Returns the propertyValues.
	 */
	public Object[] getPropertyValues() {
		return propertyValues;
	}

	/**
	 * @param propertyValues
	 *            The propertyValues to set.
	 */
	public void setPropertyValues(String[] stringPropertyValues) {
		//this.propertyValues = propertyValues;
		Object[] args = new Object[stringPropertyValues.length];
		Class[] parameterTypes = getMethod().getParameterTypes();
		for (int i = 0; i < parameterTypes.length; i++) {
			if (parameterTypes[i] != null) {
				String aString = stringPropertyValues[i];
				try {
					args[i] = convertStringToObject(parameterTypes[i], aString);
				}
				catch (Exception e) {
					System.err.println("Error in property '" + method.toString());
					e.printStackTrace();
				}
			}
		}
		setPropertyValues(args);
	}

	/**
	 * @param propertyValues
	 *            The propertyValues to set.
	 */
	public void setPropertyValues(Object[] propertyValues) {
		this.propertyValues = propertyValues;
	}

	/**
	 * Sets the property on the object instance
	 * 
	 * @param instance
	 */
	public void setPropertyOnInstance(Object instance) {
		try {
			getMethod().invoke(instance, getPropertyValues());
		}
		catch (Exception e) {
			if (instance instanceof PresentationObject) {
				PresentationObject po = (PresentationObject) instance;
				System.err.println("Error in property '" + getMethod().toString() + "' for ICObjectInstanceId="
						+ po.getICObjectInstanceID());
			}
			else {
				System.err.println("Error in property '" + getMethod().toString() + "' for instance="
						+ instance.toString());
			}
			e.printStackTrace();
		}
	}

	//Moved from ComponentPropertyHandler (in builder)
	protected Object convertStringToObject(Class parameterType, String stringValue) throws Exception {
		Object argument = null;
		if (parameterType.equals(Integer.class) || parameterType.equals(Integer.TYPE)) {
			//try{
			argument = new Integer(stringValue);
			//}
			//catch(NumberFormatException e){
			//  e.printStackTrace(System.out);
			//}
		}
		else if (parameterType.equals(String.class)) {
			argument = stringValue;
		}
		else if (parameterType.equals(Boolean.class) || parameterType.equals(Boolean.TYPE)) {
			if (stringValue.equals("Y")) {
				argument = Boolean.TRUE;
			}
			else if (stringValue.equals("N")) {
				argument = Boolean.FALSE;
			}
			else {
				argument = new Boolean(stringValue);
			}
		}
		else if (parameterType.equals(Float.class) || parameterType.equals(Float.TYPE)) {
			argument = new Float(stringValue);
		}
		else if (parameterType.equals(ICPage.class)) {
			//try {
			argument = ((com.idega.core.builder.data.ICPageHome) com.idega.data.IDOLookup.getHomeLegacy(ICPage.class)).findByPrimaryKeyLegacy(Integer.parseInt(stringValue));
			//}
			//catch (Exception ex) {
			//  ex.printStackTrace(System.err);
			//}
		}
		else if (parameterType.equals(ICFile.class)) {
			try {
				argument = ((com.idega.core.file.data.ICFileHome) com.idega.data.IDOLookup.getHome(ICFile.class)).findByPrimaryKey(new Integer(
						stringValue));
			}
			catch (Exception ex) {
				ex.printStackTrace(System.err);
			}
		}
		//        else if(parameterType.equals(IBTemplatePage.class)){
		//          try {
		//            argument = new IBTemplatePage(Integer.parseInt(stringValue));
		//          }
		//          catch (Exception ex) {
		//            ex.printStackTrace(System.err);
		//          }
		//        }
		else if (parameterType.equals(Image.class)) {
			//try {
			argument = new Image(Integer.parseInt(stringValue));
			//}
			//catch (Exception ex) {
			//  ex.printStackTrace(System.err);
			//}
		}
		//REMOVE AND MAKE GENERIC! ask tryggvi and eiki
		else if (parameterType.equals(Group.class)) {
			try {
				argument = (Group) ((GroupHome) com.idega.data.IDOLookup.getHome(Group.class)).findByPrimaryKey(new Integer(
						stringValue.substring(stringValue.lastIndexOf('_') + 1, stringValue.length())));
			}
			catch (Exception ex) {
				ex.printStackTrace(System.err);
			}
		}
		return argument;
	}
}