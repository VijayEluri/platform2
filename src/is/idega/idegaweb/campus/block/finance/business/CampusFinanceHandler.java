package is.idega.idegaweb.campus.block.finance.business;
import is.idega.idegaweb.campus.block.allocation.data.ContractBMPBean;
import is.idega.idegaweb.campus.business.CampusSettings;
import is.idega.idegaweb.campus.data.ApartmentAccountEntry;
import is.idega.idegaweb.campus.data.ApartmentAccountEntryHome;
import is.idega.idegaweb.campus.data.BatchContract;
import is.idega.idegaweb.campus.data.BatchContractHome;
import is.idega.idegaweb.campus.data.ContractAccountApartment;
import is.idega.idegaweb.campus.data.ContractAccountApartmentHome;

import java.math.BigDecimal;
import java.rmi.RemoteException;
import java.text.NumberFormat;
import java.util.Collection;
import java.util.Hashtable;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Vector;
import java.util.logging.Logger;

import javax.ejb.CreateException;
import javax.ejb.FinderException;

import com.idega.block.building.business.BuildingCacher;
import com.idega.block.finance.business.AssessmentTariffPreview;
import com.idega.block.finance.business.FinanceHandler;
import com.idega.block.finance.data.AccountEntry;
import com.idega.block.finance.data.AccountEntryHome;
import com.idega.block.finance.data.AssessmentRound;
import com.idega.block.finance.data.AssessmentRoundHome;
import com.idega.block.finance.data.Tariff;
import com.idega.block.finance.data.TariffHome;
import com.idega.business.IBOLookup;
import com.idega.business.IBOLookupException;
import com.idega.data.IDOLookup;
import com.idega.data.IDOLookupException;
import com.idega.idegaweb.IWApplicationContext;
import com.idega.util.IWTimestamp;
/**
 * Title: Description: Copyright: Copyright (c) 2001 Company:
 * 
 * @author <a href="mailto:aron@idega.is">aron@idega.is
 * @version 1.0
 */
public class CampusFinanceHandler implements FinanceHandler {
	
	private int precisionCount = 2;
	
	int count = 0;
	NumberFormat nf = NumberFormat.getPercentInstance();
	Logger logger = null;
	
	public CampusFinanceHandler() {
		logger = Logger.getLogger("is.idega.idegaweb.campus.finance");
	}
	public String getAccountType() {
		return com.idega.block.finance.data.AccountBMPBean.typeFinancial;
	}
	public boolean rollbackAssessment(IWApplicationContext iwac,Integer assessmentRoundId) {
		try {
			((CampusAssessmentBusiness)IBOLookup.getServiceInstance(iwac,CampusAssessmentBusiness.class)).rollBackAssessment(assessmentRoundId);
		} catch (IBOLookupException e) {
			e.printStackTrace();
		} catch (RemoteException e) {
			e.printStackTrace();
		}
		return false;
	}
	public boolean executeAssessment(IWApplicationContext iwac,Integer categoryId, Integer tariffGroupId, String roundName, Integer cashierId,
			Integer accountKeyId, IWTimestamp paymentdate, IWTimestamp start, IWTimestamp end,Integer excessRoundID) {
		logger.info("Starting assessment for period " + start.toString() + "-" + end.toString());
		Collection tariffs = null;
		Collection listOfUsers = null;
		String[] statuses = {ContractBMPBean.STATUS_SIGNED, ContractBMPBean.STATUS_ENDED, ContractBMPBean.STATUS_RESIGNED,
				ContractBMPBean.STATUS_TERMINATED};
		try {
			tariffs = ((TariffHome) IDOLookup.getHome(Tariff.class)).findByTariffGroup((tariffGroupId));
			ContractAccountApartmentHome caah = ((ContractAccountApartmentHome) IDOLookup.getHome(ContractAccountApartment.class));
			// excess round handling
			if(excessRoundID!=null  && excessRoundID.intValue()>0){
				listOfUsers = caah.findByTypeAndStatusAndOverlapPeriodAndNotInRound(getAccountType(), statuses, start.getDate(), end.getDate(),excessRoundID);
			}
			else{
				listOfUsers = caah.findByTypeAndStatusAndOverlapPeriod(getAccountType(), statuses, start.getDate(), end.getDate());
			}
					
		} catch (IDOLookupException e1) {
			e1.printStackTrace();
		} catch (FinderException e1) {
			e1.printStackTrace();
		}
		//FinanceFinder.getInstance().listOfTariffs(iTariffGroupId);
		//List listOfUsers =
		// CampusAccountFinder.listOfContractAccountApartment(getAccountType(),
		// start, end);
		//int iAccountCount = 0;
		if (tariffs != null) {
			if (listOfUsers != null) {
				//NumberFormat nf = NumberFormat.getPercentInstance();
				//int rlen = listOfUsers.size();
				Tariff tariff;
				char cAttribute;
				ContractAccountApartment user;
				int attributeId = -1;
				Integer roundId = new Integer( -1);
				AssessmentRound AR = null;
				javax.transaction.TransactionManager t = com.idega.transaction.IdegaTransactionManager.getInstance();
				try {
					t.begin();
					//try {
					AR = ((AssessmentRoundHome) IDOLookup.getHome(AssessmentRound.class)).create();
					AR.setAsNew(roundName);
					AR.setCategoryId(categoryId.intValue());
					//AR.setCategoryId(iCategoryId);
					AR.setTariffGroupId(tariffGroupId.intValue());
					AR.setType(com.idega.block.finance.data.AccountBMPBean.typeFinancial);
					AR.setDueDate(paymentdate.getDate());
					AR.setPeriodFromDate(start.getDate());
					AR.setPeriodToDate(end.getDate());
					AR.store();
					roundId = (Integer) AR.getPrimaryKey();
					//iRoundId++; // is this quickfix of death
					/*
					 * } catch (SQLException ex) { ex.printStackTrace(); try {
					 * AR.delete(); } catch (SQLException ex2) {
					 * ex2.printStackTrace(); AR = null; } }
					 * 
					 * if (AR != null) { javax.transaction.TransactionManager t =
					 * com.idega.transaction.IdegaTransactionManager.getInstance();
					 * 
					 * try { t.begin();
					 */
					int totals = 0;
					int totalAmount = 0;
					double factor = 1;
					int precision = getPrecision(iwac);
					// All tenants accounts (Outer loop)
					for (Iterator iter = listOfUsers.iterator(); iter.hasNext();) {
						user = (ContractAccountApartment) iter.next();
						factor = getFactor(user, start, end,precision);
						///Account eAccount =
						// ((com.idega.block.finance.data.AccountHome)com.idega.data.IDOLookup.getHomeLegacy(Account.class)).findByPrimaryKeyLegacy(user.getAccountId());
						if (factor > 0) {
							totalAmount = 0;
							float Amount = 0;
							// For each tariff (Inner loop)
							for (Iterator iter2 = tariffs.iterator(); iter2.hasNext();) {
								tariff = (Tariff) iter2.next();
								Amount = 0;
								String sAttribute = tariff.getTariffAttribute();
								// If we have an tariff attribute
								if (sAttribute != null) {
									attributeId = -1;
									cAttribute = sAttribute.charAt(0);
									// If All
									if (cAttribute == BuildingCacher.CHARALL) {
										Amount = insertEntry(tariff, user, roundId, paymentdate, cashierId, factor);
									}
									// other than all
									else {
										// attribute check
										if (sAttribute.length() >= 3) {
											attributeId = Integer.parseInt(sAttribute.substring(2));
											switch (cAttribute) {
												case BuildingCacher.CHARTYPE :
													// Apartment type
													if (attributeId == user.getApartmentTypeId())
														Amount = insertEntry(tariff, user, roundId, paymentdate,
																cashierId, factor);
													break;
												case BuildingCacher.CHARCATEGORY :
													// Apartment category
													if (attributeId == user.getApartmentCategoryId())
														Amount = insertEntry(tariff, user, roundId, paymentdate,
																cashierId, factor);
													break;
												case BuildingCacher.CHARBUILDING :
													// Building
													if (attributeId == user.getBuildingId())
														Amount = insertEntry(tariff, user, roundId, paymentdate,
																cashierId, factor);
													break;
												case BuildingCacher.CHARFLOOR :
													// Floor
													if (attributeId == user.getFloorId())
														Amount = insertEntry(tariff, user, roundId, paymentdate,
																cashierId, factor);
													break;
												case BuildingCacher.CHARCOMPLEX :
													// Complex
													if (attributeId == user.getComplexId())
														Amount = insertEntry(tariff, user, roundId, paymentdate,
																cashierId, factor);
													break;
												case BuildingCacher.CHARAPARTMENT :
													// Apartment
													if (attributeId == user.getApartmentId())
														Amount = insertEntry(tariff, user, roundId, paymentdate,
																cashierId, factor);
													break;
											} // switch
										} // attribute check
									} // other than all
									if (sAttribute.length() >= 3) {
										attributeId = Integer.parseInt(sAttribute.substring(2));
									}
									totalAmount += Amount;
								}
							} // Inner loop block
							try {
                                // If the contract got some invoices we register it to the batch work
                                createBatchContract(user,AR);
                            } catch (Exception e2) {
                                System.out.println("failing to register batchcontract for user "+user.getUserId());
                                e2.printStackTrace();
                            }
						}
						totals += totalAmount * -1;
					} // Outer loop block
					AR.store();
					t.commit();
					return true;
				} // Try block
				catch (Exception e) {
					try {
						t.rollback();
					} catch (javax.transaction.SystemException ex) {
						ex.printStackTrace();
					}
					e.printStackTrace();
				}
			}
			//}
		}
		return false;
	}
	/**
	 * Returns a multiplying factor to entry prices, for each contract The
	 * factor makes use of the contract begin and end dates. NOT if the
	 * contract has a delivertime, that is the begin date, NOT if the contract
	 * has a returntime , that is the end date.
	 */
	public double getFactor(ContractAccountApartment con, IWTimestamp start, IWTimestamp end,int precision) {
		
		start.setTime(0, 0, 0);
		end.setTime(23, 59, 59);
		long begin = start.getTimestamp().getTime();
		long endin = end.getTimestamp().getTime();

		long valfr = con.getValidFrom().getTime();
		long valto = con.getValidTo().getTime();
		double ret = getFactor(begin,endin,valfr,valto,precision);
		
		//if(ret<1)System.out.println("factor for contract "+con.getContractId() +" start:"+(new java.util.Date(begin)).toString()+" end: "+(new java.util.Date(endin)).toString()+" from"+(new java.util.Date(valfr)).toString()+" to"+(new java.util.Date(valto)).toString()+" factor: "+ret);
		
		return ret;
	}
	
	private double getFactor(long begin,long endin,long valfr,long valto,int precision){
		//long del = endin - begin;
		int periodDays = IWTimestamp.getDaysBetween(new IWTimestamp(begin),new IWTimestamp(endin))+1;
		
		
		if (begin <= valto && valto <= endin) {
			//System.out.println("ends within period");
			endin = valto;
		}
		if (begin <= valfr && valfr <= endin) {
			//System.out.println("begins within period");
			begin = valfr;
		}
		int validDays = IWTimestamp.getDaysBetween(new IWTimestamp(begin),new IWTimestamp(endin));
		if(validDays>=0 )//&& validDays< periodDays)
			validDays++;
		/*
		IWTimestamp theBegin = new IWTimestamp(begin);
		theBegin.setTime(0,0,0,0);
		IWTimestamp theEnd = new IWTimestamp(endin);
		theEnd.setTime(23,59,59);
		begin = theBegin.getDate().getTime();
		endin = theEnd.getDate().getTime();
		BigDecimal ret = new BigDecimal(endin - begin);
		if(IWTimestamp.getDaysBetween(theBegin,theEnd)>0){
			ret = ret.divide(new BigDecimal(del),4,BigDecimal.ROUND_UP);
			//ret = (diff) / del;
		}
		else
			ret=new BigDecimal(0);
		*/
		BigDecimal ret = new BigDecimal(validDays);
		ret = ret.divide(new BigDecimal(periodDays),precision,BigDecimal.ROUND_HALF_EVEN);
		return ret.doubleValue();
	}
	
	public static void main(String[] args){
		IWTimestamp begin = new IWTimestamp(1,6,2004);
		IWTimestamp end = new IWTimestamp(30,6,2004);
		
		IWTimestamp from = new IWTimestamp(1,6,2004);
		IWTimestamp to = new IWTimestamp(1,8,2004);
		for (int day = 1; day <= 30; day++) {
			to = new IWTimestamp(day,6,2004);
			to.setTime(23,59,59);
			CampusFinanceHandler handler = new CampusFinanceHandler();
			double ret = handler.getFactor(begin.getDate().getTime(),end.getDate().getTime(),from.getDate().getTime(),to.getDate().getTime(),4);
			//System.out.print(" Begin: "+begin.toString()+" End "+end.toString());
			//System.out.print(" From:"+from.toString()+" To: "+to.toString());
			System.out.println("Dagarmunur="+(day-1)+" Hlutfall= "+ret);
		}
		
		
		
		
	}
	public Collection listOfAssessmentTariffPreviews(IWApplicationContext iwac,Integer tariffGroupId, IWTimestamp start, IWTimestamp end)
			throws java.rmi.RemoteException {
		Collection tariffs = null;
		try {
			tariffs = ((TariffHome) IDOLookup.getHome(Tariff.class)).findByTariffGroup((tariffGroupId));
		} catch (IDOLookupException e) {
			e.printStackTrace();
		} catch (FinderException e) {
			e.printStackTrace();
		}
		//FinanceFinder.getInstance().listOfTariffs(iTariffGroupId);
		//List listOfTariffs = new Vector(tariffs);
		//List listOfUsers =
		// CampusAccountFinder.listOfRentingUserAccountsByType(getAccountType());
		Collection listOfUsers = null;
		
		try {
			listOfUsers = ((ContractAccountApartmentHome) IDOLookup.getHome(ContractAccountApartment.class)).findAll();
		} catch (IDOLookupException e1) {
			e1.printStackTrace();
		} catch (FinderException e1) {
			e1.printStackTrace();
		}
		if (tariffs != null && listOfUsers != null) {
			Hashtable H = new Hashtable(tariffs.size());
			//int rlen = listOfUsers.size();
			Tariff eTariff;
			char cAttribute;
			ContractAccountApartment user;
			int iAttributeId = -1;
			String sAttribute;
			int precis = getPrecision(iwac);
			// All tenants accounts (Outer loop)
			for (Iterator iter1 = listOfUsers.iterator(); iter1.hasNext();) {
				user = (ContractAccountApartment) iter1.next();
				double factor = getFactor(user, end, start,precis);
				// For each tariff (Inner loop)
				if (factor > 0) {
					for (Iterator iter = tariffs.iterator(); iter.hasNext();) {
						eTariff = (Tariff) iter.next();
						sAttribute = eTariff.getTariffAttribute();
						// If we have an tariff attribute
						if (sAttribute != null) {
							iAttributeId = -1;
							cAttribute = sAttribute.charAt(0);
							//System.err.println("att
							// "+String.valueOf(cAttribute));
							// If All
							if (cAttribute == BuildingCacher.CHARALL) {
								addAmount(H, eTariff, factor);
							}
							// other than all
							else {
								// attribute check
								if (sAttribute.length() >= 3) {
									iAttributeId = Integer.parseInt(sAttribute.substring(2));
									switch (cAttribute) {
										case BuildingCacher.CHARTYPE :
											// Apartment type
											if (iAttributeId == user.getApartmentTypeId())
												addAmount(H, eTariff, factor);
											break;
										case BuildingCacher.CHARCATEGORY :
											// Apartment category
											if (iAttributeId == user.getApartmentCategoryId())
												addAmount(H, eTariff, factor);
											break;
										case BuildingCacher.CHARBUILDING :
											// Building
											if (iAttributeId == user.getBuildingId())
												addAmount(H, eTariff, factor);
											break;
										case BuildingCacher.CHARFLOOR :
											// Floor
											if (iAttributeId == user.getFloorId())
												addAmount(H, eTariff, factor);
											break;
										case BuildingCacher.CHARCOMPLEX :
											// Complex
											if (iAttributeId == user.getComplexId())
												addAmount(H, eTariff, factor);
											break;
										case BuildingCacher.CHARAPARTMENT :
											// Apartment
											if (iAttributeId == user.getApartmentId())
												addAmount(H, eTariff, factor);
											break;
									} // switch
								} // attribute check
							} // other than all
							if (sAttribute.length() >= 3) {
								iAttributeId = Integer.parseInt(sAttribute.substring(2));
							}
						}
					} // Inner loop block
				} // factor check
			} // Outer loop block
			//System.err.println("count "+count);
			if (H != null) {
				return H.values();
			}
		} // listcheck
		else
			logger.info("nothing to preview");
		return null;
	}
	private synchronized void addAmount(Map map, Tariff tariff, double factor) throws java.rmi.RemoteException {
		//System.err.println("map size "+map.size());
		Integer id = ((Integer) tariff.getPrimaryKey());
		AssessmentTariffPreview preview;
		if (map.containsKey(id)) {
			preview = (AssessmentTariffPreview) map.get(id);
		} else {
			preview = new AssessmentTariffPreview(tariff.getName());
		}
		preview.addAmount((float) (tariff.getPrice() * factor));
		map.put(id, preview);
		count++;
	}
	private float insertEntry(Tariff T, ContractAccountApartment caa, Integer roundId, IWTimestamp paymentdate,
			Integer cashierId, double factor) throws CreateException, java.rmi.RemoteException {
		if (factor > 0) {
			AccountEntry AE = ((AccountEntryHome) IDOLookup.getHome(AccountEntry.class)).create();
			AE.setAccountId(caa.getAccountId());
			AE.setAccountKeyId(T.getAccountKeyId());
			AE.setCashierId(cashierId);
			AE.setLastUpdated(IWTimestamp.getTimestampRightNow());
			/** @todo skeptical precision cut */
			BigDecimal price = new BigDecimal(-T.getPrice());
			price = price.multiply(new BigDecimal(factor));
			BigDecimal finalPrice = price.setScale(0,BigDecimal.ROUND_HALF_UP);
			if(factor<1){
				logger.fine("price="+price.doubleValue());
				logger.fine(" finalprice="+finalPrice.doubleValue());
			}
			AE.setTotal(finalPrice.floatValue());
			//AE.setTotal((int) (-T.getPrice() * factor));
			AE.setRoundId(roundId);
			AE.setName(T.getName());
			if (T.getInfo() != null)
				AE.setInfo(T.getInfo() + " " + nf.format(factor));
			else
				AE.setInfo(nf.format(factor));
			AE.setStatus(com.idega.block.finance.data.AccountEntryBMPBean.statusCreated);
			AE.setCashierId(1);
			AE.setPaymentDate(paymentdate.getTimestamp());
			AE.store();
			createApartmentAccountEntry(caa, AE);
			return AE.getTotal();
		}
		return 0;
		/*
		 * System.err.println("totals before"+totals); totals = totals +
		 * AE.getPrice(); System.err.println("price"+AE.getPrice());
		 * System.err.println("totals after"+totals);
		 */
	}
	private void createApartmentAccountEntry(ContractAccountApartment caa, AccountEntry AE) throws CreateException, IDOLookupException {
		ApartmentAccountEntry aprtEntry = ((ApartmentAccountEntryHome) IDOLookup
				.getHome(ApartmentAccountEntry.class)).create();
		aprtEntry.setAccountEntryID((Integer) AE.getPrimaryKey());
		aprtEntry.setApartmentID(new Integer(caa.getApartmentId()));
		aprtEntry.store();
	}
	
	private void createBatchContract(ContractAccountApartment caa, AssessmentRound round )throws CreateException, IDOLookupException{
		BatchContract batchContract = ((BatchContractHome)IDOLookup.getHome(BatchContract.class)).create();
		batchContract.setBatchID((Integer)round.getPrimaryKey());
		batchContract.setContractID(new Integer(caa.getContractId()));
		batchContract.store();
	}
	public Map getAttributeMap() {
		Map map = BuildingCacher.mapOfLodgingsNames();
		map.put("a", "All");
		return map;
	}
	public List listOfAttributes() {
		List list = BuildingCacher.listOfMapEntries();
		list.add(0, "a");
		return list;
	}
	/*
	 * (non-Javadoc)
	 * 
	 * @see com.idega.block.finance.business.FinanceHandler#getTariffsByUserAndGroup(java.lang.Integer,
	 *           java.lang.Integer)
	 */
	public Collection getTariffsForAccountInGroup(Integer accountID, Integer tariffGroupID) {
		Vector userTariffs = new Vector();
		try {
			Collection tariffs = ((TariffHome) IDOLookup.getHome(Tariff.class)).findByTariffGroup(tariffGroupID);
			ContractAccountApartment account = ((ContractAccountApartmentHome) IDOLookup
					.getHome(ContractAccountApartment.class)).findByAccountAndRented(accountID,true);
			for (Iterator iter = tariffs.iterator(); iter.hasNext();) {
				Tariff tariff = (Tariff) iter.next();
				String attribute = tariff.getTariffAttribute();
				// If we have an tariff attribute
				if (attribute != null) {
					int iAttributeId = -1;
					char cAttribute = attribute.charAt(0);
					// If All
					if (cAttribute == BuildingCacher.CHARALL) {
						userTariffs.add(tariff);
					}
					// other than all
					else {
						// attribute check
						if (attribute.length() >= 3) {
							iAttributeId = Integer.parseInt(attribute.substring(2));
							switch (cAttribute) {
								case BuildingCacher.CHARTYPE :
									// Apartment type
									if (iAttributeId == account.getApartmentTypeId())
										userTariffs.add(tariff);
									break;
								case BuildingCacher.CHARCATEGORY :
									// Apartment category
									if (iAttributeId == account.getApartmentCategoryId())
										userTariffs.add(tariff);
									break;
								case BuildingCacher.CHARBUILDING :
									// Building
									if (iAttributeId == account.getBuildingId())
										userTariffs.add(tariff);
									break;
								case BuildingCacher.CHARFLOOR :
									// Floor
									if (iAttributeId == account.getFloorId())
										userTariffs.add(tariff);
									break;
								case BuildingCacher.CHARCOMPLEX :
									// Complex
									if (iAttributeId == account.getComplexId())
										userTariffs.add(tariff);
									break;
								case BuildingCacher.CHARAPARTMENT :
									// Apartment
									if (iAttributeId == account.getApartmentId())
										userTariffs.add(tariff);
									break;
							} // switch
						} // attribute check
					} // other than all
				}
			}
		} catch (IDOLookupException e) {
			e.printStackTrace();
		} catch (NumberFormatException e) {
			e.printStackTrace();
		} catch (FinderException e) {
			e.printStackTrace();
		}
		return userTariffs;
	}
	/* (non-Javadoc)
	 * @see com.idega.block.finance.business.FinanceHandler#publishAssessment(com.idega.idegaweb.IWApplicationContext, java.lang.Integer)
	 */
	public void publishAssessment(IWApplicationContext iwac, Integer roundId) {
		try {
			CampusAssessmentBusiness assBuiz = (CampusAssessmentBusiness)IBOLookup.getServiceInstance(iwac,CampusAssessmentBusiness.class);
			assBuiz.publishAssessment(roundId);
		} catch (IBOLookupException e) {
			e.printStackTrace();
		}

	}
	
	private int getPrecision(IWApplicationContext iwac){
		String precision = iwac.getIWMainApplication().getBundle(CampusSettings.IW_BUNDLE_IDENTIFIER).getProperty("FINANCE_FACTOR_PRECISION",String.valueOf(2));
		if(precision!=null)
			return Integer.parseInt(precision);
		return 2;
	}
}