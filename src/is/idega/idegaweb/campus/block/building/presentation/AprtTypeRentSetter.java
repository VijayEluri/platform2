package is.idega.idegaweb.campus.block.building.presentation;

import is.idega.idegaweb.campus.block.building.data.ApartmentTypeRent;
import is.idega.idegaweb.campus.block.building.data.ApartmentTypeRentHome;
import is.idega.idegaweb.campus.presentation.CampusBlock;

import java.rmi.RemoteException;
import java.text.DateFormat;
import java.text.NumberFormat;
import java.util.Collection;
import java.util.Iterator;

import javax.ejb.CreateException;
import javax.ejb.EJBException;
import javax.ejb.FinderException;
import javax.ejb.RemoveException;

import com.idega.block.building.data.ApartmentType;
import com.idega.block.building.data.ApartmentTypeHome;
import com.idega.data.IDOLookup;
import com.idega.data.IDOLookupException;
import com.idega.data.IDOStoreException;
import com.idega.presentation.IWContext;
import com.idega.presentation.PresentationObject;
import com.idega.presentation.Table;
import com.idega.presentation.text.Link;
import com.idega.presentation.ui.DataTable;
import com.idega.presentation.ui.DateInput;
import com.idega.presentation.ui.Form;
import com.idega.presentation.ui.HiddenInput;
import com.idega.presentation.ui.RadioButton;
import com.idega.presentation.ui.SubmitButton;
import com.idega.presentation.ui.TextInput;
import com.idega.util.IWTimestamp;
/**
 * Title:   idegaclasses
 * Description:
 * Copyright:    Copyright (c) 2001
 * Company:
 * @author  <a href="mailto:aron@idega.is">aron@idega.is
 * @version 1.0
 */
public class AprtTypeRentSetter extends CampusBlock {
	
	private boolean isAdmin = false;
	private String prmATid = "AT_id";
	private String prmATRid = "ATR_id";
	
	public String getLocalizedNameKey() {
		return "rent";
	}
	public String getLocalizedNameValue() {
		return "rent";
	}
	
	protected void control(IWContext iwc) {
		//debugParameters(iwc);
		if (isAdmin) {
			if (iwc.isParameterSet("create.x") || iwc.isParameterSet("create")) {
				createRent(iwc);
			}
			else if (iwc.isParameterSet("update.x") || iwc.isParameterSet("update")) {
				updateRent(iwc);
			}
			else if (iwc.isParameterSet("delete.x") || iwc.isParameterSet("delete")) {
				deleteRent(iwc);
			}
			if (iwc.isParameterSet(prmATid)) {
				add(getTypeRentForm(iwc));
			}
			else{
				try {
					this.add(getTypeTable());
				}
				catch (RemoteException e) {
					e.printStackTrace();
				}
				catch (FinderException e) {
					e.printStackTrace();
				}
			}
		}
		else
			this.add(getNoAccessObject(iwc));
	}
	public PresentationObject getTypeTable() throws RemoteException,FinderException{
		DataTable T = new DataTable();
		T.addTitle(localize("apartment_types", "Apartment types"));
		T.setTitlesVertical(false);
		Collection types =((ApartmentTypeHome)IDOLookup.getHome(ApartmentType.class)).findAll();
		if (types != null) {
			Iterator iter = types.iterator();
			ApartmentType AT;
			T.add(getHeader(localize("apartment_type", "Apartment type")), 1, 1);
			int row = 2;
			while (iter.hasNext()) {
				AT = (ApartmentType) iter.next();

				Link link = new Link(getHeader(AT.getName()));

				link.addParameter(prmATid, AT.getPrimaryKey().toString());
				T.add(link, 1, row++);
			}
		}
		return T;
	}
	public PresentationObject getTypeRentForm(IWContext iwc) {
		Form F = new Form();
		DataTable T = new DataTable();
		T.setWidth(Table.HUNDRED_PERCENT);
		Table inputTable = new Table(5, 2);
		try {
			String ATid = iwc.getParameter(prmATid);
			String ATRid = iwc.getParameter(prmATRid);
			if (ATid != null) {
				Integer atID = Integer.valueOf(ATid);
				
				ApartmentType AT = ((ApartmentTypeHome)IDOLookup.getHome(ApartmentType.class)).findByPrimaryKey(atID);
				T.addTitle(AT.getName());
				T.setTitlesVertical(false);
				T.add(getHeader(localize("rent", "Rent")), 1, 1);
				T.add(getHeader(localize("other_expenses", "Other expenses")), 2, 1);
				T.add(getHeader(localize("from_date", "From date (D/M/Y)")), 3, 1);
				T.add(getHeader(localize("to_date", "To date (D/M/Y)")), 4, 1);
				T.add(getHeader(localize("choise", "Choice")), 5, 1);
				int row = 2;
				Collection atrs = getAPRHome().findByType(atID.intValue());
				NumberFormat nf = NumberFormat.getInstance();
				DateFormat df = DateFormat.getDateInstance(DateFormat.FULL, iwc.getCurrentLocale());
				RadioButton rb;
				if (atrs != null) {
					Iterator iter = atrs.iterator();
					while (iter.hasNext()) {
						ApartmentTypeRent theRent = (ApartmentTypeRent) iter.next();

						T.add(getHeader(nf.format((double) theRent.getRent())), 1, row);
						T.add(getHeader(nf.format((double) theRent.getOtherExpeneses())), 2, row);
						T.add(getHeader(df.format(theRent.getValidFrom())), 3, row);

						if (theRent.getValidTo() != null)

							T.add(getHeader(df.format(theRent.getValidTo())), 4, row);

						rb = new RadioButton(prmATRid, theRent.getPrimaryKey().toString());
						if(theRent.getPrimaryKey().toString().equals(ATRid))
							rb.setSelected();
						T.add(rb, 5, row);
						row++;
					}
				}
				T.add(new HiddenInput(prmATid, ATid));

				Link btnNew = new Link(getResourceBundle().getLocalizedImageButton("btn_new","New"));

				btnNew.addParameter(prmATid,ATid);
				SubmitButton edit = new SubmitButton(getResourceBundle().getLocalizedImageButton("btn_edit", "Edit"), "edit");
				SubmitButton delete = new SubmitButton(getResourceBundle().getLocalizedImageButton("btn_delete", "Delete"), "delete");
				SubmitButton create = new SubmitButton(getResourceBundle().getLocalizedImageButton("btn_create", "Create"), "create");
				SubmitButton update = new SubmitButton(getResourceBundle().getLocalizedImageButton("btn_update", "Update"), "update");
				T.addButton(btnNew);
				T.addButton(edit);
				T.addButton(delete);
				
				TextInput rent = new TextInput("apr_rent");
				TextInput otherExpenses = new TextInput("apr_other_expenses");
				DateInput from = new DateInput("apr_from");
				DateInput to = new DateInput("apr_to");

				inputTable.add(getHeader(localize("rent", "Rent")), 1, 1);
				inputTable.add(getHeader(localize("other_expenses", "Other expenses")), 2, 1);
				inputTable.add(getHeader(localize("from_date", "From date (D/M/Y)")), 3, 1);
				inputTable.add(getHeader(localize("to_date", "To date (D/M/Y)")), 4, 1);

				inputTable.add(rent, 1, 2);
				inputTable.add(otherExpenses, 2, 2);
				inputTable.add(from, 3, 2);
				inputTable.add(to, 4, 2);
				if (iwc.isParameterSet(prmATRid)) {
					
					if (ATRid != null) {
						ApartmentTypeRent apr = getAPRHome().findByPrimaryKey(ATRid);
						rent.setContent(String.valueOf(apr.getRent()));
						from.setDate(apr.getValidFrom());
						if (apr.getValidTo() != null)
							to.setDate(apr.getValidTo());
					}
					inputTable.add(update, 5, 2);
				}
				else {
					inputTable.add(create, 5, 2);
				}
			}
		}
		catch (NumberFormatException e) {
			e.printStackTrace();
		}
		catch (IDOLookupException e) {
			e.printStackTrace();
		}
		catch (EJBException e) {
			e.printStackTrace();
		}
		catch (FinderException e) {
			e.printStackTrace();
		}
		F.add(T);
		F.add(inputTable);
		return F;
	}
	private boolean updateRent(IWContext iwc) {
		try {
			if (iwc.isParameterSet(prmATRid )&&iwc.isParameterSet("apr_rent") && iwc.isParameterSet("apr_from")) {
				Integer ATid = new Integer(iwc.getParameter(prmATid));
				Integer ATRid = new Integer(iwc.getParameter(prmATRid));
				Float rent = new Float(iwc.getParameter("apr_rent"));
				Double otherExpenses = new Double(iwc.getParameter("apr_other_expenses"));
				IWTimestamp from = new IWTimestamp(iwc.getParameter("apr_from"));
				IWTimestamp to = null;
				if (iwc.isParameterSet("apr_to"))
					to = new IWTimestamp(iwc.getParameter("apr_to"));
				ApartmentTypeRent typeRent = getAPRHome().findByPrimaryKey(ATRid);
				typeRent.setApartmentTypeId(ATid.intValue());
				typeRent.setRent(rent);
				typeRent.setOtherExpenses(otherExpenses.doubleValue());
				typeRent.setValidFrom(from.getDate());
				if (to != null)
					typeRent.setValidTo(to.getDate());
				typeRent.store();
				return true;
			}
		}
		catch (NumberFormatException e) {
			e.printStackTrace();
		}
		catch (IDOLookupException e) {
			e.printStackTrace();
		}
		catch (IDOStoreException e) {
			e.printStackTrace();
		}
		catch (FinderException e) {
			e.printStackTrace();
		}
		return false;
	}
	private boolean createRent(IWContext iwc) {
		try {
			if (iwc.isParameterSet("apr_rent") && iwc.isParameterSet("apr_from")) {
				Integer ATid = new Integer(iwc.getParameter(prmATid));
				Float rent = new Float(iwc.getParameter("apr_rent"));
				Double otherExpenses = new Double(iwc.getParameter("apr_other_expenses"));
				IWTimestamp from = new IWTimestamp(iwc.getParameter("apr_from"));
				IWTimestamp to = null;
				if (iwc.isParameterSet("apr_to"))
					to = new IWTimestamp(iwc.getParameter("apr_to"));
				ApartmentTypeRent typeRent = getAPRHome().create();
				typeRent.setApartmentTypeId(ATid.intValue());
				typeRent.setRent(rent);
				typeRent.setOtherExpenses(otherExpenses.doubleValue());
				typeRent.setValidFrom(from.getDate());
				if (to != null)
					typeRent.setValidTo(to.getDate());
				typeRent.store();
				return true;
			}
		}
		catch (NumberFormatException e) {
			e.printStackTrace();
		}
		catch (IDOLookupException e) {
			e.printStackTrace();
		}
		catch (IDOStoreException e) {
			e.printStackTrace();
		}
		catch (CreateException e) {
			e.printStackTrace();
		}
		return false;
	}
	private boolean deleteRent(IWContext iwc) {
		try {
			if (iwc.isParameterSet(prmATRid)) {
				String ID = iwc.getParameter(prmATRid);
				if (ID != null) {
					getAPRHome().findByPrimaryKey(ID).remove();
					return true;
				}
			}
		}
		catch (IDOLookupException e) {
			e.printStackTrace();
		}
		catch (EJBException e) {
			e.printStackTrace();
		}
		catch (RemoveException e) {
			e.printStackTrace();
		}
		catch (FinderException e) {
			e.printStackTrace();
		}
		return false;
	}
	public ApartmentTypeRentHome getAPRHome() throws IDOLookupException {
		return (ApartmentTypeRentHome) IDOLookup.getHome(ApartmentTypeRent.class);
	}
	public void main(IWContext iwc) {
		isAdmin = iwc.hasEditPermission(this);
		control(iwc);
	}
}
