/*
 * $Id: LeftJoin.java,v 1.1.2.1 2007/01/12 19:32:33 idegaweb Exp $
 * Created on 5.10.2004
 *
 * Copyright (C) 2004 Idega Software hf. All Rights Reserved.
 *
 * This software is the proprietary information of Idega hf.
 * Use is subject to license terms.
 */
package com.idega.data.query;

import java.util.HashSet;
import java.util.Set;
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
public class LeftJoin implements Outputable {

    private Column left, right;
    

    public LeftJoin(Column left, Column right) {
        this.left = left;
        this.right = right;
    }


    public Column getLeftColumn() {
        return this.left;
    }

    public Column getRightColumn() {
        return this.right;
    }

    public void write(Output out) {
		out.print(this.left.getTable())
		.print(" left join ")
		.print(this.right.getTable())
		.print(" on ")
		.print(this.left)
	    	.print(" = ")
		.print(this.right);
        
    }
    
    public Set getTables(){
		Set s = new HashSet();
		s.add(this.left.getTable());
		s.add(this.right.getTable());
		return s; 
    }
    
    public String toString() {
        return ToStringer.toString(this);
    }
    
}
