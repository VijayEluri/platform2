package is.idega.idegaweb.travel.business;

import javax.ejb.FinderException;
import java.rmi.RemoteException;
import com.idega.idegaweb.IWApplicationContext;
import com.idega.business.IBOLookup;
import com.idega.data.*;
import com.idega.block.trade.stockroom.business.*;
import com.idega.block.trade.stockroom.data.*;
import is.idega.idegaweb.travel.data.*;
import java.sql.Timestamp;
import com.idega.core.data.*;
import is.idega.idegaweb.travel.data.HotelPickupPlace;
import java.sql.SQLException;
import com.idega.util.*;
import java.sql.SQLException;
import java.util.*;
import com.idega.util.datastructures.HashtableDoubleKeyed;
import com.idega.presentation.IWContext;
import com.idega.transaction.IdegaTransactionManager;
import javax.transaction.TransactionManager;
import java.sql.Date;
import com.idega.block.trade.data.Currency;

/**
 * Title:        IW Travel
 * Description:  Stockroom Business
 * Copyright:    Copyright (c) 2001
 * Company:      idega.is
 * @author 2000 - idega team - <br><a href="mailto:gummi@idega.is">Gu�mundur �g�st S�mundsson</a><br><a href="mailto:gimmi@idega.is">Gr�mur J�nsson</a>
 * @version 1.0
 */

public class TravelStockroomBusinessBean extends StockroomBusinessBean implements TravelStockroomBusiness {

  private String resellerDayHashtableSessionName = "resellerDayHashtable";
  private String resellerDayOfWeekHashtableSessionName = "resellerDayOfWeekHashtable";
  private String serviceDayHashtableSessionName = "serviceDayHashtable";

  protected Timeframe timeframe;

  public TravelStockroomBusinessBean() {
  }

  public TravelStockroomBusinessBean getNewInstance(IWApplicationContext iwac) throws RemoteException{
    return (TravelStockroomBusinessBean) IBOLookup.getServiceInstance(iwac, TravelStockroomBusiness.class);
  }

  public void addSupplies(int product_id, float amount) {
    throw new java.lang.UnsupportedOperationException("Method addSupplies() not yet implemented.");
  }

  public void depleteSupplies(int product_id, float amount) {
    throw new java.lang.UnsupportedOperationException("Method depleteSupplies() not yet implemented.");
  }

  public void setSupplyStatus(int productId, float status, int period) throws SQLException {
    Supplies supplies = ((com.idega.block.trade.stockroom.data.SuppliesHome)com.idega.data.IDOLookup.getHomeLegacy(Supplies.class)).createLegacy();

    supplies.setProductId(productId);
    supplies.setCurrentSupplies(status);
    supplies.setRecordTime(idegaTimestamp.RightNow().getTimestamp());

    if(period > -1){
      supplies.setPeriod(period);
    }

    supplies.insert();
  }

  public float getSupplyStatus(int productId) throws SQLException {
    Supplies supplies = (Supplies)com.idega.block.trade.stockroom.data.SuppliesBMPBean.getStaticInstance(Supplies.class);
    List lSupplies = EntityFinder.findAllByColumnOrdered(supplies,com.idega.block.trade.stockroom.data.SuppliesBMPBean.getColumnNameProductId(),Integer.toString(productId),com.idega.block.trade.stockroom.data.SuppliesBMPBean.getColumnNameRecordTime());
    if(lSupplies != null && lSupplies.size() > 0){
      return ((Supplies)lSupplies.get(0)).getCurrentSupplies();
    }else{
      return 0;
    }
  }

  public float getSupplyStatus(int productId, Date date) throws SQLException {
    Supplies supplies = (Supplies)com.idega.block.trade.stockroom.data.SuppliesBMPBean.getStaticInstance(Supplies.class);
    List lSupplies = EntityFinder.findAll(supplies,"SELECT * FROM  "+supplies.getEntityName()+" WHERE "+com.idega.block.trade.stockroom.data.SuppliesBMPBean.getColumnNameProductId()+" = "+Integer.toString(productId)+" AND "+com.idega.block.trade.stockroom.data.SuppliesBMPBean.getColumnNameRecordTime()+" <= '"+date.toString()+"' ORDER BY "+ com.idega.block.trade.stockroom.data.SuppliesBMPBean.getColumnNameRecordTime(),1);
    if(lSupplies != null && lSupplies.size() > 0){
      return ((Supplies)lSupplies.get(0)).getCurrentSupplies();
    }else{
      return 0;
    }
  }

  public int createPriceCategory(int supplierId, String name, String description, String type, String extraInfo, boolean isNetbooking) throws Exception {
      return this.createPriceCategory(supplierId, name, description,type,extraInfo, isNetbooking, -1);
  }

  public int createPriceCategory(int supplierId, String name, String description, String type, String extraInfo, boolean isNetbooking, int parentId) throws Exception {

    PriceCategory cat = ((com.idega.block.trade.stockroom.data.PriceCategoryHome)com.idega.data.IDOLookup.getHomeLegacy(PriceCategory.class)).createLegacy();

    cat.setName(name);

    if(parentId != -1) {
      cat.setParentId(parentId);
    }

    if(description != null){
      cat.setDescription(description);
    }

    if(type != null){
      cat.setType(type);
    }

    if(extraInfo != null){
      cat.setExtraInfo(extraInfo);
    }

    cat.isNetbookingCategory(isNetbooking);
    cat.setSupplierId(supplierId);

    cat.insert();


    return cat.getID();
  }


  public int createService(int supplierId, Integer fileId, String serviceName, String number, String serviceDescription, boolean isValid, int[] addressIds, Timestamp departure, Timestamp arrival, int discountTypeId) throws Exception{
    return createService(-1,supplierId, fileId, serviceName, number, serviceDescription, isValid, addressIds, departure,arrival, discountTypeId);
  }

  public int updateService(int serviceId,int supplierId, Integer fileId, String serviceName, String number, String serviceDescription, boolean isValid, int[] addressIds, Timestamp departure, Timestamp arrival, int discountTypeId) throws Exception {
    return createService(serviceId,supplierId, fileId, serviceName, number, serviceDescription, isValid, addressIds, departure,arrival, discountTypeId);
  }

  private int createService(int serviceId,int supplierId, Integer fileId, String serviceName, String number, String serviceDescription, boolean isValid, int[] addressIds, Timestamp departure, Timestamp arrival, int discountTypeId) throws Exception{
//    TransactionManager transaction = IdegaTransactionManager.getInstance();
    try{
      //transaction.begin();

      int id = -1;
      if (serviceId == -1) {
        id = createProduct(supplierId,fileId,serviceName,number,serviceDescription,isValid, addressIds, discountTypeId);
      }else {
        id = updateProduct(serviceId, supplierId,fileId,serviceName,number,serviceDescription,isValid, addressIds, discountTypeId);
      }

      Service service = null;
      try {
        service = ((is.idega.idegaweb.travel.data.ServiceHome)com.idega.data.IDOLookup.getHome(Service.class)).findByPrimaryKey(new Integer(id));
      } catch (FinderException fe) {
        service = ((is.idega.idegaweb.travel.data.ServiceHome)com.idega.data.IDOLookup.getHome(Service.class)).create();
        service.setPrimaryKey(new Integer(id));
//        service.setID(id);
      }

        service.setDepartureTime(departure);
        service.setAttivalTime(arrival);

      service.store();
/*      if (serviceId == -1) {
        service.insert();
      }else {
        service.update();
      }*/


        if (timeframe != null) {
          try {
            Product product = ProductBusiness.getProduct(id);
            product.addTo(timeframe);
          }catch (SQLException sql) {
            //sql.printStackTrace(System.err);
          }
        }else {
          System.err.println("Timeframe is null");
        }
        //transaction.commit();

      return id;
    }catch(SQLException e){
      //transaction.rollback();
      e.printStackTrace(System.err);
      throw new RuntimeException("IWE226TB89");
    }
  }



  public void setTimeframe(idegaTimestamp from, idegaTimestamp to, boolean yearly) throws SQLException {
    setTimeframe(-1,from,to,yearly);
  }

  public void setTimeframe(int timeframeId, idegaTimestamp from, idegaTimestamp to, boolean yearly) throws SQLException {
    if (timeframeId != -1) {
      if ((from != null) && (to != null)) {
        timeframe = ((com.idega.block.trade.stockroom.data.TimeframeHome)com.idega.data.IDOLookup.getHomeLegacy(Timeframe.class)).findByPrimaryKeyLegacy(timeframeId);
          timeframe.setTo(to.getTimestamp());
          timeframe.setFrom(from.getTimestamp());
          timeframe.setYearly(yearly);
          timeframe.update();
      }
    }else {
      if ((from != null) && (to != null)) {
        timeframe = ((com.idega.block.trade.stockroom.data.TimeframeHome)com.idega.data.IDOLookup.getHomeLegacy(Timeframe.class)).createLegacy();
          timeframe.setTo(to.getTimestamp());
          timeframe.setFrom(from.getTimestamp());
          timeframe.setYearly(yearly);
          timeframe.insert();
      }
    }
  }

  /**
   * @deprecated
   */
  public Product[] getProducts(Reseller reseller) {
    Product[] returner = {};
    try {
      Reseller parent = (Reseller) reseller.getParentEntity();
      if (parent != null) {

      }
    }catch (Exception sql) {
      sql.printStackTrace(System.err);
    }

    return returner;
  }

  /**
   * @deprecated
   */
  public Product[] getProducts(int supplierId) {
    List list = ProductBusiness.getProducts(supplierId);
    if (list == null) {
      return new Product[]{};
    }else {
      return (Product[]) list.toArray(new Product[]{});
    }
  }

  /**
   * @deprecated
   */
  public Product[] getProducts(int supplierId, idegaTimestamp stamp) {
    List list = ProductBusiness.getProducts(supplierId, stamp);
    if (list == null) {
      return new Product[]{};
    }else {
      return (Product[]) list.toArray(new Product[]{});
    }
  }

  /**
   * @deprecated
   */
  public Product[] getProducts(int supplierId, idegaTimestamp from, idegaTimestamp to) {
    List list = ProductBusiness.getProducts(supplierId, from, to);
    if (list == null) {
      return new Product[]{};
    }else {
      return (Product[]) list.toArray(new Product[]{});
    }
  }


  public Service getService(Product product) throws ServiceNotFoundException, RemoteException {
    Service service = null;
    try {
      service = ((is.idega.idegaweb.travel.data.ServiceHome)com.idega.data.IDOLookup.getHome(Service.class)).findByPrimaryKey(product.getPrimaryKey());
    }
    catch (FinderException sql) {
      throw new ServiceNotFoundException(sql);
    }
    return service;
  }

  public Timeframe getTimeframe(Product product) throws ServiceNotFoundException, TimeframeNotFoundException {
    Timeframe timeFrame = null;
    try {
//      Service service = TravelStockroomBusiness.getService(product);
      timeFrame = product.getTimeframe();
    }
    catch (SQLException sql) {
      throw new TimeframeNotFoundException();
    }/*
    catch (ServiceNotFoundException snf) {
      throw new ServiceNotFoundException();
    }*/
    return timeFrame;
  }


  public PriceCategory[] getPriceCategories(int supplierId) {
    PriceCategory[] returner = {};
    try {
      returner = (PriceCategory[]) com.idega.block.trade.stockroom.data.PriceCategoryBMPBean.getStaticInstance(PriceCategory.class).findAllByColumn(com.idega.block.trade.stockroom.data.PriceCategoryBMPBean.getColumnNameSupplierId(),Integer.toString(supplierId), com.idega.block.trade.stockroom.data.PriceCategoryBMPBean.getColumnNameIsValid(), "Y");
    }catch (SQLException sql) {
      sql.printStackTrace(System.err);
    }
    return returner;
  }


  private HashtableDoubleKeyed getServiceDayHashtable(IWContext iwc) {
      HashtableDoubleKeyed hash = (HashtableDoubleKeyed) iwc.getSessionAttribute(serviceDayHashtableSessionName);
      if (hash == null) {
        hash =  new HashtableDoubleKeyed();
        iwc.setSessionAttribute(serviceDayHashtableSessionName, hash);
      }

      return hash;
  }

  public boolean getIfDay(IWContext iwc,int productId, int dayOfWeek) throws RemoteException{
      boolean returner = false;

      HashtableDoubleKeyed hash = getServiceDayHashtable(iwc);
      Object obj = hash.get(productId+"_"+dayOfWeek,"");
      if (obj == null) {
        returner = getServiceDayHome().getIfDay(productId, dayOfWeek);
        hash.put(productId+"_"+dayOfWeek,"",new Boolean(returner));
      }else {
        returner = ((Boolean)obj).booleanValue();
      }

      return returner;
  }

  public boolean getIfDay(IWContext iwc, Product product, idegaTimestamp stamp) throws ServiceNotFoundException, TimeframeNotFoundException, SQLException, RemoteException {
    return getIfDay(iwc, product, product.getTimeframes(), stamp, true, true);
  }

  public boolean getIfDay(IWContext iwc, Product product, Timeframe[] timeframes, idegaTimestamp stamp) throws ServiceNotFoundException, TimeframeNotFoundException, RemoteException {
    return getIfDay(iwc, product, timeframes, stamp, true, false);
  }

  public boolean getIfDay(IWContext iwc, Product product, Timeframe[] timeframes, idegaTimestamp stamp, boolean includePast, boolean fixTimeframe) throws ServiceNotFoundException, TimeframeNotFoundException, RemoteException {
      boolean isDay = false;
      String key1 = Integer.toString(product.getID());
      String key2 = stamp.toSQLDateString();

//      System.err.println("Checking day : "+stamp.toSQLDateString());
      HashtableDoubleKeyed serviceDayHash = getServiceDayHashtable(iwc);
      //Object obj = serviceDayHash.get(key1, key2);
      Object obj = null;
      if (obj == null) {

          int dayOfWeek = stamp.getDayOfWeek();
          // = product.getTimeframes();

          boolean tooEarly = false;
          if (!includePast) {
            idegaTimestamp now = idegaTimestamp.RightNow();
            idegaTimestamp tNow = new idegaTimestamp(now.getDay(), now.getMonth(), now.getYear());
            if (tNow.isLaterThan(stamp)) {
              tooEarly = true;
            }
          }

          if (!tooEarly) {
            boolean isValidWeekDay = getIfDay(iwc, product.getID(), dayOfWeek);
            if (isValidWeekDay) {
//              System.err.println("repps 1");
              if (isDayValid(timeframes, stamp, fixTimeframe)) {
//              System.err.println("repps 2");
                isDay = true;
                serviceDayHash.put(key1, key2, new Boolean(true) );
              }
              else {
//              System.err.println("repps 3");
                serviceDayHash.put(key1, key2, new Boolean(false) );
              }
            }else {
//              System.err.println("repps 4");
              serviceDayHash.put(key1, key2, new Boolean(false) );
            }
          }
      }
      else {
        isDay = ((Boolean) obj).booleanValue();
      }
      return isDay;
  }

  private boolean isDateValid(Contract contract, idegaTimestamp stamp) throws RemoteException {
    idegaTimestamp theStamp= idegaTimestamp.RightNow();
      theStamp.addDays(contract.getExpireDays()-1);

    return idegaTimestamp.isInTimeframe(new idegaTimestamp(contract.getFrom()), new idegaTimestamp(contract.getTo()), stamp, false);
    /*
    if (stamp.isLaterThan(theStamp)) {
      return new idegaTimestamp(contract.getTo()).isLaterThan(stamp);
    }else {
      return false;
    }
    */

  }

  private boolean isDayValid(Timeframe[] frames, idegaTimestamp stamp, boolean fixTimeframe) {
    return isDayValid(frames, null, stamp, fixTimeframe);
  }

  private boolean isDayValid(Timeframe[] frames, Contract contract, idegaTimestamp stamp) {
    return isDayValid(frames, contract, stamp, false);
  }

  private boolean isDayValid(Timeframe[] frames, Contract contract, idegaTimestamp stamp, boolean fixTimeframe) {

    boolean returner = false;

    try {
      boolean goOn = false;
      if (contract == null) {
        goOn = true;
      }else {
        goOn = isDateValid(contract, stamp);
      }


      if (goOn) {
        boolean isYearly = false;

        for (int i = 0; i < frames.length; i++) {
          if (fixTimeframe) {
//            System.err.println("---------------------------------------------------------------------------------------------------------------");
//            System.err.println("isDayValid.... : from : "+new idegaTimestamp(frames[i].getFrom()).toSQLDateString());
//            System.err.println(".............. : to   : "+new idegaTimestamp(frames[i].getTo()).toSQLDateString());
            fixTimeframe(frames[i], stamp);
//            System.err.println(":::::::FIXING:::::: "+stamp.toSQLDateString());
//            System.err.println("isDayValid.... : from : "+new idegaTimestamp(frames[i].getFrom()).toSQLDateString());
//            System.err.println(".............. : to   : "+new idegaTimestamp(frames[i].getTo()).toSQLDateString());
          }
//          System.err.println(".............. : year : "+frames[i].getYearly());
          isYearly = frames[i].getIfYearly();
          returner = idegaTimestamp.isInTimeframe(new idegaTimestamp(frames[i].getFrom()), new idegaTimestamp(frames[i].getTo() ), stamp, isYearly);
          if (returner) break;
        }


      }
    }catch (Exception e) {
        e.printStackTrace(System.err);
    }

    return returner;
  }

  public Timeframe fixTimeframe(Timeframe frame, idegaTimestamp stamp) {
    return fixTimeframe(frame, stamp, null);
  }

  public Timeframe fixTimeframe(Timeframe frame, idegaTimestamp from, idegaTimestamp to) {
    idegaTimestamp tFrom = new idegaTimestamp(frame.getFrom());
    idegaTimestamp tTo = new idegaTimestamp(frame.getTo());

    if (frame.getYearly()) {
      int fromYear = tFrom.getYear();
      int fromY = from.getYear();
      int fromMonth = tFrom.getMonth();
      int fromM = from.getMonth();

      int toYear   = tTo.getYear();
      int toMonth = tTo.getMonth();
      int yearsBetween = 0;

      int toY = 0;
      int toM = 0;
      if (to != null) {
        toY = to.getYear();
        toM = to.getMonth();
      }else {
        toY = fromY;
        toM = fromM;
      }

      if (fromYear == toYear) { // If timeframe is in the same year...
        tFrom.setYear(fromY);
        tTo.setYear(toY);
        //from.setYear(fromYear);
      }else {
          if (fromY <= fromYear) {
            if (fromM < toMonth) {
              tFrom.addYears(toY - toYear);
              tTo.addYears(toY - toYear);
              //System.err.println("Tepps : fromM ("+fromM+") < toMonth ("+toMonth+")");
            }else if (fromM == toMonth) {
              if (from.getDay() < tTo.getDay()) {
                tFrom.addYears(toY - toYear);
                tTo.addYears(toY - toYear);
              }
              //System.err.println("fromM ("+fromM+") >= toMonth ("+toMonth+")");
            }
          }else {
            if (toM < fromMonth) {
//              System.err.println("Sepps : toM ("+toM+") < fromMonth ("+fromMonth+")");
              tFrom.addYears(toY - toYear);
              tTo.addYears(toY - toYear);
            }else {
              if (to != null) {
//                System.err.println("toDay : "+to.getDay());
//                System.err.println("tFrom : "+tFrom.getDay());
                if (to.getDay() > tFrom.getDay()) {
                  tFrom.addYears(toY - toYear);
                  tTo.addYears(toY - toYear);
//                  System.err.println("craniton");
                }
              }
//              System.err.println("Ranus : toM ("+toM+") >= fromMonth ("+fromMonth+")");
            }
//                  System.err.println("sraneson");
          }
      }
      //System.err.println("yearsBetween : "+yearsBetween);
    }

    frame.setFrom(tFrom.getTimestamp());
    frame.setTo(tTo.getTimestamp());

    return frame;
  }

  public HashtableDoubleKeyed getResellerDayHashtable(IWContext iwc) {
      HashtableDoubleKeyed hash = (HashtableDoubleKeyed) iwc.getSessionAttribute(resellerDayHashtableSessionName);
      if (hash == null) {
        hash =  new HashtableDoubleKeyed();
        iwc.setSessionAttribute(resellerDayHashtableSessionName, hash);
      }
      return hash;
  }

  public void removeResellerHashtables(IWContext iwc) {
      iwc.removeSessionAttribute(resellerDayHashtableSessionName);
      iwc.removeSessionAttribute(resellerDayOfWeekHashtableSessionName);
  }

  public void removeServiceDayHashtable(IWContext iwc) {
    iwc.removeSessionAttribute(serviceDayHashtableSessionName);
  }

  private HashtableDoubleKeyed getResellerDayOfWeekHashtable(IWContext iwc) {
      HashtableDoubleKeyed hash = (HashtableDoubleKeyed) iwc.getSessionAttribute(resellerDayOfWeekHashtableSessionName);
      if (hash == null) {
        hash =  new HashtableDoubleKeyed();
        iwc.setSessionAttribute(resellerDayOfWeekHashtableSessionName, hash);
      }
      return hash;
  }

  public boolean getIfExpired(Contract contract, idegaTimestamp stamp) throws RemoteException {
    boolean returner = false;
      int daysBetween = stamp.getDaysBetween(idegaTimestamp.RightNow(), stamp);
      if (daysBetween < contract.getExpireDays()) {
        returner = true;
      }
    return returner;
  }

  public boolean getIfDay(IWContext iwc, Contract contract, Product product, idegaTimestamp stamp) throws ServiceNotFoundException, TimeframeNotFoundException, SQLException, RemoteException {
      boolean isDay = false;
      if (contract != null) {
        String key1 = ( (Integer) contract.getPrimaryKey()).toString();
        String key2 = stamp.toSQLDateString();

        int dayOfWeek = stamp.getDayOfWeek();
        boolean isValidWeekDay = false;
        boolean isValidServiceDay = false;


        isValidServiceDay = getIfDay(iwc,product,product.getTimeframes(), stamp);

        if (isValidServiceDay) {
          HashtableDoubleKeyed resellerDayOfWeekHash = getResellerDayHashtable(iwc);
          Object object = resellerDayOfWeekHash.get(key1, key2);
          if (object == null) {
              isValidWeekDay = getResellerDayHome().getIfDay(contract.getResellerId(),contract.getServiceId() , dayOfWeek);
              resellerDayOfWeekHash.put(key1, key2, new Boolean(isValidWeekDay));
          }else {
            isValidWeekDay = ((Boolean) object).booleanValue();
          }
        }


        HashtableDoubleKeyed resellerDayHash = getResellerDayHashtable(iwc);
        Object obj = resellerDayHash.get(key1, key2);
        if (obj == null) {
          if (isValidWeekDay) {
            idegaTimestamp from = new idegaTimestamp(contract.getFrom());
            idegaTimestamp to = new idegaTimestamp(contract.getTo());
            if (stamp.isLaterThan(from) && to.isLaterThan(stamp)  ) {
              isDay = true;
              resellerDayHash.put(key1, key2, new Boolean(true));
            }else if (stamp.toSQLDateString().equals(from.toSQLDateString()) || stamp.toSQLDateString().equals(to.toSQLDateString())) {
              isDay = true;
              resellerDayHash.put(key1, key2, new Boolean(true));
            }else {
              resellerDayHash.put(key1, key2, new Boolean(false));
            }
          }else {
            resellerDayHash.put(key1, key2, new Boolean(false));
          }
        }else {
          isDay = ((Boolean) obj).booleanValue();
        }
      }else {
        System.err.println("TravelStockroomBusinessBean : getIfDay(iwc, contract, product, stamp) - Contract er null");
      }

      return isDay;
  }


  /**
   * @todo sko�a betur
   */
  public int getCurrencyIdForIceland(){
      Currency curr = ((com.idega.block.trade.data.CurrencyHome)com.idega.data.IDOLookup.getHomeLegacy(Currency.class)).createLegacy();
      int returner = -1;
      try {
        String iceKr = "�slenskar Kr�nur";
        String[] id = com.idega.data.SimpleQuerier.executeStringQuery("Select "+curr.getIDColumnName()+" from "+curr.getEntityName()+" where "+com.idega.block.trade.data.CurrencyBMPBean.getColumnNameCurrencyName() +" = '"+iceKr+"'");
        if (id == null || id.length == 0) {
            curr = ((com.idega.block.trade.data.CurrencyHome)com.idega.data.IDOLookup.getHomeLegacy(Currency.class)).createLegacy();
            curr.setCurrencyName(iceKr);
            curr.setCurrencyAbbreviation("ISK");
            curr.insert();
            returner = curr.getID();
        } else if (id.length > 0) {
          returner = Integer.parseInt(id[id.length -1]);
        }

      }
      catch (Exception e) {
        e.printStackTrace(System.err);
      }

      return returner;
  }

  public List getDepartureDays(IWContext iwc, Product product) {
    return getDepartureDays(iwc, product, null, null, true);
  }

  public List getDepartureDays(IWContext iwc, Product product, boolean showPast) {
    return getDepartureDays(iwc, product, null, null, showPast);
  }

  public List getDepartureDays(IWContext iwc, Product product, idegaTimestamp from, idegaTimestamp to) {
    return getDepartureDays(iwc, product, from, to, true);
  }

  public List getDepartureDays(IWContext iwc, Product product, idegaTimestamp fromStamp, idegaTimestamp toStamp, boolean showPast) {
    List returner = new Vector();
    try {
//      Service service = ((is.idega.idegaweb.travel.data.ServiceHome)com.idega.data.IDOLookup.getHomeLegacy(Service.class)).findByPrimaryKeyLegacy(product.getID());
      Timeframe[] frames = product.getTimeframes();

      for (int j = 0; j < frames.length; j++) {
        boolean yearly = frames[j].getIfYearly();


        idegaTimestamp tFrom = new idegaTimestamp(frames[j].getFrom());
        idegaTimestamp tTo = new idegaTimestamp(frames[j].getTo());


        idegaTimestamp from = null;
        if (fromStamp != null) from = new idegaTimestamp(fromStamp);
        idegaTimestamp to = null;
        if (toStamp != null) to = new idegaTimestamp(toStamp);

        if (from == null) {
          from = new idegaTimestamp(tFrom);
        }
        if (to == null) {
          to   = new idegaTimestamp(tTo);
        }

        int toMonth = tTo.getMonth();
        int toM = to.getMonth();
        int fromM = from.getMonth();
        int yearsBetween = 0;

        to.addDays(1);

        if (yearly) {
          int fromYear = tFrom.getYear();
          int toYear   = tTo.getYear();

          int fromY = from.getYear();
          int toY = to.getYear();

          int daysBetween = idegaTimestamp.getDaysBetween(from, to);

          if (fromYear == toYear) {
            from.setYear(fromYear);
          }else {
              if (fromY >= toYear) {
                if (fromM > toMonth) {
                  from.setYear(fromYear);
                }else {
                  from.setYear(toYear);
                }
              }
          }

          to = new idegaTimestamp(from);
            to.addDays(daysBetween);

          yearsBetween = to.getYear() - toY;
        }

        idegaTimestamp stamp = new idegaTimestamp(from);
        idegaTimestamp temp;

        if (!showPast) {
          idegaTimestamp now = idegaTimestamp.RightNow();
          if (now.isLaterThan(from) && to.isLaterThan(now)) {
            stamp = new idegaTimestamp(now);
          }else if (now.isLaterThan(from) && now.isLaterThan(to)) {
            stamp = new idegaTimestamp(to);
          }
        }

        int[] weekDays = new int[]{};
        try {
          ServiceDayHome sdayHome = (ServiceDayHome) IDOLookup.getHome(ServiceDay.class);
          ServiceDay sDay = sdayHome.create();
          weekDays = sDay.getDaysOfWeek(product.getID());
        }catch (Exception e) {
          e.printStackTrace(System.err);
        }


        //weekDays = is.idega.idegaweb.travel.data.ServiceDayBMPBean.getDaysOfWeek(product.getID());

        while (to.isLaterThan(stamp)) {
          for (int i = 0; i < weekDays.length; i++) {
            if (stamp.getDayOfWeek() == weekDays[i]) {
              if (yearly) stamp.addYears(-yearsBetween);
              returner.add(stamp);
              stamp = new idegaTimestamp(stamp);
              if (yearly) stamp.addYears(yearsBetween);
            }
          }
          stamp.addDays(1);
        }
      }

    }catch (SQLException sql) {
      sql.printStackTrace(System.err);
    }

    return returner;
  }

  public boolean isWithinTimeframe(Timeframe timeframe, idegaTimestamp stamp) {
    boolean yearly = timeframe.getIfYearly();
    idegaTimestamp from = new idegaTimestamp(timeframe.getFrom());
    idegaTimestamp to   = new idegaTimestamp(timeframe.getTo());
    return idegaTimestamp.isInTimeframe(from, to, stamp,yearly);
  }


  public ServiceDayHome getServiceDayHome() throws RemoteException{
    ServiceDayHome sdHome = (ServiceDayHome) IDOLookup.getHome(ServiceDay.class);
    return sdHome;
//    return (ServiceDay) IBOLookup.getServiceInstance(this.getIWApplicationContext(), ServiceDay.class);
  }

  public ResellerDayHome getResellerDayHome() throws RemoteException{
    ResellerDayHome rdHome = (ResellerDayHome) IDOLookup.getHome(ResellerDay.class);
    return rdHome;
//    return (ResellerDay) IBOLookup.getServiceInstance(this.getIWApplicationContext(), ResellerDay.class);
  }

  public Collection getTravelAddressIdsFromRefill(Product product, int tAddressId) throws RemoteException, IDOFinderException, FinderException {
    TravelAddress ta = ((TravelAddressHome) IDOLookup.getHomeLegacy(TravelAddress.class)).findByPrimaryKey(tAddressId);
    return getTravelAddressIdsFromRefill(product, ta);
  }

  public Collection getTravelAddressIdsFromRefill(Product product, TravelAddress tAddress) throws RemoteException, IDOFinderException {
    List list = getTravelAddressesFromRefill(product, tAddress);
    Collection coll = new Vector();
    Iterator iter = list.iterator();
    while (iter.hasNext()) {
      TravelAddress item = (TravelAddress) iter.next();
//      System.err.println("adding : "+item.getID());
//      coll.add(new Integer(item.getID()));
      coll.add(item.getPrimaryKey());
    }
    return coll;
  }

  private List getTravelAddressesFromRefill(Product product, TravelAddress tAddress) throws RemoteException, IDOFinderException {
    List addresses = ProductBusiness.getDepartureAddresses(product, true);
    int indexOf = addresses.indexOf(tAddress);

    TravelAddress tAdd;
    int startIndex = 0;
    for (int i = indexOf; i >= 0; i--) {
      tAdd = (TravelAddress) addresses.get(i);
      if (tAdd.getRefillStock()) {
        startIndex = i;
        break;
      }
    }

    int size = addresses.size();
    for (int i = (indexOf+1); i < size ; i++) {
      indexOf = i;
      tAdd = (TravelAddress) addresses.get(i);
      if (tAdd.getRefillStock()) {
        --indexOf;
        break;
      }
    }

    Collection coll = new Vector();
    List list = new Vector(addresses.subList(startIndex, indexOf+1));
    if (startIndex == (indexOf+1)) {
      list = new Vector();
        list.add(tAddress);
    }

    return list;
  }


  public int getTotalSeats(Product product, ServiceDay sDay, TravelAddress tAddress, idegaTimestamp stamp) throws RemoteException, IDOFinderException{
    Booker booker = (Booker) IBOLookup.getServiceInstance(this.getIWApplicationContext(), Booker.class);
    if (sDay != null) {
      int sDayMax = sDay.getMax();
      int temp = sDayMax;
      List addresses = getTravelAddressesFromRefill(product, tAddress);
//      List addresses = ProductBusiness.getDepartureAddresses(product, true);
      TravelAddress tempAddress;
      int addressesSize = addresses.size();
      int bookings = 0;
      for (int i = 0; i < addressesSize; i++) {
        tempAddress = (TravelAddress) addresses.get(i);

        /** @todo fall getNumberOfBookings() sem tekur inn � sig travelAddress....�tti a� vera au�velt... */

        //bookings = booker.getNumberOfBookings();
      }

    }

    return 0;
  }

}
