package is.idega.travel.data;

import com.idega.data.*;
import com.idega.block.trade.stockroom.data.Reseller;
import java.sql.Timestamp;
import java.sql.SQLException;

/**
 * Title:        idegaWeb TravelBooking
 * Description:
 * Copyright:    Copyright (c) 2001
 * Company:      idega
 * @author <a href="mailto:gimmi@idega.is">Grimur Jonsson</a>
 * @version 1.0
 */

public class Contract extends GenericEntity {

  public Contract() {
    super();
  }

  public Contract(int id) throws SQLException {
    super(id);
  }

  public void initializeAttributes() {
    addAttribute(getIDColumnName());
    addAttribute(getColumnNameServiceId(), "ServiceID", true, true, Integer.class, "many-to-one",Service.class);
    addAttribute(getColumnNameResellerId(), "ResellerID", true, true, Integer.class, "many-to-one",Reseller.class);
    addAttribute(getColumnNameAlotment(), "Fj�ldi s�ta", true, true, Integer.class);
    addAttribute(getColumnNameFrom(), "Virkt fr�", true, true, Timestamp.class);
    addAttribute(getColumnNameTo(), "Virkt til", true, true, Timestamp.class);
    addAttribute(getColumnNameDiscount(), "Afsl�ttur", true, true, String.class);
    addAttribute(getColumnNameExpiresDaysBeforeDeparture(), "dagar fyrir brottf�r", true, true, Integer.class);

  }
  public String getEntityName() {
    return getContractTableName();
  }

  // Setters
  public void setServiceId(int id) {
    setColumn(getColumnNameServiceId(), id);
  }

  public void setService(Service service) {
    setServiceId(service.getID());
  }

  public void setResellerId(int id) {
    setColumn(getColumnNameResellerId(), id);
  }

  public void setReseller(Reseller reseller) {
    setResellerId(reseller.getID());
  }

  public void setAlotment(int alotment) {
    setColumn(getColumnNameAlotment(), alotment);
  }

  public void setFrom(Timestamp from) {
    setColumn(getColumnNameFrom(),from);
  }

  public void setTo(Timestamp to) {
    setColumn(getColumnNameTo(),to);
  }

  public void setDiscount(String discount) {
    setColumn(getColumnNameDiscount(), discount);
  }

  public void setExpireDays(int daysBeforeDeparture) {
    setColumn(getColumnNameExpiresDaysBeforeDeparture(), daysBeforeDeparture);
  }

  // getters
  public int getServiceId() {
    return getIntColumnValue(getColumnNameServiceId());
  }

  public Service getService() throws SQLException {
    return new Service(getServiceId());
  }

  public int getResellerId() {
    return getIntColumnValue(getColumnNameResellerId());
  }

  public Reseller getReseller() throws SQLException {
    return new Reseller(getResellerId());
  }

  public int getAlotment() {
    return getIntColumnValue(getColumnNameAlotment());
  }

  public Timestamp getFrom() {
    return (Timestamp) getColumnValue(getColumnNameFrom());
  }

  public Timestamp getTo() {
    return (Timestamp) getColumnValue(getColumnNameTo());
  }

  public String getDiscount() {
    return getStringColumnValue(getColumnNameDiscount());
  }

  public int getExpireDays() {
    return getIntColumnValue(getColumnNameExpiresDaysBeforeDeparture());
  }


  public static String getContractTableName() { return "TB_CONTRACT";}
  public static String getColumnNameServiceId() { return "TB_SERVICE_ID";}
  public static String getColumnNameResellerId() { return "SR_RESELLER_ID";}
  public static String getColumnNameAlotment() { return "ALOTMENT";}
  public static String getColumnNameFrom() { return "ACTIVE_FROM";}
  public static String getColumnNameTo() { return "ACTIVE_TO";}
  public static String getColumnNameDiscount() { return "DISCOUNT";}
  public static String getColumnNameExpiresDaysBeforeDeparture() { return "VALID_DAYS_BEFORE";}
}