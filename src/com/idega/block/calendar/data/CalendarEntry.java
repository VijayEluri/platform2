package com.idega.block.calendar.data;

import javax.ejb.*;

public interface CalendarEntry extends com.idega.data.CategoryEntity
{
 public void delete()throws java.sql.SQLException;
 public java.sql.Timestamp getDate();
 public int getEntryTypeID();
 public int getGroupID();
 public java.lang.String getIDColumnName();
 public int getUserID();
 public void setDate(java.sql.Timestamp p0);
 public void setEntryTypeID(int p0);
 public void setGroupID(int p0);
 public void setUserID(int p0);
}
