/*
 * Created on 30.12.2003
 *
 * To change the template for this generated file go to
 * Window>Preferences>Java>Code Generation>Code and Comments
 */
package com.idega.block.poll.presentation;


import java.util.HashMap;
import java.util.Iterator;
import java.util.Vector;

import com.idega.core.localisation.business.ICLocaleBusiness;
import com.idega.idegaweb.IWBundle;
import com.idega.idegaweb.IWResourceBundle;
import com.idega.presentation.Block;
import com.idega.presentation.IWContext;
import com.idega.presentation.PresentationObject;
import com.idega.presentation.Table;
import com.idega.presentation.text.Text;
import com.idega.presentation.ui.CheckBox;
import com.idega.presentation.ui.DropdownMenu;
import com.idega.presentation.ui.Fieldset;
import com.idega.presentation.ui.Form;
import com.idega.presentation.ui.GenericButton;
import com.idega.presentation.ui.Parameter;
import com.idega.presentation.ui.SubmitButton;
import com.idega.presentation.ui.TextArea;
import com.idega.presentation.ui.TextInput;
import com.idega.util.IWTimestamp;


/**
 * Title:		SurveyEditor
 * Description:
 * Copyright:	Copyright (c) 2003
 * Company:		idega Software
 * @author		2003 - idega team - <br><a href="mailto:gummi@idega.is">Gudmundur Agust Saemundsson</a><br>
 * @version		1.0
 */
public class SurveyEditor extends Block {


	private int _iLocaleID;

	private final static String IW_BUNDLE_IDENTIFIER = "com.idega.block.poll";
	protected IWResourceBundle _iwrb;
	protected IWBundle _iwb;
	protected IWBundle _iwbPoll;
	protected IWContext _iwc;
	
	private IWTimestamp _date;

	
	public final static String STYLE = "font-family:arial; font-size:8pt; color:#000000; text-align: justify; border: 1 solid #000000;";
	public final static String STYLE_2 = "font-family:arial; font-size:8pt; color:#000000; text-align: justify;";
	public final static String STYLE_BUTTON = "font-family:arial; font-size:8pt; color:#000000; text-align: center; border: 1 solid #000000;";

	public static final String PRM_ANSWERTYPE = "mfpo_ans_type";
	public final static String PRM_MAINTAIN_SUFFIX = "_mt";
	public final static int ANSWERTYPE_SINGLE_CHOICE = 1;
	public final static int ANSWERTYPE_MULTI_CHOICE = 2;
	public final static int ANSWERTYPE_TEXTAREA = 3;
	
	private int _numberOfQuestions = 6;
	public static final String PRM_NUMBER_OF_QUESTIONS_TO_ADD = "mfpo_noqta";
	public static final String PRM_NUMBER_OF_QUESTIONS = "mfpo_noq";
	
	public static final String PRM_CURRENT_STATE = "mfpo_curr_state";
	public static final String PRM_GOTO_STATE = "mfpo_goto_state";
	public static final int STATE_ONE = 1;
	public static final int STATE_TWO = 2;
	private int _state = STATE_ONE;
	private int _lastState = STATE_ONE;
	
	public static final String PRM_NUMBER_OF_ANSWERS_TO_ADD = "mfpo_noata";
	public static final String PRM_NUMBER_OF_ANSWERS = "mfpo_noa";
	public static final int _defaultNumberOfAnswers = 3;
	
	public static final String ADD_QUESTION_PRM = "add_question";
	public static final String ADD_ANSWER_PRM = "add_answer";
	
	public static final String PRM_QUESTION = "mfpo_q";
	public static final String PRM_ANSWER = "mfpo_a";
	public static final String PRM_ADD_TEXT_INPUT = "mfpo_ati";
	
	private Vector prmVector = new Vector();
	private HashMap _prmValues = new HashMap();
	


	/**
	 * 
	 */
	public SurveyEditor(int instanceID) {
		super();
		setICObjectInstanceID(instanceID);
		prmVector = new Vector();
	}
	
	public String getBundleIdentifier() {
		return IW_BUNDLE_IDENTIFIER;
	}
	
	public void setInitialNumberOfQuestions(int number){
		if(number > 0 ){
			_numberOfQuestions = number;
		}
	}
	
	public void initializeInMain(IWContext iwc) throws Exception {
		super.initializeInMain(iwc);
		_iwc = iwc;
		_iwrb = getResourceBundle(iwc);
		_iwb = iwc.getApplication().getBundle(IW_CORE_BUNDLE_IDENTIFIER);
		_iwbPoll = getBundle(iwc);
		_iLocaleID = ICLocaleBusiness.getLocaleId(iwc.getCurrentLocale());
		_date = new IWTimestamp();
		
		processParameters(iwc);
	}
	
	public void main(IWContext iwc) throws Exception {
		Form myForm = new Form();
		this.add(myForm);
		//Edit		
//		if(this.hasEditPermission()){
			switch (_state) {
				case STATE_ONE :
					myForm.add(getStateOne());
					break;
				case STATE_TWO :
					myForm.add(getStateTwo());
					break;
				default :
					myForm.add(getDefaultState());
					break;
			}			
//		} else {
//			//store information temporary while logging in
//		}
		
		//save to DB
		if(true){
			
		}
		
		
		beforeParameterListIsAdded();
		for (Iterator iter = prmVector.iterator(); iter.hasNext();) {
			myForm.add((Parameter)iter.next());
		}

	}

	/**
	 * 
	 */
	private void beforeParameterListIsAdded() {
		//Number of questions parameter
		prmVector.add(new Parameter(PRM_NUMBER_OF_QUESTIONS,String.valueOf(_numberOfQuestions)));
	}

	private void processParameters(IWContext iwc) {
		processStatePRM(iwc);
		processQuestionAndAnswerPRMs(iwc);
		processNumberOfQuestionsPRM(iwc);
		
		
		if(true){ //while in Edit mode
			maintainModePRM(iwc);
		}	
	}
	
	/**
	 * @param iwc
	 */
	private void processQuestionAndAnswerPRMs(IWContext iwc) {
		//questions
		processParameterValues(iwc,PRM_QUESTION,true);
		//answers
		String[] questions = iwc.getParameterValues(PRM_QUESTION);
		if(questions != null){
			for (int i = 1; i <= questions.length; i++) {
				processParameterValues(iwc,PRM_ANSWER+i,true);
				processParameterValues(iwc,PRM_ADD_TEXT_INPUT+i,true);
			}
		}
		//answertypes
		processParameterValues(iwc,PRM_ANSWERTYPE,true);
		//number of answers
		processParameterValues(iwc,PRM_NUMBER_OF_ANSWERS,true);
	}
	
	private void processParameterValues(IWContext iwc, String prmName, boolean maintain){
		String[] values = iwc.getParameterValues(prmName);
		if(values != null && values.length > 0){
			_prmValues.put(prmName,values);
			if(maintain){
				for (int i = 0; i < values.length; i++) {
					if(values[i] != null && !"".equals(values[i])){
						prmVector.add(new Parameter(prmName+PRM_MAINTAIN_SUFFIX,values[i]));
					}
				}
			}
		} else {
			values = iwc.getParameterValues(prmName+PRM_MAINTAIN_SUFFIX);
			if(values != null && values.length > 0){
				_prmValues.put(prmName,values);
				if(maintain){
					for (int i = 0; i < values.length; i++) {
						if(values[i] != null && !"".equals(values[i])){
							prmVector.add(new Parameter(prmName+PRM_MAINTAIN_SUFFIX,values[i]));
						}
					}
				}
			}	
		}
	}

	/**
	 * @param iwc
	 */
	private void maintainModePRM(IWContext iwc) {
		String smode = iwc.getParameter(Survey.PRM_SWITCHTO_MODE);
		String mode = iwc.getParameter(Survey.PRM_MODE);
		if( smode != null){
			prmVector.add(new Parameter(Survey.PRM_MODE,smode));
		} else if(mode != null){
			prmVector.add(new Parameter(Survey.PRM_MODE,mode));
		}
	}

	/**
	 * @param iwc
	 */
	private void processNumberOfQuestionsPRM(IWContext iwc) {
		
		String NumberOfQuestions = iwc.getParameter(PRM_NUMBER_OF_QUESTIONS);
		try {
			_numberOfQuestions = Integer.parseInt(NumberOfQuestions);
		} catch (NumberFormatException e1) {
			//
		}
		

		if((_lastState == STATE_ONE && _state==STATE_TWO)){ //|| (_lastState == STATE_TWO && _state==STATE_ONE)){
			String[] questions = (String[])_prmValues.get(PRM_QUESTION);
			if(questions != null && questions.length != 0){
				_numberOfQuestions =1;
				for (int i = 1; i < questions.length; i++) {
					if(questions[i] != null && !"".equals(questions[i])){
						_numberOfQuestions++;
					}
				}
			} else {
				//Warning
				//System.err.println(this.getClassName()+"[Warning]: Trying to go forward without defining any question");
			}
		} else {

			String alterNumberOfQuestions = iwc.getParameter(ADD_QUESTION_PRM);
			if(alterNumberOfQuestions!=null){
				String NumberOfQuestionsToAdd = iwc.getParameter(PRM_NUMBER_OF_QUESTIONS_TO_ADD);
				try {
					_numberOfQuestions += Integer.parseInt(NumberOfQuestionsToAdd);
				} catch (NumberFormatException e) {
					//
				}
			}

		}
		
	}
	
	/**
	 * @param iwc
	 */
	private void processStatePRM(IWContext iwc) {
		String state = iwc.getParameter(PRM_CURRENT_STATE);
		try {
			_state = Integer.parseInt(state);
			_lastState = _state;
		} catch (NumberFormatException e1) {
			//
		}
		
		
		String gotoState = iwc.getParameter(PRM_GOTO_STATE);
		try {
			_state = Integer.parseInt(gotoState);
		} catch (NumberFormatException e) {
			//
		}
		
		
		prmVector.add(new Parameter(PRM_CURRENT_STATE,String.valueOf(_state)));
	}

	private PresentationObject getStateOne(){
		Table stateOne = new Table();
		int rowIndex = 0;
		
		//stateOne.setBorder(1);
		
		//stateOne.add(_iwrb.getLocalizedString("multifunctionalpolleditor.instructions_step_one",""));
		
		String[] questions = (String[])_prmValues.get(PRM_QUESTION);
		String[] selectedAnsTypes = (String[])_prmValues.get(PRM_ANSWERTYPE);
		String[] numberOfAnswers = (String[])_prmValues.get(PRM_NUMBER_OF_ANSWERS);		
		
		for(int i = 1; i <= _numberOfQuestions; i++){
			String question = null;
			String selectedAnsType = null;
			String numberOfAns = null;
			if(questions != null && questions.length >= i){
				question = questions[i-1];
				selectedAnsType = selectedAnsTypes[i-1];
				numberOfAns = numberOfAnswers[i-1];
			}
			stateOne.add(getQuestionFieldset(i,question,selectedAnsType,numberOfAns),1,++rowIndex);
			
		}
		
		stateOne.add(getAddQuestionFieldset(),1,++rowIndex);
		
	

		SubmitButton saveButton = new SubmitButton("save",_iwrb.getLocalizedString("save","  Save  "));
		setStyle(saveButton);
		stateOne.add(saveButton,1,++rowIndex);
		stateOne.setRowAlignment(rowIndex,Table.HORIZONTAL_ALIGN_RIGHT);
		
		stateOne.add(Text.NON_BREAKING_SPACE,1,rowIndex);
		
		SubmitButton forwardButton = new SubmitButton(_iwrb.getLocalizedString("forward","  Forward  "),PRM_GOTO_STATE,String.valueOf(STATE_TWO));
		setStyle(forwardButton);
		stateOne.add(forwardButton,1,rowIndex);
		//stateOne.setRowAlignment(rowIndex,Table.HORIZONTAL_ALIGN_RIGHT);
		

		
		
		
		return stateOne;
	}
	
	private PresentationObject getStateTwo(){
		Table stateTwo = new Table();
		int rowIndex = 0;
		
		//stateOne.setBorder(1);
		
		//stateOne.add(_iwrb.getLocalizedString("multifunctionalpolleditor.instructions_step_one",""));
		
		String[] questions = (String[])_prmValues.get(PRM_QUESTION);
		String[] answertypes = (String[])_prmValues.get(PRM_ANSWERTYPE);
		String[] numberOfAnswers = (String[])_prmValues.get(PRM_NUMBER_OF_ANSWERS);
		if(questions != null && questions.length != 0){
			_numberOfQuestions =0;
			for (int i = 0; i < questions.length; i++) {
				String question = questions[i];
				if(question!=null && !"".equals(question)){
					++_numberOfQuestions;
					int answertype = Integer.parseInt(answertypes[i]);
					int noAnswers = Integer.parseInt(numberOfAnswers[i]);
					stateTwo.add(getAnswerFieldset(_numberOfQuestions,question,answertype,noAnswers),1,++rowIndex);
				}
			}
		}
		
		
		
		//stateOne.add(getAddQuestionFieldset(),1,++rowIndex);
		
	
		SubmitButton backButton = new SubmitButton(_iwrb.getLocalizedString("back","  Back  "),PRM_GOTO_STATE,String.valueOf(STATE_ONE));
		setStyle(backButton);
		stateTwo.add(backButton,1,++rowIndex);
		stateTwo.setRowAlignment(rowIndex,Table.HORIZONTAL_ALIGN_RIGHT);
		
		stateTwo.add(Text.NON_BREAKING_SPACE,1,rowIndex);
		
		SubmitButton saveButton = new SubmitButton("save",_iwrb.getLocalizedString("save","  Save  "));
		setStyle(saveButton);
		stateTwo.add(saveButton,1,rowIndex);
		//stateOne.setRowAlignment(rowIndex,Table.HORIZONTAL_ALIGN_RIGHT);
		
		stateTwo.add(Text.NON_BREAKING_SPACE,1,rowIndex);
		
//		SubmitButton OKButton = new SubmitButton(_iwrb.getLocalizedString("finish","  Finish  "),Survey.PRM_SWITCHTO_MODE,String.valueOf(Survey.MODE_POLL));
//		setStyle(OKButton);
//		stateTwo.add(OKButton,1,rowIndex);
		

		
		
		
		return stateTwo;
	}
	

	
	private PresentationObject getAddQuestionFieldset(){
		Table t = new Table(2,1);
		
		SubmitButton addButton = new SubmitButton(ADD_QUESTION_PRM,_iwrb.getLocalizedString("add_questions_to_form","  Add  "));
		setStyle(addButton);
		
		DropdownMenu amount = new DropdownMenu(PRM_NUMBER_OF_QUESTIONS_TO_ADD);
		setStyle(amount);
		for(int i = 1; i <= 15; i++){
			amount.addMenuElement(i,String.valueOf(i));
		}
		amount.setSelectedElement(1);
		
		
		t.add(addButton,1,1);
		t.add(amount,2,1);
		
		
		return t;
	}
	
	private PresentationObject getQuestionFieldset(int no, String question, String selectedAnsType,String numberOfAns){
		Fieldset fs = new Fieldset();
		fs.setName(_iwrb.getLocalizedString("Question","Question")+" "+no);
		Table qt = new Table(2,3);
		qt.setVerticalAlignment(1,1,Table.VERTICAL_ALIGN_TOP);
		
		//qt.setBorder(1);
		
		//todo put id from db-table in hiddeninput for update
		
		qt.add(getLabel(_iwrb.getLocalizedString("Question","Question")),1,1);

		qt.add(getQuestionTextArea(PRM_QUESTION,question),2,1);
		qt.add(getLabel(_iwrb.getLocalizedString("Answer_type","Answer type")),1,2);
		qt.add(getAnswerTypeDropdownMenu(PRM_ANSWERTYPE,selectedAnsType),2,2);
		qt.add(getLabel(_iwrb.getLocalizedString("Number_of_answers","Number of answers")),1,3);
		qt.add(getNumberOfAnswersDropdownMenu(PRM_NUMBER_OF_ANSWERS,numberOfAns),2,3);
		
		
		
		fs.add(qt);
		return fs;
	}
	
	private PresentationObject getAnswerFieldset(int no, String questionText, int answerType, int numberOfAnswers){
		Fieldset fs = new Fieldset();
		fs.setName(_iwrb.getLocalizedString("Question","Question")+" "+no);
		Table qt = new Table();
		qt.setVerticalAlignment(1,1,Table.VERTICAL_ALIGN_TOP);
		
		//qt.setBorder(1);
		
		//todo put id from db-table in hiddeninput for update
		
		qt.add(getLabel(_iwrb.getLocalizedString("Question","Question")),1,1);
		PresentationObject question = getQuestionTextArea(PRM_QUESTION,questionText);
		qt.add(question,2,1);
		
	
		qt.add(getLabel(_iwrb.getLocalizedString("Answers","Answers")),1,3);

		switch (answerType) {
			case ANSWERTYPE_SINGLE_CHOICE :
					
				//break;
			case ANSWERTYPE_MULTI_CHOICE :
				qt.add(getLabel(_iwrb.getLocalizedString("Answer_type","Answer type")),1,2);
				qt.add(getListAnswerTypeDropdownMenu(PRM_ANSWERTYPE,answerType),2,2);

				String[] answers = (String[])_prmValues.get(PRM_ANSWER+no);
				String[] useTextInput = (String[])_prmValues.get(PRM_ADD_TEXT_INPUT+no);
				for(int i = 0; i < numberOfAnswers; i++){
					String ans = null;
					String check = null;
					if(answers != null && answers.length > i){
						ans = answers[i];
					}
					if(useTextInput != null && useTextInput.length > i){
						check = answers[i];
					}
					
					qt.add(getLabel(String.valueOf(i+1)),2,i+3);
					qt.add(getAnswerTextInput(PRM_ANSWER+no,ans),2,i+3);
//					qt.add(Text.NON_BREAKING_SPACE);
//					qt.add(getAddTextInputCheckBox(PRM_ADD_TEXT_INPUT+no,check),2,i+3);
				}
				break;
			case ANSWERTYPE_TEXTAREA :
				qt.add(getAnswerTextArea("ans_ta",null,true),2,2);
				prmVector.add(new Parameter(PRM_ANSWERTYPE,String.valueOf(answerType)));

				break;
		}
		
				
		
		
		fs.add(qt);
		if(answerType != ANSWERTYPE_TEXTAREA){
			fs.add(getAddAnswerFieldSet(no));
		}
		return fs;
	}
	
	/**
	 * @param string
	 * @param check
	 * @return
	 */
	private PresentationObject getAddTextInputCheckBox(String name, String check) {
		CheckBox box = new CheckBox(name);
		if(check != null){
			box.setChecked(true);
		}
		//setStyle(box);
		return box;
	}

	/**
	 * @param string
	 * @param ans
	 * @return
	 */
	private PresentationObject getAnswerTextInput(String name, String displayText) {
		TextInput i = new TextInput(name);
		i.setSize(40);
		setStyle(i);
		if(displayText != null){
			i.setValue(displayText);
		}
		return i;
	}

	private PresentationObject getAddAnswerFieldSet(int questionNumber){
		Table t = new Table(2,1);
		
		SubmitButton addButton = new SubmitButton(_iwrb.getLocalizedString("add_answers_to_question","  Add  "),ADD_ANSWER_PRM,"_"+questionNumber);
		setStyle(addButton);
		
		DropdownMenu amount = new DropdownMenu(PRM_NUMBER_OF_ANSWERS_TO_ADD+"_"+questionNumber);
		setStyle(amount);
		for(int i = 1; i <= 15; i++){
			amount.addMenuElement(i,String.valueOf(i));
		}
		amount.setSelectedElement(1);
		
		
		t.add(addButton,1,1);
		t.add(amount,2,1);
		
		
		return t;
	}


	
	/**
	 * @param string
	 * @return
	 */
	private DropdownMenu getNumberOfAnswersDropdownMenu(String name,String value) {
		DropdownMenu d = new DropdownMenu(name);
		setStyle(d);
		for(int i = 1; i <= 15; i++){
			d.addMenuElement(i,String.valueOf(i));
		}
		if(value != null){
			d.setSelectedElement(value);
		} else {
			d.setSelectedElement(_defaultNumberOfAnswers);
		}
		
		return d;
	}

	/**
	 * @param string
	 * @return
	 */
	private DropdownMenu getAnswerTypeDropdownMenu(String name, String value) {
		DropdownMenu d = new DropdownMenu(name);
		setStyle(d);
		d.addMenuElement(ANSWERTYPE_SINGLE_CHOICE,_iwrb.getLocalizedString("Radio_group","Radio group (single-choice)"));
		d.addMenuElement(ANSWERTYPE_MULTI_CHOICE,_iwrb.getLocalizedString("Checkboxes","Checkboxes  (multi-choice)"));
		d.addMenuElement(ANSWERTYPE_TEXTAREA,_iwrb.getLocalizedString("Textarea","Textarea"));
		if(value != null){
			d.setSelectedElement(value);
		}
		return d;
	}
	
	/**
	 * @param string
	 * @return
	 */
	private DropdownMenu getListAnswerTypeDropdownMenu(String name, int value) {
		DropdownMenu d = new DropdownMenu(name);
		setStyle(d);
		d.addMenuElement(ANSWERTYPE_SINGLE_CHOICE,_iwrb.getLocalizedString("Radio_group","Radio group (single-choice)"));
		d.addMenuElement(ANSWERTYPE_MULTI_CHOICE,_iwrb.getLocalizedString("Checkboxes","Checkboxes  (multi-choice)"));
		if(value > 0){
			d.setSelectedElement(value);
		}
		return d;
	}

	/**
	 * @return
	 */
	private PresentationObject getQuestionTextArea(String name, String displayText) {
		TextArea t = new TextArea(name);
		if(displayText!=null){
			t.setValue(displayText);
		}
		setStyle(t);
		t.setColumns(50);
		t.setRows(3);
		return t;
	}
	
	
	 private PresentationObject getAnswerTextArea(String name, String displayText, boolean disabled) {
		 TextArea t = new TextArea(name);
		 if(displayText!=null){
			 t.setValue(displayText);
		 }
		 setStyle(t);
		 t.setColumns(40);
		 t.setRows(4);
		 t.setDisabled(disabled);
		 return t;
	 }

	/**
	 * @param string
	 * @return
	 */
	private Text getLabel(String string) {
		Text t = new Text(string+": ");
		setStyle(t);
		return t;
	}
	
	public void setStyle(PresentationObject obj){
		if(obj instanceof Text){
			this.setStyle((Text)obj);
		} else if(obj instanceof GenericButton) {
			obj.setMarkupAttribute("style",STYLE_BUTTON);
		} else {
			obj.setMarkupAttribute("style",STYLE);
		}
	}

	public void setStyle(Text obj){
		obj.setMarkupAttribute("style",STYLE_2);
	}

	public synchronized Object clone(){
		//TMP
		return new SurveyEditor(this.getICObjectInstanceID());
	}

}
