/*
 * Created on 2004-okt-10
 *
 * To change the template for this generated file go to
 * Window - Preferences - Java - Code Generation - Code and Comments
 */
package se.idega.idegaweb.ehealth.presentation;



import java.util.ArrayList;
import java.util.Iterator;

import javax.faces.component.UIComponent;

import se.idega.util.PIDChecker;

import com.idega.business.IBOLookup;
import com.idega.presentation.IWContext;
import com.idega.presentation.Image;
import com.idega.presentation.Layer;
import com.idega.presentation.Page;
import com.idega.presentation.Script;
import com.idega.presentation.Table;
import com.idega.presentation.text.Break;
import com.idega.presentation.text.Text;
import com.idega.presentation.ui.DateInput;
import com.idega.presentation.ui.DropdownMenu;
import com.idega.presentation.ui.Form;
import com.idega.presentation.ui.GenericButton;
import com.idega.user.business.UserBusiness;
import com.idega.user.data.User;
import com.idega.util.Age;
import com.idega.util.IWTimestamp;



/**
 * @author Malin
 *
 * To change the template for this generated type comment go to
 * Window - Preferences - Java - Code Generation - Code and Comments
 */
public class JournalTexts extends EHealthBlock {
	
	private String prefix = "patient_";
	private String prmForm = prefix + "form_visit";
	
	private String prmCareUnit = prefix + "care_unit";
	private String prmCareGiver = prefix + "care_giver";
	private String prmDateContact = prefix + "date_care_contact";
	private String prmAppointType = prefix + "appoint_type";
	private String prmPrint = prefix + "print";
	private String prmHealthCentre = prefix + "healthcentre";
	
	private String prmFrom = prefix + "from";
	private String prmTo = prefix + "to";
	private String prmSearch = prefix + "search";
	//private String prmLoglist = prefix + "loglist";
	
	private String keyOpenNewWindow = prefix + "open_in_new_window";
	private String keyFrom = prefix + "from";
	private String keyTo = prefix + "to";
	
	private String keyText1U1 = prefix + "jt1U1";
	private String keyText2U1 = prefix + "jt2U1";
	private String keyText3U1 = prefix + "jt3U1";
	private String keyText4U1 = prefix + "jt4U1";
	private String keyText5U1 = prefix + "jt5U1";
	
	private String keyText1U2 = prefix + "jt1U2";
	private String keyText2U2 = prefix + "jt2U2";
	private String keyText3U2 = prefix + "jt3U2";
	private String keyText4U2 = prefix + "jt4U2";
	private String keyText5U2 = prefix + "jt5U2";
	
	private int userID = -1;
	private User user;
	IWContext _iwc = null;
	Age age = null;
	
	private boolean showOpenButton = true;
	
	public void main(IWContext iwc) throws Exception {
		_iwc = iwc;
		
		userID = iwc.getUserId();
		
		if (userID > 0) {
			user = ((UserBusiness) IBOLookup.getServiceInstance(iwc, UserBusiness.class)).getUser(userID);
		}
		
		
		if (user != null && user.getDateOfBirth() != null)
			age = new Age(user.getDateOfBirth());
		else if (user != null && user.getPersonalID() != null)
			age = new Age(PIDChecker.getInstance().getDateFromPersonalID(user.getPersonalID()));
		
		add(getAppointmentHistoryForm());
		
	}
	
	
	
	public UIComponent getAppointmentHistoryForm(){
		Form myForm = new Form();
		myForm.setName(prmForm);
		Table T = new Table(1, 4);
		T.setCellpadding(0);
		T.setCellspacing(0);
		T.setBorder(0);
		T.setBorderColor("#000000");
		T.setVerticalAlignment(1, 3, Table.VERTICAL_ALIGN_TOP);
		T.setVerticalAlignment(1, 4, Table.VERTICAL_ALIGN_BOTTOM);
		
		T.add(getSearchSortTable(), 1, 1);
		T.add(getHeadingTable(), 1, 2);
		T.add(getInfoLayer(), 1, 3);
		T.add(getTableButtons(), 1, 4);
		
		T.add(new Break(), 1, 1);
		T.add(new Break(), 1, 3);
		T.setHeight(1, 3, "235");		
		T.setHeight(1, 4, "40");
		myForm.add(T);
		
		Page pVisit = this.getParentPage();
		if (pVisit != null) {
			Script S = pVisit.getAssociatedScript();
			pVisit.setOnLoad("setRowColor(document.getElementById('lay1_1'));");
			S.addFunction("setRowColor(obj)", setRowColorScript());
			Script timeScript = myForm.getAssociatedFormScript();
			if (timeScript == null) {
				timeScript = new Script();
				myForm.setAssociatedFormScript(timeScript);
			}
		}
		ArrayList texts = new ArrayList();
		
		if (age != null && age.getYears() >= 70){
			texts.add(localize(keyText1U1, "Texten"));
			texts.add(localize(keyText2U1, "Texten"));
			texts.add(localize(keyText3U1, "Texten"));
			texts.add(localize(keyText4U1, "Texten"));
			texts.add(localize(keyText5U1, "Texten"));	
		}		
		else{
			texts.add(localize(keyText1U2, "Texten"));
			texts.add(localize(keyText2U2, "Texten"));
			texts.add(localize(keyText3U2, "Texten"));
			texts.add(localize(keyText4U2, "Texten"));
			texts.add(localize(keyText5U2, "Texten"));	
		}
		Layer layer = new Layer(Layer.DIV);
		layer.setVisibility("hidden");
		layer.setOverflow("scroll");
		layer.setPositionType("absolute");
		layer.setWidth("610");
		layer.setHeight("150");
		layer.setMarkupAttribute("class", "ehealth_div");
		
		
		
		int theRow = 1;
		Iterator iter = texts.iterator();
		
		while (iter.hasNext()) {
			Layer layers = (Layer) layer.clone();
			layers.setID("lay" + theRow + "_");
			String text = (String) iter.next();
			layers.add(text);
						
			T.add(layers, 1, 3);
			theRow++;
		}
		
		
		return myForm;
		
		
	}
	
		
	private Layer getInfoLayer(){
		Layer layerInfo = new Layer(Layer.DIV);
		layerInfo.setOverflow("scroll");
		layerInfo.setPositionType("relative");
		layerInfo.setWidth("610");
		layerInfo.setHeight("75");
		layerInfo.setMarkupAttribute("class", "ehealth_div");
		
		Table tableInfo = new Table(7, 6);
		tableInfo.setNoWrap();
		tableInfo.setCellpadding(0);
		tableInfo.setCellspacing(0);
		tableInfo.setBorder(0);			
		tableInfo.setWidth(570);
		tableInfo.setWidth(1, 1, "125");
		tableInfo.setWidth(2, 1, "20");
		tableInfo.setWidth(3, 1, "110");
		tableInfo.setWidth(4, 1, "20");
		tableInfo.setWidth(5, 1, "150");
		tableInfo.setWidth(6, 1, "20");
		tableInfo.setWidth(7, 1, "125");
		
		Image transpImg = Table.getTransparentCell(_iwc);
		transpImg.setWidth(20);
		transpImg.setHeight(13);
		
		Layer layer = new Layer(Layer.DIV);
		layer.setOnMouseOver("setRowColor(this);");
		layer.setPositionType("relative");
		layer.setHeight(13);
		
		
		int theRow = 1;
		int theColumn = 1;
		
		ArrayList dates = new ArrayList();
		ArrayList caregivers = new ArrayList();
		ArrayList vcs = new ArrayList();
		ArrayList visitypes = new ArrayList();
		
		if (age != null && age.getYears() >= 70){
		
			dates.add("2004-10-11");
			dates.add("2004-10-06");
			dates.add("2004-06-15");
			dates.add("2004-02-07");
			dates.add("2003-12-16");
			caregivers.add("Dr Magne Syhl");
			caregivers.add("Dr Alve Don");
			caregivers.add("Dr Inga Pren");
			caregivers.add("Dr Alve Don");
			caregivers.add("Dr Alve Don");
			vcs.add("Gimo VC");
			vcs.add("Gimo VC");
			vcs.add("Gimo VC");
			vcs.add("Gimo VC");
			vcs.add("Gimo VC");
			visitypes.add("L�k mott.bes�k");
			visitypes.add("Inskrivning");
			visitypes.add("L�k mott.bes�k");
			visitypes.add("L�k mott.bes�k");
			visitypes.add("L�k mott.bes�k");
			
		}
		else{
			dates.add("2004-09-28");
			dates.add("2004-09-24");
			dates.add("2004-08-15");
			dates.add("2004-04-07");
			dates.add("2003-10-16");
			caregivers.add("Dr Inga Pren");
			caregivers.add("Dr Magne Syhl");
			caregivers.add("Dr Alve Don");			
			caregivers.add("Dr Alve Don");
			caregivers.add("Dr Alve Don");
			vcs.add("Gimo VC");
			vcs.add("Gimo VC");
			vcs.add("Gimo VC");
			vcs.add("Gimo VC");
			vcs.add("Gimo VC");
			visitypes.add("L�k mott.bes�k");
			visitypes.add("L�k mott.bes�k");
			visitypes.add("L�k mott.bes�k");
			visitypes.add("L�k mott.bes�k");
			visitypes.add("L�k mott.bes�k");	
		}
			
		
		Iterator idates = dates.iterator();
		Iterator icaregivers = caregivers.iterator();
		Iterator ivcs = vcs.iterator();
		Iterator ivisitypes = visitypes.iterator();

	
		while (idates.hasNext()) {		
		//for (theRow = 1; theRow <= 5; theRow++) {
			
			for (theColumn = 1; theColumn <= 7; theColumn++) {
				Layer layers = (Layer) layer.clone();
				layers.setID("lay" + theRow + "_"+ theColumn);
				if (theColumn % 2 == 0){
					layers.add(transpImg);
					layers.setWidth("20");
				}
				else if (theColumn == 1){
					String theDate = (String) idates.next();
					layers.add(theDate);
				}
				else if (theColumn == 3){
					String theCaregiver = (String) icaregivers.next();
					layers.add(theCaregiver);
				}
				else if (theColumn == 5){
					String theVc = (String) ivcs.next();
					layers.add(theVc);
				}
				else if (theColumn == 7){
					String theVisitType = (String) ivisitypes.next();
					layers.add(theVisitType);
				}
				
				tableInfo.add(layers, theColumn, theRow);
			}
			theRow++;
		}
	
		layerInfo.add(tableInfo);
		
		return layerInfo;
	}
	
	private Layer getHeadingTable(){
		Layer layerHead = new Layer(Layer.DIV);
		layerHead.setMarkupAttribute("class", "ehealth_div_no_border");
		
		Table table = new Table(7, 1);
		table.setCellpadding(0);
		table.setCellspacing(0);
		table.setBorder(0);
		table.setWidth(570);
		table.setHeight(20);
		
		
		table.setAlignment(1, 1, Table.HORIZONTAL_ALIGN_LEFT);
		
		
		table.setWidth(1, 1, "125");
		table.setWidth(2, 1, "20");
		table.setWidth(3, 1, "110");
		table.setWidth(4, 1, "20");
		table.setWidth(5, 1, "150");
		table.setWidth(6, 1, "20");
		table.setWidth(7, 1, "125");
		
		Text date = getLocalizedSmallHeader(prmDateContact,"Date for care contact");
		Text careGiver = getLocalizedSmallHeader(prmCareGiver,"Care giver");
		Text careUnit = getLocalizedSmallHeader(prmCareUnit,"Care unit");
		Text regReason = getLocalizedSmallHeader(prmAppointType,"Appointment type");
				
		table.add(date, 1, 1);
		table.add(careGiver, 3, 1);
		table.add(careUnit, 5, 1);
		table.add(regReason, 7, 1);
		
		
		layerHead.add(table);
		
		return layerHead;
	}
	
	private Table getSearchSortTable(){
		
		Table table = new Table(3, 5);
		table.setCellpadding(0);
		table.setCellspacing(0);
		table.setBorder(0);
		
		table.setVerticalAlignment(1, 1, Table.VERTICAL_ALIGN_BOTTOM);
		table.setVerticalAlignment(3, 1, Table.VERTICAL_ALIGN_BOTTOM);
		table.setVerticalAlignment(1, 2, Table.VERTICAL_ALIGN_BOTTOM);
		table.setVerticalAlignment(3, 2, Table.VERTICAL_ALIGN_BOTTOM);
		table.setVerticalAlignment(1, 3, Table.VERTICAL_ALIGN_BOTTOM);
		table.setVerticalAlignment(1, 4, Table.VERTICAL_ALIGN_BOTTOM);
		table.setVerticalAlignment(1, 5, Table.VERTICAL_ALIGN_BOTTOM);
		table.setVerticalAlignment(3, 3, Table.VERTICAL_ALIGN_BOTTOM);
		table.setAlignment(3, 3, Table.HORIZONTAL_ALIGN_RIGHT);
		
		table.setHeight(1, 1, "25");
		table.setHeight(1, 2, "25");
		table.setHeight(1, 3, "25");
		table.setHeight(1, 4, "25");
		table.setHeight(1, 5, "25");
		table.setWidth(2, 1, "25");
	
		IWTimestamp stamp = new IWTimestamp();
		
		DateInput from = (DateInput) getStyledInterface(new DateInput(prmFrom, true));
		from.setYearRange(stamp.getYear() - 11, stamp.getYear()+3);
		
		DateInput to = (DateInput) getStyledInterface(new DateInput(prmTo, true));
		to.setYearRange(stamp.getYear() - 11, stamp.getYear()+3);
		
		
		
		DropdownMenu dropHCentre = (DropdownMenu) getStyledInterface(new DropdownMenu(prmHealthCentre));
		dropHCentre.addMenuElementFirst("-1", "V�lj v�rdenhet");
		dropHCentre.addMenuElement("1", "Gimo VC");
		dropHCentre.addMenuElement("2", "�sthammar VC");
		dropHCentre.addMenuElement("3", "Alunda VC");
		dropHCentre.addMenuElement("4", "�sterbybruk VC");
		dropHCentre.addMenuElement("5", "Tierp VC");
		dropHCentre.addMenuElement("6", "�regrund VC");
		dropHCentre.addMenuElement("7", "Skutsk�r VC");
		dropHCentre.addMenuElement("8", "M�nkarbo VC");
		
		DropdownMenu dropCaregiver = (DropdownMenu) getStyledInterface(new DropdownMenu(prmHealthCentre));
		dropCaregiver.addMenuElementFirst("-1", "V�lj v�rdgivare");
		dropCaregiver.addMenuElement("1", "Dr Magne Syhl");
		dropCaregiver.addMenuElement("2", "Dr Alve Don");
		dropCaregiver.addMenuElement("3", "Dr Inga Pren");
		dropCaregiver.addMenuElement("4", "Dr Volta Ren");
		dropCaregiver.addMenuElement("5", "Dr Cura Don");
			
		GenericButton search = getButton(new GenericButton("search", localize(prmSearch, "Search")));
		
	//	GenericButton loglist = getButton(new GenericButton("loglist", localize(prmLoglist, "Loglist")));
		
		table.add(getSmallHeader(localize(keyFrom, "From")+": "), 1, 1);
		table.add(from, 1, 2);
		table.add(getSmallHeader(localize(keyTo, "To")+": "), 3, 1);
		table.add(to, 3, 2);
	//	table.add(loglist, 3, 3);
		table.add(dropHCentre, 1, 3);
		table.add(dropCaregiver, 1, 4);
		table.add(search, 1, 5);
		
		return table;
	}
	
	private String setRowColorScript() {
		StringBuffer s = new StringBuffer();
		
		
		s.append("function setRowColor(obj){").append(" \n\t");
		s.append("elementBase = obj.id.substring(0, 5);").append(" \n\t");
		s.append("for(i=1;i<document.all.tags('div').length;i++){").append(" \n\t");
		s.append("if (document.all.tags('div')[i].id.length == 5){").append(" \n\t");
		s.append("document.all.tags('div')[i].style.visibility = 'hidden'");
		s.append("}").append("\n\t");
		s.append("document.all.tags('div')[i].style.backgroundColor = '#ffffff';");
		s.append("}").append("\n\t");
		s.append("for (i = 1; i <= 7; i++){").append(" \n\t");
		s.append("elementName = eval(elementBase + i);").append(" \n\t");		
		s.append("document.getElementById(elementName.id).style.backgroundColor = '#CCCCCC';").append(" \n\t");
		s.append("}").append("\n\t");
		s.append("showlayer = eval(elementBase + '.id');").append(" \n\t");
		s.append("document.all(showlayer).style.visibility = 'visible';").append(" \n\t");
		
		s.append("}").append("\n\t\t\t");
		
		return s.toString();
	}
	
	
	private Table getTableButtons() {
		Table table = new Table(5, 1);
		table.setCellpadding(0);
		table.setCellspacing(0);
		table.setBorder(0);
		table.setHeight(20);
		
		
		table.setAlignment(1, 1, Table.HORIZONTAL_ALIGN_LEFT);
		
		table.setWidth(2, 1, "15");
		table.setWidth(4, 1, "15");
				
		
		Image printIcon = (Image) getPrintIcon(_iwc);
		table.add(printIcon, 1, 1);
		
		GenericButton print = getButton(new GenericButton("print", localize(prmPrint, "Print")));
		if (showOpenButton){
			GenericButton openinWindow = getButton(new GenericButton("open", localize(keyOpenNewWindow, "Open in new window")));
			table.add(openinWindow, 5, 1);
		}
		table.add(print, 3, 1);
		
			
		return table;
		
	}
	
	public void setShowOpenButton(boolean showOpenButton){
		this.showOpenButton = showOpenButton;
	}
}
