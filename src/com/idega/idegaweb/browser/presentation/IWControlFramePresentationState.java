package com.idega.idegaweb.browser.presentation;

import com.idega.idegaweb.IWLocation;
import com.idega.presentation.Frame;
import com.idega.event.IWPresentationState;
import javax.swing.event.ChangeEvent;
import javax.swing.event.ChangeListener;
import java.util.HashSet;
import java.util.Set;
import com.idega.event.IWPresentationStateImpl;

/**
 * <p>Title: idegaWeb</p>
 * <p>Description: </p>
 * <p>Copyright: Copyright (c) 2002</p>
 * <p>Company: idega Software</p>
 * @author <a href="gummi@idega.is">Gu�mundur �g�st S�mundsson</a>
 * @version 1.0
 */

public class IWControlFramePresentationState extends IWPresentationStateImpl implements ChangeListener {

  Set onLoadSet = new HashSet();

  public IWControlFramePresentationState() {
  }

  public void setOnLoad(String action){
    onLoadSet.add(action);
  }

  public void removeOnLoad(String action){
    onLoadSet.remove(action);
  }

  public void clearOnLoad(){
    onLoadSet.clear();
  }

  public Set getOnLoadSet(){
    return onLoadSet;
  }


  public void stateChanged(ChangeEvent e){
      IWPresentationState state = (IWPresentationState)e.getSource();
      IWLocation location = state.getLocation();
      location.getClass();
      String compoundId = state.getArtificialCompoundId();
      if (compoundId == null)
        compoundId = state.getCompoundId();
      String frameName = Frame.getFrameName(compoundId);
      if (frameName != null)
        this.setOnLoad("parent.frames['" + frameName + "'].location.reload()");
//      if(location.isInFrameSet()){
//        this.setOnLoad("parent.frames['" + location.getTarget() + "'].location.reload()");
//      } else if (location.isInPopUpWindow()) {
//        //this.setOnLoad("");
//      } else{
//        // is in controlframe hence is reloading.
//      }
        

    }


}