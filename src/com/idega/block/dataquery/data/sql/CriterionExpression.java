package com.idega.block.dataquery.data.sql;

import java.util.Collection;
import java.util.HashMap;
import java.util.Iterator;
import java.util.LinkedHashMap;
import java.util.Locale;
import java.util.Map;

import com.idega.block.dataquery.data.QueryConstants;
import com.idega.block.dataquery.data.xml.QueryConditionPart;
import com.idega.business.InputHandler;
import com.idega.idegaweb.IWResourceBundle;
import com.idega.presentation.IWContext;
import com.idega.repository.data.RefactorClassRegistry;
import com.idega.util.StringHandler;

/**
 * <p>Title: idegaWeb</p>
 * <p>Description: </p>
 * <p>Copyright: Copyright (c) 2003</p>
 * <p>Company: idega Software</p>
 * @author <a href="thomas@idega.is">Thomas Hilbig</a>
 * @version 1.0
 * Created on Jun 4, 2003
 */
public class CriterionExpression implements DynamicExpression {
  
  public static final char DOT = '.';
  public static final char WHITE_SPACE = ' ';
  public static final char APOSTROPHE = '\'';
  public static final char BRACKET_OPEN = '(';
  public static final char BRACKET_CLOSE = ')';
  public static final String OR = "OR";
  
  private static final String INTEGER = Integer.class.getName(); 
  

  private Object identifier = null;
  private String id = null;

  private String valueField = null;
  private String firstColumnClass = null;
  private String comparison = null;
  
  private String patternField = null;
  
  private boolean isDynamic = false;
  private Map identifierValueMap = null;
  private Map identifierInputDescriptionMap = null;
  
  private String inputHandlerClass = null;
  private String inputHandlerDescription = null;
  
  private Map typeSQL = null;
  private IWContext iwc = null;
  
  { 
    this.typeSQL = new HashMap();
    this.typeSQL.put(QueryConditionPart.TYPE_LIKE, "LIKE");
    this.typeSQL.put(QueryConditionPart.TYPE_EQ, "=");
    this.typeSQL.put(QueryConditionPart.TYPE_NEQ, "<>");
    this.typeSQL.put(QueryConditionPart.TYPE_LT, "<");
    this.typeSQL.put(QueryConditionPart.TYPE_GT, ">");    
    this.typeSQL.put(QueryConditionPart.TYPE_GEQ, ">=");
    this.typeSQL.put(QueryConditionPart.TYPE_LEQ, "<=");
  }
     
	public CriterionExpression(QueryConditionPart condition, Object identifier, SQLQuery sqlQuery, IWContext iwc)	{
		this.id = condition.getId();
		this.identifier = identifier;
		this.iwc = iwc;
		initialize(condition, sqlQuery, iwc);
	}	
  
  protected void initialize(QueryConditionPart condition, SQLQuery sqlQuery, IWContext iwc)	{
  	String field = condition.getField();
  	String path = condition.getPath();
 
  	// set Handler 
  	this.inputHandlerDescription = sqlQuery.getHandlerDescriptionForField(path, field); 
  	this.inputHandlerClass = sqlQuery.getInputHandlerForField(path, field);
  	this.firstColumnClass = sqlQuery.getTypeClassForField(path, field);
  	this.valueField =  sqlQuery.getUniqueNameForField(path,field);
    String type = condition.getType();
    this.comparison = (String) this.typeSQL.get(type);
    // set pattern
    this.identifierValueMap = new LinkedHashMap();
    String patternFieldName = condition.getPatternField();
  	String patternPath = condition.getPatternPath();
  	if (patternFieldName == null && patternPath == null) {
		  Object pattern = null;
		  if (condition.hasMoreThanOnePattern()) {
		  	pattern = condition.getPatterns();
		  }
		  else {
		  	pattern = condition.getPattern();
		  }
		 	this.identifierValueMap.put(this.identifier, pattern);
  	}
  	else {
  		this.patternField = sqlQuery.getUniqueNameForField(patternPath,patternFieldName);
   	}
   	this.identifierInputDescriptionMap = new LinkedHashMap();
   	String description = condition.getDescription();
   	if (description == null || description.length() == 0)	{
   	IWResourceBundle iwrb = getResourceBundle(iwc);
   		// localization
   		String localizedType = iwrb.getLocalizedString(StringHandler.concat(QueryConstants.LOCALIZATION_CONDITION_TYPE_PREFIX, type), type);
   		String localizedField = iwrb.getLocalizedString(field, field);
  		StringBuffer buffer = new StringBuffer(localizedField).append(" ").append(localizedType);
  		description =  buffer.toString();
   	}
		this.identifierInputDescriptionMap.put(this.identifier, new InputDescription(description, this.inputHandlerClass, this.inputHandlerDescription));
		this.isDynamic = condition.isDynamic();
  }
  
  private IWResourceBundle getResourceBundle(IWContext iwc) {
		Locale locale = iwc.getApplicationSettings().getDefaultLocale();
		return iwc.getIWMainApplication().getBundle(QueryConstants.QUERY_BUNDLE_IDENTIFIER).getResourceBundle(locale);
	}

  	

  public String toSQLString() {
    StringBuffer expression = 
      new StringBuffer(this.valueField).append(WHITE_SPACE);
    expression.append(this.comparison);
    Object pattern = this.identifierValueMap.get(this.identifier);
    InputHandler inputHandler = getInputHandler();
    // if the pattern is a collection use OR operator
    if (pattern != null) {
    	// if there is an inputhandler get the resulting objects
    	if (inputHandler != null) {
    		String[] patternAsArray = null;
    		if (pattern instanceof Collection) {
	    		patternAsArray = (String[]) ((Collection)pattern).toArray(new String[0]);
    		}
    		// pattern is not a collection
    		else {
    			patternAsArray = new String[] { (String) pattern };
    		}
	    	try {
	    		pattern = inputHandler.getResultingObject(patternAsArray, this.iwc);
	    	}
	    		catch (Exception ex) {
	    	}
    	}
    	// inputhandler handling finished
    	// ask again if NOW pattern is a collection (might have been changed)
    	if (pattern instanceof Collection) {
    		Iterator iterator = ((Collection) pattern).iterator();
    		// catch a special case
    		if (((Collection) pattern).size() == 1) {
    			return getSingleCondition(iterator.next()).toString();
    		}
    		StringBuffer buffer = new StringBuffer();
    		while (iterator.hasNext()) {
	    		Object singlePattern = iterator.next();
	    		buffer.append(BRACKET_OPEN).append(WHITE_SPACE);
	    		StringBuffer singleCondition = getSingleCondition( singlePattern);
	    		buffer.append(singleCondition);
	    		buffer.append(WHITE_SPACE).append(BRACKET_CLOSE);
	    		if (iterator.hasNext()) {
	    			buffer.append(WHITE_SPACE).append(OR).append(WHITE_SPACE);
	    		}
    		}
    		// bye bye
    		return buffer.toString();
    	}
    	// pattern is not a collection
    	else {
    		// bye bye
    		return getSingleCondition(pattern).toString();
    	}
  	}
    else if (this.patternField != null) {
    	expression.append(WHITE_SPACE).append(this.patternField);
    	// bye bye
    	return expression.toString();
    }
    // should not happen
    return "";
  }
  
  private StringBuffer getSingleCondition(Object patternObject) {
  	// change pattern to the corresponding string for the given type
  	if (this.inputHandlerClass != null && patternObject != null) {
  		InputHandler inputHandler = getInputHandler();
  		patternObject = inputHandler.convertSingleResultingObjectToType(patternObject, this.firstColumnClass).toString();
  	}
    StringBuffer expression = new StringBuffer(this.valueField).append(WHITE_SPACE);
    expression.append(this.comparison);
    if (patternObject != null)  {
      expression.append(WHITE_SPACE);
      // if it is an integer do not use apostrophe: age = 22
      if (INTEGER.equals(this.firstColumnClass)) {
        expression.append(patternObject);
      }
      // else use apostrophe: name = 'Thomas'
      else {
        expression.append(APOSTROPHE).append(patternObject).append(APOSTROPHE);
      }
    }
    return expression;
  }
    	
  public boolean isDynamic() {
  	return this.isDynamic;
  }
  
  public String getId()	{
  	return this.id;
  }
  
  public Map getIdentifierValueMap() {
  	return this.identifierValueMap;
  }
  
  public Map getIdentifierInputDescriptionMap()	{
  	return this.identifierInputDescriptionMap;
  }
  
  public void setIdentifierValueMap(Map identifierValueMap)	{
  	this.identifierValueMap = identifierValueMap;
  }
  
  public String getInputHandlerDescription() {
  	return this.inputHandlerDescription;
  }
  
  public InputHandler getInputHandler() {
  	// sometimes there isn't an inputhandler 
  	if (this.inputHandlerClass == null) {
  		return null;
  	}
  	InputHandler inputHandler = null;
  	try {
  		inputHandler = (InputHandler) RefactorClassRegistry.forName(this.inputHandlerClass).newInstance();
  	}
		catch (ClassNotFoundException ex) {
			//TODO: thi implement log 
//			log(ex);
//			logError("[CriterionExpression] Could not retrieve handler class");
		}
		catch (InstantiationException ex) {
//			log(ex);
//			logError("[CriterionExpression] Could not instanciate handler class");
		}
		catch (IllegalAccessException ex) {
//			log(ex);
//			logError("[CriterionExpression] Could not instanciate handler class");
		}
		return inputHandler;
  }
  
  public boolean isValid() {
    return true;
//    (
//       StringHandler.isNotEmpty(valueField) &&
//       StringHandler.isNotEmpty(pattern) &&
//       StringHandler.isNotEmpty(comparison));
  } 
    

}
