package is.idega.idegaweb.member.isi.block.reports.presentation;

import is.idega.idegaweb.member.isi.block.reports.business.WorkReportBusiness;
import is.idega.idegaweb.member.isi.block.reports.data.WorkReport;
import is.idega.idegaweb.member.isi.block.reports.data.WorkReportDivisionBoard;

import java.rmi.RemoteException;
import java.util.ArrayList;
import java.util.Collection;
import java.util.List;

import com.idega.block.entity.business.EntityToPresentationObjectConverter;
import com.idega.block.entity.data.EntityPath;
import com.idega.block.entity.data.EntityPathValueContainer;
import com.idega.block.entity.presentation.EntityBrowser;
import com.idega.block.entity.presentation.converters.CheckBoxConverter;
import com.idega.block.entity.presentation.converters.ConverterConstants;
import com.idega.block.entity.presentation.converters.DropDownMenuConverter;
import com.idega.block.entity.presentation.converters.EditOkayButtonConverter;
import com.idega.block.entity.presentation.converters.TextEditorConverter;
import com.idega.data.EntityRepresentation;
import com.idega.idegaweb.IWApplicationContext;
import com.idega.idegaweb.IWResourceBundle;
import com.idega.presentation.Block;
import com.idega.presentation.IWContext;
import com.idega.presentation.PresentationObject;
import com.idega.presentation.Table;
import com.idega.presentation.text.Link;
import com.idega.presentation.text.Text;
import com.idega.presentation.ui.Form;
import com.idega.util.IWTimestamp;

/**
 * <p>Description: A viewer/editor for a single workreport</p>
 * <p>Copyright: Idega SoftwareCopyright (c) 2003</p>
 * <p>Company: idega Software</p>
 * @author <a href="eiki@idega.is">Eirikur Hrafnsson</a>
 * @version 1.0
 */

public class WorkReportOverViewCloseView extends Block {
	 
	protected WorkReportBusiness reportBiz;
	
  private static final String SUBMIT_SAVE_NEW_ENTRY_KEY = "submit_sv_new_entry_key";
  private static final String SUBMIT_CANCEL_KEY = "submit_cancel_key";

  private static final Integer NEW_ENTRY_ID_VALUE = new Integer(-1);
  private static final String NO_LEAGUE_VALUE = "no_league_value";
  
  private static final String ACTION_SHOW_NEW_ENTRY = "action_show_new_entry";
  
  private static final String CHECK_BOX = "checkBox";


	private static final String REPORT_YEAR = WorkReport.class.getName()+".YEAR_OF_REPORT";
	private static final String REGIONAL_UNION_NUMBER = WorkReport.class.getName()+".REG_UNI_NR";
	private static final String REGIONAL_UNION_ABBR = WorkReport.class.getName()+".REG_UNI_ABBR";

	
  private static final String GROUP_NUMBER = WorkReportDivisionBoard.class.getName()+".IC_GROUP_ID";
	private static final String MEMBER_COUNT = WorkReportDivisionBoard.class.getName()+".TOTAL_MEMBERS";
	private static final String PLAYER_COUNT = WorkReportDivisionBoard.class.getName()+".TOTAL_PLAYERS";
	private static final String LEAGUE = WorkReportDivisionBoard.class.getName()+".ISI_WR_GROUP_ID";
	private static final String HAS_NATIONAL_LEAGUE = WorkReportDivisionBoard.class.getName()+".HAS_NATIONAL_LEAGUE";
	
	
	
  
	// define path short keys and map corresponding converters
		 //year of report
		 //regional union nr
		 //regional union abbr
		 //has national league checkbox
		 //club type
		 //Leagues nr and abbr.
		 //Total members
		 //Total players
		 //is
			 //memberpart
			 //accountpart
			 //board part arrived
			 
  private boolean editable = true;
   
	public static final String IW_BUNDLE_IDENTIFIER = "is.idega.idegaweb.member.isi";


	public String getBundleIdentifier(){
		return this.IW_BUNDLE_IDENTIFIER;
	}
	
  public WorkReportOverViewCloseView() {
    super();
  }  
  
  
  public void main(IWContext iwc) throws Exception {
   
    IWResourceBundle iwrb = getResourceBundle(iwc);

    Form form = new Form();
    PresentationObject pres = getContent(iwc, iwrb, form);
    form.add(pres);
    //HACK!
    form.maintainParameter(WorkReportWindow.ACTION);
    add(form);
  }

  
  private PresentationObject getContent(IWContext iwc, IWResourceBundle resourceBundle, Form form) throws RemoteException {
  	  
    Collection divisions =  getWorkReportBusiness(iwc).getAllWorkReportDivisionBoardForWorkReportId(Integer.parseInt(iwc.getParameter("wr_id")));
   
    EntityBrowser browser = getEntityBrowser(divisions, resourceBundle, form);
    
    // put browser into a table
    Table mainTable = new Table(1,2);
    mainTable.add(browser, 1,1);    
    
    return mainTable;    
  }
  

 
  private EntityBrowser getEntityBrowser(Collection entities, IWResourceBundle resourceBundle, Form form)  {
 
    List params = new ArrayList();
    params.add(WorkReportWindow.ACTION);
    
    // define path short keys and map corresponding converters
    //year of report
    //regional union nr
    //regional union abbr
    //has national league checkbox
    //club type
    //Leagues nr and abbr.
    //Total members
    //Total players
    //is
    	//memberpart
    	//accountpart
    	//board part arrived
    
    
    
    
    Object[] columns = {
    	REPORT_YEAR,null,
			GROUP_NUMBER,null,
			MEMBER_COUNT,null,
			PLAYER_COUNT,null,
			LEAGUE,null,
			HAS_NATIONAL_LEAGUE,null,
			"back",new BackButtonConverter(resourceBundle),
		};
      
      
    EntityBrowser browser = new EntityBrowser();
    browser.setWidth(browser.HUNDRED_PERCENT);
    browser.setCellpadding(3);
    browser.setRowHeight(1,"15");
    browser.setLeadingEntity(WorkReportDivisionBoard.class);
    browser.setAcceptUserSettingsShowUserSettingsButton(false,false);
    if( entities!=null && !entities.isEmpty()){
			browser.setDefaultNumberOfRows(entities.size());
    }
    // switch off the internal form of the browser
    browser.setUseExternalForm(true);
    
    for (int i = 0; i < columns.length; i+=2) {
      String column = (String) columns[i];
      EntityToPresentationObjectConverter converter = (EntityToPresentationObjectConverter) columns[i+1];
      browser.setMandatoryColumn(i, column);
      browser.setEntityToPresentationConverter(column, converter);
    }
    
    browser.setEntities("dummy_string", entities);
    return browser;
  }
  
  

  

	protected WorkReportBusiness getWorkReportBusiness(IWApplicationContext iwc) {
		if (reportBiz == null) {
			try {
				reportBiz = (WorkReportBusiness) com.idega.business.IBOLookup.getServiceInstance(iwc, WorkReportBusiness.class);
			}
			catch (java.rmi.RemoteException rme) {
				throw new RuntimeException(rme.getMessage());
			}
		}
		return reportBiz;
	}
	
	//TODO Make the year choosable
	protected int getYear(){
		return (new IWTimestamp(IWTimestamp.getTimestampRightNow()).getYear());
	}
	

	
	class TextToLocalizedTextConverter implements EntityToPresentationObjectConverter {
		
		public PresentationObject getHeaderPresentationObject(EntityPath entityPath, EntityBrowser browser, IWContext iwc)	{
			return browser.getDefaultConverter().getHeaderPresentationObject(entityPath, browser, iwc);
		}
		
		public PresentationObject getPresentationObject(Object value, EntityPath path, EntityBrowser browser, IWContext iwc){
			
			Object obj = path.getValue((EntityRepresentation) value);
			if(obj!=null){
				String valueString = obj.toString();
				Text text = (Text) browser.getDefaultTextProxy().clone();
				text.setText(iwc.getApplicationContext().getApplication().getBundle(IW_BUNDLE_IDENTIFIER).getResourceBundle(iwc.getCurrentLocale()).getLocalizedString(valueString,valueString));               
				return text;
			}
			else{
				return new Text("");
			}
			
		}
		
	}
	


	class BackButtonConverter implements EntityToPresentationObjectConverter {
		IWResourceBundle iwrb;
		
			public BackButtonConverter(IWResourceBundle iwrb) {
				this.iwrb = iwrb;
			}
		
			public PresentationObject getHeaderPresentationObject(EntityPath entityPath, EntityBrowser browser, IWContext iwc){
				return new Text("");
			}
		
			public PresentationObject getPresentationObject(Object value, EntityPath path, EntityBrowser browser, IWContext iwc){
			
				Link backLink = new Link(iwrb.getLocalizedString("workreportmultieditor.back_button","back"));
				backLink.setAsImageButton(true);
				
				backLink.addParameter(WorkReportWindow.ACTION,WorkReportWindow.ACTION_REPORT_OVERVIEW);
				
				return backLink;
			}
		
		}
	
	

	
}
