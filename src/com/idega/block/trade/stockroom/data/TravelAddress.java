package com.idega.block.trade.stockroom.data;

import javax.ejb.*;

public interface TravelAddress extends com.idega.data.IDOLegacyEntity
{
 public com.idega.core.data.Address getAddress();
 public int getAddressId();
 public int getAddressType();
 public java.lang.String getName();
 public boolean getRefillStock();
 public java.lang.String getStreetName();
 public java.sql.Timestamp getTime();
 public void setAddress(com.idega.core.data.Address p0);
 public void setAddressId(int p0);
 public void setAddressTypeId(int p0);
 public void setRefillStock(boolean p0);
 public void setTime(java.sql.Timestamp p0);
 public void setTime(com.idega.util.IWTimeStamp p0);
}
