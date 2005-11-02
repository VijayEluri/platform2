package is.idega.idegaweb.member.isi.block.accounting.business;

import is.idega.idegaweb.member.isi.block.accounting.data.AssessmentRound;
import is.idega.idegaweb.member.isi.block.accounting.data.ClubTariffType;
import is.idega.idegaweb.member.isi.block.accounting.data.FinanceEntry;
import is.idega.idegaweb.member.isi.block.accounting.data.FinanceEntryBMPBean;
import is.idega.idegaweb.member.isi.block.accounting.data.PaymentType;
import is.idega.idegaweb.member.util.IWMemberConstants;

import java.rmi.RemoteException;
import java.sql.Date;
import java.util.Arrays;
import java.util.Collection;
import java.util.Collections;
import java.util.Comparator;
import java.util.Iterator;
import java.util.List;
import java.util.Locale;
import java.util.Map;
import java.util.TreeMap;
import java.util.Vector;

import javax.ejb.FinderException;

import com.idega.block.datareport.util.FieldsComparator;
import com.idega.block.datareport.util.ReportableCollection;
import com.idega.block.datareport.util.ReportableData;
import com.idega.block.datareport.util.ReportableField;
import com.idega.business.IBOLookup;
import com.idega.business.IBOSessionBean;
import com.idega.core.contact.data.Phone;
import com.idega.core.contact.data.PhoneType;
import com.idega.idegaweb.IWBundle;
import com.idega.idegaweb.IWResourceBundle;
import com.idega.user.business.GroupBusiness;
import com.idega.user.data.Group;
import com.idega.user.data.User;
import com.idega.util.IWTimestamp;
import com.idega.util.text.TextSoap;

/**
 * @author Sigtryggur
 */
public class AccountingStatsBusinessBean extends IBOSessionBean implements AccountingStatsBusiness {
	
	private static final String LOCALIZED_CURRENT_DATE = "AccountingStatsBusiness.current_date";
	private static final String LOCALIZED_CLUB_NAME = "AccountingStatsBusiness.club_name";
	private static final String LOCALIZED_DIVISION_NAME = "AccountingStatsBusiness.division_name";
	private static final String LOCALIZED_GROUP_NAME = "AccountingStatsBusiness.group_name";
	private static final String LOCALIZED_NAME = "AccountingStatsBusiness.name";
	private static final String LOCALIZED_PERSONAL_ID = "AccountingStatsBusiness.personal_id";
	private static final String LOCALIZED_PHONE = "AccountingStatsBusiness.phone";
	private static final String LOCALIZED_AMOUNT = "AccountingStatsBusiness.amount";
	private static final String LOCALIZED_DATE_OF_ENTRY = "AccountingStatsBusiness.date_of_entry";
	private static final String LOCALIZED_ITEM_PRICE = "AccountingStatsBusiness.item_price";
	private static final String LOCALIZED_TARIFF_TYPE = "AccountingStatsBusiness.tariff_type";
	private static final String LOCALIZED_INFO = "AccountingStatsBusiness.info";
	private static final String LOCALIZED_PAYMENT_TYPE = "AccountingStatsBusiness.payment_type";
	private static final String LOCALIZED_SENT = "AccountingStatsBusiness.sent";
	private static final String LOCALIZED_PAYMENT_DATE = "AccountingStatsBusiness.payment_date";
	
	private static final String FIELD_NAME_DIVISION_NAME = "division_name";
	private static final String FIELD_NAME_GROUP_NAME = "group_name";
	private static final String FIELD_NAME_NAME = "name";
	private static final String FIELD_NAME_PERSONAL_ID = "personal_id";
	private static final String FIELD_NAME_PHONE = "phone";
	private static final String FIELD_NAME_AMOUNT = "amount";
	private static final String FIELD_NAME_DATE_OF_ENTRY = "date_of_entry";
	private static final String FIELD_NAME_ITEM_PRICE = "item_price";
	private static final String FIELD_NAME_TARIFF_TYPE = "tariff_type";
	private static final String FIELD_NAME_INFO = "info";
	private static final String FIELD_NAME_PAYMENT_TYPE = "payment_type";
	private static final String FIELD_NAME_SENT = "sent";
	private static final String FIELD_NAME_PAYMENT_DATE = "payment_date";
	
	private AccountingBusiness accountingBiz = null;
	private GroupBusiness groupBiz = null;
	private IWBundle _iwb = null;
	private IWResourceBundle _iwrb = null;
	private final static String IW_BUNDLE_IDENTIFIER = "is.idega.idegaweb.member.isi.block.accounting";
	
	private AccountingBusiness getAccountingBusiness() throws RemoteException {
		if (accountingBiz == null) {
			accountingBiz = (AccountingBusiness) IBOLookup.getServiceInstance(this.getIWApplicationContext(), AccountingBusiness.class);
		}	
		return accountingBiz;
	}
	
	private GroupBusiness getGroupBusiness() throws RemoteException {
		if (groupBiz == null) {
			groupBiz = (GroupBusiness) IBOLookup.getServiceInstance(this.getIWApplicationContext(), GroupBusiness.class);
		}	
		return groupBiz;
	}
	
	private void initializeBundlesIfNeeded() {
		if (_iwb == null) {
			_iwb = this.getIWApplicationContext().getIWMainApplication().getBundle(IW_BUNDLE_IDENTIFIER);
		}
		_iwrb = _iwb.getResourceBundle(this.getUserContext().getCurrentLocale());
	}
	
	/*
	 * Report A29.1 of the ISI Specs
	 */
	public ReportableCollection getPaymentStatusByDivisionsGroupsAndDateIntervalFiltering(
			Date dateFromFilter,
			Date dateToFilter,
			Collection divisionsFilter,
			Collection groupsFilter,
			String personalIDFilter)
	throws RemoteException {
		//initialize stuff
		initializeBundlesIfNeeded();
		ReportableCollection reportCollection = new ReportableCollection();
		Locale currentLocale = this.getUserContext().getCurrentLocale();
		
		Group club = null;
		try {
			club = getClubForUser(this.getCurrentUser() );
		}
		catch (FinderException e) {
			e.printStackTrace();
		}
		
		//PARAMETES
		//Add extra...because the inputhandlers supply the basic header texts
		if (club != null)
			reportCollection.addExtraHeaderParameter(
				"label_club_name", _iwrb.getLocalizedString(LOCALIZED_CLUB_NAME, "Club name"),
				"club_name", club.getName());

		reportCollection.addExtraHeaderParameter(
				"label_current_date", _iwrb.getLocalizedString(LOCALIZED_CURRENT_DATE, "Current date"),
				"current_date", TextSoap.findAndCut((new IWTimestamp()).getLocaleDateAndTime(currentLocale, IWTimestamp.LONG,IWTimestamp.SHORT),"GMT"));
		
		 //PARAMETERS that are also FIELDS
		 //data from entity columns, can also be defined with an entity definition, see getClubMemberStatisticsForRegionalUnions method
		 //The name you give the field/parameter must not contain spaces or special characters		
		 ReportableField divisionField = new ReportableField(FIELD_NAME_DIVISION_NAME, String.class);
		 divisionField.setLocalizedName(_iwrb.getLocalizedString(LOCALIZED_DIVISION_NAME, "Division"), currentLocale);
		 reportCollection.addField(divisionField);
		 
		 ReportableField groupField = new ReportableField(FIELD_NAME_GROUP_NAME, String.class);
		 groupField.setLocalizedName(_iwrb.getLocalizedString(LOCALIZED_GROUP_NAME, "Group"), currentLocale);
		 reportCollection.addField(groupField);
		 
		 ReportableField nameField = new ReportableField(FIELD_NAME_NAME, String.class);
		 nameField.setLocalizedName(_iwrb.getLocalizedString(LOCALIZED_NAME, "Name"), currentLocale);
		 reportCollection.addField(nameField);
		 
		 ReportableField personalIDField = new ReportableField(FIELD_NAME_PERSONAL_ID, String.class);
		 personalIDField.setLocalizedName(_iwrb.getLocalizedString(LOCALIZED_PERSONAL_ID, "Personal ID"),currentLocale);
		 reportCollection.addField(personalIDField);
		 
		 ReportableField amountField = new ReportableField(FIELD_NAME_AMOUNT, Double.class);
		 amountField.setLocalizedName(_iwrb.getLocalizedString(LOCALIZED_AMOUNT, "Amount"), currentLocale);
		 reportCollection.addField(amountField);
		 
		 ReportableField entryDateField = new ReportableField(FIELD_NAME_DATE_OF_ENTRY, String.class);
		 entryDateField.setLocalizedName(_iwrb.getLocalizedString(LOCALIZED_DATE_OF_ENTRY, "Date of entry"), currentLocale);
		 reportCollection.addField(entryDateField);
		 
		 ReportableField itemPriceField = new ReportableField(FIELD_NAME_ITEM_PRICE, Double.class);
		 itemPriceField.setLocalizedName(_iwrb.getLocalizedString(LOCALIZED_ITEM_PRICE, "Item price"), currentLocale);
		 reportCollection.addField(itemPriceField);
		 
		 ReportableField tariffTypeField = new ReportableField(FIELD_NAME_TARIFF_TYPE, String.class);
		 tariffTypeField.setLocalizedName(_iwrb.getLocalizedString(LOCALIZED_TARIFF_TYPE, "Tariff type"), currentLocale);
		 reportCollection.addField(tariffTypeField);
		 
		 ReportableField infoField = new ReportableField(FIELD_NAME_INFO, String.class);
		 infoField.setLocalizedName(_iwrb.getLocalizedString(LOCALIZED_INFO, "Info"), currentLocale);
		 reportCollection.addField(infoField);
		 
		 //Gathering data	 
		 //then for each division get its financeRecords and
		 //create a row and insert into an ordered map
		 //then iterate the map and insert into the final report collection.
		
		 String[] types = { FinanceEntryBMPBean.TYPE_ASSESSMENT, FinanceEntryBMPBean.TYPE_MANUAL};
		 Collection finEntries = getAccountingBusiness().getFinanceEntriesByDateIntervalDivisionsAndGroups(club, types, dateFromFilter, dateToFilter, divisionsFilter, groupsFilter, personalIDFilter);
		 Map financeEntriesByDivisions = new TreeMap();
		 
		 //Iterating through reports and creating report data
		 Group division = null;
		 Group group = null;
		 User user = null;
		 ClubTariffType tariffType = null;
		 Iterator iter = finEntries.iterator();
		 while (iter.hasNext()) {
			 FinanceEntry financeEntry = (FinanceEntry) iter.next();
			 String divisionString = null;
			 String groupString = null;
			 String userString = null;
			 String personalID = null;
			 String tariffTypeString = null;

			 division = financeEntry.getDivision();
			 if (division != null)
			 	divisionString = division.getName();
			 group = financeEntry.getGroup();
			 if (group != null)
			 	groupString = group.getName();
			 user = financeEntry.getUser();
			 if (user != null) {
			 	userString = user.getName();
			 	personalID = user.getPersonalID();
			 	if (personalID != null && personalID.length() == 10) {
			 		personalID = personalID.substring(0,6)+"-"+personalID.substring(6,10);
			 	}
			 }
			 tariffType = financeEntry.getTariffType();
			 if (tariffType != null)
			 	tariffTypeString = tariffType.getName();
			 
		 		//create a new ReportData for each row
		 		ReportableData data = new ReportableData();
		 		//	add the data to the correct fields/columns
		 		data.addData(divisionField, divisionString );
		 		data.addData(groupField, groupString );
		 		data.addData(nameField, userString );
		 		data.addData(personalIDField, personalID );
		 		data.addData(amountField, new Double(financeEntry.getAmount()) );
		 		data.addData(entryDateField, new IWTimestamp(financeEntry.getDateOfEntry()).getDateString("dd.MM.yy") );
		 		data.addData(itemPriceField, financeEntry.getItemPrice());
		 		data.addData(infoField, financeEntry.getInfo() );
		 		data.addData(tariffTypeField, tariffTypeString );		
			 		
		 		Integer divisionKey = new Integer(-1);
		 		if (division != null)
		 		    divisionKey = (Integer)division.getPrimaryKey();
		 		List statsForDivision = (List) financeEntriesByDivisions.get(divisionKey);
		 		if (statsForDivision == null)
		 			statsForDivision = new Vector();
		 		statsForDivision.add(data);
		 		financeEntriesByDivisions.put(divisionKey, statsForDivision);
		 } 
		 // iterate through the ordered map and ordered lists and add to the final collection
		 Iterator statsDataIter = financeEntriesByDivisions.keySet().iterator();
		 while (statsDataIter.hasNext()) {
		 
		 	 List datas = (List) financeEntriesByDivisions.get(statsDataIter.next());
		  	 // don't forget to add the row to the collection
		 	 reportCollection.addAll(datas);
		 }
	
		 Comparator dateComparator = new DateComparator();
		 ReportableField[] sortFields = new ReportableField[] {divisionField, entryDateField, groupField, nameField, personalIDField };
		 Comparator[] comparators = new Comparator[] {null, dateComparator, null, null, null};
		 Comparator comparator = new FieldsComparator(sortFields, comparators);
		 Collections.sort(reportCollection, comparator);
		 
		 //finished return the collection
		return reportCollection;
	}
	
	/*
	 * Report A29.2 of the ISI Specs
	 */
	public ReportableCollection getPaymentOverviewByDivisionsGroupsAndDateIntervalFiltering(
			Date dateFromFilter,
			Date dateToFilter,
			Collection divisionsFilter,
			Collection groupsFilter,
			String personalIDFilter)
	throws RemoteException {
		//initialize stuff
		initializeBundlesIfNeeded();
		ReportableCollection reportCollection = new ReportableCollection();
		Locale currentLocale = this.getUserContext().getCurrentLocale();

		Group club = null;
		try {
			club = getClubForUser(this.getCurrentUser() );
		}
		catch (FinderException e) {
			e.printStackTrace();
		}
		
		//PARAMETES
		//Add extra...because the inputhandlers supply the basic header texts
		if (club != null)
		reportCollection.addExtraHeaderParameter(
				"label_club_name", _iwrb.getLocalizedString(LOCALIZED_CLUB_NAME, "Club name"),
				"club_name", club.getName());

		reportCollection.addExtraHeaderParameter(
				"label_current_date", _iwrb.getLocalizedString(LOCALIZED_CURRENT_DATE, "Current date"),
				"current_date", TextSoap.findAndCut((new IWTimestamp()).getLocaleDateAndTime(currentLocale, IWTimestamp.LONG,IWTimestamp.SHORT),"GMT"));
		
		//PARAMETERS that are also FIELDS
		//data from entity columns, can also be defined with an entity definition, see getClubMemberStatisticsForRegionalUnions method
		//The name you give the field/parameter must not contain spaces or special characters		
		ReportableField divisionField = new ReportableField(FIELD_NAME_DIVISION_NAME, String.class);
		divisionField.setLocalizedName(_iwrb.getLocalizedString(LOCALIZED_DIVISION_NAME, "Division"), currentLocale);
		reportCollection.addField(divisionField);
		
		ReportableField groupField = new ReportableField(FIELD_NAME_GROUP_NAME, String.class);
		groupField.setLocalizedName(_iwrb.getLocalizedString(LOCALIZED_GROUP_NAME, "Group"), currentLocale);
		reportCollection.addField(groupField);
		
		ReportableField nameField = new ReportableField(FIELD_NAME_NAME, String.class);
		nameField.setLocalizedName(_iwrb.getLocalizedString(LOCALIZED_NAME, "Name"), currentLocale);
		reportCollection.addField(nameField);
		
		ReportableField personalIDField = new ReportableField(FIELD_NAME_PERSONAL_ID, String.class);
		personalIDField.setLocalizedName(_iwrb.getLocalizedString(LOCALIZED_PERSONAL_ID, "Personal ID"),currentLocale);
		reportCollection.addField(personalIDField);
		
		ReportableField amountField = new ReportableField(FIELD_NAME_AMOUNT, Double.class);
		amountField.setLocalizedName(_iwrb.getLocalizedString(LOCALIZED_AMOUNT, "Amount"), currentLocale);
		reportCollection.addField(amountField);
		
		ReportableField entryDateField = new ReportableField(FIELD_NAME_DATE_OF_ENTRY, String.class);
		entryDateField.setLocalizedName(_iwrb.getLocalizedString(LOCALIZED_DATE_OF_ENTRY, "Date of entry"), currentLocale);
		reportCollection.addField(entryDateField);
		
		ReportableField paymentTypeField = new ReportableField(FIELD_NAME_PAYMENT_TYPE, String.class);
		paymentTypeField.setLocalizedName(_iwrb.getLocalizedString(LOCALIZED_PAYMENT_TYPE, "Payment type"), currentLocale);
		reportCollection.addField(paymentTypeField);
		
		ReportableField sentField = new ReportableField(FIELD_NAME_SENT, String.class);
		sentField.setLocalizedName(_iwrb.getLocalizedString(LOCALIZED_SENT, "Sent"), currentLocale);
		reportCollection.addField(sentField);
		
		//Gathering data	 
		//then for each division get its financeRecords and
		//create a row and insert into an ordered map
		//then iterate the map and insert into the final report collection.
	
		String[] types = {FinanceEntryBMPBean.TYPE_PAYMENT };
		Collection finEntries = getAccountingBusiness().getFinanceEntriesByDateIntervalDivisionsAndGroups(club, types, dateFromFilter, dateToFilter, divisionsFilter, groupsFilter, personalIDFilter);
		Map financeEntriesByDivisions = new TreeMap();
		Group division = null;
		Group group = null;
		User user = null;
		PaymentType paymentType = null;
		//Iterating through reports and creating report data 
		Iterator iter = finEntries.iterator();
		while (iter.hasNext()) {
			FinanceEntry financeEntry = (FinanceEntry) iter.next();
			String divisionString = null;
			String groupString = null;
			String userString = null;
			String personalID = null;
			String paymentTypeString = null;

			division = financeEntry.getDivision();
			if (division != null)
				divisionString = division.getName();
			group = financeEntry.getGroup();
			if (group != null)
				groupString = group.getName();
			user = financeEntry.getUser();
			if (user != null) {
				userString = user.getName();
				personalID = user.getPersonalID();
				if (personalID != null && personalID.length() == 10) {
					personalID = personalID.substring(0,6)+"-"+personalID.substring(6,10);
				}
			}
			paymentType = financeEntry.getPaymentType();
			if (paymentType != null)
				paymentTypeString = _iwrb.getLocalizedString(paymentType.getLocalizationKey(), paymentType.getName());
			
			//create a new ReportData for each row
			ReportableData data = new ReportableData();
			//	add the data to the correct fields/columns
			data.addData(divisionField, divisionString );
			data.addData(groupField, groupString );
			data.addData(nameField, userString );
			data.addData(personalIDField, personalID );
			data.addData(amountField, new Double(financeEntry.getAmount()) );
			data.addData(entryDateField, new IWTimestamp(financeEntry.getDateOfEntry()).getDateString("dd.MM.yy") );
			data.addData(paymentTypeField, paymentTypeString );
			data.addData(sentField, financeEntry.getSent()?"J�":"Nei" );
			
			Integer divisionKey = new Integer(-1);
	 		if (division != null)
	 		    divisionKey = (Integer)division.getPrimaryKey();
	 		List statsForDivision = (List) financeEntriesByDivisions.get(divisionKey);
			if (statsForDivision == null)
				statsForDivision = new Vector();
			statsForDivision.add(data);
	 		financeEntriesByDivisions.put(divisionKey, statsForDivision);
		} 
		// iterate through the ordered map and ordered lists and add to the final collection
		Iterator statsDataIter = financeEntriesByDivisions.keySet().iterator();
		while (statsDataIter.hasNext()) {
			
			List datas = (List) financeEntriesByDivisions.get(statsDataIter.next());
			// don't forget to add the row to the collection
			reportCollection.addAll(datas);
		}
		
		Comparator dateComparator = new DateComparator();
		ReportableField[] sortFields = new ReportableField[] {divisionField, entryDateField, groupField, nameField, personalIDField, paymentTypeField};
		Comparator[] comparators = new Comparator[] {null, dateComparator, null, null, null, null};
		Comparator comparator = new FieldsComparator(sortFields, comparators);
		Collections.sort(reportCollection, comparator);
		
		//finished return the collection
		return reportCollection;
	}
	
	/*
	 * Report A29.3 of the ISI Specs
	 */
	public ReportableCollection getDebtOverviewByDivisionsGroupsAndDateIntervalFiltering(
			Date dateFromFilter,
			Date dateToFilter,
			Collection divisionsFilter,
			Collection groupsFilter,
			String personalIDFilter)
	throws RemoteException {
		//initialize stuff
		initializeBundlesIfNeeded();
		ReportableCollection reportCollection = new ReportableCollection();
		Locale currentLocale = this.getUserContext().getCurrentLocale();

		Group club = null;
		try {
			club = getClubForUser(this.getCurrentUser() );
		}
		catch (FinderException e) {
			e.printStackTrace();
		}
		
		//PARAMETES
		//Add extra...because the inputhandlers supply the basic header texts
		if (club != null)
		reportCollection.addExtraHeaderParameter(
				"label_club_name", _iwrb.getLocalizedString(LOCALIZED_CLUB_NAME, "Club name"),
				"club_name", club.getName());

		reportCollection.addExtraHeaderParameter(
				"label_current_date", _iwrb.getLocalizedString(LOCALIZED_CURRENT_DATE, "Current date"),
				"current_date", TextSoap.findAndCut((new IWTimestamp()).getLocaleDateAndTime(currentLocale, IWTimestamp.LONG,IWTimestamp.SHORT),"GMT"));
		
		//PARAMETERS that are also FIELDS
		//data from entity columns, can also be defined with an entity definition, see getClubMemberStatisticsForRegionalUnions method
		//The name you give the field/parameter must not contain spaces or special characters		
		ReportableField divisionField = new ReportableField(FIELD_NAME_DIVISION_NAME, String.class);
		divisionField.setLocalizedName(_iwrb.getLocalizedString(LOCALIZED_DIVISION_NAME, "Division"), currentLocale);
		reportCollection.addField(divisionField);
		
		ReportableField groupField = new ReportableField(FIELD_NAME_GROUP_NAME, String.class);
		groupField.setLocalizedName(_iwrb.getLocalizedString(LOCALIZED_GROUP_NAME, "Group"), currentLocale);
		reportCollection.addField(groupField);
		
		ReportableField nameField = new ReportableField(FIELD_NAME_NAME, String.class);
		nameField.setLocalizedName(_iwrb.getLocalizedString(LOCALIZED_NAME, "Name"), currentLocale);
		reportCollection.addField(nameField);
		
		ReportableField personalIDField = new ReportableField(FIELD_NAME_PERSONAL_ID, String.class);
		personalIDField.setLocalizedName(_iwrb.getLocalizedString(LOCALIZED_PERSONAL_ID, "Personal ID"),currentLocale);
		reportCollection.addField(personalIDField);
		
		ReportableField phoneField = new ReportableField(FIELD_NAME_PHONE, Double.class);
		phoneField.setLocalizedName(_iwrb.getLocalizedString(LOCALIZED_PHONE, "Phone"), currentLocale);
		reportCollection.addField(phoneField);
		
		ReportableField amountField = new ReportableField(FIELD_NAME_AMOUNT, Double.class);
		amountField.setLocalizedName(_iwrb.getLocalizedString(LOCALIZED_AMOUNT, "Amount"), currentLocale);
		reportCollection.addField(amountField);
		
		ReportableField entryDateField = new ReportableField(FIELD_NAME_DATE_OF_ENTRY, String.class);
		entryDateField.setLocalizedName(_iwrb.getLocalizedString(LOCALIZED_DATE_OF_ENTRY, "Date of entry"), currentLocale);
		reportCollection.addField(entryDateField);
		
		ReportableField itemPriceField = new ReportableField(FIELD_NAME_ITEM_PRICE, Double.class);
		itemPriceField.setLocalizedName(_iwrb.getLocalizedString(LOCALIZED_ITEM_PRICE, "Item price"), currentLocale);
		reportCollection.addField(itemPriceField);
		
		ReportableField tariffTypeField = new ReportableField(FIELD_NAME_TARIFF_TYPE, String.class);
		tariffTypeField.setLocalizedName(_iwrb.getLocalizedString(LOCALIZED_TARIFF_TYPE, "Tariff type"), currentLocale);
		reportCollection.addField(tariffTypeField);
		
		ReportableField infoField = new ReportableField(FIELD_NAME_INFO, String.class);
		infoField.setLocalizedName(_iwrb.getLocalizedString(LOCALIZED_INFO, "Info"), currentLocale);
		reportCollection.addField(infoField);
		
		//Gathering data	 
		//then for each division get its financeRecords and
		//create a row and insert into an ordered map
		//then iterate the map and insert into the final report collection.
		
		String[] types = { FinanceEntryBMPBean.TYPE_ASSESSMENT, FinanceEntryBMPBean.TYPE_MANUAL};
		Collection finEntries = getAccountingBusiness().getFinanceEntriesByDateIntervalDivisionsAndGroups(club, types, dateFromFilter, dateToFilter, divisionsFilter, groupsFilter, personalIDFilter);
		Map financeEntriesByPersons = new TreeMap();
		Group division = null;
		Group group = null;
		User user = null;
		ClubTariffType tariffType = null;
		//Iterating through reports and creating report data 
		
		Iterator iter = finEntries.iterator();
		while (iter.hasNext()) {
			FinanceEntry financeEntry = (FinanceEntry) iter.next();
			String divisionString = null;
			String groupString = null;
			String userString = null;
			String personalID = null;
			String phoneNumber = null;
			String tariffTypeString = null;

			division = financeEntry.getDivision();
			if (division != null)
				divisionString = division.getName();
			group = financeEntry.getGroup();
			if (group != null)
				groupString = group.getName();
			user = financeEntry.getUser();
			if (user != null) {
				userString = user.getName();
				personalID = user.getPersonalID();
				if (personalID != null && personalID.length() == 10) {
					personalID = personalID.substring(0,6)+"-"+personalID.substring(6,10);
				}
				phoneNumber = getPhoneNumber(user);
			}
			tariffType = financeEntry.getTariffType();
			if (tariffType != null)
				tariffTypeString = tariffType.getName();

			//create a new ReportData for each row
			ReportableData data = new ReportableData();
			//	add the data to the correct fields/columns
			data.addData(divisionField, divisionString );
			data.addData(groupField, groupString );
			data.addData(nameField, userString );
			data.addData(personalIDField, personalID );
			data.addData(phoneField, phoneNumber );
			data.addData(amountField, new Double(financeEntry.getAmount()) );
			data.addData(entryDateField, new IWTimestamp(financeEntry.getDateOfEntry()).getDateString("dd.MM.yy")  );
			data.addData(itemPriceField, financeEntry.getItemPrice());
			data.addData(infoField, financeEntry.getInfo() );
			data.addData(tariffTypeField, tariffTypeString );		
			
			List statsForPersons = (List) financeEntriesByPersons.get(personalID);
			if (statsForPersons == null)
				statsForPersons = new Vector();
			statsForPersons.add(data);
			financeEntriesByPersons.put(personalID, statsForPersons);
		} 
		// iterate through the ordered map and ordered lists and add to the final collection
		Iterator statsDataIter = financeEntriesByPersons.keySet().iterator();
		while (statsDataIter.hasNext()) {
			
			List datas = (List) financeEntriesByPersons.get(statsDataIter.next());
			// don't forget to add the row to the collection
			reportCollection.addAll(datas);
		}
		Comparator dateComparator = new DateComparator();
		ReportableField[] sortFields = new ReportableField[] {nameField, entryDateField, personalIDField, divisionField, groupField };
		Comparator[] comparators = new Comparator[] {null, dateComparator, null, null, null};
		Comparator comparator = new FieldsComparator(sortFields, comparators);
		Collections.sort(reportCollection, comparator);
		
		//finished return the collection
		return reportCollection;
	}
	
	/*
	 * Report A29.4 of the ISI Specs
	 */
	public ReportableCollection getEntryOverviewByDivisionsGroupsAndDateIntervalFiltering(
			Date dateFromFilter,
			Date dateToFilter,
			Collection divisionsFilter,
			Collection groupsFilter,
			String personalIDFilter)
	throws RemoteException {
		//initialize stuff
		initializeBundlesIfNeeded();
		ReportableCollection reportCollection = new ReportableCollection();
		Locale currentLocale = this.getUserContext().getCurrentLocale();

		Group club = null;
		try {
			club = getClubForUser(this.getCurrentUser() );
		}
		catch (FinderException e) {
			e.printStackTrace();
		}
		
		//PARAMETES
		//Add extra...because the inputhandlers supply the basic header texts
		if (club != null) {
			reportCollection.addExtraHeaderParameter(
				"label_club_name", _iwrb.getLocalizedString(LOCALIZED_CLUB_NAME, "Club name"),
				"club_name", club.getName());
		}

		reportCollection.addExtraHeaderParameter(
				"label_current_date", _iwrb.getLocalizedString(LOCALIZED_CURRENT_DATE, "Current date"),
				"current_date", TextSoap.findAndCut((new IWTimestamp()).getLocaleDateAndTime(currentLocale, IWTimestamp.LONG,IWTimestamp.SHORT),"GMT"));
		
		//PARAMETERS that are also FIELDS
		//data from entity columns, can also be defined with an entity definition, see getClubMemberStatisticsForRegionalUnions method
		//The name you give the field/parameter must not contain spaces or special characters		
		ReportableField divisionField = new ReportableField(FIELD_NAME_DIVISION_NAME, String.class);
		divisionField.setLocalizedName(_iwrb.getLocalizedString(LOCALIZED_DIVISION_NAME, "Division"), currentLocale);
		reportCollection.addField(divisionField);
		
		ReportableField groupField = new ReportableField(FIELD_NAME_GROUP_NAME, String.class);
		groupField.setLocalizedName(_iwrb.getLocalizedString(LOCALIZED_GROUP_NAME, "Group"), currentLocale);
		reportCollection.addField(groupField);
		
		ReportableField nameField = new ReportableField(FIELD_NAME_NAME, String.class);
		nameField.setLocalizedName(_iwrb.getLocalizedString(LOCALIZED_NAME, "Name"), currentLocale);
		reportCollection.addField(nameField);
		
		ReportableField personalIDField = new ReportableField(FIELD_NAME_PERSONAL_ID, String.class);
		personalIDField.setLocalizedName(_iwrb.getLocalizedString(LOCALIZED_PERSONAL_ID, "Personal ID"),currentLocale);
		reportCollection.addField(personalIDField);
		
		ReportableField phoneField = new ReportableField(FIELD_NAME_PHONE, Double.class);
		phoneField.setLocalizedName(_iwrb.getLocalizedString(LOCALIZED_PHONE, "Phone"), currentLocale);
		reportCollection.addField(phoneField);
		
		ReportableField amountField = new ReportableField(FIELD_NAME_AMOUNT, Double.class);
		amountField.setLocalizedName(_iwrb.getLocalizedString(LOCALIZED_AMOUNT, "Amount"), currentLocale);
		reportCollection.addField(amountField);
		
		ReportableField entryDateField = new ReportableField(FIELD_NAME_DATE_OF_ENTRY, String.class);
		entryDateField.setLocalizedName(_iwrb.getLocalizedString(LOCALIZED_DATE_OF_ENTRY, "Date of entry"), currentLocale);
		reportCollection.addField(entryDateField);
		
		ReportableField tariffTypeField = new ReportableField(FIELD_NAME_TARIFF_TYPE, String.class);
		tariffTypeField.setLocalizedName(_iwrb.getLocalizedString(LOCALIZED_TARIFF_TYPE, "Tariff type"), currentLocale);
		reportCollection.addField(tariffTypeField);
		
		ReportableField infoField = new ReportableField(FIELD_NAME_INFO, String.class);
		infoField.setLocalizedName(_iwrb.getLocalizedString(LOCALIZED_INFO, "Info"), currentLocale);
		reportCollection.addField(infoField);
		
		//Gathering data	 
		//then for each division get its financeRecords and
		//create a row and insert into an ordered map
		//then iterate the map and insert into the final report collection.
		
		String[] types = null;
		Collection finEntries = getAccountingBusiness().getFinanceEntriesByDateIntervalDivisionsAndGroups(club, types, dateFromFilter, dateToFilter, divisionsFilter, groupsFilter, personalIDFilter);
		Map financeEntriesByDivisions = new TreeMap();
		
		//Iterating through reports and creating report data 
		Group division = null;
		Group group = null;
		User user = null;
		ClubTariffType tariffType = null;
		Iterator iter = finEntries.iterator();
		while (iter.hasNext()) {
			FinanceEntry financeEntry = (FinanceEntry) iter.next();
			String divisionString = null;
			String groupString = null;
			String userString = null;
			String personalID = null;
			String phoneNumber = null;
			String tariffTypeString = null;

			division = financeEntry.getDivision();
			if (division != null)
				divisionString = division.getName();
			group = financeEntry.getGroup();
			if (group != null)
				groupString = group.getName();
			user = financeEntry.getUser();
			if (user != null) {
				userString = user.getName();
				personalID = user.getPersonalID();
				if (personalID != null && personalID.length() == 10) {
					personalID = personalID.substring(0,6)+"-"+personalID.substring(6,10);
				}
				phoneNumber = getPhoneNumber(user);
			}
			tariffType = financeEntry.getTariffType();
			if (tariffType != null)
				tariffTypeString = tariffType.getName();
			
			//create a new ReportData for each row
			ReportableData data = new ReportableData();
			//	add the data to the correct fields/columns
			data.addData(divisionField, divisionString );
			data.addData(groupField, groupString );
			data.addData(nameField, userString );
			data.addData(personalIDField, personalID );
			data.addData(phoneField, phoneNumber );
			data.addData(amountField, new Double(financeEntry.getAmount()) );
			data.addData(entryDateField, new IWTimestamp(financeEntry.getDateOfEntry()).getDateString("dd.MM.yy")  );			
			data.addData(infoField, financeEntry.getInfo() );
			data.addData(tariffTypeField, tariffTypeString );		
			
			Integer divisionKey = new Integer(-1);
	 		if (division != null)
	 		    divisionKey = (Integer)division.getPrimaryKey();
	 		List statsForDivision = (List) financeEntriesByDivisions.get(divisionKey);
			if (statsForDivision == null)
				statsForDivision = new Vector();
			statsForDivision.add(data);
	 		financeEntriesByDivisions.put(divisionKey, statsForDivision);
		} 
		// iterate through the ordered map and ordered lists and add to the final collection
		Iterator statsDataIter = financeEntriesByDivisions.keySet().iterator();
		while (statsDataIter.hasNext()) {
			
			List datas = (List) financeEntriesByDivisions.get(statsDataIter.next());
			// don't forget to add the row to the collection
			reportCollection.addAll(datas);
		}
		Comparator dateComparator = new DateComparator();
		ReportableField[] sortFields = new ReportableField[] {divisionField, entryDateField, groupField, nameField, personalIDField };
		Comparator[] comparators = new Comparator[] {null, dateComparator, null, null, null};
		Comparator comparator = new FieldsComparator(sortFields, comparators);
		Collections.sort(reportCollection, comparator);
		
		//finished return the collection
		return reportCollection;
	}
	
	/*
	 * Report A29.5 of the ISI Specs
	 */
	public ReportableCollection getLatePaymentListByDivisionsGroupsAndDateIntervalFiltering(
			Collection divisionsFilter,
			Collection groupsFilter,
			String personalIDFilter,
			String order)
	throws RemoteException {
		//initialize stuff
		initializeBundlesIfNeeded();
		ReportableCollection reportCollection = new ReportableCollection();
		Locale currentLocale = this.getUserContext().getCurrentLocale();

		Group club = null;
		try {
			club = getClubForUser(this.getCurrentUser() );
		}
		catch (FinderException e) {
			e.printStackTrace();
		}
		
		//PARAMETES
		//Add extra...because the inputhandlers supply the basic header texts
		if (club != null)
		reportCollection.addExtraHeaderParameter(
				"label_club_name", _iwrb.getLocalizedString(LOCALIZED_CLUB_NAME, "Club name"),
				"club_name", club.getName());

		reportCollection.addExtraHeaderParameter(
				"label_current_date", _iwrb.getLocalizedString(LOCALIZED_CURRENT_DATE, "Current date"),
				"current_date", TextSoap.findAndCut((new IWTimestamp()).getLocaleDateAndTime(currentLocale, IWTimestamp.LONG,IWTimestamp.SHORT),"GMT"));
		
		//PARAMETERS that are also FIELDS
		//data from entity columns, can also be defined with an entity definition, see getClubMemberStatisticsForRegionalUnions method
		//The name you give the field/parameter must not contain spaces or special characters		
		ReportableField divisionField = new ReportableField(FIELD_NAME_DIVISION_NAME, String.class);
		divisionField.setLocalizedName(_iwrb.getLocalizedString(LOCALIZED_DIVISION_NAME, "Division"), currentLocale);
		reportCollection.addField(divisionField);
		
		ReportableField groupField = new ReportableField(FIELD_NAME_GROUP_NAME, String.class);
		groupField.setLocalizedName(_iwrb.getLocalizedString(LOCALIZED_GROUP_NAME, "Group"), currentLocale);
		reportCollection.addField(groupField);
		
		ReportableField nameField = new ReportableField(FIELD_NAME_NAME, String.class);
		nameField.setLocalizedName(_iwrb.getLocalizedString(LOCALIZED_NAME, "Name"), currentLocale);
		reportCollection.addField(nameField);
		
		ReportableField personalIDField = new ReportableField(FIELD_NAME_PERSONAL_ID, String.class);
		personalIDField.setLocalizedName(_iwrb.getLocalizedString(LOCALIZED_PERSONAL_ID, "Personal ID"),currentLocale);
		reportCollection.addField(personalIDField);
		
		ReportableField phoneField = new ReportableField(FIELD_NAME_PHONE, Double.class);
		phoneField.setLocalizedName(_iwrb.getLocalizedString(LOCALIZED_PHONE, "Phone"), currentLocale);
		reportCollection.addField(phoneField);
		
		ReportableField amountField = new ReportableField(FIELD_NAME_AMOUNT, Double.class);
		amountField.setLocalizedName(_iwrb.getLocalizedString(LOCALIZED_AMOUNT, "Amount"), currentLocale);
		reportCollection.addField(amountField);
		
		ReportableField paymentDateField = new ReportableField(FIELD_NAME_PAYMENT_DATE, String.class);
		paymentDateField.setLocalizedName(_iwrb.getLocalizedString(LOCALIZED_PAYMENT_DATE, "Payment date"), currentLocale);
		reportCollection.addField(paymentDateField);
		
		ReportableField tariffTypeField = new ReportableField(FIELD_NAME_TARIFF_TYPE, String.class);
		tariffTypeField.setLocalizedName(_iwrb.getLocalizedString(LOCALIZED_TARIFF_TYPE, "Tariff type"), currentLocale);
		reportCollection.addField(tariffTypeField);
		
		ReportableField infoField = new ReportableField(FIELD_NAME_INFO, String.class);
		infoField.setLocalizedName(_iwrb.getLocalizedString(LOCALIZED_INFO, "Info"), currentLocale);
		reportCollection.addField(infoField);
		
		//Gathering data	 
		//then for each division get its financeRecords and
		//create a row and insert into an ordered map
		//then iterate the map and insert into the final report collection.
		
		String[] types = null;
		Collection finEntries = getAccountingBusiness().getFinanceEntriesByPaymentDateDivisionsAndGroups(club, types, divisionsFilter, groupsFilter, personalIDFilter);
		Map financeEntriesByDivisions = new TreeMap();
		
		//Iterating through reports and creating report data 
		Iterator iter = finEntries.iterator();
		Group division = null;
		Group group = null;
		User user = null;
		AssessmentRound assmRnd = null;
		ClubTariffType tariffType = null;
		
		while (iter.hasNext()) {
			FinanceEntry financeEntry = (FinanceEntry) iter.next();
			String divisionString = null;
			String groupString = null;
			String userString = null;
			String personalID = null;
			String phoneNumber = null;
			String tariffTypeString = null;

			division = financeEntry.getDivision();
			if (division != null)
				divisionString = division.getName();
			group = financeEntry.getGroup();
			if (group != null)
				groupString = group.getName();
			user = financeEntry.getUser();
			if (user != null) {
				userString = user.getName();
				personalID = user.getPersonalID();
				if (personalID != null && personalID.length() == 10) {
					personalID = personalID.substring(0,6)+"-"+personalID.substring(6,10);
				}
				phoneNumber = getPhoneNumber(user);
			}
			assmRnd = financeEntry.getAssessmentRound();
			tariffType = financeEntry.getTariffType();
			if (tariffType != null)
				tariffTypeString = tariffType.getName();

			//create a new ReportData for each row
			ReportableData data = new ReportableData();
			//	add the data to the correct fields/columns
			data.addData(divisionField, divisionString );
			data.addData(groupField, groupString );
			data.addData(nameField, userString );
			data.addData(personalIDField, personalID );
			data.addData(phoneField, phoneNumber );
			data.addData(amountField, new Double(financeEntry.getAmount()) );
			data.addData(paymentDateField, new IWTimestamp(assmRnd.getPaymentDate()).getDateString("dd.MM.yy") );  			
			data.addData(infoField, financeEntry.getInfo() );
			data.addData(tariffTypeField, tariffTypeString );		
			
			Integer divisionKey = new Integer(-1);
	 		if (division != null)
	 		    divisionKey = (Integer)division.getPrimaryKey();
	 		List statsForDivision = (List) financeEntriesByDivisions.get(divisionKey);
			if (statsForDivision == null)
				statsForDivision = new Vector();
			statsForDivision.add(data);
	 		financeEntriesByDivisions.put(divisionKey, statsForDivision);
		} 
		// iterate through the ordered map and ordered lists and add to the final collection
		Iterator statsDataIter = financeEntriesByDivisions.keySet().iterator();
		while (statsDataIter.hasNext()) {
			
			List datas = (List) financeEntriesByDivisions.get(statsDataIter.next());
			// don't forget to add the row to the collection
			reportCollection.addAll(datas);
		}
		
		Comparator dateComparator = new DateComparator();
		ReportableField[] sortFields = null;
		Comparator[] comparators = null;
		if (order.equals(IWMemberConstants.ORDER_BY_NAME)) {
			sortFields = new ReportableField[] {divisionField, nameField, personalIDField, groupField, paymentDateField};
			comparators = new Comparator[] {null, null, null, null, dateComparator};
		}
		else if (order.equals(IWMemberConstants.ORDER_BY_GROUP_NAME)) {
			sortFields = new ReportableField[] {divisionField, groupField, nameField, personalIDField, paymentDateField};
			comparators = new Comparator[] {null, null, null, null, dateComparator};
		}
		else if (order.equals(IWMemberConstants.ORDER_BY_ENTRY_DATE)) {
			sortFields = new ReportableField[] {divisionField, paymentDateField, groupField, nameField, personalIDField};
			comparators = new Comparator[] {null, dateComparator, null, null, null};
		}

		Comparator comparator = new FieldsComparator(sortFields, comparators);
		Collections.sort(reportCollection, comparator);
		
		//finished return the collection
		return reportCollection;
	}
	
	/*
	 * Report A29.6 of the ISI Specs
	 */
	public ReportableCollection getPaymentListByDivisionsGroupsAndDateIntervalFiltering(
			Date entryDateFilter,
			Date dateToFilter,
			Collection divisionsFilter,
			Collection groupsFilter,
			String personalIDFilter)
	throws RemoteException {
		//initialize stuff
		initializeBundlesIfNeeded();
		ReportableCollection reportCollection = new ReportableCollection();
		Locale currentLocale = this.getUserContext().getCurrentLocale();

		Group club = null;
		try {
			club = getClubForUser(this.getCurrentUser() );
		}
		catch (FinderException e) {
			e.printStackTrace();
		}
		
		//PARAMETES
		//Add extra...because the inputhandlers supply the basic header texts
		if (club != null)
		reportCollection.addExtraHeaderParameter(
				"label_club_name", _iwrb.getLocalizedString(LOCALIZED_CLUB_NAME, "Club name"),
				"club_name", club.getName());

		reportCollection.addExtraHeaderParameter(
				"label_current_date", _iwrb.getLocalizedString(LOCALIZED_CURRENT_DATE, "Current date"),
				"current_date", TextSoap.findAndCut((new IWTimestamp()).getLocaleDateAndTime(currentLocale, IWTimestamp.LONG,IWTimestamp.SHORT),"GMT"));
		
		//PARAMETERS that are also FIELDS
		//data from entity columns, can also be defined with an entity definition, see getClubMemberStatisticsForRegionalUnions method
		//The name you give the field/parameter must not contain spaces or special characters		
		ReportableField divisionField = new ReportableField(FIELD_NAME_DIVISION_NAME, String.class);
		divisionField.setLocalizedName(_iwrb.getLocalizedString(LOCALIZED_DIVISION_NAME, "Division"), currentLocale);
		reportCollection.addField(divisionField);
		
		ReportableField groupField = new ReportableField(FIELD_NAME_GROUP_NAME, String.class);
		groupField.setLocalizedName(_iwrb.getLocalizedString(LOCALIZED_GROUP_NAME, "Group"), currentLocale);
		reportCollection.addField(groupField);
		
		ReportableField nameField = new ReportableField(FIELD_NAME_NAME, String.class);
		nameField.setLocalizedName(_iwrb.getLocalizedString(LOCALIZED_NAME, "Name"), currentLocale);
		reportCollection.addField(nameField);
		
		ReportableField personalIDField = new ReportableField(FIELD_NAME_PERSONAL_ID, String.class);
		personalIDField.setLocalizedName(_iwrb.getLocalizedString(LOCALIZED_PERSONAL_ID, "Personal ID"),currentLocale);
		reportCollection.addField(personalIDField);
		
		ReportableField amountField = new ReportableField(FIELD_NAME_AMOUNT, Double.class);
		amountField.setLocalizedName(_iwrb.getLocalizedString(LOCALIZED_AMOUNT, "Amount"), currentLocale);
		reportCollection.addField(amountField);
		
		ReportableField entryDateField = new ReportableField(FIELD_NAME_DATE_OF_ENTRY, String.class);
		entryDateField.setLocalizedName(_iwrb.getLocalizedString(LOCALIZED_DATE_OF_ENTRY, "Date of entry"), currentLocale);
		reportCollection.addField(entryDateField);
		
		ReportableField paymentTypeField = new ReportableField(FIELD_NAME_PAYMENT_TYPE, String.class);
		paymentTypeField.setLocalizedName(_iwrb.getLocalizedString(LOCALIZED_PAYMENT_TYPE, "Payment type"), currentLocale);
		reportCollection.addField(paymentTypeField);
		
		ReportableField tariffTypeField = new ReportableField(FIELD_NAME_TARIFF_TYPE, String.class);
		tariffTypeField.setLocalizedName(_iwrb.getLocalizedString(LOCALIZED_TARIFF_TYPE, "Tariff type"), currentLocale);
		reportCollection.addField(tariffTypeField);
		
		ReportableField infoField = new ReportableField(FIELD_NAME_INFO, String.class);
		infoField.setLocalizedName(_iwrb.getLocalizedString(LOCALIZED_INFO, "Info"), currentLocale);
		reportCollection.addField(infoField);
		
		//Gathering data	 
		//then for each division get its financeRecords and
		//create a row and insert into an ordered map
		//then iterate the map and insert into the final report collection.
		
		String[] types = {FinanceEntryBMPBean.TYPE_PAYMENT};
		Collection finEntries = getAccountingBusiness().getFinanceEntriesByDateIntervalDivisionsAndGroups(club, types, entryDateFilter, dateToFilter, divisionsFilter, groupsFilter, personalIDFilter);
		Map financeEntriesByDate = new TreeMap();
		
		//Iterating through reports and creating report data
		Group division = null;
		Group group = null;
		User user = null;
		PaymentType paymentType = null;
		ClubTariffType tariffType = null;
		Iterator iter = finEntries.iterator();
		while (iter.hasNext()) {
			FinanceEntry financeEntry = (FinanceEntry) iter.next();
			String divisionString = null;
			String groupString = null;
			String userString = null;
			String personalID = null;
			String paymentTypeString = null;
			String tariffTypeString = null;
			//String dateOfEntryString = new IWTimestamp(financeEntry.getDateOfEntry()).getDateString("dd.MM.yy");
			String dateOfEntryString = TextSoap.findAndCut((new IWTimestamp(financeEntry.getDateOfEntry())).getLocaleDate(currentLocale, IWTimestamp.LONG),"GMT");
			division = financeEntry.getDivision();
			if (division != null)
				divisionString = division.getName();
			group = financeEntry.getGroup();
			if (group != null)
				groupString = group.getName();
			user = financeEntry.getUser();
			if (user != null) {
				userString = user.getName();
				personalID = user.getPersonalID();
				if (personalID != null && personalID.length() == 10) {
					personalID = personalID.substring(0,6)+"-"+personalID.substring(6,10);
				}
			}
			paymentType = financeEntry.getPaymentType();
			if (paymentType != null)
				paymentTypeString = _iwrb.getLocalizedString(paymentType.getLocalizationKey(), paymentType.getName());
			tariffType = financeEntry.getTariffType();
			if (tariffType != null)
				tariffTypeString = tariffType.getName();
			
			//create a new ReportData for each row
			ReportableData data = new ReportableData();
			//	add the data to the correct fields/columns
			data.addData(divisionField, divisionString );
			data.addData(groupField, groupString );
			data.addData(nameField, userString );
			data.addData(personalIDField, personalID );
			data.addData(amountField, new Double(financeEntry.getAmount()) );
			data.addData(paymentTypeField, paymentTypeString );
			data.addData(infoField, financeEntry.getInfo() );
			data.addData(tariffTypeField, tariffTypeString );
			data.addData(entryDateField, dateOfEntryString );
			
			List statsForDay = (List) financeEntriesByDate.get(dateOfEntryString);
			if (statsForDay == null)
				statsForDay = new Vector();
			statsForDay.add(data);
			financeEntriesByDate.put(dateOfEntryString, statsForDay);
		} 
		// iterate through the ordered map and ordered lists and add to the final collection
		Iterator statsDataIter = financeEntriesByDate.keySet().iterator();
		while (statsDataIter.hasNext()) {
			
			List datas = (List) financeEntriesByDate.get(statsDataIter.next());
			// don't forget to add the row to the collection
			reportCollection.addAll(datas);
		}
		
		ReportableField[] sortFields = new ReportableField[] {divisionField, paymentTypeField, groupField, nameField, personalIDField };
		Comparator comparator = new FieldsComparator(sortFields);
		Collections.sort(reportCollection, comparator);
		
		//finished return the collection
		return reportCollection;
	}
	
	/*
	 * Returns a club the user is a member of.
	 */
	private Group getClubForUser(User user) throws FinderException, RemoteException{
		Collection parents = getGroupBusiness().getParentGroupsRecursive(user);
		Group club = null;
		if(parents!=null && !parents.isEmpty()){
			Iterator iter = parents.iterator();
			while (iter.hasNext()) {
				club = (Group) iter.next();
				if(IWMemberConstants.GROUP_TYPE_CLUB.equals(club.getGroupType())){
					return club;
				}
			}
		}
		if(club == null){
			//if no club is found we throw the exception
			throw new FinderException(user.getName());
		}

		return club;
	}
	
	private String getPhoneNumber(User user) {
		Collection phones = user.getPhones();
		String phoneNumber = "";
		if (!phones.isEmpty()) {
			Phone phone = null;
			int tempPhoneType = 0;			
			int selectedPhoneType = 0;
			
			Iterator phIt =	phones.iterator();
			while (phIt.hasNext()) {
				phone = (Phone) phIt.next();
				if (phone != null) {
					tempPhoneType = phone.getPhoneTypeId();
					if (tempPhoneType != PhoneType.FAX_NUMBER_ID) {
						if (tempPhoneType == PhoneType.MOBILE_PHONE_ID) {							
							phoneNumber = phone.getNumber();
							break;
						}
						else if (tempPhoneType == PhoneType.HOME_PHONE_ID && selectedPhoneType != PhoneType.HOME_PHONE_ID) {
							phoneNumber = phone.getNumber();
							selectedPhoneType = phone.getPhoneTypeId();
						}
						else if (tempPhoneType == PhoneType.WORK_PHONE_ID && selectedPhoneType != PhoneType.WORK_PHONE_ID) {
							phoneNumber = phone.getNumber();
							selectedPhoneType = phone.getPhoneTypeId();
						}
					}
				}
			}
		}
		return phoneNumber;
	}
	
	class DateComparator implements Comparator {

		public int compare(Object arg0, Object arg1) {			int comp = 0;
			try {
				String[] sta0 = ((String) arg0).trim().split("\\.");
				String[] sta1 = ((String) arg1).split("\\.");
				String year0 = sta0[2];
				String year1 = sta1[2];
				comp = year0.compareTo(year1);

				if(comp == 0) {
				    String month0 = sta0[1];
					String month1 = sta1[1];
					comp = month0.compareTo(month1);				    
				}
				if(comp == 0) {
				    String day0 = sta0[0];
					String day1 = sta1[0];
					comp = day0.compareTo(day1);
				}
			} 
			catch(Exception e) {
				e.printStackTrace();
			}
			return comp;
		}
	}
}