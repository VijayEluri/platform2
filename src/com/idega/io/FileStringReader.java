package com.idega.io;



import java.io.*;

import java.awt.event.*;

import java.net.*;



public class FileStringReader {



  public static String FileToString( String fileName, String theFilePath)throws Exception{



    FileReader reader = new FileReader(theFilePath+fileName);

    StringBuffer fileString = new StringBuffer("\n");

    char[] charArr = new char[10];

    while (reader.read(charArr) != -1){

            fileString.append(String.copyValueOf(charArr));

    }

    return fileString.toString();

  }



  public static String FileToString( String theFilePathAndfileName)throws Exception{

    return FileToString( theFilePathAndfileName, "");

  }

}
