/*
 * $Id: MealChoiceMonth.java,v 1.3 2005/10/02 18:41:15 laddi Exp $
 * Created on Oct 2, 2005
 *
 * Copyright (C) 2005 Idega Software hf. All Rights Reserved.
 *
 * This software is the proprietary information of Idega hf.
 * Use is subject to license terms.
 */
package se.idega.idegaweb.commune.school.meal.data;

import com.idega.data.IDOEntity;


/**
 * <p>
 * TODO laddi Describe Type MealChoiceMonth
 * </p>
 *  Last modified: $Date: 2005/10/02 18:41:15 $ by $Author: laddi $
 * 
 * @author <a href="mailto:laddi@idega.com">laddi</a>
 * @version $Revision: 1.3 $
 */
public interface MealChoiceMonth extends IDOEntity {

	/**
	 * @see se.idega.idegaweb.commune.school.meal.data.MealChoiceMonthBMPBean#getChoice
	 */
	public MealChoice getChoice();

	/**
	 * @see se.idega.idegaweb.commune.school.meal.data.MealChoiceMonthBMPBean#getChoicePK
	 */
	public Object getChoicePK();

	/**
	 * @see se.idega.idegaweb.commune.school.meal.data.MealChoiceMonthBMPBean#hasMondays
	 */
	public boolean hasMondays();

	/**
	 * @see se.idega.idegaweb.commune.school.meal.data.MealChoiceMonthBMPBean#hasTuesdays
	 */
	public boolean hasTuesdays();

	/**
	 * @see se.idega.idegaweb.commune.school.meal.data.MealChoiceMonthBMPBean#hasWednesdays
	 */
	public boolean hasWednesdays();

	/**
	 * @see se.idega.idegaweb.commune.school.meal.data.MealChoiceMonthBMPBean#hasThursdays
	 */
	public boolean hasThursdays();

	/**
	 * @see se.idega.idegaweb.commune.school.meal.data.MealChoiceMonthBMPBean#hasFridays
	 */
	public boolean hasFridays();

	/**
	 * @see se.idega.idegaweb.commune.school.meal.data.MealChoiceMonthBMPBean#hasMilk
	 */
	public boolean hasMilk();

	/**
	 * @see se.idega.idegaweb.commune.school.meal.data.MealChoiceMonthBMPBean#hasFruits
	 */
	public boolean hasFruits();

	/**
	 * @see se.idega.idegaweb.commune.school.meal.data.MealChoiceMonthBMPBean#getAmount
	 */
	public float getAmount();

	/**
	 * @see se.idega.idegaweb.commune.school.meal.data.MealChoiceMonthBMPBean#setChoice
	 */
	public void setChoice(MealChoice choice);

	/**
	 * @see se.idega.idegaweb.commune.school.meal.data.MealChoiceMonthBMPBean#setChoice
	 */
	public void setChoice(Object choicePK);

	/**
	 * @see se.idega.idegaweb.commune.school.meal.data.MealChoiceMonthBMPBean#setMonth
	 */
	public void setMonth(int month);

	/**
	 * @see se.idega.idegaweb.commune.school.meal.data.MealChoiceMonthBMPBean#setYear
	 */
	public void setYear(int year);

	/**
	 * @see se.idega.idegaweb.commune.school.meal.data.MealChoiceMonthBMPBean#setMondays
	 */
	public void setMondays(boolean hasDay);

	/**
	 * @see se.idega.idegaweb.commune.school.meal.data.MealChoiceMonthBMPBean#setTuesdays
	 */
	public void setTuesdays(boolean hasDay);

	/**
	 * @see se.idega.idegaweb.commune.school.meal.data.MealChoiceMonthBMPBean#setWednesdays
	 */
	public void setWednesdays(boolean hasDay);

	/**
	 * @see se.idega.idegaweb.commune.school.meal.data.MealChoiceMonthBMPBean#setThursdays
	 */
	public void setThursdays(boolean hasDay);

	/**
	 * @see se.idega.idegaweb.commune.school.meal.data.MealChoiceMonthBMPBean#setFridays
	 */
	public void setFridays(boolean hasDay);

	/**
	 * @see se.idega.idegaweb.commune.school.meal.data.MealChoiceMonthBMPBean#setMilk
	 */
	public void setMilk(boolean hasMilk);

	/**
	 * @see se.idega.idegaweb.commune.school.meal.data.MealChoiceMonthBMPBean#setFruits
	 */
	public void setFruits(boolean hasFruits);

	/**
	 * @see se.idega.idegaweb.commune.school.meal.data.MealChoiceMonthBMPBean#setAmount
	 */
	public void setAmount(float amount);
}
