package com.idega.util;

import java.text.*;

/**
 * Title:
 * Description:
 * Copyright:    Copyright (c) 2001
 * Company:      idega multimedia
 * @author       <a href="mailto:aron@idega.is">aron@idega.is</a>
 * @version 1.0
 */

public class IsCollator {

  private static final String isl = "< a,A< �,�< b,B< c,C< d,D< �,�< e,E< �,�< f,F< g,G< h,H< i,I< �,�< j,J" +
                             "< k,K< l,L< m,M< n,N< o,O< �,�< p,P< q,Q< r,R< s,S< t,T" +
                             "< u,U< �,�< v,V< w,W< x,X< y,Y< �,�< z,Z< �,�< �,�< �,�";

  private RuleBasedCollator rbc;

  public IsCollator() {
    try {
      rbc = new RuleBasedCollator(isl);
    }
    catch (ParseException ex) {
      rbc = (RuleBasedCollator)Collator.getInstance();
    }
  }

  public static RuleBasedCollator getIsCollator(){
    RuleBasedCollator rbc;
    try {
      rbc = new RuleBasedCollator(isl);
    }
    catch (ParseException ex) {
      rbc = (RuleBasedCollator)Collator.getInstance();
    }
    return rbc;
  }

} // Class IsCollator