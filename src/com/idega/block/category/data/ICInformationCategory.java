package com.idega.block.category.data;

import com.idega.data.TreeableEntity;


public interface ICInformationCategory extends TreeableEntity,com.idega.block.category.data.InformationCategory
{
 public void addCategoryToInstance(int p0)throws java.sql.SQLException;
 public int getChildCount();
 public java.util.Iterator getChildren();
 public java.util.Iterator getChildren(java.lang.String p0);
 public java.sql.Timestamp getCreated();
 public boolean getDeleted();
 public int getDeletedBy();
 public java.sql.Timestamp getDeletedWhen();
 public java.lang.String getDescription();
 public int getICObjectId();
 public java.lang.String getName();
 public int getOwnerFolderId();
 public java.lang.String getType();
 public boolean getValid();
 public void removeCategoryFromInstance(int p0)throws java.sql.SQLException;
 public void setCreated(java.sql.Timestamp p0);
 public void setDeleted(int p0,boolean p1);
 public void setDescription(java.lang.String p0);
 public void setFolderSpecific(int p0);
 public void setGlobal()throws java.sql.SQLException;
 public void setICObjectId(int p0);
 public void setName(java.lang.String p0);
 public void setType(java.lang.String p0);
 public void setValid(boolean p0);
}
