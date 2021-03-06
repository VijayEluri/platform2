/*
 * $Id: StockroomBusiness.java,v 1.46 2005/10/10 10:51:24 gimmi Exp $
 * Created on 12.5.2005
 *
 * Copyright (C) 2005 Idega Software hf. All Rights Reserved.
 *
 * This software is the proprietary information of Idega hf.
 * Use is subject to license terms.
 */
package com.idega.block.trade.stockroom.business;

import java.rmi.RemoteException;
import java.sql.Date;
import java.sql.SQLException;
import java.sql.Timestamp;
import javax.ejb.CreateException;
import javax.ejb.FinderException;
import com.idega.block.trade.stockroom.data.Product;
import com.idega.block.trade.stockroom.data.ProductPrice;
import com.idega.business.IBOService;
import com.idega.data.IDOAddRelationshipException;
import com.idega.data.IDOLookupException;
import com.idega.presentation.IWContext;
import com.idega.presentation.ui.DropdownMenu;
import com.idega.user.data.User;
import com.idega.util.IWTimestamp;


/**
 * 
 *  Last modified: $Date: 2005/10/10 10:51:24 $ by $Author: gimmi $
 * 
 * @author <a href="mailto:gimmi@idega.com">gimmi</a>
 * @version $Revision: 1.46 $
 */
public interface StockroomBusiness extends IBOService {

	/**
	 * @see com.idega.block.trade.stockroom.business.StockroomBusinessBean#addSupplies
	 */
	public void addSupplies(int product_id, float amount) throws java.rmi.RemoteException;

	/**
	 * @see com.idega.block.trade.stockroom.business.StockroomBusinessBean#depleteSupplies
	 */
	public void depleteSupplies(int product_id, float amount) throws java.rmi.RemoteException;

	/**
	 * @see com.idega.block.trade.stockroom.business.StockroomBusinessBean#setSupplyStatus
	 */
	public void setSupplyStatus(int product_id, float status) throws java.rmi.RemoteException;

	/**
	 * @see com.idega.block.trade.stockroom.business.StockroomBusinessBean#getSupplyStatus
	 */
	public float getSupplyStatus(int product_id) throws SQLException, java.rmi.RemoteException;

	/**
	 * @see com.idega.block.trade.stockroom.business.StockroomBusinessBean#getSupplyStatus
	 */
	public float getSupplyStatus(int product_id, Timestamp time) throws java.rmi.RemoteException;

	/**
	 * @see com.idega.block.trade.stockroom.business.StockroomBusinessBean#setPrice
	 */
	public ProductPrice setPrice(int productPriceIdToReplace, int productId, int priceCategoryId, int currencyId,
			Timestamp time, float price, int priceType, int timeframeId, int addressId) throws IDOLookupException,
			FinderException, IDOAddRelationshipException, CreateException, java.rmi.RemoteException;

	/**
	 * @see com.idega.block.trade.stockroom.business.StockroomBusinessBean#setPrice
	 */
	public ProductPrice setPrice(int productPriceIdToReplace, int productId, int priceCategoryId, int currencyId,
			Timestamp time, float price, int priceType, int timeframeId, int addressId, int maxUsage)
			throws IDOLookupException, FinderException, IDOAddRelationshipException, CreateException,
			java.rmi.RemoteException;

	/**
	 * @see com.idega.block.trade.stockroom.business.StockroomBusinessBean#setPrice
	 */
	public ProductPrice setPrice(int productId, int priceCategoryId, int currencyId, Timestamp time, float price,
			int priceType, int timeframeId, int addressId) throws IDOAddRelationshipException, IDOLookupException,
			CreateException, java.rmi.RemoteException;

	/**
	 * @see com.idega.block.trade.stockroom.business.StockroomBusinessBean#setPrice
	 */
	public ProductPrice setPrice(int productId, int priceCategoryId, int currencyId, Timestamp time, float price,
			int priceType, int timeframeId, int addressId, int maxUsage, Date exactDate)
			throws IDOAddRelationshipException, IDOLookupException, CreateException, java.rmi.RemoteException;

	/**
	 * @see com.idega.block.trade.stockroom.business.StockroomBusinessBean#getPrice
	 */
	public float getPrice(int productPriceId, int productId, int priceCategoryId, int currencyId, Timestamp time)
			throws SQLException, RemoteException;

	/**
	 * @see com.idega.block.trade.stockroom.business.StockroomBusinessBean#getPrice
	 */
	public ProductPrice getPrice(Product product) throws RemoteException;

	/**
	 * @see com.idega.block.trade.stockroom.business.StockroomBusinessBean#getPrice
	 */
	public float getPrice(int productPriceId, int productId, int priceCategoryId, Timestamp time, int timeframeId,
			int addressId) throws SQLException, RemoteException;

	/**
	 * @see com.idega.block.trade.stockroom.business.StockroomBusinessBean#getPrice
	 */
	public float getPrice(int productPriceId, int productId, int priceCategoryId, int currencyId, Timestamp time,
			int timeframeId, int addressId) throws SQLException, RemoteException;

	/**
	 * @see com.idega.block.trade.stockroom.business.StockroomBusinessBean#getDiscount
	 */
	public float getDiscount(int productId, int priceCategoryId, Timestamp time) throws RemoteException, SQLException,
			FinderException;

	/**
	 * @see com.idega.block.trade.stockroom.business.StockroomBusinessBean#createPriceCategory
	 */
	public int createPriceCategory(int supplierId, String name, String description, String extraInfo)
			throws SQLException, java.rmi.RemoteException;

	/**
	 * @see com.idega.block.trade.stockroom.business.StockroomBusinessBean#createPriceCategory
	 */
	public int createPriceCategory(int supplierId, String name, String description, String extraInfo, String key)
			throws SQLException, java.rmi.RemoteException;

	/**
	 * @see com.idega.block.trade.stockroom.business.StockroomBusinessBean#createPriceDiscountCategory
	 */
	public void createPriceDiscountCategory(int parentId, int supplierId, String name, String description,
			String extraInfo) throws SQLException, java.rmi.RemoteException;

	/**
	 * @see com.idega.block.trade.stockroom.business.StockroomBusinessBean#getUserSupplierId
	 */
	public int getUserSupplierId(User user) throws RuntimeException, SQLException, java.rmi.RemoteException;

	/**
	 * @see com.idega.block.trade.stockroom.business.StockroomBusinessBean#getUserSupplierId
	 */
	public int getUserSupplierId(IWContext iwc) throws RuntimeException, SQLException, java.rmi.RemoteException;

	/**
	 * @see com.idega.block.trade.stockroom.business.StockroomBusinessBean#getUserResellerId
	 */
	public int getUserResellerId(IWContext iwc) throws RuntimeException, SQLException, java.rmi.RemoteException;

	/**
	 * @see com.idega.block.trade.stockroom.business.StockroomBusinessBean#getUserResellerId
	 */
	public int getUserResellerId(User user) throws RuntimeException, SQLException, java.rmi.RemoteException;

	/**
	 * @see com.idega.block.trade.stockroom.business.StockroomBusinessBean#updateProduct
	 */
	public int updateProduct(int productId, int supplierId, Integer fileId, String productName, String number,
			String productDescription, boolean isValid, int[] addressIds, int discountTypeId) throws Exception,
			java.rmi.RemoteException;

	/**
	 * @see com.idega.block.trade.stockroom.business.StockroomBusinessBean#createProduct
	 */
	public int createProduct(int supplierId, Integer fileId, String productName, String number,
			String productDescription, boolean isValid, int[] addressIds, int discountTypeId) throws Exception,
			java.rmi.RemoteException;

	/**
	 * @see com.idega.block.trade.stockroom.business.StockroomBusinessBean#getCurrencyDropdownMenu
	 */
	public DropdownMenu getCurrencyDropdownMenu(String menuName) throws java.rmi.RemoteException;

	/**
	 * @see com.idega.block.trade.stockroom.business.StockroomBusinessBean#isInTimeframe
	 */
	public boolean isInTimeframe(IWTimestamp from, IWTimestamp to, IWTimestamp stampToCheck, boolean yearly)
			throws java.rmi.RemoteException;

	/**
	 * @see com.idega.block.trade.stockroom.business.StockroomBusinessBean#isBetween
	 */
	public boolean isBetween(IWTimestamp from, IWTimestamp to, IWTimestamp stampToCheck, boolean yearly,
			boolean bordersCount) throws java.rmi.RemoteException;

	public void executeRemoteService(String remoteDomainToExclude, String methodQuery);

}
