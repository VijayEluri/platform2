package com.idega.block.login.presentation;

/**
 * Title:
 * Description:
 * Copyright:    Copyright (c) 2001
 * Company:      idega multimedia
 * @author       <a href="mailto:aron@idega.is">aron@idega.is</a>
 * @version 1.0
 */

import com.idega.idegaweb.presentation.IWAdminWindow;
import com.idega.presentation.IWContext;
import com.idega.presentation.Table;

public class RegisterWindow extends IWAdminWindow{

  public RegisterWindow() {
    super();
    setScrollbar(false);
    setWidth(300 );
    setHeight(300 );
    //keepFocus();
  }

  public void main(IWContext iwc) throws Exception{
    Register Re = new Register();
    Table T = new Table(1,1);
    T.setAlignment(1,1,"center");
    T.add(Re,1,1);
    add(T);
    setTitle("Registration");
    //addTitle("Login Editor");
  }
}