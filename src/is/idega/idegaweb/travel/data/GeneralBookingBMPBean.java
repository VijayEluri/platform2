package is.idega.idegaweb.travel.data;


import is.idega.idegaweb.travel.interfaces.Booking;
import java.rmi.RemoteException;
import java.sql.SQLException;
import java.sql.Timestamp;
import java.util.Collection;
import java.util.Iterator;
import java.util.List;
import java.util.Vector;
import javax.ejb.FinderException;
import com.idega.block.basket.data.BasketItem;
import com.idega.block.trade.stockroom.business.ProductBusiness;
import com.idega.block.trade.stockroom.data.Product;
import com.idega.block.trade.stockroom.data.ProductBMPBean;
import com.idega.block.trade.stockroom.data.Reseller;
import com.idega.block.trade.stockroom.data.ResellerHome;
import com.idega.block.trade.stockroom.data.Timeframe;
import com.idega.block.trade.stockroom.data.TravelAddress;
import com.idega.block.trade.stockroom.data.TravelAddressBMPBean;
import com.idega.business.IBOLookup;
import com.idega.core.location.data.Address;
import com.idega.data.EntityControl;
import com.idega.data.GenericEntity;
import com.idega.data.IDOAddRelationshipException;
import com.idega.data.IDOLookup;
import com.idega.data.IDOPrimaryKey;
import com.idega.data.IDOQuery;
import com.idega.data.IDORelationshipException;
import com.idega.data.IDORemoveRelationshipException;
import com.idega.data.IDOStoreException;
import com.idega.data.PrimaryKey;
import com.idega.data.SimpleQuerier;
import com.idega.data.query.AND;
import com.idega.data.query.Column;
import com.idega.data.query.Criteria;
import com.idega.data.query.InCriteria;
import com.idega.data.query.MatchCriteria;
import com.idega.data.query.OR;
import com.idega.data.query.Order;
import com.idega.data.query.SelectQuery;
import com.idega.data.query.SumColumn;
import com.idega.data.query.Table;
import com.idega.data.query.WildCardColumn;
import com.idega.presentation.IWContext;
import com.idega.util.CypherText;
import com.idega.util.IWTimestamp;
//import com.idega.util.text.TextSoap;

/**
 * Title:        IW Travel
 * Description:
 * Copyright:    Copyright (c) 2001
 * Company:      idega.is
 * @author 2000 - idega team - <br><a href="mailto:gummi@idega.is">Gu�mundur �g�st S�mundsson</a><br><a href="mailto:gimmi@idega.is">Gr�mur J�nsson</a>
 * @version 1.0
 */


public class GeneralBookingBMPBean extends GenericEntity implements Booking , GeneralBooking, BasketItem{

  public GeneralBookingBMPBean(){
          super();
  }

  public GeneralBookingBMPBean(int id)throws SQLException{
          super(id);
  }
 
  //- basketitem implementation ------------
  
  public IDOPrimaryKey ejbHomeGetPrimaryKey(Integer primaryKeyValue) {
      PrimaryKey key = new PrimaryKey();
      key.setPrimaryKeyValue(getIDColumnName(), primaryKeyValue);

      return key;
  }
  
  public IDOPrimaryKey getItemID() { 
      PrimaryKey key = new PrimaryKey();
      key.setPrimaryKeyValue(getIDColumnName(), getPrimaryKeyValue());

      return key;

//      return getPrimaryKey();
//	  String idString = Integer.toString(getServiceID());
//	  if (getBookingDate() != null) {
//		  idString += getBookingDate().toString();
//	  }
//	  return idString; 
  }
  
  public String getItemName() { return null; }
  public String getItemDescription() { return null; }
  public Double getItemPrice() { return (Double)null; }
  
  //----------------------------------------

  public void initializeAttributes(){
    addAttribute(getIDColumnName());
    addAttribute(getNameColumnName(), "Name", true, true, String.class, 255);
    addAttribute(getTelephoneNumberColumnName(), "S�man�mer", true, true, String.class, 255);
    addAttribute(getEmailColumnName(), "T�lvup�stur", true, true, String.class, 255);
    addAttribute(getCityColumnName(), "Borg", true, true, String.class, 255);
    addAttribute(getAddressColumnName(), "Heimilisfang", true, true, String.class, 255);
    addAttribute(getBookingDateColumnName(), "Dagsetning", true, true, java.sql.Timestamp.class);
    addAttribute(getTotalCountColumnName(), "Fj�ldi", true, true, Integer.class);
    addAttribute(getBookingTypeIDColumnName(), "Ger� bokunar", true, true, Integer.class);
    addAttribute(getServiceIDColumnName(), "Vara", true, true, Integer.class, "many-to-one", Service.class);
    addAttribute(getCountryColumnName(), "Land", true, true, String.class);
    addAttribute(getDateOfBookingColumnName(), "Hven�r b�kun � s�r sta�", true, true, java.sql.Timestamp.class);
    addAttribute(getPostalCodeColumnName(), "P�stn�mer", true, true, String.class);
    addAttribute(getAttendanceColumnName(), "M�ting", true, true, Integer.class);
    addAttribute(getPaymentTypeIdColumnName(), "Ger� grei�slu", true, true, Integer.class);
    addAttribute(getIsValidColumnName(), "valid", true, true, Boolean.class);
    addAttribute(getReferenceNumberColumnName(), "reference number", true, true, String.class);
    addAttribute(getOwnerIdColumnName(), "owner id", true, true, Integer.class);
    addAttribute(getUserIdColumnName(), "user id", true, true, Integer.class);
    addAttribute(getCommentColumnName(), "comment", true, true, String.class);
    addAttribute(getCreditcardAuthorizationNumberColumnName(), "cc auth", true, true, String.class);
    addAttribute(getPickupPlaceIDColumnName(),"Rick-up sta�ur",true,true,Integer.class,"many_to_one",PickupPlace.class);
    addAttribute(getPickupExtraInfoColumnName(), "Pickup extra info", true, true, String.class);
    addAttribute(getRefererUrlColumnName(), "Referer url", true, true, String.class, 2000);
    addAttribute(getBookingCodeColumnName(), "Booking code", true, true, String.class);

    this.addManyToManyRelationShip(Reseller.class);
    this.addManyToManyRelationShip(Address.class);
    this.addManyToManyRelationShip(TravelAddress.class);
    addIndex(getBookingTypeIDColumnName());
    addIndex(getServiceIDColumnName());
    addIndex("IDX_BOOK_TYPE_SERV", new String[]{getIDColumnName(), getBookingTypeIDColumnName(), getServiceIDColumnName()});
    addIndex("IDX_BOOK_SERV", new String[]{getIDColumnName(), getServiceIDColumnName()});
  }


  public void setDefaultValues() {
      this.setIsValid(true);
      this.setAttendance(-1000);
      this.setPaymentTypeId(Booking.PAYMENT_TYPE_ID_NO_PAYMENT);
      //this.setDiscountTypeId(Booking.DISCOUNT_TYPE_ID_PERCENT);
  }


  public String getEntityName(){
    return getBookingTableName();
  }

  public String getName(){
    return getStringColumnValue(getNameColumnName());
  }

  public void setName(String name){
    setColumn(getNameColumnName(),name);
  }

  public Timestamp getBookingDate() {
    return (Timestamp) getColumnValue(getBookingDateColumnName());
  }

  public void setBookingDate(Timestamp timestamp) {
    setColumn(getBookingDateColumnName(),timestamp);
  }

  public Service getService() {
    return (Service) getColumnValue(getServiceIDColumnName());
  }

  public int getServiceID() {
    return getIntColumnValue(getServiceIDColumnName());
  }

  public void setServiceID(int id) {
    setColumn(getServiceIDColumnName(), id);
  }

  public void setCountry(String country) {
    setColumn(getCountryColumnName(), country);
  }

  public String getTelephoneNumber() {
    return getStringColumnValue(getTelephoneNumberColumnName());
  }

  public void setTelephoneNumber(String number) {
    setColumn(getTelephoneNumberColumnName(), number);
  }

  public String getEmail() {
    return getStringColumnValue(getEmailColumnName());
  }

  public void setEmail(String email) {
    setColumn(getEmailColumnName(),email);
  }

  public String getCity() {
    return getStringColumnValue(getCityColumnName());
  }

  public void setCity(String city) {
    setColumn(getCityColumnName(),city);
  }

  public String getAddress() {
    return getStringColumnValue(getAddressColumnName());
  }

  public void setAddress(String address) {
    setColumn(getAddressColumnName(),address);
  }

  public int getTotalCount() {
    return getIntColumnValue(getTotalCountColumnName());
  }

  public void setTotalCount(int totalCount) {
    setColumn(getTotalCountColumnName(),totalCount);
  }

  public int getBookingTypeID() {
    return getIntColumnValue(getBookingTypeIDColumnName());
  }

  public void setBookingTypeID(int id) {
    setColumn(getBookingTypeIDColumnName(),id);
  }

  public Timestamp getDateOfBooking() {
    return (Timestamp) getColumnValue(getDateOfBookingColumnName());
  }

  public void setDateOfBooking(Timestamp dateOfBooking) {
    setColumn(getDateOfBookingColumnName(), dateOfBooking);
  }

  public String getPostalCode() {
    return getStringColumnValue(getPostalCodeColumnName());
  }

  public void setPostalCode(String code) {
    setColumn(getPostalCodeColumnName(), code);
  }

  public String getCountry() {
    return getStringColumnValue(getCountryColumnName());
  }

  public void setAttendance(int attendance) {
    setColumn(getAttendanceColumnName(), attendance);
  }

  public int getAttendance() {
    return getIntColumnValue(getAttendanceColumnName());
  }

  public void setPaymentTypeId(int id) {
    setColumn(getPaymentTypeIdColumnName(), id);
  }

  public int getPaymentTypeId() {
    return getIntColumnValue(getPaymentTypeIdColumnName());
  }

  public boolean getIsValid() {
    return getBooleanColumnValue(getIsValidColumnName());
  }

  public void setIsValid(boolean isValid) {
    setColumn(getIsValidColumnName(), isValid);
  }

  public String getCode() {
  	return getStringColumnValue(getBookingCodeColumnName());
  }
  
  public void setCode(String code) {
  	setColumn(getBookingCodeColumnName(), code);
  }

  public BookingEntry[] getBookingEntries() throws FinderException , RemoteException{
		BookingEntryHome beHome = (BookingEntryHome) IDOLookup.getHome(BookingEntry.class);
		BookingEntry bEntry;
		Collection coll = beHome.getEntries(this);
		if ( coll != null && !coll.isEmpty()) {
			Iterator iter = coll.iterator();
			BookingEntry[] entries = new BookingEntry[coll.size()];
			for (int i = 0; i < entries.length; i++) {
				bEntry = beHome.findByPrimaryKey(iter.next());
				entries[i] = bEntry;
			}
			return entries;
		} else {
			return new BookingEntry[]{};
		}
  }

  public void setReferenceNumber(String number) {
    setColumn(getReferenceNumberColumnName(), number);
  }

  public String getReferenceNumber() {
  	return getStringColumnValue(getReferenceNumberColumnName());
  }

  public int getUserId() {
    return getIntColumnValue(getUserIdColumnName());
  }

  public void setUserId(int userId) {
    setColumn(getUserIdColumnName(), userId);
  }

  public int getOwnerId() {
    return getIntColumnValue(getOwnerIdColumnName());
  }

  public void setOwnerId(int ownerId) {
    setColumn(getOwnerIdColumnName(), ownerId);
  }

  public String getComment() {
    return getStringColumnValue(getCommentColumnName());
  }

  public void setComment(String comment) {
    setColumn(getCommentColumnName(), comment);
  }

  public String getCreditcardAuthorizationNumber() {
    return getStringColumnValue(getCreditcardAuthorizationNumberColumnName());
  }

  public void setCreditcardAuthorizationNumber(String number) {
    setColumn(getCreditcardAuthorizationNumberColumnName(), number);
  }

	public int getPickupPlaceID() {
		return getIntColumnValue(getPickupPlaceIDColumnName());	
	}
	
	public PickupPlace getPickupPlace() {
    return (PickupPlace) getColumnValue(getPickupPlaceIDColumnName());
	}

	public void setPickupPlace(PickupPlace pPlace) throws RemoteException{
		setPickupPlaceId(((Integer) pPlace.getPrimaryKey()).intValue());
	}
	
	public void setPickupPlaceId(int pickupPlaceId) {
		setColumn(getPickupPlaceIDColumnName(), pickupPlaceId);
	}
	
	public String getPickupExtraInfo() {
		return getStringColumnValue(getPickupExtraInfoColumnName());	
	}
	
	public void setPickupExtraInfo(String info) {
		setColumn(getPickupExtraInfoColumnName(), info);	
	}

	public String getRefererUrl() {
		return getStringColumnValue(getRefererUrlColumnName());
	}
	
	public void setRefererUrl(String url) {
		setColumn(getRefererUrlColumnName(), url);
	}

  public void store() {
  	
  		String refNum = getReferenceNumber();
  		if (refNum == null) {
		    CypherText cyph = new CypherText();
		    refNum = cyph.getKey(8);
		
		    try {
		      Collection bookingIds = this.idoFindAllIDsByColumnBySQL(getReferenceNumberColumnName(), refNum);
		
		      while (bookingIds.size() > 0) {
		      	refNum = cyph.getKey(8);
		        bookingIds = this.idoFindAllIDsByColumnBySQL(getReferenceNumberColumnName(), refNum);
		      }
		      //System.out.println("RefNumber generated = "+refNum);
		    }catch (FinderException fe) {
		    	throw new IDOStoreException(fe.getMessage());
		    }
  		}
  		else {
    		//System.out.println("RefNumber set to = "+refNum);
  		}

	  setReferenceNumber(refNum);
  		super.store();
  }

  public static String getBookingTableName(){return "TB_BOOKING";}
  public static String getNameColumnName() {return "NAME";}
  public static String getTelephoneNumberColumnName() {return "TELEPHONE_NUMBER";}
  public static String getEmailColumnName() {return "EMAIL";}
  public static String getCityColumnName() {return "CITY";}
  public static String getAddressColumnName() {return "ADDRESS";}
  public static String getBookingDateColumnName() {return "BOOKING_DATE";}
  public static String getBookingTypeIDColumnName() {return "BOOKING_TYPE_ID";}
  public static String getTotalCountColumnName() {return "TOTAL_COUNT";}
  public static String getServiceIDColumnName() {return "TB_SERVICE_ID";}
  public static String getCountryColumnName() {return "COUNTRY";}
  public static String getDateOfBookingColumnName() {return "DATE_OF_BOOKING";}
  public static String getPostalCodeColumnName() {return "POSTAL_CODE";}
  public static String getAttendanceColumnName() {return "ATTENDANCE";}
  public static String getPaymentTypeIdColumnName() {return "PAYMENT_TYPE";}
  public static String getIsValidColumnName() {return "IS_VALID";}
  public static String getReferenceNumberColumnName() {return "REFERENCE_NUMBER";}
  public static String getOwnerIdColumnName() {return "OWNER_ID";}
  public static String getUserIdColumnName() {return "IC_USER_ID";}
  public static String getCommentColumnName() {return "BK_COMMENT";}
  public static String getCreditcardAuthorizationNumberColumnName() {return "CC_AUTH_NUMBER";}
	public static String getPickupPlaceIDColumnName() {return "PICKUP_PLACE_ID";}
	public static String getPickupExtraInfoColumnName() {return "PICKUP_EXTRA_INFO";}
	public static String getRefererUrlColumnName() {return "REFERER_URL";} 
	public static String getBookingCodeColumnName() {return "BOOKING_CODE";}


  public  Collection ejbFindBookings(int resellerId, int serviceId, IWTimestamp stamp, TravelAddress travelAddress) throws FinderException{
    return ejbFindBookings(new int[] {resellerId}, serviceId, stamp, travelAddress);
  }

  public  Collection ejbFindBookings(int[] resellerIds, int serviceId, IWTimestamp stamp, TravelAddress travelAddress) throws FinderException{
  	return ejbFindBookings(resellerIds, serviceId, stamp, null, travelAddress);
  }
  public  Collection ejbFindBookings(int[] resellerIds, int serviceId, IWTimestamp stamp, String code, TravelAddress travelAddress) throws FinderException{
    Collection returner = null;

    if (resellerIds == null) {
      resellerIds = new int[0];
    }
    Reseller reseller = (Reseller) (com.idega.block.trade.stockroom.data.ResellerBMPBean.getStaticInstance(Reseller.class));
    String addressMiddleTable = EntityControl.getManyToManyRelationShipTableName(GeneralBooking.class, TravelAddress.class);

    String[] many = {};
      StringBuffer sql = new StringBuffer();
        sql.append("Select b.* from "+getBookingTableName()+" b, "+EntityControl.getManyToManyRelationShipTableName(GeneralBooking.class,Reseller.class)+" br");

        if (travelAddress != null) {
          sql.append(", "+addressMiddleTable+" am");
        }

        sql.append(" where ");
        if (travelAddress != null) {
          sql.append("am."+getIDColumnName()+" = b."+getIDColumnName());
          sql.append(" and ");
          sql.append("am."+TravelAddressBMPBean.getTravelAddressTableName()+"_ID in (");
          sql.append(travelAddress.getPrimaryKey().toString());
          sql.append(") and ");
        }
        
        //sql.append(" where ");
        if (resellerIds.length > 0 ) {
          sql.append(" br."+reseller.getIDColumnName()+" in (");
          for (int i = 0; i < resellerIds.length; i++) {
            if (i != 0) sql.append(", ");
            sql.append(resellerIds[i]);
          }

          sql.append(") and ");
        }
        sql.append(" b."+getIDColumnName()+" = br."+this.getIDColumnName());
        sql.append(" and ");
        sql.append(" b."+getIsValidColumnName()+"='Y'");
        sql.append(" and ");
        sql.append(" b."+getServiceIDColumnName()+"="+serviceId);
        sql.append(" and ");
//				sql.append(" b."+getBookingDateColumnName()+" like '%"+TextSoap.findAndCut(stamp.toSQLDateString(),"-")+"%'");
        sql.append(" b."+getBookingDateColumnName()+" like '%"+stamp.toSQLDateString()+"%'");
        if (code != null) {
        	sql.append(" and ");
        	sql.append(" b."+getBookingCodeColumnName()+"= '"+code+"'");
        }

    returner = this.idoFindPKsBySQL(sql.toString());
	//System.out.println("FindBookings (1) SQL \n"+sql.toString());

    return returner;
  }

  public int ejbHomeGetBookingsTotalCount(int[] resellerIds, int serviceId, IWTimestamp stamp) {
    return ejbHomeGetBookingsTotalCount(resellerIds, serviceId, stamp, null);
  }

  public int ejbHomeGetNumberOfBookings(int[] resellerIds, int serviceId, IWTimestamp stamp, Collection travelAddressIds) {
    return ejbHomeGetBookingsTotalCount(resellerIds, serviceId, stamp, travelAddressIds, true, null);
  }

  public int ejbHomeGetBookingsTotalCount(int[] resellerIds, int serviceId, IWTimestamp stamp, Collection travelAddressIds) {
    return ejbHomeGetBookingsTotalCount(resellerIds, serviceId, stamp, travelAddressIds, true, null);
  }

  public int ejbHomeGetBookingsTotalCount(int[] resellerIds, int serviceId, IWTimestamp stamp, Collection travelAddressIds, boolean returnsTotalCountInsteadOfNumberOfBookings, String code) {
    int returner = 0;
    try {
        if (resellerIds == null) {
          resellerIds = new int[0];
        }
        Reseller reseller = (Reseller) (com.idega.block.trade.stockroom.data.ResellerBMPBean.getStaticInstance(Reseller.class));
        String addressMiddleTable = EntityControl.getManyToManyRelationShipTableName(GeneralBooking.class, TravelAddress.class);
        String returning = "sum(b."+getTotalCountColumnName()+")";
        if (!returnsTotalCountInsteadOfNumberOfBookings) {
        	returning = "count(*)";
        }

        String[] many = {};
          StringBuffer sql = new StringBuffer();
            sql.append("Select "+returning+" from "+getBookingTableName()+" b, "+EntityControl.getManyToManyRelationShipTableName(GeneralBooking.class,Reseller.class)+" br");
            if (travelAddressIds != null) {
              sql.append(", "+addressMiddleTable+" am");
            }

            sql.append(" where ");
            if (travelAddressIds != null) {
              sql.append("am."+getIDColumnName()+" = b."+getIDColumnName());
              sql.append(" and ");
              sql.append("am."+TravelAddressBMPBean.getTravelAddressTableName()+"_ID in (");
              Iterator iter = travelAddressIds.iterator();
              while (iter.hasNext()) {
                Object item = iter.next();
                sql.append(item.toString());
                if (iter.hasNext()) {
                  sql.append(", ");
                }
              }
              sql.append(") and ");
            }

//            sql.append(" where ");
            if (resellerIds.length > 0 ) {
              sql.append(" br."+reseller.getIDColumnName()+" in (");
              for (int i = 0; i < resellerIds.length; i++) {
                if (i != 0) sql.append(", ");
                sql.append(resellerIds[i]);
              }

              sql.append(") and ");
            }
            sql.append(" b."+getIDColumnName()+" = br."+getIDColumnName());
            sql.append(" and ");
            sql.append(" b."+getIsValidColumnName()+"='Y'");
            sql.append(" and ");
            sql.append(" b."+getServiceIDColumnName()+"="+serviceId);
            sql.append(" and ");
//						sql.append(" b."+getBookingDateColumnName()+" like '%"+TextSoap.findAndCut(stamp.toSQLDateString(),"-")+"%'");
            sql.append(" b."+getBookingDateColumnName()+" like '%"+stamp.toSQLDateString()+"%'");
						if (code != null) {
							sql.append(" and ");
							sql.append(" b."+getBookingCodeColumnName()+"= '"+code+"'");
						}            
        many = SimpleQuerier.executeStringQuery(sql.toString());
		//System.out.println("GetBookingTotalCount (1) SQL \n"+sql.toString());

        if (many != null && many.length > 0) {
          if (many[0] != null)
            returner = Integer.parseInt(many[0]);
        }


    }catch (Exception e) {
        e.printStackTrace(System.err);
    }

    return returner;
  }

  public int ejbHomeGetNumberOfBookings(int serviceId, IWTimestamp fromStamp, IWTimestamp toStamp, int bookingType){
  	Vector ids = new Vector();
  	ids.add(new Integer(serviceId));
		return ejbHomeGetBookingsTotalCount(ids, fromStamp, toStamp, bookingType, new int[]{}, null, false, false, null );
  }

  public int ejbHomeGetBookingsTotalCount(int serviceId, IWTimestamp fromStamp, IWTimestamp toStamp, int bookingType, int[] productPriceIds){
    return ejbHomeGetBookingsTotalCount(serviceId, fromStamp, toStamp, bookingType, productPriceIds, null);
  }

  public int ejbHomeGetBookingsTotalCount(int serviceId, IWTimestamp fromStamp, IWTimestamp toStamp, int bookingType, int[] productPriceIds, Collection travelAddressIds){
    return ejbHomeGetBookingsTotalCount(serviceId, fromStamp, toStamp, bookingType, productPriceIds, travelAddressIds, false);
  }

  public int ejbHomeGetBookingsTotalCountByDateOfBooking(int serviceId, IWTimestamp fromStamp, IWTimestamp toStamp, int bookingType, int[] productPriceIds, Collection travelAddressIds){
    return ejbHomeGetBookingsTotalCount(serviceId, fromStamp, toStamp, bookingType, productPriceIds, travelAddressIds, true);
  }

  public int ejbHomeGetBookingsTotalCount(int serviceId, IWTimestamp fromStamp, IWTimestamp toStamp, int bookingType, int[] productPriceIds, Collection travelAddressIds, boolean useDateOfBookingColumn){
  	Vector ids = new Vector();
  	ids.add(new Integer(serviceId));
		return ejbHomeGetBookingsTotalCount(ids, fromStamp, toStamp, bookingType, productPriceIds, travelAddressIds, useDateOfBookingColumn, true , null);
  }
  
  public int ejbHomeGetBookingsTotalCount(Collection serviceIds, IWTimestamp fromStamp, IWTimestamp toStamp, int bookingType, int[] productPriceIds, Collection travelAddressIds, boolean useDateOfBookingColumn, boolean returnTotalCountInsteadOfBookingCount, String code){
    int returner = 0;
    StringBuffer sql = new StringBuffer();
    Table bookingTable = new Table(this, "b");
    SelectQuery query = new SelectQuery(bookingTable);
    try {

      // @todo lonsa vi� getInstance crap /

      ProductBusiness pBus = (ProductBusiness) IBOLookup.getServiceInstance(IWContext.getInstance(), ProductBusiness.class);
      String middleTable = EntityControl.getManyToManyRelationShipTableName(Product.class, Timeframe.class);
      String addressMiddleTableName = EntityControl.getManyToManyRelationShipTableName(GeneralBooking.class, TravelAddress.class);

      String pTable = com.idega.block.trade.stockroom.data.ProductBMPBean.getProductEntityName();
      String tTable = com.idega.block.trade.stockroom.data.TimeframeBMPBean.getTimeframeTableName();

      Table productTable = new Table(Product.class, "p");
      Table timeframeTable = new Table(Timeframe.class, "t");
      Table addressMiddleTable = new Table(addressMiddleTableName, "am");
      Table timeframeMiddleTable = new Table(middleTable, "m");
      
      Column productPK = new Column(productTable, ProductBMPBean.getIdColumnName());

      String dateCol = this.getBookingDateColumnName();
      String returning = "sum(b."+getTotalCountColumnName()+") ";
      
      if (useDateOfBookingColumn) {
        dateCol = this.getDateOfBookingColumnName();
      }
      
      Column dateColumn = new Column(bookingTable, dateCol);
      
      if (!returnTotalCountInsteadOfBookingCount) {
      	returning = "count(*)"	;
      	query.setAsCountQuery(true);
      	query.addColumn(new WildCardColumn());
      } else {
      	query.addColumn(new SumColumn(bookingTable, getTotalCountColumnName()));
      }

//      query.addJoin(bookingTable, getServiceIDColumnName(), productTable, ProductBMPBean.getIdColumnName());
      

        String[] many = {};
            sql.append("Select "+returning+" from "+getBookingTableName()+" b , "+pTable+" p ");
//            if (timeframe != null) {
//              sql.append(", "+middleTable+" m , "+tTable+" t ");
//            }
            if (travelAddressIds != null) {
              sql.append(", "+addressMiddleTable+" am ");
            }

            sql.append(" where ");
            if (travelAddressIds != null) {
          		query.addJoin(bookingTable, getIDColumnName(), addressMiddleTable, getIDColumnName());
          		InCriteria inCrit = new InCriteria(new Column(addressMiddleTable, TravelAddressBMPBean.getTravelAddressTableName()+"_ID"), travelAddressIds);
          		query.addCriteria(inCrit);

          		sql.append("am."+getIDColumnName()+" = b."+getIDColumnName());
              sql.append(" and ");
              sql.append("am."+TravelAddressBMPBean.getTravelAddressTableName()+"_ID in (");
              Iterator iter = travelAddressIds.iterator();
              while (iter.hasNext()) {
                Object item = iter.next();
                sql.append(item.toString());
                if (iter.hasNext()) {
                  sql.append(", ");
                }
              }
              sql.append(") and ");
            }

            boolean addTimeframe = false;
            sql.append(" (");
            
            List list = new Vector();
            Iterator serviter = serviceIds.iterator();
            int id = -1;
            int count = 0;
            while (serviter.hasNext()) {
            //for (int i = 0; i < serviceIds.length; i++) {
            	++count;
            	id = ((Integer) serviter.next()).intValue();
              Timeframe timeframe = pBus.getTimeframe(pBus.getProduct(id), fromStamp);
            	MatchCriteria serID = new MatchCriteria(productPK, MatchCriteria.EQUALS, id);
	            if (timeframe != null) {
	          		query.addJoin(productTable, ProductBMPBean.getIdColumnName(), timeframeMiddleTable, ProductBMPBean.getIdColumnName());
	          		query.addJoin(timeframeMiddleTable, timeframe.getIDColumnName(), timeframeTable, timeframe.getIDColumnName());

	          		if (count > 1) {
	            		sql.append(" OR ");
	            	}
	            	sql.append("(");
	            	
	            	
	            	sql.append("p."+ProductBMPBean.getIdColumnName()+" = m."+ProductBMPBean.getIdColumnName());
	              sql.append(" and ");
	              sql.append("m."+timeframe.getIDColumnName()+" = t."+timeframe.getIDColumnName());
	              sql.append(" and ");
	              sql.append("t."+timeframe.getIDColumnName()+" = "+timeframe.getID());
	              sql.append(" and ");
	              
	              MatchCriteria timeID = new MatchCriteria(new Column(timeframeTable, timeframe.getIDColumnName()), MatchCriteria.EQUALS, timeframe.getID());
	              AND crit = new AND(serID, timeID);
	              list.add(crit);
	            } else {
	            	list.add(serID);
	            }
	            sql.append("p."+ProductBMPBean.getIdColumnName()+"="+id);
	            sql.append(") ");
	            
            }

            int listSize = list.size();
            if (listSize == 1) {
            	query.addCriteria((Criteria) list.get(0));
            } else if (listSize > 1) {
            	Iterator iter = list.iterator();
            	Criteria previous = (Criteria) iter.next();
            	Criteria criteria;
              OR orCrit;
            	while (iter.hasNext()) {
            		criteria = (Criteria) iter.next();
	        			orCrit = new OR(previous, criteria);
	        			previous = orCrit;
            	}
            	query.addCriteria(previous);
            }

            sql.append(") and ");

          	query.addCriteria(new MatchCriteria(new Column(bookingTable, getServiceIDColumnName()), MatchCriteria.EQUALS, new Column(productTable, ProductBMPBean.getIdColumnName())));
          	query.addCriteria(new MatchCriteria(new Column(bookingTable, getIsValidColumnName()), MatchCriteria.EQUALS, true));
            
            sql.append("b."+getServiceIDColumnName()+"= p."+ProductBMPBean.getIdColumnName());
            sql.append(" and ");
            sql.append("b."+getIsValidColumnName()+" = 'Y'");
            
            if (bookingType != -1) {
            	query.addCriteria(new MatchCriteria(new Column(bookingTable, getBookingTypeIDColumnName()), MatchCriteria.EQUALS, bookingType));
              sql.append(" and ");
              sql.append(getBookingTypeIDColumnName()+" = "+bookingType);
            }
            sql.append(" and (");
            if ( (fromStamp != null) && (toStamp == null) ) {
//              sql.append(dateColumn+" like '"+TextSoap.findAndCut(fromStamp.toSQLDateString(),"-")+"%'");
              sql.append(dateCol+" like '%"+fromStamp.toSQLDateString()+"%'");
              
              query.addCriteria(new MatchCriteria(dateColumn, MatchCriteria.LIKE, "%"+fromStamp.toSQLDateString()+"%"));
            }else if ( (fromStamp != null) && (toStamp != null)) {
              IWTimestamp tmpTo = new IWTimestamp(toStamp);
              tmpTo.addDays(1);

              MatchCriteria crit1 = new MatchCriteria(dateColumn, MatchCriteria.GREATEREQUAL, fromStamp.toSQLDateString()+" 00:00:00");
            	MatchCriteria crit2 = new MatchCriteria(dateColumn, MatchCriteria.LESS, tmpTo.toSQLDateString()+" 00:00:00");
            	AND andCriteria = new AND(crit1, crit2);
            	query.addCriteria(andCriteria);
              sql.append(" (");
//              sql.append(dateColumn+" >= '"+TextSoap.findAndCut(fromStamp.toSQLDateString(),"-")+"'");
              sql.append(dateCol+" >= '"+fromStamp.toSQLDateString()+"'");
              sql.append(" and ");
//              sql.append(dateColumn+" <= '"+TextSoap.findAndCut(toStamp.toSQLDateString(),"-")+"'");
              sql.append(dateCol+" < '"+tmpTo.toSQLDateString()+" 00:00:00'"); // Gimmi fixar ... +"')");
	            sql.append(" )");
            }
            sql.append(" )");
						if (code != null) {
							query.addCriteria(new MatchCriteria(new Column(bookingTable, getBookingCodeColumnName()), MatchCriteria.EQUALS, code));
							sql.append(" and ");
							sql.append(" b."+getBookingCodeColumnName()+"= '"+code+"'");
						}

            //System.out.println(sql.toString());
//            System.out.println(query.toString(false));
        return (int) idoGetValueFromSingleValueResultSet(query.toString());

//        many = SimpleQuerier.executeStringQuery(query.toString());
//        many = SimpleQuerier.executeStringQuery(sql.toString());
//        many = SimpleQuerier.executeStringQuery(sql.toString(),conn);
//		System.out.println("GetBookingsTotalCount SQL \n"+sql.toString());

//	      if (returnTotalCountInsteadOfBookingCount) {
//	        for (int i = 0; i < many.length; i++) {
//	          returner += Integer.parseInt(many[i]);
//	        }
//	      }else 
//	      	if (many != null && many.length > 0 && many[0] != null){
//	      		returner += Integer.parseInt( many[0]);	
//	      }

    }catch (Exception e) {
        System.err.println(query.toString());
        e.printStackTrace(System.err);
    }finally {
      //ConnectionBroker.freeConnection(conn);
    }

    return returner;
  }

  public Collection ejbFindBookings(int[] serviceIds, IWTimestamp fromStamp, IWTimestamp toStamp,int[] bookingTypeIds, String columnName, String columnValue, TravelAddress address, String code, boolean validOnly) throws FinderException, RemoteException{
    return ejbFindBookings(serviceIds, fromStamp, toStamp, bookingTypeIds, columnName, columnValue, address,getBookingDateColumnName() , code, validOnly);
  }

  public Collection ejbFindBookingsByDateOfBooking(int[] serviceIds, IWTimestamp fromStamp, IWTimestamp toStamp,int[] bookingTypeIds, String columnName, String columnValue, TravelAddress address, String code, boolean validOnly) throws FinderException, RemoteException{
    return ejbFindBookings(serviceIds, fromStamp, toStamp, bookingTypeIds, columnName, columnValue, address,this.getDateOfBookingColumnName() , code, validOnly);
  }

  public Collection ejbFindBookings(int[] serviceIds, IWTimestamp fromStamp, IWTimestamp toStamp,int[] bookingTypeIds, String columnName, String columnValue, TravelAddress address, String dateColumn, String code) throws FinderException, RemoteException{
  		return ejbFindBookings(serviceIds, fromStamp, toStamp, bookingTypeIds, columnName, columnValue, address, dateColumn, code, true);
  }
  	
  	public Collection ejbFindBookings(int[] serviceIds, IWTimestamp fromStamp, IWTimestamp toStamp,int[] bookingTypeIds, String columnName, String columnValue, TravelAddress address, String dateColumn, String code, boolean validOnly) throws FinderException, RemoteException{
    Collection returner = null;

    if (serviceIds.length == 0) {
      return new Vector();
    }
    StringBuffer sql = new StringBuffer();


    String addressMiddleTable = "";
    boolean useAddress = false;
    if (address != null) {
      addressMiddleTable = EntityControl.getManyToManyRelationShipTableName(GeneralBookingBMPBean.class, TravelAddress.class);
      useAddress = true;
    }

    sql.append("Select * from "+getBookingTableName()+" b");
    if (useAddress) {
      sql.append(" , "+addressMiddleTable+" am");
    }
    sql.append(" where ");
    
    if (serviceIds.length > 0 ) {
    	sql.append("b."+getServiceIDColumnName()+" in (");
	    for (int i = 0; i < serviceIds.length; i++) {
	      if (i > 0) sql.append(", ");
	      sql.append(serviceIds[i]);
	    }
	    sql.append(") and ");
		}

    if (useAddress) {
      sql.append("am."+TravelAddressBMPBean.getTravelAddressTableName()+"_id = "+address.getPrimaryKey().toString());
      sql.append(" and ");
      sql.append("b."+this.getIDColumnName() +" = am."+this.getIDColumnName());
      sql.append(" and ");
    }
    
    if (validOnly) {
    		sql.append("b."+getIsValidColumnName()+" = 'Y'");
    } else {
  			sql.append("b."+getIsValidColumnName()+" is not null");
    }
    /*else {
  			sql.append("b."+getIsValidColumnName()+" = 'N'");
    }*/
    if (fromStamp != null && toStamp == null) {
      sql.append(" and ");
//      sql.append("b."+dateColumn+" like '"+TextSoap.findAndCut(fromStamp.toSQLDateString(),"-")+"%'");
      sql.append("b."+dateColumn+" like '%"+fromStamp.toSQLString()+"%'");
    }else if (fromStamp != null && toStamp != null) {
      sql.append(" and ");
//      sql.append("b."+dateColumn+" >= '"+TextSoap.findAndCut(fromStamp.toSQLDateString(),"-")+"'");
      sql.append("b."+dateColumn+" >= '"+fromStamp.toSQLString()+"'");
      sql.append(" and ");
//      sql.append("b."+dateColumn+" <= '"+TextSoap.findAndCut(toStamp.toSQLDateString(),"-")+"'");
      sql.append("b."+dateColumn+" <= '"+toStamp.toSQLString()+"'");
    }
    if (bookingTypeIds != null) {
      if (bookingTypeIds.length > 0 ) {
        sql.append(" and (");
        for (int i = 0; i < bookingTypeIds.length; i++) {
          if (bookingTypeIds[i] != -1) {
            if (i > 0) sql.append(" OR ");
            sql.append("b."+getBookingTypeIDColumnName()+" = "+bookingTypeIds[i]);
          }
        }
        sql.append(") ");
      }
    }
    if (columnName != null && columnValue != null) {
      sql.append(" and ").append("b."+columnName).append(" = '").append(columnValue).append("'");
    }

    if (code != null) {
    	sql.append(" and ");
    	sql.append(" b."+getBookingCodeColumnName()+"= '"+code+"'");
		}
		if (dateColumn != null)  {
      sql.append(" order by "+dateColumn);
    }

		//System.out.println(sql.toString());
		returner = this.idoFindPKsBySQL(sql.toString());
    //System.out.println("FindBookings SQL \n"+sql.toString());
    //returner = (GeneralBooking[]) (((is.idega.idegaweb.travel.data.GeneralBookingHome)com.idega.data.IDOLookup.getHomeLegacy(GeneralBooking.class)).createLegacy()).findAll(sql.toString());

    return returner;
  }

  public Collection ejbHomeGetMultibleBookings(GeneralBooking booking) throws RemoteException, FinderException{
    //List list = new Vector();

	  Table table = new Table(this);
	  SelectQuery query = new SelectQuery(table);
	  
	  query.addColumn(new Column(table, getIDColumnName()));
  	  query.addCriteria(new MatchCriteria(new Column(table, getNameColumnName()), MatchCriteria.EQUALS,  booking.getName()));
	  query.addCriteria(new MatchCriteria(new Column(table, getAddressColumnName()), MatchCriteria.EQUALS,  booking.getAddress()));
	  query.addCriteria(new MatchCriteria(new Column(table, getAttendanceColumnName()), MatchCriteria.EQUALS,  booking.getAttendance()));
	  query.addCriteria(new MatchCriteria(new Column(table, getBookingTypeIDColumnName()), MatchCriteria.EQUALS,  booking.getBookingTypeID()));
	  query.addCriteria(new MatchCriteria(new Column(table, getCityColumnName()), MatchCriteria.EQUALS,  booking.getCity()));
	  query.addCriteria(new MatchCriteria(new Column(table, getCountryColumnName()), MatchCriteria.EQUALS,  booking.getCountry()));
	  query.addCriteria(new MatchCriteria(new Column(table, getIsValidColumnName()), MatchCriteria.EQUALS,  booking.getIsValid()));
	  query.addCriteria(new MatchCriteria(new Column(table, getPaymentTypeIdColumnName()), MatchCriteria.EQUALS,  booking.getPaymentTypeId()));
	  query.addCriteria(new MatchCriteria(new Column(table, getPostalCodeColumnName()), MatchCriteria.EQUALS,  booking.getPostalCode()));
	  query.addCriteria(new MatchCriteria(new Column(table, getServiceIDColumnName()), MatchCriteria.EQUALS,  booking.getServiceID()));
	  query.addCriteria(new MatchCriteria(new Column(table, getTelephoneNumberColumnName()), MatchCriteria.EQUALS,  booking.getTelephoneNumber()));
	  query.addCriteria(new MatchCriteria(new Column(table, getTotalCountColumnName()), MatchCriteria.EQUALS,  booking.getTotalCount()));
	  query.addCriteria(new MatchCriteria(new Column(table, getBookingCodeColumnName()), MatchCriteria.EQUALS,  booking.getCode()));
	  query.addCriteria(new MatchCriteria(new Column(table, getCreditcardAuthorizationNumberColumnName()), MatchCriteria.EQUALS,  booking.getCreditcardAuthorizationNumber()));
	  query.addOrder(new Order(new Column(table, getBookingDateColumnName()), true));
 
	try {
	  return idoFindPKsByQuery(query);
	  
//    try {
//      StringBuffer buff = new StringBuffer();
//        buff.append("SELECT * FROM "+getBookingTableName());
//        buff.append(" WHERE ");
//        buff.append(getNameColumnName()+" = '"+booking.getName()+"'");
//        buff.append(" AND ");
//        buff.append(getAddressColumnName()+" = '"+booking.getAddress()+"'");
//        buff.append(" AND ");
//        buff.append(getAttendanceColumnName()+" = '"+booking.getAttendance()+"'");
//        buff.append(" AND ");
//        buff.append(getBookingTypeIDColumnName()+" = '"+booking.getBookingTypeID()+"'");
//        buff.append(" AND ");
//        buff.append(getCityColumnName()+" = '"+booking.getCity()+"'");
//        buff.append(" AND ");
//        buff.append(getCountryColumnName()+" = '"+booking.getCountry()+"'");
//        buff.append(" AND ");
//        buff.append(getEmailColumnName()+" = '"+booking.getEmail()+"'");
//        buff.append(" AND ");
//        if (booking.getIsValid()) {
//          buff.append(getIsValidColumnName()+" = 'Y'");
//        }else {
//          buff.append(getIsValidColumnName()+" = 'N'");
//        }
//
//	  buff.append(" AND ");
//        buff.append(getPaymentTypeIdColumnName()+" = '"+booking.getPaymentTypeId()+"'");
//        buff.append(" AND ");
//        buff.append(getPostalCodeColumnName()+" = '"+booking.getPostalCode()+"'");
//        buff.append(" AND ");
//        buff.append(getServiceIDColumnName()+" = '"+booking.getServiceID()+"'");
//        buff.append(" AND ");
//        buff.append(getTelephoneNumberColumnName()+" = '"+booking.getTelephoneNumber()+"'");
//        buff.append(" AND ");
//        buff.append(getTotalCountColumnName()+" = '"+booking.getTotalCount()+"'");
//				buff.append(" AND ");
//				if (booking.getCode() == null) {
//					buff.append(getBookingCodeColumnName()+" is null");
//				} else {
//					buff.append(getBookingCodeColumnName()+" = '"+booking.getCode()+"'");
//				}
//				buff.append(" AND ");
//				if (booking.getCreditcardAuthorizationNumber() == null) {
//					buff.append(getCreditcardAuthorizationNumberColumnName()+" is null");
//				} else {
//					buff.append(getCreditcardAuthorizationNumberColumnName()+" = '"+booking.getCreditcardAuthorizationNumber()+"'");
//					
//				}
//				buff.append(" ORDER BY "+getBookingDateColumnName());
//      //coll = this.idoFindPKsBySQL(buff.toString());
//				System.out.println(buff.toString());
//      return this.idoFindPKsBySQL(buff.toString());
	  
    }catch (FinderException fe) {
      System.err.println("[GeneralBookingBMPBean] Error in sql : getting multiple bookings for bookingId : "+booking.getID());
      fe.printStackTrace(System.err);
      return null;
    }
  }

  public void removeAllTravelAddresses() throws IDORemoveRelationshipException{
    this.idoRemoveFrom(TravelAddress.class);
  }

  public void addTravelAddress(TravelAddress tAddress) throws IDOAddRelationshipException{
    this.idoAddTo(tAddress);
  }

  public Collection getTravelAddresses() throws IDORelationshipException {
    return this.idoGetRelatedEntities(TravelAddress.class);
  }

  public void setPrimaryKey(Object primaryKey) {
    super.setPrimaryKey(primaryKey);
  }

  public void removeFromReseller(Reseller reseller) throws IDORemoveRelationshipException {
    super.idoRemoveFrom(reseller);
  }

  public void removeFromAllResellers() throws IDORemoveRelationshipException {
    super.idoRemoveFrom(Reseller.class);
  }

  public void addToReseller(Reseller reseller) throws IDOAddRelationshipException {
    super.idoAddTo(reseller);
  }

  public Reseller getReseller() throws RemoteException, IDORelationshipException, FinderException{
    Collection coll = super.idoGetRelatedEntities(Reseller.class);
    if (coll != null && coll.size() > 0) {
      Iterator iter = coll.iterator();
      return ((ResellerHome) IDOLookup.getHome(Reseller.class)).findByPrimaryKey(iter.next());
    }else {
      throw new FinderException("Booking not connected to any reseller");
    }
  }

  public Collection ejbFindAllByCode(String code) throws FinderException {
  		return this.idoFindAllIDsByColumnsBySQL(getBookingCodeColumnName(), code, getIsValidColumnName(), "'Y'");
  }
  
  public Collection ejbFindAllByReferenceNumber(String refNum) throws FinderException {
  		IDOQuery query = this.idoQuery();
  		query.appendSelectAllFrom(this).appendWhereEqualsWithSingleQuotes(getReferenceNumberColumnName(), refNum)
			.appendAndEquals(getIsValidColumnName(), true);
		return this.idoFindPKsByQuery(query);
  }
  
	public Object ejbFindByAuthorizationNumber(String number, IWTimestamp stamp) throws FinderException {
		stamp.addDays(-1);
		IWTimestamp lessStamp = new IWTimestamp(stamp);
		stamp.addDays(2);
		IWTimestamp moreStamp = new IWTimestamp(stamp);
		
		Table table = new Table(this);
		Column auth = new Column(table, getCreditcardAuthorizationNumberColumnName());
		Column date = new Column(table, getDateOfBookingColumnName());
		
		SelectQuery query = new SelectQuery(table);
		query.addColumn(new WildCardColumn(table));
		query.addCriteria(new MatchCriteria(auth, MatchCriteria.EQUALS, number));
		query.addCriteria(new MatchCriteria(date, MatchCriteria.GREATER, lessStamp.getDate().toString()));
		query.addCriteria(new MatchCriteria(date, MatchCriteria.LESS, moreStamp.getDate().toString()));
		return this.idoFindOnePKBySQL(query.toString());
		
	}

  
}

