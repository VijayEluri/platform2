package com.idega.block.media.servlet;

/**
 * Title: MediaServlet
 * Description: A servlet for streaming data from the blob field of the ic_file table.
 * Copyright: Idega software Copyright (c) 2001
 * Company: idega
 * @author <a href = "mailto:eiki@idega.is">Eirdebikur Hrafnsson</a>
 * @version 1.0
 *
 */

import java.io.IOException;
import java.io.InputStream;
import java.io.DataOutputStream;
import java.io.ByteArrayOutputStream;
import javax.servlet.ServletOutputStream;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.sql.*;
import com.idega.servlet.IWCoreServlet;
import com.idega.util.database.ConnectionBroker;
import com.idega.idegaweb.IWMainApplication;
import com.idega.presentation.ui.Parameter;
import com.idega.block.media.servlet.MediaOutputWriter;
import com.idega.io.MemoryFileBufferWriter;
import com.idega.io.MediaWritable;


public class MediaServlet extends IWCoreServlet{

  public static final String PARAMETER_NAME = "media_id";
  public static final String USES_OLD_TABLES = "IW_USES_OLD_MEDIA_TABLES";
  private boolean usesOldTables = false;
  private static IWMainApplication iwma;
  public static boolean debug = false;

  public final static String PRM_WRITABLE_CLASS = "wrcls";
  public final static String PRM_SESSION_MEMORY_BUFFER = MemoryFileBufferWriter.PRM_SESSION_BUFFER;

  public static Parameter getParameter(int FileId){
    return new Parameter(PARAMETER_NAME,String.valueOf(FileId));
  }

  public void doGet( HttpServletRequest _req, HttpServletResponse _res) throws IOException{
    doPost(_req,_res);
  }

  public void doPost( HttpServletRequest request, HttpServletResponse response) throws IOException{

    if( iwma == null )
      iwma = IWMainApplication.getIWMainApplication(getServletContext());

    if(request.getParameter(PARAMETER_NAME)!=null || request.getParameter("image_id")!=null){
      new MediaOutputWriter().doPost(request,response,iwma);
    }
    else if(request.getParameter(PRM_SESSION_MEMORY_BUFFER)!=null){
      new MemoryFileBufferWriter().doPost(request,response);
    }
    else if(request.getParameter(MediaWritable.PRM_WRITABLE_CLASS)!=null){
      try{
        MediaWritable mw = (MediaWritable) Class.forName(IWMainApplication.decryptClassName(request.getParameter(MediaWritable.PRM_WRITABLE_CLASS))).newInstance();
        mw.init(request,iwma);
        response.setContentType(mw.getMimeType());
        ServletOutputStream out = response.getOutputStream();
        mw.writeTo(out);
        out.flush();

      }
      catch(Exception ex){
        ex.printStackTrace();
      }
    }
  }
}
