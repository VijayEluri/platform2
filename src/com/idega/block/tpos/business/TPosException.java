/*
 * $Id: TPosException.java,v 1.4 2003/02/09 13:22:49 gimmi Exp $
 *
 * Copyright (C) 2002 Idega hf. All Rights Reserved.
 *
 * This software is the proprietary information of Idega hf.
 * Use is subject to license terms.
 *
 */
package com.idega.block.tpos.business;

import com.idega.idegaweb.IWResourceBundle;

/**
 * @author <a href="mail:palli@idega.is">Pall Helgason</a>
 * @version 1.0
 */
public class TPosException extends Exception {
  protected String _errorMessage = null;
  protected String _errorNumber = null;
  protected String _displayError = null;

  /**
   *
   */
  public TPosException() {
    super();
  }

  /**
   *
   */
  public TPosException(String message) {
    super(message);
  }

  /**
   *
   */
  public void setErrorMessage(String message) {
    _errorMessage = message;
  }

  /**
   *
   */
  public String getErrorMessage() {
    return(_errorMessage);
  }

  /**
   *
   */
  public void setErrorNumber(String number) {
    _errorNumber = number;
  }

  /**
   *
   */
  public String getErrorNumber() {
    return(_errorNumber);
  }

  /**
   *
   */
  public void setDisplayError(String message) {
    _displayError = message;
  }

  /**
   *
   */
  public String getDisplayError() {
    return(_displayError);
  }
  
  public String getLocalizedMessage(IWResourceBundle iwrb) {
		System.out.println("TPOS errormessage = " + this.getErrorMessage());
		System.out.println("number = " + this.getErrorNumber());
		System.out.println("display = " + this.getDisplayError());
		int number = Integer.parseInt(this.getErrorNumber());
		switch (number) {
			case 6:
			case 12:
			case 19:
				return (iwrb.getLocalizedString("travel.creditcard_number_incorrect","Creditcard number incorrect"));
			case 10:
			case 22:
			case 74:
				return (iwrb.getLocalizedString("travel.creditcard_type_not_accepted","Creditcard type not accepted"));
			case 17:
			case 18:
				return (iwrb.getLocalizedString("travel.creditcard_is_expired","Creditcard is expired"));
			case 48:
			case 49:
			case 50:
			case 51:
			case 56:
			case 57:
			case 76:
			case 79:
			case 2002:
			case 2010:
				return (iwrb.getLocalizedString("travel.cannot_connect_to_cps","Could not connect to Central Payment Server"));
			case 7:
			case 37:
			case 69:
			case 75:
				return (iwrb.getLocalizedString("travel.creditcard_autorization_failed","Authorization failed"));
			/*case 69:
				display.setText(e.getErrorMessage());
				break;*/
			case 20:
			case 31:
				return (iwrb.getLocalizedString("travel.transaction_not_permitted","Transaction not permitted"));
			case 99999:
				return (iwrb.getLocalizedString("travel.booking_was_not_confirmed_try_again_later","Booking was not confirmed. Please try again later"));
			default:
				return (iwrb.getLocalizedString("travel.cannot_connect","Cannot communicate with server"));
		}


  }
}
