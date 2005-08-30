package is.idega.idegaweb.travel.service.hotel.business;

import is.idega.idegaweb.travel.business.ServiceNotFoundException;
import is.idega.idegaweb.travel.business.TimeframeNotFoundException;
import is.idega.idegaweb.travel.business.TravelStockroomBusinessBean;
import is.idega.idegaweb.travel.service.business.ProductCategoryFactoryBean;
import is.idega.idegaweb.travel.service.hotel.data.Hotel;
import is.idega.idegaweb.travel.service.hotel.data.HotelHome;
import is.idega.idegaweb.travel.service.presentation.BookingForm;
import java.rmi.RemoteException;
import java.sql.SQLException;
import java.util.Collection;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Vector;
import javax.ejb.FinderException;
import com.idega.block.trade.stockroom.data.Product;
import com.idega.block.trade.stockroom.data.ProductCategory;
import com.idega.block.trade.stockroom.data.ProductCategoryHome;
import com.idega.block.trade.stockroom.data.ProductHome;
import com.idega.block.trade.stockroom.data.Timeframe;
import com.idega.data.IDOLookup;
import com.idega.data.IDOLookupException;
import com.idega.presentation.IWContext;
import com.idega.util.IWTimestamp;
import com.idega.util.datastructures.HashtableDoubleKeyed;


/**
 * <p>Title: idega</p>
 * <p>Description: software</p>
 * <p>Copyright: Copyright (c) 2002</p>
 * <p>Company: idega software</p>
 * @author <a href="mailto:gimmi@idega.is">Grimur Jonsson</a>
 * @version 1.0
 */

public class HotelBusinessBean extends TravelStockroomBusinessBean implements HotelBusiness {

	private HashMap hotels = new HashMap();
	
  public HotelBusinessBean() {
  }

  public int createHotel(int supplierId, Integer fileId, String name, String number, String description, int numberOfUnits, int maxPerUnit, boolean isValid, int discountTypeId, int[] roomTypeIds, int[] hotelTypeIds, Float rating) throws Exception{
    return updateHotel(-1, supplierId, fileId, name, number, description, numberOfUnits, maxPerUnit, isValid, discountTypeId, roomTypeIds, hotelTypeIds, rating);
  }

  public int updateHotel(int serviceId, int supplierId, Integer fileId, String name, String number, String description, int numberOfUnits, int maxPerUnit, boolean isValid, int discountTypeId, int[] roomTypeIds, int[] hotelTypeIds, Float rating) throws Exception{
    int productId = -1;
    boolean isUpdate = (serviceId > -1);

    if (serviceId == -1) {
      productId = createService(supplierId, fileId, name, number, description, isValid, new int[]{}, null, null, discountTypeId);
    }else {
      productId = updateService(serviceId, supplierId, fileId, name, number, description, isValid, new int[]{}, null, null, discountTypeId);
    }

    Hotel hotel;
    HotelHome hHome = (HotelHome) IDOLookup.getHome(Hotel.class);
    try {
      /** update hotel */
      hotel = hHome.findByPrimaryKey(new Integer(productId));
      hotel.setNumberOfUnits(numberOfUnits);
      hotel.setMaxPerUnit( maxPerUnit );
	  	hotel.setRoomTypeIds(roomTypeIds);
      hotel.setHotelTypeIds(hotelTypeIds);
	  	if (rating != null) {
	  		hotel.setRating(rating.floatValue());
	  	} else {
	  		hotel.setRating(-1);
	  	}
      hotel.store();
    }catch (FinderException fe) {
      /** create hotel */
      hotel = hHome.create();
      hotel.setPrimaryKey(new Integer(productId));
      hotel.setNumberOfUnits(numberOfUnits);
      hotel.setMaxPerUnit( maxPerUnit );
      if (rating != null) {
	  			hotel.setRating(rating.floatValue());
	  		} else {
	  			hotel.setRating(-1);
	  		}
      hotel.store();
			hotel.setRoomTypeIds(roomTypeIds);
      hotel.setHotelTypeIds(hotelTypeIds);
    }

    if (!isUpdate) {
    	setActiveDaysAll(productId);
    }

    try {
      ProductCategoryHome pCatHome = (ProductCategoryHome) IDOLookup.getHomeLegacy(ProductCategory.class);
      ProductCategory pCat = pCatHome.getProductCategory(ProductCategoryFactoryBean.CATEGORY_TYPE_HOTEL);
      ProductHome pHome = (ProductHome) IDOLookup.getHome(Product.class);
      Product product = pHome.findByPrimaryKey(new Integer(productId));
      product.removeAllFrom(ProductCategory.class);
      pCat.addTo(Product.class, productId);
    }catch (SQLException sql) {}

    Product product = (( ProductHome ) IDOLookup.getHome(Product.class)).findByPrimaryKey(new Integer(productId));
    getProductBusiness().removeAllTravelAddresses(product);
//    product.removeAllFrom(TravelAddress.class);
//    product.removeAllFrom(Timeframe.class);

	hotels.remove(product.getPrimaryKey());

    return productId;
  }
  
  public void finalizeHotelCreation(Product product) throws FinderException, RemoteException{
    this.removeExtraPrices(product);
  }


	public boolean getIfDay(IWContext iwc, int serviceId, int dayOfWeek)throws RemoteException, RemoteException {
//		ServiceDayHome sdHome = (ServiceDayHome) IDOLookup.getHome(ServiceDay.class);
//		return sdHome.getIfDay(serviceId, dayOfWeek);
//		return true;
		return super.getIfDay(iwc, serviceId, dayOfWeek);
	}
/*
	public boolean getIfDay(IWContext iwc, Product product, Timeframe[] timeframes, IWTimestamp stamp) throws ServiceNotFoundException, TimeframeNotFoundException, RemoteException{
		return getIfDay(iwc, product, timeframes, stamp, false, true);		
	}*/

  public boolean getIfDay(IWContext iwc, Product product, Timeframe[] timeframes, IWTimestamp stamp, boolean includePast, boolean fixTimeframe) throws ServiceNotFoundException, TimeframeNotFoundException, RemoteException {
			
		if (timeframes == null || timeframes.length == 0) {
      boolean isDay = false;
      String key1 = Integer.toString(product.getID());
      String key2 = stamp.toSQLDateString();

      HashtableDoubleKeyed serviceDayHash = getServiceDayHashtable(iwc);
      Object obj = serviceDayHash.get(key1, key2);
      if (obj == null) {
      	boolean validDate = false;
	      if (!includePast) {
	        IWTimestamp now = IWTimestamp.RightNow();
	        IWTimestamp tNow = new IWTimestamp(now.getDay(), now.getMonth(), now.getYear());
	        if (!tNow.isLaterThan(stamp)) {
	        	validDate = true;
	      	}
	      }else {
	          validDate = true;
	      }
	      
	      if (validDate) {
	      	isDay = getIfDay(iwc, product.getID(), stamp.getDayOfWeek());
//	      	isDay = true;
	      }
	      serviceDayHash.put(key1, key2, new Boolean(isDay));
      }
      else {
        isDay = ((Boolean) obj).booleanValue();
      }
     return isDay;
	  }else {
	  	return super.getIfDay(iwc, product, timeframes, stamp, includePast, fixTimeframe);
	  }
	  
  }

	public List getDepartureDays(IWContext iwc,	Product product,	IWTimestamp fromStamp,	IWTimestamp toStamp,	boolean showPast)	throws FinderException, RemoteException, RemoteException {
    List returner = new Vector();
		IWTimestamp stamp = new IWTimestamp(fromStamp);	

		
		try {
					Timeframe[] timeframes = getProductBusiness().getTimeframes(product);
					while (toStamp.isLaterThanOrEquals( stamp)) {
						if (getIfDay(iwc, product, timeframes, stamp, false, true)) {
							returner.add(new IWTimestamp(stamp));
						}
						stamp.addDays(1);
					}
		} catch (Exception e) {
			e.printStackTrace(System.err);
		}
		
    return returner;
	}
	
	public Hotel getHotel(Object pk) throws IDOLookupException, FinderException {
		Hotel hotel = (Hotel) hotels.get(pk);
		if (hotel == null) {
			hotel = ((HotelHome) IDOLookup.getHome(Hotel.class)).findByPrimaryKey(pk);
			hotels.put(pk, hotel);
		}
		return hotel;
	}
	public void invalidateMaxDayCache(Collection products) throws RemoteException {
		super.invalidateMaxDayCache(products);
		if (products != null) {
			Iterator iter = products.iterator();
			while (iter.hasNext()) {
				maxBookings.remove(((Product) iter.next()).getPrimaryKey());
			}
		}
	}

	private HashMap maxBookings = new HashMap();
	public int getMaxBookings(Product product, IWTimestamp stamp) throws RemoteException, FinderException {
		Integer theReturner = (Integer) maxBookings.get(product.getPrimaryKey());
		if (theReturner == null) {
			Hotel hotel = getHotel(product.getPrimaryKey());
			theReturner = new Integer(hotel.getNumberOfUnits());
			maxBookings.put(product.getPrimaryKey(), theReturner);
		}
		
		if (theReturner.intValue() == BookingForm.UNLIMITED_AVAILABILITY) {
			return super.getMaxBookings(product, stamp);
		} else {
			return theReturner.intValue();
		}
		
	}

	public boolean supportsSupplyPool() {
		return true;
	}
	
}
