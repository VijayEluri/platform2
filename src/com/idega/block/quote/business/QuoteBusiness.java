package com.idega.block.quote.business;



import com.idega.presentation.IWContext;
import java.sql.SQLException;
import com.idega.util.IWTimestamp;
import com.idega.block.quote.data.QuoteEntity;
import com.idega.block.quote.data.QuoteEntityHome;
import com.idega.idegaweb.IWApplicationContext;


public class QuoteBusiness{

  public static final String PARAMETER_QUOTE = "quote";
  public static final String PARAMETER_QUOTE_ID = "quote_id";
  public static final String PARAMETER_QUOTE_DATE = "quote_date";
  public static final String PARAMETER_LOCALE_ID = "locale_id";
  public static final String PARAMETER_QUOTE_TEXT = "quote_text";
  public static final String PARAMETER_QUOTE_AUTHOR = "quote_author";
  public static final String PARAMETER_QUOTE_ORIGIN = "quote_origin";
  public static final String PARAMETER_MODE = "mode";
  public static final String PARAMETER_NEW = "new";
  public static final String PARAMETER_DELETE = "delete";
  public static final String PARAMETER_EDIT = "edit";
  public static final String PARAMETER_SAVE = "save";
  public static final String PARAMETER_CLOSE = "close";
  public static final String PARAMETER_OBJECT_INSTANCE_ID = "qu_o_i_id";

  private static QuoteBusiness instance;

  private QuoteEntityHome quoteHome;

  private QuoteBusiness(){
  }

  public static QuoteBusiness getQuoteBusinessInstace(){
    if(instance==null){
      instance = new QuoteBusiness();
    }
    return instance;
  }


  public QuoteHolder getRandomQuote(IWApplicationContext iwc, int localeID, int objectID){
    QuoteHolder holder = null;
    QuoteHolder newHolder = null;
    String date = null;
    String dateNow = new IWTimestamp().toSQLDateString();

    try {
      holder = (QuoteHolder) iwc.getApplicationAttribute(PARAMETER_QUOTE+"_"+Integer.toString(localeID)+"_"+String.valueOf(objectID));
    }
    catch (Exception e) {
      holder = null;
    }

    date = (String) iwc.getApplicationAttribute(PARAMETER_QUOTE_DATE+"_"+Integer.toString(localeID)+"_"+String.valueOf(objectID));

    if ( date != null && holder != null && date.equalsIgnoreCase(dateNow) )
      return holder;
    else {
      newHolder = getQuoteHolder(getRandomQuote(localeID));
      if ( holder != null ) {
	    while ( holder.getQuoteID() == newHolder.getQuoteID() )
	      newHolder = getQuoteHolder(getRandomQuote(localeID));
      }
      if ( newHolder != null ) {
	    iwc.setApplicationAttribute(PARAMETER_QUOTE+"_"+Integer.toString(localeID)+"_"+String.valueOf(objectID),newHolder);
	    iwc.setApplicationAttribute(PARAMETER_QUOTE_DATE+"_"+Integer.toString(localeID)+"_"+String.valueOf(objectID),dateNow);
	    return newHolder;
      }
      return null;
    }
  }

  public void saveQuote(IWContext iwc,int objectID,int quoteID,int iLocaleID,String quoteOrigin,String quoteText,String quoteAuthor) {
    try{
      boolean update = false;
      if ( quoteID != -1 ) {
	update = true;
      }
      QuoteEntityHome qhome = getQuoteHome();
      QuoteEntity quote = null;
      if ( update ) {
	quote = qhome.findByPrimaryKey(quoteID);
	if ( quote == null ) {
	    quote = qhome.create();
	    update = false;
	}
      }
      else {
	quote = qhome.create();
      }

      if ( quoteOrigin != null || quoteOrigin.length() == 0 ) {
	quote.setQuoteOrigin(quoteOrigin);
      }
      if ( quoteText != null || quoteText.length() == 0 ) {
	quote.setQuoteText(quoteText);
      }
      if ( quoteAuthor != null || quoteAuthor.length() == 0 ) {
	quote.setQuoteAuthor(quoteAuthor);
      }

      if ( !update ) {
	quote.setICLocaleID(iLocaleID);
      }

      try {
	quote.store();
	iwc.removeApplicationAttribute(PARAMETER_QUOTE+"_"+Integer.toString(iLocaleID)+"_"+String.valueOf(objectID));
	iwc.removeApplicationAttribute(PARAMETER_QUOTE_DATE+"_"+Integer.toString(iLocaleID)+"_"+String.valueOf(objectID));
      }
      catch (Exception e) {
	e.printStackTrace(System.err);
      }

    }
    catch(Exception e){
      e.printStackTrace();
    }
  }

  public void deleteQuote(IWContext iwc,int objectID,int quoteID,int localeID) {
    try {
      if ( quoteID != -1 ) {
	    QuoteEntityHome qhome = getQuoteHome();
	QuoteEntity quote = qhome.findByPrimaryKey(quoteID);
	quote.remove();
	iwc.removeApplicationAttribute(PARAMETER_QUOTE+"_"+Integer.toString(localeID)+"_"+String.valueOf(objectID));
	iwc.removeApplicationAttribute(PARAMETER_QUOTE_DATE+"_"+Integer.toString(localeID)+"_"+String.valueOf(objectID));
      }
    }
    catch (Exception e) {
      e.printStackTrace(System.err);
    }
  }


  public QuoteHolder getQuoteHolder(QuoteEntity quote) {
    try{
      QuoteHolder holder = null;
      if ( quote != null ) {
	holder = new QuoteHolder();
	holder.setQuoteID(((Integer)quote.getPrimaryKey()).intValue());
	holder.setAuthor(quote.getQuoteAuthor());
	holder.setOrigin(quote.getQuoteOrigin());
	holder.setText(quote.getQuoteText());
	holder.setLocaleID(quote.getICLocaleID());
      }
      return holder;
    }
    catch(java.rmi.RemoteException rme){
      //rme.printStackTrace();
      throw new RuntimeException("Error creating QuoteHolder: "+rme.getMessage());
    }
  }


  public QuoteHolder getQuoteHolder(int quoteID) {
    try {
      QuoteEntityHome qhome = this.getQuoteHome();
      QuoteEntity quote = qhome.findByPrimaryKey(quoteID);
      return getQuoteHolder(quote);
    }
    catch (Exception e) {
      return null;
    }
  }



  public QuoteEntity getRandomQuote(int localeID) {
    try {
      java.util.Collection cQuotes;
      QuoteEntityHome qhome = getQuoteHome();
      cQuotes = qhome.findAllQuotesByLocale(localeID);

      QuoteEntity[] quotes = (QuoteEntity[])cQuotes.toArray(new QuoteEntity[0]);
      if ( quotes != null ) {
	    if ( quotes.length > 0 ) {
	      int quoteNumber = (int) Math.round(Math.random() * (quotes.length - 1));
	      return quotes[quoteNumber];
	    }
      }
      return null;
    }
    catch (Exception e) {
      return null;
    }
  }


  protected QuoteEntityHome getQuoteHome(){
    if(quoteHome==null){
      try{
	quoteHome = (QuoteEntityHome)com.idega.data.IDOLookup.getHome(QuoteEntity.class);
      }
      catch(java.rmi.RemoteException rme){
	rme.printStackTrace();
      }
    }
    return quoteHome;
  }

}
