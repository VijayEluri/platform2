/*
 * $Id: ReferenceNumberInfo.java,v 1.23 2003/07/24 15:11:20 aron Exp $
 *
 * Copyright (C) 2001 Idega hf. All Rights Reserved.
 *
 * This software is the proprietary information of Idega hf.
 * Use is subject to license terms.
 *
 */

package is.idega.idegaweb.campus.block.application.presentation;

import is.idega.idegaweb.campus.presentation.CampusColors;
import is.idega.idegaweb.campus.block.application.data.Applied;
import is.idega.idegaweb.campus.block.allocation.data.Contract;
import is.idega.idegaweb.campus.block.application.data.WaitingList;
import is.idega.idegaweb.campus.block.application.data.CampusApplication;
import is.idega.idegaweb.campus.block.application.business.CampusApplicationFinder;
import is.idega.idegaweb.campus.block.application.business.CampusApplicationHolder;
import is.idega.idegaweb.campus.block.application.business.CampusReferenceNumberInfoHelper;
import com.idega.block.application.data.Applicant;
import com.idega.block.application.data.Application;
import com.idega.block.building.data.Apartment;
import com.idega.block.building.data.ApartmentType;
import com.idega.block.building.data.Building;
import com.idega.block.building.data.Complex;
import com.idega.block.building.data.Floor;
import com.idega.block.building.business.ApartmentTypeComplexHelper;
import com.idega.block.building.business.BuildingCacher;
import com.idega.block.application.business.ReferenceNumberHandler;
import com.idega.presentation.IWContext;
import com.idega.presentation.PresentationObjectContainer;
import com.idega.presentation.text.Text;
import com.idega.idegaweb.IWResourceBundle;
import com.idega.presentation.Table;
import com.idega.presentation.Image;
import com.idega.presentation.ui.TextInput;
import com.idega.presentation.ui.SubmitButton;
import com.idega.presentation.ui.Form;
import com.idega.presentation.ui.CheckBox;
import com.idega.presentation.ui.HiddenInput;

import java.util.Vector;
import java.util.Date;
import java.util.Iterator;
import java.util.Hashtable;
import java.util.TreeSet;
import java.text.DateFormat;

/**
 *
 * @author <a href="mailto:palli@idega.is">Pall Helgason</a>
 * @version 1.0
 */

public class ReferenceNumberInfo extends PresentationObjectContainer {
	public static final String SESSION_REFERENCE_NUMBER = "session_ref_num";
	private static final String IW_BUNDLE_IDENTIFIER = "com.idega.block.application";
	private IWResourceBundle _iwrb = null;

	/**
	 *
	 */
	public ReferenceNumberInfo() {
	}

	/**
	 *
	 */
	protected void control(IWContext iwc) {
		String which = (String) iwc.getSessionAttribute("DUMMY_LOGIN");
		if (which == null) {
			addReferenceNumberLookupResults(iwc);
		}
		else {
			addPersonalIDLookupResults(iwc);
		}
	}

	private void addReferenceNumberLookupResults(IWContext iwc) {
		String ref = ReferenceNumberHandler.getReferenceNumber(iwc);
		
		int aid = 0;
		try {
			aid = Integer.parseInt(ref);
		}
		catch (java.lang.NumberFormatException e) {
			aid = 0;
		}
		
		CampusApplicationHolder holder = CampusApplicationFinder.getApplicationInfo(aid);
		
		String update = iwc.getParameter("updatePhoneEmail");
		String confirm = iwc.getParameter("confirmWL");
		
		if (update != null) {
			updateApplicantInfo(iwc, holder);
		}
		
		else if (confirm != null) {
			holder = confirmWaitingList(iwc, aid, holder);
		}
		
		Table refTable = new Table();
		refTable.setCellpadding(5);
		
		int row = 1;
		if (holder == null) {
			refTable.add(new Text(_iwrb.getLocalizedString("appNoSuchApplication", "There is no application associated with that reference number")), 1, row);
			row++;
		}
		else {
			addWaitingListForm(iwc, holder, refTable, row);
		}
	}

	private void addWaitingListForm(IWContext iwc, CampusApplicationHolder holder, Table refTable, int row) {
		Form form = new Form();
		Applicant applicant = holder.getApplicant();
		Application app = holder.getApplication();
		CampusApplication camApp = holder.getCampusApplication();
		Text nameText = new Text(applicant.getFullName());
		nameText.setBold();
		
		refTable.add(nameText, 1, row);
		row++;
		
		DateFormat format = DateFormat.getDateInstance(1, iwc.getCurrentLocale());
		String date = format.format(new Date(app.getSubmitted().getTime()));
		Text dateText = new Text(date);
		dateText.setBold();
		
		refTable.add(new Text(_iwrb.getLocalizedString("appReceived", "Your application was received") + " "), 1, row);
		refTable.add(dateText, 1, row);
		row++;
		
		Table updateTable = new Table(3, 2);
		updateTable.add(new Text(_iwrb.getLocalizedString("phone", "Telephone") + " : "), 1, 1);
		String phone = applicant.getResidencePhone();
		if (phone == null)
			phone = "";
		updateTable.add(new TextInput("phone", phone), 2, 1);
		updateTable.add(new Text(_iwrb.getLocalizedString("email", "Email") + " : "), 1, 2);
		String email = camApp.getEmail();
		if (email == null)
			email = "";
		TextInput emailInput = new TextInput("email", email);
		emailInput.setAsEmail(_iwrb.getLocalizedString("invalid_email","Please enter a valid email !!"));
		updateTable.add(emailInput, 2, 2);
		updateTable.add(new SubmitButton("updatePhoneEmail", _iwrb.getLocalizedString("update", "Update")), 3, 2);
		
		refTable.add(updateTable, 1, row);
		row++;
		
		String status = app.getStatus();
		
		if (status.equalsIgnoreCase(com.idega.block.application.data.ApplicationBMPBean.STATUS_SUBMITTED))
			status = _iwrb.getLocalizedString("appSubmitted", "Waiting to be processed");
		else if (status.equalsIgnoreCase(com.idega.block.application.data.ApplicationBMPBean.STATUS_APPROVED)){
			status = _iwrb.getLocalizedString("appApproved", "Approved / On waiting list");
		}
		else if (status.equalsIgnoreCase(com.idega.block.application.data.ApplicationBMPBean.STATUS_SIGNED))
			status = _iwrb.getLocalizedString("appContracted", "Contracted / On waiting list");
		else if (status.equalsIgnoreCase(com.idega.block.application.data.ApplicationBMPBean.STATUS_REJECTED))
			status = _iwrb.getLocalizedString("appRejected", "Rejected");
		else
			status = _iwrb.getLocalizedString("appUnknownStatus", "Lost in limbo somewhere");
		
		refTable.add(new Text(_iwrb.getLocalizedString("appStatus", "Application status") + ": "), 1, row);
		Text statusText = new Text(status);
		statusText.setBold();
		refTable.add(statusText, 1, row);
		
		if (status.equalsIgnoreCase(com.idega.block.application.data.ApplicationBMPBean.STATUS_APPROVED)) { //F?kk ekki ?thluta?, e?a ekki b?i? a? ?thluta.
			
			Contract c = holder.getContract();
			Integer allocatedTypeID = new Integer(-1);
			Integer allocatedComplexID = new Integer(-1);
			if(c!=null){
				Apartment allocatedApartment = BuildingCacher.getApartment(c.getApartmentId().intValue());
				Floor allocatedFloor = BuildingCacher.getFloor(allocatedApartment.getFloorId());
				Building allocatedBuilding = BuildingCacher.getBuilding(allocatedFloor.getBuildingId());
				allocatedTypeID = new Integer(allocatedApartment.getApartmentTypeId());
				allocatedComplexID = new Integer(allocatedBuilding.getComplexId());
			}
					
			Vector wl = holder.getWaitingList();
			Vector choices = holder.getApplied();
		
			
			if (wl == null) {
				Text star = new Text(" *");
				star.setStyle("required");
				refTable.add(star, 1, row);
				row++;
			}
			else {
				row++;
			}
		
			Table container = new Table();
			Table appliedTable = new Table();
			appliedTable.setBorder(1);
			appliedTable.setColumns(4);
		
			Text appliedText1 = new Text(_iwrb.getLocalizedString("appAppliedHeader", "Applied for"));
			Text appliedText2 = new Text(_iwrb.getLocalizedString("appPositionOnList", "# on list"));
			Text appliedText3 = new Text(_iwrb.getLocalizedString("appStayOnList", "Stay on list"));
			Text appliedText4 = new Text(_iwrb.getLocalizedString("lastConfirmation", "Last confirmation"));
			appliedText1.setBold();
			appliedText2.setBold();
			appliedText3.setBold();
			appliedText4.setBold();
			appliedTable.add(appliedText1, 1, 1);
			appliedTable.add(appliedText2, 2, 1);
			appliedTable.add(appliedText3, 3, 1);
			appliedTable.add(appliedText4, 4, 1);
		
			int pos = 1;
			Iterator it = choices.iterator();
			DateFormat dateFormat = DateFormat.getDateTimeInstance(DateFormat.SHORT,DateFormat.SHORT,iwc.getCurrentLocale());
			while (it.hasNext()) {
				Applied applied = (Applied) it.next();
				pos++;
				ApartmentType aType = BuildingCacher.getApartmentType(applied.getApartmentTypeId().intValue());
				Complex complex = BuildingCacher.getComplex(applied.getComplexId().intValue());
				Text appType = new Text(aType.getName() + " (" + complex.getName() + ")");
		
				appliedTable.add(appType, 1, pos);
		
				// find the corresponding waitinglist entry
				WaitingList wait = null;
				if (wl != null) {
					Iterator it1 = wl.iterator();
					while (it1.hasNext()) {
						wait = (WaitingList) it1.next();
						if ((wait.getApartmentTypeId().intValue() == aType.getID()) && (wait.getComplexId().intValue() == complex.getID()))
							break;
						else
							wait = null;
					}
				}
		
				// we have a waitinglist entry and the applicant wishes to stay on it
				if (wait != null && !wait.getRemovedFromList()) {
						appliedTable.add(new Text(wait.getOrder().toString()), 2, pos);
						appliedTable.setAlignment(2, pos, "center");
						// if we have a contract check if this is the one
						if(allocatedComplexID.intValue() == wait.getComplexId().intValue() && allocatedTypeID.intValue() == wait.getApartmentTypeId().intValue()){
							Text allocatedText = new Text(_iwrb.getLocalizedString("appAllocated","Allocated"));
							appliedTable.add(allocatedText,3,pos);
						}
						else{
						// we add confirmation checkboxes
						CheckBox check = new CheckBox("confirm" + applied.getOrder().toString(), "true");
						HiddenInput hidden = new HiddenInput("wl" + applied.getOrder().toString(), wait.getIDInteger().toString());
						form.add(hidden);
						check.setChecked(true);
						appliedTable.add(check, 3, pos);
						}
						appliedTable.setAlignment(3, pos, "center");
						
						appliedTable.add(dateFormat.format(wait.getLastConfirmationDate()),4,pos);
				}
			}
		
			container.setAlignment(1, 1, "right");
			container.setAlignment(1, 2, "right");
			container.add(appliedTable, 1, 1);
			container.add(new SubmitButton("confirmWL", _iwrb.getLocalizedString("confirmWL", "Confirm")), 1, 2);
		
			refTable.add(container, 1, row);
			row++;
		
			
		}
		else if (status.equalsIgnoreCase(com.idega.block.application.data.ApplicationBMPBean.STATUS_SUBMITTED)) { //Ekki b?i? a? ?thluta
				Text notAllocated = new Text("&nbsp;*&nbsp;" + _iwrb.getLocalizedString("appNotYetAssigned", "Apartments have not yet been allocated"));
					notAllocated.setStyle("required");
					refTable.add(notAllocated, 1, row);
					row++;
		}
		else if(status.equalsIgnoreCase(com.idega.block.application.data.ApplicationBMPBean.STATUS_SIGNED)) {
			refTable.add(new Text(_iwrb.getLocalizedString("appAssigned", "You have been assigned to an apartment")), 1, row);
			row++;
		}
		
		form.add(refTable);
		add(form);
	}

	private CampusApplicationHolder confirmWaitingList(IWContext iwc, int aid, CampusApplicationHolder holder) {
		String confirm1 = iwc.getParameter("confirm1");
		String confirm2 = iwc.getParameter("confirm2");
		String confirm3 = iwc.getParameter("confirm3");
		
		String wl1 = iwc.getParameter("wl1");
		String wl2 = iwc.getParameter("wl2");
		String wl3 = iwc.getParameter("wl3");
		
		Vector v = (Vector) holder.getWaitingList();
		
		if (confirm1 == null) {
			if (wl1 != null) {
				CampusReferenceNumberInfoHelper.stayOnWaitingList(Integer.parseInt(wl1), false);
			}
		}
		else {
			if (wl1 != null) {
				CampusReferenceNumberInfoHelper.stayOnWaitingList(Integer.parseInt(wl1), true);
			}
		}
		
		if (confirm2 == null) {
			if (wl2 != null) {
				CampusReferenceNumberInfoHelper.stayOnWaitingList(Integer.parseInt(wl2), false);
			}
		}
		else {
			if (wl2 != null) {
				CampusReferenceNumberInfoHelper.stayOnWaitingList(Integer.parseInt(wl2), true);
			}
		}
		
		if (confirm3 == null) {
			if (wl3 != null) {
				CampusReferenceNumberInfoHelper.stayOnWaitingList(Integer.parseInt(wl3), false);
			}
		}
		else {
			if (wl3 != null) {
				CampusReferenceNumberInfoHelper.stayOnWaitingList(Integer.parseInt(wl3), true);
			}
		}
		
		holder = CampusApplicationFinder.getApplicationInfo(aid);
		return holder;
	}

	private void updateApplicantInfo(IWContext iwc, CampusApplicationHolder holder) {
		String phone = iwc.getParameter("phone");
		String email = iwc.getParameter("email");
		if(email!=null && email.length()> 0 &&  email.indexOf("@")>0)				
			CampusReferenceNumberInfoHelper.updatePhoneAndEmail(holder, phone, email);
	}

	private void addPersonalIDLookupResults(IWContext iwc) {
		debug("Handling display of ssn lookup " + iwc.getParameter("cam_ref_number"));
		java.util.List li = CampusReferenceNumberInfoHelper.getUserLogin(iwc);
		
		if (li == null || li.size() == 0) {
			add(new Text("?a? er enginn notandi skr??ur ? ?essa kennit?lu "));
		}
		else {
		
			if (li.size() == 1) {
				if (iwc.isParameterSet("allow") && iwc.isParameterSet("usrid")) {
					clearLoginChanged(Integer.parseInt(iwc.getParameter("usrid")));
					add(new Text("Opna? aftur fyrir kennit?lu"));
				}
				else {
					add(new Text("?a? er  b?i? a? n? ? lykilor? fyrir kennit?lu"));
					if (iwc.hasEditPermission(this)) {
						Form f = new Form();
						f.add(new SubmitButton("allow", "Leyfa innskr?ningu ? n?"));
						f.addParameter("usrid", ((Integer) li.get(0)).intValue());
						add(f);
					}
				}
			}
			else if (li.size() == 3) {
				String userid = (String) li.get(1);
				String passwd = (String) li.get(2);
				Text headerText = new Text(_iwrb.getLocalizedString("user_login", "User login"));
				headerText.setFontColor(CampusColors.WHITE);
				headerText.setBold();
				Text idText = new Text(_iwrb.getLocalizedString("userid", "Userid"));
				idText.setBold();
				//add(idText);
				//add(userid);
				//add(Text.getBreak());
				Text passwdText = new Text(_iwrb.getLocalizedString("password", "Password"));
				passwdText.setBold();
				//add(passwdText);
				//add(passwd);
				Text msg = new Text(_iwrb.getLocalizedString("change_password", "Change your password by clicking your name when logged in"));
				Table dummyTable = new Table(3, 3);
				dummyTable.setWidth("100%");
				dummyTable.setAlignment(2, 2, "center");
				dummyTable.setHeight(1, "100");
				Table table = new Table();
				table.setCellspacing(1);
				table.setCellpadding(3);
				table.mergeCells(1, 1, 2, 1);
				//table.setWidth("100%");
				table.add(headerText, 1, 1);
				table.add(idText, 1, 2);
				table.add(passwdText, 1, 3);
				table.add(userid, 2, 2);
				table.add(passwd, 2, 3);
				table.setHorizontalZebraColored(CampusColors.WHITE, CampusColors.LIGHTGREY);
				table.setColumnColor(1, CampusColors.DARKGREY);
				table.setColor(1, 1, CampusColors.DARKBLUE);
				table.setColumnVerticalAlignment(1, "top");
				table.setColumnVerticalAlignment(2, "top");
				table.mergeCells(1, 4, 2, 4);
				Image image = table.getTransparentCell(iwc);
				image.setHeight(6);
				table.add(image, 1, 4);
				table.setColor(1, 4, CampusColors.DARKRED);
				table.mergeCells(1, 5, 2, 5);
				table.add(msg, 1, 5);
				table.setRowColor(5, CampusColors.WHITE);
				dummyTable.add(table, 2, 2);
				add(dummyTable);
				iwc.removeSessionAttribute("DUMMY_LOGIN");
				iwc.removeSessionAttribute("referenceNumber");
			}
		}
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
		//debugParameters(iwc);
		_iwrb = getResourceBundle(iwc);
		control(iwc);
	}

	public boolean clearLoginChanged(int icUserId) {
		try {
			String sql = "update ic_login set last_changed = null where ic_user_id = " + icUserId;
			return com.idega.data.SimpleQuerier.execute(sql);
		}
		catch (Exception ex) {
			ex.printStackTrace();
		}
		return false;
	}
}
