package com.idega.event;

import com.idega.presentation.IWContext;

/**
 * Title:        IW Event
 * Description:
 * Copyright:    Copyright (c) 2001
 * Company:      idega.is
 * @author 2000 - idega team - <a href="mailto:gummi@idega.is">Gu�mundur �g�st S�mundsson</a>
 * @version 1.0
 */

public interface IWModuleEvent {
  public void setIWContext(IWContext iwc);
  public IWContext getIWContext();
}