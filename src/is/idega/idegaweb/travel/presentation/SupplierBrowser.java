/*
 * $Id: SupplierBrowser.java,v 1.38 2005/11/03 16:56:42 gimmi Exp $
 * Created on 19.5.2005
 *
 * Copyright (C) 2005 Idega Software hf. All Rights Reserved.
 *
 * This software is the proprietary information of Idega hf.
 * Use is subject to license terms.
 */
package is.idega.idegaweb.travel.presentation;

import is.idega.idegaweb.travel.block.search.business.SearchEventListener;
import is.idega.idegaweb.travel.block.search.business.ServiceSearchSession;
import is.idega.idegaweb.travel.block.search.presentation.AbstractSearchForm;
import is.idega.idegaweb.travel.data.PickupPlace;
import is.idega.idegaweb.travel.data.PickupPlaceHome;
import is.idega.idegaweb.travel.service.presentation.BookingForm;
import java.rmi.RemoteException;
import java.sql.SQLException;
import java.text.DecimalFormat;
import java.util.Collection;
import java.util.Iterator;
import java.util.StringTokenizer;
import javax.ejb.EJBException;
import javax.ejb.FinderException;
import com.idega.block.text.business.ContentFinder;
import com.idega.block.text.business.ContentHelper;
import com.idega.block.text.data.TxText;
import com.idega.block.trade.stockroom.business.ProductPriceException;
import com.idega.block.trade.stockroom.data.Product;
import com.idega.block.trade.stockroom.data.ProductHome;
import com.idega.block.trade.stockroom.data.ProductPrice;
import com.idega.block.trade.stockroom.data.ProductPriceHome;
import com.idega.block.trade.stockroom.data.Supplier;
import com.idega.block.trade.stockroom.data.SupplierHome;
import com.idega.block.trade.stockroom.data.Timeframe;
import com.idega.block.trade.stockroom.data.TravelAddress;
import com.idega.business.IBOLookup;
import com.idega.business.IBOLookupException;
import com.idega.business.IBORuntimeException;
import com.idega.core.builder.data.ICPage;
import com.idega.core.file.data.ICFile;
import com.idega.data.IDOCompositePrimaryKeyException;
import com.idega.data.IDOLookup;
import com.idega.data.IDOLookupException;
import com.idega.data.IDORelationshipException;
import com.idega.data.IDORuntimeException;
import com.idega.idegaweb.IWMainApplication;
import com.idega.idegaweb.IWUserContext;
import com.idega.presentation.IWContext;
import com.idega.presentation.Image;
import com.idega.presentation.PresentationObject;
import com.idega.presentation.Table;
import com.idega.presentation.text.Link;
import com.idega.presentation.text.Text;
import com.idega.presentation.ui.BackButton;
import com.idega.presentation.ui.DatePicker;
import com.idega.presentation.ui.DropdownMenu;
import com.idega.presentation.ui.Form;
import com.idega.presentation.ui.HiddenInput;
import com.idega.presentation.ui.InterfaceObject;
import com.idega.presentation.ui.ResultOutput;
import com.idega.presentation.ui.SubmitButton;
import com.idega.presentation.ui.TextArea;
import com.idega.presentation.ui.TextInput;
import com.idega.user.data.Group;
import com.idega.user.data.GroupHome;
import com.idega.util.IWTimestamp;


public class SupplierBrowser extends TravelBlock {

	static final String SHOW_SEARCH_INPUTS = "sb_p_ssi";
	static final String ACTION = "sb_a";
	private static final String ACTION_VIEW_SUPPLIERS = "sb_a_vs";
	private static final String ACTION_VIEW_PRODUCTS = "sb_a_vp";
	private static final String ACTION_VIEW_DETAILS = "sb_a_vd";
	static final String ACTION_BOOKING_FORM = "sb_a_bf";
	private static final String PARAMETER_SUPPLIER_MANAGER = "sb_sm";
	
	public static final String PARAMETER_POSTAL_CODES = "sb_pc";
	private static final String PARAMETER_SUPPLIER_ID = "sb_sid";
	private static final String PARAMETER_PRODUCT_ID = AbstractSearchForm.PARAMETER_PRODUCT_ID;
	public static final String PARAMETER_FROM = AbstractSearchForm.PARAMETER_FROM_DATE;
	public static final String PARAMETER_TO = AbstractSearchForm.PARAMETER_TO_DATE;
	public static final String PARAMETER_NUMBER_OF_DAYS = AbstractSearchForm.PARAMETER_MANY_DAYS;
	public static final String PARAMETER_SUPPLIER_NAME = AbstractSearchForm.PARAMETER_SUPPLIER_NAME;
	public static final String PARAMETER_NUMBER_OF_UNITS = "sb_nou";
	
	private static final String PARAMETER_PLUGIN = "sb_pp";
	private static final String PARAMETER_SHOW_PRICES = "sb_par_show_p";
	private static final String PARAMETER_HEADER_STYLE_CLASS = "sb_par_head_styleClass";
	private static final String PARAMETER_LINK_STYLE_CLASS = "sb_par_link_styleClass";
	private static final String PARAMETER_TEXT_STYLE_CLASS = "sb_par_text_styleClass";
	private static final String PARAMETER_USE_ONLINE_PRICES = "sb_P_uop";

	protected DecimalFormat df = new DecimalFormat("#,###");
	
	private String[][] postalCodes = null;
	private Group supplierManager = null;
	private Product product = null;
	private int supplierManagerId = -1;
	
	private SupplierBrowserPlugin plugin = null;
	private String pluginClassName = null;
	private BookingForm bookingForm = null;
	
	private IWTimestamp from = null;
	private int numberOfDays = 1;
	private String textStyleClass = null;
	private String headerStyleClass = null;
	private String linkStyleClass = null;
	private String imageStyleClass = null;
	private String interfaceObjectStyleClass = null;
	private String width = null;
	private boolean useOnlinePrices = false;
	private boolean useSearchPriceCategoryKey = false;
	private String imageWidth = "70";
	private boolean useBasket = false;
	private boolean useTravelLook = false;
	private boolean showInputs = true;
	private boolean showPriceWithProductInformation = true;
	private ICPage basketPage = null;
	private int numberOfCharactersBeforeMoreButton = 300;
	private int spaceBetweenItems = 0;
	private boolean unitQuantityUsed = false;
	public SupplierBrowser() {
		
	}
	
	public static void main(String[] args) {
	}
	/**
	 * Use this to initialize the SupplierBrowser if it is not added to a page, 
	 * but methods used.
	 */
	public void initialize(IWContext iwc) throws RemoteException {
		super.initializer(iwc);
		init(iwc);
	}
	
	public void main(IWContext iwc) throws Exception {
		super.initializer(iwc);
		init(iwc);
		
		Form form = new Form();

		if (plugin == null) {
			form.add(getText(getResourceBundle().getLocalizedString("plugin_not_defined", "Plugin not defined")));
		} else if (supplierManager == null) {
			form.add(getText(getResourceBundle().getLocalizedString("supplier_manager_not_defined", "SupplierManager not defined")));
		} else {
			form.maintainParameter(PARAMETER_POSTAL_CODES);
			form.maintainParameter(PARAMETER_SUPPLIER_MANAGER);
			form.maintainParameter(PARAMETER_SUPPLIER_ID);
			form.maintainParameter(PARAMETER_PRODUCT_ID);
			form.maintainParameter(PARAMETER_FROM);
			form.maintainParameter(PARAMETER_NUMBER_OF_DAYS);
			form.maintainParameter(SHOW_SEARCH_INPUTS);
			form.maintainParameter(PARAMETER_NUMBER_OF_UNITS);
			String[] params = plugin.getParameters();
			if (params != null) {
				for (int i = 0; i < params.length; i++) {
					form.maintainParameter(params[i]);
				}
			}

			String action = iwc.getParameter(ACTION);
			if (action == null || action.equals("")) {
				action = ACTION_VIEW_SUPPLIERS;
			}
			if (action.equals(ACTION_VIEW_SUPPLIERS)) {
				if (plugin.displaySupplierResults()) {
					listSuppliers(iwc, form);
				} else {
					listProducts(iwc, form);
				}
			} else if (action.equals(ACTION_VIEW_PRODUCTS)) {
				listProducts(iwc, form);
			} else if (action.equals(ACTION_VIEW_DETAILS)) {
				viewDetails(iwc, form);
			} else if (action.equals(ACTION_BOOKING_FORM)) {
				bookingForm(iwc, form);
			}
		}
		
		add(form);
	}
	
	private void addParametersToLink(IWContext iwc, Link link) {
		link.maintainParameter(PARAMETER_POSTAL_CODES, iwc);
		link.maintainParameter(PARAMETER_SUPPLIER_MANAGER, iwc);
		link.maintainParameter(PARAMETER_SUPPLIER_ID, iwc);
		link.maintainParameter(PARAMETER_FROM, iwc);
		link.maintainParameter(PARAMETER_NUMBER_OF_DAYS, iwc);
		link.maintainParameter(PARAMETER_NUMBER_OF_UNITS, iwc);
		link.addParameter(SHOW_SEARCH_INPUTS, Boolean.toString(showInputs));
		String[] params = plugin.getParameters();
		if (params != null) {
			for (int i = 0; i < params.length; i++) {
				link.maintainParameter(params[i], iwc);
			}
		}
	}
	
	private void init(IWContext iwc) {
		// SupplierManager check
		String suppMan = iwc.getParameter(PARAMETER_SUPPLIER_MANAGER);
		if (supplierManagerId > 0) {
			suppMan = Integer.toString(supplierManagerId);
		}
		if (suppMan != null && supplierManager == null) {
			try {
				GroupHome gHome = (GroupHome) IDOLookup.getHome(Group.class, super.getDatasource());
				supplierManager = gHome.findByPrimaryKey(new Integer(suppMan));
			}
			catch (IDOLookupException e) {
				e.printStackTrace();
			}
			catch (NumberFormatException e) {
				e.printStackTrace();
			}
			catch (FinderException e) {
				e.printStackTrace();
			}
		}
		String showSI = iwc.getParameter(SHOW_SEARCH_INPUTS);
		if (showSI != null) {
			showInputs = showSI.equalsIgnoreCase("true");
		}
		
		// Checking the postal codes
		String pcs = iwc.getParameter(PARAMETER_POSTAL_CODES);
		postalCodes = new String[2][0];
		if (pcs != null) {
			StringTokenizer tok = new StringTokenizer(pcs, ",");
			postalCodes = new String[2][tok.countTokens()];
			int i = 0;
			while (tok.hasMoreElements()) {
				String token = tok.nextToken().trim();
				StringTokenizer dashTok = new StringTokenizer(token, "-");
				String first = dashTok.nextToken().trim();
				String second = first;
				if (dashTok.hasMoreTokens()) {
					second = dashTok.nextToken().trim();
				}
				postalCodes[0][i] = first;
				postalCodes[1][i] = second;
				++i;
			}
			
		}
		if (pluginClassName == null) {
			String sPlugin = iwc.getParameter(PARAMETER_PLUGIN);
			pluginClassName = IWMainApplication.decryptClassName(sPlugin);
		}
		
		if (pluginClassName != null) {
			try {
				plugin = (SupplierBrowserPlugin) Class.forName(pluginClassName).newInstance();
			}
			catch (Exception e) {
				e.printStackTrace();
			}
		}
		
		String pId = iwc.getParameter(PARAMETER_PRODUCT_ID);
		if (pId != null) {
			try {
				ProductHome pHome = (ProductHome) IDOLookup.getHome(Product.class, getDatasource());
				product = pHome.findByPrimaryKey(new Integer(pId));
				bookingForm = getServiceHandler(iwc).getBookingForm(iwc, product, false);
			}
			catch (IDOLookupException e) {
				e.printStackTrace();
			}
			catch (FinderException e) {
				e.printStackTrace();
			}
			catch (Exception e) {
				e.printStackTrace();
			}
		} else {
			try {
				bookingForm = getServiceHandler(iwc).getBookingForm(iwc, null, false);
			}
			catch (Exception e) {
				e.printStackTrace();
			}
		}
		String sFrom = iwc.getParameter(PARAMETER_FROM);
		from = null;
		if (sFrom != null) {
			from = new IWTimestamp(sFrom);
		}
		numberOfDays = 1;
		try {
			IWTimestamp to = null;
			if (iwc.isParameterSet(PARAMETER_TO)) {
				to = new  IWTimestamp(iwc.getParameter(PARAMETER_TO));
				numberOfDays = bookingForm.getNumberOfDays(from, to);
			} else if (iwc.isParameterSet(PARAMETER_NUMBER_OF_DAYS)) {
				numberOfDays = Integer.parseInt(iwc.getParameter(PARAMETER_NUMBER_OF_DAYS));
			}
		} catch (Exception e) {
			e.printStackTrace();
		}

		// Setting parameters
		if (iwc.isParameterSet(PARAMETER_SHOW_PRICES)) {
			setShowPriceWithProductInformation(Boolean.valueOf(iwc.getParameter(PARAMETER_SHOW_PRICES)).booleanValue());
		}
		if (iwc.isParameterSet(PARAMETER_HEADER_STYLE_CLASS)) {
			setHeaderStyleClass(iwc.getParameter(PARAMETER_HEADER_STYLE_CLASS));
		}
		if (iwc.isParameterSet(PARAMETER_LINK_STYLE_CLASS)) {
			setLinkStyleClass(iwc.getParameter(PARAMETER_LINK_STYLE_CLASS));
		}
		if (iwc.isParameterSet(PARAMETER_TEXT_STYLE_CLASS)) {
			setTextStyleClass(iwc.getParameter(PARAMETER_TEXT_STYLE_CLASS));
		}
		if (iwc.isParameterSet(PARAMETER_USE_ONLINE_PRICES)) {
			setUseOnlinePrices(Boolean.valueOf(iwc.getParameter(PARAMETER_USE_ONLINE_PRICES)).booleanValue());
		}

	}
	
	private void bookingForm(IWContext iwc, Form form) {
		Table table = new Table();
		if (width != null) {
			table.setWidth(width);
		}
		
		table.add(getText("bookingForm"));
		
		form.add(table);
	}
	
	private void viewDetails(IWContext iwc, Form form) throws RemoteException {
		Table table = new Table();
		if (useTravelLook) {
			table = TravelManager.getTable();
		}
		if (width != null) {
			table.setWidth(width);
		}
		table.setColumnWidth(1, imageWidth);
		table.setBorder(0);
		Collection[] inputs = plugin.getProductSearchInputs(iwc, super.getResourceBundle());
		Table searchTable = getSearchFrom(inputs, ACTION_VIEW_PRODUCTS);
		if (searchTable != null) {
			form.add(searchTable);
		} else {
			form.addParameter(ACTION, "");
		}

		int row = 1;
		
		boolean successfulAdd = getSearchSession(iwc).getAddToBasketSuccess() && iwc.isParameterSet(AbstractSearchForm.ACTION);
		
		if (successfulAdd) {
			Table succtable = new Table();
			if (useTravelLook) {
				succtable = TravelManager.getTable();
			}
			if (width != null) {
				succtable.setWidth(width);
			}
			if (useTravelLook) {
				succtable.setRowColor(1, TravelManager.GRAY);
			}
			succtable.setAlignment(1, 1, Table.HORIZONTAL_ALIGN_CENTER);
			succtable.add(getText(getResourceBundle().getLocalizedString("travel.added_successfully_to_basket", "Added successfully to basket"), headerStyleClass), 1, 1);
//			link.setText(getText(getResourceBundle().getLocalizedString("travel.add_another_to_basket", "Add another to basket"), linkStyleClass));
			form.add(succtable);
		} else if (iwc.isParameterSet(AbstractSearchForm.ACTION)) {
			Table succtable = new Table();
			if (width != null) {
				succtable.setWidth(width);
			}
			if (useTravelLook) {
				succtable.setRowColor(1, TravelManager.GRAY);
			}
			succtable.setAlignment(1, 1, Table.HORIZONTAL_ALIGN_CENTER);
			succtable.add(getErrorText(getResourceBundle().getLocalizedString("travel.failed_adding_to_basket", "Failed adding to basket")+", "+getSearchSession(iwc).getAddToBasketError(getResourceBundle())), 1, 1);
//			linkTable.add(link, 2, 1);
			form.add(succtable);

		}

		if (useTravelLook) {
			table.mergeCells(1, row, 3, row);
			table.setRowColor(row, TravelManager.backgroundColor);
			table.add(TravelManager.getHeaderText(getResourceBundle().getLocalizedString("travel.product_details", "Product details")), 1, row++);
		}
		row = addProductInfo(iwc, table, row, iwc.getCurrentLocaleId(), product, false);
		table.mergeCells(1, row, 3, row);
		
		
		Table priceTable = new Table();
		if (useTravelLook) {
			priceTable = TravelManager.getTable();
		}
//		table.add(priceTable, 1, row);
		if (width != null) {
			priceTable.setWidth(width);
		}
		priceTable.setBorder(0);
		
		int pRow = 1;
		if (iwc.isLoggedOn()) {
			priceTable.add(new HiddenInput("ic_user", iwc.getCurrentUser().getPrimaryKey().toString()), 1, pRow);
		}
		priceTable.add(new HiddenInput(BookingForm.parameterOnlineBooking, Boolean.toString(useOnlinePrices)), 1, pRow);
		if (useSearchPriceCategoryKey) {
			priceTable.add(new HiddenInput(BookingForm.parameterPriceCategoryKey, bookingForm.getPriceCategorySearchKey()), 1, pRow);
		}
		if (useTravelLook) {
			priceTable.add(TravelManager.getHeaderText(getResourceBundle().getLocalizedString("travel.prices", "Prices")), 1, pRow);
			priceTable.add(TravelManager.getHeaderText(getResourceBundle().getLocalizedString("travel.per_unit", "Per Unit")), 2, pRow);
			priceTable.add(TravelManager.getHeaderText(bookingForm.getUnitNamePlural(getResourceBundle())), 3, pRow);
			priceTable.add(TravelManager.getHeaderText(getResourceBundle().getLocalizedString("travel.sum", "Sum")), 4, pRow);
			priceTable.setRowColor(pRow, TravelManager.backgroundColor);
		} else {
			priceTable.add(getText(getResourceBundle().getLocalizedString("travel.prices", "Prices"), headerStyleClass), 1, pRow);
			priceTable.add(getText(getResourceBundle().getLocalizedString("travel.per_unit", "Per Unit"), headerStyleClass), 2, pRow);
			priceTable.add(getText(bookingForm.getUnitNamePlural(getResourceBundle()), headerStyleClass), 3, pRow);
			priceTable.add(getText(getResourceBundle().getLocalizedString("travel.sum", "Sum"), headerStyleClass), 4, pRow);
		}
		++pRow;
		
		
		++row;
		try {
			String sFrom = iwc.getParameter(PARAMETER_FROM);
			IWTimestamp from = null;
			if (sFrom != null) {
				from = new IWTimestamp(sFrom);
			}
			ResultOutput totalResults = new ResultOutput("tmpTotal", "0");
			totalResults.setSize(7);
			Collection addresses = getServiceHandler(iwc).getProductBusiness().getDepartureAddresses(product, from, true);
			if (addresses == null || addresses.isEmpty()) {
				int addressId = -1;
				int timeframeId = -1;
				Timeframe timeframe = getServiceHandler(iwc).getProductBusiness().getTimeframe(product, from, addressId);
				if (timeframe != null) {
					timeframeId = timeframe.getID();
				}
//				ProductPriceHome ppHome = (ProductPriceHome) IDOLookup.getHome(ProductPrice.class, getDatasource());
//				pRow = listPrices(iwc, priceTable, pRow, addressId, timeframeId, ppHome, totalResults, numberOfDays);
				ProductPriceHome ppHome = (ProductPriceHome) IDOLookup.getHome(ProductPrice.class);
				pRow = listPrices(product, iwc, priceTable, pRow, addressId, timeframeId, ppHome, totalResults, numberOfDays);
			} else {
				Iterator aIter = addresses.iterator();
				TravelAddress tAddress;
				while (aIter.hasNext()) {
					tAddress = (TravelAddress) aIter.next();
					int timeframeId = -1;
					int addressId = tAddress.getID();
					if (useTravelLook) {
						priceTable.setRowColor(pRow, TravelManager.backgroundColor);
						priceTable.add(TravelManager.getHeaderText(tAddress.getName()), 1, pRow++);
					} else {
						priceTable.add(getText(tAddress.getName(), headerStyleClass), 1, pRow++);
					}
					Timeframe timeframe = getServiceHandler(iwc).getProductBusiness().getTimeframe(product, from, addressId);
					if (timeframe != null) {
						timeframeId = timeframe.getID();
					}
//					ProductPriceHome ppHome = (ProductPriceHome) IDOLookup.getHome(ProductPrice.class, getDatasource());
//					pRow = listPrices(iwc, priceTable, pRow, addressId, timeframeId, ppHome, totalResults, numberOfDays);
					ProductPriceHome ppHome = (ProductPriceHome) IDOLookup.getHome(ProductPrice.class);
					pRow = listPrices(product, iwc, priceTable, pRow, addressId, timeframeId, ppHome, totalResults, numberOfDays);
				}
			}
			if (interfaceObjectStyleClass != null) {
				totalResults.setStyleClass(interfaceObjectStyleClass);
			}
			
			if (useTravelLook) {
				priceTable.setRowColor(pRow, TravelManager.GRAY);
			}
			priceTable.add(getText(getResourceBundle().getLocalizedString("travel.total", "Total"), headerStyleClass), 1, pRow);
			priceTable.add(totalResults, 4, pRow);

			priceTable.setWidth(2, "80");
			priceTable.setWidth(3, "50");
			priceTable.setWidth(4, "50");
		}
		catch (FinderException e) {
			e.printStackTrace();
		}
		catch (EJBException e) {
			e.printStackTrace();
		}
		catch (SQLException e) {
			e.printStackTrace();
		}
		
		Link back = getLink(getResourceBundle().getLocalizedString("travel.back", "Back"));
		back.setAsBackLink();
		
		form.add(new HiddenInput(AbstractSearchForm.ACTION, ""));
		
		
		Table linkTable = new Table(2, 1);
		linkTable.setStyleClass("sb_linktable");
		if  (useTravelLook) {
			linkTable.setColor(TravelManager.WHITE);
			linkTable.setCellpaddingAndCellspacing(1);
		} else {
			linkTable.setCellpaddingAndCellspacing(0);
		}
		if (width != null) {
			linkTable.setWidth(width);
		}
		linkTable.setAlignment(1, 1, Table.HORIZONTAL_ALIGN_LEFT);
		linkTable.setAlignment(2, 1, Table.HORIZONTAL_ALIGN_RIGHT);
		linkTable.setWidth(2, 1, 170);
		linkTable.setBorder(0);
		if (useTravelLook) {
			linkTable.setRowColor(1, TravelManager.GRAY);
		}

		Link link = null;
		if (successfulAdd && basketPage != null) {
			link = getLink(getResourceBundle().getLocalizedString("travel.view_basket", "View basket"));
			link.setPage(basketPage);
		} else {
			link = getLink(getResourceBundle().getLocalizedString("travel.add_to_basket", "Add to basket"));
			
			String formRef = "document.forms['"+form.getID()+"']";
			link.setOnClick(formRef+".elements['"+AbstractSearchForm.ACTION+"'].value='"+AbstractSearchForm.ACTION_ADD_TO_BASKET+"';"+formRef+".elements['"+ACTION+"'].value='"+ACTION_VIEW_DETAILS+"';"+formRef+".submit();return false;");
			form.setEventListener(SearchEventListener.class);

		}

		linkTable.add(back, 1, 1);
		linkTable.add(link, 2, 1);
		
		form.add(table);
		form.add(priceTable);
		addExtraBookingElements(form);
		form.add(linkTable);
	}

	private void addExtraBookingElements(Form form) throws RemoteException {
		Collection[] extras = plugin.getExtraBookingFormElements(product, super.getResourceBundle());
		boolean useExtras = extras != null && extras[0] != null && extras[1] != null && !extras[0].isEmpty() && !extras[1].isEmpty();
		boolean usePickups = false;
		Collection pickups = null;
		try {
			PickupPlaceHome ppHome = (PickupPlaceHome) IDOLookup.getHome(PickupPlace.class, getDatasource());
			pickups = ppHome.findHotelPickupPlaces(product);
			usePickups = pickups != null && !pickups.isEmpty();
		} catch (FinderException f) {
			f.printStackTrace();
		}
		
		Table extraTable = new Table();
		if (useTravelLook) {
			extraTable = TravelManager.getTable();
		}
		extraTable.setWidth(width);
		extraTable.setBorder(0);
		int stRow = 1;
//		form.add(Text.BREAK);
		form.add(extraTable);
		
		extraTable.mergeCells(1, stRow, 2, stRow);
		if (useTravelLook) {
			extraTable.setRowColor(stRow, TravelManager.backgroundColor);
			extraTable.add(TravelManager.getHeaderText(getResourceBundle().getLocalizedString("travel.booking_options", "Booking options")), 1, stRow);
		} else {
			extraTable.add(getText(getResourceBundle().getLocalizedString("travel.booking_options", "Bookng options"), headerStyleClass), 1, stRow);
		}
		++stRow;
		
		if (useExtras || usePickups) {
			if (usePickups) {
				DropdownMenu menu = new DropdownMenu(pickups, BookingForm.parameterPickupId);
				if (interfaceObjectStyleClass != null) {
					menu.setStyleClass(interfaceObjectStyleClass);
				}
				extraTable.add(getText(getResourceBundle().getLocalizedString("travel.pickup","Pickup")), 1, stRow);
				extraTable.add(menu, 2, stRow);
				if (useTravelLook) {
					extraTable.setRowColor(stRow, TravelManager.GRAY);
				}
				++stRow;
			}
			
			if (useExtras) {
				int stringCount = extras[0].size();
				int iosCount = extras[1].size();
				if (stringCount == iosCount) {
					Iterator stringIter = extras[0].iterator();
					Iterator ioIter = extras[1].iterator();
					InterfaceObject io;
					
					while (stringIter.hasNext()) {
						extraTable.add(getText((String)stringIter.next()), 1, stRow);
						io = (InterfaceObject) ioIter.next();
						if (interfaceObjectStyleClass != null) {
							io.setStyleClass(interfaceObjectStyleClass);
						}
						extraTable.add(io, 2, stRow);
						if (useTravelLook) {
							extraTable.setRowColor(stRow, TravelManager.GRAY);
						}
						++stRow;
					}
					
				}
			}
			
		}
		extraTable.add(getText(getResourceBundle().getLocalizedString("travel.commend", "Comment")), 1, stRow);
		TextArea comment = new TextArea(BookingForm.PARAMETER_COMMENT);
		comment.setWidth("400");
		comment.setHeight("70");
		extraTable.add(comment, 2, stRow);
		if (useTravelLook) {
			extraTable.setRowColor(stRow, TravelManager.GRAY);
		}
		extraTable.setVerticalAlignment(1, stRow, Table.VERTICAL_ALIGN_TOP);
		++stRow;
		
		
		extraTable.setWidth(1, "100");
	}

	private int listPrices(Product product, IWContext iwc, Table priceTable, int pRow, int addressId, int timeframeId, ProductPriceHome ppHome, ResultOutput totalResults, int numberOfDays) throws FinderException, SQLException, RemoteException {
		String key = null;
		if (useSearchPriceCategoryKey) {
			key = bookingForm.getPriceCategorySearchKey();
		}
		Collection prices = getProductPriceBusiness().getProductPrices(product.getID(), timeframeId, addressId, useOnlinePrices, key, null);
		if (prices != null) {
			Collection misc = null;

			Iterator pIter = prices.iterator();
			ProductPrice price;
			while (pIter.hasNext()) {
				price = (ProductPrice) pIter.next();
				pRow = addPrice(product, iwc, priceTable, pRow, addressId, timeframeId, totalResults, numberOfDays, price, "priceCategory");
			}
			
			try {
				misc = ppHome.findMiscellaneousPrices(product.getID(), timeframeId, addressId, useOnlinePrices);
				Iterator glitter = misc.iterator();
				if (glitter.hasNext()) {
					if (useTravelLook) {
//						priceTable.mergeCells(1, pRow, 4, pRow);
						priceTable.add(getText(getResourceBundle().getLocalizedString("travel.miscellanious_prices", "Miscellanious prices"), headerStyleClass), 1, pRow);
						priceTable.add(getText(getResourceBundle().getLocalizedString("travel.per_unit", "Per Unit"), headerStyleClass), 2, pRow);
						priceTable.add(getText(getResourceBundle().getLocalizedString("travel.qty", "Qty."), headerStyleClass), 3, pRow);
						priceTable.add(getText(getResourceBundle().getLocalizedString("travel.sum", "Sum"), headerStyleClass), 4, pRow);
//						priceTable.add(getText(getResourceBundle().getLocalizedString("travel.miscellanious_prices", "Miscellanious prices"), headerStyleClass), 1, pRow);
						priceTable.setRowColor(pRow++, TravelManager.GRAY);
					} else {
						priceTable.add(getText(getResourceBundle().getLocalizedString("travel.miscellanious_prices", "Miscellanious prices"), headerStyleClass), 1, pRow);
						if (totalResults == null) {
							priceTable.mergeCells(1, pRow, 4, pRow++);
						} else {
							priceTable.add(getText(getResourceBundle().getLocalizedString("travel.qty", "Qty."), headerStyleClass), 3, pRow++);
						}

					}
				}
				while (glitter.hasNext()) {
					price = (ProductPrice) glitter.next();
					pRow = addPrice(product, iwc, priceTable, pRow, addressId, timeframeId, totalResults, numberOfDays, price, "miscPriceCategory");
				}
				
			} catch (FinderException f) {
				f.printStackTrace();
			}
		}
		return pRow;
	}

	private int addPrice(Product product, IWContext iwc, Table priceTable, int pRow, int addressId, int timeframeId, ResultOutput totalResults, int numberOfDays, ProductPrice price, String textInputPrefix) throws SQLException, RemoteException, FinderException {
		float fPrice = -1;
		try {
			fPrice = getTravelStockroomBusiness(iwc).getPrice(((Integer) price.getPrimaryKey()).intValue(), product.getID(), price.getPriceCategoryID(), price.getCurrencyId(), IWTimestamp.getTimestampRightNow(), timeframeId, addressId);
		} catch (ProductPriceException  p) {
			
		}

		String units = iwc.getParameter(PARAMETER_NUMBER_OF_UNITS);
		boolean useUnit = !unitQuantityUsed && units != null;
		
		boolean useInputs = (totalResults != null);
		
		priceTable.add(getText(price.getPriceCategory().getName()), 1, pRow);
		priceTable.add(getText(df.format(fPrice*numberOfDays)+" "+price.getCurrency().getCurrencyAbbreviation()), 2, pRow);
		if (numberOfDays > 1) {
			priceTable.add(getText(Text.BREAK+"("+df.format(fPrice)+" "+bookingForm.getPerDayString(getResourceBundle())+")"), 2, pRow);
		}
		
		if (useInputs) {
			TextInput inp = new TextInput(textInputPrefix+price.getPrimaryKey().toString());
			inp.setSize(3);
			
			ResultOutput rout = new ResultOutput("tmp"+price.getPrimaryKey().toString(), "0");
			rout.setSize(7);
			rout.add(inp, ResultOutput.OPERATOR_MULTIPLY+fPrice+ResultOutput.OPERATOR_MULTIPLY+numberOfDays);
			if (useUnit) {
				inp.setContent(units);
				rout.setContent(Integer.toString((int) (fPrice*numberOfDays*Integer.parseInt(units))));
				totalResults.setContent(Integer.toString((int) (fPrice*numberOfDays*Integer.parseInt(units))));
				unitQuantityUsed = true;
			} else {
				inp.setContent("0");
			}
			
			
			if (interfaceObjectStyleClass != null) {
				inp.setStyleClass(interfaceObjectStyleClass);
				rout.setStyleClass(interfaceObjectStyleClass);
			}
			
			totalResults.add(rout);
			priceTable.add(inp, 3, pRow);
			priceTable.add(rout, 4, pRow);
//			totalResults.setValue(Integer.toString(Integer.parseInt(totalResults.getValueAsString()) + Integer.parseInt(rout.getValueAsString())));
		}
		if (useTravelLook) {
			priceTable.setRowColor(pRow, TravelManager.GRAY);
		}
		++pRow;
		return pRow;
	}
	
	private void listProducts(IWContext iwc, Form form) throws RemoteException {
		Table table = new Table();
		if (useTravelLook) {
			table = TravelManager.getTable();
		} else {
			table.setCellpaddingAndCellspacing(0);
		}
		if (width != null) {
			table.setWidth(width);
		}
//		table.setBorder(1);
		int row = 1;

		Collection coll = getProducts(iwc);
		
		Collection[] inputs = plugin.getProductSearchInputs(iwc, super.getResourceBundle());
		Table searchTable = getSearchFrom(inputs, ACTION_VIEW_PRODUCTS);
		if (searchTable != null) {
			form.add(searchTable);
		}
		
		if (useTravelLook) {
			table.mergeCells(1, row, 3, row);
			table.setRowColor(row, TravelManager.backgroundColor);
			table.add(TravelManager.getHeaderText(getResourceBundle().getLocalizedString("travel.services", "Services")), 1, row++);
		}

		int localeID = iwc.getCurrentLocaleId();
		if (coll != null && !coll.isEmpty()) {
			Iterator iter = coll.iterator();
			Product product;
			while (iter.hasNext()) {
				product = (Product) iter.next();
				row = addProductInfo(iwc, table, row, localeID, product, true);
				if (!useTravelLook) {
					table.setHeight(row, 1);
					table.setRowStyleClass(row, "sbrowser_background_line");
					row++;
				}
			}
		} else {
			if (useTravelLook) {
				table.mergeCells(1, row, 3, row);
				table.setRowColor(row, TravelManager.GRAY);
			}
			table.add(getText(getResourceBundle().getLocalizedString("travel.no_available_products_found", "No available products found"), headerStyleClass), 1, row++);
		}
	
		table.setColumnWidth(1, imageWidth);
		
		form.add(table);
	}
	
	public Table getProductInfo(IWContext iwc, Product product, boolean addDetailLink) throws RemoteException {
		Table table = new Table();
		table.setCellpaddingAndCellspacing(0);
//		table.setBorder(1);
		numberOfCharactersBeforeMoreButton = -1;
		addProductInfo(iwc, table, 1, iwc.getCurrentLocaleId(), product, addDetailLink);
		return table;
	}

	private int addProductInfo(IWContext iwc, Table table, int row, int localeID, Product product, boolean addDetailLink) throws RemoteException {
		Image image = null;
		try {
			ICFile file = product.getFile();
			if (file != null) {
				image = new Image(Integer.parseInt(file.getPrimaryKey().toString()));
				image.setDatasource(product.getDatasource());
				if (imageStyleClass != null) {
					image.setStyleClass(imageStyleClass);
				}
				image.setMaxImageWidth(Integer.parseInt(imageWidth));
			}
		} catch (SQLException sql) {
			sql.printStackTrace();
		}
		int startRow = row;
		if (image != null) {
			table.add(image, 1, row);
		}
		table.setCellpadding(1, row, 2);
		table.setCellpadding(2, row, 2);
		table.setCellpadding(3, row, 2);
		table.setHeight(2, row, "10");
		table.setVerticalAlignment(1, row, Table.VERTICAL_ALIGN_TOP);
		table.setVerticalAlignment(2, row, Table.VERTICAL_ALIGN_TOP);
		table.setAlignment(2, row, Table.HORIZONTAL_ALIGN_LEFT);
		table.add(getText(product.getProductName(localeID), headerStyleClass), 2, row);
		if (plugin.displaySupplierResults()) {
			table.add(getText(" - "+product.getSupplier().getName(), headerStyleClass), 2, row++);
		} else {
			++row;
		}
		if (!useTravelLook) {
			table.setHeight(row, "1");
			if (!addDetailLink) {
				table.mergeCells(2, row, 3, row);
			}
			table.setCellpadding(2, row, 0);
			table.setRowStyleClass(row++, "sbrowser_header_background_line");
		}
		Link images = null;
		Link moreLink = null;
		try {
			TxText descriptionText;
			descriptionText = product.getText();
			if (descriptionText != null) {
				ContentHelper ch = null;
				ch = ContentFinder.getContentHelper(descriptionText.getContentId(), localeID, product.getDatasource());
				table.setCellpadding(2, row, 2);
				table.setVerticalAlignment(2, row, Table.VERTICAL_ALIGN_TOP);
				String desc = "";
				if (ch != null && ch.getLocalizedText() != null) {
					desc = ch.getLocalizedText().getBody();
				}
				boolean showMoreButton = desc.length() > numberOfCharactersBeforeMoreButton && numberOfCharactersBeforeMoreButton > 0;
				if (showMoreButton) {
					desc = desc.substring(0, numberOfCharactersBeforeMoreButton);
				}
				
				table.add(getText(desc), 2, row);
				if (showMoreButton) {
					moreLink = getLink(getResourceBundle().getLocalizedString("more", "More"));
					moreLink.setPublicWindowToOpen(SupplierBrowserDetailsWindow.class);
					moreLink.addParameter(SupplierBrowserDetailsWindow.PARAMETER_PRODUCT_ID, product.getPrimaryKey().toString());
					addParametersToMoreLink(moreLink);
					table.add(" ... ", 2, row);
					table.setCellpaddingBottom(3, row, 2);
					table.setCellpaddingRight(3, row, 2);
				}

				if (ch.getFiles() != null && !ch.getFiles().isEmpty()) {
					images = new Link(getText(getResourceBundle().getLocalizedString("travel.images", "Images"), linkStyleClass));
					images.setPublicWindowToOpen(ProductImageSlideShowWindow.class);
					images.addParameter(ProductImageSlideShowWindow.PARAMETER_PRODUCT_ID, product.getPrimaryKey().toString());
				}
			}
		}
		catch (SQLException e) {
			e.printStackTrace();
		}
		table.setAlignment(2, row, Table.HORIZONTAL_ALIGN_LEFT);
		table.setVerticalAlignment(2, row, Table.VERTICAL_ALIGN_TOP);

		int rowsToAdd = 2;
		if (useTravelLook)  {
			rowsToAdd = 1;
		}
		
		if (addDetailLink && plugin.isProductSearchCompleted(iwc)) {
			Link details = getLink(getResourceBundle().getLocalizedString("travel.book", "Book"));
			details.addParameter(ACTION, ACTION_VIEW_DETAILS);
			details.addParameter(PARAMETER_PRODUCT_ID, product.getPrimaryKey().toString());
			addParametersToLink(iwc, details);
			table.add(details, 3, startRow);
			table.setAlignment(3, startRow, Table.HORIZONTAL_ALIGN_RIGHT);
			table.setVerticalAlignment(3, (startRow+rowsToAdd), Table.VERTICAL_ALIGN_TOP);
			table.setAlignment(3, (startRow+rowsToAdd), Table.HORIZONTAL_ALIGN_RIGHT);
			table.add(images, 3, (startRow+rowsToAdd));
			table.setCellpadding(3, (startRow+rowsToAdd), 2);
		} else if (addDetailLink && !plugin.isProductSearchCompleted(iwc)){
			table.setAlignment(3, startRow, Table.HORIZONTAL_ALIGN_RIGHT);
			table.setVerticalAlignment(3, (startRow+rowsToAdd), Table.VERTICAL_ALIGN_TOP);
			table.setCellpadding(3, (startRow+rowsToAdd), 2);

			if (images != null) {
				table.add(images, 3, (startRow+rowsToAdd));
			}
		} else {
			table.mergeCells(2, startRow, 3, startRow);
			table.mergeCells(2, (startRow+rowsToAdd), 3, (startRow+rowsToAdd));
		}
		
		if (moreLink != null) {
			if (images != null) {
				table.add(Text.getBreak(), 3, (startRow+rowsToAdd));
			}
			table.add(moreLink, 3, (startRow+rowsToAdd));
			table.setAlignment(3, (startRow+rowsToAdd), Table.HORIZONTAL_ALIGN_RIGHT);
		}

		if (useTravelLook) {
			table.setRowColor((row-1), TravelManager.GRAY);
			table.setRowColor(row, TravelManager.GRAY);
		}
		
		
		row = plugin.addProductInfo(product, table, row, getResourceBundle());
		
		if (showPriceWithProductInformation && plugin.isProductSearchCompleted(iwc) && addDetailLink) {
			Table priceTable = new Table();
			priceTable.setCellpaddingAndCellspacing(1);
			int pRow = 1;
			table.add(priceTable, 2, ++row);
			table.setAlignment(2, row, Table.HORIZONTAL_ALIGN_LEFT);
			if (useTravelLook) {
				table.setRowColor(row, TravelManager.GRAY);
			}
			priceTable.mergeCells(1, pRow, 4, pRow);
			priceTable.add(getText(getResourceBundle().getLocalizedString("travel.prices", "Prices"), headerStyleClass), 1, pRow++);
			
			try {
				Collection addresses = getServiceHandler(iwc).getProductBusiness().getDepartureAddresses(product, from, true);
				if (addresses == null || addresses.isEmpty()) {
					int addressId = -1;
					int timeframeId = -1;
					Timeframe timeframe = getServiceHandler(iwc).getProductBusiness().getTimeframe(product, from, addressId);
					if (timeframe != null) {
						timeframeId = timeframe.getID();
					}
					ProductPriceHome ppHome = (ProductPriceHome) IDOLookup.getHome(ProductPrice.class);
					pRow = listPrices(product, iwc, priceTable, pRow, addressId, timeframeId, ppHome, null, numberOfDays);
				} else {
					Iterator aIter = addresses.iterator();
					TravelAddress tAddress;
					while (aIter.hasNext()) {
						tAddress = (TravelAddress) aIter.next();
						int timeframeId = -1;
						int addressId = tAddress.getID();
						if (useTravelLook) {
							priceTable.setRowColor(pRow, TravelManager.backgroundColor);
							priceTable.add(TravelManager.getHeaderText(tAddress.getName()), 1, pRow++);
						} else {
							priceTable.add(getText(tAddress.getName(), headerStyleClass), 1, pRow++);
						}
						Timeframe timeframe = getServiceHandler(iwc).getProductBusiness().getTimeframe(product, from, addressId);
						if (timeframe != null) {
							timeframeId = timeframe.getID();
						}
						ProductPriceHome ppHome = (ProductPriceHome) IDOLookup.getHome(ProductPrice.class);
						pRow = listPrices(product, iwc, priceTable, pRow, addressId, timeframeId, ppHome, null, numberOfDays);
					}
				}
			} catch (FinderException e) {
				e.printStackTrace();
			}
			catch (RemoteException e) {
				e.printStackTrace();
			}
			catch (SQLException e) {
				e.printStackTrace();
			}
		}

		table.mergeCells(1, startRow, 1, row);
		++row;
		
		if (spaceBetweenItems > 0) {
			if (useTravelLook) {
				table.setRowColor(row, TravelManager.GRAY);
			}
			table.setHeight(row++, spaceBetweenItems);
		}

		return row;
	}
	
	private void listSuppliers(IWContext iwc, Form form) throws RemoteException {
		Table table = new Table();
		if (useTravelLook) {
			table = TravelManager.getTable();
		} else {
			table.setCellpaddingAndCellspacing(0);
		}
		if (width != null) {
			table.setWidth(width);
		}

		table.setColumnWidth(1, imageWidth);
		
		int row = 1;
		
		Collection coll = null;
		try {
			coll = getSuppliers(iwc);
		}
		catch (IDOCompositePrimaryKeyException e) {
			e.printStackTrace();
		}
		
		Collection[] inputs = plugin.getSupplierSearchInputs(iwc, super.getResourceBundle());
		Table searchF = getSearchFrom(inputs, ACTION_VIEW_SUPPLIERS);
		if (searchF != null) {
			form.add(searchF);
		}
		
		if (coll != null && !coll.isEmpty()) {
			if (useTravelLook) {
				table.mergeCells(1, row, 3, row);
				table.setRowColor(row, TravelManager.backgroundColor);
				table.add(TravelManager.getHeaderText(getResourceBundle().getLocalizedString("travel.suppliers", "Suppliers")), 1, row++);
			}
			Iterator iter = coll.iterator();
			Supplier supplier;
			ICFile file = null;
			Image image = null;
			while (iter.hasNext()) {
				supplier = (Supplier) iter.next();
				row = addSupplierInfo(iwc, table, row, supplier, true);
				
			}
			
		} else {
			table.add(getText(getResourceBundle().getLocalizedString("travel.no_suppliers_found", "No suppliers found"), headerStyleClass), 1, row);
		}
		
		form.add(table);
	}

	public Table getSupplierInfo(IWContext iwc, Supplier supplier) throws RemoteException {
		Table table = new Table();
		table.setCellpaddingAndCellspacing(0);
		numberOfCharactersBeforeMoreButton = -1;
		addSupplierInfo(iwc, table, 1, supplier, false);
		return table;
	}
	
	private int addSupplierInfo(IWContext iwc, Table table, int row, Supplier supplier,  boolean showDetailLink) throws RemoteException {
		ICFile file;
		Image image;
		file = supplier.getICFile();
		if (file != null) {
			try {
				image = new Image(((Integer) file.getPrimaryKey()).intValue());
				image.setMaxImageWidth(Integer.parseInt(imageWidth));
				image.setDatasource(supplier.getDatasource());
				table.add(image, 1, row);
			}
			catch (Exception e) {
				e.printStackTrace();
			}
		}
		int sRow = row;
		table.setVerticalAlignment(1, row, Table.VERTICAL_ALIGN_TOP);
		table.setVerticalAlignment(2, row, Table.VERTICAL_ALIGN_TOP);
		table.setVerticalAlignment(3, row, Table.VERTICAL_ALIGN_TOP);
		table.setAlignment(2, row, Table.HORIZONTAL_ALIGN_LEFT);
		table.setAlignment(3, row, Table.HORIZONTAL_ALIGN_RIGHT);
		table.setRowPadding(row, 2);
		table.add(getText(supplier.getName(), headerStyleClass), 2, row);
		table.setColumnWidth(1, imageWidth);
		if (useTravelLook) {
			table.setRowColor(row, TravelManager.GRAY);
		}

		if (showDetailLink) {
			table.add(getDetailLink(supplier, iwc), 3, row++);
		} else {
			++row;
		}
//				table.add(getText(Text.BREAK), 2, row);
		String desc = supplier.getDescription();
		// TEST
//		if (numberOfCharactersBeforeMoreButton > 0) {
//			numberOfCharactersBeforeMoreButton = 100;
//		}
		if (desc != null && !desc.trim().equals("")) {
			boolean showMoreButton = desc.length() > numberOfCharactersBeforeMoreButton && numberOfCharactersBeforeMoreButton > 0;
			if (showMoreButton) {
				desc = desc.substring(0, numberOfCharactersBeforeMoreButton);
			}
			if (!useTravelLook) {
				table.setRowHeight(row, "1");
				table.setRowStyleClass(row++, "sbrowser_header_background_line");
			}
			table.add(getText(desc), 2, row);
			table.setRowPadding(row, 2);
			table.setVerticalAlignment(2, row, Table.VERTICAL_ALIGN_TOP);
			table.setAlignment(2, row, Table.HORIZONTAL_ALIGN_LEFT);
			table.setAlignment(3, row, Table.HORIZONTAL_ALIGN_RIGHT);
			if (showMoreButton) {
				Link moreLink = new Link(getResourceBundle().getLocalizedString("more", "More"));
				moreLink.setPublicWindowToOpen(SupplierBrowserDetailsWindow.class);
				moreLink.addParameter(SupplierBrowserDetailsWindow.PARAMETER_SUPPLIER_ID, supplier.getPrimaryKey().toString());
				addParametersToMoreLink(moreLink);
				table.add(" ... ", 2, row);
				table.add(moreLink, 3, row);
//				table.setRowPadding(row, 2);
				table.setVerticalAlignment(3, row, Table.VERTICAL_ALIGN_TOP);
//				table.setAlignment(2, row, Table.HORIZONTAL_ALIGN_LEFT);
				table.setAlignment(3, row, Table.HORIZONTAL_ALIGN_RIGHT);
				
			}
		}
		
//				table.setVerticalAlignment(4, row, Table.VERTICAL_ALIGN_TOP);
//		table.setBorder(1);
		
		if (sRow != row) {
			table.mergeCells(1, sRow, 1, row);
		}
		if (spaceBetweenItems > 0) {
			if (useTravelLook) {
				table.setRowColor(row, TravelManager.GRAY);
			}
			table.setHeight(++row, spaceBetweenItems);
		}
		if (useTravelLook) {
			table.setRowColor(row++, TravelManager.GRAY);
		} else {
			row++;
			table.setHeight(row, 1);
			table.setRowStyleClass(row, "sbrowser_background_line");
			row++;
		}
		
		return row;
	}

	/**
	 * <p>
	 * TODO gimmi describe method addParametersToMoreLink
	 * </p>
	 * @param moreLink
	 */
	private void addParametersToMoreLink(Link moreLink) {
		moreLink.addParameter(PARAMETER_PLUGIN, IWMainApplication.getEncryptedClassName(plugin.getClass()));
		moreLink.addParameter(PARAMETER_SHOW_PRICES, Boolean.toString(showPriceWithProductInformation));
		moreLink.addParameter(PARAMETER_USE_ONLINE_PRICES, Boolean.toString(useOnlinePrices));
		moreLink.addParameter(PARAMETER_HEADER_STYLE_CLASS, headerStyleClass);
		moreLink.addParameter(PARAMETER_LINK_STYLE_CLASS, linkStyleClass);
		moreLink.addParameter(PARAMETER_TEXT_STYLE_CLASS, textStyleClass);
	}

	/**
	 * @param inputs
	 * @throws RemoteException
	 */
	private Table getSearchFrom(Collection[] inputs, String action) throws RemoteException {
		if ( showInputs && inputs != null && inputs[0] != null && inputs[1] != null && !inputs[0].isEmpty() && !inputs[1].isEmpty()) {
			Table searchTable = new Table();
			int stRow = 1;
			Collection strings = inputs[0];
			Collection ios = inputs[1];
			int stringCount = strings.size();
			int iosCount = ios.size();
			if (stringCount == iosCount) {
				Iterator siter = strings.iterator();
				Iterator iiter = ios.iterator();
				PresentationObject io;
				if (useTravelLook) {
					searchTable = TravelManager.getTable();
					searchTable.mergeCells(1, stRow, 2, stRow);
					searchTable.setRowColor(stRow, TravelManager.backgroundColor);
					searchTable.add(TravelManager.getHeaderText(getResourceBundle().getLocalizedString("travel.search", "Search")), 1, stRow++);
				}
				searchTable.setWidth(width);
				
				for (int i = 0; i < iosCount; i++) {
					String s = (String) siter.next();
					io = (PresentationObject) iiter.next();
					if (interfaceObjectStyleClass != null) {
						io.setStyleClass(interfaceObjectStyleClass);
					}
					if (io instanceof DatePicker) {
						((DatePicker)io).setLength(11);
					}
					searchTable.add(getText(s), 1, stRow);
					searchTable.setAlignment(2, stRow, Table.HORIZONTAL_ALIGN_RIGHT);
					if (useTravelLook) {
						searchTable.setRowColor(stRow, TravelManager.GRAY);
					}
					searchTable.add(io, 2, stRow++);
				}
				BackButton back = new BackButton();
				SubmitButton search = new SubmitButton(super.getResourceBundle().getLocalizedString("search", "Search"), ACTION, action);
				searchTable.setAlignment(2, stRow, Table.HORIZONTAL_ALIGN_RIGHT);
				searchTable.add(back, 1, stRow);
				searchTable.add(search, 2, stRow);
				if (useTravelLook) {
					searchTable.setRowColor(stRow, TravelManager.GRAY);
				}
				++stRow;
			} else {
				System.out.println("IO stuff error yes (SupplierBrowser)");
			}
			return searchTable;
		}
		return null;
	}
	
	private Link getDetailLink(Supplier supplier, IWContext iwc) throws RemoteException {
		Link link = new Link(getText(getResourceBundle().getLocalizedString(plugin.getLocalizationPrefix()+"details", "Details"), linkStyleClass));
		link.addParameter(ACTION, ACTION_VIEW_PRODUCTS);
		link.maintainParameter(PARAMETER_POSTAL_CODES, iwc);
		link.maintainParameter(PARAMETER_SUPPLIER_MANAGER, iwc);
		link.addParameter(PARAMETER_SUPPLIER_ID, supplier.getPrimaryKey().toString());
		link.addParameter(SHOW_SEARCH_INPUTS, Boolean.toString(showInputs));
		link.addParameter(PARAMETER_SUPPLIER_NAME, supplier.getName());
		link.maintainParameter(PARAMETER_NUMBER_OF_DAYS, iwc);
		link.maintainParameter(PARAMETER_NUMBER_OF_UNITS, iwc);
		String[] params = plugin.getParameters();
		if (params != null) {
			for (int i = 0; i < params.length; i++) {
				link.maintainParameter(params[i], iwc);
			}
		}
		return link;
	}
	
	private Text getText(String content) {
		return getText(content, textStyleClass);
	}
	
	private Link getLink(String content) {
		return new Link(getText(content, linkStyleClass));
	}
	
	private Text getText(String content, String styleClass) {
		Text text = new Text(content);
		if (styleClass != null) {
			text.setStyleClass(styleClass);
		}
		return text;
	}
	
	private Text getErrorText(String content) {
		Text text = getText(content, headerStyleClass);
		text.setFontColor("#FF0000");
		return text;
	}
	
	
	// Move to a better location later... when possible
	private Collection getSuppliers(IWContext iwc) throws IDOCompositePrimaryKeyException {
		try {
			Collection coll = getSupplierHome().findByPostalCodes(supplierManager, postalCodes[0], postalCodes[1], plugin.getSupplierSearchCriterias(iwc), iwc.getParameter(PARAMETER_SUPPLIER_NAME));
			return plugin.filterSuppliers(coll, supplierManager, iwc, postalCodes, useOnlinePrices, useSearchPriceCategoryKey);
		}
		catch (IDORelationshipException e) {
			e.printStackTrace();
		}
		catch (FinderException e) {
			e.printStackTrace();
		}
		return null;
	}
	
	private Collection getProducts(IWContext iwc) throws RemoteException {
		try {
			Supplier supplier = null;
			if (iwc.isParameterSet(PARAMETER_SUPPLIER_ID)) {
				supplier = getSupplierHome().findByPrimaryKey(Integer.parseInt(iwc.getParameter(PARAMETER_SUPPLIER_ID)));
			}
			return plugin.getProducts(supplier, supplierManager, iwc, postalCodes, useOnlinePrices, useSearchPriceCategoryKey);
		}
		catch (FinderException e) {
			e.printStackTrace();
		}
		return null;
	}
	
	public void setSupplierManager(int id) {
		this.supplierManagerId = id;
	}
	
	public void setTextStyleClass(String styleClass) {
		this.textStyleClass = styleClass;
	}
	
	public void setHeaderStyleClass(String styleClass) {
		this.headerStyleClass = styleClass;
	}
		
	public void setLinkStyleClass(String styleClass) {
		this.linkStyleClass = styleClass;
	}
	
	public void setImageStyleClass(String styleClass) {
		this.imageStyleClass = styleClass;
	}
	
	public void setInterfaceObjectStyleClass(String styleClass) {
		this.interfaceObjectStyleClass = styleClass;
	}
	
	public void setPlugin(String pluginClassName) {
		this.pluginClassName = pluginClassName;
	}
	
	public void setWidth(String width) {
		this.width = width;
	}
	
	public void setUseBasket(boolean useBasket) {
		this.useBasket = useBasket;
	}
	
	public void setUseOnlinePrices(boolean useOnlinePrices) {
		this.useOnlinePrices = useOnlinePrices;
	}
	
	public void setUseSearchPriceCategoryKey(boolean useKey) {
		this.useSearchPriceCategoryKey = useKey;
	}

	public void setSupplierManager(Group suppMan) {
		this.supplierManager = suppMan;
	}
	
	public void setUseTravelLook(boolean use) {
		this.useTravelLook = use;
	}
	
	public void setShowPriceWithProductInformation(boolean show) {
		showPriceWithProductInformation = show;
	}
	
	public void setBasketPage(ICPage page) {
		this.basketPage = page;
	}
	
	public void setNumberOfCharactersBeforeMoreButton(int numberOfCharacters) {
		numberOfCharactersBeforeMoreButton = numberOfCharacters;
	}
	
	public void setSpaceBetweenItems(int spaceBetweenItems) {
		this.spaceBetweenItems = spaceBetweenItems;		
	}
	
	public void setImageWidth(String width) {
		this.imageWidth = width;
	}
	
	private ServiceSearchSession getSearchSession(IWUserContext iwc) {
		try {
			return (ServiceSearchSession) IBOLookup.getSessionInstance(iwc, ServiceSearchSession.class);
		}
		catch (IBOLookupException e) {
			throw new IBORuntimeException(e);
		}
	}
	
	private SupplierHome getSupplierHome() {
		try {
			return (SupplierHome) IDOLookup.getHome(Supplier.class, getDatasource());
		}
		catch (IDOLookupException e) {
			throw new IDORuntimeException(e);
		}
	}
	
}
