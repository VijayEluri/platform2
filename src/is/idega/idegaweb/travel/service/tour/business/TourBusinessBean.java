package is.idega.idegaweb.travel.service.tour.business;

import javax.ejb.FinderException;
import com.idega.data.IDOAddRelationshipException;
import com.idega.business.IBOLookup;
import java.rmi.RemoteException;
import com.idega.data.IDOLookup;
import is.idega.idegaweb.travel.service.tour.data.*;
import is.idega.idegaweb.travel.business.*;
import com.idega.block.trade.stockroom.data.*;
import com.idega.block.trade.stockroom.business.*;
import is.idega.idegaweb.travel.data.*;
import com.idega.core.data.*;
import com.idega.util.*;
import java.sql.SQLException;
import java.util.*;
import com.idega.presentation.IWContext;
import com.idega.idegaweb.IWApplicationContext;
import com.idega.transaction.IdegaTransactionManager;
import javax.transaction.TransactionManager;
import com.idega.presentation.ui.DropdownMenu;
import is.idega.idegaweb.travel.service.business.*;

/**
 * Title:        idegaWeb TravelBooking
 * Description:
 * Copyright:    Copyright (c) 2001
 * Company:      idega
 * @author <a href="mailto:gimmi@idega.is">Grimur Jonsson</a>
 * @version 1.0
 */

public class TourBusinessBean extends TravelStockroomBusinessBean implements TourBusiness{

  public TourBusinessBean() {
  }

  public int updateTourService(int tourId,int supplierId, Integer fileId, String serviceName, String number, String serviceDescription, boolean isValid, String departureFrom, IWTimestamp departureTime, String arrivalAt, IWTimestamp arrivalTime, String[] pickupPlaceIds,  int[] activeDays, Integer numberOfSeats, Integer minNumberOfSeats, Integer numberOfDays, Float kilometers, int estimatedSeatsUsed, int discountTypeId) throws Exception{
    return createTourService(tourId,supplierId, fileId, serviceName, number, serviceDescription, isValid, departureFrom, departureTime, arrivalAt, arrivalTime, pickupPlaceIds, activeDays, numberOfSeats, minNumberOfSeats, numberOfDays, kilometers, estimatedSeatsUsed, discountTypeId);
  }

  public int createTourService(int supplierId, Integer fileId, String serviceName, String number, String serviceDescription, boolean isValid, String departureFrom, IWTimestamp departureTime, String arrivalAt, IWTimestamp arrivalTime, String[] pickupPlaceIds,  int[] activeDays, Integer numberOfSeats, Integer minNumberOfSeats, Integer numberOfDays, Float kilometers, int estimatedSeatsUsed, int discountTypeId) throws Exception {
    return createTourService(-1,supplierId, fileId, serviceName, number, serviceDescription, isValid, departureFrom, departureTime, arrivalAt, arrivalTime, pickupPlaceIds, activeDays, numberOfSeats,minNumberOfSeats, numberOfDays, kilometers, estimatedSeatsUsed, discountTypeId);
  }

  private int createTourService(int tourId, int supplierId, Integer fileId, String serviceName, String number,  String serviceDescription, boolean isValid, String departureFrom, IWTimestamp departureTime, String arrivalAt, IWTimestamp arrivalTime, String[] pickupPlaceIds,  int[] activeDays, Integer numberOfSeats, Integer minNumberOfSeats,Integer numberOfDays, Float kilometers, int estimatedSeatsUsed, int discountTypeId) throws Exception {
      boolean isError = false;

      /**
       * @todo handle isError og pickupTime
       */
      if (super.timeframe == null) isError = true;
      if (activeDays.length == 0) isError = true;

      int hotelPickupAddressTypeId = com.idega.core.data.AddressTypeBMPBean.getId(ProductBusinessBean.uniqueHotelPickupAddressType);


      int[] departureAddressIds = setDepartureAddress(tourId, departureFrom, departureTime);
      int[] arrivalAddressIds = setArrivalAddress(tourId, arrivalAt);

      int[] hotelPickupPlaceIds ={};
      if (pickupPlaceIds != null && pickupPlaceIds.length > 0 && !pickupPlaceIds[0].equals("") ) hotelPickupPlaceIds = new int[pickupPlaceIds.length];
      for (int i = 0; i < pickupPlaceIds.length; i++) {
        hotelPickupPlaceIds[i] = Integer.parseInt(pickupPlaceIds[i]);
      }

      int serviceId = -1;
      if (tourId == -1) {
        serviceId = createService(supplierId, fileId, serviceName, number, serviceDescription, isValid, departureAddressIds, departureTime.getTimestamp(), arrivalTime.getTimestamp(), discountTypeId);
      }else {
        serviceId = updateService(tourId,supplierId, fileId, serviceName, number, serviceDescription, isValid, departureAddressIds, departureTime.getTimestamp(), arrivalTime.getTimestamp(), discountTypeId);
      }

      javax.transaction.UserTransaction userT = getSessionContext().getUserTransaction();

      if (serviceId != -1)
      try {
          userT.begin();
          Service service = ((is.idega.idegaweb.travel.data.ServiceHome)com.idega.data.IDOLookup.getHome(Service.class)).findByPrimaryKey(new Integer(serviceId));
          Product product = getProductBusiness().getProduct(serviceId);// Product(serviceId);
          Tour tour;

          if (tourId == -1) {
            tour = ((is.idega.idegaweb.travel.service.tour.data.TourHome)com.idega.data.IDOLookup.getHome(Tour.class)).create();
            tour.setPrimaryKey(new Integer(serviceId));
          }else {
            tour = ((is.idega.idegaweb.travel.service.tour.data.TourHome)com.idega.data.IDOLookup.getHome(Tour.class)).findByPrimaryKey(new Integer(tourId));
          }

          if (numberOfSeats != null)
            tour.setTotalSeats(numberOfSeats.intValue());
          if (minNumberOfSeats != null)
            tour.setMinumumSeats(minNumberOfSeats.intValue());
          if (numberOfDays != null)
            tour.setNumberOfDays(numberOfDays.intValue());
          if (kilometers != null)
            tour.setLength(kilometers.floatValue());

          if (estimatedSeatsUsed != -1)
            tour.setEstimatedSeatsUsed(estimatedSeatsUsed);

          if (arrivalAddressIds.length > 0) {
            Address addrs;
            AddressHome aHome = (AddressHome) IDOLookup.getHome(Address.class);
            try {
              for (int i = 0; i < arrivalAddressIds.length; i++) {
                addrs = aHome.findByPrimaryKey(arrivalAddressIds[i]);
                product.addArrivalAddress(addrs);
  //                product.addTo(Address.class,arrivalAddressIds[i]);
              }
            }catch (Exception e) {
              e.printStackTrace(System.err);
            }
          }


          HotelPickupPlaceHome hppHome = (HotelPickupPlaceHome) IDOLookup.getHome(HotelPickupPlace.class);
          service.removeAllHotelPickupPlaces();
//          hppHome.create().removeFromService(service);
          //service.removeFrom(HotelPickupPlace.class);

          if(hotelPickupPlaceIds.length > 0){
            for (int i = 0; i < hotelPickupPlaceIds.length; i++) {
              if (hotelPickupPlaceIds[i] != -1)
              try{
                ((is.idega.idegaweb.travel.data.HotelPickupPlaceHome)com.idega.data.IDOLookup.getHome(HotelPickupPlace.class)).findByPrimaryKey(new Integer(hotelPickupPlaceIds[i])).addToService(service);
//                service.addTo(((is.idega.idegaweb.travel.data.HotelPickupPlaceHome)com.idega.data.IDOLookup.getHome(HotelPickupPlace.class)).findByPrimaryKey(new Integer(hotelPickupPlaceIds[i])));
              }catch (IDOAddRelationshipException sql) {}
            }
            tour.setHotelPickup(true);
          }else{
            tour.setHotelPickup(false);
          }

          tour.store();


          this.removeDepartureDaysApplication(this.getIWApplicationContext(), tour);
          setActiveDays(serviceId, activeDays);


          ProductCategory pCat = ( (ProductCategoryHome) IDOLookup.getHomeLegacy(ProductCategory.class)).getProductCategory(ProductCategoryFactoryBean.CATEGORY_TYPE_TOUR);
          try {
            if (pCat != null) {
              product.removeAllFrom(ProductCategory.class);
              pCat.addTo(Product.class, serviceId);
      //        product.addTo(pCat);
            }
          }catch (SQLException sql) {
          }

          userT.commit();
      }catch (Exception e) {
          e.printStackTrace(System.err);
          userT.rollback();
      }


      return serviceId;
  }

  public int getNumberOfTours(int serviceId, IWTimestamp fromStamp, IWTimestamp toStamp) {
    int returner = 0;
    try {
      IWTimestamp toTemp = new IWTimestamp(toStamp);

      int counter = 0;

      int[] daysOfWeek = new int[]{};//is.idega.idegaweb.travel.data.ServiceDayBMPBean.getDaysOfWeek(serviceId);
      try {
        ServiceDayHome sdayHome = (ServiceDayHome) IDOLookup.getHome(ServiceDay.class);
        ServiceDay sDay = sdayHome.create();
        daysOfWeek = sDay.getDaysOfWeek(serviceId);
      }catch (Exception e) {
        e.printStackTrace(System.err);
      }

      int fromDayOfWeek = fromStamp.getDayOfWeek();
      int toDayOfWeek = toStamp.getDayOfWeek();

      toTemp.addDays(1);
      int daysBetween = toStamp.getDaysBetween(fromStamp, toTemp);

      if (fromStamp.getWeekOfYear() != toTemp.getWeekOfYear()) {
          daysBetween = daysBetween - (8 - fromDayOfWeek + toDayOfWeek);

          for (int i = 0; i < daysOfWeek.length; i++) {
              if (daysOfWeek[i]  >= fromDayOfWeek) {
                ++counter;
              }
              if (daysOfWeek[i] <= toDayOfWeek) {
                ++counter;
              }
          }

          counter += ( (daysBetween / 7) * daysOfWeek.length );

      }else {
          for (int i = 0; i < daysOfWeek.length; i++) {
              if ((daysOfWeek[i]  >= fromDayOfWeek) && (daysOfWeek[i] <= toDayOfWeek)) {
                ++counter;
              }
          }
      }
      returner = counter;

    }catch (Exception e) {
        e.printStackTrace(System.err);
    }

    return returner;
  }


  public Tour getTour(Product product) throws TourNotFoundException, RemoteException{
    Tour tour = null;
    try {
      tour = ((is.idega.idegaweb.travel.service.tour.data.TourHome)com.idega.data.IDOLookup.getHome(Tour.class)).findByPrimaryKey(product.getPrimaryKey());
    }
    catch (FinderException sql) {
      throw new TourNotFoundException();
    }
    return tour;
  }



  public DropdownMenu getDepartureDaysDropdownMenu(IWContext iwc, List days, String name) {
    DropdownMenu menu = new DropdownMenu(name);
    IWTimestamp stamp;

    for (int i = 0; i < days.size(); i++) {
      stamp = (IWTimestamp) days.get(i);
      menu.addMenuElement(stamp.toSQLDateString(),stamp.getLocaleDate(iwc));
    }

    return menu;
  }

  /**
   * return a date if the inserted date is part of a tour
   */
  private IWTimestamp getDepartureDateForDate(IWContext iwc, Tour tour, IWTimestamp stamp) throws RemoteException{
    IWTimestamp returnStamp = null;

    IWTimestamp stamp1 = null;
    IWTimestamp stamp2 = null;
    boolean found = false;
    int numberOfDays = tour.getNumberOfDays();

    IWTimestamp temp1 = new IWTimestamp(stamp);
      temp1.addDays(numberOfDays);
    IWTimestamp temp2 = new IWTimestamp(stamp);
      temp2.addDays(-1 * numberOfDays);


    List days = getDepartureDays(iwc, tour, temp1, temp2, true);

    if ( numberOfDays > 1) {
      for (int i = 0; i < days.size(); i++) {
        if (i == 0) {
          stamp1 = (IWTimestamp) days.get(0);
          stamp2 = (IWTimestamp) days.get(1);
          ++i;
        }else {
          stamp1 = (IWTimestamp) days.get(i-1);
          stamp2 = (IWTimestamp) days.get(i);
        }

        if (stamp.isLaterThanOrEquals(stamp1) && stamp2.isLaterThan(stamp)) {
          found = true;
          break;
        }
      }

      if (found) {
        int daysBetween = stamp.getDaysBetween(stamp1, stamp);
        if (stamp1.equals(stamp)) {
          return stamp;
        }else if (stamp2.equals(stamp)) {
          return stamp;
        }else if (daysBetween < numberOfDays) {
          return stamp1;
        }else if (daysBetween >= numberOfDays) {
          return null;
        }

      }else {
        return null;
      }
    }else {

    }


    return returnStamp;
  }

  public boolean getIfDay(IWContext iwc, Contract contract, Tour tour, IWTimestamp stamp) {
    try {
      IWTimestamp temp = getDepartureDateForDate(iwc, tour, stamp);
      if (temp == null) {
        return getIfDay(iwc, contract, getProductBusiness().getProduct((Integer) tour.getPrimaryKey()), stamp);
      }else {
        return (stamp.equals(temp));
      }

    }catch (Exception e) {
      e.printStackTrace(System.err);
      return false;
    }
  }

  public boolean getIfDay(IWContext iwc, Tour tour, IWTimestamp stamp, boolean includePast) {
    try {
      IWTimestamp temp = getDepartureDateForDate(iwc, tour, stamp);
      if (temp == null) {
        Product product = getProductBusiness().getProduct((Integer) tour.getPrimaryKey());
        return getIfDay(iwc, product, product.getTimeframes(), stamp, includePast, true);
      }else {
        return (stamp.equals(temp));
      }
    }catch (Exception e) {
      e.printStackTrace(System.err);
      return false;
    }
  }

  public List getDepartureDays(IWContext iwc, Tour tour) {
    return getDepartureDays(iwc, tour, true);
  }

  public List getDepartureDays(IWContext iwc, Tour tour, boolean showPast) {
    return getDepartureDays(iwc, tour, null, null, showPast);
  }

  public void removeDepartureDaysApplication(IWApplicationContext iwac, Tour tour) throws RemoteException{
    Enumeration enum = iwac.getApplication().getAttributeNames();
    String name;
    while (enum.hasMoreElements()) {
      name = (String) enum.nextElement();
      if (name.indexOf("tourDepDays"+tour.getPrimaryKey().toString()+"_") != -1) {
        iwac.removeApplicationAttribute(name);
      }
    }
  }


  public List getDepartureDays(IWContext iwc, Tour tour, IWTimestamp fromStamp, IWTimestamp toStamp, boolean showPast) {
    List returner = new Vector();

    try {
      Product product = getProductBusiness().getProduct((Integer) tour.getPrimaryKey());
      Service service = ((is.idega.idegaweb.travel.data.ServiceHome)com.idega.data.IDOLookup.getHome(Service.class)).findByPrimaryKey(tour.getPrimaryKey());
      Timeframe[] frames = product.getTimeframes();
      Timeframe tempFrame = (Timeframe) IDOLookup.create(Timeframe.class);

      String applicationString = "tourDepDays"+tour.getPrimaryKey().toString()+"_"+fromStamp+"_"+toStamp+"_"+showPast;

      List tempList = (List) iwc.getApplicationAttribute(applicationString);
      if (tempList != null) {
        returner = tempList;
      }else {
//        System.err.println("TourBusiness : getDepartDays : "+fromStamp+ " - " +toStamp);
        for (int i = 0; i < frames.length; i++) {
          //System.err.println("------------------------------------------------");
          //System.err.println("-----------------------"+i+"------------------------");
          //System.err.println("------------------------------------------------");

          boolean yearly = frames[i].getIfYearly();

          IWTimestamp tFrom = new IWTimestamp(frames[i].getFrom());
          IWTimestamp tTo = new IWTimestamp(frames[i].getTo());

          IWTimestamp from = null;
          if (fromStamp != null) from = new IWTimestamp(fromStamp);
          IWTimestamp to = null;
          if (toStamp != null) to = new IWTimestamp(toStamp);

//          System.err.println("tFrom... : "+tFrom.toSQLDateString());
//          System.err.println("tTo..... : "+tTo.toSQLDateString());

          int numberOfDays = tour.getNumberOfDays();
            if (numberOfDays < 1) numberOfDays = 1;

          if (from == null) {
            from = new IWTimestamp(tFrom);
          }
          if (to == null) {
            to   = new IWTimestamp(tTo);
          }

          from.addDays(-1);
          to.addDays(1);

          int yearsBetween = 0;
          int toY = to.getYear();

          frames[i] = fixTimeframe(frames[i], from, to);
          tFrom = new IWTimestamp(frames[i].getFrom());
          tTo = new IWTimestamp(frames[i].getTo());

          int daysBetween = IWTimestamp.getDaysBetween(from, to);

          to = new IWTimestamp(from);
            to.addDays(daysBetween);
          yearsBetween = to.getYear() - toY;
//          System.err.println("tFrom : "+tFrom.toSQLDateString());
//          System.err.println("tTo   : "+tTo.toSQLDateString());

/*
          System.err.println("------------------------------------------------");
          System.err.println("from : "+from.toSQLDateString());
          System.err.println("to   : "+to.toSQLDateString());
          System.err.println("------------------------------------------------");
          System.err.println("tFrom... : "+tFrom.toSQLDateString());
          System.err.println("tTo..... : "+tTo.toSQLDateString());
*/

        IWTimestamp stamp = new IWTimestamp(from);
        IWTimestamp temp;

        IWTimestamp now = IWTimestamp.RightNow();

        tempFrame.setFrom(tFrom.getTimestamp());
        tempFrame.setTo(tTo.getTimestamp());

          while (to.isLaterThan(stamp)) {
            //System.err.println("Stamp : "+stamp.toSQLDateString());
            temp = getNextAvailableDay(iwc, tour, product,tempFrame, stamp);
//            temp = getNextAvailableDay(iwc, tour, product,frames[i], stamp);
            if (temp != null) {
              if (getStockroomBusiness().isInTimeframe(tFrom, tTo, temp, yearly)) {
                //System.err.println("TEMP : "+temp.toSQLDateString()+" .... yearsBetween : "+yearsBetween+" ... yearly ("+yearly+")");
                if (yearly) {
                  temp.addYears(-yearsBetween);
                }
                //System.err.println("TEMP : "+temp.toSQLDateString());
                if (!showPast) {
                  if (temp.isLaterThanOrEquals(now)) {
                    returner.add(temp);
                    stamp = new IWTimestamp(temp);
                  }else {
                    stamp = new IWTimestamp(temp);
                  }
                }else {
                  returner.add(temp);
                  stamp = new IWTimestamp(temp);
                }

                if (yearly) {
                  stamp.addYears(yearsBetween);
                }

              }
              //stamp = new IWTimestamp(temp);
            }else {
              stamp.addDays(numberOfDays);
            }

          }
          //System.err.println("STAMP : "+stamp.toSQLDateString());
        }
        iwc.setApplicationAttribute(applicationString, returner);
      }
//      Exception ex = new  Exception("Repps");
//        throw ex;
    }catch (Exception sql) {
      sql.printStackTrace(System.err);
    }

    return returner;
  }

  public IWTimestamp getNextAvailableDay(IWContext iwc, Tour tour, Product product, Timeframe timeframe, IWTimestamp from) {
    return getNextAvailableDay(iwc, tour, product, new Timeframe[] {timeframe}, from);
  }

  public IWTimestamp getNextAvailableDay(IWContext iwc, Tour tour, Product product,  IWTimestamp from) throws SQLException, RemoteException {
    return getNextAvailableDay(iwc, tour, product, product.getTimeframes(), from);
  }

  public IWTimestamp getNextAvailableDay(IWContext iwc, Tour tour, Product product, Timeframe[] timeframes, IWTimestamp from) {
    IWTimestamp stamp = new IWTimestamp(from);
    boolean found = false;
/**
 * @todo Speed up....
 */
    try {
      int nod = tour.getNumberOfDays();
      if (nod < 1) nod = 1;
      int teljari = 0;


      while (teljari++ < nod) {
        stamp.addDays(1);
        if (getIfDay(iwc,product, timeframes, stamp, false, true)) {
          /** @todo breytti false i true..... skoda takk */
          found = true;
          break;
        }

      }

    }catch (Exception e) {
      e.printStackTrace(System.err);
    }
    if (found) {
      return stamp;
    }else {
      return null;
    }
  }

  protected ProductBusiness getProductBusiness() throws RemoteException {
    return (ProductBusiness) IBOLookup.getServiceInstance(getIWApplicationContext(), ProductBusiness.class);
  }
  
  protected StockroomBusiness getStockroomBusiness() throws RemoteException {
    return (StockroomBusiness) IBOLookup.getServiceInstance(getIWApplicationContext(), StockroomBusiness.class);
  }
}
