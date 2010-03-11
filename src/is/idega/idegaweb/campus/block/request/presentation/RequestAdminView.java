/*
 * $Id: RequestAdminView.java,v 1.15.4.4 2007/09/04 00:53:43 eiki Exp $
 *
 * Copyright (C) 2002 Idega hf. All Rights Reserved.
 *
 * This software is the proprietary information of Idega hf.
 * Use is subject to license terms.
 *
 */
package is.idega.idegaweb.campus.block.request.presentation;


import is.idega.idegaweb.campus.block.allocation.data.Contract;
import is.idega.idegaweb.campus.block.application.business.CampusApplicationFinder;
import is.idega.idegaweb.campus.block.application.data.CampusApplication;
import is.idega.idegaweb.campus.block.request.business.RequestFinder;
import is.idega.idegaweb.campus.block.request.business.RequestHolder;
import is.idega.idegaweb.campus.block.request.data.Request;
import is.idega.idegaweb.campus.presentation.CampusBlock;

import java.util.Collection;
import java.util.List;

import javax.ejb.FinderException;

import com.idega.block.application.data.Applicant;
import com.idega.block.building.data.ApartmentView;
import com.idega.block.building.data.ApartmentViewHome;
import com.idega.data.IDOLookup;
import com.idega.data.IDOLookupException;
import com.idega.presentation.IWContext;
import com.idega.presentation.Table;
import com.idega.presentation.text.Link;
import com.idega.presentation.ui.DataTable;
import com.idega.presentation.ui.Form;
import com.idega.presentation.ui.RadioGroup;
import com.idega.util.IWTimestamp;

/**
 * @author <a href="mail:palli@idega.is">Pall Helgason</a>
 * @version 1.0
 */
public class RequestAdminView extends CampusBlock {

	private static final String NAME_KEY = "cam_request_admin_view_block";
	private static final String DEFAULT_VALUE = "Requests";
	private static final String CAM_REQ_VIEW_SELECTED = "req_admin_filter";


	/**
	 *
	 */
	public RequestAdminView() {
	}

	/**
	 *
	 */
	public void main(IWContext iwc) {

		String selected = iwc.getParameter(CAM_REQ_VIEW_SELECTED);

		add(getRequests(iwc,selected));
	}

	/**
	 *
	 */
	public String getLocalizedNameKey() {
		return(NAME_KEY);
	}

	/**
	 *
	 */
	public String getLocalizedNameValue() {
		return(DEFAULT_VALUE);
	}

	/**
	 *
	 */
	public String getBundleIdentifier() {
		return(IW_BUNDLE_IDENTIFIER);
	}

	private Form getRequests(IWContext iwc,String selected) {
		Form f = new Form();
		Table t = new Table(1,2);
		t.setWidth("100%");
		DataTable table = new DataTable();
		table.setWidth("100%");
		table.setTitlesHorizontal(true);

		table.addTitle(localize("REQUEST_HEADER","Requests"));
		table.add(getHeader(localize("REQUEST_TYPE","Request")),1,1);
		table.add(getHeader(localize("REQUEST_SENT","Sent")),2,1);
		table.add(getHeader(localize("REQUEST_BUILDING","Building")),3,1);
		table.add(getHeader(localize("REQUEST_APARTMENT","Apartment")),4,1);
		table.add(getHeader(localize("REQUEST_TENANT","Tenant")),5,1);
		table.add(getHeader(localize("REQUEST_STATUS","Status")),6,1);

		int row = 2;

		RadioGroup grp = new RadioGroup(CAM_REQ_VIEW_SELECTED);
		grp.addRadioButton(RequestFinder.REQUEST_STATUS_SENT,getText(localize("REQUEST_STATUS_S","S")));
		if(Boolean.valueOf(iwc.getApplicationSettings().getProperty("CAMPUS_USE_RECEIVED_STATUS","true")).booleanValue()){
			grp.addRadioButton(RequestFinder.REQUEST_STATUS_RECEIVED,getText(localize("REQUEST_STATUS_R","R")));
		}
		grp.addRadioButton(RequestFinder.REQUEST_STATUS_IN_PROGRESS,getText(localize("REQUEST_STATUS_P","P")));
		grp.addRadioButton(RequestFinder.REQUEST_STATUS_DONE,getText(localize("REQUEST_STATUS_D","D")));
		grp.addRadioButton(RequestFinder.REQUEST_STATUS_DENIED,getText(localize("REQUEST_STATUS_X","X")));
		grp.setVertical(false);
		grp.keepStatusOnAction();

		if (selected == null) {
			selected = RequestFinder.REQUEST_STATUS_SENT;
			grp.setSelected(selected);
		}

		List requests = RequestFinder.getAllRequestsByStatus(selected);
		Request request = null;
		RequestHolder holder = null;

		if ( requests != null ) {
			for ( int a = 0; a < requests.size(); a++ ) {
				holder = (RequestHolder) requests.get(a);
				request = holder.getRequest();
				String type = null;
				String street = null;
				String aprt = null;
				String name = null;
				
				try {
					type = request.getRequestType();
				}
				catch(Exception e) {}
				String status = null;
				try {
					status = request.getStatus();
				}
				catch(Exception e) {}
				String linkText = localize("REQUEST_TYPE_" + type,"Almenn viðgerð");
				Link details = new Link(linkText);

				details.setWindowToOpen(RequestAdminViewDetails.class);
				//try {
				details.addParameter("request_id",((Integer)request.getPrimaryKey()).intValue());
				//}
				//catch(java.rmi.RemoteException e) {
				//  e.printStackTrace();
				//}

//				details.addParameter("id",request.getpr);

				//THE link
				table.add(details,1,row);

				//The date
				try {
					table.add(getText(new IWTimestamp(request.getDateSent()).getLocaleDate(iwc.getCurrentLocale())),2,row);
				}
				catch(Exception e) {
					table.add(null,2,row);
				}

				Contract contract = null;
				ApartmentView apartmentView = null;

				Applicant applicant = null;
				try {
					Collection contracts = getContractService(iwc)
					.getContractHome().findByUserAndRented(
							new Integer(request.getUserId()), Boolean.TRUE);
					contract = (Contract) contracts.iterator().next();
				} catch (Exception e) {
					contract = null;
				}
				if (contract != null) {
					try {
						apartmentView = ((ApartmentViewHome) IDOLookup
								.getHome(ApartmentView.class))
								.findByPrimaryKey(contract.getApartmentId());
					} catch (IDOLookupException e1) {
						e1.printStackTrace();
					} catch (FinderException e1) {
						e1.printStackTrace();
					}

					applicant = contract.getApplicant();
				}

				CampusApplication campusApplication = null;
				try {
					campusApplication = CampusApplicationFinder.getApplicationInfo(
							contract.getApplication()).getCampusApplication();
				} catch (Exception e) {
					campusApplication = null;
				}

				if (apartmentView != null) {
					 aprt = apartmentView.getApartmentName();
					 street = apartmentView.getBuildingName();
				}
				
				if (applicant != null) {
					name = applicant.getFullName();
				}
				
				table.add(getText(street),3,row);
				table.add(getText(aprt),4,row);
				table.add(getText(name),5,row);
				

				table.add(getText(localize("REQUEST_STATUS_" + status,"Innsend")),6,row);
				row++;
			}
		}

		t.add(grp,1,1);
		t.add(table,1,2);
		f.add(t);
		grp.setToSubmit();
		return(f);
	}
}
