package com.idega.user.app;

import java.util.Collection;
import java.util.Iterator;
import javax.swing.event.ChangeListener;
import com.idega.event.IWActionListener;
import com.idega.event.IWPresentationEvent;
import com.idega.event.IWPresentationState;
import com.idega.idegaweb.IWBundle;
import com.idega.idegaweb.IWLocation;
import com.idega.idegaweb.IWUserContext;
import com.idega.idegaweb.browser.presentation.IWBrowserView;
import com.idega.presentation.IWContext;
import com.idega.presentation.Page;
import com.idega.presentation.PresentationObject;
import com.idega.presentation.StatefullPresentation;
import com.idega.presentation.StatefullPresentationImplHandler;
import com.idega.presentation.ui.Window;
import com.idega.repository.data.RefactorClassRegistry;
import com.idega.user.block.search.presentation.SearchResultsWindow;
import com.idega.user.business.UserGroupPlugInBusiness;
import com.idega.user.presentation.BasicUserOverview;

/**
 * <p>
 * Title: idegaWeb
 * </p>
 * <p>
 * Description:
 * </p>
 * <p>
 * Copyright: Copyright (c) 2002
 * </p>
 * <p>
 * Company: idega Software
 * </p>
 * 
 * @author <a href="gummi@idega.is">Gu�mundur �g�st S�mundsson </a>
 * @version 1.0
 */
public class UserApplicationMainArea extends Window implements IWBrowserView, StatefullPresentation {

	private IWBundle iwb;

	private StatefullPresentationImplHandler _stateHandler = null;

	private String _controlTarget = null;

	private IWPresentationEvent _contolEvent = null;

	private String styleScript = "DefaultStyle.css";

	private static final String IW_BUNDLE_IDENTIFIER = "com.idega.user";

	private BasicUserOverview _buo = new BasicUserOverview();

	private SearchResultsWindow search = new SearchResultsWindow();//these
																   // should be
																   // added
																   // dynamically

	public static String USER_APPLICATION_MAIN_AREA_PS_KEY = "iwme_uama_ps_key";

	public UserApplicationMainArea() {
		this.setAllMargins(0);
		this._stateHandler = new StatefullPresentationImplHandler();
		getStateHandler().setPresentationStateClass(UserApplicationMainAreaPS.class);
	}

	public void setControlEventModel(IWPresentationEvent model) {
		this._contolEvent = model;
		this._buo.setControlEventModel(model);
		this.search.setControlEventModel(model);
	}

	public void setControlTarget(String controlTarget) {
		this._controlTarget = controlTarget;
		this._buo.setControlTarget(controlTarget);
		this.search.setControlTarget(controlTarget);
	}

	public Class getPresentationStateClass() {
		return this._stateHandler.getPresentationStateClass();
	}

	public IWPresentationState getPresentationState(IWUserContext iwuc) {
		return this._stateHandler.getPresentationState(this, iwuc);
	}

	public StatefullPresentationImplHandler getStateHandler() {
		return this._stateHandler;
	}

	public void main(IWContext iwc) throws Exception {
		this.empty();
		IWBundle iwb = getBundle(iwc);
		Page parentPage = this.getParentPage();
		String styleSrc = iwb.getVirtualPathWithFileNameString(this.styleScript);
		parentPage.addStyleSheetURL(styleSrc);
		UserApplicationMainAreaPS ps = (UserApplicationMainAreaPS) this.getPresentationState(iwc);
		String className = ps.getClassNameToShow();
		Collection plugins = ps.getUserGroupPlugins();
		if (className != null) {
			PresentationObject obj = (PresentationObject) RefactorClassRegistry.forName(className).newInstance();
			add(obj);
			ps.setClassNameToShow(null);
		}
		else if (ps.isSearch()) {
			add(this.search);
		}
		else if (plugins != null && !plugins.isEmpty()) {
			Iterator iter = plugins.iterator();
			boolean buoHasBeenAdded = false;
			while (iter.hasNext()) {
				UserGroupPlugInBusiness biz = (UserGroupPlugInBusiness) iter.next();
				PresentationObject obj = biz.instanciateViewer(ps.getSelectedGroup());
				if (obj == null && !buoHasBeenAdded) {
					add(this._buo);
					buoHasBeenAdded = true;
				}
				else {
					add(obj);
				}
			}
		}
		else {
			this.add(this._buo);
		}
	}

	public void initializeInMain(IWContext iwc) {
//		System.out.println("in initializeInMain getClassToShow:"
//				+ ((UserApplicationMainAreaPS) this.getPresentationState(iwc)).getClassNameToShow());
		this.iwb = getBundle(iwc);
		IWLocation location = (IWLocation) this.getLocation().clone();
		location.setSubID(1);//bara eitthva? id...herma eftir instance id
		this._buo.setLocation(location, iwc);
		this._buo.setArtificialCompoundId(getCompoundId(), iwc);
		this.search.setLocation(location, iwc);
		this.search.setArtificialCompoundId(getCompoundId(), iwc);
		//this.setIWUserContext(iwc);
		IWPresentationState buoState = this._buo.getPresentationState(iwc);
		if (buoState instanceof IWActionListener) {
			((UserApplicationMainAreaPS) this.getPresentationState(iwc)).addIWActionListener((IWActionListener) buoState);
		}
		IWPresentationState searchState = this.search.getPresentationState(iwc);
		if (searchState instanceof IWActionListener) {
			((UserApplicationMainAreaPS) this.getPresentationState(iwc)).addIWActionListener((IWActionListener) searchState);
		}
		ChangeListener[] chListeners = this.getPresentationState(iwc).getChangeListener();
		if (chListeners != null) {
			for (int i = 0; i < chListeners.length; i++) {
				buoState.addChangeListener(chListeners[i]);
				searchState.addChangeListener(chListeners[i]);
			}
		}
		//    this.getParentPage().setBackgroundColor(IWColor.getHexColorString(250,245,240));
	}

	public String getBundleIdentifier() {
		return IW_BUNDLE_IDENTIFIER;
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see com.idega.presentation.ui.Window#isFocusAllowedOnLoad()
	 */
	protected boolean isFocusAllowedOnLoad() {
		return false;
	}
}