package com.idega.data;

import java.sql.Date;
import java.sql.Time;
import java.sql.Timestamp;
import java.util.Comparator;

import com.idega.util.IsCollator;


/**
 * Title:
 * Description:
 * Copyright:    Copyright (c) 2001
 * Company:      idega
 * @author       <a href="mailto:gummi@idega.is">Gu�mundur �g�st S�mundsson</a>
 * @version 1.0
 */

public class IDOLegacyEntityComparator implements Comparator {

  private String[] sortBy;

  public IDOLegacyEntityComparator() {
      sortBy = null;
  }

  public IDOLegacyEntityComparator(String columnName ) {
      sortBy = new String[1];
        sortBy[0] = columnName;
  }

  public IDOLegacyEntityComparator(String[] columnNames){
    sortBy = columnNames;
  }

  public int compare(Object o1, Object o2) {
      IDOLegacyEntity p1 = (IDOLegacyEntity) o1;
      IDOLegacyEntity p2 = (IDOLegacyEntity) o2;
      int result = 0;


      if(sortBy == null){
        sortBy = new String[1];
        sortBy[0] = p1.getIDColumnName();
      }

      for (int i = 0; i < sortBy.length; i++) {
        int attributeStorageClass = p1.getStorageClassType(sortBy[i]);

        switch (attributeStorageClass) {
          case EntityAttribute.TYPE_JAVA_LANG_INTEGER:
            result = ((Integer)p1.getColumnValue(sortBy[i])).compareTo((Integer)p2.getColumnValue(sortBy[i]));
            break;
          case EntityAttribute.TYPE_JAVA_LANG_STRING:
            result = IsCollator.getIsCollator().compare((String)p1.getColumnValue(sortBy[i]),(String)p2.getColumnValue(sortBy[i]));
            break;
          /*case EntityAttribute.TYPE_JAVA_LANG_BOOLEAN:
            result = ((Boolean)p1.getColumnValue(sortBy[i])).compareTo((Boolean)p2.getColumnValue(sortBy[i]));
            break;*/
          case EntityAttribute.TYPE_JAVA_LANG_FLOAT:
            result = ((Float)p1.getColumnValue(sortBy[i])).compareTo((Float)p2.getColumnValue(sortBy[i]));
            break;
          case EntityAttribute.TYPE_JAVA_LANG_DOUBLE:
            result = ((Double)p1.getColumnValue(sortBy[i])).compareTo((Double)p2.getColumnValue(sortBy[i]));
            break;
          case EntityAttribute.TYPE_JAVA_SQL_DATE:
            result = ((Date)p1.getColumnValue(sortBy[i])).compareTo((Date)p2.getColumnValue(sortBy[i]));
            break;
          case EntityAttribute.TYPE_JAVA_SQL_TIMESTAMP:
            result = ((Timestamp)p1.getColumnValue(sortBy[i])).compareTo((Timestamp)p2.getColumnValue(sortBy[i]));
            break;
          case EntityAttribute.TYPE_JAVA_SQL_TIME:
            result = ((Time)p1.getColumnValue(sortBy[i])).compareTo((Time)p2.getColumnValue(sortBy[i]));
            break;
//          case EntityAttribute.TYPE_COM_IDEGA_DATA_BLOBWRAPPER:
          default:
            System.err.println(this.getClass().getName()+": obj1 and obj2 not camparable for EntityAttribute.dataType = " + attributeStorageClass);
            break;
        }

        if(result != 0){
          break;
        }
      }


      return result;
  }


  public boolean equals(Object obj) {
    /**@todo: Implement this java.util.Comparator method*/
    throw new java.lang.UnsupportedOperationException("Method equals() not yet implemented.");
  }

}
