package com.idega.business;

import com.idega.idegaweb.IWLocation;
import com.idega.idegaweb.browser.app.IWBrowser;
import com.idega.idegaweb.IWMainApplication;
import com.idega.presentation.FrameTable;
import com.idega.presentation.ui.Window;
import com.idega.presentation.Page;
import com.idega.presentation.IWContext;

import java.util.*;

/**
 * <p>Title: idegaWeb</p>
 * <p>Description: </p>
 * <p>Copyright: Copyright (c) 2002</p>
 * <p>Company: idega Software</p>
 * @author <a href="gummi@idega.is">Gu�mundur �g�st S�mundsson</a>
 * @version 1.0
 */

public class IWFrameBusinessBean extends IBOSessionBean implements IWFrameBusiness {

  //private final static String IW_FRAME_CONTENT = "iw_frame_content";
  private final static String SLASH = "/";

  private Hashtable frameStorage = new Hashtable();

  public IWFrameBusinessBean() {

  }


  public String getFrameSetIdentifier(FrameTable frame){
    int id = frame.getICObjectInstanceID();
    String identifier;
    if(id == 0){
      identifier = (0+SLASH+IWMainApplication.getEncryptedClassName(frame.getClass()));
    } else {
      identifier = Integer.toString(id);
    }
    return identifier;
  }

  public String getFrameSetIdentifier(IWLocation location){
    String identifier = (0+SLASH+IWMainApplication.getEncryptedClassName(location.getApplicationClass()));
    return identifier;
  }

  /**
   *
   * @param frame
   * @return FrameSetIdentifier to find it back
   */
  public String retainFrameSet(FrameTable frame){
    Hashtable table = getFrameSetStorage();
    String url = this.getFrameSetIdentifier(frame);
    table.put(url,frame);
    return url;
  }

//  public String retainFrameSet(FrameTable frame){
//    Hashtable table = getFrameSetStorage();
//    String url = this.getFrameSetIdentifier(frame);
//    Map frames = new Hashtable();
//    List l = frame.getAllContainedFrames();
//    if(l != null){
//      Iterator iter = l.iterator();
//      while (iter.hasNext()) {
//        Frame item = (Frame)iter.next();
//        frames.put(item.getName(),item);
//      }
//    }
//
//    table.put(url,frames);
//    return url;
//  }


  private Hashtable getFrameSetStorage(){
    if(frameStorage == null){
      frameStorage = new Hashtable();
    }
    return frameStorage;
  }

  public FrameTable getFrameSet(String identifier){
    Hashtable table = getFrameSetStorage();
    FrameTable frame = (FrameTable)table.get(identifier);
//    if(frame != null){
//      frame = (FrameSet)frame.clone();
//    }
    return frame;
  }


  public Page getFrame(String frameSetIdentifier, String frameName){
//    if(frameSetIdentifier.startsWith(0+SLASH)){
//
//    } else {
      FrameTable set = getFrameSet(frameSetIdentifier);
      if(set != null){
        if(frameName != null){
          return set.getFrame(frameName, this.getUserContext(), false);
        } else if(set instanceof IWBrowser){
          return ((IWBrowser)set).getControlFramePresentation(this.getUserContext(), false);
        }
      }
//    }
    return null;
  }

  public Page getFrame(IWLocation location){
    return this.getFrame(this.getFrameSetIdentifier(location),location.getTarget());
  }

}