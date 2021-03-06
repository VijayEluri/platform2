/*
 * $Id: UpdateManager.java,v 1.6.2.1 2007/01/12 19:31:58 idegaweb Exp $
 *
 * Copyright (C) 2002 Idega hf. All Rights Reserved.
 *
 * This software is the proprietary information of Idega hf.
 * Use is subject to license terms.
 *
 */
package com.idega.development.presentation;

import java.rmi.RemoteException;
import java.util.Collections;
import java.util.Iterator;
import java.util.List;
import com.idega.business.IBOLookup;
import com.idega.idegaweb.IWApplicationContext;
import com.idega.idegaweb.IWBundle;
import com.idega.idegaweb.IWMainApplication;
import com.idega.idegaweb.IWResourceBundle;
import com.idega.presentation.Block;
import com.idega.presentation.IWContext;
import com.idega.presentation.Table;
import com.idega.presentation.text.Text;
import com.idega.presentation.ui.CheckBox;
import com.idega.presentation.ui.DeselectAllButton;
import com.idega.presentation.ui.Form;
import com.idega.presentation.ui.SelectAllButton;
import com.idega.presentation.ui.SubmitButton;
import com.idega.versioncontrol.business.UpdateService;

/**
 * A class to get versions for all the bundles installed on the system.
 * 
 * @author <a href="palli@idega.is">Pall Helgason</a>
 * @version 1.0
 */
public class UpdateManager extends Block {
  private final static String IW_BUNDLE_IDENTIFIER="com.idega.core";
  private Table table;
  private IWBundle iwb;
private String PARAM_UPDATE_BUNDLEIDENTIFIER="iw_updman_bundleid";
private String PARAM_EXECUTE_UPDATE="iw_updateman_execupdate";

  public UpdateManager() {
  }

  public void main(IWContext iwc)throws Exception{
	this.iwb = getBundle(iwc);
  	handleAction(iwc);
    IWResourceBundle iwrb = this.iwb.getResourceBundle(iwc);
    add(IWDeveloper.getTitleTable(this.getClass()));
		if (!iwc.isIE()) {
			getParentPage().setBackgroundColor("#FFFFFF");
		}

    IWMainApplication iwma = iwc.getIWMainApplication();
    List bundles = getRegisteredBundles(iwma);

		Text blockHeader = IWDeveloper.getText("Block");
		blockHeader.setFontColor("#0E2456");
		Text versionHeader = IWDeveloper.getText("update");
		versionHeader.setFontColor("#0E2456");

		/*Package pack = Package.getPackage("com.idega.core");
		String defaultVersion = null;
		if (pack != null) {
			defaultVersion = pack.getImplementationVersion();
			if (defaultVersion == null)
				defaultVersion = "No Implementation-Version definition for package com.idega.core in Manifest";	
		}
		else {
			defaultVersion = "No Implementation-Version definition for package com.idega.core in Manifest";	
		}*/
	Form form = new Form();
	form.maintainParameter(IWDeveloper.PARAMETER_CLASS_NAME);
	add(form);
    this.table = new Table(2,bundles.size() + 1);
    this.table.setWidth(1,"160");
    this.table.add(blockHeader,1,1);
    this.table.add(versionHeader,2,1);
    int row = 2;
    Iterator it = bundles.iterator();
    String bundleName = null;
    while (it.hasNext()) {
      IWBundle item = (IWBundle)it.next();
      bundleName = item.getBundleIdentifier();
      this.table.add(bundleName,1,row);
/*			pack = Package.getPackage(bundleName);
			String version = null;
			if (pack != null) {
				version = pack.getImplementationVersion();
				if (version == null)
					version = defaultVersion;
			}
			else {
				version = defaultVersion;
			}
      
      table.add(version,2,row++);*/
      CheckBox bundleCheck = new CheckBox(this.PARAM_UPDATE_BUNDLEIDENTIFIER,item.getBundleIdentifier());
      bundleCheck.setChecked(true);
      this.table.add(bundleCheck,2,row++);
    }
    form.add(this.table);
    form.add(new SubmitButton(this.PARAM_EXECUTE_UPDATE,iwrb.getLocalizedString(this.PARAM_EXECUTE_UPDATE,"Update")));
    form.add(new DeselectAllButton(iwrb.getLocalizedString("clear_all","Clear all")));
    form.add(new SelectAllButton(iwrb.getLocalizedString("select_all", "Select all")));
  }

	/**
 * @param iwc
 */
private void handleAction(IWContext iwc) throws Exception
{
	IWResourceBundle iwrb = this.iwb.getResourceBundle(iwc);
	if(iwc.isParameterSet(this.PARAM_EXECUTE_UPDATE)){
		executeUpdate(iwc);
		add(iwrb.getLocalizedString("iw_updateman_updateex","Update Executed"));
	}
}

	/**
	 * @param iwc
	 */
	private void executeUpdate(IWContext iwc) throws Exception
	{
		UpdateService updateService = this.getUpdateService(iwc);
		String[] bundles = iwc.getParameterValues(this.PARAM_UPDATE_BUNDLEIDENTIFIER);
		for (int i = 0; i < bundles.length; i++)
		{
			String identifier = bundles[i];
			updateService.updateBundleToMostRecentVersion(identifier);
		}
		
	}

	/**
	 * Gets a List of all the bundles registered on the system.
	 * 
	 * 
	 */
  public static List getRegisteredBundles(IWMainApplication iwma){
    List bundles = iwma.getRegisteredBundles();
    Collections.sort(bundles);

    return bundles;
  }

  public String getBundleIdentifier(){
    return IW_BUNDLE_IDENTIFIER;
  }
  
  private UpdateService getUpdateService(IWApplicationContext iwac) throws RemoteException{
  	return (UpdateService)IBOLookup.getServiceInstance(iwac,UpdateService.class);
  }
  
}