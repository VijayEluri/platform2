package com.idega.block.text.business;



import com.idega.block.text.data.*;

import java.util.Vector;

import java.util.List;

import java.util.Locale;





/**

 * Title:

 * Description:

 * Copyright:    Copyright (c) 2000-2001 idega.is All Rights Reserved

 * Company:      idega

  *@author <a href="mailto:aron@idega.is">Aron Birkir</a>

 * @version 1.1

 */



public class TextHelper{

  private TxText eText;

  private ContentHelper eContentHelper;



  public TxText getTxText(){

    return eText;

  }

  public void setTxText(TxText text){

    eText = text;

  }

  public void setContentHelper(ContentHelper eContentHelper){

    eContentHelper = eContentHelper;

  }



  public ContentHelper getContentHelper(){

    return eContentHelper ;

  }



}



