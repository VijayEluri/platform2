package com.idega.block.creditcard.business;

import java.util.Collection;

/**
 * @author gimmi
 */
public interface CreditCardClient {

//	public void setupClient(String merchant, String location, String user, String password, String terminal, String extraInfo);
	public Collection getValidCardTypes();
	
	public String doRefund(String cardnumber, String monthExpires, String yearExpires, String ccVerifyNumber, double amount, String currency, String extraField) throws CreditCardAuthorizationException;
	public String doSale(String nameOnCard, String cardnumber, String monthExpires, String yearExpires, String ccVerifyNumber, double amount, String currency, String referenceNumber) throws CreditCardAuthorizationException;
	
}
