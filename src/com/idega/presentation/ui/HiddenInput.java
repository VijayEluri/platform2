/*
 * $Id: HiddenInput.java,v 1.5 2005/03/08 20:42:17 tryggvil Exp $
 * Created in 2000 by Tryggvi Larusson
 *
 * Copyright (C) 2000-2005 Idega Software hf. All Rights Reserved.
 *
 * This software is the proprietary information of Idega hf.
 * Use is subject to license terms.
 */
package com.idega.presentation.ui;

/**
 * <p>
 * This class is used to render out an (invisible hidden input) to add a parameter to a Form.<br>
 * This class is mainly preserved for backwards compatability and the newer recommended objects are Parameter 
 * or in JSF javax.faces.component.UIParameter.
 * </p>
 *  Last modified: $Date: 2005/03/08 20:42:17 $ by $Author: tryggvil $
 * 
 * @author <a href="mailto:tryggvil@idega.com">Tryggvi Larusson</a>
 * @version $Revision: 1.5 $
 * @see Parameter
 */
public class HiddenInput extends Parameter {

	/**
	 * Constructs a new <code>HiddenInput</code> with the name "untitled".
	 */
	public HiddenInput() {
		this("untitled");
	}

	/**
	 * Constructs a new <code>HiddenInput</code> with the given name and the value "unspecified".
	 */
	public HiddenInput(String name) {
		this(name, "unspecified");
	}

	/**
	 * Constructs a new <code>HiddenInput</code> with the given name and sets the given
	 * value.
	 */
	public HiddenInput(String name, String value) {
		super(name, value);
	}
}
