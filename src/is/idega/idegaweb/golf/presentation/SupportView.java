package is.idega.idegaweb.golf.presentation;

import com.idega.presentation.Table;
import com.idega.presentation.IWContext;
import com.idega.presentation.PresentationObject;
import com.idega.presentation.Image;
import java.lang.String;
import java.sql.SQLException;

import javax.ejb.FinderException;

import com.idega.presentation.text.*;
import com.idega.data.IDOLookup;
import com.idega.idegaweb.IWBundle;
import com.idega.idegaweb.IWResourceBundle;
import is.idega.idegaweb.golf.entity.GolferPageData;
import is.idega.idegaweb.golf.entity.MemberHome;

import com.idega.jmodule.text.presentation.TextReader;
import is.idega.idegaweb.golf.entity.Member;
import is.idega.idegaweb.golf.handicap.presentation.HandicapOverview;

/**
 * Title: idegaWeb Classes Description: Copyright: Copyright (c) 2001 Company:
 * 
 * @author <a href="bjarni@idega.is">Bjarni Viljhalmsson </a>
 * @version 1.0
 */

public class SupportView extends GolferJModuleObject implements LinkParameters {

	public SupportView() {
	}

	//STU�NINGSA�ILAR
	private void setSupportView(IWContext modinfo) throws SQLException {
		//Image iInterviewsLogo = iwrb.getImage("/golferpage/velkomin.gif");
		//this.addLeftLogo(iInterviewsLogo);

		Table interviewsTable = new Table(3, 1);
		interviewsTable.setWidth("100%");
		interviewsTable.setCellpadding(0);
		interviewsTable.setCellspacing(0);
		Image dotLineBackgroundImage;
		dotLineBackgroundImage = iwb.getImage("shared/brotalina.gif");
		interviewsTable.setBackgroundImage(2, 1, dotLineBackgroundImage);
		interviewsTable.setWidth(1, 1, "410");
		interviewsTable.setWidth(2, 1, "1");
		interviewsTable.add(Text.emptyString(), 2, 1);
		interviewsTable.setVerticalAlignment(1, 1, "top");

		TextReader supportText = new TextReader(this.golferPageData.getSupportesID());
		supportText.setTextStyle(Text.FONT_FACE_VERDANA);
		supportText.setAlignment("center");
		supportText.setHeadlineColor("FF6000");
		supportText.setHeadlineSize(2);
		interviewsTable.setVerticalAlignment(3, 1, "top");
		interviewsTable.setAlignment(3, 1, "center");
		interviewsTable.add(supportText, 3, 1);
		String fullName;
		try {
			Member member = ((MemberHome) IDOLookup.getHomeLegacy(Member.class)).findByPrimaryKey(member_id);
			fullName = member.getFirstName() + " " + member.getMiddleName() + " " + member.getLastName();
		}
		catch (FinderException fe) {
			throw new SQLException(fe.getMessage());
		}

		/**@todo Set the golfers (eignarfall) name in the database till then I'll use as a sessionattribute.
		 * */

		//String name =
		GolferFriendsSigningSheet golferFriendsSigningSheet = new GolferFriendsSigningSheet(this.golferPageData.getSupportPreSigningID(), (String) modinfo.getSessionAttribute("golferName"), sTopMenuParameterName, sInterviewsParameterValue, sSubmitParameterValue, fullName);
		interviewsTable.add(golferFriendsSigningSheet, 1, 1);
		add(interviewsTable);
	}

	public void main(IWContext modinfo) throws SQLException {
		super.main(modinfo);
		if (modinfo.getParameter(LinkParameters.sTopMenuParameterName).equalsIgnoreCase(LinkParameters.sInterviewsParameterValue)) {
			setSupportView(modinfo);
		}
	}
}