package com.idega.block.trade.stockroom.data;

import java.sql.*;
import com.idega.data.*;
import com.idega.core.data.*;
import com.idega.block.trade.data.Currency;

/**
 * Title:        IW Trade
 * Description:
 * Copyright:    Copyright (c) 2001
 * Company:      idega.is
 * @author 2000 - idega team - <br><a href="mailto:gummi@idega.is">Gu�mundur �g�st S�mundsson</a><br><a href="mailto:gimmi@idega.is">Gr�mur J�nsson</a>
 * @version 1.0
 */

public class ProductPrice extends GenericEntity{

  public static final int PRICETYPE_PRICE = 0;
  public static final int PRICETYPE_DISCOUNT = 1;

  public ProductPrice(){
          super();
  }
  public ProductPrice(int id)throws SQLException{
          super(id);
  }

  public void initializeAttributes(){
    addAttribute(getIDColumnName());
    addAttribute(getColumnNameProductId(), "Vara" ,true, true, Integer.class, "many_to_one", Product.class);
    addAttribute(getColumnNamePriceCategoryId(), "Ver�flokkur" ,true, true, Integer.class, "many_to_one", PriceCategory.class);
    addAttribute(getColumnNameCurrencyId(),"Gjaldmi�ill",true,true,Integer.class,"many_to_one", Currency.class);
    addAttribute(getColumnNamePrice(), "Ver�", true, true, Float.class);
    addAttribute(getColumnNamePriceDate(), "Dagsetning ver�s", true, true, Timestamp.class);
    addAttribute(getColumnNamePriceType(),"Ger�",true,true,Integer.class);
    addAttribute(getColumnNameIsValid(), "virkt", true, true, Boolean.class);

    this.addManyToManyRelationShip(Timeframe.class,getProductPriceTableName()+"_TIMEFRAME");
    this.addManyToManyRelationShip(Address.class,getProductPriceTableName()+"_ADDRESS");
    this.addManyToManyRelationShip(TravelAddress.class);
  }


  public void delete() {
    this.invalidate();
  }

  public void invalidate() {
    this.setIsValid(false);
  }

  public void validate() {
    this.setIsValid(true);
  }

  public String getEntityName(){
    return getProductPriceTableName();
  }

  public void setDefaultValues() {
    this.setIsValid(true);
  }

  public int getProductId() {
    return getIntColumnValue(getColumnNameProductId());
  }

  public void setProductId(int id){
    setColumn(getColumnNameProductId(),id);
  }


  public PriceCategory getPriceCategory() {
    return (PriceCategory) getColumnValue(getColumnNamePriceCategoryId());
  }

  public int getPriceCategoryID() {
    return getIntColumnValue(getColumnNamePriceCategoryId());
  }

  public Integer getPriceCategoryIDInteger() {
    return getIntegerColumnValue(getColumnNamePriceCategoryId());
  }

  public void setPriceCategoryID(int id) {
    setColumn(getColumnNamePriceCategoryId(), id);
  }

  public int getCurrencyId(){
    return getIntColumnValue(getColumnNameCurrencyId());
  }

  public void setCurrencyId(int id){
    setColumn(getColumnNameCurrencyId(), id);
  }

  public void setCurrencyId(Integer id){
    setColumn(getColumnNameCurrencyId(), id);
  }

  public float getPrice() {
    return getFloatColumnValue(getColumnNamePrice());
  }

  public int getDiscount() {
    int returner = 0;
    if (this.getPriceType() == PRICETYPE_DISCOUNT) {
      returner = (int) getFloatColumnValue(getColumnNamePrice());
    }
    return returner;
  }

  public void setPrice(float price) {
    setColumn(getColumnNamePrice(), price);
  }

  public Timestamp getPriceDate() {
    return (Timestamp) getColumnValue(getColumnNamePriceDate());
  }

  public void setPriceDate(Timestamp timestamp) {
    setColumn(getColumnNamePriceDate(), timestamp);
  }

  public int getPriceType(){
    return getIntColumnValue(getColumnNamePriceType());
  }

  public void setPriceType(int type){
    setColumn(getColumnNamePriceType(), type);
  }

  public void setIsValid(boolean isValid) {
    setColumn(getColumnNameIsValid(), isValid);
  }

  public boolean getIsValid() {
    return getBooleanColumnValue(getColumnNameIsValid());
  }

  public static void clearPrices(int productId) throws SQLException {
    ProductPrice[] prices = getProductPrices(productId, -1, false);
    for (int i = 0; i < prices.length; i++) {
      prices[i].invalidate();
      prices[i].update();
    }
  }

  public static ProductPrice[] getProductPrices(int productId, boolean netBookingOnly) {
    return getProductPrices(productId, -1, netBookingOnly);
  }
  /**
   * @deprecated
   */
  public static ProductPrice[] getProductPrices(int productId, int timeframeId, boolean netBookingOnly) {
    return getProductPrices(productId, timeframeId, -1, netBookingOnly);
  }
  public static ProductPrice[] getProductPrices(int productId, int timeframeId, int addressId, boolean netBookingOnly) {
      ProductPrice[] prices = {};
      try {
        ProductPrice price = (ProductPrice) ProductPrice.getStaticInstance(ProductPrice.class);
        PriceCategory category = (PriceCategory) PriceCategory.getStaticInstance(PriceCategory.class);
        Timeframe timeframe = (Timeframe) Timeframe.getStaticInstance(Timeframe.class);
        TravelAddress tAddress = (TravelAddress) TravelAddress.getStaticInstance(TravelAddress.class);
        Product product = (Product) Product.getStaticInstance(Product.class);

        String ptmTable = EntityControl.getManyToManyRelationShipTableName(ProductPrice.class, Timeframe.class);
        String pamTable = EntityControl.getManyToManyRelationShipTableName(ProductPrice.class, TravelAddress.class);
        String pTable = price.getProductPriceTableName();
        String cTable = category.getEntityName();

        StringBuffer SQLQuery = new StringBuffer();
          SQLQuery.append("SELECT "+pTable+".* FROM "+pTable+", "+cTable);
          if (timeframeId != -1) {
            SQLQuery.append(" , "+ptmTable);
          }
          if (addressId != -1) {
            SQLQuery.append(" , "+pamTable);
          }
          SQLQuery.append(" WHERE ");
          if (timeframeId != -1) {
            SQLQuery.append(ptmTable+"."+timeframe.getIDColumnName()+" = "+timeframeId);
            SQLQuery.append(" AND ");
            SQLQuery.append(ptmTable+"."+price.getIDColumnName()+" = "+pTable+"."+price.getIDColumnName());
            SQLQuery.append(" AND ");
          }
          if (addressId != -1) {
            SQLQuery.append(pamTable+"."+tAddress.getIDColumnName()+" = "+addressId);
            SQLQuery.append(" AND ");
            SQLQuery.append(pamTable+"."+price.getIDColumnName()+" = "+pTable+"."+price.getIDColumnName());
            SQLQuery.append(" AND ");
          }
          SQLQuery.append(pTable+"."+ProductPrice.getColumnNamePriceCategoryId() + " = "+cTable+"."+category.getIDColumnName());
          SQLQuery.append(" AND ");
          SQLQuery.append(pTable+"."+ProductPrice.getColumnNameProductId() +" = " + productId);
          SQLQuery.append(" AND ");
          SQLQuery.append(pTable+"."+ProductPrice.getColumnNameIsValid() +"='Y'");
          if (netBookingOnly) {
            SQLQuery.append(" AND ");
            SQLQuery.append(cTable+"."+PriceCategory.getColumnNameNetbookingCategory()+" = 'Y'");
          }
          SQLQuery.append(" ORDER BY "+pTable+"."+price.getColumnNamePriceType()+","+cTable+"."+category.getColumnNameName());

        prices = (ProductPrice[]) (ProductPrice.getStaticInstance(ProductPrice.class)).findAll(SQLQuery.toString());
      }catch (SQLException sql) {
        sql.printStackTrace(System.err);
      }
      return prices;
  }


  public static String getProductPriceTableName(){return "SR_PRODUCT_PRICE";}
  public static String getColumnNameProductId(){return "SR_PRODUCT_ID";}
  public static String getColumnNamePriceCategoryId() {return "SR_PRICE_CATEGORY_ID";}
  public static String getColumnNamePrice() {return "PRICE";}
  public static String getColumnNamePriceDate() {return "PRICE_DATE"; }
  public static String getColumnNameCurrencyId() {return "TR_CURRENCY_ID"; }
  public static String getColumnNamePriceType() {return "PRICE_TYPE"; }
  public static String getColumnNameIsValid() {return "IS_VALID";}






}
