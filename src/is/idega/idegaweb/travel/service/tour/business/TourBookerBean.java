package is.idega.idegaweb.travel.service.tour.business;

import javax.ejb.CreateException;
import javax.ejb.FinderException;
import is.idega.idegaweb.travel.business.BookerBean;
import java.rmi.RemoteException;
import com.idega.idegaweb.IWApplicationContext;
import com.idega.business.IBOLookup;
import com.idega.business.IBOServiceBean;
import is.idega.idegaweb.travel.business.Booker;

import com.idega.util.*;
import is.idega.idegaweb.travel.interfaces.Booking;
import is.idega.idegaweb.travel.service.tour.data.*;

import java.sql.SQLException;
import java.util.*;

/**
 * Title:        idegaWeb Travel
 * Description:
 * Copyright:    Copyright (c) 2001
 * Company:      idega
 * @author <a href mailto:"gimmi@idega.is">Gr�mur J�nsson</a>
 * @version 1.0
 */

public class TourBookerBean extends BookerBean implements TourBooker {

  public TourBookerBean() {
  }

  private Booker getBooker() throws RemoteException {
    return (Booker) IBOLookup.getServiceInstance(getIWApplicationContext(), Booker.class);
  }



  public int BookBySupplier(int serviceId, int hotelPickupPlaceId, String roomNumber, String country, String name, String address, String city, String telephoneNumber, String email, IWTimestamp date, int totalCount, String postalCode, int paymentType, int userId, int ownerId, int addressId, String comment) throws SQLException, RemoteException, CreateException {
    int _bookingId = getBooker().Book(serviceId, country, name, address, city, telephoneNumber, email, date, totalCount, Booking.BOOKING_TYPE_ID_SUPPLIER_BOOKING, postalCode, paymentType, userId, ownerId, addressId, comment);
    return Book(_bookingId, hotelPickupPlaceId, roomNumber);
  }

  public int Book(int serviceId, int hotelPickupPlaceId, String roomNumber, String country, String name, String address, String city, String telephoneNumber, String email, IWTimestamp date, int totalCount, int bookingType, String postalCode, int paymentType, int userId, int ownerId, int addressId, String comment) throws SQLException, RemoteException, CreateException {
    int _bookingId = getBooker().Book(serviceId, country, name, address, city, telephoneNumber, email, date, totalCount, bookingType, postalCode, paymentType, userId, ownerId, addressId, comment);
    return Book(_bookingId, hotelPickupPlaceId, roomNumber);
  }

  public int updateBooking(int bookingId, int serviceId, int hotelPickupPlaceId, String roomNumber, String country, String name, String address, String city, String telephoneNumber, String email, IWTimestamp date, int totalCount, String postalCode, int paymentType, int userId, int ownerId, int addressId, String comment) throws SQLException, RemoteException, CreateException {
    int _bookingId = getBooker().updateBooking(bookingId, serviceId, country, name, address, city, telephoneNumber, email, date, totalCount,  postalCode, paymentType, userId, ownerId, addressId, comment);
    return Book(_bookingId, hotelPickupPlaceId, roomNumber);
  }

  private static int Book(int bookingId, int hotelPickupPlaceId, String roomNumber) throws SQLException, RemoteException {
    try {
      boolean update = false;
      TourBooking booking = null;
      try {
        booking = ((is.idega.idegaweb.travel.service.tour.data.TourBookingHome)com.idega.data.IDOLookup.getHome(TourBooking.class)).findByPrimaryKey(new Integer(bookingId));
        update = true;
      }catch (Exception sql) {
        booking = ((is.idega.idegaweb.travel.service.tour.data.TourBookingHome)com.idega.data.IDOLookup.getHome(TourBooking.class)).create();
        booking.setPrimaryKey(new Integer(bookingId));
//        booking.setColumn(is.idega.idegaweb.travel.service.tour.data.TourBookingBMPBean.getTourBookingIDColumnName(), bookingId);
      }
      if (booking == null) {
        System.err.println("TourBooker : booking  == null !!!");
        System.err.println("...bookingId          =  "+bookingId);
        System.err.println("...hotelPickupPlaceId =  "+hotelPickupPlaceId);
        System.err.println("...roomNumber         =  "+bookingId);
      }

      if (hotelPickupPlaceId != -1) {
        booking.setHotelPickupPlaceID(hotelPickupPlaceId);
        if (roomNumber != null) {
          booking.setRoomNumber(roomNumber);
        }
      }

      if (hotelPickupPlaceId != -1) {
        booking.setHotelPickupPlaceID(hotelPickupPlaceId);
        if (roomNumber != null) {
          booking.setRoomNumber(roomNumber);
        }
      }


      if (update) {
        booking.store();
      } else {
        booking.store();
      }


      return bookingId;
    }catch (CreateException s) {




      s.printStackTrace(System.err);
      return bookingId;
    }


  }

  public Booking[] getBookings(int serviceId, IWTimestamp stamp, boolean withHotelPickup) throws RemoteException, FinderException {
    Booking[] bookings = getBooker().getBookings(serviceId, stamp);
    try {
      List bings = new Vector();
      TourBooking tb;
      for (int i = 0; i < bookings.length; i++) {
        try {
          tb = ((is.idega.idegaweb.travel.service.tour.data.TourBookingHome)com.idega.data.IDOLookup.getHome(TourBooking.class)).findByPrimaryKey(bookings[i].getPrimaryKey());
          if (tb.getHotelPickupPlaceID() != -1) {
            bings.add(bookings[i]);
          }
        }catch (FinderException sql) {
          System.err.println("TourBooker : getBookings : "+sql.getMessage());
        }
      }
      if (bookings.length > 0) {
        bookings = (Booking[]) bings.toArray(new Booking[]{});
      }
    }catch (javax.ejb.EJBException ejb) {
      ejb.printStackTrace(System.err);
    }
    return bookings;
  }

}
