/*
 * Created on 2003-nov-26
 *
 * To change the template for this generated file go to
 * Window - Preferences - Java - Code Generation - Code and Comments
 */
package com.idega.block.school.presentation;


import com.idega.presentation.IWContext;
import com.idega.presentation.Table;
import com.idega.presentation.ui.Window;
import com.idega.presentation.text.Text;


/**
 * @author Malin
 *
 * To change the template for this generated type comment go to
 * Window - Preferences - Java - Code Generation - Code and Comments
 */
public class SchoolEditorInfoText extends Window {
	public static final int PARAMETER_TOPIC_ID_EDITOR = 0;
	public static final int PARAMETER_TOPIC_ID_DEPM =1;
	public static final int PARAMETER_TOPIC_ID_PERSON = 2;
	

//private Text TEXT_NORMAL;
	//private Text TEXT_TITLE;
	

	public SchoolEditorInfoText() {
		setWidth(300);
		setHeight(200);
		setResizable(true);
		setScrollbar(true);
		setTitle( "School editor information" );	
		
		}
	public void main( IWContext iwc ) throws Exception {
		boolean topicEditor = iwc.isParameterSet(SchoolUserEditor.PARAMETER_TOPIC_EDITOR);
		boolean topicDepm = iwc.isParameterSet(SchoolUserEditor.PARAMETER_TOPIC_DEPM);
		boolean topicPerson = iwc.isParameterSet(SchoolUserEditor.PARAMETER_TOPIC_PERSON);
		Table table = new Table();

		if (topicEditor) {
			Text tHeading = getTextTitle("Redigeraren");
			String sText = "I redigeraren kan du l�gga in eller �ndra adress- och kontaktuppgifter f�r en skola. Rektor f�r skolan, huvudrektorn, visas �verst. Under Kontakta oss visas de kontaktpersoner som ni v�ljer att visa. Om skolan �r indelad i enheter kan du v�lja att l�gga in uppgifter och kontaktpersoner under en viss enhet. De personer som du skapat via admingr�nssnittet syns �ven h�r i redigeraren.";
			Text tText = getTextNormal(sText);
			table.add(tHeading, 1, 1);
			table.add(tText, 1, 2);

		} else if (topicDepm) {
			Text tHeading = getTextTitle("Skapa en enhet");
			String sText = "Om organisationen �r enhetsindelad kan du i systemet skapa enheter genom att skriva in enhetens namn och eventuellt telefonnummer under rubriken �Skapa en enhet� och klicka p� �Spara�.";
			Text tText = getTextNormal(sText);
			table.add(tHeading, 1, 1);
			table.add(tText, 1, 2);
		} else if (topicPerson){
			Text tHeading = getTextTitle("Skapa en kontaktperson f�r skolan eller enheten");
			Text tSubHeading = getTextTitleGray("Skapa Huvudrektor");
			
			String sTextHuvud = "F�r att skapa huvudrektorn p� skolan v�ljer du typen Rektor samt kryssar i rutan Huvudrektor. Fyll sedan i f�lten f�r kontaktuppgifterna. Du beh�ver inte v�lja n�gon enhet.";
			sTextHuvud = sTextHuvud + "F�r att personen ska visas m�ste du �ven kryssa i rutan Visa i kontaktlista och sedan klicka p� Spara.";

			Text tTextHuvud = getTextNormal(sTextHuvud);
		
			Text tHeadingKontakt = getTextTitleGray("Skapa annan kontaktperson");
			String sTextKontakt = "F�r att skapa en kontaktperson v�ljer du typ av kontaktperson samt fyller i f�lten f�r kontaktuppgifterna. Om personen tillh�r n�gon enhet v�ljer du enhet i rullgardinsmenyn."; 
			sTextKontakt = sTextKontakt + "F�r att personen ska visas m�ste du �ven kryssa i rutan Visa i kontaktlista och sedan klicka p� Spara.";
			Text tTextKontakt = getTextNormal(sTextKontakt);
			Text tHeadingEdit = getTextTitleGray("Redigera kontaktperson");
			String sTextEditKontakt = "F�r att redigera en kontaktperson klickar du p� �ndra intill personen som ska redigeras. N�r du �r f�rdig med dina �ndringar klickar du p� Spara.";
			Text sTextEdit = getTextNormal(sTextEditKontakt);
			Text tHeadingDelete = getTextTitleGray("Ta bort kontaktperson");
			String sTextDelete = "F�r att ta bort en kontaktperson klickar du p� l�nken Radera intill kontaktpersonen.";
			Text tTextDelete = getTextNormal(sTextDelete);

			table.add(tHeading, 1, 1);
			table.add(tSubHeading, 1, 2);
			table.add(tTextHuvud, 1, 3);
			table.add(tHeadingKontakt, 1, 4);
			table.add(tTextKontakt, 1, 5);
			table.add(tHeadingEdit, 1, 6);
			table.add(sTextEdit, 1, 7);
			table.add(tHeadingDelete, 1, 8);
			table.add(tTextDelete, 1, 9);

			

		}
		add(table);
		
		
	}

private Text getTextNormal(String content) {
		/*if (TEXT_NORMAL == null) {
			return _tFormat.format(content, TextFormat.NORMAL);
	 
		}else {
			Text text = (Text) TEXT_NORMAL.clone();
			text.setText(content);
			return text;	
		}
*/
Text text = new Text (content);
String STYLE_SMALL_HEADER = "font-style:normal;text-decoration:none;color:#000000;"
			+ "font-size:10px;font-family:Verdana,Arial,Helvetica;font-weight:normal;";
text.setFontStyle(STYLE_SMALL_HEADER);

return text;
	}

	private Text getTextTitle(String content) {
		/*if (TEXT_TITLE == null) {
			return _tFormat.format(content, TextFormat.TITLE);
		}else {
			Text text = (Text) TEXT_TITLE.clone();
			text.setText(content);
			return text;	
		}
*/
Text text = new Text (content);
String STYLE_SMALL_HEADER = "font-style:normal;text-decoration:none;color:#000000;"
			+ "font-size:10px;font-family:Verdana,Arial,Helvetica;font-weight:bold;";
text.setFontStyle(STYLE_SMALL_HEADER);
return text;
	}
	
	private Text getTextTitleGray(String content) {
		/*	if (TEXT_TITLE == null) {
				return _tFormat.format(content, TextFormat.TITLE);
			}else {
				Text text = (Text) TEXT_TITLE.clone();
				text.setText(content);
				text.setFontColor("#386cb7");
				return text;	
			}
*/
Text text = new Text (content);
String STYLE_SMALL_HEADER = "font-style:normal;text-decoration:none;color:#386cb7;"
			+ "font-size:10px;font-family:Verdana,Arial,Helvetica;font-weight:bold;";
text.setFontStyle(STYLE_SMALL_HEADER);

return text;
		}


public void setTextStyleNormal(Text text) {
  	//this.TEXT_NORMAL = text;	
  }
  
  public void setTextStyleTitle(Text text) {
  	//this.TEXT_TITLE = text;	
  }

}
