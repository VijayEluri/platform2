package com.idega.io;

import java.io.BufferedInputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;

import com.idega.idegaweb.IWApplicationContext;
import com.idega.util.xml.XMLData;

/**
 * <p>Title: idegaWeb</p>
 * <p>Description: </p>
 * <p>Copyright: Copyright (c) 2003</p>
 * <p>Company: idega Software</p>
 * @author <a href="thomas@idega.is">Thomas Hilbig</a>
 * @version 1.0
 * Created on Apr 1, 2004
 */
public class XMLDataReader extends ReaderFromFile {
	
	XMLDataReader(IWApplicationContext iwac) {
		super(iwac);
	}
	
	XMLDataReader(Storable storable, IWApplicationContext iwac) {
		super(storable, iwac);
	}
	/* (non-Javadoc)
	 * @see com.idega.io.ReaderFromFile#openContainer(java.io.File)
	 */
	public void openContainer(File file) throws IOException {
		readData(new BufferedInputStream(new FileInputStream(file)));
	}
	/* (non-Javadoc)
	 * @see com.idega.io.ReaderFromFile#setName(java.lang.String)
	 */
	public void setName(String name) {
		((XMLData) storable).setName(name);
	}
	/* (non-Javadoc)
	 * @see com.idega.io.ReaderFromFile#setMimeType(java.lang.String)
	 */
	public void setMimeType(String mimeType) {
		// ignored, mimetype "text/xml" is not changeable
	}
	/* (non-Javadoc)
	 * @see com.idega.io.ReaderFromFile#readData(java.io.InputStream)
	 */
	public InputStream readData(InputStream source) throws IOException {
		((XMLData) storable).initialize(source);
		return source;
	}
}
