/* -------------------------------------------------------------------
 GeoVISTA Center (Penn State, Dept. of Geography)
 Java source file for the class TreeMapMatrixElement
 Copyright (c), 2002, GeoVISTA Center
 All Rights Reserved.
 Original Author: Frank Hardisty
 $Author: hardisty $
 $Id: TreeMapMatrixElement.java,v 1.2 2005/03/24 20:40:36 hardisty Exp $
 $Date: 2005/03/24 20:40:36 $
 Reference:		Document no:
 ___				___
 -------------------------------------------------------------------  *

 */


package geovista.matrix.treemap;

import java.awt.Color;
import java.awt.Graphics;
import java.awt.event.ActionListener;
import java.util.Vector;

import javax.swing.BorderFactory;
import javax.swing.JToolBar;

import geovista.common.classification.Classifier;
import geovista.common.data.DataSetForApps;
import geovista.common.event.ColorArrayEvent;
import geovista.common.event.DataSetEvent;
import geovista.common.event.IndicationEvent;
import geovista.matrix.MatrixElement;
import geovista.symbolization.BivariateColorSymbolClassification;
import geovista.symbolization.BivariateColorSymbolClassificationSimple;

public class TreeMapMatrixElement
    extends TreeMapCanvas implements MatrixElement {

  //the following are required for returning to matrix
  private int[] elementPosition;
  private double[] xAxisExtents;
  private double[] yAxisExtents;

  private Color selectionColor;

  public TreeMapMatrixElement() {
    super();

    this.setBorder(BorderFactory.createLineBorder(Color.darkGray, 1));
  }

  /**
   * @param data
   * 
   * This method is deprecated becuase it wants to create its very own pet
   * DataSetForApps. This is no longer allowed, to allow for a mutable, 
   * common data set. Use of this method may lead to unexpected
   * program behavoir. 
   * Please use setDataSet instead.
   */
  @Deprecated
  public void setData(Object[] data) {
	 this.setDataSet(new DataSetForApps(data));
    
  }
  
  public void setDataSet(DataSetForApps data) {
    super.dataSet = data; //in case we are about to get a new dataSet, get rid of the old one
    
    //XXX has this problem been fixed by eliminating the Object[] array
//    //next -- where did Xiping hide the observation names data?
//    String[] obsNames = null;
//
//    if (data[data.length -2] instanceof String[]){
//      obsNames = (String[])data[data.length - 2];
//    } else if (data[data.length -3] instanceof String[]){
//      obsNames = (String[])data[data.length - 3];
//    }
//    dataSet.setObservationNames(obsNames);//why is this necassary????

    DataSetEvent e = new DataSetEvent(dataSet,this);
    super.dataSetChanged(e);
  }

  public void setElementPosition(int[] dataIndices) {
    this.elementPosition = (int[]) dataIndices.clone();
    super.setGroupingVarID(this.elementPosition[0]-1); //grouping = x // why -1
    super.setSizingVarID(this.elementPosition[1]-1); //size = y //why -1
    //super.setCurrOrderColumn(this.elementPosition[0]);//order = x
    //super.setCurrColorColumn(this.elementPosition[1]);//color = y

    BivariateColorSymbolClassification
        bivarColorClasser =
        new BivariateColorSymbolClassificationSimple();

    this.setBivarColorClasser(bivarColorClasser, false);

  }

  public BivariateColorSymbolClassification getBivarColorClasser() {
    return null;
  }

  public void setBivarColorClasser(BivariateColorSymbolClassification biClasser,
                                   boolean why) {
    if (super.dataSet == null) {
      return;
    }
    double[] dataX = super.dataSet.getNumericDataAsDouble(elementPosition[0]);
    double[] dataY = super.dataSet.getNumericDataAsDouble(elementPosition[1]);

    Classifier classerX = biClasser.getClasserX();
    int[] classes = classerX.classify(dataX, 3);
    super.setClassification(classes);
    Color[] colors = biClasser.symbolize(dataX, dataY);
    super.colorArrayChanged(new ColorArrayEvent(this, colors));

  }

  public int[] getElementPosition() {
    return this.elementPosition;
  }

  public void addActionListener(ActionListener l) {

  }

  public void setConditionArray(int[] condArray) {

  }

  public int[] getSelections() {
    return null;
  }

  public void setSelections(int[] sel) {

  }

  public void setSelectedObservations(Vector v) {

  }

  public Vector getSelectedObservations() {
    return null;
  }

  public void setSelOriginalColorMode(boolean b) {

  }

  public void setIndication(int i) {
      super.indicationChanged(new IndicationEvent(this,i));
  }

  //For axes of scatter plot.
  //a noop for this class
  public void setAxisOn(boolean axisOn) {
  }

  //Set min and max for axes. xAxisExtents[0] = min, [1] = max.
  public void setXAxisExtents(double[] xAxisExtents) {

  }

  public void setYAxisExtents(double[] yAxisExtents) {}

  public double[] getXAxisExtents() {
    return this.xAxisExtents;
  }

  public double[] getYAxisExtents() {
    return this.yAxisExtents;
  }

  public String getShortDiscription() {
    return "TM";
  }

  //public void setBivarColorClasser (BivariateColorSymbolClassification bivarColorClasser) {
  //this.bivarColorClasser = bivarColorClasser;
  //this.sendColorsToLayers(this.dataColorX.length);
  //}

  public void setSelectionColor(Color c) {
    this.selectionColor = c;
    //super.setColorSelection(c);
  }

  public Color getSelectionColor() {
    return this.selectionColor;
  }

  public void setMultipleSelectionColors(Color[] c) {
  }

  public void setColorArrayForObs(Color[] c) {
  }

  public JToolBar getTools() {
    return null;
  }

  /**
   * This method only paints the current contents of the drawingBuff.
   * @param g
   */
  public void paintComponent(Graphics g) {
    super.paintComponent(g);

    if (this.elementPosition == null) {
      return;
    }

  }

}
