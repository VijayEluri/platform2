package com.idega.idegaweb;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;

import java.util.Locale;
import com.idega.core.user.data.User;
import com.idega.core.accesscontrol.business.AccessController;


import com.idega.presentation.PresentationObject;
import com.idega.user.business.UserProperties;

import java.util.List;
import com.idega.core.data.ICObject;

/**
 * Title:        idegaclasses
 * Description:
 * Copyright:    Copyright (c) 2001
 * Company:      idega
 * @author <a href="tryggvi@idega.is">Tryggvi Larusson</a>
 * @version 1.0
 */

public interface IWUserContext extends java.io.Serializable{

  public Object getSessionAttribute(String attributeName);
  public void setSessionAttribute(String attributeName,Object attribute);
  public String getSessionId();
  public void removeSessionAttribute(String attributeName);
  public Locale getCurrentLocale();
  public void setCurrentLocale(Locale locale);
  /**
   * @deprecated Replaced with getCurrentUser()
   **/
  public User getUser();
  public AccessController getAccessController();
  public IWApplicationContext getApplicationContext();
  public UserProperties getUserProperties();
  /**
   * Gets the current user associated with this context
   * <br>This method is meant to replace getUser()
   * @return The current user if there is one associated with the current context. If there is none the method returns null.
   **/
  public com.idega.user.data.User getCurrentUser();



  //temp
  public boolean hasPermission(String permissionKey, PresentationObject obj);
  public boolean hasViewPermission(PresentationObject obj);
  public boolean hasEditPermission(PresentationObject obj);
  public boolean hasPermission(List groupIds, String permissionKey, PresentationObject obj);
  public boolean hasFilePermission(String permissionKey, int id);
  public boolean hasDataPermission(String permissionKey, ICObject obj, int entityRecordId);
  public boolean hasViewPermission(List groupIds, PresentationObject obj);
  public boolean hasEditPermission(List groupIds, PresentationObject obj);
  public boolean isSuperAdmin();
  public boolean isLoggedOn();


}
