package com.idega.event;

import java.awt.AWTEvent;
import com.idega.jmodule.object.textObject.Link;

/**
 * Title:        IW Event
 * Description:
 * Copyright:    Copyright (c) 2001
 * Company:      idega.is
 * @author 2000 - idega team - <a href="mailto:gummi@idega.is">Gu�mundur �g�st S�mundsson</a>
 * @version 1.0
 */

public class IWLinkEvent extends IWEvent {

  public final static int LINK_ACTION_PERFORMED = AWTEvent.RESERVED_ID_MAX +1;

  public IWLinkEvent(Link source, int id) {
    super((Object)source,id);
  }

}