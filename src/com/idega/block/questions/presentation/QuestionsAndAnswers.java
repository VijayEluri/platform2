package com.idega.block.questions.presentation;
import java.rmi.RemoteException;
import java.util.ArrayList;
import java.util.Collection;
import java.util.HashMap;
import java.util.Iterator;
import java.util.Locale;
import java.util.Map;
import java.util.Vector;

import javax.ejb.FinderException;

import com.idega.block.category.data.ICCategory;
import com.idega.block.category.presentation.CategoryBlock;
import com.idega.block.questions.business.QAndALayoutHandler;
import com.idega.block.questions.business.QuestionsService;
import com.idega.block.questions.data.Question;
import com.idega.block.text.business.ContentHelper;
import com.idega.block.text.business.TextFinder;
import com.idega.business.IBOLookup;
import com.idega.idegaweb.IWBundle;
import com.idega.idegaweb.IWResourceBundle;
import com.idega.presentation.IWContext;
import com.idega.presentation.Image;
import com.idega.presentation.PresentationObject;
import com.idega.presentation.Table;
import com.idega.presentation.text.Anchor;
import com.idega.presentation.text.AnchorLink;
import com.idega.presentation.text.Link;
import com.idega.presentation.text.Text;
import com.idega.presentation.ui.DropdownMenu;
import com.idega.presentation.ui.Form;
import com.idega.presentation.ui.HelpButton;
import com.idega.presentation.ui.HiddenInput;
import com.idega.presentation.ui.SubmitButton;

/**
 * 
 * <p>Company: idegaweb </p>
 * @author aron
 * 
 *
 */
public class QuestionsAndAnswers extends CategoryBlock {
	
	private IWResourceBundle iwrb;
	private IWBundle iwb,core;
	private String IW_BUNDLE_IDENTIFIER = "com.idega.block.questions";
	private boolean isAdmin = false;
	private QuestionsService questionsService;
	private Locale currentLocale;
	private int row = 1;
	private int qaRow = 1;
	private String prmViewCategory = "qa_view_cat_id";
	private String valViewCategory = null;
	
	private boolean showAll = true;
	private boolean showAllCategories =true;
	private boolean showQuestionTitle = true;
	private boolean showQuestionBody = true;
	private boolean showAnswerTitle = true;
	private boolean showAnswerBody = true;
	private boolean showQuestionList = true;
	private boolean showQuestionListCount = true;
	private boolean showDeletedQuestions = true;
	private boolean showDeleteButton = true;
	private boolean showMoveButtons = true;
	private boolean showHomeButton = true;
	
	private String questionPrefixText = "Q:";
	private String answerPrefixText = "A:";
	private Image questionPrefixImage = null;
	private Image answerPrefixImage = null;
	
	public final static String STYLENAME_Q_TITLE = "QuestionTitle";
	public final static String STYLENAME_Q_BODY = "QuestionBody";
	public final static String STYLENAME_A_TITLE = "AnswerTitle";
	public final static String STYLENAME_A_BODY = "AnswerBody";
	public final static String STYLENAME_Q_PREFIX = "QuestionPrefix";
	public final static String STYLENAME_A_PREFIX = "AnswerPrefix";
	public final static String STYLENAME_C_TITLE = "CategoryTitle";
	public final static String STYLENAME_Q_COUNT = "QuestionCount";
	
	public final static String DEFAULT_Q_TITLE = "font-style:normal;color:#000000;font-size:11px;font-family:Verdana,Arial,Helvetica,sans-serif;font-weight:bold;";
	public final static String DEFAULT_Q_BODY = "font-weight:plain;";
	public final static String DEFAULT_A_TITLE = "font-style:normal;color:#000000;font-size:11px;font-family:Verdana,Arial,Helvetica,sans-serif;font-weight:bold;";
	public final static String DEFAULT_A_BODY = "font-weight:plain;";
	public final static String DEFAULT_Q_PREFIX = "font-style:normal;color:#000000;font-size:13px;font-family:Verdana,Arial,Helvetica,sans-serif;font-weight:bold;";
	public final static String DEFAULT_A_PREFIX = "font-style:normal;color:#000000;font-size:13px;font-family:Verdana,Arial,Helvetica,sans-serif;font-weight:bold;";;
	public final static String DEFAULT_C_TITLE = "font-style:normal;color:#000000;font-size:11px;font-family:Verdana,Arial,Helvetica,sans-serif;font-weight:bold;";
	public final static String DEFAULT_Q_COUNT = "font-weight:plain;";
	
	private int layout = QAndALayoutHandler.DEFAULT_LAYOUT;
	
	
	public QuestionsAndAnswers(){
		setAutoCreate(false);
	}
	
	public String getBundleIdentifier(){
		return this.IW_BUNDLE_IDENTIFIER;
	}
	
	/**
	 * @see com.idega.block.category.presentation.CategoryBlock#getCategoryType()
	 */
	public String getCategoryType() {
		return "QA";
	}
	
	/**
	 * @see com.idega.block.category.presentation.CategoryBlock#getMultible()
	 */
	public boolean getMultible() {
		return true;
	}
	
	public void main(IWContext iwc)throws RemoteException{
		//debugParameters(iwc);
		this.iwb = getBundle(iwc);
		this.iwrb = getResourceBundle(iwc);
		this.core = iwc.getIWMainApplication().getCoreBundle();
		this.isAdmin = iwc.hasEditPermission(this);
		
		this.questionsService = (QuestionsService)IBOLookup.getServiceInstance(iwc,QuestionsService.class);
		this.currentLocale = iwc.getCurrentLocale();
		
		
		if(this.layout==QAndALayoutHandler.SINGLE_RANDOM_LAYOUT){
		    if(this.isAdmin){
		        
	      		add(getAdminPart(iwc));
	      		add(Text.getBreak());
		    }
		    add(getRandomQAndA(iwc));
		    
		}
		else{
			this.valViewCategory = iwc.getParameter(this.prmViewCategory);
			// form processing
			processForm(iwc);
		
			Table T = new Table();
			int row = 1;
			if(this.isAdmin){
	      		T.add(getAdminPart(iwc),1,row++);
			}
			Table QandATable = new Table();
			Table QATable =(Table) getQATable(iwc,QandATable);
			T.add(QATable,1,row++);
			if(this.showAll) {
				T.add(QandATable,1,row++);
			}
			
			add(T);
		}
	}
	
	public PresentationObject getAdminPart(IWContext iwc){
		Table T = new Table(3,1);
		T.setCellpadding(0);
	    T.setCellpadding(0);
	    T.setWidth("100%");
	    T.setWidth(2,"100%");
	    T.add(getCategoryLink(this.core.getImage("/shared/detach.gif")), 1, 1);
	    String helpTitle = this.iwrb.getLocalizedString("help_title","Q & A");
	    String helpText = this.iwrb.getLocalizedString("help_text","If the blank page icon appears you have to save changes with the save button, else changes are saved in the editor window ( when the open icon appears)");
	    HelpButton help = new HelpButton(helpTitle,helpText);
	    T.add(help,3,1);
		return T;	
	}
	
	 private Link getCategoryLink(Image image) {
	    Link L = getCategoryLink();
	    L.setImage(image);
	    return L;
	  }

	public PresentationObject getRandomQAndA(IWContext iwc)throws RemoteException{
	    Table T = new Table(),QandATable = new Table();
	    Question randomQuestion = this.questionsService.getRandomQuestion(this.getCategoryIds());
	    if(randomQuestion!=null){
	        createQuestionInfo(iwc,randomQuestion,null,null,-1,1,T,QandATable,null);
	    }
	    return QandATable;
	}
	
	public PresentationObject getQATable(IWContext iwc,Table QandATable)throws RemoteException{
		Table QTable = new Table();
		Collection categories = null;
		
		if(!this.showAllCategories && this.valViewCategory!=null){
			try {
				ICCategory viewCat = getCategoryHome().findByPrimaryKey(new Integer(this.valViewCategory));
				categories = new Vector(1);
				categories.add(viewCat);
			}
			catch (FinderException e) {
			}
		}
		else{
		 	categories = getCategories();
		}
		if(categories!=null && !categories.isEmpty()){
			Iterator iter = categories.iterator();
			fillQuestionTree(iwc,iter,QTable,QandATable);
		}
		else{
			QTable.add(this.iwrb.getLocalizedString("no_category","Please create a category"),1,this.row);
		}
		return QTable;
	}
	
	private void fillQuestionTree(IWContext iwc,Iterator iter,Table T,Table QandATable)throws RemoteException{
		 
		while(iter.hasNext()){
			ICCategory cat = (ICCategory) iter.next();
			Integer catID = (Integer)cat.getPrimaryKey();
			boolean headerAdded = false;
			int headerRow = this.row;
			this.row++;
			if(cat.isLeaf()){
				if(this.showAllCategories || (!this.showAllCategories && catID.toString().equals(this.valViewCategory))){
					Table questionsTable =(Table) getCategoryQuestions(iwc,cat,QandATable);
					if(this.showQuestionList) {
						T.add(questionsTable,2,this.row++);
					}
					if(this.isAdmin){
						Question nullQuestion=null;
						T.add(getQuestionForm(iwc,catID,nullQuestion,(Question)null,(Question)null),2,this.row++);
					}
				}
				if(!this.showAllCategories){
					Link headerLink = new Link(getStyleText( cat.getName(this.currentLocale),STYLENAME_C_TITLE));
					headerLink.addParameter(this.prmViewCategory,catID.toString());
					T.add(headerLink,1,headerRow);
					headerAdded = true;
				}
			}		
			else{
				
				fillQuestionTree(iwc,cat.getChildrenIterator(),T,QandATable);
			}
			
			if(!headerAdded){
				AnchorLink anc = new AnchorLink(getStyleText(cat.getName(this.currentLocale),STYLENAME_C_TITLE),"Cat"+catID.toString());
				T.add(anc,1,headerRow);
				T.add(new Anchor("bc"+catID.toString()),1,headerRow);
			}
			this.row++;
		}
	}
	
	private PresentationObject getQuestionForm(IWContext iwc,Integer cat,Question question,Question previous,Question latter)throws RemoteException{
		Form form = new Form();
		Table table = new Table();
		
		Integer categoryID = cat!=null?cat:new Integer(question.getCategoryId());
		table.add(new HiddenInput("save_cat",categoryID.toString()));
		boolean isForOneQuestion = true;
		Link QandAEditorLink = new Link();
		QandAEditorLink.setWindowToOpen(QandAEditorWindow.class);
		QandAEditorLink.addParameter(QandAEditorWindow.PRM_CATEGORY,categoryID.toString());
		
		Integer entID = null;
		if(question!=null){
			entID = (Integer)question.getPrimaryKey();
		}
		else {
			isForOneQuestion = false;
		}
		
		if(entID!=null && entID.intValue()>0){
			QandAEditorLink.setImage(this.iwb.getImage("open.gif",this.iwrb.getLocalizedString("button_edit_question","Edit question")));
			QandAEditorLink.addParameter(QandAEditorWindow.PRM_QA_ID,entID.toString());
		}
		else{
			QandAEditorLink.setImage(this.iwb.getImage("new.gif",this.iwrb.getLocalizedString("button_create_question","Create question")));
			
		}
		
		table.add(QandAEditorLink,2,1);
		
		if(entID!=null && this.showDeleteButton){
			Link trash = new Link(this.iwb.getImage("trashcan_empty.gif",this.iwrb.getLocalizedString("button_invalidate","Trashcan")));
			trash.addParameter("ent_id",entID.toString());
			trash.addParameter("trash_quest","true");
			Link delete = new Link(this.iwb.getImage("delete.gif",this.iwrb.getLocalizedString("button_remove","Remove from list")));
			delete.addParameter("ent_id",entID.toString());
			delete.addParameter("delete_quest","true");
			table.add(trash,5,1);
			table.add(delete,5,1);
		}
		
		if(this.showMoveButtons){
			if(previous!=null){
				Link up = new Link(this.iwb.getImage("up.gif",this.iwrb.getLocalizedString("button_up","Move up")));
				up.addParameter("move_up","true");
				up.addParameter("ent_id",entID.toString());
				up.addParameter("swap_up_quest_id"+entID,previous.getPrimaryKey().toString());
				table.add(up,5,1);
			}
			if(latter!=null){
				Link down = new Link(this.iwb.getImage("down.gif",this.iwrb.getLocalizedString("button_down","Move down")));
				down.addParameter("move_down","true");
				down.addParameter("swap_down_quest_id"+entID,latter.getPrimaryKey().toString());
				down.addParameter("ent_id",entID.toString());
				table.add(down,6,1 );
				
			}
		}
		if(!isForOneQuestion && this.isAdmin && this.showDeletedQuestions){
			table.add(getInvalidQuestions("inv_quest"+categoryID.toString(),categoryID.intValue()),5,1);
			
			table.add(new SubmitButton(this.iwb.getImage("validate.gif",this.iwrb.getLocalizedString("button_validate","Validate  selected")),"validate_quest"),6,1);
		}
		if(!this.showAllCategories && this.valViewCategory!=null) {
			table.add(new HiddenInput(this.prmViewCategory,this.valViewCategory));
		}
		
		form.add(table);
		return form;
	
	}
	

		
	private DropdownMenu getInvalidQuestions(String  name,int categoryId)throws RemoteException{
		DropdownMenu drop = new DropdownMenu(name);
		drop.addMenuElementFirst("-1",this.iwrb.getLocalizedString("deleted_questions","Deleted questions"));
		try{
		Collection questions = this.questionsService.getQuestionHome().findAllInvalidByCategory(categoryId);
		Iterator iter = questions.iterator();
			while(iter.hasNext()){
				Question quest = (Question)iter.next();
				ContentHelper helper = TextFinder.getContentHelper(quest.getQuestionID(),this.currentLocale);
				if(helper.getLocalizedText()!=null){
					String headline = helper.getLocalizedText().getHeadline();
					if(headline.length()>20) {
						headline = headline.substring(0,20)+"...";
					}
					drop.addMenuElement(quest.getPrimaryKey().toString(),headline);
				}
			}
		
		}catch(FinderException fex){
			throw new RemoteException(fex.getMessage());
		}
		return drop;
	}
	
	private void processForm(IWContext iwc)throws RemoteException{
		
			 int cat_id = -1;
			if(iwc.isParameterSet("save_cat")) {
				cat_id = Integer.parseInt(iwc.getParameter("save_cat"));
			}
			//String questionId = iwc.getParameter("quest"+cat_id);
			//String answerId = iwc.getParameter("ans"+cat_id);
			String entityId = iwc.getParameter("ent_id");
			
			//int q_id = -1, a_id = -1,
			int ent_id = -1;
			/*try {
				if(questionId!=null)
				q_id = Integer.parseInt(questionId);
			}catch (Exception e) {}
			try {
				if(answerId!=null)
				a_id = Integer.parseInt(answerId);	
			}catch (Exception e) {}
			*/
			try {
				if(entityId!=null) {
					ent_id = Integer.parseInt(entityId);
				}	
			}catch (Exception e) {}
			
			
			/*if(q_id>0 && iwc.isParameterSet("save_quest")){// && iwc.getParameter("save_quest").equals("true") ){
				questionsService.storeQuestion(ent_id,q_id,a_id,cat_id);
			}
			else*/ 
			if(ent_id>0 && iwc.isParameterSet("trash_quest")){// && iwc.getParameter("delete_quest").equals("true")){
				this.questionsService.invalidateQuestion(ent_id);
			}
			else if(ent_id>0 && iwc.isParameterSet("delete_quest")){// && iwc.getParameter("delete_quest").equals("true")){
				this.questionsService.removeQuestion(ent_id);
			}
			else if(iwc.isParameterSet("validate_quest")){// && iwc.getParameter("validate_quest").equals("true")){
				int inv_quest_id = Integer.parseInt(iwc.getParameter("inv_quest"+cat_id));
				if(inv_quest_id>0) {
					this.questionsService.validateQuestion(inv_quest_id);
				}
			}
			else if(ent_id>0 && iwc.isParameterSet("move_up") ){//&& iwc.getParameter("move_up").equals("true")){
				int swap_quest_id = Integer.parseInt(iwc.getParameter("swap_up_quest_id"+entityId));
				if(swap_quest_id >0) {
					this.questionsService.swapSequences(ent_id,swap_quest_id);
				}
			}
			else if(ent_id>0 && iwc.isParameterSet("move_down") ){//&& iwc.getParameter("move_down").equals("true")){
				int swap_quest_id = Integer.parseInt(iwc.getParameter("swap_down_quest_id"+entityId));
				if(swap_quest_id >0) {
					this.questionsService.swapSequences(ent_id,swap_quest_id);
				}
			}
		//}
	}
	
	private PresentationObject getCategoryQuestions(IWContext iwc,ICCategory cat,Table QandATable)throws RemoteException{
		Table T = new Table();
		int row=1;
		Collection questions = new Vector();
		Integer catID = (Integer)cat.getPrimaryKey();
		try{
			questions = this.questionsService.getQuestionHome().findAllByCategory(catID.intValue());
		}catch(FinderException ex){}
		Question quest,previous = null,latter= null;
		int QuestCount = 1;
		if(this.showAll && this.showAllCategories){
			createCategoryTitle(cat,QandATable);
		}
		ArrayList list = new ArrayList(questions);
		int size = list.size();
		for (int i = 0; i < list.size(); i++) {
			quest = (Question) list.get(i);
			if(i >0) {
				previous = (Question) list.get(i-1);
			}
			if(i < (size-1)) {
				latter = (Question) list.get(i+1);
			}
			if(i == size-1) {
				latter = null;
			}
			createQuestionInfo(iwc,quest,previous,latter,QuestCount,row,T,QandATable,catID);
			QuestCount++;
			row++;
		}
		return T;
	
	}
	
	private void createQuestionInfo(IWContext iwc,Question quest,Question previous,Question latter, int QuestCount,int row,Table T,Table QandATable,Integer cat)throws RemoteException{
		if(quest.getQuestionID() > 0){
			ContentHelper helper = TextFinder.getContentHelper(quest.getQuestionID(),this.currentLocale);
			String headline = helper.getLocalizedText()!=null? helper.getLocalizedText().getHeadline():"";
				if(this.showQuestionListCount) {
					T.add(getStyleText((QuestCount)+".",STYLENAME_Q_COUNT),1,row);
				}
				if(this.showAll){
					AnchorLink anc = new AnchorLink(getStyleText(headline,STYLENAME_Q_TITLE),"Q"+quest.getPrimaryKey().toString());
					if(!this.showAllCategories && this.valViewCategory!=null) {
						anc.addParameter(this.prmViewCategory,this.valViewCategory);
					}
					T.add(anc,2,row);
					createQuestionsAndAnswers(iwc,helper,quest,cat,QandATable);
				}
				else{
					T.add(getStyleText(headline,STYLENAME_Q_TITLE),2,row);
				}
				if(this.isAdmin) {
					T.add(getQuestionForm(iwc,cat,quest,previous,latter),3,row);
				}
			
		}
	}
	
	public void createQuestionsAndAnswers(IWContext iwc,ContentHelper quest,Question question,Integer cat,Table QandATable)throws RemoteException{
		Table T = new Table();
		int row = 1;
		Anchor anc = new Anchor("Q"+question.getPrimaryKey().toString());
		T.add(anc,1,1);
		if(this.questionPrefixImage==null) {
			T.add(getStyleText(this.questionPrefixText,STYLENAME_Q_PREFIX),1,row);
		}
		else {
			T.add(this.questionPrefixImage,1,row);
		}
			
		String headline = quest.getLocalizedText()!=null?quest.getLocalizedText().getHeadline():"";
		String body = quest.getLocalizedText()!=null?quest.getLocalizedText().getBody():"";
		
		if(this.showQuestionTitle && headline!=null && headline.length()>0) {
			T.add(getStyleText(headline,STYLENAME_Q_TITLE),2,row++);
		}
		if(this.showQuestionBody && body!=null && body.length()>0){
			T.add(getStyleText(body,STYLENAME_Q_BODY),2,row);
		}
		row++;
		if(this.answerPrefixImage==null) {
			T.add(getStyleText(this.answerPrefixText,STYLENAME_A_PREFIX),1,row);
		}
		else {
			T.add(this.answerPrefixImage,1,row);
		}
		if(question.getAnswerID()>0){
			ContentHelper ans = TextFinder.getContentHelper(question.getAnswerID(),this.currentLocale);
			String aheadline = ans.getLocalizedText()!=null?ans.getLocalizedText().getHeadline():"";
			String abody = ans.getLocalizedText()!=null?ans.getLocalizedText().getBody():"";
			if(this.showAnswerTitle && aheadline!=null && aheadline.length()>0) {
				T.add(getStyleText(aheadline,STYLENAME_A_TITLE),2,row++);
			}
			if(this.showAnswerBody && abody!=null && abody.length()>0){
				T.add(getStyleText(abody,STYLENAME_A_BODY),2,row++);
				if(cat!=null && this.showHomeButton && this.showAllCategories) {
					T.add(new AnchorLink(this.iwb.getImage("home.gif"),"bc"+cat.toString()),1,row);
				}
			}
			
		}
		T.setColumnVerticalAlignment(1,Table.VERTICAL_ALIGN_TOP);
		QandATable.add(T,1,this.qaRow++);
		if(this.isAdmin && !this.showQuestionList) {
			QandATable.add(getQuestionForm(iwc,cat,question,(Question)null,(Question)null),1,this.qaRow++);
		}
	}
	
	public Map getStyleNames() {
		HashMap map = new HashMap();
		String[] styleNames = { 
			STYLENAME_Q_TITLE ,
			STYLENAME_Q_BODY ,
			STYLENAME_A_TITLE,
			STYLENAME_A_BODY ,
			STYLENAME_Q_PREFIX ,
			STYLENAME_A_PREFIX ,
			STYLENAME_C_TITLE ,
			STYLENAME_Q_COUNT
		
		};
		String[] styleValues = { 
			DEFAULT_Q_TITLE ,
			DEFAULT_Q_BODY,
			DEFAULT_A_TITLE,
			DEFAULT_A_BODY ,
			DEFAULT_Q_PREFIX ,
			DEFAULT_A_PREFIX ,
			DEFAULT_C_TITLE ,
			DEFAULT_Q_COUNT
			
	 	};

		for (int a = 0; a < styleNames.length; a++) {
			map.put(styleNames[a], styleValues[a]);
		}

		return map;
	}

	
	private void createCategoryTitle(ICCategory cat,Table QandATable)throws RemoteException{
		Anchor anc = new Anchor(getStyleText(cat.getName(this.currentLocale),STYLENAME_C_TITLE),"Cat"+cat.getPrimaryKey().toString());
		QandATable.add(anc,1,this.qaRow++);
	}
	
	public void setShowAll(boolean showAll){
		this.showAll = showAll;
	}
	
	public void setShowOnlyOneCategory(boolean showOnlyOneCategory){
		this.showAllCategories = !showOnlyOneCategory;
	}
	
	public void setShowAllCategories(boolean showAllCategories){
		this.showAllCategories = showAllCategories;
	}
	public void setShowQuestionTitle (boolean showQuestionTitle){
		this.showQuestionTitle = showQuestionTitle;
	}
	public void setShowQuestionBody(boolean showQuestionBody){
		this.showQuestionBody=showQuestionBody;
	}
	
	public void setShowAnswerTitle(boolean showAnswerTitle){
		this.showAnswerTitle = showAnswerTitle;
	}
	
	public void setShowAnswerBody(boolean showAnswerBody){
		this.showAnswerBody = showAnswerBody;
	}
	
	public void setShowQuestionList(boolean showQuestionList){
		this.showQuestionList = showQuestionList;
	}
	
	public void setShowDeleteButton(boolean showDeleteButton){
		this.showDeleteButton = showDeleteButton;
	}
	
	public void setShowMoveButtons(boolean showMoveButtons){
		this.showMoveButtons = showMoveButtons;
	}
	
	public void setShowHomeButton(boolean showHomeButton){
		this.showHomeButton = showHomeButton;
	}
	
	public void setShowDeletedQuestions(boolean showDeletedQuestions){
		this.showDeletedQuestions = showDeletedQuestions;
	}
	
	 /**
     * @param showCategoryNames The showCategoryNames to set.
     */
    public void setShowCategoryNames(boolean showCategoryNames) {
        //this.showCategoryNames = showCategoryNames;
    }
	
	public void setQuestionPrefixText(String questionPrefixText){
		this.questionPrefixText = questionPrefixText;
	}
	
	public void setAnswerPrefixText(String answerPrefixText){
		this.answerPrefixText = answerPrefixText;
	}
		
	public void setQuestionPrefixImage(Image questionPrefixImage){
		this.questionPrefixImage = questionPrefixImage;
	}
	
	public void setAnswerPrefixImage(Image answerPrefixImage){
		this.answerPrefixImage= answerPrefixImage;
	}
	
	public synchronized Object clone() {
	    QuestionsAndAnswers obj = null;
	    try {
	      obj = (QuestionsAndAnswers)super.clone();
	
	      obj.answerPrefixImage = this.answerPrefixImage;
	      obj.questionPrefixImage = this.questionPrefixImage;
	
	    }
	    catch(Exception ex) {
	      ex.printStackTrace(System.err);
	    }
    	return obj;
  }
	
    /**
     * @param layout The layout to set.
     */
    public void setLayout(int layout) {
        this.layout = layout;
    }
}
