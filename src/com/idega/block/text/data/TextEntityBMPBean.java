package com.idega.block.text.data;

import com.idega.block.text.data.TxText;

/**
 * Title: Description: Copyright: Copyright (c) 2001 Company: idega.is
 * 
 * @author 2000 - idega team - <br>
 *         <a href="mailto:aron@idega.is">Aron Birkir</a><br>
 * @version 1.0
 */

public abstract class TextEntityBMPBean extends com.idega.data.GenericEntity
		implements com.idega.block.text.data.TextEntity {

	public static final String TEXT_ID = "tx_text_id";

	public static String getColumnTextId() {
		return TEXT_ID;
	}

	protected final void afterInitializeAttributes() {
		addAttribute(getColumnTextId(), "Text", true, true, Integer.class,
				"many-to_one", TxText.class);
	}

	public final int getTextId() {
		return getIntColumnValue(getColumnTextId());
	}

	public TxText getText() {
		return (TxText) getColumnValue(getColumnTextId());
	}
	
	public final void setTextId(int text_id) {
		setColumn(getColumnTextId(), text_id);
	}
}