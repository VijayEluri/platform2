/*
 * $Id: Stripper.java,v 1.1 2001/05/02 17:45:06 eiki Exp $
 *
 * Copyright (C) 2001 Idega hf. All Rights Reserved.
 *
 * This software is the proprietary information of Idega hf.
 * Use is subject to license terms.
 *
 */

package com.idega.util;

import java.io.FileReader;
import java.io.FileWriter;
import java.io.BufferedReader;
import java.io.BufferedWriter;

/**
 *  A class to strip out \CVS\ lines from files to assist in compilation of the
 *  whole com tree.
 */

public class Stripper {

  public Stripper() {
  }

  public static void main(String[] args) {
    Stripper stripper1 = new Stripper();

    if (args.length != 2) {
      System.err.println("Auli. �� �tt a� hafa tvo parametra me� �essu, innskr� og �tskr�");

      return;
    }

    BufferedReader in = null;
    BufferedWriter out = null;

    try {
      in = new BufferedReader(new FileReader(args[0]));
    }
    catch (java.io.FileNotFoundException e) {
      System.err.println("Auli. Error : " + e.toString());

      return;
    }

    try {
      out = new BufferedWriter(new FileWriter(args[1]));
    }
    catch (java.io.IOException e) {
      System.err.println("Auli. Error : " + e.toString());

      return;
    }

    try {
      String input = in.readLine();

      while (input != null) {
        int index = input.indexOf("\\CVS\\");
        if (index > -1)
          System.out.println("Skipping : " + input);
        else {
          out.write(input);
          out.newLine();
        }

        input = in.readLine();
      }
    }
    catch (java.io.IOException e) {
      System.err.println("Error reading or writing file : " + e.toString());
    }

    try {
      in.close();
      out.close();
    }
    catch (java.io.IOException e) {
      System.err.println("Error closing files : " + e.toString());
    }
  }


}