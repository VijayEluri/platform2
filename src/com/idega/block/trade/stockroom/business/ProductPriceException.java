package com.idega.block.trade.stockroom.business;

/**
 * Title:        IW Trade
 * Description:
 * Copyright:    Copyright (c) 2001
 * Company:      idega.is
 * @author 2000 - idega team - <br><a href="mailto:gummi@idega.is">Gu�mundur �g�st S�mundsson</a><br><a href="mailto:gimmi@idega.is">Gr�mur J�nsson</a>
 * @version 1.0
 */

public class ProductPriceException extends RuntimeException {

  public ProductPriceException() {
    super();
  }

  public ProductPriceException(String explanation){
    super(explanation);
  }
}