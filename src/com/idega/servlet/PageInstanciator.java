package com.idega.servlet;
import com.idega.idegaweb.IWMainApplication;
import com.idega.presentation.IWContext;
import com.idega.presentation.Page;
import com.idega.presentation.jsp.JSPModule;
/**
*@author <a href="mailto:tryggvi@idega.is">Tryggvi Larusson</a>
*@version 1.0
*/
public class PageInstanciator extends JSPModule {
	public void initializePage() {
		try {
			//String servletName = this.getServletConfig().getServletName();
			//System.out.println("Inside initializePage for "+servletName);
			setPage(getThisPage(getIWContext()));
		}
		catch (Exception ex) {
			ex.printStackTrace(System.err);
		}
	}
	private Page getThisPage(IWContext iwc) {
		String className =
			IWMainApplication.decryptClassName(iwc.getParameter(IWMainApplication.classToInstanciateParameter));
		try {
			return (Page) Class.forName(className).newInstance();
		}
		catch (Exception ex) {
			ex.printStackTrace(System.err);
			return null;
		}
	}
}
//-------------
//- End of file
//-------------
