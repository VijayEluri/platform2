package com.idega.user.app;

import com.idega.event.IWPresentationEvent;
import com.idega.event.IWPresentationState;
import com.idega.idegaweb.IWBundle;
import com.idega.idegaweb.IWUserContext;
import com.idega.idegaweb.browser.presentation.IWBrowserView;
import com.idega.presentation.IWContext;
import com.idega.presentation.Page;
import com.idega.presentation.StatefullPresentation;
import com.idega.presentation.StatefullPresentationImplHandler;
import com.idega.util.IWColor;

/**
 * <p>Title: idegaWeb</p>
 * <p>Description: </p>
 * <p>Copyright: Copyright (c) 2002</p>
 * <p>Company: idega Software</p>
 * @author <a href="gummi@idega.is">Gu�mundur �g�st S�mundsson</a>
 * @version 1.0
 */

public class UserApplicationMenuArea extends Page implements IWBrowserView, StatefullPresentation {

  private IWBundle iwb;
  private StatefullPresentationImplHandler _stateHandler = null;
  private String _controlTarget = null;
  private IWPresentationEvent _controlEvent = null;
  private Toolbar toolbar = new Toolbar();
  private static String IW_BUNDLE_IDENTIFIER = "com.idega.user";
	private String userApplicationMainAreaStateId = null;



  public UserApplicationMenuArea() {
    _stateHandler = new StatefullPresentationImplHandler();
    _stateHandler.setPresentationStateClass(UserApplicationMenuAreaPS.class);
	this.setAllMargins(0);
  }

  public void setControlEventModel(IWPresentationEvent model){
//    System.out.print("UserApplicationMenuArea: setControlEventModel(IWPresentationEvent model)");
    _controlEvent = model;
    toolbar.setControlEventModel(model);
  }

  public void setControlTarget(String controlTarget){
//    System.out.print("UserApplicationMenuArea: setControlTarget(String controlTarget)");
    _controlTarget = controlTarget;
    toolbar.setControlTarget(controlTarget);
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

  public String getBundleIdentifier(){
    return this.IW_BUNDLE_IDENTIFIER;
  }


 /* public void empty(){
    toolbarTable.empty();
  }*/


  public void initializeInMain(IWContext iwc){
    this.empty();
    iwb = getBundle(iwc);
    this.setBackgroundColor(IWColor.getHexColorString(212,208,200));
    getParentPage().setBackgroundColor(IWColor.getHexColorString(212,208,200));
    String id = getPresentationState(iwc).getCompoundId();
    toolbar.setSelectedGroupProviderStateId(id);
    toolbar.setUserApplicationMainAreaStateId(userApplicationMainAreaStateId);
    super.add(toolbar);
  }

	/**
	 * @param string
	 */
	public void setUserApplicationMainAreaStateId(String string) {
		userApplicationMainAreaStateId = string;
	}

}