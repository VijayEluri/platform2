package com.idega.xml;

import java.io.PrintStream;
import java.io.PrintWriter;

/**
 * Title:
 * Description:
 * Copyright:    Copyright (c) 2001
 * Company:      Idega hf
 * @author <a href="mail:palli@idega.is">Pall Helgason</a>
 * @version 1.0
 */

public class XMLException extends Exception {
	
  private Throwable _cause = null;
	
  public XMLException() {
    super();
  }

  public XMLException(String message) {
    super(message);
  }
  
  public XMLException(String message, Throwable cause) {
	super(message);
  }
  
  
	public void printStackTrace() { 
		super.printStackTrace();
		if(_cause != null){
			System.err.println("------ Root Cause -----");
			System.err.println(_cause.getMessage());
			_cause.printStackTrace();
		}
		
		
	}

	public void printStackTrace(PrintStream s) {
		super.printStackTrace(s);
		if(_cause != null){
			s.println("------ Root Cause -----");
			s.println(_cause.getMessage());
			_cause.printStackTrace(s);
		}
	}
  
  
	public void printStackTrace(PrintWriter s) { 
		super.printStackTrace(s);
		if(_cause != null){
			s.println("------ Root Cause -----");
			s.println(_cause.getMessage());
			_cause.printStackTrace(s);
		}
	}
  
  
}
