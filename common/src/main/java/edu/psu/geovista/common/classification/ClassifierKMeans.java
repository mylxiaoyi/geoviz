package edu.psu.geovista.common.classification;

/**
 * <p>Title: Studio applications</p>
 * <p>Description: </p>
 * <p>Copyright: Copyright (c) 2002</p>
 * <p>Company: GeoVSITA Center</p>
 * @author Xiping Dai
 * @version 1.0
 */

import edu.psu.geovista.common.data.DataSetForApps;
import edu.psu.geovista.common.event.DataSetEvent;
import edu.psu.geovista.common.event.DataSetListener;

// import edu.psu.geovista.symbolization.ColorSymbolizerForClassification;

public class ClassifierKMeans implements DataSetListener {

	private int[] cluster; // each element indicates the class number of the
							// corresponding state.
	private double[][] clustermean; // each row is a mean vector of a class.
	private int[] iniObsIdx; // the indices of observations which are the
								// assigned initial points (means).
	private int[] selectedAttIdx; // the indices of attributes considered in
									// this classification.
	private Object[] dataObject;
	private String[] attributesDisplay;
	private double[][] dataArray; // array for classification, each row is a
									// data vector of an observation, each
									// collum is an attribute vector.
	private int clusternumber = 5; // number of clusters
	public ClassifierKMeans() {
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
		// remove string data
		DataSetForApps dataObjTransfer = data;
		this.dataObject = dataObjTransfer.getDataSetNumeric();
		this.attributesDisplay = dataObjTransfer.getAttributeNamesNumeric();
		dataArray = new double[dataObjTransfer.getNumObservations()][attributesDisplay.length];
		// transfer data array to double array
		for (int j = 0; j < attributesDisplay.length; j++) {
			int t = 0;
			if (dataObject[j] instanceof double[])
				t = 0;
			else if (dataObject[j] instanceof int[])
				t = 1;
			else if (dataObject[j] instanceof boolean[])
				t = 2;
			for (int i = 0; i < dataArray.length; i++) {
				switch (t) {
				case 0:
					dataArray[i][j] = ((double[]) dataObject[j])[i];
					break;
				case 1:
					dataArray[i][j] = (double) ((int[]) dataObject[j])[i];
					break;
				case 2:
					dataArray[i][j] = ((boolean[]) dataObject[j])[i] ? 1.0
							: 0.0;
					break;
				}
			}
		}
		if (this.selectedAttIdx == null) {
			this.selectedAttIdx = new int[this.attributesDisplay.length];
			for (int i = 0; i < this.attributesDisplay.length; i++) {
				this.selectedAttIdx[i] = i;
			}
		}
		KMeansCluster();
	}

	public void setDataArray(double[][] dataArray) {
		this.dataArray = dataArray;
		if (this.selectedAttIdx == null) {
			this.selectedAttIdx = new int[this.attributesDisplay.length];
			for (int i = 0; i < this.attributesDisplay.length; i++) {
				this.selectedAttIdx[i] = i;
			}
		}
		KMeansCluster();
	}

	public void setAttributesDisplay(String[] att) {
		this.attributesDisplay = att;
	}

	public void setClusterNumber(int clusterNumber) {
		this.clusternumber = clusterNumber;
	}

	public void setIniObsIdx(int[] iniObsIdx) {
		if (iniObsIdx == null) {
			this.iniObsIdx = null;
		} else {
			this.iniObsIdx = iniObsIdx;
		}
	}

	public void setSelectedAttIdx(int[] selectedAttIdx) {

			this.selectedAttIdx = selectedAttIdx;
			System.out.print("selectedAttIdx is not null..."
					+ selectedAttIdx[0]);

	}

	public int getClusterNumber() {
		return this.clusternumber;
	}

	public int[] getKMeansClusters() {
		return this.cluster;
	}

	public double[][] getClusterMean() {
		return this.clustermean;
	}

	private void KMeansCluster() {
		// initialize clustermean
		// clustermean = new double[clusternumber][attributesDisplay.length];
		clustermean = new double[clusternumber][this.selectedAttIdx.length];
		cluster = new int[dataArray.length];
		if (this.iniObsIdx != null) {
			for (int i = 0; i < clusternumber; i++)
				for (int j = 0; j < this.selectedAttIdx.length; j++)
					clustermean[i][j] = dataArray[this.iniObsIdx[i]][this.selectedAttIdx[j]];
		} else {// no specified initial points
			for (int i = 0; i < clusternumber; i++)
				for (int j = 0; j < this.selectedAttIdx.length; j++)
					clustermean[i][j] = dataArray[i][this.selectedAttIdx[j]];
		}
		// loop to classify
		double[] obsWithConsideredAtt = new double[this.selectedAttIdx.length];// pick
																				// data
																				// within
																				// considered
																				// attributes
																				// for
																				// each
																				// observation.
		double diff = 1.0; // the change between two successive loops.
		for (int i = 0; i < 100 && diff >= 1.0e-3; i++) // every loop
		{
			// allocate each vector to the closest cluster mean
			for (int j = 0; j < dataArray.length; j++) // every vector
			{
				double distancecloser = Double.MAX_VALUE;
				double distance = 0.0;
				cluster[j] = 0;
				// copy those considered attributes value to array for distance
				// calculation
				for (int k = 0; k < this.selectedAttIdx.length; k++) {
					obsWithConsideredAtt[k] = dataArray[j][this.selectedAttIdx[k]];
				}

				for (int k = 0; k < clustermean.length; k++) // every cluster
																// mean
				{
					// distance = getDistance(dataArray[j], clustermean[k]);
					distance = getDistance(obsWithConsideredAtt, clustermean[k]);
					if (distance < distancecloser) {
						cluster[j] = k;
						distancecloser = distance;
					}
				}
			}
			// recalculate each cluster mean
			// double[][] clustermeannew = new
			// double[clusternumber][attributesDisplay.length];
			double[][] clustermeannew = new double[clusternumber][this.selectedAttIdx.length];
			int[] clusteramount = new int[clusternumber];
			for (int j = 0; j < dataArray.length; j++) {
				clusteramount[cluster[j]] += 1;
				// copy those considered attributes value to array for distance
				// calculation
				for (int k = 0; k < this.selectedAttIdx.length; k++) {
					obsWithConsideredAtt[k] = dataArray[j][this.selectedAttIdx[k]];
				}
				clustermeannew[cluster[j]] = plus(clustermeannew[cluster[j]],
						obsWithConsideredAtt);
			}
			for (int j = 0; j < clusternumber; j++) {
				if (clusteramount[j] != 0)
					clustermeannew[j] = divide(clustermeannew[j],
							(double) clusteramount[j]);
				else
					setvalue(clustermeannew[j], clustermean[j]);
			}
			// calculate cluster change
			// calculate mean of new clusters
			double[] cm = new double[this.selectedAttIdx.length];
			for (int j = 0; j < clusternumber; j++)
				cm = plus(cm, clustermeannew[j]);
			cm = divide(cm, (double) clusternumber);
			// calculate standard deviation of new clusters
			double csd = 0.0;
			for (int j = 0; j < clusternumber; j++)
				csd += distance2(cm, clustermeannew[j]);
			csd = Math.sqrt(csd / clusternumber);
			// calculate change percentage
			diff = 0.0;
			for (int j = 0; j < clusternumber; j++)
				diff += distance2(clustermean[j], clustermeannew[j]);
			diff = Math.sqrt(diff / clusternumber) / csd;
			// set new clusters
			clustermean = clustermeannew;
		}
		// fireSelectionChanged(getClusterColors());
	}

	public void setDistanceMethod(String distanceMethod) {

	}

	public static double getDistance(double[] a, double[] b) {
		return distance(a, b);
	}

	public static double[] plus(double[] a, double[] b) {
		double[] c = new double[a.length];
		for (int i = 0; i < a.length; i++)
			c[i] = a[i] + b[i];
		return c;
	}

	public static double[] divide(double[] a, double b) {
		double[] c = new double[a.length];
		for (int i = 0; i < a.length; i++)
			c[i] = a[i] / b;
		return c;
	}

	public static void setvalue(double[] a, double[] b) {
		for (int i = 0; i < a.length; i++)
			a[i] = b[i];
	}

	public static double multiply(double[] a, double[] b) {
		double c = 0.0;
		for (int i = 0; i < a.length; i++)
			c += a[i] * b[i];
		return c;
	}

	public static double distance(double[] a, double[] b) {
		return Math.sqrt(distance2(a, b));
	}

	public static double distance2(double[] a, double[] b) // square of
															// distance
	{
		double c = 0.0;
		for (int i = 0; i < a.length; i++)
			c += (a[i] - b[i]) * (a[i] - b[i]);
		return c;
	}

	// Work with coordinator.
	public void dataSetChanged(DataSetEvent e) {
		this.setDataSet(e.getDataSetForApps());
	}


}
