package com.idega.block.calendar.business;

import com.idega.block.BlockProperties;

/**
 * Title:        Calendar
 * Description:
 * Copyright:    Copyright (c) 2001
 * Company:      idega multimedia
 * @author
 * @version 1.0
 */

public class CalendarProperties extends BlockProperties {

  private static String[] IS = {"...meira","Fyrri M�nu�ur","N�sti M�nu�ur","Til Baka","Nafn","L�sing","Hreinsa","Vista","Villa kom upp","Henda","Breyta","� dag"};
  private static String[] EN = {"...more","Previous month","Next month","Back","Name","Description","Clear","Save","An error occured","Delete","Modify","Today"};
  //                            0           1               2         3     4           5           6     7         8                9       10     11
  private static String[] DK = {"...mere","F�rre m�ned","Neste m�ned","Til bage","Navn","Beskrivelse","kleer","Save","Baa!! error","Smide","modify"};



  public CalendarProperties() {
    super();
    this.setEnglishAsDefaultLanguage();
  }


}