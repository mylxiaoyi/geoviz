/* -------------------------------------------------------------------
 Java source file for the class CartogramMatrixElement
  Original Author: Frank Hardisty
  $Author: hardistf $
  $Id: ComparableShapes.java,v 1.1 2005/12/05 20:17:05 hardistf Exp $
  $Date: 2005/12/05 20:17:05 $
  This library is free software; you can redistribute it and/or
  modify it under the terms of the GNU Lesser General Public
  License as published by the Free Software Foundation; either
  version 2.1 of the License, or (at your option) any later version.
  This library is distributed in the hope that it will be useful,
  but WITHOUT ANY WARRANTY; without even the implied warranty of
  MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the GNU
  Lesser General Public License for more details.
  You should have received a copy of the GNU Lesser General Public
  License along with this library; if not, write to the Free Software
  Foundation, Inc., 59 Temple Place, Suite 330, Boston, MA  02111-1307  USA
 -------------------------------------------------------------------   */

package edu.psu.geovista.cartogram;

import java.awt.event.WindowAdapter;
import java.awt.event.WindowEvent;
import java.util.prefs.Preferences;

import javax.swing.JFrame;
import javax.swing.JProgressBar;

import edu.psu.geovista.app.map.MapMatrixElement;
import edu.psu.geovista.common.data.DataSetForApps;
import edu.psu.geovista.data.sample.GeoDataGeneralizedStates;
import edu.psu.geovista.data.shapefile.ShapeFileProjection;
import edu.psu.geovista.symbolization.BivariateColorSymbolClassification;
import edu.psu.geovista.symbolization.BivariateColorSymbolClassificationSimple;

/*
 *
 */

public class CartogramMatrixElement
    extends MapMatrixElement {

  DataSetForApps dataSet;
  String inputFileName;
  CartogramPreferences preferencesFrame;
  boolean DEBUG = false;
  TransformsMain trans;

  Object[] cartogramData;

  public CartogramMatrixElement() {
    super();

    this.trans = new TransformsMain(false);

    try {
      //initGui();
      @SuppressWarnings("unused")
	Preferences gvPrefs = Preferences.userNodeForPackage(this.getClass());
      this.preferencesFrame = new CartogramPreferences(
          "Cartogram Preferences", this, this.trans);

    }
    catch (Exception ex) {
      ex.printStackTrace();
    }
  }


  public void setElementPosition(int[] dataIndices) {
    //this happens first when a new data set comes down the pipe...before
    //setDataObject... what to do?
    

    if (dataIndices == null ){
      return;
    }

    this.elementPosition = (int[]) dataIndices.clone();
    //zero based or one based...
    //well...
    //i know...
    //let's keep changing our minds!
    //then we'll never get it straight!
    //super.setCurrColorColumnX(this.elementPosition[1]-1);//y position
    //super.setCurrColorColumnY(this.elementPosition[1]-1);//y position
    int colorVar = this.elementPosition[1]-1;

    super.dataColorY = dataSet.getNumericDataAsDouble(colorVar);//y position
    super.dataColorX = dataColorY;//y position, having same data objects triggers univariate coloring
    super.sendColorsToLayers(dataColorX.length);
    if(this.dataSet != null && this.dataSet.getSpatialType() == DataSetForApps.SPATIAL_TYPE_POLYGON){
      createCartogram();
      //sendColorsToLayers(this.dataSet.getNumObservations());
    }
    //this.setHistogramData();
  }
  /*
   * This method actually creates the temporary files and creates
    a TransformsMain to do the work.
   */
  public void createCartogram() {
    Preferences gvPrefs = Preferences.userNodeForPackage(this.getClass());


    this.trans = new TransformsMain(false);
    this.preferencesFrame.setTransformParams(this.trans);

    JProgressBar pBar = new JProgressBar();
    int currentVar = this.elementPosition[0]-1;//x var
    CartogramShapeCache cache = CartogramShapeCache.getInstance();
    ComparableCartogram compare = new ComparableCartogram(this.dataSet, trans.getMaxNSquareLog(), trans.getBlurWidth(),trans.getBlurWidthFactor(), currentVar);
    ComparableCartogram previousOne = cache.findCartogram(compare);
    DataSetForApps newData = null;
    if (previousOne == null){
      newData = MapGenFile.createCartogram(pBar, this.dataSet, gvPrefs,
          currentVar, this.trans);
      compare.setCartogramShapes(newData.getShapeData());;
      cache.addComparableCartogram(compare);
    } else{
      newData = MapGenFile.createNewDataSet(this.dataSet,previousOne.getCartogramShapes());
    }
    super.setDataSet(newData);

  }
  public void setBivarColorClasser(BivariateColorSymbolClassification
                                   bivarColorClasser) {
    BivariateColorSymbolClassificationSimple biColorSymbolizer =
        (BivariateColorSymbolClassificationSimple)
        bivarColorClasser;


//   biColorSymbolizer.setClasserX(bivarColorClasser.getClasserX());
//   biColorSymbolizer.setColorerX(bivarColorClasser.getXColorSymbolizer());
//    biColorSymbolizer.setClasserY(bivarColorClasser.getClasserX());
//    biColorSymbolizer.setColorerY(bivarColorClasser.getXColorSymbolizer());

    this.bivarColorClasser = biColorSymbolizer;
    if (this.dataSet != null) {
      sendColorsToLayers(this.dataSet.getNumObservations());
    }
  }
  
  
  
//  public void setDataSet(Object[] data) {
//
//    super.setDataSet(data);
//    this.cartogramData = null;//reset in case we already have this
//
//    DataSetForApps dataSet = new DataSetForApps(data);
//    this.dataSet = dataSet;
//
//  }
  public void setDataObject(DataSetForApps dataSet) {

    this.dataSet = new DataSetForApps();

    super.setDataSet(dataSet);
    this.setElementPosition(this.elementPosition);//triggers cartogram creation

    //super.tickleColors();
  }




  public static void main(String[] args) {

    GeoDataGeneralizedStates stateData = new GeoDataGeneralizedStates();

    CartogramMatrixElement gui = new CartogramMatrixElement();
    JFrame frame = new JFrame(
        "Cartogram GeoMap");
    frame.getContentPane().add(gui);
    ShapeFileProjection shpProj = new ShapeFileProjection();
    shpProj.setInputDataSetForApps(stateData.getDataForApps());
    gui.setDataSet(shpProj.getOutputDataSetForApps());
    frame.pack();
    frame.setVisible(true);
    frame.addWindowListener(new WindowAdapter() {
      public void windowClosing(WindowEvent e) {
        System.exit(0);
      }
    });

  }

}
