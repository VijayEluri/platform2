package com.idega.builder.presentation;

import java.io.IOException;
import java.util.Iterator;
import java.util.List;

import com.idega.builder.business.IBPageHelper;
import com.idega.builder.business.IBPageImportBusiness;
import com.idega.business.IBOLookup;
import com.idega.business.IBOLookupException;
import com.idega.idegaweb.IWApplicationContext;
import com.idega.idegaweb.IWConstants;
import com.idega.idegaweb.IWResourceBundle;
import com.idega.io.UploadFile;
import com.idega.presentation.IWContext;
import com.idega.presentation.PresentationObject;
import com.idega.presentation.Table;
import com.idega.presentation.text.Text;
import com.idega.presentation.ui.CheckBox;
import com.idega.presentation.ui.FileInput;
import com.idega.presentation.ui.Form;
import com.idega.presentation.ui.SubmitButton;
import com.idega.util.datastructures.MessageContainer;

/**
 * <p>Title: idegaWeb</p>
 * <p>Description: </p>
 * <p>Copyright: Copyright (c) 2003</p>
 * <p>Company: idega Software</p>
 * @author <a href="thomas@idega.is">Thomas Hilbig</a>
 * @version 1.0
 * Created on Mar 4, 2004
 */
public class IBPageImporterWindow extends IBPageWindow {

  public static final String IW_BUNDLE_IDENTIFIER  = "com.idega.builder";
  
  public static final String TOP_LEVEL_PAGE_KEY = "ib_page_import_top_level_key";
  public static final String TOP_LEVEL_TEMPLATE_KEY = "ib_template_import_top-level_key";
  
  public static final String SUBMIT_IMPORT_KEY = "ib_page_import_submit_key";
  
  public static final String SUBMIT_CLOSE_KEY = "ib_page_import_close_submit_key";
  public static final String PAGE_KEY = "ib_page_import_key";
  public static final String TEMPLATE_KEY = "ib_page_import_template_key";
  
  public static final String CLOSE_ACTION = "ib_page_import_close_action";
  public static final String IMPORT_ACTION = "ib_page_import_action";
  
  
  boolean topLevelForPagesIsChosen = false;
  boolean topLevelForTemplatesIsChosen = false;
  
  private int parentPageId = -1; 
  private int templatePageId = -1;
  
  private MessageContainer messageContainer = null;
	
	private IBPageImportBusiness pageImportBusiness = null;
	
	public String getBundleIdentifier() {
    return IW_BUNDLE_IDENTIFIER;
  }
	
  public IBPageImporterWindow() {
    setWidth(300);
    setHeight(400);
    setScrollbar(true);
    setResizable(true);
  }

  public void main(IWContext iwc) throws Exception {
  	setTitle("PageImporter");
  	String action = parseAction(iwc);
		IWResourceBundle resourceBundle = getResourceBundle(iwc);
		if (doAction(action, resourceBundle, iwc)) {
			getContent(resourceBundle, iwc);
		}
		else {
			getErrorContent(resourceBundle);
		}
  }
  
  private void getContent(IWResourceBundle resourceBundle, IWContext iwc)  {
  	int numberOfRows = (messageContainer == null) ? 6 : 7;
  	int row = 1;
  	Table table = new Table( 1, numberOfRows);
  	// add message if there is a message
  	if (messageContainer != null) {
  		Text text = new Text(messageContainer.getMainMessage());
  		text.setBold();
  		table.add(text,1, row++);
  	}
  	// add file input
  	table.add(getFileInput(), 1, row++);
  	// top level checkbox
  	table.add(getTopLevelCheckBox(TOP_LEVEL_PAGE_KEY, topLevelForPagesIsChosen,  resourceBundle), 1, row++);
		// page chooser
  	if (! topLevelForPagesIsChosen) {
  		table.add(getPageChooser(PAGE_CHOOSER_NAME, iwc), 1, row++);
  	}
  	table.add(getTopLevelCheckBox(TOP_LEVEL_TEMPLATE_KEY, topLevelForTemplatesIsChosen, resourceBundle), 1, row++);
  	if (! topLevelForTemplatesIsChosen) {
  		table.add(getTemplateChooser(TEMPLATE_CHOOSER_NAME, iwc, IBPageHelper.TEMPLATE), 1, row++);
  	}
  	Form form = new Form();
  	form.add(table);
  	form.add(getButtons(resourceBundle));
  	add(form);
  }
  
  private void getErrorContent(IWResourceBundle resourceBundle) {
  	List messages = messageContainer.getMessages();
  	int numberOfRows = 1 + ((messages == null) ? 0 : messages.size());
  	Table table = new Table(1, numberOfRows);
  	int row = 1;
  	String mainErrorMessage = messageContainer.getMainMessage();
  	if (mainErrorMessage == null) {
  		mainErrorMessage = resourceBundle.getLocalizedString("ib_page_export_missing_modules", "Some modules are missing:");
  	}
  	Text text = new Text(mainErrorMessage);
  	text.setBold();
  	table.add(text, 1, row++);
  	if (messages != null) {
	  	Iterator iterator = messages.iterator();
	  	while (iterator.hasNext()) {
	  		String message = (String) iterator.next();
	  		table.add(message, 1, row++);
	  	}
  	}
  	Form form = new Form();
  	form.add(table);
  	form.add(getCloseButton(resourceBundle));
  	add(form);
  }
  	
  	
  private PresentationObject getTopLevelCheckBox(String keyName, boolean setChecked, IWResourceBundle resourceBundle) {
  	Table table = new Table(2,1);
 		CheckBox topLevelCheckBox = new CheckBox(keyName, "true");
		topLevelCheckBox.setChecked(setChecked);
		Text topLevelText = new Text(resourceBundle.getLocalizedString(keyName, "Top level") + ":");
		topLevelText.setFontStyle(IWConstants.BUILDER_FONT_STYLE_LARGE);
		table.add(topLevelText, 1, 1);
		table.add(topLevelCheckBox, 2, 1);
		topLevelCheckBox.setOnClick("this.form.submit()");
		return table;
  }

  
  private FileInput getFileInput() {
  	FileInput fileInput = new FileInput();
  	return fileInput;
  }
  	
  private String parseAction(IWContext iwc) {
  	String action = null;
  	topLevelForPagesIsChosen = (new Boolean(iwc.getParameter(TOP_LEVEL_PAGE_KEY))).booleanValue();
  	topLevelForTemplatesIsChosen = (new Boolean(iwc.getParameter(TOP_LEVEL_TEMPLATE_KEY))).booleanValue();
  	parentPageId = getParentPageId(PAGE_CHOOSER_NAME,iwc);
  	templatePageId = getParentPageId(TEMPLATE_CHOOSER_NAME, iwc);
  	if (iwc.isParameterSet(SUBMIT_IMPORT_KEY)) {
  		action = IMPORT_ACTION;
  		setOnUnLoad("window.opener.parent.parent.location.reload()");
  	}
  	if (iwc.isParameterSet(SUBMIT_CLOSE_KEY)) {
  		action = CLOSE_ACTION;
  	}
  	return action;
  }
  
  private boolean doAction(String action, IWResourceBundle resourceBundle, IWContext iwc)  {
  	messageContainer = null;
  	if (CLOSE_ACTION.equals(action)) {
  		close();
  		return true;
  	}
  	else if (IMPORT_ACTION.equals(action)) {
	  	UploadFile file = iwc.getUploadedFile();
	  	if (file != null) {
	  		try {
	  			messageContainer = importPages(file, iwc);
	  		}
	  		catch (IOException ex) {
	  			messageContainer = new MessageContainer();
	  			StringBuffer mainMessage = new StringBuffer(resourceBundle.getLocalizedString("ib_page_import_error", "Import failed, but some elements might have been already imported"));
	  			mainMessage.append(" ").append(ex.getMessage());
	  			messageContainer.setMainMessage(mainMessage.toString());
	  			return false;
	  		}
	  		if (messageContainer == null) {
	  			messageContainer = new MessageContainer();
	  			messageContainer.setMainMessage(resourceBundle.getLocalizedString("ib_page_import_success", "Files were successfully imported"));
	  			return true;
	  		}
	  		return false;
	  	}
	  	else {
	  		messageContainer = new MessageContainer();
	  		String mainMessage = resourceBundle.getLocalizedString("ib_page_import_file_does_not_exist", "Import failed, the uploaded file couldn't be found");
	  		messageContainer.setMainMessage(mainMessage);
	  		return false;
	  	}
	  }
  	return true;
  }

  private int getParentPageId(String keyName, IWContext iwc) {
  	if (! iwc.isParameterSet(keyName)) {
  		return -1;
  	}
  	String parentPageIdString = iwc.getParameter(keyName);
  	try {
			return Integer.parseInt(parentPageIdString);
		}
		catch (NumberFormatException ex) {
			return -1;
		}
  }
  
  private MessageContainer importPages(UploadFile file, IWContext iwc) throws IOException { 
  	return getPageImportBusiness(iwc).importPages(file, true, parentPageId, templatePageId, iwc);
	}
  	
  		
				
  private PresentationObject getButtons(IWResourceBundle resourceBundle) {
  	SubmitButton importButton = 
      new SubmitButton(resourceBundle.getLocalizedString("ib_page_import_Import","Import"), SUBMIT_IMPORT_KEY, "true");
  	importButton.setAsImageButton(true);
  	SubmitButton closeButton = getCloseButton(resourceBundle);
  	Table table = new Table(2,1);
  	table.add(importButton, 1,1);
  	table.add(closeButton, 2,1);
  	return table;
  }
  	
  		
	private SubmitButton getCloseButton(IWResourceBundle resourceBundle) {
		SubmitButton closeButton = 
      new SubmitButton(resourceBundle.getLocalizedString("ib_page_import_Close", "Close"), SUBMIT_CLOSE_KEY, "true");
  	closeButton.setAsImageButton(true);
		return closeButton;
	}

	private IBPageImportBusiness getPageImportBusiness(IWApplicationContext iwac) throws IBOLookupException {
		if (pageImportBusiness == null) {
			pageImportBusiness =  (IBPageImportBusiness) IBOLookup.getServiceInstance(iwac,IBPageImportBusiness.class);
		}
		return pageImportBusiness;
	}
		
}