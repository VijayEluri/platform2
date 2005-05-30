/*
 * $Id: BuilderPage.java,v 1.2 2005/05/30 15:28:54 tryggvil Exp $
 * Created on 16.12.2004
 *
 * Copyright (C) 2004 Idega Software hf. All Rights Reserved.
 *
 * This software is the proprietary information of Idega hf.
 * Use is subject to license terms.
 */
package com.idega.builder.tag;

import java.util.Map;
import com.idega.presentation.BuilderPageFacetMap;
import com.idega.presentation.Page;


/**
 * 
 *  Last modified: $Date: 2005/05/30 15:28:54 $ by $Author: tryggvil $
 * 
 * @author <a href="mailto:tryggvil@idega.com">Tryggvi Larusson</a>
 * @version $Revision: 1.2 $
 */
public class BuilderPage extends Page {

	/**
	 * 
	 */
	public BuilderPage() {
		super();
		//setTransient(true);
	}

	/**
	 * @param s
	 */
	public BuilderPage(String s) {
		super(s);
		// TODO Auto-generated constructor stub
	}
	
	
	public Map getFacets(){
		if(this.facetMap==null){
			facetMap = new BuilderPageFacetMap(this);
		}
		return facetMap;
	}
}
