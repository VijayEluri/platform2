package com.idega.builder.io;

import java.io.BufferedOutputStream;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.OutputStream;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.zip.ZipEntry;
import java.util.zip.ZipOutputStream;

import com.idega.builder.data.IBExportImportData;
import com.idega.core.file.data.ICFile;
import com.idega.io.ZipOutputStreamIgnoreClose;
import com.idega.io.serialization.FileObjectWriter;
import com.idega.io.serialization.ICFileWriter;
import com.idega.io.serialization.ObjectWriter;
import com.idega.io.serialization.Storable;
import com.idega.io.serialization.WriterToFile;
import com.idega.io.serialization.XMLDataWriter;
import com.idega.presentation.IWContext;
import com.idega.util.StringHandler;
import com.idega.util.xml.XMLData;
import com.idega.xml.XMLElement;


/**
 * <p>Title: idegaWeb</p>
 * <p>Description: </p>
 * <p>Copyright: Copyright (c) 2003</p>
 * <p>Company: idega Software</p>
 * @author <a href="thomas@idega.is">Thomas Hilbig</a>
 * @version 1.0
 * Created on Mar 18, 2004
 */
public class IBExportImportDataWriter extends WriterToFile implements ObjectWriter {
	
	private static final String ZIP_EXTENSION = "zip";
	private static final String ZIP_ELEMENT_DELIMITER = "_";
	
	public IBExportImportDataWriter(IWContext iwc) {
		super(iwc);
	}
	
	public IBExportImportDataWriter(Storable storable, IWContext iwc) {
		super(storable,iwc);
	}

	public String createContainer() throws IOException {
		String name = ((IBExportImportData) storable).getName();
		long folderIdentifier = System.currentTimeMillis();
 		String path = getRealPathToFile(name, ZIP_EXTENSION, folderIdentifier);
     File auxiliaryFile = null;
     OutputStream destination = null;
     OutputStream outputStreamWrapper = null;
     try {
       auxiliaryFile = new File(path);
       destination = new BufferedOutputStream((new FileOutputStream(auxiliaryFile)));
     }
     catch (FileNotFoundException ex)  {
//     	logError("[XMLData] problem creating file.");
//     	log(ex);
     	throw new IOException("xml file could not be stored");
     }
     try {
			outputStreamWrapper = writeData(destination);
		}
		finally {
			close(outputStreamWrapper);
		}
     return getURLToFile(name, ZIP_EXTENSION, folderIdentifier);
 	}
	
	public OutputStream writeData(OutputStream destination) throws IOException {
		// use this stream because some writers call close and we don't want to close the zip output stream
		ZipOutputStreamIgnoreClose zipOutputStream = new ZipOutputStreamIgnoreClose(destination);
		IBExportImportData metadata = (IBExportImportData) storable;
		List data = metadata.getData();
		Map alreadyStoredElements = new HashMap(data.size());
		// counter for the entries in metadata
		int entryNumber = 0;
		// counter for the prefix of the stored files
		int identifierNumber = 0;
 		Iterator iterator = data.iterator();
 		while (iterator.hasNext()) {
 			Storable element = (Storable) iterator.next();
 			// do not store the same elements twice into the zip file
 			if (alreadyStoredElements.containsKey(element)) {
 				XMLElement fileElement = (XMLElement) alreadyStoredElements.get(element);
 				metadata.modifyElementSetNameSetOriginalNameLikeElementAt(entryNumber++, fileElement);
 			}
 			else {
	 			WriterToFile currentWriter = (WriterToFile) element.write(this, iwc);
	 			String originalName = currentWriter.getName();
	 			String mimeType = currentWriter.getMimeType();
	 			String zipElementName = createZipElementName(originalName, identifierNumber++);
	 			ZipEntry zipEntry = new ZipEntry(zipElementName);
	 			XMLElement fileElement = metadata.modifyElementSetNameSetOriginalName(entryNumber++, zipElementName, originalName, mimeType);
	 			alreadyStoredElements.put(element, fileElement);
	 			zipOutputStream.putNextEntry(zipEntry);
	 			try {
	 				currentWriter.writeData(zipOutputStream);
	 			}
	 			finally {
	 				closeEntry(zipOutputStream);
	 			}
 			}
 		}
 		// add metadata itself to the zip file
 		XMLData metadataSummary = metadata.createMetadataSummary();
 		WriterToFile currentWriter = (WriterToFile) metadataSummary.write(this, iwc);
		String originalName = currentWriter.getName();
		ZipEntry zipEntry = new ZipEntry(originalName);
		zipOutputStream.putNextEntry(zipEntry);
		try {
			currentWriter.writeData(zipOutputStream);
		}
		finally {
			closeEntry(zipOutputStream);
			closeStream(zipOutputStream);
		}
		return destination;
	}
	
  public String getName() {
  	return ((IBExportImportData) storable).getName();
  }
  
  public String getMimeType() {
  	return "application/zip";
  }

  public Object write(File file, IWContext context) {
  	return new FileObjectWriter((Storable) file, context);
  }
  
  public Object write(ICFile file, IWContext context) {
		return new ICFileWriter((Storable) file, context);
	}

  public Object write(XMLData xmlData, IWContext context) {
		return new XMLDataWriter(xmlData, context);
	}
  
  public Object write(IBExportImportData metadata, IWContext context) {
  	return new IBExportImportDataWriter(metadata, context);
  }

  private String createZipElementName(String originalName, int entryNumber) {
  	String originalNameWithoutExtension = StringHandler.cutExtension(originalName);
  	String modifiedName = StringHandler.stripNonRomanCharacters(originalNameWithoutExtension);
  	modifiedName = StringHandler.replaceNameKeepExtension(originalName, modifiedName);
  	StringBuffer buffer = new StringBuffer();
  	buffer.append(entryNumber).append(ZIP_ELEMENT_DELIMITER).append(modifiedName);
  	return buffer.toString();
  }

  protected void closeEntry(ZipOutputStream output) {
  	try {
  		if (output != null) {
  			output.closeEntry();
  		}
  	}

  	catch (IOException io) {
  	  	// do not hide an existing exception
  	}
  }
  
  protected void closeStream(ZipOutputStreamIgnoreClose output) {
  	try {
  		if (output != null) {
  			output.closeStream();
  		}
  	}
  	catch (IOException io) {
  	  	// do not hide an existing exception
  	}
  }

}

