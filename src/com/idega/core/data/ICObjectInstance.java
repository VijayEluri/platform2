package com.idega.core.data;

import javax.ejb.*;

public interface ICObjectInstance extends com.idega.data.IDOLegacyEntity
{
 public com.idega.core.data.ICObjectInstance[] getContainingObjects()throws java.lang.Exception;
 public int getIBPageID();
 public java.lang.String getName();
 public com.idega.presentation.PresentationObject getNewInstance()throws java.lang.ClassNotFoundException,java.lang.IllegalAccessException,java.lang.InstantiationException;
 public com.idega.core.data.ICObject getObject();
 public int getParentInstanceID();
 public void setDefaultValues();
 public void setIBPageByKey(java.lang.String p0);
 public void setIBPageID(int p0);
 public void setICObject(com.idega.core.data.ICObject p0);
 public void setICObjectID(int p0);
 public void setParentInstanceID(int p0);
}
