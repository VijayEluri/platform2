package com.idega.block.creditcard.business;
/*
* Q&D java demo for communicating with kortathjonustan's RPCS
*
* Gunnar Mar Gunnarsson 9. Dec 2003
*/

import java.io.IOException;
import java.io.UnsupportedEncodingException;
import java.net.URLEncoder;
import java.util.Collection;
import java.util.Hashtable;
import java.util.Iterator;
import java.util.Set;
import java.util.Vector;

import com.idega.block.creditcard.data.CreditCardMerchant;
import com.idega.block.creditcard.data.KortathjonustanAuthorisationEntries;
import com.idega.block.creditcard.data.KortathjonustanAuthorisationEntriesHome;
import com.idega.data.IDOLookup;
import com.idega.util.IWTimestamp;

public class KortathjonustanCreditCardClient implements CreditCardClient
{
	private String HOST_NAME;// 									= "test.kortathjonustan.is";
	private int HOST_PORT	;//										= 8443;
	String strKeystore;// = "/demoFolder/testkeys.jks";
	String strKeystorePass;// = "changeit";

	private String PROPERTY_USER								= "user";
	private String PROPERTY_PASSWORD						= "pwd";
	private String PROPERTY_SITE								= "site";
	
	private String PROPERTY_MERCHANT_LANGUAGE	= "mlang";	// valid = en, is (default en)
	private String PROPERTY_CLIENT_LANGUAGE		= "clang";	// valid = en, is (default en)
	private String PROPERTY_CLIENT_IP					= "cip";
	
	private String PROPERTY_CARDHOLDER_NAME		= "d2name";
	private String PROPERTY_CC_NUMBER					= "d2";
	private String PROPERTY_AMOUNT		 					= "d4";
	private String PROPERTY_CURRENCY_EXPONENT	= "de4";
	private String PROPERTY_CURRENT_DATE				= "d12";
	private String PROPERTY_CC_EXPIRE					= "d14";
	private String PROPERTY_REFERENCE_ID				= "d31";
	private String PROPERTY_APPROVAL_CODE			= "d38"; // Value gotten from Response
	private String PROPERTY_ACTION_CODE				= "d39"; // Value gotten from Response
	private String PROPERTY_ACCEPTOR_TERM_ID		= "d41";
	private String PROPERTY_ACCEPTOR_IDENT			= "d42";
	private String PROPERTY_CC_VERIFY_CODE			= "d47";
	private String PROPERTY_CURRENCY_CODE			= "d49";
	private String PROPERTY_ORIGINAL_DATA_ELEMENT	= "d56"; // Value from Response
	
	private String PROPERTY_AMOUNT_ECHO				= "o4";		// Echo from d4
	private String PROPERTY_CURRENT_DATE_ECHO	= "o12";	// Echo from d12
	private String PROPERTY_APPROVAL_CODE_ECHO	= "o38";	// Echo from d38
	private String PROPERTY_ACTION_CODE_ECHO		= "o39";	// Echo from d39
	private String PROPERTY_SHIPPING_ADDRESS		=	"d2saddr";
	private String PROPERTY_SHIPPING_CITY			=	"d2scity";
	private String PROPERTY_SHIPPING_ZIP				=	"d2szip";
	private String PROPERTY_SHIPPING_COUNTRY		=	"d2sctr";
	private String PROPERTY_CARD_BRAND_NAME = "d2brand";
	private String PROPERTY_TOTAL_RESPONSE			= "totalResponse";
	
	private String PROPERTY_SETTLEMENT_REFERENCE_NUMBER	= "d37";
	private String PROPERTY_ACTION_CODE_TEXT		= "d39text";
	private String PROPERTY_ERROR_CODE					=	"error";
	private String PROPERTY_ERROR_TEXT					= "errortext";
	
	private static String REQUEST_TYPE_AUTHORIZATION			= "/rpc/RequestAuthorisation";
	private static String REQUEST_TYPE_CAPTURE						= "/rpc/RequestCapture";
	private static String REQUEST_TYPE_REVERSAL					= "/rpc/RequestReversal";
	
	private static String CODE_AUTHORIZATOIN_APPROVED = "000";
	private static String CODE_AUTHORIZATOIN_DECLINED = "100";
	private static String CODE_SYSTEM_FAILURE_RETRY = "946";
	private static String CODE_SYSTEM_FAILURE_ERROR = "909";
	
	
	private String SITE = "22";
	private String USER = "idega";
	private String PASSWORD = "zde83af";
	private String ACCEPTOR_TERM_ID = "90000022";
	private String ACCEPTOR_IDENTIFICATION = "8180001";
	// tmp values
	
	private String strCCNumber								= null;//"5413033024823099";
	private String strCCExpire								= null;//"0504";
	private String strCCVerify								= null;//"150";
	
	private String strAmount									= null;//"2"; // 1 aur
	//private String strAmount									= "3000";
	private String strName										= null; //"Grimur";
	private String strCurrentDate							= null;//"031216113900";
	private String strCurrencyCode							= null; //"352"; // ISK, check Appendix A, page 20
	private String strCurrencyExponent					= null;
	private String strReferenceNumber					= null;//Integer.toString((int) (Math.random() * 43200));
	
	private Hashtable returnedProperties = null;
	// Test indicator
	private boolean bTestServer = false;
	private CreditCardTransaction cct = null;

	public KortathjonustanCreditCardClient(String host, int port, String keystoreLocation, String keystorePass, CreditCardMerchant merchant) {
		this(host, port, keystoreLocation, keystorePass, merchant.getLocation(), merchant.getUser(), merchant.getPassword(), merchant.getTerminalID(), merchant.getMerchantID());
	}
	
	public KortathjonustanCreditCardClient(String host, int port, String keystoreLocation, String keystorePass, String site, String user, String password, String acceptorTerminalID, String acceptorIdentification) {
		HOST_NAME = host;
		HOST_PORT = port;
		strKeystore = keystoreLocation;
		strKeystorePass = keystorePass;
		
		SITE = site;
		USER = user;
		PASSWORD = password;
		ACCEPTOR_TERM_ID = acceptorTerminalID;
		ACCEPTOR_IDENTIFICATION = acceptorIdentification;
	}	

	private int getAmountWithExponents(double amount) {
		int amountMultiplier = (int) Math.pow(10, Double.parseDouble(strCurrencyExponent));

		return (int) amount * amountMultiplier;

	}
	
	private void setCurrencyAndAmount(String currency, double amount) throws CreditCardAuthorizationException {
		if (currency != null) {
			int amountMultiplier = 100;
			
			if (currency.equalsIgnoreCase("ISK")) {
				strCurrencyCode = "352";
				strCurrencyExponent = "2";
				amountMultiplier = (int) Math.pow(10, Double.parseDouble(strCurrencyExponent));
			} else if (currency.equalsIgnoreCase("USD")) {
				strCurrencyCode = "840";
				strCurrencyExponent = "2";
				amountMultiplier = (int) Math.pow(10, Double.parseDouble(strCurrencyExponent));
			} else if (currency.equalsIgnoreCase("GBP")) {
				strCurrencyCode = "826";
				strCurrencyExponent = "2";
				amountMultiplier = (int) Math.pow(10, Double.parseDouble(strCurrencyExponent));
			} else if (currency.equalsIgnoreCase("DKK")) {
				strCurrencyCode = "208";
				strCurrencyExponent = "2";
				amountMultiplier = (int) Math.pow(10, Double.parseDouble(strCurrencyExponent));
			} else if (currency.equalsIgnoreCase("EUR")) {
				strCurrencyCode = "978";
				strCurrencyExponent = "2";
				amountMultiplier = (int) Math.pow(10, Double.parseDouble(strCurrencyExponent));
			} else {
				throw new CreditCardAuthorizationException("Unsupported currency ("+currency+")");
			}
			/* Setting amount with correct  */
			strAmount = Integer.toString((int) amount * amountMultiplier);
		} else {
			throw new CreditCardAuthorizationException("Currency is missing");
		}
	}
	
	public String doSale(String nameOnCard, String cardnumber, String monthExpires, String yearExpires, String ccVerifyNumber, double amount, String currency, String referenceNumber) throws CreditCardAuthorizationException {
		IWTimestamp stamp = IWTimestamp.RightNow();
		strName = nameOnCard;
		strCCNumber = cardnumber;
		strCCExpire = yearExpires+monthExpires;
		strCCVerify = ccVerifyNumber;
		setCurrencyAndAmount(currency, amount);
		strCurrentDate = getDateString(stamp);
		strReferenceNumber = referenceNumber;

		
		Hashtable returnedProperties = getFirstResponse();
		String authCode = null;
		if (returnedProperties != null) {
			Hashtable returnedCaptureProperties = finishTransaction(returnedProperties);
			if (returnedCaptureProperties != null && returnedCaptureProperties.get(PROPERTY_APPROVAL_CODE).toString() != null) {
				//System.out.println("Approval Code = "+returnedCaptureProperties.get(PROPERTY_APPROVAL_CODE).toString());
				authCode = returnedCaptureProperties.get(PROPERTY_APPROVAL_CODE).toString();//returnedCaptureProperties;
				try {

					KortathjonustanAuthorisationEntriesHome authHome = (KortathjonustanAuthorisationEntriesHome) IDOLookup.getHome(KortathjonustanAuthorisationEntries.class);
					KortathjonustanAuthorisationEntries auth = authHome.create();

					String tmpCardNum = CreditCardBusinessBean.encodeCreditCardNumber(cardnumber);
					
					auth.setAmount(Double.parseDouble(strAmount));
					auth.setAuthorizationCode(authCode);
					auth.setBrandName(returnedCaptureProperties.get(PROPERTY_CARD_BRAND_NAME).toString());
					auth.setCardExpires(strCCExpire);
					auth.setCardNumber(tmpCardNum);
					auth.setCurrency(currency);
					auth.setDate(stamp.getDate());
					auth.setErrorNumber(returnedCaptureProperties.get(PROPERTY_ERROR_CODE).toString());
					auth.setErrorText(returnedCaptureProperties.get(PROPERTY_ERROR_TEXT).toString());
					auth.setTransactionType(KortathjonustanAuthorisationEntries.AUTHORIZATION_TYPE_SALE);
					auth.setServerResponse(returnedCaptureProperties.get(PROPERTY_TOTAL_RESPONSE).toString());
					//System.out.println("Response length = "+returnedCaptureProperties.get(PROPERTY_TOTAL_RESPONSE).toString().length());
					auth.store();
				
					
					//  TODO setja store inn aftur
					/*
					CreditCardBusiness cBus = (CreditCardBusiness) IBOLookup.getServiceInstance(IWContext.getInstance(), CreditCardBusiness.class);
					
					System.out.println("amount     = "+amount);
					System.out.println("authCode   = "+authCode);
					System.out.println("brand        = "+returnedCaptureProperties.get(PROPERTY_CARD_BRAND_NAME));
					System.out.println("strCCExpire = "+strCCExpire);
					System.out.println("CardNumb  = "+CreditCardBusinessBean.encodeCreditCardNumber(cardnumber)+" ("+cardnumber+", "+cBus.verifyCreditCardNumber(cardnumber, auth)+")");
					System.out.println("Currency     = "+currency);
					System.out.println("Date          = "+stamp.getDate());
					System.out.println("ErrorNum    = "+returnedCaptureProperties.get(PROPERTY_ERROR_CODE));
					System.out.println("ErrorText    = "+returnedCaptureProperties.get(PROPERTY_ERROR_TEXT));
					System.out.println("Type          = "+KortathjonustanAuthorisationEntries.AUTHORIZATION_TYPE_SALE);
					System.out.println("Response   = "+returnedCaptureProperties.get(PROPERTY_TOTAL_RESPONSE));
					*/			


				} catch (Exception e) {
		      System.err.println("Unable to save entry to database");
		      throw new CreditCardAuthorizationException(e);
				}
			}
		}
		
		
		return authCode;
	}
	
	public String doRefund(String cardnumber, String monthExpires, String yearExpires, String ccVerifyNumber, double amount, String currency, String captureProperties) throws CreditCardAuthorizationException {
		IWTimestamp stamp = IWTimestamp.RightNow();
		strCCNumber = cardnumber;
		strCCExpire = yearExpires+monthExpires;
		strCCVerify = ccVerifyNumber;
		setCurrencyAndAmount(currency, amount);
		strCurrentDate = getDateString(stamp);

		Hashtable properties = doRefund(getAmountWithExponents(amount), parseResponse(captureProperties));
		try {
			return properties.get(PROPERTY_APPROVAL_CODE).toString();
		} catch (NullPointerException n) {
			throw new CreditCardAuthorizationException(n);
		}

	}
	
	private String getDateString(IWTimestamp stamp) {
		// stamp.addHours(-1); // Temp for gimmi to test
		return stamp.getDateString("yyMMddHHmmss");
	}
	
	public static void main(String[] args) throws Exception 
  {
			String host = "test.kortathjonustan.is";
			int port											= 8443;
		 String SITE = "22";
		 String USER = "idega";
		 String PASSWORD = "zde83af";
		 String ACCEPTOR_TERM_ID = "90000022";
		 String ACCEPTOR_IDENTIFICATION = "8180001";

			String strCCNumber								= "5413033024823099";
			String strCCExpire								= "0504";
			String strCCVerify								= "150";
			String strReferenceNumber					= Integer.toString((int) (Math.random() * 43200));
			String keystore = "/demoFolder/testkeys.jks";
			String keystorePass = "changeit";

		KortathjonustanCreditCardClient client = new KortathjonustanCreditCardClient(host, port, keystore, keystorePass, SITE, USER, PASSWORD, ACCEPTOR_TERM_ID, ACCEPTOR_IDENTIFICATION);
		try {
			String tmp = client.doSale("Gr�mur Steri", strCCNumber, strCCExpire.substring(2, 4), strCCExpire.substring(0, 2), strCCVerify, 1, "ISK", strReferenceNumber );
			
			//CreditCardBusiness cBus = (CreditCardBusiness) IBOLookup.getServiceInstance(IWContext.getInstance(), CreditCardBusiness.class);
			//KortathjonustanAuthorisationEntries entry = (KortathjonustanAuthorisationEntries) cBus.getAuthorizationEntry(supp, tmp);
			
			
			//String tmp2 = client.doRefund(strCCNumber,  strCCExpire.substring(2, 4), strCCExpire.substring(0, 2), strCCVerify, 1, "ISK", entry.getResponseString());
			System.out.println("AuthorizationNumber = "+tmp);
			//System.out.println("RefundAuthNumber    = "+tmp2);
		} catch (CreditCardAuthorizationException e) {
			System.out.println(" ---- Exception ----");
			System.out.println("DisplayText = "+e.getDisplayError());
			System.out.println("ErrorText   = "+e.getErrorMessage());
			System.out.println("ErrorNum   = "+e.getErrorNumber());
			System.out.println(" -----------------------");
			e.printStackTrace(System.err);
		}

	}
	
	
	private Hashtable doRefund(int iAmountToRefund, Hashtable captureProperties) throws CreditCardAuthorizationException {
		// TODO tjekka ef amountToRefund er sama og upphaflega refundi� ...
		System.out.println(" ------ REFUND ------");
		Hashtable refundProperties = new Hashtable();
		try {

			int iAmount = 0;
			try {
				iAmount = Integer.parseInt(captureProperties.get(PROPERTY_AMOUNT).toString());
				if (iAmountToRefund > iAmount) {
					CreditCardAuthorizationException e =  new CreditCardAuthorizationException("Amount to refund can not be higher that the original amount");
					throw e;
				}
			} catch (NumberFormatException e1) {
				throw new CreditCardAuthorizationException("Amount must be a number");
			}
			
			StringBuffer strPostData= new StringBuffer();
			// "DEFAULT" PROPERTIES
			appendProperty(strPostData, PROPERTY_USER, USER);
			appendProperty(strPostData, PROPERTY_PASSWORD, PASSWORD);
			appendProperty(strPostData, PROPERTY_SITE, SITE);
			appendProperty(strPostData, PROPERTY_CURRENT_DATE, getDateString(IWTimestamp.RightNow()));
			// TODO IMPLEMENT
			//appendProperty(strPostData, PROPERTY_MERCHANT_LANGUAGE)
			//appendProperty(strPostData, PROPERTY_CLIENT_LANGUAGE)
			appendProperty(strPostData, PROPERTY_AMOUNT_ECHO, strAmount);
			
			
			appendProperty(strPostData, PROPERTY_AMOUNT, Integer.toString(iAmountToRefund));
			if (iAmount > iAmountToRefund) {
				appendProperty(strPostData, PROPERTY_AMOUNT_ECHO, captureProperties.get(PROPERTY_AMOUNT).toString());
			}
			appendProperty(strPostData, PROPERTY_CURRENCY_EXPONENT, captureProperties.get(PROPERTY_CURRENCY_EXPONENT).toString());
			appendProperty(strPostData, PROPERTY_REFERENCE_ID, captureProperties.get(PROPERTY_REFERENCE_ID).toString());
			appendProperty(strPostData, PROPERTY_ACCEPTOR_TERM_ID, captureProperties.get(PROPERTY_ACCEPTOR_TERM_ID).toString());
			appendProperty(strPostData, PROPERTY_ACCEPTOR_IDENT, captureProperties.get(PROPERTY_ACCEPTOR_IDENT).toString());
			appendProperty(strPostData, PROPERTY_CURRENCY_CODE, captureProperties.get(PROPERTY_CURRENCY_CODE).toString());
			appendProperty(strPostData, PROPERTY_ORIGINAL_DATA_ELEMENT, captureProperties.get(PROPERTY_ORIGINAL_DATA_ELEMENT).toString());
			appendProperty(strPostData, PROPERTY_CURRENT_DATE_ECHO, captureProperties.get(PROPERTY_CURRENT_DATE).toString());
			appendProperty(strPostData, PROPERTY_ACTION_CODE_ECHO, captureProperties.get(PROPERTY_ACTION_CODE).toString());
			appendProperty(strPostData, PROPERTY_APPROVAL_CODE_ECHO, captureProperties.get(PROPERTY_APPROVAL_CODE).toString());

			
			String strResponse = null;
			
			SSLClient client = getSSLClient();
      //System.out.println("Request [" + strPostData.toString() + "]");
			try {
	      strResponse = client.sendRequest(REQUEST_TYPE_REVERSAL, strPostData.toString());
	    	} catch (Exception e) {
		  		CreditCardAuthorizationException cce = new CreditCardAuthorizationException();
		  		cce.setDisplayError("Cannot connect to Central Payment Server");
		  		cce.setErrorMessage("SendRequest failed");
		  		cce.setErrorNumber("-");
		  		cce.setParentException(e);
		  		throw cce;
	    	}
//      System.out.println("Response [" + strResponse + "]");
      if (strResponse == null) {
		  		CreditCardAuthorizationException cce = new CreditCardAuthorizationException();
		  		cce.setDisplayError("Cannot connect to Central Payment Server");
		  		cce.setErrorMessage("SendRequest returned null");
		  		cce.setErrorNumber("-");
		  		throw cce;
      } else if (!strResponse.startsWith(PROPERTY_ACTION_CODE)) {
		  		CreditCardAuthorizationException cce = new CreditCardAuthorizationException();
		  		cce.setDisplayError("Cannot connect to Central Payment Server");
		  		cce.setErrorMessage("Invalid response from host, should start with d39 ["+strResponse+"]");
		  		cce.setErrorNumber("-");
		  		throw cce;
      	}else {
      		refundProperties = parseResponse(strResponse);
      		if (CODE_AUTHORIZATOIN_APPROVED.equals(refundProperties.get(PROPERTY_ACTION_CODE))) {
      			return refundProperties;
      		} else {
	  		  		CreditCardAuthorizationException cce = new CreditCardAuthorizationException();
	  		  		cce.setDisplayError(refundProperties.get(PROPERTY_ACTION_CODE_TEXT).toString());
	  		  		cce.setErrorMessage(refundProperties.get(PROPERTY_ERROR_TEXT).toString());
	  		  		cce.setErrorNumber(refundProperties.get(PROPERTY_ERROR_CODE).toString());
	  		  		throw cce;
      		}
      	}
      
		} catch (UnsupportedEncodingException e) {
			e.printStackTrace();
		}
		
			
		return refundProperties;
	}
	
	private Hashtable finishTransaction(Hashtable properties) throws KortathjonustanAuthorizationException{
		System.out.println(" ------ CAPTURE ------");
		Hashtable captureProperties = new Hashtable();
			try {
				
				StringBuffer strPostData= new StringBuffer();
				appendProperty(strPostData, PROPERTY_PASSWORD, PASSWORD);
				addProperties(strPostData, properties);
				
				String strResponse = null;
				
				SSLClient client = getSSLClient();
	      //System.out.println("strPostData [ "+strPostData.toString()+" ]");
	      try {
		      	strResponse = client.sendRequest(REQUEST_TYPE_CAPTURE, strPostData.toString());
	      	} catch (Exception e) {
				  		KortathjonustanAuthorizationException cce = new KortathjonustanAuthorizationException();
				  		cce.setDisplayError("Cannot connect to Central Payment Server");
				  		cce.setErrorMessage("SendRequest failed");
				  		cce.setErrorNumber("-");
				  		cce.setParentException(e);
				  		throw cce;
	      }
	      //System.out.println("Response [ "+strResponse+" ]");
	      if (strResponse == null) {
			  		KortathjonustanAuthorizationException cce = new KortathjonustanAuthorizationException();
			  		cce.setDisplayError("Cannot connect to Central Payment Server");
			  		cce.setErrorMessage("SendRequest returned null");
			  		cce.setErrorNumber("-");
		  		throw cce;
	      } else if (!strResponse.startsWith(PROPERTY_ACTION_CODE)) {
			  		KortathjonustanAuthorizationException cce = new KortathjonustanAuthorizationException();
			  		cce.setDisplayError("Cannot connect to Central Payment Server");
			  		cce.setErrorMessage("Invalid response from host, should start with d39 ["+strResponse+"]");
			  		cce.setErrorNumber("-");
		  		throw cce;
	      	}else {
	  	      captureProperties = parseResponse(strResponse);
	  	      captureProperties.put(PROPERTY_CARD_BRAND_NAME, properties.get(PROPERTY_CARD_BRAND_NAME));
	      		if (CODE_AUTHORIZATOIN_APPROVED.equals(captureProperties.get(PROPERTY_ACTION_CODE))) {
	      			return captureProperties;
	      		} else {
		  		  		KortathjonustanAuthorizationException cce = new KortathjonustanAuthorizationException();
		  		  		cce.setDisplayError(captureProperties.get(PROPERTY_ACTION_CODE_TEXT).toString());
		  		  		cce.setErrorMessage(captureProperties.get(PROPERTY_ERROR_TEXT).toString());
		  		  		cce.setErrorNumber(captureProperties.get(PROPERTY_ERROR_CODE).toString());
		  		  		throw cce;
	      		}
	      	}
	
			} catch (UnsupportedEncodingException e) {
				e.printStackTrace();
			}
			
		return captureProperties;
	}
	
	private Hashtable getFirstResponse() throws KortathjonustanAuthorizationException {
		Hashtable properties = null;
		System.out.println(" ------ REQUEST ------");
		
  	
    //long lStartTime = System.currentTimeMillis();
		try {
			SSLClient client = getSSLClient();

			StringBuffer strPostData= new StringBuffer();
    		appendProperty(strPostData, PROPERTY_SITE, SITE);//"site=22"
      appendProperty(strPostData, PROPERTY_USER, USER);
      appendProperty(strPostData, PROPERTY_PASSWORD, PASSWORD);
      appendProperty(strPostData, PROPERTY_ACCEPTOR_TERM_ID, ACCEPTOR_TERM_ID);
      appendProperty(strPostData, PROPERTY_ACCEPTOR_IDENT, ACCEPTOR_IDENTIFICATION);
      appendProperty(strPostData, PROPERTY_CC_NUMBER, strCCNumber);
      appendProperty(strPostData, PROPERTY_CC_EXPIRE, strCCExpire);
      appendProperty(strPostData, PROPERTY_AMOUNT, strAmount);
      appendProperty(strPostData, PROPERTY_CURRENCY_CODE, strCurrencyCode);
      appendProperty(strPostData, PROPERTY_CURRENCY_EXPONENT, strCurrencyExponent);
      appendProperty(strPostData, PROPERTY_CARDHOLDER_NAME, strName);
      appendProperty(strPostData, PROPERTY_REFERENCE_ID, strReferenceNumber);
      appendProperty(strPostData, PROPERTY_CURRENT_DATE, strCurrentDate);
      appendProperty(strPostData, PROPERTY_CC_VERIFY_CODE, strCCVerify);
      addDefautProperties(strPostData);
      
      
      String strResponse = null;

      //System.out.println("Request [" + strPostData.toString() + "]");
      try {
      		strResponse = client.sendRequest(REQUEST_TYPE_AUTHORIZATION, strPostData.toString());
      	} catch (Exception e) {
		  		KortathjonustanAuthorizationException cce = new KortathjonustanAuthorizationException();
		  		cce.setDisplayError("Cannot connect to Central Payment Server");
		  		cce.setErrorMessage("SendRequest failed");
		  		cce.setErrorNumber("-");
		  		cce.setParentException(e);
		  		throw cce;
      }
      //System.out.println("Response [" + strResponse + "]");

      if (strResponse == null) {
		  		KortathjonustanAuthorizationException cce = new KortathjonustanAuthorizationException();
		  		cce.setDisplayError("Cannot connect to Central Payment Server");
		  		cce.setErrorMessage("SendRequest returned null");
		  		cce.setErrorNumber("-");
		  		throw cce;
      } else if (!strResponse.startsWith(PROPERTY_ACTION_CODE)) {
		  		KortathjonustanAuthorizationException cce = new KortathjonustanAuthorizationException();
		  		cce.setDisplayError("Cannot connect to Central Payment Server");
		  		cce.setErrorMessage("Invalid response from host, should start with d39 ["+strResponse+"]");
		  		cce.setErrorNumber("-");
		  		throw cce;
      	}else {
      		properties = parseResponse(strResponse);
      		if (CODE_AUTHORIZATOIN_APPROVED.equals(properties.get(PROPERTY_ACTION_CODE))) {
      			return properties;
      		} else {
	  		  		KortathjonustanAuthorizationException cce = new KortathjonustanAuthorizationException();
	  		  		cce.setDisplayError(properties.get(PROPERTY_ACTION_CODE_TEXT).toString());
	  		  		cce.setErrorMessage(properties.get(PROPERTY_ERROR_TEXT).toString());
	  		  		cce.setErrorNumber(properties.get(PROPERTY_ERROR_CODE).toString());
	  		  		throw cce;
      		}
      	}

    } 
    catch (UnsupportedEncodingException e) 
    {
	  		KortathjonustanAuthorizationException cce = new KortathjonustanAuthorizationException();
	  		cce.setDisplayError("Cannot connect to Central Payment Server");
	  		cce.setErrorMessage("UnsupportedEncodingException");
	  		cce.setErrorNumber("-");
	  		cce.setParentException(e);
	  		throw cce;
    }
    
  }

	/**
	 * @return
	 * @throws IOException
	 */
	private SSLClient getSSLClient() throws KortathjonustanAuthorizationException {

		SSLClient client;
		try {
			client = new SSLClient(HOST_NAME, HOST_PORT, strKeystore, strKeystorePass, USER, PASSWORD);
		} catch (IOException e) {
			KortathjonustanAuthorizationException cce = new KortathjonustanAuthorizationException();
			cce.setDisplayError("Cannot connect to Central Payment Server");
			cce.setErrorMessage("Cannot get SSLClient instance");
			cce.setErrorNumber("-");
			cce.setParentException(e);
			throw cce;
		}
		return client;
	}

	private void addDefautProperties(StringBuffer strPostData) throws UnsupportedEncodingException {
		//appendProperty(strPostData, , );
		//appendProperty(strPostData, PROPERTY_MERCHANT_LANGUAGE, "is");
		//appendProperty(strPostData, PROPERTY_CLIENT_LANGUAGE, "is");
		//appendProperty(strPostData, PROPERTY_CLIENT_IP, "80.62.56.56");
	}

	private Hashtable parseResponse(String response) {
		return parseResponse(response, false);
	}
	
	private Hashtable parseResponse(String response, boolean listOnly) {
		Hashtable responseElements = new Hashtable();
		int index = 0;
		int tmpIndex = 0;
		String tmpString;
		String key, value;
		responseElements.put(PROPERTY_TOTAL_RESPONSE, response);
		while (index >= 0) {
			tmpIndex = response.indexOf("&");
			tmpString = response.substring(0, tmpIndex);
			response = response.substring(tmpIndex+1, response.length());
			index = response.indexOf("&");
			if (tmpString.indexOf("=") > -1) {
				key = tmpString.substring(0, tmpString.indexOf("="));
				value = tmpString.substring(tmpString.indexOf("=")+1, tmpString.length());
				if (listOnly) {
					System.out.println(tmpString+" ("+key+","+value+")");
				} else {
					//System.out.println(tmpString+" ("+key+","+value+")");
					responseElements.put(key, value);
				}
			}
		}
		if (response.indexOf("=") > -1) {
			key = response.substring(0, response.indexOf("="));
			value = response.substring(response.indexOf("=")+1, response.length());
			if (listOnly) {
				System.out.println(response+" ("+key+","+value+")");
			} else {
				responseElements.put(key, value);
			}
		}
		return responseElements;
	}
	
	private void addProperties(StringBuffer buffer, Hashtable properties) throws UnsupportedEncodingException {
		Set keys = properties.keySet();
		Iterator iter = keys.iterator();
		if (iter != null) {
			String key;
			while (iter.hasNext()) {
				key = iter.next().toString();
				appendProperty(buffer, key, properties.get(key).toString());
			}
		}
	}
	
	private void appendProperty(StringBuffer buffer, String propertyName, String propertyValue) throws UnsupportedEncodingException {
		if (propertyValue != null) {
			buffer.append("&").append(propertyName).append("=").append(URLEncoder.encode(propertyValue, "UTF-8"));
		}
	}
	
	private String encodeBase64(String _strData)
  {
    Base64 oB64 = new Base64();
    return oB64.encode(_strData.getBytes());
  }

	public Collection getValidCardTypes() {
		Vector tmp = new Vector();
		tmp.add(CreditCardBusiness.CARD_TYPE_VISA);
		tmp.add(CreditCardBusiness.CARD_TYPE_MASTERCARD);
		tmp.add(CreditCardBusiness.CARD_TYPE_ELECTRON);
		tmp.add(CreditCardBusiness.CARD_TYPE_DINERS);
		tmp.add(CreditCardBusiness.CARD_TYPE_JCB);
		tmp.add(CreditCardBusiness.CARD_TYPE_DANKORT);
		return tmp;
	}
	
}
