package com.idega.block.reports.presentation;

import com.idega.block.reports.data.Report;
import com.idega.block.reports.business.ReportFinder;
import com.idega.block.reports.business.ReportBusiness;
import com.idega.block.reports.business.StickerReport;
import com.idega.block.reports.business.ReportWriter;
import com.idega.block.reports.data.ReportInfo;
import com.idega.block.reports.data.ReportColumnInfo;
import com.idega.presentation.Block;
import com.idega.idegaweb.IWBundle;
import com.idega.idegaweb.IWResourceBundle;
import com.idega.idegaweb.IWMainApplication;
import com.idega.presentation.IWContext;
import com.idega.presentation.PresentationObject;
import com.idega.presentation.Table;
import com.idega.presentation.Image;
import com.idega.presentation.ui.*;
import com.idega.presentation.text.*;
import com.lowagie.text.Font;
import com.lowagie.text.PageSize;
import com.lowagie.text.Rectangle;
import java.util.*;
import com.idega.util.text.Edit;

/**
 * Title:        idegaclasses
 * Description:
 * Copyright:    Copyright (c) 2001
 * Company:      idega
 * @author <a href="aron@idega.is">Aron Birkir</a>
 * @version 1.0
 */

public class ReportPrinter extends Block implements Reports{

  protected IWResourceBundle iwrb;
  protected IWBundle iwb;
  private Image pdfImage ;

  public String getBundleIdentifier(){
    return REPORTS_BUNDLE_IDENTIFIER;
  }

  public String getLocalizedNameKey(){
    return "report_printer";
  }

  public String getLocalizedNameValue(){
    return "Report printer";
  }

  public void main(IWContext iwc){
    iwrb = getResourceBundle(iwc);
    iwb = getBundle(iwc);
    pdfImage = iwb.getImage("/shared/pdf.gif");
    if(iwc.isParameterSet(PRM_REPORTID)){
      Report eReport = ReportFinder.getReport(Integer.parseInt(iwc.getParameter(PRM_REPORTID)));
      if(iwc.isParameterSet("savereps")||iwc.isParameterSet("savereps.x")){
          String[] sids= iwc.getParameterValues("rep_save");
          int[] savedids = new int[0];
          if(sids!=null)
           savedids = new int[sids.length];
          for (int i = 0; i < savedids.length; i++) {
            savedids[i] = Integer.parseInt(sids[i]);
          }
          saveReportInfo(eReport.getID(),savedids);
      }
      else if(iwc.isParameterSet("print")||iwc.isParameterSet("print.x")){

      }

      Table T = new Table();
      T.add(getForm(eReport));
      add(T);
    }
  }


  private PresentationObject getForm(Report report){
    DataTable T = new DataTable();
    T.addTitle(iwrb.getLocalizedString("report_printouts","Report Printouts"));
    T.setTitlesHorizontal(true);
    T.addButton(new SubmitButton(iwrb.getLocalizedImageButton("save","Save"),"savereps"));

    int row = 1;
    int col = 1;
    T.add(Edit.formatText(iwrb.getLocalizedString("name","Name")),col++,row);
    T.add(Edit.formatText(iwrb.getLocalizedString("type","type")),col++,row);
    T.add(Edit.formatText(iwrb.getLocalizedString("description","Description")),col++,row);
    T.add(Edit.formatText(iwrb.getLocalizedString("pagesize","Pagesize")),col++,row);
    T.add(Edit.formatText(iwrb.getLocalizedString("orientation","Orientation")),col++,row);
    T.add(Edit.formatText(iwrb.getLocalizedString("saved","Saved")),col++,row);
    T.add(Edit.formatText(iwrb.getLocalizedString("print","Print")),col++,row);

    row++;
    List infos = ReportFinder.listOfReportInfo(null);
    List repinfos = ReportFinder.listOfRelatedReportInfo(report);
    List unsaved = new Vector(infos);
    if(repinfos!=null)
      unsaved.removeAll(repinfos);
    boolean formAdded = false;
    ReportInfo info;
    String type;
    CheckBox box;
    if(repinfos !=null){
      Iterator iter = repinfos.iterator();
      while (iter.hasNext()) {
        col = 1;
        info = (ReportInfo) iter.next();
        T.add(Edit.formatText(info.getName()),col++,row);
        type = info.getType();
        T.add(Edit.formatText(type),col++,row);
        if(type.equals(ReportPDFSetupEditor.typeSticker)){
          T.add(Edit.formatText((int)info.getWidth()+" x "+(int)info.getHeight()),col++,row);

        }
        else if(type.equals(ReportPDFSetupEditor.typeColumns)){
          T.add(Edit.formatText("Cols: "+info.getColumns()),col++,row);
        }
        T.add(Edit.formatText(info.getPagesize()),col++,row);
        T.add(Edit.formatText(info.getLandscape()?"Landscape":"Portrait"),col++,row);
        box = new CheckBox("rep_save",String.valueOf(info.getID()));
        box.setChecked(true);
        T.add(box,col++,row);
        T.add(getPrintLink(pdfImage,report.getID(),info.getID()),col++,row);
        row++;
      }
    }
    if(unsaved!=null){
      Iterator iter = unsaved.iterator();
       while (iter.hasNext()) {
        col = 1;
        info = (ReportInfo) iter.next();
        T.add(Edit.formatText(info.getName()),col++,row);
        type = info.getType();
        T.add(Edit.formatText(type),col++,row);
        if(type.equals(ReportPDFSetupEditor.typeSticker)){
          T.add(Edit.formatText((int)info.getWidth()+" x "+(int)info.getHeight()),col++,row);
        }
        else if(type.equals(ReportPDFSetupEditor.typeColumns)){
          T.add(Edit.formatText("cols: "+info.getColumns()),col++,row);
        }
         T.add(Edit.formatText(info.getPagesize()),col++,row);
        T.add(Edit.formatText(info.getLandscape()?"Landscape":"Portrait"),col++,row);
        box = new CheckBox("rep_save",String.valueOf(info.getID()));
        T.add(box,col++,row);
        T.add(getPrintLink(pdfImage,report.getID(),info.getID()),col++,row);
        row++;
      }

    }

    T.add(new HiddenInput(PRM_REPORTID,Integer.toString(report.getID())));
    Form F = new Form();
    F.add(T);

    return F;
  }

  public void deleteColumnInfo(IWContext iwc,int id){
    ReportBusiness.deleteReportColumnInfo(id);
  }

  private void saveReportInfo(int iReportId ,int[] ids){
    ReportBusiness.saveRelatedReportInfo(iReportId,ids);
  }

  private Link getFontLink(String name,int columnInfoId,int iReportId){
    Link l = new Link(Edit.formatText(name));
    l.addParameter(PRM_REPORTID,iReportId);
    l.addParameter("col_nr",columnInfoId);
    return l;
  }

	private DropdownMenu getFamilyDrop(String name){
	  DropdownMenu drp = new DropdownMenu(name);
		drp.addMenuElement(String.valueOf(Font.HELVETICA),"HELVETICA");
		drp.addMenuElement(String.valueOf(Font.COURIER),"COURIER");
		drp.addMenuElement(String.valueOf(Font.TIMES_NEW_ROMAN),"TIMES_NEW_ROMAN");

		return drp;
	}

  private String getFontFamily(int family){
    switch (family) {
      case Font.HELVETICA: return "HELVETICA";
      case Font.COURIER: return "COURIER";
      case Font.TIMES_NEW_ROMAN: return "TIMES_NEW_ROMAN";
    }
    return "";
  }

  private DropdownMenu getColumnDrop(String name){
   DropdownMenu drp = new DropdownMenu(name);
		for (int i = 1; i < 4; i++) {
			drp.addMenuElement(String.valueOf(i));
		}

		return drp;
  }

	private DropdownMenu getSizeDrop(String name){
	  DropdownMenu drp = new DropdownMenu(name);
		for (int i = 8; i < 21; i++) {
			drp.addMenuElement(String.valueOf(i));
		}

		return drp;
	}
	private DropdownMenu getStyleDrop(String name){
	  DropdownMenu drp = new DropdownMenu(name);
		drp.addMenuElement(String.valueOf(Font.NORMAL),"NORMAL");
		drp.addMenuElement(String.valueOf(Font.BOLD),"BOLD");
		drp.addMenuElement(String.valueOf(Font.BOLDITALIC),"BOLDITALIC");
		drp.addMenuElement(String.valueOf(Font.ITALIC),"ITALIC");
		drp.addMenuElement(String.valueOf(Font.STRIKETHRU),"STRIKETHRU");
		drp.addMenuElement(String.valueOf(Font.UNDERLINE),"UNDERLINE");

		return drp;
	}
  private String getStyle(int style){
    switch (style) {
      case Font.NORMAL:   return "NORMAL";
      case Font.BOLD:   return "BOLD";
      case Font.BOLDITALIC:   return "BOLDITALIC";
      case Font.ITALIC:   return "ITALIC";
      case Font.STRIKETHRU:   return "STRIKETHRU";
      case Font.UNDERLINE:   return "UNDERLINE";
    }
    return "";

  }

  private DropdownMenu getEndCharDrop(String name){
	  DropdownMenu drp = new DropdownMenu(name);
		drp.addMenuElement("space","Space");
		drp.addMenuElement("newline","Newline");
		drp.addMenuElement("tab","Tab");
		drp.addMenuElement("tabtab","Two tabs");
		return drp;
	}

  private String getEndStringName(String endstring){
    if(endstring.equals("32"))
      return "space";
    else if(endstring.equals("10"))
      return "newline";
    else if(endstring.equals("\t"))
      return "tab";
    else if(endstring.equals("\t\t"))
      return "tabtab";
    return "";
  }

  private String getEndString(String stringName){
    if(stringName.equals("space"))
      return "Space";
    else if(stringName.equals("newline"))
      return "Newline";
    else if(stringName.equals("tab"))
      return "Tab";
    else if(stringName.equals("tabtab"))
      return "Double tab";
    return "";
  }

  private Link getPrintLink(Image image,int iReportId,int iReportInfoId){
    Link L = new Link( image );
    /*
    StringBuffer url = new StringBuffer("/servlet/MediaServlet?&");
    url.append(ReportWriter.prmReportId).append("=").append(iReportId);
    url.append("&").append(ReportWriter.prmReportInfoId).append("=").append(iReportInfoId);
    url.append("&").append(ReportWriter.PRM_WRITABLE_CLASS).append("=").append(IWMainApplication.getEncryptedClassName(ReportWriter.class));
    Window w = new Window("report",url.toString());
    */
    L.setWindowToOpen(ReportFileWindow.class);
    L.addParameter(PRM_REPORTID,iReportId);
    L.addParameter(ReportWriter.prmReportId,iReportId);
    L.addParameter(ReportWriter.prmReportInfoId,iReportInfoId);
    L.addParameter(ReportWriter.PRM_WRITABLE_CLASS,IWMainApplication.getEncryptedClassName(ReportWriter.class));
    return L;
  }

  private void print(Report eReport,ReportInfo reportInfo){

  }

}