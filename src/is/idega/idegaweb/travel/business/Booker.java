package is.idega.idegaweb.travel.business;

import javax.ejb.*;

public interface Booker extends com.idega.business.IBOService
{
 public int Book(int p0,java.lang.String p1,java.lang.String p2,java.lang.String p3,java.lang.String p4,java.lang.String p5,java.lang.String p6,com.idega.util.idegaTimestamp p7,int p8,int p9,java.lang.String p10,int p11,int p12,int p13,int p14,java.lang.String p15)throws javax.ejb.CreateException,java.rmi.RemoteException, java.rmi.RemoteException;
 public int BookBySupplier(int p0,java.lang.String p1,java.lang.String p2,java.lang.String p3,java.lang.String p4,java.lang.String p5,java.lang.String p6,com.idega.util.idegaTimestamp p7,int p8,java.lang.String p9,int p10,int p11,int p12,int p13,java.lang.String p14)throws javax.ejb.CreateException,java.rmi.RemoteException, java.rmi.RemoteException;
 public is.idega.idegaweb.travel.interfaces.Booking[] collectionToBookingsArray(java.util.Collection p0) throws java.rmi.RemoteException;
 public boolean deleteBooking(int p0)throws javax.ejb.FinderException,java.rmi.RemoteException, java.rmi.RemoteException;
 public boolean deleteBooking(is.idega.idegaweb.travel.interfaces.Booking p0)throws java.rmi.RemoteException, java.rmi.RemoteException;
 public int getAvailableItems(com.idega.block.trade.stockroom.data.ProductPrice p0,com.idega.util.idegaTimestamp p1) throws java.rmi.RemoteException;
 public is.idega.idegaweb.travel.data.BookingEntry[] getBookingEntries(is.idega.idegaweb.travel.interfaces.Booking p0)throws javax.ejb.FinderException,java.rmi.RemoteException, java.rmi.RemoteException;
 public float getBookingEntryPrice(com.idega.presentation.IWContext p0,is.idega.idegaweb.travel.data.BookingEntry p1,is.idega.idegaweb.travel.interfaces.Booking p2)throws java.rmi.RemoteException, java.rmi.RemoteException;
 public float getBookingPrice(com.idega.presentation.IWContext p0,is.idega.idegaweb.travel.interfaces.Booking p1)throws javax.ejb.FinderException,java.rmi.RemoteException, java.rmi.RemoteException;
 public float getBookingPrice(com.idega.presentation.IWContext p0,is.idega.idegaweb.travel.data.GeneralBooking[] p1)throws javax.ejb.FinderException,java.rmi.RemoteException, java.rmi.RemoteException;
 public float getBookingPrice(com.idega.presentation.IWContext p0,is.idega.idegaweb.travel.interfaces.Booking[] p1)throws javax.ejb.FinderException,java.rmi.RemoteException, java.rmi.RemoteException;
 public float getBookingPrice(com.idega.presentation.IWContext p0,java.util.List p1)throws javax.ejb.FinderException,java.rmi.RemoteException, java.rmi.RemoteException;
 public is.idega.idegaweb.travel.interfaces.Booking[] getBookings(int p0,com.idega.util.idegaTimestamp p1,int p2)throws javax.ejb.FinderException,java.rmi.RemoteException, java.rmi.RemoteException;
 public is.idega.idegaweb.travel.interfaces.Booking[] getBookings(int[] p0,com.idega.util.idegaTimestamp p1,int[] p2)throws javax.ejb.FinderException,java.rmi.RemoteException, java.rmi.RemoteException;
 public is.idega.idegaweb.travel.interfaces.Booking[] getBookings(int[] p0,com.idega.util.idegaTimestamp p1,com.idega.util.idegaTimestamp p2,int[] p3)throws javax.ejb.FinderException,java.rmi.RemoteException, java.rmi.RemoteException;
 public is.idega.idegaweb.travel.interfaces.Booking[] getBookings(int p0,com.idega.util.idegaTimestamp p1)throws javax.ejb.FinderException,java.rmi.RemoteException, java.rmi.RemoteException;
 public is.idega.idegaweb.travel.interfaces.Booking[] getBookings(int p0,com.idega.util.idegaTimestamp p1,int[] p2)throws javax.ejb.FinderException,java.rmi.RemoteException, java.rmi.RemoteException;
 public is.idega.idegaweb.travel.interfaces.Booking[] getBookings(java.util.List p0,int[] p1,com.idega.util.idegaTimestamp p2)throws javax.ejb.FinderException,java.rmi.RemoteException, java.rmi.RemoteException;
 public is.idega.idegaweb.travel.interfaces.Booking[] getBookings(java.util.List p0,int[] p1,com.idega.util.idegaTimestamp p2,com.idega.util.idegaTimestamp p3,java.lang.String p4,java.lang.String p5)throws javax.ejb.FinderException,java.rmi.RemoteException, java.rmi.RemoteException;
 public is.idega.idegaweb.travel.interfaces.Booking[] getBookings(java.util.List p0,com.idega.util.idegaTimestamp p1)throws javax.ejb.FinderException,java.rmi.RemoteException, java.rmi.RemoteException;
 public is.idega.idegaweb.travel.interfaces.Booking[] getBookings(java.util.List p0,com.idega.util.idegaTimestamp p1,com.idega.util.idegaTimestamp p2)throws javax.ejb.FinderException,java.rmi.RemoteException, java.rmi.RemoteException;
 public is.idega.idegaweb.travel.interfaces.Booking[] getBookings(java.util.List p0,com.idega.util.idegaTimestamp p1,com.idega.util.idegaTimestamp p2,java.lang.String p3,java.lang.String p4)throws javax.ejb.FinderException,java.rmi.RemoteException, java.rmi.RemoteException;
 public com.idega.block.trade.data.Currency getCurrency(is.idega.idegaweb.travel.interfaces.Booking p0)throws java.rmi.RemoteException,javax.ejb.FinderException,java.sql.SQLException, java.rmi.RemoteException;
 public is.idega.idegaweb.travel.data.GeneralBookingHome getGeneralBookingHome()throws java.rmi.RemoteException, java.rmi.RemoteException;
 public java.util.List getMultibleBookings(is.idega.idegaweb.travel.data.GeneralBooking p0)throws javax.ejb.FinderException,java.rmi.RemoteException, java.rmi.RemoteException;
 public int[] getMultipleBookingNumber(is.idega.idegaweb.travel.data.GeneralBooking p0)throws javax.ejb.FinderException,java.rmi.RemoteException, java.rmi.RemoteException;
 public int getNumberOfBookings(int p0,com.idega.util.idegaTimestamp p1,int p2)throws java.rmi.RemoteException, java.rmi.RemoteException;
 public int getNumberOfBookings(com.idega.block.trade.stockroom.data.ProductPrice p0)throws java.rmi.RemoteException, java.rmi.RemoteException;
 public int getNumberOfBookings(int p0,com.idega.util.idegaTimestamp p1)throws java.rmi.RemoteException, java.rmi.RemoteException;
 public int getNumberOfBookings(int p0,com.idega.util.idegaTimestamp p1,com.idega.util.idegaTimestamp p2)throws java.rmi.RemoteException, java.rmi.RemoteException;
 public int getNumberOfBookings(int p0,com.idega.util.idegaTimestamp p1,com.idega.util.idegaTimestamp p2,int p3)throws java.rmi.RemoteException, java.rmi.RemoteException;
 public int getNumberOfBookingsByReseller(int p0,int p1,com.idega.util.idegaTimestamp p2)throws java.rmi.RemoteException, java.rmi.RemoteException;
 public int getNumberOfBookingsByResellers(int[] p0,int p1,com.idega.util.idegaTimestamp p2)throws java.rmi.RemoteException, java.rmi.RemoteException;
 public int getNumberOfBookingsByResellers(int p0,com.idega.util.idegaTimestamp p1)throws java.rmi.RemoteException, java.rmi.RemoteException;
 public com.idega.presentation.ui.DropdownMenu getPaymentTypeDropdown(com.idega.idegaweb.IWResourceBundle p0,java.lang.String p1) throws java.rmi.RemoteException;
 public com.idega.presentation.ui.DropdownMenu getPaymentTypes(com.idega.idegaweb.IWResourceBundle p0) throws java.rmi.RemoteException;
 public java.lang.Object getServiceType(int p0) throws java.rmi.RemoteException;
 public void removeBookingPriceApplication(com.idega.presentation.IWContext p0,is.idega.idegaweb.travel.interfaces.Booking p1)throws java.rmi.RemoteException, java.rmi.RemoteException;
 public int updateBooking(int p0,int p1,java.lang.String p2,java.lang.String p3,java.lang.String p4,java.lang.String p5,java.lang.String p6,java.lang.String p7,com.idega.util.idegaTimestamp p8,int p9,java.lang.String p10,int p11,int p12,int p13,int p14,java.lang.String p15)throws javax.ejb.CreateException,java.rmi.RemoteException, java.rmi.RemoteException;
}
