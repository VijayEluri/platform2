package com.idega.block.trade.stockroom.business;

import java.sql.Timestamp;
import com.idega.block.trade.stockroom.data.*;
import java.sql.SQLException;

/**
 * Title:        IW Trade
 * Description:
 * Copyright:    Copyright (c) 2001
 * Company:      idega.is
 * @author 2000 - idega team - <br><a href="mailto:gummi@idega.is">Gu�mundur �g�st S�mundsson</a><br><a href="mailto:gimmi@idega.is">Gr�mur J�nsson</a>
 * @version 1.0
 */

public class StockroomBusiness implements SupplyManager {

  public StockroomBusiness() {
  }
  public void addSupplies(int product_id, float amount) {
    /**@todo: Implement this com.idega.block.trade.stockroom.business.SupplyManager method*/
    throw new java.lang.UnsupportedOperationException("Method addSupplies() not yet implemented.");
  }
  public void depleteSupplies(int product_id, float amount) {
    /**@todo: Implement this com.idega.block.trade.stockroom.business.SupplyManager method*/
    throw new java.lang.UnsupportedOperationException("Method depleteSupplies() not yet implemented.");
  }
  public void setSupplyStatus(int product_id, float status) {
    /**@todo: Implement this com.idega.block.trade.stockroom.business.SupplyManager method*/
    throw new java.lang.UnsupportedOperationException("Method addSupplies() not yet implemented.");
  }
  public float getSupplyStatus(int product_id)  throws SQLException {
    /**@todo: Implement this com.idega.block.trade.stockroom.business.SupplyManager method*/
    throw new java.lang.UnsupportedOperationException("Method getSupplyStatus() not yet implemented.");
  }
  public float getSupplyStatus(int product_id, Timestamp time) {
    /**@todo: Implement this com.idega.block.trade.stockroom.business.SupplyManager method*/
    throw new java.lang.UnsupportedOperationException("Method getSupplyStatus() not yet implemented.");
  }
  public void setPrice(int product_id, Timestamp time) {
    /**@todo: Implement this com.idega.block.trade.stockroom.business.SupplyManager method*/
    throw new java.lang.UnsupportedOperationException("Method setPrice() not yet implemented.");
  }
  public float getPrice(int product_id, Timestamp time) {
    /**@todo: Implement this com.idega.block.trade.stockroom.business.SupplyManager method*/
    throw new java.lang.UnsupportedOperationException("Method getPrice() not yet implemented.");
  }
  public void createPriceCategory() {
    /**@todo: Implement this com.idega.block.trade.stockroom.business.SupplyManager method*/
    throw new java.lang.UnsupportedOperationException("Method createPriceCategory() not yet implemented.");
  }



  public int createProduct(int supplierId, Integer fileId, String productName, String ProductDescription, boolean isValid) throws Exception{
    Product product = new Product();

    product.setSupplierId(supplierId);
    if(fileId != null){
      product.setFileId(fileId);
    }
    product.setProductName(productName);
    product.setProdcutDescription(ProductDescription);
    product.setIsValid(isValid);

    product.insert();

    return product.getID();

  }




}