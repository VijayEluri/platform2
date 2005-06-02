package is.idega.idegaweb.travel.service.carrental.presentation;

import is.idega.idegaweb.travel.block.search.business.InvalidSearchException;
import is.idega.idegaweb.travel.block.search.presentation.AbstractSearchForm;
import is.idega.idegaweb.travel.service.carrental.data.CarRental;
import is.idega.idegaweb.travel.service.carrental.data.CarRentalHome;
import is.idega.idegaweb.travel.service.presentation.BookingForm;
import java.rmi.RemoteException;
import java.util.Collection;
import java.util.List;
import java.util.Vector;
import javax.ejb.FinderException;
import com.idega.data.IDOLookup;
import com.idega.data.IDORelationshipException;
import com.idega.idegaweb.IWResourceBundle;
import com.idega.presentation.IWContext;
import com.idega.presentation.Image;
import com.idega.presentation.PresentationObject;
import com.idega.presentation.Table;
import com.idega.presentation.ui.DateInput;
import com.idega.presentation.ui.DropdownMenu;
import com.idega.presentation.ui.TextInput;
import com.idega.presentation.ui.TimeInput;
import com.idega.util.IWTimestamp;

/**
 * @author gimmi
 */
public class CarRentalSearch extends AbstractSearchForm {
	
	private IWContext iwc;
	protected String PARAMETER_TYPE_COUNT = CarRentalBookingForm.parameterCountToCheck;

	private String PARAMETER_PICKUP_PLACE = CarRentalBookingForm.parameterPickupId;
	private String PARAMETER_DROPOFF_PLACE = CarRentalBookingForm.PARAMETER_DROPOFF_PLACE;
	private String PARAMETER_PICKUP_TIME = CarRentalBookingForm.PARAMETER_PICKUP_TIME;
	private String PARAMETER_DROPOFF_TIME = CarRentalBookingForm.PARAMETER_DROPOFF_TIME;

  //private String PARAMETER_PICKUP_ID = CarRentalBookingForm.parameterPickupId;
  //private String PARAMETER_PICKUP_INF = CarRentalBookingForm.parameterPickupInf;
	
	
	public CarRentalSearch() {
		super();
	}

	public void main(IWContext iwc) throws Exception {
		this.iwc = iwc;
		super.main(iwc);
	}

	protected String getServiceName(IWResourceBundle iwrb) {
		return iwrb.getLocalizedString("travel.search.car_rental","Car Rental");		
	}
		
	protected void setupSearchForm() throws RemoteException {
		BookingForm bf = getBookingForm();
		boolean defined = hasDefinedProduct();
		
		if (!defined) {
			try {
				bf.addAreaCodeInput(null, engine.getCountries(), isVertical(), getDefaultValue(PARAMETER_POSTAL_CODE_NAME));
			}
			catch (IDORelationshipException e1) {
				e1.printStackTrace();
			}
			bf.addSupplierNameInput(isVertical());
		}
		
		IWTimestamp now = IWTimestamp.RightNow();
		IWTimestamp tomorrow = new IWTimestamp(now);
		tomorrow.addDays(1);
		
		DateInput fromDate = new DateInput(PARAMETER_FROM_DATE, true);
		fromDate.setDate(now.getDate());
		now.addDays(1);

		DateInput toDate = new DateInput(PARAMETER_TO_DATE, true);
		toDate.setDate(now.getDate());

		TextInput manyDays = new TextInput(PARAMETER_MANY_DAYS);
		manyDays.setContent("1");
		manyDays.setSize(3);
		manyDays.setAsPositiveIntegers(iwrb.getLocalizedString("travel.search.invalid_number_of_days", "Invalid number of days"));
//		addInputLine(new String[]{iwrb.getLocalizedString("travel.search.pickup","Pickup"), iwrb.getLocalizedString("travel.search.no_days","Number of days")}, new PresentationObject[]{fromDate, manyDays});
		bf.addInputLine(new String[]{iwrb.getLocalizedString("travel.search.pickup","Pickup")}, new PresentationObject[]{fromDate}, false, true, isVertical());
		bf.addInputLine(new String[]{iwrb.getLocalizedString("travel.search.dropoff","Drop off")}, new PresentationObject[]{toDate}, false, false, isVertical());
	}

	protected Collection getResults() throws RemoteException, InvalidSearchException {
		String supplierName = iwc.getParameter(PARAMETER_SUPPLIER_NAME);
		if (supplierName != null) {
			supplierName = supplierName.trim();
		}
		try {
			Collection postalCodes = getBookingForm().getPostalCodeIds(iwc);
			Object[] suppIds = getSupplierIDs();
			CarRentalHome crHome = (CarRentalHome) IDOLookup.getHome(CarRental.class);

			Collection coll = new Vector();
			if (suppIds.length > 0) {
//			coll = hHome.find(null, null, roomTypeIds, postalCodeIds, suppIds);
				coll = crHome.find(null, null, postalCodes, suppIds, supplierName);
			}
			return coll;
			//handleResults(coll);
		} catch (FinderException e) {
			e.printStackTrace();
		}
		return null;
	}

	protected Image getHeaderImage(IWResourceBundle iwrb) {
		return iwrb.getImage("/search/carrental.png");
	}

	protected String getPriceCategoryKey() {
		return CarRentalSetup.CAR_RENTAL_SEARCH_PRICE_CATEGORY_KEY;
	}

	protected String getParameterTypeCountName() {
		return PARAMETER_TYPE_COUNT;
		//return PARAMETER_TO_DATE;
	}

	protected int getCount() {
		//int count = Integer.parseInt(sCount);
		//return count;
		return 1;
	}

	protected void setupSpecialFieldsForBookingForm(Table table, int row, List errorFields) {
		CarRental carRental = null;
		if (definedProduct != null) {
			try {
			CarRentalHome crHome =  (CarRentalHome) IDOLookup.getHome(CarRental.class);
			carRental = crHome.findByPrimaryKey(super.definedProduct.getPrimaryKey());
			} catch (Exception e) {
				e.printStackTrace();
			}
		}
		if (carRental != null) {
			try {
				Collection picks = carRental.getPickupPlaces();
				Collection drops = carRental.getDropoffPlaces();
				DropdownMenu pickupPlaces = new DropdownMenu(PARAMETER_PICKUP_PLACE);
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
//				if (bPick) {
				String pp = iwc.getParameter(PARAMETER_PICKUP_PLACE);
				String dp = iwc.getParameter(PARAMETER_DROPOFF_PLACE);
				if (pp != null) { pickupPlaces.setSelectedElement(pp); }
				if (dp != null) { dropoffPlaces.setSelectedElement(dp); }
					Table localTable = new Table();
					localTable.setCellpaddingAndCellspacing(0);					
					table.add(localTable, 1, row++);
					table.setCellpaddingLeft(1, row, 10);
					table.setCellpaddingBottom(1, row, 9);
					int localRow = 1;
					if ( errorFields != null) {
						if (errorFields.contains(PARAMETER_PICKUP_PLACE)) {
							localTable.add(getErrorText("* "), 1, localRow);
						}
						if (errorFields.contains(PARAMETER_PICKUP_TIME)) {
							localTable.add(getErrorText("* "), 2, localRow);
						}
						if (errorFields.contains(PARAMETER_DROPOFF_PLACE)) {
							localTable.add(getErrorText("* "), 1, localRow+2);
						}
						if (errorFields.contains(PARAMETER_DROPOFF_TIME)) {
							localTable.add(getErrorText("* "), 2, localRow+2);
						}
					}
					
					localTable.setCellpaddingLeft(1, localRow, 10);
					localTable.setCellpaddingBottom(1, localRow, 5);
					localTable.setCellpaddingLeft(2, localRow, 10);
					localTable.setCellpaddingBottom(2, localRow, 5);
					localTable.add(getText(iwrb.getLocalizedString("travel.search.pickup","Pickup")), 1, localRow);
					localTable.add(getText(iwrb.getLocalizedString("travel.search.time","Time")), 2, localRow);
					++localRow;
					localTable.setCellpaddingLeft(1, localRow, 10);
					localTable.setCellpaddingBottom(1, localRow, 5);
					localTable.setCellpaddingLeft(2, localRow, 10);
					localTable.setCellpaddingBottom(2, localRow, 5);
					localTable.add(pickupPlaces, 1, localRow);
					localTable.add(pickupTime, 2, localRow);
					localTable.mergeCells(2, localRow, 3, localRow);
					++localRow;
//				} 
//				if (bDrop) {
					localTable.setCellpaddingLeft(1, localRow, 10);
					localTable.setCellpaddingBottom(1, localRow, 5);
					localTable.setCellpaddingLeft(2, localRow, 10);
					localTable.setCellpaddingBottom(2, localRow, 5);
					localTable.add(getText(iwrb.getLocalizedString("travel.search.dropoff","Dropoff")), 1, localRow);
					localTable.add(getText(iwrb.getLocalizedString("travel.search.time","Time")), 2, localRow);
					++localRow;
					localTable.setCellpaddingLeft(1, localRow, 10);
					localTable.setCellpaddingBottom(1, localRow, 5);
					localTable.setCellpaddingLeft(2, localRow, 10);
					localTable.setCellpaddingBottom(2, localRow, 5);
					localTable.add(dropoffPlaces, 1, localRow);
					localTable.add(dropoffTime, 2, localRow);
					localTable.mergeCells(2, localRow, 3, localRow);
					++localRow;
//				}
			} catch (Exception e) {
				e.printStackTrace();
			}
		}
	}

	/* (non-Javadoc)
	 * @see is.idega.idegaweb.travel.block.search.presentation.AbstractSearchForm#getBookingFieldsToErrorCheck()
	 */
	protected List getErrorFormFields() {
		List tmp = new Vector();
		
		String pp = iwc.getParameter(PARAMETER_PICKUP_PLACE);
		String pt = iwc.getParameter(PARAMETER_PICKUP_TIME);
		String dp = iwc.getParameter(PARAMETER_DROPOFF_PLACE);
		String dt = iwc.getParameter(PARAMETER_DROPOFF_TIME);
		
		if (pp == null || pp.equals("")) {
			tmp.add(PARAMETER_PICKUP_PLACE);
		}
		if (dp == null || dp.equals("")) {
			tmp.add(PARAMETER_DROPOFF_PLACE);
		}
		IWTimestamp stamp = new IWTimestamp(iwc.getParameter(PARAMETER_FROM_DATE));
		try {
			new IWTimestamp(stamp.toSQLDateString()+" "+pt);
		} catch (Exception e) {
			tmp.add(PARAMETER_PICKUP_TIME);
		}
		
		try {
			new IWTimestamp(stamp.toSQLDateString()+" "+dt);
		} catch (Exception e) {
			tmp.add(PARAMETER_DROPOFF_TIME);
		}
		
		return tmp;
		/*
		if (extraFieldsToCheck != null) {
			for (int i = 0; i < extraFieldsToCheck.length; i++) {
				if (extraFieldsToCheck[i] == null || extraFieldsToCheck[i].equals("")) {
					list.add(extraFieldsToCheck[i]);
				}
			}
		}		return new String[] {PARAMETER_PICKUP_PLACE, PARAMETER_DROPOFF_PLACE, PARAMETER_PICKUP_TIME, PARAMETER_DROPOFF_TIME};
		*/
	}

	/* (non-Javadoc)
	 * @see is.idega.idegaweb.travel.block.search.presentation.AbstractSearchForm#getParametersInUse()
	 */
	protected Collection getParametersInUse() {
		Vector coll = new Vector();
		coll.add(PARAMETER_DROPOFF_PLACE);
		coll.add(PARAMETER_DROPOFF_TIME);
		coll.add(PARAMETER_PICKUP_PLACE);
		coll.add(PARAMETER_PICKUP_TIME);
		coll.add(PARAMETER_TYPE_COUNT);
		return coll;
	}
	
}
