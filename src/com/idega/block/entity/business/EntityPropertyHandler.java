package com.idega.block.entity.business;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.SortedMap;
import java.util.StringTokenizer;

import com.idega.block.entity.data.EntityPath;
import com.idega.block.entity.data.EntityPropertyDefaultValues;
import com.idega.block.entity.presentation.EntityBrowser;

import com.idega.idegaweb.IWBundle;
import com.idega.idegaweb.IWProperty;
import com.idega.idegaweb.IWPropertyList;
import com.idega.idegaweb.IWUserContext;
import com.idega.user.business.UserProperties;

/**
 *@author     <a href="mailto:thomas@idega.is">Thomas Hilbig</a>
 *@version    1.0
 */
public class EntityPropertyHandler {
  
  public final static String PROPERTIES_LIST_KEY = "properties_for_entity";
  
  public final static String VISIBLE_COLUMN_KEY = "visible_columns";
  
  public final static String NUMBER_OF_ROWS_PER_PAGE_KEY = "number_of_rows_per_page";
  
  public final static int DEFAULT_NUMBER_OF_ROWS_PER_PAGE = 1;
  
  
  private Class entityClass = null;
  
  private SortedMap allEntityPathes = null;
  
  // cached value
  private IWPropertyList entityProperties = null;
  
  // cached value
  private IWUserContext userContext = null;
  
  private IWBundle bundle = null;
  
  
  public EntityPropertyHandler(IWUserContext userContext, Class entityClass)  {
    this.entityClass = entityClass;
    this.userContext = userContext;
    bundle = userContext.getApplicationContext().getApplication().getBundle(EntityBrowser.IW_BUNDLE_IDENTIFIER);
  }
  
      
  public EntityPropertyHandler(IWUserContext userContext, String entityClassName) throws ClassNotFoundException {
    this(userContext, Class.forName(entityClassName));
  }
 
 
  public static SortedMap getAllEntityPathes(Class entityClass) {    
      
    SortedMap map = EntityPath.getInstances(entityClass);
    
    // add virtual entity pathes
    String[] virtualShortKeys = EntityPropertyDefaultValues.getVirtualShortKeys(entityClass.getName());
    if (virtualShortKeys == null)
      // nothing to add...( most likely )
      return map;
      
    // add virtual pathes (virtual pathes are pathes that have children)
    int i;
    for (i = 0; i < virtualShortKeys.length ; i++)  {
      String shortKey = virtualShortKeys[i];
      EntityPath path = EntityPropertyHandler.getEntityPath(map, shortKey);
      map.put(shortKey, path);
    }
    return map;     
      
  } 
 
 
 
 
 
 
 
  public List getVisibleOrderedEntityPathes() {
    List entityPathKeyNames = getListFromProperty(getRootProperties(), VISIBLE_COLUMN_KEY);
    Iterator iterator = entityPathKeyNames.iterator();
    List entityPathes = new ArrayList();
    while (iterator.hasNext())  {
      String serialization = (String) iterator.next();
      try {
        entityPathes.add(EntityPath.getInstance(serialization));
      } 
      catch (ClassNotFoundException e)  {
        System.err.println("Can not create EntityPath " + e.getMessage());
        e.printStackTrace(System.err);
      }
    }
    return entityPathes;
  }
 
  public void setVisibleOrderedEntityPathes(List entityPathes) {
    Iterator iterator = entityPathes.iterator();
    List serializationList = new ArrayList();
    while (iterator.hasNext())  {
      EntityPath entityPath = (EntityPath) iterator.next();
      String serialization = (String) entityPath.getSerialization();
      serializationList.add(serialization);
    } 
    setListIntoProperty(getRootProperties(), VISIBLE_COLUMN_KEY, serializationList);
  }
 
  public void setNumberOfRowsPerPage(int numberOfRowsPerPage) {
    setValueIntoProperty(getRootProperties(), NUMBER_OF_ROWS_PER_PAGE_KEY, Integer.toString(numberOfRowsPerPage));
  }
 
  public int getNumberOfRowsPerPage() {
    String result = getValueFromProperty(getRootProperties(), NUMBER_OF_ROWS_PER_PAGE_KEY);
    if (result == null)
      return DEFAULT_NUMBER_OF_ROWS_PER_PAGE;
    return Integer.parseInt(result);
  } 
 
  public SortedMap getAllEntityPathes() { 
    
    if (allEntityPathes == null)
      allEntityPathes = EntityPropertyHandler.getAllEntityPathes(entityClass);  
    return allEntityPathes;
  }
      
 

  public String getEntityClassName() {
    return entityClass.getName();
  }       


 
  public EntityPath getEntityPath(String shortKey)  {
    Map map = getAllEntityPathes();
    return EntityPropertyHandler.getEntityPath(map, shortKey);
  }
    
  private static EntityPath getEntityPath(Map shortKeyPathDic, String shortKey)  {  
    StringTokenizer tokenizer = new StringTokenizer(shortKey, EntityPath.NEXT_SHORT_KEY_DELIMITER);
    EntityPath firstPath = null;
    EntityPath lastPath = null;
    while (tokenizer.hasMoreTokens()) {
      String singleShortKey = tokenizer.nextToken();
      EntityPath path = (EntityPath) shortKeyPathDic.get(singleShortKey);
      // do not use the original one !
      path = (EntityPath) path.clone();
      if (path == null)
        return null;
      if (firstPath == null)
        firstPath = path;
      else 
        lastPath.add(path);
      lastPath = path;
    }
    return firstPath;
  }
    
   
 

    
  
  private void setListIntoProperty(IWPropertyList properties, String keyName, List list) {
    // do not store just remove the old entry (if there was not a prior entry it does not matter)
    if (list == null || list.isEmpty()) {
      properties.removeProperty(keyName);
      return;
    }
    // are there any changes?  
    List existingList = getListFromProperty(properties, keyName);
    if (existingList.equals(list))
      // nothing to do....
      return;
    // there are changes!  
    // delete old entry (if there was not a prior entry it does not matter)
    properties.removeProperty(keyName);
    // create new entry
    IWPropertyList propertyList = properties.getNewPropertyList(keyName);
    Iterator iterator = list.iterator();
    // set entries
    int i = 0;
    while (iterator.hasNext())  {
      String element = (String) iterator.next();
      propertyList.setProperty(Integer.toString(i++), element);
    }
  }          
    
  private List getListFromProperty(IWPropertyList properties, String keyName) {
    IWPropertyList propertyList = properties.getIWPropertyList(keyName);
    // entry was not found
    if (propertyList == null)
      return new ArrayList();
    // get entries, use the IWPropertyListIterator    
    Iterator iterator = propertyList.iterator();
    ArrayList returnList = new ArrayList();
    while (iterator.hasNext())  {
      IWProperty property = (IWProperty) iterator.next();   
      returnList.add(property.getValue());
    }    
    return returnList;
  }
 
  private void setValueIntoProperty(IWPropertyList properties, String keyName, String value) {
    // do not store just remove the entry (if there was not a prior entry it does not matter)
    if (value == null || value.length() == 0) {
      properties.removeProperty(keyName);
      return;
    }
    // is there a change?
    String existingValue = getValueFromProperty(properties, keyName);
    if (existingValue != null && existingValue.equals(value)) 
      // nothing to do...
      return;
    // remove old entry (if there was not a prior entry it does not matter)
    properties.removeProperty(keyName);
    // create new entry
    properties.setProperty(keyName, value);
  }           
 
  private String getValueFromProperty(IWPropertyList properties, String keyName) { 
    IWProperty property = properties.getIWProperty(keyName);
    // entry was not found
    if (property == null)
      return null;
    return property.getValue();
  }
    
  private IWPropertyList getRootProperties()  {
    if (entityProperties == null) {
      // get the properties from session 
      // they are being stored when the user log off
      // !!!! do not use the wrong method IWBundle>>getUserProperties(IWUserContext)
      // because there the properties are stored with the bundle name as key !!!!
      UserProperties userProperties = userContext.getUserProperties();  
      IWPropertyList rootList = userProperties.getProperties(PROPERTIES_LIST_KEY);
      String nameOfEntity = entityClass.getName();
      entityProperties = rootList.getPropertyList(nameOfEntity);
      if (entityProperties == null) 
        entityProperties = rootList.getNewPropertyList(nameOfEntity);
    }
    return entityProperties;
  }    
    
  
   
 

  
}
