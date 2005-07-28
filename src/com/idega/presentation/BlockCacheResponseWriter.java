/*
 * $Id: BlockCacheResponseWriter.java,v 1.1 2005/07/28 18:06:30 tryggvil Exp $
 * Created on 19.7.2005 in project com.idega.core
 *
 * Copyright (C) 2005 Idega Software hf. All Rights Reserved.
 *
 * This software is the proprietary information of Idega hf.
 * Use is subject to license terms.
 */
package com.idega.presentation;

import java.io.IOException;
import java.io.Writer;
import javax.faces.component.UIComponent;
import javax.faces.context.ResponseWriter;


/**
 * <p>
 * A JSF ResponseWriter implementation to work with the Block 
 *  (IWCacheManager) Cache system in JSF.
 * </p>
 *  Last modified: $Date: 2005/07/28 18:06:30 $ by $Author: tryggvil $
 * 
 * @author <a href="mailto:tryggvil@idega.com">tryggvil</a>
 * @version $Revision: 1.1 $
 */
public class BlockCacheResponseWriter extends ResponseWriter {

	private ResponseWriter underlying;
	private StringBuffer buffer;
	
	/**
	 * 
	 */
	public BlockCacheResponseWriter(ResponseWriter underlying,StringBuffer buffer) {
		this.underlying=underlying;
		this.buffer=buffer;
	}

	/* (non-Javadoc)
	 * @see javax.faces.context.ResponseWriter#cloneWithWriter(java.io.Writer)
	 */
	public ResponseWriter cloneWithWriter(Writer arg0) {
		return underlying.cloneWithWriter(arg0);
	}

	/* (non-Javadoc)
	 * @see java.io.Writer#close()
	 */
	public void close() throws IOException {
		underlying.close();
	}

	/* (non-Javadoc)
	 * @see javax.faces.context.ResponseWriter#endDocument()
	 */
	public void endDocument() throws IOException {
		underlying.endDocument();
	}

	/* (non-Javadoc)
	 * @see javax.faces.context.ResponseWriter#endElement(java.lang.String)
	 */
	public void endElement(String arg0) throws IOException {
//		TODO: Implement writing to the cache buffer
		underlying.endElement(arg0);
	}

	/* (non-Javadoc)
	 * @see java.lang.Object#equals(java.lang.Object)
	 */
	public boolean equals(Object arg0) {
		return underlying.equals(arg0);
	}

	/* (non-Javadoc)
	 * @see javax.faces.context.ResponseWriter#flush()
	 */
	public void flush() throws IOException {
		underlying.flush();
	}

	/* (non-Javadoc)
	 * @see javax.faces.context.ResponseWriter#getCharacterEncoding()
	 */
	public String getCharacterEncoding() {
		return underlying.getCharacterEncoding();
	}

	/* (non-Javadoc)
	 * @see javax.faces.context.ResponseWriter#getContentType()
	 */
	public String getContentType() {
		return underlying.getContentType();
	}

	/* (non-Javadoc)
	 * @see java.lang.Object#hashCode()
	 */
	public int hashCode() {
		return underlying.hashCode();
	}

	/* (non-Javadoc)
	 * @see javax.faces.context.ResponseWriter#startDocument()
	 */
	public void startDocument() throws IOException {
		underlying.startDocument();
	}

	/* (non-Javadoc)
	 * @see javax.faces.context.ResponseWriter#startElement(java.lang.String, javax.faces.component.UIComponent)
	 */
	public void startElement(String arg0, UIComponent arg1) throws IOException {
//		TODO: Implement writing to the cache buffer
		underlying.startElement(arg0, arg1);
	}

	/* (non-Javadoc)
	 * @see java.lang.Object#toString()
	 */
	public String toString() {
		return underlying.toString();
	}

	/* (non-Javadoc)
	 * @see java.io.Writer#write(char[], int, int)
	 */
	public void write(char[] arg0, int arg1, int arg2) throws IOException {
		buffer.append(arg0);
		underlying.write(arg0, arg1, arg2);
	}

	/* (non-Javadoc)
	 * @see java.io.Writer#write(char[])
	 */
	public void write(char[] arg0) throws IOException {
		buffer.append(arg0);
		underlying.write(arg0);
	}

	/* (non-Javadoc)
	 * @see java.io.Writer#write(int)
	 */
	public void write(int arg0) throws IOException {
		buffer.append(arg0);
		underlying.write(arg0);
	}

	/* (non-Javadoc)
	 * @see java.io.Writer#write(java.lang.String, int, int)
	 */
	public void write(String arg0, int arg1, int arg2) throws IOException {
		buffer.append(arg0);
		underlying.write(arg0, arg1, arg2);
	}

	/* (non-Javadoc)
	 * @see java.io.Writer#write(java.lang.String)
	 */
	public void write(String arg0) throws IOException {
		buffer.append(arg0);
		underlying.write(arg0);
	}

	/* (non-Javadoc)
	 * @see javax.faces.context.ResponseWriter#writeAttribute(java.lang.String, java.lang.Object, java.lang.String)
	 */
	public void writeAttribute(String arg0, Object arg1, String arg2) throws IOException {
		//TODO: Implement writing to the cache buffer
		underlying.writeAttribute(arg0, arg1, arg2);
	}

	/* (non-Javadoc)
	 * @see javax.faces.context.ResponseWriter#writeComment(java.lang.Object)
	 */
	public void writeComment(Object arg0) throws IOException {
		//TODO: Implement writing to the cache buffer
		underlying.writeComment(arg0);
	}

	/* (non-Javadoc)
	 * @see javax.faces.context.ResponseWriter#writeText(char[], int, int)
	 */
	public void writeText(char[] arg0, int arg1, int arg2) throws IOException {
		buffer.append(arg0);
		underlying.writeText(arg0, arg1, arg2);
	}

	/* (non-Javadoc)
	 * @see javax.faces.context.ResponseWriter#writeText(java.lang.Object, java.lang.String)
	 */
	public void writeText(Object arg0, String arg1) throws IOException {
		buffer.append(arg0);
		underlying.writeText(arg0, arg1);
	}

	/* (non-Javadoc)
	 * @see javax.faces.context.ResponseWriter#writeURIAttribute(java.lang.String, java.lang.Object, java.lang.String)
	 */
	public void writeURIAttribute(String arg0, Object arg1, String arg2) throws IOException {
//		TODO: Implement writing to the cache buffer
		underlying.writeURIAttribute(arg0, arg1, arg2);
	}
}
