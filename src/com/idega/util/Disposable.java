package com.idega.util;

import com.idega.jmodule.object.ModuleInfo;
/**
 * Title:
 * Description:
 * Copyright:    Copyright (c) 2001
 * Company:      idega.is
 * @author <a href="mailto:gummi@idega.is">Gu�mundur �g�st S�mundsson</a>
 * @version 1.0
 */

public interface Disposable {
  public void dispose(ModuleInfo modinfo);
}