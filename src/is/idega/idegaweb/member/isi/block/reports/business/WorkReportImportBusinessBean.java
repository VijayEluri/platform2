/*
 * Created on Sep 18, 2003
 *
 * To change the template for this generated file go to
 * Window>Preferences>Java>Code Generation>Code and Comments
 */
package is.idega.idegaweb.member.isi.block.reports.business;

import is.idega.idegaweb.member.business.MemberUserBusiness;
import is.idega.idegaweb.member.business.MemberUserBusinessBean;
import is.idega.idegaweb.member.isi.block.reports.data.WorkReport;
import is.idega.idegaweb.member.isi.block.reports.data.WorkReportAccountKey;
import is.idega.idegaweb.member.isi.block.reports.data.WorkReportAccountKeyHome;
import is.idega.idegaweb.member.isi.block.reports.data.WorkReportBoardMember;
import is.idega.idegaweb.member.isi.block.reports.data.WorkReportBoardMemberHome;
import is.idega.idegaweb.member.isi.block.reports.data.WorkReportClubAccountRecord;
import is.idega.idegaweb.member.isi.block.reports.data.WorkReportClubAccountRecordHome;
import is.idega.idegaweb.member.isi.block.reports.data.WorkReportDivisionBoard;
import is.idega.idegaweb.member.isi.block.reports.data.WorkReportDivisionBoardHome;
import is.idega.idegaweb.member.isi.block.reports.data.WorkReportExportFile;
import is.idega.idegaweb.member.isi.block.reports.data.WorkReportExportFileHome;
import is.idega.idegaweb.member.isi.block.reports.data.WorkReportGroup;
import is.idega.idegaweb.member.isi.block.reports.data.WorkReportGroupHome;
import is.idega.idegaweb.member.isi.block.reports.data.WorkReportHome;
import is.idega.idegaweb.member.isi.block.reports.data.WorkReportImportBoardMember;
import is.idega.idegaweb.member.isi.block.reports.data.WorkReportImportBoardMemberHome;
import is.idega.idegaweb.member.isi.block.reports.data.WorkReportImportClubAccountRecord;
import is.idega.idegaweb.member.isi.block.reports.data.WorkReportImportClubAccountRecordHome;
import is.idega.idegaweb.member.isi.block.reports.data.WorkReportImportDivisionBoard;
import is.idega.idegaweb.member.isi.block.reports.data.WorkReportImportDivisionBoardHome;
import is.idega.idegaweb.member.isi.block.reports.data.WorkReportImportMember;
import is.idega.idegaweb.member.isi.block.reports.data.WorkReportImportMemberHome;
import is.idega.idegaweb.member.isi.block.reports.data.WorkReportMember;
import is.idega.idegaweb.member.isi.block.reports.data.WorkReportMemberHome;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.rmi.RemoteException;
import java.util.Collection;
import java.util.HashMap;
import java.util.Iterator;
import java.util.Map;
import java.util.Vector;

import javax.ejb.CreateException;
import javax.ejb.EJBException;
import javax.ejb.FinderException;
import javax.transaction.UserTransaction;

import org.apache.poi.hssf.usermodel.HSSFCell;
import org.apache.poi.hssf.usermodel.HSSFRow;
import org.apache.poi.hssf.usermodel.HSSFSheet;
import org.apache.poi.hssf.usermodel.HSSFWorkbook;

import com.idega.block.media.business.MediaBusiness;
import com.idega.core.file.data.ICFile;
import com.idega.core.file.data.ICFileHome;
import com.idega.core.location.data.PostalCode;
import com.idega.data.IDOAddRelationshipException;
import com.idega.data.IDOLookup;
import com.idega.data.IDORelationshipException;
import com.idega.user.data.User;
import com.idega.util.caching.Cache;
import com.idega.util.text.TextSoap;

/**
 * @author palli
 *
 * To change the template for this generated type comment go to
 * Window>Preferences>Java>Code Generation>Code and Comments
 */
public class WorkReportImportBusinessBean extends MemberUserBusinessBean implements MemberUserBusiness, WorkReportImportBusiness {

	private static int SHEET_BOARD_PART = 0;
	private static int SHEET_ACCOUNT_PART = 1;
	private static int SHEET_MEMBER_PART = 2;

	private WorkReportGroupHome workReportGroupHome;
	private WorkReportClubAccountRecordHome workReportClubAccountRecordHome;
	private WorkReportAccountKeyHome workReportAccountKeyHome;
	private WorkReportHome workReportHome;
	private WorkReportMemberHome workReportMemberHome;
	private WorkReportBoardMemberHome workReportBoardMemberHome;
	private WorkReportDivisionBoardHome workReportDivisionBoardHome;
	private WorkReportExportFileHome workReportExportFileHome;

	//Temporary import tables
	private WorkReportImportMemberHome workReportImportMemberHome;
	private WorkReportImportBoardMemberHome workReportImportBoardMemberHome;
	private WorkReportImportDivisionBoardHome workReportImportDivisionBoardHome;
	private WorkReportImportClubAccountRecordHome workReportImportClubAccountRecordHome;

	private static final short COLUMN_MEMBER_NAME = 0;
	private static final short COLUMN_MEMBER_SSN = 1;
	private static final short COLUMN_MEMBER_STREET_NAME = 2;
	private static final short COLUMN_MEMBER_POSTAL_CODE = 3;

	private static final short COLUMN_BOARD_MEMBER_LEAGUE = 0;
	private static final short COLUMN_BOARD_MEMBER_STATUS = 1;
	private static final short COLUMN_BOARD_MEMBER_SSN = 3;
	private static final short COLUMN_BOARD_MEMBER_STREET_NAME = 4;
	private static final short COLUMN_BOARD_MEMBER_POSTAL_CODE = 5;
	private static final short COLUMN_BOARD_MEMBER_PHONE_1 = 6;
	private static final short COLUMN_BOARD_MEMBER_PHONE_2 = 7;
	private static final short COLUMN_BOARD_MEMBER_FAX = 8;
	private static final short COLUMN_BOARD_MEMBER_EMAIL = 9;

	public static final String IW_BUNDLE_IDENTIFIER = "is.idega.idegaweb.member.isi";

	/**
	 * A method to import the account part of the ISI workreports. A bit of a hack, since the format 
	 * of the Excel file is "constant", and so this import assumes that the positions of the keys won't 
	 * change. This could lead to problems, but we will look the other way for now :)
	 */
	public boolean importAccountPart(int workReportFileId, int workReportId) throws WorkReportImportException, RemoteException {
		System.out.println("Starting account importing from excel file...");

		//Check to see if the work report is read only
		if (getWorkReportBusiness().isWorkReportReadOnly(workReportId))
			throw new WorkReportImportException("workreportimportexception.is_read_only");

		getWorkReportBusiness().deleteWorkReportAccountRecordsForReport(workReportId);

		WorkReportAccountKeyHome accKeyHome = getWorkReportBusiness().getWorkReportAccountKeyHome();
		WorkReportClubAccountRecordHome clubRecordHome = getWorkReportBusiness().getWorkReportClubAccountRecordHome();
		WorkReport report = getWorkReportBusiness().getWorkReportById(workReportId);
		int year = report.getYearOfReport().intValue();
		getWorkReportBusiness().createOrUpdateLeagueWorkReportGroupsForYear(year);

		report.setAccountFileId(workReportFileId);
		report.store();

		HSSFWorkbook excel = getExcelWorkBookFromFileId(workReportFileId);
		int sheets = excel.getNumberOfSheets();
		if (sheets > 3)
			throw new WorkReportImportException("workreportimportexception.wrong_number_of_sheets");

		HSSFSheet accEntries = excel.getSheetAt(SHEET_ACCOUNT_PART);
		int currRow = 2;
		int leaguesStartColumn = 7;
		int lastRow = accEntries.getLastRowNum();

		System.out.println("Current row is at: " + currRow);
		System.out.println("Last row is at: " + lastRow);

		if (lastRow != 42) {
			System.err.println("Wrong number of lines in account sheet " + lastRow);
			throw new WorkReportImportException("workreportimportexception.wrong_number_lines");
		}

		//get the top row to get a list of leagues to use.
		HSSFRow headerRow = (HSSFRow)accEntries.getRow(currRow);
		Map leaguesMap = getLeaguesMapFromRow(headerRow, year, leaguesStartColumn);

		int numberOfLeagues = 1;
		if (leaguesMap != null)
			numberOfLeagues = leaguesMap.size();

		String accKey = null;

		/**
		 * @todo hehe, put this into one submethod and then call if for each set of rows. Silly silly silly
		 */

		//Get the revenue part
		for (currRow++; currRow < 8; currRow++) {
			HSSFRow row = (HSSFRow)accEntries.getRow(currRow);
			HSSFCell cell = row.getCell((short)1);
			if (cell.getCellType() == HSSFCell.CELL_TYPE_NUMERIC)
				accKey = Integer.toString((int)cell.getNumericCellValue());
			else if (cell.getCellType() == HSSFCell.CELL_TYPE_STRING)
				accKey = cell.getStringCellValue();
			else
				accKey = null;

			WorkReportAccountKey eAccKey = null;
			try {
				eAccKey = (WorkReportAccountKey)accKeyHome.findAccountKeyByNumber(accKey);
			}
			catch (FinderException e) {
				e.printStackTrace();
				throw new WorkReportImportException("workreportimportexception.wrong_keys_in_sheet");
			}

			for (int i = 0; i < numberOfLeagues; i++) {
				HSSFCell c = row.getCell((short) (leaguesStartColumn + i));
				double val = 0.0;
				if (c != null) {
					if (c.getCellType() == HSSFCell.CELL_TYPE_FORMULA) {
						//						WorkReportImportException ex = new WorkReportImportException("workreportimportexception.formula_in_sheet");
						throw new WorkReportImportException("workreportimportexception.formula_in_sheet");
					}

					val = c.getNumericCellValue();
					System.out.println("celltype == " + c.getCellType());
				}

				System.out.println("val = " + val);

				WorkReportGroup league = (WorkReportGroup)leaguesMap.get(new Integer(leaguesStartColumn + i));

				if (val != 0.0) {
					try {
						WorkReportClubAccountRecord rec = clubRecordHome.create();
						rec.setAccountKey(eAccKey);
						rec.setWorkReportGroup(league);
						rec.setReportId(workReportId);
						rec.setAmount((float)val);
						rec.store();

						try {
							getWorkReportBusiness().getWorkReportById(workReportId).addLeague(league);
						}
						catch (Exception e) {
							//Do nothing
						}
					}
					catch (Exception e) {
						e.printStackTrace();
						throw new WorkReportImportException("workreportimportexception.unable_to_create_account_record");
					}
				}
			}
		}

		//Get the expenses part
		for (currRow = 11; currRow < 26; currRow++) {
			String parentKey = null;
			double sum = 0.0;
			if (currRow != 15) {
				HSSFRow row = (HSSFRow)accEntries.getRow(currRow);
				HSSFCell cell = row.getCell((short)1);
				if (cell.getCellType() == HSSFCell.CELL_TYPE_NUMERIC)
					accKey = Integer.toString((int)cell.getNumericCellValue());
				else if (cell.getCellType() == HSSFCell.CELL_TYPE_STRING)
					accKey = cell.getStringCellValue();
				else
					accKey = null;

				WorkReportAccountKey eAccKey = null;
				try {
					eAccKey = (WorkReportAccountKey)accKeyHome.findAccountKeyByNumber(accKey);
				}
				catch (FinderException e) {
					e.printStackTrace();
					throw new WorkReportImportException("workreportimportexception.wrong_keys_in_sheet");
				}

				for (int i = 0; i < numberOfLeagues; i++) {
					HSSFCell c = row.getCell((short) (leaguesStartColumn + i));
					double val = 0.0;
					if (c != null) {
						if (c.getCellType() == HSSFCell.CELL_TYPE_FORMULA) {
							//						WorkReportImportException ex = new WorkReportImportException("workreportimportexception.formula_in_sheet");
							throw new WorkReportImportException("workreportimportexception.formula_in_sheet");
						}

						val = c.getNumericCellValue();
					}

					System.out.println("val = " + val);
					if (currRow >= 16 && currRow <= 18)
						sum += val;
					WorkReportGroup league = (WorkReportGroup)leaguesMap.get(new Integer(leaguesStartColumn + i));
					if (val != 0.0) {
						try {
							WorkReportClubAccountRecord rec = clubRecordHome.create();
							rec.setAccountKey(eAccKey);
							rec.setWorkReportGroup(league);
							rec.setReportId(workReportId);
							rec.setAmount((float)val);
							rec.store();
							try {
								getWorkReportBusiness().getWorkReportById(workReportId).addLeague(league);
							}
							catch (Exception e) {
								//Do nothing
							}
							
							if (currRow == 18) {
								WorkReportAccountKey eParKey = null;
								try {
									eParKey = (WorkReportAccountKey)accKeyHome.findAccountKeyByNumber(parentKey);
								}
								catch (FinderException e) {
									e.printStackTrace();
									throw new WorkReportImportException("workreportimportexception.wrong_keys_in_sheet");
								}

								WorkReportClubAccountRecord rec2 = clubRecordHome.create();
								rec2.setAccountKey(eParKey);
								rec2.setWorkReportGroup(league);
								rec2.setReportId(workReportId);
								rec2.setAmount((float)sum);
								rec2.store();								
							}
						}
						catch (Exception e) {
							e.printStackTrace();
							throw new WorkReportImportException("workreportimportexception.unable_to_create_account_record");
						}
					}
				}
				
			}
			else {
				HSSFRow row = (HSSFRow)accEntries.getRow(currRow);
				HSSFCell cell = row.getCell((short)1);
				if (cell.getCellType() == HSSFCell.CELL_TYPE_NUMERIC)
					parentKey = Integer.toString((int)cell.getNumericCellValue());
				else if (cell.getCellType() == HSSFCell.CELL_TYPE_STRING)
					parentKey = cell.getStringCellValue();
				else
					parentKey = null;

			}
		}

		double totalAsset = 0;
		//Get the asset part
		for (currRow = 33; currRow < 35; currRow++) {
			HSSFRow row = (HSSFRow)accEntries.getRow(currRow);
			HSSFCell cell = row.getCell((short)1);
			if (cell.getCellType() == HSSFCell.CELL_TYPE_NUMERIC)
				accKey = Integer.toString((int)cell.getNumericCellValue());
			else if (cell.getCellType() == HSSFCell.CELL_TYPE_STRING)
				accKey = cell.getStringCellValue();
			else
				accKey = null;

			WorkReportAccountKey eAccKey = null;
			try {
				eAccKey = (WorkReportAccountKey)accKeyHome.findAccountKeyByNumber(accKey);
			}
			catch (FinderException e) {
				e.printStackTrace();
				throw new WorkReportImportException("workreportimportexception.wrong_keys_in_sheet");
			}

			for (int i = 0; i < numberOfLeagues; i++) {
				HSSFCell c = row.getCell((short) (leaguesStartColumn + i));
				double val = 0.0;
				if (c != null) {
					if (c.getCellType() == HSSFCell.CELL_TYPE_FORMULA) {
						//						WorkReportImportException ex = new WorkReportImportException("workreportimportexception.formula_in_sheet");
						throw new WorkReportImportException("workreportimportexception.formula_in_sheet");
					}

					val = c.getNumericCellValue();
				}

				System.out.println("val = " + val);
				totalAsset += val;
				WorkReportGroup league = (WorkReportGroup)leaguesMap.get(new Integer(leaguesStartColumn + i));
				if (val != 0.0) {
					try {
						WorkReportClubAccountRecord rec = clubRecordHome.create();
						rec.setAccountKey(eAccKey);
						rec.setWorkReportGroup(league);
						rec.setReportId(workReportId);
						rec.setAmount((float)val);
						rec.store();
						try {
							getWorkReportBusiness().getWorkReportById(workReportId).addLeague(league);
						}
						catch (Exception e) {
							//Do nothing
						}

					}
					catch (Exception e) {
						e.printStackTrace();
						throw new WorkReportImportException("workreportimportexception.unable_to_create_account_record");
					}
				}
			}
		}

		double totalDept = 0;
		//Get the dept part
		for (currRow = 38; currRow < 42; currRow++) {
			String parentKey = null;
			double sum = 0.0;
			if (currRow != 39) {
				HSSFRow row = (HSSFRow)accEntries.getRow(currRow);
				HSSFCell cell = row.getCell((short)1);
				if (cell.getCellType() == HSSFCell.CELL_TYPE_NUMERIC)
					accKey = Integer.toString((int)cell.getNumericCellValue());
				else if (cell.getCellType() == HSSFCell.CELL_TYPE_STRING)
					accKey = cell.getStringCellValue();
				else
					accKey = null;

				WorkReportAccountKey eAccKey = null;
				try {
					eAccKey = (WorkReportAccountKey)accKeyHome.findAccountKeyByNumber(accKey);
				}
				catch (FinderException e) {
					e.printStackTrace();
					throw new WorkReportImportException("workreportimportexception.wrong_keys_in_sheet");
				}

				for (int i = 0; i < numberOfLeagues; i++) {
					HSSFCell c = row.getCell((short) (leaguesStartColumn + i));
					double val = 0.0;
					if (c != null) {
						if (c.getCellType() == HSSFCell.CELL_TYPE_FORMULA) {
							//						WorkReportImportException ex = new WorkReportImportException("workreportimportexception.formula_in_sheet");
							throw new WorkReportImportException("workreportimportexception.formula_in_sheet");
						}

						val = c.getNumericCellValue();
					}

					System.out.println("val = " + val);
					if (currRow >= 40 && currRow <= 41)
						sum += val;
					totalDept += val;
					WorkReportGroup league = (WorkReportGroup)leaguesMap.get(new Integer(leaguesStartColumn + i));
					if (val != 0.0) {
						try {
							WorkReportClubAccountRecord rec = clubRecordHome.create();
							rec.setAccountKey(eAccKey);
							rec.setWorkReportGroup(league);
							rec.setReportId(workReportId);
							rec.setAmount((float)val);
							rec.store();
							try {
								getWorkReportBusiness().getWorkReportById(workReportId).addLeague(league);
							}
							catch (Exception e) {
								//Do nothing
							}

							if (currRow == 41) {
								WorkReportAccountKey eParKey = null;
								try {
									eParKey = (WorkReportAccountKey)accKeyHome.findAccountKeyByNumber(parentKey);
								}
								catch (FinderException e) {
									e.printStackTrace();
									throw new WorkReportImportException("workreportimportexception.wrong_keys_in_sheet");
								}

								WorkReportClubAccountRecord rec2 = clubRecordHome.create();
								rec2.setAccountKey(eParKey);
								rec2.setWorkReportGroup(league);
								rec2.setReportId(workReportId);
								rec2.setAmount((float)sum);
								rec2.store();								
							}
						}
						catch (Exception e) {
							e.printStackTrace();
							throw new WorkReportImportException("workreportimportexception.unable_to_create_account_record");
						}
					}
				}
			}
			else {
				HSSFRow row = (HSSFRow)accEntries.getRow(currRow);
				HSSFCell cell = row.getCell((short)1);
				if (cell.getCellType() == HSSFCell.CELL_TYPE_NUMERIC)
					parentKey = Integer.toString((int)cell.getNumericCellValue());
				else if (cell.getCellType() == HSSFCell.CELL_TYPE_STRING)
					parentKey = cell.getStringCellValue();
				else
					parentKey = null;
			}
		}

		if (totalAsset != totalDept)
			throw new WorkReportImportException("workreportimportexception.asset_and_dept_not_the_same");

		//Store work-report division thingie........ HOW!!!???!!!		
		Iterator it = leaguesMap.keySet().iterator();
		while (it.hasNext()) {
			Integer id = (Integer)it.next();

			WorkReportDivisionBoard board = null;
			try {
				board = (WorkReportDivisionBoard)getWorkReportBusiness().getWorkReportDivisionBoardHome().findWorkReportDivisionBoardByWorkReportIdAndWorkReportGroupId(workReportId, id.intValue());
			}
			catch (FinderException e) {
				try {
					board = (WorkReportDivisionBoard)getWorkReportBusiness().getWorkReportDivisionBoardHome().create();
					board.setGroupId(id.intValue());
				}
				catch (CreateException e1) {
					e1.printStackTrace();
					throw new WorkReportImportException("workreportimportexception.error_creating_division");
				}
			}

			try {
				board.store();
			}
			catch (Exception e) {

			}
		}

		return true;
	}

	/**
	 * A method to export the work reports to excel for those who are not using the member system.
	 * 
	 * @param regionalUnionId The id of the regional union who is to receive the file
	 * @param year The year we are creating excel files for
	 * @param templateId The id for the template for the excel files in the IW file system
	 *  
	 * @return The primary key of the ICFile entry created for the zip file, or -1 if nothing was created.
	 */
	public int exportToExcel(int regionalUnionId, int year, int templateId) throws WorkReportImportException {
		WorkReportExportFile export = null;

		try {
			export = getWorkReportExportFileHome().findWorkReportExportFileByGroupIdAndYear(regionalUnionId, year);

			if (export.getFileId() > 0) {
				return export.getFileId();
			}
		}
		catch (FinderException e) {
			System.out.println("[WorkReportBusinessBean] No report for groupId : " + regionalUnionId + " ann year : " + year + " creating a new one.");

			try {
				export = getWorkReportExportFileHome().create();
			}
			catch (CreateException e1) {
				e1.printStackTrace();
				throw new WorkReportImportException("workreportimportexception.unable_to_create_excel_file");
			}
		}

		if (export != null) {
			HSSFWorkbook workbook = getExcelWorkBookFromFileId(templateId);
			String fileName = Integer.toString(regionalUnionId) + "_" + Integer.toString(year) + ".xls";
			try {
				FileOutputStream out = new FileOutputStream(fileName);
				workbook.write(out);
				out.close();

				ICFile icfile = ((ICFileHome)IDOLookup.getHome(ICFile.class)).create();
				icfile.setFileValue(new FileInputStream(fileName));
				icfile.setName(fileName);
				icfile.store();

				export.setGroupId(regionalUnionId);
				export.setYear(year);
				export.setFile(icfile);
				export.store();
			}
			catch (FileNotFoundException e1) {
				e1.printStackTrace();
			}
			catch (IOException e1) {
				e1.printStackTrace();
			}
			catch (CreateException e1) {
				e1.printStackTrace();
			}

			//			try {
			//					// Create the ZIP file
			//					String outFilename = "outfile.zip";
			//					ZipOutputStream out = new ZipOutputStream(new FileOutputStream(outFilename));
			//
			//					// Compress the files
			//					for (int i=0; i<filenames.length; i++) {
			//							FileInputStream in = new FileInputStream(filenames[i]);
			//
			//							// Add ZIP entry to output stream.
			//							out.putNextEntry(new ZipEntry(filenames[i]));
			//
			//							// Transfer bytes from the file to the ZIP file
			//							int len;
			//							while ((len = in.read(buf)) > 0) {
			//									out.write(buf, 0, len);
			//							}
			//
			//							// Complete the entry
			//							out.closeEntry();
			//							in.close();
			//					}
			//
			//					// Complete the ZIP file
			//					out.close();
			//			} 
			//			catch (IOException e) {
			//			}

			return export.getFileId();
		}
		else {
			return -1;
		}
	}

	public boolean importBoardPart(int workReportFileId, int workReportId) throws WorkReportImportException, RemoteException {

		UserTransaction transaction;

		System.out.println("Starting board and division importing from excel file...");

		//Check to see if the work report is read only
		if (getWorkReportBusiness().isWorkReportReadOnly(workReportId))
			throw new WorkReportImportException("workreportimportexception.is_read_only");

		getWorkReportBusiness().deleteWorkReportBoardMembersForReport(workReportId);

		WorkReportBoardMemberHome membHome = getWorkReportBusiness().getWorkReportBoardMemberHome();
		WorkReport report = getWorkReportBusiness().getWorkReportById(workReportId);
		int year = report.getYearOfReport().intValue();
		getWorkReportBusiness().createOrUpdateLeagueWorkReportGroupsForYear(year);

		report.setBoardFileId(workReportFileId);
		report.store();

		HSSFWorkbook excel = getExcelWorkBookFromFileId(workReportFileId);
		int sheets = excel.getNumberOfSheets();
		if (sheets > 3)
			throw new WorkReportImportException("workreportimportexception.wrong_number_of_sheets");

		HSSFSheet members = excel.getSheetAt(SHEET_BOARD_PART);
		int firstRow = 6;
		int lastRow = members.getLastRowNum();

		System.out.println("First row is at: " + firstRow);
		System.out.println("Last row is at: " + lastRow);

		//iterate through the rows that contain the actual data and create the records in the database
		int i = firstRow;
		while (i <= lastRow) {

			HSSFRow row = (HSSFRow)members.getRow(i);

			if (row != null) {
				int firstCell = row.getFirstCellNum();
				int lastCell = row.getLastCellNum();

				String league = this.getStringValueFromExcelNumberOrStringCell(row, COLUMN_BOARD_MEMBER_LEAGUE);
				//Got to division part without finding a delimiter
				if (league != null && "Deild".equals(league.trim())) {
					throw new WorkReportImportException("workreportimportexception.missing_delimiter");
				}

				//Go on to division part?
				if (league.indexOf("##") != -1) {
					break;
				}

				WorkReportGroup group = null;
				if (league != null && !"".equals(league.trim())) {
					league = league.toUpperCase();
					try {
						group = getWorkReportBusiness().getWorkReportGroupHome().findWorkReportGroupByShortNameAndYear(league, year);
					}
					catch (FinderException e) {
						e.printStackTrace();
						System.err.println("WorkReportGroup not found by short name : " + league + " trying group name");

						try {
							group = getWorkReportBusiness().getWorkReportGroupHome().findWorkReportGroupByNameAndYear(league, year);
						}
						catch (FinderException e1) {
							throw new WorkReportImportException("workreportimportexception.league_not_found");
						}
					}
				}

				try {
					report.addLeague(group);
				}
				catch (IDORelationshipException e5) {
				}

				String status = getStringValueFromExcelNumberOrStringCell(row, COLUMN_BOARD_MEMBER_STATUS);
				String ssn = getStringValueFromExcelNumberOrStringCell(row, COLUMN_BOARD_MEMBER_SSN);
				ssn = TextSoap.findAndCut(ssn, "-");				
				ssn = (ssn.length() < 10) ? "0" + ssn : ssn;
				String streetName = getStringValueFromExcelNumberOrStringCell(row, COLUMN_BOARD_MEMBER_STREET_NAME);
				String postalCode = getStringValueFromExcelNumberOrStringCell(row, COLUMN_BOARD_MEMBER_POSTAL_CODE);
				String phone1 = getStringValueFromExcelNumberOrStringCell(row, COLUMN_BOARD_MEMBER_PHONE_1);
				String phone2 = getStringValueFromExcelNumberOrStringCell(row, COLUMN_BOARD_MEMBER_PHONE_2);
				String fax = getStringValueFromExcelNumberOrStringCell(row, COLUMN_BOARD_MEMBER_FAX);
				String email = getStringValueFromExcelNumberOrStringCell(row, COLUMN_BOARD_MEMBER_EMAIL);

				WorkReportBoardMember member;

				try {
					//the user must already exist in the database
					User user = getUser(ssn);
					try {
//						member = membHome.findWorkReportBoardMemberByUserIdAndWorkReportId(((Integer)user.getPrimaryKey()).intValue(), workReportId);
						member = membHome.findWorkReportBoardMemberByUserIdAndWorkReportIdAndLeagueId(((Integer)user.getPrimaryKey()).intValue(), workReportId, ((Integer)group.getPrimaryKey()).intValue());
//						member.store();
					}
					catch (FinderException e4) {
						//this should happen, we don't want them created twice	
						member = getWorkReportBusiness().createWorkReportBoardMember(workReportId, ssn, group); //sets basic data
						member.setPersonalId(ssn);
						member.setReportId(workReportId);
						

						if (streetName != null && !"".equals(streetName)) {
							member.setStreetName(streetName);

							try {
								PostalCode postal = getAddressBusiness().getPostalCodeHome().findByPostalCodeAndCountryId(postalCode, ((Integer)getAddressBusiness().getCountryHome().findByCountryName("Iceland").getPrimaryKey()).intValue());
								member.setPostalCode(postal);
							}
							catch (FinderException e3) {
								//e3.printStackTrace();
							}
							catch (RemoteException e) {
								e.printStackTrace();
							}
						}

						if (phone1 != null && !"".equals(phone1.trim())) {
							member.setHomePhone(phone1);
						}

						if (phone2 != null && !"".equals(phone2.trim())) {
							member.setWorkPhone(phone2);
						}

						if (fax != null && !"".equals(fax.trim())) {
							member.setFax(fax);
						}

						if (email != null && !"".equals(email.trim())) {
							member.setEmail(email);
						}
						
						
						/**
						 * @TODO Palli is this ok?
						 */
						if (status != null && !"".equals(status.trim())) {
							member.setStatus(status);
						}

						member.store();
					}
				}
				catch (EJBException e1) {
					e1.printStackTrace();
				}
				catch (CreateException e2) {
					e2.printStackTrace();
					System.err.println("Failed to create user for ssn : " + ssn);
				}
				catch (FinderException e) {
					System.err.println("User not found for ssn : " + ssn);
				}
			}

			i++;
		}

		//Division part
		i += 7;
		while (i <= lastRow) {
			HSSFRow row = (HSSFRow)members.getRow(i);

			WorkReportGroup group = null;
			if (row != null) {
				String league = getStringValueFromExcelNumberOrStringCell(row, (short)0);
				if (league != null && !"".equals(league.trim())) {
					league = league.toUpperCase();
					try {
						group = getWorkReportBusiness().getWorkReportGroupHome().findWorkReportGroupByShortNameAndYear(league, year);
					}
					catch (FinderException e) {
						e.printStackTrace();
						System.err.println("WorkReportGroup not found by short name : " + league + " trying group name");

						try {
							group = getWorkReportBusiness().getWorkReportGroupHome().findWorkReportGroupByNameAndYear(league, year);
						}
						catch (FinderException e1) {
							throw new WorkReportImportException("workreportimportexception.league_not_found");
						}
					}
				}
				else {
					break;
				}

				try {
					report.addLeague(group);
				}
				catch (IDORelationshipException e5) {
				}

				String homePage = getStringValueFromExcelNumberOrStringCell(row, (short)2);
				String ssn = getStringValueFromExcelNumberOrStringCell(row, (short)3);
				String address = getStringValueFromExcelNumberOrStringCell(row, (short)4);
				String pnr = getStringValueFromExcelNumberOrStringCell(row, (short)5);
				String tel1 = getStringValueFromExcelNumberOrStringCell(row, (short)6);
				String tel2 = getStringValueFromExcelNumberOrStringCell(row, (short)7);
				String fax = getStringValueFromExcelNumberOrStringCell(row, (short)8);

				String email = getStringValueFromExcelNumberOrStringCell(row, (short)9);
				String champ = getStringValueFromExcelNumberOrStringCell(row, (short)10);

				WorkReportDivisionBoard board = null;
				try {
					board = (WorkReportDivisionBoard)getWorkReportBusiness().getWorkReportDivisionBoardHome().findWorkReportDivisionBoardByWorkReportIdAndWorkReportGroupId(workReportId, ((Integer)group.getPrimaryKey()).intValue());
				}
				catch (FinderException e) {
					try {
						e.printStackTrace();
						board = (WorkReportDivisionBoard)getWorkReportBusiness().getWorkReportDivisionBoardHome().create();
					}
					catch (CreateException e1) {
						e1.printStackTrace();
						throw new WorkReportImportException("workreportimportexception.error_creating_division");
					}
				}

				board.setHomePage(homePage);
				board.setPersonalId(ssn);
				board.setStreetName(address);
				try {
					PostalCode postal = getAddressBusiness().getPostalCodeHome().findByPostalCodeAndCountryId(pnr, ((Integer)getAddressBusiness().getCountryHome().findByCountryName("Iceland").getPrimaryKey()).intValue());
					board.setPostalCode(postal);
				}
				catch (FinderException e3) {
					//e3.printStackTrace();
				}
				catch (RemoteException e) {
					e.printStackTrace();
				}
				board.setFirstPhone(tel1);
				board.setSecondPhone(tel2);
				board.setFax(fax);
				board.setEmail(email);
				board.setWorKReportGroupID(((Integer)group.getPrimaryKey()).intValue());
				board.setReportId(workReportId);
				if (champ != null && !"".equals(champ.trim()))
					board.setHasNationalLeague(true);
				else
					board.setHasNationalLeague(false);
				board.store();
			}
			i++;
		}

		return true;
	}

	public WorkReportImportReport importMemberPart(int workReportFileId, int workReportId, String mainBoardName) throws WorkReportImportException, RemoteException {
		int memberCount = 0;
		int playerCount = 0;
		Map divPlayerCount = new HashMap();
		Collection notRead = new Vector();
		System.out.println("Starting member importing from excel file for workreportid: " + workReportId);

		//Check to see if the work report is read only
		if (getWorkReportBusiness().isWorkReportReadOnly(workReportId))
			throw new WorkReportImportException("workreportimportexception.is_read_only");

		//clear the table first
		getWorkReportBusiness().deleteWorkReportMembersForReport(workReportId);

		WorkReportMemberHome membHome = getWorkReportBusiness().getWorkReportMemberHome();
		WorkReport report = getWorkReportBusiness().getWorkReportById(workReportId);
		int year = report.getYearOfReport().intValue();

		//update or create the league groups so we can connect the users to them
		getWorkReportBusiness().createOrUpdateLeagueWorkReportGroupsForYear(year);

		report.setMemberFileId(workReportFileId);
		report.store();

		HSSFWorkbook excel = getExcelWorkBookFromFileId(workReportFileId);
		int sheets = excel.getNumberOfSheets();
		if (sheets > 3)
			throw new WorkReportImportException("workreportimportexception.wrong_number_of_sheets");

		HSSFSheet members = excel.getSheetAt(SHEET_MEMBER_PART);
		int firstRow = 4;
		int lastRow = members.getLastRowNum();

		//System.out.println("First row is at: "+firstRow);
		//System.out.println("Last row is at: "+lastRow);

		//get the top row to get a list of leagues to use.
		HSSFRow headerRow = (HSSFRow)members.getRow(firstRow);
		Map leaguesMap = getLeaguesMapFromRow(headerRow, year, 5);

		//iterate through the rows that contain the actual data and create the records in the database
		for (int i = (firstRow + 1); i <= lastRow; i++) {
			HSSFRow row = (HSSFRow)members.getRow(i);

			if (row != null) {
				int firstCell = row.getFirstCellNum();
				int lastCell = row.getLastCellNum();

				//String name = HSSFCellUtil.translateUnicodeValues(row.getCell(COLUMN_MEMBER_NAME)).getStringCellValue();
				String name = row.getCell(COLUMN_MEMBER_NAME).getStringCellValue();
				String ssn = getStringValueFromExcelNumberOrStringCell(row, COLUMN_MEMBER_SSN);
				ssn = TextSoap.findAndCut(ssn, "-");
				ssn = (ssn.length() < 10) ? "0" + ssn : ssn;
				String first_name = "";
				try {
					if (name != null) {
				 		int first_space = name.indexOf(" ");
						first_name = name.substring(0,first_space);
					}
				}
				catch(Exception e) {
					//Who cares!
				}
				
				String streetName = getStringValueFromExcelNumberOrStringCell(row, COLUMN_MEMBER_STREET_NAME);
				String postalCode = getStringValueFromExcelNumberOrStringCell(row, COLUMN_MEMBER_POSTAL_CODE);

				try {
					//the user must already exist in the database
					User user = null;
					try {
						user = getUser(ssn);
					}
					catch(Exception e) {
						user = getUserByPartOfPersonalIdAndFirstName(ssn,first_name);
					}
					
					try {
						membHome.findWorkReportMemberByUserIdAndWorkReportId(((Integer)user.getPrimaryKey()).intValue(), workReportId);
					}
					catch (FinderException e4) {
						//this should happen, we don't want them created twice	

						WorkReportMember member = getWorkReportBusiness().createWorkReportMember(workReportId, ssn); //sets basic data
						if (streetName != null && !"".equals(streetName)) {
							member.setStreetName(streetName);

							try {
								PostalCode postal = getAddressBusiness().getPostalCodeHome().findByPostalCodeAndCountryId(postalCode, ((Integer)getAddressBusiness().getCountryHome().findByCountryName("Iceland").getPrimaryKey()).intValue());
								member.setPostalCode(postal);
							}
							catch (FinderException e3) {
								//e3.printStackTrace();
							}
							catch (RemoteException e) {
								e.printStackTrace();
							}

						}

						member.store();

						memberCount++;

						WorkReportGroup mainBoard = null;
						try {
							mainBoard = getWorkReportBusiness().getWorkReportGroupHome().findWorkReportGroupByNameAndYear(mainBoardName, year);
						}
						catch (FinderException e1) {
							throw new WorkReportImportException("workreportimportexception.main_board_not_found");
						}

						//find which leagues the member belongs to
						//and create the many to many connections
						for (int j = 5; j < lastCell; j++) {
							HSSFCell leagueCell = row.getCell((short)j);

							if (leagueCell != null) {
								String check = leagueCell.getStringCellValue();
								//								boolean isChecked = (check != null && !"".equals(check) && "X".equals(check.toUpperCase()));
								boolean isChecked = (check != null && !"".equals(check));
								if (isChecked) {
									WorkReportGroup league = (WorkReportGroup)leaguesMap.get(new Integer(j));
									if (league != null) {
										try {
											league.addEntity(member);
											try {
												report.addLeague(league);
											}
											catch (Exception e) {
												//e.printStackTrace();
											}
											try {
												mainBoard.addEntity(member);
											}
											catch (Exception e) {
												e.printStackTrace();
											}
											playerCount++;
											Integer count = (Integer)divPlayerCount.get(new Integer(j));
											if (count == null)
												count = new Integer(1);
											else
												count = new Integer(count.intValue() + 1);

											divPlayerCount.put(new Integer(j), count);
										}
										catch (IDOAddRelationshipException e5) {
											e5.printStackTrace();
											throw new WorkReportImportException("workreportimportexception.database_error_could_not_add_member_to_group");
										}
									}
								}
							}

						}
					}
				}
				catch (EJBException e1) {
					e1.printStackTrace();
					throw new WorkReportImportException("workreportimportexception.database_error");
				}
				catch (CreateException e2) {
					//failed to create move on.
					e2.printStackTrace();
					System.err.println("Failed to create user for ssn : " + ssn);
					throw new WorkReportImportException("workreportimportexception.database_error_failed_to_create_user");
				}
				catch (FinderException e) {
					System.err.println("User not found for ssn : " + ssn + " skipping...");
					notRead.add(ssn);
				}
			}
		}

		report.setNumberOfMembers(memberCount);
		report.setNumberOfPlayers(playerCount);
		report.store();

		//Store work-report division thingie........ HOW!!!???!!!		
		Iterator it = leaguesMap.keySet().iterator();
		while (it.hasNext()) {
			Integer id = (Integer)it.next();
			Integer val = (Integer)divPlayerCount.get(id);

			WorkReportDivisionBoard board = null;
			try {
				board = (WorkReportDivisionBoard)getWorkReportBusiness().getWorkReportDivisionBoardHome().findWorkReportDivisionBoardByWorkReportIdAndWorkReportGroupId(workReportId, id.intValue());
			}
			catch (FinderException e) {
				e.printStackTrace();
				try {
					board = (WorkReportDivisionBoard)getWorkReportBusiness().getWorkReportDivisionBoardHome().create();
				}
				catch (CreateException e1) {
					e1.printStackTrace();
					throw new WorkReportImportException("workreportimportexception.error_creating_division");
				}
			}

			board.setReportId(workReportId);
			board.setWorKReportGroupID(id.intValue());
			if (val != null)
				board.setNumberOfPlayers(val.intValue());
			else
				board.setNumberOfPlayers(0);
			board.store();
		}

		WorkReportImportReport ret = new WorkReportImportReport();
		ret.numberOfMembers = memberCount;
		ret.numberOfPlayers = playerCount;
		ret.leaguesMap = leaguesMap;
		ret.playerCountPrLeague = divPlayerCount;
		ret.notRead = notRead;
	
		return ret;
	}

	public WorkReportImportClubAccountRecordHome getWorkReportImportClubAccountRecordHome() {
		if (workReportImportClubAccountRecordHome == null) {
			try {
				workReportImportClubAccountRecordHome = (WorkReportImportClubAccountRecordHome)IDOLookup.getHome(WorkReportImportClubAccountRecord.class);
			}
			catch (RemoteException rme) {
				throw new RuntimeException(rme.getMessage());
			}
		}
		return workReportImportClubAccountRecordHome;
	}

	public WorkReportExportFileHome getWorkReportExportFileHome() {
		if (workReportExportFileHome == null) {
			try {
				workReportExportFileHome = (WorkReportExportFileHome)IDOLookup.getHome(WorkReportExportFile.class);
			}
			catch (RemoteException rme) {
				throw new RuntimeException(rme.getMessage());
			}
		}
		return workReportExportFileHome;
	}

	public WorkReportImportMemberHome getWorkReportImportMemberHome() {
		if (workReportImportMemberHome == null) {
			try {
				workReportImportMemberHome = (WorkReportImportMemberHome)IDOLookup.getHome(WorkReportImportMember.class);
			}
			catch (RemoteException rme) {
				throw new RuntimeException(rme.getMessage());
			}
		}
		return workReportImportMemberHome;
	}

	public WorkReportImportBoardMemberHome getWorkReportImportBoardMemberHome() {
		if (workReportImportBoardMemberHome == null) {
			try {
				workReportImportBoardMemberHome = (WorkReportImportBoardMemberHome)IDOLookup.getHome(WorkReportImportBoardMember.class);
			}
			catch (RemoteException rme) {
				throw new RuntimeException(rme.getMessage());
			}
		}
		return workReportImportBoardMemberHome;
	}

	public WorkReportImportDivisionBoardHome getWorkReportImportDivisionBoardHome() {
		if (workReportImportDivisionBoardHome == null) {
			try {
				workReportImportDivisionBoardHome = (WorkReportImportDivisionBoardHome)IDOLookup.getHome(WorkReportImportDivisionBoard.class);
			}
			catch (RemoteException rme) {
				throw new RuntimeException(rme.getMessage());
			}
		}
		return workReportImportDivisionBoardHome;
	}

	/**
	 * Gets the cell value as string even if it is a number and strips all ".", "-" and trailing exponent "098098809E8"
	 * @param row object
	 * @param column number
	 * @return
	 */
	private String getStringValueFromExcelNumberOrStringCell(HSSFRow row, short columnNumber) {
		String cell = "";
		HSSFCell myCell = row.getCell((short)columnNumber);
		if (myCell != null) {
			if (myCell.getCellType() == HSSFCell.CELL_TYPE_STRING) {
				//HSSFCell myCell2 = HSSFCellUtil.translateUnicodeValues(myCell);
				cell = myCell.getStringCellValue();
			}
			else {
				double d = myCell.getNumericCellValue();
				if (((int)d) != 0)
					cell = Integer.toString((int)d);
				cell = TextSoap.findAndCut(cell, "-");
				cell = TextSoap.findAndCut(cell, ".");

				int index = cell.indexOf("E");

				if (index != -1) {
					cell = cell.substring(0, index);
				}
			}
		}

		return cell;
	}

	protected WorkReportBusiness getWorkReportBusiness() throws RemoteException {
		return (WorkReportBusiness)this.getServiceInstance(WorkReportBusiness.class);
	}

	private HSSFWorkbook getExcelWorkBookFromFileId(int fileId) throws WorkReportImportException {
		HSSFWorkbook excel = null;
		File file = getFileObjectForFileId(fileId);

		try {
			excel = new HSSFWorkbook(new FileInputStream(file));
		}
		catch (FileNotFoundException e) {
			e.printStackTrace();
			throw new WorkReportImportException("workreportimportexception.file_not_found");
		}
		catch (IOException e) {
			e.printStackTrace();
			throw new WorkReportImportException("workreportimportexception.could_not_read_file");
		}

		return excel;
	}

	private File getFileObjectForFileId(int fileId) {
		Cache file = MediaBusiness.getCachedFileInfo(fileId, this.getIWApplicationContext().getApplication());

		return new File(file.getRealPathToFile());
	}

	/**
	 * @param headerRow, the first row of the members-part worksheet
	 */
	private Map getLeaguesMapFromRow(HSSFRow headerRow, int year, int startColumn) throws WorkReportImportException {
		Map leagues = new HashMap();
		int lastCell = headerRow.getLastCellNum();
		WorkReportGroupHome home = null;
		try {
			home = getWorkReportBusiness().getWorkReportGroupHome();
		}
		catch (RemoteException e2) {
			e2.printStackTrace();
		}

		for (int j = startColumn; j < lastCell; j++) {
			HSSFCell cell = headerRow.getCell((short)j);
			if (cell != null) {
				String leagueName = cell.getStringCellValue();
				//stupid framework returns "null" as a string
				if (leagueName != null && !"".equals(leagueName)) {
					String shortName = null;
					String name = null;
					int index = leagueName.indexOf('-');
					shortName = (index != -1) ? leagueName.substring(0, index) : leagueName;
					name = (index != -1) ? leagueName.substring(index, leagueName.length()) : leagueName;

					shortName = shortName.toUpperCase();
					name = name.toUpperCase();

					WorkReportGroup group = null;

					try {
						group = getWorkReportBusiness().getWorkReportGroupHome().findWorkReportGroupByShortNameAndYear(shortName, year);
					}
					catch (FinderException e) {
						e.printStackTrace();
						System.err.println("WorkReportGroup not found by short name : " + shortName + " trying group name");

						try {
							group = getWorkReportBusiness().getWorkReportGroupHome().findWorkReportGroupByNameAndYear(name, year);
						}
						catch (FinderException e1) {
							throw new WorkReportImportException("workreportimportexception.league_not_found");
						}
						catch (RemoteException e1) {
							e.printStackTrace();
						}

					}
					catch (RemoteException e) {
						e.printStackTrace();
					}

					if (group != null) {
						leagues.put(new Integer(j), group);
					}
				}

			}

		}

		return leagues;
	}
}