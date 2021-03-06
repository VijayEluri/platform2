/*
 * $Id: ReportWindow.java,v 1.3 2003/12/15 12:22:13 anders Exp $
 *
 * Copyright (C) 2003 Agura IT. All Rights Reserved.
 *
 * This software is the proprietary information of Agura IT AB.
 * Use is subject to license terms.
 *
 */
package se.idega.idegaweb.commune.school.report.presentation;

import se.idega.idegaweb.commune.school.report.business.ReportModel;

import com.idega.presentation.IWContext;
import com.idega.presentation.ui.Window;

/** 
 * Window for viewing a printer-friendly school report.
 * <p>
 * Last modified: $Date: 2003/12/15 12:22:13 $ by $Author: anders $
 *
 * @author Anders Lindman
 * @version $Revision: 1.3 $
 */
public class ReportWindow extends Window {

	/**
	 * Constructs a new report window.
	 * @param sessionTimeoutMessage the message to display if the user session has timed out
	 */
	public ReportWindow() {
		this.setWidth(600);
		this.setHeight(400);
		this.setScrollbar(true);
		this.setResizable(true);	
		this.setAllMargins(20);
	}

	/**
	 * @see com.idega.presentation.PresentationObject#main(IWContext)
	 */
	public void main(IWContext iwc) throws Exception {
		ReportModel reportModel = null;
		try {
			reportModel = (ReportModel) iwc.getSession().getAttribute(ReportBlock.PARAMETER_REPORT_MODEL);
		} catch (Exception e) {
			log(e);
		}
		ReportBlock reportBlock = new ReportBlock(reportModel);
		reportBlock.setShowPrintButton(true);
		reportBlock.setShowTitle(true);
		reportBlock.setShowDate(true);
		add(reportBlock);
	}
	
}
