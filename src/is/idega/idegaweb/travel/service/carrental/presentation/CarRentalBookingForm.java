package is.idega.idegaweb.travel.service.carrental.presentation;

import is.idega.idegaweb.travel.business.ServiceNotFoundException;
import is.idega.idegaweb.travel.business.TimeframeNotFoundException;
import is.idega.idegaweb.travel.data.BookingEntry;
import is.idega.idegaweb.travel.data.PickupPlace;
import is.idega.idegaweb.travel.data.PickupPlaceHome;
import is.idega.idegaweb.travel.presentation.PublicBooking;
import is.idega.idegaweb.travel.service.carrental.business.CarRentalBooker;
import is.idega.idegaweb.travel.service.carrental.business.CarRentalBusiness;
import is.idega.idegaweb.travel.service.carrental.data.CarRental;
import is.idega.idegaweb.travel.service.carrental.data.CarRentalBooking;
import is.idega.idegaweb.travel.service.carrental.data.CarRentalBookingHome;
import is.idega.idegaweb.travel.service.carrental.data.CarRentalHome;
import is.idega.idegaweb.travel.service.presentation.BookingForm;
import java.rmi.RemoteException;
import java.sql.SQLException;
import java.util.Collection;
import java.util.Iterator;
import java.util.List;
import java.util.Vector;
import javax.ejb.CreateException;
import javax.ejb.FinderException;
import com.idega.block.trade.data.Currency;
import com.idega.block.trade.stockroom.business.ProductPriceException;
import com.idega.block.trade.stockroom.data.PriceCategory;
import com.idega.block.trade.stockroom.data.Product;
import com.idega.block.trade.stockroom.data.ProductPrice;
import com.idega.block.trade.stockroom.data.Timeframe;
import com.idega.block.trade.stockroom.data.TravelAddress;
import com.idega.business.IBOLookup;
import com.idega.data.IDOException;
import com.idega.data.IDOFinderException;
import com.idega.data.IDOLookup;
import com.idega.data.IDOLookupException;
import com.idega.data.IDORelationshipException;
import com.idega.idegaweb.IWApplicationContext;
import com.idega.idegaweb.presentation.CalendarParameters;
import com.idega.presentation.IWContext;
import com.idega.presentation.Table;
import com.idega.presentation.text.HorizontalRule;
import com.idega.presentation.text.Link;
import com.idega.presentation.text.Text;
import com.idega.presentation.ui.DateInput;
import com.idega.presentation.ui.DropdownMenu;
import com.idega.presentation.ui.Form;
import com.idega.presentation.ui.HiddenInput;
import com.idega.presentation.ui.InterfaceObject;
import com.idega.presentation.ui.ResultOutput;
import com.idega.presentation.ui.SubmitButton;
import com.idega.presentation.ui.TextArea;
import com.idega.presentation.ui.TextInput;
import com.idega.presentation.ui.TimeInput;
import com.idega.util.IWTimestamp;
import com.idega.util.text.TextSoap;

/**
 * <p>Title: idega</p>
 * <p>Description: software</p>
 * <p>Copyright: Copyright (c) 2002</p>
 * <p>Company: idega software</p>
 * @author <a href="mailto:gimmi@idega.is">Grimur Jonsson</a>
 * @version 1.0
 */

public class CarRentalBookingForm extends BookingForm {

	private CarRental _carRental;
	//private String PARAMETER_PICKUP_PLACE = "crbf_ppp";
	public static String PARAMETER_DROPOFF_PLACE = "crbf_pdp";
	public static String PARAMETER_PICKUP_TIME = "crbf_ppt";
	public static String PARAMETER_DROPOFF_TIME = "crbf_pdt";

  public CarRentalBookingForm(IWContext iwc, Product product) throws Exception{
    this(iwc, product, true);
  }
  public CarRentalBookingForm(IWContext iwc, Product product, boolean doInit) throws Exception{
	  super(iwc, product, doInit);
	  if (doInit) {
		setCarRental(iwc, product);
	  }
  }
  private void setCarRental(IWContext iwc, Product product) throws RemoteException{
		try {
		  _carRental = getCarRentalHome().findByPrimaryKey(product.getPrimaryKey());
			}catch (FinderException fe) {
		  fe.printStackTrace(System.err);
		}
  }  
  public Form getBookingForm(IWContext iwc) throws RemoteException, FinderException {
		if (!getIfExpired(iwc)) {
		  return getForm(iwc);
		}else {
		  return getExpiredForm(iwc);
		}
  }
  
  public boolean getIfExpired(IWContext iwc) throws RemoteException{
		boolean isExpired = false;
		if (_reseller != null) {
		  return isExpired = getCarRentalBusiness(iwc).getIfExpired(_contract, _stamp);
		} else {
			IWTimestamp now = IWTimestamp.RightNow();
			now.addDays(-1);
	    	
			return now.isLaterThanOrEquals(_stamp);
		}
  }
    
  private Form getForm(IWContext iwc) throws RemoteException, FinderException {
		Form form = new Form();
	  form.addParameter(CalendarParameters.PARAMETER_YEAR,_stamp.getYear());
	  form.addParameter(CalendarParameters.PARAMETER_MONTH,_stamp.getMonth());
	  form.addParameter(CalendarParameters.PARAMETER_DAY,_stamp.getDay());

		Table table = new Table();
		table.setBorder(0);
		table.setVerticalAlignment(Table.VERTICAL_ALIGN_TOP);
		form.add(table);

	  if (supplier != null) {
			form.addParameter(this.parameterSupplierId, supplier.getID());
	  }
	  table.setWidth("100%");
	
		table.setColumnAlignment(1,"right");
		table.setColumnAlignment(2,"left");

		List addresses;
		try {
				addresses = super.getProductBusiness(iwc).getDepartureAddresses(_product, _stamp, false);
		}catch (IDOFinderException ido) {
		  ido.printStackTrace(System.err);
		  addresses = new Vector();
		}
		TravelAddress tAddress;
		int addressId = -1;
		String sAddressId = iwc.getParameter(parameterDepartureAddressId);
		if (sAddressId != null) {
		  addressId = Integer.parseInt(sAddressId);
		}else if (addresses.size() > 0) {
		  addressId = ((TravelAddress) addresses.get(0)).getID();
		}
	
			int bookingDays = 1;
		Collection prices = null;
		Collection misc = null;
		Timeframe tFrame = getProductBusiness(iwc).getTimeframe(_product, _stamp, addressId);
		int timeframeId = -1;
		if (tFrame != null) {
		  timeframeId = tFrame.getID();
		  prices = getProductPriceHome().findProductPrices(_service.getID(), tFrame.getID(), addressId, false);
		  misc = getProductPriceHome().findMiscellaneousPrices(_service.getID(), tFrame.getID(), addressId, false);
		}else {
		  prices = getProductPriceHome().findProductPrices(_service.getID(), -1, -1, false);
		  misc = getProductPriceHome().findMiscellaneousPrices(_service.getID(), -1, -1, false);
		}
	
		if (prices.size() == 1) {
	
			int row = 1;
			int textInputSizeLg = 38;
			int textInputSizeMd = 18;
			int textInputSizeSm = 5;
	
			  DateInput fromDate = new DateInput(parameterFromDate);
				fromDate.setDay(_stamp.getDay());
				fromDate.setMonth(_stamp.getMonth());
				fromDate.setYear(_stamp.getYear());
				fromDate.setDisabled(true);
	
	//			DateInput toDate = new DateInput("baraBogus");
	//			toDate.setDisabled(true);
	
	
			  TextInput manyDays = new TextInput(parameterManyDays);
				manyDays.setSize(5);
				manyDays.setContent("1");
	
			Text surnameText = (Text) theText.clone();
				surnameText.setText(iwrb.getLocalizedString("travel.surname","surname"));
			Text lastnameText = (Text) theText.clone();
				lastnameText.setText(iwrb.getLocalizedString("travel.last_name","last name"));
			Text addressText = (Text) theText.clone();
				addressText.setText(iwrb.getLocalizedString("travel.address","address"));
			Text areaCodeText = (Text) theText.clone();
				areaCodeText.setText(iwrb.getLocalizedString("travel.area_code","area code"));
			Text emailText = (Text) theText.clone();
				emailText.setText(iwrb.getLocalizedString("travel.email","e-mail"));
			Text telNumberText = (Text) theText.clone();
				telNumberText.setText(iwrb.getLocalizedString("travel.telephone_number","telephone number"));
			Text cityText = (Text) theText.clone();
				cityText.setText(iwrb.getLocalizedString("travel.city_sm","city"));
			Text countryText = (Text) theText.clone();
				countryText.setText(iwrb.getLocalizedString("travel.country_sm","country"));
			Text depPlaceText = (Text) theText.clone();
				depPlaceText.setText(iwrb.getLocalizedString("travel.departure_place","Departure place"));
			Text fromText = (Text) theText.clone();
				fromText.setText(iwrb.getLocalizedString("travel.arrival_date","Arrival date"));
			Text toText = (Text) theText.clone();
				toText.setText(iwrb.getLocalizedString("travel.departure_date","Departure date"));
			Text manyDaysText = (Text) theText.clone();
				manyDaysText.setText(iwrb.getLocalizedString("travel.number_of_days","Number of days"));
	//			  Text toText = (Text) theText.clone();
	//				  toText.setText(iwrb.getLocalizedString("travel.departure_day","Departure day"));
			Text commentText = (Text) theText.clone();
				commentText.setText(iwrb.getLocalizedString("travel.comment","Comment"));
	
			DropdownMenu depAddr = new DropdownMenu(addresses, this.parameterDepartureAddressId);
			  depAddr.setToSubmit();
			  depAddr.setSelectedElement(Integer.toString(addressId));
	
			TextInput surname = new TextInput("surname");
				surname.setSize(textInputSizeLg);
				surname.keepStatusOnAction();
			TextInput lastname = new TextInput("lastname");
				lastname.setSize(textInputSizeLg);
				lastname.keepStatusOnAction();
			TextInput address = new TextInput("address");
				address.setSize(textInputSizeLg);
				address.keepStatusOnAction();
			TextInput areaCode = new TextInput("area_code");
				areaCode.setSize(textInputSizeSm);
				areaCode.keepStatusOnAction();
			TextInput email = new TextInput("e-mail");
				email.setSize(textInputSizeMd);
				email.keepStatusOnAction();
			TextInput telNumber = new TextInput("telephone_number");
				telNumber.setSize(textInputSizeMd);
				telNumber.keepStatusOnAction();
			TextInput city = new TextInput("city");
				city.setSize(textInputSizeLg);
				city.keepStatusOnAction();
			TextInput country = new TextInput("country");
				country.setSize(textInputSizeMd);
				country.keepStatusOnAction();
			TextArea comment = new TextArea("comment");
				  comment.setWidth("350");
				  comment.setHeight("60");
				comment.keepStatusOnAction();
	
			DropdownMenu usersDrop = null;
			DropdownMenu payType = getBooker(iwc).getPaymentTypeDropdown(iwrb, "payment_type");
	
			++row;
			table.add(surnameText,1,row);
			table.add(surname,2,row);
	
			++row;
			table.add(lastnameText,1,row);
			table.add(lastname,2,row);
	
			++row;
			table.add(addressText,1,row);
			table.add(address,2,row);
	
			++row;
			table.add(cityText,1,row);
			table.add(city,2,row);
	
			++row;
			table.add(areaCodeText,1,row);
			table.add(areaCode,2,row);
	
			++row;
			table.add(countryText,1,row);
			table.add(country,2,row);
	
			++row;
			table.add(emailText,1,row);
			table.add(email,2,row);
	
			++row;
			table.add(telNumberText,1,row);
			table.add(telNumber,2,row);
	
			if (addresses.size() > 1) {
			  ++row;
			  table.add(depPlaceText, 1, row);
			  table.add(depAddr, 2,row);
			}else {
			  table.add(new HiddenInput(this.parameterDepartureAddressId, Integer.toString(addressId)));
			}
	
			++row;
			table.add(fromText, 1, row);
			table.add(fromDate, 2, row);
			++row;
			table.add(manyDaysText, 1, row);
			table.add(manyDays, 2, row);

			++row;
			table.add(Text.BREAK, 1, row);
	
			DropdownMenu pickupPlaces = null;
			TimeInput pickupTime = null;
			DropdownMenu dropoffPlaces = null;
			TimeInput dropoffTime = null;
			try {
				Collection coll;
				pickupPlaces = new DropdownMenu( super.parameterPickupId );
				coll = _carRental.getPickupPlaces();
				if (coll != null && !coll.isEmpty()) {
					Iterator iter = coll.iterator();
					PickupPlace p;
					while (iter.hasNext()) {
						p = (PickupPlace) iter.next();
						pickupPlaces.addMenuElement(p.getPrimaryKey().toString(), p.getName());
					}	
				}
	 		  pickupTime = new TimeInput( PARAMETER_PICKUP_TIME );
				pickupTime.setHour(8);
				pickupTime.setMinute(0);
				
				++row;
				Text pickupPlaceText = (Text) theText.clone();
				pickupPlaceText.setText(iwrb.getLocalizedString("travel.pickup_place","Pickup place"));
				Text pickupTimeText = (Text) theText.clone();
				pickupTimeText.setText(iwrb.getLocalizedString("travel.pickup_time","Pickup time"));
				table.add(pickupPlaceText, 1, row);
				table.add(pickupPlaces, 2, row);
				++row;
				table.add(pickupTimeText, 1, row);
				table.add(pickupTime, 2, row);

				dropoffPlaces = new DropdownMenu( PARAMETER_DROPOFF_PLACE );
				coll = _carRental.getDropoffPlaces();
				if (coll != null && !coll.isEmpty()) {
					Iterator iter = coll.iterator();
					PickupPlace p;
					while (iter.hasNext()) {
						p = (PickupPlace) iter.next();
						dropoffPlaces.addMenuElement(p.getPrimaryKey().toString(), p.getName());
					}	
				}
				dropoffTime = new TimeInput( PARAMETER_DROPOFF_TIME );
				dropoffTime.setHour(8);
				dropoffTime.setMinute(0);
				
				++row;
				Text dropoffPlaceText = (Text) theText.clone();
				dropoffPlaceText.setText(iwrb.getLocalizedString("travel.dropoff_place","Dropoff place"));
				Text dropoffTimeText = (Text) theText.clone();
				dropoffTimeText.setText(iwrb.getLocalizedString("travel.dropoff_time","Dropoff time"));
				table.add(dropoffPlaceText, 1, row);
				table.add(dropoffPlaces, 2, row);
				++row;
				table.add(dropoffTimeText, 1, row);
				table.add(dropoffTime, 2, row);

			} catch (IDORelationshipException e1) {
				e1.printStackTrace(System.err);
			}

			if (_booking != null) {
	//			fromDate.setDate(_booking.getBookingDate());
							fromDate.setDisabled(false);
							if (this._multipleBookings) {
								bookingDays = super._multipleBookingNumber[1];
								manyDays.setContent(Integer.toString(bookingDays));	
							}
	//			++row;
	//			table.add(toText, 1, row);
	//			table.add(toDate, 2, row);
	//			manyDays.setOnBlur("this.form."+toDate.sety+".value=\"2002-11-23\"");
			}/*else {
			  table.add(new HiddenInput(parameterFromDate, new IWTimestamp(_booking.getBookingDate()).toSQLDateString()), 1, row);
			  GeneralBookingHome gbHome = (GeneralBookingHome) IDOLookup.getHome(GeneralBooking.class);
			  GeneralBooking tempBooking = gbHome.findByPrimaryKey(_booking.getPrimaryKey());
			  List bookingsJa = gbHome.getMultibleBookings(tempBooking);
			  table.add(new HiddenInput(parameterManyDays, Integer.toString(bookingsJa.size())), 1, row);
			}*/
	
			Text pPriceCatNameText;
			ResultOutput pPriceText;
//			RadioButton pPriceMany;
			//TextInput pPriceMany;
			InterfaceObject pPriceMany;
			PriceCategory category;
			Text txtPrice;
			Text txtPerPerson = (Text) theText.clone();
			  txtPerPerson.setText(iwrb.getLocalizedString("travel.per_person","per person"));
	
			Text totalText = (Text) theBoldText.clone();
			  totalText.setText(iwrb.getLocalizedString("travel.total","Total"));
			ResultOutput TotalPassTextInput = new ResultOutput("total_pass","0");
			  TotalPassTextInput.setSize(5);
			ResultOutput TotalTextInput = new ResultOutput("total","0");
			  TotalTextInput.setSize(8);
			/*try {
				String sPrice = Float.toString(getTravelStockroomBusiness(iwc).getPrice(prices[0].getID(), _service.getID(),prices[0].getPriceCategoryID(),prices[0].getCurrencyId(),IWTimestamp.getTimestampRightNow(), timeframeId, addressId));
				TotalPassTextInput = new ResultOutput("total_pass",sPrice);
				  TotalPassTextInput.setSize(5);
				TotalTextInput = new ResultOutput("total",sPrice);
				  TotalTextInput.setSize(8);
			} catch (SQLException e) {
				
			}*/
	
			++row;
			table.add(Text.NON_BREAKING_SPACE, 1,row);
	
			BookingEntry[] entries = null;
			ProductPrice pPri = null;
			int totalCount = 0;
			int totalSum = 0;
			int currentSum = 0;
			int currentCount = 0;
			if (_booking != null) {
			  entries = getBooker(iwc).getBookingEntries(_booking);
			}
	
			++row;
	
			Table pTable = new Table(3,1);
			  pTable.setWidth(1, Integer.toString(pWidthLeft));
			  pTable.setWidth(2, Integer.toString(pWidthCenter));
			  pTable.setWidth(3, Integer.toString(pWidthRight));
			  pTable.setCellpaddingAndCellspacing(0);
			table.add(pTable, 2, row+1);
	//			pTable.setBorder(1);
	
			Text count = (Text) super.theSmallBoldText.clone();
			  count.setText(iwrb.getLocalizedString("travel.number_of_units","Number of units"));
			Text unitPrice = (Text) super.theSmallBoldText.clone();
			  unitPrice.setText(iwrb.getLocalizedString("travel.unit_price","Unit price"));
			Text amount = (Text) super.theSmallBoldText.clone();
			  amount.setText(iwrb.getLocalizedString("travel.total_price","Total price"));
	
			pTable.add(count, 1, 1);
			pTable.add(unitPrice, 2, 1);
			pTable.add(amount, 3, 1);
	
			int pricesLength = prices.size();
			int miscLength = misc.size();
			ProductPrice[] pPrices = new ProductPrice[pricesLength+miscLength];
			Iterator iter = prices.iterator();
			for (int i = 0; i < pricesLength; i++) {
			  pPrices[i] = (ProductPrice) iter.next();
			}
			iter = misc.iterator();
			for (int i = 0; i < miscLength; i++) {
			  pPrices[i+pricesLength] = (ProductPrice) iter.next();
			}
			int mainPrice = 0;
			for (int i = 0; i < pPrices.length; i++) {
				try {
					++row;
					category = pPrices[i].getPriceCategory();
					int price = (int) getTravelStockroomBusiness(iwc).getPrice(((Integer) pPrices[i].getPrimaryKey()).intValue(), _service.getID(),pPrices[i].getPriceCategoryID(),pPrices[i].getCurrencyId(),IWTimestamp.getTimestampRightNow(), timeframeId, addressId);
		//              pPrices[i].getPrice();
					pPriceCatNameText = (Text) theText.clone();
					  pPriceCatNameText.setText(category.getName());
	
					pPriceText = new ResultOutput("thePrice"+((Integer) pPrices[i].getPrimaryKey()).intValue(),"0");
					  pPriceText.setSize(8);
//					pPriceMany = new RadioButton("priceCategory","0");
//					pPriceMany = new RadioButton("priceCategory"+pPrices[i].getID() ,"0");
//					pPriceMany.setValue("0");
//					pPriceMany.setValueOnClick(pPriceMany, "1");
					  //pPriceMany.setSize(5);
					pPriceMany = new HiddenInput("priceCategory"+((Integer) pPrices[i].getPrimaryKey()).intValue() ,"1");
	
					if (i == pricesLength) {
					  Text tempTexti = (Text) theBoldText.clone();
						tempTexti.setText(iwrb.getLocalizedString("travel.miscellaneous_services","Miscellaneous services"));
	//					table.mergeCells(1, row, 2, row);
					  table.add(tempTexti, 1, row);
					  ++row;
					}
					/* Removed because price categories are not displayed
					else if (i == 0) {
					  Text tempTexti = (Text) theBoldText.clone();
						tempTexti.setText(iwrb.getLocalizedString("travel.basic_prices","Basic prices"));
						tempTexti.setUnderline(true);
	//					table.mergeCells(1, row, 2, row);
					  table.add(tempTexti, 1, row);
					  ++row;
					}
					*/
					if (i >= pricesLength) {
						pPriceMany = new TextInput("miscPriceCategory"+((Integer) pPrices[i].getPrimaryKey()).intValue());
					  //pPriceMany.setName("miscPriceCategory"+pPrices[i].getID());
						((TextInput) pPriceMany).setSize(5);					  
					}else {
						mainPrice = price;
					}
	
					if (_booking != null) {
					  if (entries != null) {
						for (int j = 0; j < entries.length; j++) {
						  if (entries[j].getProductPrice().getPriceCategoryID() == pPrices[i].getPriceCategoryID()) {
							pPri = entries[j].getProductPrice();
							currentCount = entries[j].getCount();
							Collection pTimeframes;
													try {
								price = (int) getTravelStockroomBusiness(iwc).getPrice(((Integer) pPri.getPrimaryKey()).intValue(), _productId,pPri.getPriceCategoryID(),pPri.getCurrencyId(),IWTimestamp.getTimestampRightNow(), timeframeId, addressId);
													} catch (ProductPriceException e) {
														try {
									int pTimeframeId = -1;
															pTimeframes = pPri.getTimeframes();
									if (pTimeframes != null && pTimeframes.size()>0) {
										Iterator its = pTimeframes.iterator();
										pTimeframeId = ((Timeframe)its.next()).getID();	
									}
									price = (int) getTravelStockroomBusiness(iwc).getPrice(((Integer) pPri.getPrimaryKey()).intValue(), _productId,pPri.getPriceCategoryID(),pPri.getCurrencyId(),IWTimestamp.getTimestampRightNow(), pTimeframeId, addressId);
														} catch (IDORelationshipException idoe) {
															idoe.printStackTrace(System.err);
														}
													}
	//						  price = (int) getTravelStockroomBusiness(iwc).getPrice(pPri.getID(), _productId,pPri.getPriceCategoryID(),pPri.getCurrencyId(),IWTimestamp.getTimestampRightNow(), pTimeframeId, addressId);
							currentSum = (int) (currentCount * price);
	
							totalCount += currentCount;
							totalSum += currentSum;
							pPriceMany.setContent(Integer.toString(currentCount));
							pPriceText = new ResultOutput("thePrice"+((Integer) pPrices[i].getPrimaryKey()).intValue(),Integer.toString(currentSum));
							  pPriceText.setSize(8);
						  }
						}
					  }
					}
	
	
		//                  table.add(Text.NON_BREAKING_SPACE,2,row);
					if (pPriceMany instanceof TextInput ) {
						pPriceText.add(manyDays, ResultOutput.OPERATOR_MULTIPLY, null);
						TotalTextInput.add(pPriceMany ,ResultOutput.OPERATOR_MULTIPLY+Integer.toString(price));
						pPriceText.add(pPriceMany,ResultOutput.OPERATOR_MULTIPLY, ResultOutput.OPERATOR_MULTIPLY+Integer.toString(price));
						TotalPassTextInput.add(pPriceMany);

						table.add(pPriceCatNameText, 1,row);
	
						txtPrice = (Text) theText.clone();
						  txtPrice.setText(Integer.toString(price));

						pTable = new Table(4,1);
						  pTable.setWidth(1, Integer.toString(pWidthLeft));
						  pTable.setWidth(2, Integer.toString(pWidthCenter));
						  pTable.setWidth(3, Integer.toString(pWidthRight));
						  pTable.setCellpaddingAndCellspacing(0);
						  pTable.add(pPriceMany,1,1);
						  pTable.add(txtPrice,2,1);
						  pTable.add(pPriceText, 3,1);
		
		
			//                    pTable.add();
						table.add(pTable, 2, row);
					} else {
						--row;
						//pPriceText.add(manyDays, ResultOutput.OPERATOR_MULTIPLY, ResultOutput.OPERATOR_MULTIPLY+price);
						//TotalTextInput.add(pPriceMany ,ResultOutput.OPERATOR_MULTIPLY, ResultOutput.OPERATOR_MULTIPLY+Integer.toString(price));
						//TotalTextInput.setExtraForTotal(ResultOutput.OPERATOR_PLUS+Integer.toString(price));
						//pPriceText.add(pPriceMany,ResultOutput.OPERATOR_MULTIPLY, ResultOutput.OPERATOR_MULTIPLY+Integer.toString(price));
						//TotalPassTextInput.add(ResultOutput.OPERATOR_MULTIPLY+Integer.toString(price));
//						TotalPassTextInput.setExtraForTotal(ResultOutput.OPERATOR_PLUS+"1");

						table.add(pPriceMany, 1, row);
					}
	
				}catch (SQLException sql) {
				  sql.printStackTrace(System.err);
				}catch (FinderException fe) {
				  fe.printStackTrace(System.err);
				}
			}
	
			++row;
			++row;
	
			table.add(totalText,1,row);
	
			// TODO reppis peppis

			if (_booking != null) {
			  TotalPassTextInput.setContent(Integer.toString(totalCount));
			  TotalTextInput.setContent(Integer.toString(totalSum * bookingDays));
			}
			else {
				TotalPassTextInput.setContent("1");
				TotalTextInput.setContent(Integer.toString(mainPrice));
			}
			
			TotalTextInput.add(manyDays, ResultOutput.OPERATOR_MULTIPLY, null);
			TotalTextInput.setExtraForTotal(ResultOutput.OPERATOR_PLUS+"(myForm."+manyDays.getName()+".value"+ResultOutput.OPERATOR_MULTIPLY+Integer.toString(mainPrice)+")");
			TotalPassTextInput.setExtraForTotal(ResultOutput.OPERATOR_PLUS+"1");
			
			pTable = new Table(3,1);
			  pTable.setWidth(1, Integer.toString(pWidthLeft));
			  pTable.setWidth(2, Integer.toString(pWidthCenter));
			  pTable.setWidth(3, Integer.toString(pWidthRight));
			  pTable.setCellpaddingAndCellspacing(0);
	
			pTable.add(TotalPassTextInput,1,1);
			pTable.add(TotalTextInput,3,1);
			table.add(pTable, 2, row);
			 table.add(new HiddenInput("available",Integer.toString(available)),2,row);
	
			++row;
			table.add(Text.NON_BREAKING_SPACE,1, row);
	
			if (super.getUser() != null) {
			  ++row;
			  List users = null;
			  if ( this.supplier != null) users = getSupplierManagerBusiness(iwc).getUsersIncludingResellers(supplier);
			  if ( _reseller != null) users = getResellerManager(iwc).getUsersIncludingSubResellers(_reseller);
			  if (users == null) users = com.idega.util.ListUtil.getEmptyList();
			  usersDrop = this.getDropdownMenuWithUsers(users, "ic_user");
			  usersDrop.setSelectedElement(Integer.toString(super.getUserId()));
			  usersDrop.keepStatusOnAction();
	
			  Text tUser = (Text) theText.clone();
				tUser.setFontColor(WHITE);
				tUser.setText(iwrb.getLocalizedString("travel.user","User"));
			  table.add(tUser, 1, row);
			  table.add(usersDrop, 2 ,row);
			}
	
			++row;
			Text payText = (Text) theText.clone();
			  payText.setText(iwrb.getLocalizedString("travel.payment_type","Payment type"));
			table.add(payText, 1, row);
			table.add(payType, 2, row);
	
	
			  ++row;
			  table.add(commentText, 1, row);
			  table.setVerticalAlignment(1, row, Table.VERTICAL_ALIGN_TOP);
			  table.add(comment, 2, row);
	
				row = addCreditcardInputForm(iwc, table, row);
	
			if (_booking != null) {
			  form.addParameter(this.parameterBookingId,_booking.getID());
			  surname.setContent(_booking.getName());
			  address.setContent(_booking.getAddress());
			  city.setContent(_booking.getCity());
			  areaCode.setContent(_booking.getPostalCode());
			  country.setContent(_booking.getCountry());
			  email.setContent(_booking.getEmail());
			  telNumber.setContent(_booking.getTelephoneNumber());
	
			  if (usersDrop != null) {
				usersDrop.setSelectedElement(Integer.toString(_booking.getUserId()));
			  }
			  payType.setSelectedElement(Integer.toString(_booking.getPaymentTypeId()));
			  if (_booking.getComment() != null) {
				comment.setContent(_booking.getComment());
			  }
			  
			  CarRentalBooking crBooking = getCarRentalBookingHome().findByPrimaryKey(_booking.getPrimaryKey());
			  
				if (pickupPlaces != null) {
					pickupPlaces.setSelectedElement(Integer.toString(crBooking.getPickupPlaceID()));
					IWTimestamp stamp = new IWTimestamp(crBooking.getPickupTime());
					pickupTime.setHour(stamp.getHour());
					pickupTime.setMinute(stamp.getMinute());
				}
				if (dropoffPlaces != null) {
					dropoffPlaces.setSelectedElement(Integer.toString(crBooking.getDropoffPlaceId()));
					IWTimestamp stamp = new IWTimestamp(crBooking.getDropoffTime());
					dropoffTime.setHour(stamp.getHour());
					dropoffTime.setMinute(stamp.getMinute());
				}
			}
	        
			++row;
			if (_booking != null) {
			  table.add(new SubmitButton(iwrb.getImage("buttons/update.gif"), this.sAction, this.parameterSaveBooking),2,row);
			}else {
			  table.add(new SubmitButton(iwrb.getImage("buttons/book.gif"), this.sAction, this.parameterSaveBooking),2,row);
			}
			table.add(new HiddenInput(this.BookingAction,this.BookingParameter),2,row);
			table.setAlignment(2, row, Table.HORIZONTAL_ALIGN_RIGHT);
		}	else if (prices.size() > 2) {
			Text text = (Text) theText.clone();
			text.setText(iwrb.getLocalizedString("travel.too_many_price_categories","Too many price categories, should be only 1."));
			table.add(text);
			table.setAlignment(1, 1, Table.HORIZONTAL_ALIGN_LEFT);
		} else {
		  if (supplier != null || _reseller != null) {
				Text text = (Text) theText.clone();
				text.setText(iwrb.getLocalizedString("travel.too_few_price_categories","Too few price categories, should be 1."));
			  table.add(text);
			  table.setAlignment(1, 1, Table.HORIZONTAL_ALIGN_LEFT);
		  }
		}
	
		return form;
  }
 
  public Form getPublicBookingFormOld(IWContext iwc, Product product) throws RemoteException, FinderException {
			try {
				/** Not tested 100% here, but seems to work at other places... */
				if (isFullyBooked(iwc, product, _stamp) || isUnderBooked(iwc, product, _stamp)) {
					_useInquiryForm	= true;
				}
			} catch (CreateException e) {
				e.printStackTrace(System.err);
			}
	
		try {
		  return getPublicBookingFormPrivate(iwc, product);
		}catch (ServiceNotFoundException snfe) {
		  throw new FinderException(snfe.getMessage());
		}catch (TimeframeNotFoundException tnfe) {
		  throw new FinderException(tnfe.getMessage());
		}
  }


  private Form getPublicBookingFormPrivate(IWContext iwc, Product product) throws RemoteException, ServiceNotFoundException, TimeframeNotFoundException, FinderException {
		Form form = new Form();
		  form.addParameter(this.parameterOnlineBooking, "true");
		Table table = new Table();
		  table.setCellpadding(0);
		  table.setCellspacing(6);
		  table.setBorder(0);
		  table.setWidth("100%");
		  form.add(table);
	
		  if (_stamp != null) {
				form.addParameter(CalendarParameters.PARAMETER_YEAR,_stamp.getYear());
				form.addParameter(CalendarParameters.PARAMETER_MONTH,_stamp.getMonth());
				form.addParameter(CalendarParameters.PARAMETER_DAY,_stamp.getDay());
		  }
	
		  boolean isDay = true;
	
		  try {
			isDay = this.getIsDayVisible(iwc);
	//		  isDay = getTravelStockroomBusiness(iwc).getIfDay(iwc, this._product, stamp);
		  }catch (SQLException sql) {
			throw new FinderException(sql.getMessage());
		  }
	
		  Collection prices = null;
		  Collection misc = null;
		  Timeframe tFrame = getProductBusiness(iwc).getTimeframe(_product, _stamp, -1);
		  int timeframeId = -1;
		  if (tFrame != null) {
			timeframeId = tFrame.getID();
			prices = getProductPriceHome().findProductPrices(_service.getID(), timeframeId, -1, true);
			misc = getProductPriceHome().findMiscellaneousPrices(_service.getID(), timeframeId, -1, true);
		  }else {
			prices = getProductPriceHome().findProductPrices(_service.getID(), -1, -1, true);
			misc = getProductPriceHome().findMiscellaneousPrices(_service.getID(), -1, -1, true);
		  }
	
	
		  Text availSeats = (Text) theText.clone();
			availSeats.setText(iwrb.getLocalizedString("travel.there_is_availability","There is availability "));
	
		  Text notAvailSeats = (Text) theText.clone();
			notAvailSeats.setText(iwrb.getLocalizedString("travel.there_is_no_availability","There is no availability "));
	
		  Text inquiryText = (Text) theBoldText.clone();
			inquiryText.setText(iwrb.getLocalizedString("travel.attention","Attention!"));
			//inquiryText.setText(iwrb.getLocalizedString("travel.please_fill_out_inquiry_form","An inquiry must be sent. Please fill out the inquiry form, or select another day."));
		  Text inquiryExplain = (Text) theText.clone();
			inquiryExplain.setText(iwrb.getLocalizedString("travel.inquiry_explain","A departure on the selected day cannot be guarenteed. By filling out this form you will send us your request and we will try to meet your requirements.\nYou can also select another day from the calendar."));
	
		  Text dateText = (Text) theBoldText.clone();
			dateText.setText(getLocaleDate(_stamp));
			dateText.addToText("."+Text.NON_BREAKING_SPACE);
	
		  Text pleaseBook = (Text) theText.clone();
			pleaseBook.setText(iwrb.getLocalizedString("travel.please_book","Please book"));
	
		  Text pleaseFindAnotherDay = (Text) theText.clone();
			pleaseFindAnotherDay.setText(iwrb.getLocalizedString("travel.please_find_another_day","Please find another day"));
	
		  if (prices.size() > 0) {
			  int row = 1;
			  int textInputSizeLg = 28;
			  int textInputSizeMd = 28;//18;
			  int textInputSizeSm = 28;//5;
	
			  Table pTable;
			  Table pTableToClone = new Table();
			  int pWidthLeft = 60;
			  int pWidthCenter = 60;
			  int pWidthRight = 75;
				pTableToClone.setWidth(1, Integer.toString(pWidthLeft));
				pTableToClone.setWidth(2, Integer.toString(pWidthCenter));
				pTableToClone.setWidth(3, Integer.toString(pWidthRight));
				pTableToClone.setCellpaddingAndCellspacing(0);
	
			  HorizontalRule hr = new HorizontalRule("100%");
				hr.setColor(WHITE);
	
			  Text subHeader;
	
	
			  table.mergeCells(1,row,6,row);
	
	
			  if (isDay) {
				if (_useInquiryForm) {
				  table.add(inquiryText, 1, row);
				  table.add(Text.BREAK, 1, row);
				  table.add(inquiryExplain, 1, row);
				}else {
				  table.add(availSeats,1,row);
				  table.add(dateText,1,row);
				  table.add(pleaseBook,1,row);
				}
				++row;
	
			  String star = " * ";
	
			  Text surnameText = (Text) theText.clone();
				  surnameText.setText(star);
				  surnameText.addToText(iwrb.getLocalizedString("travel.surname","surname"));
			  Text lastnameText = (Text) theText.clone();
				  lastnameText.setText(star);
				  lastnameText.addToText(iwrb.getLocalizedString("travel.last_name","last name"));
			  Text addressText = (Text) theText.clone();
				  addressText.setText(star);
				  addressText.addToText(iwrb.getLocalizedString("travel.address","address"));
			  Text areaCodeText = (Text) theText.clone();
				  areaCodeText.setText(star);
				  areaCodeText.addToText(iwrb.getLocalizedString("travel.area_code","area code"));
			  Text emailText = (Text) theText.clone();
				  emailText.setText(star);
				  emailText.addToText(iwrb.getLocalizedString("travel.email","e-mail"));
			  Text telNumberText = (Text) theText.clone();
				  telNumberText.setText(iwrb.getLocalizedString("travel.telephone_number","telephone number"));
			  Text cityText = (Text) theText.clone();
				  cityText.setText(star);
				  cityText.addToText(iwrb.getLocalizedString("travel.city_sm","city"));
			  Text countryText = (Text) theText.clone();
				  countryText.setText(star);
				  countryText.addToText(iwrb.getLocalizedString("travel.country_sm","country"));
			  Text depPlaceText = (Text) theText.clone();
				  depPlaceText.setText(iwrb.getLocalizedString("travel.departure_place","Departure place"));
			  Text fromText = (Text) theText.clone();
				  fromText.setText(iwrb.getLocalizedString("travel.date_of_departure","Date of departure"));
			  Text manyDaysText = (Text) theText.clone();
				  manyDaysText.setText(iwrb.getLocalizedString("travel.number_of_days","Number of days"));
			  Text commentText = (Text) theText.clone();
				  commentText.setText(iwrb.getLocalizedString("travel.comment","Comment"));
	
	
			  TextInput surname = new TextInput("surname");
				  surname.setSize(textInputSizeLg);
			  TextInput lastname = new TextInput("lastname");
				  lastname.setSize(textInputSizeLg);
			  TextInput address = new TextInput("address");
				  address.setSize(textInputSizeLg);
			  TextInput areaCode = new TextInput("area_code");
				  areaCode.setSize(textInputSizeSm);
			  TextInput email = new TextInput("e-mail");
				  email.setSize(textInputSizeMd);
			  TextInput telNumber = new TextInput("telephone_number");
				  telNumber.setSize(textInputSizeMd);
			  TextInput city = new TextInput("city");
				  city.setSize(textInputSizeLg);
			  TextInput country = new TextInput("country");
				  country.setSize(textInputSizeMd);
	
			  DateInput fromDate = new DateInput(parameterFromDate);
				fromDate.setDay(_stamp.getDay());
				fromDate.setMonth(_stamp.getMonth());
				fromDate.setYear(_stamp.getYear());
				fromDate.setDisabled(true);
	
			  TextInput manyDays = new TextInput(parameterManyDays);
				manyDays.setContent("1");
				manyDays.setSize(5);
	
			  TextArea comment = new TextArea("comment");
				  comment.setWidth("350");
				  comment.setHeight("60");
	
			  ++row;
			  table.mergeCells(1,row,6,row);
			  table.add(hr,1,row);
			  ++row;
			  subHeader = (Text) theBoldText.clone();
				subHeader.setFontColor(WHITE);
				subHeader.setText(iwrb.getLocalizedString("travel.booking_information","Booking information"));
			  table.add(subHeader, 1, row);
			  table.mergeCells(1, row, 6 ,row);
	
			  ++row;
			  table.add(fromText, 1, row);
        table.add(new HiddenInput(parameterFromDate, _stamp.toSQLString()));
        Text currDate = (Text) theText.clone();
        currDate.setText(_stamp.getLocaleDate(iwc.getCurrentLocale()));
        table.add(currDate,  2, row);//fromDate, 2, row);
        //table.add(fromDate, 2, row);
			  table.setAlignment(1,row,"right");
			  table.setAlignment(2,row,"left");
			  table.mergeCells(2,row,6,row);
			  ++row;
			  table.add(manyDaysText, 1, row);
			  table.add(manyDays, 2, row);
			  table.setAlignment(1,row,"right");
			  table.setAlignment(2,row,"left");
			  table.mergeCells(2,row,6,row);
	
			  Text pPriceCatNameText;
			  ResultOutput pPriceText;
			  InterfaceObject pPriceMany;
			  PriceCategory category;
			  Text txtPrice;
			  Text txtPerPerson = (Text) theBoldText.clone();
				txtPerPerson.setText(iwrb.getLocalizedString("travel.per_person","per person"));
	
			  Text totalText = (Text) theBoldText.clone();
				totalText.setText(iwrb.getLocalizedString("travel.total","Total"));
			  ResultOutput TotalPassTextInput = new ResultOutput("total_pass","0");
				TotalPassTextInput.setSize(5);
			  ResultOutput TotalTextInput = new ResultOutput("total","0");
				TotalTextInput.setSize(8);
	
			  ++row;
	
	//			Text count = (Text) super.theSmallBoldText.clone();
	//			  count.setText(iwrb.getLocalizedString("travel.number_of_seats","Number of seats"));
	//			Text unitPrice = (Text) super.theSmallBoldText.clone();
	//			  unitPrice.setText(iwrb.getLocalizedString("travel.price_per_seat","Price pr. seat"));
			  Text count = (Text) super.theSmallBoldText.clone();
				count.setText(iwrb.getLocalizedString("travel.number_of_units","Number of units"));
			  Text unitPrice = (Text) super.theSmallBoldText.clone();
				unitPrice.setText(iwrb.getLocalizedString("travel.unit_price","Unit price"));
			  Text amount = (Text) super.theSmallBoldText.clone();
				amount.setText(iwrb.getLocalizedString("travel.total_price","Total price"));
			  Text space = (Text) super.theSmallBoldText.clone();
				space.setText(Text.NON_BREAKING_SPACE);
	
	//			Table priceTable = new Table();
	//			  priceTable.setBorder(0);
	//			  priceTable.setCellpadding(0);
	//			  priceTable.setCellspacing(6);
	//			int pRow = 1;
	
			  pTable = (Table) pTableToClone.clone();
			  pTable.add(count, 1, 1);
			  pTable.add(unitPrice, 2, 1);
			  pTable.add(amount, 3, 1);
	
	//			priceTable.add(count, 1, pRow);
	//			priceTable.add(unitPrice, 2, pRow);
	//			priceTable.add(amount, 3, pRow);
	
	//			table.add(space, 1, row);
			  table.add(pTable, 2, row+1);
	//			table.mergeCells(2, row, 2, row + prices.length + misc.length + 1);
	
	
			  BookingEntry[] entries = null;
			  ProductPrice pPri = null;
			  int totalCount = 0;
			  int totalSum = 0;
			  int currentSum = 0;
			  int currentCount = 0;
			  if (_booking != null) {
				entries = getBooker(iwc).getBookingEntries(_booking);
			  }
	
			  int pricesLength = prices.size();
			  int miscLength = misc.size();
			  ProductPrice[] pPrices = new ProductPrice[pricesLength+miscLength];
			  Iterator iter = prices.iterator();
			  for (int i = 0; i < pricesLength; i++) {
				pPrices[i] = (ProductPrice) iter.next();
			  }
			  iter = misc.iterator();
			  for (int i = 0; i < miscLength; i++) {
				pPrices[i+pricesLength] = (ProductPrice) iter.next();
			  }
				int mainPrice = 0;
			  for (int i = 0; i < pPrices.length; i++) {
				  try {
					  ++row;
	//					++pRow;
					  category = pPrices[i].getPriceCategory();
					  int price = (int) getTravelStockroomBusiness(iwc).getPrice(((Integer)pPrices[i].getPrimaryKey()).intValue() ,_product.getID(),pPrices[i].getPriceCategoryID(),pPrices[i].getCurrencyId(),IWTimestamp.getTimestampRightNow(), timeframeId, -1);
		//              pPrices[i].getPrice();
					  pPriceCatNameText = (Text) theText.clone();
						pPriceCatNameText.setText(category.getName());
	
					  pPriceText = new ResultOutput("thePrice"+((Integer)pPrices[i].getPrimaryKey()).intValue(),"0");
						pPriceText.setSize(8);
	
						if (prices.size() == 1) {
							pPriceMany = new HiddenInput("priceCategory"+((Integer)pPrices[i].getPrimaryKey()).intValue(), "1");
						}else {
							pPriceMany = new TextInput("priceCategory"+((Integer)pPrices[i].getPrimaryKey()).intValue(), "0");
							((TextInput) pPriceMany).setSize(5);
						}
						
					  if (i == pricesLength) {
						Text tempTexti = (Text) theBoldText.clone();
						  tempTexti.setText(iwrb.getLocalizedString("travel.miscellaneous_services","Miscellaneous services"));
						//table.mergeCells(1, row, 2, row);
						table.setAlignment(1, row, "RIGHT");
						table.add(tempTexti, 1, row);
						++row;
					  }
					  
						if (prices.size() > 1 && i == 0) {
//					  else if (i == 0) {
							Text tempTexti = (Text) theBoldText.clone();
							  tempTexti.setText(iwrb.getLocalizedString("travel.basic_prices","Basic prices"));
							  tempTexti.setUnderline(true);
							//table.mergeCells(1, row, 2, row);
							table.setAlignment(1, row, "RIGHT");
							table.add(tempTexti, 1, row);
							++row;
					  }
					  if (i >= pricesLength) {
							pPriceMany = new TextInput("miscPriceCategory"+((Integer)pPrices[i].getPrimaryKey()).intValue() ,"0");
							  ((TextInput) pPriceMany).setSize(5);
							//pPriceMany.setName("miscPriceCategory"+pPrices[i].getID());
					  }else {
					  	mainPrice = price;
				  	}
	
					  if (_booking != null) {
							if (entries != null) {
							  for (int j = 0; j < entries.length; j++) {
									if (entries[j].getProductPriceId() == ((Integer)pPrices[i].getPrimaryKey()).intValue()) {
									  pPri = entries[j].getProductPrice();
									  currentCount = entries[j].getCount();
									  currentSum = (int) (currentCount * getTravelStockroomBusiness(iwc).getPrice(((Integer)pPri.getPrimaryKey()).intValue(), _productId,pPri.getPriceCategoryID(),pPri.getCurrencyId(),IWTimestamp.getTimestampRightNow(), timeframeId, -1));
			
									  totalCount += currentCount;
									  totalSum += currentSum;
									  pPriceMany.setContent(Integer.toString(currentCount));
									  pPriceText = new ResultOutput("thePrice"+((Integer)pPrices[i].getPrimaryKey()).intValue(),Integer.toString(currentSum));
										pPriceText.setSize(8);
									}
							  }
							}
					  }

					  System.out.println("Instance "+pPriceMany.getClassName());
	
					  if (pPriceMany instanceof TextInput ) {
						  pPriceText.add(manyDays, ResultOutput.OPERATOR_MULTIPLY, null);
						  TotalTextInput.add(pPriceMany ,ResultOutput.OPERATOR_MULTIPLY+Integer.toString(price));
						  pPriceText.add(pPriceMany,ResultOutput.OPERATOR_MULTIPLY, ResultOutput.OPERATOR_MULTIPLY+Integer.toString(price));
						  TotalPassTextInput.add(pPriceMany);

				//		pPriceText.add(pPriceMany,ResultOutput.OPERATOR_MULTIPLY+price);
				//		pPriceText.add(manyDays, ResultOutput.OPERATOR_MULTIPLY, null);
				//		TotalPassTextInput.add(pPriceMany);
				//		TotalTextInput.add(pPriceMany,ResultOutput.OPERATOR_MULTIPLY+price);
				//		TotalTextInput.add(manyDays, ResultOutput.OPERATOR_MULTIPLY, null);
	
							pTable = (Table) pTableToClone.clone();
	
							table.add(pPriceCatNameText, 1,row);
							pTable.add(pPriceMany,1,1);
							pTable.add(pPriceText, 3,1);
		
							txtPrice = (Text) theText.clone();
							  txtPrice.setText(Integer.toString(price));
							pTable.add(txtPrice, 2,1);
		  //					table.add(txtPerPerson,3,row);
							table.setAlignment(1,row,"right");
							table.setAlignment(2,row,"left");
							table.setAlignment(3,row,"left");
			  //                    pTable.add();
						  table.add(pTable, 2, row);
					  } else {
						  --row;
						  //pPriceText.add(manyDays, ResultOutput.OPERATOR_MULTIPLY, ResultOutput.OPERATOR_MULTIPLY+price);
						  //TotalTextInput.add(pPriceMany ,ResultOutput.OPERATOR_MULTIPLY, ResultOutput.OPERATOR_MULTIPLY+Integer.toString(price));
						  //TotalTextInput.setExtraForTotal(ResultOutput.OPERATOR_PLUS+Integer.toString(price));
						  //pPriceText.add(pPriceMany,ResultOutput.OPERATOR_MULTIPLY, ResultOutput.OPERATOR_MULTIPLY+Integer.toString(price));
						  //TotalPassTextInput.add(ResultOutput.OPERATOR_MULTIPLY+Integer.toString(price));
//						  TotalPassTextInput.setExtraForTotal(ResultOutput.OPERATOR_PLUS+"1");

						  table.add(pPriceMany, 1, row);
					  }
	

	
				  }catch (SQLException sql) {
					sql.printStackTrace(System.err);
				  }catch (FinderException fe) {
					fe.printStackTrace(System.err);
				  }
			  }
	
			  ++row;
	//			++pRow;
	// TODO PREPPS
			  table.add(totalText,1,row);
			  if (_booking != null) {
					TotalPassTextInput.setContent(Integer.toString(totalCount));
					TotalTextInput.setContent(Integer.toString(totalSum));
			  }

			  if (_booking != null) {
				TotalPassTextInput.setContent(Integer.toString(totalCount));
				TotalTextInput.setContent(Integer.toString(totalSum));
//				TotalTextInput.setContent(Integer.toString(totalSum * bookingDays));
			  }
			  else {
				  TotalPassTextInput.setContent("1");
				  TotalTextInput.setContent(Integer.toString(mainPrice));
			  }
			
			  TotalTextInput.add(manyDays, ResultOutput.OPERATOR_MULTIPLY, null);
			  TotalTextInput.setExtraForTotal(ResultOutput.OPERATOR_PLUS+"(myForm."+manyDays.getName()+".value"+ResultOutput.OPERATOR_MULTIPLY+Integer.toString(mainPrice)+")");
			  if (prices.size() == 1) {
			  	TotalPassTextInput.setExtraForTotal(ResultOutput.OPERATOR_PLUS+"1");
			  }



			  pTable = (Table) pTableToClone.clone();
			  pTable.add(TotalPassTextInput,1,1);
			  pTable.add(TotalTextInput,3,1);
			  pTable.setColumnAlignment(2, "right");
			  table.setAlignment(1,row,"right");
			  table.setAlignment(2,row,"left");
			  table.add(pTable, 2, row);
	
			  //priceTable.setBorder(1);
	
			  ++row;
			  table.mergeCells(1,row,6,row);
			  table.add(hr,1,row);
			  ++row;
			  table.mergeCells(1,row,6,row);
			  subHeader = (Text) theBoldText.clone();
				subHeader.setFontColor(WHITE);
				subHeader.setText(iwrb.getLocalizedString("travel.personal_information","Personal information"));
			  table.add(subHeader,1,row);
			  table.setAlignment(1,row,"left");
			  ++row;
	
			  ++row;
			  table.add(surnameText,1,row);
			  table.add(surname,2,row);
			  table.add(lastnameText,3,row);
			  table.add(lastname,4,row);
			  table.mergeCells(4,row,6,row);
	
			  table.setAlignment(1,row,"right");
			  table.setAlignment(2,row,"left");
			  table.setAlignment(3,row,"right");
			  table.setAlignment(4,row,"left");
	
			  ++row;
			  table.add(addressText,1,row);
			  table.add(address,2,row);
			  table.add(areaCodeText,3,row);
			  table.add(areaCode,4,row);
	
			  table.setAlignment(1,row,"right");
			  table.setAlignment(2,row,"left");
			  table.setAlignment(3,row,"right");
			  table.setAlignment(4,row,"left");
			  table.mergeCells(4,row,6,row);
	
			  ++row;
			  table.add(cityText,1,row);
			  table.add(city,2,row);
			  table.add(countryText,3,row);
			  table.add(country,4,row);
	
			  table.setAlignment(1,row,"right");
			  table.setAlignment(2,row,"left");
			  table.setAlignment(3,row,"right");
			  table.setAlignment(4,row,"left");
			  table.mergeCells(4,row,6,row);
	
			  ++row;
			  table.add(emailText,1,row);
			  table.add(email,2,row);
			  table.add(telNumberText,3,row);
			  table.add(telNumber,4,row);
	
			  table.setAlignment(1,row,"right");
			  table.setAlignment(2,row,"left");
			  table.setAlignment(3,row,"right");
			  table.setAlignment(4,row,"left");
			  table.mergeCells(4,row,6,row);
	
			  ++row;
			  table.add(commentText,1,row);
			  table.add(comment,2,row);
			  table.mergeCells(2, row, 6, row);
	
			  table.setAlignment(1,row,"right");
			  table.setVerticalAlignment(1,row,"top");
			  table.setAlignment(2,row,"left");
	
				++row;
				table.mergeCells(1, row, 6, row);
				table.add(hr, 1, row);
				++row;
				table.mergeCells(1,row,6,row);
				subHeader = (Text) theBoldText.clone();
			  subHeader.setFontColor(WHITE);
			  subHeader.setText(iwrb.getLocalizedString("travel.booking_pickup_info","Pickup infomation"));
			  table.add(subHeader, 1, row);
	
				/** PICKUP/DROPOFF */ 
			  DropdownMenu pickupPlaces = null;
			  TimeInput pickupTime = null;
			  DropdownMenu dropoffPlaces = null;
			  TimeInput dropoffTime = null;
			  try {
				  Collection coll;
				  pickupPlaces = new DropdownMenu( super.parameterPickupId );
				  coll = _carRental.getPickupPlaces();
				  if (coll != null && !coll.isEmpty()) {
					  Iterator piter = coll.iterator();
					  PickupPlace p;
					  while (piter.hasNext()) {
						  p = (PickupPlace) piter.next();
						  pickupPlaces.addMenuElement(p.getPrimaryKey().toString(), p.getName());
					  }	
				  }
					pickupTime = new TimeInput( PARAMETER_PICKUP_TIME );
				  pickupTime.setHour(8);
				  pickupTime.setMinute(0);
				
				  ++row;
				  Text pickupPlaceText = (Text) theText.clone();
				  pickupPlaceText.setText(iwrb.getLocalizedString("travel.pickup_place","Pickup place"));
				  Text pickupTimeText = (Text) theText.clone();
				  pickupTimeText.setText(iwrb.getLocalizedString("travel.pickup_time","Pickup time"));
				  table.add(pickupPlaceText, 1, row);
				  table.add(pickupPlaces, 2, row);
				  table.add(pickupTimeText, 3, row);
				  table.add(pickupTime, 4, row);
				  table.setAlignment(1, row, Table.HORIZONTAL_ALIGN_RIGHT);
					table.setAlignment(3, row, Table.HORIZONTAL_ALIGN_RIGHT);

				  dropoffPlaces = new DropdownMenu( PARAMETER_DROPOFF_PLACE );
				  coll = _carRental.getDropoffPlaces();
				  if (coll != null && !coll.isEmpty()) {
					  Iterator piter = coll.iterator();
					  PickupPlace p;
					  while (piter.hasNext()) {
						  p = (PickupPlace) piter.next();
						  dropoffPlaces.addMenuElement(p.getPrimaryKey().toString(), p.getName());
					  }	
				  }
				  dropoffTime = new TimeInput( PARAMETER_DROPOFF_TIME );
				  dropoffTime.setHour(8);
				  dropoffTime.setMinute(0);
				
				  ++row;
				  Text dropoffPlaceText = (Text) theText.clone();
				  dropoffPlaceText.setText(iwrb.getLocalizedString("travel.dropoff_place","Dropoff place"));
				  Text dropoffTimeText = (Text) theText.clone();
				  dropoffTimeText.setText(iwrb.getLocalizedString("travel.dropoff_time","Dropoff time"));
				  table.add(dropoffPlaceText, 1, row);
				  table.add(dropoffPlaces, 2, row);
				  table.add(dropoffTimeText, 3, row);
				  table.add(dropoffTime, 4, row);
					table.setAlignment(1, row, Table.HORIZONTAL_ALIGN_RIGHT);
				  table.setAlignment(3, row, Table.HORIZONTAL_ALIGN_RIGHT);

			  } catch (IDORelationshipException e1) {
				  e1.printStackTrace(System.err);
			  }

	
	
			   table.add(new HiddenInput("available",Integer.toString(available)),2,row);
	
         row = addCreditCardFormElements(iwc, product, table, row, hr, star);

				if (super.getUser() != null) {
				  ++row;
				  table.mergeCells(1,row,6,row);
				  table.add(hr,1,row);
	
				  ++row;
				  List users = null;
				  if ( this.supplier != null) {
					users = getSupplierManagerBusiness(iwc).getUsersIncludingResellers(supplier);
				  }else if ( _reseller != null) {
					users = getResellerManager(iwc).getUsersIncludingSubResellers(_reseller);
				  }
				  if (users == null) users = new Vector();
	//				DropdownMenu usersDrop = new DropdownMenu(users, "ic_user");
				  DropdownMenu usersDrop = this.getDropdownMenuWithUsers(users, "ic_user");
				  usersDrop.setSelectedElement(Integer.toString(super.getUserId()));
	
				  Text tUser = (Text) theBoldText.clone();
					tUser.setFontColor(WHITE);
					tUser.setText(iwrb.getLocalizedString("travel.user","User"));
				  table.setAlignment(1,row, "right");
				  table.add(tUser, 1, row);
				  table.add(usersDrop, 2 ,row);
				}
	
				++row;
				table.mergeCells(1,row,6,row);
				table.add(hr,1,row);
	
				++row;
				if (_booking != null) {
				  table.add(new SubmitButton(iwrb.getImage("buttons/update.gif"), this.sAction, this.parameterSaveBooking),6,row);
				}else {
				  if (this._useInquiryForm) {
					table.add(new SubmitButton(iwrb.getLocalizedImageButton("travel.send_inquiry","Semd Inquiry"), this.sAction, this.parameterSaveBooking),6,row);
				  }else {
					table.add(new SubmitButton(iwrb.getImage("buttons/book.gif"), this.sAction, this.parameterSaveBooking),6,row);
				  }
				}
				table.add(new HiddenInput(this.BookingAction,this.BookingParameter),6,row);
	
				Text starTextOne = (Text) theText.clone();
				  starTextOne.setFontColor(WHITE);
				  starTextOne.setText(iwrb.getLocalizedString("travel.fields_marked_with_a_star","* Fields marked with a star must be filled."));
	
				table.mergeCells(1,row,5,row);
				table.add(starTextOne,1,row);
	//			  ++row;
	//			  table.mergeCells(1,row,5,row);
	//			  table.add(starTextTwo,1,row);
				table.setAlignment(6,row,"right");
	
	
			  }
			  else {
				table.add(notAvailSeats,1,row);
				table.add(dateText,1,row);
				table.add(pleaseFindAnotherDay,1,row);
			  }
			}else {
				table.add(notAvailSeats,1,1);
				table.add(dateText,1,1);
				table.add(pleaseFindAnotherDay,1,1);
			}
			table.setAlignment(1,1,"left");
					  //table.setBorder(1);
		return form;
  }

  public Table getVerifyBookingTableOld(IWContext iwc, Product product) throws RemoteException, SQLException, FinderException{
		String surname = iwc.getParameter("surname");
		String lastname = iwc.getParameter("lastname");
		String address = iwc.getParameter("address");
		String area_code = iwc.getParameter("area_code");
		String email = iwc.getParameter("e-mail");
		String telephoneNumber = iwc.getParameter("telephone_number");
		String city = iwc.getParameter("city");
		String country = iwc.getParameter("country");
		String comment = iwc.getParameter("comment");
	
		String fromDate = iwc.getParameter(parameterFromDate);
		String manyDays = iwc.getParameter(parameterManyDays);
	    
		String pickupId = iwc.getParameter(parameterPickupId);
		String pickupTime = iwc.getParameter(PARAMETER_PICKUP_TIME);
		String dropoffId = iwc.getParameter(PARAMETER_DROPOFF_PLACE);
		String dropoffTime = iwc.getParameter(PARAMETER_DROPOFF_TIME);
		
	
		String ccNumber = iwc.getParameter(parameterCCNumber);
		String ccMonth = iwc.getParameter(parameterCCMonth);
		String ccYear = iwc.getParameter(parameterCCYear);
	
		String inquiry = iwc.getParameter(parameterInquiry);
		int productId = product.getID();
	
		boolean valid = true;
		String errorColor = "YELLOW";
		Text star = new Text(Text.NON_BREAKING_SPACE+"*");
		  star.setFontColor(errorColor);
	
		  IWTimestamp fromStamp = new IWTimestamp(fromDate);
	
		  Collection prices = null;
		  Collection misc = null;
		  Timeframe tFrame = getProductBusiness(iwc).getTimeframe(_product, fromStamp, -1);
		  int timeframeId = -1;
		  if (tFrame != null) {
			timeframeId = tFrame.getID();
			prices = getProductPriceHome().findProductPrices(_service.getID(), timeframeId, -1, true);
			misc = getProductPriceHome().findMiscellaneousPrices(_service.getID(), timeframeId, -1, true);
		  }else {
			prices = getProductPriceHome().findProductPrices(_service.getID(), -1, -1, true);
			misc = getProductPriceHome().findMiscellaneousPrices(_service.getID(), -1, -1, true);
		  }
	  
	/*    ProductPrice[] pPrices = com.idega.block.trade.stockroom.data.ProductPriceBMPBean.getProductPrices(this.product.getID(), true);
		ProductPrice[] prices = {};
		ProductPrice[] misc = {};
		  prices = com.idega.block.trade.stockroom.data.ProductPriceBMPBean.getProductPrices(product.getID(), -1, -1, true);
		  misc = com.idega.block.trade.stockroom.data.ProductPriceBMPBean.getMiscellaneousPrices(product.getID(), -1, -1, true);
	*/
		Table table = new Table();
		  table.setCellpadding(3);
		  table.setCellspacing(3);
		  int row = 1;
	
		  table.mergeCells(1,1,2,1);
		  table.add(getBoldTextWhite(iwrb.getLocalizedString("travel.is_information_correct","Is the following information correct ?")),1,1);
	
	
		  ++row;
		  table.setAlignment(1,row,"right");
		  table.setAlignment(2,row,"left");
		  table.add(getTextWhite(iwrb.getLocalizedString("travel.name_of_trip","Name of trip")),1,row);
		  table.add(getBoldTextWhite(product.getProductName(iwc.getCurrentLocaleId())),2,row);
	
		  ++row;
		  table.setAlignment(1,row,"right");
		  table.setAlignment(2,row,"left");
	
		  try {
			int iManyDays = Integer.parseInt(manyDays);
			IWTimestamp toStamp = new IWTimestamp(fromStamp);
			if (iManyDays > 1) {
			  toStamp.addDays(iManyDays);
			  table.add(getBoldTextWhite(getLocaleDate(fromStamp)+ " - "+getLocaleDate(toStamp)),2,row);
			}else {
			  table.add(getBoldTextWhite(getLocaleDate(fromStamp)),2,row);
			}
		  }catch (NumberFormatException n) {
			table.add(star, 2,row);
		  }
		  table.add(getTextWhite(iwrb.getLocalizedString("travel.date","Date")),1,row);
	
	
		  ++row;
		  table.setAlignment(1,row,"right");
		  table.setAlignment(2,row,"left");
		  table.add(getTextWhite(iwrb.getLocalizedString("travel.name","Name")),1,row);
		  table.add(getBoldTextWhite(surname+" "+lastname),2,row);
		  if (surname.length() < 1) {
			valid = false;
			table.add(star, 2, row);
		  }
	
		  ++row;
		  table.setAlignment(1,row,"right");
		  table.setAlignment(2,row,"left");
		  table.add(getTextWhite(iwrb.getLocalizedString("travel.address","Address")),1,row);
		  table.add(getBoldTextWhite(address),2,row);
		  if (address.length() < 1) {
			valid = false;
			table.add(star, 2, row);
		  }
	
		  ++row;
		  table.setAlignment(1,row,"right");
		  table.setAlignment(2,row,"left");
		  table.add(getTextWhite(iwrb.getLocalizedString("travel.area_code","Area code")),1,row);
		  table.add(getBoldTextWhite(area_code),2,row);
		  if (area_code.length() < 1) {
			valid = false;
			table.add(star, 2, row);
		  }
	
		  ++row;
		  table.setAlignment(1,row,"right");
		  table.setAlignment(2,row,"left");
		  table.add(getTextWhite(iwrb.getLocalizedString("travel.city","City")),1,row);
		  table.add(getBoldTextWhite(city),2,row);
		  if (city.length() < 1) {
			valid = false;
			table.add(star, 2, row);
		  }
	
		  ++row;
		  table.setAlignment(1,row,"right");
		  table.setAlignment(2,row,"left");
		  table.add(getTextWhite(iwrb.getLocalizedString("travel.country","Country")),1,row);
		  table.add(getBoldTextWhite(country),2,row);
		  if (country.length() < 1) {
			valid = false;
			table.add(star, 2, row);
		  }
	
		  ++row;
		  table.setAlignment(1,row,"right");
		  table.setAlignment(2,row,"left");
		  table.add(getTextWhite(iwrb.getLocalizedString("travel.email","E-mail")),1,row);
		  table.add(getBoldTextWhite(email),2,row);
		  if (email.length() < 1) {
			valid = false;
			table.add(star, 2, row);
		  }
	
	  ++row;
	  table.setAlignment(1,row,"right");
	  table.setAlignment(2,row,"left");
	  table.add(getTextWhite(iwrb.getLocalizedString("travel.telephone_number","Telephone number")),1,row);
	  table.add(getBoldTextWhite(telephoneNumber),2,row);

		  ++row;
		  table.setAlignment(1,row,"right");
		  table.setAlignment(2,row,"left");
		  table.add(getTextWhite(iwrb.getLocalizedString("travel.pickup","Pickup")),1,row);
			
			
			try {
			  PickupPlace pickup = ((PickupPlaceHome) IDOLookup.getHome(PickupPlace.class)).findByPrimaryKey(new Integer(pickupId));
			  table.add(getBoldTextWhite(pickup.getAddress().getStreetName()),2,row);
		  }catch (Exception e) {
				e.printStackTrace(System.err);
			  valid = false;
			  table.add(star, 2, row);
			}

		  ++row;
		  table.setAlignment(1,row,"right");
		  table.setAlignment(2,row,"left");
		  table.add(getTextWhite(iwrb.getLocalizedString("travel.pickup_time","Pickup  time")),1,row);
		  try {
		  	IWTimestamp pickupStamp = new IWTimestamp(fromStamp.toSQLDateString() +" "+pickupTime);
		  	table.add(getBoldTextWhite(TextSoap.addZero(pickupStamp.getHour())+":"+TextSoap.addZero(pickupStamp.getMinute())),2,row);
		  }catch (Exception e) {
				e.printStackTrace(System.err);
				valid = false;
				table.add(star, 2, row);
		  }

		  ++row;
		  table.setAlignment(1,row,"right");
		  table.setAlignment(2,row,"left");
		  table.add(getTextWhite(iwrb.getLocalizedString("travel.dropoff","Dropoff")),1,row);
		  try {
				PickupPlace dropoff = ((PickupPlaceHome) IDOLookup.getHome(PickupPlace.class)).findByPrimaryKey(new Integer(dropoffId));
		  	table.add(getBoldTextWhite(dropoff.getAddress().getStreetName()),2,row);
		  }catch (Exception e) {
			e.printStackTrace(System.err);
		  	valid = false;
		  	table.add(star, 2, row);	
		  }
		  
		  ++row;
		  table.setAlignment(1,row,"right");
		  table.setAlignment(2,row,"left");
		  table.add(getTextWhite(iwrb.getLocalizedString("travel.dropoff_time","Dropoff  time")),1,row);
		  try {
		  	IWTimestamp dropoffStamp = new IWTimestamp(fromStamp.toSQLDateString() +" "+dropoffTime);
		  	table.add(getBoldTextWhite(TextSoap.addZero(dropoffStamp.getHour())+":"+TextSoap.addZero(dropoffStamp.getMinute())),2,row);
		  }catch (Exception e) {
		  	e.printStackTrace(System.err);
		  	valid = false;
		  	table.add(star, 2, row);	
		  }

	
	/*      ++row;
		  table.setAlignment(1,row,"right");
		  table.setAlignment(2,row,"left");
		  table.add(getTextWhite(iwrb.getLocalizedString("travel.comment","Comment")),1,row);
		  table.add(getBoldTextWhite(comment),2,row);
	*/
		  ++row;
	
		  float price = 0;
		  int total = 0;
		  int current = 0;
		  Currency currency = null;
	
		  int pricesLength = prices.size();
		  int miscLength = misc.size();
		  ProductPrice[] pPrices = new ProductPrice[pricesLength+miscLength];
		  Iterator iter = prices.iterator();
		  for (int i = 0; i < pricesLength; i++) {
			pPrices[i] = (ProductPrice) iter.next();
		  }
		  iter = misc.iterator();
		  for (int i = 0; i < miscLength; i++) {
			pPrices[i+pricesLength] = (ProductPrice) iter.next();
		  }
	
		  for (int i = 0; i < pPrices.length; i++) {
			++row;
			table.setAlignment(1,row,"right");
			table.setAlignment(2,row,"left");
	
			try {
			  if (i >= pricesLength) {
				current = Integer.parseInt(iwc.getParameter("miscPriceCategory"+((Integer)pPrices[i].getPrimaryKey()).intValue()));
			  }else {
				current = Integer.parseInt(iwc.getParameter("priceCategory"+((Integer)pPrices[i].getPrimaryKey()).intValue()));
				total += current;
			  }
			}catch (NumberFormatException n) {
			  current = 0;
			}
	
			try {
			  if (i == 0)
			  currency = ((com.idega.block.trade.data.CurrencyHome)com.idega.data.IDOLookup.getHomeLegacy(Currency.class)).findByPrimaryKeyLegacy(pPrices[i].getCurrencyId());
				price += current * getTravelStockroomBusiness(iwc).getPrice(((Integer)pPrices[i].getPrimaryKey()).intValue() ,productId,pPrices[i].getPriceCategoryID(), pPrices[i].getCurrencyId() ,IWTimestamp.getTimestampRightNow(), timeframeId, -1);
			}catch (SQLException sql) {
			}catch (NumberFormatException n) {}
	
			table.add(getTextWhite(pPrices[i].getPriceCategory().getName()),1,row);
			table.add(getBoldTextWhite(Integer.toString(current)),2,row);
		  }
	
		  ++row;
		  table.setAlignment(1,row,"right");
		  table.setAlignment(2,row,"left");
		  table.add(getTextWhite(iwrb.getLocalizedString("travel.total_passengers","Total passengers")),1,row);
		  table.add(getBoldTextWhite(Integer.toString(total)),2,row);
	
		  ++row;
		  table.setAlignment(1,row,"right");
		  table.setAlignment(2,row,"left");
		  table.add(getTextWhite(iwrb.getLocalizedString("travel.price","Price")),1,row);
		  price *= Integer.parseInt(manyDays);
		  table.add(getBoldTextWhite(df.format(price) + " "),2,row);
		  if (currency != null) {
			table.add(getBoldTextWhite(currency.getCurrencyAbbreviation()),2,row);
		  }
				if (price <= 0) {
					valid = false;
					table.add(star, 2, row);
				}
	
	
		  if (inquiry == null) {
	    		valid = insertCreditcardBookingVerification(iwc, row, table, errorColor);
	    		row += 5;
		  }else {
		  		debug("inquiry");
		  }
	
	
		  if (inquiry == null) {
			Text bookingsError = getBoldText(iwrb.getLocalizedString("travel.some_days_are_not_available","Some of the selected days are not available"));
			  bookingsError.setFontColor(errorColor);
			try {
			  BookingForm bf = getServiceHandler(iwc).getBookingForm(iwc, product);
	//			TourBookingForm tbf = new TourBookingForm(iwc, product);
			  int id = bf.checkBooking(iwc, false);
			  if (id != BookingForm.errorTooMany) {
			  }else {
				++row;
				table.mergeCells(1, row, 2, row);
				table.add(bookingsError, 1, row);
				List errorDays = bf.getErrorDays();
				Text dayText;
				if (errorDays != null) {
				  valid = false;
				  for (int i = 0; i < errorDays.size(); i++) {
					++row;
					dayText = getBoldText(getLocaleDate(((IWTimestamp) errorDays.get(i))));
					  dayText.setFontColor(errorColor);
					table.add(dayText, 2, row);
				  }
				}
	
			  }
			}catch (Exception e) {
			  valid = false;
			  table.mergeCells(1, row, 2, row);
			  table.add(bookingsError, 1, row);
			  e.printStackTrace(System.err);
			}
		  }else {
			debug("INQUIRY");
		  }
	
	//		SubmitButton yes = new SubmitButton(iwrb.getImage("buttons/yes.gif"),this.sAction, this.parameterBookingVerified);
		  SubmitButton yes = new SubmitButton(iwrb.getLocalizedString("yes","Yes"));
	//		  yes.setOnSubmit("this.form."+yes.getName()+".disabled = true");
	//		table.add("[HotelBookingForm] adding "+super.sAction+" as "+PublicBooking.parameterBookingVerified, 2, row);
	//		  yes.setOnClick("this.form.submit()");
	  //      yes.setOnClick("this.form."+yes.getName()+".disabled = true");
	
		  Link no = new Link(iwrb.getImage("buttons/no.gif"),"#");
	//			no.setAttribute("onClick","history.go(-1)");
	
		  ++row;
		  table.setAlignment(1,row,"left");
		  table.setAlignment(2,row,"right");
		  table.add(no,1,row);
		  if (valid) {
	//		  table.add(new HiddenInput(this.sAction, "Test jamms"),2,row);
			  table.add(new HiddenInput(this.sAction, PublicBooking.parameterBookingVerified),2,row);
			  table.add(new HiddenInput("Gimmi", "Test"),2,row);
			table.add(yes,2,row);
		  }
	
	
		return table;
  }
 
  public void saveServiceBooking(IWContext iwc, int bookingId, IWTimestamp stamp) throws RemoteException, IDOException {
		//System.out.println("[CarRentalBookingForm] bookingId = "+bookingId+" ..... stamp = "+stamp.toString());
		String pickupPlaceId = iwc.getParameter( super.parameterPickupId );
		String pickupTime = iwc.getParameter( PARAMETER_PICKUP_TIME );
		String dropoffPlaceId = iwc.getParameter( PARAMETER_DROPOFF_PLACE );
		String dropoffTime = iwc.getParameter( PARAMETER_DROPOFF_TIME );
		int iPickupId = -1;
		IWTimestamp pickupStamp = null;
		int iDropoffId = -1;
		IWTimestamp dropoffStamp = null;
  	
		try {
		  iPickupId = Integer.parseInt(pickupPlaceId);
		  System.out.println(stamp.toSQLDateString()+" "+pickupTime);
		  pickupStamp = new IWTimestamp(stamp.toSQLDateString()+" "+pickupTime);	
		}catch (Exception e) {
			e.printStackTrace(System.err);	
		}
		try {
		  iDropoffId = Integer.parseInt(dropoffPlaceId);
		  dropoffStamp = new IWTimestamp(stamp.toSQLDateString()+" "+dropoffTime);	
		}catch (Exception e) {
			e.printStackTrace(System.err);	
		}
		
		getCarRentalBooker(iwc).book(bookingId, iPickupId, pickupStamp, iDropoffId, dropoffStamp);		
  	
  }
 
  private CarRentalBooker getCarRentalBooker(IWApplicationContext iwac) throws RemoteException {
  		return (CarRentalBooker) IBOLookup.getServiceInstance(iwac, CarRentalBooker.class);
  }

	private CarRentalBusiness getCarRentalBusiness(IWContext iwc) throws RemoteException{
	  return (CarRentalBusiness) IBOLookup.getServiceInstance(iwc, CarRentalBusiness.class);
	}
	  
	private CarRentalHome getCarRentalHome() throws RemoteException {
	  return (CarRentalHome) IDOLookup.getHome(CarRental.class);	
	}
	
	private CarRentalBookingHome getCarRentalBookingHome() throws IDOLookupException {
		return (CarRentalBookingHome) IDOLookup.getHome(CarRentalBooking.class);	
	}
    
  private PickupPlaceHome getPickupPlaceHome() throws IDOLookupException {
  		return (PickupPlaceHome) IDOLookup.getHome(PickupPlace.class);	
  }

	public Form getFormMaintainingAllParameters( IWContext iwc,	boolean withBookingAction, boolean withSAction) throws RemoteException, FinderException {
		//System.out.println("[CarRentalBookingForm] getFormMaintainingAllParameters()");
		Form form = super.getFormMaintainingAllParameters( iwc, withBookingAction, withSAction);
		form.maintainParameter(this.PARAMETER_DROPOFF_PLACE);
		form.maintainParameter(this.PARAMETER_DROPOFF_TIME);
		form.maintainParameter(this.PARAMETER_PICKUP_TIME);
		return form;
	}

	public String getPriceCategorySearchKey() {
		return CarRentalSetup.CAR_RENTAL_SEARCH_PRICE_CATEGORY_KEY;
	}

	protected void setupSpecialFieldsForBookingForm(Table table, int row, List errorFields) {
			CarRental carRental = null;
			carRental = this._carRental;
/*			if (definedProduct != null) {
				try {
					carRental = this._carRental;
				//CarRentalHome crHome =  (CarRentalHome) IDOLookup.getHome(CarRental.class);
				//carRental = crHome.findByPrimaryKey(super.definedProduct.getPrimaryKey());
				} catch (Exception e) {
					e.printStackTrace();
				}
			}
*/
			if (carRental != null) {
				try {
					Collection picks = carRental.getPickupPlaces();
					Collection drops = carRental.getDropoffPlaces();
					DropdownMenu pickupPlaces = new DropdownMenu(parameterPickupId);
					DropdownMenu dropoffPlaces = new DropdownMenu(PARAMETER_DROPOFF_PLACE);
					TimeInput pickupTime = new TimeInput(PARAMETER_PICKUP_TIME);
					pickupTime.setName(PARAMETER_PICKUP_TIME);
					pickupTime.setHour(8);
					pickupTime.setMinute(0);
					pickupTime.keepStatusOnAction();
					TimeInput dropoffTime = new TimeInput(PARAMETER_DROPOFF_TIME);
					dropoffTime.setName(PARAMETER_DROPOFF_TIME);
					dropoffTime.setHour(8);
					dropoffTime.setMinute(0);
					dropoffTime.keepStatusOnAction();
					boolean bPick = false;
					boolean bDrop = false;
					if (picks != null && !picks.isEmpty()) {
						pickupPlaces.addMenuElements(picks);
						bPick = true;
					}
					if (drops != null && !drops.isEmpty()) {
						dropoffPlaces.addMenuElements(drops);
						bDrop = true;
					}
					/*
					if (bPick && bDrop) {
						addInputLine(new String[]{iwrb.getLocalizedString("travel.search.pickup","Pickup"), iwrb.getLocalizedString("travel.search.dropoff","Dropoff")}, new PresentationObject[]{pickupPlaces, dropoffPlaces});
					} else */
//					if (bPick) {
					String pp = iwc.getParameter(parameterPickupId);
					String dp = iwc.getParameter(PARAMETER_DROPOFF_PLACE);
					if (pp != null) { pickupPlaces.setSelectedElement(pp); }
					if (dp != null) { dropoffPlaces.setSelectedElement(dp); }
					
						if ( errorFields != null) {
							if (errorFields.contains(parameterPickupId)) {
								table.add(getErrorText("* "), 1, row);
							}
							if (errorFields.contains(PARAMETER_PICKUP_TIME)) {
								table.add(getErrorText("* "), 2, row);
							}
							if (errorFields.contains(PARAMETER_DROPOFF_PLACE)) {
								table.add(getErrorText("* "), 1, row+2);
							}
							if (errorFields.contains(PARAMETER_DROPOFF_TIME)) {
								table.add(getErrorText("* "), 2, row+2);
							}
						}
						table.add(getText(iwrb.getLocalizedString("travel.search.pickup","Pickup")), 1, row);
						table.add(getText(iwrb.getLocalizedString("travel.search.time","Time")), 2, row);
						++row;
						table.add(pickupPlaces, 1, row);
						table.add(pickupTime, 2, row);
						table.mergeCells(2, row, 3, row);
						++row;
//					} 
//					if (bDrop) {
						table.add(getText(iwrb.getLocalizedString("travel.search.dropoff","Dropoff")), 1, row);
						table.add(getText(iwrb.getLocalizedString("travel.search.time","Time")), 2, row);
						++row;
						table.add(dropoffPlaces, 1, row);
						table.add(dropoffTime, 2, row);
						table.mergeCells(2, row, 3, row);
						++row;
//					}
				} catch (Exception e) {
					e.printStackTrace();
				}
			}
	}

	public String getParameterTypeCountName() {
		return parameterCountToCheck;
	}
	
	protected int addPublicFromDateInput(IWContext iwc, Table table, int fRow) {
		table.add(getSmallText(iwrb.getLocalizedString("travel.date_of_pickup", "Date of pickup")), 1, fRow);
		table.add(new HiddenInput(parameterFromDate, _stamp.toSQLDateString()), 2, fRow);
		table.add(getOrangeText(_stamp.getLocaleDate(iwc)), 2, fRow++);
		return fRow;
	}
	
	protected int addPublicToDateInput(IWContext iwc, Table table, int fRow) {
		table.add(getSmallText(iwrb.getLocalizedString("travel.date_of_departure", "Date of departure")), 1, fRow);
		DateInput inp = new DateInput(parameterToDate);
		IWTimestamp toS = new IWTimestamp(_stamp);
		toS.addDays(1);
		inp.setDate(toS.getDate());
		table.add(getStyleObject(inp, getStyleName(BookingForm.STYLENAME_INTERFACE)), 2, fRow++);
		return fRow;
	}	
	
	public String getUnitName() {
		return iwrb.getLocalizedString("travel.car", "Car");
	}
	
	public String getUnitNamePlural() {
		return iwrb.getLocalizedString("travel.cars", "Cars");
	}
	public boolean useNumberOfDays() {
		return true;
	}

	protected int addPublicExtraBookingInput(IWContext iwc, Table table, int fRow) {
		try {
			Collection pickup = _carRental.getPickupPlaces();
			Collection dropoff = _carRental.getDropoffPlaces();
			if (pickup != null && !pickup.isEmpty()) {
				table.mergeCells(2, fRow, 4, fRow);
				table.setCellpaddingTop(1, fRow, 3);
				table.setCellpaddingBottom(1, fRow, 3);
				table.setCellpaddingLeft(1, fRow, 10);
				table.add(getSmallText(iwrb.getLocalizedString("travel.pickup", "Pickup")), 1, fRow);
				
				DropdownMenu pickDropdown = (DropdownMenu) getStyleObject(new DropdownMenu(this.parameterPickupId), BookingForm.STYLENAME_INTERFACE);
				pickDropdown.addMenuElements(pickup);

				TimeInput pickupTime = (TimeInput) getStyleObject(new TimeInput(PARAMETER_PICKUP_TIME), BookingForm.STYLENAME_INTERFACE);
				pickupTime.setName(PARAMETER_PICKUP_TIME);
				pickupTime.setHour(8);
				pickupTime.setMinute(0);
				pickupTime.keepStatusOnAction();

				table.add(pickDropdown, 2, fRow);
				table.add(Text.NON_BREAKING_SPACE, 2, fRow);
				table.add(pickupTime, 2, fRow++);

			}
			
			if (dropoff != null && !dropoff.isEmpty()) {
				table.mergeCells(2, fRow, 4, fRow);
				table.setCellpaddingTop(1, fRow, 3);
				table.setCellpaddingBottom(1, fRow, 3);
				table.setCellpaddingLeft(1, fRow, 10);
				table.add(getSmallText(iwrb.getLocalizedString("travel.dropoff", "Dropoff")), 1, fRow);
				
				DropdownMenu dropDropdown = (DropdownMenu) getStyleObject(new DropdownMenu(this.PARAMETER_DROPOFF_PLACE), BookingForm.STYLENAME_INTERFACE);
				dropDropdown.addMenuElements(dropoff);
				
				TimeInput dropoffTime = (TimeInput) getStyleObject(new TimeInput(PARAMETER_DROPOFF_TIME), BookingForm.STYLENAME_INTERFACE);
				dropoffTime.setName(PARAMETER_DROPOFF_TIME);
				dropoffTime.setHour(8);
				dropoffTime.setMinute(0);
				dropoffTime.keepStatusOnAction();

				table.add(dropDropdown, 2, fRow);
				table.add(Text.NON_BREAKING_SPACE, 2, fRow);
				table.add(dropoffTime, 2, fRow++);
			}
		}
		catch (FinderException e) {
			e.printStackTrace();
		}
		catch (IDOLookupException e) {
			e.printStackTrace();
		}
		catch (IDORelationshipException e) {
			e.printStackTrace();
		}
		return fRow;
	}

	public String getNumberOfDaysString() {
		return iwrb.getLocalizedString("travel.number_of_days", "Number of days");
	}
	
	public String getPerDayString() {
		return iwrb.getLocalizedString("travel.search.per_day","per day");
	}

}
