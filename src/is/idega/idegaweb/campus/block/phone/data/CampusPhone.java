package is.idega.idegaweb.campus.block.phone.data;

import javax.ejb.*;

public interface CampusPhone extends com.idega.data.IDOLegacyEntity
{
 public int getApartmentId();
 public java.sql.Date getDateInstalled();
 public java.sql.Date getDateResigned();
 public java.lang.String getPhoneNumber();
 public void setApartmentId(int p0);
 public void setDateInstalled(java.sql.Date p0);
 public void setDateResigned(java.sql.Date p0);
 public void setPhoneNumber(java.lang.String p0);
}
