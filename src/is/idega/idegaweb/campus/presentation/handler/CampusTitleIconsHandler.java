/*

 * $Id: CampusTitleIconsHandler.java,v 1.7 2004/06/28 14:05:56 thomas Exp $

 *

 * Copyright (C) 2001 Idega hf. All Rights Reserved.

 *

 * This software is the proprietary information of Idega hf.

 * Use is subject to license terms.

 *

 */

package is.idega.idegaweb.campus.presentation.handler;



import is.idega.idegaweb.campus.presentation.TitleIcons;

import java.util.List;

import com.idega.core.builder.presentation.ICPropertyHandler;
import com.idega.presentation.IWContext;
import com.idega.presentation.PresentationObject;
import com.idega.presentation.ui.DropdownMenu;



/**

 * @author <a href="aron@idega.is">Aron Birkir</a>

 * @version 1.0

 */

public class CampusTitleIconsHandler implements ICPropertyHandler {

  public final static String MENU = TitleIcons.MAINMENU;

  public final static String LOGIN = TitleIcons.LOGIN;

  public final static String IDEGA = TitleIcons.IDEGALOGO;



  /**

   *

   */

  public CampusTitleIconsHandler() {

  }



  /**

   *

   */

  public List getDefaultHandlerTypes() {

    return(null);

  }



  /**

   *

   */

  public PresentationObject getHandlerObject(String name, String value, IWContext iwc) {

    DropdownMenu menu = new DropdownMenu(name);

    menu.addMenuElement("","Select:");

    menu.addMenuElement(MENU ,"MENU");

    menu.addMenuElement(LOGIN,"LOGIN");

    menu.addMenuElement(IDEGA,"IDEGALOGO");

    menu.setSelectedElement(value);

    return(menu);

  }



  /**

   *

   */

  public void onUpdate(String values[], IWContext iwc) {

  }

}
