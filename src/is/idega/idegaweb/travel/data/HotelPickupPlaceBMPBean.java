package is.idega.idegaweb.travel.data;

import javax.ejb.FinderException;
import java.rmi.RemoteException;
import java.util.Collection;
import java.sql.*;
import com.idega.data.*;
import com.idega.core.data.*;
import com.idega.block.trade.stockroom.data.Supplier;

/**
 * Title:        IW Travel
 * Description:
 * Copyright:    Copyright (c) 2001
 * Company:      idega.is
 * @author 2000 - idega team - <br><a href="mailto:gummi@idega.is">Gu�mundur �g�st S�mundsson</a><br><a href="mailto:gimmi@idega.is">Gr�mur J�nsson</a>
 * @version 1.0
 */

public class HotelPickupPlaceBMPBean extends com.idega.data.GenericEntity implements is.idega.idegaweb.travel.data.HotelPickupPlace {

  public HotelPickupPlaceBMPBean(){
          super();
  }
  public HotelPickupPlaceBMPBean(int id)throws SQLException{
          super(id);
  }
  public void initializeAttributes(){
    addAttribute(getIDColumnName());
    addAttribute(getNameColumnName(), "Name", true, true, String.class);
    addAttribute(getAddressIDColumnName(), "Heimilisfang", true, true, Integer.class ,"many_to_one",Address.class);
    addAttribute(getDeletedColumnName(), "Hent", true, true, Boolean.class);

    this.addManyToManyRelationShip(Supplier.class,"TB_HOTEL_PICKUP_PL_SR_SUPPLIER");
  }

  public void insertStartData()throws Exception{
  }

  public static String getHotelPickupPlaceTableName(){return "TB_HOTEL_PICKUP_PLACE";}
  public static String getNameColumnName() {return "NAME";}
  public static String getAddressIDColumnName() {return "IC_ADDRESS_ID";}
  public static String getDeletedColumnName() {return "DELETED";}

  public void setDefaultValues() {
    setColumn(getDeletedColumnName(),false);
  }

  public String getEntityName(){
    return getHotelPickupPlaceTableName();
  }
  public String getName(){
    return getStringColumnValue(getNameColumnName());
  }

  public void delete() {
    try {
      setColumn(getDeletedColumnName(),true);
      this.update();
    }catch (SQLException sql) {
      sql.printStackTrace(System.err);
    }
  }

  public void setName(String name){
    setColumn(getNameColumnName(),name);
  }

  public Address getAddress() {
      return (Address) getColumnValue(getAddressIDColumnName());
  }

  public void setAddress(Address address) {
      setColumn(getAddressIDColumnName(), address.getID());
  }

  public void setAddressId(int addressId) {
      setColumn(getAddressIDColumnName(), addressId);
  }


  public Collection ejbFindHotelPickupPlaces(Service service)throws RemoteException, FinderException {
    Collection returner = null;
//        HotelPickupPlace hp = (HotelPickupPlace) is.idega.idegaweb.travel.data.HotelPickupPlaceBMPBean.getStaticInstance(HotelPickupPlace.class);

    StringBuffer buffer = new StringBuffer();
      buffer.append("select h.* from ");
      buffer.append(is.idega.idegaweb.travel.data.ServiceBMPBean.getServiceTableName()+" s,");
      buffer.append(com.idega.data.EntityControl.getManyToManyRelationShipTableName(Service.class,HotelPickupPlace.class)+" smh, ");
      buffer.append(getHotelPickupPlaceTableName() +" h ");
      buffer.append(" WHERE ");
      buffer.append("s."+is.idega.idegaweb.travel.data.ServiceBMPBean.getServiceIDColumnName()+" = "+((Integer) service.getPrimaryKey()).intValue());
      buffer.append(" AND ");
      buffer.append("s."+is.idega.idegaweb.travel.data.ServiceBMPBean.getServiceIDColumnName()+" = smh."+is.idega.idegaweb.travel.data.ServiceBMPBean.getServiceIDColumnName());
      buffer.append(" AND ");
      buffer.append(" smh."+getIDColumnName()+" = h."+getIDColumnName());
      buffer.append(" AND ");
      buffer.append(getDeletedColumnName() +" = 'N'");
      buffer.append(" ORDER BY "+getNameColumnName());

    returner = this.idoFindPKsBySQL(buffer.toString());
//        returner = (HotelPickupPlace[]) hp.findAll(buffer.toString());
    return returner;
  }

  public Collection ejbFindHotelPickupPlaces(Supplier supplier) throws FinderException{
    Collection returner = null;

    StringBuffer buffer = new StringBuffer();
      buffer.append("select h.* from ");
      buffer.append(com.idega.block.trade.stockroom.data.SupplierBMPBean.getSupplierTableName()+" s,");
      buffer.append(com.idega.data.EntityControl.getManyToManyRelationShipTableName(Supplier.class,HotelPickupPlace.class)+" smh, ");
      buffer.append(is.idega.idegaweb.travel.data.HotelPickupPlaceBMPBean.getHotelPickupPlaceTableName() +" h ");
      buffer.append(" WHERE ");
      buffer.append("s."+supplier.getIDColumnName()+" = "+supplier.getID());
      buffer.append(" AND ");
      buffer.append("s."+supplier.getIDColumnName()+" = smh."+supplier.getIDColumnName());
      buffer.append(" AND ");
      buffer.append(" smh."+getIDColumnName()+" = h."+getIDColumnName());
      buffer.append(" AND ");
      buffer.append("h."+is.idega.idegaweb.travel.data.HotelPickupPlaceBMPBean.getDeletedColumnName() +" = 'N'");
      buffer.append(" ORDER BY h."+is.idega.idegaweb.travel.data.HotelPickupPlaceBMPBean.getNameColumnName());


      returner = this.idoFindPKsBySQL(buffer.toString());
//                  returner = (HotelPickupPlace[]) hp.findAll(buffer.toString());
    return returner;
  }

  public void addToSupplier(Supplier supplier) throws IDOAddRelationshipException {
    this.idoAddTo(supplier);
  }
  public void removeFromSupplier(Supplier supplier) throws IDORemoveRelationshipException{
    System.err.println("Trying to remove ID "+this.getID()+" from "+supplier.getName());
    System.err.println("Trying to remove PK "+this.getPrimaryKey()+" from "+supplier.getName());
    super.idoRemoveFrom(supplier);
  }

  public void addToService(Service service) throws IDOAddRelationshipException {
    this.idoAddTo(service);
  }

  public void removeFromService(Service service) throws IDORemoveRelationshipException{
    this.idoRemoveFrom(service);
  }

  public void ejbHomeRemoveFromAllServices() throws IDORemoveRelationshipException{
    this.idoRemoveFrom(HotelPickupPlace.class);
  }



}

