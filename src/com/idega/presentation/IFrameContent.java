package com.idega.presentation;

/**
 * Title:        IW Project
 * Description:
 * Copyright:    Copyright (c) 2001
 * Company:      idega
 * @author <a href="gummi@idega.is">Gu�mundur �g�st S�mundsson</a>
 * @version 1.0
 */

public interface IFrameContent {
  public void setOwnerInstance(PresentationObject obj);
  public PresentationObject getOwnerInstance();
}