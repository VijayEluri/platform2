package is.idega.idegaweb.golf.entity;

import javax.ejb.*;

public interface StartingtimeFieldConfig extends is.idega.idegaweb.golf.entity.GolfEntity
{
 public java.sql.Timestamp getBeginDate();
 public java.sql.Timestamp getCloseTime();
 public int getDaysKept();
 public int getDaysShown();
 public java.sql.Timestamp getEndDate();
 public int getFieldID();
 public int getMinutesBetweenStart();
 public java.sql.Timestamp getOpenTime();
 public int getTournamentID();
 public boolean publicRegistration();
 public void setBeginDate(java.sql.Timestamp p0);
 public void setCloseTime(java.sql.Timestamp p0);
 public void setDaysKept(java.lang.Integer p0);
 public void setDaysShown(java.lang.Integer p0);
 public void setDefultValues();
 public void setEndDate(java.sql.Timestamp p0);
 public void setFieldID(java.lang.Integer p0);
 public void setMinutesBetweenStart(java.lang.Integer p0);
 public void setOpenTime(java.sql.Timestamp p0);
 public void setPublicRegistration(boolean p0);
 public void setTournamentID(int p0);
}
