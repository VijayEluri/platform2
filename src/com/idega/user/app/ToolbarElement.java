package com.idega.user.app;
import com.idega.presentation.*;

/**
 * <p>Title: idegaWeb</p>
 * <p>Description: </p>
 * <p>Copyright: Copyright (c) 2002</p>
 * <p>Company: idega Software</p>
 * @author <a href="gummi@idega.is">Gu�mundur �g�st S�mundsson</a>
 * @version 1.0
 */

public interface ToolbarElement {

  public Image getButtonImage(IWContext iwc);
  public String getName(IWContext iwc);
  public PresentationObject getPresentationObject(IWContext iwc);

}