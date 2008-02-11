/* -------------------------------------------------------------------
 GeoVISTA Center (Penn State, Dept. of Geography)
 Java source file for the class GeoMapUni
 Copyright (c), 2002, GeoVISTA Center
 All Rights Reserved.
 Original Author: Frank Hardisty
 $Author: hardisty $
 $Id: GeoMapUni.java,v 1.12 2005/08/12 17:25:21 hardisty Exp $
 $Date: 2005/08/12 17:25:21 $
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

package geovista.matrix.map;

import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.Dimension;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.util.Vector;
import java.util.logging.Logger;

import javax.swing.BorderFactory;
import javax.swing.BoxLayout;
import javax.swing.JButton;
import javax.swing.JList;
import javax.swing.JPanel;
import javax.swing.ListSelectionModel;
import javax.swing.border.LineBorder;
import javax.swing.event.TableModelEvent;
import javax.swing.event.TableModelListener;

import geovista.common.data.DataSetForApps;
import geovista.common.data.DescriptiveStatistics;
import geovista.common.data.SpatialStatistics;
import geovista.common.data.SpatialWeights;
import geovista.common.event.DataSetEvent;
import geovista.common.event.DataSetListener;
import geovista.common.event.IndicationEvent;
import geovista.common.event.IndicationListener;
import geovista.common.event.PaletteEvent;
import geovista.common.event.PaletteListener;
import geovista.common.event.SelectionEvent;
import geovista.common.event.SelectionListener;
import geovista.common.event.SpatialExtentEvent;
import geovista.common.event.SpatialExtentListener;
import geovista.geoviz.map.GeoMap;
import geovista.geoviz.scatterplot.SingleScatterPlot;
import geovista.symbolization.event.ColorClassifierEvent;
import geovista.symbolization.event.ColorClassifierListener;

/**
 * A Moran Map has a choropleth map and a scatterplot tied together
 */
public class MoranMap extends JPanel implements SelectionListener,
		IndicationListener, DataSetListener, ColorClassifierListener,
		SpatialExtentListener, PaletteListener, TableModelListener,
		ActionListener {
	protected final static Logger logger = Logger.getLogger(MoranMap.class
			.getName());
	GeoMap map;
	SingleScatterPlot sp;
	DataSetForApps dataSetOriginal;
	DataSetForApps dataSetZ;
	DataSetForApps dataSetMoran;
	SpatialWeights spatialWeights;
	JList varList;
	JButton sendButt;
	int monteCarloIterations;

	public MoranMap() {
		super();
		BoxLayout box = new BoxLayout(this, BoxLayout.X_AXIS);
		map = new GeoMap();
		sp = new SingleScatterPlot();
		map.addSelectionListener(sp);
		sp.addSelectionListener(map);
		map.addIndicationListener(sp);
		sp.addIndicationListener(map);
		setLayout(box);
		Dimension prefSize = new Dimension(300, 300);
		map.setPreferredSize(prefSize);
		sp.setPreferredSize(prefSize);
		LineBorder border = (LineBorder) BorderFactory
				.createLineBorder(Color.black);
		map.setBorder(border);
		sp.setBorder(border);

		this.add(map);
		this.add(sp);

		JPanel varPanel = new JPanel();
		varList = new JList();
		sendButt = new JButton("Add Var");
		varList.setSelectionMode(ListSelectionModel.SINGLE_SELECTION);
		sendButt.addActionListener(this);
		varPanel.setLayout(new BorderLayout());
		varPanel.add(varList, BorderLayout.CENTER);
		varPanel.add(sendButt, BorderLayout.SOUTH);

		this.add(varPanel);

	}

	public void selectionChanged(SelectionEvent e) {
		map.selectionChanged(e);
		sp.selectionChanged(e);

	}

	public SelectionEvent getSelectionEvent() {
		return new SelectionEvent(this, map.getSelectedObservations());
	}

	public void indicationChanged(IndicationEvent e) {
		map.indicationChanged(e);
		sp.indicationChanged(e);

	}

	public void dataSetChanged(DataSetEvent e) {
		dataSetOriginal = e.getDataSetForApps();
		spatialWeights = dataSetOriginal.getSpatialWeights();
		;
		// first get the z scores....
		Object[] dataObjects = dataSetOriginal.getDataObjectOriginal();
		Object[] zDataObjects = new Object[dataObjects.length];
		String[] names = (String[]) dataObjects[0];
		String[] newNames = new String[names.length];
		System.arraycopy(names, 0, newNames, 0, names.length);
		for (int i = 1; i < dataObjects.length; i++) {
			Object thing = dataObjects[i];
			if (thing instanceof int[]) {
				int[] intThing = (int[]) thing;
				double[] doublething = new double[intThing.length];
				for (int obs = 0; obs < intThing.length; obs++) {
					doublething[obs] = intThing[obs];
				}
				newNames[i - 1] = names[i - 1] + "_Z";
				zDataObjects[i] = DescriptiveStatistics
						.calculateZScores(doublething);
			} else if (thing instanceof double[]) {
				double[] doublething = (double[]) thing;
				newNames[i - 1] = names[i - 1] + "_Z";
				zDataObjects[i] = DescriptiveStatistics
						.calculateZScores(doublething);
			} else {
				zDataObjects[i] = dataObjects[i];
			}
		}
		zDataObjects[0] = newNames;
		dataSetZ = new DataSetForApps(zDataObjects);

		// now do the moran's

		Object[] moranDataObjects = new Object[dataObjects.length];

		String[] moranNames = new String[names.length];
		System.arraycopy(names, 0, moranNames, 0, names.length);

		for (int i = 1; i < zDataObjects.length; i++) {
			Object thing = zDataObjects[i];
			if (thing instanceof double[]) {
				double[] doublething = (double[]) thing;
				moranNames[i - 1] = names[i - 1] + "_M";
				moranDataObjects[i] = SpatialStatistics.calculateMoranScores(
						doublething, spatialWeights);
			} else {
				moranDataObjects[i] = dataObjects[i];
			}
		}
		moranDataObjects[0] = moranNames;

		dataSetMoran = new DataSetForApps(moranDataObjects);

		// now do monte carlo
		Object[] monteCarloDataObjects = new Object[dataObjects.length];

		String[] monteCarloNames = new String[names.length];
		System.arraycopy(names, 0, monteCarloNames, 0, names.length);

		for (int i = 1; i < zDataObjects.length; i++) {
			Object thing = zDataObjects[i];
			if (thing instanceof double[]) {
				double[] zData = (double[]) thing;
				double[] moranData = (double[]) moranDataObjects[i];
				monteCarloIterations = 100;
				monteCarloDataObjects[i] = SpatialStatistics.findPValues(zData,
						moranData, monteCarloIterations, spatialWeights);
				monteCarloNames[i - 1] = names[i - 1] + "_Sig";
			} else {
				monteCarloDataObjects[i] = dataObjects[i];
			}
		}
		monteCarloDataObjects[0] = monteCarloNames;

		DataSetForApps dataSetMonteCarlo = new DataSetForApps(
				monteCarloDataObjects);
		DataSetForApps dataSetAppended = dataSetMonteCarlo
				.appendDataSet(dataSetZ);

		DataSetEvent e2 = new DataSetEvent(dataSetAppended, this);
		Vector vecData = new Vector();
		for (String element : monteCarloNames) {
			vecData.add(element);
		}
		varList.setListData(vecData);
		map.dataSetChanged(e2);
		sp.dataSetChanged(e2);
	}

	public void colorClassifierChanged(ColorClassifierEvent e) {
		// TODO Auto-generated method stub

	}

	public void spatialExtentChanged(SpatialExtentEvent e) {
		// TODO Auto-generated method stub

	}

	public void paletteChanged(PaletteEvent e) {
		// TODO Auto-generated method stub

	}

	public void tableChanged(TableModelEvent e) {
		// TODO Auto-generated method stub

	}

	public void actionPerformed(ActionEvent e) {
		if (e.getSource() == sendButt) {
			if (varList.getSelectedIndex() < 0) {
				return;
			}

			dataSetOriginal.addColumn((String) varList.getSelectedValue(),
					dataSetMoran.getNumericDataAsDouble(varList
							.getSelectedIndex()));
		}

	}

	public static void main(String[] args) {

		MoranMap map = new MoranMap();
		double[] vals = { 0, 1, 2, 3 };
		double along = DescriptiveStatistics.percentAbove(vals, -1);
		System.out.println(along);

		/**
		 * boolean useProj = false; boolean useResource = false; JFrame app =
		 * new JFrame("MoranMap Main Class: Why?");
		 * app.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
		 * 
		 * app.getContentPane().setLayout( new BoxLayout(app.getContentPane(),
		 * BoxLayout.X_AXIS));
		 * 
		 * app.pack(); app.setVisible(true);
		 * 
		 * MoranMap map2 = new MoranMap(); app.getContentPane().add(map2);
		 * app.pack(); app.setVisible(true);
		 * 
		 * String fileName =
		 * "C:\\arcgis\\arcexe81\\Bin\\TemplateData\\USA\\counties.shp";
		 * fileName = "C:\\temp\\shapefiles\\intrstat.shp"; fileName =
		 * "C:\\data\\geovista_data\\shapefiles\\larger_cities.shp"; fileName =
		 * "C:\\data\\geovista_data\\shapefiles\\jin\\CompanyProdLL2000Def.shp";
		 * fileName =
		 * "C:\\data\\geovista_data\\Historical-Demographic\\census\\census80_90_00.shp";
		 * 
		 * ShapeFileDataReader shpRead = new ShapeFileDataReader();
		 * shpRead.setFileName(fileName); CoordinationManager coord = new
		 * CoordinationManager(); ShapeFileToShape shpToShape = new
		 * ShapeFileToShape(); ShapeFileProjection shpProj = new
		 * ShapeFileProjection(); GeoData48States stateData = new
		 * GeoData48States(); //coord.addBean(map2); coord.addBean(shpToShape);
		 * 
		 * if (useResource) {
		 * 
		 * shpProj.setInputDataSetForApps(stateData.getDataForApps()); } else {
		 * if (useProj) { stateData.addActionListener(shpProj);
		 * shpProj.setInputDataSet(shpRead.getDataSet()); } } Object[] data =
		 * null; if (useProj) { data = shpProj.getOutputDataSet(); } else { data =
		 * shpRead.getDataSet(); }
		 * 
		 * shpToShape.setInputDataSet(data); DataSetForApps dataSet =
		 * shpToShape.getOutputDataSetForApps();
		 * 
		 * long startTime = System.currentTimeMillis(); double total = 0; long
		 * count = 0;
		 * 
		 * int nNumeric = dataSet.getNumberNumericAttributes(); for (int i = 0 ;
		 * i < nNumeric; i++){ double[] zVals =
		 * DescriptiveStatistics.calculateZScores(dataSet.getNumericDataAsDouble(i));
		 * total = total + zVals[0]; count++; }
		 * 
		 * 
		 * 
		 * 
		 * long endTime = System.currentTimeMillis(); logger.finest("that took = " +
		 * (endTime -startTime)); logger.finest("count " + count);
		 */

	}

}
