/*
 * Created on Jul 2, 2003
 */
package is.idega.idegaweb.member.isi.block.reports.business;

/**
 * Description: WorkReportImportException is thrown when an import exception is encountered such as if the import file is corrupt,missing etc.<br>
 * It is also a superclass for similar WorkReport related errors.
 * Copyright: Idega Software 2003 <br>
 * Company: Idega Software <br>
 * @author <a href="mailto:eiki@idega.is">Eirikur S. Hrafnsson</a>
 */
public class WorkReportImportException extends Exception {
	private String _col = null;
	private String _row = null;
	private String _detail = null;
	
	public WorkReportImportException() {
		super();
	}
	
	/**
	 * @param message
	 */
	public WorkReportImportException(String message) {
		super(message);
	}

	public WorkReportImportException(String message, String row, String col, String detail) {
		super(message);
		_col = col;
		_row = row;
		_detail = detail;
	}

	public WorkReportImportException(String message, int row, int col, String detail) {
		this(message,Integer.toString(row),Integer.toString(col),detail);	
	}
	
	public String getColumnForError() {
		return _col;
	}
	
	public void setColumnForError(String col) {
		_col = col;
	}
	
	public String getRowForError() {
		return _row;
	}
	
	public void setRowForError(String row) {
		_row = row;
	}
	
	public String getDetail() {
		return _detail;
	}
	
	public void setDetail(String detail) {
		_detail = detail;
	}
}