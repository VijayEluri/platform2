package com.idega.block.finance.data;

import javax.ejb.*;

public interface RoundInfo extends com.idega.data.IDOLegacyEntity
{
 public void delete()throws java.sql.SQLException;
 public int getAccounts();
 public int getCategoryId();
 public int getGroupId();
 public java.lang.String getName();
 public float getNetto();
 public int getRoundId();
 public java.sql.Timestamp getRoundStamp();
 public java.lang.String getStatus();
 public float getTotals();
}
