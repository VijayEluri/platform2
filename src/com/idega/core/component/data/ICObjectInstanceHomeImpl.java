/*
 * $Id: ICObjectInstanceHomeImpl.java,v 1.2 2004/10/14 18:45:04 aron Exp $
 * Created on 14.10.2004
 *
 * Copyright (C) 2004 Idega Software hf. All Rights Reserved.
 *
 * This software is the proprietary information of Idega hf.
 * Use is subject to license terms.
 */
package com.idega.core.component.data;


import com.idega.data.IDOFactory;
import com.idega.data.IDORemoveRelationshipException;

/**
 * 
 *  Last modified: $Date: 2004/10/14 18:45:04 $ by $Author: aron $
 * 
 * @author <a href="mailto:aron@idega.com">aron</a>
 * @version $Revision: 1.2 $
 */
public class ICObjectInstanceHomeImpl extends IDOFactory implements
        ICObjectInstanceHome {
    protected Class getEntityInterfaceClass() {
        return ICObjectInstance.class;
    }

    public ICObjectInstance create() throws javax.ejb.CreateException {
        return (ICObjectInstance) super.createIDO();
    }
    
    public ICObjectInstance createLegacy(){
        try{
    		return create();
    	}
    	catch(javax.ejb.CreateException ce){
    		throw new RuntimeException("CreateException:"+ce.getMessage());
    	}

    }

    public ICObjectInstance findByPrimaryKey(Object pk)
            throws javax.ejb.FinderException {
        return (ICObjectInstance) super.findByPrimaryKeyIDO(pk);
    }
    
    public ICObjectInstance findByPrimaryKey(int id) throws javax.ejb.FinderException{
        return (ICObjectInstance) super.findByPrimaryKeyIDO(id);
       }


       public ICObjectInstance findByPrimaryKeyLegacy(int id) throws java.sql.SQLException{
      	try{
      		return findByPrimaryKey(id);
      	}
      	catch(javax.ejb.FinderException fe){
      		throw new java.sql.SQLException(fe.getMessage());
      	}

       }

    public void removeRelation(ICObjectInstance instance, Class relatedEntity)
            throws IDORemoveRelationshipException {
        com.idega.data.IDOEntity entity = this.idoCheckOutPooledEntity();
        ((ICObjectInstanceBMPBean) entity).ejbHomeRemoveRelation(instance, relatedEntity);
        this.idoCheckInPooledEntity(entity);
        
    }

}
