package com.idega.idegaweb.browser.app;

import com.idega.event.*;
import com.idega.idegaweb.browser.presentation.*;
import java.util.Iterator;
import java.util.List;
import javax.swing.event.ChangeListener;
import com.idega.business.IBOLookup;
import com.idega.presentation.*;
import com.idega.business.IWFrameBusiness;
import com.idega.idegaweb.IWUserContext;
import com.idega.idegaweb.browser.event.IWBrowseEvent;
import java.rmi.RemoteException;

/**
 * <p>Title: idegaWeb</p>
 * <p>Description: </p>
 * <p>Copyright: Copyright (c) 2002</p>
 * <p>Company: idega Software</p>
 * @author <a href="gummi@idega.is">Gu�mundur �g�st S�mundsson</a>
 * @version 1.0
 */

public class IWBrowser extends FrameTable implements StatefullPresentation {


  private String _frameName[] = {"iwb_top","iwb_menu","iwb_middle","iwb_bottom","iwb_main_left","iwb_main","iwb_main_right"};

  public static final int POS_TOP = 0;
  public static final int POS_MENU = 1;
  public static final int POS_MIDDLE = 2;
  public static final int POS_BOTTOM = 3;

  public static final int POS_LEFTMAIN = 4;
  public static final int POS_MAIN = 5;
  public static final int POS_RIGHTMAIN = 6;

  private int _controlPosition = POS_TOP;

//  private IWBrowserFrame _topFrame = null;
//  private IWBrowserFrame _menuFrame = null;
//  private IWBrowserFrame _mainFrame = null;
//  private IWBrowserFrame _leftFrame = null;
//  private IWBrowserFrame _rightFrame = null;
  private IWBrowserFrame[] _browserFrames = new IWBrowserFrame[6];

  private FrameTable _middleFrameset;

  private boolean _showTopFrame = true;
  private boolean _showMenuFrame = true;
  private boolean _showBottomFrame = false;
  private boolean _showLeftMainFrame = true;
  private boolean _showRightMainFrame = false;

  private IWBrowserPresentationState _presentationState = null;

  private final static String IW_BUNDLE_IDENTIFIER = "com.idega.idegaweb.browser";

  public IWBrowser() {
    this.setResizable(true);
    this.setHeight(400);
    this.setWidth(600);
    initializeFrames();

  }

  protected void initializeFrames(){
    for (int i = 0; i < _browserFrames.length; i++) {
//      System.out.println("IWBrowser.initializeFrames(): "+i);
      _browserFrames[i] = new IWBrowserFrame();
      _browserFrames[i].setNameProperty(this.getFrameName(i));
      _browserFrames[i].getLocation().setApplicationClass(this.getClass());
      //_browserFrames[i].getLocation().isInFrameSet(true);
    }
    _middleFrameset = new FrameTable();
    _middleFrameset.setHorizontal();
  }

  public String getControlframeTarget(){
    return this.getFrameName(_controlPosition);
  }

  public Frame getControlframe(){
    return this.getFrame(this.getFrameName(_controlPosition));
  }

  public String getFrameName(int pos){
    return _frameName[pos];
  }

  protected FrameTable getMiddleFrameset(){
    return _middleFrameset;
  }

  public void showTopFrame(boolean value){
    _showTopFrame = value;
  }

  public void showMenuFrame(boolean value){
    _showMenuFrame = value;
  }

  public void showRightMainFrame(boolean value){
    _showRightMainFrame = value;
  }

  public void showLeftMainFrame(boolean value){
    _showLeftMainFrame = value;
  }

  public void showBottomFrame(boolean value){
    _showBottomFrame = value;
  }

  public boolean showBottomFrame(){
    return _showBottomFrame;
  }

  public boolean showRightMainFrame(){
    return _showRightMainFrame;
  }

  public boolean showLeftMainFrame(){
    return _showLeftMainFrame;
  }

  public boolean showTopFrame(){
    return _showTopFrame;
  }

  public boolean showMenuFrame(){
    return _showMenuFrame;
  }


  public String getBundleIdentifier(){
    return IW_BUNDLE_IDENTIFIER;
  }

  public void addToTop(IWBrowserCompliant obj){
    this.addToFrame(obj,POS_TOP);
  }

  public void addToMenu(IWBrowserCompliant obj){
    this.addToFrame(obj,POS_MENU);
  }

  public void addToMain(IWBrowserCompliant obj){
    this.addToFrame(obj,POS_MAIN);
  }

  public void addToLeftMain(IWBrowserCompliant obj){
    this.addToFrame(obj,POS_LEFTMAIN);
  }

  public void addToRightMain(IWBrowserCompliant obj){
    this.addToFrame(obj,POS_RIGHTMAIN);
  }

  public void addToBottom(IWBrowserCompliant obj){
    this.addToFrame(obj,POS_BOTTOM);
  }

  public void setSpanPixels(int pos, int pixels){
    _browserFrames[pos].setSpanPixels(pixels);
  }

  protected IWBrowserFrame getTopFrame(){
    return _browserFrames[POS_TOP];
  }

  protected IWBrowserFrame getMenuFrame(){
    return _browserFrames[POS_MENU];
  }

  protected IWBrowserFrame getMiddleFrame(){
    return _browserFrames[POS_MIDDLE];
  }

  protected IWBrowserFrame getMainFrame(){
    return _browserFrames[POS_MAIN];
  }

  protected IWBrowserFrame getLeftMainFrame(){
    return _browserFrames[POS_LEFTMAIN];
  }

  protected IWBrowserFrame getRightMainFrame(){
    return _browserFrames[POS_RIGHTMAIN];
  }

  protected IWBrowserFrame getBottomFrame(){
    return _browserFrames[POS_BOTTOM];
  }


  public void addIWActionListener(int pos, IWActionListener l){
    this.getFrame(this.getFrameName(pos)).addIWActionListener(l);
  }


  public void modifyFrameObject(IWContext iwc, IWFrameBusiness fb, Frame frame) throws RemoteException {
//      System.out.println("IWBrowser.modifyFrameObject");

    //if(frame.getFrameType() == Frame.OBJ || frame.getFrameType() == Frame.FRAMESET ){
      PresentationObject obj = frame.getPresentationObject();
//      System.out.println("frame.getPresentationObject() = "+obj);
      if(obj instanceof IWBrowserView){
        ((IWBrowserView)obj).setControlTarget(this.getControlframeTarget());

        IWBrowseEvent model = new IWBrowseEvent();
        model.setApplicationIdentifier(this,fb);
        model.setControlFrameTarget(getControlframeTarget());
        model.setSourceTarget(frame);
//        System.out.println("-----------------------------");
//        System.out.println("IWBrowser.frame.location: " +frame.getLocation().getLocationString());
        model.setSource(frame.getLocation());

        ((IWBrowserView)obj).setControlEventModel(model);


//        Parameter appPrm = new Parameter(IW_FRAMESET_PAGE_PARAMETER,fb.getFrameSetIdentifier(this));
//        ((IWBrowserView)obj).setApplicationParameter(appPrm);
//        Parameter ctrlFP = new Parameter(IW_FRAME_NAME_PARAMETER,getControlframeTarget());
//        ((IWBrowserView)obj).setControlFrameParameter(ctrlFP);
//        Parameter src = new Parameter(PRM_IW_BROWSE_EVENT_SOURCE, frame.getName());
//        ((IWBrowserView)obj).setSourceParamenter(src);
      }
    //}
  }

  protected void addToFrame(Frame frame, IWBrowserCompliant obj){
    if (obj instanceof PresentationObject ){

      if(frame.getPresentationObject() != null){
          ((Page)frame.getPresentationObject()).add((PresentationObject)obj);
      } else {
        if(obj instanceof Page){
          frame.setPresentationObject((Page)obj);
        } else {
          /**
           * @todo
           */
          Page page = new Page();
          page.add(obj);
          frame.setPresentationObject(page);
        }
      }
    } else {
      // unexpected Object
      System.err.println("IWBrowser.addToFrame(Frame frame, SomeName obj) - unexpected Object type");
    }
  }

  protected void addToFrame(IWBrowserCompliant obj, int pos){
    IWBrowserFrame frame = null;
    switch (pos) {
      case POS_TOP:
        frame = this.getTopFrame();
        addToFrame(frame, obj);
        break;
      case POS_MENU:
        frame = this.getMenuFrame();
        addToFrame(frame, obj);
        break;
      case POS_BOTTOM:
        frame = this.getBottomFrame();
        addToFrame(frame, obj);
        break;
      case POS_MAIN:
        frame = this.getMainFrame();
        addToFrame(frame, obj);
        break;
      case POS_LEFTMAIN:
        frame = this.getLeftMainFrame();
        addToFrame(frame, obj);
        break;
      case POS_RIGHTMAIN:
        frame = this.getRightMainFrame();
        addToFrame(frame, obj);
        break;
      default :
        // throw new Exception("Position not defined");
        break;
    }

//
//    switch (pos) {
//      case POS_TOP:
//
//        break;
//      case POS_MENU:
//
//        break;
//      case POS_MAIN:
//
//        break;
//      case POS_LEFTMAIN:
//
//        break;
//      case POS_RIGHTMAIN:
//
//        break;
//      default :
//        // throw new Exception("Position not defined");
//        break;
//    }
//
  }



  public Page getControlFramePresentation(IWUserContext iwc, boolean askForPermission){
    return getFrame(this.getFrameName(_controlPosition), iwc, askForPermission);
  }


  public void _main(IWContext iwc) throws Exception {
    //System.out.println("in _main()");

    if(_showTopFrame || _showMenuFrame || _showBottomFrame){
      if(_showTopFrame){
        this.add(this.getTopFrame());
      }

      if(_showMenuFrame){
        this.add(this.getMenuFrame());
      }

      if(_showLeftMainFrame || _showRightMainFrame ){
        if(_showLeftMainFrame){
          _middleFrameset.add(this.getLeftMainFrame());
        }

        _middleFrameset.add(this.getMainFrame());

        if(_showRightMainFrame){
          _middleFrameset.add(this.getRightMainFrame());
        }

        IWBrowserFrame bFrame = this.getMiddleFrame();
        bFrame.setPresentationObject(_middleFrameset);
        this.add(bFrame);

      } else {
        this.add(this.getMainFrame());
      }

      if(_showBottomFrame){
        this.add(this.getBottomFrame());
      }

    } else if (_showLeftMainFrame || _showRightMainFrame ) {
      this.setAttribute(_middleFrameset.getAttributes());

      if(_showLeftMainFrame){
        this.add(this.getLeftMainFrame());
      }

      this.add(this.getMainFrame());

      if(_showRightMainFrame){
        this.add(this.getRightMainFrame());
      }

    } else {
      this.add(this.getMainFrame());
    }


//    System.out.println("IWBrowser: addChangeListener ...");
    Frame ctrlFrame = this.getControlframe();

    if(ctrlFrame != null){
      PresentationObject ctrlFrameObject = ctrlFrame.getPresentationObject();
//      System.out.println("IWBrowser: addChangeListener ...0");
      if(ctrlFrameObject instanceof IWBrowseControl){
//        System.out.println("IWBrowser: addChangeListener ...1");
        ChangeListener ctrlFrameListener = ((IWBrowseControl)ctrlFrameObject).getChangeControler();
        List l = this.getAllContainedFrames();
        if(l != null){
//          System.out.println("IWBrowser: addChangeListener ...2");
          Iterator iter = l.iterator();
          while (iter.hasNext()) {
//            System.out.println("IWBrowser: addChangeListener ...3 while");
            Frame item = (Frame)iter.next();
  //          if(item != ctrlFrame){
              PresentationObject obj = item.getPresentationObject();
              if(obj instanceof StatefullPresentation){
//                System.out.println("IWBrowser: addChangeListener -> "+ctrlFrameListener);
                ((StatefullPresentation)obj).getPresentationState(iwc).addChangeListener(ctrlFrameListener);
              }
  //          }
          }
        }
      }
    }


    super._main(iwc);

  }





  protected class IWBrowserFrame extends Frame { //implements IWBrowserCompliant{

//    private boolean _isControlFrame = false;

    public IWBrowserFrame(){

    }

//    public boolean isControlFrame(){
//      return _isControlFrame;
//    }

    public void setPresentationObject(PresentationObject obj){
      if(!(obj instanceof IWBrowserCompliant)){
        // Warning
      }
      super.setPresentationObject(obj);
    }

    public void setPresentationObject(IWBrowserCompliant obj){
      if(obj instanceof PresentationObject){
        super.setPresentationObject((PresentationObject)obj);
      } else {
        //Error
      }
    }


//    public IWEventListener getListener(){return null;}

  }




//  public class TopFrame extends Window {}
//  public class MenuFrame extends Window {}
//
//  public class MainFrame extends FrameSet {
//
//    public MainFrame(){
//      this.add(LeftMain.class, frameNameMainLeft);
//      this.add(RightMain.class, frameNameMainRight);
//      this.setHorizontal();
//    }
//
//  }
//
//  public class LeftMain extends Window {}
//  public class RightMain extends Window {}

//  public void setPresentationState( IWPresentationState state ){
//    IWStateMachine stateMachine = IBOLookup.getSessionInstance(this.getIWUserContext(),IWStateMachine.class);
//    if(state instanceof IWBrowserPresentationState){
//      _presentationState = (IWBrowserPresentationState)state;
//    } else {
//      System.err.println("PresentationState not instanceof IWBrowserPresentationState");
//    }
//
//  }

  public IWPresentationState getPresentationState(IWUserContext iwuc){
    if(_presentationState == null){
      try {
        IWStateMachine stateMachine = (IWStateMachine)IBOLookup.getSessionInstance(iwuc,IWStateMachine.class);
        _presentationState = (IWBrowserPresentationState)stateMachine.getStateFor(this.getLocation(),this.getPresentationStateClass());
      }
      catch (RemoteException re) {
        throw new RuntimeException(re.getMessage());
      }
    }
    return _presentationState;
  }

  public Class getPresentationStateClass(){
    return IWBrowserPresentationState.class;
  }




}