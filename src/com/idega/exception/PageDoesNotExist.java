/*
 * $Id: PageDoesNotExist.java,v 1.1 2001/07/16 10:42:06 tryggvil Exp $
 *
 * Copyright (C) 2001 Idega hf. All Rights Reserved.
 *
 * This software is the proprietary information of Idega hf.
 * Use is subject to license terms.
 *
 */
package com.idega.exception;

import com.idega.util.ExceptionHelper;

/**
 *
 *
 * @author <a href="mailto:palli@idega.is">Pall Helgason</a>
 * @version 1.0alpha
 */
 public class PageDoesNotExist extends Exception {

  private static String key = "PageDoesNotExist";
  private static ExceptionHelper e = new ExceptionHelper();

  public PageDoesNotExist() {
    super(e.getExceptionText(key));
  }

  public PageDoesNotExist(String userDefinedKey) {
    super(e.getExceptionText(userDefinedKey));
  }
}