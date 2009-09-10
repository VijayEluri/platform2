/*
 * $Id: MINUS.java,v 1.1.2.1 2007/01/12 19:32:33 idegaweb Exp $
 * Created on 5.10.2004
 *
 * Copyright (C) 2004 Idega Software hf. All Rights Reserved.
 *
 * This software is the proprietary information of Idega hf.
 * Use is subject to license terms.
 */
package com.idega.data.query;

import com.idega.data.query.output.Output;
import com.idega.data.query.output.Outputable;
import com.idega.data.query.output.ToStringer;


/**
 * 
 *  Last modified: $Date: 2007/01/12 19:32:33 $ by $Author: idegaweb $
 * 
 * @author <a href="mailto:gummi@idega.com">Gudmundur Agust Saemundsson</a>
 * @version $Revision: 1.1.2.1 $
 */
public class MINUS implements Outputable{

	SelectQuery query1, query2;
	
	/**
	 * 
	 */
	public MINUS(SelectQuery query1, SelectQuery query2) {
		this.query1 = query1;
		this.query2 = query2;
	}

	/* (non-Javadoc)
	 * @see com.idega.data.query.output.Outputable#write(com.idega.data.query.output.Output)
	 */
	public void write(Output out) {
		out.print("(")
		.print(this.query1)
		.print(") MINUS (")
		.print(this.query2)
		.print(")");
	}
	
    public String toString() {
        return ToStringer.toString(this);
    }
}
