/*
 * Created on 4.3.2004
 */
package is.idega.idegaweb.golf.startingtime.presentation;

import java.io.IOException;
import java.sql.SQLException;
import java.util.StringTokenizer;
import java.util.Vector;

import javax.ejb.FinderException;

import com.idega.presentation.Image;
import com.idega.presentation.IWContext;
import com.idega.presentation.Page;
import com.idega.presentation.Table;
import com.idega.presentation.ui.DropdownMenu;
import com.idega.presentation.ui.Form;
import com.idega.presentation.ui.HiddenInput;
import com.idega.presentation.ui.SelectionBox;
import com.idega.presentation.ui.SubmitButton;
import com.idega.presentation.ui.TextInput;
import com.idega.presentation.text.Link;
import com.idega.presentation.text.Text;
import is.idega.idegaweb.golf.GolfField;
import is.idega.idegaweb.golf.SqlTime;
import is.idega.idegaweb.golf.TableInfo;
import is.idega.idegaweb.golf.entity.Field;
import is.idega.idegaweb.golf.entity.StartingtimeFieldConfig;
import is.idega.idegaweb.golf.entity.Union;
import is.idega.idegaweb.golf.presentation.GolfBlock;
import is.idega.idegaweb.golf.startingtime.business.TeeTimeBusiness;
import is.idega.idegaweb.golf.startingtime.data.TeeTime;
import com.idega.util.IWCalendar;
import com.idega.util.IWTimestamp;

/**
 * @author laddi
 */
public class StartingTime extends GolfBlock {

	private TeeTimeBusiness service = new TeeTimeBusiness();
	private String COLOR1 = "#FFFFFF"; //  #336661
	private String COLOR2 = "#6E9173"; //  #CDDFD1
	private String COLOR3 = "#ADC9B0"; //  #ADC9B0
	private String COLOR4 = "#CDDFD1"; //  #6E9173
	private String COLOR5 = "#336661";
	private String COLOR6 = "#FFFFFF";
	private String COLOR7 = "#336661";
	
	static final String 	PRM_ACTION = "GO_ST_ACTON";
	static final int ACTION_NO_ACTION = 0;
	static final int  ACTION_GO_TO_TIMETABLE = 1;
	int _action = ACTION_NO_ACTION;
	static final String PRM_MODE = "GO_ST_MODE";
	static final int MODE_MENU = 0;
	static final int MODE_TIMETABLE = 1;
	int _mode = MODE_MENU;

	
	public void processActionAndModeParameters(IWContext iwc) throws Exception {
		
		String mode = iwc.getParameter(PRM_MODE);
		if(mode!=null) {
			try {
				_mode = Integer.parseInt(mode);
			} catch(NumberFormatException e) {
				_mode=MODE_MENU;
				System.err.println("[Warning]: "+this.getClassName()+": unsupported mode parameter value "+mode);
			}
		} else {
			_mode = MODE_MENU;
		}
	
		
		String action = iwc.getParameter(PRM_ACTION);
		if(action!=null) {
			try {
				_action = Integer.parseInt(action);
			} catch(NumberFormatException e) {
				_action=ACTION_NO_ACTION;
				System.err.println("[Warning]: "+this.getClassName()+": unsupported action parameter value "+action);
			}
		} else {
			_action = ACTION_NO_ACTION;
		}
	}
		
	public void main(IWContext iwc) throws Exception {
		processActionAndModeParameters(iwc);
		
		switch (_action) {
			case ACTION_GO_TO_TIMETABLE :
				_mode = MODE_TIMETABLE;
				break;
			default : // _action = ACTION_NO_ACTION;
				break;
		}
		
		switch (_mode) {
		case MODE_TIMETABLE:
			this.add(new StartingTimeTable());
			break;
	
		default: // _mode = MODE_MENU;
			mainStartingTimeSearch(iwc);
			break;
		}
		
	}
	
	public void mainStartingTimeSearch(IWContext modinfo) throws Exception {
		IWTimestamp funcDate = new IWTimestamp();

		try {
			removeUnionIdSessionAttribute(modinfo);

			if (modinfo.getSessionAttribute("side_num") == null && modinfo.getParameter("side") == null)
				modinfo.setSessionAttribute("side_num", "0");
			else if (modinfo.getParameter("side") != null)
				modinfo.setSessionAttribute("side_num", modinfo.getParameter("side"));

			if (modinfo.getSessionAttribute("when") == null && modinfo.getParameter("hvenaer") == null)
				modinfo.setSessionAttribute("when", "0");
			else if (modinfo.getParameter("hvenaer") != null)
				modinfo.setSessionAttribute("when", modinfo.getParameter("hvenaer"));

			if (modinfo.getSessionAttribute("field_id") == null && modinfo.getParameter("hvar") == null)
				modinfo.setSessionAttribute("field_id", "1");
			else if (modinfo.getParameter("hvar") != null)
				modinfo.setSessionAttribute("field_id", modinfo.getParameter("hvar"));

			if (modinfo.getSessionAttribute("date") == null && modinfo.getRequest().getParameter("day") == null)
				modinfo.setSessionAttribute("date", new IWTimestamp().toSQLDateString());
			else if (modinfo.getRequest().getParameter("day") != null)
				modinfo.setSessionAttribute("date", modinfo.getRequest().getParameter("day"));

			modinfo.setDefaultFontSize("2");

		}
		catch (Exception E) {
			E.printStackTrace();
		}

		Form myForm = new Form();
		GolfField myField = new GolfField();
		GolfField Today = new GolfField();
		TableInfo myTableInfo = new TableInfo();
		Page myPage = new Page(getResourceBundle().getLocalizedString("start.search.register_tee_times", "Register tee times"));

		myPage.setMarginWidth(5);
		myPage.setMarginHeight(5);
		myPage.setLeftMargin(5);
		myPage.setTopMargin(5);
		myPage.setAlinkColor(COLOR1);
		myPage.setVlinkColor(COLOR1);

		Table myTable = new Table(1, 1);
		myTable.setCellpadding(0);
		myTable.setCellspacing(0);
		myTable.setVerticalAlignment("top");
		myTable.setAlignment("center");

		Vector Groups = new Vector();

		if ((String) modinfo.getParameter("results") == null) {
			boolean search = ((String) modinfo.getParameter("state") != null && ((String) modinfo.getParameter("state")).equals("search"));
			Table startTable = new Table(1, 2);
			startTable.setHeight(1, "15");
			startTable.setCellpadding(0);
			startTable.setCellspacing(0);
			startTable.add(getEntryLink(modinfo, !search), 1, 1);
			startTable.add(getSearchLink(modinfo, search), 1, 1);
			startTable.add(Text.emptyString(), 1, 1);
			if (search) {
				startTable.add(searchEntry(modinfo, funcDate), 1, 2);
			}
			else {
				startTable.add(enterClub(modinfo), 1, 2);
			}
			myTable.add(startTable, 1, 1);

			//setVerticalAlignment("top");
			add(myTable);
		}
		else {
			boolean search = ((String) modinfo.getParameter("state") != null && ((String) modinfo.getParameter("state")).equals("search"));
			Table startTable = new Table(1, 2);
			startTable.setHeight(1, "15");
			startTable.setCellpadding(0);
			startTable.setCellspacing(0);
			startTable.add(getEntryLink(modinfo, !search), 1, 1);
			startTable.add(getSearchLink(modinfo, search), 1, 1);
			startTable.add(Text.emptyString(), 1, 1);
			if (search) {
				startTable.add(searchEntry(modinfo, funcDate), 1, 2);
			}
			else {
				startTable.add(enterClub(modinfo), 1, 2);
			}
			myTable.add(startTable, 1, 1);

			//setVerticalAlignment("top");
			add(myTable);
		}

		if ((String) modinfo.getParameter("results") != null) {
			if (modinfo.getParameterValues("fields") != null && modinfo.getParameter("fjoldi") != null && !modinfo.getParameter("fjoldi").equals("")) {
				if (numericString(modinfo.getParameter("fjoldi"))) {
					String[] myParameters = modinfo.getParameterValues("fields");
					int fields;
					for (int i = 0; i < myParameters.length; i++) {
						fields = Integer.parseInt(myParameters[i]);
						myField = getFieldInfo(fields, modinfo.getParameter("date").toString());
						Today = getFieldInfo(fields, funcDate.toSQLDateString());
						try {
							Groups = search(funcDate, modinfo, myField, Today, Integer.parseInt(modinfo.getParameter("fjoldi").toString()), modinfo.getParameter("date").toString(), modinfo.getParameter("ftime").toString(), modinfo.getParameter("ltime").toString(), 0, 36);
							myTable.add(Results(modinfo, Groups, myField, modinfo.getParameter("date").toString(), 8), 1, 1);
						}
						catch (Exception E) {
							if (E.getMessage().equals("Error1")) {
								Table Error1 = new Table(1, 1);
								Error1.setWidth("381");
								Error1.setHeight(1, "21");
								//Error1.setBorder(1);
								Error1.setColumnAlignment(1, "center");
								Error1.add(this.getSmallErrorText(getResourceBundle().getLocalizedString("start.search.error1", "_")), 1, 1);
								Error1.setRowColor(1, COLOR2);
								myTable.add(Error1, 1, 1);
								break;
							}
							if (E.getMessage().equals("Error2")) {
								Table Error2 = new Table(1, 1);
								Error2.setWidth("381");
								Error2.setHeight(1, "21");
								//Error2.setBorder(1);
								Error2.setColumnAlignment(1, "center");
								Error2.add(this.getSmallErrorText(getResourceBundle().getLocalizedString("start.search.error2", "_")), 1, 1);
								Error2.setRowColor(1, COLOR2);
								myTable.add(Error2, 1, 1);
								break;
							}
							if (E.getMessage().equals("Error3")) {
								Table Error3 = new Table(1, 3);
								Error3.setRowColor(1, COLOR2);
								Error3.setRowColor(2, COLOR3);
								Error3.setWidth("381");
								Error3.setHeight(1, "30");
								Error3.setHeight(2, "25");
								Error3.setCellspacing(0);
								//Error3.setBorder(1);
								Error3.setColumnAlignment(1, "center");
								Error3.add(this.getSmallErrorText(getFieldName(myField.get_field_id())), 1, 1);
								Error3.add(this.getSmallErrorText(getResourceBundle().getLocalizedString("start.search.error3", "_")), 1, 2);
								myTable.add(Error3, 1, 1);
							}
						}
					}
				}
				else {
					Table Error = new Table(1, 1);
					Error.setWidth("381");
					Error.setHeight(1, "21");
					Error.setRowColor(1, COLOR2);
					Error.setColumnAlignment(1, "center");
					Error.add(this.getSmallErrorText(getResourceBundle().getLocalizedString("start.search.error4", "_")), 1, 1);
					myTable.add(Error, 1, 1);
				}
			}
			else {
				Table Error = new Table(1, 1);
				Error.setWidth("381");
				Error.setHeight(1, "21");
				Error.setRowColor(1, COLOR2);
				Error.setColumnAlignment(1, "center");
				Error.add(this.getSmallErrorText(getResourceBundle().getLocalizedString("start.search.error5", "_")), 1, 1);
				myTable.add(Error, 1, 1);
			}
		}
	}

	public Link getSearchLink(IWContext modinfo, boolean inUse) {
		Link myLink;
		if (inUse)
			myLink = new Link(getResourceBundle().getImage("tabs/search.gif"));
		else
			myLink = new Link(getResourceBundle().getImage("tabs/search1.gif"));

		myLink.addParameter("state", "search");

		return myLink;
	}

	public Link getEntryLink(IWContext modinfo, boolean inUse) {
		Link myLink;
		if (inUse)
			myLink = new Link(getResourceBundle().getImage("tabs/teetimes.gif"));
		else
			myLink = new Link(getResourceBundle().getImage("tabs/teetimes1.gif"));

		return myLink;
	}

	public Form enterClub(IWContext modinfo) throws IOException, SQLException {
		Form myForm = new Form();
		//myForm.setAction("/start/start.jsp");
		myForm.addParameter(PRM_ACTION,ACTION_GO_TO_TIMETABLE);

		Table myTable = new Table(3, 4);
		myTable.setAlignment("center");
		myTable.setColor(COLOR4);
		myTable.setWidth("381");
		//		myTable.setBorder(1);
		myTable.setCellspacing(0);
		myTable.setCellpadding(0);

		Text myText = getSmallText(" ");
		myText.setFontSize(1);
		myTable.add(myText, 1, 1);
		myTable.add(myText, 2, 1);
		myTable.add(myText, 3, 1);
		myTable.setHeight(1, "10");

		myTable.add(myText, 1, 4);
		myTable.add(myText, 2, 4);
		myTable.add(myText, 3, 4);
		myTable.setHeight(4, "30");

		myTable.setWidth(1, "15");
		myTable.setWidth(2, "183");
		myTable.setAlignment(3, 3, "center");

		Text klubbur = getSmallHeader(getResourceBundle().getLocalizedString("start.search.club", "Club"));
		klubbur.setFontColor(COLOR7);
		klubbur.setFontSize("2");
		klubbur.setFontStyle("Arial");
		klubbur.setBold();

		myTable.setVerticalAlignment(3, 3, "bottom");

		myTable.add(klubbur, 2, 2);
		myTable.add(insertClubSelectionBox("club", modinfo, 6), 2, 3);
		
		SubmitButton sNext = new SubmitButton(getResourceBundle().getImage("buttons/continue.gif"), "  �fram  ");
		
		myTable.add(sNext, 3, 3);

		myForm.add(myTable);

		return myForm;
	}

	public Form searchEntry(IWContext modinfo, IWTimestamp dateFunc) throws IOException, SQLException {

		Form myForm = new Form();
		//		myForm.setMethod("Get");
		myForm.add(new HiddenInput("state", "search"));

		Table myTable = new Table(2, 5);
		myTable.setAlignment("center");
		myTable.setColor(COLOR4);
		myTable.setWidth("381");
		//		myTable.setBorder(1);
		myTable.setCellspacing(0);
		myTable.setCellpadding(0);
		myTable.setWidth(1, "15");

		//		myTable.setColumnAlignment(1, "center");
		//		myTable.setColumnAlignment(1, "center");

		Table mergeTable = new Table(4, 2);
		mergeTable.setAlignment("left");
		//		mergeTable.setBorder(1);
		mergeTable.setCellspacing(5);
		mergeTable.setCellpadding(0);
		mergeTable.setWidth(1, "60");
		mergeTable.setWidth(2, "80");
		mergeTable.setWidth(3, "80");
		//mergeTable.setWidth(4, "110");

		Table SelectSubmit = new Table(2, 2);
		SelectSubmit.setAlignment("left");
		//		SelectSubmit.setBorder(1);
		SelectSubmit.setCellspacing(0);
		SelectSubmit.setCellpadding(0);
		SelectSubmit.setWidth(1, "183");
		SelectSubmit.setWidth(2, "183");
		SelectSubmit.setAlignment(2, 2, "center");

		Text myText = getSmallText(" ");
		myText.setFontSize(1);
		myTable.add(myText, 1, 1);
		myTable.add(myText, 2, 1);
		myTable.setHeight(1, "10");

		myTable.add(myText, 1, 3);
		myTable.add(myText, 2, 3);
		myTable.setHeight(3, "10");

		myTable.add(myText, 1, 5);
		myTable.add(myText, 2, 5);
		myTable.setHeight(5, "10");

		//		myTable.setRowAlignment (1, "center");
		//		myTable.setRowAlignment (2, "center");
		//		myTable.setRowAlignment (3, "center");
		//		myTable.setRowAlignment (4, "center");

		//		myTable.mergeCells( 1, 2, 3, 2 );
		//		myTable.mergeCells( 1, 3, 3, 3 );

		Text vollur = getSmallHeader(getResourceBundle().getLocalizedString("start.search.course", "Course"));
		Text fjoldi = getSmallHeader(getResourceBundle().getLocalizedString("start.search.how_many", "How many?"));
		Text fKL = getSmallHeader(getResourceBundle().getLocalizedString("start.search.from", "From"));
		Text tKL = getSmallHeader(getResourceBundle().getLocalizedString("start.search.to", "To"));
		Text dags = getSmallHeader(getResourceBundle().getLocalizedString("start.search.date", "Date"));

//		vollur.setFontColor(COLOR7);
//		fjoldi.setFontColor(COLOR7);
//		fKL.setFontColor(COLOR7);
//		tKL.setFontColor(COLOR7);
//		dags.setFontColor(COLOR7);
//
//		vollur.setFontSize("2");
//		fjoldi.setFontSize("2");
//		fKL.setFontSize("2");
//		tKL.setFontSize("2");
//		dags.setFontSize("2");
//
//		vollur.setFontStyle("Arial");
//		fjoldi.setFontStyle("Arial");
//		fKL.setFontStyle("Arial");
//		tKL.setFontStyle("Arial");
//		dags.setFontStyle("Arial");
//
//		vollur.setBold();
//		fjoldi.setBold();
//		fKL.setBold();
//		tKL.setBold();
//		dags.setBold();

		SelectSubmit.setVerticalAlignment(2, 2, "bottom");

		myTable.setAlignment(2, 3, "left");
		//		myTable.setAlignment( 2, 3, "center" );

		SelectSubmit.add(vollur, 1, 1);
		//		System.out.println(" insertSelectionBox ....");
		SelectSubmit.add(insertSelectionBox("fields", modinfo, 6), 1, 2);

		mergeTable.add(fjoldi, 1, 1);

		mergeTable.add(insertEditBox("fjoldi", 2), 1, 2);
		mergeTable.add(fKL, 2, 1); //	insertTimeDrowdown( String dropdownName,
		// String auto, int firstHour, int lastHour
		// int interval);
		mergeTable.add(insertTimeDrowdown("ftime", "22:00", getHours(getFirstOpentime()), getHours(getLastClosetime()), 30), 2, 2);
		//		mergeTable.add(insertDrowdown( "ftime" , "08:00", 30 ), 2, 2);
		mergeTable.add(tKL, 3, 1);
		mergeTable.add(insertTimeDrowdown("ltime", "22:00", getHours(getFirstOpentime()), getHours(getLastClosetime()), 30), 3, 2);
		//		mergeTable.add(insertDrowdown( "ltime", "22:00", 30 , true ), 3, 2);

		myTable.add(SelectSubmit, 2, 2);
		myTable.add(mergeTable, 2, 4);

		mergeTable.add(dags, 4, 1);

		mergeTable.add(insertDropdown("date", dateFunc, getMaxDaysShown(), modinfo), 4, 2);

		SelectSubmit.add(new SubmitButton(getResourceBundle().getImage("buttons/search.gif"), "  Leita  "), 2, 2);
		insertHiddenInput("results", "1", myForm);

		myForm.add(myTable);

		return myForm;
	}

	public boolean checkTime(int begin_hour, int begin_min, int end_hour, int end_min) {
		return (begin_hour < end_hour || (begin_hour == end_hour && begin_min < end_min)) && begin_hour >= 0 && begin_hour <= 24 && end_hour >= 0 && end_hour <= 24 && begin_min >= 0 && begin_min < 60 && end_min >= 0 && end_min < 60;
	}

	public Table Results(IWContext modinfo, Vector Groups, GolfField info, String date1, int resultCol) throws SQLException, IOException, FinderException {

		Table myTable = new Table(1, 2);
		myTable.setRowColor(1, COLOR2);
		myTable.setCellspacing(0);
		myTable.setCellpadding(0);
		myTable.setHeight(1, "30");
		myTable.setRowAlignment(1, "center");

		Vector myVector = new Vector();
		Vector boolVector = new Vector();

		myVector = (Vector) Groups.elementAt(0);
		boolVector = (Vector) Groups.elementAt(1);

		int count = 0;

		for (int i = 0; i < boolVector.size(); i++) {
			if (((Boolean) boolVector.elementAt(i)).booleanValue())
				count++;
		}

		Link myLink = new Link(getFieldName(info.get_field_id()));//, "/start/start.jsp");
		myLink.addParameter(PRM_ACTION,ACTION_GO_TO_TIMETABLE);
		myLink.addParameter("hvar", "" + info.get_field_id());
		myLink.addParameter("search", "1");
		myLink.addParameter("club", "" + getFieldUnion(info.get_field_id()));
		myLink.addParameter("day", date1);

		myTable.add(myLink, 1, 1);
		//		myTable.addText("" + getFieldUnion(info.get_field_id(), Conn), 1, 1);

		Text smallText = getSmallText("");
//		smallText.setFontSize(1);

		if ((count % 10 == 1 || (count % 100) % 10 == 1) && count % 100 != 11) {
			smallText.setText(" (" + count + " " + getResourceBundle().getLocalizedString("start.search.available_tee_time", "Available tee time") + ")");
			myTable.add(smallText, 1, 1);
		}
		else if (count != 0) {
			smallText.setText(" (" + count + " " + getResourceBundle().getLocalizedString("start.search.available_tee_times", "Available tee_times") + ")");
			myTable.add(smallText, 1, 1);
		}
		else {
			Table zero = new Table(1, 2);
			zero.setCellspacing(0);
			zero.setRowAlignment(1, "center");
			zero.setHeight(1, "21");
			zero.setWidth("381");
			zero.add(getText(getResourceBundle().getLocalizedString("start.search.no_tee_times", "_")), 1, 1);
			zero.setRowColor(1, COLOR3);
			myTable.add(zero, 1, 2);
			//					myTable.setRows(2);
		}

		boolean first = true;

		Link[] Times = new Link[count];
		int links = 0;
		int hour = 0;
		int rows = 0;
		int width = 381; //        Breidd t�flunnar sem s�nir ni�urst��ur

		if (count != 0) {
			Table resultTable = new Table(resultCol, 1);
			resultTable.setCellspacing(0);
			resultTable.setCellpadding(0);

			for (int i = 1; i <= resultCol; i++) {
				resultTable.setWidth(i, "" + width / resultCol);
			}

			resultTable.setWidth("" + width);

			for (int i = 0; i < boolVector.size(); i++) {
				if (((Boolean) boolVector.elementAt(i)).booleanValue()) {
					//				if (!first)
					//					myTable.addText(", ", 1, 2);

					hour = getHours(TimeVsGroupnum(Integer.parseInt(myVector.elementAt(i).toString()), info) + ":00");

					Times[links] = new Link(TimeVsGroupnum(Integer.parseInt(myVector.elementAt(i).toString()), info));//, "/start/start.jsp");

					if (hour < 13)
						Times[links].addParameter("hvenaer", "0");
					else if (hour < 17)
						Times[links].addParameter("hvenaer", "1");
					else
						Times[links].addParameter("hvenaer", "2");

					Times[links].addParameter(PRM_ACTION,ACTION_GO_TO_TIMETABLE);
					Times[links].addParameter("hvar", "" + info.get_field_id());
					Times[links].addParameter("search", "1");
					Times[links].addParameter("club", "" + getFieldUnion(info.get_field_id()));
					Times[links].addParameter("day", date1);

					Times[links].setFontColor(COLOR5);

					if (links % resultCol == 0) {
						rows++;
						resultTable.setRows(rows);
						resultTable.setHeight(rows, "25");
					}

					resultTable.add(Times[links], links % resultCol + 1, rows);
					links++;
					//				myTable.addText(
					// TimeVsGroupnum(Integer.parseInt(myVector.elementAt(i).toString())
					// , info) ,
					// 1, 2);
					//				first = false;
				}
			}

			for (int i = 1; i <= resultCol; i++) {
				resultTable.setColumnAlignment(i, "center");
			}

			resultTable.setHorizontalZebraColored(COLOR4, COLOR3);

			resultTable.setRows(++rows);
			resultTable.setHeight(rows, "25");
			resultTable.setRowColor(rows, COLOR6);

			myTable.add(resultTable, 1, 2);
		}

		return myTable;
	}

	public Vector search(IWTimestamp funcDate, IWContext modinfo, GolfField info, GolfField today, int fjoldi, String date, String firstTime, String lastTime, int firstHandicap, int LastHandicap) throws SQLException, IOException, Exception {

		boolean is_allowed = false;

		for (int i = 0; i < today.get_days_shown(); i++) {
			if (getNextDaysRS(funcDate, funcDate.toSQLDateString(), i).equals(date)) {
				is_allowed = true;
			}
		}
		if (!is_allowed)
			throw new Exception("Error3");

		int numOfGroup = fjoldi / 4;
		if (fjoldi % 4 > 0)
			numOfGroup++;

		Vector frameGroups = new Vector();
		Vector Groups = new Vector();
		Vector boolGroups = new Vector();
		frameGroups.add(0, Groups);
		frameGroups.add(1, boolGroups);

		int gr = numberOfGroups(firstTime, lastTime, info.get_interval());
		int firstgr = numberOfGroups(info.get_open_hour(), info.get_open_min(), firstTime, info.get_interval()) + 1;
		int lastgr = firstgr + gr;

		int j = firstgr;
		for (int i = 0; i <= gr; i++) {
			Groups.add(i, new Integer(j++));
			boolGroups.add(i, new Boolean(true));
		}

		TeeTime[] result = service.getTableEntries(date, firstgr, (lastgr + numOfGroup), info.get_field_id());

		Vector RSvector = new Vector();
		Vector group_num = new Vector();
		Vector name = new Vector();
		Vector handycap = new Vector();
		Vector club = new Vector();

		RSvector.add(0, group_num);
		RSvector.add(1, name);
		RSvector.add(2, handycap);
		RSvector.add(3, club);

		int k = 0;
		for (int i = 0; i < result.length; i++) {
			group_num.add(k, "" + result[i].getGroupNum());
			name.add(k, result[i].getName());
			handycap.add(k, "" + result[i].getHandicap());
			club.add(k, result[i].getClubName());

			k++;
		}
		group_num.add(k, "-1"); // sett inn �ar sem group_num m� ekki vera af
		// lengd 0 er aldrei fari� � seinni for-loopuna
		// og ekki passa� upp � a� allir komist fyrir
		// lokun.

		int count = 0;
		int p = 0;
		int m = 0;

		for (m = firstgr; m <= lastgr; m++) {
			for (int n = 0; n < group_num.size(); n++) {
				if (Integer.parseInt(group_num.elementAt(n).toString()) >= m && Integer.parseInt(group_num.elementAt(n).toString()) < (m + numOfGroup)) {
					count++;
				}
				//				out.print( "<br>" +count + " > " + (numOfGroup * 4 - fjoldi) + " ||
				// ( " + (m
				// + numOfGroup) + " > " + getLastGroup(info)+1 + " && " + count + " >
				// " +
				// (-(m-getLastGroup(info))*4 - fjoldi ) + " )" );
				if (count > (numOfGroup * 4 - fjoldi) || (((m + numOfGroup) > getLastGroup(info) + 1) && (count > (-(m - getLastGroup(info)) * 4 - fjoldi)))) {
					boolGroups.set(p, new Boolean(false));
					count = 0;
				}
			}
			count = 0;
			p++;
		}

		for (int i = 0; i < Groups.size(); i++) {
			if (Integer.parseInt(Groups.elementAt(i).toString()) < 1 || Integer.parseInt(Groups.elementAt(i).toString()) > getLastGroup(info))
				boolGroups.set(i, new Boolean(false));
		}

		return frameGroups;

	}

	public int numberOfGroups(String firstTime, String lastTime, int interval) throws Exception {

		SqlTime mySqlTime = new SqlTime(firstTime);
		int firsthour = mySqlTime.get_hour();
		int firstmin = mySqlTime.get_min();
		mySqlTime.set_sqltime(lastTime);
		int lasthour = mySqlTime.get_hour();
		int lastmin = mySqlTime.get_min();

		if (!checkTime(firsthour, firstmin, lasthour, lastmin))
			throw new Exception("Error1");

		int time = (lasthour - firsthour) * 60 + (lastmin - firstmin);

		return time / interval;
	}

	public int numberOfGroups(int firsthour, int firstmin, String lastTime, int interval) {

		SqlTime mySqlTime = new SqlTime(lastTime);
		int lasthour = mySqlTime.get_hour();
		int lastmin = mySqlTime.get_min();

		int time = (lasthour - firsthour) * 60 + (lastmin - firstmin);

		return time / interval;
	}

	public GolfField getFieldInfo(int field, String date) throws SQLException, IOException {
		StartingtimeFieldConfig FieldConfig = service.getFieldConfig(field, date);
		GolfField field_info = new GolfField(new IWTimestamp(FieldConfig.getOpenTime()).toSQLTimeString(), new IWTimestamp(FieldConfig.getCloseTime()).toSQLTimeString(), FieldConfig.getMinutesBetweenStart(), field, date, FieldConfig.getDaysShown(), FieldConfig.publicRegistration());
		return field_info;
	}

	public SubmitButton insertButton(String btnName, String Method, String Action, Form theForm) {
		SubmitButton mySubmit = new SubmitButton(btnName);
		theForm.addObject(mySubmit);

		theForm.setMethod(Method);
		theForm.setAction(Action);

		return mySubmit;
	}

	private SubmitButton insertButton(Image image, String imageName, String Method, String Action, Form theForm) {
		SubmitButton mySubmit = new SubmitButton(image, imageName);
		theForm.addObject(mySubmit);

		theForm.setMethod(Method);
		theForm.setAction(Action);
		return mySubmit;
	}

	public SubmitButton insertButton(String btnName) {
		SubmitButton mySubmit = new SubmitButton(btnName);
		return mySubmit;
	}

	public SubmitButton insertButton(Image myImage, String btnName) {
		SubmitButton mySubmit = new SubmitButton(myImage, btnName);
		return mySubmit;
	}

	public HiddenInput insertHiddenInput(String inpName, String value, Form theForm) {
		HiddenInput myObject = new HiddenInput(inpName, value);
		theForm.addObject(myObject);

		return myObject;
	}

	public SelectionBox insertSelectionBox(String SelectionBoxName, IWContext modinfo, int height) throws IOException, SQLException {
		SelectionBox mySelectionBox = new SelectionBox(SelectionBoxName);
		mySelectionBox.setHeight(height);
		Field[] field = service.getStartingEntryField();
		for (int i = 0; i < field.length; i++) {
			mySelectionBox.addElement("" + field[i].getID(), field[i].getName());
		}
		mySelectionBox.keepStatusOnAction();
		return mySelectionBox;
	}

	public DropdownMenu insertClubSelectionBox(String SelectionBoxName, IWContext modinfo, int height) throws IOException, SQLException {
		DropdownMenu mySelectionBox = new DropdownMenu(SelectionBoxName);
		mySelectionBox.setMarkupAttribute("size", Integer.toString(height));

		Union[] union = service.getStartingEntryUnion();
		for (int i = 0; i < union.length; i++) {
			mySelectionBox.addMenuElement("" + union[i].getID(), union[i].getName());
		}
		if (union.length > 0)
			mySelectionBox.setSelectedElement(Integer.toString(union[0].getID()));
		mySelectionBox.keepStatusOnAction();
		return mySelectionBox;
	}

	public DropdownMenu insertDropdown(String dropdownName, int countFrom, int countTo) {
		String from = Integer.toString(countFrom);
		DropdownMenu myDropdown = new DropdownMenu(dropdownName);

		for (; countFrom <= countTo; countFrom++) {
			myDropdown.addMenuElement(Integer.toString(countFrom), Integer.toString(countFrom));
		}
		myDropdown.keepStatusOnAction();

		return myDropdown;
	}

	public DropdownMenu insertDropdown(String dropdownName, IWTimestamp funcDate, GolfField today, IWContext modinfo) {
		//String funcyDate = funcDate.getDateStamp();
		String funcyDateRS = funcDate.toSQLDateString();

		DropdownMenu myDropdown = new DropdownMenu(dropdownName);

		//		myDropdown.addMenuElement(funcyDateRS, getNextDays(funcDate,
		// funcyDateRS,
		// 0));

		for (int i = 0; i < today.get_days_shown(); i++) {
			myDropdown.addMenuElement(getNextDaysRS(funcDate, funcyDateRS, i), getNextDays(funcDate, funcyDateRS, i));
		}

		myDropdown.keepStatusOnAction();
		myDropdown.setToSubmit();
		myDropdown.setSelectedElement(modinfo.getSession().getAttribute("date").toString());

		return myDropdown;
	}

	public DropdownMenu insertDropdown(String dropdownName, IWTimestamp funcDate, int days_shown, IWContext modinfo) {
		//String funcyDate = funcDate.getDateStamp();
		String funcyDateRS = funcDate.toSQLDateString();

		DropdownMenu myDropdown = new DropdownMenu(dropdownName);

		//		myDropdown.addMenuElement(funcyDateRS, getNextDays(funcDate,
		// funcyDateRS,
		// 0));

		for (int i = 0; i < days_shown; i++) {
			myDropdown.addMenuElement(getNextDaysRS(funcDate, funcyDateRS, i), getNextDays(funcDate, funcyDateRS, i));
		}

		myDropdown.keepStatusOnAction();
		//		myDropdown.setToSubmit();
		//		myDropdown.setSelectedElement(modinfo.getSession().getAttribute("date").toString());

		return myDropdown;
	}

	public DropdownMenu insertDrowdown(String dropdownName, TableInfo myTableInfo, IWContext modinfo) throws SQLException, IOException {
		//		PrintWriter out = modinfo.getResponse().getWriter();

		DropdownMenu myDropdown = new DropdownMenu(dropdownName);
		int end = myTableInfo.get_row_num();
		int interval = myTableInfo.get_interval();
		int pic_min = myTableInfo.get_first_pic_min();
		int pic_hour = myTableInfo.get_first_pic_hour();
		int first_group = myTableInfo.get_first_group();
		String Time;
		String TimeVal;
		int val = 0;

		for (int i = 1; i <= end; i++) {

			if (pic_min >= 60) {
				pic_min -= 60;
				pic_hour++;
			}

			if (pic_min < 10)
				Time = pic_hour + ":0" + pic_min;
			else
				Time = pic_hour + ":" + pic_min;

			val = (first_group + i) - 1;
			TimeVal = Integer.toString(val);

			myDropdown.addMenuElement(TimeVal, Time);

			pic_min += interval;
		}
		myDropdown.keepStatusOnAction();
		return myDropdown;
	}

	/*
	 * public String getTime( int end, GolfField myGolfField) { int interval =
	 * myGolfField.get_interval(); int openMin = myGolfField.get_open_min(); int
	 * openHour = myGolfField.get_open_hour(); String Time = ""; for(int i = 1; i <=
	 * end; i ++){ if (openMin >= 60){ openMin -= 60; openHour++; } if (openMin <
	 * 10) Time = openHour + ":0" + openMin; else Time = openHour + ":" +
	 * openMin; openMin += interval; } return Time; }
	 */

	public DropdownMenu insertDrowdown(String dropdownName, String auto, int bil) {

		DropdownMenu myDropdown = new DropdownMenu(dropdownName);

		String time;
		String TimeVal;

		int pic_hour;

		for (int i = 0; i <= 23; i++) {
			pic_hour = i;

			if (pic_hour != 24) {
				for (int pic_min = 0; pic_min < 60; pic_min += bil) {
					if (pic_min < 10 && pic_hour < 10)
						time = "0" + pic_hour + ":0" + pic_min;
					else if (pic_min < 10)
						time = "" + pic_hour + ":0" + pic_min;
					else if (pic_hour < 10)
						time = "0" + pic_hour + ":" + pic_min;
					else
						time = "" + pic_hour + ":" + pic_min;

					TimeVal = time + ":00";

					myDropdown.addMenuElement(TimeVal, time);
				}
			}
			else {
				myDropdown.addMenuElement("24:00:00", "00:00");
				continue;
			}
		}

		//myDropdown.setSelectedElement(auto + ":00");
		myDropdown.keepStatusOnAction();

		return myDropdown;
	}

	public DropdownMenu insertDrowdown(String dropdownName, String auto, int bil, boolean ltime) {

		DropdownMenu myDropdown = new DropdownMenu(dropdownName);

		String time;
		String TimeVal;
		boolean first = true;
		int pic_hour;

		for (int i = 0; i <= 24; i++) {
			pic_hour = i;

			if (pic_hour != 24) {
				for (int pic_min = 0; pic_min < 60; pic_min += bil) {
					if (!first) {
						if (pic_min < 10 && pic_hour < 10)
							time = "0" + pic_hour + ":0" + pic_min;
						else if (pic_min < 10)
							time = "" + pic_hour + ":0" + pic_min;
						else if (pic_hour < 10)
							time = "0" + pic_hour + ":" + pic_min;
						else
							time = "" + pic_hour + ":" + pic_min;

						TimeVal = time + ":00";

						myDropdown.addMenuElement(TimeVal, time);

					}
					first = false;
				}
			}
			else {
				myDropdown.addMenuElement("24:00:00", "00:00");
				continue;
			}

		}

		//myDropdown.setSelectedElement(auto + ":00");
		myDropdown.keepStatusOnAction();

		return myDropdown;
	}

	public DropdownMenu insertTimeDrowdown(String dropdownName, String auto, int firstHour, int lastHour, int interval) {

		DropdownMenu myDropdown = new DropdownMenu(dropdownName);

		String time;
		String TimeVal;
		//		boolean first = true;
		int pic_hour;
		if (lastHour != 24)
			lastHour++;

		for (int i = firstHour; i <= lastHour; i++) {
			pic_hour = i;
			if (pic_hour != 24) {
				for (int pic_min = 0; pic_min < 60; pic_min += interval) {
					if (pic_min < 10 && pic_hour < 10)
						time = "0" + pic_hour + ":0" + pic_min;
					else if (pic_min < 10)
						time = "" + pic_hour + ":0" + pic_min;
					else if (pic_hour < 10)
						time = "0" + pic_hour + ":" + pic_min;
					else
						time = "" + pic_hour + ":" + pic_min;

					TimeVal = time + ":00";

					myDropdown.addMenuElement(TimeVal, time);

					if (i == lastHour)
						break;

				}
			}
			else {
				myDropdown.addMenuElement("24:00:00", "00:00");
				continue;
			}
		}

		//myDropdown.setSelectedElement(auto + ":00");
		myDropdown.keepStatusOnAction();

		return myDropdown;
	}

	public TextInput insertEditBox(String name) {
		TextInput myInput = new TextInput(name);

		myInput.keepStatusOnAction();

		return myInput;
	}

	public TextInput insertEditBox(String name, int size) {
		TextInput myInput = new TextInput(name);
		myInput.setSize(size);

		myInput.keepStatusOnAction();

		return myInput;
	}

	public String getNextDaysRS(IWTimestamp funcDate, String date, int days) {
		IWCalendar calendar = new IWCalendar(funcDate);
		int day, month, year;
		StringTokenizer Timetoken = new StringTokenizer(date, "-");

		year = Integer.parseInt(Timetoken.nextToken());
		month = Integer.parseInt(Timetoken.nextToken());
		day = Integer.parseInt(Timetoken.nextToken()) + days;

		if (day > calendar.getLengthOfMonth(month, year)) {
			day -= (calendar.getLengthOfMonth(month, year));
			month++;
		}
		if (month > 12) {
			year++;
			month -= 12;
		}
		String d, m;

		if (day < 10)
			d = "0" + day;
		else
			d = "" + day;

		if (month < 10)
			m = "0" + month;
		else
			m = "" + month;

		return year + "-" + m + "-" + d;

	}

	public String getNextDays(IWTimestamp funcDate, String date, int days) {
		IWCalendar calendar = new IWCalendar(funcDate);
		int day, month, year;
		StringTokenizer Timetoken = new StringTokenizer(date, "-");

		year = Integer.parseInt(Timetoken.nextToken());
		month = Integer.parseInt(Timetoken.nextToken());
		day = Integer.parseInt(Timetoken.nextToken()) + days;

		if (day > calendar.getLengthOfMonth(month, year)) {
			day -= calendar.getLengthOfMonth(month, year);
			month++;
		}
		if (month > 12) {
			year++;
			month -= 12;
		}

		String mon = calendar.getMonthName(month);

		if (day < 10)
			return day + ".&nbsp;&nbsp;" + mon.toLowerCase() + " " + year;
		else
			return day + ". " + mon.toLowerCase() + " " + year;
	}

	public String getFieldName(int field_id) throws SQLException, IOException, FinderException {
		return service.getFieldName(field_id);
	}

	public String getFirstOpentime() throws SQLException, IOException {
		String time = "08:00:00";
		if (service.getFirstOpentime() != null)
			time = service.getFirstOpentime().toSQLTimeString();
		return time;
	}

	public int getMaxDaysShown() throws SQLException, IOException {
		return service.getMax_days_shown();
	}

	public String getLastClosetime() throws SQLException, IOException {
		String time = "23:00:00";
		if (service.getLastClosetime() != null)
			time = service.getLastClosetime().toSQLTimeString();
		return time;
	}

	public int getFieldUnion(int field_id) throws SQLException, IOException, FinderException {
		return service.get_field_union(field_id);
	}

	//	/#### Skilar Klukkustundinni �r streng � forminu 'klst:min:sec'
	// \(08:00:00)\
	// ####///
	public int getHours(String Hours) {
		SqlTime mySqlTime = new SqlTime(Hours);
		return mySqlTime.get_hour();
	}

	//	/######## Skilar n�meri � �v� holli sem er s��ast fyrir lokun
	// #####////////
	public int getLastGroup(GolfField myGolfField) {

		int interval = myGolfField.get_interval();

		int Hours = myGolfField.get_close_hour() - myGolfField.get_open_hour();
		int Min = myGolfField.get_close_min() - myGolfField.get_open_min();

		return (Hours * 60 + Min) / interval;
	}

	//	/#### Skilar t�ma m.v. v�ll og n�mer � holli ####///
	public String TimeVsGroupnum(int group, GolfField myGolfField) {

		int interval = myGolfField.get_interval();
		int openHour = myGolfField.get_open_hour();
		int openMin = myGolfField.get_open_min();

		int Hour = openHour + ((group - 1) * interval) / 60;
		int Min = openMin + ((group - 1) * interval) % 60;

		if (Min >= 60) {
			Min -= 60;
			Hour++;
		}

		String time;

		if (Min < 10 && Hour < 10)
			time = "0" + Hour + ":0" + Min;
		else if (Min < 10)
			time = "" + Hour + ":0" + Min;
		else if (Hour < 10)
			time = "0" + Hour + ":" + Min;
		else
			time = "" + Hour + ":" + Min;

		return time;
	}

	public boolean numericString(String myString) {

		boolean isTrue = true;

		for (int i = 0; i < myString.length(); i++) {
			if (!(myString.charAt(i) == '0' || myString.charAt(i) == '1' || myString.charAt(i) == '2' || myString.charAt(i) == '3' || myString.charAt(i) == '4' || myString.charAt(i) == '5' || myString.charAt(i) == '6' || myString.charAt(i) == '7' || myString.charAt(i) == '8' || myString.charAt(i) == '9'))
				isTrue = false;
		}

		return isTrue;
	}
}