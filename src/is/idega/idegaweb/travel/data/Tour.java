package is.idega.travel.data;

import com.idega.data.*;
import java.sql.*;


/**
 * Title:        idegaWeb TravelBooking
 * Description:
 * Copyright:    Copyright (c) 2001
 * Company:      idega
 * @author <a href="mailto:gimmi@idega.is">Grimur Jonsson</a>
 * @version 1.0
 */

public class Tour extends GenericEntity {

  public Tour() {
    super();
  }

  public Tour(int id) throws SQLException{
    super(id);
  }

  public void initializeAttributes() {
    addAttribute(getIDColumnName(),"Service_id",true,true,Integer.class,"one-to-one",Service.class);
    addAttribute(getHotelPickupColumnName(), "Hotel pick-up", true, true, Boolean.class);
    addAttribute(getHotelPickupTimeColumnName(), "Hotel pick-up time", true, true, Timestamp.class);
    addAttribute(getTotalSeatsColumnName(), "Total seats", true, true, Integer.class);
    addAttribute(getMinimumSeatsColumnName(), "L�gmark s�ta", true, true, Integer.class);
    addAttribute(getNumberOfDaysColumnName(), "Fj�ldi daga", true, true, Integer.class);
    addAttribute(getLengthColumnName(), "Lengd", true, true, Float.class);
  }
  public String getEntityName() {
    return getTripTableName();
  }


  public void setDefaultValues() {
      this.setLength(0);
      this.setTotalSeats(0);
      this.setMinumumSeats(0);
      this.setNumberOfDays(1);
  }

  public boolean getIsHotelPickup() {
    return getHotelPickup();
  }

  public boolean getHotelPickup() {
    return getBooleanColumnValue(getHotelPickupColumnName());
  }

  public void setIsHotelPickup(boolean pickup) {
    setHotelPickup(pickup);
  }

  public void setHotelPickup(boolean pickup) {
    setColumn(getHotelPickupColumnName(),pickup);
  }

  public void setHotelPickupTime(Timestamp pickupTime) {
    setColumn(getHotelPickupTimeColumnName(), pickupTime);
  }

  public Timestamp getHotelPickupTime() {
    return (Timestamp) getColumnValue(getHotelPickupTimeColumnName());
  }

  public int getTotalSeats() {
    return getIntColumnValue(getTotalSeatsColumnName());
  }

  public void setTotalSeats(int totalSeats) {
    setColumn(getTotalSeatsColumnName(), totalSeats);
  }

  public int getMinimumSeats() {
    return getIntColumnValue(getMinimumSeatsColumnName());
  }

  public void setMinumumSeats(int seats) {
    setColumn(getMinimumSeatsColumnName(), seats);
  }

  public void setNumberOfDays(int numberOfSeats) {
    setColumn(getNumberOfDaysColumnName(), numberOfSeats);
  }

  public int getNumberOfDays() {
    return getIntColumnValue(getNumberOfDaysColumnName());
  }

  public void setLength(float length) {
    setColumn(getLengthColumnName(), length);
  }

  public float getLength() {
    return getFloatColumnValue(getLengthColumnName());
  }

  public static String getTripTableName() {return "TB_TOUR";}
  public static String getHotelPickupColumnName() {return "HOTEL_PICKUP";}
  public static String getHotelPickupTimeColumnName() {return "HOTEL_PICKUP_TIME";}
  public static String getTotalSeatsColumnName() {return "TOTAL_SEATS";}
  public static String getMinimumSeatsColumnName() {return "MINIMUM_SEATS";}
  public static String getNumberOfDaysColumnName() {return "NUMBER_OF_SEATS";}
  public static String getLengthColumnName() {return "TOUR_LENGTH";}

}