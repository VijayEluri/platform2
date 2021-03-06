// idega 2000 - gimmi
package com.idega.block.poll.presentation;

import java.util.HashMap;
import java.util.Map;

import com.idega.block.poll.business.PollBusiness;
import com.idega.block.poll.business.PollFinder;
import com.idega.block.poll.business.PollListener;
import com.idega.block.poll.data.PollAnswer;
import com.idega.block.poll.data.PollEntity;
import com.idega.block.poll.data.PollQuestion;
import com.idega.block.text.business.TextFinder;
import com.idega.block.text.data.LocalizedText;
import com.idega.core.component.data.ICObjectInstance;
import com.idega.core.localisation.business.ICLocaleBusiness;
import com.idega.idegaweb.IWBundle;
import com.idega.idegaweb.IWResourceBundle;
import com.idega.idegaweb.block.presentation.Builderaware;
import com.idega.presentation.Block;
import com.idega.presentation.IWContext;
import com.idega.presentation.Image;
import com.idega.presentation.PresentationObject;
import com.idega.presentation.Table;
import com.idega.presentation.text.Link;
import com.idega.presentation.text.Text;
import com.idega.presentation.ui.Form;
import com.idega.presentation.ui.GenericButton;
import com.idega.presentation.ui.Parameter;
import com.idega.presentation.ui.RadioButton;
import com.idega.presentation.ui.SubmitButton;
import com.idega.util.IWTimestamp;

public class Poll extends Block implements Builderaware {

	private boolean _isAdmin;
	private int _pollID;
	private String _sAttribute = null;
	private int _iLocaleID;

	private final static String IW_BUNDLE_IDENTIFIER = "com.idega.block.poll";

	private final static String TEXT_STYLE = "TextStyle";
	private final static String LINK_STYLE = "LinkStyle";
	private final static String QUESTION_STYLE = "QuestionStyle";
	private final static String ANSWER_STYLE = "AnswerStyle";
	private final static String BUTTON_STYLE = "ButtonStyle";
	private final static String RADIO_STYLE = "RadioStyle";

	protected IWResourceBundle _iwrb;
	protected IWBundle _iwb;
	protected IWBundle _iwbPoll;

	private boolean _styles = true;

	private Table _myTable;

	public static String _prmPollID = "po.poll_id";
	public static String _prmPollCollection = "po.poll_collection";
	public static String _prmShowVotes = "po.show_votes";
	public static String _prmNumberOfPolls = "po.number_of_votes";

	private boolean _newObjInst = false;
	private boolean _newWithAttribute = false;

	private String _parameterString;
	private String _styleAttribute;
	private String _hoverStyle;
	private String _questionStyleAttribute;
	private String _votedColor;
	private String _whiteColor;

	private String _pollWidth;

	private int _numberOfShownPolls;
	private boolean _showVotes;

	private boolean _showCollection;

	private IWTimestamp _date;

	private Image _linkImage;
	private Image _linkOverImage;
	private Image _questionImage;

	private boolean _showInformation = false;

	private String _questionAlignment;
	private String _name;

	public static final int RADIO_BUTTON_VIEW = 1;
	public static final int LINK_VIEW = 2;

	private int _layout = RADIO_BUTTON_VIEW;

	public Poll() {
		setDefaultValues();
	}

	public Poll(String sAttribute) {
		this();
		this._pollID = -1;
		this._sAttribute = sAttribute;
	}

	public Poll(int pollID) {
		this();
		this._pollID = pollID;
	}

	public void main(IWContext iwc) throws Exception {
		this._iwrb = getResourceBundle(iwc);
		this._iwb = iwc.getIWMainApplication().getBundle(IW_CORE_BUNDLE_IDENTIFIER);
		this._iwbPoll = getBundle(iwc);

		this._isAdmin = iwc.hasEditPermission(this);
		this._iLocaleID = ICLocaleBusiness.getLocaleId(iwc.getCurrentLocale());
		this._parameterString = iwc.getParameter(PollBusiness._PARAMETER_POLL_VOTER);
		this._date = new IWTimestamp();

		PollEntity poll = null;

		this._myTable = new Table(1, 2);
		this._myTable.setCellpadding(0);
		this._myTable.setCellspacing(0);
		this._myTable.setBorder(0);
		this._myTable.setWidth(this._pollWidth);

		if (this._pollID <= 0) {
			String sPollID = iwc.getParameter(_prmPollID);
			if (sPollID != null) {
				this._pollID = Integer.parseInt(sPollID);
			}
			else if (getICObjectInstanceID() > 0) {
				this._pollID = PollFinder.getRelatedEntityId(getICObjectInstance());
				if (this._pollID <= 0) {
					this._newObjInst = true;
					PollBusiness.savePoll(this._pollID, -1, getICObjectInstanceID(), null);
				}
			}
		}

		if (this._newObjInst) {
			this._pollID = PollFinder.getRelatedEntityId(((com.idega.core.component.data.ICObjectInstanceHome) com.idega.data.IDOLookup.getHomeLegacy(ICObjectInstance.class)).findByPrimaryKeyLegacy(getICObjectInstanceID()));
		}

		if (this._pollID > 0) {
			poll = ((com.idega.block.poll.data.PollEntityHome) com.idega.data.IDOLookup.getHomeLegacy(PollEntity.class)).findByPrimaryKeyLegacy(this._pollID);
		}
		else if (this._sAttribute != null) {
			poll = PollFinder.getPoll(this._sAttribute);
			if (poll != null) {
				this._pollID = poll.getID();
			}
			this._newWithAttribute = true;
		}

		int row = 1;
		if (this._isAdmin) {
			this._myTable.add(getAdminPart(this._pollID, this._newObjInst, this._newWithAttribute), 1, row);
			row++;
		}

		this._myTable.add(getPoll(iwc, poll), 1, row);
		add(this._myTable);
	}

	private Link getAdminPart(int pollID, boolean newObjInst, boolean newWithAttribute) {
		Image editImage = this._iwb.getImage("shared/edit.gif");
		//Link adminLink = new Link(_iwb.getImage("shared/edit.gif"));
		Link adminLink = new Link(editImage);
		adminLink.setWindowToOpen(PollAdminWindow.class, this.getICObjectInstanceID());
		adminLink.addParameter(PollAdminWindow.prmID, pollID);
		if (newObjInst) {
			adminLink.addParameter(PollAdminWindow.prmObjInstId, getICObjectInstanceID());
		}
		else if (newWithAttribute) {
			adminLink.addParameter(PollAdminWindow.prmAttribute, this._sAttribute);
		}

		return adminLink;
	}

	private PresentationObject getPoll(IWContext iwc, PollEntity poll) {
		LocalizedText locText = null;
		PollQuestion pollQuestion = PollBusiness.getQuestion(poll);
		IWTimestamp after;
		boolean pollByDate = false;

		if (pollQuestion != null) {
			if (pollQuestion.getEndTime() != null) {
				after = new IWTimestamp(pollQuestion.getEndTime());
				if (this._date.isLaterThan(after)) {
					pollQuestion = PollBusiness.getPollByDate(poll, this._date);
					pollByDate = true;
				}
			}
		}
		else {
			pollQuestion = PollBusiness.getPollByDate(poll, this._date);
			pollByDate = true;
		}

		if (pollQuestion != null) {
			locText = TextFinder.getLocalizedText(pollQuestion, this._iLocaleID);
			if (pollByDate) {
				PollBusiness.setPollQuestion(poll, pollQuestion);
			}
		}

		PresentationObject obj = null;

		if (locText != null) {
			switch (this._layout) {
				case RADIO_BUTTON_VIEW:
					obj = getRadioButtonView(locText, pollQuestion);
					break;
				case LINK_VIEW:
					obj = getLinkView(iwc, locText, pollQuestion);
					break;
			}
			return obj;
		}
		else {
			return new Form();
		}
	}

	private Form getRadioButtonView(LocalizedText locText, PollQuestion pollQuestion) {
		//Image submitImage = _iwrb.getLocalizedImageButton("vote", "Vote");
		//Image submitImage = _iwrb.getImage("vote.gif");
		//Image olderPollsImage = _iwrb.getLocalizedImageButton("older_polls", "Older polls");
		//Image olderPollsImage = _iwrb.getImage("older_polls.gif");

		Form form = new Form();
		form.setWindowToOpen(PollResult.class);

		Table pollTable = new Table(2, 3);
		pollTable.setCellpadding(5);
		pollTable.setCellspacing(0);
		pollTable.mergeCells(1, 1, 2, 1);
		pollTable.mergeCells(1, 2, 2, 2);
		pollTable.setWidth(this._pollWidth);
		pollTable.setAlignment(1, 1, this._questionAlignment);
		pollTable.setAlignment(2, 3, "right");
		form.add(pollTable);

		Text question = getStyleText(new Text(locText.getHeadline()), QUESTION_STYLE);

		pollTable.add(question, 1, 1);

		Table radioTable = new Table();
		radioTable.setColumns(2);

		PollAnswer[] answers = PollBusiness.getAnswers(pollQuestion.getID());
		boolean hasAnswers = false;

		int row = 1;
		if (answers != null) {
			for (int a = 0; a < answers.length; a++) {
				LocalizedText locAnswerText = TextFinder.getLocalizedText(answers[a], this._iLocaleID);
				if (locAnswerText != null) {
					hasAnswers = true;
					radioTable.add(getStyleObject(new RadioButton(PollBusiness._PARAMETER_POLL_ANSWER, String.valueOf(answers[a].getID())), ANSWER_STYLE), 1, row);
					radioTable.setVerticalAlignment(1, row, Table.VERTICAL_ALIGN_TOP);
					radioTable.add(getStyleText(locAnswerText.getHeadline(), ANSWER_STYLE), 2, row++);
				}
			}
		}

		if (hasAnswers) {
			pollTable.add(radioTable, 1, 2);
		}

		GenericButton collectionLink = (GenericButton) getStyleObject(new GenericButton("", this._iwrb.getLocalizedString("older_polls", "Older polls")), BUTTON_STYLE);
		collectionLink.setWindowToOpen(PollResult.class);
		collectionLink.addParameterToWindow(Poll._prmPollID, this._pollID);
		collectionLink.addParameterToWindow(Poll._prmPollCollection, PollBusiness._PARAMETER_TRUE);
		collectionLink.addParameterToWindow(Poll._prmNumberOfPolls, this._numberOfShownPolls);
		if (this._showVotes) {
			collectionLink.addParameterToWindow(Poll._prmShowVotes, PollBusiness._PARAMETER_TRUE);
		}
		else {
			collectionLink.addParameterToWindow(Poll._prmShowVotes, PollBusiness._PARAMETER_FALSE);
		}

		if (this._showCollection) {
			pollTable.add(collectionLink, 1, 3);
		}
		pollTable.add(getStyleObject(new SubmitButton(this._iwrb.getLocalizedString("vote", "Vote")), BUTTON_STYLE), 2, 3);
		pollTable.add(new Parameter(PollBusiness._PARAMETER_POLL_VOTER, PollBusiness._PARAMETER_TRUE));
		pollTable.add(new Parameter(PollBusiness._PARAMETER_POLL_QUESTION, Integer.toString(pollQuestion.getID())));
		if (this._showVotes) {
			pollTable.add(new Parameter(Poll._prmShowVotes, PollBusiness._PARAMETER_TRUE));
		}
		else {
			pollTable.add(new Parameter(Poll._prmShowVotes, PollBusiness._PARAMETER_FALSE));
		}

		return form;
	}

	private Table getLinkView(IWContext iwc, LocalizedText locText, PollQuestion pollQuestion) {
		//Image olderPollsImage = _iwrb.getImage("older_polls.gif");
		//Image olderPollsImage = _iwrb.getLocalizedImageButton("older_polls", "Older polls");

		Table pollTable = new Table();
		pollTable.setCellpadding(3);
		pollTable.setCellspacing(0);
		pollTable.setWidth(this._pollWidth);
		pollTable.setAlignment(1, 1, this._questionAlignment);
		int pollRow = 1;

		Text question = getStyleText(locText.getHeadline(), QUESTION_STYLE);

		if (this._questionImage != null) {
			Table questionTable = new Table(3, 1);
			questionTable.setCellpadding(0);
			questionTable.setCellspacing(0);
			questionTable.setVerticalAlignment(1, 1, "top");
			this._questionImage.setVerticalSpacing(2);
			questionTable.add(this._questionImage, 1, 1);
			questionTable.setWidth(2, 1, "4");
			questionTable.add(question, 3, 1);

			pollTable.setAlignment(1, pollRow, this._questionAlignment);
			pollTable.add(questionTable, 1, pollRow);
		}
		else {
			pollTable.add(question, 1, pollRow);
		}
		pollRow++;

		Table answerTable = new Table();
		answerTable.setCellspacing(0);
		answerTable.setCellpadding(0);
		answerTable.setWidth("100%");
		PollAnswer[] answers = PollBusiness.getAnswers(pollQuestion.getID());

		boolean canVote = true;
		if (iwc.getParameter(PollBusiness._PARAMETER_POLL_VOTER) != null) {
			canVote = false;
		}
		if (canVote) {
			canVote = PollBusiness.canVote(iwc, pollQuestion.getID());
		}

		if (canVote) {
			boolean hasAnswers = false;

			if (answers != null) {
				int row = 1;

				for (int a = 0; a < answers.length; a++) {
					LocalizedText locAnswerText = TextFinder.getLocalizedText(answers[a], this._iLocaleID);
					if (locAnswerText != null) {
						hasAnswers = true;

						Link answerLink = getStyleLink(locAnswerText.getHeadline(), LINK_STYLE);
						answerLink.addParameter(PollBusiness._PARAMETER_POLL_QUESTION, pollQuestion.getID());
						answerLink.addParameter(PollBusiness._PARAMETER_POLL_ANSWER, answers[a].getID());
						answerLink.addParameter(PollBusiness._PARAMETER_POLL_VOTER, PollBusiness._PARAMETER_TRUE);
						answerLink.addParameter(PollBusiness._PARAMETER_CLOSE, PollBusiness._PARAMETER_TRUE);
						answerLink.setEventListener(PollListener.class);
						if (this._name != null) {
							answerLink.setStyle(this._name);
						}

						if (this._linkImage != null) {
							Table imageTable = new Table(3, 1);
							imageTable.setCellspacing(0);
							imageTable.setCellpadding(0);

							Image image = new Image(this._linkImage.getMediaURL(iwc));

							image.setVerticalSpacing(3);
							if (this._linkOverImage != null) {
								image.setOverImage(this._linkOverImage);
								this._linkOverImage.setVerticalSpacing(3);
								answerLink.setMarkupAttribute("onMouseOver", "swapImage('" + image.getName() + "','','" + this._linkOverImage.getMediaURL(iwc) + "',1)");
								answerLink.setMarkupAttribute("onMouseOut", "swapImgRestore()");
							}

							imageTable.add(image, 1, 1);
							imageTable.setVerticalAlignment(1, 1, "top");
							imageTable.setWidth(2, "8");
							imageTable.add(answerLink, 3, 1);
							answerTable.add(imageTable, 1, row);
						}
						else {
							answerTable.add(answerLink, 1, row);
						}
						row++;
						answerTable.setHeight(row, "4");
						row++;
					}
				}
			}

			if (hasAnswers) {
				pollTable.add(answerTable, 1, pollRow);
				pollRow++;
			}
		}
		else {
			int total = 0;
			int row = 0;

			if (answers != null) {
				if (answers.length > 0) {
					for (int i = 0; i < answers.length; i++) {
						total += answers[i].getHits();
					}
					for (int i = 0; i < answers.length; i++) {
						LocalizedText answerLocText = TextFinder.getLocalizedText(answers[i], this._iLocaleID);
						if (answerLocText != null) {
							++row;

							float percent = 0;
							if (answers[i].getHits() > 0) {
								percent = ((float) answers[i].getHits() / (float) total) * 100;
							}

							Text answerText = getStyleText(answerLocText.getHeadline(), ANSWER_STYLE);
							if (this._showVotes || this._isAdmin) {
								answerText.addToText(" (" + Integer.toString(answers[i].getHits()) + ")");
							}
							Text percentText = getStyleText(Text.NON_BREAKING_SPACE + com.idega.util.text.TextSoap.decimalFormat(percent, 1) + "%", TEXT_STYLE);

							answerTable.mergeCells(1, row, 2, row);
							answerTable.add(answerText, 1, row);
							row++;

							Table table = new Table();
							table.setCellpadding(0);
							table.setCellspacing(1);
							table.setWidth("100%");
							table.setColor("#000000");

							Image transImage = Table.getTransparentCell(iwc);
							transImage.setHeight(10);
							transImage.setWidth("100%");
							Image transImage2 = Table.getTransparentCell(iwc);
							transImage2.setHeight(10);
							transImage2.setWidth("100%");
							if (percent > 0) {
								table.setColor(1, 1, this._votedColor);
								table.add(transImage, 1, 1);
								table.setWidth(1, 1, Integer.toString((int) percent) + "%");
								if (percent < 100) {
									table.setColor(2, 1, this._whiteColor);
									table.add(transImage2, 2, 1);
									table.setWidth(2, 1, Integer.toString(100 - (int) percent) + "%");
								}
							}
							else if (percent <= 0) {
								table.setColor(1, 1, this._whiteColor);
								table.setWidth(1, 1, "100%");
								table.add(transImage2, 1, 1);
							}

							answerTable.add(table, 1, row);
							answerTable.add(percentText, 2, row);
							answerTable.setWidth(1, row, "100%");

							row++;
							answerTable.setHeight(row, "6");
						}
					}
				}
				answerTable.setWidth(1, "100%");
				pollTable.add(answerTable, 1, pollRow);
				pollRow++;

				String information = PollBusiness.getLocalizedInformation(pollQuestion.getID(), this._iLocaleID);
				if (information != null && this._showInformation) {
					Text informationText = getStyleText(information, TEXT_STYLE);
					pollTable.add(informationText, 1, pollRow);
					pollRow++;
				}
			}
		}

		GenericButton collectionLink = (GenericButton) getStyleObject(new GenericButton("", this._iwrb.getLocalizedString("older_polls", "Older polls")), BUTTON_STYLE);
		collectionLink.setWindowToOpen(PollResult.class);
		collectionLink.addParameterToWindow(Poll._prmPollID, this._pollID);
		collectionLink.addParameterToWindow(Poll._prmPollCollection, PollBusiness._PARAMETER_TRUE);
		collectionLink.addParameterToWindow(Poll._prmNumberOfPolls, this._numberOfShownPolls);
		if (this._showVotes) {
			collectionLink.addParameterToWindow(Poll._prmShowVotes, PollBusiness._PARAMETER_TRUE);
		}
		else {
			collectionLink.addParameterToWindow(Poll._prmShowVotes, PollBusiness._PARAMETER_FALSE);
		}

		if (this._showCollection) {
			pollTable.add(collectionLink, 1, pollRow);
		}

		return pollTable;
	}

	private void setDefaultValues() {
		this._pollWidth = "100%";
		this._numberOfShownPolls = 3;
		this._showVotes = true;
		this._showCollection = true;
		this._questionAlignment = "left";
		this._pollID = -1;
		this._votedColor = "#104584";
		this._whiteColor = "#FFFFFF";
	}

	public boolean deleteBlock(int ICObjectInstanceId) {
		PollEntity poll = PollFinder.getObjectInstanceFromID(ICObjectInstanceId);
		return PollBusiness.deletePoll(poll);
	}

	public String getBundleIdentifier() {
		return IW_BUNDLE_IDENTIFIER;
	}

	public void setWidth(int pollWidth) {
		this._pollWidth = Integer.toString(pollWidth);
	}

	public void setWidth(String pollWidth) {
		this._pollWidth = pollWidth;
	}

	public void setStyle(String style) {
		this._styleAttribute = style;
	}

	public void setHoverStyle(String hoverStyle) {
		this._hoverStyle = hoverStyle;
	}

	public void setQuestionStyle(String style) {
		this._questionStyleAttribute = style;
	}

	public void setNumberOfShownPolls(int numberOfShownPolls) {
		this._numberOfShownPolls = numberOfShownPolls;
	}

	public void showVotes(boolean showVotes) {
		this._showVotes = showVotes;
	}

	public void showCollection(boolean collection) {
		this._showCollection = collection;
	}

	public void setQuestionImage(Image image) {
		this._questionImage = image;
	}

	public void setLinkImage(Image image) {
		this._linkImage = image;
	}

	public void setLinkOverImage(Image image) {
		this._linkOverImage = image;
	}

	public void setLayout(int layout) {
		this._layout = layout;
	}

	public void setShowInformation(boolean showInformation) {
		this._showInformation = showInformation;
	}

	public void setQuestionAlignment(String alignment) {
		this._questionAlignment = alignment;
	}

	public void setVotedColor(String color) {
		this._votedColor = color;
	}

	public void setWhiteColor(String color) {
		this._whiteColor = color;
	}

	public Object clone() {
		Poll obj = null;
		try {
			obj = (Poll) super.clone();

			if (this._myTable != null) {
				obj._myTable = (Table) this._myTable.clone();
			}
			if (this._linkImage != null) {
				obj._linkImage = (Image) this._linkImage.clone();
			}
			if (this._linkOverImage != null) {
				obj._linkOverImage = (Image) this._linkOverImage.clone();
			}
			if (this._questionImage != null) {
				obj._questionImage = (Image) this._questionImage.clone();
			}
		}
		catch (Exception ex) {
			ex.printStackTrace(System.err);
		}
		return obj;
	}

	protected String getCacheState(IWContext iwc, String cacheStatePrefix) {
		String returnString = iwc.getParameter(PollBusiness._PARAMETER_POLL_VOTER);

		if (returnString == null) {
			returnString = "";
		}
		else {
			returnString = "";//minimise the number of states cached
			setCacheable(false);//do this when you want to be sure to go through
													// main(iwc) and no cache.
			invalidateCache(iwc, cacheStatePrefix + Boolean.TRUE);
			invalidateCache(iwc, cacheStatePrefix + Boolean.FALSE);
		}

		try {
			this._pollID = PollFinder.getRelatedEntityId(getICObjectInstance());
		}
		catch (Exception ex) {
			ex.printStackTrace();
		}

		String canVote = String.valueOf(PollBusiness.canVote(iwc, this._pollID));
		if (canVote.equals("false")) {
			returnString = "";
		}

		return cacheStatePrefix + canVote + returnString;
	}
	
	/* (non-Javadoc)
	 * @see com.idega.presentation.Block#getStyleNames()
	 */
	public Map getStyleNames() {
		Map map = new HashMap();
		map.put(TEXT_STYLE, "font-family: Verdana, Arial, Helvetica, sans-serif; font-size: 8pt; text-decoration: none;");
		map.put(QUESTION_STYLE, "font-family: Verdana, Arial, Helvetica, sans-serif; font-size: 11pt; font-weight: bold");
		map.put(ANSWER_STYLE, "font-family: Verdana, Arial, Helvetica, sans-serif; font-size: 8pt; text-decoration: none;");
		map.put(BUTTON_STYLE, "font-family: Verdana, Arial, Helvetica, sans-serif; font-size: 8pt; border: 1px solid #000000;");
		map.put(RADIO_STYLE, "font-family: Verdana, Arial, Helvetica, sans-serif; font-size: 8pt; width: 12px; height: 12px;");
		map.put(LINK_STYLE, "font-family: Verdana, Arial, Helvetica, sans-serif; font-size: 8pt; text-decoration: none;");
		map.put(LINK_STYLE+":hover", "font-family: Verdana, Arial, Helvetica, sans-serif; font-size: 8pt; text-decoration: none;");
		
		return map;
	}
}