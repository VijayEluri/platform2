package is.idega.idegaweb.golf.block.image.presentation;

import com.idega.presentation.Block;
import com.idega.presentation.IWContext;


public class ImageEditor extends Block{
private boolean refresh = false;
private boolean showAll = true;

  public void main(IWContext modinfo)throws Exception{
    String refreshing = (String) modinfo.getSessionAttribute("refresh");
    String sRefresh = modinfo.getParameter("refresh");
    ImageBrowser browser = new ImageBrowser();
    browser.setShowAll(showAll);

    if( (sRefresh!=null) || refresh || (refreshing!=null) ) browser.refresh();

    add(browser);
  }

  public void refresh(){
    this.refresh=true;
  }

  public void setShowAll(boolean showAll){
    this.showAll = showAll;
  }
}
