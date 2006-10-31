/*
 * $Id: ReferenceNumberInfo.java,v 1.42.4.3 2006/10/31 16:24:59 palli Exp $
 *
 * Copyright (C) 2001 Idega hf. All Rights Reserved.
 *
 * This software is the proprietary information of Idega hf.
 * Use is subject to license terms.
 *
 */

package is.idega.idegaweb.campus.block.application.presentation;

import is.idega.idegaweb.campus.block.allocation.business.ContractService;
import is.idega.idegaweb.campus.block.allocation.data.Contract;
import is.idega.idegaweb.campus.block.allocation.data.ContractBMPBean;
import is.idega.idegaweb.campus.block.allocation.data.ContractHome;
import is.idega.idegaweb.campus.block.application.business.ApplicationService;
import is.idega.idegaweb.campus.block.application.business.CampusApplicationHolder;
import is.idega.idegaweb.campus.block.application.data.Applied;
import is.idega.idegaweb.campus.block.application.data.CampusApplication;
import is.idega.idegaweb.campus.block.application.data.WaitingList;
import is.idega.idegaweb.campus.block.application.data.WaitingListHome;
import is.idega.idegaweb.campus.presentation.CampusBlock;

import java.rmi.RemoteException;
import java.text.DateFormat;
import java.util.Collection;
import java.util.Date;
import java.util.Iterator;
import java.util.Vector;

import javax.ejb.EJBException;
import javax.ejb.FinderException;

import com.idega.block.application.data.Applicant;
import com.idega.block.application.data.Application;
import com.idega.block.application.data.ApplicationBMPBean;
import com.idega.block.application.data.ApplicationHome;
import com.idega.block.application.presentation.ReferenceNumber;
import com.idega.block.building.data.ApartmentType;
import com.idega.block.building.data.ApartmentTypeHome;
import com.idega.block.building.data.ApartmentView;
import com.idega.block.building.data.Complex;
import com.idega.block.building.data.ComplexHome;
import com.idega.core.accesscontrol.business.LoginCreator;
import com.idega.core.accesscontrol.business.LoginDBHandler;
import com.idega.core.accesscontrol.data.LoginTable;
import com.idega.data.IDOLookup;
import com.idega.data.IDOLookupException;
import com.idega.data.IDOStoreException;
import com.idega.idegaweb.IWApplicationContext;
import com.idega.idegaweb.IWBundle;
import com.idega.idegaweb.IWResourceBundle;
import com.idega.presentation.IWContext;
import com.idega.presentation.Table;
import com.idega.presentation.text.Text;
import com.idega.presentation.ui.CheckBox;
import com.idega.presentation.ui.Form;
import com.idega.presentation.ui.HiddenInput;
import com.idega.presentation.ui.SubmitButton;
import com.idega.presentation.ui.TextInput;
import com.idega.user.data.User;
import com.idega.util.CypherText;
import com.idega.util.IWTimestamp;

/**
 * 
 * @author <a href="mailto:palli@idega.is">Pall Helgason</a>
 * @version 1.0
 */

public class ReferenceNumberInfo extends CampusBlock {
	// private static final String DUMMY_LOGIN = "DUMMY_LOGIN";

	public static final String SESSION_REFERENCE_NUMBER = "session_ref_num";

	private static final String IW_BUNDLE_IDENTIFIER = "com.idega.block.application";

	private IWResourceBundle _iwrb = null;

	private CampusApplicationHolder holder = null;

	private Integer allocatedTypeID = new Integer(-1);

	private Integer allocatedComplexID = new Integer(-1);

	private Integer allocatedWaitinglistID = null;

	private DateFormat dateFormat, dateTimeFormat;

	private ApplicationService applicationService;

	private int row = 1;

	private String ref = null, refnum = null;

	/**
	 * 
	 */
	public ReferenceNumberInfo() {
	}

	/**
	 * 
	 */
	protected void control(IWContext iwc) throws RemoteException {
		applicationService = getApplicationService(iwc);
		dateTimeFormat = DateFormat.getDateTimeInstance(DateFormat.SHORT,
				DateFormat.SHORT, iwc.getCurrentLocale());
		dateFormat = DateFormat.getDateInstance(DateFormat.SHORT, iwc
				.getCurrentLocale());
		refnum = iwc.getParameter(ReferenceNumber.CAM_REF_NUMBER);
		if (refnum != null && refnum.length() != 10) {
			CypherText cyph = new CypherText(iwc);
			String cypherKey = getCypherKey(iwc);

			ref = cyph.doDeCypher(refnum, cypherKey);
			addReferenceNumberLookupResults(iwc);
		} else {
			addPersonalIDLookupResults(iwc);
		}
	}

	public static String getCypherKey(IWApplicationContext iwc) {
		IWBundle iwb = iwc.getIWMainApplication().getBundle(
				IW_BUNDLE_IDENTIFIER);
		CypherText cyph = new CypherText(iwc);

		String cypherKey = iwb.getProperty("cypherKey");
		if ((cypherKey == null) || (cypherKey.equalsIgnoreCase(""))) {
			cypherKey = cyph.getKey(100);
			iwb.setProperty("cypherKey", cypherKey);
		}

		return (cypherKey);
	}

	private void addReferenceNumberLookupResults(IWContext iwc)
			throws RemoteException {

		int aid = 0;
		try {
			aid = Integer.parseInt(ref);
		} catch (java.lang.NumberFormatException e) {
			aid = 0;
		}

		if (aid > 0)
			holder = applicationService.getApplicationInfo(aid);

		String update = iwc.getParameter("updatePhoneEmail");
		String confirm = iwc.getParameter("confirmWL");

		if (update != null) {
			updateApplicantInfo(iwc, holder);
		} else if (confirm != null) {
			holder = confirmWaitingList(iwc, aid, holder);
		} else if (iwc.isParameterSet("denyAllocation")) {
			denyWaitingListEntry(iwc);
		} else if (iwc.isParameterSet("acceptAllocation")) {
			acceptAllocation(iwc);
		} else if (iwc.isParameterSet("cancelApplication")) {
			cancelApplication(iwc);
		}

		// fetch again after changed data
		if (aid > 0) {
			holder = applicationService.getApplicationInfo(aid);
		}

		Table refTable = new Table();
		refTable.setWidth(this.getWidth());
		refTable.setCellpadding(5);

		row = 1;
		if (holder == null) {
			refTable
					.add(
							new Text(
									_iwrb
											.getLocalizedString(
													"appNoSuchApplication",
													"There is no application associated with that reference number")),
							1, row);
			row++;
		} else {
			addWaitingListForm(iwc, holder, refTable);
			addAllocationDenialTable(refTable);
		}
	}

	private void addWaitingListForm(IWContext iwc,
			CampusApplicationHolder holder, Table refTable)
			throws RemoteException {
		Form form = new Form();
		Applicant applicant = holder.getApplicant();
		Application app = holder.getApplication();
		CampusApplication camApp = holder.getCampusApplication();
		Text nameText = new Text(applicant.getFullName());
		nameText.setBold();

		refTable.add(nameText, 1, row);
		row++;

		DateFormat format = DateFormat.getDateInstance(1, iwc
				.getCurrentLocale());
		String date = format.format(new Date(app.getSubmitted().getTime()));
		Text dateText = new Text(date);
		dateText.setBold();

		refTable.add(new Text(_iwrb.getLocalizedString("appReceived",
				"Your application was received")
				+ " "), 1, row);
		refTable.add(dateText, 1, row);
		row++;

		Table updateTable = new Table(3, 2);
		updateTable.add(new Text(_iwrb.getLocalizedString("phone", "Telephone")
				+ " : "), 1, 1);
		String phone = applicant.getResidencePhone();
		if (phone == null)
			phone = "";
		updateTable.add(new TextInput("phone", phone), 2, 1);
		updateTable.add(new Text(_iwrb.getLocalizedString("email", "Email")
				+ " : "), 1, 2);
		String email = camApp.getEmail();
		if (email == null)
			email = "";
		TextInput emailInput = new TextInput("email", email);
		emailInput.setAsEmail(_iwrb.getLocalizedString("invalid_email",
				"Please enter a valid email !!"));
		updateTable.add(emailInput, 2, 2);
		updateTable.add(new SubmitButton("updatePhoneEmail", _iwrb
				.getLocalizedString("update", "Update")), 3, 2);

		refTable.add(updateTable, 1, row);
		row++;

		String status = app.getStatus();
		String statusText = app.getStatus();

		if (status
				.equalsIgnoreCase(com.idega.block.application.data.ApplicationBMPBean.STATUS_SUBMITTED))
			statusText = _iwrb.getLocalizedString("appSubmitted",
					"Waiting to be processed");
		else if (status
				.equalsIgnoreCase(com.idega.block.application.data.ApplicationBMPBean.STATUS_APPROVED)) {
			statusText = _iwrb.getLocalizedString("appApproved",
					"Approved / On waiting list");
		} else if (status
				.equalsIgnoreCase(com.idega.block.application.data.ApplicationBMPBean.STATUS_SIGNED))
			statusText = _iwrb
					.getLocalizedString("appContracted", "Contracted");
		else if (status
				.equalsIgnoreCase(com.idega.block.application.data.ApplicationBMPBean.STATUS_REJECTED))
			statusText = _iwrb.getLocalizedString("appRejected", "Rejected");
		else
			statusText = _iwrb.getLocalizedString("appUnknownStatus",
					"Lost in limbo somewhere");

		refTable.add(new Text(_iwrb.getLocalizedString("appStatus",
				"Application status")
				+ ": "), 1, row);
		Text stsText = new Text(statusText);
		stsText.setBold();
		refTable.add(stsText, 1, row);

		if (status
				.equalsIgnoreCase(com.idega.block.application.data.ApplicationBMPBean.STATUS_APPROVED)) { // F?kk
			// ekki
			// ?thluta?,
			// e?a
			// ekki
			// b?i?
			// a?
			// ?thluta.
			Table denialTable = null;
			Contract c = holder.getContract();

			if (c != null
					&& c.getStatus().equals(ContractBMPBean.STATUS_CREATED)) {
				try {
					ApartmentView allocatedApartment = applicationService
							.getBuildingService().getApartmentViewHome()
							.findByPrimaryKey(c.getApartmentId());

					allocatedTypeID = (allocatedApartment.getTypeID());
					allocatedComplexID = (allocatedApartment.getComplexID());
				} catch (RemoteException e) {
					e.printStackTrace();
				} catch (FinderException e) {
					e.printStackTrace();
				}
			}

			Vector wl = holder.getWaitingList();
			Vector choices = holder.getApplied();

			if (wl == null) {
				Text star = new Text(" *");
				star.setStyle("required");
				refTable.add(star, 1, row);
				row++;
			} else {
				row++;
			}

			Table container = new Table();
			Table appliedTable = new Table();
			appliedTable.setBorder(1);
			appliedTable.setColumns(5);

			Text appliedText1 = new Text(_iwrb.getLocalizedString(
					"appAppliedHeader", "Applied for"));
			Text appliedText2 = new Text(_iwrb.getLocalizedString(
					"appPositionOnList", "# on list"));
			Text appliedText3 = new Text(_iwrb.getLocalizedString(
					"appStayOnList", "Stay on list"));
			Text appliedText4 = new Text(_iwrb.getLocalizedString(
					"lastConfirmation", "Last confirmation"));
			Text appliedText5 = new Text(_iwrb.getLocalizedString("denials",
					"Denials"));
			appliedText1.setBold();
			appliedText2.setBold();
			appliedText3.setBold();
			appliedText4.setBold();
			appliedText5.setBold();
			appliedTable.add(appliedText1, 1, 1);
			appliedTable.add(appliedText2, 2, 1);
			appliedTable.add(appliedText3, 3, 1);
			appliedTable.add(appliedText4, 4, 1);
			appliedTable.add(appliedText5, 5, 1);

			int pos = 1;
			if (choices != null) {
				Iterator it = choices.iterator();

				ApartmentTypeHome ath = applicationService.getBuildingService()
						.getApartmentTypeHome();
				ComplexHome cxh = applicationService.getBuildingService()
						.getComplexHome();
				while (it.hasNext()) {
					try {
						Applied applied = (Applied) it.next();
						pos++;
						ApartmentType aType = ath.findByPrimaryKey(applied
								.getApartmentTypeId());
						Complex complex = cxh.findByPrimaryKey(applied
								.getComplexId());
						Text appType = new Text(aType.getName() + " ("
								+ complex.getName() + ")");

						appliedTable.add(appType, 1, pos);

						// find the corresponding waitinglist entry
						WaitingList wait = null;
						if (wl != null) {
							Iterator it1 = wl.iterator();
							while (it1.hasNext()) {
								wait = (WaitingList) it1.next();
								if ((wait.getApartmentTypeId().intValue() == ((Integer) aType
										.getPrimaryKey()).intValue())
										&& (wait.getComplexId().intValue() == ((Integer) complex
												.getPrimaryKey()).intValue()))
									break;
								else
									wait = null;
							}
						}

						// we have a waitinglist entry and the applicant wishes
						// to stay on it
						if (wait != null && !wait.getRemovedFromList()) {
							appliedTable.add(new Text(wait.getOrder()
									.toString()), 2, pos);
							appliedTable.setAlignment(2, pos, "center");

							if (wait.getNumberOfRejections() > 0) {
								appliedTable.addText(wait
										.getNumberOfRejections(), 5, pos);
								appliedTable.setAlignment(5, pos, "center");
							}

							// // if we have a norejected contract, check if
							// this is the one
							if (!wait.getRejectFlag()
									&& allocatedComplexID.intValue() == wait
											.getComplexId().intValue()
									&& allocatedTypeID.intValue() == wait
											.getApartmentTypeId().intValue()) {
								Text allocatedText = new Text(_iwrb
										.getLocalizedString("appAllocated",
												"Allocated"));
								appliedTable.add(allocatedText, 3, pos);
								// if not already denied

								allocatedWaitinglistID = (Integer) wait
										.getPrimaryKey();

							} else {
								// we add confirmation checkboxes
								CheckBox check = new CheckBox("confirm"
										+ applied.getOrder().toString(), "true");
								HiddenInput hidden = new HiddenInput("wl"
										+ applied.getOrder().toString(), wait
										.getPrimaryKey().toString());
								form.add(hidden);
								check.setChecked(true);
								appliedTable.add(check, 3, pos);
							}
							appliedTable.setAlignment(3, pos, "center");

							appliedTable.add(dateTimeFormat.format(wait
									.getLastConfirmationDate()), 4, pos);
						}
						// no waitinglist entries
						else {
							appliedTable.mergeCells(2, pos, 4, pos);
							appliedTable.addText(_iwrb.getLocalizedString(
									"appNoWaitingList", "Not on waitinglist"),
									2, pos);
							appliedTable.setAlignment(2, pos,
									Table.HORIZONTAL_ALIGN_CENTER);
						}
					} catch (EJBException e) {
						e.printStackTrace();
					} catch (FinderException e) {
						e.printStackTrace();
					}
				}

				container.setAlignment(1, 1, "right");
				container.setAlignment(1, 2, "right");
				container.add(appliedTable, 1, 1);
				container.add(new SubmitButton("confirmWL", _iwrb
						.getLocalizedString("confirmWL", "Confirm")), 1, 2);

				refTable.add(container, 1, row);
				row++;

			}
		} else if (status
				.equalsIgnoreCase(com.idega.block.application.data.ApplicationBMPBean.STATUS_SUBMITTED)) { // Ekki
			// b?i?
			// a?
			// ?thluta
			Text notAllocated = new Text("&nbsp;*&nbsp;"
					+ _iwrb.getLocalizedString("appSubmitted",
							"Application not processed yet"));
			notAllocated.setStyle("required");
			refTable.add(notAllocated, 1, row);
			row++;
		} else if (status
				.equalsIgnoreCase(com.idega.block.application.data.ApplicationBMPBean.STATUS_SIGNED)) {
			Text signed = new Text("&nbsp;*&nbsp;"
					+ _iwrb.getLocalizedString("appAssigned",
							"You have been assigned to an apartment"));
			signed.setStyle("required");
			refTable.add(signed, 1, row);
			row++;
		}

		form.maintainParameter(ReferenceNumber.CAM_REF_NUMBER);
		form.add(refTable);
		add(form);
		add(Text.getBreak());
		if (!status.equalsIgnoreCase(ApplicationBMPBean.STATUS_GARBAGE)) {
			addAbortApplicationTable(refTable);
		}
		add(Text.getBreak());
		add(Text.getBreak());

	}

	private void cancelApplication(IWContext iwc) {
		if (iwc.isParameterSet("cancel_application_id")) {
			try {
				Integer AID = new Integer(iwc
						.getParameter("cancel_application_id"));
				Application application = ((ApplicationHome) IDOLookup
						.getHome(Application.class)).findByPrimaryKey(AID);
				application.setStatus(ApplicationBMPBean.STATUS_GARBAGE);
				application.store();
				ContractService service = getContractService(iwc);
				service.deleteFromWaitingList(application.getApplicant());
				Collection contracts = ((ContractHome) IDOLookup
						.getHome(Contract.class)).findByApplicant(new Integer(
						application.getApplicantId()));
				if (contracts != null && !contracts.isEmpty()) {
					for (Iterator iter = contracts.iterator(); iter.hasNext();) {
						Contract con = (Contract) iter.next();
						if (con.getStatus().equalsIgnoreCase(
								ContractBMPBean.STATUS_CREATED)) {
							service.doGarbageContract(((Integer) con
									.getPrimaryKey()));
						}
					}
				}
			} catch (NumberFormatException e) {
				e.printStackTrace();
			} catch (IDOLookupException e) {
				e.printStackTrace();
			} catch (IDOStoreException e) {
				e.printStackTrace();
			} catch (IllegalStateException e) {
				e.printStackTrace();
			} catch (FinderException e) {
				e.printStackTrace();
			} catch (RemoteException e) {
				e.printStackTrace();
			}
		}
	}

	private void acceptAllocation(IWContext iwc) {
		if (iwc.isParameterSet("adWaitL_id")) {
			try {

				Integer WID = Integer.valueOf(iwc.getParameter("adWaitL_id"));
				WaitingList wl = ((WaitingListHome) IDOLookup
						.getHome(WaitingList.class)).findByPrimaryKey(WID);
				wl.setAcceptedDate(IWTimestamp.RightNow().getTimestamp());
				wl.store();
			} catch (NumberFormatException e) {
				e.printStackTrace();
			} catch (IDOLookupException e) {
				e.printStackTrace();
			} catch (IDOStoreException e) {
				e.printStackTrace();
			} catch (FinderException e) {
				e.printStackTrace();
			}

		}

	}

	private void denyWaitingListEntry(IWContext iwc) {
		if (iwc.isParameterSet("adWaitL_id")) {
			try {

				Integer WID = Integer.valueOf(iwc.getParameter("adWaitL_id"));
				WaitingList wl = ((WaitingListHome) IDOLookup
						.getHome(WaitingList.class)).findByPrimaryKey(WID);
				wl.incrementRejections(true);
				wl.store();
			} catch (NumberFormatException e) {
				e.printStackTrace();
			} catch (IDOLookupException e) {
				e.printStackTrace();
			} catch (IDOStoreException e) {
				e.printStackTrace();
			} catch (FinderException e) {
				e.printStackTrace();
			}

		}

	}

	private void addAbortApplicationTable(Table refTable) {
		if (holder != null) {
			Form form = new Form();
			Table table = new Table();
			form.add(table);
			form.maintainParameter(ReferenceNumber.CAM_REF_NUMBER);
			add(form);

			Text tCancel = new Text(_iwrb.getLocalizedString(
					"appCancelApplication",
					"Cancel application, all entries will be removed"));
			tCancel.setBold();
			SubmitButton cancelButton = new SubmitButton("cancelApplication",
					_iwrb.getLocalizedString("cancelApplication", "Cancel"));
			String message = _iwrb
					.getLocalizedString(
							"cancelApplicationWarning",
							"Do you really want to cancel your application, all application entries will be removed");
			cancelButton.setOnClick("return confirm('" + message + "');");
			table.add(tCancel, 1, 1);
			table.add(Text.getNonBrakingSpace(), 1, 1);
			table.add(Text.getNonBrakingSpace(), 1, 1);
			table.add(cancelButton);
			table.add(new HiddenInput("cancel_application_id", holder
					.getApplication().getPrimaryKey().toString()));
		}
	}

	private void addAllocationDenialTable(Table refTable)
			throws RemoteException {
		if (holder != null && allocatedWaitinglistID != null) {
			Contract contract = holder.getContract();
			if (contract != null) {
				Table dTable = new Table();
				dTable.setCellspacing(5);
				// dTable.setBorder(1);
				dTable.setWidth(Table.HUNDRED_PERCENT);
				refTable.add(dTable, 1, row);

				Text tPeriod = new Text(_iwrb.getLocalizedString(
						"appContractPeriod", "Contract period"));
				Text tApartment = new Text(_iwrb.getLocalizedString(
						"appAllocatedApartment", "Allocated apartment"));
				Text tAcceptance = new Text(_iwrb.getLocalizedString(
						"appAcceptance", "Acceptance"));
				tPeriod.setBold();
				tApartment.setBold();
				tAcceptance.setBold();
				dTable.add(tApartment, 1, 1);
				dTable.add(tPeriod, 2, 1);
				dTable.add(tAcceptance, 3, 1);

				dTable.addText(dateFormat.format(contract.getValidFrom())
						+ " - " + dateFormat.format(contract.getValidTo()), 2,
						2);
				try {
					ApartmentView apv = applicationService.getBuildingService()
							.getApartmentViewHome().findByPrimaryKey(
									contract.getApartmentId());
					dTable.addText(apv.getApartmentString(" "), 1, 2);
				} catch (FinderException e) {
					e.printStackTrace();
				}
				dTable.setAlignment(3, 2, Table.HORIZONTAL_ALIGN_RIGHT);
				SubmitButton deny = new SubmitButton("denyAllocation", _iwrb
						.getLocalizedString("denyAllotion", "No thanks"));
				String message = (_iwrb.getLocalizedString(
						"denyAllocationWarning",
						"Do you really want to deny this apartment ?"));
				deny.setOnClick("return confirm('" + message + "');");

				SubmitButton accept = new SubmitButton("acceptAllocation",
						_iwrb.getLocalizedString("acceptAllotion", "Accept"));
				String acceptMsg = (_iwrb
						.getLocalizedString("acceptAllocationWarning",
								"Please contact Student Housing office as soon as possible."));
				accept.setOnClick("return confirm('" + acceptMsg + "');");

				dTable.add(deny, 3, 2);
				dTable.add(accept, 3, 2);
				dTable.add(new HiddenInput("adWaitL_id", allocatedWaitinglistID
						.toString()));

			}
		}
	}

	private CampusApplicationHolder confirmWaitingList(IWContext iwc, int aid,
			CampusApplicationHolder holder) throws RemoteException {
		String confirm1 = iwc.getParameter("confirm1");
		String confirm2 = iwc.getParameter("confirm2");
		String confirm3 = iwc.getParameter("confirm3");

		String wl1 = iwc.getParameter("wl1");
		String wl2 = iwc.getParameter("wl2");
		String wl3 = iwc.getParameter("wl3");

		Vector v = (Vector) holder.getWaitingList();

		if (confirm1 == null) {
			if (wl1 != null) {
				applicationService.confirmOnWaitingList(Integer.valueOf(wl1),
						false);
				// CampusReferenceNumberInfoHelper.stayOnWaitingList(Integer.parseInt(wl1),
				// false);
			}
		} else {
			if (wl1 != null) {
				applicationService.confirmOnWaitingList(Integer.valueOf(wl1),
						true);
				// CampusReferenceNumberInfoHelper.stayOnWaitingList(Integer.parseInt(wl1),
				// true);
			}
		}

		if (confirm2 == null) {
			if (wl2 != null) {
				applicationService.confirmOnWaitingList(Integer.valueOf(wl2),
						false);
				// CampusReferenceNumberInfoHelper.stayOnWaitingList(Integer.parseInt(wl2),
				// false);
			}
		} else {
			if (wl2 != null) {
				applicationService.confirmOnWaitingList(Integer.valueOf(wl2),
						true);
				// CampusReferenceNumberInfoHelper.stayOnWaitingList(Integer.parseInt(wl2),
				// true);
			}
		}

		if (confirm3 == null) {
			if (wl3 != null) {
				applicationService.confirmOnWaitingList(Integer.valueOf(wl3),
						false);
				// CampusReferenceNumberInfoHelper.stayOnWaitingList(Integer.parseInt(wl3),
				// false);
			}
		} else {
			if (wl3 != null) {
				applicationService.confirmOnWaitingList(Integer.valueOf(wl3),
						true);
				// CampusReferenceNumberInfoHelper.stayOnWaitingList(Integer.parseInt(wl3),
				// true);
			}
		}

		holder = applicationService.getApplicationInfo(aid);
		return holder;
	}

	private void updateApplicantInfo(IWContext iwc,
			CampusApplicationHolder holder) throws RemoteException {
		String phone = iwc.getParameter("phone");
		String email = iwc.getParameter("email");
		if (email != null && email.length() > 0 && email.indexOf("@") > 0)
			applicationService.storePhoneAndEmail((Integer) holder
					.getCampusApplication().getPrimaryKey(), phone, email);
		// CampusReferenceNumberInfoHelper.updatePhoneAndEmail(holder, phone,
		// email);
	}

	private void addPersonalIDLookupResults(IWContext iwc) {
		debug("Handling display of ssn lookup "
				+ iwc.getParameter("cam_ref_number"));
		Table table = new Table();
		table.setCellpadding(2);
		table.setCellspacing(2);
		int row = 1;
		User user = null;
		try {
			user = getUserService(iwc).getUserHome().findByPersonalID(refnum);
			if (user == null) {
				table.add(getErrorText(localize(
						"reference.no_user_found_for_personal_id",
						"No user found for personal ID")), 1, row++);
			} else {

				Integer userID = (Integer) user.getPrimaryKey();
				ContractService conService = getCampusService(iwc)
						.getContractService();
				Collection contracts = conService.getContractHome()
						.findByUserAndStatus(userID,
								conService.getRentableStatuses());

				boolean cleared = false;
				if (iwc.isParameterSet("reference_reset")) {
					cleared = clearLoginChanged(userID.intValue());
				}

				LoginTable login = LoginDBHandler.getUserLogin(userID
						.intValue());
				String userLogin = login.getUserLogin();
				String password = "********";
				java.sql.Timestamp t = login.getLastChanged();
				boolean showSubmit = t == null ? true : false;
				if (showSubmit) {
					if (iwc.isParameterSet("reference_submit")) {

						if (iwc.isParameterSet("reference_nwpssw")) {
							password = iwc.getParameter("reference_nwpssw")
									.trim();
						} else if (iwc.isParameterSet("reference_upaps"))
							password = user.getPersonalID();
						else
							password = LoginCreator.createPasswd(8);
						LoginDBHandler.updateLogin(userID.intValue(), login
								.getUserLogin(), password);
						showSubmit = false;
					} else {
						table.add(getHeader(localize(
								"reference.user_login_ready_for_update",
								"Login is ready for update")), 1, row);
						table.mergeCells(1, row, 2, row);
						row++;
						table
								.add(
										getHeader(localize(
												"reference.user_login_submit_to_create_new_password",
												"Submit to create new password")),
										1, row);
						table.mergeCells(1, row, 2, row);
						row++;
					}
				} else {
					table.add(getErrorText(localize(
							"reference.user_login_already_fetched",
							"Login has already been fetched")), 1, row);
					table.mergeCells(1, row, 2, row);
					row++;
					table
							.add(
									getErrorText(localize(
											"reference.contact_office_to_reopen_acces_to_login",
											"Contact office to reopen")), 1,
									row);
					table.mergeCells(1, row, 2, row);
					row++;
				}
				row++;
				table
						.add(getHeader(localize("reference.user_name",
								"User name")), 1, row);
				table.add(getText(user.getName()), 2, row);
				row++;
				table.add(getText(user.getPersonalID()), 2, row);
				row = row + 2;
				table.add(getHeader(localize("reference.user_login", "Login")),
						1, row);
				table.add(getText(userLogin), 2, row);
				row++;
				table.add(getHeader(localize("reference.user_password",
						"Password")), 1, row);
				table.add(getText(password), 2, row);
				row = row + 2;
				boolean isAdmin = isAdministrator(iwc);
				if (isAdmin || showSubmit) {
					TextInput newPassword = getTextInput("reference_nwpssw");
					table.add(getHeader(localize(
							"reference.user_login_custom_password",
							"Custom password")), 1, row);
					table.add(newPassword, 2, row);
					row++;
					CheckBox userPID = getCheckBox("reference_upaps", "true");
					table.add(userPID, 1, row);
					table.add(getText(localize("reference.use_pid_as_password",
							"Use personal ID as password")), 1, row);
					table.mergeCells(1, row, 2, row);
					row++;
				}
				if (isAdmin) {
					SubmitButton reset = (SubmitButton) getSubmitButton(
							"reference_reset", "true", "Reset",
							"reference.reset_login");
					table.add(reset, 2, row);
				}
				if (isAdmin || showSubmit) {
					SubmitButton submit = (SubmitButton) getSubmitButton(
							"reference_submit", "true", "Submit",
							"reference.submit");
					table.add(submit, 2, row);

				}
				table.setAlignment(2, row, Table.HORIZONTAL_ALIGN_RIGHT);

			}

		} catch (RemoteException e) {
			e.printStackTrace();
		} catch (EJBException e) {
			e.printStackTrace();
		} catch (FinderException e) {

		} catch (Exception e) {

			e.printStackTrace();
		}
		if (user == null)
			table.add(getErrorText(localize(
					"reference.no_user_found_for_personal_id",
					"No user found for personal ID")), 1, row);
		Form form = new Form();
		form.maintainParameter(ReferenceNumber.CAM_REF_NUMBER);
		form.add(table);
		add(form);
		add(Text.getBreak());
		add(Text.getBreak());
	}

	/**
	 * 
	 */
	private void approved(IWContext iwc) {
	}

	/**
	 * 
	 */
	private void rejected(IWContext iwc) {
	}

	/**
	 * 
	 */
	public String getBundleIdentifier() {
		return (IW_BUNDLE_IDENTIFIER);
	}

	/**
	 * 
	 */
	public void main(IWContext iwc) {
		// debugParameters(iwc);
		_iwrb = getResourceBundle(iwc);
		try {
			control(iwc);
		} catch (RemoteException e) {
			add("Service is unavailable please come back later");
		}
	}

	public boolean clearLoginChanged(int icUserId) {
		try {
			String sql = "update ic_login set last_changed = null where ic_user_id = "
					+ icUserId;
			return com.idega.data.SimpleQuerier.execute(sql);
		} catch (Exception ex) {
			ex.printStackTrace();
		}
		return false;
	}
}
