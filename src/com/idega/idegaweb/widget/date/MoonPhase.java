/*
 * $Id: MoonPhase.java,v 1.1 2004/10/26 09:05:20 laddi Exp $
 * Created on 14.10.2004
 *
 * Copyright (C) 2004 Idega Software hf. All Rights Reserved.
 *
 * This software is the proprietary information of Idega hf.
 * Use is subject to license terms.
 */
package com.idega.idegaweb.widget.date;

import java.math.BigDecimal;

import com.idega.idegaweb.widget.Widget;
import com.idega.presentation.IWContext;
import com.idega.presentation.Image;
import com.idega.presentation.PresentationObject;
import com.idega.util.IWCalendar;


/**
 * Last modified: 14.10.2004 14:14:38 by laddi
 * 
 * @author <a href="mailto:laddi@idega.com">laddi</a>
 * @version $Revision: 1.1 $
 */
public class MoonPhase extends Widget {
	
	private int width = -1;
	private int height = -1;

	/* (non-Javadoc)
	 * @see com.idega.idegaweb.widget.Widget#getWidget(com.idega.presentation.IWContext)
	 */
	protected PresentationObject getWidget(IWContext iwc) {
		IWCalendar calendar = new IWCalendar(getLocale());
		double moonPhase = calendar.getMoonPhase();
		
		BigDecimal bd = new BigDecimal(moonPhase);
    bd = bd.setScale(2, BigDecimal.ROUND_HALF_DOWN);
    moonPhase = bd.doubleValue();
		
		Image image = null;
		if (moonPhase == IWCalendar.NEW_MOON || moonPhase == IWCalendar.NEW_MOON + 1) {
			image = getBundle().getImage("/moonphase/new_moon.jpg", getResourceBundle().getLocalizedString("moon_phase.new_moon", "New moon"));
		}
		else if (moonPhase == IWCalendar.FIRST_QUARTER) {
			image = getBundle().getImage("/moonphase/waxing_moon.jpg", getResourceBundle().getLocalizedString("moon_phase.waxing_moon", "Waxing moon"));
		}
		else if (moonPhase == IWCalendar.FULL_MOON) {
			image = getBundle().getImage("/moonphase/full_moon.jpg", getResourceBundle().getLocalizedString("moon_phase.full_moon", "Full moon"));
		}
		else if (moonPhase == IWCalendar.LAST_QUARTER) {
			image = getBundle().getImage("/moonphase/waning_moon.jpg", getResourceBundle().getLocalizedString("moon_phase.waning_moon", "Waning moon"));
		}
		
		if (image != null) {
			if (width > 0) {
				image.setWidth(width);
			}
			if (height > 0) {
				image.setHeight(height);
			}
			return image;
		}

		return null;
	}

	/**
	 * @param height The height to set.
	 */
	public void setHeight(int height) {
		this.height = height;
	}
	/**
	 * @param width The width to set.
	 */
	public void setWidth(int width) {
		this.width = width;
	}
}