/*
 * $Id: Chart.java,v 1.5 2003/04/03 09:48:02 laddi Exp $
 *
 * Copyright (C) 2001 Idega hf. All Rights Reserved.
 *
 * This software is the proprietary information of Idega hf.
 * Use is subject to license terms.
 *
 */
package com.idega.graphics;

import java.awt.Color;

/**
 *
 *
 * @author <a href="mailto:palli@idega.is">Pall Helgason</a>
 * @version 1.0alpha
 */
public abstract class Chart {

  /*
   * The data to be plotted
   */
  protected Double data_[] = null;
  /*
   * The legend for the data
   */
  protected String legend_[] = null;
  /*
   * The URL for the chart created
   */
  protected String URL_ = null;
  /*
   * The background colour for the chart
   */
  protected Color backGround_ = null;
  /*
   * The colours for the chart elements
   */
  protected Color colours_[] = null;
  /*
   * The prefix for the chart filename
   */
  protected String prefix_ = null;
  /*
   * The prefix for the chart url
   */
  protected String webPrefix_ = null;
  /*
   * The postfix for the chart filename
   */
  protected String postfix_ = null;

  protected int numberOfDigits_ = -1;
  protected String addToBarLabel_ = null;


  /**
   * Sets the names of the chart elements
   *
   * @param parameter-name description  Adds a parameter to the "Parameters" section. The description may be continued on the next line.
   * @return description                Adds a "Returns" section with the description text. This text should describe the return type and permissible range of values.
   * @throws class-name description
   */
  public void setLegend(String legend[]) throws ChartException {
    if (data_ != null) {
      if (data_.length != legend.length) {
        throw new ChartException("The number of data elements do not match the number of legend elements");
      }
    }

    if (legend == null)
      throw new ChartException("Legend is null");

    if (legend.length == 0)
      throw new ChartException("Legend is empty");

    legend_ = new String[legend.length];
    for (int i = 0; i < legend.length; i++)
      legend_[i] = new String(legend[i]);
  }

  /**
   * L�sing � falli
   *
   * @param parameter-name description  Adds a parameter to the "Parameters" section. The description may be continued on the next line.
   * @return description                Adds a "Returns" section with the description text. This text should describe the return type and permissible range of values.
   * @throws class-name description
   */
  public void setData(Double data[]) throws ChartException {
    if (legend_ != null) {
      if (legend_.length != data.length) {
        throw new ChartException("The number of data elements do not match the number of legend elements");
      }
    }

    if (data == null)
      throw new ChartException("Data is null");

    if (data.length == 0)
      throw new ChartException("Empty data");

    data_ = new Double[data.length];
    for (int i = 0; i < data.length; i++)
      data_[i] = new Double(data[i].doubleValue());
  }

  /**
   * L�sing � falli
   *
   * @param parameter-name description  Adds a parameter to the "Parameters" section. The description may be continued on the next line.
   * @return description                Adds a "Returns" section with the description text. This text should describe the return type and permissible range of values.
   * @throws class-name description
   */
  public void setBackgroundColour(Color c) {
    backGround_ = c;
  }

  /**
   * L�sing � falli
   *
   * @param parameter-name description  Adds a parameter to the "Parameters" section. The description may be continued on the next line.
   * @return description                Adds a "Returns" section with the description text. This text should describe the return type and permissible range of values.
   * @throws class-name description
   */
  public void setChartColours(Color colours[]) throws ChartException {
    if (colours == null)
      throw new ChartException("Colours is null");

    if (colours.length == 0)
      throw new ChartException("Colours is empty");

    colours_ = new Color[colours.length];
    for (int i = 0; i < colours.length; i++)
      colours_[i] = new Color(colours[i].getRGB());
  }

  /**
   * L�sing � falli
   *
   * @param parameter-name description  Adds a parameter to the "Parameters" section. The description may be continued on the next line.
   * @return description                Adds a "Returns" section with the description text. This text should describe the return type and permissible range of values.
   * @throws class-name description
   */
  public void setFilePrefix(String prefix) {
    prefix_ = prefix;
  }

  /**
   * L�sing � falli
   *
   * @param parameter-name description  Adds a parameter to the "Parameters" section. The description may be continued on the next line.
   * @return description                Adds a "Returns" section with the description text. This text should describe the return type and permissible range of values.
   * @throws class-name description
   */
  public void setWebPrefix(String prefix) {
    webPrefix_ = prefix;
  }

  /**
   * L�sing � falli
   *
   * @param parameter-name description  Adds a parameter to the "Parameters" section. The description may be continued on the next line.
   * @return description                Adds a "Returns" section with the description text. This text should describe the return type and permissible range of values.
   * @throws class-name description
   */
  public void setFilePostfix(String postfix) {
    postfix_ = postfix;
  }

  public void setNumberOfBarLabelDigits(int number) {
    numberOfDigits_ = number;
  }

  public void addToBarLabel(String stringToAdd) {
    addToBarLabel_ = stringToAdd;
  }

  /**
   * L�sing � falli
   *
   * @param parameter-name description  Adds a parameter to the "Parameters" section. The description may be continued on the next line.
   * @return description                Adds a "Returns" section with the description text. This text should describe the return type and permissible range of values.
   * @throws class-name description
   */
  public abstract String create() throws ChartException;
}
