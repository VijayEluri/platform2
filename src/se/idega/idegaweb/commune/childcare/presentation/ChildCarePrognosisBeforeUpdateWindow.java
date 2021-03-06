/*
 * Created on 28.5.2003
 */
package se.idega.idegaweb.commune.childcare.presentation;

import com.idega.presentation.IWContext;
import com.idega.presentation.ui.Window;

/**
 * @author laddi
 */
public class ChildCarePrognosisBeforeUpdateWindow extends Window {

	public ChildCarePrognosisBeforeUpdateWindow() {
		this.setWidth(730);
		this.setHeight(450);
		this.setScrollbar(true);
		this.setResizable(true);	
	}

	/**
	 * @see com.idega.presentation.PresentationObject#main(IWContext)
	 */
	public void main(IWContext iwc) throws Exception {
		add(new ChildCarePrognosisStatisticsBeforeUpdate());
	}
}
