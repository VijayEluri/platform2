package com.idega.block.entity.data;

/**
 *@author     <a href="mailto:thomas@idega.is">Thomas Hilbig</a>
 *@version    1.0
 */
public class DummyEntityPath extends EntityPath {
  
  public final String VALUE_OF_A_DUMMY_ENTITYPATH = "value_of_a_dummy_entity_path";
  
  private String shortKey;
  
  public DummyEntityPath(String shortKey) {
    super(DummyEntityPath.class);
    this.shortKey = shortKey;
  }
  
  
  public Object getValue()  {
    return VALUE_OF_A_DUMMY_ENTITYPATH;
  }
  
  
  public  String getShortKey()  {
    return shortKey;
  }
  
  public String getSerialization() {
    Class sourceEntity = getSourceEntityClass();
    String name = (sourceEntity == null) ? "Unknown source" : sourceEntity.getName();
    StringBuffer serialization = new StringBuffer();
      serialization
        .append(name)
        .append(SERIALIZATION_DELIMITER)
        .append(shortKey);
     return serialization.toString();
  }
  
  public String getDescription()  {
    return shortKey;
  }      
  
}
