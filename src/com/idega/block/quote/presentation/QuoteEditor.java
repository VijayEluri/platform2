package com.idega.block.quote.presentation;

import com.idega.presentation.IWContext;
import com.idega.presentation.ui.TextArea;
import com.idega.presentation.ui.TextInput;
import com.idega.presentation.ui.SubmitButton;
import com.idega.presentation.ui.CloseButton;
import com.idega.presentation.ui.HiddenInput;
import com.idega.idegaweb.presentation.IWAdminWindow;
import com.idega.core.localisation.business.ICLocaleBusiness;
import com.idega.core.accesscontrol.business.AccessControl;
import com.idega.idegaweb.IWBundle;
import com.idega.idegaweb.IWResourceBundle;
import com.idega.block.quote.business.QuoteBusiness;
import com.idega.block.quote.business.QuoteHolder;

public class QuoteEditor extends IWAdminWindow{

private int _quoteID = -1;
private boolean _update = false;
private boolean _save = false;
private int _objectID = -1;
private int _iLocaleID;
private QuoteHolder _quote;

private final static String IW_BUNDLE_IDENTIFIER="com.idega.block.quote";
private IWBundle _iwb;
private IWResourceBundle _iwrb;

public QuoteEditor(){
  setWidth(420);
  setHeight(270);
  setUnMerged();
  setMethod("get");
}

  public void main(IWContext iwc) throws Exception {
    _iwb = getBundle(iwc);
    _iwrb = getResourceBundle(iwc);
    addTitle(_iwrb.getLocalizedString("quote_admin","Quote Admin"));
    _iLocaleID = ICLocaleBusiness.getLocaleId(iwc.getCurrentLocale());

    try {
      _quoteID = Integer.parseInt(iwc.getParameter(QuoteBusiness.PARAMETER_QUOTE_ID));
      _quote = getQuoteBusiness().getQuoteHolder(_quoteID);
    }
    catch (NumberFormatException e) {
      _quoteID = -1;
    }

    _objectID = Integer.parseInt(iwc.getParameter(QuoteBusiness.PARAMETER_OBJECT_INSTANCE_ID));

    String mode = iwc.getParameter(QuoteBusiness.PARAMETER_MODE);

    if ( mode.equalsIgnoreCase(QuoteBusiness.PARAMETER_EDIT) ) {
      if ( _quoteID != -1 ) {
	    _update = true;
      }
      processForm();
    }
    else if ( mode.equalsIgnoreCase(QuoteBusiness.PARAMETER_NEW) ) {
      processForm();
    }
    else if ( mode.equalsIgnoreCase(QuoteBusiness.PARAMETER_DELETE) ) {
      deleteQuote(iwc);
    }
    else if ( mode.equalsIgnoreCase(QuoteBusiness.PARAMETER_SAVE) ) {
      saveQuote(iwc);
    }
  }

  private void processForm() {
    TextInput quoteOrigin = new TextInput(QuoteBusiness.PARAMETER_QUOTE_ORIGIN);
      quoteOrigin.setLength(24);
    TextInput quoteAuthor = new TextInput(QuoteBusiness.PARAMETER_QUOTE_AUTHOR);
      quoteAuthor.setLength(24);
    TextArea quoteText = new TextArea(QuoteBusiness.PARAMETER_QUOTE_TEXT,40,6);

    if ( _update && _quote != null ) {
      if ( _quote.getOrigin() != null ) {
	quoteOrigin.setContent(_quote.getOrigin());
      }
      if ( _quote.getText() != null ) {
	quoteText.setContent(_quote.getText());
      }
      if ( _quote.getAuthor() != null ) {
	quoteAuthor.setContent(_quote.getAuthor());
      }
    }

    addLeft(_iwrb.getLocalizedString("origin","Origin")+":",quoteOrigin,true);
    addLeft(_iwrb.getLocalizedString("quote","Quote")+":",quoteText,true);
    addLeft(_iwrb.getLocalizedString("author","Author")+":",quoteAuthor,true);
    addHiddenInput(new HiddenInput(QuoteBusiness.PARAMETER_QUOTE_ID,Integer.toString(_quoteID)));
    addHiddenInput(new HiddenInput(QuoteBusiness.PARAMETER_OBJECT_INSTANCE_ID,Integer.toString(_objectID)));

    addSubmitButton(new CloseButton());
    addSubmitButton(new SubmitButton(_iwrb.getLocalizedImageButton("save","SAVE"),QuoteBusiness.PARAMETER_MODE,QuoteBusiness.PARAMETER_SAVE));
  }

  private void saveQuote(IWContext iwc) {
    String quoteOrigin = iwc.getParameter(QuoteBusiness.PARAMETER_QUOTE_ORIGIN);
    String quoteText = iwc.getParameter(QuoteBusiness.PARAMETER_QUOTE_TEXT);
    String quoteAuthor = iwc.getParameter(QuoteBusiness.PARAMETER_QUOTE_AUTHOR);

    getQuoteBusiness().saveQuote(iwc,_objectID,_quoteID,_iLocaleID,quoteOrigin,quoteText,quoteAuthor);

    setParentToReload();
    close();
  }

  private void deleteQuote(IWContext iwc) {
    getQuoteBusiness().deleteQuote(iwc,_objectID,_quoteID,_iLocaleID);
    setParentToReload();
    close();
  }

  public String getBundleIdentifier(){
    return IW_BUNDLE_IDENTIFIER;
  }

  private QuoteBusiness getQuoteBusiness(){
    return QuoteBusiness.getQuoteBusinessInstace();
  }
}

