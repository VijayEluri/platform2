/*
 * Created on Jun 21, 2003
 *
 * To change this generated comment go to 
 * Window>Preferences>Java>Code Generation>Code Template
 */
package com.idega.developer.presentation.comp;

import com.idega.core.component.data.BundleComponent;
import com.idega.core.component.data.ICObjectBMPBean;

/**
 * <p>Title: idegaWeb</p>
 * <p>Description: </p>
 * <p>Copyright: Copyright (c) 2003</p>
 * <p>Company: idega Software</p>
 * @author aron 
 * @version 1.0
 */
public class IWBlockComponent extends IWPresentationComponent implements BundleComponent {
	
	/* (non-Javadoc)
	 * @see com.idega.development.presentation.comp.BundleComponent#type()
	 */
	public String type() {
		// TODO Auto-generated method stub
		return ICObjectBMPBean.COMPONENT_TYPE_BLOCK;
	}

}
