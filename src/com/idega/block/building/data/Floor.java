package com.idega.block.building.data;

import javax.ejb.*;

public interface Floor extends com.idega.data.IDOLegacyEntity
{
 public int getBuildingId();
 public int getImageId();
 public java.lang.String getInfo();
 public java.lang.String getName();
 public void setBuildingId(java.lang.Integer p0);
 public void setBuildingId(int p0);
 public void setImageId(java.lang.Integer p0);
 public void setImageId(int p0);
 public void setInfo(java.lang.String p0);
 public void setName(java.lang.String p0);
}
