/*
 * $Id: ApartmentTypeComplexHelper.java,v 1.5.6.2 2007/01/12 19:31:17 idegaweb Exp $
 *
 * Copyright (C) 2001 Idega hf. All Rights Reserved.
 *
 * This software is the proprietary information of Idega hf.
 * Use is subject to license terms.
 *
 */
package com.idega.block.building.business;

import java.util.StringTokenizer;

import com.idega.block.building.data.ApartmentType;
import com.idega.block.building.data.Complex;

/**
 * 
 * @author <a href="mailto:palli@idega.is">Pall Helgason</a>
 * @version 1.0
 */
public class ApartmentTypeComplexHelper {
	private int iKey_1, iKey_2;

	private String key = null;

	private String name = null;

	private Complex complex = null;

	private ApartmentType type = null;

	public ApartmentTypeComplexHelper() {
	}

	public ApartmentTypeComplexHelper(int key1, int key2) {
		setKey(key1, key2);
	}

	public void setKey(int key1, int key2) {
		this.iKey_1 = key1;
		this.iKey_2 = key2;
		this.key = Integer.toString(key1) + "-" + Integer.toString(key2);
	}

	public int getKeyOne() {
		return this.iKey_1;
	}

	public int getKeyTwo() {
		return this.iKey_2;
	}

	public void setKey(String key) {
		this.key = key;
	}

	public String getKey() {
		return this.key;
	}

	public void setName(String name) {
		this.name = name;
	}

	public String getName() {
		return this.name;
	}
	
	public void setComplex(Complex complex) {
		this.complex = complex;
	}
	
	public Complex getComplex() {
		return this.complex;
	}
	
	public void setApartmentType(ApartmentType type) {
		this.type = type;;
	}

	public ApartmentType getApartmentType() {
		return this.type;
	}
	public static int getPartKey(String key, int index) {
		StringTokenizer t = new StringTokenizer(key, "-");
		int not = t.countTokens();
		if (index > not) {
			return -1;
		}

		int i = 0;
		while (t.hasMoreElements()) {
			i++;
			String txt = (String) t.nextElement();
			if (index == i) {
				int ret = -1;
				try {
					ret = Integer.parseInt(txt);
				} catch (java.lang.NumberFormatException e) {
				}

				return ret;
			}
		}

		return -1;
	}
}