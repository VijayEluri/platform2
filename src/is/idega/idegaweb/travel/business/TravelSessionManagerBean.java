package is.idega.idegaweb.travel.business;

import is.idega.idegaweb.travel.block.search.data.ServiceSearchEngine;
import java.util.Collection;
import java.util.HashMap;
import java.util.Locale;
import java.util.Map;
import com.idega.block.trade.stockroom.business.TradePermissionBusiness;
import com.idega.block.trade.stockroom.data.Reseller;
import com.idega.block.trade.stockroom.data.Supplier;
import com.idega.business.IBOLookup;
import com.idega.business.IBOLookupException;
import com.idega.business.IBORuntimeException;
import com.idega.business.IBOSessionBean;
import com.idega.core.accesscontrol.business.NotLoggedOnException;
import com.idega.core.localisation.business.ICLocaleBusiness;
import com.idega.idegaweb.IWBundle;
import com.idega.idegaweb.IWResourceBundle;
import com.idega.user.data.Group;
import com.idega.user.data.User;

/**
 * Title:        idegaWeb
 * Description:
 * Copyright:    Copyright (c) 2001
 * Company:      idega
 * @author <a href="mailto:gimmi@idega.is>Gr�mur J�nsson</a>
 * @version 1.0
 */

public class TravelSessionManagerBean extends IBOSessionBean implements TravelSessionManager {

  public static String IW_BUNDLE_IDENTIFIER="is.idega.travel";
  private IWResourceBundle _iwrb;
  private IWBundle _bundle;
  private Locale _locale;
  private int _localeId = -1;
  private boolean _isSet = false;

  private Supplier _supplier;
  private Reseller _reseller;
  private Group _supplierManager;
  private ServiceSearchEngine _engine;
  private boolean _isSupplierManager;
  private User _user;
  private int _userId = -1;
  private Map permissionMap;

  public TravelSessionManagerBean() {
  }

  public void clearLocale() {
    _locale = null;
    _localeId = -1;
    _iwrb = null;
  }

  public void clearAll() {
    clearLocale();
    _supplier = null;
    _reseller = null;
    _engine = null;
    _userId = -1;
    _user = null;
    _supplierManager = null;
    _isSupplierManager = false;
    _isSet = false;
    clearRoleCache();
  }
  
//  public boolean isSupplierManager() {
//		return _isSupplierManager;
//  }
  
  public boolean isSet() {
  	return _isSet;
  }
  
//  public void setIsSupplierManager(boolean isSupplierManager) {
//  	this._isSupplierManager = isSupplierManager;
//  }
  
  public void setSupplierManager(Group supplierManager) {
  	this._supplierManager = supplierManager;
  }
  
  public Group getSupplierManager() {
  	return _supplierManager;
  }

  public User getUser() {
    if (_user == null) {
    	try {
    		_user = getUserContext().getCurrentUser();
    		_isSet = (_user != null);
    	} catch (NotLoggedOnException e) {
//    		System.out.println("SessionManager : Noone is logged on");
    	}
    }
    return _user;
  }

  public int getUserId() {
    if (_userId == -1) {
      if (getUser() != null) {
        _userId = getUser().getID();
      }
    }
    return _userId;
  }

  public Locale getLocale() {
    if (_locale == null) {
      _locale = super.getUserContext().getCurrentLocale();
    }
    return _locale;
  }

  public int getLocaleId() {
    if (_localeId == -1) {
      _localeId = ICLocaleBusiness.getLocaleId(getLocale());
    }
    return _localeId;
  }

  public IWResourceBundle getIWResourceBundle() {
    if (_iwrb == null) {
      _iwrb = getIWBundle().getResourceBundle(getLocale());
    }
    return _iwrb;
  }

  public IWBundle getIWBundle() {
    if (_bundle == null) {
      _bundle = super.getIWApplicationContext().getIWMainApplication().getBundle(this.IW_BUNDLE_IDENTIFIER);
    }
    return _bundle;
  }

  public Supplier getSupplier() {
    return _supplier;
  }

  public void setSupplier(Supplier supplier) {
    _supplier = supplier;
  }

  public Reseller getReseller() {
    return _reseller;
  }

  public void setReseller(Reseller reseller) {
    _reseller = reseller;
  }
  
  public void setSearchEngine(ServiceSearchEngine engine) {
  	this._engine = engine;
  }
  
  public ServiceSearchEngine getSearchEngine() {
  	return _engine;
  }
  
  private Map getPermissionMap() {
  	if (permissionMap == null) {
  		permissionMap = new HashMap();
  	}
  	return permissionMap;
  }
  
  public boolean hasRole(String role) {
  	Boolean bool = (Boolean) getPermissionMap().get(role);
  	if (bool == null) { 
  		try {
  			Collection coll = getIWMainApplication().getAccessController().getAllRolesForCurrentUser(getUserContext());
  			if (coll != null && !coll.isEmpty()) {
  				bool = new Boolean(coll.contains(role));
  			}
  		} catch (NotLoggedOnException e) {
  		} catch (Exception e) {
  			e.printStackTrace();
  		}
  		if (bool == null) {
  			bool = new Boolean(false); // Returning false, if nothing is found
  		} else {
	  		getPermissionMap().put(role, bool);
  		}
  	}
  	
  	return bool.booleanValue();
  }
  
  public void clearRoleCache() {
  	permissionMap = null;
  }
  
  protected TradePermissionBusiness getTradePermissionBusiness() {
  	try {
		return (TradePermissionBusiness) IBOLookup.getServiceInstance(getIWApplicationContext(), TradePermissionBusiness.class);
	}
	catch (IBOLookupException e) {
		throw new IBORuntimeException(e);
	}
  }

}