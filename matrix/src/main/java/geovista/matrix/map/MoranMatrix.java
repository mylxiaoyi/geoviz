/* Licensed under LGPL v. 2.1 or any later version;
 see GNU LGPL for details.
 Original Author: Frank Hardisty */

package geovista.matrix.map;

import java.awt.Color;
import java.awt.Dimension;
import java.util.logging.Logger;

import javax.swing.BorderFactory;
import javax.swing.BoxLayout;
import javax.swing.JFrame;
import javax.swing.JPanel;
import javax.swing.border.LineBorder;
import javax.swing.event.TableModelEvent;
import javax.swing.event.TableModelListener;

import geovista.common.data.DataSetForApps;
import geovista.common.data.DescriptiveStatistics;
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
import geovista.coordination.CoordinationManager;
import geovista.geoviz.map.GeoMapUni;
import geovista.geoviz.map.OldSpatialWeights;
import geovista.geoviz.scatterplot.SingleScatterPlot;
import geovista.readers.example.GeoData48States;
import geovista.readers.shapefile.ShapeFileDataReader;
import geovista.readers.shapefile.ShapeFileProjection;
import geovista.readers.shapefile.ShapeFileToShape;
import geovista.symbolization.event.ColorClassifierEvent;
import geovista.symbolization.event.ColorClassifierListener;

/**
 * A Moran matrix has a whole series of plots tied together:
 * 
 * 1. map of original data map of moran's I Bivariate map of the two 2.
 * histogram of original data Histogram of same scatterplot of the two
 */
public class MoranMatrix extends JPanel implements SelectionListener,
		IndicationListener, DataSetListener, ColorClassifierListener,
		SpatialExtentListener, PaletteListener, TableModelListener {

	GeoMapUni map;
	SingleScatterPlot sp;
	DataSetForApps dataSetOriginal;
	DataSetForApps dataSetZ;
	DataSetForApps dataSetMoran;
	OldSpatialWeights spatialWeights;
	final static Logger logger = Logger.getLogger(MoranMatrix.class.getName());

	public MoranMatrix() {
		super();
		BoxLayout box = new BoxLayout(this, BoxLayout.X_AXIS);
		map = new GeoMapUni();
		sp = new SingleScatterPlot();
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
		spatialWeights = new OldSpatialWeights(dataSetOriginal.getShapeData());
		// first get the z scores....
		Object[] dataObjects = dataSetOriginal.getDataObjectOriginal();
		Object[] zDataObjects = new Object[dataObjects.length];
		String[] names = (String[]) dataObjects[0];
		String[] newNames = new String[names.length];
		System.arraycopy(names, 0, newNames, 0, names.length);
		for (int i = 1; i < dataObjects.length; i++) {
			Object thing = dataObjects[i];
			if (thing instanceof double[]) {
				double[] doublething = (double[]) thing;
				newNames[i - 1] = names[i - 1] + "_Z";
				zDataObjects[i] = calculateZScores(doublething);
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
		for (int i = 1; i < dataObjects.length; i++) {
			Object thing = dataObjects[i];
			if (thing instanceof double[]) {
				double[] doublething = (double[]) thing;
				moranNames[i - 1] = names[i - 1] + "_M";
				moranDataObjects[i] = calculateMoranScores(doublething, i);
			} else {
				moranDataObjects[i] = dataObjects[i];
			}
		}
		moranDataObjects[0] = moranNames;
		dataSetMoran = new DataSetForApps(moranDataObjects);

		DataSetEvent e2 = new DataSetEvent(dataSetMoran, this);
		map.dataSetChanged(e2);
		sp.dataSetChanged(e2);
	}

	public void colorClassifierChanged(ColorClassifierEvent e) {
		// TODO Auto-generated method stub

	}

	SpatialExtentEvent savedEvent;

	public SpatialExtentEvent getSpatialExtentEvent() {
		return savedEvent;
	}

	public void spatialExtentChanged(SpatialExtentEvent e) {
		savedEvent = e;

	}

	public void paletteChanged(PaletteEvent e) {
		// TODO Auto-generated method stub

	}

	public void tableChanged(TableModelEvent e) {
		// TODO Auto-generated method stub

	}

	/**
	 * @return
	 */
	double[] calculateZScores(double[] data) {
		double[] xData;

		xData = new double[data.length];
		System.arraycopy(data, 0, xData, 0, data.length);

		double meanX = DescriptiveStatistics.fineMean(xData);
		double stdDevX = DescriptiveStatistics.fineStdDev(xData, false);
		double[] fullXData = new double[xData.length];
		for (int i = 0; i < xData.length; i++) {
			fullXData[i] = (xData[i] - meanX) / stdDevX;
		}
		return fullXData;
	}

	/**
	 * @return
	 */
	double[] calculateMoranScores(double[] zData, int whichVar) {
		double[] moranData;
		moranData = new double[zData.length];
		System.arraycopy(zData, 0, moranData, 0, zData.length);
		double[] moranScores = new double[moranData.length];
		for (int i = 0; i < moranData.length; i++) {
			int[] iBors = spatialWeights.getNeighbors(i);
			double sumScore = 0;
			for (int element : iBors) {
				sumScore = sumScore
						+ dataSetZ.getValueAsDouble(whichVar, element);
			}
			moranScores[i] = dataSetZ.getValueAsDouble(whichVar, i) * sumScore;
		}
		return moranScores;
	}

	public static void main(String[] args) {

		boolean useProj = false;
		boolean useResource = true;
		JFrame app = new JFrame("MoranMap Main Class: Why?");
		app.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);

		app.getContentPane().setLayout(
				new BoxLayout(app.getContentPane(), BoxLayout.X_AXIS));

		app.pack();
		app.setVisible(true);

		MoranMap map2 = new MoranMap();
		app.getContentPane().add(map2);
		app.pack();
		app.setVisible(true);

		String fileName = "C:\\arcgis\\arcexe81\\Bin\\TemplateData\\USA\\counties.shp";
		fileName = "C:\\temp\\shapefiles\\intrstat.shp";
		fileName = "C:\\data\\geovista_data\\shapefiles\\larger_cities.shp";
		fileName = "C:\\data\\geovista_data\\shapefiles\\jin\\CompanyProdLL2000Def.shp";
		fileName = "C:\\data\\geovista_data\\Historical-Demographic\\census\\census80_90_00.shp";

		ShapeFileDataReader shpRead = new ShapeFileDataReader();
		shpRead.setFileName(fileName);
		CoordinationManager coord = new CoordinationManager();
		ShapeFileToShape shpToShape = new ShapeFileToShape();
		ShapeFileProjection shpProj = new ShapeFileProjection();
		GeoData48States stateData = new GeoData48States();
		// coord.addBean(map2);
		coord.addBean(shpToShape);

		if (useResource) {

			shpProj.setInputDataSetForApps(stateData.getDataForApps());
		} else {
			if (useProj) {
				// stateData.addActionListener(shpProj);
				shpProj.setInputDataSet(shpRead.getDataSet());
			}
		}
		Object[] data = null;
		if (useProj) {
			data = shpProj.getOutputDataSet();
		} else {
			data = shpRead.getDataSet();
		}

		shpToShape.setInputDataSet(data);
		DataSetForApps dataSet = shpToShape.getOutputDataSetForApps();

		long startTime = System.currentTimeMillis();
		double total = 0;
		long count = 0;

		int nNumeric = dataSet.getNumberNumericAttributes();
		for (int i = 0; i < nNumeric; i++) {
			double[] zVals = DescriptiveStatistics.calculateZScores(dataSet
					.getNumericDataAsDouble(i));
			total = total + zVals[0];
			count++;
		}

		long endTime = System.currentTimeMillis();
		logger.info("that took = " + (endTime - startTime));
		logger.info("" + count);

	}

}
