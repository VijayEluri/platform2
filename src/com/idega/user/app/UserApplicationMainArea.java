package com.idega.user.app;

import com.idega.presentation.ui.Window;

import java.util.Collection;
import java.util.Iterator;

import javax.swing.event.ChangeListener;
import com.idega.idegaweb.*;
import com.idega.business.IBOLookup;
import com.idega.event.*;
import com.idega.user.business.UserGroupPlugInBusiness;
import com.idega.user.data.UserGroupPlugIn;
import com.idega.user.presentation.BasicUserOverview;
import com.idega.user.presentation.UserGroupPlugInPresentable;
import com.idega.idegaweb.browser.presentation.IWBrowserView;
import com.idega.presentation.*;

/**
 * <p>Title: idegaWeb</p>
 * <p>Description: </p>
 * <p>Copyright: Copyright (c) 2002</p>
 * <p>Company: idega Software</p>
 * @author <a href="gummi@idega.is">Gu�mundur �g�st S�mundsson</a>
 * @version 1.0
 */

public class UserApplicationMainArea extends Window implements IWBrowserView, StatefullPresentation {


  private IWBundle iwb;
  private StatefullPresentationImplHandler _stateHandler = null;
  private String _controlTarget = null;
  private IWPresentationEvent _contolEvent = null;

  private BasicUserOverview _buo = new BasicUserOverview();


  public UserApplicationMainArea() {
    this.setAllMargins(0);
  _stateHandler = new StatefullPresentationImplHandler();
    getStateHandler().setPresentationStateClass(UserApplicationMainAreaPS.class);
  }

  public void setControlEventModel(IWPresentationEvent model){
    _contolEvent = model;
    _buo.setControlEventModel(model);
  }

  public void setControlTarget(String controlTarget){
    _controlTarget = controlTarget;
    _buo.setControlTarget(controlTarget);
  }

  public Class getPresentationStateClass(){
    return _stateHandler.getPresentationStateClass();
  }

  public IWPresentationState getPresentationState(IWUserContext iwuc){
    return _stateHandler.getPresentationState(this,iwuc);
  }

  public StatefullPresentationImplHandler getStateHandler(){
    return _stateHandler;
  }

  public void main(IWContext iwc) throws Exception {
    this.empty();
    UserApplicationMainAreaPS ps = (UserApplicationMainAreaPS)this.getPresentationState(iwc);
	String className = ps.getClassNameToShow();
	Collection plugins = ps.getUserGroupPlugins();

    if( className != null ){
    	PresentationObject obj = (PresentationObject)Class.forName(className).newInstance();
    	add(obj);
        ps.setClassNameToShow(null);
    }
    else if( plugins!=null && !plugins.isEmpty() ){
    	System.out.println("Plugins are not null");
    	Iterator iter = plugins.iterator();
    	
    	while (iter.hasNext()) {
    		
			UserGroupPlugIn plugin = (UserGroupPlugIn) iter.next();
			className = plugin.getBusinessICObject().getClassName();
			System.out.println("Plugin business class : "+className);
			UserGroupPlugInBusiness biz = (UserGroupPlugInBusiness) IBOLookup.getServiceInstance(iwc,Class.forName(className));
			PresentationObject obj = biz.instanciateViewer(ps.getSelectedGroup());
			
			add(obj);
		}
    }
    else{
      this.add(_buo);
    }

  }


  public void initializeInMain(IWContext iwc){

    System.out.println("in initializeInMain getClassToShow:"+((UserApplicationMainAreaPS)this.getPresentationState(iwc)).getClassNameToShow());


    iwb = getBundle(iwc);

    IWLocation location = (IWLocation)this.getLocation().clone();
    location.setSubID(1);//bara eitthva? id...herma eftir instance id
    _buo.setLocation(location,iwc);


    this.setIWUserContext(iwc);

    IWPresentationState buoState = _buo.getPresentationState(iwc);
    if(buoState instanceof IWActionListener){
      ((UserApplicationMainAreaPS)this.getPresentationState(iwc)).addIWActionListener((IWActionListener)buoState);
    }


    ChangeListener[] chListeners = this.getPresentationState(iwc).getChangeListener();
    if(chListeners != null){
      for (int i = 0; i < chListeners.length; i++) {
        buoState.addChangeListener(chListeners[i]);
      }
    }

//    this.getParentPage().setBackgroundColor(IWColor.getHexColorString(250,245,240));

  }


}