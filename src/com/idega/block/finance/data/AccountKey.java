package com.idega.block.finance.data;

import javax.ejb.*;

public interface AccountKey extends com.idega.data.IDOLegacyEntity,com.idega.block.finance.business.Key
{
 public int getCategoryId();
 public java.lang.String getInfo();
 public java.lang.String getName();
 public int getTariffKeyId();
 public void setCategoryId(int p0);
 public void setInfo(java.lang.String p0);
 public void setName(java.lang.String p0);
 public void setTariffKeyId(int p0);
}
