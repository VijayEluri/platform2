package com.idega.util;

import java.sql.Date;
import java.util.Locale;
/**
 * Title:        A class for formatting presentation of PersonalIDs
 * Description:
 * Copyright:    Copyright (c) 2002
 * Company:      idega
 * @author <a href="tryggvi@idega.is">Tryggvi Larusson</a>
 * @version 1.0
 */

public class PersonalIDFormatter {

	private static String DASH = "-";

	private PersonalIDFormatter() {
	}

	/**
	 * Accepts input in which is a valid personalID for a specific locale.
	 * @return A formatted version of the personalID if the personalID is valid for the specified locale and the PersonalIDFormatter can handle the locale.
	 **/
	public static String format(
		String originalString,
		Locale locale) {
		if (originalString != null) {
			int length = originalString.length();
			if (locale.equals(LocaleUtil.getSwedishLocale())) {
				if (length == 12) {
					return originalString.substring(2, 8)
						+ DASH
						+ originalString.substring(8, 12);
				} else if (length == 10) {
					return originalString.substring(0, 6)
						+ DASH
						+ originalString.substring(6, 10);
				}
			} else if (locale.equals(LocaleUtil.getIcelandicLocale())) {
				if (length == 10) {
					return originalString.substring(0, 6)
						+ DASH
						+ originalString.substring(6, 10);
				}
			}

		}
		return originalString;
	}
	
	public static void main(String[] args){
			test(args);
	}


	public static void test(String[] args){
		String personalID= null;
		String localeString=null;
		try{
			personalID=args[0];
			localeString=args[1];
		}
		catch(RuntimeException rme){}
			
		
		if(personalID==null){
			personalID="197811103433";	
		}
		if(localeString==null){
			localeString="sv_SE";	
		}
		System.out.println("Output: "+format(personalID,LocaleUtil.getLocale(localeString)));
	}
}
