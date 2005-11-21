package is.idega.idegaweb.campus.presentation;

import is.idega.idegaweb.campus.block.allocation.data.Contract;

import java.rmi.RemoteException;
import java.text.DateFormat;
import java.util.Collection;
import java.util.Iterator;

import javax.ejb.FinderException;

import com.idega.block.application.data.Applicant;
import com.idega.block.application.data.ApplicantHome;
import com.idega.block.building.data.Apartment;
import com.idega.block.building.data.ApartmentView;
import com.idega.block.building.data.ApartmentViewHome;
import com.idega.data.IDOLookup;
import com.idega.data.IDOLookupException;
import com.idega.presentation.IWContext;
import com.idega.presentation.PresentationObject;
import com.idega.presentation.Table;
import com.idega.presentation.text.Link;
import com.idega.presentation.text.Text;
import com.idega.presentation.ui.DataTable;

/**
 * <p>
 * Title:
 * </p>
 * <p>
 * Description:
 * </p>
 * <p>
 * Copyright: Copyright (c) 2002
 * </p>
 * <p>
 * Company:
 * </p>
 * 
 * @author <br>
 *         <a href="mailto:aron@idega.is">Aron Birkir</a><br>
 * @version 1.0
 */

public class PersonalNumberResult extends CampusBlock implements Campus {

	private String SSN = null;

	private Integer applicantID = null;

	private DateFormat df;

	public PersonalNumberResult() {
	}

	public void control(IWContext iwc) {
		parse(iwc);
		add(getSSNResult(iwc));
	}

	public void parse(IWContext iwc) {
		if (iwc.isParameterSet(PersonalNumberSearch.PERSONAL_NUMBER))
			SSN = iwc.getParameter(PersonalNumberSearch.PERSONAL_NUMBER);
		else if (iwc.isParameterSet("appl_info")) {
			applicantID = Integer.valueOf(iwc.getParameter("appl_info"));
		}
	}

	private PresentationObject getSSNResult(IWContext iwc) {
		Table T = new Table();
		int col = 1, row = 1;

		if (SSN != null) {
			try {
				// Collection applicants =
				// ContractFinder.findAllNonContractApplicationsBySSN(SSN);
				Collection applicants = getContractService(iwc)
						.getContractHome().getUnsignedApplicants(SSN);
				if (applicants != null && !applicants.isEmpty()) {
					T.add(getNonContractApplicantInfo(applicants), col, row++);
				}
				Collection contracts = getContractService(iwc)
						.getContractHome().findByPersonalID(SSN);
				if (contracts != null && !contracts.isEmpty()) {
					T.add(getContractInfo(iwc, contracts), col, row++);
				}
			} catch (com.idega.data.IDOFinderException ex) {
				ex.printStackTrace();
				T.add(getErrorText(localize("error_finding_from_ssn",
						"Error in ssn search")), col, row);
			} catch (Exception ex) {
				ex.printStackTrace();
			}
		} else if (applicantID != null) {
			try {
				Applicant applicant = ((ApplicantHome) IDOLookup
						.getHome(Applicant.class))
						.findByPrimaryKey(applicantID);
				T.add(getApplicantInfo(applicant), col, row);
			} catch (IDOLookupException e) {
				e.printStackTrace();
			} catch (FinderException e) {
				e.printStackTrace();
				T.add(getErrorText(localize("error_finding_from_id",
						"Error in id search")), col, row);
			}
		} else
			T.add(getErrorText(localize("warning_no_ssn_provided",
					"No ssn provided")), col, row);

		return T;
	}

	private PresentationObject getNonContractApplicantInfo(Collection applicants) {
		DataTable T = new DataTable();

		T.setUseBottom(false);
		T.setUseTop(false);
		T.addTitle(localize("applicants_without_contracts",
				"Applicants without contracts"));
		T.setTitlesHorizontal(true);
		int col = 1;
		int row = 1;
		T.add(getHeader(localize("id", "ID")), col++, row);
		T.add(getHeader(localize("ssn", "SSN")), col++, row);
		T.add(getHeader(localize("name", "Name")), col++, row);
		// T.add(getHeader(localize("legal_residence","Legal
		// residence")),col++,row);
		// T.add(getHeader(localize("residence","Residence")),col++,row);
		// T.add(getHeader(localize("zip","Zip")),col++,row);
		// T.add(getHeader(localize("phone","Phone")),col++,row);
		col += 4;
		T.add(getHeader(localize("mobile", "Mobile")), col++, row);
		T.add(getHeader(localize("status", "Status")), col++, row);

		Iterator iter = applicants.iterator();
		while (iter.hasNext()) {
			col = 1;
			row++;
			Applicant applicant = (Applicant) iter.next();

			T.add(getApplicantInfoLink(getText(applicant.getPrimaryKey()
					.toString()), (Integer) applicant.getPrimaryKey()), col++,
					row);
			T.add(getText(applicant.getSSN()), col++, row);
			T.add(getText(applicant.getFullName()), col++, row);
			T.add(getText(applicant.getLegalResidence()), col++, row);
			T.add(getText(applicant.getResidence()), col++, row);
			T.add(getText(applicant.getPO()), col++, row);
			T.add(getText(applicant.getResidencePhone()), col++, row);
			T.add(getText(applicant.getMobilePhone()), col++, row);
			T.add(getText(applicant.getStatus()), col++, row);
		}
		return T;
	}

	private PresentationObject getContractInfo(IWContext iwc,
			Collection contracts) throws FinderException, RemoteException {
		DataTable T = new DataTable();
		T.setUseBottom(false);
		T.setUseTop(false);
		T.addTitle(localize("applicants_with_contracts",
				"Applicants with contracts"));
		T.setTitlesHorizontal(true);
		int col = 1;
		int row = 1;
		T.add(getHeader(localize("id", "ID")), col++, row);
		T.add(getHeader(localize("ssn", "SSN")), col++, row);
		T.add(getHeader(localize("name", "Name")), col++, row);
		// T.add(getHeader(localize("address","Address")),col++,row);
		T.add(getHeader(localize("mobile", "Mobile")), col++, row);
		T.add(getHeader(localize("apartment", "Apartment")), col++, row);
		T.add(getHeader(localize("from", "From")), col++, row);
		T.add(getHeader(localize("to", "To")), col++, row);
		T.add(getHeader(localize("status", "Status")), col++, row);

		ApplicantHome ahome = (ApplicantHome) IDOLookup
				.getHomeLegacy(Applicant.class);
		Iterator iter = contracts.iterator();
		ApartmentViewHome avh = (ApartmentViewHome) IDOLookup
				.getHome(ApartmentView.class);
		while (iter.hasNext()) {
			col = 1;
			row++;
			Contract contract = (Contract) iter.next();

			Applicant applicant = contract.getApplicant();
			Apartment apartment = contract.getApartment();
			T.add(getApplicantInfoLink(getText(applicant.getPrimaryKey()
					.toString()), (Integer) applicant.getPrimaryKey()), col++,
					row);
			T.add(getText(applicant.getSSN()), col++, row);
			T.add(getText(applicant.getFullName()), col++, row);
			// T.add(getText(applicant.getLegalResidence()),col++,row);
			T.add(getText(applicant.getMobilePhone()), col++, row);
			T
					.add(getText(apartment.getName() + " "
							+ apartment.getFloor().getBuilding().getName()),
							col++, row);
			T.add(getText(df.format(contract.getValidFrom())), col++, row);
			T.add(getText(df.format(contract.getValidTo())), col++, row);
			T.add(getText(getContractService(iwc).getLocalizedStatus(
					getResourceBundle(), contract.getStatus())), col++, row);

		}
		return T;
	}

	public PresentationObject getApplicantInfo(Applicant applicant) {
		DataTable T = new DataTable();
		T.setUseBottom(false);
		T.setUseTop(false);
		T.addTitle(localize("applicant_info", "Applicant info"));
		T.setTitlesHorizontal(false);
		int col = 1;
		int row = 1;
		T.add(getHeader(localize("id", "ID")), col, row++);
		T.add(getHeader(localize("ssn", "SSN")), col, row++);
		T.add(getHeader(localize("name", "Name")), col, row++);
		T.add(getHeader(localize("legal_residence", "Legal residence")), col,
				row++);
		T.add(getHeader(localize("residence", "Residence")), col, row++);
		T.add(getHeader(localize("zip", "Zip")), col, row++);
		T.add(getHeader(localize("phone", "Phone")), col, row++);
		T.add(getHeader(localize("mobile", "Mobile")), col, row++);

		if (applicant != null) {
			row = 1;
			col++;

			T.add(
					getText(String
							.valueOf(applicant.getPrimaryKey().toString())),
					col, row++);
			T.add(getSSNLink(getText(applicant.getSSN()), applicant.getSSN()),
					col, row++);
			T.add(getText(applicant.getFullName()), col, row++);
			T.add(getText(applicant.getLegalResidence()), col, row++);
			T.add(getText(applicant.getResidence()), col, row++);
			T.add(getText(applicant.getPO()), col, row++);
			T.add(getText(applicant.getResidencePhone()), col, row++);
			T.add(getText(applicant.getMobilePhone()), col, row++);

		}
		return T;
	}

	public Link getApplicantInfoLink(Text text, Integer applicantID) {
		Link L = new Link(text);
		L.addParameter("appl_info", applicantID.toString());
		return L;
	}

	public Link getSSNLink(Text text, String ssn) {
		Link L = new Link(text);
		L.addParameter(PersonalNumberSearch.PERSONAL_NUMBER, ssn);
		return L;
	}

	public void main(IWContext iwc) {

		df = DateFormat.getDateInstance(DateFormat.SHORT, iwc
				.getCurrentLocale());
		control(iwc);
	}
}