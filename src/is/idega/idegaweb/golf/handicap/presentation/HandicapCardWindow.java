package is.idega.idegaweb.golf.handicap.presentation;

import com.idega.presentation.IWContext;
import com.idega.presentation.ui.Window;
import is.idega.idegaweb.golf.handicap.presentation.*;

/**
 * @author gimmi
 */
public class HandicapCardWindow extends Window{

	public static String PARAMETER_MEMBER_ID = "hcw_m_id";

	public HandicapCardWindow() {
		super.setWidth(275);
		super.setHeight(300);
		super.setResizable(true);
	}

	public void main(IWContext modinfo) {
		String memberId = modinfo.getParameter(PARAMETER_MEMBER_ID);
		if (memberId != null) {
			HandicapCard ch = new HandicapCard(Integer.parseInt(memberId));
			ch.addName(true);
			ch.addClub(true);
			add(ch);
		}	
	}
}
