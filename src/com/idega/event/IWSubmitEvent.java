package com.idega.event;

import java.awt.AWTEvent;
import com.idega.jmodule.object.interfaceobject.SubmitButton;

/**
 * Title:        IW Event
 * Description:
 * Copyright:    Copyright (c) 2001
 * Company:      idega.is
 * @author 2000 - idega team - <a href="mailto:gummi@idega.is">Gu�mundur �g�st S�mundsson</a>
 * @version 1.0
 */

public class IWSubmitEvent extends IWEvent {

  public static final int SUBMIT_PERFORMED = AWTEvent.RESERVED_ID_MAX + 11;

  public IWSubmitEvent(SubmitButton source, int id) {
    super((Object)source,id);
  }


}