package com.idega.block.trade.stockroom.data;


public class TravelAddressHomeImpl extends com.idega.data.IDOFactory implements TravelAddressHome
{
 protected Class getEntityInterfaceClass(){
  return TravelAddress.class;
 }

 public TravelAddress create() throws javax.ejb.CreateException{
  return (TravelAddress) super.idoCreate();
 }

 public TravelAddress createLegacy(){
	try{
		return create();
	}
	catch(javax.ejb.CreateException ce){
		throw new RuntimeException("CreateException:"+ce.getMessage());
	}

 }

 public TravelAddress findByPrimaryKey(int id) throws javax.ejb.FinderException{
  return (TravelAddress) super.idoFindByPrimaryKey(id);
 }

 public TravelAddress findByPrimaryKey(Object pk) throws javax.ejb.FinderException{
  return (TravelAddress) super.idoFindByPrimaryKey(pk);
 }

 public TravelAddress findByPrimaryKeyLegacy(int id) throws java.sql.SQLException{
	try{
		return findByPrimaryKey(id);
	}
	catch(javax.ejb.FinderException fe){
		throw new java.sql.SQLException(fe.getMessage());
	}

 }


}