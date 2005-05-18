package is.idega.idegaweb.travel.service.tour.presentation;

import is.idega.idegaweb.travel.block.search.business.InvalidSearchException;
import is.idega.idegaweb.travel.block.search.presentation.AbstractSearchForm;
import is.idega.idegaweb.travel.service.presentation.BookingForm;
import is.idega.idegaweb.travel.service.tour.business.TourBusiness;
import is.idega.idegaweb.travel.service.tour.data.Tour;
import is.idega.idegaweb.travel.service.tour.data.TourHome;
import is.idega.idegaweb.travel.service.tour.data.TourType;
import is.idega.idegaweb.travel.service.tour.data.TourTypeHome;
import java.rmi.RemoteException;
import java.util.Collection;
import java.util.Iterator;
import java.util.Vector;
import javax.ejb.FinderException;
import com.idega.block.trade.stockroom.business.ProductComparator;
import com.idega.block.trade.stockroom.data.Product;
import com.idega.business.IBOLookup;
import com.idega.business.IBOLookupException;
import com.idega.business.IBORuntimeException;
import com.idega.data.IDOLookup;
import com.idega.data.IDOLookupException;
import com.idega.data.IDORelationshipException;
import com.idega.idegaweb.IWApplicationContext;
import com.idega.idegaweb.IWResourceBundle;
import com.idega.presentation.IWContext;
import com.idega.presentation.Image;
import com.idega.presentation.PresentationObject;
import com.idega.presentation.ui.DateInput;
import com.idega.presentation.ui.SelectPanel;
import com.idega.presentation.ui.TextInput;
import com.idega.presentation.ui.util.SelectorUtility;
import com.idega.util.IWTimestamp;

/**
 * @author gimmi
 */
public abstract class TourSearch extends AbstractSearchForm {

	IWContext iwc;
	private static String PARAMETER_TOUR_TYPE_ID = "ts_prm_tti";
	private static String PARAMETER_MANY_SEATS = BookingForm.parameterCountToCheck;//"ts_prm_ms";
	
	protected abstract String getTourCategory();
	
	public TourSearch() {
		super();
	}
	
	public void main(IWContext iwc) throws Exception {
		this.iwc = iwc;
		super.main(iwc);
	}

	protected int getDefaultSortMethod() {
		return ProductComparator.NAME;
	}

	protected void setupSearchForm() throws RemoteException {
		BookingForm bf = getBookingForm();
		boolean defined = hasDefinedProduct();
		
		
		IWTimestamp now = IWTimestamp.RightNow();
		
		DateInput fromDate = new DateInput(PARAMETER_FROM_DATE, true);
		fromDate.setDate(now.getDate());
		now.addDays(1);
		TextInput manySeats = new TextInput(PARAMETER_MANY_SEATS);
		manySeats.setContent("1");
		manySeats.setSize(3);
		manySeats.setAsPositiveIntegers(iwrb.getLocalizedString("travel.search.invalid_number_of_seats", "Invalid number of seats"));
		
		SelectPanel tourTypes = new SelectPanel(PARAMETER_TOUR_TYPE_ID );
		try {
			tourTypes.setHeight("60");
			Collection categories = getTourTypeHome().findByCategory(getTourCategory());
			SelectorUtility su = new SelectorUtility();
			tourTypes = (SelectPanel) su.getSelectorFromIDOEntities(tourTypes, categories, "getLocalizationKey", iwrb);

			if (super.hasDefinedProduct()) {
				Collection coll = getTourTypes(definedProduct);
				if (coll != null && !coll.isEmpty()) {
					TourType tourType;
					Iterator iter = coll.iterator();
					while (iter.hasNext()) {
						tourType = (TourType) iter.next();
						tourTypes.setSelectedOption(tourType.getPrimaryKey().toString());
					}
				}
				tourTypes.setDisabled(true);
			}
			
		} catch (Exception e) {
			e.printStackTrace(System.err);
		}
		tourTypes.setAsNotEmpty(iwrb.getLocalizedString("travel.search.tour_type_must_be_selected", "Tour type must be selected."));

		if (!defined) {
			try {
				bf.addAreaCodeInput(null, engine.getCountries(), isVertical());
			}
			catch (IDORelationshipException e1) {
				e1.printStackTrace();
			}
			bf.addSupplierNameInput(isVertical());
		}
		bf.addInputLine(new String[]{iwrb.getLocalizedString("travel.search.date","Date")}, new PresentationObject[]{fromDate}, false, isVertical(), isVertical());
		bf.addInputLine(new String[]{iwrb.getLocalizedString("travel.search.number_of_seats","Number of seats")}, new PresentationObject[]{manySeats}, false, true, isVertical());
		bf.addInputLine(new String[]{iwrb.getLocalizedString("travel.search.type_of","Type of ")+getTourCategory().toLowerCase()}, new PresentationObject[]{tourTypes}, isVertical());
	
	}

	private TourTypeHome getTourTypeHome() throws IDOLookupException {
		return (TourTypeHome) IDOLookup.getHome(TourType.class);
	}

	protected String getParameterTypeCountName() {
		return PARAMETER_MANY_SEATS;
	}

	protected Collection getResults() throws RemoteException, InvalidSearchException {
		String sManySeats = iwc.getParameter(PARAMETER_MANY_SEATS);
		String sTourType[] = iwc.getParameterValues(PARAMETER_TOUR_TYPE_ID);
		String supplierName = iwc.getParameter(PARAMETER_SUPPLIER_NAME);
		if (supplierName != null) {
			supplierName = supplierName.trim();
		}
		
		InvalidSearchException ie = null;
		try {
			try {
				int tmp = Integer.parseInt(sManySeats);
				if (tmp < 1) {
					if (ie == null) {
						ie = new InvalidSearchException("Error in search");
						ie.addErrorField(PARAMETER_MANY_SEATS);
					}
				}
			} catch (Exception e) {
				if (ie == null) {
					ie = new InvalidSearchException("Error in search");
					ie.addErrorField(PARAMETER_MANY_SEATS);
				}
			}
			
			Object[] tourTypeIds = null;
			if (sTourType != null && sTourType.length > 0) {
				tourTypeIds = new Object[sTourType.length];
				for (int i = 0; i < tourTypeIds.length; i++) {
					//System.out.println("Adding roomtype to array roomTypeIds["+i+"] = "+roomTypeIds[i]+" ... "+i+" of "+sRoomType.length);
					tourTypeIds[i] = sTourType[i];
				}
			} else {
				if (ie == null) {
					ie = new InvalidSearchException("Error in search");
				}
				ie.addErrorField(PARAMETER_TOUR_TYPE_ID);
			}

			Collection postalCodes = getBookingForm().getPostalCodeIds(iwc);
			Object[] suppIds = getSupplierIDs();

			TourHome tHome = (TourHome) IDOLookup.getHome(Tour.class);

			if (ie != null) {
				throw ie;
			}

			Collection coll = new Vector();
			
			if (suppIds.length > 0) {
				coll = tHome.find(null, null, tourTypeIds, postalCodes, suppIds, supplierName);
			}
			
			return coll;
			//handleResults(coll);
		} catch (IDOLookupException e) {
			e.printStackTrace();
		} catch (FinderException e) {
			e.printStackTrace();
		}
		return null;
	}

	protected int getNumberOfDays(IWTimestamp fromDate) {
		return 1;
	}

	protected Image getHeaderImage(IWResourceBundle iwrb) {
		return iwrb.getImage("/search/tour.png");
	}

	protected String getPriceCategoryKey() {
		return TourSetup.TOUR_SEARCH_PRICE_CATEGORY_KEY;
	}
	
	protected TourBusiness getTourBusiness(IWApplicationContext iwac) {
		try {
			return (TourBusiness) IBOLookup.getServiceInstance(iwac, TourBusiness.class);
		} catch (IBOLookupException e) {
			throw new IBORuntimeException(e);
		}
	}
	
	protected Collection getTourTypes(Product product) {
		try {
			Tour tour = getTourBusiness(iwc).getTour(product);
			return tour.getTourTypes();
		} catch (Exception e) {
			return null;
		}
	}
	
	protected Collection getParametersInUse() {
		Vector coll = new Vector();
		coll.add(PARAMETER_MANY_SEATS);
		coll.add(PARAMETER_TOUR_TYPE_ID);
		return coll;
	}
	
	
}
